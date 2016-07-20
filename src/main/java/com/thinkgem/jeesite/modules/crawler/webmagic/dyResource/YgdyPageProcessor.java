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
 * 阳光电影爬虫<br>
 * 输入搜索用户关键词(keyword)，并把搜出来的用户信息爬出来<br>

 * @date 2016-5-3
 * @website ghb.soecode.com
 * @csdn blog.csdn.net/antgan
 * @author antgan
 * 
 */
public class YgdyPageProcessor implements PageProcessor{
    //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setCharset("gb2312").setRetryTimes(20).setTimeOut(10000).setSleepTime(2000);
    //爬虫电影数量
    private static int num = 0;
    //搜索关键词
    //private static String keyword = "JAVA";
    //数据库持久化对象，用于将用户信息存入数据库
    private YgdyDao ygdyDao = new YgdyDaoImpl();

    

    /**
     * process 方法是webmagic爬虫的核心<br>
     * 编写抽取【待爬取目标链接】的逻辑代码在html中。
     *  http://www.ygdy8.net/html/gndy/dyzz/list_23_2.html
	 * 	http://www.ygdy8.net/html/gndy/dyzz/20160531/51077.html
     *  http://www\\.ygdy8\\.net/html/gndy/[\\w]+/list_\\d+_\\d+.html
     * 
     */
    @Override
    public void process(Page page) {

        //1. 如果是用户列表页面 【入口页面】，将所有用户的详细页面的url放入target集合中。
        if(page.getUrl().regex("http://www\\.ygdy8\\.net/html/\\w+/index\\.html").match()){
            page.addTargetRequests(page.getHtml().xpath("//div[@class='title_all']//a").links().all());
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']//table//a").links().all());
        //资源列表					
    	}else if(page.getUrl().regex("http://www\\.ygdy8\\.net/html/\\w+/\\w+/index\\.html").match()){
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/div[@class='x']").links().all());
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/ul").links().all());
    	//资源列表					
        }else if(page.getUrl().regex("http://www\\.ygdy8\\.net/html/\\w+/[\\w]+/list_\\d+_\\d+\\.html").match()){
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']//a").links().all());
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/div[@class='x']").links().all());
        //2. 如果是用户详细页面	http://www.ygdy8.net/html/tv/gangtai/tw/20100920/28275.html
        }else if(page.getUrl().regex("(http://www\\.ygdy8\\.net/html/\\w+/\\w+/\\d{8}/\\d+\\.html)|(http://www\\.ygdy8\\.net/html/\\w+/\\w+/\\w+/\\d{8}/\\d+\\.html)").match()){
        	//判断电影类型
        	String category = findDyType(page.getUrl().toString(),"html");
        	String categoryDetail = findDyType(page.getUrl().toString(),category);
        	String publishDate = findDyType(page.getUrl().toString(),"datetime");
        	
            
            /*实例化ZhihuUser，方便持久化存储。*/
            YgdyArticle article = new YgdyArticle();
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            String dyName = page.getHtml().xpath("//title/text()").get();
            List<String> imgs = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']//img").all();
            List<String> contents = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']//tidyText()").all();
            List<String> urls = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']//table//a/outerHtml()").all();
            
            //对象赋值
            article.setDyName(dyName);
            article.setDomain(page.getUrl().toString());
            article.setCategory(category);
            
            //拼接content
            StringBuffer sbContent = new StringBuffer(500);
            sbContent.append(imgs.size()>0?imgs.get(0):"");
            
            //去掉所有的链接
            Pattern pattern = Pattern.compile("(<.+?>)|(http\\:\\/\\/.+)|(ftp\\:\\/\\/.+)", Pattern.DOTALL);
            Matcher matcher = null;
            //文字内容
            for(int i=0; i < contents.size(); i++){
            	if(i == (contents.size()-1)){	//最后一行，处理掉“01”“001”的情况
            		matcher = pattern.matcher(contents.get(i).replaceAll("\\d+", ""));
            	}else{
            		matcher = pattern.matcher(contents.get(i));
            	}
            	
            	sbContent.append(matcher.replaceAll("").replaceAll("【下载地址】", ""));
            	sbContent.append("\n");
            }
            //图片
            for(int i = 1; i < imgs.size(); i++){
            	sbContent.append(imgs.get(i) );
            	sbContent.append("\n");
            }
            
            
            //去掉空行
            article.setContent(sbContent.toString().replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "")); 
            
            StringBuilder sbUrl = new StringBuilder(500);
            for(String str : urls){
            	sbUrl.append(str).append("\n");
            }
            article.setUrl(sbUrl.toString());
            article.setPublishDate(publishDate);

            num++;//电影数++
            System.out.println("num:"+num  );//输出对象
            ygdyDao.saveArticle(article,"yg");//保存用户信息到数据库
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
		  
		  Pattern patt= Pattern.compile("\\d{8}",Pattern.DOTALL);
           
		   
          
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
			  //匹配时间
			  if(aftername.equals("datetime")){
				  Matcher m = patt.matcher(str);   
				  if(m.find()){
					  return str;
				  }
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
        System.out.println("========阳光电影信息小爬虫【启动】喽！=========");
        startTime = new Date().getTime();
        //入口为：【https://www.zhihu.com/search?type=people&q=xxx 】，其中xxx 是搜索关键词
        Spider.create(new YgdyPageProcessor()).addUrl(
        		//"http://www.ygdy8.net/plus/sitemap.html",		//网站地图
        		"http://www.ygdy8.net/html/dongman/index.html",	//动漫
        		"http://www.ygdy8.net/html/game/index.html",	//游戏
        		"http://www.ygdy8.net/html/gndy/index.html",	//电影
        		"http://www.ygdy8.net/html/tv/index.html"		//电视剧
        		)
        .setScheduler(new FileCacheQueueScheduler("c:/data/webmagic/ygdy"))
        .thread(50).run();
        
//        Spider.create(new YgdyPageProcessor()).addUrl(
//        		//"http://www.ygdy8.net/plus/sitemap.html",		//网站地图
//        		//"http://www.ygdy8.net/html/dongman/index.html",	//动漫
//        		//"http://www.ygdy8.net/html/game/index.html",	//游戏
//        		//"http://www.ygdy8.net/html/gndy/index.html",	//电影
//        		"http://www.ygdy8.net/html/tv/index.html"		//电视剧
//        		
//        		)
//        .thread(10).run();
        
        endTime = new Date().getTime();
        System.out.println("========阳光电影小爬虫【结束】喽！=========");
        System.out.println("一共爬到"+num+"个用户信息！用时为："+(endTime-startTime)/1000+"s");
    }
}