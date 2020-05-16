package io.github.a5h73y.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import io.github.a5h73y.Parkour;
import io.github.a5h73y.course.CourseInfo;
import io.github.a5h73y.course.CourseMethods;
import io.github.a5h73y.enums.ConfigType;
import io.github.a5h73y.kit.ParkourKitInfo;
import io.github.a5h73y.manager.QuestionManager;
import io.github.a5h73y.manager.QuietModeManager;
import io.github.a5h73y.database.TimeEntry;
import io.github.a5h73y.other.Validation;
import io.github.a5h73y.player.PlayerInfo;
import io.github.a5h73y.player.PlayerMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Stairs;

public final class Utils {

    /**
     * Get translation of string key.
     * The string parameter will be matched to an entry in the Strings.yml
     * The boolean will determine whether to display the Parkour prefix
     *
     * @param translationKey to translate
     * @param prefix         display Parkour prefix
     * @return String of appropriate translation
     */
    public static String getTranslation(String translationKey, boolean prefix) {
        if (!Validation.isStringValid(translationKey)) {
            return "Invalid translation.";
        }

        String translated = Parkour.getConfig(ConfigType.STRINGS).getString(translationKey);
        translated = translated != null ? colour(translated) : "String not found: " + translationKey;
        return prefix ? Static.getParkourString().concat(translated) : translated;
    }

    /**
     * Override method, but with a default of an enabled Parkour prefix.
     *
     * @param translationKey to translate
     * @return String of appropriate translation
     */
    public static String getTranslation(String translationKey) {
        return getTranslation(translationKey, true);
    }

    /**
     * Check if they have permission without alerting them of failure.
     * Branch will check example scenarios for "Parkour.Admin", "Delete":
     * - Parkour.Admin.Delete
     * - Parkour.Admin.*
     * - Parkour.*
     *
     * @param player
     * @param permissionBranch
     * @param permission
     * @return
     */
    public static boolean hasPermissionNoMessage(CommandSender player, String permissionBranch, String permission) {
        return player.hasPermission(permissionBranch + ".*")
                || player.hasPermission(permissionBranch + "." + permission)
                || player.hasPermission("Parkour.*");
    }

    /**
     * Check if they have permission without alerting them of failure.
     * Branch will check example scenarios for "Parkour.Admin":
     * - Parkour.Admin.*
     * - Parkour.*
     *
     * @param player
     * @param permission
     * @return
     */
    public static boolean hasPermissionNoMessage(CommandSender player, String permission) {
        return player.hasPermission(permission + ".*")
                || player.hasPermission("Parkour.*");
    }

    /**
     * Return whether the player has a permission wildcard.
     * i.e. "Parkour.Basic" becomes "Parkour.Basic.*"
     * If they don't, a message will be sent alerting them.
     *
     * @param sender     the Player
     * @param permission
     * @return whether they have permission
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        if (hasPermissionNoMessage(sender, permission)) {
            return true;
        }

        sender.sendMessage(getTranslation("NoPermission").replace("%PERMISSION%", permission));
        return false;
    }

    /**
     * Return whether the player has a specific permission OR has the branch permission.
     * Example "parkour.basic.join" OR "parkour.basic.*"
     *
     * @param sender
     * @param permissionBranch i.e. "parkour.basic"
     * @param permission       "join"
     * @return whether they have permission
     */
    public static boolean hasPermission(CommandSender sender, String permissionBranch, String permission) {
        if (hasPermissionNoMessage(sender, permissionBranch, permission)) {
            return true;
        }

        sender.sendMessage(getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
        return false;
    }

    /**
     * Check if the player has the permission, if haven't check if they're the creator the course.
     * Example, they may not be an admin, but they can change the start location of their own course.
     *
     * @param player
     * @param permissionBranch
     * @param permission
     * @param courseName
     * @return whether they have permission
     */
    public static boolean hasPermissionOrCourseOwnership(Player player, String permissionBranch, String permission, String courseName) {
        if (!(CourseMethods.exist(courseName))) {
            player.sendMessage(getTranslation("Error.NoExist").replace("%COURSE%", courseName));
            return false;

        } else if (hasPermissionNoMessage(player, permissionBranch, permission)) {
            return true;

        } else if (player.getName().equals(CourseInfo.getCreator(courseName))) {
            return true;
        }

        player.sendMessage(getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
        return false;
    }

    /**
     * Return whether the player has a specific sign permission.
     * This is checked on the creation of a Parkour sign,
     * so the sign will be broken if they don't have permission.
     *
     * @param player
     * @param permission
     * @return whether they have permission
     */
    public static boolean hasSignPermission(Player player, SignChangeEvent sign, String permission) {
        if (!hasPermission(player, "Parkour.Sign", permission)) {
            sign.setCancelled(true);
            sign.getBlock().breakNaturally();
            return false;
        }
        return true;
    }

    /**
     * Validate the length of the arguments before allowing it to be processed further.
     *
     * @param sender
     * @param args
     * @param desired
     * @return whether the arguments match the criteria
     */
    public static boolean validateArgs(CommandSender sender, String[] args, int desired) {
        if (args.length > desired) {
            sender.sendMessage(getTranslation("Error.TooMany") + " (" + desired + ")");
            sender.sendMessage(getTranslation("Help.Command").replace("%COMMAND%", standardizeText(args[0])));
            return false;

        } else if (args.length < desired) {
            sender.sendMessage(getTranslation("Error.TooLittle") + " (" + desired + ")");
            sender.sendMessage(getTranslation("Help.Command").replace("%COMMAND%", standardizeText(args[0])));
            return false;
        }
        return true;
    }

    /**
     * Validate the range of the arguments before allowing it to be processed further.
     *
     * @param sender
     * @param args
     * @param minimum args length
     * @param maximum args length
     * @return whether the arguments match the criteria
     */
    public static boolean validateArgs(CommandSender sender, String[] args, int minimum, int maximum) {
        if (args.length > maximum) {
            sender.sendMessage(getTranslation("Error.TooMany") + " (between " + minimum + " and " + maximum + ")");
            sender.sendMessage(getTranslation("Help.Command").replace("%COMMAND%", standardizeText(args[0])));
            return false;

        } else if (args.length < minimum) {
            sender.sendMessage(getTranslation("Error.TooLittle") + " (between " + minimum + " and " + maximum + ")");
            sender.sendMessage(getTranslation("Help.Command").replace("%COMMAND%", standardizeText(args[0])));
            return false;
        }
        return true;
    }

    /**
     * Format and standardize text to a constant case.
     * Will transform "hElLO" into "Hello"
     *
     * @param text
     * @return standardized input
     */
    public static String standardizeText(String text) {
        if (!Validation.isStringValid(text)) {
            return text;
        }
        return text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase());
    }

    /**
     * Converts text to the appropriate colours
     * "&4Hello" is the same as ChatColor.RED + "Hello"
     *
     * @param text
     * @return colourised input
     */
    public static String colour(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Convert milliseconds into formatted time HH:MM:SS(.sss)
     *
     * @param millis
     * @return formatted time: HH:MM:SS.(sss)
     */
    public static String displayCurrentTime(long millis) {
        MillisecondConverter time = new MillisecondConverter(millis);
        String pattern = Parkour.getSettings().isDisplayMilliseconds() ? "%02d:%02d:%02d.%03d" : "%02d:%02d:%02d";
        return String.format(pattern, time.getHours(), time.getMinutes(), time.getSeconds(), time.getMilliseconds());
    }

    /**
     * Used for logging plugin events, varying in severity.
     * 0 - Info; 1 - Warn; 2 - Severe.
     *
     * @param message
     * @param severity (0 - 2)
     */
    public static void log(String message, int severity) {
        switch (severity) {
            case 1:
                Parkour.getInstance().getLogger().warning(message);
                break;
            case 2:
                Parkour.getInstance().getLogger().severe("! " + message);
                break;
            case 3:
                Parkour.getInstance().getLogger().info("~ " + message);
                break;
            case 0:
            default:
                Parkour.getInstance().getLogger().info(message);
                break;
        }
    }

    /**
     * Debug a message to the console.
     * Has to be manually enabled in the config.
     * @param message
     */
    public static void debug(String message) {
        if (Parkour.getInstance().getConfig().getBoolean("Debug", false)) {
            log(message, 3);
        }
    }

    /**
     * Default level of logging (INFO)
     *
     * @param message
     */
    public static void log(String message) {
        log(message, 0);
    }

    /**
     * This will write 'incriminating' events to a separate file that can't be erased.
     * Examples: playerA deleted courseB
     * This means any griefers can easily be caught etc.
     *
     * @param message
     */
    public static void logToFile(String message) {
        if (!Parkour.getInstance().getConfig().getBoolean("Other.LogToFile")) {
            return;
        }

        try {
            File saveTo = new File(Parkour.getInstance().getDataFolder(), "Parkour.log");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(getDateTime() + " " + message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts messages to each online player and the server (for logging).
     *
     * @param message
     * @param permission
     */
    public static void broadcastMessage(String message, String permission) {
        Bukkit.broadcast(message, permission);
        log(message);
    }

    /**
     * Get current DateTime
     * This is currently only used for logToFile method.
     *
     * @return formatted datetime DD/MM/YYYY | HH:MM:SS
     */
    public static String getDateTime() {
        Format formatter = new SimpleDateFormat("[dd/MM/yyyy | HH:mm:ss]");
        return formatter.format(new Date());
    }

    /**
     * Get current Date
     * This is currently only used for backup folder names
     *
     * @return formatted date DD-MM-YYYY
     */
    public static String getDate() {
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(new Date());
    }

    /**
     * Display invalid syntax error
     * Using parameters to populate the translation message
     *
     * @param command
     * @param arguments
     * @return formatted error message
     */
    public static String invalidSyntax(String command, String arguments) {
        return getTranslation("Error.Syntax")
                .replace("%COMMAND%", command)
                .replace("%ARGUMENTS%", arguments);
    }

    /**
     * Retrieve the GameMode for the integer, survival being default.
     *
     * @param gamemode
     * @return chosen GameMode
     */
    public static GameMode getGamemode(int gamemode) {
        switch (gamemode) {
            case 1:
                return GameMode.CREATIVE;
            case 2:
                return GameMode.ADVENTURE;
            case 3:
                return GameMode.SPECTATOR;
            case 0:
            default:
                return GameMode.SURVIVAL;
        }
    }

    /**
     * Used for saving the ParkourSessions.
     * Thanks to Tomsik68 for this code.
     *
     * @param obj
     * @param path
     */
    public static void saveAllPlaying(Object obj, String path) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
            oos.writeObject(obj);
            oos.flush();
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Used for loading the ParkourSessions.
     * Thanks to Tomsik68 for this code.
     *
     * @param path
     */
    public static Object loadAllPlaying(String path) {
        Object result = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            result = ois.readObject();
            ois.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * The following methods use the BountifulAPI plugin
     * If the user is has 'Quiet mode' enabled, no message will be sent
     * If the BountifulAPI plugin is not installed, the message will be sent via chat
     *
     * @param player
     * @param title
     * @param attemptTitle
     */
    public static void sendTitle(Player player, String title, boolean attemptTitle) {
        if (QuietModeManager.getInstance().isInQuietMode(player.getName())) {
            return;
        }

        if (Static.getBountifulAPI() && attemptTitle) {
            BountifulAPI.sendTitle(player,
                    Parkour.getSettings().getTitleIn(),
                    Parkour.getSettings().getTitleStay(),
                    Parkour.getSettings().getTitleOut(),
                    title, "");
        } else {
            player.sendMessage(Static.getParkourString() + title);
        }
    }

    public static void sendFullTitle(Player player, String title, String subTitle, boolean attemptTitle) {
        if (QuietModeManager.getInstance().isInQuietMode(player.getName())) {
            return;
        }

        if (Static.getBountifulAPI() && attemptTitle) {
            BountifulAPI.sendTitle(player,
                    Parkour.getSettings().getTitleIn(),
                    Parkour.getSettings().getTitleStay(),
                    Parkour.getSettings().getTitleOut(),
                    title, subTitle);
        } else {
            player.sendMessage(Static.getParkourString() + title + " " + subTitle);
        }
    }

    public static void sendSubTitle(Player player, String subTitle, boolean attemptTitle) {
        if (QuietModeManager.getInstance().isInQuietMode(player.getName())) {
            return;
        }

        if (Static.getBountifulAPI() && attemptTitle) {
            BountifulAPI.sendTitle(player,
                    Parkour.getSettings().getTitleIn(),
                    Parkour.getSettings().getTitleStay(),
                    Parkour.getSettings().getTitleOut(),
                    "", subTitle);
        } else {
            player.sendMessage(Static.getParkourString() + subTitle);
        }
    }

    public static void sendActionBar(Player player, String title, boolean attemptTitle) {
        if (QuietModeManager.getInstance().isInQuietMode(player.getName())) {
            return;
        }

        if (Static.getBountifulAPI() && attemptTitle) {
            BountifulAPI.sendActionBar(player, title);
        } else {
            player.sendMessage(Static.getParkourString() + title);
        }
    }

    /**
     * Delete command method
     * Possible arguments include Course, Checkpoint, Lobby, Kit and AutoStart
     * This will only add a Question object with the relevant data until the player confirms the action later on.
     *
     * @param args
     * @param player
     */
    public static void deleteCommand(String[] args, Player player) {
        if (args[1].equalsIgnoreCase("course")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(getTranslation("Error.Unknown"));
                return;
            }

            if (!Validation.deleteCourse(args[2], player)) {
                return;
            }

            QuestionManager.getInstance().askDeleteCourseQuestion(player, args[2]);

        } else if (args[1].equalsIgnoreCase("checkpoint")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(getTranslation("Error.Unknown"));
                return;
            }

            int checkpoints = CourseInfo.getCheckpointAmount(args[2]);
            // if it has no checkpoints
            if (checkpoints <= 0) {
                player.sendMessage(Static.getParkourString() + args[2] + " has no checkpoints!");
                return;
            }

            QuestionManager.getInstance().askDeleteCheckpointQuestion(player, args[2], checkpoints);

        } else if (args[1].equalsIgnoreCase("lobby")) {
            if (!Parkour.getInstance().getConfig().contains("Lobby." + args[2].toLowerCase() + ".World")) {
                player.sendMessage(Static.getParkourString() + "This lobby does not exist!");
                return;
            }

            if (!Validation.deleteLobby(args[2], player)) {
                return;
            }

            QuestionManager.getInstance().askDeleteLobbyQuestion(player, args[2]);

        } else if (args[1].equalsIgnoreCase("kit")) {
            if (!ParkourKitInfo.doesParkourKitExist(args[2])) {
                player.sendMessage(Static.getParkourString() + "This ParkourKit does not exist!");
                return;
            }

            if (!Validation.deleteParkourKit(args[2], player)) {
                return;
            }

            QuestionManager.getInstance().askDeleteKitQuestion(player, args[2]);

        } else if (args[1].equalsIgnoreCase("autostart")) {
            Location location = player.getLocation();
            if (CourseMethods.getAutoStartCourse(location) == null) {
                player.sendMessage(Static.getParkourString() + "There is no autostart at this location");
                return;
            }

            String coordinates = location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ();
            if (!Validation.deleteAutoStart(args[2], coordinates, player)) {
                return;
            }

            QuestionManager.getInstance().askDeleteAutoStartQuestion(player, coordinates);

        } else {
            player.sendMessage(invalidSyntax("delete", "(course / checkpoint / lobby / kit / autostart) (name)"));
        }
    }

    /**
     * Reset command method
     * Possible arguments include Course, Player and Leaderboard
     * This will only add a Question object with the relevant data until the player confirms the action later on.
     *
     * @param args
     * @param player
     */
    public static void resetCommand(String[] args, Player player) {
        if (args[1].equalsIgnoreCase("course")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(getTranslation("Error.Unknown"));
                return;
            }

            QuestionManager.getInstance().askResetCourseQuestion(player, args[2]);

        } else if (args[1].equalsIgnoreCase("player")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
            if (target == null || !PlayerInfo.hasPlayerInfo(target)) {
                player.sendMessage(getTranslation("Error.UnknownPlayer"));
                return;
            }

            QuestionManager.getInstance().askResetPlayerQuestion(player, args[2]);

        } else if (args[1].equalsIgnoreCase("leaderboard")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(getTranslation("Error.Unknown"));
                return;
            }

            if (args.length > 3) {
                QuestionManager.getInstance().askResetPlayerLeaderboardQuestion(player, args[2], args[3]);
            } else {
                QuestionManager.getInstance().askResetLeaderboardQuestion(player, args[2]);
            }

        } else if (args[1].equalsIgnoreCase("prize")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(getTranslation("Error.Unknown"));
                return;
            }

            QuestionManager.getInstance().askResetPrizeQuestion(player, args[2]);

        } else {
            player.sendMessage(invalidSyntax("reset", "(course / player / leaderboard / prize) (argument)"));
        }
    }

    /**
     * Return the standardised heading used for Parkour
     *
     * @param headingText
     * @return standardised Parkour heading
     */
    public static String getStandardHeading(String headingText) {
        return "-- " + ChatColor.BLUE + ChatColor.BOLD + headingText + ChatColor.RESET + " --";
    }

    /**
     * Validate amount of Material
     * Must be between 1 and 64.
     *
     * @param amountString
     * @return int
     */
    public static int parseMaterialAmount(String amountString) {
        int amount = Integer.parseInt(amountString);
        return amount < 1 ? 1 : Math.min(amount, 64);
    }

    /**
     * Display Leaderboards
     * Present the course times to the player.
     *
     * @param times
     * @param player
     * @param courseName
     */
    public static void displayLeaderboard(Player player, List<TimeEntry> times, String courseName) {
        if (times.isEmpty()) {
            player.sendMessage(Static.getParkourString() + "No results were found!");
            return;
        }

        String heading = getTranslation("Parkour.LeaderboardHeading", false)
                .replace("%COURSE%", courseName)
                .replace("%AMOUNT%", String.valueOf(times.size()));

        player.sendMessage(getStandardHeading(heading));

        for (int i = 0; i < times.size(); i++) {
            String translation = getTranslation("Parkour.LeaderboardEntry", false)
                    .replace("%POSITION%", String.valueOf(i + 1))
                    .replace("%PLAYER%", times.get(i).getPlayer())
                    .replace("%TIME%", displayCurrentTime(times.get(i).getTime()))
                    .replace("%DEATHS%", String.valueOf(times.get(i).getDeaths()));

            player.sendMessage(translation);
        }
    }

    /**
     * Get an ItemStack for the material with a display name of a translated message
     *
     * @param material
     * @param itemLabel
     * @return ItemStack
     */
    public static ItemStack getItemStack(Material material, String itemLabel) {
        return getItemStack(material, itemLabel, 1);
    }

    public static ItemStack getItemStack(Material material, String itemLabel, Integer amount) {
        ItemStack item = new ItemStack(material, amount);
        if (itemLabel != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(itemLabel);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Delay Player event
     * When a player triggers an event, they must wait X more seconds before they can trigger it again
     * An example of this is using the HideAll tool
     * This can not be bypassed by admins / ops
     *
     * @param player
     * @param secondsToWait
     * @return whether the event can trigger
     */
    public static boolean delayPlayerEvent(Player player, int secondsToWait) {
        if (!Static.getDelay().containsKey(player.getName())) {
            Static.getDelay().put(player.getName(), System.currentTimeMillis());
            return true;
        }

        long lastAction = Static.getDelay().get(player.getName());
        int secondsElapsed = (int) ((System.currentTimeMillis() - lastAction) / 1000);

        if (secondsElapsed >= secondsToWait) {
            Static.getDelay().put(player.getName(), System.currentTimeMillis());
            return true;
        }

        return false;
    }

    /**
     * Delay certain actions
     * This check can be bypassed by admins / ops
     *
     * @param player
     * @param secondsToWait
     * @param displayMessage display cooldown error
     * @return boolean (wait expired)
     */
    public static boolean delayPlayer(Player player, int secondsToWait, boolean displayMessage) {
        if (player.isOp()) {
            return true;
        }

        if (!Static.getDelay().containsKey(player.getName())) {
            Static.getDelay().put(player.getName(), System.currentTimeMillis());
            return true;
        }

        long lastAction = Static.getDelay().get(player.getName());
        int secondsElapsed = (int) ((System.currentTimeMillis() - lastAction) / 1000);

        if (secondsElapsed >= secondsToWait) {
            Static.getDelay().put(player.getName(), System.currentTimeMillis());
            return true;
        }

        if (displayMessage && !QuietModeManager.getInstance().isInQuietMode(player.getName())) {
            player.sendMessage(getTranslation("Error.Cooldown").replace("%AMOUNT%", String.valueOf(secondsToWait - secondsElapsed)));
        }
        return false;
    }

    /**
     * Add a whitelisted command
     *
     * @param args
     * @param player
     */
    public static void addWhitelistedCommand(String[] args, Player player) {
        if (Parkour.getSettings().getWhitelistedCommands().contains(args[1].toLowerCase())) {
            player.sendMessage(Static.getParkourString() + "This command is already whitelisted!");
            return;
        }

        Static.addWhitelistedCommand(args[1].toLowerCase());
        player.sendMessage(Static.getParkourString() + "Command " + ChatColor.AQUA + args[1] + ChatColor.WHITE + " added to the whitelisted commands!");
    }

    /**
     * Convert the number of seconds to a HH:MM:SS format.
     * This is used for the live time display on a course.
     *
     * @param totalSeconds
     * @return formatted time HH:MM:SS
     */
    public static String convertSecondsToTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Display ParkourKit
     * Can either display all the ParkourKit available
     * Or specify which ParkourKit you want to see and which materials are used and their corresponding action
     *
     * @param args
     * @param sender
     */
    public static void listParkourKit(String[] args, CommandSender sender) {
        Set<String> parkourKit = ParkourKitInfo.getParkourKitNames();

        // specifying a kit
        if (args.length == 2) {
            String kitName = args[1].toLowerCase();
            if (!parkourKit.contains(kitName)) {
                sender.sendMessage(Static.getParkourString() + "This ParkourKit set does not exist!");
                return;
            }

            sender.sendMessage(getStandardHeading("ParkourKit: " + kitName));
            Set<String> materials = ParkourKitInfo.getParkourKitContents(kitName);

            for (String material : materials) {
                String action = ParkourKitInfo.getActionForMaterial(kitName, material);
                sender.sendMessage(material + ": " + ChatColor.GRAY + action);
            }

        } else {
            //displaying all available kits
            sender.sendMessage(getStandardHeading(parkourKit.size() + " ParkourKit found"));
            for (String kit : parkourKit) {
                sender.sendMessage("* " + kit);
            }
        }
    }

    /**
     * Made because < 1.8
     *
     * @param player
     * @return
     */
    public static Material getMaterialInPlayersHand(Player player) {
        return getItemStackInPlayersHand(player).getType();
    }

    /**
     * Made because < 1.8
     *
     * @param player
     * @return
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getItemStackInPlayersHand(Player player) {
        ItemStack stack;

        try {
            stack = player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError ex) {
            stack = player.getItemInHand();
        }

        return stack;
    }

    /**
     * Get all players that are online and on a course using the plugin
     *
     * @return List<Player>
     */
    public static List<Player> getOnlineParkourPlayers() {
        List<Player> onlineParkourPlayers = new ArrayList<>();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (PlayerMethods.isPlaying(player.getName())) {
                onlineParkourPlayers.add(player);
            }
        }

        return onlineParkourPlayers;
    }

    public static void toggleVisibility(Player player) {
        toggleVisibility(player, false);
    }

    /**
     * Toggle visibility of all players for the player
     * Can be overwritten to force the reappearance of all players (i.e. when a player leaves / finishes a course)
     * Option can be chosen whether to hide all online players, or just parkour players
     *
     * @param player
     * @param override
     */
    public static void toggleVisibility(Player player, boolean override) {
        boolean enabled = override || Static.containsHidden(player.getName());
        List<Player> playerScope;

        if (Parkour.getInstance().getConfig().getBoolean("OnJoin.Item.HideAll.Global") || override) {
            playerScope = (List<Player>) Bukkit.getOnlinePlayers();
        } else {
            playerScope = getOnlineParkourPlayers();
        }

        for (Player players : playerScope) {
            if (enabled) {
                //TODO test me
//                if (!Static.containsHidden(players.getName())) {
                    player.showPlayer(players);
//                }
            } else {
                player.hidePlayer(players);
            }
        }
        if (enabled) {
            Static.removeHidden(player.getName());
            player.sendMessage(getTranslation("Event.HideAll1"));
        } else {
            Static.addHidden(player.getName());
            player.sendMessage(getTranslation("Event.HideAll2"));
        }
    }

    public static void forceVisible(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.showPlayer(player);
        }
    }

    public static boolean isCheckpointSafe(Player player, Block block) {
        Block blockUnder = block.getRelative(BlockFace.DOWN);

        List<Material> validMaterials = new ArrayList<>();
        Collections.addAll(validMaterials, Material.AIR, XMaterial.CAVE_AIR.parseMaterial(), Material.REDSTONE_BLOCK,
                XMaterial.ACACIA_SLAB.parseMaterial(), XMaterial.BIRCH_SLAB.parseMaterial(), XMaterial.BRICK_SLAB.parseMaterial(),
                XMaterial.COBBLESTONE_SLAB.parseMaterial(), XMaterial.DARK_OAK_SLAB.parseMaterial(), XMaterial.DARK_PRISMARINE_SLAB.parseMaterial(),
                XMaterial.JUNGLE_SLAB.parseMaterial(), XMaterial.NETHER_BRICK_SLAB.parseMaterial(), XMaterial.OAK_SLAB.parseMaterial(),
                XMaterial.PETRIFIED_OAK_SLAB.parseMaterial(), XMaterial.PRISMARINE_BRICK_SLAB.parseMaterial(), XMaterial.PRISMARINE_SLAB.parseMaterial(),
                XMaterial.QUARTZ_SLAB.parseMaterial(), XMaterial.RED_SANDSTONE_SLAB.parseMaterial(), XMaterial.SANDSTONE_SLAB.parseMaterial(),
                XMaterial.SPRUCE_SLAB.parseMaterial(), XMaterial.STONE_BRICK_SLAB.parseMaterial(), XMaterial.STONE_SLAB.parseMaterial());

        if (!Bukkit.getBukkitVersion().contains("1.8")) {
            validMaterials.add(Material.PURPUR_SLAB);
        }

        //check if player is standing in a half-block
        if (!block.getType().equals(Material.AIR) && !block.getType().equals(XMaterial.CAVE_AIR.parseMaterial()) && !block.getType().equals(lookupMaterial(Parkour.getInstance().getConfig().getString("OnCourse.CheckpointMaterial")))) {
            player.sendMessage(Static.getParkourString() + "Invalid block for checkpoint: " + ChatColor.AQUA + block.getType());
            return false;
        }

        if (!blockUnder.getType().isOccluding()) {
            if (blockUnder.getState().getData() instanceof Stairs) {
                Stairs stairs = (Stairs) blockUnder.getState().getData();
                if (!stairs.isInverted()) {
                    player.sendMessage(Static.getParkourString() + "Invalid block for checkpoint: " + ChatColor.AQUA + blockUnder.getType());
                    return false;
                }
            } else if (!validMaterials.contains(blockUnder.getType())) {
                player.sendMessage(Static.getParkourString() + "Invalid block for checkpoint: " + ChatColor.AQUA + blockUnder.getType());
                return false;
            }
        }
        return true;
    }

    /**
     * Check to see if the minimum amount of time has passed (in days) to allow the plugin to provide the prize again
     *
     * @param player
     * @param courseName
     * @param displayMessage
     * @return boolean
     */
    public static boolean hasPrizeCooldownDurationPassed(Player player, String courseName, boolean displayMessage) {
        int rewardDelay = CourseInfo.getRewardDelay(courseName);

        if (rewardDelay <= 0) {
            return true;
        }

        long lastRewardTime = PlayerInfo.getLastRewardedTime(player, courseName);

        if (lastRewardTime <= 0) {
            return true;
        }

        long timeDifference = System.currentTimeMillis() - lastRewardTime;
        long daysDelay = convertDaysToMilliseconds(rewardDelay);

        if (timeDifference > daysDelay) {
            return true;
        }

        if (Parkour.getSettings().isDisplayPrizeCooldown() && displayMessage) {
            player.sendMessage(getTranslation("Error.PrizeCooldown").replace("%TIME%", getTimeRemaining(player, courseName)));
        }
        return false;
    }

    public static String getTimeRemaining(Player player, String courseName) {
        long daysDelay = convertDaysToMilliseconds(CourseInfo.getRewardDelay(courseName));
        long timeDifference = System.currentTimeMillis() - PlayerInfo.getLastRewardedTime(player, courseName);
        return displayTimeRemaining(daysDelay - timeDifference);
    }

    private static String displayTimeRemaining(long millis) {
        MillisecondConverter time = new MillisecondConverter(millis);
        StringBuilder totalTime = new StringBuilder();

        if (time.getDays() > 2) {
            totalTime.append(time.getDays());
            totalTime.append(" days"); //todo translate
            return totalTime.toString();
        }

        if (time.getDays() > 0) {
            totalTime.append(1);
            totalTime.append(" day, ");
        }
        if (time.getHours() > 0) {
            totalTime.append(time.getHours());
            totalTime.append(" hours, ");
        }
        totalTime.append(time.getMinutes());
        totalTime.append(" minutes");
        return totalTime.toString();
    }

    private static long convertDaysToMilliseconds(int days) {
        return days * 86400000; //(24*60*60*1000)
    }

    /**
     * Lookup the matching Material
     * Use the 1.13 API to lookup the Material,
     * It will fall back to XMaterial if it fails to find it
     *
     * @param materialName
     * @return matching Material
     */
    public static Material lookupMaterial(String materialName) {
        Material material = Material.getMaterial(materialName);

        if (material == null) {
            XMaterial matching = XMaterial.fromString(materialName);

            if (matching != null) {
                material = matching.parseMaterial();
            }
        }

        return material;
    }


    /**
     * Lookup the Material information requested by player
     * Will either lookup the provided argument
     * Or lookup the ItemStack in the players main hand
     *
     * @param args
     * @param player
     */
    public static void lookupMaterialInformation(String[] args, Player player) {
        Material material;
        ItemStack data = null;

        if (args.length > 1) {
            material = Material.getMaterial(args[1]);
        } else {
            data = getItemStackInPlayersHand(player);
            material = data.getType();
        }
        if (material != null) {
            player.sendMessage(Static.getParkourString() + "Material: " + material.name());
            if (data != null) {
                player.sendMessage(Static.getParkourString() + "Data: " + data.toString());
            }
        } else {
            player.sendMessage(Static.getParkourString() + "Invalid material!");
        }
    }

    /**
     * Basically a fancy way of saying, is the server 1.13+
     * If the Version is weird (like older versions), we can assume it's under 13
     *
     * @return server version
     */
    public static int getMinorServerVersion() {
        String version = Bukkit.getBukkitVersion().split("\\.")[1];
        //TODO print these two values out
//        Bukkit.getBukkitVersion().split("\\.")[3].replace("v", "").replace("R", "").replace("_", ".");
        return Validation.isInteger(version) ? Integer.parseInt(version) : 12;
    }

    public static boolean doesGameModeEnumExist(String value) {
        boolean match = false;
        try {
            GameMode.valueOf(value.toUpperCase());
            match = true;
        } catch (IllegalArgumentException ignore) {}

        return match;
    }

    public static String getTimerSound() {
        String snd = "BLOCK_NOTE_BLOCK_PLING";
        if (getMinorServerVersion() <= 8) {
            snd = "NOTE_PLING";
        } else if (getMinorServerVersion() <= 12) {
            snd = "BLOCK_NOTE_PLING";
        }
        return snd;
    }

}
