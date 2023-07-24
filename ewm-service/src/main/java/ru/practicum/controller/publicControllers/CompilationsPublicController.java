package ru.practicum.controller.publicControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationsPublicController {

    private final CompilationService compilationService;


    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(name = "pinned", defaultValue = "false") boolean pinned,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive int size) {

        List<CompilationDto> list = compilationService.getAll(pinned, from, size);
        log.info("compilations list founded");

        return list;
    }


    @GetMapping("/{compId}")
    public CompilationDto getCompilations(@PathVariable int compId) {

        CompilationDto compilationDto = compilationService.get(compId);
        log.info("compilation found, id - {}", compId);

        return compilationDto;
    }
}
