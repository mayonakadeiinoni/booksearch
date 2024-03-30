package com.example.demo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import lombok.Data;

@Controller
public class SystemController {
	/*@InitBinder
	public void initBinder(WebDataBinder binder) {
		System.out.println(binder);
		System.out.println(binder.getFieldDefaultPrefix());
	}*/
	
	@RequestMapping(value="/test")
	private String test(/*TestForm checks,*/RakutenPara post_info,Model model) {
		//System.out.println(checks.getCheck());
		var temp=new RakutenPara();
		temp.setParameter(post_info.getParameter());
		System.out.println(temp.getParameter());
		model.addAttribute("parameter", temp.getParameter());
		return "/test";
	}
	
	private static final LinkedHashMap<String,String> sizes=new LinkedHashMap<>();
	private static final LinkedHashMap<String,String> orders=new LinkedHashMap<>();
	private static final LinkedHashMap<String,String> genres=new LinkedHashMap<>();
	static {
		sizes.put("0","全て");
		sizes.put("1","単行本");
		sizes.put("2","文庫");
		sizes.put("3","新書");
		sizes.put("4","全集・双書");
		sizes.put("5","事・辞典");
		sizes.put("6","図鑑");
		sizes.put("7","絵本");
		sizes.put("8","カセット,CDなど");
		sizes.put("9","コミック");
		sizes.put("10","ムックその他");
		orders.put("standard","標準");
		orders.put("sales","売れている");
		orders.put("+releaseDate","発売日(古い)");
		orders.put("-releaseDate","発売日(新しい)");
		orders.put("+itemPrice","価格が安い");
		orders.put("-itemPrice","価格が高い");
		orders.put("reviewCount","レビューの件数が多い");
		orders.put("reviewAverage","レビューの評価(平均)が高い");
		genres.put( "001","全て");
		genres.put( "001001","漫画（コミック）");
		genres.put( "001002","語学・学習参考書");
		genres.put( "001003","絵本・児童書・図鑑");
		genres.put( "001004","小説・エッセイ");
		genres.put( "001005","パソコン・システム開発");
		genres.put( "001006","ビジネス・経済・就職");
		genres.put( "001007","旅行・留学・アウトドア");
		genres.put( "001008","人文・思想・社会");
		genres.put( "001009","ホビー・スポーツ・美術");
		genres.put( "001010","美容・暮らし・健康・料理");
		genres.put( "001011","エンタメ・ゲーム");
		genres.put( "001012","科学・技術");
		genres.put( "001013","写真集・タレント");
		genres.put( "001016","資格・検定");
		genres.put( "001017","ライトノベル");
		genres.put( "001018","楽譜");
		genres.put( "001019","文庫");
		genres.put( "001020","新書");
		genres.put( "001021","ボーイズラブ（BL）");
		genres.put( "001022","付録付き");
		genres.put( "001023","バーゲン本");
		genres.put( "001025","セット本");
		genres.put( "001026","カレンダー・手帳・家計簿");
		genres.put( "001027","文具・雑貨");
		genres.put( "001028","医学・薬学・看護学・歯科学");
		genres.put( "001029","ティーンズラブ（TL）");
	}
	
	@RequestMapping(value="/lib_search_test")
	private String controllLibSearch(RakutenPara post_info,Integer form_num,Long search_id,
			Integer order,Integer page,String selector,Model model,/**temp*/Integer page_size/**temp*/) {
		Info info=new Info();
		model.addAttribute("sizes", sizes);
		model.addAttribute("orders", orders);
		model.addAttribute("genres", genres);
		if(form_num==null) {
			info.setId(-1L);
			info.setDefault();
			model.addAttribute("info", info);
			model.addAttribute("parameter", info.getPostInfo().getParameter());
			return "/lib_search_test";
		}
		switch(form_num) {
		case 1:
			info=search(post_info);
			break;
		case 2:
			info=order(search_id,order);//delete
			break;
		case 3:
			info=paging(search_id,page);
			break;
		case 4:
			info=select(search_id,selector);
		case 5:
			info=setPageSize(search_id,page_size);
		default:
			//error();
		}
		model.addAttribute("info", info);
		model.addAttribute("parameter", info.getPostInfo().getParameter());
		//model.addAttribute("pagelist", Arrays.asList(1,2,3));
		//System.out.println(info.getBooks());
		return "/lib_search_test";
	}
	
	@RequestMapping(value="/tosyo_search_test")
	private String controllTosyoSearch(String pref,String city,String isbnNum,Integer form_num,
			String address,Model model) {
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
			break;
		case 3:
			tosyoSearch(address,isbnNum,model);
		}
		return "/tosyo_search_test";
	}
	
	
	private static HashMap<Long,Info> information=new HashMap<>(); 
	
	private Info search(RakutenPara post_info) {
		System.out.println("API access");
		RakutenPara checked=new RakutenPara();
		checked.setParameter(post_info.getParameter());
		Info info=new Info();
		info.setPostInfo(checked);
		long id=0;
		Util util=new Util();
		id=util.getId();
		info.setId(id);
		information.put(id, info);
		getPage(id,1);
		info.setDefault();
		return info;
	}
	/**delete*/
	private long getId() {
		//get from detabese
		/*System.out.println("IdAccess");
		return new Random().nextLong(Long.MAX_VALUE);*/
		throw new RuntimeException("SystemController.getId() is deleted");
	}
	/**delete*/
	private Info order(long id,int order) {
		/*Info info=information.get(id);
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
		return info;*/
		return null;
	}
	private Info paging(long id,int page) {
		Info info=information.get(id);
		if(info==null) {
			//error or time out
			throw new RuntimeException();
		}
		System.out.println(page);
		if(!"".equals(info.getSelector())) {
			System.out.println(Conversion.converts(info.getSelector().split("\n")));
		}
		/*処理予定*/
		//if it has selector?
		//true -> getSelectedPage(id,page,convertedSelector);
		//false -> getPage(id,page);
		if("".equals(info.getSelector())) {
			if(info.getPageSize()==30) {
				getPage(id,page);
			} else {
				getPage(id,page,info.getPageSize());
			}
		} else {
			getSelectedPage(id, page, Conversion.converts(info.getSelector().split("\n")));
		}
		info.setPage(page);
		return info;
	}
	private Info select(long id,String selector) {
		Info info=information.get(id);
		if(info==null) {
			//error or time out
			throw new RuntimeException();
		}
		//if("".equals(selector)) return info;
		String[] queries=selector.split("\n");
		selector=Conversion.converts(queries);
		selector="".equals(selector)?"true":selector;
		getSelectedPage(id,1,selector);
		info.setSelector(String.join("\n", queries));
		info.setPage(1);
		return info;
	}
	private Info setPageSize(long id,Integer pageSize) {
		Info info=information.get(id);
		/**temp*/if(pageSize!=null&&pageSize>0&&pageSize<=100) {
			info.setPageSize(pageSize);
		}/*temp**/
		return paging(id,1);
	}
	private Info getPage(long id,int page) {
		//Is it in cache?
		//true -> get from cache
		//false -> get from API
		Info info=information.get(id);
		Util util=new Util();
		if(util.hasCache(id, page)) {
			info.setBooks((ArrayList)util.getData(id, page));
		} else {
			BooksInfos infos=getFromApi(id,page);
			info.setBooks(infos.getBookInfoList());
			info.setLastPage(infos.getPageCount());
		}
		return info;
	}
	private Info getPage(long id,int page,int pageSize) {
		//select detabase
		//if not full and not end
		//get from api and continue
		Util util=new Util();
		Info info=information.get(id);
		List<BookInfo> books=util.getData(id, page, pageSize);
		while(true) {
			if(books.size()>=pageSize||info.isLast()) break;
			int maxPage=util.getMaxPage(id);
			BooksInfos infos=getFromApi(id,maxPage+1);
			if(infos.getCount()==0) break;
			try {
				Thread.sleep(500);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			books=util.getData(id, page, pageSize);
		}
		info.setBooks((ArrayList)books);
		return info;
	}
	/**temp*/
	private BooksInfos getFromApi(long id,int page) {
		//api
		//add database
		//get from cache?
		Info info=information.get(id);
		RakutenPara postInfo=info.getPostInfo();
		postInfo.setMap("page", ""+page);
		BooksInfos infos=new BooksInfos().RakutenBooksSearch(postInfo);
		/**temp*/
		if(infos.getCount()!=0)
			new Util().setData(infos, id, page);
		/*temp**/
		return infos;
	}
	private Info getSelectedPage(long id,int page,String convertedSelector) {
		//select detabase
		//if not full and not end
		//get from api and continue
		Util util=new Util();
		Info info=information.get(id);
		int pageSize=info.getPageSize();
		List<BookInfo> books=util.getSelectedData(id, page, pageSize, convertedSelector);
		while(true) {
			if(books.size()>=pageSize||info.isLast()) break;
			int maxPage=util.getMaxPage(id);
			BooksInfos infos=getFromApi(id,maxPage+1);
			if(infos.getCount()==0) break;
			try {
				Thread.sleep(500);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
			books=util.getSelectedData(id, page, pageSize, convertedSelector);
		}
		info.setBooks((ArrayList)books);
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
	private void tosyoSearch(String address,String isbnNum,Model model) {
		model.addAttribute("isbnNum", isbnNum);
		model.addAttribute("pref", "");
		model.addAttribute("city", "");
		Libinfos libinfos=new Libinfos();
	    var temp=new GeocodingExample();
		libinfos.setLibkeys(libinfos.searchLibraryAvailability(isbnNum , libinfos.LibLocSearch(temp.geocoding(address))));
	    model.addAttribute("lib_map",libinfos.getLibkeys());
	    model.addAttribute("address",temp.getTitle());
	    //title get and set address
	}
	
	class Info {
		private ArrayList<BookInfo> books;
		private Long id;
		private String selector;
		private RakutenPara postInfo;
		private Integer page=1;
		private int lastPage=-1;
		private int pageSize=30;
		public String getSelector() {
			return selector;
		}
		public void setSelector(String selector) {
			this.selector = selector;
		}
		public RakutenPara getPostInfo() {
			return postInfo;
		}
		public void setPostInfo(RakutenPara postInfo) {
			this.postInfo = postInfo;
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
		public Integer getPage() {
			return page;
		}
		public void setPage(Integer page) {
			this.page = page;
		}
		public boolean isLast() {
			return lastPage==page||books.size()<pageSize;
		}
		public void setLastPage(int lastPage) {
			this.lastPage = lastPage;
		}
		public int getLastPage() {
			return lastPage;
		}
		public int getPageSize() {
			return pageSize;
		}
		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
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
			if(postInfo==null) {
				postInfo=new RakutenPara();
			}
			if(selector==null) {
				selector="";
			}
		}
	}
}
