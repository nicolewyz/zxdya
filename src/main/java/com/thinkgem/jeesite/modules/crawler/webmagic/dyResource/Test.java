package com.thinkgem.jeesite.modules.crawler.webmagic.dyResource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	static String teststr = "UAPPROJECT_ID='402894cb4833decf014833e04fd70002 ; \n\r */' select ";  
    
	/** 
	 * 包含回车换行符的处理 
	 */  
	public void testa(){  
	    Pattern wp = Pattern.compile("\\d{5}",Pattern.DOTALL);   
	    Matcher m = wp.matcher(teststr);  
	    //String result = m.replaceAll("");  
	    System.out.println("result:" + m.find());       
	}  
	  
	/** 
	 * 包含回车换行符的处理 
	 */  
	public void testb(){  
	    String result = teststr.replaceAll("(?s)'.*?'", "");          
	    System.out.println("result:" + result);   
	}  
	
	public static void main(String[] args) {
		
		//String str = "ed2k://|file|%E9%AD%94%E5%85%BD(%E9%9F%A9%E7%89%88).720p.HD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97[www.66ys.tv].mp4|2044254966|CC02D8907F97AED4828D8A4C14C7AC25|h=HLZZ6CN5SSWUZ4M6QDI3PTLPVR3SLIQO|/";
//		String str = "<a href=\"ed2k://|file|%E9%AD%94%E5%85%BD(%E9%9F%A9%E7%89%88).720p.HD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97[www.66ys.tv].mp4|2044254966|CC02D8907F97AED4828D8A4C14C7AC25|h=HLZZ6CN5SSWUZ4M6QDI3PTLPVR3SLIQO|/\">abc</a>\n"
//				+ " <a href=\"magnet:?xt=urn:btih:8164e3b08c75c2b24fddbe9dec87ef4b0b4eae52&dn=%5B%E6%B5%B7%E6%B4%8B%E6%B7%B1%E5%A4%84%5D.In.The.Heart.Of.The.Sea.2015.3D.BluRay.1080p.HSBS.x264.TrueHD7.1-CMCT&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80%2Fannounce&tr=http%3A%2F%2Ftracker.cmct.cc%3A2710%2Fannounce\">dd</a>";
//		Pattern patt= Pattern.compile("(ed2k://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：]+)|(thunder://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：]+)|(magnet:\\?[\\|\\w\\(\\)\\.\\?\\&\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：\\-]+)",Pattern.DOTALL);
//        
//        Matcher matc = patt.matcher(str);
//        
//        StringBuilder stringBuilder = new StringBuilder();
//        while (matc.find()) {
//            System.out.println(matc.group());
//        }
//		Calendar ca = Calendar.getInstance();//创建一个日期实例
//		ca.setTime(new Date());//实例化一个日期
//		System.out.println(ca.get(Calendar.DAY_OF_YEAR));
		
//		String reg = "(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+";	// 提起字符串中间的多个数字
//		String s = "2016年5月2日";  
//		Pattern p2 = Pattern.compile(reg);  
//		Matcher m2 = p2.matcher(s);  
//		int historyHighestLevel = 1;
//		if(m2.find()){  
//		    historyHighestLevel = Integer.parseInt(m2.group(1));
//		    System.out.println(m2.group(1)+""+(m2.group(2).length()==1?("0"+m2.group(2)):m2.group(2)) + (m2.group(3).length()==1?("0"+m2.group(3)):m2.group(3)));  // 组提取字符串  
//		} 
		
		String str = "http://xz.66vod.net:889/2015/[Natsu%20no%20Hi%20Kimi%20no%20Koe][720p].torrent";
		
		Pattern patt= Pattern.compile("(ed2k://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：]+)|(thunder://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：]+)|(magnet:\\?[\\|\\w\\(\\)\\.\\?\\&\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：\\-\\;]+)|(ftp:\\?[\\|\\w\\(\\)\\.\\?\\&\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：\\-\\;]+)|(http://[\\|\\w\\(\\)\\.\\?\\&\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：\\-\\;]+\\.torrent$)",Pattern.DOTALL);
        
        List<String> ed2ks = new ArrayList<String>();
        Matcher matc = patt.matcher(str);
        while (matc.find()) {
        	System.out.println(matc.group());
        }
	}
}
