package main.java.library.models;

import java.time.LocalDate;

public class BookRequest {
    private int id;
    private Member requestedBy;
    private String bookTitle;
    private String authorName;
    private String reason;
    private LocalDate requestDate;
    private String status;

    public BookRequest(int id, Member requestedBy, String bookTitle, String authorName, String reason) {
        this.id = id;
        this.requestedBy = requestedBy;
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.reason = reason;
        this.requestDate = LocalDate.now();
        this.status = "Pending";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Member getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Member requestedBy) { this.requestedBy = requestedBy; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void approve() { this.status = "Approved"; }
    public void reject() { this.status = "Rejected"; }
    public boolean isPending() { return "Pending".equals(status); }
}
