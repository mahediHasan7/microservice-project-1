package org.mahedi.photoappapiuser.mapper;

import org.mahedi.photoappapiuser.dto.UserCreateDto;
import org.mahedi.photoappapiuser.dto.UserResponseDto;
import org.mahedi.photoappapiuser.dto.UserUpdateDto;
import org.mahedi.photoappapiuser.entity.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public User toEntity(UserCreateDto userCreateDto) {
        return modelMapper.map(userCreateDto, User.class);
    }

    public UserResponseDto toUserResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

    public void updateEntityFromDto(UserUpdateDto userUpdateDto, User user) {
        if (userUpdateDto.getFirstName() != null) {
            user.setFirstName(userUpdateDto.getFirstName());
        }
        if (userUpdateDto.getLastName() != null) {
            user.setLastName(userUpdateDto.getLastName());
        }
        if (userUpdateDto.getEmail() != null) {
            user.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getPassword() != null) {
            user.setPassword(userUpdateDto.getPassword());
        }
    }
}
