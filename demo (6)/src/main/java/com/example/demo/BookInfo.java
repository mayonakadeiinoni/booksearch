package com.example.demo;

// 楽天ブックスAPIで取得した一つの本の情報を入れるクラス
public class BookInfo {
    private String bookName = "";
    private String author = "";
    private String isbnNum = "";
    private String itemCaption = "";
    private long itemPrice = -1;
    private String publisherName = "";
    private String salesDate = "";
    private String seriesName = "";
    private String itemUrl = "";
    private String genreId="";
    private String largeImageUrl = "";
    /**/private int id;/**/
    
    public BookInfo(){

    }
    public BookInfo(String bookName, String author, String isbnNum, String itemCaption, long itemPrice, 
    		String publisherName, String salesDate, String seriesName,String itemUrl,String largeImageUrl,String genreId) {
        this.bookName = bookName;
        this.author = author;
        this.isbnNum = isbnNum;
        this.itemCaption = itemCaption;
        
        this.itemPrice = itemPrice;
        this.publisherName = publisherName;
        this.salesDate = salesDate;
        this.seriesName = seriesName;
        this.itemUrl = itemUrl;
        this.largeImageUrl = largeImageUrl; // 新しいフィールドの初期化
        this.genreId=genreId;
    }
    /**temp*/
    public BookInfo(/**/int id,/**/String bookName, String author, String isbnNum, String itemCaption, long itemPrice, String publisherName, String salesDate, String seriesName,String itemUrl) {
        this.id=id;
        this.bookName = bookName;
        this.author = author;
        this.isbnNum = isbnNum;
        this.itemCaption = itemCaption;
        
        this.itemPrice = itemPrice;
        this.publisherName = publisherName;
        this.salesDate = salesDate;
        this.seriesName = seriesName;
        this.itemUrl = itemUrl;
    }

    /**/public int getId() {
    	return id;
    }
    
    public String getLargeImageUrl() {
        return largeImageUrl;
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
    
    public String getItemUrl() {
    	return itemUrl;
    }
    
    public String getGenreId() {
    	return genreId;
    }

     // toString()メソッドをオーバーライド
     @Override
     public String toString() {
         return "フィールド名: フィールド値\n" +
                "bookName: " + bookName + "\n" +
                "author: " + author + "\n" +
                "isbnNum: " + isbnNum + "\n" +
                "itemCaption: " + itemCaption + "\n" +
                "itemPrice: " + itemPrice + "\n" +
                "publisherName: " + publisherName + "\n" +
                "salesDate: " + salesDate + "\n" +
                "seriesName: " + seriesName + "\n" +
                "itemUrl: " + itemUrl;
     }
}
