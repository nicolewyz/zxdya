package com.thinkgem.jeesite.modules.crawler.webmagic.dyResource;
/**
 * 知乎用户信息
 * @author antgan
 *
 */
public class YgdyArticle {
    private String dyName;//电影名称
    private String domain;//域名
    private String category;//类别
    private String content;//内容
    private String url;//下载地址
    private String publishDate;//发布时间
    
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDyName() {
		return dyName;
	}
	public void setDyName(String dyName) {
		this.dyName = dyName;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "YgdyArticle [dyName=" + dyName + ", domain=" + domain+ ", url=" + url+ ", publishDate=" + publishDate
				+ ", category=" + category + ", content=" + content + "]";
	}
    
}