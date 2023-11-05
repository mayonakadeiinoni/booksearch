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
import java.util.ArrayList;
import java.util.List;

// 一つの本の情報　これを複数扱うクラス
public class BooksInfos {
    static String API_KEY_RAKU = "1095761689829578454"; // 楽天市場のAPIキー
    private List<BookInfo> bookInfoList;

    public BooksInfos() {
        bookInfoList = new ArrayList<>();
    }

    public void addBookInfo(BookInfo bookInfo) {
        bookInfoList.add(bookInfo);
    }

    public List<BookInfo> getBookInfoList() {
        return bookInfoList;
    }

    public BooksInfos RakutenBooksSearch(Application apl){
        String rakutenUrl ="";
        String jsonResponse = "";

          // 楽天api実行部
        BooksInfos infos = new BooksInfos();

       try {
           String encodedTitle = java.net.URLEncoder.encode(apl.getBookName() , "UTF-8");
           String encodedAuthor = java.net.URLEncoder.encode(apl.getAuthor(), "UTF-8");
       // urlの部分はor検索などを導入したいところ
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

        for(int i = 0 ;i<items.size() ;i++){
        
// "Items"内の最初のオブジェクトを取得
       JSONObject firstItem = (JSONObject) items.get(i);
       JSONObject firstItema = (JSONObject) firstItem.get("Item");
// "title"キーの値を取得
        String bookName = (String) firstItema.get("title");
        String author = (String) firstItema.get("author");
        String isbnNum =  (String) firstItema.get("isbn");;
        String itemCaption = (String) firstItema.get("itemCaption");
        long itemPrice = (long) firstItema.get("itemPrice");
        String publisherName = (String) firstItema.get("publisherName");
        String salesDate = (String) firstItema.get("salesDate");
        String seriesName = (String) firstItema.get("seriesName");
        String itemUrl =  (String) firstItema.get("itemUrl");
    
        BookInfo book = new BookInfo(bookName, author, isbnNum, itemCaption, itemPrice, publisherName, salesDate, seriesName,itemUrl) ;
        infos.bookInfoList.add(book);
            
    }
    } catch (Exception  e) {
        e.printStackTrace();
    }
    


    return infos;
    }

    // 他に必要なメソッドを追加できます
 // toString()メソッドをオーバーライド
 @Override
 public String toString() {
     StringBuilder result = new StringBuilder();
     int count = 1;
     result.append("BooksInfos [\n");
     for (BookInfo bookInfo : bookInfoList) {
         result.append(count++ +"ヒット目:");
         result.append("  " + bookInfo.toString() + "\n");
     }
     result.append("]");

     return result.toString();
 }
}
