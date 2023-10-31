package com.example.demo;

// 楽天ブックスAPIで取得した一つの本の情報を入れるクラス
public class BookInfo {
    private String bookName;
    private String author;
    private String isbnNum;
    private String itemCaption;
    private String isbn;
    private long itemPrice;
    private String publisherName;
    private String salesDate;
    private String seriesName;

    public BookInfo(String bookName, String author, String isbnNum, String itemCaption, String isbn, long itemPrice, String publisherName, String salesDate, String seriesName) {
        this.bookName = bookName;
        this.author = author;
        this.isbnNum = isbnNum;
        this.itemCaption = itemCaption;
        this.isbn = isbn;
        this.itemPrice = itemPrice;
        this.publisherName = publisherName;
        this.salesDate = salesDate;
        this.seriesName = seriesName;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbnNum() {
        return isbnNum;
    }

    public String getItemCaption() {
        return itemCaption;
    }

    public String getIsbn() {
        return isbn;
    }

    public long getItemPrice() {
        return itemPrice;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getSalesDate() {
        return salesDate;
    }

    public String getSeriesName() {
        return seriesName;
    }
}
