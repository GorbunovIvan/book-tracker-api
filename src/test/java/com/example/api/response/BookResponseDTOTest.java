package com.example.api.response;

import com.example.model.Author;
import com.example.model.Book;
import com.example.model.Category;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BookResponseDTOTest {
    
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnBookResponseDTOWhenToBook() {

        var book = easyRandom.nextObject(Book.class);
        book.setCategories(easyRandom.objects(Category.class, 5).collect(Collectors.toSet()));
        book.setAuthors(easyRandom.objects(Author.class, 3).collect(Collectors.toSet()));

        var bookDTO = BookResponseDTO.fromBook(book);

        assertEquals(book.getId(), bookDTO.getId());
        assertEquals(book.getTitle(), bookDTO.getTitle());
        assertEquals(book.getPublishedAt(), bookDTO.getPublishedAt());
        assertEquals(book.getTotalPages(), bookDTO.getTotalPages());
        assertEquals(book.getAddedAt(), bookDTO.getAddedAt());

        assertNotNull(bookDTO.getCategories());
        assertEquals(book.getCategories().size(), bookDTO.getCategories().size());

        assertNotNull(bookDTO.getAuthors());
        assertEquals(book.getAuthors().size(), bookDTO.getAuthors().size());
    }

    @Test
    void shouldReturnCollectionOfBooksResponseDTOWhenToBooks() {

        var books = easyRandom.objects(Book.class, 4).toList();

        for (var book : books) {
            book.setCategories(easyRandom.objects(Category.class, 5).collect(Collectors.toSet()));
            book.setAuthors(easyRandom.objects(Author.class, 3).collect(Collectors.toSet()));
        }

        var booksDTO = BookResponseDTO.fromBooks(books);

        assertNotNull(booksDTO);
        assertEquals(books.size(), booksDTO.size());

        var booksIterator = books.iterator();
        var booksDTOIterator = booksDTO.iterator();

        assertEquals(booksIterator.hasNext(), booksDTOIterator.hasNext());

        while (booksIterator.hasNext() && booksDTOIterator.hasNext()) {

            var book = booksIterator.next();
            var bookDTO = booksDTOIterator.next();

            assertEquals(book.getTitle(), bookDTO.getTitle());
            assertEquals(book.getPublishedAt(), bookDTO.getPublishedAt());
            assertEquals(book.getTotalPages(), bookDTO.getTotalPages());
            assertEquals(book.getAddedAt(), bookDTO.getAddedAt());
        }

        assertEquals(booksIterator.hasNext(), booksDTOIterator.hasNext());
    }
}