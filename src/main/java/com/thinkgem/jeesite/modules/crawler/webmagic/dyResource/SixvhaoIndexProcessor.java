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
public class SixvhaoIndexProcessor implements PageProcessor{
    //抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setCharset("gb2312").setRetryTimes(10).setTimeOut(10000).setSleepTime(1000);
    //爬虫电影数量
    private static int num = 1;
    //数据库持久化对象，用于将用户信息存入数据库
    private YgdyDao ygdyDao = new YgdyDaoImpl();

    @Override
    public void process(Page page) {

        //1. 如果是用户列表页面 【入口页面】，将所有用户的详细页面的url放入target集合中。
        if(page.getUrl().regex("http://www\\.6vhao\\.com/\\w+/index[_\\d]*.html").match()){
            page.addTargetRequests(page.getHtml().xpath("//div[@class='listpage']//").links().all());
            page.addTargetRequests(page.getHtml().xpath("//ul[@class='list']//").links().all());
            	
//        //资源列表					
//    	}else if(page.getUrl().regex("http://www\\.ygdy8\\.net/html/gndy/[\\w]+/index\\.html").match()){
//        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/div[@class='x']").links().all());
//        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/ul").links().all());
//    	//资源列表					
//        }else if(page.getUrl().regex("http://www\\.ygdy8\\.net/html/gndy/[\\w]+/list_\\d+_\\d+\\.html").match()){
//        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']//a").links().regex(".*/html/gndy/.*/[\\d]{8}/.*").all());
//        	page.addTargetRequests(page.getHtml().xpath("//div[@class='co_content8']/div[@class='x']").links().all());
        //2. 如果是用户详细页面		//http://www.6vhao.com/dy/2016-06-18/27366.html
        }else if(page.getUrl().regex("http://www\\.6vhao\\.com/\\w+/\\d{4}-\\d{2}-\\d{2}/\\w+\\.html").match()){
        	//判断电影类型
        	String category = findDyType(page.getUrl().toString(),"www.6vhao.com");
        	String publishDate = findDyType(page.getUrl().toString(),category).replaceAll("-", "");
        	
            /*实例化ZhihuUser，方便持久化存储。*/
            YgdyArticle article = new YgdyArticle();
            /*从下载到的用户详细页面中抽取想要的信息，这里使用xpath居多*/
            /*为了方便理解，抽取到的信息先用变量存储，下面再赋值给对象*/
            String dyName = page.getHtml().xpath("//div[@id='main']//div[@class='box']//h1/text()").get();
//            String content1 = page.getHtml().xpath("//div[@id='main']//div[@id='endText']/p[1]/html()").get();
//            String content2 = page.getHtml().xpath("//div[@id='main']//div[@id='endText']/p[2]/tidyText()").get();
//            String content3 = page.getHtml().xpath("//div[@id='main']//div[@id='endText']/p[3]/text()").get();
//            String content4 = page.getHtml().xpath("//div[@id='main']//div[@id='endText']/p[4]/tidyText()").get();
            StringBuilder content = new StringBuilder(2000);
            
//            Matcher m = Pattern.compile("\n").matcher(string);
//            String str = m.replaceAll("");
            
            List<String> contentImgs = page.getHtml().xpath("//div[@id='main']//div[@id='endText']/p/img/outerHtml()").all();
            if(contentImgs.size()==0){
            	contentImgs = page.getHtml().xpath("//div[@id='main']//div[@id='endText']//img/outerHtml()").all();
            }
            List<String> contentIntro = page.getHtml().xpath("//div[@id='main']//div[@id='endText']/p/tidyText()").all();
            if(contentIntro.size()==0){
            	contentIntro = page.getHtml().xpath("//div[@id='main']//div[@id='endText']/p/tidyText()").all();
            }
            
            //默认第一张图片放最开始
//            content.append("<pre>");
            content.append(contentImgs.size()>0?contentImgs.get(0).replaceAll("alt=\"\"", "alt=\"电影东东\""):"");
            
            //替换相关的url
            Pattern pattern = Pattern.compile("<.+?>|http\\:\\/\\/.+", Pattern.DOTALL);
            Matcher matcher = null;
            //文字内容
            for(String c : contentIntro){
            	
            	matcher = pattern.matcher(c);
            	content.append(matcher.replaceAll("").replaceAll("【下载地址】", ""));
            	content.append("\n");
            }
            //图片
            for(int i = 1; i < contentImgs.size(); i++){
            	content.append(contentImgs.get(i) );
            	content.append("\n");
            }
//            content.append("</pre>");
            //System.out.println(sb.toString().replaceAll("(?m)^\\s*$" + System.lineSeparator(), ""));
            
            //下载地址
            StringBuilder urls = new StringBuilder(2000);
//            urls.append("<pre>");
            
            //urls.append("【下载地址】\n");
            List<String> url = page.getHtml().xpath("//div[@id='main']//div[@id='endText']//table/tbody//td/html()").all();
            
            //ed2k://|file|%E9%AD%94%E5%85%BD.%E9%9F%A9%E7%89%88.HD1280%E8%B6%85%E6%B8%85%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97.mp4|2724866096|737520BECBD0BB60ADC5AF1702EF2CC7|h=MJYYOKHVPFZTHPAVBYMUQCJC2FT25VAC|/
            //ed2k://|file|%E9%AD%94%E5%85%BD(%E9%9F%A9%E7%89%88).720p.HD%E4%B8%AD%E8%8B%B1%E5%8F%8C%E5%AD%97[www.66ys.tv].mp4|2044254966|CC02D8907F97AED4828D8A4C14C7AC25|h=HLZZ6CN5SSWUZ4M6QDI3PTLPVR3SLIQO|/
            //ed2k://|file|忍者神龟：变种时代.BD1280超清国英双语中英双字.mp4|2577949605|CE6016500409BF19D8765C274CAF91E8|h=PCVMYCDL2EVKMJVMEZT5OFJLI2R2WE3N|/
            //magnet:?xt=urn:btih:8164e3b08c75c2b24fddbe9dec87ef4b0b4eae52&dn=%5B%E6%B5%B7%E6%B4%8B%E6%B7%B1%E5%A4%84%5D.In.The.Heart.Of.The.Sea.2015.3D.BluRay.1080p.HSBS.x264.TrueHD7.1-CMCT&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80%2Fannounce&tr=http%3A%2F%2Ftracker.cmct.cc%3A2710%2Fannounce
            //magnet:?xt=urn:btih:c677241fb37c1c906ac44bae6eb4f4217368b88a&amp;dn=Point.Break.2015.1080p.3D.BluRay.Half-SBS.x264.DTS-HD.MA.7.1-RARBG&amp;tr=http%3A%2F%2Ftracker.trackerfix.com%3A80%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.me%3A2710%2Fannounce&amp;tr=udp%3A%2F%2F9.rarbg.to%3A2710%2Fannounce
            //thunder://QUFodHRwOi8vYnQuMnR1LmNjLzQ5QjM4MzQ5QzI4NTM4NDQzNDEyOEM4NzVDOTFBNjAyNzU3QzlDQkUvyaW80tauxa4uQkQxMjgws6zH5dbQ06LLq9fWLm1wNFpa
            
            Pattern patt= Pattern.compile("(ed2k://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：]+)|(thunder://[\\|\\w\\(\\)\\.\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：]+)|(magnet:\\?[\\|\\w\\(\\)\\.\\?\\&\\%\\[\\]\\/\\=\u4E00-\u9FA5\\:\\：\\-\\;]+)",Pattern.DOTALL);
            
            List<String> ed2ks = new ArrayList<String>();
            Matcher matc = patt.matcher(page.getRawText());
            while (matc.find()) {
            	ed2ks.add(matc.group());
            }
            
            //把所有的url替换回来
            int j = 0;	
            for(String u : url){
            	
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
                        
            //对象赋值
            article.setDyName(dyName);
            article.setDomain(page.getUrl().toString());
            article.setCategory(category);
            
            //去掉所有空行
            article.setContent(content.toString().replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "")); 
            article.setUrl(urls.toString());
            article.setPublishDate(publishDate);

            System.out.println("--------------------num:"+ num++ );//输出对象
            ygdyDao.saveArticle(article,"6v");//保存电影信息到数据库
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
        System.out.println("========6v电影信息小爬虫【启动】喽！=========");
        startTime = new Date().getTime();
        //入口为：【https://www.zhihu.com/search?type=people&q=xxx 】，其中xxx 是搜索关键词
//        Spider.create(new SixvhaoPageProcessor()).addUrl("http://www.6vhao.com/dy/index.html")
//        //.setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(1000000)) )
//        .setScheduler(new FileCacheQueueScheduler("c:/data/webmagic/6vhao"))
//        .thread(5).run();
        Spider.create(new SixvhaoIndexProcessor()).addUrl(
        		"http://www.6vhao.com/jddy/2009-03-15/6346.html"
        		
        		
        		)
        .thread(1).run();
        endTime = new Date().getTime();
        System.out.println("========6v电影小爬虫【结束】喽！=========");
        System.out.println("一共爬到"+num+"个电影信息！用时为："+(endTime-startTime)/1000+"s");
    }
}