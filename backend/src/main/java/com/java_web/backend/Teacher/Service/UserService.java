package com.java_web.backend.Teacher.Service;

import com.java_web.backend.Common.DTO.LoginDTO;
import com.java_web.backend.Common.DTO.UpdateUserDTO;
import com.java_web.backend.Common.Entity.User;
import com.java_web.backend.Common.Mapper.UserMapper;
import com.java_web.backend.Common.Service.JWTService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JWTService jwtService;
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(LoginDTO dto) {
        // 查询用户
        User user = userMapper.findByEmail(dto.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 校验密码 (实际项目中应比较加密后的密码)
        if (!dto.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 校验角色
        if (!"teacher".equals(user.getRole())) {
            throw new RuntimeException("非教师账号");
        }
        
        // 校验状态
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账号已被冻结");
        }
        
        // 生成token
        String token = jwtService.generateToken(user);
        
        // 构建返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        
        return result;
    }
    
    /**
     * 获取用户信息
     */
    public User getUserProfile(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }
        
        // 不返回敏感信息
        user.setPassword(null);
        
        return user;
    }
    
    /**
     * 更新用户信息
     */
    public User updateUserProfile(UpdateUserDTO dto, Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDeleted() == 1) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新可修改的字段
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        
        user.setUpdatedAt(new Date());
        userMapper.updateById(user);
        
        // 不返回敏感信息
        user.setPassword(null);
        
        return user;
    }
}
