<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="src" type="java.lang.String" %>

	<script src="${pageContext.request.contextPath}<spring:message code="scriptpath"/><%=src %>" ></script>