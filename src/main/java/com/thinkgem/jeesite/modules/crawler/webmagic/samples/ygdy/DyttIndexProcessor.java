package com.thinkgem.jeesite.modules.crawler.webmagic.samples.ygdy;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

/**
 * 首页的爬虫

 * @date 2016-5-3
 * @website ghb.soecode.com
 * @csdn blog.csdn.net/antgan
 * @author antgan
 * 
 */
public class DyttIndexProcessor implements PageProcessor{
    //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setCharset("gb2312").setRetryTimes(10).setTimeOut(10000).setSleepTime(1000);
    //爬虫电影数量
    private static int dynum = 0;
    //爬虫电视剧数量
    private static int tvnum = 0;
    //数据库持久化对象，用于将用户信息存入数据库
    YgdyDao ygdyDao = new YgdyDaoImpl();

    

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

    	String lastDate = "";
    	try {
			Date date = ygdyDao.getLastDate("tt_"+"gndy");
			SimpleDateFormat simpledf = new SimpleDateFormat("yyyyMMdd");
			lastDate = simpledf.format(date);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        //1. 如果是用户列表页面 【入口页面】，将所有用户的详细页面的url放入target集合中。
        if(page.getUrl().regex("http://www\\.dytt8\\.net/index\\.html").match()){
            page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/ul/table").links().regex(".*/html/gndy/.*/[\\d]{8}/.*").all());
            page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content3']/ul/table").links().regex(".*/html/tv/.*/[\\d]{8}/.*").all());
            	
        //2. 如果是用户详细页面
        }else if(page.getUrl().regex("http://www\\.dytt8\\.net/html/gndy/[\\w]+/\\d{8}/\\d+\\.html").match()){
        	
        	//判断电影类型
        	String category = findDyType(page.getUrl().toString(),"gndy");
        	String publishDate = findDyType(page.getUrl().toString(),category);
        	System.out.println(publishDate);
        	
        	if(publishDate.compareTo(lastDate) < 0) return;
        	
            dynum++;//电影数++
            /*实例化ZhihuUser，方便持久化存储。*/
            YgdyArticle article = new YgdyArticle();
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            String dyName = page.getHtml().xpath("//title/text()").get();
            String content = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']/span/p").get();
            String url = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']/span/table//a").links().get();
            
            //对象赋值
            article.setDyName(dyName);
            article.setDomain("www.dytt8.net");
            article.setCategory(category);
            article.setContent(content); 
            article.setUrl(url);
            article.setPublishDate(publishDate);

            //保存电影信息到数据库
            try {
				int result = ygdyDao.updateArticle(article,"gndy","");
				if(result==1){
					
					System.out.println("电影num:"+dynum +" " + article.toString());//输出对象
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }else if(page.getUrl().regex("http://www\\.dytt8\\.net/html/tv/[\\w]+/\\d{8}/\\d+\\.html").match()){
        	
        	//判断电视剧类型
        	String category = findDyType(page.getUrl().toString(),"tv");
        	String publishDate = findDyType(page.getUrl().toString(),category);
        	System.out.println(publishDate);
        	
        	if(publishDate.compareTo(lastDate) <= 0) return;
        	
            tvnum++;//电视剧数++
            /*实例化ZhihuUser，方便持久化存储。*/
            YgdyArticle article = new YgdyArticle();
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            String dyName = page.getHtml().xpath("//title/text()").get();
            String content = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']/span/p").get();
            String url = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']/span/table//a").links().get();
            
            //对象赋值
            article.setDyName(dyName);
            article.setDomain("www.dytt8.net");
            article.setCategory(category);
            article.setContent(content); 
            article.setUrl(url);
            article.setPublishDate(publishDate);

            
            //保存电视剧信息到数据库
            try {
				int result = ygdyDao.updateArticle(article,"tv","");
				if(result==1){
					
					System.out.println("电视剧num:"+tvnum +" " + article.toString());//输出对象
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
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

	public void run(){
		long startTime ,endTime;
        System.out.println("==www.dytt8.net======电影信息首页小爬虫【启动】喽！=========");
        startTime = new Date().getTime();
        Spider.create(new DyttIndexProcessor()).addUrl(
        		"http://www.dytt8.net/index.html")
        .thread(1).run();
        endTime = new Date().getTime();
        System.out.println("==www.dytt8.net======电影首页小爬虫【结束】喽！=========");
        
        //更新最新时间
        YgdyDao ygdyDao = new YgdyDaoImpl();
		try {
			ygdyDao.setLastDate("tt_"+"gndy", new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        System.out.println("===www.dytt8.net=====一共爬到"+dynum+"个电影信息, "+tvnum+"个连续剧！用时为："+(endTime-startTime)/1000+"s");
        
	}
	
    public static void main(String[] args) {
        long startTime ,endTime;
        System.out.println("==www.dytt8.net======电影信息首页小爬虫【启动】喽！=========");
        startTime = new Date().getTime();
        Spider.create(new DyttIndexProcessor()).addUrl(
        		"http://www.dytt8.net/index.html")
        .thread(1).run();
        endTime = new Date().getTime();
        System.out.println("==www.dytt8.net======电影首页小爬虫【结束】喽！=========");
        
        //更新最新时间
        YgdyDao ygdyDao = new YgdyDaoImpl();
		try {
			ygdyDao.setLastDate("tt_"+"gndy", new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        System.out.println("===www.dytt8.net=====一共爬到"+dynum+"个电影信息, "+tvnum+"个连续剧！用时为："+(endTime-startTime)/1000+"s");
        
        
    }
}