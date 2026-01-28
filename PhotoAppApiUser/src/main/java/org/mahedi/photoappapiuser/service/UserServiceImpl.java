package org.mahedi.photoappapiuser.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mahedi.photoappapiuser.dto.UserCreateDto;
import org.mahedi.photoappapiuser.dto.UserResponseDto;
import org.mahedi.photoappapiuser.entity.User;
import org.mahedi.photoappapiuser.mapper.UserMapper;
import org.mahedi.photoappapiuser.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        // Encrypt password before save
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        try {
            User savedUser = userRepository.save(user);
            return userMapper.toUserResponseDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("User could not be saved: " + e.getMessage());
        }
    }

    @Override
    public UserResponseDto findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        }
        return userMapper.toUserResponseDto(user);
    }


    @Override
    public @NonNull UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // here in my case, username is email
        User user = userRepository.findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with email: " + username + " not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}


