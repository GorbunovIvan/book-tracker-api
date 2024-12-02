package com.example.service;

import com.example.api.request.AuthorRequestDTO;
import com.example.api.request.BookRequestDTO;
import com.example.api.request.CategoryRequestDTO;
import com.example.api.response.BookResponseDTO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServicesFacade {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;

    public List<BookResponseDTO> getAllBooks() {
        return bookService.getAll();
    }

    public BookResponseDTO getBookById(@NotNull Long id) {
        return bookService.getById(id);
    }

    public BookResponseDTO getBookByTitle(@NotNull String title) {
        return bookService.getByTitle(title);
    }

    public Collection<BookResponseDTO> getBooksByCategory(@NotNull String categoryName) {
        return categoryService.getBooksByCategoryName(categoryName);
    }

    public Collection<BookResponseDTO> getBooksByAuthor(@NotNull String authorName) {
        return authorService.getBooksByAuthorName(authorName);
    }

    public BookResponseDTO createBook(@NotNull BookRequestDTO bookRequestDTO) {
        return bookService.create(bookRequestDTO);
    }

    public BookResponseDTO updateBook(@NotNull Long id,
                                          @NotNull BookRequestDTO bookRequestDTO) {
        return bookService.update(id, bookRequestDTO);
    }

    public void deleteBookById(@NotNull Long id) {
        bookService.deleteById(id);
    }

    public void addCategoryToBook(@NotNull Long bookId,
                                   @NotNull CategoryRequestDTO categoryRequestDTO) {
        categoryService.addCategoryToBook(bookId, categoryRequestDTO);
    }

    public boolean removeCategoryFromBook(@NotNull Long bookId,
                                          @NotNull CategoryRequestDTO categoryRequestDTO) {
        return categoryService.removeCategoryFromBook(bookId, categoryRequestDTO);
    }

    public void updateCategoriesForBook(@NotNull Long bookId,
                                         @NotNull List<CategoryRequestDTO> categoriesRequestDTO) {
        categoryService.updateCategoriesForBook(bookId, categoriesRequestDTO);
    }

    public void addAuthorToBook(@NotNull Long bookId,
                                 @NotNull AuthorRequestDTO authorRequestDTO) {
        authorService.addAuthorToBook(bookId, authorRequestDTO);
    }

    public boolean removeAuthorFromBook(@NotNull Long bookId,
                                      @NotNull AuthorRequestDTO authorRequestDTO) {
        return authorService.removeAuthorFromBook(bookId, authorRequestDTO);
    }

    public void updateAuthorsForBook(@NotNull Long bookId,
                                         @NotNull List<AuthorRequestDTO> authorsRequestDTO) {
        authorService.updateAuthorsForBook(bookId, authorsRequestDTO);
    }
}
