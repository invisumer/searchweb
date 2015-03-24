<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Searchweb</title>
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
		<nav class="collapse navbar-collapse bs-navbar-collapse">
			<ul class="nav navbar-nav navbar-right">
				<li class="active"><a href="#">Help</a></li>
			</ul>
		</nav>
	</div>
	</header>

	<div class="container" style="margin-top: 60px;">
		<div class="row">
			<div class="col-md-2 col-md-offset-5 col-xs-4 col-xs-offset-4">
				<img src="<c:url value="/resources/img/logo.png" />" alt="..." class="img-responsive center-block" />
			</div>
		</div>
		<br>
		<div class="row">
			<div class="col-md-8 col-md-offset-2 col-xs-12">
				<c:url var="url" value="/search" />
				<form:form action="${url}" method="post" modelAttribute="queryForm">
				<div class="input-group">
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
	</div>
	
	<footer class="footer">
      <div class="container">
        <p class="text-muted">Performed by BPR Search Engine.</p>
      </div>
    </footer>

	<!-- scripts -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
	<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>

</body>
</html>