<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>

<%
	request.setAttribute("_mappingPath",request.getContextPath() 
			+ request.getAttribute(org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)
				.toString().replaceFirst("\\/$", "") );
%>
<style>
	#submain {margin: 5px;min-width: 950px;}
</style>
<script language="javascript" type="text/javascript">
	ST3Lib.dialog.btn.ok = '<spring:message code="ST3.dialog.ok"/>';
	ST3Lib.dialog.btn.cancel = '<spring:message code="ST3.dialog.cancel"/>';
	ST3Lib.validate.msgcode['MSTD0031AERR'] = '<spring:message code="MSTD0031AERR"/>';
	ST3Lib.validate.msgcode['MSTD0043AERR'] = '<spring:message code="MSTD0043AERR"/>';
	ST3Lib.validate.msgcode['MSTD0053AERR'] = '<spring:message code="MSTD0053AERR"/>';
</script>
<body>
	<div id="submain" class="overflow-hidden" >
		<!-- div id="content-panel" class="iframe overflow-hidden autoheight" >  -->
			<views:messageBar/>
			<tiles:insertAttribute name="body" />
		<!-- /div> -->
	</div>
	<a id="lureTab" onfocus="$('input,select,teatarea,button,a').filter(':tabbable:first').focus();" 
		href="#" style="display:inline-block;position:absolute;left:-100px;top:-100px;z-index:100;">&nbsp;</a>
</body>
</html>