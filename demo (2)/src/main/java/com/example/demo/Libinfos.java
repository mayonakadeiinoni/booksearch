package com.example.demo;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;
import java.io.*;

public class Libinfos {
    static String API_KEY_RAKU = "1095761689829578454"; // 楽天市場のAPIキー
    static String API_KEY_Lib = "f97ee4442195b7b3e53286cc4ad93d0c"; // 図書館APIキー
    private HashMap<String, HitLib> libkeys; // key: Systemid , value: その図書館の蔵書の有無 

    // デフォルトコンストラクタ
    public Libinfos() {
        // デフォルトコンストラクタは空のままでOK
    }

    // システムIDとHitLibのHashMapを受け取るコンストラクタ
    public Libinfos(HashMap<String, HitLib> libkeys) {
        this.libkeys = libkeys;
    }

    // libkeys のsetter
    public void setLibkeys(HashMap<String, HitLib> libkeys) {
        this.libkeys = libkeys;
    }

    // libkeys のgetter
    public HashMap<String, HitLib> getLibkeys() {
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

     // 住所の緯度、経度の文字列。返り値：図書館apiの返り値json 中にその市区町村にある図書館のシステムidがある。
    public static String searchNearbyLibraries(String geocode) {
        String libraryInfo = "";

        try {
            String gecoding = java.net.URLEncoder.encode(geocode, "UTF-8");
            String encodedEmpty = java.net.URLEncoder.encode("", "UTF-8");
            String url = "https://api.calil.jp/library?appkey=" + API_KEY_Lib
                    + "&geocode=" + gecoding+"&limit=20&distance=5&format=json&callback="+encodedEmpty;

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
// 上記2つのメソッドを組み合わせて、その住所入力から、範囲内の図書館のシステムidの文字列を返す関数
    public String LibLocSearch(String geocode) {
        return extractSystemIds(searchNearbyLibraries(new GeocodingExample().geocoding(geocode)));
    }

// 入力：検索したい本のisbn番号,検索対象の図書館のsystemid
// 出力： 図書館とその図書館の蔵書をkey ,valueにしたハッシュマップを返す関数
    public HashMap<String, HitLib> searchLibraryAvailability(String isbnNum, String systemidStr) {
        String libraryAvailability = "";
        String[] ids = systemidStr.split(",");
        HashMap<String, HitLib> libs = new HashMap<>();
        HitLib lib = new HitLib();
        try {
            String encodedISBN = java.net.URLEncoder.encode(isbnNum, "UTF-8");
            String encodedEmpty = java.net.URLEncoder.encode("", "UTF-8");
            String url = "https://api.calil.jp/check?appkey=" + API_KEY_Lib
                    + "&isbn=" + encodedISBN + "&systemid=" + systemidStr + "&format=json&callback="+"no";
            long continueValue = 1;
            while(continueValue == 1){
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            Thread.sleep(1000); 
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject books = (JSONObject) jsonObject.get("books");
            JSONObject book9784102114018 = (JSONObject) books.get(isbnNum);//change from "9784102114018" to isbnNum
           
            for(String id : ids){
                 int count = 0;
                JSONObject a = (JSONObject) book9784102114018.get(id);
                lib.setReserveurl((String) a.get("reserveurl"));
                JSONObject b = (JSONObject) a.get("libkey");
                if(b != null){
                for(Object r  : new ArrayList(((JSONObject) a.get("libkey")).keySet())){
                    String libStr = (String) r;
                    String libSituation = (String) b.get(libStr);
                    if(!"{}".equals(libStr))
                         lib.getLibkey().put(libStr, libSituation);
                         count++;
                }
            
                if (count != 0)
                   libs.put(id , new HitLib(lib));
           //        System.out.println(libs);
               
            }
             lib.getLibkey().clear();
            }
  
             // "continue" の値を取得
            continueValue = (Long) jsonObject.get("continue");

             System.out.println("continue: " + continueValue);

            libraryAvailability = jsonResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        } catch (IOException e) {
            e.printStackTrace();
        }
    

        return libs;
    }

     // toString メソッドを実装
    @Override
    public String toString() {
        return "Libinfos [libkeys=" + libkeys + "]";
    }


}