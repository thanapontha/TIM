<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sc2" uri="/WEB-INF/tld/sc2.tld"%>

<div id="left-menu-panel">

	<!-- Declare variables for EL use -->
	<spring:message code="imagepath" var="rootimagepath" />
	<spring:message code="ST3.menu.group.master" var="groupMaster" />
	<spring:message code="ST3.menu.master.item.system" var="itemSystemMaster" />
	<spring:message code="ST3.menu.master.item.batch" var="itemBatchMaster" />
	<spring:message code="ST3.menu.group.common" var="groupCommon" />
	<spring:message code="ST3.menu.sub.group.monitoring" var="subGroupMonitoring" />
	<spring:message code="ST3.menu.common.item.log" var="itemLogMonitoring" />
	<spring:message code="ST3.menu.common.item.excelDownloadMonitoring" var="itemExcelDownloadMonitoring"/>
	<spring:message code="ST3.menu.common.item.batchstatus" var="itemBatchStatusMonitoring" />
	<spring:message code="ST3.menu.sub.group.download" var="subGroupDownload" />
	<spring:message code="ST3.menu.common.item.excelDownload" var="itemExcelDownload" />
	<spring:message code="ST3.menu.group.help" var="groupHelp" />
	<spring:message code="ST3.menu.help.item.applicationAbout" var="itemApplicationAbout" />

	<spring:message code="BW0.menu.group.Kaikieng" var="groupKaikieng" />
	<spring:message code="BW0.menu.group.RundownandUnit" var="groupRundownandUnit" />
	<spring:message code="BW0.menu.sub.group.common.gwrds" var="subGroupGWRDSDownload" />
	<spring:message code="BW0.menu.sub.group.KaikiengMaster" var="subGroupKaikiengMaster" />
	<spring:message code="BW0.menu.sub.group.RundownandUnitMaster" var="subGroupRundownandUnitMaster" />

	<spring:message code="BW0.menu.Kaikieng.item.KaikiengInputScreen" var="itemKaikiengInput" />
	<spring:message code="BW0.menu.Kaikieng.item.GetsudoWorksheetManagementScreen" var="itemGetsudoWorksheetManagement" />
	<spring:message code="BW0.menu.RundownandUnit.item.RundownAndKompoManagementScreen" var="itemRundownAndKompoManagement" />
	<spring:message code="BW0.menu.RundownandUnit.item.UnitCapacityManagementScreen" var="itemUnitCapacityManagement" />
	<spring:message code="BW0.menu.RundownandUnit.item.CommonReportGenerationScreen" var="itemCommonReportGeneration" />
	<spring:message code="BW0.menu.KaikiengMaster.item.VehiclePlant" var="itemVehiclePlant" />
	<spring:message code="BW0.menu.KaikiengMaster.item.UnitPlant" var="itemUnitPlant" />
	<spring:message code="BW0.menu.KaikiengMaster.item.VehicleUnitRelation" var="itemVehicleUnitRelation" />
	<spring:message code="BW0.menu.RundownandUnitMaster.item.Calendar" var="itemCalendar" />
	<spring:message code="BW0.menu.RundownandUnitMaster.item.StandardStock" var="itemStandardStock" />
	<spring:message code="BW0.menu.RundownandUnitMaster.item.UnitPlantCapacity" var="itemUnitPlantCapacity" />
	
	<!-- Create the Menu -->
	<sc2:menu idContainer="menu-container" idMenu="menu"
		onClick="ST3Lib.menu.toggleLock()?[$('> #unpin' ,this).hide(),$('> #pin' ,this).show() ]:[$('> #pin' ,this).hide(),$('> #unpin' ,this).show() ];"
		srcImgUnpin="${pageContext.request.contextPath}${rootimagepath}icons/unpin.png"
		srcImgPin="${pageContext.request.contextPath}${rootimagepath}icons/pin.png">
		
		<!-- Application module -->
		<sc2:menuGroup groupId="BW0100" title="${groupKaikieng}">
			<!-- Define Menu Item -->
			<sc2:menuItem screenId="WBW01110" href="${pageContext.request.contextPath}/Kaikieng/kaikiengInput" target="WBW01110">${itemKaikiengInput}</sc2:menuItem>
			<sc2:menuItem screenId="WBW01150" href="${pageContext.request.contextPath}/Kaikieng/getsudoWorksheetManagement" target="WBW01150">${itemGetsudoWorksheetManagement}</sc2:menuItem>
		</sc2:menuGroup>
		
		<sc2:menuGroup groupId="BW0200" title="${groupRundownandUnit}">
			<!-- Define Menu Item -->
			<sc2:menuItem screenId="WBW02110" href="${pageContext.request.contextPath}/RundownandUnit/rundownAndKompoManagement" target="WBW02110">${itemRundownAndKompoManagement}</sc2:menuItem>
			<sc2:menuItem screenId="WBW02160" href="${pageContext.request.contextPath}/RundownandUnit/unitCapacityManagement" target="WBW02160">${itemUnitCapacityManagement}</sc2:menuItem>
		</sc2:menuGroup>

		<!-- Define the Group Menu -->
		<sc2:menuGroup groupId="ST3001" title="${groupMaster}">
			<!-- Define Menu Item -->
			<sc2:menuItem screenId="WST33060" href="${pageContext.request.contextPath}/master/systemMaster" target="WST33060">${itemSystemMaster}</sc2:menuItem>
			<!--
			<sc2:menuItem screenId="WST33040" href="${pageContext.request.contextPath}/master/batchMaster" target="WST33040">${itemBatchMaster}</sc2:menuItem>
			-->
			<sc2:menuSubGroup subGroupId="ST30011" title="${subGroupKaikiengMaster}">
				<sc2:menuItem screenId="WBW04110" href="${pageContext.request.contextPath}/master/vehiclePlant" target="WBW04110">${itemVehiclePlant}</sc2:menuItem>
				<sc2:menuItem screenId="WBW04120" href="${pageContext.request.contextPath}/master/unitPlant" target="WBW04120">${itemUnitPlant}</sc2:menuItem>
				<sc2:menuItem screenId="WBW04130" href="${pageContext.request.contextPath}/master/vehicleUnitRelation" target="WBW04130">${itemVehicleUnitRelation}</sc2:menuItem>
			</sc2:menuSubGroup>
			<sc2:menuSubGroup subGroupId="ST30012" title="${subGroupRundownandUnitMaster}">
				<sc2:menuItem screenId="WBW04210" href="${pageContext.request.contextPath}/master/calendar" target="WBW04210">${itemCalendar}</sc2:menuItem>
				<sc2:menuItem screenId="WBW04230" href="${pageContext.request.contextPath}/master/standardStock" target="WBW04230">${itemStandardStock}</sc2:menuItem>
				<sc2:menuItem screenId="WBW04240" href="${pageContext.request.contextPath}/master/unitPlantCapacity" target="WBW04240">${itemUnitPlantCapacity}</sc2:menuItem>
			</sc2:menuSubGroup>
		</sc2:menuGroup>
		
		<sc2:menuGroup groupId="ST3002" title="${groupCommon}">
			<sc2:menuSubGroup subGroupId="ST30021" title="${subGroupMonitoring}">
				<sc2:menuItem screenId="WST33010" href="${pageContext.request.contextPath}/common/logMonitoring" target="WST33010">${itemLogMonitoring}</sc2:menuItem>
				<!--
				<sc2:menuItem screenId="WST33020" href="${pageContext.request.contextPath}/common/excelDownloadMonitoring" target="WST33020">${itemExcelDownloadMonitoring}</sc2:menuItem>
				<sc2:menuItem screenId="WST33050" href="${pageContext.request.contextPath}/common/batchStatus" target="WST33050">${itemBatchStatusMonitoring}</sc2:menuItem> 
				-->
			</sc2:menuSubGroup>
			
			<!--
 			<sc2:menuSubGroup subGroupId="ST30022" title="${subGroupDownload}">
				<sc2:menuItem screenId="WST30900" href="${pageContext.request.contextPath}/common/excelDownload" target="WST30900">${itemExcelDownload}</sc2:menuItem>
			</sc2:menuSubGroup>
			-->
			
			<sc2:menuSubGroup subGroupId="ST30023" title="${subGroupGWRDSDownload}">
				<sc2:menuItem screenId="WBW03210" href="${pageContext.request.contextPath}/common/gwrdsCommonDownload" target="WBW03210">${itemCommonReportGeneration}</sc2:menuItem>
			</sc2:menuSubGroup>
			
		</sc2:menuGroup>
		<!--
		<sc2:menuGroup groupId="ST3003" title="${groupHelp}">
			<sc2:menuItem screenId="WST30300" href="${pageContext.request.contextPath}/common/about" target="WST30300">${itemApplicationAbout}</sc2:menuItem>
			<sc2:menuItem screenId="WST30070" href="${pageContext.request.contextPath}/common/test" target="WST3007Test">API Test</sc2:menuItem>  
		</sc2:menuGroup>
		-->
	</sc2:menu>
</div>
