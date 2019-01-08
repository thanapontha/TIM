<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="src" type="java.lang.String" %>
<%@ attribute name="width" type="java.lang.Integer" %>
<%@ attribute name="height" type="java.lang.Integer" %>
<%@ attribute name="id" type="java.lang.String" %>
<img src="${pageContext.request.contextPath}<spring:message code="imagepath"/><%=src %>" 
	<c:if test="${not empty width }">width="<%=width %>"</c:if>
	<c:if test="${not empty height }">height="<%=height %>"</c:if>
	<c:if test="${not empty id }">id="<%=id %>"</c:if>
/>