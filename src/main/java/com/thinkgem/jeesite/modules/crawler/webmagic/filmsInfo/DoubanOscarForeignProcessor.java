package com.thinkgem.jeesite.modules.crawler.webmagic.filmsInfo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thinkgem.jeesite.common.utils.StringUtils;
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
 * 豆瓣电影 Top 250 的相关信息
 * @author sdd
 *
 */
public class DoubanOscarForeignProcessor implements PageProcessor{
    //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setCharset("utf-8").setRetryTimes(10).setTimeOut(10000).setSleepTime(1000)
    		.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
    		;
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
        if(page.getUrl().regex("https://www\\.douban\\.com/doulist/22479/\\?start=\\d+&sort=time&sub_type=").match()){
            page.addTargetRequests(page.getHtml().xpath("//div[@class='paginator']/a").links().all());
            	
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            List<String> imgSrcs = page.getHtml().xpath("//div[@class='doulist-subject']//img/@src").all();
            List<String> titles = page.getHtml().xpath("//div[@class='doulist-subject']//div[@class='title']/a/text()").all();
            List<String> rateds = page.getHtml().xpath("//div[@class='doulist-subject']//div[@class='rating']/span[@class='rating_nums']/text()").all();
            List<String> ratedAndUsers = page.getHtml().xpath("//div[@class='doulist-subject']//div[@class='rating']/span[3]/regex('\\d+')").all();
            
            List<String> abstracts = page.getHtml().xpath("//div[@class='doulist-subject']//div[@class='abstract']/text()").all();
            List<String> years = page.getHtml().xpath("//div[@class='doulist-subject']//div[@class='abstract']/regex('[\\d]{4}')").all();
            
            List<String> comment = page.getHtml().xpath("//div[@class='ft']//blockquote[@class='comment']/text()").all();
            
            //`namezh`,`onyear`,`rate`,`rate_users`,`img_url`,`publishTime`,`type`,`movie_info`,`comment`
            for(int i=0; i<titles.size(); i++){
            	try {
    				ygdyDao.insertDoubanTop(
    						titles.get(i).trim().split(" ")[0],//中文名
    						titles.get(i).trim().substring(titles.get(i).trim().split(" ")[0].length()+1),//其他名称
    						years.get(i), 
    						StringUtils.isBlank(rateds.get(i))?"0":rateds.get(i),
							ratedAndUsers.get(i).replace("(", "").replace(")", ""),
							imgSrcs.get(i), 
							"20160709",
							"OscarForeign",
							abstracts.get(i).trim(),
							comment.get(i).trim()
    						);
    				
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
        Spider.create(new DoubanOscarForeignProcessor()).addUrl(
        		"https://www.douban.com/doulist/22479/?start=0&sort=time&sub_type=")
        .thread(1).run();
        System.out.println("== ==== 小爬虫【结束】喽！=========");
        
    }
}