package com.ks.jdfen.controller;

import com.ks.jdfen.Entity.User;
import com.ks.jdfen.dao.UserDao;
import com.ks.jdfen.myutil.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/authc")
public class AuthcController {

    @Autowired
    private static RedisUtil redisUtil;

    @Autowired
    private static UserDao userDao;

    @RequestMapping("/room")
    public Object room(Model model) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        model.addAttribute("userId",user.getUsername());
        return "authc/room";
    }

    @ResponseBody
    @RequestMapping("/settleAccounts")
    public void settleAccounts() {
        String winner = (String) SecurityUtils.getSubject().getSession().getAttribute("winner");
        int jifenchiTotal = 0;
        List<User> users = userDao.findAll();
        for (String key : redisUtil.keys("*")) {
            for (User user : users) {
                //每个人都结算下并存入数据库
                if (user.getUsername().equals(key)){
                    jifenchiTotal+=countAndUpdateUser(user,winner);
                    break;
                }
            }
        }

    }

    /**
     * @Author imc700
     * @Description 所有玩家都减少他投注的钱,赢家也减少投注的钱,然后加上积分池所有的钱
     * @Date 3:21 下午 2020/3/31
     * @Param [user, winner]
     * @return void
     **/
    private int countAndUpdateUser(User user,String winner){
        String username = user.getUsername();
        List<Integer> jifens = (List<Integer>) redisUtil.get(username);
        int total = 0;
        for (Integer jifen : jifens) {
            total+=jifen;
        }
//        if (!winner.equals(username)) total = total*-1;
        user.setMoney(user.getMoney()-total);
        userDao.save(user);
        return total;
    }

}
