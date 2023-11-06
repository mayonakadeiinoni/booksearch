package com.example.demo;

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

public class LibraryApiJava {

    static String API_KEY_RAKU = "1095761689829578454"; // 楽天市場のAPIキー
    static String API_KEY_Lib = "f97ee4442195b7b3e53286cc4ad93d0c"; // 図書館APIキー

    public static void main(String[] args) {
        String lineMessage = "仮面の告白/三島由紀夫/県/市"; // ここにLINEからのメッセージを設定
        String[] lineMessageText = lineMessage.split("/");
        String bookName = lineMessageText[0];
        String author = lineMessageText[1];
        String pref = lineMessageText[2];
        String city = lineMessageText[3];

        Application application = new Application(bookName, author, pref, city);
/* 
    //    // 1. 楽天ブックスAPIを呼び出して本の情報を取得
      //  String rakutenUrl = "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?applicationId=" + API_KEY_RAKU
       //         + "&format=json&title=" + application.getBookName() + "&author=" + application.getAuthor();
 String rakutenUrl ="";
 String jsonResponse = "";
 String isbnNum = "";
try {
    String encodedTitle = java.net.URLEncoder.encode(application.getBookName() , "UTF-8");
    String encodedAuthor = java.net.URLEncoder.encode(author, "UTF-8");

   rakutenUrl = "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?applicationId=" + API_KEY_RAKU
                + "&format=json&title=" +  encodedTitle + "&author=" + encodedAuthor;

    HttpClient httpClient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(rakutenUrl);
    HttpResponse response = httpClient.execute(httpGet);
     jsonResponse = EntityUtils.toString(response.getEntity());
    System.out.println(jsonResponse);
    // レスポンスの処理...
} catch (IOException e) {
    e.printStackTrace();
}

                try {
                    // jsonの抜き出しは
                    // jackson  https://www.jsonschema2pojo.org/
                    // https://qiita.com/opengl-8080/items/b613b9b3bc5d796c840c
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(jsonResponse);
                    JSONArray items = (JSONArray) json.get("Items");
                  
            // "Items"内の最初のオブジェクトを取得
                   JSONObject firstItem = (JSONObject) items.get(0);
                   JSONObject firstItema = (JSONObject) firstItem.get("Item");;
            // "title"キーの値を取得
                      String title = (String) firstItema.get("title");

                     System.out.println("Title: " + title);
                    String publisherName = (String) firstItema.get("publisherName");
                    String salesDate = (String) firstItema.get("salesDate");
                    long itemPrice = (long) firstItema.get("itemPrice");
                    String itemUrl = (String) firstItema.get("largeImageUrl");
                    isbnNum = (String) firstItema.get("isbn");
                
                    String answer1 = "書誌情報一覧です!!!\n" + "出版日: " + publisherName + "\n出版日: " + salesDate + "\n値段: " + itemPrice
                            + "\n商品URL: " + itemUrl;
                    System.out.println(answer1);
                
                } catch (Exception  e) {
                    e.printStackTrace();
                }*/
        // 2. 図書館APIを呼び出して図書館情報を取得
          // 2. 図書館APIを呼び出して図書館情報を取得
        String isbnNum = "9784102114018";
         String libraryInfo = searchNearbyLibraries(pref, city);
        System.out.println(libraryInfo);

        // 2-2: 指定図書館に蔵書があるかを検索
        String systemidStr = libraryInfo != null ? extractSystemIds(libraryInfo) : "";
        
        // 文字列をカンマで分割
        String[] parts = systemidStr .split(",");
        
        // 重複を削除するためにセットを使用
        Set<String> uniqueParts = new LinkedHashSet<>(Arrays.asList(parts));
        
        // 重複を削除した文字列をカンマで連結
        systemidStr  = String.join(",", uniqueParts);
        
      
        System.out.println(systemidStr);
        String libraryAvailability = searchLibraryAvailability(isbnNum, systemidStr);
      //  System.out.println(libraryAvailability);
        // （同様のHTTPリクエストの設定とデータ処理を行ってください）

        // 3. 中古本情報を取得
       // isbnNum = application.getIsbnNum();
        String answer3 = "中古本の最安値を検索しました!! このURLを押してね!!\nhttps://bookget.net/search?q=" + isbnNum;
        System.out.println(answer3);
    }


    public static int rakutenMiniPrice(JSONArray array) {
        int miniPrice = ((JSONObject) array.get(0)).get("itemPrice") != null ? Integer.parseInt(((JSONObject) array.get(0)).get("itemPrice").toString()) : 0;
        int miniPriceNum = 0;
    
        for (int i = 0; i < array.size(); i++) {
            int itemPrice = ((JSONObject) array.get(i)).get("itemPrice") != null ? Integer.parseInt(((JSONObject) array.get(i)).get("itemPrice").toString()) : 0;
            if (itemPrice < miniPrice) {
                miniPriceNum = i;
                miniPrice = itemPrice;
            }
        }
    
        return miniPriceNum;
    }

    
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

    public static String extractSystemIds(String libraryInfo) {
        String systemidStr = "";

        try {
            JSONParser parser = new JSONParser();
            JSONArray libraries = (JSONArray) parser.parse(libraryInfo);

            for (int i = 0; i < libraries.size(); i++) {
                JSONObject library = (JSONObject) libraries.get(i);
                String systemid = (String) library.get("systemid");
                String formal = (String) library.get("formal");
                System.out.println(formal);
                systemidStr += systemid;

                if (i != libraries.size() - 1) {
                    systemidStr += ",";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return systemidStr;
    }

    public static String searchLibraryAvailability(String isbnNum, String systemidStr) {
        String libraryAvailability = "";
        String[] ids = systemidStr.split(",");
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
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JSONParser parser = new JSONParser();
           // System.out.println(jsonResponse);
           
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject books = (JSONObject) jsonObject.get("books");
            JSONObject book9784102114018 = (JSONObject) books.get("9784102114018");
          //  JSONObject a = (JSONObject) book9784102114018.getKeys();
            for(String id : ids){
                JSONObject a = (JSONObject) book9784102114018.get(id);
                JSONObject b = (JSONObject) a.get("libkey");
          //      System.out.println(a);
                System.out.println(b);
            }
        //    String [] s = book9784102114018.keySet().toString().replace("[","").trim().replace("]","").split(",");
    //        JSONArray array = (JSONArray) (books.get("9784102114018"));
       

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
    

        return libraryAvailability;
    }
}
 