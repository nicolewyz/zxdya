<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>电影东东下载网</title>
	<meta name="description" content="${site.description}" />
	<meta name="keywords" content="${site.keywords}" />
	<meta name="msvalidate.01" content="BF039D1D416D0F70AB8DC7262B32737E" />
	
	<meta name="decorator" content="cms_default_${site.theme}"/>
</head>
<body>
	<!-- <div class="alert alert-info">
		<button type="button" class="close" data-dismiss="alert">×</button>
		<font size="2">
			<strong>请支持正版电影！</strong>
			本站下载资源均来自互联网，只供网络测试和学习，请在24小时内删除所下内容574401899
		</font>
	</div> -->
	<div class="alert alert-info">
		<button type="button" class="close" data-dismiss="alert"></button>
		<font size="2">
			<strong>如果喜欢请推荐给你的朋友啊！记得收藏哦！   欢迎<a target="_blank" href="http://shang.qq.com/wpa/qunwpa?idkey=f81baad1a3e31d296274a3dc57b71d746f21e0d2a4e184b742daeec71e46a290"><img border="0" src="http://pub.idqqimg.com/wpa/images/group.png" alt="电影东东" title="电影东东"></a>，一起分享快乐！(反馈页面错误有奖励哦) </strong>
		</font>
	</div>
    <div class="hero-unit" style="padding-bottom:25px;padding-top:25px;margin:1px 0;">
      <p><h3><a href="http://zxdya.com"><font color="#4F94CD">最新电影啊(www.zxdya.com)——尽在电影东东</font></a><h3></p>
      	<blockquote >
      		<p>${fnc:getSaying().desc}</p>
			<small>${fnc:getSaying().name} <cite title="电影东东">www.zxdya.com</cite></small>
      	</blockquote>
    </div>
    
    <div class="row">
      <div class="span4">
        <h5><small><a href="${ctx}/list-2${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-film"></i>&nbsp;&nbsp;最新电影<font size="1" color="red"><!-- (红色为推荐) --></font></h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 2, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,30)}</a></li>
		</c:forEach></ul>
      </div>
      <div class="span4">
        <h5><small><a href="${ctx}/list-3${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-play-circle"></i>&nbsp;&nbsp;经典电影</h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 6, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,30)}</a></li>
		</c:forEach></ul>
      </div>
      <div class="span4">
        <h5><small><a href="${ctx}/list-5${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-lock"></i>&nbsp;&nbsp;看电影涨姿势</h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 5, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,30)}</a></li>
		</c:forEach></ul>
      </div>
    </div>
    <hr style="margin:20px 0 10px;">
    <div class="row">
      <div class="span4">
        <h5><small><a href="${ctx}/list-2${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-film"></i>&nbsp;&nbsp;3D电影<font size="1" color="red"><!-- (红色为推荐) --></font></h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 4, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,30)}</a></li>
		</c:forEach></ul>
      </div>
      <div class="span4">
        <h5><small><a href="${ctx}/list-3${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-play-circle"></i>&nbsp;&nbsp;动漫电影</h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 9, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,30)}</a></li>
		</c:forEach></ul>
      </div>
      <div class="span4">
        <h5><small><a href="${ctx}/list-5${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-lock"></i>&nbsp;&nbsp;最新连续剧</h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 3, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,30)}</a></li>
		</c:forEach></ul>
      </div>
    </div>
    <script>
	(function(){
	    var bp = document.createElement('script');
	    var curProtocol = window.location.protocol.split(':')[0];
	    if (curProtocol === 'https') {
	        bp.src = 'https://zz.bdstatic.com/linksubmit/push.js';        
	    }
	    else {
	        bp.src = 'http://push.zhanzhang.baidu.com/push.js';
	    }
	    var s = document.getElementsByTagName("script")[0];
	    s.parentNode.insertBefore(bp, s);
	})();
	</script>
</body>
</html>