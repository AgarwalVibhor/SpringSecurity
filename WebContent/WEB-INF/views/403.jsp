<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Error Page</title>
</head>
<body>
	
	<h1>HTTP Status : 403 - Access Denied !!</h1>
	
	<br />
	
	<c:choose>
		<c:when test="${empty username}">
			<h2>You do not have permission to access this page.</h2>
		</c:when>
		<c:otherwise>
			<h2>User Name : ${username} ! You do not have permission to access this page.</h2>
		</c:otherwise>
	</c:choose>
	
	<c:url value="/j_spring_security_logout" var="logoutUrl" />
	
	<form id="logoutForm" action="${logoutUrl}" method="POST">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	</form>
	
	<script type="text/javascript">
		function formSubmit()
		{
			document.getElementById("logoutForm").submit();
		}
	</script>
	
	<br />
	<h3>Click <a href="javascript:formSubmit()">Here</a> to logout.</h3>
</body>
</html>