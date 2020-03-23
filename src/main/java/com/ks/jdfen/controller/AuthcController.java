package com.ks.jdfen.controller;

import com.ks.jdfen.Entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/authc")
public class AuthcController {

    @RequestMapping("/room")
    public Object room(Model model) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        model.addAttribute("userId",user.getUsername());
        return "authc/room";
    }


}
