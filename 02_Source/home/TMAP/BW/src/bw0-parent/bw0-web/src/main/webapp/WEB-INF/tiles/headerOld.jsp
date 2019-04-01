<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="header-mobile">
	<div id="logo-panel-mobile">
		<span class="logo toggle-menu"><img
			src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/toyota-transparent.png" /></span>
	</div>
	<div class="application-panel">
		<span class="application"><spring:message code="ST3.lbl.Application"/></span>
		<span class="userinfo"><c:out value="${sessionScope.tscuserinfo.firstName}" /></span>
	</div>
	<div id="grayline-logo-panel-mobile" class="toggle-menu"
		style="height: 16px">
		<span class="screenid"><c:out value="${payload.screenId}: ${payload.screenDescription}" /></span> 
		<span class="time"><c:out value="${payload.dateTimeNow}" /></span>
	</div>
</div>
<div id="header">
	<div id="logo-panel">
		<span id="logo" class="toggle-menu"><img
			src="${pageContext.request.contextPath}<spring:message code="imagepath"/>images/toyota-transparent.png" /></span>
		<span id="grayline-logo-panel" class="toggle-menu"> <input tabindex="-1"
			type="image"
			src="${pageContext.request.contextPath}<spring:message code="imagepath"/>icons/left.gif"
			id="toggle-menu-left" width="16" /> <input type="image" tabindex="-1"
			src="${pageContext.request.contextPath}<spring:message code="imagepath"/>icons/right.gif"
			id="toggle-menu-right" width="16" />
		</span>
	</div>
</div>
<div id="right-area">
	<div class="page-header">
		<div class="application-panel">
			<span class="application"><spring:message code="BW0.lbl.Application"/></span> <span
				class="userinfo"><c:out value="${sessionScope.tscuserinfo.firstName}" /></span>
		</div>
		<div class="screenid-panel">
			<span class="screenid">
			<c:out value="${payload.screenId} : ${payload.screenDescription}" /></span> 
			<span class="time"><c:out value="${payload.dateTimeNow}" /></span>
			<span class="timeSec hide"><c:out value="${payload.dateTimeNowSec}" /></span>
		</div>
	</div>
</div>
