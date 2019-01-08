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
	
	
	$( function() {
		 
	   /*  // There's the TeamSetUp and the TeleMarketingStaff
	    var $TeamSetUp = $( "#TeamSetUp" ),
	    	$TeleMarketingStaff = $( "#TeleMarketingStaff" );
	    
	    // Let the TeamSetUp items be draggable
	    $( ".item", $TeamSetUp ).draggable({
			//cancel: "a.ui-icon", // clicking an icon won't initiate dragging
			revert: "invalid", // when not dropped, the item will revert back to its initial position
			containment: "document",
			helper: "clone",
			cursor: "move"
	    });
	 
	    // Let the TeleMarketingStaff be droppable, accepting the TeamSetUp items
	    $TeleMarketingStaff.droppable({
			tolerance: "pointer",
		 	accept: "#TeamSetUp > .item, #TeleMarketingStaff > .item",
		 	drop: function( event, ui ) {
		  		appendToTeleMarketingStaff( ui.draggable );
		 	}
	    });
	    
	    function appendToTeleMarketingStaff( $item) {
	      	$item.fadeOut(function() {
	    	  	$item .appendTo( $TeleMarketingStaff ).fadeIn();
	      	});
	    }
	    
	 	// Let the TeleMarketingStaff items be draggable
	    $( ".item", $TeleMarketingStaff ).draggable({
	      	//cancel: "a.ui-icon", // clicking an icon won't initiate dragging
	      	revert: "invalid", // when not dropped, the item will revert back to its initial position
	      	containment: "document",
	      	helper: "clone",
	      	cursor: "move"
	    });
	    
	 	// Let the TeamSetUp be droppable, accepting the TeleMarketingStaff items
	    $TeamSetUp.droppable({
	    	tolerance: "pointer",
		    accept: "#TeleMarketingStaff > .item, #TeamSetUp > .item",
		    drop: function( event, ui ) {
		    	appendToTeamSetUp( ui.draggable);
		    }
		 });
	 
	    function appendToTeamSetUp( $item) {
	      	$item.fadeOut(function() {
	    	  	$item .appendTo( $TeamSetUp ).fadeIn();
	      	});
	    } */
	     
	    
        $("#TeamSetUp, #TeleMarketingStaff").sortable({
            connectWith: "#TeamSetUp, #TeleMarketingStaff",
            start: function (event, ui) {
                    ui.item.toggleClass("highlight");
            },
            stop: function (event, ui) {
                    ui.item.toggleClass("highlight");
            }
    	});
        
    	$("#TeamSetUp, #TeleMarketingStaff").disableSelection();
		 
	  } );
	
	
</script>
<style>

#TeamSetUp { 
	float: left; 
	max-width: 100%; 
	min-height: 6em; 
	/* max-height: 12.5em; 
	overflow: auto; */
	}
	
#TeleMarketingStaff { 
	float: left; 
	max-width: 100%; 
	min-height: 6em; 
	/* max-height: 12.5em; 
	overflow: auto; */
	}


.items li { float: left; padding: 0.4em; margin: 0.2em 0.4em 0.4em 0; text-align: center; }

.panelData{
	width: 7em;
}

.panel-body{
	border: gray dotted 0px;
}

ul { list-style-type: none;}

.items{
	width: 100%;
	border: black solid 0px;
}

.item{
	text-align: center;
	z-index: 999;
	border: black dotted 1px;
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
			   				<div class="col-2">
								<p>Team A<br/>
								2nd Y.  Success Ratio: 30%<br/>
								Cust Data: 5,000</p>
							</div>
							<div class="col-10">
								<ul id="TeamSetUp" class="items d-flex flex-wrap">
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Telesale NameTest1</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 2</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 3</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 4</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 5</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 6</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 7</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 8</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 9</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 101</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 711</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 811</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
									<li class="item">
										<div class="panelData">
											<div class="panel-heading">
									        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
											</div>
											<div class="panel-body">
												<span>Tele 911</span>
												<br/>
												<span>10% 500</span>
											</div>
										</div>
									</li>
								</ul>
							</div>
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
												<ul id="SalesTeamLeader" class="items d-flex flex-wrap">
													<li class="item">
														<div class="panelData">
													        <div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="40"/>
															</div>
													  		<div class="panel-body">Content 1</div>
													  	</div>
													</li>
													<li class="item">
														<div class="panelData">
														  	<div class="panel-heading">
																<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/cal.png" width="40"/>
															</div>
													    	<div class="panel-body">Content 2</div>
													    </div>
													</li>
												</ul>
											</div>
							    		</div>
							    	</div>
							    </div>
							    
							    <div class="card">
									<div class="card-header text-white bg-secondary">Tele Marketing Staff</div>
							     	<div class="card-body">
							        	<div class="row"> 
							   				<div class="col-12">
							   					<ul id="TeleMarketingStaff" class="items d-flex flex-wrap">
													<li class="item">
														<div class="panelData">
															<div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															</div>
															<div class="panel-body">
																<span>Tele 210</span>
																<br/>
																<span>80% 2,560</span>
															</div>
														</div>
													</li>
													<li class="item">
														<div class="panelData">
															<div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															</div>
															<div class="panel-body">
																<span>Tele 811</span>
																<br/>
																<span>34% 560</span>
															</div>
														</div>
													</li>
													<li class="item">
														<div class="panelData">
															<div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															</div>
															<div class="panel-body">
																<span>Tele 127</span>
																<br/>
																<span>45% 1,500</span>
															</div>
														</div>
													</li>
													<li class="item">
														<div class="panelData">
															<div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															</div>
															<div class="panel-body">
																<span>Tele 160</span>
																<br/>
																<span>80% 2,560</span>
															</div>
														</div>
													</li>
													<li class="item">
														<div class="panelData">
															<div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															</div>
															<div class="panel-body">
																<span>Tele 161</span>
																<br/>
																<span>34% 560</span>
															</div>
														</div>
													</li>
													<li class="item">
														<div class="panelData">
															<div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															</div>
															<div class="panel-body">
																<span>Tele 152</span>
																<br/>
																<span>45% 1,500</span>
															</div>
														</div>
													</li>
													<li class="item">
														<div class="panelData">
															<div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															</div>
															<div class="panel-body">
																<span>Tele 104</span>
																<br/>
																<span>80% 2,560</span>
															</div>
														</div>
													</li>
													<li class="item">
														<div class="panelData">
															<div class="panel-heading">
													        	<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/callcenter_icon.png" width="40"/>
															</div>
															<div class="panel-body">
																<span>Tele 113</span>
																<br/>
																<span>34% 560</span>
															</div>
														</div>
													</li>
												</ul>
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

