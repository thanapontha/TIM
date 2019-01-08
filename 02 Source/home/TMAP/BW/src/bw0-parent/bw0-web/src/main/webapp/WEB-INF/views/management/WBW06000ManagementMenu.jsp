<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sc2nav" uri="/WEB-INF/tld/sc2nav.tld"%>

<style>
</style>

<script type="text/javascript">
</script>

<spring:message code="imagepath" var="imagepath" />
<spring:message code="BW0.menu.group.Management" var="ManagementLabel" />
<spring:message code="BW0.menu.sub.group.Management.NewCarInsurance" var="ManagementNewCarInsuranceLabel" />
<spring:message code="BW0.menu.sub.group.Management.InsuranceRenewal" var="ManagementInsuranceRenewalLabel" />
<spring:message code="BW0.menu.sub.group.Management.ManpowerAccount" var="ManagementManpowerAccountLabel" />
<spring:message code="BW0.menu.group.Management.InsurancePremium" var="ManagementInsurancePremiumLabel" />
<spring:message code="BW0.menu.sub.group.Management.Report" var="ManagementReportLabel" />

<div class="container-fluid">
	<div id="managementMenu" class="row"> 
	 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	 		<div class="form-row center">
	 			<sc2nav:menuImage type="subGroupId" subGroupId="BW06100" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/NewCarInsuranceSubMenu" title="${ManagementNewCarInsuranceLabel}" src="${imagepath}images/tim/New_Car_Insurance.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="subGroupId" subGroupId="BW06200" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/InsuranceRenewalManagementSubMenu" title="${ManagementInsuranceRenewalLabel}" src="${imagepath}images/tim/Insurance_Renewal.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="subGroupId" subGroupId="BW06300" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/ManpowerAccountManagementSubMenu" title="${ManagementManpowerAccountLabel}" src="${imagepath}images/tim/Manpower_Account_Management.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="screenId" screenId="WBW06410" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/InsurancePremium" title="${ManagementInsurancePremiumLabel}" src="${imagepath}images/tim/Insurance_Premium_Management.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="subGroupId" subGroupId="BW06500" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/ReportSubMenu" title="${ManagementReportLabel}" src="${imagepath}images/tim/Report.png">
	 			</sc2nav:menuImage>
            </div>
		</div>
	</div>
</div>