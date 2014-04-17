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
package com.epam.cme.facades.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.converters.ConversionException;

/*
 * Adds the search fields to the search query
 */
public class GuidedSellingSearchSolrQueryPopulator implements
        Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest> {
    @Override
    public void populate(final SearchQueryPageableData<SolrSearchQueryData> source, final SolrSearchRequest target)
            throws ConversionException {
        if (StringUtils.isEmpty(source.getSearchQueryData().getCategoryCode())
                && StringUtils.isEmpty(source.getSearchQueryData().getFreeTextSearch())) {
            final SearchQuery searchQuery = (SearchQuery) target.getSearchQuery();

            if (searchQuery != null && CollectionUtils.isNotEmpty(source.getSearchQueryData().getFilterTerms())) {
                for (final SolrSearchQueryTermData solrSearchQueryTermData : source.getSearchQueryData()
                        .getFilterTerms()) {
                    searchQuery.searchInField(solrSearchQueryTermData.getKey(), solrSearchQueryTermData.getValue());
                }
            }
        }
    }
}
