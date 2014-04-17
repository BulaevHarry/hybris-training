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
import javax.validation.constraints.Size;

/**
 */
public class AddressForm {
    public static final Boolean DEFAULT_SAVE_IN_ADDRESS_BOOK = Boolean.TRUE;

    private String addressId;
    private String titleCode;
    private String firstName;
    private String lastName;
    private String line1;
    private String line2;
    private String townCity;
    private String postcode;
    private String countryIso;
    private Boolean saveInAddressBook = DEFAULT_SAVE_IN_ADDRESS_BOOK;
    private Boolean defaultAddress;
    private Boolean shippingAddress;
    private Boolean billingAddress;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(final String addressId) {
        this.addressId = addressId;
    }

    /**
     * @return the titleCode
     */
    @NotNull(message = "{address.title.invalid}")
    @Size(min = 1, max = 255, message = "{address.title.invalid}")
    public String getTitleCode() {
        return titleCode;
    }

    /**
     * @param titleCode
     *            the titleCode to set
     */
    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    /**
     * @return the firstName
     */
    @NotNull(message = "{address.firstName.invalid}")
    @Size(min = 1, max = 255, message = "{address.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    @NotNull(message = "{address.lastName.invalid}")
    @Size(min = 1, max = 255, message = "{address.lastName.invalid}")
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the line1
     */
    @NotNull(message = "{address.line1.invalid}")
    @Size(min = 1, max = 255, message = "{address.line1.invalid}")
    public String getLine1() {
        return line1;
    }

    /**
     * @param line1
     *            the line1 to set
     */
    public void setLine1(final String line1) {
        this.line1 = line1;
    }

    /**
     * @return the line2
     */
    public String getLine2() {
        return line2;
    }

    /**
     * @param line2
     *            the line2 to set
     */
    public void setLine2(final String line2) {
        this.line2 = line2;
    }

    /**
     * @return the townCity
     */
    @NotNull(message = "{address.townCity.invalid}")
    @Size(min = 1, max = 255, message = "{address.townCity.invalid}")
    public String getTownCity() {
        return townCity;
    }

    /**
     * @param townCity
     *            the townCity to set
     */
    public void setTownCity(final String townCity) {
        this.townCity = townCity;
    }

    /**
     * @return the postcode
     */
    @NotNull(message = "{address.postcode.invalid}")
    @Size(min = 1, max = 10, message = "{address.postcode.invalid}")
    public String getPostcode() {
        return postcode;
    }

    /**
     * @param postcode
     *            the postcode to set
     */
    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    /**
     * @return the countryIso
     */
    @NotNull(message = "{address.country.invalid}")
    @Size(min = 1, max = 255, message = "{address.country.invalid}")
    public String getCountryIso() {
        return countryIso;
    }

    /**
     * @param countryIso
     *            the countryIso to set
     */
    public void setCountryIso(final String countryIso) {
        this.countryIso = countryIso;
    }

    public Boolean getSaveInAddressBook() {
        return saveInAddressBook;
    }

    public void setSaveInAddressBook(final Boolean saveInAddressBook) {
        this.saveInAddressBook = saveInAddressBook;
    }

    public Boolean getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(final Boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public Boolean getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(final Boolean shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Boolean getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final Boolean billingAddress) {
        this.billingAddress = billingAddress;
    }
}
