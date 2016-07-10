package com.thinkgem.jeesite.modules.crawler.webmagic.dyResource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thinkgem.jeesite.modules.crawler.DBEXEMport.Gndy;

/**
 * 知乎 数据库持久化接口 实现
 * @author 甘海彬
 *
 */
public class YgdyDaoImpl implements YgdyDao{
	
    @Override
    public int saveArticle(YgdyArticle article, String type) {
        DBHelper dbhelper = new DBHelper();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO cms_yg_" + type + " ( `domain`,`category`,`name`,`content`,`url`,`publishDate`)")
        //`key`,`name`,identity,location,profession,sex,school,major,recommend,picUrl,agree,thanks,ask,answer,article,collection
        .append("VALUES (? , ? , ? , ? , ?, ? ) ");
        //设置 sql values 的值
        List<String> sqlValues = new ArrayList<>();
        sqlValues.add(article.getDomain());
        sqlValues.add(article.getCategory());
        sqlValues.add(article.getDyName());
        sqlValues.add(article.getContent());
        sqlValues.add(article.getUrl());
        sqlValues.add(article.getPublishDate());
        int result = dbhelper.executeUpdate(sql.toString(), sqlValues);
        return result;
    }

    /**
     * 返回0：失败
     * 返回1：成功保存一条
     * @throws SQLException 
     */
	@Override
	public int updateArticle(YgdyArticle article, String type, String tablePre) throws SQLException {
		
		//默认值
		if("".equals(tablePre)){
			tablePre = "cms_yg_";
		}
		
		DBHelper dbhelper = new DBHelper();
        StringBuffer sql = new StringBuffer();
        sql.append("select count(1) c from "+ tablePre + type + " where name = ? or url=?");
        List<String> sqlVal = new ArrayList<>();
        sqlVal.add(article.getDyName());
        sqlVal.add(article.getUrl());
        ResultSet lastDate = dbhelper.executeQuery(sql.toString(), sqlVal);
        if(lastDate.next()) {
        	if(lastDate.getInt("c") > 0)
        		return 0; 
        }
        
        return saveArticle(article, type);
	}

	@Override
	public Date getLastDate(String type) throws SQLException {
 		DBHelper dbhelper = new DBHelper();
        StringBuffer sql = new StringBuffer();
        sql.append("select recorddate from cms_recordlasttime where typename=? ");
        List<String> sqlValues = new ArrayList<>();
        sqlValues.add(type);
        ResultSet lastDate = dbhelper.executeQuery(sql.toString(), sqlValues);
        if(lastDate.next()){
        	return lastDate.getDate("recorddate");
        }
        return null;
	}

	@Override
	public void setLastDate(String type, java.util.Date date) throws SQLException {
		DBHelper dbhelper = new DBHelper();
        StringBuffer sql = new StringBuffer();
        sql.append("update  cms_recordlasttime set recorddate = ? where typename=? ");
        List<String> sqlValues = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        sqlValues.add(simpleDateFormat.format(date));
        sqlValues.add(type);
        
        dbhelper.executeUpdate(sql.toString(), sqlValues);
	}

	@Override
	public List<Gndy> getGndys(String table, String type, String datetime) throws SQLException {
		List<Gndy> lists = new ArrayList<Gndy>();
		
		DBHelper dbhelper = new DBHelper();
        StringBuffer sql = new StringBuffer();
        sql.append("select name,content,url,publishDate from ? where  publishDate> ? ");
        List<String> sqlValues = new ArrayList<>();
        sqlValues.add(table);
        sqlValues.add(datetime);
        
        ResultSet rs = dbhelper.executeQuery(sql.toString(), sqlValues);
        while(rs.next()){
        	Gndy dy = new Gndy();
        	dy.setName(rs.getString("name"));
        	dy.setContent(rs.getString("content"));
        	dy.setUrl(rs.getString("url"));
        	dy.setPublishDate(rs.getString("publishDate"));
        	
        	lists.add(dy);
        }
		return lists;
	}

	@Override
	public int insertIMDBTop250(String... strs) throws SQLException {
		
		DBHelper dbhelper = new DBHelper();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO cms_top_rated  ( `namezh`,`nameen`,`nameothers`,`onyear`,`rate`,`sorted`,`rate_users`,`img_url`,`publishTime`,`type`)")
        //`key`,`name`,identity,location,profession,sex,school,major,recommend,picUrl,agree,thanks,ask,answer,article,collection
        .append("VALUES (? , ? , ? , ? , ?, ?,?,?,?,? ) ");
        //设置 sql values 的值
        List<String> sqlValues = new ArrayList<>();
        for(String str : strs){
        	
        	sqlValues.add(str);
        }
        int result = dbhelper.executeUpdate(sql.toString(), sqlValues);
        return result;
	}

	@Override
	public int insertDoubanTop(String... strs) throws SQLException {
		DBHelper dbhelper = new DBHelper();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO cms_top_rated  ( `namezh`,`nameothers`,`onyear`,`rate`,`rate_users`,`img_url`,`publishTime`,`type`,`movie_info`,`comment`)")
        //`key`,`name`,identity,location,profession,sex,school,major,recommend,picUrl,agree,thanks,ask,answer,article,collection
        .append("VALUES (?,? , ? , ? , ? , ?, ?,?,?,? ) ");
        //设置 sql values 的值
        List<String> sqlValues = new ArrayList<>();
        for(String str : strs){
        	
        	sqlValues.add(str);
        }
        int result = dbhelper.executeUpdate(sql.toString(), sqlValues);
        return result;
	}
}