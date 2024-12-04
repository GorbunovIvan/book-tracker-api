package com.example.api.response;

import com.example.model.Category;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CategoryResponseDTOTest {
    
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnCategoryWhenToCategory() {

        var categoryDTO = easyRandom.nextObject(CategoryResponseDTO.class);

        var categoryReceived = categoryDTO.toCategory();
        assertEquals(categoryDTO.getName(), categoryReceived.getName());
        assertEquals(categoryDTO.getId(), categoryReceived.getId());
        assertNotNull(categoryReceived.getBooks());
        assertTrue(categoryReceived.getBooks().isEmpty());
    }

    @Test
    void shouldReturnCategoriesWhenToCategories() {

        var categoriesDTO = easyRandom.objects(CategoryResponseDTO.class, 4).collect(Collectors.toList());

        var categoriesReceived = CategoryResponseDTO.toCategories(categoriesDTO);

        assertNotNull(categoriesReceived);
        assertEquals(categoriesReceived.size(), categoriesDTO.size());

        var categoriesDTONames = categoriesDTO.stream().map(CategoryResponseDTO::getName).collect(Collectors.toSet());
        var categoriesReceivedNames = categoriesReceived.stream().map(Category::getName).collect(Collectors.toSet());

        assertEquals(categoriesReceivedNames, categoriesDTONames);
    }

    @Test
    void shouldReturnCategoriesResponseDTOWhenFromCategory() {

        var category = easyRandom.nextObject(Category.class);

        var categoryResponseDTO = CategoryResponseDTO.fromCategory(category);
        assertEquals(category.getId(), categoryResponseDTO.getId());
        assertEquals(category.getName(), categoryResponseDTO.getName());
    }

    @Test
    void shouldReturnCollectionOfCategoriesResponseDTOWhenFromCategories() {

        var categories = easyRandom.objects(Category.class, 4).collect(Collectors.toList());

        var categoriesResponseDTO = CategoryResponseDTO.fromCategories(categories);

        assertNotNull(categoriesResponseDTO);
        assertEquals(categoriesResponseDTO.size(), categories.size());

        var categoriesNames = categories.stream().map(Category::getName).collect(Collectors.toSet());
        var categoriesResponseDTONames = categoriesResponseDTO.stream().map(CategoryResponseDTO::getName).collect(Collectors.toSet());

        assertEquals(categoriesResponseDTONames, categoriesNames);
    }
}