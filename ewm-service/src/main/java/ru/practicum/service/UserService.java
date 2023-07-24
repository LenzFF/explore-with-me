package ru.practicum.service;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest newUserRequest);

    void delete(long userId);

    List<UserDto> getUsersWithParameters(List<Long> ids, int from, int size);

    UserDto get(long userId);

    List<UserDto> getAllUsersByIdIn(List<Long> ids);
}
