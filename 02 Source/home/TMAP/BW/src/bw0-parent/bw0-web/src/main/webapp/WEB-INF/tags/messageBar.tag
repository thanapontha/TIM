<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<div id="messageBar" class="overflow-hidden toggle messageBar" <c:if test="${sessionScope.tscmessages.haveMessages || payload.haveMessages}">style="display:block;"</c:if> >
	<div class="toggle-icon ui-icon ui-icon-circle-triangle-s"></div>
	<c:if test="${sessionScope.tscmessages.haveMessages}">
		<c:forEach var="msg" items="${sessionScope.tscmessages.errorMessages}">
			<div class="message err">
				<c:out value="${msg}" />
			</div>
		</c:forEach>
	</c:if>  
	<c:if test="${payload.haveMessages}">
		<c:forEach var="msg" items="${payload.errorMessages}">
			<div class="message err">
				<c:out value="${msg}" />
			</div>
		</c:forEach>
	</c:if>
	<c:if test="${sessionScope.tscmessages.haveMessages}">
		<c:forEach var="msg" items="${sessionScope.tscmessages.warningMessages}">
			<div class="message warn">
				<c:out value="${msg}" />
			</div>
		</c:forEach>
	</c:if>
	<c:if test="${payload.haveMessages}">
		<c:forEach var="msg" items="${payload.warningMessages}">
			<div class="message warn">
				<c:out value="${msg}" />
			</div>
		</c:forEach>
	</c:if>
	<c:if test="${sessionScope.tscmessages.haveMessages}">
		<c:forEach var="msg" items="${sessionScope.tscmessages.infoMessages}">
			<div class="message inf">
				<c:out value="${msg}" />
			</div>
		</c:forEach>
	</c:if>
	<c:if test="${payload.haveMessages}">
		<c:forEach var="msg" items="${payload.infoMessages}">
			<div class="message inf">
				<c:out value="${msg}" />
			</div>
		</c:forEach>
	</c:if>
	
	<c:if test="${sessionScope.tscmessages != null}">
		<c:remove var="tscmessages" scope="session" />
	</c:if>
</div>
