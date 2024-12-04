package com.example.api.request;

import com.example.model.Author;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AuthorRequestDTOTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnAuthorWhenToAuthor() {

        var authorDTO = easyRandom.nextObject(AuthorRequestDTO.class);

        var authorReceived = authorDTO.toAuthor();
        assertEquals(authorDTO.getName(), authorReceived.getName());
        assertNotNull(authorReceived.getBooks());
        assertTrue(authorReceived.getBooks().isEmpty());
    }

    @Test
    void shouldReturnAuthorsWhenToAuthors() {

        var authorsDTO = easyRandom.objects(AuthorRequestDTO.class, 4).toList();

        var authorsReceived = AuthorRequestDTO.toAuthors(authorsDTO);

        assertNotNull(authorsReceived);
        assertEquals(authorsReceived.size(), authorsDTO.size());

        var authorsDTONames = authorsDTO.stream().map(AuthorRequestDTO::getName).collect(Collectors.toSet());
        var authorsReceivedNames = authorsReceived.stream().map(Author::getName).collect(Collectors.toSet());

        assertEquals(authorsReceivedNames, authorsDTONames);
    }

    @Test
    void shouldReturnAuthorsRequestDTOWhenFromAuthor() {

        var author = easyRandom.nextObject(Author.class);

        var authorRequestDTO = AuthorRequestDTO.fromAuthor(author);
        assertEquals(author.getName(), authorRequestDTO.getName());
    }

    @Test
    void shouldReturnCollectionOfAuthorsRequestDTOWhenFromAuthors() {

        var authors = easyRandom.objects(Author.class, 4).collect(Collectors.toSet());

        var authorsRequestDTO = AuthorRequestDTO.fromAuthors(authors);

        assertNotNull(authorsRequestDTO);
        assertEquals(authorsRequestDTO.size(), authors.size());

        var authorsNames = authors.stream().map(Author::getName).collect(Collectors.toSet());
        var authorsRequestDTONames = authorsRequestDTO.stream().map(AuthorRequestDTO::getName).collect(Collectors.toSet());

        assertEquals(authorsRequestDTONames, authorsNames);
    }
}