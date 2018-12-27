<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="views" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<views:script src="json2.js"/>
<views:script src="jquery.caret.1.02.min.js"/>

<views:style src="tim/WBW04210.css"/>

<style>

</style>

<script>
	var __provinceList = ${provinceList};

	mappingPath = '${_mappingPath}';
	firstResult = '${form.firstResult}';
	rowsPerPage = '${form.rowsPerPage}';
	MBW00001ACFM = '<spring:message code="MBW00001ACFM"></spring:message>';
	MSTD0003ACFM = '<spring:message code="MSTD0003ACFM"></spring:message>';
	MSTD0031AERR = '<spring:message code="MSTD0031AERR"></spring:message>';
	
</script>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate pattern = "yyyyMMddmmss"  value="${now}" var="currentTimestamp" />

<views:script src="tim/WBW04210.js?t=${currentTimestamp}"/>

<form id="result" 
		action="dataList" 
		method="post"  
		ajax="updateObjectFinish" 
		ajax-loading-target="#screen-panel" 
 		validate-error="saveValidateError"> 		
 		<input name="updateDate" id="updateDate"  type="hidden" />
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
		        						<input type="text" class="form-control form-control-sm MandatoryField" id="brand" name="brand">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="vin" class="mx-1 my-0">VIN&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm MandatoryField" id="vin" name="vin">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="model" class="mx-1 my-0">Model&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm MandatoryField" id="model" name="model">
		        					</div>
	        					</div>
        					</div>
        					<div class="col-md-6 col-12">
        						<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="engine" class="mx-1 my-0">Engine&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm MandatoryField" id="engine" name="engine">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="grade" class="mx-1 my-0">Grade&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="grade" name="grade">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="color" class="mx-1 my-0">Color&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="color" name="color">
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
				                       	<select class="form-control form-control-sm MandatoryField" id="typeOfPurchase" name="typeOfPurchase">
				                           <option value="Cash">Cash</option>
				                           <option value="Financial">Financial</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-9 col-12">
		        						<label for="fnCompany" class="mx-1 my-0">FN Company&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="fnCompany" name="fnCompany">
				                           <option value="TLT">TLT</option>
				                           <option value="KK">KK</option>
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
		        						<label for="compulsoryIns" class="mx-1 my-0"><spring:message code="Label.Compulsory.Insurance"/>&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="compulsoryIns" name="compulsoryIns">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="premiumOfCompulsoryIns" class="mx-1 my-0">Premium of Compulsory INS&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="premiumOfCompulsoryIns" name="premiumOfCompulsoryIns">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="typeOfInsurance" class="mx-1 my-0">Type of Insurance&nbsp;:</label>
				                       <select class="form-control form-control-sm" id="typeOfInsurance" name="typeOfInsurance">
				                           <option value="1st">1st class</option>
				                           <option value="2nd">2nd class</option>
				                           <option value="3rd">3rd class</option>
				                           <option value="Oth">other</option>
				                       </select>
		        					</div>
		        				</div>
	        				</div>
	        				<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="insuranceCompany" class="mx-1 my-0">Insurance Company&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="insuranceCompany" name="insuranceCompany">
				                           <option value="">All</option>
				                           <option value="AIOI">AIOI</option>
				                           <option value="Viriya">Viriya</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="breakdownTypeOf1ClassIns" class="mx-1 my-0">Break down type of 1st class Insurance&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="breakdownTypeOf1ClassIns" name="breakdownTypeOf1ClassIns">
				                           <option value="TC">T. Care</option>
				                           <option value="NT">Non T.Care</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="breakdownTypeOfTCareNonTCare" class="mx-1 my-0">Breakdown type of T.Care/ Non T.Care&nbsp;:</label>
		        						<select class="form-control form-control-sm" id="breakdownTypeOfTCareNonTCare" name="breakdownTypeOfTCareNonTCare">
				                           <option value="1Y">1year</option>
				                           <option value="LT">Long Term Insurance</option>
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
		        						<label for="durationOfLongTermInsCoverage" class="mx-1 my-0">Duration of Long Term Insurance Coverage&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="durationOfLongTermInsCoverage" name="durationOfLongTermInsCoverage">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="premiumInput" class="mx-1 my-0">Premium input&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="premiumInput" name="premiumInput" maxlength="10">
		        						</div>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="activation" class="mx-1 my-0">Activation&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="activation" name="activation" maxlength="10">
		        						</div>
		        					</div>
		        				</div>
        					</div>
        					<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="coverageUntil" class="mx-1 my-0">Coverage until&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="coverageUntil" name="coverageUntil" maxlength="10">
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
        						<select class="form-control form-control-sm" id="typeOfCustomer" name="typeOfCustomer">
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
		        						<label for="addressDeliveryINSPolicy" class="mx-1 my-0">Address to delivery INS policy&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="addressDeliveryINSPolicy" name="addressDeliveryINSPolicy">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-8 col-12">
		        						<label for="idCardNo" class="mx-1 my-0">ID Card No.&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="idCardNo" name="idCardNo">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="gender" class="mx-1 my-0">Gender&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="gender" name="gender">
				                           <option value="M">Male</option>
				                           <option value="F">Female</option>
				                       	</select>
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-2 col-12">
		        						<label for="prefix" class="mx-1 my-0">Prefix&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="prefix" name="prefix">
				                       		<option value="">Select</option>
				                           	<option value="Mr.">Mr.</option>
				                           	<option value="Ms.">Ms.</option>
				                           	<option value="Miss.">Miss.</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-2 col-12">
		        						<label for="title" class="mx-1 my-0">Title&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="title" name="title">
				                       		<option value="">Select</option>
				                           	<option value="Military">Military</option>
				                           	<option value="Police">Police</option>
				                           	<option value="Gov.">Gov.</option>
				                           	<option value="Doctorial">Doctorial</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="name" class="mx-1 my-0">Name&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="name" name="name">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="lastName" class="mx-1 my-0">Last Name&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="lastName" name="lastName">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="birthDate" class="mx-1 my-0">Date of birth&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="birthDate" name="birthDate" maxlength="10">
		        						</div>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="contactNumber1" class="mx-1 my-0">Contact number 1&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="contactNumber1" name="contactNumber1">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="contactNumber2" class="mx-1 my-0">Contact number 2&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="contactNumber2" name="contactNumber2">
		        					</div>
		        				</div>
		        			</div>
        					<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="referContactNumber" class="mx-1 my-0">Reference contact number&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="referContactNumber" name="referContactNumber">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="relationshipWithCustomer" class="mx-1 my-0">Relationship with customer&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="relationshipWithCustomer" name="relationshipWithCustomer">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="lineId" class="mx-1 my-0">Line ID&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="lineId" name="lineId">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="email" class="mx-1 my-0">Email&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="email" name="email">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="address" class="mx-1 my-0">Address&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="address" name="address">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="province" class="mx-1 my-0">Province&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="province" name="province">
				                       		<option value="">&lt;Select&gt;</option>
				                       	</select>
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-md-4 col-12">
		        						<label for="amphur" class="mx-1 my-0">District&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="amphur" name="amphur">
				                       		<option value="">&lt;Select&gt;</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="district" class="mx-1 my-0">Sub District&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="district" name="district">
				                       		<option value="">&lt;Select&gt;</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="zipcode" class="mx-1 my-0">Postal Code&nbsp;:</label>
		        						<input type="text" class="form-control form-control-sm" id="zipcode" name="zipcode" maxlength="5">
		        					</div>
		        				</div>
		        			</div>
        				</div>
        					
        				<div class="row" id="customerJuristic">
        					<div class="col-md-6 col-12">
			     				<div class="row"> 
		        					<div class="col-12">
		        						<label for="compAddressDeliveryINSPolicy" class="mx-1 my-0">Address to delivery INS policy&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compAddressDeliveryINSPolicy" name="compAddressDeliveryINSPolicy">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-4 col-12">
		        						<label for="compTradingId" class="mx-1 my-0"><spring:message code="Label.Company.Trading.ID"/>&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="compTradingId" name="compTradingId">
		        					</div>
		        					<div class="col-md-8 col-12">
		        						<label for="compName" class="mx-1 my-0">Company Name&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="compName" name="compName">
		        					</div>
		        				</div>
		        			</div>
        					<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-4 col-12">
		        						<label for="compBranchNo" class="mx-1 my-0">Branch number&nbsp;:</label>
		                       			<input type="text" class="form-control form-control-sm" id="compBranchNo" name="compBranchNo">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="compRegisterDate" class="mx-1 my-0">Date of company registration for trading ID&nbsp;:</label>
		        						<div class="d-flex flex-row">
		        							<input type="text" class="form-control form-control-sm" id="compRegisterDate" name="compRegisterDate" maxlength="10">
		        						</div>
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="compContactName" class="mx-1 my-0">Name (of one of the board committee/ contact person)&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compContactName" name="compContactName">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-4 col-12">
		        						<label for="compContactLastName" class="mx-1 my-0">Last Name (of one of the board committee/ contact person)&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compContactLastName" name="compContactLastName">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="compPhone" class="mx-1 my-0">Company's phone number&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compPhone" name="compPhone">
		        					</div>
		        					<div class="col-md-4 col-12">
		        						<label for="compContactNumber1" class="mx-1 my-0">1st Phone number of contact person&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compContactNumber1" name="compContactNumber1">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
		        					<div class="col-md-3 col-12">
		        						<label for="compContactNumber2" class="mx-1 my-0">2nd Phone number of contact person&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compContactNumber2" name="compContactNumber2">
		        					</div>
		        					<div class="col-md-3 col-12">
		        						<label for="compreferContactNumber" class="mx-1 my-0">Phone number of reference person&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compreferContactNumber" name="compreferContactNumber">
		        					</div>
		        					<div class="col-md-6 col-12">
		        						<label for="compAddress" class="mx-1 my-0">Address&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compAddress" name="compAddress">
		        					</div>
		        				</div>
		        			</div>
		        			<div class="col-md-6 col-12">
			     				<div class="row">
			     					<div class="col-md-3 col-12">
		        						<label for="compProvince" class="mx-1 my-0">Province&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="compProvince" name="compProvince">
				                       		<option value="">&lt;Select&gt;</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-3 col-12">
		        						<label for="compAmphur" class="mx-1 my-0">District&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="compAmphur" name="compAmphur">
				                       		<option value="">&lt;Select&gt;</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-3 col-12">
		        						<label for="compDistrict" class="mx-1 my-0">Sub District&nbsp;:</label>
				                       	<select class="form-control form-control-sm" id="compDistrict" name="compDistrict">
				                       		<option value="">&lt;Select&gt;</option>
				                       	</select>
		        					</div>
		        					<div class="col-md-3 col-12">
		        						<label for="compZipcode" class="mx-1 my-0">Postal Code&nbsp;:</label>
        								<input type="text" class="form-control form-control-sm" id="compZipcode" name="compZipcode" maxlength="5">
		        					</div>
		        				</div>
		        			</div>
        				</div>
			      	</div>
			  	</div>
			</div>
		</div>
		
		<div class="row"> 
        	<div class="py-1 col-md-6 col-12">
				<div class="card">
					<div class="card-header text-white bg-success">Salesperson Information</div>
			     	<div class="card-body">
			        	<div class="row"> 
        					<div class="col-md-2 col-12">
        						<label for="saleTitle" class="mx-1 my-0">Title&nbsp;:</label>
		                       <select class="form-control form-control-sm" id="saleTitle" name="saleTitle">
		                           <option value="Mr.">Mr.</option>
		                           <option value="Ms.">Ms.</option>
		                           <option value="Miss.">Miss.</option>
		                       </select>
        					</div>
        					<div class="col-md-5 col-12">
        						<label for="saleName" class="mx-1 my-0">Name&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="saleName" name="saleName">
        					</div>
        					<div class="col-md-5 col-12">
        						<label for="saleLastName" class="mx-1 my-0">Last Name&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="saleLastName" name="saleLastName">
        					</div>
        					<div class="col-md-4 col-12">
        						<label for="saleTelephone" class="mx-1 my-0">Telephone number&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="saleTelephone" name="saleTelephone">
        					</div>
        					<div class="col-md-8 col-12">
        						<label for="saleIdCardNo" class="mx-1 my-0">TMT's salesperson ID card No.&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="saleIdCardNo" name="saleIdCardNo">
        					</div>
        				</div>
			      	</div>
			  	</div>
			</div>
			<div class="py-1 col-md-6 col-12">
				<div class="card">
					<div class="card-header text-white bg-success">Administration Information</div>
			     	<div class="card-body">
			        	<div class="row"> 
        					<div class="col-md-2 col-12">
        						<label for="adminTitle" class="mx-1 my-0">Title&nbsp;:</label>
		                       <select class="form-control form-control-sm" id="adminTitle" name="adminTitle">
		                           <option value="Mr.">Mr.</option>
		                           <option value="Ms.">Ms.</option>
		                           <option value="Miss.">Miss.</option>
		                       </select>
        					</div>
        					<div class="col-md-5 col-12">
        						<label for="adminName" class="mx-1 my-0">Name&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="adminName" name="adminName">
        					</div>
        					<div class="col-md-5 col-12">
        						<label for="adminLastName" class="mx-1 my-0">Last Name&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="adminLastName" name="adminLastName">
        					</div>
        					<div class="col-md-4 col-12">
        						<label for="adminTelephone" class="mx-1 my-0">Telephone number&nbsp;:</label>
        						<input type="text" class="form-control form-control-sm" id="adminTelephone" name="adminTelephone">
        					</div>
        				</div>
			      	</div>
			  	</div>
			</div>
		</div>
		
		<div class="row pt-1 pb-5">
			<div class="py-1 col-12" style="text-align: right;">
            	<sc2:button functionId="BW0421"  screenId="WBW04210" buttonId="WBW04210Save" type="button" value="Save"
					styleClass="button" secured="false" onClick="saveAddEditObject();"/>
				<sc2:button functionId="BW0421"  screenId="WBW04210" buttonId="WBW04210Cancel" type="button" value="Cancel"
					styleClass="button" secured="false" onClick="cancelAddEditObject();"/>
        	</div>
		</div>
		
	</div>
</form>