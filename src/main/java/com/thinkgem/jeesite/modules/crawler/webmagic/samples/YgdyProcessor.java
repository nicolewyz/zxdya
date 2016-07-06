package com.thinkgem.jeesite.modules.crawler.webmagic.samples;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author code4crafter@gmail.com <br>
 */
public class YgdyProcessor implements PageProcessor {

    public static final String URL_LIST = "http://blog\\.sina\\.com\\.cn/s/articlelist_1487828712_0_\\d+\\.html";
    //首页
    public static final String URL_INDEX = "http://www.ygdy8.net/index.html";
    //列表页
    public static final String URL_LIST_1 = "http://www\\.ygdy8\\.net/html/\\w+/\\w+/list_23_\\d+\\.html";

    public static final String URL_POST = "http://www\\.ygdy8\\.net/html/\\w+/\\w+/\\d+/\\d+\\.html";

    private Site site = Site
            .me()
            .setDomain("blog.sina.com.cn")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        //列表页
        if (page.getUrl().regex(URL_LIST_1).match() || "http://www.ygdy8.net/index.html".equals(page.getUrl())) {
        	System.out.println("===="+page.getUrl());
            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"co_content2\"]//a").links().regex(URL_POST).all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST_1).all());
            //文章页
        } else {
        	System.out.println("=else==="+page.getUrl());
            page.putField("title", page.getHtml().xpath("//div[@class='title_all']/h1/font/text()"));
            //page.putField("content", page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalContent']"));
            page.putField("date", page.getHtml().xpath("//div[@class=\"co_content8\"]/ul/"));
            page.putField("content", page.getHtml().xpath("//div[@id='Zoom']/").replace("http://www.ygdy8.net/", "http://www.zxdya.com/").replace("html/gonggao/20070323/483.html", ""));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new YgdyProcessor()).addUrl("http://www.ygdy8.net/html/gndy/dyzz/list_23_1.html")
                .run();
    }
}
