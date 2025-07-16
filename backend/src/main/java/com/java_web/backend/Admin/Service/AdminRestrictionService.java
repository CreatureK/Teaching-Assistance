package com.java_web.backend.Admin.Service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java_web.backend.Common.Entity.Restriction;
import com.java_web.backend.Common.Entity.User;
import com.java_web.backend.Common.Mapper.RestrictionMapper;
import com.java_web.backend.Common.Mapper.UserMapper;

@Service
public class AdminRestrictionService {
    @Autowired
    private RestrictionMapper restrictionMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    public ResponseEntity<?> addRestriction(Restriction restriction) {
        // 检查用户是否存在
        User user = userMapper.selectById(restriction.getUserId());
        if (user == null) {
            return ResponseEntity.badRequest().body("用户不存在");
        }
        
        // 设置创建时间
        restriction.setCreatedAt(new Date());
        
        restrictionMapper.insert(restriction);
        return ResponseEntity.ok("添加功能限制成功");
    }
    
    public ResponseEntity<?> removeRestriction(Integer id) {
        int result = restrictionMapper.deleteById(id);
        if (result > 0) {
            return ResponseEntity.ok("移除功能限制成功");
        } else {
            return ResponseEntity.badRequest().body("限制不存在");
        }
    }
    
    public List<Restriction> getUserRestrictions(Integer userId) {
        QueryWrapper<Restriction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return restrictionMapper.selectList(queryWrapper);
    }
}
