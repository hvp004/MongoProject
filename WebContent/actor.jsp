<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	if(session.getAttribute("page_load") == null)
	{
		String id = request.getParameter("id");
		System.out.println("FROM PAGE: "+id);
		response.sendRedirect(request.getContextPath()+"/SearchPath?flag=byActor&id="+id);
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${actor.actor}</title>
<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<!-- Optional theme -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
	<link rel="stylesheet" href="./style/main.css">
</head>
<body>
<%-- <a href = "<%=request.getContextPath()%>">Home</a>
 --%>
<div class="col-xs-8 col-xs-offset-2">
<form action = "<%=request.getContextPath()%>/SearchPath" method = "get">
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
</div>
<div class="container">
<div class="row">
<div class="col-xs-3">
<img src = "${actor.image_url}"/>
</div>
<div class="col-xs-9">
<div class="row">
<div class="col-xs-8"><h2><a href="actor.jsp?flag=byActor&id=${actor.id}">${actor.actor} (${actor.age} years)</a></h2></div>
<div class="col-xs-4 v-offset">
<a class="btn-info btn" href="<%=request.getContextPath()%>"><i class="glyphicon glyphicon-circle-arrow-left" aria-hidden="true"></i>Return to Search Page</a>
</div>
</div>
</div>
<%-- <p>Age: ${actor.age}</p> --%>
<hr />
<div class="scrollable">${actor.biography}</div>
<hr>
<h4 class="muted">Comments:</h4>

<c:choose>
<c:when test="${not empty actor.list_comment }">
<c:forEach items="${actor.list_comment}" var="comment" >
<p>${comment.comment_content}</p>
</c:forEach>
</c:when>
<c:otherwise>
<p>No comments so far. Be the first to comment. </p>
</c:otherwise>
</c:choose>
<form action="<%=request.getContextPath()%>/SearchPath" method="post">
<p>Enter Comment:</p><textarea class="form-control" rows="3" id="comment" name="comment" required></textarea>
<input type = "hidden" name = "flag" value = "insert_comment">
<input type = "hidden" name = "id" value = "<%=request.getParameter("id") %>">
<input type = "submit" value = "Comment" class="btn btn-primary">
</form>
</div>
</div>
<%session.setAttribute("page_load", null);%>
</body>
</html>