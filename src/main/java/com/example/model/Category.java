package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = @UniqueConstraint(columnNames = { "name" })
)
@NamedEntityGraph(
        name = "graph.CategoryBooksAuthors",
        attributeNodes = @NamedAttributeNode(value = "books", subgraph = "subgraph.book"),
        subgraphs = {
                @NamedSubgraph(name = "subgraph.book", attributeNodes = {
                        @NamedAttributeNode(value = "categories"),
                        @NamedAttributeNode(value = "authors"),
                })
        }
)
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "name")
@ToString
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotNull
    @Size(min = 1, max = 500)
    private String name;

    @ManyToMany(mappedBy = "categories")
    @ToString.Exclude
    private Set<Book> books = new HashSet<>();
}
