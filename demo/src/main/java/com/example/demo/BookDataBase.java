package com.example.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public enum BookDataBase {
	INSTANCE;

	private Semaphore available = new Semaphore(1, true);

	public static BookDataBase getInstance() {
		return INSTANCE;
	}

	private BookDataBase() {// コンストラクタ
		try {
			available.acquire();
			Connection con = Connect();
			String sql = "create table if not exists books(id integer primary key, bookName String, author String, isbnNum String, itemCaption String, itemPrice long, publisherName String, salesDate String, seriesName String, itemUrl String)";
			Statement stm = con.createStatement();
			stm.executeUpdate(sql);
			Close(con);
			available.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getMaxId() {
		int maxid = -1;
		try {
			available.acquire();
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("select max(id) as maxid from books");
			ResultSet rs = pstmt.executeQuery();
			maxid = rs.getInt("maxid");
			Close(con);
			available.release();
		} catch (Exception e) {
			System.out.println(e);
		}
		return maxid + 1;
	}

	public void Insert(BookInfo book) {// データベースに要素を挿入する
		try {
			if (Select(book.getId()) == null) {
				available.acquire();
				Connection con = Connect();
				PreparedStatement pstmt;
				pstmt = con.prepareStatement(
						"insert into books(id, bookName, author, isbnNum, itemCaption, itemPrice, publisherName, salesDate, seriesName, itemUrl) values(?,?,?,?,?,?,?,?,?,?)");
				pstmt.setInt(1, book.getId());
				pstmt.setString(2, book.getBookName());
				pstmt.setString(3, book.getAuthor());
				pstmt.setString(4, book.getIsbnNum());
				pstmt.setString(5, book.getItemCaption());
				pstmt.setLong(6, book.getItemPrice());
				pstmt.setString(7, book.getPublisherName());
				pstmt.setString(8, book.getSalesDate());
				pstmt.setString(9, book.getSeriesName());
				pstmt.setString(10, book.getItemUrl());
				pstmt.executeUpdate();
				pstmt.close();
				Close(con);
				available.release();
			} else {
				System.out.println("重複しています");
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Insertでエラー");
		}
	}

	public BookInfo Select(int id) {// データベースから要素のリストを得る
		BookInfo book = null;
		try {
			available.acquire();
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("select * from books where id=?");
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				book = new BookInfo(rs.getInt("id"), rs.getString("bookName"), rs.getString("author"),
						rs.getString("isbnNum"), rs.getString("itemCaption"), rs.getLong("itemPrice"),
						rs.getString("publisherName"), rs.getString("salesDate"), rs.getString("seriesName"),
						rs.getString("itemUrl"));
			}
			rs.close();
			pstmt.close();
			Close(con);
			available.release();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Selectでエラー");
		}
		return book;
	}

	public List<BookInfo> SelectAll() {
		List<BookInfo> list = new ArrayList<>();
		try {
			available.acquire();
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("select * from books");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new BookInfo(rs.getInt("id"), rs.getString("bookName"), rs.getString("author"),
						rs.getString("isbnNum"), rs.getString("itemCaption"), rs.getLong("itemPrice"),
						rs.getString("publisherName"), rs.getString("salesDate"), rs.getString("seriesName"),
						rs.getString("itemUrl")));
			}
			rs.close();
			pstmt.close();
			Close(con);
			available.release();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("SelectAllでエラー");
		}
		return list;
	}

	public Connection Connect() {
		Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:sqlite:pro3db");
		} catch (Exception e) {
			System.out.println(e);
		}
		return con;
	}

	public void Close(Connection con) {// データベースとの接続を切る
		try {
			con.close();
		} catch (Exception e) {
		}
	}

}
