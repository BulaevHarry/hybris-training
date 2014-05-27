<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/desktop/checkout/multi" %>

<c:url value="/checkout/multi/choose-payment-method" var="choosePaymentMethodUrl"/>
<template:page pageTitle="${pageTitle}">

	<div id="breadcrumb" class="breadcrumb">
		<breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}"/>
	</div>
	<div id="globalMessages">
		<common:globalMessages/>
	</div>

<div class="telco-checkout checkout-add-payment"> <!-- telco change -->
		
	<multi-checkout:checkoutProgressBar steps="${checkoutSteps}" currentStep="3" stepName="paymentMethod"/>
	
	<div class="span-24 last multicheckout">
		<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top"><span></span></div>
				</div>
				<h2>
					<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.header" text="Payment Details"/></h2>
			</div>
			<div class="item_container">
				<form:form method="post" commandName="sopPaymentDetailsForm" class="create_update_payment_form" action="${silentOrderPageData.postUrl}">
					<div class="payment_details_left_col">
						<h1><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.paymentCard"/></h1>

						<p>
							<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.enterYourCardDetails"/></p>

						<p><spring:theme code="form.required"/></p>
						<dl>
							<form:hidden path="vin_WebSession_version" class="create_update_payment_id" value="4.0"/>
							<form:hidden path="vin_WebSession_vid" class="create_update_payment_id" value="${silentOrderPageData.parameters.sessionTransactionToken}"/>
							<form:hidden path="vin_PaymentMethod_type" class="create_update_payment_id" value="CreditCard"/>
							<formElement:formSelectBox idKey="payment.cardType" labelKey="payment.cardType" path="vin_PaymentMethod_customerSpecifiedType" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect" items="${cardTypes}" tabindex="1"/>
							<formElement:formInputBox idKey="payment.nameOnCard" labelKey="payment.nameOnCard" path="vin_PaymentMethod_accountHolderName" inputCSS="text" mandatory="true" tabindex="2"/>
							<formElement:formInputBox idKey="payment.cardNumber" labelKey="payment.cardNumber" path="vin_PaymentMethod_creditCard_account" inputCSS="text" mandatory="true" tabindex="3"/>

							<template:errorSpanField path="startMonth">
								<dt><label for="StartMonth"><spring:theme code="payment.startDate"/></label></dt>
								<dd>
									<form:select id="StartMonth" path="startMonth" cssClass="card_date" tabindex="4">
										<option value="" label="<spring:theme code='payment.month'/>"/>
										<form:options items="${months}" itemValue="code" itemLabel="name"/>
									</form:select>

									<form:select id="StartYear" path="startYear" cssClass="card_date" tabindex="5">
										<option value="" label="<spring:theme code='payment.year'/>"/>
										<form:options items="${startYears}" itemValue="code" itemLabel="name"/>
									</form:select>
								</dd>

								<dt><label for="ExpiryMonth"><spring:theme code="payment.expiryDate"/></label></dt>
								<dd>
									<template:errorSpanField path="vin_PaymentMethod_creditCard_expirationDate_Month">
										<form:select id="ExpiryMonth" path="vin_PaymentMethod_creditCard_expirationDate_Month" cssClass="card_date" tabindex="6">
											<option value="" label="<spring:theme code='payment.month'/>"/>
											<form:options items="${months}" itemValue="code" itemLabel="name"/>
										</form:select>
									</template:errorSpanField>

									<template:errorSpanField path="vin_PaymentMethod_creditCard_expirationDate_Year">
										<form:select id="ExpiryYear" path="vin_PaymentMethod_creditCard_expirationDate_Year" cssClass="card_date" tabindex="7">
											<option value="" label="<spring:theme code='payment.year'/>"/>
											<form:options items="${expiryYears}" itemValue="code" itemLabel="name"/>
										</form:select>
									</template:errorSpanField>
								</dd>
							</template:errorSpanField>

							<formElement:formInputBox idKey="payment.issueNumber" labelKey="payment.issueNumber" path="vin_PaymentMethod_nameValues_cvn" inputCSS="text" mandatory="false" tabindex="8"/>
						</dl>
					</div>

					<div class="payment_details_right_col">

						<h1><spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/></h1>

						<p><spring:theme code="form.required"/></p>
						<dl id="newBillingAddressFields">
							<form:hidden path="billingAddress.addressId" class="create_update_address_id"/>
							<formElement:formSelectBox idKey="address.title" labelKey="address.title" path="billingAddress.titleCode" mandatory="true" skipBlank="false" skipBlankMessageKey="address.title.pleaseSelect" items="${titles}" tabindex="10"/>
							<formElement:formInputBox idKey="address.firstName" labelKey="product.list.viewplans.name" path="vin_PaymentMethod_billingAddress_name" inputCSS="text" mandatory="true" tabindex="11"/>
							<!-- <formElement:formInputBox idKey="address.surname" labelKey="address.surname" path="billingAddress.lastName" inputCSS="text" mandatory="true" tabindex="12"/> -->
							<formElement:formInputBox idKey="address.line1" labelKey="address.line1" path="vin_PaymentMethod_billingAddress_addr1" inputCSS="text" mandatory="true" tabindex="13"/>
							<formElement:formInputBox idKey="address.line2" labelKey="address.line2" path="vin_PaymentMethod_billingAddress_addr2" inputCSS="text" mandatory="false" tabindex="14"/>
							<formElement:formInputBox idKey="address.townCity" labelKey="address.townCity" path="vin_PaymentMethod_billingAddress_city" inputCSS="text" mandatory="true" tabindex="15"/>
							<formElement:formInputBox idKey="address.postcode" labelKey="address.postcode" path="vin_PaymentMethod_billingAddress_postalCode" inputCSS="text" mandatory="true" tabindex="16"/>
							<formElement:formSelectBox idKey="address.country" labelKey="address.country" path="vin_PaymentMethod_billingAddress_country" mandatory="true" skipBlank="false" skipBlankMessageKey="address.selectCountry" items="${billingCountries}" itemValue="isocode" selectedValue="US" tabindex="17"/>
							<form:hidden path="billingAddress.shippingAddress"/>
							<form:hidden path="billingAddress.billingAddress"/>
						</dl>
					</div>
					<div class="save_payment_details">
						<span style="display: block; clear: both;">
							<ycommerce:testId code="editPaymentMethod_savePaymentMethod_button">
								<button class="positive" tabindex="19" id="lastInTheForm">
									<spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.useThesePaymentDetails"/>
								</button>
							</ycommerce:testId>
							<c:if test="${not hasNoPaymentInfo}">
								<a href="${choosePaymentMethodUrl}">
									<button class="form" type="button">
									<spring:theme code="checkout.multi.cancel" text="Cancel"/></button>
								</a>
							</c:if>
						</span>
					</div>
				</form:form>
			</div>
		</div>
		
		<!-- telco changes -->
	<!-- <cms:slot var="feature" contentSlot="${slots.SideContent}"><cms:component component="${feature}"/></cms:slot> -->
	
	</div>
	
</div>
</template:page>
