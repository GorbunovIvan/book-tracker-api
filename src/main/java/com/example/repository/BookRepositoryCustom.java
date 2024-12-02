package com.example.repository;

import com.example.model.Book;

public interface BookRepositoryCustom {
    Book saveCascading(Book book);
}
