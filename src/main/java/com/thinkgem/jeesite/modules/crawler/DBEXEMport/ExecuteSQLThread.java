package com.thinkgem.jeesite.modules.crawler.DBEXEMport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.common.index.DocException;
import com.thinkgem.jeesite.common.index.IndexWriterCloseException;
import com.thinkgem.jeesite.common.index.LuceneUtil;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.entity.ArticleData;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.DBHelper;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.MovieTypeConstants;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyDao;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyDaoImpl;

public class ExecuteSQLThread /*implements Runnable*/ {
	
	private static Logger logger = LoggerFactory.getLogger(ExecuteSQLThread.class);

	private String type = "";
	private String toCategory = "";
	private String fromCategory = "";

	private static String[] otherName = new String[]{"电影天堂","阳光电影"};
	private static String[] crawlDyName = new String[]{"dy","gndy","zydy","movie"};
	
	DBHelper dbhelper = new DBHelper();
	YgdyDao ygdyDao = new YgdyDaoImpl();
	
	public ExecuteSQLThread(){ }
	
	public ExecuteSQLThread(String type){ 
		this.type = type;
	}
	
	
//	@Override
	public void run() {
		
        StringBuffer sql = new StringBuffer();
        String lastDate = "";
    	try {
			Date date = ygdyDao.getLastDate("resource2article_" + type);
			SimpleDateFormat simpledf = new SimpleDateFormat("yyyyMMdd");
			lastDate = simpledf.format(date);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	sql.delete(0, sql.toString().length());//清空
        sql.append("select domain,category,name,content,url,publishDate from cms_resouce_"+type+" where  publishDate>=? order by publishDate");
        //设置 sql values 的值
        List<String> sqlValues = new ArrayList<>();	
        sqlValues.add(lastDate);
        
        //List<Gndy> lists = new ArrayList<Gndy>();
        List<Article> articles = new ArrayList<Article>();
        List<ArticleData> articleDatas = new ArrayList<ArticleData>();
        List<Gndy> gndys = new ArrayList<Gndy>();
        
        String dateCount = "2000-01-01";	//计数开始的临时时间
        int i = 1;
        
        ResultSet rs = dbhelper.executeQuery(sql.toString(), sqlValues);
        try {
			while(rs.next()){
				
				//logger.info("-------enter executSQL while rs--------------");
				
				//判断名称是否已经存在，若存在，continue
		        StringBuffer sqlCount = new StringBuffer();
		        sqlCount.delete(0, sql.toString().length());//清空
		        sqlCount.append("select count(1) c from  cms_article  where title =?");
		        List<String> sqlValCount = new ArrayList<>();
		        sqlValCount.add(replaceString(rs.getString("name")));
		        ResultSet hasDy = dbhelper.executeQuery(sqlCount.toString(), sqlValCount);
		        if(hasDy.next()) {
		        	if(hasDy.getInt("c") > 0)
		        		continue;
		        }
        
				
				
				Gndy gndy = new Gndy();
				gndy.setCategory(rs.getString("category"));
				gndy.setDomain(rs.getString("domain"));
				
				String t = replaceString(rs.getString("content")==null?"":rs.getString("content").replace("'", "\"")
						.replaceAll("<a", "<a rel=\"nofollow\"").replaceAll("<img", "<img alt=\"电影东东\"").replaceAll("\"", "\'"));
				gndy.setContent("tv".equals(type) ? (rs.getString("content")==null?"":rs.getString("content").replace("'", "\"")
						.replaceAll("<a", "<a rel=\"nofollow\"").replaceAll("<img", "<img alt=\"电影东东\"").replaceAll("\"", "\'")): t);
				
				gndy.setName(replaceString(rs.getString("name")));
				
				//只有在是电影类型的时候，若不存在《》，则无法取真正的标题，因此过滤
				if(! ( gndy.getName().contains("《") && gndy.getName().contains("》")) ) {
					
					for(String dyName : crawlDyName){
						
						if(dyName.equals(gndy.getCategory())) continue;
					}
					
					
				}
				
				gndy.setUrl(rs.getString("url"));
				gndy.setPublishDate(rs.getString("publishDate"));
				
				
				//lists.add(gndy);
				//计算id用到的时间
				if(! dateCount.equals(gndy.getPublishDate().substring(0,10).replace("-", ""))){
					dateCount = gndy.getPublishDate().substring(0,10).replace("-", "");
					i = 1;
				}
				
				//赋值到另外两张表中
				System.out.println(rs.getString("category")+"-------"+rs.getString("domain"));
				String fromToCategory = MovieTypeConstants.typeMap.get("TYPE_"+type.toUpperCase()+"_" + rs.getString("category"));
				String id = dateCount+ getEveryId(i++, fromToCategory, 4);
				
				//判断id是否存在在库中，若存在，则查询日期内的最大值
				sqlCount.delete(0, sql.toString().length());//清空
		        sqlCount.append("select max(id) c from  cms_article  where id like ?");
		        List<String> sqlVal_ = new ArrayList<>();
		        
		        sqlVal_.add(dateCount + fromToCategory + "%");
		        ResultSet hasDyid_ = dbhelper.executeQuery(sqlCount.toString(), sqlVal_);
		        if(hasDyid_.next()) {
		        	id = (hasDyid_.getLong("c")==0 ? Long.parseLong(id) : (hasDyid_.getLong("c")+ 1))   + "";
		        }
				
				
				sql.delete(0, sql.toString().length());//清空
				sql.append("insert into cms_article_data values(?,?,?,'',1,?)");
		        //设置 sql values 的值
		        List<String> sqlValuesArticleData = new ArrayList<>();
				sqlValuesArticleData.add(id);
				sqlValuesArticleData.add(gndy.getContent());
				sqlValuesArticleData.add(gndy.getDomain());
				sqlValuesArticleData.add(gndy.getUrl());
				dbhelper.executeUpdate(sql.toString(), sqlValuesArticleData);
				//System.out.println("insert cms_article_data one...");
				
				sql.delete(0, sql.toString().length());//清空
				sql.append("insert into cms_article values(?,'"+ fromToCategory +"',?,?,'','','','电影东东,最新电影下载，最新电视剧下载，高清电影下载，免费电影下载,3D电影,动漫电影下载,游戏下载','电影东东',0,null,1,'','','',1,?,1,?,null,0)");
		        //设置 sql values 的值
		        List<String> sqlValuesArticle = new ArrayList<>();
		        sqlValuesArticle.add(id);
		        //sqlValuesArticle.add(gndy.getCategory());
		        sqlValuesArticle.add(gndy.getName());
		        //System.out.println(gndy.getName());
		        //取真正的名字 ， 《》里的名字
		        String realName = (gndy.getName().contains("《") && gndy.getName().contains("》"))
		        		? gndy.getName().substring(gndy.getName().indexOf("《")+1,gndy.getName().indexOf("》")) : gndy.getName();
		        sqlValuesArticle.add(realName);
		        sqlValuesArticle.add(gndy.getPublishDate());
		        sqlValuesArticle.add(gndy.getPublishDate());
		        
		        dbhelper.executeUpdate(sql.toString(), sqlValuesArticle);
		        //System.out.println("insert cms_article one...");
		        
		        //更新到gndy，下面生成索引
		        gndy.setId(id);
		        
		        gndys.add(gndy);
			}
			
			//添加到索引
			try {
				logger.info( "------create index start >>>>>>>>>>>>>>>>" +type);
				//LuceneUtil luceneUtil = new LuceneUtil();
				if(gndys != null && gndys.size()>0){
					LuceneUtil util = new LuceneUtil();
					util.addIndex(gndys);
				}
				logger.info( "------create index end <<<<<<<<<<<<<<<<<<"+type);
			} catch (IndexWriterCloseException e1) {
				e1.printStackTrace();
			} catch (DocException e1) {
				e1.printStackTrace();
			}
			
			//主动推送百度
//			PushBaidu pushBaidu = new PushBaidu();
//			for(Gndy g : gndys){
//				
//				pushBaidu.call(g.getId());
//			}
			
			
			//更新toArticle的时间
	        YgdyDao ygdyDao = new YgdyDaoImpl();
			try {
				ygdyDao.setLastDate("resource2article_" + type, new Date());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 去掉第三方的名称
	 * @param rs   
	 * @param str  :name还是content
	 * @return
	 * @throws SQLException
	 */
	private String replaceString(String nameNew) throws SQLException {
		for(String name:otherName){
			nameNew = nameNew.replaceAll(name, "电影东东");
		}
		return nameNew;
	}
	
	/**
	 * 获取length位的id，第一位为category
	 * @param id
	 * @param category
	 * @param length
	 * @return
	 */
	public  static String getEveryId(int id, String category, int length){
		
		String ids = String.valueOf(id);
		if(ids.length()>=length){
			return ids.substring(ids.length() - length , ids.length());
		}else{
			String reStr =  category;
			for(int i=0; i < length - ids.length() - 1; i++){
				reStr += "0";
			}
			reStr += ids;
			return reStr;
		}
		
	}

}
