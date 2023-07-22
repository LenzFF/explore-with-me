package ru.practicum.controller.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {

    private final CategoryService categoryService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {

        CategoryDto newCategory = categoryService.createCategory(categoryDto);
        log.info("category added : {}", newCategory);
        return newCategory;
    }


    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable(name = "catId") long catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {

        CategoryDto updatedCategoryDto = categoryService.updateCategory(catId, categoryDto);
        log.info("category changed : {}", updatedCategoryDto);

        return updatedCategoryDto;
    }


    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") long catId) {

        categoryService.delete(catId);
        log.info("category deleted, id - {}", catId);
    }
}
