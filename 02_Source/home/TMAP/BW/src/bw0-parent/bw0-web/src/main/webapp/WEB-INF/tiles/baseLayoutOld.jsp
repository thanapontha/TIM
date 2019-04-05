<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>

<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<META http-equiv="Pragma" CONTENT="no-cache"> 
	<META http-equiv="Expires" CONTENT="-1">
	<title><tiles:insertAttribute name="title" ignore="true" /></title>	
	<link rel="stylesheet" href="${pageContext.request.contextPath}<spring:message code="stylehotsneaks"/>" media="all" />	
	<link rel="icon" href="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/favicon.ico" type="image/x-icon" />
	<views:style src="styles.css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}<spring:message code="fontsizestylepath"/>" media="all" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}<spring:message code="monthpickeruicss"/>" media="all" />
	
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjs"/>"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryuicss"/>"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjspath"/>jquery.filter_input.js"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjspath"/>jquery.inputmask.bundle.js"></script>
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjspath"/>jquery.cookie.js"></script>
	<!--[if IE 6]><script src="${pageContext.request.contextPath}<spring:message code="jquerybgiframe"/>"></script><![endif]-->
	<script src="${pageContext.request.contextPath}<spring:message code="jqueryjspath"/>jquery.fixedheadertable.min.js"></script>
	<views:script src="st3.lib.js"/>
	<views:script src="gwrds.lib.js"/>
	<views:script src="gwrds.lib.ext.js"/>
	<views:script src="bootstrap.min.js"/>
 	<views:style src="bootstrap.min.css" />
 	<views:style src="bootstrap-reboot.min.css" />
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
		//ST3Lib.compareTime._getTimeServer = ('${getTimeServer}'=="") ? '${pageContext.request.contextPath}<spring:message code="gettimeserver.url"/>':'${pageContext.request.contextPath}${getTimeServer}';//gettimeserver.url /*attribute=getTimeServer
		//ST3Lib.compareTime._getTimeServer = (/(http|https):\/\/[^\/]+/).exec(window.location.href)[0] + ST3Lib.compareTime._getTimeServer; //fixed for difference domain
	</script>
	<!--[if IE 5]><script type="text/javascript">window['isIE5'] = true;window['IEVersion']=5;</script><![endif]-->
	<!--[if IE 6]><script type="text/javascript">window['isIE6'] = true;window['IEVersion']=6;</script><![endif]-->
	<!--[if IE 7]><script type="text/javascript">window['isIE7'] = true;window['IEVersion']=7;</script><![endif]-->
	<!--[if IE 8]><script type="text/javascript">window['isIE8'] = true;window['IEVersion']=8;</script><![endif]-->
	<!--[if IE 9]><script type="text/javascript">window['isIE9'] = true;window['IEVersion']=9;</script><![endif]-->
	<!--[if IE]><script type="text/javascript">window['isIE'] = true;window['IEVersion']?'':window['isIE6']=true;</script><![endif]-->
	
</head>

<body>
	<div id="main" class="overflow-hidden autoheight" >
		<tiles:insertAttribute name="header" />
		<tiles:insertAttribute name="menu" />
		<div id="content-panel" class="iframe overflow-hidden autoheight" >
			<views:messageBar/>
			<tiles:insertAttribute name="body" />
		</div>
		<tiles:insertAttribute name="footer" />
	</div>
	<a id="lureTab" onfocus="$('input,select,teatarea,button,a').filter(':tabbable:first').focus();" 
		href="#" style="display:inline-block;position:absolute;left:-100px;top:-100px;z-index:100;">&nbsp;</a>
</body>
</html>