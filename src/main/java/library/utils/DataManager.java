package main.java.library.utils;

import main.java.library.models.Book;
import main.java.library.models.BookRequest;
import main.java.library.models.LibraryInfo;
import main.java.library.models.Member;
import main.java.library.models.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    private List<Book> books;
    private List<Member> members;
    private List<Transaction> transactions;
    private List<BookRequest> bookRequests;
    private LibraryInfo libraryInfo;
    private Member currentUser;
    private int nextBookId = 1;
    private int nextMemberId = 1;
    private int nextTransactionId = 1;
    private int nextRequestId = 1;

    private DataManager() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        transactions = new ArrayList<>();
        bookRequests = new ArrayList<>();
        libraryInfo = new LibraryInfo();
        loadSampleData();
        if (!members.isEmpty()) {
            currentUser = members.get(0);
        }
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void loadSampleData() {
        addBook(new Book(0, "To Kill a Mockingbird", "Harper Lee", "978-0061120084", true, "Fiction", "Classic Fiction", "A1", 3, 3));
        addBook(new Book(0, "1984", "George Orwell", "978-0451524935", true, "Fiction", "Dystopian", "A2", 2, 2));
        addBook(new Book(0, "The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", true, "Fiction", "Classic Fiction", "A1", 2, 2));
        addBook(new Book(0, "Pride and Prejudice", "Jane Austen", "978-0141439518", true, "Fiction", "Romance", "A3", 2, 2));
        addBook(new Book(0, "The Catcher in the Rye", "J.D. Salinger", "978-0316769488", true, "Fiction", "Coming of Age", "A2", 1, 1));
        addBook(new Book(0, "Introduction to Algorithms", "Thomas H. Cormen", "978-0262033848", true, "Academic", "Computer Science", "B1", 3, 3));
        addBook(new Book(0, "Clean Code", "Robert C. Martin", "978-0132350884", true, "Academic", "Software Engineering", "B2", 2, 2));
        addBook(new Book(0, "A Brief History of Time", "Stephen Hawking", "978-0553380163", true, "Science", "Physics", "C1", 2, 2));

        addMember(new Member(0, "Alice Johnson", "alice@university.edu", "+1-555-0101", LocalDate.of(2024, 9, 1)));
        addMember(new Member(0, "Bob Smith", "bob@university.edu", "+1-555-0102", LocalDate.of(2024, 9, 15)));
        addMember(new Member(0, "Carol Williams", "carol@university.edu", "+1-555-0103", LocalDate.of(2025, 1, 10)));

        Transaction t1 = new Transaction(0, books.get(0), members.get(0), LocalDate.now().minusDays(10), LocalDate.now().plusDays(4));
        addTransactionDirect(t1);
        books.get(0).borrowCopy();

        Transaction t2 = new Transaction(0, books.get(5), members.get(1), LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
        addTransactionDirect(t2);
        books.get(5).borrowCopy();

        Transaction t3 = new Transaction(0, books.get(2), members.get(0), LocalDate.now().minusDays(30), LocalDate.now().minusDays(16));
        t3.setReturnDate(LocalDate.now().minusDays(14));
        addTransactionDirect(t3);
    }

    private void addTransactionDirect(Transaction t) {
        t.setId(nextTransactionId++);
        transactions.add(t);
    }

    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<BookRequest> getBookRequests() {
        return new ArrayList<>(bookRequests);
    }

    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }

    public Member getCurrentUser() { return currentUser; }
    public void setCurrentUser(Member user) { this.currentUser = user; }

    public void addBook(Book book) {
        book.setId(nextBookId++);
        books.add(book);
    }

    public void updateBook(Book book) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                break;
            }
        }
    }

    public void deleteBook(int bookId) {
        books.removeIf(b -> b.getId() == bookId);
    }

    public void addMember(Member member) {
        member.setId(nextMemberId++);
        members.add(member);
    }

    public void updateMember(Member member) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId() == member.getId()) {
                members.set(i, member);
                break;
            }
        }
    }

    public void deleteMember(int memberId) {
        members.removeIf(m -> m.getId() == memberId);
    }

    public boolean addTransaction(Transaction transaction) {
        Book book = transaction.getBook();
        if (book.getAvailableQuantity() <= 0) {
            return false;
        }
        transaction.setId(nextTransactionId++);
        transactions.add(transaction);
        book.borrowCopy();
        return true;
    }

    public boolean returnBook(Transaction transaction) {
        if (transaction.isReturned()) {
            return false;
        }
        transaction.setReturnDate(LocalDate.now());
        transaction.getBook().returnCopy();
        return true;
    }

    public void addBookRequest(BookRequest request) {
        request.setId(nextRequestId++);
        bookRequests.add(request);
    }

    public List<Book> searchBooks(String query) {
        String q = query.toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q) ||
                        b.getAuthor().toLowerCase().contains(q) ||
                        b.getIsbn().toLowerCase().contains(q) ||
                        b.getSection().toLowerCase().contains(q) ||
                        b.getGenre().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByField(String query, String field) {
        if ("All".equals(field)) return searchBooks(query);
        String q = query.toLowerCase();
        return books.stream()
                .filter(b -> {
                    switch (field) {
                        case "Title": return b.getTitle().toLowerCase().contains(q);
                        case "Author": return b.getAuthor().toLowerCase().contains(q);
                        case "ISBN": return b.getIsbn().toLowerCase().contains(q);
                        case "Section": return b.getSection().toLowerCase().contains(q);
                        case "Genre": return b.getGenre().toLowerCase().contains(q);
                        default: return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooks() {
        return books.stream()
                .filter(b -> b.getAvailableQuantity() > 0)
                .collect(Collectors.toList());
    }

    public List<Transaction> getActiveTransactions() {
        return transactions.stream()
                .filter(t -> !t.isReturned())
                .collect(Collectors.toList());
    }

    public List<Transaction> getOverdueTransactions() {
        return transactions.stream()
                .filter(t -> !t.isReturned() && t.isOverdue())
                .collect(Collectors.toList());
    }

    public List<Transaction> getMemberTransactions(Member member) {
        return getMemberAllTransactions(member);
    }

    public List<Transaction> getMemberActiveTransactions(Member member) {
        return transactions.stream()
                .filter(t -> t.getMember().getId() == member.getId() && !t.isReturned())
                .collect(Collectors.toList());
    }

    public List<Transaction> getMemberAllTransactions(Member member) {
        return transactions.stream()
                .filter(t -> t.getMember().getId() == member.getId())
                .collect(Collectors.toList());
    }

    public int getMemberBorrowedCount(Member member) {
        return (int) transactions.stream()
                .filter(t -> t.getMember().getId() == member.getId() && !t.isReturned())
                .count();
    }

    public int getRemainingBorrowLimit(Member member) {
        return libraryInfo.getMaxBooksPerStudent() - getMemberBorrowedCount(member);
    }

    public List<BookRequest> getPendingRequests() {
        return bookRequests.stream()
                .filter(BookRequest::isPending)
                .collect(Collectors.toList());
    }

    public List<BookRequest> getMemberBookRequests(Member member) {
        return bookRequests.stream()
                .filter(r -> r.getRequestedBy().getId() == member.getId())
                .collect(Collectors.toList());
    }

    public Book getBookById(int id) {
        return books.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    public Member getMemberById(int id) {
        return members.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    public int getTotalBooks() { return books.size(); }
    public int getTotalMembers() { return members.size(); }

    public int getActiveLoansCount() {
        return (int) transactions.stream().filter(t -> !t.isReturned()).count();
    }

    public int getOverdueCount() {
        return (int) transactions.stream().filter(t -> !t.isReturned() && t.isOverdue()).count();
    }

    public double getMemberTotalFines(Member member) {
        return getMemberAllTransactions(member).stream()
                .filter(t -> t.calculateFine() > 0 && !t.isFinePaid())
                .mapToDouble(Transaction::calculateFine)
                .sum();
    }

    public List<String> getGenres() {
        return books.stream()
                .map(Book::getGenre)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public double getTotalFinesCollected() {
        return transactions.stream()
                .filter(t -> t.calculateFine() > 0 && t.isFinePaid())
                .mapToDouble(Transaction::calculateFine)
                .sum();
    }

    public double getTotalOutstandingFines() {
        return transactions.stream()
                .filter(t -> t.calculateFine() > 0 && !t.isFinePaid())
                .mapToDouble(Transaction::calculateFine)
                .sum();
    }

    public void updateLibraryInfo(LibraryInfo info) {
        this.libraryInfo = info;
    }

    public void updateBookRequestStatus(int requestId, String status) {
        for (BookRequest r : bookRequests) {
            if (r.getId() == requestId) {
                r.setStatus(status);
                break;
            }
        }
    }

    public void collectFine(int transactionId) {
        for (Transaction t : transactions) {
            if (t.getId() == transactionId) {
                t.payFine();
                break;
            }
        }
    }
}
