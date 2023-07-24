package ru.practicum.controller.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationService compilationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {

        CompilationDto compilationDto = compilationService.create(newCompilationDto);
        log.info("Events compilation created, id - {}", compilationDto.getId());

        return compilationDto;
    }


    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") @Positive long compId) {

        compilationService.deleteById(compId);
        log.info("Events compilation deleted, id - {}", compId);
    }


    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable(name = "compId") @Positive long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateRequest) {

        CompilationDto compilationDto = compilationService.update(compId, updateRequest);
        log.info("Events compilation updated, id - {}", compId);
        return compilationDto;
    }
}
