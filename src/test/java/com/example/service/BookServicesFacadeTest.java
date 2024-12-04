package com.example.service;

import com.example.api.request.AuthorRequestDTO;
import com.example.api.request.BookRequestDTO;
import com.example.api.request.CategoryRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.model.Book;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServicesFacadeTest {

    @Autowired
    private BookServicesFacade bookServicesFacade;

    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private CategoryService categoryService;
    @MockitoBean
    private AuthorService authorService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnCollectionOfBooksResponseDTOWhenGetAllBooks() {

        var books = easyRandom.objects(BookResponseDTO.class, 4).toList();

        when(bookService.getAll()).thenReturn(books);

        var booksReceived = bookServicesFacade.getAllBooks();
        assertSame(books, booksReceived);

        verify(bookService, times(1)).getAll();
    }

    @Test
    void shouldReturnBookResponseDTOWhenGetBookById() {

        var book = easyRandom.nextObject(BookResponseDTO.class);

        when(bookService.getById(book.getId())).thenReturn(book);

        var bookReceived = bookServicesFacade.getBookById(book.getId());
        assertSame(book, bookReceived);

        verify(bookService, times(1)).getById(book.getId());
    }

    @Test
    void shouldReturnBookResponseDTOWhenGetBookByTitle() {

        var book = easyRandom.nextObject(BookResponseDTO.class);

        when(bookService.getByTitle(book.getTitle())).thenReturn(book);

        var bookReceived = bookServicesFacade.getBookByTitle(book.getTitle());
        assertSame(book, bookReceived);

        verify(bookService, times(1)).getByTitle(book.getTitle());
    }

    @Test
    void shouldReturnCollectionsOfBooksResponseDTOWhenGetBooksByCategory() {

        var categoryName = "test-category";
        var books = easyRandom.objects(BookResponseDTO.class, 5).toList();

        when(categoryService.getBooksByCategoryName(categoryName)).thenReturn(books);

        var booksReceived = bookServicesFacade.getBooksByCategory(categoryName);
        assertSame(books, booksReceived);

        verify(categoryService, times(1)).getBooksByCategoryName(categoryName);
    }

    @Test
    void shouldReturnCollectionsOfBooksResponseDTOWhenGetBooksByAuthor() {

        var authorName = "test-author";
        var books = easyRandom.objects(BookResponseDTO.class, 5).toList();

        when(authorService.getBooksByAuthorName(authorName)).thenReturn(books);

        var booksReceived = bookServicesFacade.getBooksByAuthor(authorName);
        assertSame(books, booksReceived);

        verify(authorService, times(1)).getBooksByAuthorName(authorName);
    }

    @Test
    void shouldReturnBookResponseDTOWhenCreateBook() {

        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);
        var bookResponse = easyRandom.nextObject(BookResponseDTO.class);

        when(bookService.create(bookRequest)).thenReturn(bookResponse);

        var bookReceived = bookServicesFacade.createBook(bookRequest);
        assertSame(bookResponse, bookReceived);

        verify(bookService, times(1)).create(bookRequest);
    }

    @Test
    void shouldReturnBookResponseDTOWhenUpdateBook() {

        var bookId = 97L;

        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);
        var bookResponse = easyRandom.nextObject(BookResponseDTO.class);

        when(bookService.update(bookId, bookRequest)).thenReturn(bookResponse);

        var bookReceived = bookServicesFacade.updateBook(bookId, bookRequest);
        assertSame(bookResponse, bookReceived);

        verify(bookService, times(1)).update(bookId, bookRequest);
    }

    @Test
    void shouldDeleteBookWhenDeleteBookById() {
        var bookId = 97L;
        bookServicesFacade.deleteBookById(bookId);
        verify(bookService, times(1)).deleteById(bookId);
    }

    @Test
    void shouldAddCategoryToBookWhenAddCategoryToBook() {

        var categoryDTO = easyRandom.nextObject(CategoryRequestDTO.class);
        var book = easyRandom.nextObject(Book.class);

        bookServicesFacade.addCategoryToBook(book.getId(), categoryDTO);

        verify(categoryService, times(1)).addCategoryToBook(book.getId(), categoryDTO);
    }

    @Test
    void shouldRemoveCategoryFromBookWhenRemoveCategoryFromBook() {

        var categoryDTO = easyRandom.nextObject(CategoryRequestDTO.class);

        var book = easyRandom.nextObject(Book.class);
        book.addCategory(categoryDTO.toCategory());

        bookServicesFacade.removeCategoryFromBook(book.getId(), categoryDTO);

        verify(categoryService, times(1)).removeCategoryFromBook(book.getId(), categoryDTO);
    }

    @Test
    void shouldUpdateCategoriesForBookWhenUpdateCategoriesForBook() {

        var categoriesDTO = easyRandom.objects(CategoryRequestDTO.class, 5).toList();
        var book = easyRandom.nextObject(Book.class);

        bookServicesFacade.updateCategoriesForBook(book.getId(), categoriesDTO);

        verify(categoryService, times(1)).updateCategoriesForBook(book.getId(), categoriesDTO);
    }

    @Test
    void shouldAddAuthorToBookWhenAddAuthorToBook() {

        var authorDTO = easyRandom.nextObject(AuthorRequestDTO.class);
        var book = easyRandom.nextObject(Book.class);

        bookServicesFacade.addAuthorToBook(book.getId(), authorDTO);

        verify(authorService, times(1)).addAuthorToBook(book.getId(), authorDTO);
    }

    @Test
    void shouldRemoveAuthorFromBookWhenRemoveAuthorFromBook() {

        var authorDTO = easyRandom.nextObject(AuthorRequestDTO.class);

        var book = easyRandom.nextObject(Book.class);
        book.addAuthor(authorDTO.toAuthor());

        bookServicesFacade.removeAuthorFromBook(book.getId(), authorDTO);

        verify(authorService, times(1)).removeAuthorFromBook(book.getId(), authorDTO);
    }

    @Test
    void shouldUpdateAuthorsForBookWhenUpdateAuthorsForBook() {

        var authorsDTO = easyRandom.objects(AuthorRequestDTO.class, 5).toList();
        var book = easyRandom.nextObject(Book.class);

        bookServicesFacade.updateAuthorsForBook(book.getId(), authorsDTO);

        verify(authorService, times(1)).updateAuthorsForBook(book.getId(), authorsDTO);
    }
}