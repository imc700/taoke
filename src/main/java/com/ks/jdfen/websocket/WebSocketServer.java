package com.ks.jdfen.websocket;

import com.ks.jdfen.Entity.User;
import com.ks.jdfen.dao.UserDao;
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
import java.util.*;

@Slf4j
@ServerEndpoint("/websocket/{userId}")
@Component
public class WebSocketServer {

    private static UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        WebSocketServer.userService = userService;
    }
    private static UserDao userDao;
    @Autowired
    public void setUserDao(UserDao userDao) {
        WebSocketServer.userDao = userDao;
    }

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    private static ArrayList<Player> list = new ArrayList<>();
    private static List<String> seatNameList = Arrays.asList("","","","","","","","");
    private static List<Integer> seatMoneyList = new ArrayList<>();
    //创建一个线程安全的map
    private static Map<String, WebSocketServer> users = Collections.synchronizedMap(new HashMap());

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
        this.username = username;
        if (!seatNameList.contains(username))
            seatNameList.set(seatNameList.indexOf(""),username);//有人加入了,就请上座.


        users.put(username, this);
        addOnlineCount();           //在线数加1

        //如果第一个人进场,默认是房主,给发牌权限(暂时有user是否具有fapai的permission的shiro标签来判定)




        log.info(username + "加入！当前在线人数为" + getOnlineCount() + ",分别是" + getAllNames());
        try {
            seatMoneyList.clear();
            getMoneyListByNames(seatNameList);
            sendInfo("shangzuo#"+seatNameList+"#"+seatMoneyList);//只要是让所有人的桌面UI发生变化,就要通信
            sendInfo(username + "加入！当前在线人数为" + getOnlineCount() + ",分别是" + getAllNames());
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    public void getMoneyListByNames(List<String> seatNameList){
        List<User> allUser = userDao.findAll();
        for (String name : seatNameList) {
            if (!StringUtils.isEmpty(name)){
                for (User u : allUser) {
                    if (name.equals(u.getUsername())){
                        seatMoneyList.add(u.getMoney());
                        break;
                    }
                }

            }else{
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
        log.info("一个连接关闭！当前在线人数为" + getOnlineCount());
        try {
            seatNameList.set(seatNameList.indexOf(this.username), "");
            seatMoneyList.clear();
            getMoneyListByNames(seatNameList);
            sendInfo("shangzuo#"+seatNameList+"#"+seatMoneyList);
            sendInfo(this.username + "已退出！当前在线人数为" + getOnlineCount() + ",分别是" + getAllNames());
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

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
            if (StringUtils.isEmpty(message)) {
                return;
            }
            //如果给所有人发消息携带@ALL, 给特定人发消息携带@xxx@xxx#message
            String[] split = message.split("#");
            if (split.length > 1) {
                String[] split1 = split[0].split("@");
                if (split1.length < 2) {
                    return;
                }
                String firstuser = split1[1].trim();


                //暂时肯定是给所有发送消息
                if (StringUtils.isEmpty(firstuser) || "ALL".equals(firstuser.toUpperCase())) {
                    if (split[1].contains("fapai")) {
                        //接到发牌指令,后台开始发牌,给每个人生成牌并且用map接收
                        list.clear();
                        for (String s : users.keySet()) {
                            list.add(new Player(s));
                        }
                        try {
                            WinThreePoker poker = new WinThreePoker(list);
                            poker.startPlayingCards();
                            list = poker.getPlayers();
                        } catch (Exception e) {
                            sendInfo("exception:" + e.getMessage());
                            return;
                        }
                    }


                    String msg = username + ": " + split[1];
                    sendInfo(msg);//群发消息

                } else {//给特定人员发消息
                    for (String user : split1) {
                        if (!StringUtils.isEmpty(user.trim())) {
                            if ("kanpai".equals(split[1])) {//如果是某人要看牌,就从list里把他的牌挑出来给前台
//                                list.stream().filter(s->s.getName().equals(user)).collect(Collectors.toList());
                                for (Player p : list) {
                                    if (p.getName().equals(user)) {
                                        sendMessageToSomeBody(user.trim(), "kanpai#" + p.getPlayerCards().toString());
                                    }
                                }
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
        users.get(username).session.getBasicRemote().sendText(message);
        this.session.getBasicRemote().sendText(this.username + "@" + username + ": " + message);
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

    public static synchronized int getOnlineCount() {
        return onlineCount;
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
