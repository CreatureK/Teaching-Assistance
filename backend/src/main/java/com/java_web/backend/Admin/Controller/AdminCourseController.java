package com.java_web.backend.Admin.Controller;

import com.java_web.backend.Admin.Service.AdminCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/course")
public class AdminCourseController {
    @Autowired
    private AdminCourseService adminCourseService;
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingCourses() {
        return ResponseEntity.ok(adminCourseService.getPendingCourses());
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveCourse(@PathVariable Integer id) {
        return adminCourseService.updateCourseStatus(id, "approved");
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectCourse(@PathVariable Integer id) {
        return adminCourseService.updateCourseStatus(id, "rejected");
    }
    
    @GetMapping("/list")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(adminCourseService.getAllCourses());
    }
}
