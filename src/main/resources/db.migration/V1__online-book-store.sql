CREATE SCHEMA IF NOT EXISTS online_book_store;

CREATE TABLE online_book_store.author (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE online_book_store.book (
    isbn VARCHAR(13) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author_id SERIAL NOT NULL,
    FOREIGN KEY (author_id) REFERENCES online_book_store.author(id)
);

CREATE TABLE online_book_store.review (
    id SERIAL PRIMARY KEY,
    book_isbn VARCHAR(13) NOT NULL,
    reviewer_name VARCHAR(255) NOT NULL,
    content text NOT NULL,
    FOREIGN KEY (book_isbn) REFERENCES online_book_store.book(isbn)
);


INSERT INTO online_book_store.author (name) VALUES
('John Doe'),
('Jane Smith'),
('Robert Johnson'),
('Emily Davis'),
('Michael Brown');
