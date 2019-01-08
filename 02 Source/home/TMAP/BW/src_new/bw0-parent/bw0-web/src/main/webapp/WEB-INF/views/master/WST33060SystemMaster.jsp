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

<views:style src="tim/WST33060.css"/>
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
	MSTD1016AERR = '<spring:message code="MSTD1016AERR"></spring:message>';
	MSTD0001ACFM = '<spring:message code="MSTD0001ACFM"></spring:message>';
	MSTD1005AERR = '<spring:message code="MSTD1005AERR"></spring:message>';
	MSTD0090AINF = '<spring:message code="MSTD0090AINF"></spring:message>';
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="tim/WST33060.js?t=${currentTimestamp}"/>

<div id="screen-panel" class="container-fluid">
	<div id="search-criteria">
		<form:form method="post" 
					   id="search-form" 
					   action="${_mappingPath}/search" 
					   ajax="searchFinish" 
					   ajax-loading-target="#screen-panel" 
					   validate-error="searchValidateError">
	
			<input type="hidden" id="firstResult" name="firstResult" value="${form.firstResult}" default="${form.firstResult}" />
			<input type="hidden" id="rowsPerPage" name="rowsPerPage" value="${form.rowsPerPage}" default="${form.rowsPerPage}" />
			<input name="messageResult" id="messageResult"  type="hidden" />
			
			<div id="search-field" class="row pt-2"> 
        		<div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
        			<label for="categorySearch" class="mx-1 my-0">
        				<span class="MandatoryFieldFont">*</span>
						<spring:message code="ST3.WST33060.Label.Category"/>:
					</label>
					<select name="categorySearch" id="categorySearch" class="form-control form-control-sm MandatoryField">
					 	<option value=""><spring:message code="BW0.common.combobox.select"/></option>
						<c:forEach items="${form.categoryList}" var="item">
							<option value ="${item.stValue}"><c:out value="${item.stLabel}"/></option>
						</c:forEach>
					</select>
        		</div>
        		<div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
        			<label for="subCategorySearch" class="mx-1 my-0">
							<spring:message code="ST3.WST33060.Label.SubCategory"/>:
					</label>
					<select name="subCategorySearch" id="subCategorySearch" class="form-control form-control-sm">
					 	<option value=""><spring:message code="BW0.common.combobox.all"/></option>
						<c:forEach items="${form.subCategoryList}" var="item">
							<option value ="${item.stValue}"><c:out value="${item.stLabel}"/></option>
						</c:forEach>
					</select>
        		</div>
        		<div class="col-xl-6 col-lg-6 col-md-6 col-sm-6 col-12">
        			<label for="codeSearch" class="mx-1 my-0">
						<spring:message code="ST3.WST33060.Label.Code"/>:
					</label>
					<input class="form-control form-control-sm" type="text" name="codeSearch" id="codeSearch" style="text-transform:uppercase;" maxlength="40" format-msg="code" >
        		</div>
        	</div>
			<div class="row py-2">        		
        		<div id="search-panel" class="col-xl-3 col-lg-3 col-md-3 col-sm-12 col-12" style="text-align: left;">
        			<spring:message code="ST3.WST33060.Label.BtnSearch" var="WST33060Search" />
					<sc2:button functionId="ST3306"  screenId="WST33060" buttonId="WST33060Search" 
						type="button" value="${WST33060Search}"  style="width:80px;"
						styleClass="button" onClick="clickSearch();"
					/>
					<spring:message code="ST3.WST33060.Label.BtnClear" var="WST33060Clear" />
					<sc2:button functionId="ST3306"  screenId="WST33060" buttonId="WST33060Clear"
						type="button" value="${WST33060Clear}"  style="width:80px;"
						styleClass="button" secured="false" onClick="clearSearch();"
					/>
        		</div>
        		<div id="operation-panel" class="col-xl-9 col-lg-9 col-md-9 col-sm-12 col-12" style="text-align: right;">
        			<spring:message code="ST3.WST33060.Label.BtnAdd" var="WST33060Add" />
					<sc2:button functionId="ST3306"  screenId="WST33060" buttonId="WST33060Add"
						type="button" value="${WST33060Add}" 
						styleClass="button"
						style="width:80px;"
						onClick="addObject();"
						/>
					<spring:message code="ST3.WST33060.Label.BtnEdit" var="WST33060Edit" />
					<sc2:button functionId="ST3306"  screenId="WST33060" buttonId="WST33060Edit" 
						type="button" value="${WST33060Edit}" 
						styleClass="button"
						style="width:80px;"
						onClick="editObject();"
						/>
					<spring:message code="ST3.WST33060.Label.BtnDelete" var="WST33060Delete" />
					<sc2:button functionId="ST3306"  screenId="WST33060" buttonId="WST33060Delete" 
						type="button" value="${WST33060Delete}" 
						styleClass="button"
						style="width:80px;"
						onClick="deleteObject();"
						/>
					<spring:message code="ST3.WST33060.Label.BtnDownload" var="WST33060Download" />
					<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Download" 
						type="button" value="${WST33060Download}" 
						styleClass="button"
						style="width:80px;"
						onClick="downloadSystem();"
						/>
        		</div>
        	</div>
		</form:form>
	</div>
	<div id="search-result" class="row">
		<div id="dataList" class="col-12">
			<form id="result-list-form" 
					action="dataList" 
					method="post"  
					ajax="updateObjectFinish" 
					ajax-loading-target="#screen-panel" 
		   			validate-error="saveValidateError">
		   	
				<input type="hidden" name="updateKeySet" id="updateKeySet" />
				
											
			    <spring:message code="ST3.WST33060.Label.ColNo" var="ColNo"/>
			    <spring:message code="ST3.WST33060.Label.ColCategory" var="ColCategory"/>
			    <spring:message code="ST3.WST33060.Label.ColSubCategory" var="ColSubCategory"/>
			    <spring:message code="ST3.WST33060.Label.ColCode" var="ColCode"/>
			    <spring:message code="ST3.WST33060.Label.ColValue" var="ColValue"/>
			    <spring:message code="ST3.WST33060.Label.ColRemark" var="ColRemark"/>
			    <spring:message code="ST3.WST33060.Label.ColStatus" var="ColStatus"/>
			    <spring:message code="ST3.WST33060.Label.ColUpdateDate" var="ColUpdateDate"/>
			    <spring:message code="ST3.WST33060.Label.ColUpdateBy" var="ColUpdateBy"/>
			    <spring:message code="ST3.WST33060.Label.ColCreateDate" var="ColCreateDate"/>
			    <spring:message code="ST3.WST33060.Label.ColCreateBy" var="ColCreateBy"/>
			    
				<datatables:table id="result" 
				    					data="${dataList}" 
				    					cdn="false" 
				    					row="row" 
				    					cssClass="result fixedheader fixedcolumn" 
				    					paginate="true" 
				    					cssStyle="height:300px;width:1645px;"
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
	        		<datatables:column title="${ColNo}" cssClass="col2 rownum fixed" cssStyle="text-align:center;" sortable="false" searchable="false"/>
	        		<datatables:column title="${ColCategory}" cssClass="col3" sortable="false" cssStyle="text-align:center;" />
					<datatables:column title="${ColSubCategory}" cssClass="col4" sortable="false" cssStyle="text-align:center;" />
					<datatables:column title="${ColCode}" cssClass="col5" sortable="false" searchable="false" cssStyle="text-align:center;" />
					<datatables:column title="${ColValue}" cssClass="col6" sortable="false" cssStyle="text-align:center;" />
					<datatables:column title="${ColRemark}" cssClass="col7" sortable="false" cssStyle="text-align:center;" />
					<datatables:column title="${ColStatus}" cssClass="col8" sortable="false" searchable="false" cssStyle="text-align:center;" />										
					<datatables:column title="${ColUpdateDate}" cssClass="col9" sortable="false" searchable="false" cssStyle="text-align:center;" />
					<datatables:column title="${ColUpdateBy}" cssClass="col10" sortable="false" searchable="false" cssStyle="text-align:center;" />
					<datatables:column title="${ColCreateDate}" cssClass="col11" sortable="false" searchable="false" cssStyle="text-align:center;" />
					<datatables:column title="${ColCreateBy}" cssClass="col12" sortable="false" searchable="false" cssStyle="text-align:center;" />
				</datatables:table>
				<iframe id="downloadIframe" name="downloadIframe" frameborder="0" style="width:100px;height:100px;border:0px solid black;position:static;left:0px;display:none;" ></iframe>
			</form>
			<table id="datatemplate" class="template">
				<tr class="template add">
						<td class="col1">&nbsp;										
						</td>
						<td class="col2">&nbsp;
						</td>
						<td class="col3">
							<input type="text" name="category" id="category" class="form-control form-control-sm MandatoryField" 
							style="text-transform:uppercase;" title="${ColCategory}" format-msg="bodycode"  maxlength="40"/>
						</td>
						<td class="col4">
							<input type="text" name="subCategory" id="subCategory" class="form-control form-control-sm MandatoryField" 
							style="text-transform:uppercase;" title="${ColSubCategory}" format-msg="bodycode" maxlength="40"/>
						</td>
						<td class="col5">
							<input type="text" name="code" id="code" class="form-control form-control-sm MandatoryField" 
							style="text-transform:uppercase;" title="${ColCode}" format-msg="bodycode"  maxlength="40"/>
						</td>
						<td class="col6">
							<input type="text" name="value" id="value" class="form-control form-control-sm MandatoryField" maxlength="200" title="${ColValue}"/>
						</td>
						<td class="col7">
							<input type="text" name="remark" id="remark" class="form-control form-control-sm " maxlength="200" title="${ColRemark}"/>
						</td>
						<td class="col8">
							<select name="status" id="status" title="${ColStatus}" class="form-control form-control-sm ">
								<option value="Y" selected>Active</option>
								<option value="N">Inactive</option>
							</select>
						</td>											
						<td class="col9"></td>
						<td class="col10"></td>
						<td class="col11"></td>
						<td class="col12"></td>
				</tr>
				<tr class="template edit">
						<td class="col1">@data</td>
						<td class="col2">@data</td>
						<td class="col3">@data
							<input type="hidden" name="category" id="category" value="@data"/>
						</td>
						<td class="col4">@data
							<input type="hidden" name="subCategory" id="subCategory" value="@data"/>
						</td>
						<td class="col5">@data
							<input type="hidden" name="code" id="code" value="@data"/>
						</td>
						<td class="col6">
							<input type="text" name="value" id="value" class="form-control form-control-sm MandatoryField" maxlength="200" title="${ColValue}" value="@data"/>
						</td>
						<td class="col7">
							<input type="text" name="remark" id="remark" class="form-control form-control-sm" maxlength="200" title="${ColRemark}" value="@data"/>
						</td>																				
						<td class="col8">
							<select name="status" id="status" _value="@data" class="form-control form-control-sm" title="${ColStatus}">
								<option value="Y" selected>Active</option>
								<option value="N">Inactive</option>
							</select>
						</td>
						<td class="col9">@data</td>
						<td class="col10">@data</td>
						<td class="col11">@data</td>
						<td class="col12">@data</td>
				</tr>
			</table>			
		</div>
		<div id="save-panel" class="col-12 hide" style="text-align: right;">
			<spring:message code="ST3.WST33060.Label.BtnSave" var="WST33060Save" />
			<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Save" 
				type="button" value="${WST33060Save}" 
				styleClass="button"
				style="width:80px;"
				onClick="saveAddEditObject();"
				/>
			<spring:message code="ST3.WST33060.Label.BtnCancel" var="WST33060Cancel" />
			<sc2:button functionId="ST3306" screenId="WST33060" buttonId="WST33060Cancel" 
				type="button" value="${WST33060Cancel}" 
				styleClass="button" secured="false"
				style="width:80px;"
				onClick="cancelAddEditObject();"
				/>
		</div>
	</div>
</div>