package com.thinkgem.jeesite.modules.crawler.webmagic.dyResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

/**
 * 知乎用户小爬虫<br>
 * 输入搜索用户关键词(keyword)，并把搜出来的用户信息爬出来<br>

 * @date 2016-5-3
 * @website ghb.soecode.com
 * @csdn blog.csdn.net/antgan
 * @author antgan
 * 
 */
public class LblPageProcessor implements PageProcessor{
    //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setCharset("UTF-8").setRetryTimes(10).setTimeOut(10000).setSleepTime(1000);
    //爬虫电影数量
    private static int num = 1;
    //数据库持久化对象，用于将用户信息存入数据库
    private YgdyDao ygdyDao = new YgdyDaoImpl();

    @Override
    public void process(Page page) {

           	
        if(page.getUrl().regex("http://www\\.lbldy\\.com/\\w+/\\w+\\.html").match()){
        	//判断电影类型
        	String category = findDyType(page.getUrl().toString(),"www.lbldy.com");
        	String publishDate = page.getHtml().xpath("//div[@class='postmeat']//text()").regex("\\d+年\\d+月\\d+日").get();
        	//对日期进行处理
        	String reg = "(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+";	// 提起字符串中间的多个数字
        	Pattern p2 = Pattern.compile(reg);  
    		Matcher m2 = p2.matcher(publishDate);
    		if(m2.find()){  
    			publishDate = m2.group(1)+""+(m2.group(2).length()==1?("0"+m2.group(2)):m2.group(2)) + (m2.group(3).length()==1?("0"+m2.group(3)):m2.group(3));  
    		} 
        	
            YgdyArticle article = new YgdyArticle();
            String dyName = page.getHtml().xpath("//title/text()").get();
            //处理标题
            String[] names = dyName.split("\\|");
            if(names.length > 1){
            	dyName = names[0];
            }
            
            List<String> contentImgs = page.getHtml().xpath("//div[@class='entry']/p/img/outerHtml()").all();
            List<String> contentIntro = page.getHtml().xpath("//div[@class='entry']/p/tidyText()").all();
            //内容去掉相关的url
            StringBuilder content = new StringBuilder(2000);
            
            content.append(contentImgs.size()>0?contentImgs.get(0).replaceAll("alt=\"\"", "alt=\"电影东东\""):"");
            
            //替换相关的url
            Pattern pattern = Pattern.compile("<.+?>|http\\:\\/\\/.+", Pattern.DOTALL);
            Matcher matcher = null;
            //文字内容
            for(String c : contentIntro){
            	
            	matcher = pattern.matcher(c);
            	content.append(matcher.replaceAll("").replaceAll("：", ":").replaceAll("下载地址:", "")
            				.replaceAll("网盘:", "").replaceAll(".*<>", "")
            				.replaceAll("网盘链接:", "").replaceAll("预告片:", ""));
            	content.append("\n");
            }
            //图片
            for(int i = 1; i < contentImgs.size(); i++){
            	content.append(contentImgs.get(i) );
            	content.append("\n");
            }
            
            //下载地址
            StringBuilder urls = new StringBuilder(2000);
            
            List<String> baiduUrls = page.getHtml().xpath("//div[@class='entry']/p/a/outerHtml()").regex("(.*pan\\.baidu\\.com.*)").all();
            List<String> baiduPasswords = page.getHtml().xpath("//div[@class='entry']/p/text()").regex("密码.*").all();
            for(int i=0; i<baiduUrls.size(); i++){
            	urls.append(baiduUrls.get(i));
            	urls.append("\t");
            	urls.append(baiduPasswords.size()>=(i+1)?baiduPasswords.get(i):"");
            	urls.append("\n");
            }
            
            Pattern patt_= Pattern.compile("(http://[\\|\\w\\(\\)\\.\\?\\&\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：\\-\\;]+.torrent)",Pattern.DOTALL);
            Matcher matc_ = patt_.matcher(page.getRawText());
            while (matc_.find()) {
            	urls.append("<a href="+matc_.group()+">" + (dyName+" - 电影东东") + "</a>");
            }
            
            List<String> otherUrls = page.getHtml().xpath("//div[@class='entry']/p/a/outerHtml()").regex("(.*href=\"\".*)").all();
            Pattern patt= Pattern.compile("(ed2k://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：]+)|(thunder://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：]+)|(magnet:\\?[\\|\\w\\(\\)\\.\\?\\&\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：\\-\\;]+)|(ftp:\\?[\\|\\w\\(\\)\\.\\?\\&\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：\\-\\;]+)",Pattern.DOTALL);
            
            List<String> ed2ks = new ArrayList<String>();
            Matcher matc = patt.matcher(page.getRawText());
            while (matc.find()) {
            	ed2ks.add(matc.group());
            }
            
            //把所有的url替换回来
            int j = 0;	
            for(int k = 0; k < otherUrls.size(); k++){
            	String u = otherUrls.get(k);
            	if(k > (ed2ks.size()-1)) continue;
            	
            	if(u.contains("href=\"\"")){
            		u = u.replace("href=\"\"", "href=\"" + ed2ks.get(j++) + "\"");
            		urls.append(u);
            		urls.append("\n");
            		continue;
            	}else{
            		urls.append(u);
            		urls.append("\n");
            		continue;
            	}
            }
//            urls.append("</pre>");
            
            //此条电影信息过滤
            if(urls == null || urls.length()==0){
            	page.setSkip(true);
            }else{
	            //对象赋值
	            article.setDyName(dyName.trim());
	            article.setDomain(page.getUrl().toString());
	            article.setCategory(category);
	            
	            //去掉所有空行
	            article.setContent(content.toString().replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "")); 
	            article.setUrl(urls.toString());
	            article.setPublishDate(publishDate);
	
	            System.out.println("--------------------num:"+ num++ );//输出对象
	            ygdyDao.saveArticle(article,"lbl");//保存电影信息到数据库
            }
        } else  if(page.getUrl().regex("http://www\\.lbldy\\.com/\\w+/").match() || page.getUrl().regex("http://www\\.lbldy\\.com/\\w+/page/\\d+/").match()){
        	//1. 如果是用户列表页面 【入口页面】，将所有用户的详细页面的url放入target集合中。
        	page.addTargetRequests(page.getHtml().xpath("//div[@id='page']//div[@class='postlist']/h4/").links().all());
            page.addTargetRequests(page.getHtml().xpath("//div[@class='nav-links']/").links().all());
        	
        }
        
    }

    /**
     * 
     * @param url		匹配的网址
     * @param aftername 匹配名称后面 的一个
     * @return
     */
    private String findDyType(String url, String aftername) {
    	 //String url = "http://www.ygdy8.net/html/gndy/dyzz/index.html";  
		  //将字符串以/切分并存到数组中  
		  String[] split = url.split("/");
		  
		  String type = "";
		  boolean flag =  false;
		  for(String str : split){  
			  if(flag == true){
				  type = str;
				  break;
			  }
			  
			  if(aftername.equalsIgnoreCase(str)){
				  flag = true;
			  }
		  }
		return type.contains("index")?"":type;
	}

	@Override
    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args) {
    	
    	
    	
    	
        long startTime ,endTime;
        System.out.println("========lbl电影信息小爬虫【启动】喽！=========");
        startTime = new Date().getTime();
        //入口为：【https://www.zhihu.com/search?type=people&q=xxx 】，其中xxx 是搜索关键词
        Spider.create(new LblPageProcessor()).addUrl(
        		"http://www.lbldy.com/movie/",	//电影
        		"http://www.lbldy.com/television/",	//电视剧http://www.lbldy.com/television/page/2/
        		"http://www.lbldy.com/dongman/",	//动漫http://www.lbldy.com/dongman/page/2/
        		"http://www.lbldy.com/video/"	//娱乐综艺
        		)
        .setScheduler(new FileCacheQueueScheduler("c:/data/webmagic/lbl"))
        .thread(50).run();
        
//        Spider.create(new LblPageProcessor()).addUrl(
//        		"http://www.lbldy.com/movie/" 
////        		,	//电影
////        		"http://www.lbldy.com/television/",	//电视剧http://www.lbldy.com/television/page/2/
////        		"http://www.lbldy.com/dongman/",	//动漫http://www.lbldy.com/dongman/page/2/
////        		"http://www.lbldy.com/video/"	//娱乐综艺
////        		
//        		
//        		)
//        .thread(1).run();
        endTime = new Date().getTime();
        System.out.println("========lbl电影小爬虫【结束】喽！=========");
        System.out.println("一共爬到"+num+"个电影信息！用时为："+(endTime-startTime)/1000+"s");
    }
}