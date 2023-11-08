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

public class test {
    static String API_KEY_RAKU = "1095761689829578454"; // 楽天市場のAPIキー
    static String API_KEY_Lib = "f97ee4442195b7b3e53286cc4ad93d0c"; // 図書館APIキー

    public static void main(String[] args) {
        // 入力部分はおいおい変える
        String lineMessage = "異邦人/カミュ/県/市"; // ここにLINEからのメッセージを設定
        String[] lineMessageText = lineMessage.split("/");
        String bookName = lineMessageText[0];
        String author = lineMessageText[1];
        String pref = lineMessageText[2];
        String city = lineMessageText[3];

        // 楽天api実行部
        Application application = new Application(bookName, author, pref, city);
         BooksInfos infos = new BooksInfos();
         infos = infos.RakutenBooksSearch(application);
         System.out.println(infos);

        // 図書館api 実行
        String isbnNum = "9784102114018";
        Libinfos libinfos = new Libinfos();
        libinfos.setLibkeys(libinfos.searchLibraryAvailability(isbnNum , libinfos.LibLocSearch(application.getPref() , application.getCity())));
        System.out.println(libinfos);


         
        // 3. 中古本情報を取得
       // isbnNum = application.getIsbnNum();
        String answer3 = "中古本の最安値を検索しました!! このURLを押してね!!\nhttps://bookget.net/search?q=" + infos.getBookInfoList().get(0).getIsbnNum();
        System.out.println(answer3);
        

    }
}




