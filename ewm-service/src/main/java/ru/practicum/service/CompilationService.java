package ru.practicum.service;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;


public interface CompilationService {

    CompilationDto create(NewCompilationDto newCompilationDto);

    void deleteById(long compId);

    CompilationDto update(long compId, UpdateCompilationRequest updateRequest);

    List<CompilationDto> getAll(boolean pinned, int from, int size);

    CompilationDto get(long compId);
}
