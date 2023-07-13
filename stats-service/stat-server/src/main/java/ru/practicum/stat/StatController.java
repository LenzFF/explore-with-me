package ru.practicum.stat;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<String> postStat(@RequestBody @Valid EndpointHitDto newHit) {

        statService.writeStat(newHit);
        log.info("Stats-service: сохранена статистика для эндпоинта: {}", newHit.getUri());
        return new ResponseEntity<>("Информация сохранена", HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start, @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        log.info("Stats-service: отправлена статистика для эндпоинтов: {}", uris);
        return statService.getStats(start, end, uris, unique);
    }
}
