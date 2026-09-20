package com.healthai.mapper;

import com.healthai.dto.UserDto;
import com.healthai.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAge(),
                user.getGender(),
                user.getPreferredLanguage(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
