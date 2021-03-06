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
	<form action="controller" method="POST">
		Search Query: <input type="text" name="query" size="50" value="${query}" />
		<input type="submit" name="action" value="Search" />
		<input type="submit" name="action" value="Index" />
		<br>
		Type &nbsp&nbsp&nbsp   <select name="queryType">
		<option value="default" selected="selected">None</option>
		<option value="image">Image</option>
		<option value="author">Author</option>
		<option value="hashtag">#hashtag</option>
		</select>
		&nbsp&nbsp Country &nbsp&nbsp   <select name="queryCountry">
		<option value="default" selected="selected">None</option>
		<option value="United States">United States</option>
		<option value="United Kingdom">United Kingdom</option>
		<option value="Ireland">Ireland</option>
		<option value="Deutschland">Germany</option>
		<option value="France">France</option>
		<option value="Italia">Italy</option>
		<option value="Schweiz">Switzerland</option>
		</select>
		&nbsp&nbsp Sort by &nbsp&nbsp   <select name="querySort">
		<option value="default" selected="selected">None</option>
		<option value="date">Newest</option>
		<option value="popularity">Popular</option>
		</select>
	</form>
</div>	
	<jsp:include page="${queryDisplayTarget}" ></jsp:include>

</body>
</html>
