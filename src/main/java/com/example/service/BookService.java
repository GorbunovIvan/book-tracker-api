package com.example.service;

import com.example.api.request.BookRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponseDTO> getAll() {
        log.info("Retrieving all books");
        return BookResponseDTO.fromBooks(bookRepository.findAll());
    }

    public BookResponseDTO getById(@NotNull Long id) {
        log.info("Retrieving book by id '{}'", id);
        return bookRepository.findById(id)
                .map(BookResponseDTO::fromBook)
                .orElse(null);
    }

    public BookResponseDTO getByTitle(@NotNull String name) {
        log.info("Retrieving book by title '{}'", name);
        return bookRepository.findByTitle(name)
                .map(BookResponseDTO::fromBook)
                .orElse(null);
    }

    public BookResponseDTO create(@NotNull BookRequestDTO bookRequestDTO) {
        log.info("Adding new book");
        var book = bookRequestDTO.toBook();
        if (book.getAddedAt() == null) {
            book.setAddedAt(LocalDateTime.now());
        }
        book.setNew(true);
        var bookCreated = bookRepository.saveCascading(book);
        return BookResponseDTO.fromBook(bookCreated);
    }

    @Transactional
    public BookResponseDTO update(@NotNull Long id,
                                  @NotNull BookRequestDTO bookRequestDTO) {

        log.info("Updating book with id '{}'", id);

        var bookFoundByIdOptional = bookRepository.findById(id);
        if (bookFoundByIdOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Book with id '%d' not found", id));
        }
        var bookFoundById = bookFoundByIdOptional.get();

        if (bookRequestDTO.getTitle() != null) {
            bookFoundById.setTitle(bookRequestDTO.getTitle());
        }
        if (bookRequestDTO.getPublishedAt() != null) {
            bookFoundById.setPublishedAt(bookRequestDTO.getPublishedAt());
        }
        if (bookRequestDTO.getTotalPages() != null) {
            bookFoundById.setTotalPages(bookRequestDTO.getTotalPages());
        }
        if (bookRequestDTO.getAddedAt() != null) {
            bookFoundById.setAddedAt(bookRequestDTO.getAddedAt());
        }

        var bookUpdated = bookRepository.save(bookFoundById);

        return BookResponseDTO.fromBook(bookUpdated);
    }

    public void deleteById(@NotNull Long id) {
        log.warn("Deleting book with id '{}'", id);
        bookRepository.deleteById(id);
    }
}
