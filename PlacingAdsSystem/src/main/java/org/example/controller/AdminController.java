package org.example.controller;

import org.example.dto.AddBalanceDto;
import org.example.dto.UserDto;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/balance")
    public ResponseEntity<?> addBalance(@RequestBody AddBalanceDto addBalanceDto) {
        UserDto updatedUser = userService.addBalance(addBalanceDto.getUsername(), addBalanceDto.getAmount());
        return ResponseEntity.ok(updatedUser);
    }
}
