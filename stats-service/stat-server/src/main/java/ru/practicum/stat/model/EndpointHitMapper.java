package ru.practicum.stat.model;

import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointHitMapper {

    public static EndpointHit fromEndpointHitDto(EndpointHitDto hit) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new EndpointHit(hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                LocalDateTime.parse(hit.getTimestamp(), dateTimeFormatter));
    }
}
