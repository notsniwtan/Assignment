<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>

#search{
			position: absolute;
		    top: 35px;
		    bottom: 100px;
		    left: 425px;
		    right: 0;
		    width: 1000px;
		    height: 10px;
		}
		
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IRWeb Search Results</title>
</head>
<body>
<div id="search">
	<form action="processquery" method="POST">
		Search Query: <input type="text" name="query" size="50" value="${query}" />
		<input type="submit" value="Search" />
	</form>
</div>	
	<jsp:include page="resultSection.jsp" />

</body>
</html>