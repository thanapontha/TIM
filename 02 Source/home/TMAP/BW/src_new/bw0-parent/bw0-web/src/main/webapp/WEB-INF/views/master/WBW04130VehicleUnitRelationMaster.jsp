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

<views:style src="gwrds/WBW04130.css"/>
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
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW04130.js?t=${currentTimestamp}"/>
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
				<col width="20%">
				<col width="10%">
				<col width="20%">
				<col width="10%">
				<col width="10%">
				<col width="20%">
			</colgroup>
			<tbody>
				<tr>
					<td align="right">
						<span class="MandatoryFieldFont">*</span>
						<label class="label" for="vehiclePlantSearch">
							<spring:message code="BW0.WBW04130.Label.VehiclePlant"/>:
						</label>
					</td>
					<td>
						<select name="vehiclePlantSearch" id="vehiclePlantSearch" class="MandatoryField"
							style="width:100%;text-align: left;">
							 	<option value=""><spring:message code="BW0.common.combobox.select"/></option>
								<c:forEach items="${form.vehiclePlantList}" var="item">
									<option value ="${item.stValue}"><c:out value="${item.stLabel}"/></option>
								</c:forEach>
						</select>
					</td>
					<td align="right">
						<span class="MandatoryFieldFont">*</span>
						<label class="label" for="vehicleModelSearch">
							<spring:message code="BW0.WBW04130.Label.VehicleModel"/>:
						</label>
					</td>
					<td>
						<select name="vehicleModelSearch" id="vehicleModelSearch" class="MandatoryField"
							style="width:100%;text-align: left;">
							 	<option value=""><spring:message code="BW0.common.combobox.select"/></option>
								<c:forEach items="${form.vehicleModelList}" var="item">
									<option value ="${item.stValue}"><c:out value="${item.stLabel}"/></option>
								</c:forEach>
						</select>
					</td>
					<td align="right">
		                 <input type="checkbox" id="includeExpiredData" name="includeExpiredData" style="vertical-align: middle;"/>
					</td>
					<td>
						<label class="label" for="includeExpiredData"><spring:message code="BW0.WBW04130.Label.IncludeExpiredData"/></label>
					</td>					
					<td align="right">
						<div id="search-panel" style="display:inline;zoom:1;position:relative;">
							<spring:message code="ST3.WST33060.Label.BtnSearch" var="WBW04130Search" />
							<sc2:button functionId="BW0413"  screenId="WBW04130" buttonId="WBW04130Search" 
								type="button" value="${WBW04130Search}"  style="width:80px;"
								styleClass="button" onClick="clickSearch();"
							/>
							<spring:message code="ST3.WST33060.Label.BtnClear" var="WBW04130Clear" />
							<sc2:button functionId="BW0413"  screenId="WBW04130" buttonId="WBW04130Clear"
								type="button" value="${WBW04130Clear}"  style="width:80px;"
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
		<tr><td>
			<div id="operation-panel" style="display:inline;zoom:1;position:relative;white-space: nowrap;float: right;padding-top: 4px;">
				<spring:message code="ST3.WST33060.Label.BtnAdd" var="WBW04130Add" />
				<sc2:button functionId="BW0413"  screenId="WBW04130" buttonId="WBW04130Add"
					type="button" value="${WBW04130Add}" 
					styleClass="button"
					style="width:80px;"
					onClick="addObject();"
					/>
				<spring:message code="ST3.WST33060.Label.BtnEdit" var="WBW04130Edit" />
				<sc2:button functionId="BW0413"  screenId="WBW04130" buttonId="WBW04130Edit" 
					type="button" value="${WBW04130Edit}" 
					styleClass="button"
					style="width:80px;"
					onClick="editObject();"
					/>
				<spring:message code="ST3.WST33060.Label.BtnDelete" var="WBW04130Delete" />
				<sc2:button functionId="BW0413"  screenId="WBW04130" buttonId="WBW04130Delete" 
					type="button" value="${WBW04130Delete}" 
					styleClass="button"
					style="width:80px;"
					onClick="deleteObject();"
					/>
			</div>
		</td></tr>
		<tr><td>
			<div id="data-head-panel" class="container" style="border: none;">	
				<div id="divresult" style="padding-left : 5px;padding-right : 5px;padding-top : 5px;">
				<div id="search-result" class="overflow-hidden" style="height: 330px;">
							<div id="dataList" class="container" style="height: 310px; width:100%;border: none;" >
								<form id="result-list-form" 
										action="dataList" 
										method="post"  
										ajax="updateObjectFinish" 
										ajax-loading-target="#screen-panel" 
							   			validate-error="saveValidateError">
							   	
									<input type="hidden" name="updateKeySet" id="updateKeySet" />
									
									<input name="unitModel" id="unitModel"  type="hidden" />
									<input name="unitType" id="unitType"  type="hidden" />
									<input name="priority" id="priority"  type="hidden" />
									<input name="unitPlant" id="unitPlant"  type="hidden" />
									
									<input name="vehicleModel" id="vehicleModel"  type="hidden" />
									<input name="vehiclePlant" id="vehiclePlant"  type="hidden" />
		
																
								    <spring:message code="BW0.WBW04130.Label.No" var="ColNo"/>
									<spring:message code="BW0.WBW04130.Label.UnitModel" var="ColUnitModel"/>
									<spring:message code="BW0.WBW04130.Label.UnitType" var="ColUnitType"/>
									<spring:message code="BW0.WBW04130.Label.Priority" var="ColPriority"/>
									<spring:message code="BW0.WBW04130.Label.UnitPlant" var="ColUnitPlant"/>
									<spring:message code="BW0.WBW04130.Label.TCFrom" var="ColTCFrom"/>
									<spring:message code="BW0.WBW04130.Label.TCTo" var="ColTCTo"/>
									<spring:message code="BW0.common.label.UpdateBy" var="ColUpdateBy"/>
									<spring:message code="BW0.common.label.UpdateDate" var="ColUpdateDate"/>
								    
									<datatables:table id="result" 
									    					data="${dataList}" 
									    					cdn="false" 
									    					row="row" 
									    					cssClass="result fixedheader fixedcolumn" 
									    					paginate="true" 
									    					cssStyle="height:300px;width:100%;"
															info="true" 
															filter="false"
															displayLength="${form.rowsPerPage}" 
															paginationType="full_numbers" 
															fixedPosition="col"
															lengthChange="false"
															sort="false"
															serverData="serverData"
															serverSide="true"
															>
													  
										<datatables:column title="" cssClass="col1" sortable="false" searchable="false" cssStyle="text-align:center;" />
						        		<datatables:column title="${ColNo}" cssClass="col2 rownum" cssStyle="text-align:center;" sortable="false" searchable="false"/>
						        		<datatables:column title="${ColUnitModel}" cssClass="col3" sortable="false" cssStyle="text-align:center;" />
										<datatables:column title="${ColUnitType}" cssClass="col4" sortable="false" cssStyle="text-align:center;" />
										<datatables:column title="${ColPriority}" cssClass="col5" sortable="false" searchable="false" cssStyle="text-align:center;" />
										<datatables:column title="${ColUnitPlant}" cssClass="col6" sortable="false" cssStyle="text-align:center;" />
										<datatables:column title="${ColTCFrom}" cssClass="col7" sortable="false" cssStyle="text-align:center;" />
										<datatables:column title="${ColTCTo}" cssClass="col8" sortable="false" searchable="false" cssStyle="text-align:center;" />										
										<datatables:column title="${ColUpdateBy}" cssClass="col9" sortable="false" searchable="false" cssStyle="text-align:center;" />
										<datatables:column title="${ColUpdateDate}" cssClass="col10" sortable="false" searchable="false" cssStyle="text-align:center;" />
									</datatables:table>
								</form>
								<table id="datatemplate" class="template">
									<tr class="template add">
											<td class="col1">
												
											</td>
											 <!-- Unit Model -->
											<td class="col2 rownum"> 	
											</td>
											<td class="col3">
												<div class="input-group" onclick="popupSubsreen()" >
												    <input type="text" class="form-control" disabled="disabled" title="${ColUnitModel}" onclick="popupSubsreen()" style="width: 90%;background: #DEF7FD"   name="txtUnitModel" id="txtUnitModel"/>
													<input type="button" id = "btnPopupSubsreen" value="..." onclick="popupSubsreen()" style="width: 15px;"/>
												</div>
												<!-- Unit Type -->
											</td>
											<td class="col4">
												<!-- Priority -->
											</td>
											<td class="col5">
												<!-- Unit plant -->
											</td>		
											<td class="col6"></td>									
											<td class="col7">
												<input name="tcFrom" id="tcFrom" type="text"
														   maxlength="6" class="MandatoryField"
														   style="width:75%;text-align: left;"
														   title="${ColTCFrom}"/>	
											</td>
											<td class="col8">
												<input name="tcTo" id="tcTo" type="text"
														   maxlength="6"
														   style="width:75%;text-align: left;"
														   title="${ColTCTo}"/>	
											</td>											
											<td class="col9"></td>
											<td class="col10"></td>
									</tr>
									<tr class="template edit">
											<td class="col1">@data</td>
											<td class="col2 rownum">@data</td>
											<td class="col3">@data
												<input type="hidden" name="unitModel" id="unitModel" value="@data"/>
											</td>
											<td class="col4">@data
												<input type="hidden" name="unitType" id="unitType" value="@data"/>
											</td>
											<td class="col5">@data
												<input type="hidden" name="priority" id="priority" value="@data"/>
											</td>
											<td class="col6">@data
												<input type="hidden" name="unitPlant" id="unitPlant" value="@data"/>
											</td>
											<td class="col7">@data</td>																				
											<td class="col8">
												<input name="tcTo" id="tcTo" type="text"
													value="@data" maxlength="6"
												   style="width:75%;text-align: left;"
												   title="${ColTCTo}"/>	
											</td>
											<td class="col9">@data</td>
											<td class="col10">@data</td>
									</tr>
								</table>
							</div>				
					</div>
				</div>
			</div>
		</td></tr>
		<tr><td>
			<div id="save-panel" class="hide" style="display:inline;zoom:1;position:relative;float: right;">
				<spring:message code="STD.WSTD3060.Label.BtnSave" var="WBW04130Save" />
				<sc2:button functionId="BW0413"  screenId="WBW04130" buttonId="WBW04130Save" 
					type="button" value="${WBW04130Save}" 
					styleClass="button"
					style="width:80px;"
					onClick="saveAddEditObject();"
					/>
				<spring:message code="ST3.WST33060.Label.BtnCancel" var="WBW04130Cancel" />
				<sc2:button functionId="BW0413"  screenId="WBW04130" buttonId="WBW04130Cancel" 
					type="button" value="${WBW04130Cancel}" 
					styleClass="button" secured="false"
					style="width:80px;"
					onClick="cancelAddEditObject();"
					/>
			</div>
		</td></tr>
	</table>
	
</div>