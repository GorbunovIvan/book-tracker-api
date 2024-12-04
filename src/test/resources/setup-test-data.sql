INSERT INTO categories (name) VALUES ('category-1');
INSERT INTO categories (name) VALUES ('category-2');
INSERT INTO categories (name) VALUES ('category-3');

INSERT INTO authors (name) VALUES ('author-1');
INSERT INTO authors (name) VALUES ('author-2');
INSERT INTO authors (name) VALUES ('author-3');

INSERT INTO books (title, published_at, total_pages, added_at) values ('book-1', '2010-08-26', 2, '2024-12-02 13:57:27.0');

INSERT INTO books_authors (book_id, author_id) VALUES (1, 1);
INSERT INTO books_authors (book_id, author_id) VALUES (1, 2);
INSERT INTO books_authors (book_id, author_id) VALUES (1, 3);

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (1, 2);
INSERT INTO books_categories (book_id, category_id) VALUES (1, 3);
