package com.thinkgem.jeesite.modules.crawler.webmagic.filmsInfo;

import java.util.Date;

import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyArticle;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyDao;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyDaoImpl;

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
public class DoubanPageProcessor implements PageProcessor{
    //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setCharset("gb2312").setRetryTimes(10).setTimeOut(10000).setSleepTime(1000);
    //爬虫电影数量
    private static int num = 0;
    //搜索关键词
    //private static String keyword = "JAVA";
    //数据库持久化对象，用于将用户信息存入数据库
    private YgdyDao ygdyDao = new YgdyDaoImpl();

    

    /**
     * process 方法是webmagic爬虫的核心<br>
     * 编写抽取【待爬取目标链接】的逻辑代码在html中。
     *  http://www.ygdy8.net/html/tv/dyzz/list_23_2.html
	 * 	http://www.ygdy8.net/html/tv/dyzz/20160531/51077.html
     *  http://www\\.ygdy8\\.net/html/tv/[\\w]+/list_\\d+_\\d+.html
     * 
     */
    @Override
    public void process(Page page) {

        //1. 如果是用户列表页面 【入口页面】，将所有用户的详细页面的url放入target集合中。
        if(page.getUrl().regex("http://www\\.ygdy8\\.net/plus/sitemap\\.html").match()){
            page.addTargetRequests(page.getHtml().xpath("//table[@bgcolor='#CEDD9B']//").links().regex(".*/html/tv/.*").all());
            	
        //资源列表					
    	}else if(page.getUrl().regex("http://www\\.ygdy8\\.net/html/tv/[\\w]+/index\\.html").match()){
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/div[@class='x']").links().all());
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/ul").links().all());
    	//资源列表					
        }else if(page.getUrl().regex("http://www\\.ygdy8\\.net/html/tv/[\\w]+/list_\\d+_\\d+\\.html").match()){
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']//a").links().regex(".*/html/tv/.*/[\\d]{8}/.*").all());
        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/div[@class='x']").links().all());
        //2. 如果是用户详细页面
        }else if(page.getUrl().regex("http://www\\.ygdy8\\.net/html/tv/[\\w]+/\\d{8}/\\d+\\.html").match()){
        	//判断电影类型
        	String category = findDyType(page.getUrl().toString(),"tv");
        	String publishDate = findDyType(page.getUrl().toString(),category);
        	
            num++;//电影数++
            /*实例化ZhihuUser，方便持久化存储。*/
            YgdyArticle article = new YgdyArticle();
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            String dyName = page.getHtml().xpath("//title/text()").get();
            String content = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']").get();
            //String url = page.getHtml().xpath("//div[@class='co_content8']//div[@id='Zoom']/span/table//a").links().get();
            String url = "";
            
            //对象赋值
            article.setDyName(dyName);
            article.setDomain("www.ygdy8.net");
            article.setCategory(category);
            article.setContent(content); 
            article.setUrl(url);
            article.setPublishDate(publishDate);

            //System.out.println("num:"+num +" " + article.toString());//输出对象
            ygdyDao.saveArticle(article,"tv");//保存用户信息到数据库
        }
    }

    /**
     * 
     * @param url		匹配的网址
     * @param aftername 匹配名称后面 的一个
     * @return
     */
    private String findDyType(String url, String aftername) {
    	 //String url = "http://www.ygdy8.net/html/tv/dyzz/index.html";  
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
        System.out.println("========豆瓣信息小爬虫【启动】喽！=========");
        startTime = new Date().getTime();
        Spider.create(new DoubanPageProcessor()).addUrl("http://www.ygdy8.net/plus/sitemap.html")
        .setScheduler(new FileCacheQueueScheduler("c:/data/webmagic/douban"))
        .thread(5).run();
        endTime = new Date().getTime();
        System.out.println("========豆瓣小爬虫【结束】喽！=========");
        System.out.println("一共爬到"+num+"个用户信息！用时为："+(endTime-startTime)/1000+"s");
    }
}