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
	
	$(function(){
		
		$('#insActivationDate, #insActivationPeriod, #insSuccessRenewDate').datepicker({
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

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW04110.js?t=${currentTimestamp}"/>

<form>
	<input name="firstResult" type="hidden" value="0" default="0" />
	<input name="rowsPerPage" type="hidden" value="10" default="10" />
	<input name="messageResult" id="messageResult"  type="hidden" />
	
	<div id="screen-panel" class="container-fluid">
		<div class="row"> 
            <div class="pt-2 col-12">
                <label>Toyota Dealer&nbsp;:&nbsp;Test Dealer</label>
            </div>
            <div class="col-12">
                <label>Branch&nbsp;:&nbsp;XXXX</label>
            </div>
            <div class="col-12">
                <label>Accumulation of month premium&nbsp;:&nbsp;XXX THB</label>
            </div>
            <div class="col-12">
				<hr class="my-3">
			</div>
		 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	               <h3>Screening Criteria</h3>
	               
	        		<div class="row mt-1">
	               		<div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="insuranceCompany" class="mx-1 my-0">Insurance Company&nbsp;:</label>
	                       	<select class="form-control form-control-sm" id="insuranceCompany">
	                           <option>All</option>
	                           <option>AIOI</option>
	                           <option>Viriya</option>
	                       	</select>
	                   	</div>
	                   	<div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="exampleAmount" class="mx-1 my-0">INS&nbsp;:</label>
	                       	<input type="text" class="form-control form-control-sm" id="exampleAmount">
	                   	</div>
	                   	<div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="insActivationDate" class="mx-1 my-0">INS Activation Date&nbsp;:</label>
	                       	<div class="d-flex flex-row">
	                       		<input type="text" class="form-control form-control-sm" id="insActivationDate">
	                       	</div>
	                   	</div>
	                   	<div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="automotiveBrand" class="mx-1 my-0">Automotive Brand&nbsp;:</label>
	                       	<input type="text" class="form-control form-control-sm" id="automotiveBrand">
	                   	</div>
	                   	<div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       <label for="exampleCtrl" class="mx-1 my-0">Class of INS&nbsp;:</label>
	                       <select class="form-control form-control-sm" id="exampleSt">
	                           <option>1st class</option>
	                           <option>2nd class</option>
	                           <option>3rd class</option>
	                           <option>other</option>
	                       </select>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="insActivationPeriod" class="mx-1 my-0">INS Activation Period&nbsp;:</label>
	                       	<div class="d-flex flex-row">
	                       		<input type="text" class="form-control form-control-sm" id="insActivationPeriod">
	                       	</div>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="customerName" class="mx-1 my-0">Customer Name&nbsp;:</label>
	                       	<input type="text" class="form-control form-control-sm" id="customerName">
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="vehicleModel" class="mx-1 my-0">Vehicle Model&nbsp;:</label>
	                       	<input type="text" class="form-control form-control-sm" id="vehicleModel">
	                   </div>	                   
	               	   <div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       <label for="typeOfInsurance" class="mx-1 my-0">Type of Insurance&nbsp;:</label>
	                       <select class="form-control form-control-sm" id="typeOfInsurance">
	                           <option>Care</option>
	                           <option>Non Care</option>
	                           <option>LT INS</option>
	                           <option>C. INSURE</option>
	                       </select>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="insSuccessRenewDate" class="mx-1 my-0">INS Success Renew Date&nbsp;:</label>
	                       	<div class="d-flex flex-row">
	                       		<input type="text" class="form-control form-control-sm" id="insSuccessRenewDate">
	                       	</div>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="discount" class="mx-1 my-0">Discount&nbsp;:</label>
	                       	<input type="text" class="form-control form-control-sm" id="discount">
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-3 col-lg-3 col-md-6 col-sm-6 col-12">
	                       	<label for="licensePlate" class="mx-1 my-0">License Plate&nbsp;:</label>
	                       	<input type="text" class="form-control form-control-sm" id="licensePlate">
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12" style="text-align: right;">
	                       <sc2:button functionId="BW0651"  screenId="WBW06510" buttonId="WBW06510Search"
								type="button" value="Search"
								styleClass="button" secured="false" onClick="clearSearch();"
							/>
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
</form>