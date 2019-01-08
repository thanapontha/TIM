<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sc2nav" uri="/WEB-INF/tld/sc2nav.tld"%>

<style>
</style>

<script type="text/javascript">
</script>

<spring:message code="imagepath" var="imagepath" />
<spring:message code="BW0.menu.NewCarInsurance.ActivateNewCarInsurance" var="ActivateNewCarInsuranceLabel" />
<spring:message code="BW0.menu.NewCarInsurance.Report" var="NewCarInsuranceReportLabel" />

<div class="container-fluid">
	<div class="row"> 
	 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	 		<div class="form-row center">
	 			<sc2nav:menuImage type="screenId" screenId="WBW03110" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="NewCarInsurance" title="${ActivateNewCarInsuranceLabel}" src="${imagepath}images/tim/New_Car_Insurance_Red_Plant.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="screenId" screenId="WBW03210" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="NewCarInsurance/Report" title="${NewCarInsuranceReportLabel}" src="${imagepath}images/tim/Report_Data_Export.png">
	 			</sc2nav:menuImage>
            </div>
		</div>
	</div>
</div>