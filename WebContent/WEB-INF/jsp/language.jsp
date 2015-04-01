<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:template>
	<jsp:attribute name="title">
		Searchweb - Select language
	</jsp:attribute>
	<jsp:attribute name="body">
		<div class="row">
			<div class="col-md-8 col-md-offset-2 col-xs-12">
				<c:url var="url" value="/select/language" />
				<form:form action="${url}" method="post" modelAttribute="languageForm">
					<div class="form-group">
						<label for="language">Select the languages that you prefer:</label>
					</div>
					<form:select path="language" items="${langOptions}" />
					<br>
					<br>
					<button class="btn btn-default" type="submit">
						Submit
					</button>
				</form:form>
			</div>
		</div>
	</jsp:attribute>
</t:template>