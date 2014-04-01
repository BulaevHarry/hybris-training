/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at Apr 1, 2014 6:05:46 PM                      ---
 * ----------------------------------------------------------------
 */
package epam.customermodelext.jalo;

import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.jalo.Item.AttributeMode;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.util.Utilities;
import epam.customermodelext.constants.CustomermodelextConstants;
import epam.customermodelext.jalo.BlockableCustomer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated class for type {@link epam.customermodelext.jalo.Organization Organization}.
 */
@SuppressWarnings({"deprecation","unused","cast","PMD"})
public abstract class GeneratedOrganization extends GenericItem
{
	/** Qualifier of the <code>Organization.phone</code> attribute **/
	public static final String PHONE = "phone";
	/** Qualifier of the <code>Organization.customers</code> attribute **/
	public static final String CUSTOMERS = "customers";
	/** Relation ordering override parameter constants for BlockableCustomerOrganizationRelation from ((customermodelext))*/
	protected static String BLOCKABLECUSTOMERORGANIZATIONRELATION_SRC_ORDERED = "relation.BlockableCustomerOrganizationRelation.source.ordered";
	protected static String BLOCKABLECUSTOMERORGANIZATIONRELATION_TGT_ORDERED = "relation.BlockableCustomerOrganizationRelation.target.ordered";
	/** Relation disable markmodifed parameter constants for BlockableCustomerOrganizationRelation from ((customermodelext))*/
	protected static String BLOCKABLECUSTOMERORGANIZATIONRELATION_MARKMODIFIED = "relation.BlockableCustomerOrganizationRelation.markmodified";
	/** Qualifier of the <code>Organization.name</code> attribute **/
	public static final String NAME = "name";
	/** Qualifier of the <code>Organization.id</code> attribute **/
	public static final String ID = "id";
	protected static final Map<String, AttributeMode> DEFAULT_INITIAL_ATTRIBUTES;
	static
	{
		final Map<String, AttributeMode> tmp = new HashMap<String, AttributeMode>();
		tmp.put(PHONE, AttributeMode.INITIAL);
		tmp.put(NAME, AttributeMode.INITIAL);
		tmp.put(ID, AttributeMode.INITIAL);
		DEFAULT_INITIAL_ATTRIBUTES = Collections.unmodifiableMap(tmp);
	}
	@Override
	protected Map<String, AttributeMode> getDefaultAttributeModes()
	{
		return DEFAULT_INITIAL_ATTRIBUTES;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.customers</code> attribute.
	 * @return the customers
	 */
	public Collection<BlockableCustomer> getCustomers(final SessionContext ctx)
	{
		final List<BlockableCustomer> items = getLinkedItems( 
			ctx,
			false,
			CustomermodelextConstants.Relations.BLOCKABLECUSTOMERORGANIZATIONRELATION,
			null,
			Utilities.getRelationOrderingOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_SRC_ORDERED, true),
			Utilities.getRelationOrderingOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_TGT_ORDERED, true)
		);
		return items;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.customers</code> attribute.
	 * @return the customers
	 */
	public Collection<BlockableCustomer> getCustomers()
	{
		return getCustomers( getSession().getSessionContext() );
	}
	
	public long getCustomersCount(final SessionContext ctx)
	{
		return getLinkedItemsCount(
			ctx,
			false,
			CustomermodelextConstants.Relations.BLOCKABLECUSTOMERORGANIZATIONRELATION,
			null
		);
	}
	
	public long getCustomersCount()
	{
		return getCustomersCount( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.customers</code> attribute. 
	 * @param value the customers
	 */
	public void setCustomers(final SessionContext ctx, final Collection<BlockableCustomer> value)
	{
		setLinkedItems( 
			ctx,
			false,
			CustomermodelextConstants.Relations.BLOCKABLECUSTOMERORGANIZATIONRELATION,
			null,
			value,
			Utilities.getRelationOrderingOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_SRC_ORDERED, true),
			Utilities.getRelationOrderingOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_TGT_ORDERED, true),
			Utilities.getMarkModifiedOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_MARKMODIFIED)
		);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.customers</code> attribute. 
	 * @param value the customers
	 */
	public void setCustomers(final Collection<BlockableCustomer> value)
	{
		setCustomers( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Adds <code>value</code> to customers. 
	 * @param value the item to add to customers
	 */
	public void addToCustomers(final SessionContext ctx, final BlockableCustomer value)
	{
		addLinkedItems( 
			ctx,
			false,
			CustomermodelextConstants.Relations.BLOCKABLECUSTOMERORGANIZATIONRELATION,
			null,
			Collections.singletonList(value),
			Utilities.getRelationOrderingOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_SRC_ORDERED, true),
			Utilities.getRelationOrderingOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_TGT_ORDERED, true),
			Utilities.getMarkModifiedOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_MARKMODIFIED)
		);
	}
	
	/**
	 * <i>Generated method</i> - Adds <code>value</code> to customers. 
	 * @param value the item to add to customers
	 */
	public void addToCustomers(final BlockableCustomer value)
	{
		addToCustomers( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Removes <code>value</code> from customers. 
	 * @param value the item to remove from customers
	 */
	public void removeFromCustomers(final SessionContext ctx, final BlockableCustomer value)
	{
		removeLinkedItems( 
			ctx,
			false,
			CustomermodelextConstants.Relations.BLOCKABLECUSTOMERORGANIZATIONRELATION,
			null,
			Collections.singletonList(value),
			Utilities.getRelationOrderingOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_SRC_ORDERED, true),
			Utilities.getRelationOrderingOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_TGT_ORDERED, true),
			Utilities.getMarkModifiedOverride(BLOCKABLECUSTOMERORGANIZATIONRELATION_MARKMODIFIED)
		);
	}
	
	/**
	 * <i>Generated method</i> - Removes <code>value</code> from customers. 
	 * @param value the item to remove from customers
	 */
	public void removeFromCustomers(final BlockableCustomer value)
	{
		removeFromCustomers( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.id</code> attribute.
	 * @return the id - Identifier
	 */
	public Integer getId(final SessionContext ctx)
	{
		return (Integer)getProperty( ctx, ID);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.id</code> attribute.
	 * @return the id - Identifier
	 */
	public Integer getId()
	{
		return getId( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.id</code> attribute. 
	 * @return the id - Identifier
	 */
	public int getIdAsPrimitive(final SessionContext ctx)
	{
		Integer value = getId( ctx );
		return value != null ? value.intValue() : 0;
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.id</code> attribute. 
	 * @return the id - Identifier
	 */
	public int getIdAsPrimitive()
	{
		return getIdAsPrimitive( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.id</code> attribute. 
	 * @param value the id - Identifier
	 */
	public void setId(final SessionContext ctx, final Integer value)
	{
		setProperty(ctx, ID,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.id</code> attribute. 
	 * @param value the id - Identifier
	 */
	public void setId(final Integer value)
	{
		setId( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.id</code> attribute. 
	 * @param value the id - Identifier
	 */
	public void setId(final SessionContext ctx, final int value)
	{
		setId( ctx,Integer.valueOf( value ) );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.id</code> attribute. 
	 * @param value the id - Identifier
	 */
	public void setId(final int value)
	{
		setId( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.name</code> attribute.
	 * @return the name - Organization name
	 */
	public String getName(final SessionContext ctx)
	{
		return (String)getProperty( ctx, NAME);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.name</code> attribute.
	 * @return the name - Organization name
	 */
	public String getName()
	{
		return getName( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.name</code> attribute. 
	 * @param value the name - Organization name
	 */
	public void setName(final SessionContext ctx, final String value)
	{
		setProperty(ctx, NAME,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.name</code> attribute. 
	 * @param value the name - Organization name
	 */
	public void setName(final String value)
	{
		setName( getSession().getSessionContext(), value );
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.phone</code> attribute.
	 * @return the phone - Phone number
	 */
	public String getPhone(final SessionContext ctx)
	{
		return (String)getProperty( ctx, PHONE);
	}
	
	/**
	 * <i>Generated method</i> - Getter of the <code>Organization.phone</code> attribute.
	 * @return the phone - Phone number
	 */
	public String getPhone()
	{
		return getPhone( getSession().getSessionContext() );
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.phone</code> attribute. 
	 * @param value the phone - Phone number
	 */
	public void setPhone(final SessionContext ctx, final String value)
	{
		setProperty(ctx, PHONE,value);
	}
	
	/**
	 * <i>Generated method</i> - Setter of the <code>Organization.phone</code> attribute. 
	 * @param value the phone - Phone number
	 */
	public void setPhone(final String value)
	{
		setPhone( getSession().getSessionContext(), value );
	}
	
}
