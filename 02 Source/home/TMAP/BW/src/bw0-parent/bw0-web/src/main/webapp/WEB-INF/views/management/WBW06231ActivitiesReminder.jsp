<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<views:script src="json2.js"/>
<views:script src="jquery.caret.1.02.min.js"/>
<views:script src="jquery-ui-1.9.1/jquery.dataTables.js"/>
<views:style src="jquery.dataTables.css"/>

<views:script src="Chart.min.js"/>

<views:style src="gwrds/WBW04110.css"/>
<script>
	mappingPath = '${_mappingPath}';
	firstResult = '${form.firstResult}';
	rowsPerPage = '${form.rowsPerPage}';
	
	$(function(){
		
	});
	
</script>
<style>

.centered {
    position: absolute;
    top: 60%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: .9rem;
}
.cal-container{
	position: relative;
	text-align: center;
}

.table-bordered td, .table-bordered th{
	border-right: 2px dotted #dee2e6;
}
</style>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW04110.js?t=${currentTimestamp}"/>

<form>	
	<div id="screen-panel" class="container-fluid mb-5 pt-2">
		<div class="row">
			<div class="col-12">
			<div class="table-responsive">
			<table id="tblOverallSOPSetup" class="table table-bordered">
				<tr>
					<td style="min-width:150px;">&nbsp;</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">Jan</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">Feb</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">Mar</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">Apr</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">May</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">Jun</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">Jul</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">Aug</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="text-left">SMS</td>
					<td>
						&nbsp;
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/sms_icon.png" width="40"/>
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
				<tr>
					<td class="text-left">Letter printing</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
					</td>
					<td class="center">
						<img class="pb-1" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
						<br>
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
					</td>
					<td class="center">
						<img class="pb-1" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
						<br>
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
					</td>
					<td>
						&nbsp;
					</td>
					<td class="center">
						<img class="pb-1" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
						<br>
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
					</td>
				</tr>
				<tr>
					<td class="text-left">E-Mail</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
					<td>
						&nbsp;
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/mail_icon.png" width="40"/>
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/mail_icon.png" width="40"/>
					</td>
					<td>
						&nbsp;
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/mail_icon.png" width="40"/>
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/mail_icon.png" width="40"/>
					</td>
				</tr>
				<tr>
					<td class="text-left" rowspan="4">Social Media</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/line_icon.png" width="40"/>
					</td>
					<td>
						&nbsp;
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/line_icon.png" width="40"/>
					</td>
					<td>
						&nbsp;
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/line_icon.png" width="40"/>
					</td>
					<td>
						&nbsp;
					</td>
					<td class="center">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/line_icon.png" width="40"/>
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
			</table>
			</div>
			</div>
		</div>
		<div class="d-flex justify-content-end">
			<sc2:button functionId="BW0623" 
					    screenId="WBW06231" 
					    buttonId="WBW06231Confirm"
						type="button" 
						value="Confirm" 
						styleClass="btn btn-primary btn-md" 
						secured="false" />
		</div>
	</div>
</form>