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

<views:style src="gwrds/WBW04110.css"/>
<script>
	mappingPath = '${_mappingPath}';
	firstResult = '${form.firstResult}';
	rowsPerPage = '${form.rowsPerPage}';
	MSTD0031AERR = '<spring:message code="MSTD0031AERR"></spring:message>';

	
	
	$(function(){
		
		 
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
                <label>Telesales ID&nbsp;:&nbsp;22323017</label>
            </div>                   
            <div class="col-12">
                <label>Telesales Name&nbsp;:&nbsp;Mr. Mana Talesale</label>
            </div>
            <div class="col-12">
                <label>Telesales Insurance Agent Number&nbsp;:&nbsp;32129243</label>
            </div>
            <div class="d-flex flex-row col-12 justify-content-end py-0">
            	<div class="px-1">
                	<label>Search</label>
                </div>
                <div class="px-1">
                	<input type="text" class="form-control form-control-sm" id="exampleAmount">
                </div>
                <div>
                  	<sc2:button functionId="BW0411"  screenId="WBW04110" buttonId="WBW04110Search"
					type="button" value="Search" styleClass="button" secured="false" onClick="clearSearch();"
					/>
				</div>
			</div>
		</div>
		<div class="row"> 
			<div class="col-12">
				<hr class="my-3">
			</div>
			<div class="col-12">
				<h3>2. Call Plan (divide by activity)</h3>
			</div>
			<div class="col-12">
				<label>1. (100 Customers) Urgent Call (Accident Customer)/ follow up/ Closing Deal/ PAYD Inquiry</label>
			</div>
			<div class="col-12">
				<label>2. (200 Customers) N+1</label>
			</div>
			<div class="col-12">
				<label>3. (200 Customers)  N</label>
			</div>
			<div class="col-12">
				<label>4. (159 Customers)  N-1</label>
			</div>
			<div class="col-12">
				<label>5. (50  Customers)  N-2</label>
			</div>
			<div class="col-12">
				<label>6. (81  Customers)  N-3</label>
			</div>
			<div class="col-12">
				<label>7. (61  Customers)  N-4</label>
			</div>
			<div class="col-12">
				<label>8. (200 Customers)  N-5</label>
			</div>
			<div class="col-12">
				<label>9. (200 Customers)  N-6</label>
			</div>
			<div class="col-12">
				<label>10. (100 Customers) Special Call Plan</label>
			</div>
		</div>
		<div id="data-head-panel" class="row" style="border: none;">	
			<div class="pb-5 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
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
				        		<datatables:column title="Call Activity" cssClass="col2 rownum" cssStyle="text-align:center;" sortable="false" searchable="false"/>
				        		<datatables:column title="Cust. Prefer Timing" cssClass="col3" sortable="false" cssStyle="text-align:center;" />
								<datatables:column title="Call Activity" cssClass="col4" sortable="false" cssStyle="text-align:center;" />
								<datatables:column title="Last Call Who/When" cssClass="col5" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="INS Expiry date" cssClass="col6" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="NCB Premium" cssClass="col7" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Vehicle Model" cssClass="col8" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Claim History" cssClass="col9" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Detail & Call" cssClass="col10" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								
							</datatables:table>
						</form>		
					</div>
				</div>
			</div>
		</div>
	</div>
</form>