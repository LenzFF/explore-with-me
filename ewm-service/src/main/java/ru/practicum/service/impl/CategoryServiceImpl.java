package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryMapper;
import ru.practicum.exception.DataAlreadyExistException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {

        if (categoryRepository.findByName(categoryDto.getName()) != null) {
            throw new DataAlreadyExistException("category already exist");
        }

        return CategoryMapper.toCategoryDto(categoryRepository
                .save(CategoryMapper.fromCategoryDto(categoryDto)));
    }


    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("category not exist, id - " + catId));

        String newName = categoryDto.getName();

        if (category.getName().equals(newName)) {
            return CategoryMapper.toCategoryDto(category);
        }

        if (categoryRepository.findByName(newName) != null) {
            throw new DataAlreadyExistException("category already exist");
        }

        category.setName(categoryDto.getName());
        categoryRepository.save(category);

        return CategoryMapper.toCategoryDto(category);
    }


    @Override
    @Transactional
    public void delete(long catId) {

        categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Category with id=" + catId + " was not found"));

        categoryRepository.deleteById(catId);
    }


    @Override
    public List<CategoryDto> getCategories(int from, int size) {

        PageRequest page = PageRequest.of(from / size, size, Sort.by("id").ascending());

        return categoryRepository.findAll(page).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }


    @Override
    public CategoryDto getById(long categoryId) {

        return CategoryMapper.toCategoryDto(categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("category not exist, id - " + categoryId)));
    }
}
