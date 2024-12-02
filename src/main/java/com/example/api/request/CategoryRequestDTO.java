package com.example.api.request;

import com.example.model.Category;
import lombok.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class CategoryRequestDTO {

    private String name;

    public Category toCategory() {
        var category = new Category();
        category.setName(getName());
        category.setBooks(Collections.emptySet());
        return category;
    }
    
    public static Set<Category> toCategories(Collection<CategoryRequestDTO> categoriesDTO) {
        if (categoriesDTO == null) {
            return Collections.emptySet();
        }
        return categoriesDTO.stream()
                .map(CategoryRequestDTO::toCategory)
                .collect(Collectors.toSet());
    }

    public static CategoryRequestDTO fromCategory(Category category) {
        var categoryDTO = new CategoryRequestDTO();
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }

    public static Set<CategoryRequestDTO> fromCategories(Collection<Category> categories) {
        if (categories == null) {
            return Collections.emptySet();
        }
        return categories.stream()
                .map(CategoryRequestDTO::fromCategory)
                .collect(Collectors.toSet());
    }
}
