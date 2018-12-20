package me.A5H73Y.Parkour.GUI;

import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class InventoryBuilder {

    public static final String PARKOUR_TITLE = "Parkour Courses - Page ";

    //TODO make more generic, more like getItems()
    public abstract List<String> getCourses();

    public Inventory buildInventory(Player player, int page) {
        Inventory inv = Bukkit.createInventory(null, 18, PARKOUR_TITLE + page); //TODO config rows (rows * 9) + 1 (for nav) (Limit to Minecraft max)

        List<String> courses = getCourses();

        //TODO tidy up - make more dynamic (configurable amounts)
        int start = (page * 9) - 9;
        int remaining = Math.min(courses.size() - start, 9);
        int limit = start + remaining;

        courses = courses.subList(start, limit);

        //TODO make everything configurable

        for (int i = 0; i < courses.size(); i++) {
            String course = courses.get(i);
            ItemStack book = new ItemStack(Material.BOOK);
            ItemMeta metadata = book.getItemMeta();
            metadata.setDisplayName(Utils.colour("&fJoin &b" + course));
            metadata.setLore(Arrays.asList("pa join " + course));
            // command type will have to be abstract, for full customisation "pkr join" etc.
            book.setItemMeta(metadata);
            inv.setItem(i, book);
        }

        if (page > 1) {
            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta metadata = arrow.getItemMeta();
            metadata.setDisplayName(Utils.colour("< &bPrevious page"));
            metadata.setLore(Arrays.asList(String.valueOf(page - 1)));
            arrow.setItemMeta(metadata);
            inv.setItem(9, arrow);
        }

        if ((getCourses().size()) - limit > 0) {
            ItemStack arrow = new ItemStack(Material.ARROW);
            ItemMeta metadata = arrow.getItemMeta();
            metadata.setDisplayName(Utils.colour("&bNext page &f>"));
            metadata.setLore(Arrays.asList(String.valueOf(page + 1)));
            arrow.setItemMeta(metadata);
            inv.setItem(17, arrow);
        }

        return inv;
    }
}