package io.github.a5h73y.parkour.listener;

import static io.github.a5h73y.parkour.other.ParkourConstants.PARKOUR_RANK_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_PLACEHOLDER;

import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.utility.permission.Permission;
import io.github.a5h73y.parkour.other.AbstractPluginReceiver;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.utility.permission.PermissionUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener extends AbstractPluginReceiver implements Listener {

    private static final String MESSAGE_PLACEHOLDER = "%MESSAGE%";

    public ChatListener(final Parkour parkour) {
        super(parkour);
    }

    /**
     * Handle the Player Chat Event.
     * Used to insert the Player's ParkourRank into the chat prefix when enabled.
     *
     * @param event AsyncPlayerChatEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!parkour.getParkourConfig().isChatPrefix()) {
            return;
        }

        String finalMessage;
        String rank = PlayerConfig.getConfig(event.getPlayer()).getParkourRank();

        // should we completely override the chat format
        if (parkour.getParkourConfig().isChatPrefixOverride()) {
            finalMessage = TranslationUtils.getTranslation("Event.Chat", false)
                    .replace(PARKOUR_RANK_PLACEHOLDER, rank)
                    .replace(PLAYER_PLACEHOLDER, event.getPlayer().getDisplayName())
                    .replace(MESSAGE_PLACEHOLDER, event.getMessage());
        } else {
            // or do we use the existing format, just replacing the Parkour variables
            finalMessage = event.getFormat()
                    .replace(PARKOUR_RANK_PLACEHOLDER, rank)
                    .replace(PLAYER_PLACEHOLDER, event.getPlayer().getDisplayName());
        }
        finalMessage = parkour.getPlaceholderApi().parsePlaceholders(event.getPlayer(), finalMessage);
        event.setFormat(StringUtils.colour(finalMessage));
    }

    /**
     * Handle Command Preprocess Event.
     * Used to prevent non-whitelisted non-parkour commands while on a Course.
     * Used to force the player to answer an outstanding question.
     *
     * @param event PlayerCommandPreprocessEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        List<String> aliases = parkour.getCommand(Parkour.PLUGIN_NAME).getAliases();

        // if the command is "parkour" or an alias.
        boolean isParkourCommand = event.getMessage().startsWith("/parkour")
                || aliases.stream().anyMatch(alias -> event.getMessage().startsWith("/" + alias));

        Player player = event.getPlayer();

        if (isParkourCommand && parkour.getQuestionManager().hasBeenAskedQuestion(player)) {
            String[] args = event.getMessage().split(" ");
            if (args.length <= 1) {
                TranslationUtils.sendTranslation("Error.InvalidQuestionAnswer", player);
                TranslationUtils.sendTranslation("Error.QuestionAnswerChoices", false, player);
            } else {
                parkour.getQuestionManager().answerQuestion(player, args[1]);
            }
            event.setCancelled(true);
        }

        if (!isParkourCommand && parkour.getParkourSessionManager().isPlaying(player)) {
            if (!parkour.getParkourConfig().isDisableCommandsOnCourse()
                    || PermissionUtils.hasPermission(player, Permission.ADMIN_ALL)) {
                return;
            }

            boolean allowed = false;
            for (String word : parkour.getParkourConfig().getWhitelistedCommands()) {
                if (event.getMessage().startsWith("/" + word + " ") || event.getMessage().equalsIgnoreCase("/" + word)) {
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
