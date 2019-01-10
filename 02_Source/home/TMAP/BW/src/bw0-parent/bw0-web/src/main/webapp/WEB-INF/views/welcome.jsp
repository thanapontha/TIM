<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
	.WelcomeLabel {
	font-family: "Times New Roman", Times, serif;
	font-size: 40px; 
	font-weight: bold; 
	color: #37259E; 
	}
	
	.WelcomeLabelTMT {
	font-family: "Times New Roman", Times, serif;
	font-size: 24px; 
	font-weight: bold; 
	color: #0000EE; 
	}
	
	.textTMT {
	font-family: "Times New Roman", Times, serif;
	font-size: 16px; 
	font-weight: bold;
	}
	
	.textTMTBlue {
	font-family: "Times New Roman", Times, serif;
	font-size: 16px; 
	font-weight: bold;
	color: #0000EE; 
	}
	
	.textTMTRed {
	font-family: "Times New Roman", Times, serif;
	font-size: 16px; 
	font-weight: bold;
	color: #37259E; 
	}
	
	.textHeadTBTMT {
	font-family: "Times New Roman", Times, serif;
	font-size: 14px; 
	font-weight: bold;
	color: #FFFFFF;
	}
	
	.textBodyTBTMT {
	font-family: "Times New Roman", Times, serif;
	font-size: 12px; 
	}
	
	ul {
    list-style-position: inside;
	}
</style>

<script type="text/javascript">
	$(function() {
		window.name = "myHome";
	});
</script>
	
<c:if test="${isApplicationUser == false }">
	<script type="text/javascript">
	(function($){
		$(document).ready(function() {
			var realConfirm = window.confirm;
			window.confirm = null;
			
			function UnPopIt()  { /* nothing to return */ }
			
			var dialogOption = {
					width		: 450,
					buttons		: 
						[{
					        text: '<spring:message code="STD.dialog.ok" />' ,
					        click: function() {
					        	var self = $(this);
					        	self.siblings('.ui-dialog-buttonpane').find('input,button').prop('disabled', true);
								self.dialog('close');
								
								window.onbeforeunload = null;
								$("#left-menu-panel").remove();
								var win=open("","_self", "");
								win.close();
					        }
					    }]	
				};
			ST3Lib.dialog.confirm('<spring:message code="MSTD0012AERR" arguments="Employee No. of User login, Employee Master." />', 'MSTD0012AERR', dialogOption);
		});
	})(ST3Lib.$);;
	</script>
</c:if>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />	
<TABLE width="100%" border="0">
	<TR>
		<TD width="7%">&nbsp;</TD>
		<TD width="90%">&nbsp;</TD>
		<TD width="5%">&nbsp;</TD>
	</TR>
	<TR>
		<TD height="460">&nbsp;</TD>
		<TD align="center">
			<P CLASS="WelcomeLabel"><SPAN CLASS="style9">W</SPAN>elcome</P>
			<P CLASS="WelcomeLabel"><SPAN CLASS="style9">T</SPAN>o</P>
			<P CLASS="WelcomeLabel"><SPAN CLASS="style8"><spring:message code="BW0.lbl.Application" /> System</SPAN></P>	
		</TD>
		<TD>&nbsp;</TD>
	</TR>
	<TR>
		<TD height="30">&nbsp;</TD>
		<TD>&nbsp;</TD>
		<TD>&nbsp;</TD>
	</TR>
</TABLE>