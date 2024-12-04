package com.example.service;

import com.example.api.request.AuthorRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.model.Author;
import com.example.model.Book;
import com.example.repository.AuthorRepository;
import com.example.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @MockitoBean
    private AuthorRepository authorRepository;
    @MockitoBean
    private BookRepository bookRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnCollectionOfBooksResponseDTOWhenGetBooksByAuthorName() {

        var author = easyRandom.nextObject(Author.class);

        var books = easyRandom.objects(Book.class, 3).collect(Collectors.toSet());
        for (var book : books) {
            book.setAuthors(easyRandom.objects(Author.class, 3).collect(Collectors.toSet()));
            book.addAuthor(author);
        }

        author.setBooks(books);

        when(authorRepository.findByName(author.getName())).thenReturn(Optional.of(author));

        var booksExpected = BookResponseDTO.fromBooks(books);

        var booksReceived = authorService.getBooksByAuthorName(author.getName());
        assertNotNull(booksReceived);
        assertEquals(booksExpected.size(), booksReceived.size());
        assertEquals(booksExpected, new ArrayList<>(booksReceived));

        verify(authorRepository, times(1)).findByName(author.getName());
    }

    @Test
    void shouldReturnEmptyCollectionWhenGetBooksByAuthorName() {

        var authorName = "test-author";

        when(authorRepository.findByName(anyString())).thenReturn(Optional.empty());

        var booksReceived = authorService.getBooksByAuthorName(authorName);
        assertNotNull(booksReceived);
        assertTrue(booksReceived.isEmpty());

        verify(authorRepository, times(1)).findByName(authorName);
    }

    @Test
    void shouldAddNewAuthorWhenAddAuthorToBook() {

        var book = easyRandom.nextObject(Book.class);
        var author = easyRandom.nextObject(Author.class);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var authorDTO = AuthorRequestDTO.fromAuthor(author);

        assertFalse(book.getAuthors().contains(author));
        authorService.addAuthorToBook(book.getId(), authorDTO);
        assertTrue(book.getAuthors().contains(author));

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, times(1)).saveCascading(book);
    }

    @Test
    void shouldNotAddNewAuthorWhenAddAuthorToBook() {

        var author = easyRandom.nextObject(Author.class);

        var book = easyRandom.nextObject(Book.class);
        book.setAuthors(new HashSet<>(Set.of(author)));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var authorDTO = AuthorRequestDTO.fromAuthor(author);

        authorService.addAuthorToBook(book.getId(), authorDTO);

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, never()).saveCascading(any());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenAddAuthorToBook() {

        var bookId = 99L;
        var authorDTO = easyRandom.nextObject(AuthorRequestDTO.class);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.addAuthorToBook(bookId, authorDTO));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).saveCascading(any());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldReturnTrueWhenRemoveAuthorFromBook() {

        var author = easyRandom.nextObject(Author.class);

        var book = easyRandom.nextObject(Book.class);
        book.setAuthors(new HashSet<>(Set.of(author)));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var authorDTO = AuthorRequestDTO.fromAuthor(author);

        assertTrue(book.getAuthors().contains(author));

        var result = authorService.removeAuthorFromBook(book.getId(), authorDTO);
        assertTrue(result);

        assertFalse(book.getAuthors().contains(author));

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, times(1)).saveCascading(book);
    }

    @Test
    void shouldReturnFalseWhenRemoveAuthorFromBook() {

        var author = easyRandom.nextObject(Author.class);

        var book = easyRandom.nextObject(Book.class);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var authorDTO = AuthorRequestDTO.fromAuthor(author);

        assertFalse(book.getAuthors().contains(author));

        var result = authorService.removeAuthorFromBook(book.getId(), authorDTO);
        assertFalse(result);

        assertFalse(book.getAuthors().contains(author));

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenRemoveAuthorFromBook() {

        var bookId = 99L;
        var authorDTO = easyRandom.nextObject(AuthorRequestDTO.class);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.removeAuthorFromBook(bookId, authorDTO));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).saveCascading(any());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldSetNewAuthorsToBookWhenUpdateAuthorsForBook() {

        var authors = easyRandom.objects(Author.class, 4).collect(Collectors.toSet());

        var book = easyRandom.nextObject(Book.class);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var authorsDTO = AuthorRequestDTO.fromAuthors(authors);

        assertTrue(book.getAuthors().isEmpty());
        authorService.updateAuthorsForBook(book.getId(), authorsDTO);
        assertEquals(authors, book.getAuthors());

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, times(1)).saveCascading(book);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdateAuthorsForBook() {

        var bookId = 99L;
        var authorsDTO = easyRandom.objects(AuthorRequestDTO.class, 4).toList();

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.updateAuthorsForBook(bookId, authorsDTO));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).saveCascading(any());
        verify(bookRepository, never()).saveCascading(any());
    }
}