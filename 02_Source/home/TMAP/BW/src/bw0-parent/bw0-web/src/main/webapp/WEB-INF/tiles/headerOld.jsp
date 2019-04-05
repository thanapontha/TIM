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
        <li>
        	<span class="px-2 time">&nbsp;&nbsp;<c:out value="${payload.dateTimeNow}" /></span>
        </li>
      </ul>
    </div>
  </div>
</div>

