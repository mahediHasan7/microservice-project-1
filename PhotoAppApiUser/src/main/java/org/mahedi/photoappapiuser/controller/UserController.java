package org.mahedi.photoappapiuser.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mahedi.photoappapiuser.dto.UserCreateDto;
import org.mahedi.photoappapiuser.dto.UserResponseDto;
import org.mahedi.photoappapiuser.service.UserService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final Environment environment;
    private final UserService userService;

    @GetMapping("/status/check")
    public String getStatus() {
        return "users-ws is running on " + environment.getProperty("local.server.port") + "\nsecret token: " + environment.getProperty("token.secret");
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        UserResponseDto userResponseDto = userService.createUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long userId) {
        UserResponseDto userResponseDto = userService.findUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUserCreationException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
