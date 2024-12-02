package com.example.api.response;

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
public class AuthorResponseDTO {

    private Long id;
    private String name;

    public Author toAuthor() {
        var author = new Author();
        author.setId(getId());
        author.setName(getName());
        author.setBooks(Collections.emptySet());
        return author;
    }
    
    public static Set<Author> toAuthors(Collection<AuthorResponseDTO> authorsDTO) {
        if (authorsDTO == null) {
            return Collections.emptySet();
        }
        return authorsDTO.stream()
                .map(AuthorResponseDTO::toAuthor)
                .collect(Collectors.toSet());
    }

    public static AuthorResponseDTO fromAuthor(Author author) {
        var authorDTO = new AuthorResponseDTO();
        authorDTO.setId(author.getId());
        authorDTO.setName(author.getName());
        return authorDTO;
    }

    public static Set<AuthorResponseDTO> fromAuthors(Collection<Author> authors) {
        if (authors == null) {
            return Collections.emptySet();
        }
        return authors.stream()
                .map(AuthorResponseDTO::fromAuthor)
                .collect(Collectors.toSet());
    }
}
