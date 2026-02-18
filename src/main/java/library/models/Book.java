package main.java.library.models;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;
    private String section;
    private String genre;
    private String shelf;
    private int quantity;
    private int availableQuantity;

    public Book(int id, String title, String author, String isbn, boolean available) {
        this(id, title, author, isbn, available, "General", "General", "A1", 1, 1);
    }

    public Book(int id, String title, String author, String isbn, boolean available,
                String section, String genre, String shelf, int quantity, int availableQuantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.available = available;
        this.section = section;
        this.genre = genre;
        this.shelf = shelf;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isAvailable() { return availableQuantity > 0; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getShelf() { return shelf; }
    public void setShelf(String shelf) { this.shelf = shelf; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
        this.available = availableQuantity > 0;
    }

    public String getLocation() {
        return section + " - Shelf " + shelf;
    }

    public String getStatus() {
        if (availableQuantity > 0) {
            return "Available (" + availableQuantity + "/" + quantity + ")";
        }
        return "All Borrowed";
    }

    public void borrowCopy() {
        if (availableQuantity > 0) {
            availableQuantity--;
            available = availableQuantity > 0;
        }
    }

    public void returnCopy() {
        if (availableQuantity < quantity) {
            availableQuantity++;
            available = true;
        }
    }
}
