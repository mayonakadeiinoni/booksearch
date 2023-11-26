package com.example.demo;

import java.util.HashMap;

public class HitLib implements Cloneable {
    private String reserveurl = "";
    private HashMap<String, String> libkey = new HashMap<>(); // key 図書館名 value 貸出状況

    // デフォルトコンストラクタ
    public HitLib() {
    }

    // コンストラクタ
    public HitLib(String reserveurl, HashMap<String, String> libkey) {
        this.reserveurl = reserveurl;
        this.libkey = libkey;
    }

    // コピーコンストラクタ
    public HitLib(HitLib other) {
        this.reserveurl = other.reserveurl;
        this.libkey = new HashMap<>(other.libkey);
    }

    // クローンメソッドを実装
    @Override
    public HitLib clone() throws CloneNotSupportedException {
        return (HitLib) super.clone();
    }

    // reserveurl のsetter
    public void setReserveurl(String reserveurl) {
        this.reserveurl = reserveurl;
    }

    // reserveurl のgetter
    public String getReserveurl() {
        return reserveurl;
    }

    // libkey のsetter
    public void setLibkey(HashMap<String, String> libkey) {
        this.libkey = libkey;
    }

    // libkey のgetter
    public HashMap<String, String> getLibkey() {
        return libkey;
    }

    // toString メソッドを実装
    @Override
    public String toString() {
        return "HitLib [reserveurl=" + reserveurl + ", libkey=" + libkey + "]";
    }
}