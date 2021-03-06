package com.ks.jdfen.websocket;

import com.alibaba.fastjson.JSON;
import com.ks.jdfen.Entity.User;
import com.ks.jdfen.controller.AuthcController;
import com.ks.jdfen.dao.UserDao;
import com.ks.jdfen.myutil.RedisUtil;
import com.ks.jdfen.service.UserService;
import com.ks.jdfen.zha.Player;
import com.ks.jdfen.zha.WinThreePoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ServerEndpoint("/websocket/{userId}")
@Component
public class WebSocketServer {

    private static RedisUtil redisUtil;

    @Autowired
    public void setRedisUtil(RedisUtil redisUtil) {
        WebSocketServer.redisUtil = redisUtil;
    }

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        WebSocketServer.userService = userService;
    }

    private static AuthcController authcController;

    @Autowired
    public void setAuthcController(AuthcController authcController) {
        WebSocketServer.authcController = authcController;
    }

    private static UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        WebSocketServer.userDao = userDao;
    }

    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    private static ArrayList<Player> list = new ArrayList<>();
    private static List<String> seatNameList = Arrays.asList("", "", "", "", "", "", "", "");
    private static List<String> qipaiNameList = Arrays.asList("", "", "", "", "", "", "", "");
    private static List<String> tempNameList = Arrays.asList("", "", "", "", "", "", "", "");//从发牌时初始化这个值.这局他参与了,但是弃牌了就从temp里删掉.
    private static List<Integer> seatMoneyList = new ArrayList<>();
    //创建一个线程安全的map
    private static Map<String, WebSocketServer> users = Collections.synchronizedMap(new HashMap());
    private static Map<String, Boolean> kanpaiusers = Collections.synchronizedMap(new HashMap());

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //放入map中的key,用来表示该连接对象
    private String username;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String username) {
        this.session = session;
        this.username = username.trim();
        if (!seatNameList.contains(username))
            seatNameList.set(seatNameList.indexOf(""), username.trim());//有人加入了,就请上座.


        users.put(username, this);
        addOnlineCount();           //在线数加1

        //如果第一个人进场,默认是房主,给发牌权限(暂时有user是否具有fapai的permission的shiro标签来判定)


        log.info(username + "加入！当前在线人数为" + getOnlineCount() + ",分别是" + getAllNames());
        try {
            seatMoneyList.clear();
            getMoneyListByNames(seatNameList);
            sendInfo("shangzuo#" + seatNameList + "#" + seatMoneyList);//只要是让所有人的桌面UI发生变化,就要通信
            sendInfo(username + "加入！当前在线人数为" + getOnlineCount() + ",分别是" + getAllNames());
            //告诉刚进来的人自己坐哪个位置上
            sendMessageToSomeBody(this.username, "whoami#" + this.username);
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    public void getMoneyListByNames(List<String> seatNameList) {
        List<User> allUser = userDao.findAll();
        for (String name : seatNameList) {
            if (!StringUtils.isEmpty(name)) {
                for (User u : allUser) {
//                    redisUtil.del(u.getUsername());
                    if (name.equals(u.getUsername())) {
                        seatMoneyList.add(u.getMoney());
                        break;
                    }
                }

            } else {
                seatMoneyList.add(-1);
            }
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        users.remove(this.username);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info(this.username + "一个连接关闭！当前在线人数为" + getOnlineCount());
        try {
            if (seatNameList.indexOf(this.username) >= 0) seatNameList.set(seatNameList.indexOf(this.username), "");
            seatMoneyList.clear();
            getMoneyListByNames(seatNameList);
            sendInfo("shangzuo#" + seatNameList + "#" + seatMoneyList);
            sendInfo(this.username + "已退出！当前在线人数为" + getOnlineCount() + ",分别是" + getAllNames());
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    private boolean kaipaiStatus = false;

    /**
     * 收到客户端消息后触发的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("来自客户端的消息:" + message);
        //群发消息
        try {
            if (StringUtils.isEmpty(message)) return;
            //只要收到消息,就判断tempNameList是否只有两个人,是,则显现,
            if (!kaipaiStatus)
                if (judgePlayersCount() == 2) sendTheLastTwoPlayerInfo("keyikai");
            //如果给所有人发消息携带@ALL, 给特定人发消息携带@xxx@xxx#message
            String[] split = message.split("#");
            if (split.length > 1) {
                //暂时肯定是给所有发送消息
                if (message.contains("ALL")) {
                    if (split[1].contains("fapai")) {
                        //发牌的时候把上一局的下注记录都删除掉.因为上一局已经结算了.
//                        List<User> allUser = userDao.findAll();
//                        for (User u : allUser) {
//                            redisUtil.del(u.getUsername());
//                        }
                        redisUtil.flushdb();
                        tempNameList = seatNameList;//开局就把temp设值
                        //接到发牌指令,后台开始发牌,给每个人生成牌并且用map接收
                        list.clear();//这里必须请掉,因为再发牌的时候还不晓得是几个人呢,因为有人可能不玩了.
                        //发牌滴话,每个人下个底子,默认1块钱
                        List<Integer> list1 = new ArrayList<>();
                        list1.add(1);
                        for (String s : users.keySet()) {
//                            redisUtil.del(s);//发牌的话,把上一局的所有投注记录都删掉.2020年3月30日21:35:35决定在查询到所有人的数据的时候去删redis
                            list.add(new Player(s));
                            redisUtil.set(s, list1);
                        }
                        try {
                            WinThreePoker poker = new WinThreePoker(list);
                            poker.startPlayingCards();
                            list = poker.getPlayers();
                            //发牌完后让下家开始下注
                            String nextPlayerName = nextPlayerName();
                            sendMessageToSomeBody(nextPlayerName, "zhunbeixia");
                            //底子都下了,然后就是在前台刷新下注记录
                            Map<String, List> map = new HashMap<>();
                            for (String key : redisUtil.keys("*")) {
                                map.put(key, (List<Integer>) redisUtil.get(key));
                            }
                            sendInfo(nextPlayerName + "yixiazhu" + JSON.toJSONString(map));
                        } catch (Exception e) {
                            sendInfo("exception:" + e.getMessage());
                            return;
                        }
                    } else if (split[1].contains("xiazhu")) {
                        xiazhu(split[1]);
                        return;
                    }else if (split[1].contains("log")) {
                        Date date = new Date();
                        sendInfo(format.format(date)+"      "+this.username+split[1].split("log")[1] );
                        return;
                    } else if (split[1].contains("qipai")) {//弃牌就是从temp里删掉并告诉所有前端该人已弃牌(比如把这个人面前的牌置白)
                        System.out.println(this.username+"弃牌了-------------------------------");
                        tempNameList.set(tempNameList.indexOf(this.username), "");
                        if (judgePlayersCount() == 1) {
                            String winner = kaipai2();
                            sendInfo("kaipai:" + winner);
                            authcController.settleAccounts(winner.trim());
                        }
                        //如果只有俩个人的时候还有一家直接丢牌,就设之后一个人胜利
                        if (!kaipaiStatus)
                            if (judgePlayersCount() == 2) sendTheLastTwoPlayerInfo("keyikai");
                    } else if (split[1].contains("kaipai")) {//点击开牌的话,
                        String winner = kaipai();
                        sendInfo("kaipai:" + winner);
                        authcController.settleAccounts(winner.trim());
                        return;
                    }
                    String msg = username + ": " + split[1];
                    sendInfo(msg);//群发消息

                } else {//给特定人员发消息
                    String user = split[0].substring(1);
                    if (!StringUtils.isEmpty(user.trim())) {
                        if ("kanpai".equals(split[1])) {//如果是某人要看牌,就从list里把他的牌挑出来给前台
                            for (Player p : list) {
                                if (p.getName().equals(user)) {
                                    //后台记住这个人看牌了
                                    kanpaiusers.put(user.trim(), true);
                                    sendMessageToSomeBody(user.trim(), "kanpai#" + p.getPlayerCards().toString());
                                }
                            }
                        } else if ("timeisup".equals(split[1])) {//如果是某人要看牌,就从list里把他的牌挑出来给前台
                            String qipaiUserName = user.trim();
                            if (!qipaiNameList.contains(qipaiUserName)){
                                qipaiNameList.set(qipaiNameList.indexOf(""), qipaiUserName);//有人弃牌了,就放进去,如果已经在里面,就不做任何操作
                                sendMessageToSomeBody(qipaiUserName, "timeisup#" + user.trim());
                            }
                        }


                    }

                }
            } else {
                sendInfo(username + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int judgePlayersCount() {
        int nowCount = 0;
        for (String player : tempNameList) {
            if (!StringUtils.isEmpty(player)) nowCount++;
        }
        return nowCount;
    }

    private String kaipai() {
        ArrayList<Player> nowPlayers = new ArrayList<>();
        StringBuilder finalPlayerCards=new StringBuilder();
        for (Player p : list) {
            if (tempNameList.contains(p.getName())){
                nowPlayers.add(p);
                finalPlayerCards.append(p.getName()).append(":").append(p.getPlayerCards()).append("     ");
            }
        }
        finalPlayerCards.append("finish.");
        for (Player p : nowPlayers) {
            try {
                sendMessageToSomeBody(p.getName().trim(), finalPlayerCards.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            WinThreePoker threePoker = new WinThreePoker(nowPlayers);
            String finalWinnerName = threePoker.judge(nowPlayers);
            //此时应该把全局变量全部初始化,让玩家刷新浏览器重新进场并开局
            initVaris();
            return finalWinnerName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 所有人把牌全丢了,那最后一个人直接赢
     *
     * @return
     */
    private String kaipai2() {
        String finalWinnerName = "";
        try {
            for (String name : tempNameList) {
                if (!StringUtils.isEmpty(name)) {
                    finalWinnerName = name;
                    break;
                }
            }
            initVaris();
            return finalWinnerName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initVaris() {
        //此时应该把全局变量全部初始化,让玩家刷新浏览器重新进场并开局
        onlineCount = 0;
        list.clear();
        seatNameList = Arrays.asList("", "", "", "", "", "", "", "");
        tempNameList = Arrays.asList("", "", "", "", "", "", "", "");//从发牌时初始化这个值.这局他参与了,但是弃牌了就从temp里删掉.
        seatMoneyList.clear();
        qipaiNameList = Arrays.asList("", "", "", "", "", "", "", "");
        kaipaiStatus = false;
        kanpaiusers.clear();
    }

    private void xiazhu(String s) throws IOException {
        int money = Integer.parseInt(s.split("xiazhu")[1]);
        if (redisUtil.hasKey(this.username)) {
            List<Integer> list = (List<Integer>) redisUtil.get(this.username);
            list.add(money);
            redisUtil.set(this.username, list);
        } else {
            List<Integer> list = new ArrayList<>();
            list.add(money);
            redisUtil.set(this.username, list);
        }
        Map<String, List> map = new HashMap<>();
        for (String key : redisUtil.keys("*")) {
            map.put(key, (List<Integer>) redisUtil.get(key));
        }
        sendInfo(nextPlayerName() + "yixiazhu" + JSON.toJSONString(map));
        //并告诉下一个人要显示下注按钮了
        String nextPlayerName = nextPlayerName();
        int nextManMoney = judgeNextManMoney(money);
        System.out.println("轮到" + nextPlayerName + "下注了...下注金额默认为" + nextManMoney);
        sendMessageToSomeBody(nextPlayerName, "zhunbeixia" + nextManMoney);
    }

    public int judgeNextManMoney(int money) {
        String nextPlayerName = nextPlayerName();
        //如果当前下注人没看牌
        if (!kanpaiusers.containsKey(this.username)) {
            //下家也没看牌
            if (!kanpaiusers.containsKey(nextPlayerName)) return money;
            else return money * 2;
        } else {
            if (!kanpaiusers.containsKey(nextPlayerName)) return money / 2;
            else return money;
        }
    }

    private String nextPlayerName() {
        List<String> arrayList = new ArrayList<>();
        arrayList.addAll(tempNameList);//这里换一下因为如果有人弃牌了,下注的人就从temp里拿,因为弃牌的人不用再下注
        arrayList.addAll(tempNameList);
        List<String> collect = arrayList.stream().filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toList());
        //防止最后一名兄弟发牌,然后一个循环的话,找不到下家
        if (collect.isEmpty()) return "";
        return collect.get(collect.indexOf(this.username) + 1);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误 session: " + session);
        error.printStackTrace();
    }

    //    给特定人员发送消息
    public void sendMessageToSomeBody(String username, String message) throws IOException {
        if (users.get(username) == null) {
            return;
        }
        users.get(username).session.getBasicRemote().sendText(this.username + "@" + username + ": " + message);
//        this.session.getBasicRemote().sendText(this.username + "@" + username + ": " + message);
        System.out.println("给特定人员发送消息:" + this.username + "@" + username + ": " + message);
    }

    /**
     * 群发自定义消息
     */
    public void sendInfo(String message) throws IOException {
        for (WebSocketServer item : users.values()) {
            try {
                item.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 开牌时只给最后两个人点亮开牌按钮,而非给在座所有人发
     */
    public void sendTheLastTwoPlayerInfo(String message) throws IOException {
        kaipaiStatus = true;
        Map<String, WebSocketServer> tempMap = Collections.synchronizedMap(new HashMap());
        for (String username : users.keySet()) {
            if (tempNameList.contains(username)) {
                tempMap.put(username, users.get(username));
            }
        }
        for (WebSocketServer item : tempMap.values()) {
            try {
                item.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return users.keySet().size();
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static String getAllNames() {
        StringBuffer sb = new StringBuffer();
        for (String s : users.keySet()) {
            sb.append(s).append(" , ");
        }
        return sb.toString();
    }
}
