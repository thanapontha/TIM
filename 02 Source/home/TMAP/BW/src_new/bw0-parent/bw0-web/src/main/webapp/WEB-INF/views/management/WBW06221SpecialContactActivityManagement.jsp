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


<views:style src="tim/WBW06221.css"/>
<script>
	mappingPath = '${_mappingPath}';
	firstResult = '${form.firstResult}';
	rowsPerPage = '${form.rowsPerPage}';
	
	$(function(){
		
		$('#expiryDate').datepicker({
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

<views:script src="tim/WBW03110.js?t=${currentTimestamp}"/>
	
	<div id="screen-panel" class="container-fluid">
	
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
		               <h3>Screening Criteria</h3>
		               
		               <div class="row mt-1">
		                   <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
		                       <label for="exampleAccount" class="mx-1 my-0">Status of Activity&nbsp;:</label>
		                       <select class="form-control form-control-sm" id="exampleSt">
		                           <option>All</option>
		                           <option>On Going</option>
		                           <option>Terminated</option>
		                       </select>
		                   </div>
		                   <div class="col-xl-3 col-lg-3 col-md-3 col-sm-6 col-12">
		                       	<label for="expiryDate" class="mx-1 my-0">Expiry date&nbsp;:</label>
		                       	<div class="d-flex flex-row">
	                       			<input type="text" class="form-control form-control-sm" id="expiryDate">
	                       		</div>
		                   </div>
		                   
		                   <div class="col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12" style="text-align: right;">
		                   		<div class="d-flex align-items-end justify-content-end" style="height:100%">
			                        <sc2:button functionId="BW0622"  screenId="WBW06221" buttonId="WBW06221Search"
										type="button" value="Search" styleClass="button mr-1" secured="false" onClick=""/>
									<sc2:button functionId="BW0622"  screenId="WBW06221" buttonId="WBW06221Clear"
										type="button" value="Clear" styleClass="button" secured="false" onClick=""/>
								</div>
		                   </div>
		               </div>
				</div>
			</div>
			</form:form>
		</div>
		
		<div id="data-head-panel" class="row" style="border: none; padding-top: 0.6rem;">	
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
											  
								<datatables:column title="No." cssClass="col1 rownum" sortable="false" searchable="false" cssStyle="text-align:center;" />
				        		<datatables:column title="Activity Name" cssClass="col2" cssStyle="text-align:center;" sortable="false" searchable="false"/>
				        		<datatables:column title="Status of Activity" cssClass="col3" sortable="false" cssStyle="text-align:center;" />
								<datatables:column title="Current Progress" cssClass="col4" sortable="false" cssStyle="text-align:center;" />
								<datatables:column title="No. of PIC Involved" cssClass="col5" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Expiry of call" cssClass="col6" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								
							</datatables:table>
						</form>		
					</div>
				</div>
			</div>
		</div>
		
		<div class="row pt-1 pb-5">
			<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12" style="text-align: right;">
            	<sc2:button functionId="BW0622"  screenId="WBW06221" buttonId="WBW06221Download" type="button" value="Export to Excel format"
					styleClass="button" secured="false" onClick=""/>
        	</div>
		</div>
	</div>