package com.example.controller;

import com.example.api.request.AuthorRequestDTO;
import com.example.api.request.BookRequestDTO;
import com.example.api.request.CategoryRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.service.BookServicesFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    
    private final BookServicesFacade bookService;

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        var books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        var book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<BookResponseDTO> getBookByTitle(@PathVariable String title) {
        var book = bookService.getBookByTitle(title);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Collection<BookResponseDTO>> getBooksByCategory(@PathVariable("category") String categoryName) {
        var books = bookService.getBooksByCategory(categoryName);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<Collection<BookResponseDTO>> getBooksByAuthor(@PathVariable("author") String authorName) {
        var books = bookService.getBooksByAuthor(authorName);
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO book) {
        var bookResponse = bookService.createBook(book);
        return ResponseEntity.ok(bookResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id,
                                                      @RequestBody BookRequestDTO book) {
        var bookResponse = bookService.updateBook(id, book);
        return ResponseEntity.ok(bookResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
    }

    @PutMapping("/{id}/add-category")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addCategoryToBook(@PathVariable("id") Long bookId,
                                  @RequestBody CategoryRequestDTO category) {
        bookService.addCategoryToBook(bookId, category);
    }

    @DeleteMapping("/{id}/delete-category")
    public ResponseEntity<Boolean> removeCategoryFromBook(@PathVariable("id") Long bookId,
                                                          @RequestBody CategoryRequestDTO category) {
        var result = bookService.removeCategoryFromBook(bookId, category);
        if (!result) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    @PutMapping("/{id}/update-categories")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateCategoriesForBooks(@PathVariable("id") Long bookId,
                                         @RequestBody List<CategoryRequestDTO> categories) {
        bookService.updateCategoriesForBook(bookId, categories);
    }

    @PutMapping("/{id}/add-author")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addAuthorToBook(@PathVariable("id") Long bookId,
                                @RequestBody AuthorRequestDTO authorRequestDTO) {
        bookService.addAuthorToBook(bookId, authorRequestDTO);
    }

    @DeleteMapping("/{id}/delete-author")
    public ResponseEntity<Boolean> removeAuthorFromBook(@PathVariable("id") Long bookId,
                                                         @RequestBody AuthorRequestDTO author) {
        var result = bookService.removeAuthorFromBook(bookId, author);
        if (!result) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    @PutMapping("/{id}/update-authors")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateAuthorsForBooks(@PathVariable("id") Long bookId,
                                      @RequestBody List<AuthorRequestDTO> authors) {
        bookService.updateAuthorsForBook(bookId, authors);
    }
}
