<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IRWeb Search Results</title>
</head>
<body>
	<form action="processquery" method="POST">
		Search Query: <input type="text" name="query" value="${query}" />
		<input type="submit" value="Search" />
	</form>
	
	<jsp:include page="resultSection.jsp" />
</body>
</html>