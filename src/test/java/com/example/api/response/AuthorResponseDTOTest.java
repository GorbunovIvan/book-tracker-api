package com.example.api.response;

import com.example.model.Author;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AuthorResponseDTOTest {

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnAuthorWhenToAuthor() {

        var authorDTO = easyRandom.nextObject(AuthorResponseDTO.class);

        var authorReceived = authorDTO.toAuthor();
        assertEquals(authorDTO.getId(), authorReceived.getId());
        assertEquals(authorDTO.getName(), authorReceived.getName());
        assertNotNull(authorReceived.getBooks());
        assertTrue(authorReceived.getBooks().isEmpty());
    }

    @Test
    void shouldReturnAuthorsWhenToAuthors() {

        var authorsDTO = easyRandom.objects(AuthorResponseDTO.class, 4).toList();

        var authorsReceived = AuthorResponseDTO.toAuthors(authorsDTO);

        assertNotNull(authorsReceived);
        assertEquals(authorsReceived.size(), authorsDTO.size());

        var authorsDTONames = authorsDTO.stream().map(AuthorResponseDTO::getName).collect(Collectors.toSet());
        var authorsReceivedNames = authorsReceived.stream().map(Author::getName).collect(Collectors.toSet());

        assertEquals(authorsReceivedNames, authorsDTONames);
    }

    @Test
    void shouldReturnAuthorsResponseDTOWhenFromAuthor() {

        var author = easyRandom.nextObject(Author.class);

        var authorResponseDTO = AuthorResponseDTO.fromAuthor(author);
        assertEquals(author.getName(), authorResponseDTO.getName());
    }

    @Test
    void shouldReturnCollectionOfAuthorsResponseDTOWhenFromAuthors() {

        var authors = easyRandom.objects(Author.class, 4).collect(Collectors.toSet());

        var authorsResponseDTO = AuthorResponseDTO.fromAuthors(authors);

        assertNotNull(authorsResponseDTO);
        assertEquals(authorsResponseDTO.size(), authors.size());

        var authorsNames = authors.stream().map(Author::getName).collect(Collectors.toSet());
        var authorsResponseDTONames = authorsResponseDTO.stream().map(AuthorResponseDTO::getName).collect(Collectors.toSet());

        assertEquals(authorsResponseDTONames, authorsNames);
    }
}