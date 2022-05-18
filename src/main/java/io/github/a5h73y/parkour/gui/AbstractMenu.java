package io.github.a5h73y.parkour.gui;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.entity.Player;

/**
 * Abstract GUI Menu.
 * Can specify its layout, and contents of the GUI.
 */
public interface AbstractMenu {

	/**
	 * Get the GUI Title.
	 *
	 * @return gui title
	 */
	String getTitle();

	/**
	 * Get the layout of the GUI setup.
	 * Each character acts as a placeholder for an Item to interact with.
	 *
	 * @return gui layout
	 */
	String[] getGuiLayout();

	/**
	 * Add Content to the InventoryGui.
	 *
	 * @param parent parent inventory to add contents to
	 * @param player requesting player
	 */
	void addContent(InventoryGui parent, Player player);

}
