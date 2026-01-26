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
        return "users-ws is running on " + environment.getProperty("local.server.port");
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        UserResponseDto userResponseDto = userService.createUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }
}
