package com.thinkgem.jeesite.modules.crawler.webmagic.filmsInfo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * IMDB Top 250 相关的电影信息
 * @author sdd
 *
 */
public class IMDBTop250Processor implements PageProcessor{
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

        if(page.getUrl().regex("http://www\\.imdb\\.com/chart/top/\\?sort=us\\,desc&mode=simple&page=1").match()){
        	
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            List<String> names = page.getHtml().xpath("//tbody[@class='lister-list']//td[@class='titleColumn']/a/text()").all();
            List<String> sorteds = page.getHtml().xpath("//tbody[@class='lister-list']//td[@class='titleColumn']/text()").all();
            List<String> years = page.getHtml().xpath("//tbody[@class='lister-list']//td[@class='titleColumn']/span[@class='secondaryInfo']/text()").all();
            List<String> rateds = page.getHtml().xpath("//tbody[@class='lister-list']//td[@class='imdbRating']/strong[@title]/text()").all();
            List<String> ratedAndUsers = page.getHtml().xpath("//tbody[@class='lister-list']//td[@class='imdbRating']/strong/@title").all();
            List<String> imgSrcs = page.getHtml().xpath("//tbody[@class='lister-list']//img/@src").all();
            //`name`,`onyear`,`rate`,`sorted`,`rate_users`,`publishTime`,`type`
            for(int i=0; i<250; i++){
            	
            	String userCount = "";
            	String[] strs = ratedAndUsers.get(i).split(" ");
            	for(String str : strs){
            		if(str.contains(",")){
            			userCount =  str.replaceAll(",", "");
                        break;
            		}
            	}
            	
            	try {
    				int result = ygdyDao.insertIMDBTop250(names.get(i),years.get(i), rateds.get(i),sorteds.get(i), userCount, imgSrcs.get(i),"20160703","IMDBTop250");
    					
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
        System.out.println("==http://www.imdb.com/======信息首页小爬虫【启动】喽！=========");
        Spider.create(new IMDBTop250Processor()).addUrl(
        		"http://www.imdb.com/chart/top/?sort=us,desc&mode=simple&page=1")
        .thread(1).run();
        System.out.println("==http://www.imdb.com/======首页小爬虫【结束】喽！=========");
        
    }
}