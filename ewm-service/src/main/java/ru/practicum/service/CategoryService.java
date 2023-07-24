package ru.practicum.service;

import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);

    void delete(long catId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getById(long categoryId);
}
