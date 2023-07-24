package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.exception.DataAlreadyExistException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.findByName(newUserRequest.getName()) != null) {
            throw new DataAlreadyExistException("this name is already used - " + newUserRequest.getName());
        }

        return UserMapper.toUserDto(userRepository
                .save(UserMapper.fromNewUserRequestDto(newUserRequest)));
    }


    @Transactional
    public void delete(long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("user not found, id - " + userId));

        userRepository.deleteById(userId);
    }


    @Override
    public UserDto get(long userId) {

        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("user not found, id - " + userId)));
    }


    @Override
    public List<UserDto> getUsersWithParameters(List<Long> ids, int from, int size) {

        if (ids == null || ids.isEmpty()) {
            PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());

            return userRepository.findAll(page)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }


        return getAllUsersByIdIn(ids);
    }


    @Override
    public List<UserDto> getAllUsersByIdIn(List<Long> ids) {

        return userRepository.findAllByIdIn(ids)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}

