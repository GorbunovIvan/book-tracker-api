package com.example.repository;

import com.example.model.Author;
import com.example.model.Book;
import com.example.model.Category;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookRepositoryCustomImplTest {

    @Autowired
    private BookRepository bookRepository;

    @MockitoSpyBean
    private CategoryRepository categoryRepository;
    @MockitoSpyBean
    private AuthorRepository authorRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldSaveAndReturnNewBookWhenSaveCascading() {

        // Preparing data
        var book = easyRandom.nextObject(Book.class);
        book.setId(null);
        book.setNew(true);
        book.setTotalPages(345);

        book.setCategories(easyRandom.objects(Category.class, 4).collect(Collectors.toSet()));
        book.setAuthors(easyRandom.objects(Author.class, 5).collect(Collectors.toSet()));

        for (var category : book.getCategories()) {
            category.setId(null);
        }
        for (var author : book.getAuthors()) {
            author.setId(null);
        }

        var categoriesNames = book.getCategories().stream().map(Category::getName).distinct().toList();
        var authorsNames = book.getAuthors().stream().map(Author::getName).distinct().toList();

        // Testing data
        var bookSaved = bookRepository.saveCascading(book);

        // Verifying results
        assertNotNull(bookSaved.getId());
        assertEquals(bookSaved, book);

        assertEquals(book.getCategories().size(), bookSaved.getCategories().size());
        assertEquals(book.getAuthors().size(), bookSaved.getAuthors().size());

        verify(categoryRepository, times(1)).findAllByNameIn(categoriesNames);
        verify(authorRepository, times(1)).findAllByNameIn(authorsNames);

        verify(categoryRepository, times(1)).saveAll(anyCollection());
        verify(authorRepository, times(1)).saveAll(anyCollection());

        // Verifying current state of data in DB
        var booksFromDBAfter = bookRepository.findAll();
        assertEquals(1, booksFromDBAfter.size());

        var bookFromDBAfter = booksFromDBAfter.getFirst();
        assertEquals(bookFromDBAfter, bookSaved);
        assertEquals(bookFromDBAfter.getCategories(), bookSaved.getCategories());
        assertEquals(bookFromDBAfter.getAuthors(), bookSaved.getAuthors());

        var categoriesFromDBAfter = categoryRepository.findAll();
        assertFalse(categoriesFromDBAfter.isEmpty());
        assertEquals(bookSaved.getCategories(), new HashSet<>(categoriesFromDBAfter));

        var authorsFromDBAfter = authorRepository.findAll();
        assertFalse(authorsFromDBAfter.isEmpty());
        assertEquals(bookSaved.getAuthors(), new HashSet<>(authorsFromDBAfter));
    }

    @Test
    @Sql(scripts = "/setup-test-data.sql",
         config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)  // Forces separate transaction
    )
    void shouldSaveAndReturnExistingBookWithNoChangesWhenSaveCascading() {

        // Preparing data
        var bookExistingOptional = bookRepository.findById(1L);
        assertTrue(bookExistingOptional.isPresent(), "Book with id '" + 1L + "' must be present in test DB before running this test");
        var bookExisting = bookExistingOptional.get();

        var categoriesNames = bookExisting.getCategories().stream().map(Category::getName).distinct().toList();
        var authorsNames = bookExisting.getAuthors().stream().map(Author::getName).distinct().toList();

        // Testing
        System.out.println("1.2 - Saving: " + bookExisting);
        var bookSaved = bookRepository.saveCascading(bookExisting);

        // Verifying results
        assertNotNull(bookSaved.getId());
        assertEquals(bookExisting, bookSaved);

        assertEquals(bookExisting.getCategories().size(), bookSaved.getCategories().size());
        assertEquals(bookExisting.getAuthors().size(), bookSaved.getAuthors().size());

        verify(categoryRepository, times(1)).findAllByNameIn(categoriesNames);
        verify(authorRepository, times(1)).findAllByNameIn(authorsNames);

        verify(categoryRepository, never()).saveAll(any());
        verify(authorRepository, never()).saveAll(any());

        // Verifying current state of data in DB
        var booksFromDBAfter = bookRepository.findAll();
        assertEquals(1, booksFromDBAfter.size());

        var bookFromDBAfter = booksFromDBAfter.getFirst();
        assertEquals(bookFromDBAfter, bookSaved);
        assertEquals(bookFromDBAfter.getCategories(), bookSaved.getCategories());
        assertEquals(bookFromDBAfter.getAuthors(), bookSaved.getAuthors());

        var categoriesFromDBAfter = categoryRepository.findAll();
        assertFalse(categoriesFromDBAfter.isEmpty());
        assertEquals(bookSaved.getCategories(), new HashSet<>(categoriesFromDBAfter));

        var authorsFromDBAfter = authorRepository.findAll();
        assertFalse(authorsFromDBAfter.isEmpty());
        assertEquals(bookSaved.getAuthors(), new HashSet<>(authorsFromDBAfter));
    }

    @Test
    @Sql(scripts = "/setup-test-data.sql",
         config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)  // Forces separate transaction
    )
    void shouldSaveAndReturnExistingBookWithNewCategoriesAndAuthorsWhenSaveCascading() {

        // Preparing data
        var bookExistingOptional = bookRepository.findById(1L);
        assertTrue(bookExistingOptional.isPresent(), "Book with id '" + 1L + "' must be present in test DB before running this test");
        var bookExisting = bookExistingOptional.get();

        var newCategory = easyRandom.nextObject(Category.class);
        newCategory.setId(null);
        bookExisting.addCategory(newCategory);

        var newAuthor = easyRandom.nextObject(Author.class);
        newAuthor.setId(null);
        bookExisting.addAuthor(newAuthor);

        var categoriesNames = bookExisting.getCategories().stream().map(Category::getName).distinct().toList();
        var authorsNames = bookExisting.getAuthors().stream().map(Author::getName).distinct().toList();

        // Testing
        System.out.println("2.2 - Saving: " + bookExisting);
        var bookSaved = bookRepository.saveCascading(bookExisting);

        // Verifying results
        assertEquals(bookExisting.getId(), bookSaved.getId());
        assertEquals(bookExisting.getTitle(), bookSaved.getTitle());
        assertEquals(bookExisting.getPublishedAt(), bookSaved.getPublishedAt());

        assertEquals(bookExisting.getCategories().size(), bookSaved.getCategories().size());
        assertEquals(bookExisting.getAuthors().size(), bookSaved.getAuthors().size());

        verify(categoryRepository, times(1)).findAllByNameIn(categoriesNames);
        verify(authorRepository, times(1)).findAllByNameIn(authorsNames);

        verify(categoryRepository, times(1)).saveAll(anyCollection());
        verify(authorRepository, times(1)).saveAll(anyCollection());

        // Verifying current state of data in DB
        var booksFromDBAfter = bookRepository.findAll();
        assertEquals(1, booksFromDBAfter.size());

        var bookFromDBAfter = booksFromDBAfter.getFirst();
        assertEquals(bookFromDBAfter, bookSaved);
        assertEquals(bookFromDBAfter.getCategories(), bookSaved.getCategories());
        assertEquals(bookFromDBAfter.getAuthors(), bookSaved.getAuthors());

        var categoriesFromDBAfter = categoryRepository.findAll();
        assertFalse(categoriesFromDBAfter.isEmpty());
        assertEquals(bookSaved.getCategories(), new HashSet<>(categoriesFromDBAfter));

        var authorsFromDBAfter = authorRepository.findAll();
        assertFalse(authorsFromDBAfter.isEmpty());
        assertEquals(bookSaved.getAuthors(), new HashSet<>(authorsFromDBAfter));
    }
}