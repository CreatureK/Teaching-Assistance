package com.java_web.backend.Admin.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java_web.backend.Common.DTO.AdminLoginDTO;
import com.java_web.backend.Common.Entity.User;
import com.java_web.backend.Common.Mapper.UserMapper;
import com.java_web.backend.Common.Service.JWTService;

@Service
public class AdminUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTService jwtService;

    public ResponseEntity<?> login(AdminLoginDTO loginDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginDTO.getUsername())
                .eq("role", "admin");
        User admin = userMapper.selectOne(queryWrapper);

        if (admin == null) {
            return ResponseEntity.status(401).body("账号不存在");
        }

        if (!admin.getPassword().equals(loginDTO.getPassword())) {
            return ResponseEntity.status(401).body("密码错误");
        }

        // 生成JWT
        String token = jwtService.generateToken(admin);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", admin);

        return ResponseEntity.ok(result);
    }

    public List<User> listUsers() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", "teacher")
                .eq("is_deleted", 0);
        return userMapper.selectList(queryWrapper);
    }

    public ResponseEntity<?> addUser(User user) {
        // 设置默认值
        user.setRole("teacher");
        user.setIsDeleted(0);
        user.setPassword("123456"); // 设置默认密码
        user.setStatus("active"); // 设置账户状态为激活
        user.setCreatedAt(new Date()); // 设置创建时间
        user.setUpdatedAt(new Date()); // 设置更新时间

        // 检查邮箱是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", user.getEmail());

        if (userMapper.selectOne(queryWrapper) != null) {
            return ResponseEntity.badRequest().body("该邮箱已被注册");
        }

        userMapper.insert(user);
        return ResponseEntity.ok("添加用户成功");
    }

    public ResponseEntity<?> deleteUser(Integer id) {
        // 逻辑删除
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body("用户不存在");
        }

        user.setIsDeleted(1);
        userMapper.updateById(user);
        return ResponseEntity.ok("删除用户成功");
    }

    public User getUserInfo(Integer id) {
        return userMapper.selectById(id);
    }
    
    public ResponseEntity<?> updateUsername(Integer id, String username) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body("用户不存在");
        }
        
        user.setUsername(username);
        user.setUpdatedAt(new Date()); // 更新修改时间
        
        userMapper.updateById(user);
        return ResponseEntity.ok("用户名更新成功");
    }
    
    public ResponseEntity<?> updateEmail(Integer id, String email) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body("用户不存在");
        }
        
        // 检查新邮箱是否已被其他用户使用
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email)
                  .ne("id", id); // 排除当前用户
        
        if (userMapper.selectOne(queryWrapper) != null) {
            return ResponseEntity.badRequest().body("该邮箱已被其他用户注册");
        }
        
        user.setEmail(email);
        user.setUpdatedAt(new Date()); // 更新修改时间
        
        userMapper.updateById(user);
        return ResponseEntity.ok("邮箱更新成功");
    }
}
