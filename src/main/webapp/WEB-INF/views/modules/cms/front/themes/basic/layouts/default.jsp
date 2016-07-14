<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<html>
<head>
	<title>${site.title} - <sitemesh:title default="电影东东"/> - 最新电影 - 免费下载 - 高清电影 - 经典电影 - 免费电影 - 最新大片 - 最新连续剧  - 最新美剧 </title>
	<%@include file="/WEB-INF/views/modules/cms/front/include/head.jsp" %>
	<!-- Baidu tongji analytics -->
	<script>var _hmt = _hmt || [];(function() {  var hm = document.createElement("script");  hm.src = "//hm.baidu.com/hm.js?79e3009a98e37abe72603a2e9ac366ca";  var s = document.getElementsByTagName("script")[0];  s.parentNode.insertBefore(hm, s);})();</script>
	
	<sitemesh:head/>
</head>
<body>
	<div class="navbar navbar-fixed-top" style="position:static;margin-bottom:10px;">
      <div class="navbar-inner">
        <div class="container">
          <c:choose>
   			<c:when test="${not empty site.logo}">
   				<div class="row-fluid">
   					<div class="span4">
		   				<img alt="${site.title}" src="${site.logo}" class="container" onclick="location='${ctx}/index-${site.id}${fns:getUrlSuffix()}'" style="width: 300px;width: 175px;">
   					</div>
   					<div class="span8">
	   					<span></span>
	   				</div> 
   				</div>
   			</c:when>
   			<c:otherwise><a class="brand" href="${ctx}/index-${site.id}${fns:getUrlSuffix()}">${site.title}</a></c:otherwise>
   			
   		  </c:choose>
          <div class="nav-collapse">
            <ul id="main_nav" class="nav nav-pills">
             	<li class="${not empty isIndex && isIndex ? 'active' : ''}"><a href="${ctx}/index-1${fns:getUrlSuffix()}"><span>${site.id eq '1'?'首　 页':'返回主站'}</span></a></li>
				<c:forEach items="${fnc:getMainNavList(site.id)}" var="category" varStatus="status"><c:if test="${status.index lt 6}">
                    <c:set var="menuCategoryId" value=",${category.id},"/>
		    		<li class="${requestScope.category.id eq category.id||fn:indexOf(requestScope.category.parentIds,menuCategoryId) ge 1?'active':''}"><a href="${category.url}" target="${category.target}"><span>${category.name}</span></a></li>
		    	</c:if></c:forEach>
			    <%-- <li id="siteSwitch" class="dropdown">
			       	<a class="dropdown-toggle" data-toggle="dropdown" href="#" title="站点"><i class="icon-retweet"></i></a>
					<ul class="dropdown-menu">
					  <c:forEach items="${fnc:getSiteList()}" var="site"><li><a href="#" onclick="location='${ctx}/index-${site.id}${urlSuffix}'">${site.title}</a></li></c:forEach>
					</ul>
				</li>  --%>
		    	<%-- <li id="themeSwitch" class="dropdown">
			       	<a class="dropdown-toggle" data-toggle="dropdown" href="#" title="主题切换"><i class="icon-th-large"></i></a>
				    <ul class="dropdown-menu">
				      <c:forEach items="${fns:getDictList('theme')}" var="dict"><li><a href="#" onclick="location='${pageContext.request.contextPath}/theme/${dict.value}?url='+location.href">${dict.label}</a></li></c:forEach>
				    </ul>
				    <!--[if lte IE 6]><script type="text/javascript">$('#themeSwitch').hide();</script><![endif]-->
			    </li> --%>
			    <li >
	            	<a id=setfavorite href="http://www.zxdya.com" title="添加收藏" class="pull-right"><font color=green>添加收藏</font></a> 
			    </li>
            </ul>
            <form class="navbar-form pull-right" action="${ctx}/search" method="get">
              	<input type="text" name="q" maxlength="30" style="width:200px;" placeholder="输入搜索词，按回车" value="${q}">
            </form>
          </div><!--/.nav-collapse -->
          
        </div>
      </div>
    </div>
	<div class="container">
	
		<div class="content">
<!-- 			<script type="text/javascript">(function(){document.write(unescape('%3Cdiv id="bdcs"%3E%3C/div%3E'));var bdcs = document.createElement('script');bdcs.type = 'text/javascript';bdcs.async = true;bdcs.src = 'http://znsv.baidu.com/customer_search/api/js?sid=9306553867887891687' + '&plate_url=' + encodeURIComponent(window.location.href) + '&t=' + Math.ceil(new Date()/3600000);var s = document.getElementsByTagName('script')[0];s.parentNode.insertBefore(bdcs, s);})();</script> -->
			<sitemesh:body/>
		</div>
		<hr style="margin:20px 0 10px;">
		<footer>
			<div class="footer_nav"><a href="${ctx}/guestbook" target="_blank">公共留言</a> | <a href="${ctx}/search" target="_blank">全站搜索</a> | <a href="${ctx}/map-${site.id}${fns:getUrlSuffix()}" target="_blank">站点地图</a> | <a href="mailto:zhaoyu_0216@163.com">技术支持</a> <%-- | <a href="${pageContext.request.contextPath}${fns:getAdminPath()}" target="_blank">后台管理</a> --%></div>
			<div class="pull-right">京ICP备16028667 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ${fns:getDate('yyyy年MM月dd日 E')}</div><div class="copyright">${site.copyright}  </div>
      	</footer>
    </div> <!-- /container -->
    <script src="${ctxStaticTheme}/script.js" type="text/javascript"></script>
    <script src="//cdn.bootcss.com/bootstrap/2.3.1/js/bootstrap.min.js"></script>
    <script src="http://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
	<script src="http://cdn.bootcss.com/jquery-migrate/1.4.1/jquery-migrate.min.js"></script>
	
	<script>
	if (!window.jQuery) {
		var script = document.createElement('script');
		script.src = "${ctxStatic}/jquery/jquery-1.9.1.min.js";
		document.body.appendChild(script);
	}
	</script>
	
    <script type="text/javascript"> 
    
	$(document).ready(function(){ 
		// 添加收藏 
		$("#setfavorite").click(function(){ 
		            var ctrl = (navigator.userAgent.toLowerCase()).indexOf('mac') != -1 ? 'Command/Cmd': 'CTRL'; 
		            if (document.all) { 
		                window.external.addFavorite('http://www.zxdya.com/', '电影东东'); 
		            } else if (window.sidebar) { 
		                window.sidebar.addPanel('电影东东', 'http://www.zxdya.com/', ""); 
		            } else { 
		                alert('您可以尝试通过快捷键' + ctrl + ' + D 加入到收藏夹~'); 
		            }	
		                return false; 
		}); 
	}); 
	
	</script> 
	
</body>
</html>