package com.example.demo.controller;

import com.example.demo.common.ResultInfo;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther: raohr
 * @Title:
 * @Description:
 * @Date: 2019/11/25 9:16
 * @param:
 * @return:
 * @throws:
 */
@RestController
@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("save")
    public ResultInfo save(User user) throws Exception {
        userService.save(user);
        return ResultInfo.success();
    }
}
