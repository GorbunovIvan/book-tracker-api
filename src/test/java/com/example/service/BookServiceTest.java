package com.example.service;

import com.example.api.request.BookRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.model.Book;
import com.example.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @MockitoBean
    private BookRepository bookRepository;
    
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnCollectionOfBooksResponseDTOWhenGetAllBooks() {

        var books = easyRandom.objects(Book.class, 4).toList();
        var booksDTO = BookResponseDTO.fromBooks(books);

        when(bookRepository.findAll()).thenReturn(books);

        var booksReceived = bookService.getAll();
        assertEquals(booksDTO, booksReceived);

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnBookResponseDTOWhenGetBookById() {

        var book = easyRandom.nextObject(Book.class);
        var bookDTO = BookResponseDTO.fromBook(book);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var bookReceived = bookService.getById(book.getId());
        assertEquals(bookDTO, bookReceived);

        verify(bookRepository, times(1)).findById(book.getId());
    }

    @Test
    void shouldReturnNullWhenGetBookById() {

        var bookId = 32L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        var bookReceived = bookService.getById(bookId);
        assertNull(bookReceived);

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void shouldReturnBookResponseDTOWhenGetBookByTitle() {

        var book = easyRandom.nextObject(Book.class);
        var bookDTO = BookResponseDTO.fromBook(book);

        when(bookRepository.findByTitle(book.getTitle())).thenReturn(Optional.of(book));

        var bookReceived = bookService.getByTitle(book.getTitle());
        assertEquals(bookDTO, bookReceived);

        verify(bookRepository, times(1)).findByTitle(book.getTitle());
    }

    @Test
    void shouldReturnBookResponseDTOWhenCreateBook() {

        var bookId = 87L;

        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);

        var book = bookRequest.toBook();

        var bookResponse = BookResponseDTO.fromBook(book);
        bookResponse.setId(bookId);

        when(bookRepository.saveCascading(book)).thenReturn(book);

        var bookReceived = bookService.create(bookRequest);
        assertEquals(bookId, bookResponse.getId());
        assertEquals(bookResponse, bookReceived);

        verify(bookRepository, times(1)).saveCascading(book);
    }

    @Test
    void shouldReturnBookResponseDTOWhenUpdateBook() {

        var bookId = 97L;

        var bookCurrent = easyRandom.nextObject(Book.class);
        bookCurrent.setId(bookId);
        bookCurrent.setAuthors(Collections.emptySet());
        bookCurrent.setCategories(Collections.emptySet());

        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);
        bookRequest.setAuthors(Collections.emptySet());
        bookRequest.setCategories(Collections.emptySet());

        var bookNew = bookRequest.toBook();
        bookNew.setId(bookId);
        var bookResponse = BookResponseDTO.fromBook(bookNew);
        
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookCurrent));
        when(bookRepository.save(bookNew)).thenReturn(bookNew);

        var bookReceived = bookService.update(bookId, bookRequest);
        assertEquals(bookResponse, bookReceived);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(bookNew);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdateBook() {

        var bookId = 97L;
        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.update(bookId, bookRequest));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldDeleteBookWhenDeleteBookById() {
        var bookId = 97L;
        bookService.deleteById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }
}