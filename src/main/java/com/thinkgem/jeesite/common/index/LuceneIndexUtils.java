package com.thinkgem.jeesite.common.index;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.modules.cms.web.ArticleController;
import com.thinkgem.jeesite.modules.crawler.DBEXEMport.Gndy;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyDao;
import com.thinkgem.jeesite.modules.crawler.webmagic.dyResource.YgdyDaoImpl;

/**
 * 对lucene相关的一些增删改查
 * @author sdd
 *
 */
//@Service
//@Lazy(false)
public class LuceneIndexUtils {

	private static Logger logger = LoggerFactory.getLogger(ArticleController.class);
	
	//参考：http://yangjizhong.iteye.com/blog/1415658
//	@Autowired(required = false)//这里我写了required = false,需要时再引入，不写的话会报错，大家有更好解决方案请留言哈   
//	private Analyzer analyzer;   
	//@Autowired(required = false)   
	private IndexWriter indexWriter;   
	//@Autowired(required = false)   
	private IndexSearcher indexSearcher;
	//@Autowired(required = false)   
	private IndexReader indexReader;
	
	//@Scheduled(cron = "0 37 21 * * ?")
	public int indexDocuments(final String type, IndexWriter indexWriter) throws Exception {
		
		logger.info("enter index................");
		
		
		final int startDocs = indexWriter.numDocs();
		System.out.println("当前存储的文档数：:"+indexWriter.numDocs());
		
		YgdyDao dao = new YgdyDaoImpl();
		List<Gndy> lists = dao.getGndys("cms_yg_gndy", "2", "20160501");
		Iterator<Gndy> it = lists.iterator();
		while(it.hasNext()){
			
			Gndy dy = it.next();
            System.out.println("------------"+ dy.getName());
            Document doc = new Document();
            doc.add(new StringField("name", dy.getName(), Field.Store.YES ));
            doc.add(new StringField("content", dy.getContent(), Field.Store.YES ));
            
            indexWriter.addDocument(doc);
        }
        indexWriter.commit();
        
        System.out.println("当前存储的文档数：:"+indexWriter.numDocs());
        System.out.println("当前存储的文档数，包含回收站的文档：:"+indexWriter.maxDoc());
        
        return (indexWriter.numDocs()-startDocs);
	
	}
	
}
