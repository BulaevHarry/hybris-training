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

import org.hibernate.validator.constraints.Range;


/**
 * Form object for writing reviews
 */
public class ReviewForm
{
	private String headline;
	private String comment;
	private Double rating;
	private String alias;

	/**
	 * @return the headline
	 */
	@NotNull(message = "{review.headline.invalid}")
	@Size(min = 1, max = 255, message = "{review.headline.invalid}")
	public String getHeadline()
	{
		return headline;
	}

	/**
	 * @param headline
	 *           the headline to set
	 */
	public void setHeadline(final String headline)
	{
		this.headline = headline;
	}

	/**
	 * @return the comment
	 */
	@NotNull(message = "{review.comment.invalid}")
	@Size(min = 1, max = 4000, message = "{review.comment.invalid}")
	public String getComment()
	{
		return comment;
	}

	/**
	 * @param comment
	 *           the comment to set
	 */
	public void setComment(final String comment)
	{
		this.comment = comment;
	}

	/**
	 * @return the rating
	 */
	@NotNull(message = "{review.rating.invalid}")
	@Range(min = 1, max = 5, message = "{review.rating.invalid}")
	public Double getRating()
	{
		return rating;
	}

	/**
	 * @param rating
	 *           the rating to set
	 */
	public void setRating(final Double rating)
	{
		this.rating = rating;
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(final String alias)
	{
		this.alias = alias;
	}
}
