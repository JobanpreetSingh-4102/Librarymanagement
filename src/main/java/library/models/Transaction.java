package main.java.library.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Transaction {
    private int id;
    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private boolean finePaid;
    private static final double FINE_PER_DAY = 2.0;

    public Transaction(int id, Book book, Member member, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fine = 0.0;
        this.finePaid = false;
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

    public double calculateFine() {
        if (isReturned()) {
            if (returnDate.isAfter(dueDate)) {
                long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
                return daysOverdue * FINE_PER_DAY;
            }
            return 0.0;
        }
        if (LocalDate.now().isAfter(dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            return daysOverdue * FINE_PER_DAY;
        }
        return 0.0;
    }

    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }

    public void payFine() { this.finePaid = true; }

    public String getStatus() {
        if (isReturned()) return "Returned";
        if (isOverdue()) {
            double fineAmount = calculateFine();
            return String.format("Overdue (Fine: $%.2f)", fineAmount);
        }
        return "Active";
    }
}
