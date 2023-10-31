package com.example.demo;

// 本が図書館にあるかを調べるための場所を扱うクラス。
public class LibLocation {
    private String pref;
    private String city;

    public LibLocation() {
        this.pref = "愛知県";
        this.city = "名古屋市";
    }

    public LibLocation(String pref, String city) {
        this.pref = pref;
        this.city = city;
    }

    public String getPref() {
        return pref;
    }

    public void setPref(String pref) {
        this.pref = pref;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
