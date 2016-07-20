<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>${article.title}</title>
	<meta name="decorator" content="cms_default_${site.theme}"/>
	<meta name="keywords" content="${site.keywords},${article.keywords}" />
	<meta name="description" content="${site.description},${article.description}" />
	
</head>
<body>
	<div class="row">
	   <div class="span2">
		 <h5><i class="icon-star"></i> 推荐下载</h5>
		 <ol>
		 	<cms:frontArticleHitsTop category="${category}"/>
		 </ol>
		 <h5><i class="icon-star"></i> 月下载排行</h4>
		 <ol>
		 	<cms:frontArticleDownloadTop category="${category}" beforeDay="30"/>
		 </ol>
		 <h5><i class="icon-star"></i> 年下载排行</h4>
		 <ol>
		 	<cms:frontArticleDownloadTop category="${category}" beforeDay="365" pageSize="20"/>
		 </ol>
	   </div>
	   <div class="span10">
		 <ul class="breadcrumb">
		    <cms:frontCurrentPosition category="${category}"/>
		 </ul>
	   </div>
	   <div class="span10">
	     <div class="row">
	       <div class="span10">
			<h3 style="color:#555555;font-size:20px;text-align:center;border-bottom:1px solid #ddd;padding-bottom:15px;margin:25px 0;">${article.title}</h3>
			<c:if test="${not empty article.description}"><div>摘要：${article.description}</div></c:if>
			<div><pre>${article.articleData.content}</pre></div>
			<c:if test="${not empty article.articleData.url}">
				<div>
				<br>下载地址：
					<pre>${article.articleData.url}</pre>
				</div>
			</c:if>
			<c:if test="${ empty article.articleData.url}">
				<div><br>
					来源自：${article.articleData.copyfrom}
				</div>
			</c:if>
			<div style="border-top:1px solid #ddd;padding:10px;margin:25px 0;">发布者：${article.user.name} &nbsp; 点击数：${article.hits} &nbsp; 发布时间：<fmt:formatDate value="${article.createDate}" pattern="yyyy-MM-dd"/> &nbsp; 更新时间：<fmt:formatDate value="${article.updateDate}" pattern="yyyy-MM-dd"/></div>
  	       </div>
  	     </div>
	     <div class="row">
			<div id="comment" class="hide span10">
				正在加载评论...
			</div>
	     </div>
	     <div class="row">
	       <div class="span10">
			<h5>相关文章</h5>
			<ol><c:forEach items="${relationList}" var="relation">
				<li style="float:left;width:230px;"><a href="${ctx}/view-${relation[0]}-${relation[1]}${urlSuffix}">${fns:abbr(relation[2],30)}</a></li>
			</c:forEach></ol>
	  	  </div>
  	    </div>
  	  </div>
   </div>
   <script type="text/javascript">
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

		
		$(document).ready(function() {
			if ("${category.allowComment}"=="1" && "${article.articleData.allowComment}"=="1"){
				$("#comment").show();
				page(1);
			}
		});
		function page(n,s){
			console.log(n + "---------" + s);
			$.get("${ctx}/comment",{theme: '${site.theme}', 'category.id': '${category.id}',
				contentId: '${article.id}', title: '${article.title}', pageNo: n, pageSize: s, date: new Date().getTime()
			},function(data){
				$("#comment").html(data);
			});
		}
	
	</script>
</body>
</html>