package org.mahedi.photoappapiuser.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mahedi.photoappapiuser.client.AlbumServiceClient;
import org.mahedi.photoappapiuser.dto.AlbumResponseDto;
import org.mahedi.photoappapiuser.dto.UserCreateDto;
import org.mahedi.photoappapiuser.dto.UserResponseDto;
import org.mahedi.photoappapiuser.entity.User;
import org.mahedi.photoappapiuser.mapper.UserMapper;
import org.mahedi.photoappapiuser.repository.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;
    private final Environment environment;
    private final AlbumServiceClient albumServiceClient;

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
    public UserResponseDto findUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        /*
            // calling Album Microservice using RestTemplate
            String albumUrl = environment.getProperty("user.albums.url") + userId;
            ResponseEntity<List<AlbumResponseDto>> albumsResponse = restTemplate.exchange(
                    albumUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AlbumResponseDto>>() {
                    });
            List<AlbumResponseDto> albums = albumsResponse.getBody();
         */

        List<AlbumResponseDto> albums = albumServiceClient.getAlbumsByUserId(userId);
        return userMapper.toUserResponseDtoWithAlbums(user, albums);
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


