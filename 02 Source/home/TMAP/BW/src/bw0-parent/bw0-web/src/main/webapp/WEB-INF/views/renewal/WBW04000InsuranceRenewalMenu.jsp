<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sc2nav" uri="/WEB-INF/tld/sc2nav.tld"%>

<style>
</style>

<script type="text/javascript">
</script>

<spring:message code="imagepath" var="imagepath" />
<spring:message code="BW0.menu.InsuranceRenewal.Telemarketing" var="TelemarketingLabel" />
<spring:message code="BW0.menu.InsuranceRenewal.AddNewCustomerProfile" var="AddNewCustomerProfileLabel" />

<div class="container-fluid">
	<div class="row"> 
	 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	 		<div class="form-row center">
	 			<sc2nav:menuImage type="screenId" screenId="WBW04110" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="InsuranceRenewal/Telemarketing" title="${TelemarketingLabel}" src="${imagepath}images/tim/Insurance_Renewal.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="screenId" screenId="WBW04210" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="InsuranceRenewal/AddNewCustomerProfile" title="${AddNewCustomerProfileLabel}" src="${imagepath}images/tim/AddNewCustomer.png">
	 			</sc2nav:menuImage>
            	<%-- <div class="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6">
                	<A href="${pageContext.request.contextPath}/InsuranceRenewal/Telemarketing">
                    	<input id="WBW04110" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/Insurance_Renewal.png" title="${TelemarketingLabel}"/>
                    </A>
                    <div>
                    	<label for="exampleAccount" class="my-1 textMenuRed">${TelemarketingLabel}</label>
                    </div>
		        </div>
		        <div class="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6">
	            	<A href="${pageContext.request.contextPath}/InsuranceRenewal/AddNewCustomerProfile">
                    	<input id="WBW04210" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/tim/AddNewCustomer.png" title="${AddNewCustomerProfileLabel}"/>
                    </A>
                    <div>
                    	<label for="exampleAccount" class="my-1 textMenuRed">${AddNewCustomerProfileLabel}</label>
                    </div>
		        </div> --%>
            </div>
		</div>
	</div>
</div>