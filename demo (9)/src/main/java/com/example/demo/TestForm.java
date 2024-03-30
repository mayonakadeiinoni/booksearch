package com.example.demo;

import java.util.*;

public class TestForm {
	private List<Integer> check;
	public TestForm() {
		check=new ArrayList<>();
	}
	public List<Integer> getCheck() {
		return check;
	}
	public void setCheck(List<Integer> check) {
		//this.check.add(check);
		System.out.println("setting");
		this.check=check;
	}
}
