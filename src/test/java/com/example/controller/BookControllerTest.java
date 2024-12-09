package com.example.controller;

import com.example.api.request.AuthorRequestDTO;
import com.example.api.request.BookRequestDTO;
import com.example.api.request.CategoryRequestDTO;
import com.example.api.response.BookResponseDTO;
import com.example.model.Role;
import com.example.model.User;
import com.example.security.JwtTokenProvider;
import com.example.service.BookServicesFacade;
import com.example.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private BookServicesFacade bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseURI = "/api/v1/books";

    private final EasyRandom easyRandom = new EasyRandom();

    @MockitoBean
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private User testUser;
    private String token;

    @PostConstruct
    private void init() {
        objectMapper.findAndRegisterModules();
        testUser = new User(1, "test-user", "test-password", Role.ADMIN);
        token = jwtTokenProvider.createToken(testUser.getUsername(), testUser.getAuthorities());
    }

    @BeforeEach
    void setUp() {
        when(userService.loadUserByUsername(testUser.getUsername())).thenReturn(testUser);
    }

    @Test
    void shouldReturnForbiddenWhenCreateBook() throws Exception {

        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);
        var bookJsonParam = objectMapper.writeValueAsString(bookRequest);

        mockMvc.perform(post(baseURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJsonParam))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenCreateBook() throws Exception {

        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);
        var bookJsonParam = objectMapper.writeValueAsString(bookRequest);

        var wrongToken = token + "1";

        mockMvc.perform(post(baseURI)
                        .header("Authorization", wrongToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJsonParam))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnCollectionOfBooksResponseDTOWhenGetAllBooks() throws Exception {

        var books = easyRandom.objects(BookResponseDTO.class, 4).toList();

        when(bookService.getAllBooks()).thenReturn(books);

        var jsonResponse = mockMvc.perform(get(baseURI)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BookResponseDTO> booksReceived = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertNotNull(booksReceived);
        assertEquals(books, booksReceived);

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void shouldReturnBookResponseDTOWhenGetBookById() throws Exception {

        var book = easyRandom.nextObject(BookResponseDTO.class);

        when(bookService.getBookById(book.getId())).thenReturn(book);

        var jsonResponse = mockMvc.perform(get(baseURI + "/{id}", book.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponseDTO bookReceived = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertEquals(book, bookReceived);

        verify(bookService, times(1)).getBookById(book.getId());
    }

    @Test
    void shouldReturnNotFoundWhenGetBookById() throws Exception {

        var bookId = 11L;

        when(bookService.getBookById(bookId)).thenReturn(null);

        mockMvc.perform(get(baseURI + "/{id}", bookId))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(bookId);
    }

    @Test
    void shouldReturnBookResponseDTOWhenGetBookByTitle() throws Exception {

        var book = easyRandom.nextObject(BookResponseDTO.class);

        when(bookService.getBookByTitle(book.getTitle())).thenReturn(book);

        var jsonResponse = mockMvc.perform(get(baseURI + "/title/{title}", book.getTitle())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponseDTO bookReceived = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        assertEquals(book, bookReceived);

        verify(bookService, times(1)).getBookByTitle(book.getTitle());
    }

    @Test
    void shouldReturnNotFoundWhenGetBookByTitle() throws Exception {

        var bookTitle = "test-title";

        when(bookService.getBookByTitle(bookTitle)).thenReturn(null);

        mockMvc.perform(get(baseURI + "/title/{title}", bookTitle)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookByTitle(bookTitle);
    }

    @Test
    void shouldReturnCollectionsOfBooksResponseDTOWhenGetBooksByCategory() throws Exception {

        var categoryName = "test-category";
        var books = easyRandom.objects(BookResponseDTO.class, 5).toList();

        when(bookService.getBooksByCategory(categoryName)).thenReturn(books);

        var jsonResponse = mockMvc.perform(get(baseURI + "/category/{category}", categoryName)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BookResponseDTO> booksReceived = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        assertEquals(books, booksReceived);

        verify(bookService, times(1)).getBooksByCategory(categoryName);
    }

    @Test
    void shouldReturnCollectionsOfBooksResponseDTOWhenGetBooksByAuthor() throws Exception {

        var authorName = "test-author";
        var books = easyRandom.objects(BookResponseDTO.class, 5).toList();

        when(bookService.getBooksByAuthor(authorName)).thenReturn(books);

        var jsonResponse = mockMvc.perform(get(baseURI + "/author/{author}", authorName)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<BookResponseDTO> booksReceived = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        assertEquals(books, booksReceived);

        verify(bookService, times(1)).getBooksByAuthor(authorName);
    }

    @Test
    void shouldReturnBookResponseDTOWhenCreateBook() throws Exception {

        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);
        var bookResponse = easyRandom.nextObject(BookResponseDTO.class);

        when(bookService.createBook(bookRequest)).thenReturn(bookResponse);

        var bookJsonParam = objectMapper.writeValueAsString(bookRequest);

        var jsonResponse = mockMvc.perform(post(baseURI)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJsonParam))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponseDTO bookReceived = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        assertEquals(bookResponse, bookReceived);

        verify(bookService, times(1)).createBook(bookRequest);
    }

    @Test
    void shouldReturnBookResponseDTOWhenUpdateBook() throws Exception {

        var bookId = 97L;

        var bookRequest = easyRandom.nextObject(BookRequestDTO.class);
        var bookResponse = easyRandom.nextObject(BookResponseDTO.class);

        when(bookService.updateBook(bookId, bookRequest)).thenReturn(bookResponse);

        var bookJsonParam = objectMapper.writeValueAsString(bookRequest);

        var jsonResponse = mockMvc.perform(put(baseURI + "/{id}", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJsonParam))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BookResponseDTO bookReceived = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
        assertEquals(bookResponse, bookReceived);

        verify(bookService, times(1)).updateBook(bookId, bookRequest);
    }

    @Test
    void shouldDeleteBookWhenDeleteBookById() throws Exception {
        var bookId = 97L;
        mockMvc.perform(delete(baseURI + "/{id}", bookId)
                        .header("Authorization", token))
                .andExpect(status().isAccepted());
        verify(bookService, times(1)).deleteBookById(bookId);
    }

    @Test
    void shouldAddCategoryToBookWhenAddCategoryToBook() throws Exception {

        var bookId = 88L;
        var categoryDTO = easyRandom.nextObject(CategoryRequestDTO.class);

        var categoryJsonParam = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put(baseURI + "/{id}/add-category", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJsonParam))
                .andExpect(status().isAccepted());

        verify(bookService, times(1)).addCategoryToBook(bookId, categoryDTO);
    }

    @Test
    void shouldRemoveCategoryFromBookWhenRemoveCategoryFromBook() throws Exception {

        var bookId = 88L;
        var categoryDTO = easyRandom.nextObject(CategoryRequestDTO.class);

        when(bookService.removeCategoryFromBook(bookId, categoryDTO)).thenReturn(true);

        var categoryJsonParam = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(delete(baseURI + "/{id}/delete-category", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJsonParam))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(bookService, times(1)).removeCategoryFromBook(bookId, categoryDTO);
    }

    @Test
    void shouldReturnNotFoundWhenRemoveCategoryFromBook() throws Exception {

        var bookId = 88L;
        var categoryDTO = easyRandom.nextObject(CategoryRequestDTO.class);

        when(bookService.removeCategoryFromBook(bookId, categoryDTO)).thenReturn(false);

        var categoryJsonParam = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(delete(baseURI + "/{id}/delete-category", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJsonParam))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).removeCategoryFromBook(bookId, categoryDTO);
    }

    @Test
    void shouldUpdateCategoriesForBookWhenUpdateCategoriesForBook() throws Exception {

        var bookId = 88L;
        var categoriesDTO = easyRandom.objects(CategoryRequestDTO.class, 5).toList();

        var categoriesJsonParam = objectMapper.writeValueAsString(categoriesDTO);

        mockMvc.perform(put(baseURI + "/{id}/update-categories", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoriesJsonParam))
                .andExpect(status().isAccepted());

        verify(bookService, times(1)).updateCategoriesForBook(bookId, categoriesDTO);
    }

    @Test
    void shouldAddAuthorToBookWhenAddAuthorToBook() throws Exception {

        var bookId = 88L;
        var authorDTO = easyRandom.nextObject(AuthorRequestDTO.class);

        var authorJsonParam = objectMapper.writeValueAsString(authorDTO);

        mockMvc.perform(put(baseURI + "/{id}/add-author", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJsonParam))
                .andExpect(status().isAccepted());

        verify(bookService, times(1)).addAuthorToBook(bookId, authorDTO);
    }

    @Test
    void shouldRemoveAuthorFromBookWhenRemoveAuthorFromBook() throws Exception {

        var bookId = 88L;
        var authorDTO = easyRandom.nextObject(AuthorRequestDTO.class);

        when(bookService.removeAuthorFromBook(bookId, authorDTO)).thenReturn(true);

        var authorJsonParam = objectMapper.writeValueAsString(authorDTO);

        mockMvc.perform(delete(baseURI + "/{id}/delete-author", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJsonParam))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(bookService, times(1)).removeAuthorFromBook(bookId, authorDTO);
    }

    @Test
    void shouldReturnNotFoundWhenRemoveAuthorFromBook() throws Exception {

        var bookId = 88L;
        var authorDTO = easyRandom.nextObject(AuthorRequestDTO.class);

        when(bookService.removeAuthorFromBook(bookId, authorDTO)).thenReturn(false);

        var authorJsonParam = objectMapper.writeValueAsString(authorDTO);

        mockMvc.perform(delete(baseURI + "/{id}/delete-author", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJsonParam))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).removeAuthorFromBook(bookId, authorDTO);
    }

    @Test
    void shouldUpdateAuthorsForBookWhenUpdateAuthorsForBook() throws Exception {

        var bookId = 88L;
        var authorsDTO = easyRandom.objects(AuthorRequestDTO.class, 5).toList();

        var authorsJsonParam = objectMapper.writeValueAsString(authorsDTO);

        mockMvc.perform(put(baseURI + "/{id}/update-authors", bookId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorsJsonParam))
                .andExpect(status().isAccepted());

        verify(bookService, times(1)).updateAuthorsForBook(bookId, authorsDTO);
    }
}