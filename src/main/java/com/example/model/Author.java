package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "authors",
        uniqueConstraints = @UniqueConstraint(columnNames = { "name" })
)
@NamedEntityGraph(
        name = "graph.AuthorBooksCategories",
        attributeNodes = @NamedAttributeNode(value = "books", subgraph = "subgraph.book"),
        subgraphs = {
                @NamedSubgraph(name = "subgraph.book", attributeNodes = {
                        @NamedAttributeNode(value = "authors"),
                        @NamedAttributeNode(value = "categories")
                })
        }
)
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "name")
@ToString
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotNull
    @NotEmpty
    private String name;

    @ManyToMany(mappedBy = "authors")
    @ToString.Exclude
    private Set<Book> books = new HashSet<>();
}
