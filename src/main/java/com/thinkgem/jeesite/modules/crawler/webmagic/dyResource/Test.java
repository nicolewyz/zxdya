package com.thinkgem.jeesite.modules.crawler.webmagic.dyResource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Test {

	static String teststr = "UAPPROJECT_ID='402894cb4833decf014833e04fd70002 ; \n\r */' select ";  
    
	/** 
	 * 包含回车换行符的处理 
	 */  
	public void testa(){  
	    Pattern wp = Pattern.compile("'.*?'", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);   
	    Matcher m = wp.matcher(teststr);  
	    String result = m.replaceAll("");  
	    System.out.println("result:" + result);       
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
		String str = "<a href=\"ed2k://|file|%E9%AD%94%E5%85%BD(%E9%9F%A9%E7%89%88).720p.HD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97[www.66ys.tv].mp4|2044254966|CC02D8907F97AED4828D8A4C14C7AC25|h=HLZZ6CN5SSWUZ4M6QDI3PTLPVR3SLIQO|/\">abc</a>\n"
				+ " <a href=\"ed2k://|file|%E9%AD%94%E5%85%BD(%E9%9F%A9%E7%89%88).720p.HD%E4%>dd</a>";
        Pattern patt= Pattern.compile("(ed2k://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=]+)",Pattern.DOTALL);
        
        Matcher matc = patt.matcher(str);
        
        StringBuilder stringBuilder = new StringBuilder();
        while (matc.find()) {
            System.out.println(matc.group());
        }
	}
}
