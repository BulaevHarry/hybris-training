<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url value="${urlLink}" var="encodedUrl" />
<c:if test="${media.url}">
<div class="simple_banner">

	<c:choose>
		<c:when test="${empty encodedUrl || encodedUrl eq '#'}">
			<img title="${media.altText}" alt="${media.altText}" src="${media.url}">
		</c:when>
		<c:otherwise>
			<a href="${encodedUrl}"><img title="${media.altText}" alt="${media.altText}" src="${media.url}"></a>
		</c:otherwise>
	</c:choose>

</div>
</c:if>	