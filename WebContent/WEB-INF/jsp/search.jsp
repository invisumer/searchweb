<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:template>
	<jsp:attribute name="title">
		Searchweb - Search web pages
	</jsp:attribute>
	<jsp:attribute name="body">
		<div class="row">
			<div class="col-md-12">
				<c:url var="url" value="/search" />
				<form:form action="${url}" method="get" modelAttribute="queryForm">
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
		
		<c:if test="${error!=null}">
			<div class="alert alert-danger alert-dismissible" role="alert">
			  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			  <strong>${error}</strong>
			</div>
		</c:if>
		
		<c:if test="${error==null && empty results}">
			<div class="alert alert-info alert-dismissible" role="alert">
			  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			  <strong>There is no match for the query you submitted.</strong>
			</div>
		</c:if>
		
		<c:if test="${statistics != null}">
			<div class="row">
				<div class="col-md-12">
					<p>${statistics}</p>
				</div>
			</div>
		</c:if>
		
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
								${fn:substring(result.url, 0, 35)}<c:if test="${fn:length(result.url) > 40}">...</c:if>
								</span>
								<br>
								${result.snippet}
							</p>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
		
		<c:if test="${results!=null && ! empty results}">
 		<div class="row">
			<div class="col-md-12">
				<ul class="pagination pagination-sm">
					<li>
						<a href="<c:url value="/search/page/1"/>">&laquo;&laquo;</a>
					</li>
					<li>
						<c:choose>
							<c:when test="${currentPage-1>0}">
								<a href="<c:url value="/search/page/"/>${currentPage-1}"><b>&laquo;</b></a>
							</c:when>
							<c:otherwise>
								<a class="disabled">&laquo;</a>
							</c:otherwise>
						</c:choose>
					</li>
					<c:choose>
						<c:when test="${currentPage<=3 || pages<=5}">
							<c:set value="1" var="start"></c:set>
							<c:set value="${pages<=5?pages:5}" var="end"></c:set>
						</c:when>
						<c:when test="${currentPage>=pages-2}">
							<c:set value="${pages-4}" var="start"></c:set>
							<c:set value="${pages}" var="end"></c:set>
						</c:when>
						<c:otherwise>
							<c:set value="${currentPage-2}" var="start"></c:set>
							<c:set value="${currentPage+2}" var="end"></c:set>
						</c:otherwise>
					</c:choose>
					<c:forEach begin="${start}" end="${end}" varStatus="i">
						<c:if test="${i.index==currentPage}"> 
							<c:set value="active" var="cssClass"></c:set>
						</c:if>
						<li class="${cssClass}">
							<a  href="<c:url value="/search/page/"/>${i.index}" ><b>${i.index}</b></a>
						</li>
						<c:set value="" var="cssClass"></c:set>
					</c:forEach>
					<li>
						<c:choose>
							<c:when test="${currentPage+1<=pages}">
								<a href="<c:url value="/search/page/"/>${currentPage+1}"><b>&raquo;</b></a>
							</c:when>
							<c:otherwise>
								<a class="disabled">&raquo;</a>
							</c:otherwise>
						</c:choose>
					</li>
					<li>
						<a href="<c:url value="/search/page/"/>${pages}">&raquo;&raquo;</a>
					</li>
				</ul>
			</div>
		</div>
		</c:if>
	</jsp:attribute>
</t:template>