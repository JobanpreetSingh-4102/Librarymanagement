package main.java.library.models;

public class LibraryInfo {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String mondayToFriday;
    private String saturday;
    private String sunday;
    private int maxBooksPerStudent;

    public LibraryInfo() {
        this.name = "University Library Heidelberg";
        this.address = "Heidelberg, Germany";
        this.phone = "+49 176 296 13484";
        this.email = "info@universitylibrary-heidelberg.de";
        this.mondayToFriday = "8:00 AM - 8:00 PM";
        this.saturday = "9:00 AM - 5:00 PM";
        this.sunday = "Closed";
        this.maxBooksPerStudent = 5;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMondayToFriday() { return mondayToFriday; }
    public void setMondayToFriday(String mondayToFriday) { this.mondayToFriday = mondayToFriday; }

    public String getSaturday() { return saturday; }
    public void setSaturday(String saturday) { this.saturday = saturday; }

    public String getSunday() { return sunday; }
    public void setSunday(String sunday) { this.sunday = sunday; }

    public int getMaxBooksPerStudent() { return maxBooksPerStudent; }
    public void setMaxBooksPerStudent(int maxBooksPerStudent) { this.maxBooksPerStudent = maxBooksPerStudent; }
}
