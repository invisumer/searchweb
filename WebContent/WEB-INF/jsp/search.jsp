<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Searchweb - Results</title>
<link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet">
<style>
/* Sticky footer styles
-------------------------------------------------- */
html {
  position: relative;
  min-height: 100%;
}
body {
  /* Margin bottom by footer height */
  margin-bottom: 60px;
}
.footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  /* Set the fixed height of the footer here */
  height: 60px;
  background-color: #f5f5f5;
}


/* Custom page CSS
-------------------------------------------------- */
/* Not required for template or sticky footer method. */

body > .container {
  padding: 60px 15px 0;
}
.container .text-muted {
  margin: 20px 0;
}

.footer > .container {
  padding-right: 15px;
  padding-left: 15px;
}

code {
  font-size: 80%;
}
</style>
</head>
<body>

	<header class="navbar navbar-inverse">
	<div class="container">
		<div class="navbar-header">
 			<button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
				<span class="sr-only">Toggle navigation</span> 
				<span class="icon-bar"></span> <span class="icon-bar">
				</span> <span class="icon-bar"></span>
			</button>
			
			<a href="<c:url value="/" />" class="navbar-brand">SearchWeb</a>
		</div>
		<nav class="collapse navbar-collapse">
 			<ul class="nav navbar-nav navbar-right">
				<li class="active"><a href="#">Help</a></li>
			</ul>
		</nav>
	</div>
	</header>

	<div class="container" style="margin-top: 60px;">
		<div class="row">
			<div class="col-md-12">
				<c:url var="url" value="/search" />
				<form:form action="${url}" method="post" modelAttribute="queryForm">
				<div class="input-group">
					<span class="input-group-addon" id="basic-addon1"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></span>
					<form:input path="query" class="form-control" placeholder="Search for..."/>
					<span class="input-group-btn">
						<button class="btn btn-default" type="submit">
							Search
						</button>
					</span>
				</div>
				</form:form>
			</div>
		</div>

		<br>
		
		<div class="row">
			<div class="col-md-6 col-xs-12">
				<ul class="list-unstyled">
					<c:forEach var="result" items="${results}">
						<li style="margin-bottom: 30px;">
							<h4>
							<a href="${result.url}">
								<c:choose>
								    <c:when test="${empty result.title}">
								        Title not found
								    </c:when>
								    <c:otherwise>
								        ${fn:substring(result.title, 0, 50)}
								        <c:if test="${fn:length(result.title) > 50}">...</c:if>
								    </c:otherwise>
								</c:choose>
							</a>
							</h4>
							<p>
								<span class="text-success">
								${fn:substring(result.url, 0, 40)}<c:if test="${fn:length(result.url) > 40}">...</c:if>
								</span>
								<br>
								${result.snippet}
							</p>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
	
	<br>
	
	<footer class="footer">
      <div class="container">
        <p class="text-muted">Powered by BPR Search Engine.</p>
      </div>
    </footer>

	<!-- scripts -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
	<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>

</body>
</html>