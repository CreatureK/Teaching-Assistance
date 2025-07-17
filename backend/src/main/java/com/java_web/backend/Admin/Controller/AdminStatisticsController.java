package com.java_web.backend.Admin.Controller;

import com.java_web.backend.Admin.Service.AdminStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {
    @Autowired
    private AdminStatisticsService adminStatisticsService;
    
    @GetMapping("/users")
    public ResponseEntity<?> getUserStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getUserStatistics());
    }
    
    @GetMapping("/courses")
    public ResponseEntity<?> getCourseStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getCourseStatistics());
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        return ResponseEntity.ok(adminStatisticsService.getDashboardData());
    }
}
