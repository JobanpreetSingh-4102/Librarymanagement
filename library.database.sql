DROP TABLE IF EXISTS fines CASCADE;
DROP TABLE IF EXISTS book_requests CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS books CASCADE;

CREATE TABLE books (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       author VARCHAR(150) NOT NULL,
                       isbn VARCHAR(50),
                       available BOOLEAN DEFAULT TRUE,
                       section VARCHAR(50),
                       genre VARCHAR(100),
                       shelf VARCHAR(50),
                       total_quantity INT DEFAULT 1,
                       available_quantity INT DEFAULT 1
);

CREATE TABLE members (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(150) NOT NULL,
                         email VARCHAR(150),
                         phone VARCHAR(50),
                         join_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              book_id INT REFERENCES books(id),
                              member_id INT REFERENCES members(id),
                              borrow_date DATE DEFAULT CURRENT_DATE,
                              due_date DATE,
                              return_date DATE
);

CREATE TABLE fines (
                       id SERIAL PRIMARY KEY,
                       transaction_id INT REFERENCES transactions(id),
                       amount DECIMAL(10,2),
                       paid BOOLEAN DEFAULT FALSE
);

CREATE TABLE book_requests (
                               id SERIAL PRIMARY KEY,
                               member_id INT REFERENCES members(id),
                               book_title VARCHAR(200),
                               status VARCHAR(50) DEFAULT 'Pending'
);

INSERT INTO books (title, author, isbn, section, genre, shelf, total_quantity, available_quantity)
VALUES
    ('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 'Fiction', 'Classic Fiction', 'A1', 3, 3),
    ('1984', 'George Orwell', '978-0451524935', 'Fiction', 'Dystopian', 'A2', 2, 2),
    ('Clean Code', 'Robert C. Martin', '978-0132350884', 'Academic', 'Software Engineering', 'B2', 2, 2);

INSERT INTO members (name, email, phone, join_date)
VALUES
    ('Alice Johnson', 'alice@university.edu', '+1-555-0101', '2024-09-01'),
    ('Bob Smith', 'bob@university.edu', '+1-555-0102', '2024-09-15');

INSERT INTO transactions (book_id, member_id, borrow_date, due_date)
VALUES
    (1, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '14 days'),
    (2, 2, CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE + INTERVAL '7 days');

INSERT INTO fines (transaction_id, amount, paid)
VALUES
    (1, 5.00, FALSE);

INSERT INTO book_requests (member_id, book_title, status)
VALUES
    (2, 'Data Structures in Java', 'Pending');


