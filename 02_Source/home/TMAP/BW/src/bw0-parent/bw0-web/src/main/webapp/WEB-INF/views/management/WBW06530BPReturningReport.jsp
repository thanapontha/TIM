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

<views:style src="tim/WBW06530.css"/>
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
		
		var densityCanvas = document.getElementById("barChart");

		Chart.defaults.global.defaultFontFamily = "Lato";
		Chart.defaults.global.defaultFontSize = 12;

		var densityData = {
		  label: 'Return Ratio to Accident Rate',
		  data: [63, 97, 96, 100, 100, 83],
		  backgroundColor: 'rgba(52, 152, 219, 1)',
		  borderWidth: 0,
		  yAxisID: "y-axis-density"
		};

		var densityData2 = {
		  label: 'Return Ratio to Ins. Sale',
		  data: [38, 58, 58, 60, 60, 50],
		  backgroundColor: 'rgba(192, 57, 43, 0.6)',
		  borderWidth: 0,
		  yAxisID: "y-axis-density"
		};

		var planetData = {
		  labels: ["Company A", "Company B", "Company C", "Company D", "Company E", "Sum/Avg"],
		  datasets: [densityData, densityData2]
		};

		var chartOptions = {
		  scales: {
		    xAxes: [{
		      barPercentage: 1,
		      categoryPercentage: 0.5
		    }],
		    yAxes: [{
		      id: "y-axis-density",
		      ticks: {
		          min: 0,
		          max: this.max,// Your absolute max value
		          callback: function (value) {
		            return (value / this.max * 100).toFixed(0) + '%'; // convert it to percentage
		          },
		        },
		        scaleLabel: {
		          display: false,
		          labelString: 'Percentage',
		        }
		    }
		    ]
		  }
		};

		var barChart = new Chart(densityCanvas, {
		  type: 'bar',
		  data: planetData,
		  options: chartOptions
		});
		
	});
	
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="tim/WBW06530.js?t=${currentTimestamp}"/>

<form>
	<input name="firstResult" type="hidden" value="0" default="0" />
	<input name="rowsPerPage" type="hidden" value="10" default="10" />
	<input name="messageResult" id="messageResult"  type="hidden" />
	
	<div id="screen-panel" class="container-fluid">
		<div id="search-criteria">
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
						    
						    <div id="fake-header" style="display: none;">
								<table id="fake-header-table" class="result dataTable fixed-header fixed-column" 
								style="width: 100%; position: relative; border-bottom-width: 0px; border-bottom-style: solid; border-bottom-color: white;">
									<colgroup>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									      <col style="width: 10%;"/>
									   </colgroup>
									<thead>					   
									<tr>
										<th colspan="4" class="headcol">Insurance Sale</th>
										<th colspan="5" class="headcol">BP Repair</th>
										<th colspan="4" class="headcol">DLR's Profit</th>
									</tr>
									</thead>
								</table>
							</div>
						    
							<datatables:table id="result" 
							    					data="${dataList}" 
							    					cdn="false" 
							    					row="row" 
							    					cssClass="result fixedheader" 
							    					paginate="false" 
							    					cssStyle="height:200px;width:100%;"
													info="false" 
													filter="false"
													displayLength="${form.rowsPerPage}" 
													paginationType="full_numbers" 
													fixedPosition="col"
													lengthChange="false"
													sort="false"
													serverData="serverData"
													serverSide="true"
													>
											  
								<datatables:column title="Insurance<br>Company" cssClass="col1" sortable="false" searchable="false" cssStyle="text-align:center;" />
				        		<datatables:column title="Total Unit<br>(Red Plate+ Renew)" cssClass="col2" cssStyle="text-align:center;" sortable="false" searchable="false"/>
				        		<datatables:column title="Sum. Premium" cssClass="col3" sortable="false" cssStyle="text-align:center;" />
								<datatables:column title="Current Port" cssClass="col4" sortable="false" cssStyle="text-align:center;" />
								<datatables:column title="Expected BP Unit<br>(Refer Accident Rate 0.76)" cssClass="col5" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="BP CPU**" cssClass="col6" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="BP Sale Amount<br>(Current)(A)" cssClass="col7" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Return Ratio<br>to Accident Rate" cssClass="col8" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Return Ratio<br>to Ins. Sale" cssClass="col9" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Insurance Com." cssClass="col10" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="BP Profit" cssClass="col11" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Ttl Profit" cssClass="col12" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								<datatables:column title="Profit/Policy" cssClass="col13" sortable="false" searchable="false" cssStyle="text-align:center;"/>
								
							</datatables:table>
						</form>		
					</div>
				</div>
			</div>
		</div>
		
		<div class="row"> 
		 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
		 		<div class="form-row mt-1">
                	<div class="pb-5 col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
                    	<canvas id="barChart" height="80%"></canvas>
			       	</div>
			    </div>
			</div>
		</div>
	</div>
</form>