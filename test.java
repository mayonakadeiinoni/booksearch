static  String  API_KEY_RAKU = '1095761689829578454'; // 楽天市場のapi 取得済み
const API_KEY_Lib = 'f97ee4442195b7b3e53286cc4ad93d0c'; // 図書館api 取得済み

const CHANEL_ACCESS_TOKEN = "29pZ8n97SVYB8dAQFn2muEEbLy+rlldS+l4bq6wjEJAAJtLL/R4JEeZnEXZ3AxoV6jMg2IWQhVb/ocl5zpBKTS6eb3hPmiQOtGkSlDXc/5eXk/dWdbg/hfizsQhYNCUKbY0KJ4vk06HT4fQeobRuxwdB04t89/1O/w1cDnyilFU=";

const LINE_PUSH_URL = "https://api.line.me/v2/bot/message/push";    //プッシュメッセージ送信
const LINE_REPLY_URL = "https://api.line.me/v2/bot/message/reply";  //応答(リプライ)メッセージ送信


class application {
  //bookName = "";  // 本の名前
  // location ; // 位置情報?
  // bookPrice;  // 本と値段の二次元配列？

  constructor(bookName, author, pref, city) {
    this.bookName = bookName; // 本の名前
    this.author = author;  // 本の作者
    this.pref = pref; // 県
    this.city = city;  // 市区
    this.isbnNum = ""; // 本のisbn番号　後で入れる。
  }

}

// 楽天booksapiで取得した商品の配列から一番安い値段の文庫本のitemを取得する関数
// 入力 Items itemが並んだ配列
// 出力 Itemsの中での最安値の番号 0から数えてね
function rakutenMiniPrice(array) {

  let miniPrice = array[0].Item.itemPrice;
  let miniPriceNum = 0;

  for (let i = 0; i < array.length; i++) {
    if (miniPrice > array[i].Item.itemPrice) {
      miniPriceNum = i;
      miniPrice = array[i].Item.itemPrice;
    }
  }

  return miniPriceNum

}

//---------------------------------------------------
// プッシュメッセージ送信
function pushMessage(userId, messages) {
  const payload = JSON.stringify({
    "to": userId,
    "messages": messages
  });

  UrlFetchApp.fetch(LINE_PUSH_URL, {
    "method": "post",
    "headers": {
      "Authorization": "Bearer " + CHANEL_ACCESS_TOKEN,
      "Content-Type": "application/json"
    },
    "payload": payload
  })

}




function doPost(e) {
  console.log("淺野友紀::::::::::::::::::");


  // リクエストからコンテンツ(ボディ)を取得。コンテンツの内容はLineから送信されたJSON文字列。
  const lineContents = e.postData.contents;

  // JSON文字列をオブジェクトにパース
  const lineJson = JSON.parse(lineContents);

  // lineからもらった入力メッセージ / ごとに必要な情報を出すことにする。　本の名前/作者名/住所県/市区
  const lineMessage = lineJson.events[0].message.text;

  const lineMessageText = lineMessage.split("/"); // 要素ごとに区切って配列にする。
  // 送信者のユーザID
  const userId = lineJson.events[0].source.userId;
  // console.log("SSV淺野友紀　ユーザーId：" + userId);







  const prot = new application(lineMessageText[0], lineMessageText[1], lineMessageText[2], lineMessageText[3]); // ほんの名前、作者,県、市


  // 1 楽天ブックスapiで本の新品情報を調べる　獲得すべき情報 isbn と本値段
  const rakutenURl = "https://app.rakuten.co.jp/services/api/BooksBook/Search/20170404?applicationId=" + API_KEY_RAKU + "&format=json&title=" + encodeURIComponent(prot.bookName) + "&author=" + encodeURIComponent(prot.author);

  const returnJson = UrlFetchApp.fetch(rakutenURl).getContentText(); //返信されたjson

  console.log(returnJson);

  console.log(rakutenMiniPrice(JSON.parse(returnJson).Items));

  // Itemsの番号がrekutenMiniPriceで取得できたのであとはほんの情報を出す。
  const answer1 = "書誌情報一覧です!!!\n" + "出版日:" +
    JSON.parse(returnJson).Items[rakutenMiniPrice(JSON.parse(returnJson).Items)].Item.publisherName +
    " \n出版日:" +
    JSON.parse(returnJson).Items[rakutenMiniPrice(JSON.parse(returnJson).Items)].Item.salesDate +
    " \n値段:" +
    JSON.parse(returnJson).Items[rakutenMiniPrice(JSON.parse(returnJson).Items)].Item.itemPrice +
    " \n商品url:" +
    JSON.parse(returnJson).Items[rakutenMiniPrice(JSON.parse(returnJson).Items)].Item.itemUrl;
  // LINE送信用のメッセージオブジェクト作成
  let messages = [{
    "type": "text",
    "text": answer1
  }];
  // console.log("SSV鈴木規之　メッセージオブジェクト：" + JSON.stringify(messages));
  pushMessage(userId, messages)

  // 次につなげられるようにisbnの情報を格納しておく。

  prot.isbnNum = JSON.parse(returnJson).Items[rakutenMiniPrice(JSON.parse(returnJson).Items)].Item.isbn;

  console.log(prot);


  // 2　図書館api 
  //2-1 入力住所から近い場所の図書館のすべてのシステムIDを取得します。
  /*
  // 2-1-1 住所を緯度経度に変換
  let returnLoc ="";
  do{
   returnLoc = (UrlFetchApp.fetch("https://msearch.gsi.go.jp/address-search/AddressSearch?q="+encodeURIComponent(prot.location)).getContentText());
 
  console.log(returnLoc=="");
  }
  while(returnLoc=="")
 
  returnLoc =JSON.parse(returnLoc);
  //console.log(returnLoc[0].geometry.coordinates);
 
  let locationArray = returnLoc[0].geometry.coordinates;
  */

  // 2-1-2 緯度から近い図書館を出す。
  let returnLoc = JSON.parse((UrlFetchApp.fetch("https://api.calil.jp/library?appkey=" + API_KEY_Lib + "&pref=" + prot.pref + "&vity=" + prot.city + "&limit=50&distance=1000&format=json&callback=no").getContentText()));
  //console.log((returnLoc));

  // systemidをつなげた文字列を作る。 これを図書館の蔵書検索に使います。
  let systemidStr = "";

  for (let i = 0; i < returnLoc.length; i++) {
    systemidStr = systemidStr.concat(returnLoc[i].systemid);
    if (i != returnLoc.length - 1)
      systemidStr += ",";

  }

  console.log(systemidStr);


  //  myFunction((returnLoc));

  //2-2 指定図書館に蔵書があるかを検索します。
  let coninue = 1;
  let libExistsArray = [];
  while (coninue == 1) {
    let session = "";
    try {
      console.log(coninue)
      returnLoc = JSON.parse((UrlFetchApp.fetch("https://api.calil.jp/check?appkey=" + API_KEY_Lib + "&isbn=" + encodeURIComponent(prot.isbnNum) + "&systemid=" + systemidStr + "&format=json&callback=no").getContentText()));

      coninue = returnLoc.continue;
      console.log("gjaojaoja" + coninue);
    } catch (e) {
      console.log(e);
    };
  }
  let a = prot.isbnNum.toString();
  //console.log(a);
  console.log(JSON.stringify(returnLoc));
  //console.log(Object.keys(returnJson.books[prot.isbnNum]));
  // myFunction((returnLoc));
  // すべてのlibkeyを重複なしで結合しよう。
  let systemidArray = systemidStr.split(",");
  systemidArray = deleteElement(systemidArray);
  console.log("変更後" + systemidArray);

  for (let i = 0; i < systemidArray.length; i++) {

    console.log(i);
    console.log(systemidArray[i]);
    console.log(returnLoc.books[a][systemidArray[i]]);
    let libkeyArray = JSON.stringify(returnLoc.books[a][systemidArray[i]].libkey);
    console.log((libkeyArray) == ["{}"]);
    if (!(libkeyArray == ["{}"])) {

      if (!(returnLoc.books[a][systemidArray[i]].libkey == null || returnLoc.books[a][systemidArray[i]].libkey == undefined)) {
        libExistsArray = libExistsArray.concat([systemidArray[i] + "辺り："]);
        libExistsArray = libExistsArray.concat(returnLoc.books[a][systemidArray[i]].libkey);
      }
      // console.log((libExistsArray[0]));



    }


  }




  libExistsArray = deleteElement(libExistsArray);



  console.log("jgiaojaos" + libExistsArray);

  let answer2 = "";

  for (let i = 0; i < libExistsArray.length; i++) {
    answer2 = answer2.concat(JSON.stringify(libExistsArray[i]));
  }
  // LINE送信用のメッセージオブジェクト作成
  messages = [{
    "type": "text",
    "text": answer2.replace("{", "").replace("}", "").replace("{", "").replace("}", "").replace(",", "\n") == "[]" ? "図書館では今のところ借りられなさそうです。" : "借りられる図書館一覧:\n" + answer2.replace("{", "").replace("}", "").replace("{", "").replace("}", "").replace(",", "\n")
  }];
  // console.log("SSV鈴木規之　メッセージオブジェクト：" + JSON.stringify(messages));
  pushMessage(userId, messages)




  // 3 中古本検索機能
  const answer3 = "中古本の最安値を検索しました!! このurlを押してね!!\n" + "https://bookget.net/search?q=" + encodeURIComponent(prot.isbnNum);

  console.log(answer3);

  // LINE送信用のメッセージオブジェクト作成
  messages = [{
    "type": "text",
    "text": answer3
  }];
  // console.log("SSV鈴木規之　メッセージオブジェクト：" + JSON.stringify(messages));
  pushMessage(userId, messages)


  libExistsArray = [];
  systemidStr = [];

}




