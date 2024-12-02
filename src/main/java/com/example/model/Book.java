package com.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "books",
        uniqueConstraints = @UniqueConstraint(columnNames = { "title", "published_at" })
)
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "title", "publishedAt", "authors" })
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @NotNull
    @Size(min = 2, max = 500)
    private String title;

    @Column(name = "published_at")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedAt;

    @Column(name = "total_pages")
    @NotNull
    @Min(1)
    @Max(99_999)
    private Integer totalPages;

    @Column(name = "added_at")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime addedAt;

    @Transient
    private boolean isNew = false;

    @ManyToMany
    @JoinTable(
            name = "books_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "books_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    public void addCategory(@NotNull Category category) {
        categories.add(category);
    }

    public boolean removeCategory(@NotNull Category category) {
        return categories.remove(category);
    }

    public void addAuthor(@NotNull Author author) {
        authors.add(author);
    }

    public boolean removeAuthor(@NotNull Author author) {
        return authors.remove(author);
    }
}
