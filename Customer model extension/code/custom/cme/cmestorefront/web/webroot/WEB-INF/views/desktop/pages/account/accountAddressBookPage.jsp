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
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>

<template:page pageTitle="${pageTitle}">
	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<nav:accountNav selected="address-book" />
	<div class="span-20 last cust_acc-page">
		<div class="item_container_holder address_book">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h2><spring:theme code="text.account.addressBook" text="Address Book"/></h2>
			</div>
			<div class="item_container">
				<p><spring:theme code="text.account.addressBook.manageYourAddresses" text="Manage your address book"/></p>
				<c:if test="${not empty addressData}">
					<table id="address_book">
						<thead>
							<tr>
								<th id="header1"><spring:theme code="text.address" text="Address"/></th>
								<th id="header2"><spring:theme code="text.updates" text="Updates"/></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${addressData}" var="address">
								<tr>
									<td headers="header1">
										<ycommerce:testId code="addressBook_address_label">
											<ul>
												<li>${fn:escapeXml(address.title)}&nbsp;${fn:escapeXml(address.firstName)}&nbsp;${fn:escapeXml(address.lastName)}</li>
												<li>${fn:escapeXml(address.line1)}</li>
												<li>${fn:escapeXml(address.line2)}</li>
												<li>${fn:escapeXml(address.town)}</li>
												<li>${fn:escapeXml(address.postalCode)}</li>
												<li>${fn:escapeXml(address.country.name)}</li>
											</ul>
										</ycommerce:testId>
									</td>
									<td headers="header2">
										<ycommerce:testId code="addressBook_addressOptions_label">
											<ul class="updates">
												<li><ycommerce:testId code="addressBook_editAddress_button"><a href="edit-address/${address.id}" class="actionlink"><spring:theme code="text.edit" text="Edit"/></a></ycommerce:testId></li>
												<li><ycommerce:testId code="addressBook_removeAddress_button"><a href="remove-address/${address.id}" class="actionlink"><spring:theme code="text.remove" text="Remove"/></a></ycommerce:testId></li>
												<c:if test="${not address.defaultAddress}">
													<li><ycommerce:testId code="addressBook_isDefault_button"><a href="set-default-address/${address.id}" class="actionlink"><spring:theme code="text.setDefault" text="Set as default"/></a></ycommerce:testId></li>
												</c:if>
												<c:if test="${address.defaultAddress}">
													<li><ycommerce:testId code="addressBook_isDefault_label"><spring:theme code="text.default" text="Default"/></ycommerce:testId></li>
												</c:if>
											</ul>
										</ycommerce:testId>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:if>
                <ycommerce:testId code="addressBook_addNewAddress_button">
					<a href="add-address" class="add_address_book">
						<button class="positive left" type="submit">
							<spring:theme code="text.account.addressBook.addAddress" text="Add new address"/>
						</button>
					</a>
				</ycommerce:testId>
			</div>
		</div>
	</div>
</template:page>