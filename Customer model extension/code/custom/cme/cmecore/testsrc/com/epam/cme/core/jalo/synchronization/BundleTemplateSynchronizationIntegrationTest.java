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
package com.epam.cme.core.jalo.synchronization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.acceleratorservices.setup.SetupSyncJobService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.configurablebundleservices.enums.BundleTemplateStatusEnum;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateStatusModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test suite for testing the synchronization of {@link BundleTemplateModel}s and
 * dependent objects
 * 
 */
public class BundleTemplateSynchronizationIntegrationTest extends ServicelayerTest {
    private static final Logger LOG = Logger.getLogger(BundleTemplateSynchronizationIntegrationTest.class);

    private static final String CATALOG_ID = "testCatalog";
    private static final String SYNC_ROOT_TEMPLATE = "SyncPackage";

    @Resource
    private UserService userService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private TypeService typeService;

    @Resource
    private SetupSyncJobService setupSyncJobService;

    @Resource
    private ModelService modelService;

    @Resource
    private FlexibleSearchService flexibleSearchService;

    private CatalogVersionModel onlineCatalogVersion;
    private CatalogVersionModel stagedCatalogVersion;

    @Before
    public void setUp() throws Exception {
        // final Create data for tests
        LOG.info("Creating data for BundleTemplateSynchronizationIntegrationTest ..");
        userService.setCurrentUser(userService.getAdminUser());
        final long startTime = System.currentTimeMillis();
        new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);

        // importing test csv
        final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
        LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
        Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
        importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
        Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
        importCsv("/subscriptionservices/test/testSubscriptionCommerceCartService.impex", "utf-8");
        importCsv("/configurablebundleservices/test/testBundleCommerceCartService.impex", "utf-8");
        Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

        onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, CatalogManager.ONLINE_VERSION);
        stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, CatalogManager.OFFLINE_VERSION);

        LOG.info("Finished data for BundleTemplateSynchronizationIntegrationTest "
                + (System.currentTimeMillis() - startTime) + "ms");
        modelService.detachAll();
    }

    @Test
    public void testInitSyncBundleTemplate() {
        // check data before synchronization
        List<BundleTemplateModel> templates = getBundleTemplatesByIdAndCatalogVersion(SYNC_ROOT_TEMPLATE, null);
        assertEquals("", 1, templates.size());
        assertEquals("", stagedCatalogVersion, templates.iterator().next().getCatalogVersion());
        checkSyncTemplate(templates.iterator().next(), stagedCatalogVersion);

        // create/run synchronization job
        createSyncJob(CATALOG_ID);
        runSyncJob(CATALOG_ID);

        templates = getBundleTemplatesByIdAndCatalogVersion(SYNC_ROOT_TEMPLATE, null);
        assertEquals("", 2, templates.size());

        // check staged data
        templates = getBundleTemplatesByIdAndCatalogVersion(SYNC_ROOT_TEMPLATE, stagedCatalogVersion);
        assertEquals("", 1, templates.size());
        checkSyncTemplate(templates.iterator().next(), stagedCatalogVersion);

        // check online data
        templates = getBundleTemplatesByIdAndCatalogVersion(SYNC_ROOT_TEMPLATE, onlineCatalogVersion);
        assertEquals("", 1, templates.size());
        checkSyncTemplate(templates.iterator().next(), onlineCatalogVersion);
    }

    @Test
    public void testSyncBundleTemplateAfterUpdates() {
        // create/run synchronization job
        createSyncJob(CATALOG_ID);
        runSyncJob(CATALOG_ID);

        List<BundleTemplateModel> templates = getBundleTemplatesByIdAndCatalogVersion("SyncDeviceSelection",
                stagedCatalogVersion);
        assertEquals("", 1, templates.size());
        final BundleTemplateModel bundleTemplate = templates.iterator().next();
        final BundleTemplateModel rootTemplateStaged = bundleTemplate.getParentTemplate();

        // update status
        rootTemplateStaged.getStatus().setStatus(BundleTemplateStatusEnum.APPROVED);
        modelService.save(bundleTemplate.getStatus());
        modelService.refresh(bundleTemplate);

        for (final BundleTemplateModel childTemplate : rootTemplateStaged.getChildTemplates()) {
            assertEquals("", BundleTemplateStatusEnum.APPROVED, childTemplate.getStatus().getStatus());
        }

        // update price
        for (final ChangeProductPriceBundleRuleModel priceRule : bundleTemplate.getChangeProductPriceBundleRules()) {
            assertEquals("", 59, priceRule.getPrice().intValue());
            priceRule.setPrice(BigDecimal.valueOf(69));
            modelService.save(priceRule);
            assertEquals("", 69, priceRule.getPrice().intValue());
        }

        templates = getBundleTemplatesByIdAndCatalogVersion("SyncDeviceSelection", onlineCatalogVersion);
        assertEquals("", 1, templates.size());
        BundleTemplateModel rootTemplateOnline = templates.iterator().next().getParentTemplate();
        assertEquals("", BundleTemplateStatusEnum.UNAPPROVED, rootTemplateOnline.getStatus().getStatus());

        for (final BundleTemplateModel childTemplate : rootTemplateOnline.getChildTemplates()) {
            assertEquals("", BundleTemplateStatusEnum.UNAPPROVED, childTemplate.getStatus().getStatus());
        }

        for (final ChangeProductPriceBundleRuleModel priceRule : templates.iterator().next()
                .getChangeProductPriceBundleRules()) {
            assertEquals("", 59, priceRule.getPrice().intValue());
        }

        // run synchronization job again
        runSyncJob(CATALOG_ID);

        templates = getBundleTemplatesByIdAndCatalogVersion("SyncDeviceSelection", onlineCatalogVersion);
        assertEquals("", 1, templates.size());
        final BundleTemplateModel bundleTemplateOnline = templates.iterator().next();
        rootTemplateOnline = bundleTemplateOnline.getParentTemplate();
        assertEquals("", BundleTemplateStatusEnum.APPROVED, rootTemplateOnline.getStatus().getStatus());

        for (final BundleTemplateModel childTemplate : rootTemplateOnline.getChildTemplates()) {
            assertEquals("", BundleTemplateStatusEnum.APPROVED, childTemplate.getStatus().getStatus());
        }

        for (final ChangeProductPriceBundleRuleModel priceRule : bundleTemplateOnline
                .getChangeProductPriceBundleRules()) {
            assertEquals("", 69, priceRule.getPrice().intValue());
        }
    }

    private void checkSyncTemplate(final BundleTemplateModel rootTemplate, final CatalogVersionModel catalogVersion) {
        assertEquals("", 3, rootTemplate.getChildTemplates().size());
        for (final BundleTemplateModel bundleTemplate : rootTemplate.getChildTemplates()) {
            assertEquals("", catalogVersion, bundleTemplate.getCatalogVersion());
            assertNotNull("", bundleTemplate.getBundleSelectionCriteria());
            assertEquals("", catalogVersion, bundleTemplate.getBundleSelectionCriteria().getCatalogVersion());
            assertNotNull("", bundleTemplate.getStatus());
            assertEquals("", BundleTemplateStatusEnum.UNAPPROVED, bundleTemplate.getStatus().getStatus());

            assertEquals("", 1, bundleTemplate.getProducts().size());
            for (final ProductModel product : bundleTemplate.getProducts()) {
                assertEquals("", catalogVersion, product.getCatalogVersion());
            }

            if ("SyncDeviceSelection".equals(bundleTemplate.getId())) {
                assertEquals("", 2, bundleTemplate.getChangeProductPriceBundleRules().size());
                for (final ChangeProductPriceBundleRuleModel priceRule : bundleTemplate
                        .getChangeProductPriceBundleRules()) {
                    assertEquals("", catalogVersion, priceRule.getCatalogVersion());

                    assertTrue("", priceRule.getConditionalProducts().size() > 0);
                    for (final ProductModel conditionalProduct : priceRule.getConditionalProducts()) {
                        assertEquals("", catalogVersion, conditionalProduct.getCatalogVersion());
                    }

                    assertEquals("", 1, priceRule.getTargetProducts().size());
                    for (final ProductModel targetProduct : priceRule.getTargetProducts()) {
                        assertEquals("", catalogVersion, targetProduct.getCatalogVersion());
                    }
                }
            }
        }
    }

    private void createSyncJob(final String catalogId) {
        // create a synchronization job
        setupSyncJobService.createProductCatalogSyncJob(catalogId);

        // add types BundleTemplate and BundleTemplateStatus as root types to the synchronization
        // job
        final List<SyncItemJobModel> syncItemJobs = catalogVersionService.getCatalogVersion(catalogId,
                CatalogManager.OFFLINE_VERSION).getSynchronizations();
        for (final SyncItemJobModel syncItemJob : syncItemJobs) {
            final List<ComposedTypeModel> rootTypes = new ArrayList<ComposedTypeModel>(syncItemJob.getRootTypes());
            final ComposedTypeModel bundleTemplate = typeService.getComposedTypeForClass(BundleTemplateModel.class);
            final ComposedTypeModel bundleTemplateStatus = typeService
                    .getComposedTypeForClass(BundleTemplateStatusModel.class);
            rootTypes.add(0, bundleTemplateStatus);
            rootTypes.add(0, bundleTemplate);
            syncItemJob.setRootTypes(rootTypes);
            modelService.save(syncItemJob);
        }
    }

    private void runSyncJob(final String catalogId) {
        // start the synchronization job
        final PerformResult res = setupSyncJobService.executeCatalogSyncJob(catalogId);
        final CronJobStatus cronJobStatus = res.getStatus();
        assertEquals("", CronJobStatus.FINISHED, cronJobStatus);
        final CronJobResult cronJobResult = res.getResult();
        assertEquals("", CronJobResult.SUCCESS, cronJobResult);
    }

    private List<BundleTemplateModel> getBundleTemplatesByIdAndCatalogVersion(final String bundleId,
            final CatalogVersionModel catalogVersion) {
        final BundleTemplateModel exampleModel = new BundleTemplateModel();
        exampleModel.setId(bundleId);
        if (catalogVersion != null) {
            exampleModel.setCatalogVersion(catalogVersion);
        }

        return flexibleSearchService.getModelsByExample(exampleModel);
    }
}
