package com.thinkgem.jeesite.modules.crawler.DBEXEMport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PushBaidu {

	private static Logger logger = LoggerFactory.getLogger(PushBaidu.class);
	private static final String url = "http://data.zz.baidu.com/urls?site=zxdya.com&token=QcxVjpORJhqJ4ShE";
	
	public PushBaidu(){}
	
	public void call(String dyId) {
		String[] param = {
				"http://zxdya.com/f/view-"+ dyId.indexOf(8) +"-" + dyId + ".html"//需要推送的网址
		};
		String json = Post(url, param);//执行推送方法
        logger.info("推送结果结果是"+json);  //打印推送结果

	}
	
	/**
	 * 百度链接实时推送
	 * @param PostUrl
	 * @param Parameters
	 * @return
	 */
	public static String Post(String PostUrl,String[] Parameters){
		if(null == PostUrl || null == Parameters || Parameters.length ==0){
			return null;
		}
        String result="";
        PrintWriter out=null;
        BufferedReader in=null;
        try {
            //建立URL之间的连接
            URLConnection conn=new URL(PostUrl).openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("Host","data.zz.baidu.com");
            conn.setRequestProperty("User-Agent", "curl/7.12.1");
            conn.setRequestProperty("Content-Length", "83");
            conn.setRequestProperty("Content-Type", "text/plain");
             
            //发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);
             
            //获取conn对应的输出流
            out=new PrintWriter(conn.getOutputStream());
            //发送请求参数
            String param = "";
            for(String s : Parameters){
            	param += s+"\n";
            }
            out.print(param.trim());
            //进行输出流的缓冲
            out.flush();
            //通过BufferedReader输入流来读取Url的响应
            in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line=in.readLine())!= null){
                result += line;
            }
             
        } catch (Exception e) {
            logger.error("发送post请求出现异常！"+e);
            e.printStackTrace();
        } finally{
            try{
                if(out != null){
                    out.close();
                }
                if(in!= null){
                    in.close();
                }
                 
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

}
