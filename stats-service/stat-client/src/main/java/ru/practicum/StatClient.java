package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatClient {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RestTemplate rest;

    public void StatsClient(@Value("${client.url}") String baseUrl) {
        RestTemplateBuilder builder = new RestTemplateBuilder();

        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                .build();
    }

    public List<ViewStatsDto> getStats(LocalDateTime startTime, LocalDateTime endTime, @Nullable String[] uris, @Nullable Boolean unique) {
        String startString = startTime.format(DATE_TIME_FORMATTER);
        String endString = endTime.format(DATE_TIME_FORMATTER);
        String startEncoded = URLEncoder.encode(startString, StandardCharsets.UTF_8);
        String endEncoded = URLEncoder.encode(endString, StandardCharsets.UTF_8);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", startEncoded);
        parameters.put("end", endEncoded);

        StringBuilder sb = new StringBuilder();
        sb.append("/stats?start={start}&end={end}");
        if (uris != null) {
            parameters.put("uris", uris);
            sb.append("&uris={uris}");
        }
        if (unique != null) {
            parameters.put("unique", unique);
            sb.append("&unique={unique}");
        }
        return makeAndSendGetStatsRequest(HttpMethod.GET, sb.toString(), parameters, null);
    }

    public ResponseEntity<String> postStat(EndpointHitDto hit) {
        return makeAndSendPostStatRequest(HttpMethod.POST, "/hit", null, hit);
    }


    private <T> List<ViewStatsDto> makeAndSendGetStatsRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<List<ViewStatsDto>> statServerResponse;
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<List<ViewStatsDto>>() {
                }, parameters);
            } else {
                statServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<List<ViewStatsDto>>() {
                });
            }
        } catch (HttpStatusCodeException e) {
            return null;
        }
        return statServerResponse.getBody();
    }

    private <T> ResponseEntity<String> makeAndSendPostStatRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<String> statServerResponse;
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, method, requestEntity, String.class, parameters);
            } else {
                statServerResponse = rest.exchange(path, method, requestEntity, String.class);
            }
        } catch (HttpStatusCodeException e) {
            return null;
        }
        return statServerResponse;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
