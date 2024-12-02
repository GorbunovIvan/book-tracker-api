package com.example.repository;

import com.example.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @EntityGraph(value = "graph.CategoryBooksAuthors")
    Optional<Category> findByName(@NonNull String name);

    List<Category> findAllByNameIn(@NonNull List<String> names);
}
