package com.java_web.backend.Common.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.java_web.backend.Common.Service.SignupService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/signup")
public class SignupController {
  @Autowired
  private SignupService signupService;

  /**
   * 用户注册接口
   * 
   * @param payload 包含用户注册信息的请求体
   * @return 注册结果
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
    String username = payload.get("username");
    String password = payload.get("password");
    String email = payload.get("email");
    String role = payload.get("role");

    // 参数验证
    if (username == null || username.trim().isEmpty() ||
        password == null || password.trim().isEmpty() ||
        email == null || email.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("用户名、密码和邮箱不能为空");
    }

    // 角色验证，默认为teacher
    if (role == null || role.trim().isEmpty()) {
      role = "teacher"; // 如果未提供角色，默认为教师
    }

    // 调用服务层进行注册
    Map<String, Object> result = signupService.register(username, password, email, role);

    if ((boolean) result.get("success")) {
      // 注册成功，但不返回JWT令牌和用户敏感信息
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "注册成功，请登录");
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(result);
    }
  }

  /**
   * 检查用户名是否可用
   * 
   * @param username 用户名
   * @return 是否可用
   */
  @GetMapping("/check-username")
  public ResponseEntity<?> checkUsernameAvailable(@RequestParam String username) {
    boolean isAvailable = signupService.isUsernameAvailable(username);
    Map<String, Object> result = new HashMap<>();
    result.put("available", isAvailable);
    return ResponseEntity.ok(result);
  }

  /**
   * 检查邮箱是否可用
   * 
   * @param email 邮箱
   * @return 是否可用
   */
  @GetMapping("/check-email")
  public ResponseEntity<?> checkEmailAvailable(@RequestParam String email) {
    boolean isAvailable = signupService.isEmailAvailable(email);
    Map<String, Object> result = new HashMap<>();
    result.put("available", isAvailable);
    return ResponseEntity.ok(result);
  }
}
