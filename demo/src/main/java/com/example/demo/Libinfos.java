package com.example.demo;

import java.util.HashMap;
import java.io.IOException;
import org.apache.catalina.util.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.*;
import java.util.*;
public class Libinfos {
    static String API_KEY_RAKU = "1095761689829578454"; // 楽天市場のAPIキー
    static String API_KEY_Lib = "f97ee4442195b7b3e53286cc4ad93d0c"; // 図書館APIキー
    private String[] Systemids;
    private HashMap<String, String> libkeys; // key: Systemid , value: その図書館の蔵書の有無 

    // デフォルトコンストラクタ
    public Libinfos() {
        // デフォルトコンストラクタは空のままでOK
    }

    // コンストラクタ
    public Libinfos(String[] Systemids, HashMap<String, String> libkeys) {
        this.Systemids = Systemids;
        this.libkeys = libkeys;
    }

    // Systemids のsetter
    public void setSystemids(String[] Systemids) {
        this.Systemids = Systemids;
    }

    // Systemids のgetter
    public String[] getSystemids() {
        return Systemids;
    }

    // libkeys のsetter
    public void setLibkeys(HashMap<String, String> libkeys) {
        this.libkeys = libkeys;
    }

    // libkeys のgetter
    public HashMap<String, String> getLibkeys() {
        return libkeys;
    }


    // 県と市区町村を入れる。返り値：図書館apiの返り値json 中にその市区町村にある図書館のシステムidがある。
    public static String searchNearbyLibraries(String pref, String city) {
        String libraryInfo = "";

        try {
            String encodedPref = java.net.URLEncoder.encode(pref, "UTF-8");
            String encodedCity = java.net.URLEncoder.encode(city, "UTF-8");
            String encodedEmpty = java.net.URLEncoder.encode("", "UTF-8");
            String url = "https://api.calil.jp/library?appkey=" + API_KEY_Lib
                    + "&pref=" + encodedPref + "&city=" + encodedCity + "&limit=20&distance=50&format=json&callback="+encodedEmpty;

            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            libraryInfo = jsonResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return libraryInfo;
    }

// 図書館apiで図書館の有無検索をした結果のjsonを入れる。　返り値：　図書館のシステムidの文字列
    public static String extractSystemIds(String libraryInfo) {
        String systemidStr = "";

        try {
            JSONParser parser = new JSONParser();
            JSONArray libraries = (JSONArray) parser.parse(libraryInfo);

            for (int i = 0; i < libraries.size(); i++) {
                JSONObject library = (JSONObject) libraries.get(i);
                String systemid = (String) library.get("systemid");
             //   String formal = (String) library.get("formal");
              //  System.out.println(formal);
                systemidStr += systemid;

                if (i != libraries.size() - 1) {
                    systemidStr += ",";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 重複を消す処理を挟む
        
        // 文字列をカンマで分割
        String[] parts = systemidStr .split(",");
        
        // 重複を削除するためにセットを使用
        Set<String> uniqueParts = new LinkedHashSet<>(Arrays.asList(parts));
        
        // 重複を削除した文字列をカンマで連結
        systemidStr  = String.join(",", uniqueParts);

        return systemidStr;
    }

// 上記2つのメソッドを組み合わせて、その市区町村の入力から、範囲内の図書館のシステムidの文字列を返す関数
    public String LibLocSearch(String pref, String city) {
        return extractSystemIds(searchNearbyLibraries(pref, city));
    }


}
