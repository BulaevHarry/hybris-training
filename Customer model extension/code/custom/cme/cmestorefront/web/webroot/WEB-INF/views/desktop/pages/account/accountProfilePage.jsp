<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>

	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="profile" />
	<div class="span-20 last cust_acc-page">
		<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2><spring:theme code="text.account.profile" text="Profile"/></h2>
			</div>
			<div class="item_container">
				<table class="cust_acc-profile">
					<tr>
						<td><spring:theme code="profile.title" text="Title"/>: </td>
						<td>${fn:escapeXml(title.name)}</td>
					</tr>
					<tr>
						<td><spring:theme code="profile.firstName" text="First name"/>: </td>
						<td>${fn:escapeXml(customerData.firstName)}</td>
					</tr>
					<tr>
						<td><spring:theme code="profile.lastName" text="Last name"/>: </td>
						<td>${fn:escapeXml(customerData.lastName)}</td>
					</tr>
					<tr>
						<td><spring:theme code="profile.email" text="E-mail"/>: </td>
						<td>${fn:escapeXml(customerData.displayUid)}</td>
					</tr>
				</table>
				<ul class="updates">
								<li><a href="update-password"><button class="positive left" type="submit"><spring:theme code="text.account.profile.changePassword" text="Change password"/></button></a></li>
								<li><a href="update-profile"><button class="positive left" type="submit"><spring:theme code="text.account.profile.updatePersonalDetails" text="Update personal details"/></button></a></li>
								<li><a href="update-email"><button class="positive left" type="submit"><spring:theme code="text.account.profile.updateEmail" text="Update email"/></button></a></li>
							</ul>
			</div>
			
		</div>
	</div>
</template:page>