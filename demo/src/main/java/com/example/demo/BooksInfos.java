package com.example.demo;

import java.util.ArrayList;
import java.util.List;

// 一つの本の情報　これを複数扱うクラス
public class BooksInfos {
    private List<BookInfo> bookInfoList;

    public BooksInfos() {
        bookInfoList = new ArrayList<>();
    }

    public void addBookInfo(BookInfo bookInfo) {
        bookInfoList.add(bookInfo);
    }

    public List<BookInfo> getBookInfoList() {
        return bookInfoList;
    }

    // 他に必要なメソッドを追加できます

    public static void main(String[] args) {
        // BooksInfos クラスを使用して BookInfo のリストを管理する例
        BooksInfos booksInfos = new BooksInfos();

        // BookInfo オブジェクトの作成
        BookInfo book1 = new BookInfo("Book1", "Author1", "123456789", "Description1", "ISBN1", 1000, "Publisher1", "2023-01-01", "Series1");
        BookInfo book2 = new BookInfo("Book2", "Author2", "987654321", "Description2", "ISBN2", 1500, "Publisher2", "2023-02-01", "Series2");

        // BookInfo を BooksInfos に追加
        booksInfos.addBookInfo(book1);
        booksInfos.addBookInfo(book2);

        // BookInfo リストの取得
        List<BookInfo> bookInfoList = booksInfos.getBookInfoList();

        // BookInfo リストの操作などを行うことができます
    }
}
