CREATE DATABASE library_management;

CREATE TABLE books (
    book_id int PRIMARY KEY,
    booktitle CHAR,
    available int not null,
    ebooks CHAR(5) not null,
    author CHAR,
    ISBN CHAR not null,
    QTY int not null,
    SECTION CHAR,
    SHELF CHAR,
);

CREATE TABLE members (
    member_id int PRIMARY KEY,
    membername CHAR,
    email CHAR,
    phone CHAR,
    membersince DATE,
);

CREATE TABLE TRANSACTIONS (
    transactionid int PRIMARY KEY,
    actionbook_id int not null,
    actionmember_id int not null,
    brrwdate DATE not null,
    duedate DATE not null,
    returndate DATE,
    FOREIGN KEY(actionbook_id) REFERENCES books(book_id),
    FOREIGN KEY(actionmember_id) REFERENCES members(member_id),
);

CREATE TABLE BOOKREQUEST (
    request_id int PRIMARY KEY,
    requestmember_id int not null,
    requestbook_id int not null,
    reason CHAR, 
    FOREIGN KEY(requestmember_id) REFERENCES member(member_id),
    FOREIGN KEY(requestbook_id) REFERENCES books(book_id),
)

