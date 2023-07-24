package ru.practicum.dto.user;

import ru.practicum.model.User;

public class UserMapper {

    public static User fromUserDto(UserDto userDto) {
        return new User(userDto.getId(),
                userDto.getEmail(),
                userDto.getName());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getEmail(),
                user.getId(),
                user.getName());
    }

    public static User fromNewUserRequestDto(NewUserRequest newUserRequest) {
        return new User(0L,
                newUserRequest.getEmail(),
                newUserRequest.getName());
    }

    public static UserShortDto toShortDto(User user) {
        return new UserShortDto(user.getId(),
                user.getName());
    }
}
