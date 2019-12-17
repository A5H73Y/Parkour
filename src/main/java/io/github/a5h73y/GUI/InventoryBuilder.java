package io.github.a5h73y.GUI;

import java.util.Arrays;
import java.util.List;

import io.github.a5h73y.Parkour;
import io.github.a5h73y.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class InventoryBuilder {

    public static final String PARKOUR_TITLE_PREFIX = "Parkour ";
    private List<String> filteredListItems;

    public abstract List<String> getAllItems();

    public abstract String getInventoryTitle();

    public Inventory buildInventory(Player player, int page) {
        int inventorySize = calculateInventorySize();
        String inventoryTitle = PARKOUR_TITLE_PREFIX + getInventoryTitle()
                .replace("%PAGE%", String.valueOf(page));

        List<String> items = getAllItems();

        int listStartIndex = (page * (inventorySize - 9)) - (inventorySize - 9);
        int remainingItems = Math.min(items.size() - listStartIndex, inventorySize - 9);
        int listEndIndex = listStartIndex + remainingItems;

        filteredListItems = items.subList(listStartIndex, listEndIndex);

        Inventory inv = Bukkit.createInventory(null, inventorySize, inventoryTitle);

        // previous page
        if (page > 1) {
            //TODO change Arrow?
            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta metadata = arrow.getItemMeta();
            metadata.setDisplayName(Utils.getTranslation("ParkourGUI.PreviousPage", false));
            metadata.setLore(Arrays.asList(String.valueOf(page - 1)));
            arrow.setItemMeta(metadata);
            inv.setItem(inventorySize - 9, arrow);
        }

        // next page
        if ((items.size() - listEndIndex) > 0) {
            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta metadata = arrow.getItemMeta();
            metadata.setDisplayName(Utils.getTranslation("ParkourGUI.NextPage", false));
            metadata.setLore(Arrays.asList(String.valueOf(page + 1)));
            arrow.setItemMeta(metadata);
            inv.setItem(inventorySize - 1, arrow);
        }

        return inv;
    }

    protected List<String> getFilteredItems() {
        return filteredListItems;
    }

    private int calculateInventorySize() {
        int rows = Parkour.getInstance().getConfig().getInt("ParkourGUI.Rows", 2);

        rows = Math.max(1, rows);
        rows = Math.min(rows, 5);

        return ((rows + 1) * 9);
    }
}
