package com.thinkgem.jeesite.modules.crawler.DBEXEMport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.common.index.LuceneUtil;
import com.thinkgem.jeesite.modules.crawler.webmagic.samples.ygdy.DBHelper;
import com.thinkgem.jeesite.modules.crawler.webmagic.samples.ygdy.NewIncreMain;
import com.thinkgem.jeesite.modules.crawler.webmagic.samples.ygdy.YgdyDao;
import com.thinkgem.jeesite.modules.crawler.webmagic.samples.ygdy.YgdyDaoImpl;

/**
 * 爬虫完保存到mysql后，然后导入到相应的表中的逻辑处理
 * @author sdd
 * 
 * 
 * 
 * select * from cms_article where title like '%阳光电影%'
 * 
 * update cms_article set title=REPLACE(title,'阳光电影','电影东东')
 *
 */
//@Component("dBEXEMport")
@Service
@Lazy(false)
public class DBEXEMport {
	
	private static Logger logger = LoggerFactory.getLogger(DBEXEMport.class);
	
	
	@Autowired
	private LuceneUtil luceneUtil;
	
	public DBEXEMport(){ }
	
	DBHelper dbhelper = new DBHelper();
	YgdyDao ygdyDao = new YgdyDaoImpl();
	
	/**
	 * 	秒    0-59    , - * /
		分    0-59    , - * /
		小时    0-23    , - * /
		日期    1-31    , - * ? / L W C
		月份    1-12 或者 JAN-DEC    , - * /
		星期    1-7 或者 SUN-SAT    , - * ? / L C #
		年（可选）    留空, 1970-2099    , - * / 
		- 区间  
	 	* 通配符  
		? 你不想设置那个字段
			"0 0 12 * * ?"    每天中午十二点触发 
			"0 15 10 ? * *"    每天早上10：15触发 
			"0 15 10 * * ?"    每天早上10：15触发 
			"0 15 10 * * ? *"    每天早上10：15触发 
			"0 15 10 * * ? 2005"    2005年的每天早上10：15触发 
			"0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发 
			"0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发 
			"0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发 
			"0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发 
			"0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发 
			"0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发 
	 * @param type
	 * @param category
	 */
	@Scheduled(cron = "0 45 1,8,17,23 * * ?")
	public void executScheduled(){
		logger.info("-------enter executScheduled--------------");
		
		//先抓取最近更新的电影和电视剧
		NewIncreMain newMain = new NewIncreMain();
		newMain.run();
				
		//导入到另外的表中（cms_article和cms_article_data）
		new Thread(new ExecuteSQLThread("gndy",""+2)).start();
		
		//建立索引，不能并行，否则会出问题
		try {
			Thread.currentThread().sleep(1000l * 60 * 30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//连续剧
		new Thread(new ExecuteSQLThread("tv",""+3)).start();
		//综艺
		//new Thread(new ExecuteSQLThread("zongyi",""+4)).start();
	}
	
	public static void main(String[] args) {
		
		DBEXEMport db = new DBEXEMport();
		db.executScheduled();
	}
	
}
