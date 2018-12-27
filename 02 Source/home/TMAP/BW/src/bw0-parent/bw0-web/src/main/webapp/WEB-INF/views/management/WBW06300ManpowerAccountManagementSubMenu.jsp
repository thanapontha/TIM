<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sc2nav" uri="/WEB-INF/tld/sc2nav.tld"%>

<style>	
	
</style>

<script type="text/javascript">
</script>

<spring:message code="imagepath" var="imagepath" />
<spring:message code="BW0.menu.Management.ManpowerAccount.InsuranceCustomerDatabased" var="InsuranceCustomerDatabasedManagementLabel" />
<spring:message code="BW0.menu.Management.ManpowerAccount.AccountManagement" var="AccountManagementLabel" />

<div class="container-fluid">
	<div class="row"> 
	 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	 		<div class="form-row center">
	 			<sc2nav:menuImage type="screenId" screenId="WBW06311" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/InsuranceCustomerDatabasedManagement" title="${InsuranceCustomerDatabasedManagementLabel}" src="${imagepath}images/tim/Insurance_Customer_Databased_Management.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="screenId" screenId="WBW06321" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/AccountManagement" title="${AccountManagementLabel}" src="${imagepath}images/tim/Account_Management.png">
	 			</sc2nav:menuImage>
            </div>
		</div>
	</div>
</div>