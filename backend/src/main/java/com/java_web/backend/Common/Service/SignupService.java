package com.java_web.backend.Common.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.java_web.backend.Common.Entity.User;
import com.java_web.backend.Common.Mapper.UserMapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SignupService {
  @Autowired
  private UserMapper userMapper;

  /**
   * 用户注册
   * 
   * @param username 用户名
   * @param password 密码
   * @param email    邮箱
   * @param role     用户角色 (teacher/admin)
   * @return 注册结果，包含状态和消息
   */
  public Map<String, Object> register(String username, String password, String email, String role) {
    Map<String, Object> result = new HashMap<>();

    // 验证用户名和邮箱是否已存在
    if (userMapper.findByUsername(username) != null) {
      result.put("success", false);
      result.put("message", "用户名已被使用");
      return result;
    }

    if (userMapper.findByEmail(email) != null) {
      result.put("success", false);
      result.put("message", "邮箱已被注册");
      return result;
    }

    // 验证角色是否有效
    if (!"teacher".equals(role) && !"admin".equals(role)) {
      result.put("success", false);
      result.put("message", "无效的用户角色");
      return result;
    }

    // 创建新用户
    User user = new User();
    user.setUsername(username);
    // 注意: 实际项目中应对密码进行加密存储，这里暂时与现有登录逻辑保持一致使用明文存储
    user.setPassword(password);
    user.setEmail(email);
    user.setRole(role); // 设置用户选择的角色
    user.setStatus("active"); // 默认状态为激活
    user.setIsDeleted(0); // 未删除
    user.setCreatedAt(new Date());
    user.setUpdatedAt(new Date());

    try {
      // 保存用户到数据库
      userMapper.insert(user);

      // 返回成功结果
      result.put("success", true);
      result.put("message", "注册成功");
      result.put("user", user);

      return result;
    } catch (Exception e) {
      result.put("success", false);
      result.put("message", "注册失败：" + e.getMessage());
      return result;
    }
  }

  /**
   * 验证用户名是否可用
   * 
   * @param username 用户名
   * @return 是否可用
   */
  public boolean isUsernameAvailable(String username) {
    return userMapper.findByUsername(username) == null;
  }

  /**
   * 验证邮箱是否可用
   * 
   * @param email 邮箱
   * @return 是否可用
   */
  public boolean isEmailAvailable(String email) {
    return userMapper.findByEmail(email) == null;
  }
}
