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
<views:script src="gwrds.lib.js"/>
<views:style src="jquery.dataTables.css"/>

<views:style src="gwrds/WBW04131.css"/>
<views:script src="gwrds/WBW04131.js"/>

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

<div id="screen-panel" style="padding-top: 4px;">
	<div id="search-criteria" style="">
		<form:form method="post" 
					   id="search-form" 
					   action="${_mappingPath}/search" 
					   ajax="searchFinish" 
					   ajax-loading-target="#screen-panel" 
					   validate-error="searchValidateError">
	
		<input type="hidden"  id="firstResult" name="firstResult" value="${form.firstResult}" default="${form.firstResult}" />
		<input type="hidden" id="rowsPerPage" name="rowsPerPage" value="${form.rowsPerPage}" default="${form.rowsPerPage}" />
		<input name="messageResult" id="messageResult"  type="hidden" />
		
		<input name="vehiclePlantSearch" id="vehiclePlantSearch"  type="hidden" />
		<input name="vehicleModelSearch" id="vehicleModelSearch"  type="hidden" />
					   
		<div id="search-field" style="zoom:1;position:relative;">
			<table style="width: 100%;">
			<colgroup>
				<col width="10%">
				<col width="20%">
				<col width="10%">
				<col width="20%">
				<col width="10%">
			</colgroup>
			<tbody>
			</tbody>
		</table>
		</div>
		<div class="clear"></div>
		</form:form>
	</div>
	
	<table style="width: 100%; border: 0;">
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
							   	
									<input type="hidden" name="updateKeySet" id="updateKeySet"  />
									<input type="hidden" name="unitPlant" id="unitPlant" />
									<input name="lastAction" id="lastAction"  type="hidden" />
																
								    <spring:message code="BW0.WBW04131.Label.No" var="ColNo"/>
									<spring:message code="BW0.WBW04131.Label.UnitModel" var="ColUnitModel"/>
									<spring:message code="BW0.WBW04131.Label.UnitType" var="ColType"/>
									<spring:message code="BW0.WBW04131.Label.UnitPlant" var="ColUnitPlant"/>
								    <input type="hidden" name="listSubScreen" id="listSubScreen"  />
									<datatables:table id="result" 
									    					data="${dataList}" 
									    					cdn="false" 
									    					row="row" 
									    					cssClass="result fixedheader fixedcolumn" 
									    					paginate="true" 
									    					cssStyle="height:300px;width:100%;"
															info="true" 
															filter="true"
															displayLength="${form.rowsPerPage}" 
															paginationType="full_numbers" 
															fixedPosition="col"
															lengthChange="false"
															sort="false"
															serverSide="false"
															>
													  
										<datatables:column title="" cssClass="col1" sortable="false" searchable="false" cssStyle="text-align:center;" />
						        		<datatables:column title="${ColNo}" cssClass="col2 rownum" cssStyle="text-align:center;" sortable="false" searchable="false"/>
						        		<datatables:column title="${ColUnitModel}" cssClass="col3" sortable="false" cssStyle="text-align:center;" />
										<datatables:column title="${ColType}" cssClass="col4" sortable="false" cssStyle="text-align:center;"/>
										<datatables:column title="${ColUnitPlant}" cssClass="col5" sortable="false" searchable="false" cssStyle="text-align:center;" />
									</datatables:table>
								</form>
							</div>				
					</div>
				</div>
			</div>
		</td></tr>
		<tr><td>
			<div id="save-panel" style="display:inline;zoom:1;position:relative;float: right;">
				<spring:message code="ST3.WST33060.Label.BtnSelect" var="WBW04131Select" />
				<sc2:button functionId="BW0413"  screenId="WBW04131" buttonId="WBW04131Select" 
					type="button" value="${WBW04131Select}"  style="width:80px;"
					styleClass="button" onClick="selectData();"
				/>
				<spring:message code="ST3.WST33021.Label.btnDetailClose" var="WBW04131Cancel" />
				<sc2:button functionId="BW0413"  screenId="WBW04131" buttonId="WBW04131Cancel" 
					type="button" value="${WBW04131Cancel}" 
					styleClass="button" secured="false"
					style="width:80px;"
					onClick="cancelSubScreen();"
					/>
			</div>
		</td></tr>
	</table>
</div>


