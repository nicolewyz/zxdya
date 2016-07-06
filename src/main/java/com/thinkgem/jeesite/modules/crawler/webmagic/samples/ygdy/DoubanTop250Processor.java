package com.thinkgem.jeesite.modules.crawler.webmagic.samples.ygdy;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
 * 豆瓣电影 Top 250 的相关信息
 * @author sdd
 *
 */
public class DoubanTop250Processor implements PageProcessor{
    //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setCharset("utf-8").setRetryTimes(10).setTimeOut(10000).setSleepTime(1000);
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

        //1. 如果是用户列表页面 【入口页面】，将所有用户的详细页面的url放入target集合中。
        if(page.getUrl().regex("https://movie\\.douban\\.com/top250\\?start=\\d+&filter=").match()){
            page.addTargetRequests(page.getHtml().xpath("//div[@class='paginator']/a").links().all());
            	
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            List<String> nameZhs = page.getHtml().xpath("//ol[@class='grid_view']//div[@class='info']//a/span[1]/text()").all();
            List<String> nameEns = page.getHtml().xpath("//ol[@class='grid_view']//div[@class='info']//a/span[2]/text()").all();
            List<String> nameOtherss = page.getHtml().xpath("//ol[@class='grid_view']//div[@class='info']//a/span[3]/text()").all();
            List<String> sorteds = page.getHtml().xpath("//ol[@class='grid_view']//em/text()").all();
            List<String> years = page.getHtml().xpath("//ol[@class='grid_view']//div[@class='bd']/p").regex("\\d{4}").all();
            List<String> rateds = page.getHtml().xpath("//ol[@class='grid_view']//div[@class='star']/span[@class='rating_num']/text()").all();
            List<String> ratedAndUsers = page.getHtml().xpath("//ol[@class='grid_view']//div[@class='star']/span[4]/text()").regex("\\d+").all();
            List<String> imgSrcs = page.getHtml().xpath("//ol[@class='grid_view']//div[@class='pic']//img/@src").all();
            
            //`name`,`onyear`,`rate`,`sorted`,`rate_users`,`publishTime`,`type`
            for(int i=0; i<25; i++){
            	try {
    				ygdyDao.insertIMDBTop250(nameZhs.get(i).trim(),nameEns.get(i).replaceAll("/", "").trim(),nameOtherss.size()<=i?"":nameOtherss.get(i),years.get(i), rateds.get(i),sorteds.get(i), ratedAndUsers.get(i),imgSrcs.get(i), "20160703","DoubanTop250");
    				
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
            }
        }
    }


	@Override
    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args) {
        System.out.println("== ===== 小爬虫【启动】喽！=========");
        Spider.create(new DoubanTop250Processor()).addUrl(
        		"https://movie.douban.com/top250?start=0&filter=")
        .thread(1).run();
        System.out.println("== ==== 小爬虫【结束】喽！=========");
        
    }
}