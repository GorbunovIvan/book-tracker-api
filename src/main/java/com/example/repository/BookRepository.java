package com.example.repository;

import com.example.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    @Override
    @NonNull
    @EntityGraph(attributePaths = { "categories", "authors" })
    List<Book> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = { "categories", "authors" })
    Optional<Book> findById(@NonNull Long id);

    @EntityGraph(attributePaths = { "categories", "authors" })
    Optional<Book> findByTitle(@NonNull String title);
}
