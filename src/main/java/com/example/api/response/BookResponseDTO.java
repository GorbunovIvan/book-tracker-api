package com.example.api.response;

import com.example.model.Book;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "title", "publishedAt", "authors" })
@ToString
public class BookResponseDTO {

    private Long id;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedAt;

    private Integer totalPages;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime addedAt;

    private Set<CategoryResponseDTO> categories;

    private Set<AuthorResponseDTO> authors;

    public static BookResponseDTO fromBook(Book book) {

        var bookDTO = new BookResponseDTO();

        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setPublishedAt(book.getPublishedAt());
        bookDTO.setTotalPages(book.getTotalPages());
        bookDTO.setAddedAt(book.getAddedAt());

        bookDTO.setAuthors(AuthorResponseDTO.fromAuthors(book.getAuthors()));
        bookDTO.setCategories(CategoryResponseDTO.fromCategories(book.getCategories()));

        return bookDTO;
    }

    public static List<BookResponseDTO> fromBooks(Collection<Book> books) {
        if (books == null) {
            return Collections.emptyList();
        }
        return books.stream()
                .map(BookResponseDTO::fromBook)
                .collect(Collectors.toList());
    }
}
