package com.mochousoft.springdata.jpa.controller;

import com.mochousoft.springdata.jpa.entity.User;
import com.mochousoft.springdata.jpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("page")
    public void page(@RequestParam int page, @RequestParam int size) {
        Page<User> userPage = userService.getPage(page, size);
    }
}
