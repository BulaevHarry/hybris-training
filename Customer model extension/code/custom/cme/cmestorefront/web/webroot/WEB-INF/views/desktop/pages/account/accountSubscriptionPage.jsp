<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
	
		<c:url value="${subscriptionData.productUrl}" var="productDetailsUrl"/>
		<c:url value="/my-account/order/${subscriptionData.orderNumber}" var="orderDetailsUrl"/>
		<spring:theme code="text.account.subscription.contractDuration.${subscriptionData.contractFrequency}" arguments="${subscriptionData.contractDuration}" var="contractDuration"/>
		
		
		<div class="item_container_holder subscription_details">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2><spring:theme code="text.account.subscription.yourSubscription" text="Your Subscription"/></h2>
			</div>
			<div class="item_container">
				<!-- <p><spring:theme code="text.account.subscriptions.manageSubscriptions" text="Manage your subscriptions"/></p> -->

				<table id="subscription_details">
					<tr>
						<td class="name">
							<ycommerce:testId code="subscription_productName_link">
								<a href="${productDetailsUrl}">${subscriptionData.name}</a>
							</ycommerce:testId>
						</td>
						
						<td class="status">
							<span><spring:theme code="text.account.subscription.status" text="Status"/>:</span>
							<ycommerce:testId code="subscription_status_label">
								<spring:theme code="text.account.subscription.status.${subscriptionData.subscriptionStatus}"/>
							</ycommerce:testId>
						</td>
					</tr>
					
					<tr class="border">
						<td>
							<ycommerce:testId code="subscription_description_label">
								<c:choose>
									<c:when test="${subscriptionData.contractFrequency eq 'none'}">
										<spring:theme code="text.account.subscription.description.value.nocontract" arguments="${subscriptionData.billingFrequency}" text="-"/>
									</c:when>
									<c:otherwise>
										<spring:theme code="text.account.subscription.description.value.contract" arguments="${contractDuration}, ${subscriptionData.billingFrequency}" text="-"/>
									</c:otherwise>
								</c:choose>
							</ycommerce:testId>
						</td>
						
						<td>
							<span><spring:theme code="text.account.subscription.orderNumber" text="Order Number"/>:</span>
							<ycommerce:testId code="subscription_orderNumber_link">
								<a href="${orderDetailsUrl}">${subscriptionData.orderNumber}</a>
							</ycommerce:testId>
						</td>
					</tr>
					
					<tr>
						<td>
							<span><spring:theme code="text.account.subscription.contractDuration" text="Contract Duration"/>:</span>
							<ycommerce:testId code="subscription_contractDuration_label">
								${contractDuration}
							</ycommerce:testId>
						</td>
						
						<td>
							<span><spring:theme code="text.account.subscription.placedOn" text="Placed On"/>:</span>
							<ycommerce:testId code="subscription_placedOn_label">
								<fmt:formatDate value="${subscriptionData.placedOn}" dateStyle="long" timeStyle="short" type="both"/>
							</ycommerce:testId>
						</td>
					</tr>
					
				
					
					<tr>
						<td>
							<span><spring:theme code="text.account.subscription.billingFrequency" text="Billing Frequency"/>:</span>			
							<ycommerce:testId code="subscription_billingFrequency_label">
								<spring:theme code="text.account.subscription.billingFrequency.${subscriptionData.billingFrequency}"/>
							</ycommerce:testId>
						</td>
						
						<td>
							<span><spring:theme code="text.account.subscription.startDate" text="Start Date"/>:</span>
						
							<ycommerce:testId code="subscription_startDate_label">
								<fmt:formatDate value="${subscriptionData.startDate}" dateStyle="long" timeStyle="short" type="both"/>
							</ycommerce:testId>
						</td>
					</tr>
					
					<tr>
						<td>
							<span><spring:theme code="text.account.subscription.renewalType" text="Renewal Type"/>:</span>
					
							<ycommerce:testId code="subscription_renewalType_label">
								<spring:theme code="text.account.subscription.renewalType.${subscriptionData.renewalType}"/>
							</ycommerce:testId>
						</td>
						
						<td>
							<span><spring:theme code="text.account.subscription.endDate" text="End Date"/>:</span>
					
							<ycommerce:testId code="subscription_endDate_label">
								<fmt:formatDate value="${subscriptionData.endDate}" dateStyle="long" timeStyle="short" type="both"/>
							</ycommerce:testId>
						</td>
					</tr>
					
					<tr>
						<td>
							<span><spring:theme code="text.account.subscription.Cancellable" text="Cancellable"/>:</span>
							<ycommerce:testId code="subscription_status_label">
								<c:choose>
									<c:when test="${subscriptionData.cancellable}">
										<spring:theme code="text.account.subscription.cancellable.yes"/>
									</c:when>
									<c:otherwise>
										<spring:theme code="text.account.subscription.cancellable.no"/>
									</c:otherwise>
								</c:choose>
							</ycommerce:testId>
						</td>
						
						<td>
							<c:if test="${subscriptionData.subscriptionStatus == 'cancelled'}">
								<span><spring:theme code="text.account.subscription.cancelledDate" text="Cancelled Date"/>:</span>
								<ycommerce:testId code="subscription_cancelledDate_label">
									<fmt:formatDate value="${subscriptionData.cancelledDate}" dateStyle="long" timeStyle="short" type="both"/>
								</ycommerce:testId>
							</c:if>
						</td>
					</tr>
				</table>
			
				<c:if test="${subscriptionData.cancellable && subscriptionData.subscriptionStatus != 'cancelled'}">
					<c:url value="/my-account/cancelsubscription/${subscriptionData.id}" var="cancelUrl"/>
						<button type="submit" onclick="window.location='${cancelUrl}'" class="positive left"><spring:theme code="text.account.subscription.cancelSubscription" text="Cancel Subscription"/></button>
				</c:if>
				
			</div>
		</div>
	</div>
</template:page>