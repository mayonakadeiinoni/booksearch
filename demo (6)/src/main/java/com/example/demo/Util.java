package com.example.demo;

import java.sql.*;
import java.util.concurrent.Semaphore;
import java.util.*;

public class Util {
	private static Semaphore available = new Semaphore(1, true);
	private static DataBase database=new DataBase();
	public long getId() {
		long id;
		Random rand=new Random();
		Connection con=null;
		try {
			available.acquire();
			con=database.Connect();
			PreparedStatement pstmt=con.prepareStatement("SELECT ses_id FROM sessions WHERE ses_id=?");
			while(true) {
				id=rand.nextLong(Long.MAX_VALUE);
				pstmt.setString(1, id+"");
				ResultSet rs=pstmt.executeQuery();
				if(!rs.next()) {
					rs.close();
					break;
				}
				rs.close();
			}
			pstmt.close();
			pstmt=con.prepareStatement("INSERT INTO sessions values(?,0)");
			pstmt.setString(1, id+"");
			if(pstmt.executeUpdate()==0) new RuntimeException();
			pstmt.close();
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("getId Error");
		} finally {
			database.Close(con);
			available.release();
		}
		return id;
	}
	
	public void setData(BooksInfos infos,long id/**TEMP*/,int page/**/) {
		//Connection con=null;
		try {
			available.acquire();
			/*con=Connect();
			con.setAutoCommit(false);
			PreparedStatement pstmt1=con.prepareStatement(
					"INSERT INTO books VALUES(?,?,?) ON CONFLICT(isbn) DO UPDATE SET (name,price)=(excluded.name,excluded.price)");
			PreparedStatement pstmt2=con.prepareStatement("INSERT INTO authors VALUES(?,?)");
			PreparedStatement pstmt3=con.prepareStatement("INSERT INTO cache VALUES(?,?,?)");
			pstmt3.setInt(3, page);
			pstmt3.setString(2, ""+id);*/
			for(BookInfo info:infos.getBookInfoList()) {
				/*
				pstmt1.setString(1, info.getIsbnNum());
				pstmt1.setString(2, info.getBookName());
				pstmt1.setInt(3, (int)info.getItemPrice());
				pstmt1.executeUpdate();
				pstmt2.setString(1, info.getIsbnNum());
				for(String author:info.getAuthor().split("/")) {
					pstmt2.setString(2, author);
					try {
						pstmt2.executeUpdate();
					} catch(SQLException e) {
						break;
					}
				}
				pstmt3.setString(1, info.getIsbnNum());
				pstmt3.executeUpdate();
				*/
				database.Insert(info, id+"", page);
				database.sessionUpdate(id+"", page);
			}
			/*
			pstmt1.close();
			pstmt2.close();
			pstmt3.close();
			con.commit();*/
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("setData Error");
		} finally {
			//Close(con);
			available.release();
		}
	}

	public List<BookInfo> getData(long id,int page) {
		//Connection con=null;
		List<BookInfo> result=new ArrayList<>();
		try {
			available.acquire();
			/*con=Connect();
			PreparedStatement pstmt=con.prepareStatement("SELECT * FROM (SELECT * FROM cache WHERE ses_id=?) NATURAL JOIN books NATURAL JOIN (SELECT isbn,group_concat(name,'/') FROM authors GROUP BY isbn) WHERE page=?");
			pstmt.setString(1, ""+id);
			pstmt.setInt(2, page);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()) {
				BookInfo info=new BookInfo(rs.getString(4),rs.getString(6),rs.getString(1),null,rs.getInt(5),null,null,null,null,null);
				result.add(info);
			}*/
			result=database.BookSelect(id+"", page);
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("getData Error");
		} finally {
			//Close(con);
			available.release();
		}
		return result;
	}
	
	public boolean hasCache(long id,int page) {
		return database.cacheSelect(""+id, page).size()!=0;
	}
	
	public List<BookInfo> getSelectedData(long id,int page,String selector) {
		return database.selectWithSelector(""+id, selector, 30, 30*(page-1));
	}
	
	public int getMaxPage(long id) {
		return database.selectMaxPage(""+id);
	}
	
	/*public Connection Connect() {
		Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:sqlite:test.db");
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
	}*/
	
	public static void main(String[] args) {
		Util util=new Util();
		long id=util.getId();
		Application app=new Application("異邦人","カミュ",null,null);
		BooksInfos infos = new BooksInfos();
       infos = infos.RakutenBooksSearch(app);
       util.setData(infos, id, 1);
       util.setData(infos, id, 2);
       System.out.println(util.getData(id, 1));
	}
}
