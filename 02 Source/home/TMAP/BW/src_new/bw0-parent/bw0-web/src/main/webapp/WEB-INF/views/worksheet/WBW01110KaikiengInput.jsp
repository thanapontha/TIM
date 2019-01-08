<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="datatables"
	uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>
<%@page import="th.co.toyota.bw0.api.constants.AppConstants"%>

<views:script src="json2.js"/>
<views:script src="jquery.form.js"/>

<views:script src="jquery.caret.1.02.min.js" />
<views:script src="jquery-ui-1.9.1/jquery.dataTables.js" />
<views:style src="jquery.dataTables.css" />

<views:style src="gwrds/WBW01110.css" />
<script>
	mappingPath = '${_mappingPath}';
	version = '${form.version}';
	TDEM = '<%=AppConstants.COMPANY_CD_TDEM%>';
	downloadTimingCheckStatus = ${downloadTimingCheckStatus};
	uploadTimingCheckStatus = ${uploadTimingCheckStatus};
	
	MSTD0031AERR = '<spring:message code="MSTD0031AERR"></spring:message>';
	MSTD0003ACFM = '<spring:message code="MSTD0003ACFM"></spring:message>';
	MBW00002ACFM = '<spring:message code="MBW00002ACFM"></spring:message>';
	MSTD0050AERR = '<spring:message code="MSTD0050AERR"></spring:message>';
	MBW00006ACFM = '<spring:message code="MBW00006ACFM" arguments="Volume has been changed,|adjust" argumentSeparator="|"></spring:message>';
	MBW00004ACFM_SubmitKaikieng = '<spring:message code="MBW00004ACFM" arguments="submit Kaikieng data"></spring:message>';
	MBW00004ACFM_FixKaikieng = '<spring:message code="MBW00004ACFM" arguments="fix Kaikieng data"></spring:message>';
	MBW00004ACFM_RejectKaikieng = '<spring:message code="MBW00004ACFM" arguments="reject Kaikieng data back to TMAP-MS"></spring:message>';
	
	var menucolumn = [ "", 5, 0, 'CheckDecimalFormat', '99999' ]; 
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW01110.js?t=${currentTimestamp}" />
<div id="screen-panel" style="height: 800px; padding-top: 4px;">
	<div id="search-criteria"
		style="border-bottom: 2px solid #AAA; padding-bottom: 4px;">
		<form:form method="post" id="search-form"
			action="${_mappingPath}/search" ajax="searchFinish"
			ajax-loading-target="#screen-panel"
			validate-error="searchValidateError">
			
			<div id="search-field" style="zoom: 1; position: relative;">
				<table style="width: 100%;">
					<colgroup>
						<col width="10%">
						<col width="15%">
						<col width="10%">
						<col width="15%">
						<col width="10%">
						<col width="40%">
					</colgroup>
					<tbody>
						<tr>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="getsudoMonthSearch">
									<spring:message code="BW0.WBW01110.Label.GetsudoMonth" />:
								</label>
							</td>
							<td>
								<input name="getsudoMonthSearch" id="getsudoMonthSearch"
									value="${form.getsudoMonthSearch}" type="text" maxlength="6"
									class="MandatoryField" style="width: 50px; text-align: left;"
									tabindex="1" />
							</td>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="vehiclePlantSearch">
									<spring:message code="BW0.WBW01110.Label.VehiclePlant" />:
								</label>
							</td>
							<td>
								<select name="vehiclePlantSearch" id="vehiclePlantSearch"
									class="MandatoryField" style="width: 100%; text-align: left;"
									tabindex="3">
									<option value=""><spring:message code="BW0.common.combobox.select" /></option>
									<c:forEach items="${form.vehiclePlantList}" var="item">
										<option value="${item.stValue}"><c:out
												value="${item.stLabel}" /></option>
									</c:forEach>
								</select>
							</td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="timingSearch">
									<spring:message code="BW0.WBW01110.Label.Timing" />:
								</label>
							</td>
							<td>
								<select name="timingSearch" id="timingSearch"
									class="MandatoryField" style="width: 50%; text-align: left;"
									tabindex="2">
									<option value=""><spring:message code="BW0.common.combobox.select" /></option>
									<c:forEach items="${form.timingList}" var="item">
										<option value="${item.stValue}"><c:out
												value="${item.stLabel}" /></option>
									</c:forEach>
								</select>
							</td>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="vehicleModelSearch">
									<spring:message code="BW0.WBW01110.Label.VehicleModel" />:
								</label>
							</td>
							<td>
								<select name="vehicleModelSearch" id="vehicleModelSearch"
									class="MandatoryField" style="width: 100%; text-align: left;"
									tabindex="4">
									<option value=""><spring:message code="BW0.common.combobox.select" /></option>
									<c:forEach items="${form.vehicleModelList}" var="item">
										<option value="${item.stValue}"><c:out value="${item.stLabel}" /></option>
									</c:forEach>
								</select>
							</td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td colspan="6" align="right">
								<div id="search-panel"
									style="display: inline; zoom: 1; position: relative;">
									<spring:message code="ST3.WST33060.Label.BtnSearch"
										var="WBW01110Search" />
									<sc2:button functionId="BW0111" screenId="WBW01110"
										buttonId="WBW01110Search" type="button" tabIndex="5"
										value="${WBW01110Search}" style="width:80px;"
										styleClass="button" onClick="clickSearch();" />
									<spring:message code="ST3.WST33060.Label.BtnClear"
										var="WBW01110Clear" />
									<sc2:button functionId="BW0111" screenId="WBW01110"
										buttonId="WBW01110Clear" type="button" tabIndex="6"
										value="${WBW01110Clear}" style="width:80px;"
										styleClass="button" secured="false" onClick="clearSearch();" />
								</div>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="clear"></div>
		</form:form>
	</div>
	
	<div id="operation-panel" style="padding-top: 5px;">
		<div id="operation-download" style="display: inline; zoom: 1; position: relative; white-space: nowrap; float: left; padding-top: 4px;">
			<spring:message code="BW0.WBW01110.Label.BtnDownload" var="WBW01110Download" />
			<sc2:button functionId="BW0111" screenId="WBW01110"
							buttonId="WBW01110Download" type="button"
							value="${WBW01110Download}" styleClass="button"
							style="width:80px;" onClick="doDownload();" />
		</div>
		<div id="operation-upload" class="operation-upload">
			<spring:message code="BW0.WBW01110.Label.BtnBrowse" var="WBW01110Browse" />
			<spring:message code="BW0.WBW01110.Label.BtnUpload" var="WBW01110Upload" />
			<form id="upload-form" name="upload-form" 
							method="post"
							action="${_mappingPath}/upload"
							enctype="multipart/form-data"
							validate-error="searchValidateError">
				<div style="margin-top:0px;">					
					<label class="label" for="WBW01110Browse" style="display:none"><spring:message code="BW0.WBW01110.Label.FileName" /></label>
					<sc2:button functionId="BW0111" screenId="WBW01110"
						buttonId="WBW01110Browse" type="file" value="${WBW01110Browse}"
						styleClass="button MandatoryField" style="padding-top: 2.0px;width:400px;"
						onClick="" />
					<sc2:button functionId="BW0111" screenId="WBW01110"
						buttonId="WBW01110Upload" type="button" value="${WBW01110Upload}"
						styleClass="button" style="width:80px;" onClick="doUpload();" />
				</div>
			</form>
		</div>
		<div id="operation-btn" style="display: inline; zoom: 1; position: relative; white-space: nowrap; float: right; padding-top: 4px;">
			<spring:message code="BW0.WBW01110.Label.BtnRejectKaikieng" var="WBW01110RejectKaikieng" />
			<spring:message code="STD.WSTD3060.Label.BtnEdit" var="WBW01110Edit" />
			<spring:message code="BW0.WBW01110.Label.BtnSubmit" var="WBW01110Submit" />
			<spring:message code="BW0.WBW01110.Label.BtnFix" var="WBW01110Fix" />

			<sc2:button functionId="BW0111" screenId="WBW01110"
				buttonId="WBW01110RejectKaikieng" type="button" value="${WBW01110RejectKaikieng}"
				styleClass="button" style="width:120px;" onClick="rejectKaikieng();" />
			<sc2:button functionId="BW0111" screenId="WBW01110"
				buttonId="WBW01110Edit" type="button" value="${WBW01110Edit}"
				styleClass="button" style="width:80px;" onClick="editObject();" />
			<sc2:button functionId="BW0111" screenId="WBW01110"
				buttonId="WBW01110Fix" type="button" value="${WBW01110Fix}"
				styleClass="button" style="width:80px;" onClick="fixKaikieng();" />
			<sc2:button functionId="BW0111" screenId="WBW01110"
				buttonId="WBW01110Submit" type="button" value="${WBW01110Submit}"
				styleClass="button" style="width:80px;" onClick="submitKaikieng();" />
		</div>
	</div>
	<div style="display:block;"><div style="border-bottom: solid #AAA 2px;margin-top:45px;"></div></div>
	<div id="search-criteria-label" style="display: -webkit-flex;" class="hide">
		<div class="criteria-label-item" style="width:230px;"><label class="label"><spring:message code="BW0.WBW01110.Label.GetsudoMonth"/> : <span id="getsudoMonthCriteria">-</span></label></div>
		<div class="criteria-label-item" style="width:230px;"><label class="label"><spring:message code="BW0.WBW01110.Label.Timing"/> : <span id="timingCriteria">-</span></label></div>
		<div class="criteria-label-item" style="width:230px;"><label class="label"><spring:message code="BW0.WBW01110.Label.VehiclePlant"/> : <span id="vehiclePlantCriteria">-</span></label></div>
		<div class="criteria-label-item" style="width:230px;"><label class="label"><spring:message code="BW0.WBW01110.Label.VehicleModel"/> : <span id="vehicleModelCriteria">-</span></label></div>
	</div>

 	<div id="data-head-panel" class="container" style="border: none;">	
		<div id="divresult" style="padding-left : 5px;padding-right : 5px;padding-top : 0px;">
			<div id="search-result" class="search-result-height" style="overflow: auto;">
				<div id="dataList" class="container" style="height: 100%;border: none; padding-top: 5px; padding-left: 10px;" >
					<form id="result-list-form" 
							action="dataList" 
							method="post"  
							ajax="updateObjectFinish" 
							ajax-loading-target="#screen-panel" 
				   			validate-error="saveValidateError"
				   			style="">
				   		
				   		<input type="hidden" name="allSelect" id="allSelect" /> 
						<input type="hidden" name="rowLength" id="rowLength" />
				   		<input type="hidden" name="getsudoMonths" id="getsudoMonths" />
				   		<input type="hidden" name="updateKeySet" id="updateKeySet" />
				   		<input type="hidden" name="kaikiengStatus" id="kaikiengStatus" />
				   		<input type="hidden" name="enableFixButton" id="enableFixButton" />
				   		<input type="hidden" name="dataNotFound" id="dataNotFound" />		   		
				   		
				   		<table id="result" class="result dataTable" style="width: auto; border: 0; float: left;">
							<thead>
								<tr role="row" id="result-header">

								</tr>
							</thead>
							<tbody id="result-body">

							</tbody>
						</table>					
					</form>					
				</div>		
			</div>
		</div>
	</div> 
	<div>
		<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
	</div>
	<div id="save-panel" class="hide" style="display: inline; zoom: 1; position: relative; float: right; padding-top: 5px;">
		<spring:message code="STD.WSTD3060.Label.BtnSave" var="WBW01110Save" />
		<sc2:button functionId="BW0111" screenId="WBW01110"
			buttonId="WBW01110Save" type="button" value="${WBW01110Save}"
			styleClass="button" style="width:80px;"
			onClick="saveAddEditObject();" />
		<spring:message code="ST3.WST33060.Label.BtnCancel"
			var="WBW01110Cancel" />
		<sc2:button functionId="BW0111" screenId="WBW01110"
			buttonId="WBW01110Cancel" type="button" value="${WBW01110Cancel}"
			styleClass="button" secured="false" style="width:80px;"
			onClick="cancelAddEditObject();" />
	</div>
	
</div>