<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="datatables"
	uri="http://github.com/dandelion/datatables"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<views:script src="json2.js" />
<views:script src="jquery.caret.1.02.min.js" />
<views:script src="jquery-ui-1.9.1/jquery.dataTables.js" />
<views:style src="jquery.dataTables.css" />
<views:script src="jquery.form.js"/>

<views:style src="gwrds/WBW04240.css" />
<script>
	mappingPath = '${_mappingPath}';
	downloadTimingCheckStatus = ${downloadTimingCheckStatus};
	uploadTimingCheckStatus = ${uploadTimingCheckStatus};
	
	MSTD0006ACFM = '<spring:message code="MSTD0006ACFM"></spring:message>';
	MSTD0003ACFM = '<spring:message code="MSTD0003ACFM"></spring:message>';
	MSTD0001ACFM = '<spring:message code="MSTD0001ACFM"></spring:message>';
	MSTD1016AERR = '<spring:message code="MSTD1016AERR"></spring:message>';
	MSTD1017AERR = '<spring:message code="MSTD1017AERR"></spring:message>';
	MBW00001ACFM = '<spring:message code="MBW00001ACFM"></spring:message>';
	MBW00002ACFM = '<spring:message code="MBW00002ACFM"></spring:message>';
	
	MSTD0024AERR = '<spring:message code="MSTD0024AERR"></spring:message>';
	MSTD0031AERR = '<spring:message code="MSTD0031AERR"></spring:message>';
	MBW00011AERR = '<spring:message code="MBW00011AERR"></spring:message>';
	MSTD0050AERR = '<spring:message code="MSTD0050AERR"></spring:message>';

	monthlyCapacity = '<spring:message code="BW0.WBW04240.Label.MonthlyCapacity"></spring:message>';
	dailyCapacity = '<spring:message code="BW0.WBW04240.Label.DailyCapacity"></spring:message>';
	workingdays = '<spring:message code="BW0.WBW04240.Label.WorkingDays"></spring:message>';
	workingtime = '<spring:message code="BW0.WBW04240.Label.WorkingTime"></spring:message>';
	workingshift = '<spring:message code="BW0.WBW04240.Label.WorkingShift"></spring:message>';
	takttime = '<spring:message code="BW0.WBW04240.Label.TaktTime"></spring:message>';
	efficiency = '<spring:message code="BW0.WBW04240.Label.Efficiency"></spring:message>';
	dailyovertime = '<spring:message code="BW0.WBW04240.Label.DailyOvertime"></spring:message>';
	holidayovertime = '<spring:message code="BW0.WBW04240.Label.HolidayOvertime"></spring:message>';
	specialeffort = '<spring:message code="BW0.WBW04240.Label.SpecialEffort"></spring:message>';

	var menucolumn = [ [ monthlyCapacity, 5, 6, 'MandatoryField CheckDecimalFormat', '99999' ],
			[ dailyCapacity, 5, 6, 'MandatoryField CheckDecimalFormat','99999' ],
			[ workingdays, 2, 0, 'MandatoryField CheckDecimalFormat', '99' ],
			[ workingtime, 3, 0, 'MandatoryField CheckDecimalFormat', '999' ],
			[ workingshift, 1, 0, 'MandatoryField CheckDecimalFormat', '9' ],
			[ takttime, 5, 0, 'MandatoryField CheckDecimalFormat', '99.99' ],
			[ efficiency, 6, 0, 'MandatoryField CheckDecimalFormat', '999.99' ],
			[ dailyovertime, 5, 0, 'MandatoryField CheckDecimalFormat','99.99' ],
			[ holidayovertime, 2, 0, 'MandatoryField CheckDecimalFormat','99' ],
			[ specialeffort, 4, 0, 'CheckDecimalFormat', '9999' ] ]; 
</script>
 
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />
 
<views:script src="gwrds/WBW04240.js?t=${currentTimestamp}" />
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
						<col width="20%">
						<col width="20%">
					</colgroup>
					<tbody>
						<tr>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="unitPlantSearch">
									<spring:message code="BW0.WBW04240.Label.UnitPlant" />:
								</label>
							</td>
							<td>
								<select name="unitPlantSearch" id="unitPlantSearch"
									class="MandatoryField" style="width: 100%; text-align: left;"
									tabindex=1>
									<option value=""><spring:message code="BW0.common.combobox.select" /></option>
									<c:forEach items="${form.unitPlantList}" var="item">
										<option value="${item.stValue}"><c:out
												value="${item.stLabel}" /></option>
									</c:forEach>
								</select>
							</td>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="unitParentLineSearch">
									<spring:message code="BW0.WBW04240.Label.ParentLine" />:
								</label>
							</td>
							<td>
								<select name="unitParentLineSearch" id="unitParentLineSearch"
									class="MandatoryField" style="width: 100%; text-align: left;"
									tabindex=2>
									<option value=""><spring:message code="BW0.common.combobox.select" /></option>
									<c:forEach items="${form.unitParentLineList}" var="item">
										<option value="${item.stValue}"><c:out
												value="${item.stLabel}" /></option>
									</c:forEach>
								</select>
							</td>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="getsudoMonthSearch">
									<spring:message code="BW0.WBW04240.Label.GetsudoMonth" />:
								</label>
							</td>
							<td>
								<input name="getsudoMonthSearch" id="getsudoMonthSearch"
									value="${form.getsudoMonthSearch}" type="text" maxlength="6"
									class="MandatoryField" style="width:50px;text-align: left;"
									tabindex=4 />
							</td>
							<td></td>
						</tr>
						<tr>
							<td></td>
							<td></td>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="unitSubLineSearch">
									<spring:message code="BW0.WBW04240.Label.SubLine" />:
								</label>
							</td>
							<td>
								<select name="unitSubLineSearch" id="unitSubLineSearch"
									class="MandatoryField" style="width: 100%; text-align: left;"
									tabindex=3>
									<option value=""><spring:message code="BW0.common.combobox.select" /></option>
									<c:forEach items="${form.unitSubLineList}" var="item">
										<option value="${item.stValue}"><c:out
												value="${item.stLabel}" /></option>
									</c:forEach>
								</select>
							</td>
							<td align="right">
								<span class="MandatoryFieldFont">*</span>
								<label class="label" for="timingSearch">
									<spring:message code="BW0.WBW04240.Label.Timing" />:
								</label>
							</td>
							<td>
								<select name="timingSearch" id="timingSearch"
									class="MandatoryField" style="width: 75%; vtext-align: left;"
									tabindex=5>
									<option value=""><spring:message
											code="BW0.common.combobox.select" /></option>
									<c:forEach items="${form.timingList}" var="item">
										<option value="${item.stValue}"><c:out
												value="${item.stLabel}" /></option>
									</c:forEach>
								</select>
							</td>
							<td></td>
						</tr>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td align="right">
								<div id="search-panel"
									style="display: inline; zoom: 1; position: relative;">
									<spring:message code="ST3.WST33060.Label.BtnSearch"
										var="WBW04240Search" />
									<sc2:button functionId="BW0424" screenId="WBW04240"
										buttonId="WBW04240Search" type="button" tabIndex="6"
										value="${WBW04240Search}" style="width:80px;"
										styleClass="button" onClick="clickSearch();" />
									<spring:message code="ST3.WST33060.Label.BtnClear"
										var="WBW04240Clear" />
									<sc2:button functionId="BW0424" screenId="WBW04240"
										buttonId="WBW04240Clear" type="button" tabIndex="7"
										value="${WBW04240Clear}" style="width:80px;"
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
		<div id="second-operation-panel" style="display: inline; zoom: 1; position: relative; white-space: nowrap; float: left; padding-top: 4px;">
			<spring:message code="ST3.WST33060.Label.BtnEdit" var="WBW04240Edit" />
			<spring:message code="BW0.WBW04240.Label.BtnDownload" var="WBW04240Download" />
			<spring:message code="BW0.WBW04240.Label.BtnBrowse" var="WBW04240Browse" />
			<spring:message code="BW0.WBW04240.Label.BtnUpload" var="WBW04240Upload" />
			<div id="operation-download" style="display: inline; zoom: 1; position: relative; white-space: nowrap; float: left; padding-top: 4px; ">
				<sc2:button functionId="BW0424" screenId="WBW04240"
						buttonId="WBW04240Download" type="button"
						value="${WBW04240Download}" styleClass="button"
						style="width:80px;" onClick="doDownload();" />
			</div>					
			<div id="operation-upload" class="operation-upload">
				<form id="upload-form" name="upload-form" 
						method="post"
						action="${_mappingPath}/upload"
						enctype="multipart/form-data"
						validate-error="searchValidateError">																
						<div style="margin-top:0px;">
						<label class="label" for="WBW04240Browse" style="display:none"><spring:message code="BW0.WBW04240.Label.FileName" /></label>				
							<sc2:button functionId="BW0424" screenId="WBW04240"
								buttonId="WBW04240Browse" type="file" value="${WBW04240Browse}"
								styleClass="button MandatoryField" style="padding-top: 2.0px;width:400px;" />
							<sc2:button functionId="BW0424" screenId="WBW04240"
								buttonId="WBW04240Upload" type="button" value="${WBW04240Upload}"
								styleClass="button" style="width:80px;" onClick="doUpload();" />
						</div>
				</form>
			</div>					
		</div>
		<div id="operation-btn" style="display: inline; zoom: 1; position: relative; white-space: nowrap; float: right; padding-top: 4px;">
			<spring:message code="ST3.WST33060.Label.BtnEdit"
				var="WBW04240Edit" />
			<sc2:button functionId="BW0424" screenId="WBW04240"
				buttonId="WBW04240Edit" type="button" value="${WBW04240Edit}"
				styleClass="button" style="width:80px;" onClick="editObject();" />
		</div>
	</div>

	<div style="display:block;"><div style="border-bottom: solid #AAA 2px;margin-top:53px;"></div></div>
	 
	<div id="search-criteria-label" class="hide">
		<div style="display: -webkit-flex;display: flex;">
			<div class="criteria-label-item" style="width:10%;text-align: right;"><label class="label"><spring:message code="BW0.WBW04240.Label.UnitPlant"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item" style="width:15%"><label class="label"><span id="resultUnitPlantCriteria">-</span></label></div>
			<div class="criteria-label-item" style="width:10%;text-align: right;"><label class="label"><spring:message code="BW0.WBW04240.Label.ParentLine"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item" style="width:15%"><label class="label"><span id="resultUnitParentLineCriteria">-</span></label></div>
			<div class="criteria-label-item" style="width:10%;text-align: right;"><label class="label"><spring:message code="BW0.WBW04240.Label.GetsudoMonth"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item"><label class="label"><span id="resultGetsudoMonthCriteria">-</span></label></div>
		</div>
		<div style="display: -webkit-flex;display: flex;">
			<div class="criteria-label-item" style="width:10%;text-align: right;"><label class="label">&nbsp;</label></div> 
			<div class="criteria-label-item" style="width:15%"><label class="label"></label></div><br/>
			<div class="criteria-label-item" style="width:10%;text-align: right;"><label class="label"><spring:message code="BW0.WBW04240.Label.SubLine"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item" style="width:15%"><label class="label"><span id="resultUnitSubLineCriteria">-</span></label></div>
			<div class="criteria-label-item" style="width:10%;text-align: right;"><label class="label"><spring:message code="BW0.WBW04240.Label.Timing"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item"><label class="label"><span id="resultTimingCriteria">-</span></label></div>
		</div> 
	</div>
	
	<div id="data-head-panel" class="container" style="border: none;">
		<div id="divresult" style="padding-left : 5px;padding-right : 5px;padding-top : 0px;">
			<div id="search-result" class="" style="height: 250px;">
				<div id="dataList" class="container" style="height: 100%;border: none; padding-top: 5px; padding-left: 10px;">
					<!-- FORM  -->
					<form id="result-list-form" action="dataList" method="post"
						ajax="updateObjectFinish" ajax-loading-target="#screen-panel" style="text-align:left;"
						validate-error="saveValidateError">

						<input type="hidden" name="allSelect" id="allSelect" /> 
						<input type="hidden" name="rowLength" id="rowLength" />
						<input type="hidden" name="unitPlantSearch" id="unitPlantSearchHdd" />
						<input type="hidden" name="unitParentLineSearch" id="unitParentLineSearchHdd" />
						<input type="hidden" name="unitSubLineSearch" id="unitSubLineSearchHdd" />
						<input type="hidden" name="getsudoMonthSearch" id="getsudoMonthSearchHdd" />
						<input type="hidden" name="timingSearch" id="timingSearchHdd" />
						<input type="hidden" name="getsudoMonths" id="getsudoMonths" />
						<input type="hidden" name="updateKeySet" id="updateKeySet" />

						<table id="result" class="result dataTable" style="width: auto; border: 0; float: left;">
							<thead>
								<tr role="row" id="result-header">

								</tr>
							</thead>
							<tbody id="result-body">

							</tbody>
						</table>
					</form>
					<!-- END FORM  -->

				</div>
			</div>
		</div>
	</div>
	
	<div>
		<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
	</div>
	
	<div id="save-panel" class="hide" style="display: inline; zoom: 1; position: relative; float: right; padding-top: 5px;">
		<spring:message code="STD.WSTD3060.Label.BtnSave"
			var="WBW04240Save" />
		<sc2:button functionId="BW0424" screenId="WBW04240"
			buttonId="WBW04240Save" type="button" value="${WBW04240Save}"
			styleClass="button" style="width:80px;"
			onClick="saveAddEditObject();" />
		<spring:message code="ST3.WST33060.Label.BtnCancel"
			var="WBW04240Cancel" />
		<sc2:button functionId="BW0424" screenId="WBW04240"
			buttonId="WBW04240Cancel" type="button" value="${WBW04240Cancel}"
			styleClass="button" secured="false" style="width:80px;"
			onClick="cancelAddEditObject();" />
	</div>

</div>