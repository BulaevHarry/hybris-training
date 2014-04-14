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
package com.epam.cme.core.validation;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.validation.constants.ValidationConstants;
import de.hybris.platform.validation.daos.ConstraintDao;
import de.hybris.platform.validation.exceptions.ConfigurableHybrisConstraintViolation;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.extractor.ConstraintsExtractor;
import de.hybris.platform.validation.model.constraints.AbstractConstraintModel;
import de.hybris.platform.validation.model.constraints.ConstraintGroupModel;
import de.hybris.platform.validation.services.ValidationService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.engine.ConfigurationImpl;
import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * Validation service which by default use HybrisHibernateValidator. Service exposes two main features: 1) validation
 * methods 2) validation engine reloading
 * 
 **/
public class CustomValidationService extends AbstractBusinessService implements ValidationService
{
	private static final Logger LOG = Logger.getLogger(CustomValidationService.class);
	private static final String SESSIONCONSTRAINTGROUPS = "session.active.constraintgroups";

	// volatile due to being lazily created
	private volatile Validator _validator; //NOPMD
	// guarded by volatile 'validator' (piggy-back)
	private long lastReloadOfValidationEngine; //NOPMD
	// guarded by volatile 'validator' (piggy-back)
	private Configuration<?> _config; //NOPMD

	// these members are guarded by Spring
	private ConstraintsExtractor constraintsExtractor;
	private ConstraintDao constraintDao;
	private FlexibleSearchService flexibleSearchService;

	private Validator getValidator()
	{
		Validator ret = _validator;
		if (ret == null)
		{
			synchronized (this)
			{
				ret = _validator;
				if (ret == null)
				{
					setValidator(ret = createCustomizedValidator());
				}
			}
		}
		return ret;
	}

	private void setValidator(final Validator newOne)
	{
		this.lastReloadOfValidationEngine = System.currentTimeMillis() - 1;
		// since we write volatile validator at *last* the date is also thread-safe as
		// long as validator is always read *first*
		this._validator = newOne;
	}

	private Configuration createConfiguration()
	{
		final Configuration<?> config = Validation.byDefaultProvider().configure();
		config.ignoreXmlConfiguration();
		config.traversableResolver(new NonJPATraversableResolver());
		config.messageInterpolator(new CustomMessageInterpolator());
		return config;
	}

	private Validator createStandardValidator()
	{
		return getOrCreateConfiguration(true).buildValidatorFactory().getValidator();
	}

	protected Configuration getOrCreateConfiguration(final boolean replace)
	{
		// create new configuration on demand
		if (_config == null || replace)
		{
			// this is thread-safe since volatile _validator is written *afterwards* and
			// each future call to this method involved reading _validator *before*
			_config = createConfiguration();
		}
		return _config;
	}

	private Validator createCustomizedValidator()
	{
		Validator ret = null;

		final Configuration cfg = getOrCreateConfiguration(false);
		final InputStream input = appendXMLMapping(cfg);
		try
		{
			ret = cfg.buildValidatorFactory().getValidator();
		}
		catch (final ValidationException e)
		{
			LOG.error("Problem occured with loading mappings stream: " + e.getMessage(), e);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Unparsable XML stream [\n%s\n]", readXMLSource()));
			}
			else
			{
				LOG.error("Please set log level to DEBUG for more details!");
			}

			if (cfg instanceof ConfigurationImpl)
			{
				((ConfigurationImpl) cfg).getMappingStreams().remove(input);
				LOG.info("Trying to unload last problematic configuration file and create upon it validator");
				ret = cfg.buildValidatorFactory().getValidator();
			}
		}
		return ret;
	}

	private InputStream appendXMLMapping(final Configuration cfg)
	{
		InputStream input = null;
		try
		{
			input = constraintsExtractor.extractConstraints();
			cfg.addMapping(input);
		}
		finally
		{
			IOUtils.closeQuietly(input);
		}
		return input;
	}

	private String readXMLSource()
	{
		String src = null;
		InputStream stream = null;
		try
		{
			src = IOUtils.toString(stream = constraintsExtractor.extractConstraints());
		}
		catch (final IOException e1)
		{
			LOG.error(e1);
			src = "error getting xml source: " + e1.getMessage();
		}
		finally
		{
			IOUtils.closeQuietly(stream);
		}
		return src;
	}

	private static class NonJPATraversableResolver implements TraversableResolver
	{
		@Override
		public boolean isReachable(final Object traversableObject, final Path.Node traversableProperty,
				final Class<?> rootBeanType, final Path pathToTraversableObject, final ElementType elementType)
		{
			return true;
		}

		@Override
		public boolean isCascadable(final Object traversableObject, final Path.Node traversableProperty,
				final Class<?> rootBeanType, final Path pathToTraversableObject, final ElementType elementType)
		{
			return true;
		}
	}

	/**
	 * 
	 * Just set a flag , lazy with first validation engine will be reloaded.
	 */
	@Override
	public synchronized void reloadValidationEngine()
	{
		_validator = null;
	}

	@Override
	public boolean needReloadOfValidationEngine(final AbstractConstraintModel model)
	{
		validateParameterNotNullStandardMessage("model", model);
		// if validator !=null the lastReloadTime must be set -> no null check neede
		// this is thread-safe
		if (getModelService().isNew(model))
		{
			return false;
		}
		return _validator != null && lastReloadOfValidationEngine < model.getModifiedtime().getTime();
	}

	@Override
	public <T> Set<HybrisConstraintViolation> validate(final T object, final Collection<ConstraintGroupModel> groups)
	{
		return validate(object, convertConstraintGroupModelArray(groups));
	}

	@Override
	public <T> Set<HybrisConstraintViolation> validate(final T object, final Class<?>... groups)
	{
		final Set<ConstraintViolation<T>> resultRaw = getValidator().validate(object, groups);
		final Set<HybrisConstraintViolation> result = new HashSet<HybrisConstraintViolation>(resultRaw.size());
		for (final ConstraintViolation singleViolation : resultRaw)
		{
			final HybrisConstraintViolation viol = wrapConstraint(singleViolation);
			if (viol != null)
			{
				result.add(viol);
			}
		}
		return result;
	}

	@Override
	public <T> Set<HybrisConstraintViolation> validateProperty(final T object, final String propertyName,
			final Collection<ConstraintGroupModel> groups)
	{
		return validateProperty(object, propertyName, convertConstraintGroupModelArray(groups));
	}

	@Override
	public <T> Set<HybrisConstraintViolation> validateProperty(final T object, final String propertyName, final Class<?>... groups)
	{
		final Set<ConstraintViolation<T>> resultRaw = getValidator().validateProperty(object, propertyName, groups);
		final Set<HybrisConstraintViolation> result = new HashSet<HybrisConstraintViolation>(resultRaw.size());
		for (final ConstraintViolation singleViolation : resultRaw)
		{
			final HybrisConstraintViolation viol = wrapConstraint(singleViolation);
			if (viol != null)
			{
				result.add(viol);
			}
		}
		return result;
	}

	@Override
	public <T> Set<HybrisConstraintViolation> validateValue(final Class<T> beanType, final String propertyName,
			final Object value, final Collection<ConstraintGroupModel> groups)
	{
		return validateValue(beanType, propertyName, value, convertConstraintGroupModelArray(groups));
	}

	@Override
	public <T> Set<HybrisConstraintViolation> validateValue(final Class<T> beanType, final String propertyName,
			final Object value, final Class<?>... groups)
	{
		final Set<ConstraintViolation<T>> resultRaw = getValidator().validateValue(beanType, propertyName, value, groups);
		final Set<HybrisConstraintViolation> result = new HashSet<HybrisConstraintViolation>(resultRaw.size());
		for (final ConstraintViolation singleViolation : resultRaw)
		{
			final HybrisConstraintViolation viol = wrapConstraint(singleViolation);
			if (viol != null)
			{
				result.add(viol);
			}
		}
		return result;
	}

	private HybrisConstraintViolation wrapConstraint(final ConstraintViolation violation)
	{
		final ConfigurableHybrisConstraintViolation result = lookupViolation();
		result.setConstraintViolation(violation);
		return result;
	}

	protected ConfigurableHybrisConstraintViolation lookupViolation()
	{
		throw new UnsupportedOperationException("please override DefaultValidationService.lookupViolation() or "
				+ "use <lookup-method name=\"lookupViolation\" bean=\"..\">");
	}

	private Class[] convertConstraintGroupModelArray(final Collection<ConstraintGroupModel> groups)
	{
		final Class[] clgroups = new Class[groups.size()];
		int index = 0;
		for (final ConstraintGroupModel cgm : groups)
		{
			clgroups[index++] = constraintDao.getTargetClass(cgm);
		}
		return clgroups;
	}

	@Override
	public Collection<ConstraintGroupModel> getActiveConstraintGroups()
	{
		final Object groups = getSessionService().getAttribute(SESSIONCONSTRAINTGROUPS);
		return groups == null ? Collections.EMPTY_SET : (Collection<ConstraintGroupModel>) groups;
	}

	@Override
	public void setActiveConstraintGroups(final Collection<ConstraintGroupModel> groups)
	{
		getSessionService().setAttribute(SESSIONCONSTRAINTGROUPS, groups);
	}

	@Override
	public synchronized void unloadValidationEngine()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Detaching all constraints");
		}
		setValidator(createStandardValidator());
	}

	@Deprecated
	@Override
	public Class getTargetClass(final ConstraintGroupModel group)
	{
		return constraintDao.getTargetClass(group);
	}

	@Override
	public ConstraintGroupModel getDefaultConstraintGroup()
	{
		final FlexibleSearchQuery fsq = new FlexibleSearchQuery("SELECT {PK} FROM {ConstraintGroup} WHERE {id}=?id");
		fsq.addQueryParameter("id", ValidationConstants.DEFAULT_CONSTRAINTGROUP_ID);
		fsq.setResultClassList(Arrays.asList(ConstraintGroupModel.class));
		final SearchResult<ConstraintGroupModel> sresult = flexibleSearchService.search(fsq);
		if (sresult.getTotalCount() == 1)
		{
			final ConstraintGroupModel def = sresult.getResult().get(0);
			getModelService().refresh(def);
			getModelService().detach(def);
			return def;
		}
		throw new ModelNotFoundException(
				"Default group was not found! Please update the system for creating this default group (essential data).");
	}

	@Required
	public void setConstraintsExtractor(final ConstraintsExtractor constraintsExtractor)
	{
		this.constraintsExtractor = constraintsExtractor;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Required
	public void setConstraintDao(final ConstraintDao constraintDao)
	{
		this.constraintDao = constraintDao;
	}
}
