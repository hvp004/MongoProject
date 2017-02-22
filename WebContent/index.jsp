<%@page import="controller.PathBuilder"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	if(session.getAttribute("page_load") == null)
	{
		String path="";
		if(request.getParameter("flag") == null)
		{
			session.setAttribute("page_load", true);
		}
		else
		{
			path = "flag=search";
			path = PathBuilder.buildPath(request, path);
			System.out.println("PATH: "+path);
			response.sendRedirect(request.getContextPath()+"/SearchPath?"+path);
		}
	}
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Application</title>
<!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    <script
            src="https://code.jquery.com/jquery-3.1.1.slim.min.js"
            integrity="sha256-/SIrNqv8h6QGKDuNoLGA4iret+kyesCkHGzVUUV0shc="
            crossorigin="anonymous"></script>

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>

</head>
<body>
<div class="container">
<div class="row">
<div class="col-xs-12"> 
<h3><a href = "index.jsp" class="btn btn-success">Home</a></h3></div>
<div class="col-xs-8 col-xs-offset-2">
<form action = "<%=request.getContextPath()%>/SearchPath" method = "get">
<p>Enter Search Term: </p>
<c:choose>
<c:when test="${ not empty param.search}">
<input type = "text" name = "search" value="${param.search }" required>
</c:when>
<c:otherwise>
<input type = "text" name = "search"  required>
</c:otherwise>
</c:choose>

<input type = "hidden" name ="flag" value = "search">
<input type = "submit" value = "Search" class="btn btn-primary">
</form>
</div></div></div>
<div class="container">
<c:choose>
<c:when test="${ not empty param.search}">
<h1><small>You searched for ${param.search }</small></h1>
<c:choose>
<c:when test="${empty param.limit }">
<h3>Results: ${fn:length(list_actor)}</h3><br>
</c:when>
<c:otherwise>
<h3>Results: ${param.limit}</h3><br>
</c:otherwise>
</c:choose>
</c:when>
</c:choose>
 
<c:choose>
<c:when test="${not empty sessionScope.list_actor}">
<p>Sort By:</p>

<form action="<%=request.getContextPath()%>/SearchPath" method="get">
<input type="hidden" name="flag" value="search">
<input type="hidden" name="search" value="${param.search }" >
<c:choose><c:when test="${not empty param.limit}">
	<input type="hidden" name="limit" value="${param.limit}">
</c:when></c:choose>
<input type="submit" name="filter" value="relevance" class="btn btn-primary">
<input type="submit" name="filter" value="alphabatically" class="btn btn-primary">
<input type="submit" name="filter" value="age" class="btn btn-primary">
<input type="submit" name="filter" value="comments" class="btn btn-primary">
</form>
<c:choose>
<c:when test="${(not empty param.filter) and (param.filter ne 'relevance') }">
<form action="<%=request.getContextPath()%>/SearchPath" method="get">
<c:choose>
<c:when test="${not empty param.limit}">
	<input type="hidden" name="limit" value="${param.limit}">
</c:when>
</c:choose>
<input type="hidden" name="filter" value=<%=request.getParameter("filter") %>>
<input type="hidden" name="search" value="${param.search }" >
<input type="hidden" name="flag" value="search">
<c:choose>
<c:when test="${empty param.reverse }">
<input type="submit" name="reverse" value="Reverse" class="btn btn-danger">
</c:when>
</c:choose>
</form>
</c:when>
</c:choose>
<c:choose>
<c:when test="${fn:length(sessionScope.list_actor) gt 20}">
<form action="<%=request.getContextPath()%>/SearchPath" method="get">
<input type="hidden" name="flag" value="search">
<input type="hidden" name="search" value="${param.search}" >
<c:choose>
<c:when test="${not empty param.filter }">
<input type="hidden" name="filter" value="${param.filter}" >
</c:when>
<c:when test="${not empty param.reverse }">
<input type="hidden" name="reverse" value="${param.reverse}" >
</c:when>
</c:choose>
<input type="hidden" name="search" value="${param.filter}" >
	<select onchange="this.form.submit()" name="limit">
	<option disabled selected>Select Results</option>
	<c:forEach begin="20" end="${fn:length(sessionScope.list_actor)}" step="20" var="loop">
	<c:choose>
	<c:when test="${loop eq param.limit }">
		<option value="${loop}" selected> ${loop} </option>
	</c:when>
	<c:otherwise>
		<option value="${loop}"> ${loop} </option>
	</c:otherwise>
	</c:choose>
	</c:forEach>
	<c:choose>
	<c:when test="${fn:length(sessionScope.list_actor) eq limit}">
	<option value="${fn:length(sessionScope.list_actor)}" selected>All</option>
	</c:when>
	<c:otherwise>
	<option value="${fn:length(sessionScope.list_actor)}">All</option>
	</c:otherwise>
	</c:choose>
	</select>
</form>
</c:when>
</c:choose>


<c:choose>
<c:when test="${empty param.limit }">
	<c:forEach items="${sessionScope.list_actor}" var="x" >
	<div class="item  col-xs-4 col-lg-4">
    <div class="thumbnail">
	<img class="group list-group-image" src = "${x.image_url}"/>
	<div class="caption">
	<h4 class="group inner list-group-item-heading">${x.actor} (Age: ${x.age})</h4>
	<p class="group inner list-group-item-text">${x.biography.substring(0, 80)}...</p>
	<div class="row">
    <div class="col-xs-12 col-md-6">
	<p class="lead">Comments: ${fn:length(x.list_comment)} </p>
	</div>
	<div class="col-xs-12 col-md-6">
	<a class="btn btn-success" href="actor.jsp?flag=byActor&id=${x.id}">View Full Profile</a>
	</div>
	</div>
	</div>
	</div>
	</div>
	</c:forEach>
</c:when>
<c:otherwise>
	<c:forEach begin="0" end="${param.limit}" items="${sessionScope.list_actor}" var="x" >
	<div class="item  col-xs-4 col-lg-4">
    <div class="thumbnail">
	<img class="group list-group-image" src = "${x.image_url}"/>
	<div class="caption">
	<h4 class="group inner list-group-item-heading">${x.actor} (Age: ${x.age})</h4>
	<p class="group inner list-group-item-text">${x.biography.substring(0, 80)}...</p>
	<div class="row">
    <div class="col-xs-12 col-md-6">
	<p class="lead">Comments: ${fn:length(x.list_comment)} </p>
	</div>
	<div class="col-xs-12 col-md-6">
	<a class="btn btn-success" href="actor.jsp?flag=byActor&id=${x.id}">View Full Profile</a>
	</div>
	</div>
	</div>
	</div>
	</div>
	</c:forEach>
</c:otherwise>
</c:choose>

<%session.setAttribute("list_actor", null); %>  
</c:when>
<c:when test="${not empty param.flag }">
<p>Your search matched no result!! Try searching again.</p>    
</c:when>
</c:choose>
</div>
<%session.setAttribute("page_load", null); %>
</body>
</html>