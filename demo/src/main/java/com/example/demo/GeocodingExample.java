package com.example.demo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


// 住所から緯度経度を求める国土地理院apiを扱うクラス
public class GeocodingExample {
    public static String geocoding(String address) {
  
        String geocode = "";
        try {
            String encodedAddress = java.net.URLEncoder.encode(address, "UTF-8");
            String apiUrl ="https://msearch.gsi.go.jp/address-search/AddressSearch?q="+ encodedAddress;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // レスポンスをJSON形式としてパースし、緯度と経度を取得
                String jsonResponse = response.toString();
                 System.out.println(jsonResponse);
                // ここでJSONをパースして緯度と経度を取得する処理を追加
            // JSONデータをパース
            JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(jsonResponse);

            // 最初の要素を取得
            if (!jsonArray.isEmpty()) {
                JSONObject firstObject = (JSONObject) jsonArray.get(0);

                // geometryオブジェクトの中からcoordinatesを取得
                JSONObject geometry = (JSONObject) firstObject.get("geometry");
                JSONArray coordinates = (JSONArray) geometry.get("coordinates");

                // この配列から緯度と経度を取得
                if (coordinates.size() == 2) {
                    double longitude = (double) coordinates.get(0);
                    double latitude = (double) coordinates.get(1);
                    System.out.println("経度: " + longitude);
                    System.out.println("緯度: " + latitude);
                    geocode = longitude + "," + latitude;
                    System.out.println(geocode);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
              //  System.out.println("緯度: " + latitude);
             //   System.out.println("経度: " + longitude);
            } else {
                System.out.println("HTTPリクエストが失敗しました。レスポンスコード: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return geocode;
    }
}
