<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<views:script src="json2.js"/>
<views:script src="jquery.caret.1.02.min.js"/>

<views:style src="gwrds/WBW04110.css"/>

<style>

</style>

<script>
	mappingPath = '${_mappingPath}';
	firstResult = '${form.firstResult}';
	rowsPerPage = '${form.rowsPerPage}';
	MSTD0031AERR = '<spring:message code="MSTD0031AERR"></spring:message>';

	
	
	$(function(){
		
		$('#premiumInput, #activation, #birthDate, #dateCompRegistration').datepicker({
			showOn: "button",
			buttonImage: calendarImgPath,
			buttonImageOnly: true,
			buttonText: "Select date",
			dateFormat: 'dd/mm/yy',
			onSelect: function(){
				$(this).focus();	
			}
		});
		
		validateDate(document.getElementById('premiumInput'));
		validateDate(document.getElementById('activation'));
		validateDate(document.getElementById('birthDate'));
		validateDate(document.getElementById('dateCompRegistration'));
		
		$('#coverageUntil').MonthPicker({
	        MonthFormat: 'M-y', // Short month name, Full year.
	        Button: '<img class="ui-datepicker-trigger" title="Select Month" src='+calendarImgPath+ '/>',
	        AltFormat: 'M-y', //result
	        onSelect: function(){
				$(this).focus();
			},
			OnAfterChooseMonth: function(){
				$('#coverageUntil').focus();
			}
	    });
		
		validateDateMMMYY(document.getElementById('coverageUntil'));
		
		$('#typeOfCustomer').change(function() {			
			if($(this).val()=='Individual'){
				$('#customerIndividual').show();
				$('#customerJuristic').hide();
			}else{
				$('#customerIndividual').hide();
				$('#customerJuristic').show();
			}
		});
		
		$('#typeOfCustomer').val('Individual').change();
		 
	});
	
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="gwrds/WBW04110.js?t=${currentTimestamp}"/>

<form>
	<div id="screen-panel" class="container-fluid">
		<div class="row"> 
        	<div class="py-1 col-12">
				<div class="card">
					<div class="card-header text-white bg-primary">Vehicle Information</div>
			     	<div class="card-body">
			     		<div class="row"> 
	        				<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="brand" class="mx-1 my-0">Brand&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm MandatoryField" id="brand" value="Toyota" disabled>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="vin" class="mx-1 my-0">VIN&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm MandatoryField" id="vin" value="3242423233" disabled>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="model" class="mx-1 my-0">Model&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm MandatoryField" id="model" value="Altis" disabled>
		        					</div>
	        					</div>
	       					</div>
	       					<div class="col-md-6 col-12">
	       						<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="engine" class="mx-1 my-0">Engine&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm MandatoryField" id="engine" value="XXX" disabled>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="grade" class="mx-1 my-0">Grade&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="grade" value="SE" disabled>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="color" class="mx-1 my-0">Color&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="color" value="BLACK" disabled>
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
					<div class="card-header text-white bg-danger">Financial Information</div>
			     	<div class="card-body">
			        	<div class="row"> 
			        		<div class="col-md-6 col-12">
			        			<div class="row"> 
		        					<div class="col-md-3 col-12">
		        						<label for="typeOfPurchase" class="mx-1 my-0">Type of purchase&nbsp;:</label>
				                       	<select class="form-control form-control-sm MandatoryField" id="typeOfPurchase">
				                           <option>Cash</option>
				                           <option>Financial</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-9 col-12">
		        						<label for="fnCompany" class="mx-1 my-0">FN Company&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="fnCompany">
				                           <option>TLT</option>
				                           <option>KK</option>
				                       	</select>
		        					</div>
	        					</div>
        					</div>
        					<div class="col-md-6 col-12">
        					</div>
        				</div>
			      	</div>
			  	</div>
			</div>
		</div>
		<div class="row"> 
        	<div class="py-1 col-12">
				<div class="card">
					<div class="card-header text-white bg-warning">Insurance Information</div>
			     	<div class="card-body">
			     		<div class="row">
			        		<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="compulsoryInsurance" class="mx-1 my-0"><spring:message code="Label.Compulsory.Insurance"/>&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="compulsoryInsurance">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="vin" class="mx-1 my-0">Premium of Compulsory INS&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="vin">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="exampleCtrl" class="mx-1 my-0">Type of Insurance&nbsp;:</label>
				                       <select class="form-control form-control-sm" id="exampleSt">
				                           <option>1st class</option>
				                           <option>2nd class</option>
				                           <option>3rd class</option>
				                           <option>other</option>
				                       </select>
		        					</div>
		        				</div>
	        				</div>
	        				<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="insuranceCompany" class="mx-1 my-0">Insurance Company&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="insuranceCompany">
				                           <option>All</option>
				                           <option>AIOI</option>
				                           <option>Viriya</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="insuranceCompany" class="mx-1 my-0">Break down type of 1st class Insurance&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="insuranceCompany">
				                           <option>T. Care</option>
				                           <option>Non T.Care</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="insuranceCompany" class="mx-1 my-0">Breakdown type of T.Care/ Non T.Care&nbsp;:</label>
		        						<select class="form-control form-control-sm" id="insuranceCompany">
				                           <option>1year</option>
				                           <option>Long Term Insurance</option>
				                       	</select>
		        						<!-- <div>
					                       	<div class="form-check form-check-inline">
											  <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio1" value="option1">
											  <label class="form-check-label" for="inlineRadio1">1year</label>
											</div>
											<div class="form-check form-check-inline">
											  <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio2" value="option2">
											  <label class="form-check-label" for="inlineRadio2">Long Term Insurance</label>
											</div>
										</div> -->
		        					</div>
			        			</div>
			        		</div>
			        		<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="vin" class="mx-1 my-0">Duration of Long Term Insurance Coverage&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="vin">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="premiumInput" class="mx-1 my-0">Premium input&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="premiumInput" maxlength="10">
		        						</div>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="activation" class="mx-1 my-0">Activation&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="activation" maxlength="10">
		        						</div>
		        					</div>
		        				</div>
        					</div>
        					<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="coverageUntil" class="mx-1 my-0">Coverage until&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="coverageUntil" maxlength="6">
		        						</div>
		        					</div>
		        					<div class="col-md-4 col-12">
		        					</div>
		        					<div class="col-md-4 col-12">
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
					<div class="card-header text-white bg-warning">Customer Information</div>
			     	<div class="card-body">
			        	<div class="row"> 
        					<div class="col-md-2 col-12">
        						<label for="typeOfCustomer" class="mx-1 my-0">Type of customer&nbsp;:</label>
        						<select class="form-control form-control-sm" id="typeOfCustomer">
		                           <option value="Individual">Individual</option>
		                           <option value="Juristic">Juristic person</option>
		                       </select>
        					</div>
        					<div class="col-md-10 col-12">
        					</div>
        				</div>
        				
        				<div class="row" id="customerIndividual"> 
	        				<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-12">
		        						<label for="address" class="mx-1 my-0">Address to delivery INS policy&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="address">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-8 col-12">
		        						<label for="idCardNo" class="mx-1 my-0">ID Card No.&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="idCardNo">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="gender" class="mx-1 my-0">Gender&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="gender">
				                           <option>Male</option>
				                           <option>Female</option>
				                       	</select>
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-2 col-12">
		        						<label for="prefix" class="mx-1 my-0">Prefix&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="prefix">
				                       		<option>Select</option>
				                           	<option>Mr.</option>
				                           	<option>Ms.</option>
				                           	<option>Miss.</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-2 col-12">
		        						<label for="title" class="mx-1 my-0">Title&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="title">
				                       		<option>Select</option>
				                           	<option>Military</option>
				                           	<option>Police</option>
				                           	<option>Gov.</option>
				                           	<option>Doctorial</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="firstName" class="mx-1 my-0">Name&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="firstName">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="lastName" class="mx-1 my-0">Last Name&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="lastName">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="birthDate" class="mx-1 my-0">Date of birth&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="birthDate" maxlength="10">
		        						</div>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="contactNumber1" class="mx-1 my-0">Contact number 1&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="contactNumber1">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="contactNumber2" class="mx-1 my-0">Contact number 2&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="contactNumber2">
		        					</div>
		        				</div>
		        			</div>
        					<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="refContactNumber" class="mx-1 my-0">Reference contact number&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="refContactNumber">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="relationship" class="mx-1 my-0">Relationship with customer&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="relationship">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="lineId" class="mx-1 my-0">Line ID&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="lineId">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="email" class="mx-1 my-0">Email&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="email">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="address" class="mx-1 my-0">Address&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="address">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="subDistrict" class="mx-1 my-0">Sub District&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="subDistrict">
				                       		<option>Select</option>
				                           	<option>XXX</option>
				                       	</select>
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="district" class="mx-1 my-0">District&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="district">
				                       		<option>Select</option>
				                           	<option>XXX</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="province" class="mx-1 my-0">Province&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="province">
				                       		<option>Select</option>
				                           	<option>Bangkok</option>
				                           	<option>XXXX</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="postalCode" class="mx-1 my-0">Postal Code&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="postalCode">
		        					</div>
		        				</div>
		        			</div>
        				</div>
        					
        				<div class="row" id="customerJuristic">
        					<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-12">
		        						<label for="address" class="mx-1 my-0">Address to delivery INS policy&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="address">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-4 col-12">
		        						<label for="companyTradingID" class="mx-1 my-0"><spring:message code="Label.Company.Trading.ID"/>&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="companyTradingID">
		        					</div>
		        					<div class="col-md-8 col-12">
		        						<label for="companyTradingName" class="mx-1 my-0">Company Name&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="companyTradingName">
		        					</div>
		        				</div>
		        			</div>
        					<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-4 col-12">
		        						<label for="branchNumber" class="mx-1 my-0">Branch number&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="branchNumber">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="dateCompRegistration" class="mx-1 my-0">Date of company registration for trading ID&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="dateCompRegistration" maxlength="10">
		        						</div>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="committeeName" class="mx-1 my-0">Name (of one of the board committee/ contact person)&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="committeeName">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-4 col-12">
		        						<label for="committeeLastName" class="mx-1 my-0">Last Name (of one of the board committee/ contact person)&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="committeeLastName">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="companyPhone" class="mx-1 my-0">Company's phone number&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="companyPhone">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="companyPhone1" class="mx-1 my-0">1st Phone number of contact person&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="companyPhone1">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-3 col-12">
		        						<label for="companyPhone2" class="mx-1 my-0">2nd Phone number of contact person&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="companyPhone2">
		        					</div>
		        					<div class="col-md-3 col-12">
		        						<label for="companyPhoneRef" class="mx-1 my-0">Phone number of reference person&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="companyPhoneRef">
		        					</div>
		        					<div class="col-md-6 col-12">
		        						<label for="compAddress" class="mx-1 my-0">Address&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compAddress">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-3 col-12">
		        						<label for="compSubDistrict" class="mx-1 my-0">Sub District&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="compSubDistrict">
				                       		<option>Select</option>
				                           	<option>XXX</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-3 col-12">
		        						<label for="compDistrict" class="mx-1 my-0">District&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="compDistrict">
				                       		<option>Select</option>
				                           	<option>XXX</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-3 col-12">
		        						<label for="compProvince" class="mx-1 my-0">Province&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="compProvince">
				                       		<option>Select</option>
				                           	<option>Bangkok</option>
				                           	<option>XXXX</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-3 col-12">
		        						<label for="compPostalCode" class="mx-1 my-0">Postal Code&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compPostalCode">
		        					</div>
		        				</div>
		        			</div>
        				</div>
			      	</div>
			  	</div>
			</div>
		</div>
		
		<div class="row"> 
        	<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<div class="card">
					<div class="card-header text-white bg-success">Salesperson Information</div>
			     	<div class="card-body">
			        	<div class="row"> 
        					<div class="col-md-2 col-12">
        						<label for="saleTitle" class="mx-1 my-0">Title&nbsp;:</label>
		                       <select class="form-control form-control-sm" id="saleTitle">
		                           <option>Mr.</option>
		                           <option>Ms.</option>
		                           <option>Miss.</option>
		                       </select>
        					</div>
        					<div class="col-md-5 col-12">
        						<label for="saleName" class="mx-1 my-0">Name&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="saleName">
        					</div>
        					<div class="col-md-5 col-12">
        						<label for="saleLastName" class="mx-1 my-0">Last Name&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="saleLastName">
        					</div>
        					<div class="col-md-4 col-12">
        						<label for="saleTelephone" class="mx-1 my-0">Telephone number&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="saleTelephone">
        					</div>
        					<div class="col-md-8 col-12">
        						<label for="saleIDCard" class="mx-1 my-0">TMT's salesperson ID card No.&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="saleIDCard">
        					</div>
        				</div>
			      	</div>
			  	</div>
			</div>
			<div class="py-1 col-xl-6 col-lg-6 col-md-6 col-sm-12 col-12">
				<div class="card">
					<div class="card-header text-white bg-success">Administration Information</div>
			     	<div class="card-body">
			        	<div class="row"> 
        					<div class="col-md-2 col-12">
        						<label for="adminTitle" class="mx-1 my-0">Title&nbsp;:</label>
		                       <select class="form-control form-control-sm" id="adminTitle">
		                           <option>Mr.</option>
		                           <option>Ms.</option>
		                           <option>Miss.</option>
		                       </select>
        					</div>
        					<div class="col-md-5 col-12">
        						<label for="adminName" class="mx-1 my-0">Name&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="adminName">
        					</div>
        					<div class="col-md-5 col-12">
        						<label for="adminLastName" class="mx-1 my-0">Last Name&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="adminLastName">
        					</div>
        					<div class="col-md-4 col-12">
        						<label for="adminTelephone" class="mx-1 my-0">Telephone number&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="adminTelephone">
        					</div>
        				</div>
			      	</div>
			  	</div>
			</div>
		</div>
		<div class="row pb-5"> 
        	<div class="py-1 col-12" style="text-align: right;">
        		<sc2:button functionId="BW0312"  screenId="WBW03120" buttonId="WBW03120Activate"
							type="button" value="Activate" styleClass="button" secured="false" onClick=""/>
        	</div>
        </div>
		
	</div>
</form>