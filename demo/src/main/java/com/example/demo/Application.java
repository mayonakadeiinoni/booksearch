package com.example.demo;

public class Application {
    

    private String bookName;
    private String author;
    private String pref;
    private String city;


    public Application(String bookName, String author, String pref, String city) {
        this.bookName = bookName;
        this.author = author;
        this.pref = pref;
        this.city = city;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public String getPref() {
        return pref;
    }

    public String getCity() {
        return city;
    }


}

