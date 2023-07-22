package ru.practicum.service;

import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.Location;

public interface LocationService {

    Location create(LocationDto locationDto);
}
