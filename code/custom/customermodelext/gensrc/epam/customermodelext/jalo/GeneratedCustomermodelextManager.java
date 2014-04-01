/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Apr 1, 2014 5:27:54 PM                      ---
 * ----------------------------------------------------------------
 */
package epam.customermodelext.jalo;

import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.link.Link;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.util.Utilities;
import epam.customermodelext.constants.CustomermodelextConstants;
import epam.customermodelext.jalo.Organization;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Generated class for type <code>CustomermodelextManager</code>.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedCustomermodelextManager extends Extension
{
	/** Relation ordering override parameter constants for CustomerOrganizationRelation from ((customermodelext))*/
	protected static String CUSTOMERORGANIZATIONRELATION_SRC_ORDERED = "relation.CustomerOrganizationRelation.source.ordered";
	protected static String CUSTOMERORGANIZATIONRELATION_TGT_ORDERED = "relation.CustomerOrganizationRelation.target.ordered";
	/** Relation disable markmodifed parameter constants for CustomerOrganizationRelation from ((customermodelext))*/
	protected static String CUSTOMERORGANIZATIONRELATION_MARKMODIFIED = "relation.CustomerOrganizationRelation.markmodified";
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.attemptCount</code> attribute.
	 * @return the attemptCount - Login attempts counter
	 */
	public Integer getAttemptCount(final SessionContext ctx, final Customer item)
	{
		return (Integer)item.getProperty( ctx, CustomermodelextConstants.Attributes.Customer.ATTEMPTCOUNT);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.attemptCount</code> attribute.
	 * @return the attemptCount - Login attempts counter
	 */
	public Integer getAttemptCount(final Customer item)
	{
		return getAttemptCount( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.attemptCount</code> attribute. 
	 * @return the attemptCount - Login attempts counter
	 */
	public int getAttemptCountAsPrimitive(final SessionContext ctx, final Customer item)
	{
		Integer value = getAttemptCount( ctx,item );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.attemptCount</code> attribute. 
	 * @return the attemptCount - Login attempts counter
	 */
	public int getAttemptCountAsPrimitive(final Customer item)
	{
		return getAttemptCountAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.attemptCount</code> attribute. 
	 * @param value the attemptCount - Login attempts counter
	 */
	public void setAttemptCount(final SessionContext ctx, final Customer item, final Integer value)
	{
		item.setProperty(ctx, CustomermodelextConstants.Attributes.Customer.ATTEMPTCOUNT,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.attemptCount</code> attribute. 
	 * @param value the attemptCount - Login attempts counter
	 */
	public void setAttemptCount(final Customer item, final Integer value)
	{
		setAttemptCount( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.attemptCount</code> attribute. 
	 * @param value the attemptCount - Login attempts counter
	 */
	public void setAttemptCount(final SessionContext ctx, final Customer item, final int value)
	{
		setAttemptCount( ctx, item, Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.attemptCount</code> attribute. 
	 * @param value the attemptCount - Login attempts counter
	 */
	public void setAttemptCount(final Customer item, final int value)
	{
		setAttemptCount( getSession().getSessionContext(), item, value );
	}
	
	public Organization createOrganization(final SessionContext ctx, final Map attributeValues)
	{
		try
		{
			ComposedType type = getTenant().getJaloConnection().getTypeManager().getComposedType( CustomermodelextConstants.TC.ORGANIZATION );
			return (Organization)type.newInstance( ctx, attributeValues );
		}
		catch( JaloGenericCreationException e)
		{
			final Throwable cause = e.getCause();
			throw (cause instanceof RuntimeException ?
			(RuntimeException)cause
			:
			new JaloSystemException( cause, cause.getMessage(), e.getErrorCode() ) );
		}
		catch( JaloBusinessException e )
		{
			throw new JaloSystemException( e ,"error creating Organization : "+e.getMessage(), 0 );
		}
	}
	
	public Organization createOrganization(final Map attributeValues)
	{
		return createOrganization( getSession().getSessionContext(), attributeValues );
	}
	
	@Override
	public String getName()
	{
		return CustomermodelextConstants.EXTENSIONNAME;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.media</code> attribute.
	 * @return the media
	 */
	public Media getMedia(final SessionContext ctx, final Customer item)
	{
		return (Media)item.getProperty( ctx, CustomermodelextConstants.Attributes.Customer.MEDIA);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.media</code> attribute.
	 * @return the media
	 */
	public Media getMedia(final Customer item)
	{
		return getMedia( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.media</code> attribute. 
	 * @param value the media
	 */
	public void setMedia(final SessionContext ctx, final Customer item, final Media value)
	{
		item.setProperty(ctx, CustomermodelextConstants.Attributes.Customer.MEDIA,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.media</code> attribute. 
	 * @param value the media
	 */
	public void setMedia(final Customer item, final Media value)
	{
		setMedia( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.organizations</code> attribute.
	 * @return the organizations
	 */
	public Collection<Organization> getOrganizations(final SessionContext ctx, final Customer item)
	{
		final List<Organization> items = item.getLinkedItems( 
			ctx,
			true,
			CustomermodelextConstants.Relations.CUSTOMERORGANIZATIONRELATION,
			null,
			Utilities.getRelationOrderingOverride(CUSTOMERORGANIZATIONRELATION_SRC_ORDERED, true),
			Utilities.getRelationOrderingOverride(CUSTOMERORGANIZATIONRELATION_TGT_ORDERED, true)
		);
		return items;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.organizations</code> attribute.
	 * @return the organizations
	 */
	public Collection<Organization> getOrganizations(final Customer item)
	{
		return getOrganizations( getSession().getSessionContext(), item );
	}
	
	public long getOrganizationsCount(final SessionContext ctx, final Customer item)
	{
		return item.getLinkedItemsCount(
			ctx,
			true,
			CustomermodelextConstants.Relations.CUSTOMERORGANIZATIONRELATION,
			null
		);
	}
	
	public long getOrganizationsCount(final Customer item)
	{
		return getOrganizationsCount( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.organizations</code> attribute. 
	 * @param value the organizations
	 */
	public void setOrganizations(final SessionContext ctx, final Customer item, final Collection<Organization> value)
	{
		item.setLinkedItems( 
			ctx,
			true,
			CustomermodelextConstants.Relations.CUSTOMERORGANIZATIONRELATION,
			null,
			value,
			Utilities.getRelationOrderingOverride(CUSTOMERORGANIZATIONRELATION_SRC_ORDERED, true),
			Utilities.getRelationOrderingOverride(CUSTOMERORGANIZATIONRELATION_TGT_ORDERED, true),
			Utilities.getMarkModifiedOverride(CUSTOMERORGANIZATIONRELATION_MARKMODIFIED)
		);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.organizations</code> attribute. 
	 * @param value the organizations
	 */
	public void setOrganizations(final Customer item, final Collection<Organization> value)
	{
		setOrganizations( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Adds <code>value</code> to organizations. 
	 * @param value the item to add to organizations
	 */
	public void addToOrganizations(final SessionContext ctx, final Customer item, final Organization value)
	{
		item.addLinkedItems( 
			ctx,
			true,
			CustomermodelextConstants.Relations.CUSTOMERORGANIZATIONRELATION,
			null,
			Collections.singletonList(value),
			Utilities.getRelationOrderingOverride(CUSTOMERORGANIZATIONRELATION_SRC_ORDERED, true),
			Utilities.getRelationOrderingOverride(CUSTOMERORGANIZATIONRELATION_TGT_ORDERED, true),
			Utilities.getMarkModifiedOverride(CUSTOMERORGANIZATIONRELATION_MARKMODIFIED)
		);
	}
	
	/**
	 * <i>Generated method</i> - Adds <code>value</code> to organizations. 
	 * @param value the item to add to organizations
	 */
	public void addToOrganizations(final Customer item, final Organization value)
	{
		addToOrganizations( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Removes <code>value</code> from organizations. 
	 * @param value the item to remove from organizations
	 */
	public void removeFromOrganizations(final SessionContext ctx, final Customer item, final Organization value)
	{
		item.removeLinkedItems( 
			ctx,
			true,
			CustomermodelextConstants.Relations.CUSTOMERORGANIZATIONRELATION,
			null,
			Collections.singletonList(value),
			Utilities.getRelationOrderingOverride(CUSTOMERORGANIZATIONRELATION_SRC_ORDERED, true),
			Utilities.getRelationOrderingOverride(CUSTOMERORGANIZATIONRELATION_TGT_ORDERED, true),
			Utilities.getMarkModifiedOverride(CUSTOMERORGANIZATIONRELATION_MARKMODIFIED)
		);
	}
	
	/**
	 * <i>Generated method</i> - Removes <code>value</code> from organizations. 
	 * @param value the item to remove from organizations
	 */
	public void removeFromOrganizations(final Customer item, final Organization value)
	{
		removeFromOrganizations( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.status</code> attribute.
	 * @return the status - Account lock state
	 */
	public Boolean isStatus(final SessionContext ctx, final Customer item)
	{
		return (Boolean)item.getProperty( ctx, CustomermodelextConstants.Attributes.Customer.STATUS);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.status</code> attribute.
	 * @return the status - Account lock state
	 */
	public Boolean isStatus(final Customer item)
	{
		return isStatus( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.status</code> attribute. 
	 * @return the status - Account lock state
	 */
	public boolean isStatusAsPrimitive(final SessionContext ctx, final Customer item)
	{
		Boolean value = isStatus( ctx,item );
		return value != null ? value.booleanValue() : false;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Customer.status</code> attribute. 
	 * @return the status - Account lock state
	 */
	public boolean isStatusAsPrimitive(final Customer item)
	{
		return isStatusAsPrimitive( getSession().getSessionContext(), item );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.status</code> attribute. 
	 * @param value the status - Account lock state
	 */
	public void setStatus(final SessionContext ctx, final Customer item, final Boolean value)
	{
		item.setProperty(ctx, CustomermodelextConstants.Attributes.Customer.STATUS,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.status</code> attribute. 
	 * @param value the status - Account lock state
	 */
	public void setStatus(final Customer item, final Boolean value)
	{
		setStatus( getSession().getSessionContext(), item, value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.status</code> attribute. 
	 * @param value the status - Account lock state
	 */
	public void setStatus(final SessionContext ctx, final Customer item, final boolean value)
	{
		setStatus( ctx, item, Boolean.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Customer.status</code> attribute. 
	 * @param value the status - Account lock state
	 */
	public void setStatus(final Customer item, final boolean value)
	{
		setStatus( getSession().getSessionContext(), item, value );
	}
	
}
