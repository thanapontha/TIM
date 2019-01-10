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
	MSTD0031AERR = '<spring:message code="MSTD0031AERR"></spring:message>';
	MSTD0059AERR = '<spring:message code="MSTD0059AERR"></spring:message>';
	MBW00001ACFM = '<spring:message code="MBW00001ACFM"></spring:message>';
	MSTD0101AINF = '<spring:message code="MSTD0101AINF"></spring:message>';
	MSTD0003ACFM = '<spring:message code="MSTD0003ACFM"></spring:message>';
	MSTD1017AERR = '<spring:message code="MSTD1017AERR"></spring:message>';
	MBW00002ACFM = '<spring:message code="MBW00002ACFM"></spring:message>';
	MSTD0022AERR = '<spring:message code="MSTD0022AERR"></spring:message>';
	MSTD0101AINF = '<spring:message code="MSTD0101AINF"></spring:message>';
	MSTD0003ACFM = '<spring:message code="MSTD0003ACFM"></spring:message>';
	MSTD1016AERR = '<spring:message code="MSTD1016AERR"></spring:message>';
	MSTD0001ACFM = '<spring:message code="MSTD0001ACFM"></spring:message>';
	MSTD1005AERR = '<spring:message code="MSTD1005AERR"></spring:message>';
	MSTD0090AINF = '<spring:message code="MSTD0090AINF"></spring:message>';
	
	
	$(function(){
		
	});
	
</script>
<style>
#tblOverallSOPSetup tr td {
	text-align: center;
}

/* The heart of the matter */
.testimonial-group > .row {
  overflow-x: auto;
  white-space: nowrap;
}
.testimonial-group > .row > .col-xs-4 {
  display: inline-block;
  float: none;
}

/* Decorations */
.col-4 { color: #fff; font-size: 48px; padding-bottom: 20px; padding-top: 18px; }
.col-4:nth-child(3n+1) { background: #c69; }
.col-4:nth-child(3n+2) { background: #9c6; }
.col-4:nth-child(3n+3) { background: #69c; }

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
</style>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW04110.js?t=${currentTimestamp}"/>

<form>
	<input name="firstResult" type="hidden" value="0" default="0" />
	<input name="rowsPerPage" type="hidden" value="10" default="10" />
	<input name="messageResult" id="messageResult"  type="hidden" />
	
	<div id="screen-panel" class="container-fluid mb-5">
		<div class="d-flex flex-wrap justify-content-around bd-highlight">
		    <div class="p-2 bd-highlight"><button type="button" class="btn btn-primary btn-md disabled">1. Overall SOP Set up</button></div>
		    <div class="p-2 bd-highlight"><button type="button" class="btn btn-secondary btn-md" onclick="location.href = 'InsuranceRenewPortfolioControl';">2. INS Renew Portfolio control</button></div>
		    <div class="p-2 bd-highlight"><button type="button" class="btn btn-secondary btn-md" onclick="location.href = 'TeleSalesTeamManagement';">3. Sales Team Management</button></div>
		</div>
		<div class="row">
			<div class="col-12">
			<div class="table-responsive">
			<table id="tblOverallSOPSetup" class="table table-bordered">
				<tr>
					<td style="min-width:150px;">&nbsp;</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N-11</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N-6</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N-4</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N-3</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N-2</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N-1</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N</div>
						</div>
					</td>
					<td style="min-width:100px;">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N+1</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="text-left" rowspan="4">1. Mandatory Call<br/>(Fixed by TMT cannot change)</td>
					<td rowspan="4">&nbsp;</td>
					<td><img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/></td>
					<td rowspan="4">&nbsp;</td>
					<td><img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/></td>
					<td><img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/></td>
					<td><img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/></td>
					<td><img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/></td>
					<td rowspan="4">&nbsp;</td>
				</tr>
				<tr>
					<td><select class="form-control form-control-sm"><option value="0">Telemarketing</option><option value="1">Salesperson</option><option value="2">Other</option></select></td>
					<td><select class="form-control form-control-sm"><option value="0">Telemarketing</option><option value="1">Salesperson</option><option value="2">Other</option></select></td>
					<td><select class="form-control form-control-sm"><option value="0">Telemarketing</option><option value="1">Salesperson</option><option value="2">Other</option></select></td>
					<td><select class="form-control form-control-sm"><option value="0">Telemarketing</option><option value="1">Salesperson</option><option value="2">Other</option></select></td>
					<td><select class="form-control form-control-sm"><option value="0">Telemarketing</option><option value="1">Salesperson</option><option value="2">Other</option></select></td>
				</tr>
				<tr>
					<td><select class="form-control form-control-sm"><option value="0">N-6</option></select></td>
					<td><select class="form-control form-control-sm"><option value="0">N-3</option></select></td>
					<td><select class="form-control form-control-sm"><option value="0">N-2</option></select></td>
					<td><select class="form-control form-control-sm"><option value="0">N-1</option></select></td>
					<td><select class="form-control form-control-sm"><option value="0">N</option></select></td>
				</tr>
				<tr>
					<td>
						<select class="form-control form-control-sm">
						<option value="0">Inform of INS renewal service</option>
						<option value="0">Offer transfer premium</option>
						<option value="0">Offer NCB premium</option>
						<option value="0">Follow up call to closing</option>
						<option value="0">Other</option>
					</select>
					</td>
					<td>
						<select class="form-control form-control-sm">
						<option value="0">Inform of INS renewal service</option>
						<option value="0">Offer transfer premium</option>
						<option value="0">Offer NCB premium</option>
						<option value="0">Follow up call to closing</option>
						<option value="0">Other</option>
					</select>
					</td>
					<td>
						<select class="form-control form-control-sm">
						<option value="0">Inform of INS renewal service</option>
						<option value="0">Offer transfer premium</option>
						<option value="0">Offer NCB premium</option>
						<option value="0">Follow up call to closing</option>
						<option value="0">Other</option>
					</select>
					</td>
					<td>
						<select class="form-control form-control-sm">
						<option value="0">Inform of INS renewal service</option>
						<option value="0">Offer transfer premium</option>
						<option value="0">Offer NCB premium</option>
						<option value="0">Follow up call to closing</option>
						<option value="0">Other</option>
					</select>
					</td>
					<td>
						<select class="form-control form-control-sm">
						<option value="0">Inform of INS renewal service</option>
						<option value="0">Offer transfer premium</option>
						<option value="0">Offer NCB premium</option>
						<option value="0">Follow up call to closing</option>
						<option value="0">Other</option>
					</select>
					</td>
				</tr>
				<tr>
					<td class="text-left">2. DLR Self Set up Activity</td>
					<td colspan="3">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N-6</div>
						</div>
					</td>
					<td colspan="3">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N-2</div>
						</div>
					</td>
					<td colspan="2">
						<div class="cal-container">
							<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="50"/>
							<div class="centered">N</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="text-left" rowspan="4">Call</td>
					<td colspan="3">
						<select class="form-control form-control-sm" style="width:50px;"><option value="Y">Y</option><option value="N">N</option></select>
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
					</td>
					<td colspan="3">
						<select class="form-control form-control-sm" style="width:50px;"><option value="Y">Y</option><option value="N">N</option></select>
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
					</td>
					<td colspan="2">
						<select class="form-control form-control-sm" style="width:50px;"><option value="Y">Y</option><option value="N">N</option></select>
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
					</td>
				</tr>
				<tr>
					<td colspan="3"><select class="form-control form-control-sm"><option value="0">Telemarketing</option><option value="1">Salesperson</option><option value="2">Other</option></select></td>
					<td colspan="3"><select class="form-control form-control-sm"><option value="0">Telemarketing</option><option value="1">Salesperson</option><option value="2">Other</option></select></td>
					<td colspan="2"><select class="form-control form-control-sm"><option value="0">Telemarketing</option><option value="1">Salesperson</option><option value="2">Other</option></select></td>
				</tr>
				<tr>
					<td colspan="3"><select class="form-control form-control-sm"><option value="0">N-6</option></select></td>
					<td colspan="3"><select class="form-control form-control-sm"><option value="0">N-2</option></select></td>
					<td colspan="2"><select class="form-control form-control-sm"><option value="0">N</option></select></td>
				</tr>
				<tr>
					<td colspan="3">
						<select class="form-control form-control-sm">
						<option value="0">Inform of INS renewal service</option>
						<option value="0">Offer transfer premium</option>
						<option value="0">Offer NCB premium</option>
						<option value="0">Follow up call to closing</option>
						<option value="0">Other</option>
					</select>
					</td>
					<td colspan="3">
						<select class="form-control form-control-sm">
						<option value="0">Inform of INS renewal service</option>
						<option value="0">Offer transfer premium</option>
						<option value="0">Offer NCB premium</option>
						<option value="0">Follow up call to closing</option>
						<option value="0">Other</option>
					</select>
					</td>
					<td colspan="2">
						<select class="form-control form-control-sm">
						<option value="0">Inform of INS renewal service</option>
						<option value="0">Offer transfer premium</option>
						<option value="0">Offer NCB premium</option>
						<option value="0">Follow up call to closing</option>
						<option value="0">Other</option>
					</select>
					</td>
				</tr>
				<tr>
					<td class="text-left">SMS</td>
					<td colspan="3">
						&nbsp;
					</td>
					<td colspan="5">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/sms_icon.png" width="40"/>
					</td>
				</tr>
				<tr>
					<td class="text-left">Letter printing</td>
					<td colspan="3">
						&nbsp;
					</td>
					<td colspan="5">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/letter_icon.png" width="40"/>
					</td>
				</tr>
				<tr>
					<td class="text-left">E-Mail</td>
					<td colspan="3">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/mail_icon.png" width="40"/>
					</td>
					<td colspan="5">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td class="text-left" rowspan="4">Social Media</td>
					<td colspan="3">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/line_icon.png" width="40"/>
					</td>
					<td colspan="5">
						<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/line_icon.png" width="40"/>
					</td>
				</tr>
			</table>
			</div>
			</div>
		</div>
		<div class="d-flex justify-content-end">
			<sc2:button functionId="BW0621" 
					    screenId="WBW06211" 
					    buttonId="WBW06211Confirm"
						type="button" 
						value="Confirm" 
						styleClass="btn btn-primary btn-md" 
						secured="false" />
		</div>
	</div>
</form>