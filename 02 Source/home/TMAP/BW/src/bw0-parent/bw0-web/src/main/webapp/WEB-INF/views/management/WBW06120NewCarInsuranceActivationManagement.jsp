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
		
		/*   new Chart(document.getElementById("pie-chart-1"), {
		    type: 'pie',
		    data: {
		      labels: ["AIOI", "XX"],
		      datasets: [{
		        label: "percentage",
		        backgroundColor: ["#3e95cd", "#8e5ea2"],
		        data: [2478,52]
		      }]
		    },
		    options: {
		      responsive: true,
		      title: {
		        display: true,
		        text: 'Portfolio by INS Company'
		      }
		    }
		});
		
		
		new Chart(document.getElementById("pie-chart-2"), {
		    type: 'pie',
		    data: {
		      labels: ["1st Class", "2nd Class", "Other"],
		      datasets: [{
		        label: "percentage",
		        backgroundColor: ["#3e95cd", "#8e5ea2","#3cba9f"],
		        data: [734,784,433]
		      }]
		    },
		    options: {
		      title: {
		        display: true,
		        text: 'Portfolio by INS Type'
		      }
		    }
		});
		
		new Chart(document.getElementById("pie-chart-3"), {
		    type: 'pie',
		    data: {
		      labels: ["Toyota Care", "XX"],
		      datasets: [{
		        label: "percentage",
		        backgroundColor: ["#e8c3b9","#c45850"],
		        data: [5267,433]
		      }]
		    },
		    options: {
		      title: {
		        display: true,
		        text: 'Toyota Care Shared of 1st'
		      }
		    }
		}); 
		 */
		
		
		Chart.defaults.pieLabels = Chart.helpers.clone(Chart.defaults.pie);

		var helpers = Chart.helpers;
		var defaults = Chart.defaults;

		Chart.controllers.pieLabels = Chart.controllers.pie.extend({
			updateElement: function(arc, index, reset) {
		    var _this = this;
		    var chart = _this.chart,
		        chartArea = chart.chartArea,
		        opts = chart.options,
		        animationOpts = opts.animation,
		        arcOpts = opts.elements.arc,
		        centerX = (chartArea.left + chartArea.right) / 2,
		        centerY = (chartArea.top + chartArea.bottom) / 2,
		        startAngle = opts.rotation, // non reset case handled later
		        endAngle = opts.rotation, // non reset case handled later
		        dataset = _this.getDataset(),
		        circumference = reset && animationOpts.animateRotate ? 0 : arc.hidden ? 0 : _this.calculateCircumference(dataset.data[index]) * (opts.circumference / (2.0 * Math.PI)),
		        innerRadius = reset && animationOpts.animateScale ? 0 : _this.innerRadius,
		        outerRadius = reset && animationOpts.animateScale ? 0 : _this.outerRadius,
		        custom = arc.custom || {},
		        valueAtIndexOrDefault = helpers.getValueAtIndexOrDefault;

		    helpers.extend(arc, {
		      // Utility
		      _datasetIndex: _this.index,
		      _index: index,

		      // Desired view properties
		      _model: {
		        x: centerX + chart.offsetX,
		        y: centerY + chart.offsetY,
		        startAngle: startAngle,
		        endAngle: endAngle,
		        circumference: circumference,
		        outerRadius: outerRadius,
		        innerRadius: innerRadius,
		        label: valueAtIndexOrDefault(dataset.label, index, chart.data.labels[index])
		      },

		      draw: function () {
		      	var ctx = this._chart.ctx,
								vm = this._view,
								sA = vm.startAngle,
								eA = vm.endAngle,
								opts = this._chart.config.options;
						
							var labelPos = this.tooltipPosition();
							var segmentLabel = vm.circumference / opts.circumference * 100;
							
							ctx.beginPath();
							
							ctx.arc(vm.x, vm.y, vm.outerRadius, sA, eA);
							ctx.arc(vm.x, vm.y, vm.innerRadius, eA, sA, true);
							
							ctx.closePath();
							ctx.strokeStyle = vm.borderColor;
							ctx.lineWidth = vm.borderWidth;
							
							ctx.fillStyle = vm.backgroundColor;
							
							ctx.fill();
							ctx.lineJoin = 'bevel';
							
							if (vm.borderWidth) {
								ctx.stroke();
							}
							
							if (vm.circumference > 0.15) { // Trying to hide label when it doesn't fit in segment
								ctx.beginPath();
								ctx.font = helpers.fontString(opts.defaultFontSize, opts.defaultFontStyle, opts.defaultFontFamily);
								ctx.fillStyle = "#fff";
								ctx.textBaseline = "top";
								ctx.textAlign = "center";
		            
		            // Round percentage in a way that it always adds up to 100%
								ctx.fillText(segmentLabel.toFixed(0) + "%", labelPos.x, labelPos.y);
							}
		      }
		    });

		    var model = arc._model;
		    model.backgroundColor = custom.backgroundColor ? custom.backgroundColor : valueAtIndexOrDefault(dataset.backgroundColor, index, arcOpts.backgroundColor);
		    model.hoverBackgroundColor = custom.hoverBackgroundColor ? custom.hoverBackgroundColor : valueAtIndexOrDefault(dataset.hoverBackgroundColor, index, arcOpts.hoverBackgroundColor);
		    model.borderWidth = custom.borderWidth ? custom.borderWidth : valueAtIndexOrDefault(dataset.borderWidth, index, arcOpts.borderWidth);
		    model.borderColor = custom.borderColor ? custom.borderColor : valueAtIndexOrDefault(dataset.borderColor, index, arcOpts.borderColor);

		    // Set correct angles if not resetting
		    if (!reset || !animationOpts.animateRotate) {
		      if (index === 0) {
		        model.startAngle = opts.rotation;
		      } else {
		        model.startAngle = _this.getMeta().data[index - 1]._model.endAngle;
		      }

		      model.endAngle = model.startAngle + model.circumference;
		    }

		    arc.pivot();
		  }
		});

		
		var configChart1 = {
		  type: 'pieLabels',
		  data: {
		    datasets: [{
		      data: [
		        1200,
		        1112
		      ],
		      backgroundColor: [
		        "#3e95cd", "#8e5ea2",
		      ],
		      label: 'Dataset 1'
		    }],
		    labels: [
		      "AIOI",
		      "XXX"
		    ]
		  },
		  options: {
		    responsive: true,
		    legend: {
		      	position: "top",
		      	labels: {
		        	boxWidth: 10,
		        	boxHeight: 2
		      	}
		    },
		    title: {
		      display: true,
		      text: 'Portfolio by INS Company'
		    },
		    animation: {
		      animateScale: true,
		      animateRotate: true
		    }
		  }
		};

		var ctx = document.getElementById("pieChart1").getContext("2d");
		new Chart(ctx, configChart1);
		
		var configChart2 = {
				  type: 'pieLabels',
				  data: {
				    datasets: [{
				      data: [
				        1200,
				        1112,
				        5333,
				      ],
				      backgroundColor: [
				        "#2e93cd", "#8e5ea2","#3cba9f",
				      ],
				      label: 'Dataset 1'
				    }],
				    labels: [
				      "1st Class",
				      "2nd Class",
				      "Other"
				    ]
				  },
				  options: {
				    responsive: true,
				    legend: {
				    	position: "top",
					    labels: {
					        boxWidth: 10,
					        boxHeight: 2
					    }
				    },
				    title: {
				      display: true,
				      text: 'Portfolio by INS Type'
				    },
				    animation: {
				      animateScale: true,
				      animateRotate: true
				    }
				  }
				};
		
		var ctx = document.getElementById("pieChart2").getContext("2d");
		new Chart(ctx, configChart2);
		
		var configChart3 = {
				  type: 'pieLabels',
				  data: {
				    datasets: [{
				      data: [
				        1200,
				        533,
				      ],
				      backgroundColor: [
						"#e8c3b9","#c45850",
				      ],
				      label: 'Dataset 1'
				    }],
				    labels: [
				      "Toyota Care",
				      "Test"
				    ]
				  },
				  options: {
				    responsive: true,
				    legend: {
				    	position: "top",
				      	labels: {
				        	boxWidth: 10,
				        	boxHeight: 2
				      	}
				    },
				    title: {
				      display: true,
				      text: 'Toyota Care Shared of 1st'
				    },
				    animation: {
				      animateScale: true,
				      animateRotate: true
				    }
				  }
				};
		
		var ctx = document.getElementById("pieChart3").getContext("2d");
		new Chart(ctx, configChart3);
			
		
		 
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
		 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
		 		
		 		<div class="form-row mt-1">
	                <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-12 col-12">
	                    	<canvas id="pieChart1"></canvas>
			        </div>
			        <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-12 col-12">
	                    	<canvas id="pieChart2"></canvas>
			        </div>
			        <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-12 col-12">
	                    	<canvas id="pieChart3"></canvas>
			        </div>
	               </div>
		 		
	           	<hr class="my-4">
	               <h3>Screening Criteria</h3>
	               
	               <!-- form complex example -->
	               <div class="form-row mt-1">
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       <label for="exampleAccount" class="my-1">INS Status&nbsp;:&nbsp;All</label>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       <label for="exampleCtrl" class="my-1">Type of purchase&nbsp;:&nbsp;All</label>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       <label for="exampleAmount" class="my-1">DDMS Period of time&nbsp;:&nbsp;12/12/2017</label>
	                   </div>
	                   
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       <label for="exampleAccount" class="my-1">DDMS Status&nbsp;:&nbsp;All</label>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       <label for="exampleCtrl" class="my-1">Insurance Company&nbsp;:&nbsp;AIOI</label>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       <label for="exampleAmount" class="my-1">INS Activation Period&nbsp;:&nbsp;12/12/2017</label>
	                   </div>
	                   
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       <label for="exampleAccount" class="my-1">Model&nbsp;:&nbsp;Vios</label>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       <label for="exampleCtrl" class="my-1">Type of Insurance&nbsp;:&nbsp;1st class</label>
	                   </div>
	                   <div class="pb-1 pr-3 col-xl-4 col-lg-4 col-md-6 col-sm-6 col-12">
	                       
	                   </div>
	               </div>
			</div>
		</div>
		<div id="data-head-panel" class="row" style="border: none;">	
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
		<div class="row" style="padding-top: 0.5rem; padding-bottom: 2rem; text-align: right;">	
			<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
				<sc2:button functionId="BW0412"  screenId="WBW04120" buttonId="WBW04120Clear"
								type="button" value="Export to excel format"
								styleClass="button" secured="false" onClick="clearSearch();"
							/>
			</div>
		</div>
	</div>
</form>