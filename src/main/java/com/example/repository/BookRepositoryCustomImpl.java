package com.example.repository;

import com.example.model.Author;
import com.example.model.Book;
import com.example.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;

@RequiredArgsConstructor
@Slf4j
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Book saveCascading(@NotNull Book book) {

        // Persisting new categories
        var categories = saveCategoriesWhichNotExist(book.getCategories());
        book.setCategories(new HashSet<>(categories));

        // Persisting new authors
        var authors = saveAuthorsWhichNotExist(book.getAuthors());
        book.setAuthors(new HashSet<>(authors));

        if (book.isNew()) {
            entityManager.persist(book);
            return book;
        }

        return entityManager.merge(book);
    }

    private Collection<Category> saveCategoriesWhichNotExist(@NotNull Collection<Category> categories) {

        log.info("Saving categories for a book as cascade operation");

        if (categories.isEmpty()) {
            return categories;
        }

        var categoriesNames = categories.stream()
                .map(Category::getName)
                .distinct()
                .toList();

        var categoriesExisting = categoryRepository.findAllByNameIn(categoriesNames);

        var categoriesNew = new HashSet<>(categories);
        categoriesNew.removeAll(categoriesExisting);
        if (categoriesNew.isEmpty()) {
            return categoriesExisting;
        }

        var categoriesPersisted = categoryRepository.saveAll(categoriesNew);
        categoryRepository.flush();

        categoriesPersisted.addAll(categoriesExisting);

        return categoriesPersisted;
    }

    private Collection<Author> saveAuthorsWhichNotExist(@NotNull Collection<Author> authors) {

        if (authors.isEmpty()) {
            return authors;
        }

        log.info("Saving authors for a book as cascade operation");

        var authorsNames = authors.stream()
                .map(Author::getName)
                .distinct()
                .toList();

        var authorsExisting = authorRepository.findAllByNameIn(authorsNames);

        var authorsNew = new HashSet<>(authors);
        authorsNew.removeAll(authorsExisting);
        if (authorsNew.isEmpty()) {
            return authorsExisting;
        }

        var authorsPersisted = authorRepository.saveAll(authorsNew);
        authorRepository.flush();

        authorsPersisted.addAll(authorsExisting);

        return authorsPersisted;
    }
}
