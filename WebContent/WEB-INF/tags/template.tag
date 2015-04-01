<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="body" fragment="true" %>
<%@ attribute name="title" fragment="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<title><jsp:invoke fragment="title"/></title>
	
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
				<ul class="nav navbar-nav">
					<li><a href="<c:url value="/" />">Web</a></li>
					<li><a href="#">Image</a></li>
					<li><a href="#">Music</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li class="active"><a href="<c:url value="/select/language" />">Language</a></li>
				</ul>
			</nav>
		</div>
	</header>
	
	<div class="container">
		<jsp:invoke fragment="body"/>
	</div>
	
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