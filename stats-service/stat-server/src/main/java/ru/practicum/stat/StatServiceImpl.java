package ru.practicum.stat;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stat.model.EndpointHitMapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatRepository statRepository;

    @Override
    public void writeStat(EndpointHitDto newHit) {
        statRepository.save(EndpointHitMapper.fromEndpointHitDto(newHit));
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {

        LocalDateTime startDecoded = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), DATE_TIME_FORMATTER);
        LocalDateTime endDecoded = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), DATE_TIME_FORMATTER);


        if (unique) {
            if (uris == null) {
                return statRepository.getStatsUniqueIp(startDecoded, endDecoded);
            } else {
                return statRepository.getStatsUniqueIpWithUris(startDecoded, endDecoded, uris);
            }
        } else {
            if (uris == null) {
                return statRepository.getStatsNotUniqueIp(startDecoded, endDecoded);
            } else {
                return statRepository.getStatsNotUniqueIpWithUris(startDecoded, endDecoded, uris);
            }
        }
    }
}
