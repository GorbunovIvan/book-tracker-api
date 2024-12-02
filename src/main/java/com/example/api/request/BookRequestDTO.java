package com.example.api.request;

import com.example.model.Book;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "title", "publishedAt", "authors" })
@ToString
public class BookRequestDTO {

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedAt;

    private Integer totalPages;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime addedAt;

    private Set<CategoryRequestDTO> categories;

    private Set<AuthorRequestDTO> authors;

    public Book toBook() {

        var book = new Book();

        book.setTitle(getTitle());
        book.setPublishedAt(getPublishedAt());
        book.setTotalPages(getTotalPages());
        book.setAddedAt(getAddedAt());

        book.setAuthors(AuthorRequestDTO.toAuthors(getAuthors()));
        book.setCategories(CategoryRequestDTO.toCategories(getCategories()));

        return book;
    }

    public static List<Book> toBooks(List<BookRequestDTO> booksDTO) {
        if (booksDTO == null) {
            return Collections.emptyList();
        }
        return booksDTO.stream()
                .map(BookRequestDTO::toBook)
                .collect(Collectors.toList());
    }
}
