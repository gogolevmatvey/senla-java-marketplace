package org.example.controller;

import org.example.dto.EmailDto;
import org.example.dto.SaleHistoryDto;
import org.example.dto.UserDto;
import org.example.dto.UsernameDto;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUserData(@RequestBody UserDto userDto) throws UserAlreadyExistsException {
        UserDto updatedUser = userService.updateUserData(userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/username")
    public ResponseEntity<?> changeUsername(@RequestBody UsernameDto usernameDto) throws UserAlreadyExistsException {
        UserDto updatedUser = userService.changeUsername(usernameDto.getUsername());
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/email")
    public ResponseEntity<?> changeEmail(@RequestBody EmailDto emailDto) throws UserAlreadyExistsException {
        UserDto updatedUser = userService.changeEmail(emailDto.getNewEmail());
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile avatar) {
        UserDto updatedUser = userService.setAvatar(avatar);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{username}/sales")
    public ResponseEntity<List<SaleHistoryDto>> getUserSales(@PathVariable("username") String username) {
        List<SaleHistoryDto> sales = userService.getUserSales(username);
        return ResponseEntity.ok(sales);
    }
}
