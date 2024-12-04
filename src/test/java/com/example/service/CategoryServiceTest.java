package com.example.service;

import com.example.api.request.CategoryRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.model.Book;
import com.example.model.Category;
import com.example.repository.BookRepository;
import com.example.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;
    @MockitoBean
    private BookRepository bookRepository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnCollectionOfBooksResponseDTOWhenGetBooksByCategoryName() {

        var category = easyRandom.nextObject(Category.class);

        var books = easyRandom.objects(Book.class, 3).collect(Collectors.toSet());
        for (var book : books) {
            book.setCategories(easyRandom.objects(Category.class, 3).collect(Collectors.toSet()));
            book.addCategory(category);
        }

        category.setBooks(books);

        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));

        var booksExpected = BookResponseDTO.fromBooks(books);

        var booksReceived = categoryService.getBooksByCategoryName(category.getName());
        assertNotNull(booksReceived);
        assertEquals(booksExpected.size(), booksReceived.size());
        assertEquals(booksExpected, new ArrayList<>(booksReceived));

        verify(categoryRepository, times(1)).findByName(category.getName());
    }

    @Test
    void shouldReturnEmptyCollectionWhenGetBooksByCategoryName() {

        var categoryName = "test-category";

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        var booksReceived = categoryService.getBooksByCategoryName(categoryName);
        assertNotNull(booksReceived);
        assertTrue(booksReceived.isEmpty());

        verify(categoryRepository, times(1)).findByName(categoryName);
    }

    @Test
    void shouldAddNewCategoryWhenAddCategoryToBook() {

        var book = easyRandom.nextObject(Book.class);
        var category = easyRandom.nextObject(Category.class);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var categoryDTO = CategoryRequestDTO.fromCategory(category);

        assertFalse(book.getCategories().contains(category));
        categoryService.addCategoryToBook(book.getId(), categoryDTO);
        assertTrue(book.getCategories().contains(category));

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, times(1)).saveCascading(book);
    }

    @Test
    void shouldNotAddNewCategoryWhenAddCategoryToBook() {

        var category = easyRandom.nextObject(Category.class);

        var book = easyRandom.nextObject(Book.class);
        book.setCategories(new HashSet<>(Set.of(category)));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var categoryDTO = CategoryRequestDTO.fromCategory(category);

        categoryService.addCategoryToBook(book.getId(), categoryDTO);

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, never()).saveCascading(any());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenAddCategoryToBook() {

        var bookId = 99L;
        var categoryDTO = easyRandom.nextObject(CategoryRequestDTO.class);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.addCategoryToBook(bookId, categoryDTO));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).saveCascading(any());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldReturnTrueWhenRemoveCategoryFromBook() {

        var category = easyRandom.nextObject(Category.class);

        var book = easyRandom.nextObject(Book.class);
        book.setCategories(new HashSet<>(Set.of(category)));

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var categoryDTO = CategoryRequestDTO.fromCategory(category);

        assertTrue(book.getCategories().contains(category));

        var result = categoryService.removeCategoryFromBook(book.getId(), categoryDTO);
        assertTrue(result);

        assertFalse(book.getCategories().contains(category));

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, times(1)).saveCascading(book);
    }

    @Test
    void shouldReturnFalseWhenRemoveCategoryFromBook() {

        var category = easyRandom.nextObject(Category.class);

        var book = easyRandom.nextObject(Book.class);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var categoryDTO = CategoryRequestDTO.fromCategory(category);

        assertFalse(book.getCategories().contains(category));

        var result = categoryService.removeCategoryFromBook(book.getId(), categoryDTO);
        assertFalse(result);

        assertFalse(book.getCategories().contains(category));

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenRemoveCategoryFromBook() {

        var bookId = 99L;
        var categoryDTO = easyRandom.nextObject(CategoryRequestDTO.class);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.removeCategoryFromBook(bookId, categoryDTO));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).saveCascading(any());
        verify(bookRepository, never()).saveCascading(any());
    }

    @Test
    void shouldSetNewCategoriesToBookWhenUpdateCategoriesForBook() {

        var categories = easyRandom.objects(Category.class, 4).collect(Collectors.toSet());

        var book = easyRandom.nextObject(Book.class);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var categoriesDTO = CategoryRequestDTO.fromCategories(categories);

        assertTrue(book.getCategories().isEmpty());
        categoryService.updateCategoriesForBook(book.getId(), categoriesDTO);
        assertEquals(categories, book.getCategories());

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookRepository, times(1)).saveCascading(book);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenUpdateCategoriesForBook() {

        var bookId = 99L;
        var categoriesDTO = easyRandom.objects(CategoryRequestDTO.class, 4).toList();

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategoriesForBook(bookId, categoriesDTO));

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).saveCascading(any());
        verify(bookRepository, never()).saveCascading(any());
    }
}