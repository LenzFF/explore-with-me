package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.dto.location.LocationMapper;
import ru.practicum.model.Location;
import ru.practicum.repository.LocationRepository;
import ru.practicum.service.LocationService;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;


    @Transactional(readOnly = true)
    @Override
    public Location create(LocationDto locationDto) {

        return locationRepository
                .save(LocationMapper.fromLocationDto(locationDto));
    }
}
