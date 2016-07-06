<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>电影天堂</title>
	<meta name="decorator" content="cms_default_${site.theme}"/>
	<meta name="description" content="${site.description}" />
	<meta name="keywords" content="${site.keywords}" />
	<meta name="msvalidate.01" content="BF039D1D416D0F70AB8DC7262B32737E" />
	
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
		<button type="button" class="close" data-dismiss="alert">×</button>
		<font size="2">
			<strong>如果喜欢请推荐给你的朋友啊！记得收藏哦！   欢迎加电影东东QQ群(574401899)，一起分享快乐！ </strong>
		</font>
	</div>
    <div class="hero-unit" style="padding-bottom:25px;padding-top:25px;margin:1px 0;">
      <p><h3><a href="http://zxdya.com"><font color="#4F94CD">最新电影啊(zxdya.com)——尽在电影东东</font></a><h3></p>
      	<blockquote >
		  <p>我要你知道，这个世界上有一个人会永远等着你。无论是在什么时候，无论你在什么地方，反正你知道总会有这样一个人！</p>
		  <small>《半生缘》 <cite title="Source Title">电影东东</cite></small>
		</blockquote>
    </div>
    
    <div class="row">
      <div class="span4">
        <h5><small><a href="${ctx}/list-2${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-film"></i>&nbsp;&nbsp;最新电影</h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 2, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="yyyy-MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,26)}</a></li>
		</c:forEach></ul>
      </div>
      <div class="span4">
        <h5><small><a href="${ctx}/list-3${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-play-circle"></i>&nbsp;&nbsp;最新连续剧</h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 3, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="yyyy-MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,26)}</a></li>
		</c:forEach></ul>
      </div>
      <div class="span4">
        <h5><small><a href="${ctx}/list-5${urlSuffix}" class="pull-right">更多<i class="icon-forward"></i></a></small>&nbsp;<i class="icon-lock"></i>&nbsp;&nbsp;看电影涨姿势</h5>
		<ul><c:forEach items="${fnc:getArticleList(site.id, 5, 30, '')}" var="article">
			<li><span class="pull-right"><fmt:formatDate value="${article.updateDate}" pattern="yyyy-MM-dd"/></span><a href="${article.url}" style="color:${article.color}" title="${article.title }"  target=_blank>${fns:abbr(article.title,26)}</a></li>
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