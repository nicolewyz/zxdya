package com.thinkgem.jeesite.modules.crawler.webmagic.dyResource;

import java.util.HashMap;
import java.util.Map;

public class MovieTypeConstants {

	public static final String TYPE_humanNature = "人性题材";
	public static final String TYPE_OscarForeign = "奥斯卡外语奖";
	public static final String TYPE_IMDBTop250 = "IMDB250高分电影";
	public static final String TYPE_DoubanTop250 = "Douban250高分电影";
	public static final String TYPE_TSPDT1000 = "TSPDT1000伟大电影";
	
	static{
		
	}
	
	//阳光电影，所有对应的值，都归于1-10之间。
	public static final String TYPE_YG_tv = "3";
	public static final String TYPE_YG_game = "7";
	public static final String TYPE_YG_gndy = "2";
	public static final String TYPE_YG_dongman = "9";
	public static final String TYPE_YG_3gp = "2";
	public static final String TYPE_YG_zongyi = "8";
	
	public static Map<String,String> typeMap = new HashMap<String, String>();
	static {
		//阳光电影
		typeMap.put("TYPE_YG_tv", "3");
		typeMap.put("TYPE_YG_game", "7");
		typeMap.put("TYPE_YG_gndy", "2");		//电影
		typeMap.put("TYPE_YG_3gp", "2");
		typeMap.put("TYPE_YG_dongman", "9");	//动漫
		typeMap.put("TYPE_YG_zongyi", "8");		//综艺
		
		//6v电影
		typeMap.put("TYPE_6V_dlz", "3");	
		typeMap.put("TYPE_6V_rj", "3");
		typeMap.put("TYPE_6V_game", "7");	//游戏
		typeMap.put("TYPE_6V_dy", "2");		//电影
		typeMap.put("TYPE_6V_3D", "4");		//3D电影
		typeMap.put("TYPE_6V_gq", "6");		//经典高清
		typeMap.put("TYPE_6V_zy", "8");		//综艺
		typeMap.put("TYPE_6V_jddy", "9");	//动漫
		typeMap.put("TYPE_6V_zydy", "2");	//电影
		typeMap.put("TYPE_6V_mj", "3");		//连续剧
		
		//龙部落
		typeMap.put("TYPE_LBL_video", "8");		//综艺
		typeMap.put("TYPE_LBL_movie", "2");		//电影
		typeMap.put("TYPE_LBL_dongman", "9");	//动漫
		typeMap.put("TYPE_LBL_television", "3");//电视剧
	}
	
	
	
}
