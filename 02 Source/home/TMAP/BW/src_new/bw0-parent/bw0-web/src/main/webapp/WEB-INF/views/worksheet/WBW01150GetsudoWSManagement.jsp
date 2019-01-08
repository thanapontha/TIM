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

<views:style src="gwrds/WBW01150.css" />
<script>
	mappingPath = '${_mappingPath}';
	currentMonthYear = '${currentMonthYear}';
	downloadTimingCheckStatus = ${downloadTimingCheckStatus};
	uploadTimingCheckStatus = ${uploadTimingCheckStatus};
	
	MSTD0031AERR = '<spring:message code="MSTD0031AERR"></spring:message>';
	MSTD0003ACFM = '<spring:message code="MSTD0003ACFM"></spring:message>';
	MBW00002ACFM = '<spring:message code="MBW00002ACFM"></spring:message>';
	MSTD0050AERR = '<spring:message code="MSTD0050AERR"></spring:message>';
	
	MBW00004ACFM_Complete = '<spring:message code="MBW00004ACFM" arguments="complete data"></spring:message>';
	MBW00004ACFM_Reset = '<spring:message code="MBW00004ACFM" arguments="reset data to Kaikieng data input by TMAP-MS"></spring:message>';
	MBW00005ACFM_Upload = '<spring:message code="MBW00005ACFM" arguments="The Worksheet was already uploaded on {Date_Time}|re-upload the Worksheet" argumentSeparator="|"></spring:message>';
	MBW00006ACFM_CompleteReset = '<spring:message code="MBW00006ACFM" arguments="Data already completed in this Getsudo Month.|reset data to Kaikieng data input by TMAP-MS" argumentSeparator="|"></spring:message>';
	
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW01150.js?t=${currentTimestamp}" />
<div id="screen-panel" style="height: 800px; padding-top: 4px;">
	<div id="search-criteria"
		style="border-bottom: 2px solid #AAA; padding-bottom: 4px;">
		<form:form method="post" id="search-form"
			action="${_mappingPath}/search" ajax="searchFinish"
			ajax-loading-target="#screen-panel"
			validate-error="searchValidateError">
			
			<input type="hidden" name="updateKeySet" id="updateKeySet" />
			
			<div id="search-field" style="zoom: 1; position: relative;">
				<table style="width: 100%;">
					<colgroup>
						<col width="10%">
						<col width="10%">
						<col width="7%">
						<col width="10%">
						<col width="10%">
						<col width="53%">
					</colgroup>
					<tbody>
						<tr>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="getsudoMonthSearch">
									<spring:message code="BW0.WBW01150.Label.GetsudoMonth" />:
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
								<label class="label" for="timingSearch">
									<spring:message code="BW0.WBW01150.Label.Timing" />:
								</label>
							</td>
							<td>
								<select name="timingSearch" id="timingSearch"
									class="MandatoryField" style="width: 80%; text-align: left;"
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
								<label class="label" for="vehiclePlantSearch">
									<spring:message code="BW0.WBW01150.Label.VehiclePlant" />:
								</label>
							</td>
							<td>
								<select name="vehiclePlantSearch" id="vehiclePlantSearch"
									class="MandatoryField" style="width: 50%; text-align: left;"
									tabindex="3">
									<option value=""><spring:message code="BW0.common.combobox.select" /></option>
									<c:forEach items="${form.vehiclePlantList}" var="item">
										<option value="${item.stValue}"><c:out
												value="${item.stLabel}" /></option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td colspan="6" align="right">
								<div id="search-panel"
									style="display: inline; zoom: 1; position: relative;">
									<spring:message code="ST3.WST33060.Label.BtnSearch"
										var="WBW01150Search" />
									<sc2:button functionId="BW0115" screenId="WBW01150"
										buttonId="WBW01150Search" type="button" tabIndex="4"
										value="${WBW01150Search}" style="width:80px;"
										styleClass="button" onClick="clickSearch();" />
									<spring:message code="ST3.WST33060.Label.BtnClear"
										var="WBW01150Clear" />
									<sc2:button functionId="BW0115" screenId="WBW01150"
										buttonId="WBW01150Clear" type="button" tabIndex="5"
										value="${WBW01150Clear}" style="width:80px;"
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
			<spring:message code="BW0.WBW01150.Label.BtnDownload" var="WBW01150Download" />
			<sc2:button functionId="BW0115" screenId="WBW01150"
							buttonId="WBW01150Download" type="button"
							value="${WBW01150Download}" styleClass="button"
							style="width:80px;" onClick="doDownload();" />
		</div>
		<div id="operation-upload" class="operation-upload">
			<spring:message code="BW0.WBW01150.Label.BtnBrowse" var="WBW01150Browse" />
			<spring:message code="BW0.WBW01150.Label.BtnUpload" var="WBW01150Upload" />
			<spring:message code="BW0.WBW01150.Label.BtnLog" var="WBW01150Log" />
			<form id="upload-form" name="upload-form" 
							method="post"
							action="${_mappingPath}/upload"
							enctype="multipart/form-data"
							validate-error="searchValidateError">
				<div style="margin-top:0px;">					
					<label class="label" for="WBW01150Browse" style="display:none"><spring:message code="BW0.WBW01150.Label.FileName" /></label>
					<sc2:button functionId="BW0115" screenId="WBW01150"
						buttonId="WBW01150Browse" type="file" value="${WBW01150Browse}"
						styleClass="button MandatoryField" style="padding-top: 2.0px;width:400px;"
						onClick="" />
					<sc2:button functionId="BW0115" screenId="WBW01150"
						buttonId="WBW01150Upload" type="button" value="${WBW01150Upload}"
						styleClass="button" style="width:80px;" onClick="doUpload();" />
					<sc2:button functionId="BW0115" screenId="WBW01150"
						buttonId="WBW01150Log" type="button" value="${WBW01150Log}"
						styleClass="button" style="width:80px;" onClick="doOpenLog();" />
				</div>
			</form>
		</div>
		<div id="operation-btn" style="display: inline; zoom: 1; position: relative; white-space: nowrap; float: right; padding-top: 4px;">
			<spring:message code="BW0.WBW01150.Label.BtnComplete" var="WBW01150Complete" />
			<spring:message code="BW0.WBW01150.Label.BtnReset" var="WBW01150Reset" />
			<spring:message code="BW0.WBW01150.Label.BtnCompletedReset" var="WBW01150CompletedReset" />

			<sc2:button functionId="BW0115" screenId="WBW01150"
				buttonId="WBW01150Complete" type="button" value="${WBW01150Complete}"
				styleClass="button" style="width:80px;" onClick="doComplete();" />
			<sc2:button functionId="BW0115" screenId="WBW01150"
				buttonId="WBW01150Reset" type="button" value="${WBW01150Reset}"
				styleClass="button" style="width:80px;" onClick="doReset();" />
			<sc2:button functionId="BW0115" screenId="WBW01150"
				buttonId="WBW01150CompletedReset" type="button" value="${WBW01150CompletedReset}"
				styleClass="button" style="width:120px;" onClick="doCompletedReset();" />
		</div>
	</div>
	<div style="display:block;"><div style="border-bottom: solid #AAA 2px;margin-top:45px;"></div></div>
	<div id="search-criteria-label" style="display: -webkit-flex;" class="">
		<div class="criteria-label-item"><label class="label"><spring:message code="BW0.WBW01150.Label.GetsudoMonth"/> : <span id="getsudoMonthCriteria">-</span></label></div>
		<div class="criteria-label-item"><label class="label"><spring:message code="BW0.WBW01150.Label.Timing"/> : <span id="timingCriteria">-</span></label></div>
		<div class="criteria-label-item"><label class="label"><spring:message code="BW0.WBW01150.Label.VehiclePlant"/> : <span id="vehiclePlantCriteria">-</span></label></div>
	</div>

	<div id="data-head-panel" class="container" style="border: none;">	
		<div id="divresult" style="padding-left : 5px;padding-right : 5px;padding-top : 5px;">
			<div id="search-result" class="" style="height: 360px;">
				<div id="dataList" class="container" style="overflow:auto;height: 360px;border: none;" >
					<form id="result-list-form" 
							action="dataList" 
							method="post"  
							ajax="updateObjectFinish" 
							ajax-loading-target="#screen-panel" 
				   			validate-error="saveValidateError">
		   		
				   		<spring:message code="BW0.WBW01150.Label.No" var="ColNo"/>
						<spring:message code="BW0.WBW01150.Label.VehicleModel" var="ColVehicleModel"/>
						<spring:message code="BW0.WBW01150.Label.Status" var="ColStatus"/>
						<spring:message code="BW0.WBW01150.Label.UpdateBy" var="ColUpdateBy"/>
						<spring:message code="BW0.WBW01150.Label.UpdateDate" var="ColUpdateDate"/>
						<spring:message code="BW0.WBW01150.Label.CompleteStatus" var="ColCompleteStatus"/>	
						
						 
					   <datatables:table id="result" 
				    					data="${dataList}" 
				    					cdn="false" 
				    					row="row" 
				    					cssClass="result" 
				    					paginate="false" 
				    					cssStyle="height:300px;width:100%;"
										info="false" 
										filter="false"
										displayLength="${form.rowsPerPage}" 
										lengthChange="false"
										sort="false"
										>
					    	
					    	<datatables:column title="" cssClass="col1" sortable="false" searchable="false" />
			        		<datatables:column title="${ColNo}" cssClass="col2" cssStyle="text-align:center;" sortable="false" searchable="false"/>
			        		<datatables:column title="${ColVehicleModel}" cssClass="col3" sortable="false" />
							<datatables:column title="${ColStatus}" cssClass="col4" sortable="false" />
							<datatables:column title="${ColUpdateBy}" cssClass="col5" cssStyle="text-align:center;" sortable="false" />
							<datatables:column title="${ColUpdateDate}" cssClass="col6" sortable="false" />
							<datatables:column title="${ColCompleteStatus}" cssClass="col7" sortable="false" />
						</datatables:table>
						
					</form>
					
				</div>		
			</div>
		</div>
	</div> 
	
	<div>
		<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
	</div>
</div>