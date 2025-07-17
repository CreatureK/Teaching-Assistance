package com.java_web.backend.Admin.Controller;

import com.java_web.backend.Admin.Service.AdminRestrictionService;
import com.java_web.backend.Common.Entity.Restriction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/restriction")
public class AdminRestrictionController {
    @Autowired
    private AdminRestrictionService adminRestrictionService;
    
    @PostMapping("/add")
    public ResponseEntity<?> addRestriction(@RequestBody Restriction restriction) {
        return adminRestrictionService.addRestriction(restriction);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeRestriction(@PathVariable Integer id) {
        return adminRestrictionService.removeRestriction(id);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRestrictions(@PathVariable Integer userId) {
        return ResponseEntity.ok(adminRestrictionService.getUserRestrictions(userId));
    }
} 