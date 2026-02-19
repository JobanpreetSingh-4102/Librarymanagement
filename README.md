# Library Management System

This is a desktop application I built using Java and Java Swing for managing a library. It has two portals — one for students and one for staff.

## What it does

Students can search for books, see what they've borrowed, check their fines, request new books, and view library info.

Staff can add/edit/delete books and members, issue and return books, collect fines, approve book requests, see reports, and update library settings.

There's also a fine system — if a book is returned late, it charges $2.00 per day.

## Technologies

- Java for the backend
- Java Swing for the UI
- Data is stored in-memory using ArrayLists (no database needed)

## How to run

In IntelliJ, just open the project, go to `src/main/java/library/LibraryApp.java`, right-click and run it.

Or from the terminal:
```
cd src/main/java
javac library/LibraryApp.java
java library.LibraryApp
```

## Project structure

- `LibraryApp.java` — main file that starts the app
- `RoleSelectionDialog.java` — the screen where you pick Student or Staff
- `StudentPanel.java` — the student interface (5 tabs)
- `StaffPanel.java` — the staff interface (6 tabs)
- `DataManager.java` — handles all the data (books, members, transactions)
- `Book.java`, `Member.java`, `Transaction.java`, `BookRequest.java`, `LibraryInfo.java` — data models

## Sample data

The app comes with some pre-loaded data so you can test it right away — 8 books, 3 members, and a few transactions including an overdue one to show how fines work.
