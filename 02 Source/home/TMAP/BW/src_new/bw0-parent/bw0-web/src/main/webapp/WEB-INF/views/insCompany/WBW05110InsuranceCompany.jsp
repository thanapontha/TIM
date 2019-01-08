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


<views:style src="gwrds/WBW04110.css"/>
<script>
	mappingPath = '${_mappingPath}';
	firstResult = '${form.firstResult}';
	rowsPerPage = '${form.rowsPerPage}';
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW04110.js?t=${currentTimestamp}"/>

 <div class="container-fluid">
 
 	 <div class="row"> 
	 	<div class="py-2 mx-auto text-center">
	   		<h3 class="display-5">Insurance Company Data Import & Export</h3>
	 	</div>
	</div>
 	<div class="row"> 
       	<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
			<div class="card">
				<div class="card-header text-white bg-primary">Import</div>
		     	<div class="card-body">
		        	<div class="row"> 
       					<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
       						<label for="importType" class="mx-1 my-0">Import Type&nbsp;:</label>
	                       	<select class="form-control form-control-sm" id="importType">
	                           <option>1st year INS Sales Premium <br>(Normal/TLT Exclusive and etc.)</option>
	                           <option>Import Transfer Premium</option>
	                           <option>No Claim Bonus Premium(NCB)</option>
	                           <option>Status of issued INS Policy</option>
	                       	</select>
       					</div>
       					<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
       						<label for="importFile" class="mx-1 my-0"><spring:message code="BW0.WBW01110.Label.FileName" />&nbsp;:</label>
							<input type="file" class="form-control-file" id="importFile">
       					</div>
       					<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12 text-right">
	                       <sc2:button functionId="BW0511"  screenId="WBW05110" buttonId="WBW05110Import"
								type="button" value="Import"
								styleClass="button" secured="false" onClick="clearSearch();"
							/>
	                   </div>
       				</div>
		      	</div>
		  	</div>
		</div>
		<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
			<div class="card">
				<div class="card-header text-white bg-success">Export</div>
		     	<div class="card-body">
		        	<div class="row"> 
       					<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
       						<label for="exportType" class="mx-1 my-0">Type&nbsp;:</label>
	                       <select class="form-control form-control-sm" id=""exportType"">
	                           <option>1st year INS Sales Daily</option>
	                           <option>Renewal Insurance Sales Daily</option>
	                       </select>
       					</div>
       					<div class="py-1 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12 text-right" >
	                       <sc2:button functionId="BW0511"  screenId="WBW05110" buttonId="WBW05110Export"
								type="button" value="Export"
								styleClass="button" secured="false" onClick="clearSearch();"
							/>
	                   </div>
       				</div>
		      	</div>
		  	</div>
		</div>
	</div>
	
	<div id="data-head-panel" class="row" style="border: none; padding-top: 0.5rem;">	
		<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
			<div id="search-result" class="overflow-hidden">
				<div id="dataList" class="" style="width:100%;" >
					<form id="result-list-form" 
							action="dataList" 
							method="post"  
							ajax="updateObjectFinish" 
							ajax-loading-target="#screen-panel" 
				   			validate-error="saveValidateError">
					    
						<datatables:table id="result" 
						    					data="${dataList}" 
						    					cdn="false" 
						    					row="row" 
						    					cssClass="result fixedheader fixedcolumn" 
						    					paginate="true" 
						    					cssStyle="height:100%;width:100%;"
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
										  
							<datatables:column title="No." cssClass="col1" sortable="false" searchable="false" cssStyle="text-align:center;" />
			        		<datatables:column title="DDMS Status" cssClass="col2 rownum" cssStyle="text-align:center;" sortable="false" searchable="false"/>
			        		<datatables:column title="INS Status" cssClass="col3" sortable="false" cssStyle="text-align:center;" />
							<datatables:column title="Model" cssClass="col4" sortable="false" cssStyle="text-align:center;" />
							<datatables:column title="V/N" cssClass="col5" sortable="false" searchable="false" cssStyle="text-align:center;"/>
							<datatables:column title="Type of purchase" cssClass="col6" sortable="false" searchable="false" cssStyle="text-align:center;"/>
							<datatables:column title="FN Company" cssClass="col7" sortable="false" searchable="false" cssStyle="text-align:center;"/>
							<datatables:column title="INS Code" cssClass="col8" sortable="false" searchable="false" cssStyle="text-align:center;"/>
							<datatables:column title="INS Company" cssClass="col9" sortable="false" searchable="false" cssStyle="text-align:center;"/>
							<datatables:column title="INS Type" cssClass="col10" sortable="false" searchable="false" cssStyle="text-align:center;"/>
							<datatables:column title="INS Premium" cssClass="col11" sortable="false" searchable="false" cssStyle="text-align:center;"/>
							<datatables:column title="INS Activation Date" cssClass="col12" sortable="false" searchable="false" cssStyle="text-align:center;"/>
							
						</datatables:table>
					</form>		
				</div>
			</div>
		</div>
	</div>

</div>