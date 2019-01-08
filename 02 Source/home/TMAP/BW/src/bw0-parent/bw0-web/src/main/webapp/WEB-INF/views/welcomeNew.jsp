<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sc2nav" uri="/WEB-INF/tld/sc2nav.tld"%>
<style>
</style>

<script type="text/javascript">
</script>
	
<c:if test="${isApplicationUser == false }">
	<script type="text/javascript">
	(function($){
		$(document).ready(function() {
			var realConfirm = window.confirm;
			window.confirm = null;
			
			function UnPopIt()  { /* nothing to return */ }
			
			var dialogOption = {
					width		: 450,
					buttons		: 
						[{
					        text: '<spring:message code="STD.dialog.ok" />' ,
					        click: function() {
					        	var self = $(this);
					        	self.siblings('.ui-dialog-buttonpane').find('input,button').prop('disabled', true);
								self.dialog('close');
								
								window.onbeforeunload = null;
								$("#left-menu-panel").remove();
								var win=open("","_self", "");
								win.close();
					        }
					    }]	
				};
			ST3Lib.dialog.confirm('<spring:message code="MSTD0012AERR" arguments="Employee No. of User login, Employee Master." />', 'MSTD0012AERR', dialogOption);
		});
	})(ST3Lib.$);;
	</script>
</c:if>

<spring:message code="imagepath" var="imagepath" />
<spring:message code="BW0.menu.group.NewCarInsurance" var="NewCarInsuranceLabel" />
<spring:message code="BW0.menu.group.InsuranceRenewal" var="InsuranceRenewalLabel" />
<spring:message code="BW0.menu.group.InsuranceCompany" var="InsuranceCompanyLabel" />
<spring:message code="BW0.menu.group.Management" var="ManagementLabel" />

<div class="container-fluid">
	<div class="row"> 
	 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	 		<div class="form-row center">
	 			<sc2nav:menuImage type="groupId" groupId="BW0300" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="NewCarInsuranceMenu" title="${NewCarInsuranceLabel}" src="${imagepath}images/tim/New_Car_Insurance.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="groupId" groupId="BW0400" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="InsuranceRenewalMenu" title="${InsuranceRenewalLabel}" src="${imagepath}images/tim/Insurance_Renewal.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="groupId" groupId="BW0500" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="InsuranceCompany" title="${InsuranceCompanyLabel}" src="${imagepath}images/tim/Insurance_Company.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="groupId" groupId="BW0600" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="ManagementMenu" title="${ManagementLabel}" src="${imagepath}images/tim/Management.png">
	 			</sc2nav:menuImage>
            </div>
		</div>
	</div>
</div>