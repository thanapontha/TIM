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

 <views:style src="gwrds/WBW04230.css" />
 <script>
	mappingPath = '${_mappingPath}';
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
	
	
	var menucolumn = [ "", 5, 6, 'MandatoryField CheckDecimalFormat', '' ];
	
 </script>
 
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />
 
 <views:script src="gwrds/WBW04230.js?t=${currentTimestamp}" />

 <div id="screen-panel" style="height: 800px; padding-top: 4px;">
 	<div id="search-criteria" style="border-bottom: 2px solid #AAA;padding-bottom: 4px;">
	<form:form method="post" 
				   id="search-form" 
				   action="${_mappingPath}/search" 
				   ajax="searchFinish" 
				   ajax-loading-target="#screen-panel" 
				   validate-error="searchValidateError">
					   
		<input type="hidden" id="firstResult" name="firstResult" value="${form.firstResult}" default="${form.firstResult}" />
		<input type="hidden" id="rowsPerPage" name="rowsPerPage" value="${form.rowsPerPage}" default="${form.rowsPerPage}" />
 		<input name="messageResult" id="messageResult"  type="hidden" />
 		<div id="search-field" style="zoom:1;position:relative;">
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
 						<label class="label" for="getsudoMonthSearch">
							<spring:message code="BW0.WBW04230.Label.GetsudoMonth" />:
 						</label>
 					</td>
 					<td>
 						<input name="getsudoMonthSearch" id="getsudoMonthSearch" 
						value="${form.getsudoMonthSearch}" type="text" maxlength="6"
 							class="MandatoryField" style="width:50px;text-align: left;"
 							tabindex=1  />
 					</td> 
					
 					<td align="right">
 						<span class="MandatoryFieldFont">*</span>
 						<label class="label" for="vehiclePlantSearch">
							<spring:message code="BW0.WBW04230.Label.VehiclePlant" />:
 						</label>
 					</td>
 					<td>
 						<select name="vehiclePlantSearch" id="vehiclePlantSearch"
 							class="MandatoryField" style="width: 75%; vtext-align: left;"
 							tabindex=3>
 						<option value=""><spring:message code="BW0.common.combobox.select" /></option> 
 						<c:forEach items="${form.vehiclePlantList}" var="item"> 
 							<option value="${item.stValue}"><c:out 
 									value="${item.stLabel}" /></option> 
 						</c:forEach> 
 						</select>
 					</td>
					
 					<td align="right">
 						<span class="MandatoryFieldFont">*</span>
 						<label class="label" for="unitModelSearch">
							<spring:message code="BW0.WBW04230.Label.UnitModel" />:
 						</label>
 					</td>
 					<td>
 						<select name="unitModelSearch" id="unitModelSearch"
 							class="MandatoryField" style="width: 75%; vtext-align: left;"
 							tabindex=5>
						<option value=""><spring:message code="BW0.common.combobox.select" /></option> 
						<c:forEach items="${form.unitModelList}" var="item"> 
							<option value="${item.stValue}"><c:out 
									value="${item.stLabel}" /></option> 
						</c:forEach> 
 						</select>
 					</td>
 				</tr>
 				<tr>
 					<td align="right">
 						<span class="MandatoryFieldFont">*</span>
 						<label class="label" for="timingSearch">
							<spring:message code="BW0.WBW04230.Label.Timing" />:
 						</label>
 					</td>
 					<td>
 						<select name="timingSearch" id="timingSearch"
 							class="MandatoryField" style="width: 75%; vtext-align: left;"
 							tabindex=2>
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
 							class="MandatoryField" style="width: 75%; vtext-align: left;"
 							tabindex=4>
						<option value=""><spring:message code="BW0.common.combobox.select" /></option> 
						<c:forEach items="${form.vehicleModelList}" var="item"> 
							<option value="${item.stValue}"><c:out 
									value="${item.stLabel}" /></option> 
						</c:forEach> 
 						</select>
 					</td>
					
 					<td align="right">
 						<span class="MandatoryFieldFont">*</span>
						<label class="label" for="unitPlantSearch">
							<spring:message code="BW0.WBW04230.Label.UnitPlant" />:
						</label>
 					</td>
 					<td>
 						<select name="unitPlantSearch" id="unitPlantSearch"
 							class="MandatoryField" style="width: 75%; vtext-align: left;"
 							tabindex=6>
						<option value=""><spring:message code="BW0.common.combobox.select" /></option> 
 						<c:forEach items="${form.unitPlantList}" var="item"> 
 							<option value="${item.stValue}"><c:out 
 									value="${item.stLabel}" /></option> 
 						</c:forEach> 
 						</select>
 					</td>
 				</tr>
 				<tr>
 					<td></td>
 					<td></td>
 					<td></td>
 					<td></td>
 					<td></td>
 					<td></td>	
 					<td align="right">
 						<div id="search-panel" style="display:inline;zoom:1;position:relative;">
						<spring:message code="ST3.WST33060.Label.BtnSearch" var="WBW04120Search" />
						<sc2:button functionId="BW0412"  screenId="WBW04120" buttonId="WBW04120Search" 
							type="button" value="${WBW04120Search}"  style="width:80px;" tabIndex="7"
							styleClass="button" onClick="clickSearch();"
						/>
						<spring:message code="ST3.WST33060.Label.BtnClear" var="WBW04120Clear" />
						<sc2:button functionId="BW0412"  screenId="WBW04120" buttonId="WBW04120Clear"
							type="button" value="${WBW04120Clear}"  style="width:80px;" tabIndex="8"
							styleClass="button" secured="false" onClick="clearSearch();"
						/>
 						</div>
 					</td>
 				</tr>
 			</tbody>
 		</table>
 		</div>
 		<div class="clear"></div>
	</form:form>
 	</div>

	<table style="width: 100%; border: 0;">
		<tr>
			<td>
				<div id="operation-panel" style="padding-top: 5px;">
				<div id="operation-btn" style="display: inline; zoom: 1; position: relative; white-space: nowrap; float: right; padding-top: 4px;">
					<spring:message code="ST3.WST33060.Label.BtnEdit"
						var="WBW04230Edit" />
					<sc2:button functionId="BW0423" screenId="WBW04230"
						buttonId="WBW04230Edit" type="button" value="${WBW04230Edit}"
						styleClass="button" style="width:80px;" onClick="editObject();" />
				</div>
				</div>
			</td>
		</tr>
	</table>
	<div id="operation-panel" style="padding-top: 5px;"></div>
	<div style="display: block;">
		<div style="border-bottom: solid #AAA 2px; margin-top: 2px;"></div>
	</div>
	
	
	<div id="search-criteria-label"  class="hide">
		 <div style="display: -webkit-flex;display: flex;">
			<div class="criteria-label-item" style="text-align: right;"><label class="label"><spring:message code="BW0.WBW04230.Label.GetsudoMonth"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item"><label class="label"><span id="resultGetsudoMonthSearch">-</span></label></div>
			<div class="criteria-label-item" style="text-align: right;"><label class="label"><spring:message code="BW0.WBW04230.Label.VehiclePlant"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item"><label class="label"><span id="resultVehiclePlantSearch">-</span></label></div>
			<div class="criteria-label-item" style="text-align: right;"><label class="label"><spring:message code="BW0.WBW04230.Label.UnitModel"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item"><label class="label"><span id="resultUnitModelSearch">-</span></label></div>
		</div>
		<div style="display: -webkit-flex;display: flex;">
			<div class="criteria-label-item" style="text-align: right;"><label class="label"><spring:message code="BW0.WBW04230.Label.Timing"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item"><label class="label"><span id="resultTimingSearch">-</span></label></div><br/>
			<div class="criteria-label-item" style="text-align: right;"><label class="label"><spring:message code="BW0.WBW04230.Label.VehicleModel"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item"><label class="label"><span id="resultVehicleModelSearch">-</span></label></div>
			<div class="criteria-label-item" style="text-align: right;"><label class="label"><spring:message code="BW0.WBW04230.Label.UnitPlant"/>&nbsp;:&nbsp;</label></div>
			<div class="criteria-label-item"><label class="label"><span id="resultUnitPlantSearch">-</span></label></div>
		</div>
	</div>
	
	<div id="data-head-panel" class="container" style="border: none;">
		<div id="divresult" style="padding-left: 5px; padding-right: 5px; padding-top: 0px;">
			<div id="search-result" class="overflow-hidden" style="height: 280px;">
				<div id="dataList" class="container" style="height: 310px; width: 100%; border: none;">
					<form id="result-list-form" action="dataList" method="post"
						ajax="updateObjectFinish" ajax-loading-target="#screen-panel"
						validate-error="saveValidateError">
						
						<div id="operation-panel" style="padding-top: 5px;"></div>
						<input type="hidden" name="allSelect" id="allSelect" /> 
						<input type="hidden" name="rowLength" id="rowLength" />
						<input name="strJsonHeader" id="strJsonHeader"  type="hidden" />
						<input name="strJsonBodyMax" id="strJsonBodyMax"  type="hidden" />
						<input name="strJsonBodyMin" id="strJsonBodyMin"  type="hidden" />
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
				</div>
			</div>
		</div>
	</div>
	
	<div id="save-panel" class="hide"
		style="display: inline; zoom: 1; position: relative; float: right;">
		<spring:message code="STD.WSTD3060.Label.BtnSave"
			var="WBW04230Save" />
		<sc2:button functionId="BW0423" screenId="WBW04230"
			buttonId="WBW04230Save" type="button" value="${WBW04230Save}"
			styleClass="button" style="width:80px;"
			onClick="saveAddEditObject();" />
		<spring:message code="ST3.WST33060.Label.BtnCancel"
			var="WBW04230Cancel" />
		<sc2:button functionId="BW0423" screenId="WBW04230"
			buttonId="WBW04230Cancel" type="button" value="${WBW04230Cancel}"
			styleClass="button" secured="false" style="width:80px;"
			onClick="cancelAddEditObject();" />
	</div>


 </div>
