package com.example.demo;

import java.util.HashMap;
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

import java.util.HashMap;
import java.util.HashSet;

/**
 * 入力フォームで入力された内容に応じて、楽天ブックスAPIのパラメータを作成するクラス。
 * 例：作者入力、ジャンル入力ならその２つのパラメータが入ったURLを作る。
 */
public class RakutenPara {
    static String API_KEY_RAKU = "1095761689829578454"; // 楽天市場のAPIキー
    static String API_KEY_Lib = "f97ee4442195b7b3e53286cc4ad93d0c"; // 図書館APIキー
    private HashMap<String, String> parameter;

    // 必要なStringを格納するためのHashSet ここに必要なパラメータを入れる。
    private static HashSet<String> NoBanSet = new HashSet<String>() {{
        add("author");
        add("title");
        add("publisherName");
     //   add("sort");
        add("size");
        add("isbn");
        add("booksGenreId");
    }};

    // Constructor
    public RakutenPara() {
        this.parameter = new HashMap<>();
    }

    // Getter for the parameter
    public HashMap<String, String> getParameter() {
        return parameter;
    }
    // Setter for the parameter
    public void setParameter(HashMap<String, String> parameter) {
    	this.parameter=new HashMap<>();
    	for(String key:parameter.keySet()) {
    		setMap(key,parameter.get(key));
    	}
    }

    boolean flag=false;
    /**
     * パラメータを追加するメソッド。
     *
     * @param genre ジャンル
     * @param input 入力値
     */
    public void setMap(String genre, String input) {
    	if(NoBanSet.contains(parameter)) flag=true;
        this.parameter.put(genre, input);
    }



    /**
     * 指定されたパラメータに基づいて楽天APIのURLを生成します。
     *
     * @return 楽天APIのURL。
     */
    public String GenerateRakutenApi() {
        // 楽天APIのベースURL
        String rakutenUrl = "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?applicationId="
                + API_KEY_RAKU
                + "&format=json&";

        String encoding = ""; // URLエンコードされたパラメータの値を格納する変数
      //  inspectMap();
        // 各ジャンルについて繰り返し、URLにパラメータとして追加します
        for (String genre : this.parameter.keySet()) {
            try {
                // パラメータ値をURLエンコード
                encoding = java.net.URLEncoder.encode(this.parameter.get(genre), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ジャンルとそのエンコードされた値をURLに追加
            rakutenUrl = rakutenUrl + genre + "=" + encoding + "&";
        }

        // 少なくとも1つのパラメータが追加されたかどうかを確認
        if (!flag) {
            System.out.println("エラー: 少なくとも1つのパラメータを指定してください。");
        }

        // URLの末尾の "&" を削除
        rakutenUrl = rakutenUrl.substring(0, rakutenUrl.length() - 1);

        return rakutenUrl;
    }

    public static void main(String[] args) {
        RakutenPara test = new RakutenPara();

        test.setMap("author", "太陽");
        test.setMap("publisheme", "講談社");

        System.out.println(test.GenerateRakutenApi());
    }
}
