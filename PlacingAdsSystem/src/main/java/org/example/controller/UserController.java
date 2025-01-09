package org.example.controller;

import org.example.dto.UsernameDto;
import org.example.exceptions.UserAlreadyExistsException;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/username")
    public ResponseEntity<?> changeUsername(@RequestBody UsernameDto usernameDto)
            throws UserAlreadyExistsException {
        User updatedUser = userService.changeUsername(usernameDto.getNewUsername());
        return ResponseEntity.ok(updatedUser);
    }
}
