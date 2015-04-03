<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>

	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<title></title>

		<style>
	        
		#searchResults{
			position: absolute;
		    top: 100px;
		    bottom: 0;
		    left: 200px;
		    right: 0;
		    width: 1000px;
		    height: 100px;
		}
		
		#bottom{
			border-bottom: thin solid;
		}
  		#a{background-color: white;
  			color:blue;
  			width: 5px;
  			text-align: left;
  			display: initial;}
  		
        a:link, a:visited {
        	display: inline-block;
        	font-weight: bold;
        	color: white;
        	background-color: darkgray;
        	width: 20px;
        	text-align: center;
        	padding: 1px;
        	text-decoration: none;
        }

        a:hover {
        	background-color: gray;
        }

        a.active {
        	background-color: black;
        }
    </style>

    <script type="text/javascript" src="jquery-1.11.2.min.js"></script>
    <script>
        $(document).ready(function(){
            $('#data').after('<div id="nav"></div>');
            var rowsShown = 40;
            var rowsTotal = $('#data tbody tr').length;
            var numPages = Math.round(rowsTotal/rowsShown);
            for(i = 0;i < numPages;i++) {
                var pageNum = i + 1;
                $('#nav').append('<a href="#" rel="'+i+'">'+pageNum+'</a> ');
            }
            $('#data tbody tr').hide();
            $('#data tbody tr').slice(0, rowsShown).show();
            $('#nav a:first').addClass('active');
            $('#nav a').bind('click', function(){

                $('#nav a').removeClass('active');
                $(this).addClass('active');
                var currPage = $(this).attr('rel');
                var startItem = currPage * rowsShown;
                var endItem = startItem + rowsShown;
                $('#data tbody tr').css('opacity','0.0').hide().slice(startItem, endItem).
                        css('display','table-row').animate({opacity:1}, 300);
            });
        });
    </script>

</head>

<body>
<div id="searchResults">
	<table id="data" cellspacing="10" cellpadding="1">
	<tr><td colspan="2" id="bottom">${numHits} results found (${timeTaken/1000} seconds)</td></tr>
		<c:choose>
			<c:when test="${listEmpty == true}">
				No search results were found!
			</c:when>	
		<c:otherwise>
			<c:forEach items="${resultList}" var="document">
				<tr>
					<td width="1%" rowspan="2"><img width="43px" src="${document.authorProfilePic}"></td>	
					<td align="left"><span style="font-weight:bold">${document.author}</span>
					&nbsp &nbsp <img src="http://i27.photobucket.com/albums/c164/lene321/${document.country}.jpg"> &nbsp &nbsp ${document.country}
					</td>					
				</tr>
				<tr>
					<td><span style="font-size:small">Tweeted at: ${document.date.date}/${document.date.month}/${document.date.year+1900}  
					${document.date.hours}:${document.date.minutes}:${document.date.seconds}
					</span></td>
				</tr>
				<tr>
					<td colspan="2"><span style="font-size:x-large; font-weight:bold">${document.content}</span></td>
				</tr>
				<tr>
					<td id="bottom" colspan="2"><a id="a" href="${document.url}">${document.url}</a></td>
				</tr>
			</c:forEach>
		</c:otherwise>
		</c:choose>
	</table>
	</div>
</body>

</html>