package com.thinkgem.jeesite.modules.crawler.DBEXEMport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.common.index.LuceneUtil;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.entity.ArticleData;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.DBHelper;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.NewIncreMain;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyDao;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyDaoImpl;

/**
 * 处理cms_top_rated表中，给加上cms_article的关联
 * @author sdd
 * 
 * 
 *  
 *
 */
//@Component("dBEXEMport")
//@Service
//@Lazy(false)
public class TopHandlerId {
	
	private static Logger logger = LoggerFactory.getLogger(TopHandlerId.class);
	
	
	
	public TopHandlerId(){ }
	
	DBHelper dbhelper = new DBHelper();
	YgdyDao ygdyDao = new YgdyDaoImpl();
	
	public void executScheduled(){
		logger.info("-------enter executScheduled--------------");
		
		StringBuffer sql = new StringBuffer();
		sql.delete(0, sql.toString().length());//清空
        sql.append("select namezh from cms_top_rated where film_id is null");
        List<String> sqlValues = new ArrayList<>();	
        List<String> namezhs = new ArrayList<String>();
        ResultSet rs = dbhelper.executeQuery(sql.toString(), sqlValues);
        try {
			while(rs.next()){
				String namezh = rs.getString("namezh");
				namezhs.add(namezh);				
			}
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        
    	sql.delete(0, sql.toString().length());//清空
        sql.append("select id, title from cms_article");
        List<String> sqlValues2 = new ArrayList<>();	
        
        int i=1;
        ResultSet rs2 = dbhelper.executeQuery(sql.toString(), sqlValues2);
        try {
			while(rs2.next()){
				String id = rs2.getString("id");
				String titleRaw = rs2.getString("title");
				if(titleRaw.indexOf("《") == -1 || titleRaw.indexOf("》") == -1){
					System.out.println(titleRaw+"====");
					continue;
				}
				String title = titleRaw.substring(titleRaw.indexOf("《")+1,titleRaw.indexOf("》"));
				
				for(String str : namezhs){
					if(title.equals(str)){
						System.out.println("----------------------" + i++);
						System.out.println(id+", "+title + ", " + str);
						
						//更新id
						sql.delete(0, sql.toString().length());//清空
				        sql.append("update cms_top_rated set film_id=? where namezh=?");
				        List<String> sqlValues3 = new ArrayList<>();	
				        sqlValues3.add(id);
				        sqlValues3.add(str);
				        
				        dbhelper.executeUpdate(sql.toString(), sqlValues3);
					}
				}
			}
        }catch(Exception ex){
        	ex.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		
		TopHandlerId db = new TopHandlerId();
		db.executScheduled();
	}
	
}
