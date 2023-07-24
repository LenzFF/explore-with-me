package ru.practicum.dto.location;

import ru.practicum.model.Location;

public class LocationMapper {

    public static Location fromLocationDto(LocationDto locationDto) {
        Location location = new Location();
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }

    public static LocationDto toLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLon(location.getLon());
        locationDto.setLat(location.getLat());
        return locationDto;
    }
}
