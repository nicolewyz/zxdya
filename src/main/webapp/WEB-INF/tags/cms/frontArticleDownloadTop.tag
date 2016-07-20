<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<%@ attribute name="category" type="com.thinkgem.jeesite.modules.cms.entity.Category" required="true" description="推荐下载"%>
<%@ attribute name="pageSize" type="java.lang.Integer" required="false" description="页面大小"%>
<%@ attribute name="beforeDay" type="java.lang.Integer" required="true" description="多少天前"%>
<c:forEach items="${fnc:getArticleDownloadList(category.site.id, category.id, beforeDay, not empty pageSize?pageSize:10 )}" var="article">
	<li><a href="${ctx}/view-${article.category.id}-${article.id}${urlSuffix}" style="color:${article.color}" title="${article.title}">${fns:abbrInMark(article.title,16)}</a></li>
</c:forEach>