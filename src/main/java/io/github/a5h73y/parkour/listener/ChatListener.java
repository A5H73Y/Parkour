package io.github.a5h73y.parkour.listener;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.enums.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.PermissionUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener extends AbstractPluginReceiver implements Listener {

    public ChatListener(final Parkour parkour) {
        super(parkour);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!parkour.getConfig().isChatPrefix()) {
            return;
        }

        String finalMessage;
        String rank = PlayerInfo.getRank(event.getPlayer());

        // should we completely override the chat format
        if (parkour.getConfig().isChatPrefixOverride()) {
            finalMessage = TranslationUtils.getTranslation("Event.Chat", false)
                    .replace("%RANK%", rank)
                    .replace("%PLAYER%", event.getPlayer().getDisplayName())
                    .replace("%MESSAGE%", event.getMessage());
        } else {
            // or do we use the existing format, just replacing the Parkour variables
            finalMessage = event.getFormat()
                    .replace("%RANK%", rank)
                    .replace("%PLAYER%", event.getPlayer().getDisplayName());
        }

        event.setFormat(StringUtils.colour(finalMessage));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        List<String> aliases = parkour.getCommand("parkour").getAliases();

        // if the command is "parkour" or an alias.
        boolean isParkourCommand = event.getMessage().startsWith("/parkour")
                || aliases.stream().anyMatch(alias -> event.getMessage().startsWith("/" + alias));

        Player player = event.getPlayer();

        if (isParkourCommand && parkour.getQuestionManager().hasPlayerBeenAskedQuestion(player.getName())) {
            String[] args = event.getMessage().split(" ");
            if (args.length <= 1) {
                player.sendMessage(Parkour.getPrefix() + "Invalid answer.");
                player.sendMessage("Please use either " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " or " + ChatColor.AQUA + "/pa no");
            } else {
                parkour.getQuestionManager().answerQuestion(player, args[1]);
            }
            event.setCancelled(true);
        }

        if (!isParkourCommand && parkour.getPlayerManager().isPlaying(player)) {
            if (!parkour.getConfig().isDisableCommandsOnCourse()
                    || PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                return;
            }

            boolean allowed = false;
            for (String word : parkour.getConfig().getWhitelistedCommands()) {
                if (event.getMessage().startsWith("/" + word + " ") || (event.getMessage().equalsIgnoreCase("/" + word))) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                event.setCancelled(true);
                TranslationUtils.sendTranslation("Error.Command", player);
            }
        }
    }
}
