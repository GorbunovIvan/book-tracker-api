package com.example.api.request;

import com.example.model.Author;
import lombok.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class AuthorRequestDTO {

    private String name;

    public Author toAuthor() {
        var author = new Author();
        author.setName(getName());
        author.setBooks(Collections.emptySet());
        return author;
    }
    
    public static Set<Author> toAuthors(Collection<AuthorRequestDTO> authorsDTO) {
        if (authorsDTO == null) {
            return Collections.emptySet();
        }
        return authorsDTO.stream()
                .map(AuthorRequestDTO::toAuthor)
                .collect(Collectors.toSet());
    }

    public static AuthorRequestDTO fromAuthor(Author author) {
        var authorDTO = new AuthorRequestDTO();
        authorDTO.setName(author.getName());
        return authorDTO;
    }

    public static Set<AuthorRequestDTO> fromAuthors(Set<Author> authors) {
        if (authors == null) {
            return null;
        }
        return authors.stream()
                .map(AuthorRequestDTO::fromAuthor)
                .collect(Collectors.toSet());
    }
}
