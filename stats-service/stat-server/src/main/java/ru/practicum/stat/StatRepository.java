package ru.practicum.stat;

import ru.practicum.dto.ViewStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stat.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.dto.ViewStatsDto(hit.app, hit.uri, count(hit.ip)) " +
            "from EndpointHit as hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "group by hit.uri, hit.app " +
            "order by count(hit.id) desc")
    List<ViewStatsDto> getStatsNotUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStatsDto(hit.app, hit.uri, count(DISTINCT hit.ip)) " +
            "from EndpointHit as hit " +
            "where hit.timestamp between ?1 and ?2 " +
            "group by hit.uri, hit.app " +
            "order by count(hit.id) desc")
    List<ViewStatsDto> getStatsUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.ViewStatsDto(hit.app, hit.uri, count(hit.ip)) " +
            "from EndpointHit as hit " +
            "where hit.uri in ?3 " +
            "and hit.timestamp between ?1 and ?2 " +
            "group by hit.uri, hit.app " +
            "order by count(hit.id) desc")
    List<ViewStatsDto> getStatsNotUniqueIpWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.ViewStatsDto(hit.app, hit.uri, count(DISTINCT hit.ip)) " +
            "from EndpointHit as hit " +
            "where hit.uri in ?3 " +
            "and hit.timestamp between ?1 and ?2 " +
            "group by hit.uri, hit.app " +
            "order by count(hit.id) desc")
    List<ViewStatsDto> getStatsUniqueIpWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
