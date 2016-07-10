package com.thinkgem.jeesite.modules.crawler.webmagic.dyResource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.thinkgem.jeesite.modules.crawler.DBEXEMport.Gndy;

/**
 * 数据持久化 接口
 * @author 甘海彬
 *
 */
public interface YgdyDao {
	
    /**
     * 全量爬虫时
     * @param user
     * @param type
     * @return
     */
    public int saveArticle(YgdyArticle user, String type);
    
    /**
     * 增量爬虫时，需要进行时间的判断
     * @param type
     * @return
     */
    public Date getLastDate(String type) throws SQLException;
    
    /**
     * 增量爬虫时，需要进行时间的更新
     * @param type
     * @return
     */
    public void setLastDate(String type,  Date date) throws SQLException;
    
    /**
     * 增量爬虫时，其实就是增量保存
     * @param user
     * @param type
     * @param tablePre
     * @return
     */
    public int updateArticle(YgdyArticle article, String type, String tablePre)  throws SQLException ;
    
    /**
     * 获取数据中指定类别，指定时间的电影信息
     * @param table
     * @param type
     * @param datetime
     * @return
     */
    public List<Gndy> getGndys(String table, String type, String datetime) throws SQLException;
    
    /**
     * 插入IMDBTop250相关的数据
     * @param user
     * @param type
     * @param tablePre
     * @return
     */
    public int insertIMDBTop250(String...strings )  throws SQLException ;
    
    /**
     * 插入如历年最佳外语电影等
     * @param strings
     * @return
     * @throws SQLException
     */
    public int insertDoubanTop(String...strings )  throws SQLException ;
    
}