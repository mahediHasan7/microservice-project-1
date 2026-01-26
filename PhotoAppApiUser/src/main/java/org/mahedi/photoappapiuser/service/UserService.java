package org.mahedi.photoappapiuser.service;

import org.mahedi.photoappapiuser.dto.UserCreateDto;
import org.mahedi.photoappapiuser.dto.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserResponseDto createUser(UserCreateDto userCreateDto);

    UserResponseDto findUserByEmail(String email);
}
