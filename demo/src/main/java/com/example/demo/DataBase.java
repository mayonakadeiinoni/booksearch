package com.example.demo;

import java.sql.*;
import java.util.*;

public class DataBase {
	public DataBase() {//コンストラクタ、テーブル生成
		Connection con = Connect();
		try {
			String sql = "create table if not exists books(isbn TEXT PRIMARY KEY,bookName TEXT,price Long,itemCaption String, publisherName String, salesDate String, seriesName String, itemUrl String, imageUrl String)";
			Statement stm = con.createStatement();
			stm.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Close(con);
		}
		con = Connect();
		try {
			String sql = "create table if not exists authors(isbn TEXT REFERENCES books,authorName TEXT,PRIMARY KEY(isbn,authorName))";
			Statement stm = con.createStatement();
			stm.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Close(con);
		}
		con = Connect();
		try {
			String sql = "create table if not exists genres(isbn TEXT REFERENCES books,genreName TEXT,PRIMARY KEY(isbn,genreName))";
			Statement stm = con.createStatement();
			stm.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Close(con);
		}
		con = Connect();
		try {
			String sql = "create table if not exists sessions(ses_id TEXT PRIMARY KEY,max_page INTEGER)";
			Statement stm = con.createStatement();
			stm.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Close(con);
		}
		con = Connect();
		try {
			String sql = "create table if not exists cache(isbn TEXT REFERENCES books,ses_id TEXT REFERENCES sessions,page INTEGER,list INTEGER,PRIMARY KEY(isbn,ses_id,page))";
			Statement stm = con.createStatement();
			stm.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Close(con);
		}
	}
	//本、著者、キャッシュの情報をINSERTする
	public void Insert(BookInfo book, String ses_id, /*int max_page,*/ int page,int list) {
		Connection con = Connect();
		PreparedStatement pstmt;
		try {
			pstmt = con.prepareStatement(
					"insert into books(isbn, bookName, price, itemCaption, publisherName, salesDate, seriesName, itemUrl, imageUrl) values(?,?,?,?,?,?,?,?,?)"+
					"ON CONFLICT(isbn) DO UPDATE SET (bookName, price, itemCaption, publisherName, salesDate, seriesName, itemUrl)=(excluded.bookName, excluded.price, excluded.itemCaption, excluded.publisherName, excluded.salesDate, excluded.seriesName, excluded.itemUrl)");
			pstmt.setString(1, book.getIsbnNum());
			pstmt.setString(2, book.getBookName());
			pstmt.setLong(3, book.getItemPrice());
			pstmt.setString(4, book.getItemCaption());
			pstmt.setString(5, book.getPublisherName());
			pstmt.setString(6, book.getSalesDate());
			pstmt.setString(7, book.getSeriesName());
			pstmt.setString(8, book.getItemUrl());
			pstmt.setString(9, book.getLargeImageUrl());
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Insertでエラー");
		} finally {
			Close(con);
		}
		con=Connect();
		try {
			pstmt = con.prepareStatement("insert into authors(isbn, authorName) values(?,?)");
			pstmt.setString(1, book.getIsbnNum());
			/*pstmt.setString(2, book.getAuthor());
			pstmt.executeUpdate();*/
			for(String author:book.getAuthor().split("/")) {
				pstmt.setString(2, author);
				pstmt.executeUpdate();
			}
			pstmt.close();
		} catch (SQLException e) {
			/*System.out.println(e);
			System.out.println("Insertでエラー");*/
		} finally {
			Close(con);
		}
		con=Connect();
		try {
			pstmt = con.prepareStatement("insert into genres(isbn, genreName) values(?,?)");
			pstmt.setString(1, book.getIsbnNum());
			/*pstmt.setString(2, book.getGenreldId());
			pstmt.executeUpdate();*/
			for(String genreId:book.getGenreId().split("/")) {
				pstmt.setString(2, genreId);
				pstmt.executeUpdate();
			}
			pstmt.close();
		} catch (Exception e) {
			/*System.out.println(e);
			System.out.println("Insertでエラー");*/
		} finally {
			Close(con);
		}
		con=Connect();
		try {
			pstmt = con.prepareStatement("insert into cache(isbn,ses_id, page,list) values(?,?,?,?)");
			pstmt.setString(1, book.getIsbnNum());
			pstmt.setString(2, ses_id);
			pstmt.setInt(3, page);
			pstmt.setInt(4, list);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Insertでエラー");
		} finally {
			Close(con);
		}
	}
	//セッションをINSERTする
	public void sessionInsert(String ses_id, int max_page) {
		Connection con = Connect();
		try {
			PreparedStatement pstmt = con.prepareStatement("insert into sessions(ses_id, max_page) values(?,?)");
			pstmt.setString(1, ses_id);
			pstmt.setInt(2, max_page);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("sessionInsertでエラー");
		} finally {
			Close(con);
		}
	}
	/**temp*/
	//セッションをUPDATEする
	public void sessionUpdate(String ses_id, int max_page) {
		Connection con = Connect();
		try {
			PreparedStatement pstmt = con.prepareStatement("update sessions set max_page=? where ses_id=? and max_page<?");
			pstmt.setString(2, ses_id);
			pstmt.setInt(1, max_page);
			pstmt.setInt(3, max_page);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("sessionInsertでエラー");
		} finally {
			Close(con);
		}
	}
	public int selectMaxPage(String ses_id) {
		int result=0;
		try {
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT max_page FROM sessions WHERE ses_id=?");
			pstmt.setString(1, ses_id);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				result=rs.getInt("max_page");
			}
			rs.close();
			pstmt.close();
			Close(con);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("SelectMaxPageでエラー");
		}
		return result;
	}
	/*temp**/
	//キャッシュを取り出す
	public List<Cache> cacheSelect(String ses_id, int page) {
		List<Cache> list = new ArrayList<>();
		try {
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM cache WHERE ses_id=? AND page=?");
			pstmt.setString(1, ses_id);
			pstmt.setInt(2, page);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new Cache(rs.getString("isbn"), rs.getString("ses_id"), rs.getInt("page")));
			}
			rs.close();
			pstmt.close();
			Close(con);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Selectでエラー");
		}
		return list;
	}
	//ses_idとページをもとに検索する
	public List<BookInfo> BookSelect(String ses_id, int page) {
		List<BookInfo> list = new ArrayList<>();
		try {
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement(
					"SELECT * FROM (SELECT * FROM cache WHERE ses_id=? AND page=?) NATURAL JOIN books NATURAL JOIN (SELECT isbn,group_concat(authorName,'/') FROM authors GROUP BY isbn) NATURAL JOIN (SELECT isbn,group_concat(genreName,'/') FROM genres GROUP BY isbn) ORDER BY page ASC,list ASC");
			pstmt.setString(1, ses_id);
			pstmt.setInt(2, page);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new BookInfo(rs.getString("bookName"), rs.getString("group_concat(authorName,'/')"),
						rs.getString("isbn"), rs.getString("itemCaption"), rs.getLong("price"),
						rs.getString("publisherName"), rs.getString("salesDate"), rs.getString("seriesName"),
						rs.getString("itemUrl"), rs.getString("imageUrl"),rs.getString("group_concat(genreName,'/')")));
			}
			rs.close();
			pstmt.close();
			Close(con);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("BookSelectでエラー");
		}
		return list;
	}
	/*
	//値段と取得数を設定して検索する
	public List<BookInfo> selectPriceLimit(int price, int limit, String ses_id) {
		List<BookInfo> list = new ArrayList<>();
		try {
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement(
					"SELECT * FROM (SELECT * FROM cache WHERE ses_id=?) NATURAL JOIN books NATURAL JOIN (SELECT isbn,group_concat(authorName,'/') FROM authors GROUP BY isbn) WHERE price>=? LIMIT ?;");
			pstmt.setString(1, ses_id);
			pstmt.setInt(2, price);
			pstmt.setInt(3, limit);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new BookInfo(rs.getString("bookName"), rs.getString("group_concat(authorName,'/')"),
						rs.getString("isbn"), rs.getString("itemCaption"), rs.getLong("price"),
						rs.getString("publisherName"), rs.getString("salesDate"), rs.getString("seriesName"),
						rs.getString("itemUrl"), "genreldId"));
			}
			rs.close();
			pstmt.close();
			Close(con);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("BookSelectでエラー");
		}
		return list;
	}
	//値段と取得数とオフセットを指定して検索する
	public List<BookInfo> selectPriceLimitOffset(String ses_id, int price, int limit, int offset) {
		List<BookInfo> list = new ArrayList<>();
		try {
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement(
					"SELECT * FROM (SELECT * FROM cache WHERE ses_id=?) NATURAL JOIN books NATURAL JOIN (SELECT isbn,group_concat(authorName,'/') FROM authors GROUP BY isbn) WHERE price>=? LIMIT ? OFFSET ?;");
			pstmt.setString(1, ses_id);
			pstmt.setInt(2, price);
			pstmt.setInt(3, limit);
			pstmt.setInt(4, offset);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new BookInfo(rs.getString("bookName"), rs.getString("group_concat(authorName,'/')"),
						rs.getString("isbn"), rs.getString("itemCaption"), rs.getLong("price"),
						rs.getString("publisherName"), rs.getString("salesDate"), rs.getString("seriesName"),
						rs.getString("itemUrl"), "genreldId"));
			}
			rs.close();
			pstmt.close();
			Close(con);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("BookSelectでエラー");
		}
		return list;
	}
	//著者をもとに検索
	public List<BookInfo> selectByName(String ses_id, String authorName) {
		List<BookInfo> list = new ArrayList<>();
		try {
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement(
					"SELECT * FROM (SELECT * FROM cache WHERE ses_id=?) NATURAL JOIN books NATURAL JOIN (SELECT isbn,group_concat(authorName,'/') FROM authors GROUP BY isbn) WHERE isbn IN (SELECT isbn FROM authors WHERE authorName=?);");
			pstmt.setString(1, ses_id);
			pstmt.setString(2, authorName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new BookInfo(rs.getString("bookName"), rs.getString("group_concat(authorName,'/')"),
						rs.getString("isbn"), rs.getString("itemCaption"), rs.getLong("price"),
						rs.getString("publisherName"), rs.getString("salesDate"), rs.getString("seriesName"),
						rs.getString("itemUrl"), "genreldId"));
			}
			rs.close();
			pstmt.close();
			Close(con);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("BookSelectでエラー");
		}
		return list;
	}
	*/
	public List<BookInfo> selectWithSelector(String ses_id, String selector, int limit, int offset) {
		List<BookInfo> list = new ArrayList<>();
		try {
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement(
					"SELECT * FROM (SELECT * FROM cache WHERE ses_id=?) NATURAL JOIN books NATURAL JOIN (SELECT isbn,group_concat(authorName,'/') FROM authors GROUP BY isbn) NATURAL JOIN (SELECT isbn,group_concat(genreName,'/') FROM genres GROUP BY isbn) WHERE "+selector+" ORDER BY page ASC,list ASC LIMIT ? OFFSET ?");
			pstmt.setString(1, ses_id);
			pstmt.setInt(2, limit);
			pstmt.setInt(3, offset);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new BookInfo(rs.getString("bookName"), rs.getString("group_concat(authorName,'/')"),
						rs.getString("isbn"), rs.getString("itemCaption"), rs.getLong("price"),
						rs.getString("publisherName"), rs.getString("salesDate"), rs.getString("seriesName"),
						rs.getString("itemUrl"), rs.getString("imageUrl"),rs.getString("group_concat(genreName,'/')")));
			}
			rs.close();
			pstmt.close();
			Close(con);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("selectWithSelectorでエラー");
		}
		return list;
	}
	public List<BookInfo> BookSelect(String ses_id, int limit, int offset) {
		List<BookInfo> list = new ArrayList<>();
		try {
			Connection con = Connect();
			PreparedStatement pstmt;
			pstmt = con.prepareStatement(
					"SELECT * FROM (SELECT * FROM cache WHERE ses_id=?) NATURAL JOIN books NATURAL JOIN (SELECT isbn,group_concat(authorName,'/') FROM authors GROUP BY isbn) NATURAL JOIN (SELECT isbn,group_concat(genreName,'/') FROM genres GROUP BY isbn) ORDER BY page ASC,list ASC LIMIT ? OFFSET ?");
			pstmt.setString(1, ses_id);
			pstmt.setInt(2, limit);
			pstmt.setInt(3, offset);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new BookInfo(rs.getString("bookName"), rs.getString("group_concat(authorName,'/')"),
						rs.getString("isbn"), rs.getString("itemCaption"), rs.getLong("price"),
						rs.getString("publisherName"), rs.getString("salesDate"), rs.getString("seriesName"),
						rs.getString("itemUrl"), rs.getString("imageUrl"),rs.getString("group_concat(genreName,'/')")));
			}
			rs.close();
			pstmt.close();
			Close(con);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("BookSelect(limitOffset)でエラー");
		}
		return list;
	}

	public Connection Connect() {
		Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:sqlite:database");
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