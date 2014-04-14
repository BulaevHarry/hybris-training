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
package com.epam.cme.facades.order.converters.populator;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.BillingTimeData;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import com.epam.cme.facades.data.BundleTemplateData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class BundleCartPopulatorTest
{

	private final BundleCartPopulator bundlingCartPopulator = new BundleCartPopulator();

	@Mock
	private BundleTemplateService bundleTemplateService;

	@Mock
	private Converter<BundleTemplateModel, BundleTemplateData> converter;

	@Mock
	private BundleCommerceCartService bundleCommerceCartService;

	@Mock
	private Converter<BillingTimeModel, BillingTimeData> billingTimeConverter;

	private final BillingTimeModel masterBillingTimeModelModel = new BillingTimeModel();
	private final BillingTimeData billingTimeData = new BillingTimeData();
	private final CartModel masterCart = new CartModel();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		bundlingCartPopulator.setBundleTemplateService(bundleTemplateService);
		bundlingCartPopulator.setBundleTemplateConverter(converter);
		bundlingCartPopulator.setBundleCommerceCartService(bundleCommerceCartService);
		bundlingCartPopulator.setBillingTimeConverter(billingTimeConverter);
		when(bundleCommerceCartService.getMasterBillingTime()).thenReturn(masterBillingTimeModelModel);
		when(billingTimeConverter.convert(masterBillingTimeModelModel)).thenReturn(billingTimeData);

	}

	@Test
	public void testaddEmptyBundleComponents2ToEntries()
	{
		final BundleTemplateModel rootTemplate = createBundleTemplates("Type1");
		when(bundleTemplateService.getBundleTemplateForCode("Type1", null)).thenReturn(rootTemplate);
		for (final BundleTemplateModel child : rootTemplate.getChildTemplates())
		{
			when(Boolean.valueOf(bundleCommerceCartService.checkIsComponentEditable(masterCart, child, 1))).thenReturn(Boolean.TRUE);
		}
		final Collection<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		final BundleTemplateData root1 = new BundleTemplateData();
		root1.setId("Type1");
		final BundleTemplateData root2 = new BundleTemplateData();
		root2.setId("Type2");
		//Multi cart looks like this now :
		//@formatter:off
		//	  
		//	  - master cart (pay now):
		//	    - 1 Type1 (bundle 1) is editable true
		//     - 2 Type2 bundle 2) is editable false
		//
		//@formatter:on
		orderEntries.add(createOrderEntry(1, "Type1child1", root1));
		final Collection<OrderEntryData> modifiedOrderEntries = bundlingCartPopulator.addEmptyBundleComponents(orderEntries,
				masterCart);

		Assert.assertEquals(5, modifiedOrderEntries.size());

	}

	@Test
	public void testThatStandaloneProductsWorkOK()
	{
		final Collection<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();

		orderEntries.add(createOrderEntry(0, null, null));
		orderEntries.add(createOrderEntry(0, null, null));
		//Multi cart looks like this now :
		//@formatter:off
		//	  
		//	  - master cart (pay now):
		//	    - Standalone (bundle 0) 
		//     - Standalone (bundle 0) 
		//
		//@formatter:on
		final Collection<OrderEntryData> modifiedOrderEntries = bundlingCartPopulator.addEmptyBundleComponents(orderEntries,
				masterCart);
		Assert.assertEquals(2, modifiedOrderEntries.size());
	}

	@Test
	public void testGetLastBundlesEmptyChildrenGetAdded()
	{
		final BundleTemplateModel rootTemplate1 = createBundleTemplates("Type1");
		when(bundleTemplateService.getBundleTemplateForCode("Type1", null)).thenReturn(rootTemplate1);
		final BundleTemplateModel rootTemplate2 = createBundleTemplates("Type2");
		when(bundleTemplateService.getBundleTemplateForCode("Type2", null)).thenReturn(rootTemplate2);
		final Collection<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		final BundleTemplateData root1 = new BundleTemplateData();
		root1.setId("Type1");
		final BundleTemplateData root2 = new BundleTemplateData();
		root2.setId("Type2");
		for (final BundleTemplateModel child : rootTemplate1.getChildTemplates())
		{
			when(Boolean.valueOf(bundleCommerceCartService.checkIsComponentEditable(masterCart, child, 1))).thenReturn(Boolean.TRUE);
		}
		for (final BundleTemplateModel child : rootTemplate2.getChildTemplates())
		{
			when(Boolean.valueOf(bundleCommerceCartService.checkIsComponentEditable(masterCart, child, 2))).thenReturn(Boolean.TRUE);
		}
		orderEntries.add(createOrderEntry(1, "Type1child1", root1));
		orderEntries.add(createOrderEntry(2, "Type2child1", root2));
		//Multi cart looks like this now :
		//@formatter:off
		//	  
		//	  - master cart (pay now):
		//	    - 1 Type1 (bundle 1) is editable true
		//     - 2 Type2 bundle 2) is editable true
		//
		//@formatter:on
		final Collection<OrderEntryData> modifiedOrderEntries = bundlingCartPopulator.addEmptyBundleComponents(orderEntries,
				masterCart);

		Assert.assertEquals(10, modifiedOrderEntries.size());
	}

	@Test
	public void testMultiTypeCart()
	{
		final BundleTemplateModel rootTemplate1 = createBundleTemplates("Type1");
		final BundleTemplateModel rootTemplate2 = createBundleTemplates("Type2");
		when(bundleTemplateService.getBundleTemplateForCode("Type1", null)).thenReturn(rootTemplate1);
		when(bundleTemplateService.getBundleTemplateForCode("Type2", null)).thenReturn(rootTemplate2);
		for (final BundleTemplateModel child : rootTemplate1.getChildTemplates())
		{
			when(Boolean.valueOf(bundleCommerceCartService.checkIsComponentEditable(masterCart, child, 1))).thenReturn(Boolean.TRUE);
		}
		for (final BundleTemplateModel child : rootTemplate2.getChildTemplates())
		{
			when(Boolean.valueOf(bundleCommerceCartService.checkIsComponentEditable(masterCart, child, 2))).thenReturn(Boolean.TRUE);
		}
		final Collection<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		final BundleTemplateData root1 = new BundleTemplateData();
		root1.setId("Type1");
		final BundleTemplateData root2 = new BundleTemplateData();
		root2.setId("Type2");
		orderEntries.add(createOrderEntry(1, "Type1child1", root1));
		orderEntries.add(createOrderEntry(2, "Type2child1", root2));
		orderEntries.add(createOrderEntry(0, null, null));
		orderEntries.add(createOrderEntry(0, null, null));
		//Multi cart looks like this now :
		//@formatter:off
		//	  
		//	  - master cart (pay now):
		//	    - 1 Type1 (bundle 1) is editable true
		//     - 2 Type2 bundle 2) is editable false
		//    - Standalone bundle 0
		//    - Standalone bundle 0
		//@formatter:on
		final Collection<OrderEntryData> modifiedOrderEntries = bundlingCartPopulator.addEmptyBundleComponents(orderEntries,
				masterCart);

		Assert.assertEquals(12, modifiedOrderEntries.size());
		final Iterator<OrderEntryData> entryIterator = modifiedOrderEntries.iterator();
		OrderEntryData prev = entryIterator.next();
		for (int i = 1; i < modifiedOrderEntries.size() - 2; i++)
		{
			final OrderEntryData cur = entryIterator.next();
			Assert.assertTrue(cur.getComponent().getId().compareTo(prev.getComponent().getId()) > 0);
			prev = cur;
		}

	}

	@Test
	public void testFindFirstInvalidComponent()
	{
		final CartData target = new CartData();
		target.setEntries(createInvalidEntries());
		bundlingCartPopulator.setFirstIncompleteComponent(target);
		final Map<Integer, BundleTemplateData> incompleteBundleMap = target.getFirstIncompleteBundleComponentsMap();
		Assert.assertEquals(1, incompleteBundleMap.size());

		Assert.assertEquals("MOCK_B1", incompleteBundleMap.get(Integer.valueOf(1)).getId());
	}

	@Test
	public void testFindFirstInvalidComponentEdgeCase()
	{
		final CartData target = new CartData();
		target.setEntries(createValidEntries());
		bundlingCartPopulator.setFirstIncompleteComponent(target);
		Assert.assertTrue(target.getFirstIncompleteBundleComponentsMap().isEmpty());
	}

	@Test
	public void findAllFirsts()
	{
		final CartData target = new CartData();
		target.setEntries(createInvalidEntries2());
		bundlingCartPopulator.setFirstIncompleteComponent(target);
		Assert.assertEquals(1, target.getFirstIncompleteBundleComponentsMap().size());
	}

	@Test
	public void testNonEditableShouldNotGetAdded()
	{
		final BundleTemplateModel rootTemplate = createBundleTemplates("Type1");
		when(bundleTemplateService.getBundleTemplateForCode("Type1", null)).thenReturn(rootTemplate);
		for (int i = 1; i < rootTemplate.getChildTemplates().size(); i++)
		{
			when(
					Boolean.valueOf(bundleCommerceCartService.checkIsComponentEditable(masterCart, rootTemplate.getChildTemplates()
							.get(i), 1))).thenReturn(Boolean.TRUE);
		}
		when(
				Boolean.valueOf(bundleCommerceCartService.checkIsComponentEditable(masterCart, rootTemplate.getChildTemplates()
						.get(0), 1))).thenReturn(Boolean.FALSE);

		final Collection<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		final BundleTemplateData root1 = new BundleTemplateData();
		root1.setId("Type1");

		orderEntries.add(createOrderEntry(1, "Type1child1", root1));
		final Collection<OrderEntryData> modifiedOrderEntries = bundlingCartPopulator.addEmptyBundleComponents(orderEntries,
				masterCart);

		Assert.assertEquals(4, modifiedOrderEntries.size());
	}

	private List<OrderEntryData> createInvalidEntries()
	{
		final List<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		final OrderEntryData first = new OrderEntryData();
		first.setValid(true);

		final OrderEntryData second = new OrderEntryData();
		second.setValid(false);
		second.setBundleNo(1);
		final BundleTemplateData bundleTemplate = new BundleTemplateData();
		bundleTemplate.setId("MOCK_B1");
		second.setComponent(bundleTemplate);

		final OrderEntryData third = new OrderEntryData();
		third.setValid(false);
		third.setBundleNo(1);
		final BundleTemplateData bundleTemplate2 = new BundleTemplateData();
		bundleTemplate2.setId("MOCK_B2");

		orderEntries.add(first);
		orderEntries.add(second);
		orderEntries.add(third);
		return orderEntries;
	}

	private List<OrderEntryData> createInvalidEntries2()
	{
		final List<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		final OrderEntryData first = new OrderEntryData();
		first.setValid(true);

		final OrderEntryData second = new OrderEntryData();
		second.setValid(false);
		second.setBundleNo(1);
		final BundleTemplateData bundleTemplate = new BundleTemplateData();
		bundleTemplate.setId("MOCK_B1");
		second.setComponent(bundleTemplate);

		final OrderEntryData third = new OrderEntryData();
		third.setValid(false);
		third.setBundleNo(2);
		final BundleTemplateData bundleTemplate2 = new BundleTemplateData();
		bundleTemplate2.setId("MOCK_B2");
		third.setComponent(bundleTemplate2);

		orderEntries.add(first);
		orderEntries.add(second);
		orderEntries.add(third);
		return orderEntries;
	}

	private List<OrderEntryData> createValidEntries()
	{
		final List<OrderEntryData> orderEntries = new ArrayList<OrderEntryData>();
		final OrderEntryData first = new OrderEntryData();
		first.setValid(true);

		final OrderEntryData third = new OrderEntryData();
		third.setValid(true);

		orderEntries.add(first);
		orderEntries.add(third);
		return orderEntries;
	}

	private OrderEntryData createOrderEntry(final int bundleNo, final String bundleId, final BundleTemplateData rootBundleTemplate)
	{

		final OrderEntryData orderEntry = new OrderEntryData();
		orderEntry.setBundleNo(bundleNo);
		if (bundleNo > 0)
		{
			final BundleTemplateData bundleTemplateData = new BundleTemplateData();
			bundleTemplateData.setId(bundleId);
			orderEntry.setComponent(bundleTemplateData);
			orderEntry.setRootBundleTemplate(rootBundleTemplate);
		}


		return orderEntry;
	}

	private BundleTemplateModel createBundleTemplates(final String rootId)
	{

		final BundleTemplateModel root = new BundleTemplateModel();
		root.setId(rootId);
		final BundleTemplateData rootBTData = new BundleTemplateData();
		rootBTData.setId(rootId);
		when(converter.convert(root)).thenReturn(rootBTData);
		final List<BundleTemplateModel> childTemplates = new ArrayList<BundleTemplateModel>();
		for (int i = 0; i < 5; i++)
		{
			final List<ProductModel> products = new ArrayList<ProductModel>();
			products.add(new ProductModel());
			final BundleTemplateModel childTemplate = new BundleTemplateModel();
			final String childId = rootId + "child" + i;
			childTemplate.setProducts(products);
			childTemplate.setId(childId);
			childTemplate.setParentTemplate(root);
			childTemplates.add(childTemplate);
			final BundleTemplateData childData = new BundleTemplateData();
			childData.setId(childId);
			when(converter.convert(childTemplate)).thenReturn(childData);
		}
		root.setChildTemplates(childTemplates);
		return root;
	}

}
