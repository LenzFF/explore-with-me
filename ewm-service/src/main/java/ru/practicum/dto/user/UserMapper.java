package ru.practicum.dto.user;

import ru.practicum.model.User;

public class UserMapper {

    public static User fromUserDto(UserDto userDto) {
        return new User(userDto.getId(),
                userDto.getEmail(),
                userDto.getName(),
                userDto.getRating());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getEmail(),
                user.getId(),
                user.getName(),
                user.getRating());
    }

    public static User fromNewUserRequestDto(NewUserRequest newUserRequest) {
        User user = new User();
        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());
        return user;
    }

    public static UserShortDto toShortDto(User user) {
        return new UserShortDto(user.getId(),
                user.getName());
    }
}
