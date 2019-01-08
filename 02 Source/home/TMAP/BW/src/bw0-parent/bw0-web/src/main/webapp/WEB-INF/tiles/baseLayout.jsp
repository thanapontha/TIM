<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><tiles:insertAttribute name="title" ignore="true" /></title>	
	<link rel="stylesheet" href="${pageContext.request.contextPath}<spring:message code="stylehotsneaks"/>" media="all" />	
	<link rel="icon" href="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/favicon.ico" type="image/x-icon" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}<spring:message code="fontsizestylepath"/>" media="all" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}<spring:message code="monthpickeruicss"/>" media="all" />
	   
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjs"/>"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryuicss"/>"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjspath"/>jquery.filter_input.js"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjspath"/>jquery.inputmask.bundle.js"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjspath"/>jquery.cookie.js"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjspath"/>jquery.fixedheadertable.min.js"></script>
	
	<script src="${pageContext.request.contextPath}/webjars/bootstrap/4.1.3/js/bootstrap.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/4.1.3/css/bootstrap.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webjars/bootstrap/4.1.3/css/bootstrap-reboot.min.css" />
     <views:style src="styles.css" />

    	
	<views:script src="st3.lib.js"/>
	<views:script src="gwrds.lib.js"/>
	<views:script src="gwrds.lib.ext.js"/>
	<views:script src="st3.script.js"/>
	<script src="${pageContext.request.contextPath}<spring:message code="monthpickerjs"/>"></script>
	
	<%
		request.setAttribute("_mappingPath",request.getContextPath() 
				+ request.getAttribute(org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)
					.toString().replaceFirst("\\/$", "") );
	%>
	<script language="javascript" type="text/javascript">
		var _rootPath = '${pageContext.request.contextPath}';
		var _mappingPath = '${_mappingPath}';
		var _imagePath = '${pageContext.request.contextPath}<spring:message code="imagepath"/>';
		var _scriptPath = '${pageContext.request.contextPath}<spring:message code="scriptpath"/>';
		var _stylePath = '${pageContext.request.contextPath}<spring:message code="stylepath"/>';
		var _I18N_ = {
			'ST3.lbl.Pagination.PageInfo': '<spring:message code="ST3.lbl.Pagination.PageInfo"/>',
			'ST3.lbl.Search': '<spring:message code="ST3.lbl.Search"/>',
			'ST3.lbl.EmptyTable': '<spring:message code="ST3.lbl.EmptyTable"/>',
			'ST3.lbl.Show': '<spring:message code="ST3.lbl.Show"/>',
			'ST3.lbl.Entries': '<spring:message code="ST3.lbl.Entries"/>',
			'ST3.lbl.More': '<spring:message code="ST3.lbl.More"/>'
		};
		

		var calendarImgPath = _imagePath + "icons/calendar.png";
		
		ST3Lib.dialog.btn.ok = '<spring:message code="ST3.dialog.ok"/>';
		ST3Lib.dialog.btn.cancel = '<spring:message code="ST3.dialog.cancel"/>';
		ST3Lib.validate.msgcode['MSTD0031AERR'] = '<spring:message code="MSTD0031AERR"/>';
		ST3Lib.validate.msgcode['MSTD0043AERR'] = '<spring:message code="MSTD0043AERR"/>';
		ST3Lib.validate.msgcode['MSTD0053AERR'] = '<spring:message code="MSTD0053AERR"/>';
		
		// Set windows size to full screen
		$(function(){
			moveTo(0,0);
			resizeTo(screen.width,screen.height);
		});
	</script>
	
</head>

<body>
  	<tiles:insertAttribute name="header" />
	<div class="mainbody">
		<div id="content-panel">
			<views:messageBar/>
			<tiles:insertAttribute name="body" />
		</div>
	</div>
	<tiles:insertAttribute name="footer" />
</body>
</html>