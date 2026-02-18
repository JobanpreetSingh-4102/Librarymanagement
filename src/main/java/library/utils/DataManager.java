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
    private int nextBookId = 1;
    private int nextMemberId = 1;
    private int nextTransactionId = 1;
    private int nextRequestId = 1;
    private Member currentUser;

    private DataManager() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        transactions = new ArrayList<>();
        bookRequests = new ArrayList<>();
        libraryInfo = new LibraryInfo();
        initializeSampleData();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void initializeSampleData() {
        addBook(new Book(nextBookId++, "The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", true,
                "Fiction", "Fiction", "A1", 3, 3));
        addBook(new Book(nextBookId++, "To Kill a Mockingbird", "Harper Lee", "978-0061120084", true,
                "Fiction", "Fiction", "A2", 2, 2));
        addBook(new Book(nextBookId++, "1984", "George Orwell", "978-0451524935", true,
                "Science Fiction", "Science Fiction", "B1", 4, 4));
        addBook(new Book(nextBookId++, "Pride and Prejudice", "Jane Austen", "978-0141439518", true,
                "Romance", "Romance", "C1", 2, 2));
        addBook(new Book(nextBookId++, "The Catcher in the Rye", "J.D. Salinger", "978-0316769488", true,
                "Fiction", "Fiction", "A3", 3, 3));
        addBook(new Book(nextBookId++, "Introduction to Algorithms", "Thomas Cormen", "978-0262033848", true,
                "Computer Science", "Computer Science", "D1", 5, 5));
        addBook(new Book(nextBookId++, "Clean Code", "Robert C. Martin", "978-0132350884", true,
                "Computer Science", "Computer Science", "D2", 3, 3));
        addBook(new Book(nextBookId++, "The Art of War", "Sun Tzu", "978-1599869773", true,
                "History", "History", "E1", 2, 2));

        addMember(new Member(nextMemberId++, "John Smith", "john.smith@email.com", "555-0101", LocalDate.of(2024, 1, 15)));
        addMember(new Member(nextMemberId++, "Emily Johnson", "emily.j@email.com", "555-0102", LocalDate.of(2024, 3, 22)));
        addMember(new Member(nextMemberId++, "Michael Brown", "m.brown@email.com", "555-0103", LocalDate.of(2024, 6, 10)));
        addMember(new Member(nextMemberId++, "Sarah Davis", "sarah.d@email.com", "555-0104", LocalDate.of(2024, 8, 5)));

        currentUser = members.get(0);

        createSampleTransaction(books.get(2), members.get(0),
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));
        createSampleTransaction(books.get(0), members.get(0),
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(9));

        bookRequests.add(new BookRequest(nextRequestId++, members.get(1), "Design Patterns", "Gang of Four", "Needed for software engineering course"));
    }

    private void createSampleTransaction(Book book, Member member, LocalDate borrowDate, LocalDate dueDate) {
        Transaction transaction = new Transaction(nextTransactionId++, book, member, borrowDate, dueDate);
        transactions.add(transaction);
        book.borrowCopy();
    }

    public List<Book> getBooks() { return new ArrayList<>(books); }
    public List<Member> getMembers() { return new ArrayList<>(members); }
    public List<Transaction> getTransactions() { return new ArrayList<>(transactions); }
    public List<BookRequest> getBookRequests() { return new ArrayList<>(bookRequests); }
    public LibraryInfo getLibraryInfo() { return libraryInfo; }

    public Member getCurrentUser() { return currentUser; }
    public void setCurrentUser(Member user) { this.currentUser = user; }

    public void addBook(Book book) {
        if (book.getId() == 0) book.setId(nextBookId++);
        books.add(book);
    }

    public void updateBook(Book book) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                return;
            }
        }
    }

    public void deleteBook(int bookId) {
        books.removeIf(b -> b.getId() == bookId);
    }

    public void addMember(Member member) {
        if (member.getId() == 0) member.setId(nextMemberId++);
        members.add(member);
    }

    public void updateMember(Member member) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId() == member.getId()) {
                members.set(i, member);
                return;
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
        if (transaction.getId() == 0) transaction.setId(nextTransactionId++);
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
        if (request.getId() == 0) request.setId(nextRequestId++);
        bookRequests.add(request);
    }

    public List<Book> searchBooks(String query) {
        String lowerQuery = query.toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery) ||
                        b.getAuthor().toLowerCase().contains(lowerQuery) ||
                        b.getIsbn().toLowerCase().contains(lowerQuery) ||
                        b.getSection().toLowerCase().contains(lowerQuery) ||
                        b.getGenre().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByField(String query, String field) {
        String lowerQuery = query.toLowerCase();
        return books.stream()
                .filter(b -> {
                    switch (field) {
                        case "Title": return b.getTitle().toLowerCase().contains(lowerQuery);
                        case "Author": return b.getAuthor().toLowerCase().contains(lowerQuery);
                        case "ISBN": return b.getIsbn().toLowerCase().contains(lowerQuery);
                        case "Section": return b.getSection().toLowerCase().contains(lowerQuery);
                        case "Genre": return b.getGenre().toLowerCase().contains(lowerQuery);
                        default: return b.getTitle().toLowerCase().contains(lowerQuery) ||
                                b.getAuthor().toLowerCase().contains(lowerQuery) ||
                                b.getIsbn().toLowerCase().contains(lowerQuery) ||
                                b.getSection().toLowerCase().contains(lowerQuery) ||
                                b.getGenre().toLowerCase().contains(lowerQuery);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Book> getAvailableBooks() {
        return books.stream().filter(Book::isAvailable).collect(Collectors.toList());
    }

    public List<Transaction> getActiveTransactions() {
        return transactions.stream().filter(t -> !t.isReturned()).collect(Collectors.toList());
    }

    public List<Transaction> getOverdueTransactions() {
        return transactions.stream().filter(Transaction::isOverdue).collect(Collectors.toList());
    }

    public List<Transaction> getMemberTransactions(Member member) {
        return transactions.stream()
                .filter(t -> t.getMember().getId() == member.getId())
                .collect(Collectors.toList());
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
        return bookRequests.stream().filter(BookRequest::isPending).collect(Collectors.toList());
    }

    public Book getBookById(int id) {
        return books.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    public Member getMemberById(int id) {
        return members.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
    }

    public int getTotalBooks() { return books.size(); }
    public int getTotalMembers() { return members.size(); }
    public int getActiveLoansCount() { return getActiveTransactions().size(); }
    public int getOverdueCount() { return getOverdueTransactions().size(); }

    public double getMemberTotalFines(Member member) {
        return transactions.stream()
                .filter(t -> t.getMember().getId() == member.getId() && t.calculateFine() > 0 && !t.isFinePaid())
                .mapToDouble(Transaction::calculateFine)
                .sum();
    }

    public List<String> getGenres() {
        return books.stream()
                .map(Book::getGenre)
                .distinct()
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
}
