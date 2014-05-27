<%@ page session="false"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<style type="text/css">
		.desc { /* padding: 0px; */  width: 120px;   padding-left:3px; padding-right:20px; font-family: Arial; font-size: 11pt;}
		.desc2 { padding-right: 5px; border-left: 1px solid #808080; width: 40px; color: #4b5b70; font-family: Arial; font-size: 11pt;}
		.zoom { width: 40px; color: #4b5b70; font-family: Arial; font-size: 11pt;}
		.zoomel {padding-left: 0px;}
		.tablestyle { width: 100%; border-bottom: 1px solid #808080; background-color: #F0F0F0}
		.colortd {  width: 12px; }
		.zoomicon { width: 20px;}
	</style>
<meta http-equiv="content-type" content="text/html;charset=utf-8" /></head>
<body>

<div>
	
	<div class="tablestyle">
		<span class="zoom"><spring:message code="workflowvisualization.zoom"/></span>
		<span class="zoomicon"><a class="zoomel" href="/productcockpit/controllers/workflowVisualization?pk=${pk}&scale=${higher}&lang=${lang}">	<img alt="Up" src="/productcockpit/productcockpit/images/workflowVisualization/icon_func_zoom_in.png">
    	</img></a></span>
		<span class="zoomicon" style="padding-right: 30px;"><a class="zoomel" href="/productcockpit/controllers/workflowVisualization?pk=${pk}&scale=${lower}&lang=${lang}"><img alt="Up" src="/productcockpit/productcockpit/images/workflowVisualization/icon_func_zoom_out.png">
    	</img></a></span>
		<span class="desc2" style="padding-left:20px;"><spring:message code="workflowvisualization.legend"/></span>
		<span class="colortd"><img alt="Up" src="/productcockpit/productcockpit/images/workflowVisualization/image_legend_grey.png"></span>
		<span  class="desc" style="color: #afafaf;"><spring:message code="workflowvisualization.pending"/></span>
		<span class="colortd"><img alt="Up" src="/productcockpit/productcockpit/images/workflowVisualization/image_legend_orange.png"></span>
		<span  class="desc" style="color: #f39422"><spring:message code="workflowvisualization.inprogress"/></span>
		<span class="colortd"><img alt="Up" src="/productcockpit/productcockpit/images/workflowVisualization/image_legend_green.png"></span>
		<span  class="desc" style="color: #3e9743"><spring:message code="workflowvisualization.finished"/></span>
		<span class="colortd"><img alt="Red" src="/productcockpit/productcockpit/images/workflowVisualization/image_legend_red.png"></span>
		<span class="desc" style="color: #f54028"><spring:message code="workflowvisualization.terminated"/></span>
	</div>
	
</div>
<div >
	<embed src="/productcockpit/controllers/workflowDiagramRenderer?pk=${pk}&style=LABELS_IN_CIRCLE&scale=${scale}&lang=${lang}" type="image/svg+xml"/>
</div>
</body>

</html>