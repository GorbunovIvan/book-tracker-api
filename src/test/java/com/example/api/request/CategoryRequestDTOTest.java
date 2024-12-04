package com.example.api.request;

import com.example.model.Category;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRequestDTOTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnCategoryWhenToCategory() {

        var categoryDTO = easyRandom.nextObject(CategoryRequestDTO.class);

        var categoryReceived = categoryDTO.toCategory();
        assertEquals(categoryDTO.getName(), categoryReceived.getName());
        assertNotNull(categoryReceived.getBooks());
        assertTrue(categoryReceived.getBooks().isEmpty());
    }

    @Test
    void shouldReturnCategoriesWhenToCategories() {

        var categoriesDTO = easyRandom.objects(CategoryRequestDTO.class, 4).collect(Collectors.toList());

        var categoriesReceived = CategoryRequestDTO.toCategories(categoriesDTO);

        assertNotNull(categoriesReceived);
        assertEquals(categoriesReceived.size(), categoriesDTO.size());

        var categoriesDTONames = categoriesDTO.stream().map(CategoryRequestDTO::getName).collect(Collectors.toSet());
        var categoriesReceivedNames = categoriesReceived.stream().map(Category::getName).collect(Collectors.toSet());

        assertEquals(categoriesReceivedNames, categoriesDTONames);
    }

    @Test
    void shouldReturnCategoriesRequestDTOWhenFromCategory() {

        var category = easyRandom.nextObject(Category.class);

        var categoryRequestDTO = CategoryRequestDTO.fromCategory(category);
        assertEquals(category.getName(), categoryRequestDTO.getName());
    }

    @Test
    void shouldReturnCollectionOfCategoriesRequestDTOWhenFromCategories() {

        var categories = easyRandom.objects(Category.class, 4).collect(Collectors.toList());

        var categoriesRequestDTO = CategoryRequestDTO.fromCategories(categories);

        assertNotNull(categoriesRequestDTO);
        assertEquals(categoriesRequestDTO.size(), categories.size());

        var categoriesNames = categories.stream().map(Category::getName).collect(Collectors.toSet());
        var categoriesRequestDTONames = categoriesRequestDTO.stream().map(CategoryRequestDTO::getName).collect(Collectors.toSet());

        assertEquals(categoriesRequestDTONames, categoriesNames);
    }
}