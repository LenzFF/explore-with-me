package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class StatClient {

    private final RestTemplate rest;
    private final String baseUrl;


    public StatClient(@Value("${stat-server.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.rest = getRestTemplate();
    }

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
                .build();
    }

    public List<ViewStatsDto> getStats(String startTime, String endTime, @Nullable String[] uris, @Nullable Boolean unique) {

        String startEncoded = URLEncoder.encode(startTime, StandardCharsets.UTF_8);
        String endEncoded = URLEncoder.encode(endTime, StandardCharsets.UTF_8);

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
        return makeAndSendGetStatsRequest(sb.toString(), parameters).orElse(Collections.EMPTY_LIST);
    }

    public void postStat(EndpointHitDto hit) {
        makeAndSendPostStatRequest("/hit", null, hit);
    }


    private <T> Optional<List<ViewStatsDto>> makeAndSendGetStatsRequest(String path, @Nullable Map<String, Object> parameters) {
        HttpEntity<T> requestEntity = new HttpEntity<>(null, defaultHeaders());

        ResponseEntity<List<ViewStatsDto>> statServerResponse;
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<ViewStatsDto>>() {
                }, parameters);
            } else {
                statServerResponse = rest.exchange(path, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<ViewStatsDto>>() {
                });
            }
        } catch (HttpStatusCodeException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(statServerResponse.getBody());
    }

    private <T> ResponseEntity<String> makeAndSendPostStatRequest(String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<String> statServerResponse;
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, HttpMethod.POST, requestEntity, String.class, parameters);
            } else {
                statServerResponse = rest.exchange(path, HttpMethod.POST, requestEntity, String.class);
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
