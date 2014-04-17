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
package com.epam.cme.core.setup;

import de.hybris.platform.acceleratorservices.setup.AbstractSystemSetup;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateStatusModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.validation.services.ValidationService;
import com.epam.cme.core.constants.CmeCoreConstants;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * This class provides hooks into the system's initialization and update processes.
 * 
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = CmeCoreConstants.EXTENSIONNAME)
public class CoreSystemSetup extends AbstractSystemSetup {
    public static final String IMPORT_SITES = "importSites";
    public static final String IMPORT_SYNC_PRODUCTS = "syncProducts";
    public static final String IMPORT_SYNC_CONTENT = "syncContent";
    public static final String IMPORT_COCKPIT_COMPONENTS = "cockpitComponents";
    public static final String IMPORT_ACCESS_RIGHTS = "accessRights";
    public static final String ACTIVATE_SOLR_CRON_JOBS = "activateSolrCronJobs";

    public static final String TELCO = "telco";

    private TypeService typeService;
    private ModelService modelService;
    private UserService userService;

    /**
     * This method will be called by system creator during initialization and system update. Be sure
     * that this method can be called repeatedly.
     * 
     * @param context
     *            the context provides the selected parameters and values
     */
    @SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
    public void createEssentialData(final SystemSetupContext context) {
        importImpexFile(context, "/cmecore/import/common/essential-data.impex");
        importImpexFile(context, "/cmecore/import/common/countries.impex");
        importImpexFile(context, "/cmecore/import/common/delivery-modes.impex");

        importImpexFile(context, "/cmecore/import/common/themes.impex");
        importImpexFile(context, "/cmecore/import/common/user-groups.impex");

        // add SearchRestriction for customergroup on BundleTemplate/catalogVersion
        final ComposedTypeModel restrictedType = typeService.getComposedTypeForClass(BundleTemplateModel.class);
        final PrincipalModel principal = userService.getUserGroupForUID("customergroup");
        final SearchRestrictionModel searchRestriction = modelService.create(SearchRestrictionModel.class);
        searchRestriction.setCode("Frontend_BundleTemplate");
        searchRestriction.setActive(Boolean.TRUE);
        searchRestriction.setQuery("{catalogVersion} IN ( ?session.catalogversions ) OR  {catalogVersion} IS NULL");
        searchRestriction.setRestrictedType(restrictedType);
        searchRestriction.setPrincipal(principal);
        searchRestriction.setGenerate(Boolean.TRUE);
        try {
            modelService.save(searchRestriction);
        } catch (final ModelSavingException e) {
            // if the function is called repeatedly the Search restriction already exists
            logInfo(context,
                    "Cannot add SearchRestriction for customergroup on BundleTemplate/catalogVersion as it already exists");
        }
    }

    /**
     * Generates the Dropdown and Multi-select boxes for the project data import
     */
    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

        params.add(createBooleanSystemSetupParameter(IMPORT_SITES, "Import Sites", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_PRODUCTS, "Sync Products", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CONTENT, "Sync Content", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_COCKPIT_COMPONENTS, "Import Cockpit Components", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_ACCESS_RIGHTS, "Import Users & Groups", true));
        params.add(createBooleanSystemSetupParameter(ACTIVATE_SOLR_CRON_JOBS, "Activate Solr Cron Jobs", false));

        return params;
    }

    /**
     * This method will be called during the system initialization.
     * 
     * @param context
     *            the context provides the selected parameters and values
     */
    @SystemSetup(type = Type.PROJECT, process = Process.ALL)
    public void createProjectData(final SystemSetupContext context) {
        final boolean importSites = getBooleanSystemSetupParameter(context, IMPORT_SITES);
        final boolean syncProducts = getBooleanSystemSetupParameter(context, IMPORT_SYNC_PRODUCTS);
        final boolean syncContent = getBooleanSystemSetupParameter(context, IMPORT_SYNC_CONTENT);
        final boolean importAccessRights = getBooleanSystemSetupParameter(context, IMPORT_ACCESS_RIGHTS);

        if (importSites) {
            importProductCatalog(context, TELCO, syncProducts);

            importContentCatalog(context, TELCO, syncContent);

            importStore(context, TELCO);

            createAndActivateSolrIndex(context, TELCO);

            ((ValidationService) Registry.getApplicationContext().getBean("validationService"))
                    .reloadValidationEngine();
        }

        final List<String> extensionNames = Registry.getCurrentTenant().getTenantSpecificExtensionNames();

        if (importAccessRights && extensionNames.contains("cmscockpit")) {
            importImpexFile(context, "/cmecore/import/cockpits/cmscockpit/cmscockpit-access-rights.impex");
        }

        if (importAccessRights && extensionNames.contains("productcockpit")) {
            importImpexFile(context, "/cmecore/import/cockpits/productcockpit/productcockpit-constraints.impex");
        }

        if (importAccessRights && extensionNames.contains("cscockpit")) {
            importImpexFile(context, "/cmecore/import/cockpits/cscockpit/cscockpit-users.impex");
            importImpexFile(context, "/cmecore/import/cockpits/cscockpit/cscockpit-access-rights.impex");
        }

        if (extensionNames.contains("mcc")) {
            importImpexFile(context, "/cmecore/import/common/mcc-sites-links.impex");
        }
    }

    protected void importProductCatalog(final SystemSetupContext context, final String catalogName, final boolean sync) {
        logInfo(context, "Begin importing catalog [" + catalogName + "]");

        importImpexFile(context, "/cmecore/import/productCatalogs/" + catalogName + "ProductCatalog/catalog.impex",
                true);

        createProductCatalogSyncJob(context, catalogName + "ProductCatalog");

        // add types BundleTemplate and BundleTemplateStatus as root types to the ProductCatalog
        // synchronization job
        final List<SyncItemJobModel> syncItemJobs = getCatalogSyncJob(catalogName + "ProductCatalog");
        for (final SyncItemJobModel syncItemJob : syncItemJobs) {
            final List<ComposedTypeModel> rootTypes = new ArrayList<ComposedTypeModel>(syncItemJob.getRootTypes());
            final ComposedTypeModel bundleTemplateStatus = typeService
                    .getComposedTypeForClass(BundleTemplateStatusModel.class);
            final ComposedTypeModel bundleTemplate = typeService.getComposedTypeForClass(BundleTemplateModel.class);
            rootTypes.add(0, bundleTemplateStatus);
            rootTypes.add(0, bundleTemplate);
            syncItemJob.setRootTypes(rootTypes);
            modelService.save(syncItemJob);
        }

        if (sync) {
            executeCatalogSyncJob(context, catalogName + "ProductCatalog");
        }

        logInfo(context, "Done importing catalog [" + catalogName + "]");
    }

    private void createAndActivateSolrIndex(final SystemSetupContext context, final String catalogName) {
        logInfo(context, "Begin SOLR index setup [" + catalogName + "]");

        importImpexFile(context, "/cmecore/import/productCatalogs/" + catalogName + "ProductCatalog/solr.impex", false);

        createSolrIndexerCronJobs(catalogName + "Index");

        importImpexFile(context, "/cmecore/import/productCatalogs/" + catalogName + "ProductCatalog/solrtrigger.impex");

        if (getBooleanSystemSetupParameter(context, ACTIVATE_SOLR_CRON_JOBS)) {
            executeSolrIndexerCronJob(catalogName + "Index", true);
            activateSolrIndexerCronJobs(catalogName + "Index");
        }

        logInfo(context, "Done SOLR index setup [" + catalogName + "]");
    }

    protected void importContentCatalog(final SystemSetupContext context, final String catalogName, final boolean sync) {
        logInfo(context, "Begin importing catalog [" + catalogName + "]");

        importImpexFile(context, "/cmecore/import/contentCatalogs/" + catalogName + "ContentCatalog/catalog.impex",
                true);
        importImpexFile(context, "/cmecore/import/contentCatalogs/" + catalogName + "ContentCatalog/cms-content.impex",
                false);
        importImpexFile(context, "/cmecore/import/contentCatalogs/" + catalogName
                + "ContentCatalog/email-content.impex", false);

        createContentCatalogSyncJob(context, catalogName + "ContentCatalog");

        if (sync) {
            executeCatalogSyncJob(context, catalogName + "ContentCatalog");
        }

        logInfo(context, "Done importing catalog [" + catalogName + "]");
    }

    protected void importStore(final SystemSetupContext context, final String storeName) {
        logInfo(context, "Begin importing store [" + storeName + "]");

        importImpexFile(context, "/cmecore/import/stores/" + storeName + "/store.impex");
        importImpexFile(context, "/cmecore/import/stores/" + storeName + "/site.impex");

        logInfo(context, "Done importing store [" + storeName + "]");
    }

    /**
     * @return the typeService
     */
    protected TypeService getTypeService() {
        return typeService;
    }

    @Required
    public void setTypeService(final TypeService typeService) {
        this.typeService = typeService;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    protected UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }
}
