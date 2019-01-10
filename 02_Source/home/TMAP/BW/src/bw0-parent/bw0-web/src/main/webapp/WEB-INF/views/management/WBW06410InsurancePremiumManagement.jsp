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

<style>

.centered {
    position: absolute;
    top: 45%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 2rem;
    color: white;
}
.img-container{
	position: relative;
	text-align: center;
}

</style>

<script>
	mappingPath = '${_mappingPath}';
	firstResult = '${form.firstResult}';
	rowsPerPage = '${form.rowsPerPage}';
	
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW04110.js?t=${currentTimestamp}"/>

<form>
	<div id="screen-panel" class="container-fluid">
		<div class="row"> 
        	<div class="py-1 col-12">
				<div class="card">
					<div class="card-header text-white bg-primary">1. Insurance Premium Management</div>
			     	<div class="card-body">
			     		<div class="row"> 
        					<div class="col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6">
        						<div class="img-container">
									<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/ins_company_box.png"/>
									<div class="centered">AIOI<br/><img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/aioi.png"/></div>
								</div>
        					</div>
        					<div class="col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6">
        						<div class="img-container">
									<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/ins_company_box.png"/>
									<div class="centered">MSIG<br/><img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/msig.png"/></div>
								</div>
        					</div>
        				</div>
			      	</div>
			  	</div>
			</div>
		</div>
		
		<div class="row"> 
			<div class="py-1 col-12">
				<div class="card">
					<div class="card-header text-white bg-danger">2. Normal Premium Offer</div>
			     	<div class="card-body">
			     		<div class="row"> 
				        	<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
								<div class="card">
									<div class="card-header text-white bg-success">1st year</div>
							     	<div class="card-body">
							        	<div class="row"> 
				        					<div class="col-12">
												<label>- T.Care premium List</label>
											</div>
											<div class="col-12">
												<label>&nbsp;&nbsp;&nbsp;&nbsp;- Long Term Insurance List</label>
											</div>
											<div class="col-12">
												<label>&nbsp;&nbsp;&nbsp;&nbsp;- CONVINI-INSURE List</label>
											</div>
											<div class="col-12">
												<label>- Non T. Care List</label>
											</div>
				        					<div class="col-12 d-flex justify-content-end">
				        						<sc2:button functionId="BW0641"  screenId="WBW06410" buttonId="WBW06410Download1"
													type="button" value="PDF Export & Printing" styleClass="button" secured="false" onClick=""/>
				        					</div>
				        				</div>
							      	</div>
							  	</div>
							</div>
							<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
								<div class="card">
									<div class="card-header text-white bg-success">Transfer Premium (both Toyota & Non Toyota Bands)</div>
							     	<div class="card-body">
							        	<div class="row"> 
				        					<div class="col-12">
												<label>- T.Care premium List</label>
											</div>
											<div class="col-12">
												<label>&nbsp;&nbsp;&nbsp;&nbsp;- Long Term Insurance List</label>
											</div>
											<div class="col-12">
												<label>&nbsp;&nbsp;&nbsp;&nbsp;- CONVINI-INSURE List</label>
											</div>
											<div class="col-12">
												<label>- Non T. Care List</label>
											</div>
				        					<div class="col-12 d-flex justify-content-end">
				        						<sc2:button functionId="BW0641"  screenId="WBW06410" buttonId="WBW06410Download2"
													type="button" value="PDF Export & Printing" styleClass="button" secured="false" onClick=""/>
				        					</div>
				        				</div>
							      	</div>
							  	</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row"> 
        	<div class="py-1 col-12">
				<div class="card">
					<div class="card-header text-white bg-primary">3. No Claim Bonus Offer (NCB)</div>
				</div>
			</div>
		</div>
		
       	<div class="row pb-5"> 
    		<div class="col-12">
				<div class="d-flex flex-row col-12 justify-content-end py-0">
	            	<div class="px-1">
	                	<label>Search</label>
	                </div>
	                <div class="px-1">
	                	<input type="text" class="form-control form-control-sm" id="exampleAmount">
	                </div>
	                <div>
	                  	<sc2:button functionId="BW0641"  screenId="WBW06410" buttonId="WBW06410Search"
						type="button" value="Search" styleClass="button" secured="false" onClick=""
						/>
					</div>
				</div>
    		</div>
    		<div class="col-12">    						
    			<div id="data-head-panel" class="row pt-1" style="border: none;">	
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
    	</div>
	</div>
</form>