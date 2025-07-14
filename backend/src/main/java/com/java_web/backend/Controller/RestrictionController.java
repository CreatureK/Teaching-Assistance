package com.java_web.backend.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.java_web.backend.Entity.Restriction;

@RestController
public class RestrictionController {
    @GetMapping("/restriction/{id}")
    public String getById(@PathVariable int id){
        System.out.println(id);
        return "根据ID获取功能限制";
    }

    @PostMapping("/restriction")
    public String save(@RequestBody Restriction restriction){
        return "添加功能限制";
    }

    @PutMapping("/restriction")
    public String update(@RequestBody Restriction restriction){
        return "更新功能限制";
    }

    @DeleteMapping("/restriction/{id}")
    public String deleteById(@PathVariable int id){
        System.out.println(id);
        return "根据ID删除功能限制";
    }
} 