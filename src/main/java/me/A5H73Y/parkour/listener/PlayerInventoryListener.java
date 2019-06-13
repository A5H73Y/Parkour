package me.A5H73Y.parkour.listener;

import me.A5H73Y.parkour.Parkour;
import me.A5H73Y.parkour.gui.InventoryBuilder;
import me.A5H73Y.parkour.gui.ParkourCoursesInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class PlayerInventoryListener implements Listener {

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (!event.getView().getTitle().startsWith(InventoryBuilder.PARKOUR_TITLE_PREFIX)) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) {
            return;
        }

        Material clickedItem = event.getCurrentItem().getType();

        if (clickedItem.equals(Material.AIR)) {
            return;
        }

        if (!event.getCurrentItem().hasItemMeta() || !event.getCurrentItem().getItemMeta().hasLore()) {
            return;
        }

        List<String> metadata = event.getCurrentItem().getItemMeta().getLore();
        Material itemMaterial = Parkour.getSettings().getGUIMaterial();

        if (clickedItem.equals(itemMaterial)) { //&& event.isRightClick()
            String command = metadata.get(0);
            player.performCommand(command);

        } else if (event.getCurrentItem().getType().equals(Material.ARROW)) {
            player.closeInventory();

            Integer newPage = Integer.valueOf(metadata.get(0));
            Inventory inv = new ParkourCoursesInventory().buildInventory(player, newPage);

            player.openInventory(inv);
        }
    }
}
