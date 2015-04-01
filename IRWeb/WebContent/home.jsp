<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<style>

#search{
    display: inline-block;
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    width: 400px;
    height: 100px;
    margin: auto;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IRWeb Main Page</title>
</head>
<body>
<div id="search">
	<form action="processquery" method="POST">
		Search Query: <input type="text" name="query">
	<input type="submit" value="Search" />
</form>
</div>
</body>
</html>