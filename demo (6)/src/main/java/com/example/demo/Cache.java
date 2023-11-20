package com.example.demo;

public class Cache {
	private String isbn;
	private String ses_id;
	private int page;
	
	public Cache(String isbn,String ses_id,int page) {
		this.isbn=isbn;
		this.ses_id=ses_id;
		this.page=page;
	}
	
	public String getIsbn() {
		return this.isbn;
	}
	public String getSesId() {
		return this.ses_id;
	}
	public int getPage() {
		return this.page;
	}
	
	public String toString() {
		return isbn+"|"+ses_id+"|"+page;
	}
}