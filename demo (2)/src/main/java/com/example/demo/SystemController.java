package com.example.demo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.Data;

@Controller
public class SystemController {
	@RequestMapping(value="/test")
	private String test(TestForm checks) {
		System.out.println(checks.getCheck());
		return "/test";
	}
	
	@RequestMapping(value="/lib_search_test")
	private String controllLibSearch(String post_info,Integer form_num,Long search_id,
			Integer order,Integer page,Model model) {
		Info info=new Info();
		if(form_num==null) {
			info.setId(-1L);
			info.setDefault();
			model.addAttribute("info", info);
			return "/lib_search_test";
		}
		switch(form_num) {
		case 1:
			info=search(post_info);
			break;
		case 2:
			info=order(search_id,order);
			break;
		case 3:
			info=paging(search_id,page);
		default:
			//error();
		}
		model.addAttribute("info", info);
		/*temp*/model.addAttribute("pagelist", Arrays.asList(1,2,3));
		//System.out.println(info.getBooks());
		return "/lib_search_test";
	}
	
	@RequestMapping(value="/tosyo_search_test")
	private String controllTosyoSearch(String pref,String city,String isbnNum,Integer form_num,
			Model model) {
		if(form_num==null) {
			return "error.html";
		}
		model.addAttribute("form_num", form_num);
		model.addAttribute("prefs", prefList);
		switch(form_num) {
		case 1:
			tosyoInit(isbnNum,model);
			break;
		case 2:
			tosyoSearch(pref,city,isbnNum,model);
		}
		return "/tosyo_search_test";
	}
	
	
	private static HashMap<Long,Info> information=new HashMap<>(); 
	/**temp*/
	/*private String getBooksJson(String bookName,String author) {
		return "{\"GenreInformation\":[],\"Items\":[{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢 賢治/藤城 清治\",\"authorKana\":\"ミヤザワ ケンジ/フジシロ セイジ\",\"availability\":\"1\",\"booksGenreId\":\"001003003001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784065283936\",\"itemCaption\":\"４０年前の画像をデジタル技術で美しく、ページ増で影絵をゆったり堪能できる。物語の世界が伝わりやすい横型の判型。いつまでも読み継ぎたい、珠玉の一冊！\",\"itemPrice\":2530,\"itemUrl\":\"https://books.rakuten.co.jp/rb/17211241/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3936/9784065283936_1_2.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3936/9784065283936_1_2.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"講談社\",\"reviewAverage\":\"4.33\",\"reviewCount\":3,\"salesDate\":\"2022年08月18日頃\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"絵本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3936/9784065283936_1_2.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"新装版　銀河鉄道の夜\",\"titleKana\":\"シンソウバン ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢 賢治/日下 明/小埜 裕二\",\"authorKana\":\"ミヤザワ ケンジ/クサカ アキラ/オノ ユウジ\",\"availability\":\"1\",\"booksGenreId\":\"001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784418228478\",\"itemCaption\":\"生と死を見つめ、“ほんとうの幸”を問いつづけた宮沢賢治。不朽の名作「銀河鉄道の夜」をはじめ、人間の真理を紡いだ多彩な童話を美しく幻想的な絵で贈ります。\",\"itemPrice\":1430,\"itemUrl\":\"https://books.rakuten.co.jp/rb/17335197/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/8478/9784418228478_1_4.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/8478/9784418228478_1_4.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"株式会社 世界文化社\",\"reviewAverage\":\"4.5\",\"reviewCount\":2,\"salesDate\":\"2022年12月26日頃\",\"seriesName\":\"100年読み継がれる名作\",\"seriesNameKana\":\"\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/8478/9784418228478_1_4.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"宮沢賢治童話集　猫の事務所・銀河鉄道の夜など\",\"titleKana\":\"ミヤザワケンジドウワシュウネコノジムショギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治/芝田勝茂/戸部淑/加藤康子\",\"authorKana\":\"ミヤザワケンジ/シバタカツモ/トベスナホ/カトウヤスコ\",\"availability\":\"1\",\"booksGenreId\":\"001003002006/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784052046070\",\"itemCaption\":\"町で、銀河のお祭りがある日のできごと。少年ジョバンニと、親友カムパネルラは、ふしぎな鉄道に乗って、旅に出ます。そこで出会ったのは、たくさんのきれいな風景と個性ゆたかな人たちでしたー。人や自然へのやさしさがつまった、一度は読んでおきたい名作。カラーイラストいっぱい！お話図解「物語ナビ」つき！\",\"itemPrice\":1034,\"itemUrl\":\"https://books.rakuten.co.jp/rb/14753738/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6070/9784052046070.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6070/9784052046070.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"学研プラス\",\"reviewAverage\":\"4.5\",\"reviewCount\":14,\"salesDate\":\"2017年04月25日頃\",\"seriesName\":\"10歳までに読みたい日本名作　1\",\"seriesNameKana\":\"10サイマデニヨミタイニホンメイサク　1\",\"size\":\"全集・双書\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6070/9784052046070.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治\",\"authorKana\":\"ミヤザワ,ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001019001/001003007/001004008007\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784101092058\",\"itemCaption\":\"貧しく孤独な少年ジョバンニが、親友カムパネルラと銀河鉄道に乗って美しく悲しい夜空の旅をする、永遠の未完成の傑作である表題作や、「よだかの星」「オツベルと象」「セロ弾きのゴーシュ」など、イーハトーヴォの切なく多彩な世界に、「北守将軍と三人兄弟の医者」「饑餓陣営」「ビジテリアン大祭」を加えた１４編を収録。賢治童話の豊饒な味わいをあますところなく披露する。\",\"itemPrice\":473,\"itemUrl\":\"https://books.rakuten.co.jp/rb/390515/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2058/9784101092058.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2058/9784101092058.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"新潮社\",\"reviewAverage\":\"4.21\",\"reviewCount\":552,\"salesDate\":\"2012年04月\",\"seriesName\":\"新潮文庫\",\"seriesNameKana\":\"シンチョウ ブンコ\",\"size\":\"文庫\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2058/9784101092058.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"新編銀河鉄道の夜改版\",\"titleKana\":\"シンペン ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治/清川あさみ\",\"authorKana\":\"ミヤザワ,ケンジ/キヨカワ,アサミ\",\"availability\":\"1\",\"booksGenreId\":\"001003003001/001009009006/001004008007/001004008002\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784898152782\",\"itemCaption\":\"布、糸、ビーズやクリスタルで織りなす宇宙ー空前絶後の名作『銀河鉄道の夜』決定版。\",\"itemPrice\":1980,\"itemUrl\":\"https://books.rakuten.co.jp/rb/6251728/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2782/9784898152782.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2782/9784898152782.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"リトル・モア\",\"reviewAverage\":\"4.35\",\"reviewCount\":78,\"salesDate\":\"2009年12月\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2782/9784898152782.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治/ますむらひろし\",\"authorKana\":\"ミヤザワ,ケンジ/マスムラ,ヒロシ\",\"availability\":\"1\",\"booksGenreId\":\"001001012\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784904732830\",\"itemCaption\":\"\",\"itemPrice\":1870,\"itemUrl\":\"https://books.rakuten.co.jp/rb/16741802/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2830/9784904732830.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2830/9784904732830.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"風呂猫（台東区）\",\"reviewAverage\":\"3.0\",\"reviewCount\":2,\"salesDate\":\"2021年05月\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2830/9784904732830.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜　四次稿編（2）\",\"titleKana\":\"ギンガ テツドウ ノ ヨル ヨジコウヘン\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治/ますむらひろし\",\"authorKana\":\"ミヤザワ,ケンジ/マスムラ,ヒロシ\",\"availability\":\"1\",\"booksGenreId\":\"001001012\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784904732816\",\"itemCaption\":\"「銀河鉄道の夜」、「銀河鉄道の夜　初期形ブルカニロ博士篇」に次ぐ、三度目の漫画化。　２０２０年１月から「赤旗日曜版」連載開始「銀河鉄道の夜・四次稿編」の第１巻。\",\"itemPrice\":1870,\"itemUrl\":\"https://books.rakuten.co.jp/rb/16516605/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2816/9784904732816.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2816/9784904732816.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"風呂猫（台東区）\",\"reviewAverage\":\"5.0\",\"reviewCount\":5,\"salesDate\":\"2020年10月\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2816/9784904732816.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜　四次稿編（1）\",\"titleKana\":\"ギンガ テツドウ ノ ヨル ヨジコウヘン\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治/田原田鶴子\",\"authorKana\":\"ミヤザワ,ケンジ/タハラ,タズコ\",\"availability\":\"1\",\"booksGenreId\":\"001003003001/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784039720306\",\"itemCaption\":\"\",\"itemPrice\":1980,\"itemUrl\":\"https://books.rakuten.co.jp/rb/1202225/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0397/03972030.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0397/03972030.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"偕成社\",\"reviewAverage\":\"4.29\",\"reviewCount\":16,\"salesDate\":\"2000年11月\",\"seriesName\":\"宮沢賢治童話傑作選\",\"seriesNameKana\":\"ミヤザワ ケンジ ドウワ ケッサクセン\",\"size\":\"絵本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0397/03972030.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢 賢治/藤城 清治\",\"authorKana\":\"ミヤザワ ケンジ/フジシロ セイジ\",\"availability\":\"1\",\"booksGenreId\":\"001003006/001003003001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784062003957\",\"itemCaption\":\"\",\"itemPrice\":2090,\"itemUrl\":\"https://books.rakuten.co.jp/rb/22969/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3957/9784062003957.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3957/9784062003957.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"講談社\",\"reviewAverage\":\"4.6\",\"reviewCount\":76,\"salesDate\":\"1982年12月\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"絵本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3957/9784062003957.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治\",\"authorKana\":\"ミヤザワ,ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001019001/001003007/001004008007\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784758435482\",\"itemCaption\":\"\",\"itemPrice\":293,\"itemUrl\":\"https://books.rakuten.co.jp/rb/11152262/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/5482/9784758435482.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/5482/9784758435482.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"角川春樹事務所\",\"reviewAverage\":\"4.05\",\"reviewCount\":56,\"salesDate\":\"2011年04月\",\"seriesName\":\"ハルキ文庫\",\"seriesNameKana\":\"ハルキ ブンコ\",\"size\":\"文庫\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/5482/9784758435482.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治/太田大八\",\"authorKana\":\"ミヤザワ ケンジ/オオタ ダイハチ\",\"availability\":\"1\",\"booksGenreId\":\"001020004/001003002002/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784062850513\",\"itemCaption\":\"ジョバンニとカムパネルラの二人の少年は、銀河鉄道にのって四次元へのふしぎな旅に出ます。美しい音楽を聞きながら、まるで銀河系宇宙のかなたを旅しているような気持ちになる『銀河鉄道の夜』ほか五編と、代表的な詩『雨ニモマケズ』の全文を収録。小学中級から。\",\"itemPrice\":748,\"itemUrl\":\"https://books.rakuten.co.jp/rb/5954545/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0513/9784062850513.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0513/9784062850513.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"講談社\",\"reviewAverage\":\"4.54\",\"reviewCount\":13,\"salesDate\":\"2009年01月16日頃\",\"seriesName\":\"講談社青い鳥文庫\",\"seriesNameKana\":\"コウダンシャアオイトリブンコ\",\"size\":\"新書\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0513/9784062850513.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜新装版\",\"titleKana\":\"ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"ますむらひろし/宮沢賢治\",\"authorKana\":\"マスムラ,ヒロシ/ミヤザワ,ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001001012\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784030148307\",\"itemCaption\":\"\",\"itemPrice\":2200,\"itemUrl\":\"https://books.rakuten.co.jp/rb/1358372/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/8307/9784030148307.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/8307/9784030148307.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"偕成社\",\"reviewAverage\":\"4.84\",\"reviewCount\":20,\"salesDate\":\"2001年07月13日頃\",\"seriesName\":\"ますむら版宮沢賢治童話集\",\"seriesNameKana\":\"マスムラバン ミヤザワ ケンジ ドウワシュウ\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/8307/9784030148307.jpg?_ex=64x64\",\"subTitle\":\"最終形・初期形「ブルカニロ博士篇」\",\"subTitleKana\":\"サイシュウケイ ショキケイ ブルカニロ ハカセヘン\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢　賢治\",\"authorKana\":\"ミヤザワ　ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001019001/001003007/001004008007\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784041040034\",\"itemCaption\":\"ー永久の未完成これ完成であるー。自らの言葉を体現するかのように、賢治の死の直前まで変化発展しつづけた、最大にして最高の傑作「銀河鉄道の夜」。そして、いのちを持つものすべての胸に響く名作「よだかの星」のほか、「ひかりの素足」「双子の星」「貝の火」などの代表作を収める。\",\"itemPrice\":484,\"itemUrl\":\"https://books.rakuten.co.jp/rb/805088/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0034/9784041040034_1_2.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0034/9784041040034_1_2.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"KADOKAWA\",\"reviewAverage\":\"3.89\",\"reviewCount\":154,\"salesDate\":\"1969年07月19日頃\",\"seriesName\":\"角川文庫\",\"seriesNameKana\":\"カドカワブンコ\",\"size\":\"文庫\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0034/9784041040034_1_2.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢 賢治/北沢 夕芸\",\"authorKana\":\"ミヤザワケンジ/キタザワユウキ\",\"availability\":\"1\",\"booksGenreId\":\"001020004/001003002008/001003002006/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784083210457\",\"itemCaption\":\"賢治の作品の中でもっとも有名で、ユーモアにあふれた傑作『注文の多い料理店』、銀河鉄道に乗って宇宙を旅するファンタジー『銀河鉄道の夜』、大風の日に現れた転校生の物語『風の又三郎』、そのほか、いろいろな動物たちによる不思議な物語『どんぐりとやまねこ』『よだかの星』『やまなし』『セロ弾きのゴーシュ』や、『雨ニモマケズ』の８編を収録。\",\"itemPrice\":770,\"itemUrl\":\"https://books.rakuten.co.jp/rb/11318419/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0457/9784083210457.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0457/9784083210457.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"集英社\",\"reviewAverage\":\"4.43\",\"reviewCount\":18,\"salesDate\":\"2011年09月05日頃\",\"seriesName\":\"集英社みらい文庫\",\"seriesNameKana\":\"\",\"size\":\"新書\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0457/9784083210457.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"注文の多い料理店 銀河鉄道の夜\",\"titleKana\":\"チュウモンノオオイリョウリテン ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"金井　一郎/宮沢　賢治\",\"authorKana\":\"カナイ　イチロウ/ミヤザワ　ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001003003001/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784895881296\",\"itemCaption\":\"ーするとどこかで、ふしぎな声が、銀河ステーション、銀河ステーションと云う声がしたと思うと、眼の前がさあっと明るくなり、気がついてみると、ジョバンニは、親友のカムパネルラといっしょに、ごとごとごとごとと小さな列車にのって走っていたのだー。銀河を走るその鉄道は、人々の透明な意思と願いとをのせて、生と死のはざまを、かけぬけていく…。「翳り絵」で描かれた『銀河鉄道の夜』。\",\"itemPrice\":2530,\"itemUrl\":\"https://books.rakuten.co.jp/rb/12506356/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/1296/9784895881296.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/1296/9784895881296.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"三起商行\",\"reviewAverage\":\"4.0\",\"reviewCount\":6,\"salesDate\":\"2013年10月\",\"seriesName\":\"ミキハウスの宮沢賢治絵本シリーズ\",\"seriesNameKana\":\"ミキハウスノミヤザワケンジエホンシリーズ\",\"size\":\"絵本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/1296/9784895881296.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"ますむらひろし/宮沢賢治\",\"authorKana\":\"マスムラヒロシ/ミヤザワケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001019011/001001006010\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784594095048\",\"itemCaption\":\"ジョバンニとカムパネルラ。２人の少年が育む深い友情を通して、正しく生きることの難しさと尊さを描き、日本の文学史上にさん然と輝く宮沢賢治の名作『銀河鉄道の夜』。その賢治の心の中にあり、ビジュアル化は不可能と言われ続けた幻想第四次・銀河空間をますむらひろしが完全劇画化。また、『銀河鉄道の夜』の初期形「ブルカニロ博士篇」も合わせて収録。２つの作品を読み比べることで、賢治の世界がより近くなる。\",\"itemPrice\":1100,\"itemUrl\":\"https://books.rakuten.co.jp/rb/17501176/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/5048/9784594095048_1_3.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/5048/9784594095048_1_3.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"扶桑社\",\"reviewAverage\":\"5.0\",\"reviewCount\":1,\"salesDate\":\"2023年06月02日\",\"seriesName\":\"扶桑社文庫\",\"seriesNameKana\":\"\",\"size\":\"文庫\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/5048/9784594095048_1_3.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜ーーますむらひろし賢治シリーズ1\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治\",\"authorKana\":\"ミヤザワ　ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001020004/001003002001/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784001140125\",\"itemCaption\":\"宮沢賢治の童話集。夜の軽便鉄道に乗って天空を旅する少年ジョバンニの心の動きを描いた表題作のほか、「やまなし」「貝の火」「なめとこ山のくま」「オッペルとぞう」「カイロ団長」「雁の童子」の７編。幻想性に富んだ作品を収めます。小学５・６年以上。\",\"itemPrice\":847,\"itemUrl\":\"https://books.rakuten.co.jp/rb/1301995/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0125/9784001140125.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0125/9784001140125.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"岩波書店\",\"reviewAverage\":\"3.92\",\"reviewCount\":19,\"salesDate\":\"2000年12月18日頃\",\"seriesName\":\"岩波少年文庫\",\"seriesNameKana\":\"イワナミショウネンブンコ012\",\"size\":\"全集・双書\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0125/9784001140125.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢　賢治/谷川　徹三\",\"authorKana\":\"ミヤザワ　ケンジ/タニカワ　テツゾウ\",\"availability\":\"1\",\"booksGenreId\":\"001019001/001003007/001004008007/001004008004\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784003107638\",\"itemCaption\":\"宮沢賢治（１８９６-１９３３）の童話はその詩とともにきわめて特異なものである。「あなたのすきとおったほんとうのたべもの」になることを念じて書かれた心象的なこの童話の一つ一つは、故郷の土と、世界に対する絶えざる新鮮な驚きのなかから生まれたものである。どの１篇もそれぞれに不思議な魅力をたたえた傑作ぞろい。\",\"itemPrice\":1111,\"itemUrl\":\"https://books.rakuten.co.jp/rb/137020/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0031/00310763.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0031/00310763.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"岩波書店\",\"reviewAverage\":\"4.16\",\"reviewCount\":40,\"salesDate\":\"1966年07月16日頃\",\"seriesName\":\"岩波文庫　緑76-3\",\"seriesNameKana\":\"イワナミブンコ\",\"size\":\"文庫\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0031/00310763.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜　他十四篇（童話集）\",\"titleKana\":\"ギンガテツドウノヨルホカジュウヨンヘン\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治/KAGAYA\",\"authorKana\":\"ミヤザワケンジ/カガヤ\",\"availability\":\"1\",\"booksGenreId\":\"001009009011/001009009006\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784309276441\",\"itemCaption\":\"きらめく銀河をパノラマ体験ーＫＡＧＡＹＡが贈る、天上の画本。観音開き入り・特別仕様。プラネタリウムで上映され１００万人が感動した、あの「銀河鉄道の夜」の画集版！\",\"itemPrice\":3080,\"itemUrl\":\"https://books.rakuten.co.jp/rb/13341194/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6441/9784309276441.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6441/9784309276441.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"河出書房新社\",\"reviewAverage\":\"4.33\",\"reviewCount\":5,\"salesDate\":\"2015年08月26日頃\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6441/9784309276441.jpg?_ex=64x64\",\"subTitle\":\"画集\",\"subTitleKana\":\"ガシュウ\",\"title\":\"画集　銀河鉄道の夜\",\"titleKana\":\"ガシュウギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"Teamバンミカス/宮沢賢治\",\"authorKana\":\"チームバンミカス/ミヤザワケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001019007/001019001/001004008007/001004008004\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784054069275\",\"itemCaption\":\"あらゆる人の一番の幸福を探すんだー！貧しく孤独な日々を送る少年ジョバンニは、祭りの夜に一枚の切符を手にし、不思議な列車に乗る。それは宇宙を旅する銀河鉄道だった。気づくとそばには親友・カンパネルラがおり、一緒に不思議な世界をめぐる。奇妙な乗客たちを乗せた汽車はどこへ向かうのか。「ほんとうの幸せ」とはー。宮沢賢治の不朽の名作童話を詩情豊かに漫画化。\",\"itemPrice\":792,\"itemUrl\":\"https://books.rakuten.co.jp/rb/17513214/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/9275/9784054069275_1_3.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/9275/9784054069275_1_3.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"Gakken\",\"reviewAverage\":\"0.0\",\"reviewCount\":0,\"salesDate\":\"2023年07月13日\",\"seriesName\":\"まんがで読破\",\"seriesNameKana\":\"マンガデドクハ\",\"size\":\"文庫\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/9275/9784054069275_1_3.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治\",\"authorKana\":\"ミヤザワ,ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001019001/001003007/001004008007\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784087520033\",\"itemCaption\":\"青や橙色に輝く星の野原を越え、白く光る銀河の岸をわたり、ジョバンニとカムパネルラを乗せた幻の列車は走る。不思議なかなしみの影をたたえた乗客たちは何者なのか？列車はどこへ向かおうとするのか？孤独な魂の旅を抒情豊かにつづる表題作ほか、「風の又三郎」「よだかの星」など、著者の代表的作品を六編収録する。\",\"itemPrice\":429,\"itemUrl\":\"https://books.rakuten.co.jp/rb/445073/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0033/9784087520033.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0033/9784087520033.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"集英社\",\"reviewAverage\":\"4.25\",\"reviewCount\":96,\"salesDate\":\"1990年12月\",\"seriesName\":\"集英社文庫\",\"seriesNameKana\":\"シュウエイシャ ブンコ\",\"size\":\"文庫\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/0033/9784087520033.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治\",\"authorKana\":\"ミヤザワ,ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001004008007\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784779636738\",\"itemCaption\":\"父親が帰ってこないことでいじめられていた少年・ジョバンニ。星祭りの夜に、親友のカムパネルラと銀河鉄道に乗って星座から星座へ旅をすることで本当の幸い、生きる意味を悟る。\",\"itemPrice\":693,\"itemUrl\":\"https://books.rakuten.co.jp/rb/15527509/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6738/9784779636738.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6738/9784779636738.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"三栄\",\"reviewAverage\":\"4.33\",\"reviewCount\":4,\"salesDate\":\"2018年08月\",\"seriesName\":\"マンガでBUNGAKU\",\"seriesNameKana\":\"マンガ デ ブンガク\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6738/9784779636738.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガ テツドウ ノ ヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢　賢治/ヤスダ　スズヒト\",\"authorKana\":\"ミヤザワ　ケンジ/ヤスダ　スズヒト\",\"availability\":\"4\",\"booksGenreId\":\"001020004/001003002007/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784046312150\",\"itemCaption\":\"祭りの夜、ジョバンニは、草むらにねころんで、星空をながめていた。すると、ふしぎな声と明るい光につつまれたと思うと、幼なじみのカムパネルラと、銀河鉄道に乗っていた…。二人は、美しい宇宙の旅へ。宮沢賢治の最高傑作「銀河鉄道の夜」のほか、「雨ニモマケズ」「グスコーブドリの伝記」「ふたごの星」「よだかの星」が入った日本を代表する名作。小学中級から。\",\"itemPrice\":748,\"itemUrl\":\"https://books.rakuten.co.jp/rb/11648849/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2150/9784046312150.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2150/9784046312150.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"KADOKAWA\",\"reviewAverage\":\"3.2\",\"reviewCount\":6,\"salesDate\":\"2012年06月13日頃\",\"seriesName\":\"角川つばさ文庫\",\"seriesNameKana\":\"カドカワツバサブンコ\",\"size\":\"新書\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/2150/9784046312150.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"宮沢賢治童話集　銀河鉄道の夜\",\"titleKana\":\"ミヤザワケンジドウワシュウ　ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢賢治\",\"authorKana\":\"ミヤザワ ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001020004/001003002006/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784575239799\",\"itemCaption\":\"お祭りの日の夜、いじめられっ子のジョバンニがひとり丘の上で星空を眺めていると、いつの間にか、宇宙を旅する銀河鉄道に乗っていた。向かいの座席には親友のカムパネルラもいて、２人は一緒に満天の星空を巡っていく。２人の旅は、このままどこまでも続くように思われたが…悲しくも美しい宮沢賢治の代表傑作！表題作以外に「双子の星」「よだかの星」「土神ときつね」「セロ弾きのゴーシュ」「雨ニモマケズ」を収録。小学上級から。\",\"itemPrice\":693,\"itemUrl\":\"https://books.rakuten.co.jp/rb/14303330/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/9799/9784575239799.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/9799/9784575239799.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"双葉社\",\"reviewAverage\":\"4.5\",\"reviewCount\":2,\"salesDate\":\"2016年07月頃\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"新書\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/9799/9784575239799.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢 賢治/天沢 退二郎/入沢 康夫/栗原 敦/杉浦 静\",\"authorKana\":\"ミヤザワ ケンジ/アマザワ タイジロウ/イリサワ ヤスオ/クリハラ アツシ/スギウラ シズカ\",\"availability\":\"1\",\"booksGenreId\":\"001008022007/001004015/001004008007/001004008001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784480706218\",\"itemCaption\":\"童話・詩作品を中心に賢治の作品世界を、より深く、より広く味わえるコレクション全１０巻。第１巻は童話の代表的作品七篇に加え、「銀河鉄道の夜　初期形第三次稿」「農民芸術概論」を収録。宮沢賢治生誕１２０年記念出版。\",\"itemPrice\":2750,\"itemUrl\":\"https://books.rakuten.co.jp/rb/14522156/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6218/9784480706218_1_10.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6218/9784480706218_1_10.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"筑摩書房\",\"reviewAverage\":\"3.5\",\"reviewCount\":3,\"salesDate\":\"2016年12月21日頃\",\"seriesName\":\"シリーズ・全集\",\"seriesNameKana\":\"シリーズ・ゼンシュウ\",\"size\":\"全集・双書\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6218/9784480706218_1_10.jpg?_ex=64x64\",\"subTitle\":\"銀河鉄道の夜ー童話1・少年小説ほか\",\"subTitleKana\":\"ギンガテツドウノヨルドウワイチショウネンショウセツホカ\",\"title\":\"宮沢賢治コレクション1　銀河鉄道の夜\",\"titleKana\":\"ミヤザワケンジコレクションイチギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"Teamバンミカス/宮沢賢治/長尾 誠夫\",\"authorKana\":\"チームバンミカス/ミヤザワケンジ/ナガオ セイオ\",\"availability\":\"1\",\"booksGenreId\":\"001003006/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784052057779\",\"itemCaption\":\"親友カンパネルラとの旅の行方は。そして「ほんとうの幸せ」とは。宮沢賢治の不朽の名作童話を詩情豊かに漫画化。時代背景や作者について分かりやすく解説するカラーページつきの、ひと回り大きなサイズのジュニア版です。誰もが知る名作の世界への第一歩に。\",\"itemPrice\":1100,\"itemUrl\":\"https://books.rakuten.co.jp/rb/17545692/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/7779/9784052057779_1_3.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/7779/9784052057779_1_3.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"Gakken\",\"reviewAverage\":\"0.0\",\"reviewCount\":0,\"salesDate\":\"2023年08月31日頃\",\"seriesName\":\"まんがで読破　ジュニア　2\",\"seriesNameKana\":\"マンガデドクハ　ジュニア　2\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/7779/9784052057779_1_3.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮沢 賢治/徳田 秀雄\",\"authorKana\":\"ミヤザワ ケンジ/トクダ ヒデオ\",\"availability\":\"1\",\"booksGenreId\":\"001003007\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784062826587\",\"itemCaption\":\"ジョバンニとカムパネルラを乗せた汽車ははるか銀河の彼方へー。二人の旅は、豊かな詩情をたたえた一編の物語に結実した。美しき理想にささえられた、宮沢賢治の幻想の世界。表題作をはじめ、時代を超えて、今も私たちの心のなかに生きつづける九編を収録。ふりがなと行間注で、最後までスラスラ。児童向け文学全集の決定版。\",\"itemPrice\":1540,\"itemUrl\":\"https://books.rakuten.co.jp/rb/5990006/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6587/9784062826587.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6587/9784062826587.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"講談社\",\"reviewAverage\":\"0.0\",\"reviewCount\":0,\"salesDate\":\"2009年02月\",\"seriesName\":\"21世紀版・少年少女日本文学館\",\"seriesNameKana\":\"ニジュウイッセイキバンショウネンショウジョニホンブンガクカン21\",\"size\":\"全集・双書\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6587/9784062826587.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜\",\"titleKana\":\"ギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮澤賢治/門馬洋子\",\"authorKana\":\"ミヤザワケンジ/モンマヨウコ\",\"availability\":\"2\",\"booksGenreId\":\"001015\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"2300000057034\",\"itemCaption\":\"\",\"itemPrice\":1430,\"itemUrl\":\"https://books.rakuten.co.jp/rb/16320056/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/7034/2300000057034.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/7034/2300000057034.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"on the wind\",\"reviewAverage\":\"0.0\",\"reviewCount\":0,\"salesDate\":\"2020年05月01日頃\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/7034/2300000057034.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"【POD】宮澤賢治オリジナル挿絵シリーズ　銀河鉄道の夜\",\"titleKana\":\"ミヤザワケンジオリジナルサシエシリーズギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮澤賢治/司修\",\"authorKana\":\"ミヤザワケンジ/ツカサオサム\",\"availability\":\"1\",\"booksGenreId\":\"001003003001/001003001001\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784030166509\",\"itemCaption\":\"新風を巻きおこした実業之日本社版『宮沢賢治童話集』の挿画から４５年。あらたに生まれた、司修による『絵本　銀河鉄道の夜』。中学生から。\",\"itemPrice\":1540,\"itemUrl\":\"https://books.rakuten.co.jp/rb/12662563/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6509/9784030166509.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6509/9784030166509.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"偕成社\",\"reviewAverage\":\"4.0\",\"reviewCount\":2,\"salesDate\":\"2014年03月05日頃\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"絵本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/6509/9784030166509.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"絵本　銀河鉄道の夜\",\"titleKana\":\"エホンギンガテツドウノヨル\"}},{\"Item\":{\"affiliateUrl\":\"\",\"author\":\"宮澤 賢治\",\"authorKana\":\"ミヤザワ ケンジ\",\"availability\":\"1\",\"booksGenreId\":\"001004008007\",\"chirayomiUrl\":\"\",\"contents\":\"\",\"discountPrice\":0,\"discountRate\":0,\"isbn\":\"9784862513816\",\"itemCaption\":\"\",\"itemPrice\":3850,\"itemUrl\":\"https://books.rakuten.co.jp/rb/16040245/\",\"largeImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3816/9784862513816.jpg?_ex=200x200\",\"limitedFlag\":0,\"listPrice\":0,\"mediumImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3816/9784862513816.jpg?_ex=120x120\",\"postageFlag\":2,\"publisherName\":\"三和書籍\",\"reviewAverage\":\"0.0\",\"reviewCount\":0,\"salesDate\":\"2019年10月01日頃\",\"seriesName\":\"\",\"seriesNameKana\":\"\",\"size\":\"単行本\",\"smallImageUrl\":\"https://thumbnail.image.rakuten.co.jp/@0_mall/book/cabinet/3816/9784862513816.jpg?_ex=64x64\",\"subTitle\":\"\",\"subTitleKana\":\"\",\"title\":\"銀河鉄道の夜（宮澤賢治大活字本シリーズ1）\",\"titleKana\":\"ギンガテツドウノヨル\"}}],\"carrier\":0,\"count\":43,\"first\":1,\"hits\":30,\"last\":30,\"page\":1,\"pageCount\":2}";
	}*/
	/**temp*/
	private Info search(String post_info) {
		System.out.println("API access");
		String[] splitedInfo=post_info.split("/");
		String bookName=splitedInfo[0];
		String author=splitedInfo[1];
		/*
		String booksJson=getBooksJson(bookName,author);
		JSONParser parser = new JSONParser();
		JSONObject json=null;
		try {
			json = (JSONObject) parser.parse(booksJson);
		} catch(Exception e) {
			e.printStackTrace();
		}
		JSONArray books = (JSONArray) json.get("Items");
		ArrayList<Book> list=new ArrayList<>();
		for(Object book:books) {
			list.add(new Book((JSONObject)((JSONObject)book).get("Item")));
		}
		*/
		Application ap=new Application(bookName,author,"","");
		BooksInfos infos=new BooksInfos();
		infos=infos.RakutenBooksSearch(ap);
		Info info=new Info();
		info.setBooks(infos.getBookInfoList());
		long id=0;
		while(information.containsKey(id=getId()));
		information.put(id, info);
		info.setId(id);
		info.setText(post_info);
		info.setDefault();
		return info;
	}
	/**temp*/
	private long getId() {
		System.out.println("IdAccess");
		return new Random().nextLong(Long.MAX_VALUE);
	}
	/**temp*/
	private Info order(long id,int order) {
		Info info=information.get(id);
		if(info==null) {
			//error or time out
			throw new RuntimeException();
		}
		ArrayList<BookInfo> books=info.getBooks();
		switch(order) {
		case 1:
			Collections.sort(books, (a,b)->(a.getItemPrice()<b.getItemPrice()?1:-1));
			break;
		case 2:
			Collections.sort(books, (b,a)->(a.getItemPrice()>b.getItemPrice()?-1:1));
			//a.getSalesDate()!=null?a.getSalesDate().compareTo(b.getSalesDate()):-1
			break;
		default:
			//error
		}
		info.setDefault();
		return info;
	}
	/**temp*/
	private Info paging(long id,int page) {
		Info info=information.get(id);
		if(info==null) {
			//error or time out
			throw new RuntimeException();
		}
		System.out.println(page);
		/*処理予定*/
		return info;
	}
	
	private static String[] prefList= {"愛知県","青森県","秋田県","石川県","茨城県","岩手県","愛媛県",
										"大分県","大阪府","岡山県","沖縄県","香川県","鹿児島県","神奈川県",
										"岐阜県","京都府","熊本県","群馬県","高知県","埼玉県","佐賀県",
										"滋賀県","静岡県","島根県","千葉県","東京都","徳島県","栃木県",
										"鳥取県","富山県","長崎県","長野県","奈良県","新潟県","兵庫県",
										"広島県","福井県","福岡県","福島県","北海道","三重県","宮城県",
										"宮崎県","山形県","山口県","山梨県","和歌山県"};
	
	private void tosyoInit(String isbnNum,Model model) {
		model.addAttribute("isbnNum", isbnNum);
		model.addAttribute("pref", "");
		model.addAttribute("city", "");
		model.addAttribute("lib_map", new HashMap());
	}
	
	private void tosyoSearch(String pref,String city,String isbnNum,Model model) {
		model.addAttribute("isbnNum", isbnNum);
		model.addAttribute("pref", pref);
		model.addAttribute("city", city);
		/*
		String librarysJson=LibraryApiJava.searchNearbyLibraries(pref, city);
		String systemidStr=librarysJson!=null?LibraryApiJava.extractSystemIds(librarysJson):"";
		systemidStr=String.join(",", new LinkedHashSet<String>(Arrays.asList(systemidStr.split(","))));
		String tosyoSearchJson=//LibraryApiJava.searchLibraryAvailability(isbnNum, systemidStr);
				tosyoJsonContinue;
		//case3->booling libInfo=>HashMap(key=>sessionid)ifcontinue */
		Libinfos libinfos=new Libinfos();
		libinfos.setLibkeys(libinfos.searchLibraryAvailability(isbnNum , libinfos.LibLocSearch(pref , city)));
       model.addAttribute("lib_map",libinfos.getLibkeys());
	}
	
	/**temp*/private static String tosyoJsonContinue="{\"session\": \"c2bef9b927dad41d46a640b07aee9135a9f434b312c28e6c21b7e38d426e504f\", \"continue\": 1, \"books\": {\"9784102114018\": {\"Mie_Inabe\": {\"status\": \"Running\", \"reserveurl\": \"\"}, \"Mie_Iga\": {\"status\": \"Running\", \"reserveurl\": \"\"}, \"Mie_Ise\": {\"status\": \"Running\", \"reserveurl\": \"\"}}}}";
	/**temp*/private static String tosyoJson="{\"session\": \"c2bef9b927dad41d46a640b07aee9135a9f434b312c28e6c21b7e38d426e504f\", \"continue\": 0, \"books\": {\"9784102114018\": {\"Mie_Inabe\": {\"status\": \"OK\", \"libkey\": {\"北勢図書館\": \"貸出可\"}, \"reserveurl\": \"https://lib2.city.inabe.mie.jp/opac/item-details?id=5476\"}, \"Mie_Iga\": {\"status\": \"OK\", \"libkey\": {\"上野図書館\": \"貸出可\"}, \"reserveurl\": \"https://ilisod002.apsel.jp/iga-library/item-details?id=43503\"}, \"Mie_Ise\": {\"status\": \"OK\", \"libkey\": {\"伊勢図書館\": \"貸出可\"}, \"reserveurl\": \"https://ilisod001.apsel.jp/ise-library/wopc/pc/OpacServlet?disp=searchResultDetail&id=1827875\"}}}}";
	
	
	/*class Book {
		private final String title;
		private final String author;
		private final String publisherName;
		private final String salesDate;
		private final String itemPrice;
		private final String itemUrl;
		private final String isbnNum;
		Book(JSONObject book) {
			this.title=""+book.get("title");
			this.author=""+book.get("author");
			this.publisherName=""+book.get("publisherName");
			this.salesDate=""+book.get("salesDate");
			this.itemPrice=""+book.get("itemPrice");
			this.itemUrl=""+book.get("itemUrl");
			this.isbnNum=""+book.get("isbn");
		}
		public String getTitle() {
			return title;
		}
		public String getAuthor() {
			return author;
		}
		public String getPublisherName() {
			return publisherName;
		}
		public String getSalesDate() {
			return salesDate;
		}
		public String getItemPrice() {
			return itemPrice;
		}
		public String getItemUrl() {
			return itemUrl;
		}
		public String getIsbnNum() {
			return isbnNum;
		}
	}*/
	
	class Info {
		private ArrayList<BookInfo> books;
		private Long id;
		private String text;
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public ArrayList<BookInfo> getBooks() {
			return books;
		}
		public void setBooks(ArrayList<BookInfo> books) {
			this.books = books;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public void setDefault() {
			//if null -> default
			if(books==null) {
				books=new ArrayList<>();
			}
			if(id==null) {
				//temp
				throw new RuntimeException();
			}
			if(text==null) {
				text="";
			}
		}
	}
}
