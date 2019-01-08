<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sc2nav" uri="/WEB-INF/tld/sc2nav.tld"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="url">${pageContext.request.requestURL}</c:set>
<c:set var="uri">${pageContext.request.requestURI}</c:set>
<c:set var="baseURL">${fn:substring(url, 0, fn:length(url) - fn:length(uri))}${pageContext.request.contextPath}/</c:set>

<style>

	.application-panel{
		background-color:#E60000;
		vertical-align:middle;
		color: white;
		border-top-left-radius: 4px;
	    border-top-right-radius: 4px;
	}
	
	.application {
		margin-left: 3px;
	}
	
	.userinfo {
		float: right;
		line-height: 2.8 !important;
		margin-right: 3px;
	}

	.screenid-panel{
		height: 16px;
		background-color: rgb(127,127,127);
		vertical-align:middle;
		color: white;
	}
	
	.screenid-panel .screenid{
		float: left;
		color: white;
		margin-left: 3px;
	}
	
	.screenid-panel .time,
	#grayline-logo-panel-mobile .time
	 {
	 	float: right;
		color: white;
		position:relative;
		white-space: nowrap;
		margin-right: 3px;
	}

	.screenid-panel .language{
		float: right;
		position:relative;
	}
	
	.top-menu {
		 background: #93c3cd url(${baseURL}/resources/scripts/jquery-ui-1.9.1/themes/hot-sneaks/images/ui-bg_diagonals-small_50_93c3cd_40x40.png) 50% 50% repeat;
	}
	
	.top-menu a:link,  .top-menu a:visited{
		color: black;
	}
	.top-menu a:hover{
		color: white;
	}
	
	.top-menu .dropdown-menu a:active{
		background: #ccd232 url(${baseURL}/resources/scripts/jquery-ui-1.9.1/themes/hot-sneaks/images/ui-bg_diagonals-small_75_ccd232_40x40.png) 50% 50% repeat;
		border-radius: 0.25rem;
	}
	
	.top-menu li.nav-item:hover, .top-menu li.nav-item:active, .top-menu li.nav-item.show, .top-menu li.nav-item.active{
		background: #ff3853 url(${baseURL}/resources/scripts/jquery-ui-1.9.1/themes/hot-sneaks/images/ui-bg_diagonals-small_50_ff3853_40x40.png) 50% 50% repeat;
		border-radius: 0.25rem;
	}
	
	.dropdown-menu {
	  	margin: 0;	  	
	  	padding: .1rem 0;
	  	border: 1px solid rgba(0, 0, 0, .25);
	}

	.dropdown-submenu {
	  	position: relative;
	}
	
	.dropdown-submenu a::after {
	  transform: rotate(-90deg);
	  position: absolute;
	  right: 6px;
	  top: .8em;
	}
	
	.dropdown-submenu .dropdown-menu {
	  	top: 0;
	  	left: 100%;
	  	margin-left: .1rem;
	  	margin-right: .1rem;
	}
	
	.dropdown-submenu .dropdown-menu .dropdown-submenu .dropdown-menu {
	  	top: 0;
	  	left: 100%;
	  	margin-left: -0.1rem;
	  	margin-right: .1rem;
	}
	
	.dropdown-item:hover{
		background-color: #ff3853;
		background: #ff3853 url(${baseURL}/resources/scripts/jquery-ui-1.9.1/themes/hot-sneaks/images/ui-bg_diagonals-small_50_ff3853_40x40.png) 50% 50% repeat;
		border-radius: 0.25rem;
	}
	
	.navbar-toggler{
  		padding:.05rem .75rem;
  		font-size:0.9rem;
  		line-height:1;
  		background-color:transparent;
  		border:1px solid transparent;
  		border-radius:.25rem
  	}  
  	
  	.navbar-brand{
  		padding-top: 0;
  		padding-bottom: 0;
  		margin-left: 0.5rem;
  		margin-right: 0.5rem;
  		font-size: 1rem;
  	}
  	
 	@media (min-width: 768px) {
	  	.navbar-nav > li > a {
	    	padding-top: 6.5px;
	    	padding-bottom: 7px;
	    	line-height: 6px;
	  	}
  	}
  	
	@media (min-width: 768px) and (max-width: 1089px) {
		.dropdown-submenu-newcar{
			margin-left: -41.1rem !important;
		}
		
		.dropdown-submenu-renewal{
			margin-left: -38.7rem !important;
		}
		
		.dropdown-submenu-manpower{
			margin-left: -42rem !important;
		}
		
		.dropdown-submenu-report{
			margin-left: -38.5rem !important;
		}
	}
	
	@media (min-width: 768px) and (max-width: 957px) {
		.dropdown-submenu-common{
			margin: 0rem 0rem 0rem -13.1rem !important;
		}
	}
	
	@media (min-width: 768px) and (max-width: 1350px) {
		.dropdown-submenu-renewal{
			margin-left: -38.7rem !important;
		}
		
		.dropdown-submenu-special-contact{
			margin: 2rem 0rem 0rem -30.5rem !important;
		}
	}

 	.badge2 {
		position:relative;
	}
	
	.badge2[data-badge]:after {
		content:attr(data-badge);
		position:absolute;
		font-family: arial, sans-serif;
		font-weight: bold;
		font-size:.7em;
		line-height: 16px;
		height: 16px;width: 30px;
		padding: 0 5px;
		font-family: Arial, sans-serif;
		text-shadow: 0 1px rgba(0, 0, 0, 0.25);
		border: 1px solid;
		border-radius: 10px;
		-webkit-box-shadow: inset 0 1px rgba(255, 255, 255, 0.3), 0 1px 1px rgba(0, 0, 0, 0.08);
		box-shadow: inset 0 1px rgba(255, 255, 255, 0.3), 0 1px 1px rgba(0, 0, 0, 0.08);
		
		color: white;
		background: #fa623f;
		border-color: #fa5a35;
		background-image: -webkit-linear-gradient(top, #fc9f8a, #fa623f);
		background-image: -moz-linear-gradient(top, #fc9f8a, #fa623f);
		background-image: -o-linear-gradient(top, #fc9f8a, #fa623f);
		background-image: linear-gradient(to bottom, #fc9f8a, #fa623f);
		
		/* Safari */
		-webkit-transform: rotate(360deg);
		
		/* Firefox */
		-moz-transform: rotate(-360deg);
		
		/* IE */
		-ms-transform: rotate(-360deg);
		
		/* Opera */
		-o-transform: rotate(-360deg);
		top: 1px;
		left: 135px;
	}
	

</style>

<script type="text/javascript">

$(document).ready(function () {

	$('.dropdown-menu a.dropdown-toggle').on('click', function(e) {
		  if (!$(this).next().hasClass('show')) {
		    $(this).parents('.dropdown-menu').first().find('.show').removeClass("show");
		  }
		  var $subMenu = $(this).next(".dropdown-menu");
		  $subMenu.toggleClass('show');
		  

		  $(this).parents('li.nav-item.dropdown.show').on('hidden.bs.dropdown', function(e) {
		    $('.dropdown-submenu .show').removeClass("show");
		  });


		  return false;
		});
	
});
	
</script>

<spring:message code="ST3.menu.master.item.system" var="itemSystemMasterLabel" />
<spring:message code="ST3.menu.common.item.log" var="itemLogMonitoringLabel" />
<spring:message code="ST3.menu.common.item.excelDownloadMonitoring" var="itemExcelDownloadMonitoringLabel"/>
<spring:message code="ST3.menu.group.common" var="groupCommonLabel" />
<spring:message code="ST3.menu.group.master" var="groupMasterLabel" />

<spring:message code="BW0.menu.group.NewCarInsurance" var="NewCarInsuranceLabel" />
<spring:message code="BW0.menu.NewCarInsurance.ActivateNewCarInsurance" var="ActivateNewCarInsuranceLabel" />
<spring:message code="BW0.menu.NewCarInsurance.Report" var="NewCarInsuranceReportLabel" />

<spring:message code="BW0.menu.group.InsuranceRenewal" var="InsuranceRenewalLabel" />
<spring:message code="BW0.menu.InsuranceRenewal.Telemarketing" var="TelemarketingLabel" />
<spring:message code="BW0.menu.InsuranceRenewal.AddNewCustomerProfile" var="AddNewCustomerProfileLabel" />

<spring:message code="BW0.menu.group.InsuranceCompany" var="InsuranceCompanyLabel" />

<spring:message code="BW0.menu.group.Management" var="ManagementLabel" />
<spring:message code="BW0.menu.sub.group.Management.NewCarInsurance" var="ManagementNewCarInsuranceLabel" />
<spring:message code="BW0.menu.sub.group.Management.InsuranceRenewal" var="ManagementInsuranceRenewalLabel" />
<spring:message code="BW0.menu.sub.group.Management.ManpowerAccount" var="ManagementManpowerAccountLabel" />
<spring:message code="BW0.menu.group.Management.InsurancePremium" var="ManagementInsurancePremiumLabel" />
<spring:message code="BW0.menu.sub.group.Management.Report" var="ManagementReportLabel" />

<spring:message code="BW0.menu.Management.NewCarInsurance.Portfolio" var="ManagementNewCarInsurancePortfolioLabel" />
<spring:message code="BW0.menu.Management.NewCarInsurance.Activation" var="ManagementNewCarInsuranceActivationLabel" />

<spring:message code="BW0.menu.Management.InsuranceRenewal.SOP.PICSetting" var="StandardRenewalSOPPICSettingLabel" />
<spring:message code="BW0.menu.sub.group.Management.InsuranceRenewal.SpecialContactSetting" var="SpecialContactSettingLabel" />
<spring:message code="BW0.menu.Management.InsuranceRenewal.SpecialContactActivity" var="SpecialContactActivityLabel" />
<spring:message code="BW0.menu.Management.InsuranceRenewal.CreateNewSpecialContact" var="CreateNewSpecialContactLabel" />
<spring:message code="BW0.menu.Management.InsuranceRenewal.ActivitiesReminder" var="ActivitiesReminderLabel" />
<spring:message code="BW0.menu.Management.ManpowerAccount.InsuranceCustomerDatabased" var="InsuranceCustomerDatabasedManagementLabel" />
<spring:message code="BW0.menu.Management.ManpowerAccount.AccountManagement" var="AccountManagementLabel" />

<spring:message code="BW0.menu.Management.Report.ExportOfINSRenewal" var="ExportOfINSRenewalLabel" />
<spring:message code="BW0.menu.Management.Report.ProcessResultKPIs" var="ProcessResultKPIsLabel" />
<spring:message code="BW0.menu.Management.Report.BPReturningReport" var="BPReturningReportLabel" />
<spring:message code="BW0.menu.Management.Report.FirstYearRedPlateINSReport" var="FirstYearRedPlateINSReport" />

	
<div class="fixed-top">
  <div class="application-panel d-flex justify-content-between p-0">
    <div><span class="application"><spring:message code="BW0.lbl.Application"/></span> </div>
    <div><span class="userinfo"><c:out value="${sessionScope.tscuserinfo.firstName}" /></span></div>
  </div>
  <div class="screenid-panel d-flex justify-content-between p-0">
    <div><span class="screenid"><c:out value="${payload.screenId} : ${payload.screenDescription}" /></span></div>
    <div class="d-flex">
      <ul class="d-flex list-unstyled m-0 p-0">
        <li>
        	<a href="${_mappingPath}?language=th_TH" class="px-2">
          		<input tabindex="-1" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>icons/th_TH.png" width="17" title="TH"/>
        	</a>
        </li>
        <li>
        	<a href="${_mappingPath}?language=en_US" class="px-2">
				<input tabindex="-1" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>icons/en_US.png" width="17" title="EN"/>
			</a>
        </li>
       <%--  <li>
        	<a href="${_mappingPath}?language=ja_JP" class="px-2">
				<input tabindex="-1" type="image" src="${pageContext.request.contextPath}<spring:message code="imagepath"/>icons/ja_JP.png" width="17"  title="JP"/>
			</a>
        </li> --%>
      </ul>
      <div class="ml-3 d-none d-sm-block"><span class="time">&nbsp;&nbsp;<c:out value="${payload.dateTimeNow}" /></span></div>
    </div>
  </div>

	<nav class="top-menu navbar navbar-expand-md navbar-light p-0">	  	
	  	<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTogglerDemo01" aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
	    	<span class="navbar-toggler-icon"></span>
	  	</button>
	  	<a class="navbar-brand" href="${pageContext.request.contextPath}">TIM</a>
	  	
	  	<div class="collapse navbar-collapse" id="navbarTogglerDemo01">
	    	<ul class="navbar-nav mr-auto">
	      		<li id="homeMenu" class="nav-item">
          			<a class="nav-link" href="${pageContext.request.contextPath}"><span class="px-1">Home</span></a>
        		</li>
        		<sc2nav:menuGroup groupId="ST3001" title="${groupMasterLabel}" ariaLabelledby="navbarDropdownMenuLink1" haveSubMenu="true">
	      			<ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink1">	      				
	      				<sc2nav:menuItem screenId="WST33060" href="master/systemMaster" target="WST33060">${itemSystemMasterLabel}</sc2nav:menuItem>
	      				<sc2nav:menuItem screenId="WBW04130" href="master/vehicleUnitRelation" target="WBW04130">Vehicle UnitRelation</sc2nav:menuItem>
	      			</ul>
	      		</sc2nav:menuGroup>
        		<sc2nav:menuGroup groupId="BW0300" title="${NewCarInsuranceLabel}" ariaLabelledby="navbarDropdown" haveSubMenu="true">
        			<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
	        			<sc2nav:menuItem screenId="WBW03110" href="NewCarInsurance" target="WBW03110">${ActivateNewCarInsuranceLabel}</sc2nav:menuItem>
	        			<sc2nav:menuItem screenId="WBW03210" href="NewCarInsurance/Report" target="WBW03210">${NewCarInsuranceReportLabel}</sc2nav:menuItem>
	        		</ul>
        		</sc2nav:menuGroup>
        		
				<sc2nav:menuGroup groupId="BW0400" title="${InsuranceRenewalLabel}" ariaLabelledby="navbarDropdown" haveSubMenu="true">
					<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
	        			<sc2nav:menuItem screenId="WBW04110" href="InsuranceRenewal/Telemarketing" target="WBW04110">${TelemarketingLabel}</sc2nav:menuItem>
	        			<sc2nav:menuItem screenId="WBW04210" href="InsuranceRenewal/AddNewCustomerProfile" target="WBW04210">${AddNewCustomerProfileLabel}</sc2nav:menuItem>
	        		</ul>
        		</sc2nav:menuGroup>
        		
        		<sc2nav:menuGroup groupId="BW0500" screenId="WBW05110" href="InsuranceCompany" title="${InsuranceCompanyLabel}" ariaLabelledby="navbarDropdown" haveSubMenu="false">
        		</sc2nav:menuGroup>
	      		
	      		<sc2nav:menuGroup groupId="BW0600" title="${ManagementLabel}" ariaLabelledby="navbarDropdownMenuLink2" haveSubMenu="true">
	      			<ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink2">
	      				<sc2nav:menuSubGroup subGroupId="BW06100" title="${ManagementNewCarInsuranceLabel}">
	      					<ul class="dropdown-menu dropdown-submenu-newcar">
	      						<sc2nav:menuItem screenId="WBW06110" href="Management/NewCarInsurancePortfolioManagement" target="WBW06110">${ManagementNewCarInsurancePortfolioLabel}</sc2nav:menuItem>
	      						<sc2nav:menuItem screenId="WBW06120" href="Management/NewCarInsuranceActivationManagement" target="WBW06120">${ManagementNewCarInsuranceActivationLabel}</sc2nav:menuItem>
	      					</ul>
	      				</sc2nav:menuSubGroup>
	      				<sc2nav:menuSubGroup subGroupId="BW06200" title="${ManagementInsuranceRenewalLabel}">
	      					<ul class="dropdown-menu dropdown-submenu-renewal">
	      						<sc2nav:menuItem screenId="WBW06211" href="Management/StandardRenewalSOPPICSetting" target="WBW06211">${StandardRenewalSOPPICSettingLabel}</sc2nav:menuItem>
	      						<%-- <sc2nav:menuItem screenId="WBW06221" href="Management/SpecialContactSetting" target="WBW06221">${SpecialContactSettingLabel}</sc2nav:menuItem> --%>
	      						<sc2nav:menuSubGroup subGroupId="BW06220" title="${SpecialContactSettingLabel}">
	      							<ul class="dropdown-menu dropdown-submenu-special-contact">
	      								<sc2nav:menuItem screenId="WBW06221" href="Management/SpecialContactActivity" target="WBW06221">${SpecialContactActivityLabel}</sc2nav:menuItem>
	      								<sc2nav:menuItem screenId="WBW06222" href="Management/CreateNewSpecialContact" target="WBW06222">${CreateNewSpecialContactLabel}</sc2nav:menuItem>
	      							</ul>
	      						</sc2nav:menuSubGroup>
	      						<sc2nav:menuItem screenId="WBW06231" href="Management/ActivitiesReminder" target="WBW06231" style="dropdown-item badge2" dataBadge="119">${ActivitiesReminderLabel}</sc2nav:menuItem>
	      					</ul>
	      				</sc2nav:menuSubGroup>
	      				<sc2nav:menuSubGroup subGroupId="BW06300" title="${ManagementManpowerAccountLabel}">
	      					<ul class="dropdown-menu dropdown-submenu-manpower">
	      						<sc2nav:menuItem screenId="WBW06311" href="Management/InsuranceCustomerDatabasedManagement" target="WBW06311">${InsuranceCustomerDatabasedManagementLabel}</sc2nav:menuItem>
	      						<sc2nav:menuItem screenId="WBW06321" href="Management/AccountManagement" target="WBW06321">${AccountManagementLabel}</sc2nav:menuItem>
	      					</ul>
	      				</sc2nav:menuSubGroup>
	      				<sc2nav:menuItem screenId="WBW06410" href="Management/InsurancePremium" target="WBW06410">${ManagementInsurancePremiumLabel}</sc2nav:menuItem>
	      				<sc2nav:menuSubGroup subGroupId="BW06500" title="${ManagementReportLabel}">
	      					<ul class="dropdown-menu dropdown-submenu-report">
	      						<sc2nav:menuItem screenId="WBW06510" href="Management/ReportDataOfINSRenewal" target="WBW06510">${ExportOfINSRenewalLabel}</sc2nav:menuItem>
	      						<sc2nav:menuItem screenId="WBW06520" href="Management/ProcessResultKPIs" target="WBW06520">${ProcessResultKPIsLabel}</sc2nav:menuItem>
	      						<sc2nav:menuItem screenId="WBW06530" href="Management/BPReturningReport" target="WBW06530">${BPReturningReportLabel}</sc2nav:menuItem>
	      						<sc2nav:menuItem screenId="WBW06540" href="Management/FirstYearRedPlateINSReport" target="WBW06540">${FirstYearRedPlateINSReport}</sc2nav:menuItem>
	      					</ul>
	      				</sc2nav:menuSubGroup>
	      			</ul>
	      		</sc2nav:menuGroup>
	      		
	      		<sc2nav:menuGroup groupId="ST3002" title="${groupCommonLabel}" ariaLabelledby="navbarDropdown" haveSubMenu="true">
					<ul class="dropdown-menu dropdown-submenu-common" aria-labelledby="navbarDropdown">
	        			<sc2nav:menuItem screenId="WST33010" href="common/logMonitoring" target="WST33010">${itemLogMonitoringLabel}</sc2nav:menuItem>
	        			<sc2nav:menuItem screenId="WST30900" href="common/excelDownloadMonitoring" target="WBW03210">${itemExcelDownloadMonitoringLabel}</sc2nav:menuItem>
	        		</ul>
        		</sc2nav:menuGroup>
	    	</ul>
	  	</div>
	  	
	  	
	</nav>
   
	<%-- 
	<nav class="top-menu navbar navbar-expand-md navbar-light p-0">
	  	
	  	<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTogglerDemo01" aria-controls="navbarTogglerDemo01" aria-expanded="false" aria-label="Toggle navigation">
	    	<span class="navbar-toggler-icon"></span>
	  	</button>
	  	<a class="navbar-brand" href="${pageContext.request.contextPath}">TIM</a>
	  	<div class="collapse navbar-collapse" id="navbarTogglerDemo01">
	    	<ul class="navbar-nav mr-auto">
	      		<li id="homeMenu" class="nav-item">
          			<a class="nav-link" href="${pageContext.request.contextPath}"><span class="pl-4">Home</span></a>
        		</li>
	      		<li id="BW0300" class="nav-item dropdown">
          			<a class="nav-link" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            			<span class="pl-4">New Car Insurance</span>
          			</a>
          			<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            			<a id="WBW02110" class="dropdown-item" href="${pageContext.request.contextPath}/NewCarInsurance/ActivateRedPlantScreen">Activate New Car Insurance (Red Plant)</a>
            			<a id="WBW02120" class="dropdown-item" href="${pageContext.request.contextPath}/NewCarInsurance/Report">Report & Data Export</a>
          			</ul>
        		</li>
        		<li id="BW0400" class="nav-item dropdown">
          			<a class="nav-link" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            			<span class="pl-4">&nbsp;Insurance Renewal</span>
          			</a>
          			<ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            			<a d="WBW03110" class="dropdown-item" href="${pageContext.request.contextPath}/InsuranceRenewal/Telemarketing">Insurance Renewal</a>
            			<a d="WBW03210" class="dropdown-item" href="${pageContext.request.contextPath}/Kaikieng/kaikiengInput">Add New Customer Profile</a>
          			</ul>
        		</li>
        		<li id="BW0500" class="nav-item">
          			<a id="BW05110" class="nav-link" href="${pageContext.request.contextPath}/InsuranceCompany"><span class="pl-4">&nbsp;Insurance Company</span></a>
        		</li>
	   			<li class="nav-item dropdown">
	        		<a class="nav-link" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
			          	<span class="pl-4">&nbsp;Management</span>
			        </a>
			        <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
			        	<li class="dropdown-submenu"><a class="dropdown-item dropdown-toggle" href="#">New Car Insurance</a>
			            	<ul class="dropdown-menu dropdown-submenu-newcar">
			              		<li><a class="dropdown-item" href="${pageContext.request.contextPath}/Management/NewCarInsurancePortfolioManagement">New Car Insurance Portfolio Management</a></li>
			              		<li><a class="dropdown-item" href="${pageContext.request.contextPath}/Management/NewCarInsuranceActivationManagement">New Car Insurance Activation Management</a></li>
			            	</ul>
			          	</li>
			          	<li class="dropdown-submenu"><a class="dropdown-item dropdown-toggle" href="#">Insurance Renewal Management</a>
			            	<ul class="dropdown-menu dropdown-submenu-renewal">
			              		<li><a class="dropdown-item" href="${pageContext.request.contextPath}/Management/StandardRenewalSOPPICSetting">Standard Renewal SOP & PIC Setting</a></li>
			              		<li><a class="dropdown-item" href="${pageContext.request.contextPath}/Management/SpecialContactSetting">Special Contact Setting</a>
			              		
			              		
			              		</li>
			              		
			              		<li class="dropdown-submenu"><a class="dropdown-item dropdown-toggle" href="#">Special Contact Setting</a>
					            	<ul class="dropdown-menu dropdown-submenu-special-contact">
					              		<li><a class="dropdown-item" href="${pageContext.request.contextPath}/Management/SpecialContactActivitivity">Special Contact Activitivity Management</a></li>
					              		<li><a class="dropdown-item" href="${pageContext.request.contextPath}/Management/CreateNewSpecialContact">Create New Special Contact</a></li>
					            	</ul>
					          	</li>
			              		
			              		
			              		<li><a class="dropdown-item badge2" data-badge="119" href="${pageContext.request.contextPath}/Management/ActivitiesReminder">Activities Reminder</a></li>
			            	</ul>
			          	</li>
            			<li><a class="dropdown-item" href="${pageContext.request.contextPath}/ManpowerAccount">Manpower Account Management</a></li>
            			<li><a class="dropdown-item" href="${pageContext.request.contextPath}/InsurancePremium">Insurance Premium Management</a></li>
			        				          	
           				<li><a class="dropdown-item" href="${pageContext.request.contextPath}/Report">Report</a></li>
	        		</ul>
	      		</li>
	      		
	      		<li id="ST3002" class="nav-item dropdown">
          			<a class="nav-link" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            			<span class="pl-4">&nbsp;Common</span>
          			</a>
          			<ul class="dropdown-menu dropdown-submenu-common" aria-labelledby="navbarDropdown">
            			<a id="WST33010" class="dropdown-item" href="${pageContext.request.contextPath}/common/logMonitoring">${itemLogMonitoring}</a>
            			<a id="WBW03210" class="dropdown-item" href="${pageContext.request.contextPath}/common/excelDownloadMonitoring">${itemExcelDownloadMonitoring}</a>
          			</ul>
        		</li>
	    	</ul>
	  	</div>
	</nav> --%>

	
</div>