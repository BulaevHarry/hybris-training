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
package com.epam.cme.storefront.controllers.pages;

import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.ui.Model;

/**
 */
public abstract class AbstractSearchPageController extends AbstractPageController {

    private static final int MAX_PAGE_LIMIT = 100;// should be configured

    public static enum ShowMode {
        Page,
        All
    }

    protected PageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode,
            final ShowMode showMode) {
        final PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(pageNumber);
        pageableData.setSort(sortCode);

        if (ShowMode.All == showMode) {
            pageableData.setPageSize(MAX_PAGE_LIMIT);
        } else {
            pageableData.setPageSize(pageSize);
        }
        return pageableData;
    }

    protected boolean isShowAllAllowed(final SearchPageData<?> searchPageData) {
        return searchPageData.getPagination().getNumberOfPages() > 1
                && searchPageData.getPagination().getTotalNumberOfResults() < MAX_PAGE_LIMIT;
    }

    protected void populateModel(final Model model, final SearchPageData<?> searchPageData, final ShowMode showMode) {
        model.addAttribute("searchPageData", searchPageData);
        model.addAttribute("isShowAllAllowed",
                Boolean.valueOf(showMode == ShowMode.Page && isShowAllAllowed(searchPageData)));
        model.addAttribute("isShowAllRequested", Boolean.valueOf(showMode == ShowMode.All));
        model.addAttribute("maxPageLimit", Integer.valueOf(getMaxSearchPageSize()));
    }

    protected Map<String, FacetData<SearchStateData>> convertBreadcrumbsToFacets(
            final List<BreadcrumbData<SearchStateData>> breadcrumbs) {
        final Map<String, FacetData<SearchStateData>> facets = new HashMap<String, FacetData<SearchStateData>>();
        if (breadcrumbs == null) {
            return facets;
        }

        for (final BreadcrumbData<SearchStateData> breadcrumb : breadcrumbs) {
            FacetData<SearchStateData> facet = facets.get(breadcrumb.getFacetName());
            if (facet == null) {
                facet = new FacetData<SearchStateData>();
                facet.setName(breadcrumb.getFacetName());
                facet.setCode(breadcrumb.getFacetCode());
                facets.put(breadcrumb.getFacetName(), facet);
            }

            final List<FacetValueData<SearchStateData>> facetValues = facet.getValues() != null ? new ArrayList<FacetValueData<SearchStateData>>(
                    facet.getValues()) : new ArrayList<FacetValueData<SearchStateData>>();
            final FacetValueData<SearchStateData> facetValueData = new FacetValueData<SearchStateData>();
            facetValueData.setSelected(true);
            facetValueData.setName(breadcrumb.getFacetValueName());
            facetValueData.setCode(breadcrumb.getFacetValueCode());
            facetValueData.setCount(0L);
            facetValueData.setQuery(breadcrumb.getRemoveQuery());
            facetValues.add(facetValueData);
            facet.setValues(facetValues);
        }
        return facets;
    }

    protected List<FacetData<SearchStateData>> refineFacets(final List<FacetData<SearchStateData>> facets,
            final Map<String, FacetData<SearchStateData>> selectedFacets) {
        final List<FacetData<SearchStateData>> refinedFacets = new ArrayList<FacetData<SearchStateData>>();
        for (final FacetData<SearchStateData> facet : facets) {
            facet.setTopValues(Collections.<FacetValueData<SearchStateData>>emptyList());
            final List<FacetValueData<SearchStateData>> facetValues = new ArrayList<FacetValueData<SearchStateData>>(
                    facet.getValues());

            for (final FacetValueData<SearchStateData> facetValueData : facetValues) {
                if (selectedFacets.containsKey(facet.getName())) {
                    final boolean foundFacetWithName = null != CollectionUtils.find(selectedFacets.get(facet.getName())
                            .getValues(), new BeanPropertyValueEqualsPredicate("name", facetValueData.getName(), true));
                    facetValueData.setSelected(foundFacetWithName);
                }
            }

            if (selectedFacets.containsKey(facet.getName())) {
                facetValues.addAll(selectedFacets.get(facet.getName()).getValues());
                selectedFacets.remove(facet.getName());
            }

            refinedFacets.add(facet);
        }

        if (!selectedFacets.isEmpty()) {
            refinedFacets.addAll(selectedFacets.values());
        }

        return refinedFacets;
    }

    /**
     * Get the default search page size.
     * 
     * @return the number of results per page, <tt>0</tt> (zero) indicated 'default' size should be
     *         used
     */
    protected int getSearchPageSize() {
        final String pageSizeStr = getSiteConfigService().getProperty("storefront.search.pageSize");
        return NumberUtils.toInt(pageSizeStr, 0);
    }

    protected int getMaxSearchPageSize() {
        return MAX_PAGE_LIMIT;
    }

    public static class SearchResultsData<RESULT> {
        private List<RESULT> results;
        private PaginationData pagination;

        public List<RESULT> getResults() {
            return results;
        }

        public void setResults(final List<RESULT> results) {
            this.results = results;
        }

        public PaginationData getPagination() {
            return pagination;
        }

        public void setPagination(final PaginationData pagination) {
            this.pagination = pagination;
        }
    }

}
