package io.github.a5h73y.parkour.gui;

import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Gui Manager used to open and manage GUIs.
 */
public class ParkourGuiManager extends AbstractPluginReceiver {

	private static final String PARKOUR_TITLE_PREFIX = "Parkour - ";

	public ParkourGuiManager(final Parkour parkour) {
		super(parkour);
	}

	/**
	 * Open the specified parkour menu for the player.
	 *
	 * @param player requesting player
	 * @param menu requested menu
	 */
	public void showMenu(Player player, AbstractMenu menu) {
		InventoryGui gui = new InventoryGui(parkour, null,
				PARKOUR_TITLE_PREFIX + menu.getTitle(), menu.getGuiLayout());
		gui.setFiller(new ItemStack(parkour.getParkourConfig().getGuiFillerMaterial(), 1));

		menu.addContent(gui, player);

		gui.addElement(new GuiPageElement('1', new ItemStack(Material.BOOK),
				GuiPageElement.PageAction.FIRST, "Go to first page (current: %page%)"));
		gui.addElement(new GuiPageElement('2', new ItemStack(Material.ARROW),
				GuiPageElement.PageAction.PREVIOUS, "Go to previous page (%prevpage%)"));
		gui.addElement(new GuiPageElement('3', new ItemStack(Material.ARROW),
				GuiPageElement.PageAction.NEXT, "Go to next page (%nextpage%)"));
		gui.addElement(new GuiPageElement('4', new ItemStack(Material.BOOK),
				GuiPageElement.PageAction.LAST, "Go to last page (%pages%)"));

		gui.show(player);
	}
}
