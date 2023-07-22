package ru.practicum.dto.category;

import ru.practicum.model.Category;

public class CategoryMapper {
    public static Category fromCategoryDto(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(),
                categoryDto.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(),
                category.getName());
    }
}
