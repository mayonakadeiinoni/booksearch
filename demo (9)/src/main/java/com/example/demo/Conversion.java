package com.example.demo;

import java.util.regex.Pattern;
import java.util.*;

public class Conversion {
	private Conversion() {}
	private final static String UseForE="/=><!";
	private final static String Tail="(([^\s].*[^\s])|[^\s])$";//"([^\s"+UseForE+"]|/["+UseForE+"])([^"
									//+UseForE+"]|(/["+UseForE+"]))*([^\s"+UseForE+"]|(/["+UseForE+"]))?$";
	private final static String THead="(author|title|caption|publisherName)"/*+"|(header)"*/;
	private final static String IHead="price";
	private final static String IMid="(=|>|<|<=|>=|!=)";
	private final static String Mid="(=|>|<|<=|>=|!=|~|!~)";
	private final static Pattern p1=Pattern.compile("^"+THead+"\s(=|!=|~|!~)\s"+Tail);
	private final static Pattern p2=Pattern.compile("^"+IHead+"\s"+IMid+"\s([1-9][0-9]*|0)$");

	public static String converts(String[] queries) {
		String result="";
		for(int i=0;i<queries.length;i++) {
			String addedQuery="";
			for(String base:queries[i].split(",")) {
				String convert=convert(base.trim());
				if(convert!=null) {
					addedQuery+=base+',';
					result+=convert+" AND ";
				}
			}
			if(!"".equals(addedQuery)) {
				addedQuery=addedQuery.substring(0, addedQuery.length()-1);
				result=result.substring(0,result.length()-5)+" OR ";
			}
			queries[i]=addedQuery;
		}
		if(!"".equals(result))
			result=result.substring(0, result.length()-4);
		return result;
	}
	/**It return null if it can't convert.*/
	public static String convert(String base) {
		if(base==null||!(p1.matcher(base).matches()||p2.matcher(base).matches())) return null;
		//System.out.println("inConvert");
		//System.out.printf("%s,%s\n",p1.matcher(base).matches(),p2.matcher(base).matches());
		String head=getHead(base);
		if("author".equals(head)) return authorCase("authorName",base,head.length());//select from he
		if("title".equals(head)) return eqCase("bookName",base,head.length());
		if("price".equals(head)) return intCase(head,base,head.length());
		if("caption".equals(head)) return eqCase("itemCaption",base,head.length());
		if("publisherName".equals(head)) return eqCase(head,base,head.length());
		System.out.println("error:Conversion.convert("+base+")");
		return null;
	}
	private static String getHead(String base) {
		return base.split("\s", 2)[0];//Head not in \s+selector+
	}
	private static String getTail(String base) {
		return base.split("\s"+Mid+"\s")[1];
	}
	private final static String authorFormat="isbn IN (SELECT isbn FROM authors WHERE %s %s '%s')";
	private static String authorCase(String head,String base,int length) {
		char c1=base.charAt(length+1);
		char c2=base.charAt(length+2);
		if(c1=='=') return String.format(authorFormat, head,"=",getTail(base));
		if(c1=='!') return (c2=='=')?
				String.format(authorFormat, head,"!=",getTail(base)):
				String.format(authorFormat, "NOT "+head,"LIKE",'%'+getTail(base)+'%');
		if(c1=='~') return String.format(authorFormat, head,"LIKE",'%'+getTail(base)+'%');
		return null;
	}
	private static String eqCase(String head,String base,int length) {
		char c1=base.charAt(length+1);
		char c2=base.charAt(length+2);
		if(c1=='=') return head+" = '"+getTail(base)+"'";
		if(c1=='!') return (c2=='=')?(head+" != '"+getTail(base)+"'"):("NOT "+head+" LIKE '%"+getTail(base)+"%'");
		if(c1=='~') return head+" LIKE '%"+getTail(base)+"%'";
		return null;
	}
	private static String intCase(String head,String base,int length) {
		//System.out.println("intCase");
		char c1=base.charAt(length+1);
		char c2=base.charAt(length+2);
		//System.out.println(c1+""+c2);
		if(c1=='<') return c2=='='?head+" <= "+getTail(base):head+" < "+getTail(base);
		if(c1=='>') return c2=='='?head+" >= "+getTail(base):head+" > "+getTail(base);
		if(c1=='=') return head+" = "+getTail(base);
		if(c1=='!') return head+" != "+getTail(base);
		return null;
	}
	public static void main(String[] args) {
		/*List<String> testStrs=new ArrayList<>();
		testStrs.add("price <= 100");
		testStrs.add("title = aefjo");
		testStrs.add("author = a bc");
		testStrs.add("title != jio w");
		testStrs.add("title <= 100");
		testStrs.add("price != 100");
		testStrs.add("price != 0");
		testStrs.add("title != !abn");
		testStrs.add("author ~ aid");
		testStrs.add("title ~ aid");
		testStrs.add("author !~ aid");
		testStrs.add("title !~ aid");
		testStrs.add("author !=  extraSpace");
		testStrs.add("author != nonTrim ");
		testStrs.add("title = ae = fjo");
		System.out.println(p1);
		for(String testStr:testStrs) {
			System.out.println(testStr);
			System.out.println(convert(testStr));
			System.out.println();
		}*/
		String testStr=
				//"price <= 100,title = aefjo\nprice != 100\nauthor !~ aid";
				"author !=  extraSpace\na,b\ntitle !~ aid\n";
		System.out.println("test:\n"+testStr);
		String[] queries=testStr.split("\n");
		System.out.println("converts:\n"+converts(queries));
		System.out.println("join:\n"+String.join("\n", queries));
		//System.out.println(p1);
	}
}
