package com.example.service;

import com.example.api.request.CategoryRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.model.Book;
import com.example.repository.BookRepository;
import com.example.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public Collection<BookResponseDTO> getBooksByCategoryName(@NotNull String categoryName) {
        log.info("Retrieving books by category '{}'", categoryName);
        var categoryOptional = categoryRepository.findByName(categoryName);
        if (categoryOptional.isEmpty()) {
            return Collections.emptySet();
        }
        var category = categoryOptional.get();
        return BookResponseDTO.fromBooks(category.getBooks());
    }

    @Transactional
    public void addCategoryToBook(@NotNull Long bookId,
                                  @NotNull CategoryRequestDTO categoryRequestDTO) {

        log.info("Adding category to the book with id '{}', new category - {}", bookId, categoryRequestDTO);

        var categoryName = categoryRequestDTO.getName();
        if (Objects.requireNonNullElse(categoryName, "").isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        var book = findBookByIdOrThrowException(bookId);

        // Checking if the book already has the category with the same name
        var bookHasThisCategoryAlready = book.getCategories()
                .stream()
                .anyMatch(category -> Objects.equals(categoryName, category.getName()));

        if (bookHasThisCategoryAlready) {
            log.warn("The book with id '{}' already has the category with name '{}'", bookId, categoryName);
            return;
        }

        var category = categoryRequestDTO.toCategory();
        book.addCategory(category);

        bookRepository.saveCascading(book);
    }

    @Transactional
    public boolean removeCategoryFromBook(@NotNull Long bookId,
                                         @NotNull CategoryRequestDTO categoryRequestDTO) {

        log.info("Removing category from the book with id '{}', category - {}", bookId, categoryRequestDTO);

        var book = findBookByIdOrThrowException(bookId);

        var category = categoryRequestDTO.toCategory();

        var resultOfRemoval = book.removeCategory(category);
        if (!resultOfRemoval) {
            log.warn("Category '{}' for the book with id '{}' not found. The categories of the book are: {}",
                    category, bookId, book.getCategories());
            return false;
        }

        bookRepository.saveCascading(book);
        return true;
    }

    @Transactional
    public void updateCategoriesForBook(@NotNull Long bookId,
                                        @NotNull Collection<CategoryRequestDTO> categoriesRequestDTO) {

        log.info("Updating categories for the book with id '{}', new categories - {}", bookId, categoriesRequestDTO);

        var book = findBookByIdOrThrowException(bookId);

        var categories = CategoryRequestDTO.toCategories(categoriesRequestDTO);

        book.setCategories(categories);

        bookRepository.saveCascading(book);
    }

    private Book findBookByIdOrThrowException(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Book with id '%d' not found", bookId)));
    }
}
