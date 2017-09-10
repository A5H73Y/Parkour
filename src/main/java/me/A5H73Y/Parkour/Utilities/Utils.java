package me.A5H73Y.Parkour.Utilities;

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
import java.util.*;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Course.CourseMethods;
import me.A5H73Y.Parkour.Enums.QuestionType;
import me.A5H73Y.Parkour.Other.Question;
import me.A5H73Y.Parkour.Other.TimeObject;
import me.A5H73Y.Parkour.Other.ValidationMethods;

import me.A5H73Y.Parkour.Player.PlayerMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.connorlinfoot.bountifulapi.BountifulAPI;


/**
 * This work is licensed under a Creative Commons 
 * Attribution-NonCommercial-ShareAlike 4.0 International License. 
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author A5H73Y
 */
public final class Utils {

    /**
     * The string parameter will be matched to an entry in the Strings.yml
     * The boolean will decide to display the Parkour prefix
     *
     * @param string to translate
     * @param prefix display Parkour prefix
     * @return String of appropriate translation
     */
    public static String getTranslation(String string, boolean prefix) {
        if (string == null || string.isEmpty())
            return "Invalid translation.";

        String translated = Parkour.getParkourConfig().getStringData().getString(string);
        translated = translated != null ? colour(translated) : "String not found: " + string;
        return prefix ? Static.getParkourString().concat(translated) : translated;
    }

    /**
     * Override method, but with a default of an enabled Parkour prefix.
     *
     * @param string
     * @return String of appropriate translation
     */
    public static String getTranslation(String string) {
        return getTranslation(string, true);
    }

    /**
     * Return whether the player has a specific permission.
     * If they don't, a message will be sent alerting them.
     *
     * @param player
     * @param permission
     * @return whether they have permission
     */
    public static boolean hasPermission(Player player, String permission) {
        if (player.hasPermission(permission) || player.hasPermission("Parkour.*"))
            return true;

        player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permission));
        return false;
    }

    /**
     * Return whether the player has a specific permission OR has the branch permission.
     * Example "parkour.basic.join" OR "parkour.basic.*"
     *
     * @param player
     * @param permissionBranch i.e. "parkour.basic"
     * @param permission "join"
     * @return whether they have permission
     */
    public static boolean hasPermission(Player player, String permissionBranch, String permission) {
        if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*"))
            return true;

        player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
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
            player.sendMessage(Utils.getTranslation("Error.NoExist").replace("%COURSE%", courseName));
            return false;

        } else if (player.hasPermission(permissionBranch + ".*") || player.hasPermission(permissionBranch + "." + permission) || player.hasPermission("Parkour.*")) {
            return true;

        } else if (player.getName().equals(Parkour.getParkourConfig().getCourseData().getString(courseName.toLowerCase() + ".Creator"))) {
            return true;
        }

        player.sendMessage(Utils.getTranslation("NoPermission").replace("%PERMISSION%", permissionBranch + "." + permission));
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
    public static boolean hasSignPermission(Player player, SignChangeEvent sign, String permission){
        if (!Utils.hasPermission(player, "Parkour.Sign", permission)){
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
            sender.sendMessage(Utils.getTranslation("Error.TooMany") + " (" + desired + ")");
            sender.sendMessage(Utils.getTranslation("Help.Command").replace("%COMMAND%", Utils.standardizeText(args[0])));
            return false;

        } else if (args.length < desired) {
            sender.sendMessage(Utils.getTranslation("Error.TooLittle") + " (" + desired + ")");
            sender.sendMessage(Utils.getTranslation("Help.Command").replace("%COMMAND%", Utils.standardizeText(args[0])));
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
        if (text == null || text.length() == 0) {
            return text;
        }
        return text.substring(0, 1).toUpperCase().concat(text.substring(1).toLowerCase());
    }

    /**
     * Check if the argument is numeric
     * "1" - true, "Hi" - false
     *
     * @param text
     * @return whether the input is numeric
     */
    public static boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {}
        return false;
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
    public static String calculateTime(long millis) {
    	if (Parkour.getPlugin().getConfig().getBoolean("Other.Display.ShowMilliseconds")) {
    		return String.format("%02d:%02d:%02d.%03d", millis / (3600 * 1000),
    				millis / (60 * 1000) % 60,
    				millis / 1000 % 60,
    				millis % 1000);
    	}
        return String.format("%02d:%02d:%02d", millis / (3600 * 1000),
                millis / (60 * 1000) % 60,
                millis / 1000 % 60);
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
                Parkour.getPlugin().getLogger().warning(message);
                break;
            case 2:
                Parkour.getPlugin().getLogger().severe("! " + message);
                break;
            case 0:
            default:
                Parkour.getPlugin().getLogger().info(message);
                break;
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
        if (!Parkour.getPlugin().getConfig().getBoolean("Other.LogToFile"))
            return;

        try {
            File saveTo = new File(Parkour.getParkourConfig().getDataFolder(), "Parkour.log");
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
     * Validate ParkourKit set
     * Used for identifying what the problem with the ParkourKit is.
     *
     * @param args
     * @param player
     */
    public static void validateParkourKit(String[] args, Player player) {

        /*TODO - update me

        String name = (args.length == 2 ? "ParkourKit." + args[1].toLowerCase() : "DefaultBlocks");

        if (!Parkour.getPlugin().getConfig().contains(name + ".Death.Material")) {
            player.sendMessage("ParkourKit " + name + " doesn't exist!");
            return;
        }

        List<String> invalidTypes = new ArrayList<String>();
        String[] types = {"Death", "Finish", "Climb", "Launch", "Speed", "NoRun", "NoPotion", "Bounce"};

        for (String type : types){
            if (getParkourMaterial(name + "." + type + ".Material") == null)
                invalidTypes.add(type);
        }

        player.sendMessage(Static.getParkourString() + invalidTypes.size() + " problems with " + ChatColor.AQUA + name + ChatColor.WHITE + " found.");
        if (invalidTypes.size() > 0){
            String message = ChatColor.RED + "";
            for (String type : invalidTypes){
                message = message.concat(type + " ");
            }
            player.sendMessage(message);
        }
        */

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
        if (Static.containsQuiet(player.getName()))
            return;

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
        if (Static.containsQuiet(player.getName()))
            return;

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
        if (Static.containsQuiet(player.getName()))
            return;

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
        if (Static.containsQuiet(player.getName()))
            return;

        if (Static.getBountifulAPI() && attemptTitle) {
            BountifulAPI.sendActionBar(player, title);
        } else {
            player.sendMessage(Static.getParkourString() + title);
        }
    }

    /**
     * Delete command method
     * Possible arguments include Course, Checkpoint and Lobby
     * This will only add a Question object with the relevant data until the player confirms the action later on.
     *
     * @param args
     * @param player
     */
    public static void deleteCommand(String[] args, Player player) {
        if (args[1].equalsIgnoreCase("course")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(Utils.getTranslation("Error.Unknown"));
                return;
            }

            if (!ValidationMethods.deleteCourse(args[2], player))
                return;

            player.sendMessage(Static.getParkourString() + "You are about to delete course " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
            player.sendMessage(ChatColor.GRAY + "This will remove all information about the course ever existing, which includes all leaderboard data, course statistics and everything else the plugin knows about it.");
            player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
            Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_COURSE, args[2].toLowerCase()));

        } else if (args[1].equalsIgnoreCase("checkpoint")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(Utils.getTranslation("Error.Unknown"));
                return;
            }

            int checkpoints = Parkour.getParkourConfig().getCourseData().getInt(args[2].toLowerCase() + ".Points");
            // if it has no checkpoints
            if (checkpoints <= 0) {
                player.sendMessage(Static.getParkourString() + args[2] + " has no checkpoints!");
                return;
            }

            player.sendMessage(Static.getParkourString() + "You are about to delete checkpoint " + ChatColor.AQUA + checkpoints + ChatColor.WHITE + " for course " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
            player.sendMessage(ChatColor.GRAY + "Deleting a checkpoint will impact everybody that is currently playing on " + args[2] + ". You should not set a course to finished and then continue to make changes.");
            player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
            Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_CHECKPOINT, args[2].toLowerCase()));

        } else if (args[1].equalsIgnoreCase("lobby")) {
            if (!Parkour.getPlugin().getConfig().contains("Lobby." + args[2].toLowerCase() + ".World")) {
                player.sendMessage(Static.getParkourString() + "This lobby does not exist!");
                return;
            }

            if (!ValidationMethods.deleteLobby(args[2], player))
                return;

            player.sendMessage(Static.getParkourString() + "You are about to delete lobby " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
            player.sendMessage(ChatColor.GRAY + "Deleting a lobby will remove all information about it from the server. If any courses are linked to this lobby, they will be broken.");
            player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
            Static.addQuestion(player.getName(), new Question(QuestionType.DELETE_LOBBY, args[2].toLowerCase()));

        } else {
            player.sendMessage(invalidSyntax("delete", "(course / checkpoint / lobby) (name)"));
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
                player.sendMessage(Utils.getTranslation("Error.Unknown"));
                return;
            }

            player.sendMessage(Static.getParkourString() + "You are about to reset " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
            player.sendMessage(ChatColor.GRAY + "Resetting a course will delete all the statistics stored, which includes leaderboards and various parkour attributes. This will NOT affect the spawn / checkpoints.");
            player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
            Static.addQuestion(player.getName(), new Question(QuestionType.RESET_COURSE, args[2].toLowerCase()));

        } else if (args[1].equalsIgnoreCase("player")) {
            if (Bukkit.getPlayer(args[2]) == null || !Parkour.getParkourConfig().getUsersData().contains("PlayerInfo." + args[2])) {
                player.sendMessage(Utils.getTranslation("Error.UnknownPlayer"));
                return;
            }

            player.sendMessage(Static.getParkourString() + "You are about to reset player " + ChatColor.AQUA + args[2] + ChatColor.WHITE + "...");
            player.sendMessage(ChatColor.GRAY + "Resetting a player will delete all their times across all courses and delete all various parkour attributes.");
            player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
            Static.addQuestion(player.getName(), new Question(QuestionType.RESET_PLAYER, args[2]));

        } else if (args[1].equalsIgnoreCase("leaderboard")) {
            if (!CourseMethods.exist(args[2])) {
                player.sendMessage(Utils.getTranslation("Error.Unknown"));
                return;
            }

            player.sendMessage(Static.getParkourString() + "You are about to reset " + ChatColor.AQUA + args[2] + ChatColor.WHITE + " leaderboards...");
            player.sendMessage(ChatColor.GRAY + "Resetting the leaderboards will remove all times from the database for this course. This will NOT affect the course in any other way.");
            player.sendMessage("Please enter " + ChatColor.GREEN + "/pa yes" + ChatColor.WHITE + " to confirm!");
            Static.addQuestion(player.getName(), new Question(QuestionType.RESET_LEADERBOARD, args[2].toLowerCase()));

        } else {
            invalidSyntax("reset", "(course / player / leaderboard) (argument)");
        }
    }

    /**
     * Return the standardised heading used for Parkour
     * @param headingText
     * @return standardised Parkour heading
     */
    public static String getStandardHeading(String headingText){
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
        return amount < 1 ? 1 : amount > 64 ? 64 : amount;
    }

    /**
     * Display Leaderboards
     * Present the course times to the player.
     *
     * @param times
     * @param player
     * @param courseName
     */
    public static void displayLeaderboard(Player player, List<TimeObject> times, String courseName) {
        if (times.isEmpty()) {
            player.sendMessage(Static.getParkourString() + "No results were found!");
            return;
        }

        player.sendMessage(Utils.getStandardHeading(courseName + " : Top " + times.size() + " results"));
        for (int i = 0; i < times.size(); i++) {
            player.sendMessage(Utils.colour((i + 1) + ") &b" + times.get(i).getPlayer() + "&f in &3" + Utils.calculateTime(times.get(i).getTime()) + "&f, dying &7" + times.get(i).getDeaths() + " &ftimes"));
            //TODO - translate
        }
    }

    /**
     * Get a list of possible ParkourKit
     * @return Set<String> of ParkourKit names
     */
    public static Set<String> getParkourKitList() {
        return Parkour.getParkourConfig().getParkourKitData().getConfigurationSection("ParkourKit").getKeys(false);
    }

    /**
     * Get an ItemStack for the material with a display name of a translated message
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
        if (player.isOp())
            return true;

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

        if (displayMessage && !Static.containsQuiet(player.getName()))
            player.sendMessage(Utils.getTranslation("Error.Cooldown").replace("%AMOUNT%", String.valueOf(secondsToWait - secondsElapsed)));

        return false;
    }

    /**
     * Add a whitelisted command
     * @param args
     * @param player
     */
    public static void addWhitelistedCommand(String[] args, Player player) {
        if (Static.getWhitelistedCommands().contains(args[1].toLowerCase())) {
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
        Set<String> parkourKit = getParkourKitList();

        // specifying a kit
        if (args.length == 2) {
            String kitName = args[1].toLowerCase();
            if (!parkourKit.contains(kitName)) {
                sender.sendMessage(Static.getParkourString() + "This ParkourKit set does not exist!");
                return;
            }

            sender.sendMessage(Utils.getStandardHeading("ParkourKit: " + kitName));
            Set<String> materials = Parkour.getParkourConfig().getParkourKitData()
                    .getConfigurationSection("ParkourKit." + kitName).getKeys(false);

            for (String material : materials) {
                String action = Parkour.getParkourConfig().getParkourKitData()
                        .getString("ParkourKit." + kitName + "." + material + ".Action");

                sender.sendMessage(material + ": " + ChatColor.GRAY + action);
            }

        } else {
            //displaying all available kits
            sender.sendMessage(Utils.getStandardHeading(parkourKit.size() + " ParkourKit found"));
            for (String kit : parkourKit) {
                sender.sendMessage("* " + kit);
            }
        }
    }

    /**
     * Made because < 1.8
     * @param player
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Material getMaterialInPlayersHand(Player player) {
        ItemStack stack;

        try {
            stack = player.getInventory().getItemInMainHand();
        } catch (NoSuchMethodError ex) {
            stack = player.getItemInHand();
        }

        return stack.getType();
    }

    /**
     * Get all players that are online and on a course using the plugin
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
     * Option can be choasen whether to hide all online players, or just parkour players
     *
     * @param player
     * @param override
     */
    public static void toggleVisibility(Player player, boolean override) {
        boolean enabled = override || Static.containsHidden(player.getName());
        List<Player> playerScope;

        if (Parkour.getPlugin().getConfig().getBoolean("OnJoin.Item.HideAll.Global") || override) {
            playerScope = (List<Player>) Bukkit.getOnlinePlayers();
        } else {
            playerScope = Utils.getOnlineParkourPlayers();
        }

        for (Player players : playerScope) {
            if (enabled)
                player.showPlayer(players);
            else
                player.hidePlayer(players);
        }
        if (enabled) {
            Static.removeHidden(player.getName());
            player.sendMessage(Utils.getTranslation("Event.HideAll1"));
        } else {
            Static.addHidden(player.getName());
            player.sendMessage(Utils.getTranslation("Event.HideAll2"));
        }
    }

    public static void forceVisible(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.showPlayer(player);
        }
    }
}
