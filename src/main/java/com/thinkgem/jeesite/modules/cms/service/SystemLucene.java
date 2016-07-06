package com.thinkgem.jeesite.modules.cms.service;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.NumericUtils;
 
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
 
/**
 * Created by liujh on 16/5/22.
 */
 
public class SystemLucene {
    static Analyzer analyzer = new StandardAnalyzer();
 
    public enum CacheMarker {
        SELLER_
    }
 
    /**
     * 新增
     *
     * @param tableName 标识
     * @param data      字段
     * @throws IOException
     */
    public static void insert(String tableName, Map<String, Object> data) throws IOException {
        Directory directory = FSDirectory.open(Paths.get("/Users/workplace/luceneIndex"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        Document doc = new Document();
        Set<String> keys = data.keySet();
        data.put("TABLENAME", tableName);//添加标识,用于查询所有
        for (String key : keys) {
            doc.add(new TextField(tableName + key, data.get(key) + "", Field.Store.YES));
        }
        iwriter.addDocument(doc);
        iwriter.close();
    }
 
    /**
     * 更新
     *
     * @param tableName 标识
     * @param data      更新内容
     * @param filedName 查询条件字段
     * @param value     值
     * @throws Exception
     */
    public static void update(String tableName, Map<String, Object> data, String filedName, String value) throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/Users/workplace/luceneIndex"));
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        QueryParser parser = new QueryParser(tableName + filedName, analyzer);
        Query query = parser.parse(value);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        ScoreDoc[] hits = isearcher.search(query, Integer.MAX_VALUE).scoreDocs;
        Set<String> keys = data.keySet();
        Document hitDoc = null;
        for (int i = 0; i < hits.length; i++) {
            hitDoc = isearcher.doc(hits[i].doc);
            for (String key : keys) {
                hitDoc.removeField(tableName + key);//删除字段,重新添加
                hitDoc.add(new TextField(tableName + key, data.get(key) + "", Field.Store.YES));
            }
        }
        iwriter.deleteDocuments(query);
        iwriter.addDocument(hitDoc);
        iwriter.commit();
        iwriter.close();
        ireader.close();
    }
 
    /**
     * 删除
     *
     * @param tableName 标识
     * @param filedName 字段
     * @param value     值
     * @throws Exception
     */
    public static void delete(String tableName, String filedName, String value) throws Exception {
        Directory directory = FSDirectory.open(Paths.get("/Users/workplace/luceneIndex"));
        DirectoryReader ireader = DirectoryReader.open(directory);
        QueryParser parser = new QueryParser(tableName + filedName, analyzer);
        Query query = parser.parse(value);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        iwriter.deleteDocuments(query);
        iwriter.commit();
        iwriter.close();
        ireader.close();
    }
 
    /**
     * 查询
     *
     * @param tableName 标识
     * @param filedName 查询字段
     * @param value     查询值
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static Document query(String tableName, String filedName, String value) throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get("/Users/workplace/luceneIndex"));
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        QueryParser parser = new QueryParser(tableName + filedName, analyzer);
        Query query = parser.parse(value);
        ScoreDoc[] hits = isearcher.search(query, Integer.MAX_VALUE).scoreDocs;
        Document hitDoc = null;
        for (int i = 0; i < hits.length; i++) {
            hitDoc = isearcher.doc(hits[i].doc);
            System.out.println(hitDoc);
        }
        ireader.close();
        return hitDoc;
    }
}