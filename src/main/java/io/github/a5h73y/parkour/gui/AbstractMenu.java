package io.github.a5h73y.parkour.gui;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.entity.Player;

/**
 * Abstract GUI Menu.
 * Can specify it's layout, and contents of the GUI.
 */
public abstract class AbstractMenu {

	public abstract String getTitle();

	public abstract String[] getGuiSetup();

	public abstract GuiElementGroup getGroupContent(InventoryGui parent, Player player);

}
