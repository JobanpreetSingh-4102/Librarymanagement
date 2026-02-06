package main.java.library.models;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Transaction(int id, Book book, Member member, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue() {
        if (isReturned()) return false;
        return LocalDate.now().isAfter(dueDate);
    }

    public String getStatus() {
        if (isReturned()) return "Returned";
        if (isOverdue()) return "Overdue";
        return "Active";
    }
}
