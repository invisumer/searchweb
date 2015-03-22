<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Searchweb</title>
<link href="<c:url value="/resources/css/bootstrap.min.css" />"
	rel="stylesheet">
</head>
<body>

	<header
		class="navbar navbar navbar-inverse navbar-static-top bs-docs-nav"
		id="top" role="banner">
	<div class="container">
		<div class="navbar-header">
			<button class="navbar-toggle collapsed" type="button"
				data-toggle="collapse" data-target=".bs-navbar-collapse">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a href="<c:url value="/" />" class="navbar-brand">SearchWeb</a>
		</div>
		<nav class="collapse navbar-collapse bs-navbar-collapse">
		<ul class="nav navbar-nav navbar-right">
			<li><a href="#">Help</a></li>
		</ul>
		</nav>
	</div>
	</header>

	<div class="container">
		<div class="row">
			<div class="col-md-4 col-md-offset-4 col-xs-6 col-xs-offset-3">
				<img src="<c:url value="/resources/img/logo.png" />" alt="..." class="img-responsive center-block" />
			</div>
		</div>
		<br>
		<div class="row">
			<div class="col-md-12">
				<div class="input-group">
					<input type="text" class="form-control" placeholder="Search for...">
					<span class="input-group-btn">
						<button class="btn btn-default" type="button">
							<span class="glyphicon glyphicon-search" aria-hidden="true"></span>
							Search
						</button>
					</span>
				</div>
			</div>
		</div>
	</div>

	<!-- scripts -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
	<script src="<c:url value="/resources/js/bootstrap.min.js" />"></script>

</body>
</html>