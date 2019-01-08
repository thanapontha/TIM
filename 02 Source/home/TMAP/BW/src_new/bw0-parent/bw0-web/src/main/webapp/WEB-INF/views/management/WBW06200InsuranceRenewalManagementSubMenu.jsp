<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sc2nav" uri="/WEB-INF/tld/sc2nav.tld"%>
<style>	
	.badge1 {
		position:relative;
	}
	
	.badge1[data-badge]:after {
		content:attr(data-badge);
		position:absolute;
		font-family: arial, sans-serif;
		font-weight: bold;
		font-size:.7em;
		right: -10px;
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
		
	}
	/* 
	 .badge {
		background: #67c1ef;
		border-color: #30aae9;
		background-image: -webkit-linear-gradient(top, #acddf6, #67c1ef);
		background-image: -moz-linear-gradient(top, #acddf6, #67c1ef);
		background-image: -o-linear-gradient(top, #acddf6, #67c1ef);
		background-image: linear-gradient(to bottom, #acddf6, #67c1ef);
	}
	
	 .badge.green {
		background: #77cc51;
		border-color: #59ad33;
		background-image: -webkit-linear-gradient(top, #a5dd8c, #77cc51);
		background-image: -moz-linear-gradient(top, #a5dd8c, #77cc51);
		background-image: -o-linear-gradient(top, #a5dd8c, #77cc51);
		background-image: linear-gradient(to bottom, #a5dd8c, #77cc51);
	}
	
	 .badge.yellow {
	 	color: white;
		background: #faba3e;
		border-color: #f4a306;
		background-image: -webkit-linear-gradient(top, #fcd589, #faba3e);
		background-image: -moz-linear-gradient(top, #fcd589, #faba3e);
		background-image: -o-linear-gradient(top, #fcd589, #faba3e);
		background-image: linear-gradient(to bottom, #fcd589, #faba3e);
	}
	
	 .badge.red {
	 	color: white;
		background: #fa623f;
		border-color: #fa5a35;
		background-image: -webkit-linear-gradient(top, #fc9f8a, #fa623f);
		background-image: -moz-linear-gradient(top, #fc9f8a, #fa623f);
		background-image: -o-linear-gradient(top, #fc9f8a, #fa623f);
		background-image: linear-gradient(to bottom, #fc9f8a, #fa623f);
	}
 */
	
</style>

<script type="text/javascript">
</script>

<spring:message code="imagepath" var="imagepath" />
<spring:message code="BW0.menu.Management.InsuranceRenewal.SOP.PICSetting" var="StandardRenewalSOPPICSettingLabel" />
<spring:message code="BW0.menu.sub.group.Management.InsuranceRenewal.SpecialContactSetting" var="SpecialContactSettingLabel" />
<spring:message code="BW0.menu.Management.InsuranceRenewal.ActivitiesReminder" var="ActivitiesReminderLabel" />

<div class="container-fluid">
	<div class="row"> 
	 	<div class="col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12">
	 		<div class="form-row center">
	 			<sc2nav:menuImage type="screenId" screenId="WBW06211" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/StandardRenewalSOPPICSetting" title="${StandardRenewalSOPPICSettingLabel}" src="${imagepath}images/tim/Standard_Renewal_SOP_PIC_Setting.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="screenId" screenId="WBW06221" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/SpecialContactSettingSubMenu" title="${SpecialContactSettingLabel}" src="${imagepath}images/tim/Special_Contact_Setting.png">
	 			</sc2nav:menuImage>
	 			<sc2nav:menuImage type="screenId" screenId="WBW06231" styleColDiv="pt-3 pb-1 col-xl-6 col-lg-6 col-md-6 col-sm-6 col-6" style="my-1 textMenuImage"
	 				href="Management/ActivitiesReminder" title="${ActivitiesReminderLabel}" dataBadge="199" badgeStyle="badge1" 
	 				src="${imagepath}images/tim/Activities_Reminder.png">
	 			</sc2nav:menuImage>
            </div>
		</div>
	</div>
</div>