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

public class LibraryApiJava {

    static String API_KEY_RAKU = "1095761689829578454"; // 楽天市場のAPIキー
    static String API_KEY_Lib = "f97ee4442195b7b3e53286cc4ad93d0c"; // 図書館APIキー

    public static void main(String[] args) {
        String lineMessage = "本の名前/作者名/県/市"; // ここにLINEからのメッセージを設定

        String[] lineMessageText = lineMessage.split("/");
        String bookName = lineMessageText[0];
        String author = lineMessageText[1];
        String pref = lineMessageText[2];
        String city = lineMessageText[3];

        Application application = new Application(bookName, author, pref, city);

    //    // 1. 楽天ブックスAPIを呼び出して本の情報を取得
      //  String rakutenUrl = "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?applicationId=" + API_KEY_RAKU
       //         + "&format=json&title=" + application.getBookName() + "&author=" + application.getAuthor();
 String rakutenUrl ="";

try {
    String encodedTitle = URLEncoder.encode(application.getBookName() , "UTF-8");
    String encodedAuthor = URLEncoder.encode(author, "UTF-8");

   rakutenUrl = "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?applicationId=" + API_KEY_RAKU
                + "&format=json&title=" +  encodedTitle + "&author=" + encodedAuthor;

    HttpClient httpClient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(rakutenUrl);
    HttpResponse response = httpClient.execute(httpGet);
    String jsonResponse = EntityUtils.toString(response.getEntity());

    // レスポンスの処理...
} catch (IOException e) {
    e.printStackTrace();
}

                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(rakutenUrl);
                    JSONArray items = (JSONArray) json.get("Items");
                    int minPriceIndex = rakutenMiniPrice(items);
                
                    JSONObject item = (JSONObject) items.get(minPriceIndex);
                    String publisherName = (String) item.get("publisherName");
                    String salesDate = (String) item.get("salesDate");
                    String itemPrice = (String) item.get("itemPrice");
                    String itemUrl = (String) item.get("itemUrl");
                
                    String answer1 = "書誌情報一覧です!!!\n" + "出版日: " + publisherName + "\n出版日: " + salesDate + "\n値段: " + itemPrice
                            + "\n商品URL: " + itemUrl;
                    System.out.println(answer1);
                
                } catch (Exception  e) {
                    e.printStackTrace();
                }
        // 2. 図書館APIを呼び出して図書館情報を取得
        // （同様のHTTPリクエストの設定とデータ処理を行ってください）

        // 3. 中古本情報を取得
        String isbnNum = application.getIsbnNum();
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

}
class Application {
    private String bookName;
    private String author;
    private String pref;
    private String city;
    private String isbnNum;

    public Application(String bookName, String author, String pref, String city) {
        this.bookName = bookName;
        this.author = author;
        this.pref = pref;
        this.city = city;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public String getPref() {
        return pref;
    }

    public String getCity() {
        return city;
    }

    public String getIsbnNum() {
        return isbnNum;
    }

    public void setIsbnNum(String isbnNum) {
        this.isbnNum = isbnNum;
    }
}
