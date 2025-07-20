package com.java_web.backend.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java_web.backend.Admin.Service.AdminUserService;
import com.java_web.backend.Common.DTO.AdminLoginDTO;
import com.java_web.backend.Common.Entity.User;

@RestController
@RequestMapping("/admin")
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginDTO loginDTO) {
        return adminUserService.login(loginDTO);
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> getUserList() {
        return ResponseEntity.ok(adminUserService.listUsers());
    }
    
    @PostMapping("user/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return adminUserService.addUser(user);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        return adminUserService.deleteUser(id);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Integer id) {
        return ResponseEntity.ok(adminUserService.getUserInfo(id));
    }
}
