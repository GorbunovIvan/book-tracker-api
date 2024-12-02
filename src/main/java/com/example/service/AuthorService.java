package com.example.service;

import com.example.api.request.AuthorRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.model.Book;
import com.example.repository.AuthorRepository;
import com.example.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    
    public Collection<BookResponseDTO> getBooksByAuthorName(@NotNull String authorName) {
        log.info("Retrieving books by author '{}'", authorName);
        var authorOptional = authorRepository.findByName(authorName);
        if (authorOptional.isEmpty()) {
            return Collections.emptyList();
        }
        var author = authorOptional.get();
        return BookResponseDTO.fromBooks(author.getBooks());
    }

    @Transactional
    public void addAuthorToBook(@NotNull Long bookId,
                                @NotNull AuthorRequestDTO authorRequestDTO) {

        log.info("Adding author to the book with id '{}', new author - {}", bookId, authorRequestDTO);

        var authorName = authorRequestDTO.getName();
        if (Objects.requireNonNullElse(authorName, "").isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty");
        }

        var book = findBookByIdOrThrowException(bookId);

        // Checking if the book already has the author with the same name
        var bookHasThisAuthorAlready = book.getAuthors()
                .stream()
                .anyMatch(author -> Objects.equals(authorName, author.getName()));

        if (bookHasThisAuthorAlready) {
            log.warn("The book with id '{}' already has the author with name '{}'", bookId, authorName);
            return;
        }

        // If the author with the same name already exists in DB, then pick it instead of creating new
        var author = authorRequestDTO.toAuthor();
        book.addAuthor(author);

        bookRepository.saveCascading(book);
    }

    @Transactional
    public boolean removeAuthorFromBook(@NotNull Long bookId,
                                         @NotNull AuthorRequestDTO authorRequestDTO) {

        log.info("Removing author from the book with id '{}', author - {}", bookId, authorRequestDTO);

        var book = findBookByIdOrThrowException(bookId);

        var author = authorRequestDTO.toAuthor();

        var resultOfRemoval = book.removeAuthor(author);
        if (!resultOfRemoval) {
            log.warn("Author '{}' for the book with id '{}' not found. The authors of the book are: {}",
                    author, bookId, book.getAuthors());
            return false;
        }

        bookRepository.saveCascading(book);
        return true;
    }

    @Transactional
    public void updateAuthorsForBook(@NotNull Long bookId,
                                        @NotNull Collection<AuthorRequestDTO> authorsRequestDTO) {

        log.info("Updating authors for the book with id '{}', new authors - {}", bookId, authorsRequestDTO);

        var book = findBookByIdOrThrowException(bookId);

        var authors = AuthorRequestDTO.toAuthors(authorsRequestDTO);

        book.setAuthors(authors);

        bookRepository.saveCascading(book);
    }

    private Book findBookByIdOrThrowException(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Book with id '%d' not found", bookId)));
    }
}
