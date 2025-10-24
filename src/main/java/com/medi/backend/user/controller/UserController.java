package com.medi.backend.user.controller;


import com.medi.backend.user.dto.User;
import com.medi.backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 전체 사용자 조회 (DB 연결 테스트용)
     * GET http://localhost:8080/api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("=== 전체 사용자 조회 요청 ===");
        List<User> users = userService.getAllUsers();
        System.out.println("조회된 사용자 수: " + users.size());
        return ResponseEntity.ok(users);
    }

}