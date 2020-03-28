package com.ks.jdfen.controller;

import com.ks.jdfen.Entity.User;
import com.ks.jdfen.service.UserService;
import com.ks.jdfen.shiro.PasswordHelper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordHelper passwordHelper;

    @GetMapping("doLogin")
    public Object doLogin(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException ice) {
            return "password error!";
        } catch (UnknownAccountException uae) {
            return "username error!";
        }
//        SecurityUtils.getSubject().isPermitted(username);
        User user = userService.findUserByName(username);
//        User user=(User) subject.getPrincipal();
        subject.getSession().setAttribute("user", user);
        return "SUCCESS";
    }

    @GetMapping("doRegister")
    public Object register(@RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        passwordHelper.encryptPassword(user);

        userService.saveUser(user);
        return "SUCCESS";
    }
}
