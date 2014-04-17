/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.epam.cme.storefront.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 */
public class SopPaymentDetailsForm {
    private String startMonth;
    private String startYear;
    private AddressForm billingAddress;

    private String vin_WebSession_version;
    private String vin_WebSession_method;
    private String vin_WebSession_vid;
    private String vin_PaymentMethod_type;
    private String vin_PaymentMethod_accountHolderName;
    private String vin_PaymentMethod_creditCard_account;
    private String vin_PaymentMethod_creditCard_expirationDate;
    private String vin_PaymentMethod_creditCard_expirationDate_Month;
    private String vin_PaymentMethod_creditCard_expirationDate_Year;
    private String vin_PaymentMethod_customerSpecifiedType;
    private String vin_PaymentMethod_nameValues_cvn;
    private String vin_PaymentMethod_billingAddress_name;
    private String vin_PaymentMethod_billingAddress_addr1;
    private String vin_PaymentMethod_billingAddress_addr2;
    private String vin_PaymentMethod_billingAddress_city;
    private String vin_PaymentMethod_billingAddress_district;
    private String vin_PaymentMethod_billingAddress_postalCode;
    private String vin_PaymentMethod_billingAddress_country;
    private String vin_PaymentMethod_billingAddress_phone;
    private String vin_Account_merchantAccountId;

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(final String startMonth) {
        this.startMonth = startMonth;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(final String startYear) {
        this.startYear = startYear;
    }

    // @Valid
    public AddressForm getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final AddressForm billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getVin_PaymentMethod_creditCard_expirationDate() {
        return vin_PaymentMethod_creditCard_expirationDate;
    }

    public void setVin_PaymentMethod_creditCard_expirationDate(final String vin_PaymentMethod_creditCard_expirationDate) {
        this.vin_PaymentMethod_creditCard_expirationDate = vin_PaymentMethod_creditCard_expirationDate;
    }

    public String getVin_PaymentMethod_nameValues_cvn() {
        return vin_PaymentMethod_nameValues_cvn;
    }

    @Pattern(regexp = "(^$|^?\\d*$)", message = "{payment.issueNumber.invalid}")
    public void setVin_PaymentMethod_nameValues_cvn(final String vin_PaymentMethod_nameValues_cvn) {
        this.vin_PaymentMethod_nameValues_cvn = vin_PaymentMethod_nameValues_cvn;
    }

    @NotNull(message = "{payment.expiryMonth.invalid}")
    @Size(min = 1, max = 2, message = "{payment.expiryMonth.invalid}")
    public String getVin_PaymentMethod_creditCard_expirationDate_Month() {
        return vin_PaymentMethod_creditCard_expirationDate_Month;
    }

    public void setVin_PaymentMethod_creditCard_expirationDate_Month(
            final String vin_PaymentMethod_creditCard_expirationDate_Month) {
        this.vin_PaymentMethod_creditCard_expirationDate_Month = vin_PaymentMethod_creditCard_expirationDate_Month;
    }

    public String getVin_WebSession_version() {
        return vin_WebSession_version;
    }

    public void setVin_WebSession_version(final String vin_WebSession_version) {
        this.vin_WebSession_version = vin_WebSession_version;
    }

    public String getVin_WebSession_method() {
        return vin_WebSession_method;
    }

    public void setVin_WebSession_method(final String vin_WebSession_method) {
        this.vin_WebSession_method = vin_WebSession_method;
    }

    public String getVin_WebSession_vid() {
        return vin_WebSession_vid;
    }

    public void setVin_WebSession_vid(final String vin_WebSession_vid) {
        this.vin_WebSession_vid = vin_WebSession_vid;
    }

    public String getVin_PaymentMethod_type() {
        return vin_PaymentMethod_type;
    }

    public void setVin_PaymentMethod_type(final String vin_PaymentMethod_type) {
        this.vin_PaymentMethod_type = vin_PaymentMethod_type;
    }

    @NotNull(message = "{payment.nameOnCard.invalid}")
    @Size(min = 1, max = 255, message = "{payment.nameOnCard.invalid}")
    public String getVin_PaymentMethod_accountHolderName() {
        return vin_PaymentMethod_accountHolderName;
    }

    public void setVin_PaymentMethod_accountHolderName(final String vin_PaymentMethod_accountHolderName) {
        this.vin_PaymentMethod_accountHolderName = vin_PaymentMethod_accountHolderName;
    }

    @NotNull(message = "{payment.cardNumber.invalid}")
    @Size(min = 16, max = 16, message = "{payment.cardNumber.invalid}")
    public String getVin_PaymentMethod_creditCard_account() {
        return vin_PaymentMethod_creditCard_account;
    }

    public void setVin_PaymentMethod_creditCard_account(final String vin_PaymentMethod_creditCard_account) {
        this.vin_PaymentMethod_creditCard_account = vin_PaymentMethod_creditCard_account;
    }

    @NotNull(message = "{payment.expiryYear.invalid}")
    @Size(min = 2, max = 4, message = "{payment.expiryYear.invalid}")
    public String getVin_PaymentMethod_creditCard_expirationDate_Year() {
        return vin_PaymentMethod_creditCard_expirationDate_Year;
    }

    public void setVin_PaymentMethod_creditCard_expirationDate_Year(
            final String vin_PaymentMethod_creditCard_expirationDate_Year) {
        this.vin_PaymentMethod_creditCard_expirationDate_Year = vin_PaymentMethod_creditCard_expirationDate_Year;
    }

    @NotNull(message = "{payment.cardType.invalid}")
    @Size(min = 1, max = 255, message = "{payment.cardType.invalid}")
    public String getVin_PaymentMethod_customerSpecifiedType() {
        return vin_PaymentMethod_customerSpecifiedType;
    }

    public void setVin_PaymentMethod_customerSpecifiedType(final String vin_PaymentMethod_customerSpecifiedType) {
        this.vin_PaymentMethod_customerSpecifiedType = vin_PaymentMethod_customerSpecifiedType;
    }

    public String getVin_PaymentMethod_billingAddress_name() {
        return vin_PaymentMethod_billingAddress_name;
    }

    public void setVin_PaymentMethod_billingAddress_name(final String vin_PaymentMethod_billingAddress_name) {
        this.vin_PaymentMethod_billingAddress_name = vin_PaymentMethod_billingAddress_name;
    }

    public String getVin_PaymentMethod_billingAddress_addr1() {
        return vin_PaymentMethod_billingAddress_addr1;
    }

    public void setVin_PaymentMethod_billingAddress_addr1(final String vin_PaymentMethod_billingAddress_addr1) {
        this.vin_PaymentMethod_billingAddress_addr1 = vin_PaymentMethod_billingAddress_addr1;
    }

    public String getVin_PaymentMethod_billingAddress_addr2() {
        return vin_PaymentMethod_billingAddress_addr2;
    }

    public void setVin_PaymentMethod_billingAddress_addr2(final String vin_PaymentMethod_billingAddress_addr2) {
        this.vin_PaymentMethod_billingAddress_addr2 = vin_PaymentMethod_billingAddress_addr2;
    }

    public String getVin_PaymentMethod_billingAddress_city() {
        return vin_PaymentMethod_billingAddress_city;
    }

    public void setVin_PaymentMethod_billingAddress_city(final String vin_PaymentMethod_billingAddress_city) {
        this.vin_PaymentMethod_billingAddress_city = vin_PaymentMethod_billingAddress_city;
    }

    public String getVin_PaymentMethod_billingAddress_district() {
        return vin_PaymentMethod_billingAddress_district;
    }

    public void setVin_PaymentMethod_billingAddress_district(final String vin_PaymentMethod_billingAddress_district) {
        this.vin_PaymentMethod_billingAddress_district = vin_PaymentMethod_billingAddress_district;
    }

    public String getVin_PaymentMethod_billingAddress_postalCode() {
        return vin_PaymentMethod_billingAddress_postalCode;
    }

    public void setVin_PaymentMethod_billingAddress_postalCode(final String vin_PaymentMethod_billingAddress_postalCode) {
        this.vin_PaymentMethod_billingAddress_postalCode = vin_PaymentMethod_billingAddress_postalCode;
    }

    public String getVin_PaymentMethod_billingAddress_country() {
        return vin_PaymentMethod_billingAddress_country;
    }

    public void setVin_PaymentMethod_billingAddress_country(final String vin_PaymentMethod_billingAddress_country) {
        this.vin_PaymentMethod_billingAddress_country = vin_PaymentMethod_billingAddress_country;
    }

    public String getVin_PaymentMethod_billingAddress_phone() {
        return vin_PaymentMethod_billingAddress_phone;
    }

    public void setVin_PaymentMethod_billingAddress_phone(final String vin_PaymentMethod_billingAddress_phone) {
        this.vin_PaymentMethod_billingAddress_phone = vin_PaymentMethod_billingAddress_phone;
    }

    public String getVin_Account_merchantAccountId() {
        return vin_Account_merchantAccountId;
    }

    public void setVin_Account_merchantAccountId(final String vin_Account_merchantAccountId) {
        this.vin_Account_merchantAccountId = vin_Account_merchantAccountId;
    }

}
