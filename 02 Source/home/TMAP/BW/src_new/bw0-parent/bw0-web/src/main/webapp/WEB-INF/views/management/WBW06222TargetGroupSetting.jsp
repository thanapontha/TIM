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

<views:style src="tim/WBW03110.css"/>
<script>
	mappingPath = '${_mappingPath}';
	firstResult = '${form.firstResult}';
	rowsPerPage = '${form.rowsPerPage}';
	
	$(function(){
		
		$('#insActivationPeriod, #insExpiryPeriod').datepicker({
			showOn: "button",
			buttonImage: calendarImgPath,
			buttonImageOnly: true,
			buttonText: "Select date",
			dateFormat: 'dd/mm/yy',
			onSelect: function(){
				$(this).focus();	
			}
		});
		
	});
	
</script>
<style>

</style>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="tim/WBW03110.js?t=${currentTimestamp}"/>

<div id="screen-panel" class="container-fluid mb-5">
	<div class="d-flex flex-wrap justify-content-around bd-highlight">
		<div class="p-2 bd-highlight"><button type="button" class="btn btn-primary btn-md">1. Target Group Setting</button></div>
	    <div class="p-2 bd-highlight"><button type="button" class="btn btn-secondary btn-md" onclick="location.href = 'StandardRenewalSOPPICSetting';">2. Overall SOP Set up</button></div>
	    <div class="p-2 bd-highlight"><button type="button" class="btn btn-secondary btn-md" onclick="location.href = 'InsuranceRenewPortfolioControl';">3. INS Renew Portfolio control</button></div>
	    <div class="p-2 bd-highlight"><button type="button" class="btn btn-secondary btn-md" onclick="location.href = 'TeleSalesTeamManagement';">4. Sales Team Management</button></div>
	</div>
	
	<div id="search-criteria">
		<form:form method="post" 
			   id="search-form" 
			   action="${_mappingPath}/search" 
			   ajax="searchFinish" 
			   ajax-loading-target="#screen-panel" 
			   validate-error="searchValidateError">
		<input name="firstResult" type="hidden" value="0" default="0" />
		<input name="rowsPerPage" type="hidden" value="10" default="10" />
		<input name="messageResult" id="messageResult"  type="hidden" />
		
		<div class="row"> 
		 	<div class="pt-2 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	               <h3>Target Group Calling Criteria</h3>
	               
	               <div class="row mt-1">
	               	   <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
	                       	<label for="insActivationPeriod" class="mx-1 my-0">INS Activation Period&nbsp;:</label>
	                       	<div class="d-flex flex-row">
                       			<input type="text" class="form-control form-control-sm" id="insActivationPeriod">
                       		</div>
	                   </div>
	                   <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
	                       	<label for="insuranceCompany" class="mx-1 my-0">Insurance Company&nbsp;:</label>
                     		<input type="text" class="form-control form-control-sm" id="insuranceCompany">
	                   </div>
	                   <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
	                       <label for="typeOfInsurance" class="mx-1 my-0">Type of Insurance&nbsp;:</label>
	                       <select class="form-control form-control-sm" id="typeOfInsurance">
	                           <option>1st Class</option>
	                           <option>2nd Class</option>
	                           <option>3rd Class</option>
	                           <option>Other</option>
	                       </select>
	                   </div>
	                   <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
	                       	<label for="model" class="mx-1 my-0">Model&nbsp;:</label>
                     		<input type="text" class="form-control form-control-sm" id="model">
	                   </div>
	                   
	                   <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
	                       	<label for="insExpiryPeriod" class="mx-1 my-0">INS Expiry Period&nbsp;:</label>
	                       	<div class="d-flex flex-row">
                       			<input type="text" class="form-control form-control-sm" id="insExpiryPeriod">
                       		</div>
	                   </div>
	                   
	                   <div class="col-xl-9 col-lg-9 col-md-9 col-sm-12 col-12" style="text-align: right;">
	                   		<div class="d-flex align-items-end justify-content-end" style="height:100%">
		                       	<sc2:button functionId="BW0622"  screenId="WBW06222" buttonId="WBW06222Search"
									type="button" value="Search" styleClass="button mr-1" secured="false" onClick=""/>
								<sc2:button functionId="BW0622"  screenId="WBW06222" buttonId="WBW06222Clear"
									type="button" value="Clear" styleClass="button" secured="false" onClick=""/>
							</div>
	                   </div>
	               </div>
			</div>
		</div>
		</form:form>
	</div>
	
	<div id="data-head-panel" class="row pt-3" style="border: none;">	
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
						    					cssStyle="height:240px;width:100%;"
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
										  
							<datatables:column title="No." cssClass="col1 rownum" sortable="false" searchable="false" cssStyle="text-align:center;" />
			        		<datatables:column title="DDMS Status" cssClass="col2" cssStyle="text-align:center;" sortable="false" searchable="false"/>
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
	
	<div class="row pt-1 pb-5">
		<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12" style="text-align: right;">
           	<sc2:button functionId="BW0622"  screenId="WBW06222" buttonId="WBW06222Confirm" type="button" value="Confirm"
				styleClass="button" secured="false" onClick=""/>
       	</div>
	</div>
</div>