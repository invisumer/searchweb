<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:template>
	<jsp:attribute name="title">
		Searchweb - Home
	</jsp:attribute>
	<jsp:attribute name="body">
		<div class="row">
			<div class="col-md-2 col-md-offset-5 col-xs-4 col-xs-offset-4">
				<img src="<c:url value="/resources/img/logo.png" />" alt="..." class="img-responsive center-block" />
			</div>
		</div>
		<br>
		<div class="row">
			<div class="col-md-8 col-md-offset-2 col-xs-12">
				<c:url var="url" value="/search" />
				<form:form action="${url}" method="get" modelAttribute="queryForm">
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
	</jsp:attribute>
</t:template>
