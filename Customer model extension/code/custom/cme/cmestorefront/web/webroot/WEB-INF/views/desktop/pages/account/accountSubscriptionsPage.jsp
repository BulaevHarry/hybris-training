<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="subscriptions" />
	<div class="span-20 last">
		<div class="span-20 wide-content-slot advert">
			<cms:slot var="feature" contentSlot="${slots['TopContent']}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>
	
		<div class="item_container_holder subscriptions">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2><spring:theme code="text.account.subscriptions" text="Subscriptions"/></h2>
			</div>
			<div class="item_container">
				<c:if test="${not empty subscriptions}">
					<p><spring:theme code="text.account.subscriptions.manageSubscriptions" text="Manage your subscriptions"/></p>

					<table id="subscriptions">
						<thead>
							<tr>
								<th id="header1"><spring:theme code="text.account.subscriptions.productName" text="Product Name"/></th>
								<th id="header2"><spring:theme code="text.account.subscriptions.startDate" text="Start Date"/></th>
								<th id="header3"><spring:theme code="text.account.subscriptions.endDate" text="End Date"/></th>
								<th id="header4"><spring:theme code="text.account.subscriptions.renewalType" text="Renewal Type"/></th>
								<th id="header5"><spring:theme code="text.account.subscriptions.status" text="Status"/></th>
								<th id="header6"><spring:theme code="text.account.subscriptions.actions" text="Actions"/></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${subscriptions}" var="subscription">

								<c:url value="/my-account/subscription/${subscription.id}" var="myAccountSubscriptionDetailsUrl"/>
								<c:url value="${subscription.productUrl}" var="productDetailsUrl"/>
								
								<tr>
									<td headers="header1">
										<ycommerce:testId code="subscriptions_productName_link">
											<p><a href="${productDetailsUrl}">${subscription.name}</a></p>
										</ycommerce:testId>
									</td>
									<td headers="header2">
										<ycommerce:testId code="subscriptions_startDate_label">
											<p><fmt:formatDate value="${subscription.startDate}" dateStyle="long" timeStyle="short" type="both"/></p>
										</ycommerce:testId>
									</td>
									<td headers="header3">
										<ycommerce:testId code="subscriptions_endDate_label">
											<p><fmt:formatDate value="${subscription.endDate}" dateStyle="long" timeStyle="short" type="both"/></p>
										</ycommerce:testId>
									</td>
									<td headers="header4">
										<ycommerce:testId code="subscriptions_renewalType_label">
											<p><spring:theme code="text.account.subscription.renewalType.${subscription.renewalType}"/></p>
										</ycommerce:testId>
									</td>
									<td headers="header5">
										<ycommerce:testId code="subscriptions_status_label">
											<p><spring:theme code="text.account.subscription.status.${subscription.subscriptionStatus}"/></p>
										</ycommerce:testId>
									<td headers="header6">
										<ycommerce:testId code="subscriptions_actions_links">
											<p><a href="${myAccountSubscriptionDetailsUrl}" class="actionlink"><spring:theme code="text.view" text="View"/></a></p>
										</ycommerce:testId>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>

				</c:if>
				<c:if test="${empty subscriptions}">
					<p><spring:theme code="text.account.subscriptions.noSubscriptions" text="You have no subscriptions"/></p>
				</c:if>
			</div>
		</div>
	</div>
</template:page>