package com.example.api.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookRequestDTOTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnBookWhenToBook() {

        var bookDTO = easyRandom.nextObject(BookRequestDTO.class);

        var bookReceived = bookDTO.toBook();

        assertEquals(bookDTO.getTitle(), bookReceived.getTitle());
        assertEquals(bookDTO.getPublishedAt(), bookReceived.getPublishedAt());
        assertEquals(bookDTO.getTotalPages(), bookReceived.getTotalPages());
        assertEquals(bookDTO.getAddedAt(), bookReceived.getAddedAt());

        assertNotNull(bookReceived.getCategories());
        assertEquals(bookDTO.getCategories().size(), bookReceived.getCategories().size());

        assertNotNull(bookReceived.getAuthors());
        assertEquals(bookDTO.getAuthors().size(), bookReceived.getAuthors().size());
    }

    @Test
    void shouldReturnCollectionOfBooksWhenToBooks() {

        var booksDTO = easyRandom.objects(BookRequestDTO.class, 4).toList();

        var booksReceived = BookRequestDTO.toBooks(booksDTO);

        assertNotNull(booksReceived);
        assertEquals(booksDTO.size(), booksReceived.size());

        var booksDTOIterator = booksDTO.iterator();
        var booksReceivedIterator = booksReceived.iterator();

        assertEquals(booksDTOIterator.hasNext(), booksReceivedIterator.hasNext());

        while (booksDTOIterator.hasNext() && booksReceivedIterator.hasNext()) {

            var bookDTO = booksDTOIterator.next();
            var bookReceived = booksReceivedIterator.next();

            assertEquals(bookDTO.getTitle(), bookReceived.getTitle());
            assertEquals(bookDTO.getPublishedAt(), bookReceived.getPublishedAt());
            assertEquals(bookDTO.getTotalPages(), bookReceived.getTotalPages());
            assertEquals(bookDTO.getAddedAt(), bookReceived.getAddedAt());
        }

        assertEquals(booksDTOIterator.hasNext(), booksReceivedIterator.hasNext());
    }
}