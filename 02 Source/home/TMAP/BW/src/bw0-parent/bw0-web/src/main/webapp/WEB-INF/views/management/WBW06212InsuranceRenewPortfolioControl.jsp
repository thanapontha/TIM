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

<%-- <views:style src="gwrds/WBW04110.css"/> --%>
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
				      text: 'Chart A'
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
						      text: 'Chart B'
						    },
						    animation: {
						      animateScale: true,
						      animateRotate: true
						    }
						  }
						};
				
				var ctx = document.getElementById("pieChart2").getContext("2d");
				new Chart(ctx, configChart2);
	});
	
</script>
<style>
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
		    <div class="p-2 bd-highlight"><button type="button" class="btn btn-primary btn-md disabled">2. INS Renew Portfolio control</button></div>
		    <div class="p-2 bd-highlight"><button type="button" class="btn btn-secondary btn-md" onclick="location.href = 'TeleSalesTeamManagement';">3. Sales Team Management</button></div>
		</div>
		<div class="row"> 
	       	<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<div class="card">
					<div class="card-header text-white bg-primary">1. T. Care INS Company BP Returning Ratio Ranking</div>
			     	<div class="card-body">
			        	<div class="row"> 
	       					<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	       						<p>1. AIOI = 99%</p>
								<p>2. XX = XX%</p>
	       					</div>
	       				</div>
			      	</div>
			  	</div>
			</div>
			<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<div class="card">
					<div class="card-header text-white bg-primary">2. Non T. Care INS Company BP Returning Ratio Ranking</div>
			     	<div class="card-body">
			        	<div class="row"> 
	       					<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	       						<p>1. YY = XX%</p>
								<p>2. XX = XX%</p>
	       					</div>
	       				</div>
			      	</div>
			  	</div>
			</div>
		</div>
		<div class="row"> 
	       	<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<div class="card">
					<div class="card-header text-white bg-warning">3. Suggested This Month New Car Insurance Portfolio Setting</div>
			     	<div class="card-body">
			        	<div class="row"> 
	       					<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	       						<canvas id="pieChart1"></canvas>
	       					</div>
	       				</div>
			      	</div>
			  	</div>
			</div>
			<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<div class="card">
					<div class="card-header text-white bg-warning">4. XX (month) New Car Insurance Portfolio Setting</div>
			     	<div class="card-body">
			        	<div class="row"> 
	       					<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	       						<canvas id="pieChart2"></canvas>
	       					</div>
	       				</div>
			      	</div>
			  	</div>
			</div>
		</div>
		<div class="d-flex justify-content-end">
			<sc2:button functionId="BW0621" 
					    screenId="WBW06212" 
					    buttonId="WBW06212Confirm"
						type="button" 
						value="Confirm" 
						styleClass="btn btn-primary btn-md" 
						secured="false" />
		</div>
	</div>
</form>