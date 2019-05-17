package me.A5H73Y.Parkour.Listeners;

import java.util.List;

import me.A5H73Y.Parkour.Managers.QuestionManager;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Player.PlayerInfo;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!Parkour.getSettings().isChatPrefix()) {
            return;
        }

        String finalMessage;
        String rank = PlayerInfo.getRank(event.getPlayer());

        // should we completely override the chat format
        if (Parkour.getSettings().isChatPrefixOverride()) {
            finalMessage = Utils.colour(Utils.getTranslation("Event.Chat", false)
                    .replace("%RANK%", rank)
                    .replace("%PLAYER%", event.getPlayer().getDisplayName())
                    .replace("%MESSAGE%", event.getMessage()));
        } else {
            // or do we use the existing format, just replacing the Parkour variables
            finalMessage = Utils.colour(event.getFormat()
                    .replace("%RANK%", rank)
                    .replace("%PLAYER%", event.getPlayer().getDisplayName()));
        }

        event.setFormat(finalMessage);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        List<String> aliases = Parkour.getPlugin().getCommand("parkour").getAliases();

        // if the command is "parkour" or an alias.
        boolean isParkourCommand = event.getMessage().startsWith("/parkour")
                || aliases.stream().anyMatch(alias -> event.getMessage().startsWith("/" + alias));

        Player player = event.getPlayer();

        if (isParkourCommand && QuestionManager.getInstance().hasPlayerBeenAskedQuestion(player.getName())) {
            String[] args = event.getMessage().split(" ");
            if (args.length <= 1) {
                player.sendMessage(Static.getParkourString() + "Invalid answer.");
                player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
            } else {
                QuestionManager.getInstance().answerQuestion(player, args[1]);
            }
            event.setCancelled(true);
        }

        if (!isParkourCommand && PlayerMethods.isPlaying(player.getName())) {
            if (!Parkour.getSettings().isDisableCommandsOnCourse() ||
                    Utils.hasPermissionNoMessage(player, "Parkour.Admin")) {
                return;
            }

            boolean allowed = false;
            for (String word : Parkour.getSettings().getWhitelistedCommands()) {
                if (event.getMessage().startsWith("/" + word + " ") || (event.getMessage().equalsIgnoreCase("/" + word))) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                event.setCancelled(true);
                player.sendMessage(Utils.getTranslation("Error.Command"));
            }
        }
    }
}
