package com.example.repository;

import com.example.model.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @EntityGraph(value = "graph.AuthorBooksCategories")
    Optional<Author> findByName(@NonNull String name);

    List<Author> findAllByNameIn(@NonNull List<String> names);
}
