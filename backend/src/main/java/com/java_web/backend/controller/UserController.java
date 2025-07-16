package com.java_web.backend.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.java_web.backend.Entity.User;

@RestController
public class UserController {
    @GetMapping("/user/{id}")
    public String getUserById(@PathVariable int id){
        System.out.println(id);
        return "根据ID" + id + "获取用户";
    }

    @PostMapping("/user")
    public String save(@RequestBody User user){
        return "添加用户";
    }

    @PutMapping("/user")
    public String update(@RequestBody User user){
        return "更新用户";
    }

    @DeleteMapping("/user/{id}")
    public String deleteById(@PathVariable int id){
        System.out.println(id);
        return "根据ID删除用户";
    }
} 