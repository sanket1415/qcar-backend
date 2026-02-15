package com.qcar.controller;

import com.qcar.dto.LoginRequest;
import com.qcar.model.Admin;
import com.qcar.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Admin> admin = adminRepository.findByUsername(loginRequest.getUsername());
        
        if (admin.isPresent() && admin.get().getPassword().equals(loginRequest.getPassword())) {
            session.setAttribute("adminId", admin.get().getId());
            session.setAttribute("username", admin.get().getUsername());
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("username", admin.get().getUsername());
            return ResponseEntity.ok(response);
        }
        
        response.put("success", false);
        response.put("message", "Invalid credentials");
        return ResponseEntity.status(401).body(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Object>> checkAuth(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Object adminId = session.getAttribute("adminId");
        
        if (adminId != null) {
            response.put("authenticated", true);
            response.put("username", session.getAttribute("username"));
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }
}
