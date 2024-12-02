package com.example.api.response;

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
public class CategoryResponseDTO {

    private Integer id;
    private String name;

    public Category toCategory() {
        var category = new Category();
        category.setId(getId());
        category.setName(getName());
        category.setBooks(Collections.emptySet());
        return category;
    }
    
    public static Set<Category> toCategories(Collection<CategoryResponseDTO> categoriesDTO) {
        if (categoriesDTO == null) {
            return Collections.emptySet();
        }
        return categoriesDTO.stream()
                .map(CategoryResponseDTO::toCategory)
                .collect(Collectors.toSet());
    }

    public static CategoryResponseDTO fromCategory(Category category) {
        var categoryDTO = new CategoryResponseDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }

    public static Set<CategoryResponseDTO> fromCategories(Collection<Category> categories) {
        if (categories == null) {
            return Collections.emptySet();
        }
        return categories.stream()
                .map(CategoryResponseDTO::fromCategory)
                .collect(Collectors.toSet());
    }
}
