package com.epam.cme.hmc;

import de.hybris.platform.hmc.AbstractEditorMenuChip;
import de.hybris.platform.hmc.AbstractExplorerMenuTreeNodeChip;
import de.hybris.platform.hmc.EditorTabChip;
import de.hybris.platform.hmc.extension.HMCExtension;
import de.hybris.platform.hmc.extension.MenuEntrySlotEntry;
import de.hybris.platform.hmc.generic.ClipChip;
import de.hybris.platform.hmc.generic.ToolbarActionChip;
import de.hybris.platform.hmc.webchips.Chip;
import de.hybris.platform.hmc.webchips.DisplayState;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


public class CMEHMCExtension extends HMCExtension
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CMEHMCExtension.class.getName());

	public final static String RESOURCE_PATH = "com.epam.cme.hmc.locales";

	@Override
	public List<EditorTabChip> getEditorTabChips(final DisplayState arg0, final AbstractEditorMenuChip arg1)
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public ResourceBundle getLocalizeResourceBundle(final Locale arg0) throws MissingResourceException
	{
		return null;
	}

	@Override
	public List<MenuEntrySlotEntry> getMenuEntrySlotEntries(final DisplayState arg0, final Chip arg1)
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getResourcePath()
	{
		return RESOURCE_PATH;
	}

	@Override
	public List<ClipChip> getSectionChips(final DisplayState arg0, final ClipChip arg1)
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<ToolbarActionChip> getToolbarActionChips(final DisplayState arg0, final Chip arg1)
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<AbstractExplorerMenuTreeNodeChip> getTreeNodeChips(final DisplayState arg0, final Chip arg1)
	{
		return Collections.EMPTY_LIST;
	}

}
