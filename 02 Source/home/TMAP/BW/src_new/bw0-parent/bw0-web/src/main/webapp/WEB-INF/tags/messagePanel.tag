<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- Placeholder for messages -->
<c:forEach var="displaymessage" items="${xmlPayload.errorMessages}">
	<c:out value="${displaymessage}"/>
</c:forEach>
