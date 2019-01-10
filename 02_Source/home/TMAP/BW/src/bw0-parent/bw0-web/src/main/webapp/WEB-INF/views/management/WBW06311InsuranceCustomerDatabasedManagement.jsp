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
	
	
	$(function(){
        var panelList = $('.draggablePanelList');

        panelList.sortable({
            // Only make the .panel-heading child elements support dragging.
            // Omit this to make then entire <li>...</li> draggable.
            handle: '.panel-heading', 
            update: function() {
                $('.panel', panelList).each(function(index, elem) {
                     var $listItem = $(elem),
                         newIndex = $listItem.index();

                     // Persist the new indices.
                });
            }
        });
    });
</script>
<style>
#tblOverallSOPSetup tr td {
	text-align: center;
}

/* The heart of the matter */
.testimonial-group > .row {
  overflow-x: auto;
  white-space: nowrap;
}
.testimonial-group > .row > .col-xs-4 {
  display: inline-block;
  float: none;
}

/* Decorations */
.col-4 { color: #fff; font-size: 48px; padding-bottom: 20px; padding-top: 18px; }
.col-4:nth-child(3n+1) { background: #c69; }
.col-4:nth-child(3n+2) { background: #9c6; }
.col-4:nth-child(3n+3) { background: #69c; }

.centered {
    position: absolute;
    top: 60%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: .9rem;
}
.cal-container{
	position: relative;
	text-align: center;
}
.panel-heading {
        cursor: move;
    }
</style>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW04110.js?t=${currentTimestamp}"/>

<form>
	<input name="firstResult" type="hidden" value="0" default="0" />
	<input name="rowsPerPage" type="hidden" value="10" default="10" />
	<input name="messageResult" id="messageResult"  type="hidden" />
	
	<div id="screen-panel" class="container-fluid mb-5">
		<div class="d-flex flex-wrap justify-content-around bd-highlight">
		    <div class="p-2 bd-highlight"><button type="button" class="btn btn-secondary btn-md" onclick="location.href = 'StandardRenewalSOPPICSetting';">1. Overall SOP Set up</button></div>
		    <div class="p-2 bd-highlight"><button type="button" class="btn btn-secondary btn-md" onclick="location.href = 'InsuranceRenewPortfolioControl';">2. INS Renew Portfolio control</button></div>
		    <div class="p-2 bd-highlight"><button type="button" class="btn btn-primary btn-md">3. Sales Team Management</button></div>
		</div>

		<div class="row">
			<div class="py-1 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
				<div class="card">
					<div class="card-header text-white bg-primary">1. Team Set up</div>
			     	<div class="card-body">
			        	<div class="row"> 
			   				<div class="col-3">
								<p>Team A<br/>
								2nd Y.  Success Ratio: 30%<br/>
								Cust Data: 5,000</p>
							</div>
							<div class="col-9">
								<div class="draggablePanelList d-flex flex-row">
							        <div>
								        <div class="panel-heading">
								        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											<span class="panel-body" style="border: black solid 1px;">10% 500</span>
										</div>
									</div>
									<div>
									  	<div class="panel-heading">
								        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											<span class="panel-body" style="border: black solid 1px;">20% 1000</span>
										</div>
									</div>
									<div>
										<div class="panel-heading">
								        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											<span class="panel-body" style="border: black solid 1px;">10% 50</span>
										</div>
									</div>
								</div>
							</div><!--end col-->
			    		</div>
			    		<hr/>
			    		<div class="row">
			    			<div class="col-12">
			    				<button type="button" class="btn btn-light btn-md" onclick="">Create New Team</button>
			    			</div>
			    		</div>
			    	</div>
			    </div>
			</div>
		</div>
		
		<div class="row">
			<div class="py-1 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
				<div class="card">
					<div class="card-header text-white bg-primary">2. Existing Manpower</div>
			     	<div class="card-body">
			        	<div class="row">
							<div class="py-1 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
								<div class="card">
									<div class="card-header text-white bg-secondary">Sales Team Leader</div>
							     	<div class="card-body">
							        	<div class="row"> 
											<div class="col-12">
												<div class="draggablePanelList d-flex flex-row">
													<div class="">
												        <div class="panel-heading">
												        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="40"/>
														</div>
												  	<div class="panel-body">Content 1 ...</div>
													</div>
													<div class="">
													  	<div class="panel-heading">
															<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="40"/>
														</div>
												    	<div class="panel-body">Content 2 ...</div>
													</div>
												</div>
											</div><!--end col-->
							    		</div>
							    	</div>
							    </div>
							    
							    <div class="card">
									<div class="card-header text-white bg-secondary">Tele Marketing Staff</div>
							     	<div class="card-body">
							        	<div class="row"> 
							   				<div class="col-12">
												<div class="draggablePanelList d-flex flex-row">
													<div>
													  	<div class="panel-heading">
												        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															<span class="panel-body" style="border: black solid 1px;">20% 2000</span>
														</div>
													</div>
													<div>
														<div class="panel-heading">
												        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															<span class="panel-body" style="border: black solid 1px;">10% 30</span>
														</div>
													</div>
												</div>
											</div><!--end col-->
							    		</div>
							    	</div>
							    </div>
							</div>
						</div>
			    	</div>
			    </div>
			    
			
			</div>
		</div>
	
		<div class="d-flex justify-content-end">
			<sc2:button functionId="BW0621" 
					    screenId="WBW06213" 
					    buttonId="WBW06213Confirm"
						type="button" 
						value="Confirm" 
						styleClass="btn btn-primary btn-md" 
						secured="false" />
		</div>
	</div>
</form>