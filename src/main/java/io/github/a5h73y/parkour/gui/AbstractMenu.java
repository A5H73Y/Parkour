package io.github.a5h73y.parkour.gui;

import de.themoep.inventorygui.InventoryGui;
import org.bukkit.entity.Player;

/**
 * Abstract GUI Menu.
 * Can specify it's layout, and contents of the GUI.
 */
public interface AbstractMenu {

	/**
	 * Get the GUI Title.
	 *
	 * @return gui title
	 */
	public abstract String getTitle();

	/**
	 * Get the layout of the GUI setup.
	 * Each character acts as a placeholder for a Item to interact with.
	 *
	 * @return gui layout
	 */
	public abstract String[] getGuiLayout();

	/**
	 * Add Content to the InventoryGui.
	 *
	 * @param parent parent inventory to add contents to
	 * @param player requesting player
	 */
	public abstract void addContent(InventoryGui parent, Player player);

}
