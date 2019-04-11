package me.A5H73Y.Parkour.GUI;

import me.A5H73Y.Parkour.Course.CourseInfo;
import me.A5H73Y.Parkour.Utilities.Utils;
import me.A5H73Y.Parkour.Parkour;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ParkourCoursesInventory extends InventoryBuilder {

    @Override
    public List<String> getAllItems() {
        return CourseInfo.getAllCourses();
    }

    @Override
    public String getInventoryTitle() {
        return Utils.getTranslation("ParkourGUI.AllCourses.Title", false);
    }

    @Override
    public Inventory buildInventory(Player player, int page) {
        Inventory inventory = super.buildInventory(player, page);

        Material material = Parkour.getSettings().getGUIMaterial();

        for (int i = 0; i < getFilteredItems().size(); i++) {
            String course = getFilteredItems().get(i);
            ItemStack inventoryItem = new ItemStack(material);
            ItemMeta metadata = inventoryItem.getItemMeta();

            metadata.setDisplayName(
                    Utils.getTranslation("ParkourGUI.AllCourses.Description", false)
                            .replace("%COURSE%", course)
            );

            metadata.setLore(Arrays.asList(
                    Utils.getTranslation("ParkourGUI.AllCourses.Command", false)
                            .replace("%COURSE%", course)
            ));

            inventoryItem.setItemMeta(metadata);
            inventory.setItem(i, inventoryItem);
        }

        return inventory;
    }
}