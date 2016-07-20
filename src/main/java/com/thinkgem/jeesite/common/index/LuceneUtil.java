package com.thinkgem.jeesite.common.index;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.modules.crawler.DBEXEMport.Gndy;

/**
 * @author TaoYu
 * @Description LUCENE工具类，不支持并发(后面会改成按登录用户写地址，写入对应的路径，即可支持多人操作)
 */
@Component  
//@Lazy(false)
public class LuceneUtil {
	
	private static Logger logger = LoggerFactory.getLogger(LuceneUtil.class);
	
	//需要从属性文件中读值的静态变量
	private static String path = "/opt/soft/luceneIndex/lucene"  ;
	//private static String path = "D:\\LuceneDemo";// 默认的索引存储地址
	
	//配置一个setter, 为该静态变量赋值---------报错
//	@Value("${lucene.path}")
//	public void setPath(String path) {
//		LuceneUtil.path = path;
//	}

	private static Directory dir;// 存储地目录
	private IndexWriterConfig iwc;// 写索引配置文件
	private static Analyzer analyzer;// 分词器
	static {
		try {
			dir = FSDirectory.open(Paths.get(path));// 初始化目录
			//analyzer = new AnsjAnalyzer();
			analyzer = new SmartChineseAnalyzer();// 初始化中文分词器，可以更换为其他的
			
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @Describe：得到写索引实例
	 */
	private IndexWriter getWriter() {
		IndexWriter writer = null;
		try {
			//iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			iwc = new IndexWriterConfig(analyzer);// 初始化写索引配置文件
			
			writer = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return writer;
	}

	/**
	 * @Describe：添加索引
	 */
	public void addIndex(List<Gndy> gndys) throws IndexWriterCloseException, DocException {
		IndexWriter writer = getWriter();
		try {
			for (Gndy gndy : gndys) {
				Document doc = new Document();
				doc.add(new StringField("id", gndy.getId(), Store.YES));
				doc.add(new StringField("name", gndy.getName(), Store.YES));
				//写索引时，去掉相关的HTML标签
				doc.add(new TextField("content", gndy.getContent()==null?"":com.thinkgem.jeesite.common.utils.StringUtils.replaceHtml(gndy.getContent()), Store.NO));
				doc.add(new StringField("publishDate", gndy.getPublishDate(), Store.YES));
				doc.add(new StringField("category", gndy.getCategory()==null?"":gndy.getCategory(), Store.YES));
				
				writer.addDocument(doc);
				
				writer.commit();
			}
			logger.info("新增"+gndys.size()+"个对象添加到索引");	
		} catch (IOException e) {
			throw new DocException("添加电影索引异常");
		} finally {
			closeWriter(writer);

		}

	}

	/**
	 * @Describe：关闭写索引流
	 */
	private static void closeWriter(IndexWriter writer) throws IndexWriterCloseException {
		try {
			writer.close();
		} catch (IOException e) {
			throw new IndexWriterCloseException();
		}

	}

	/**
	 * @Describe：更新索引
	 */
	public void updateIndex(Gndy gndy) throws DocException, IndexWriterCloseException {
		IndexWriter writer = getWriter();
		try {
			Document doc = new Document();
			doc.add(new StringField("id", gndy.getId(), Store.YES));
			doc.add(new StringField("name", gndy.getName(), Store.YES));
			doc.add(new TextField("content", gndy.getContent()==null?"":com.thinkgem.jeesite.common.utils.StringUtils.replaceHtml(gndy.getContent()), Store.YES));
			doc.add(new StringField("publishDate", gndy.getPublishDate(), Store.YES));
			doc.add(new StringField("category", gndy.getCategory()==null ?"":gndy.getCategory(), Store.YES));
			writer.updateDocument(new Term("id", gndy.getId()), doc);
			writer.commit();
			
			logger.info("更新一个索引");
		} catch (IOException e) {
			throw new DocException("更新文档异常");
		} finally {
			closeWriter(writer);
		}

	}

	/**
	 * @throws IndexWriterCloseException
	 * @throws DocException
	 * @Describe：删除索引
	 */
	public void deleteIndex(String dyId) throws IndexWriterCloseException, DocException {
		IndexWriter writer = getWriter();
		try {
			writer.deleteDocuments(new Term("id", dyId));
			writer.forceMergeDeletes();// 真正删除(好像有效率问题)TODO 改天详细看一下
			writer.commit();
			
			logger.info("删除一个索引");	
		} catch (IOException e) {
			throw new DocException("删除文档异常");
		} finally {
			closeWriter(writer);
		}
	}

	/**
	 * @Describe：查询索引博客（里面一些可以抽出来，为了流程完整暂不处理） @Date： 2016年6月16日下午9:45:35
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static List<Gndy> searchDy(String param, Integer nums) throws Exception {
		// 1打开度索引流
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(reader);
		// 2构建组合查询并添加二个查询器
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();// 组合查询
		QueryParser titleParser = new QueryParser("name", analyzer);
		Query titleQuery = titleParser.parse(param);
		QueryParser contentParser = new QueryParser("content", analyzer);
		Query contentQuery = contentParser.parse(param);
		booleanQuery.add(titleQuery, BooleanClause.Occur.SHOULD);
		booleanQuery.add(contentQuery, BooleanClause.Occur.SHOULD);
		// 3构建高亮插件
		QueryScorer scorer = new QueryScorer(titleQuery);// 以标题构建
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);// 简单高亮插件
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><mark>", "</mark></b>");// 设置返回结果
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
		highlighter.setTextFragmenter(fragmenter);
		// 4初始化返回的集合，并通过条件查询搜索得到命中的文档
		List<Gndy> blogList = new LinkedList<Gndy>();
		TopDocs hits = is.search(booleanQuery.build(), nums);
		// 5遍历命中的文档
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Gndy dy = new Gndy();
			Document doc = is.doc(scoreDoc.doc);// 什么鬼设计（显示的调用ID，不友好差评）
			dy.setId(doc.get("id"));
			dy.setName(doc.get("name"));
			dy.setPublishDate(doc.get("publishDate"));
			dy.setCategory(doc.get("id").substring(8,9));
			String title = doc.get("name");
			String content = StringEscapeUtils.escapeHtml(doc.get("content").substring(0, doc.get("content").length()>500?500:(doc.get("content").length())));
			// 设置返回标题高亮
			if (title != null) {
				TokenStream tokenStream = analyzer.tokenStream("name", new StringReader(title));
				String hTitle = highlighter.getBestFragment(tokenStream, title);
				if (StringUtils.isEmpty(hTitle)) {
					dy.setName(title);
				} else {
					dy.setName(hTitle);
				}
			}
			// 设置返回内容高亮
			if (content != null) {
				TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content));
				String hContent = highlighter.getBestFragment(tokenStream, content);
				if (StringUtils.isEmpty(hContent)) {
					if (content.length() <= 200) {
						dy.setContent(content);
					} else {
						dy.setContent(content.substring(0, 200));
					}
				} else {
					dy.setContent(hContent);
				}
			}
			blogList.add(dy);
		}
		return blogList;
	}
	
	public static void main(String[] args) {
		
		
//		try {
//			Gndy d = new Gndy();
//			d.setId("201606212001");
//			d.setName("老爸上战场 BD中英双字幕迅雷下载");
//			d.setContent("撒法发顺丰打发手动阀手动阀撒阿斯蒂芬撒旦发送发送mg style='WIDTH: 580px; HEIGHT: 876px' border='0' src='h");
//			d.setPublishDate("2016060101");
//			LuceneUtil util = new LuceneUtil();
//			util.updateIndex(d);
//			
//			List<Gndy> dys = searchDy("老爸上战场", 8);
//			Iterator<Gndy> it = dys.iterator();
//			while(it.hasNext()){
//				Gndy dy = it.next();
//				System.out.println(dy.getId());
//				System.out.println(dy.getName());
//				System.out.println(dy.getContent());
//			}
//			
//			LuceneUtil util2 = new LuceneUtil();
//			util2.deleteIndex("201606212004");
//			
//			List<Gndy> dyss = searchDy("喜剧", 6);
//			Iterator<Gndy> its = dyss.iterator();
//			while(its.hasNext()){
//				Gndy dy = its.next();
//				System.out.println(dy.getId());
//				System.out.println(dy.getName());
//				System.out.println(dy.getContent());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
