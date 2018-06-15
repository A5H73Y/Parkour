package me.A5H73Y.Parkour.Other;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configurations {

    private File dataFolder, courseFile, stringFile, usersFile, invFile, checkFile, econFile, kitFile;
    private FileConfiguration courseData, stringData, usersData, invData, checkData, econData, kitData;

    /**
     * This no longer generates the default config.yml to allow the ability of creating a backup of the existing config.
     *
     */
    public Configurations(){
        Parkour.getPlugin().saveConfig();

        dataFolder = Parkour.getPlugin().getDataFolder();

        courseFile = new File(dataFolder, "courses.yml");
        courseData = new YamlConfiguration();
        stringFile = new File(dataFolder, "strings.yml");
        stringData = new YamlConfiguration();
        usersFile = new File(dataFolder, "players.yml");
        usersData = new YamlConfiguration();
        invFile = new File(dataFolder, "inventory.yml");
        invData = new YamlConfiguration();
        checkFile = new File(dataFolder, "checkpoints.yml");
        checkData = new YamlConfiguration();
        kitFile = new File(dataFolder, "parkourkit.yml");
        kitData = new YamlConfiguration();

        // courses
        if (!courseFile.exists()) {
            try {
                courseFile.createNewFile();
                Utils.log("Created courses.yml");
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.log("Failed!");
            }
        }

        // Strings
        if (!stringFile.exists()) {
            try {
                stringFile.createNewFile();
                Utils.log("Created strings.yml");
                saveStrings();
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.log("Failed!");
            }
        }

        // users
        if (!usersFile.exists()) {
            try {
                usersFile.createNewFile();
                Utils.log("Created players.yml");
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.log("Failed!");
            }
        }

        // inventory
        if (!invFile.exists()) {
            try {
                invFile.createNewFile();
                Utils.log("Created inventory.yml");
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.log("Failed!");
            }
        }

        // checkpoints
        if (!checkFile.exists()) {
            try {
                checkFile.createNewFile();
                Utils.log("Created checkpoints.yml");
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.log("Failed!");
            }
        }

        // parkourkit
        if (!kitFile.exists()) {
            try {
                kitFile.createNewFile();
                Utils.log("Created parkourkit.yml");
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.log("Failed!");
            }
        }

        try{
            courseData.load(courseFile);
            stringData.load(stringFile);
            usersData.load(usersFile);
            invData.load(invFile);
            checkData.load(checkFile);
            kitData.load(kitFile);

        } catch (Exception ex){
            Utils.log("Failed loading config: " + ex.getMessage());
            ex.printStackTrace();
        }

        saveAll();
    }

    public void saveAll(){
        saveCheck();
        saveCourses();
        //saveEcon();
        saveInv();
        saveStrings();
        saveUsers();
        saveParkourKit();
        Parkour.getPlugin().saveConfig();
    }

    public void reload(){
        Parkour.getPlugin().reloadConfig();

        courseData = YamlConfiguration.loadConfiguration(courseFile);
        stringData = YamlConfiguration.loadConfiguration(stringFile);
        usersData = YamlConfiguration.loadConfiguration(usersFile);
        invData = YamlConfiguration.loadConfiguration(invFile);
        checkData = YamlConfiguration.loadConfiguration(checkFile);
        kitData = YamlConfiguration.loadConfiguration(kitFile);
        if (Static.getEconomy())
            econData = YamlConfiguration.loadConfiguration(econFile);
    }

    public FileConfiguration getCheckData() {
        return checkData;
    }

    public FileConfiguration getCourseData() {
        return courseData;
    }

    public FileConfiguration getStringData() {
        return stringData;
    }

    public FileConfiguration getUsersData() {
        return usersData;
    }

    public FileConfiguration getInvData() {
        return invData;
    }

    public FileConfiguration getEconData() {
        return econData;
    }

    public FileConfiguration getParkourKitData() {
        return kitData;
    }

    public File getDataFolder(){
        return dataFolder;
    }

    public void saveCourses() {
        try {
            courseData.addDefault("Courses", new ArrayList<String>());
            courseData.options().copyDefaults(true);
            courseData.save(courseFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveUsers() {
        try {
            usersData.save(usersFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveInv() {
        try {
            invData.save(invFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveCheck() {
        try {
            checkData.save(checkFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveEcon() {
        try {
            econData.save(econFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveParkourKit() {
        try {
            if (!kitData.contains("ParkourKit.default")) {
                ParkourKit.createStandardKit(kitData, "default");
            }
            kitData.save(kitFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getAllCourses() {
        return courseData.getStringList("Courses");
    }

    public void initiateEconomy(){
        Static.enableEconomy();
        econFile = new File(Parkour.getPlugin().getDataFolder(), "economy.yml");
        econData = new YamlConfiguration();

        if (!econFile.exists()) {
            try {
                econFile.createNewFile();
                Utils.log("Created economy.yml");
                saveEcon();
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.log("Failed.");
            }
        }
        try {
            econData.load(econFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Default Translations
     * Seperated and organised into sections of the plugins
     */
    private void saveStrings() {
        try{
            stringData.addDefault("Parkour.Prefix", "&0[&bParkour&0] &f");
            stringData.addDefault("Parkour.SignHeading", "&0[&bParkour&0]");
            stringData.addDefault("Event.Join", "This server uses &bParkour &3%VERSION%");
            stringData.addDefault("Event.Checkpoint", "Checkpoint set to ");
            stringData.addDefault("Event.AllCheckpoints", "All checkpoints achieved!");
            stringData.addDefault("Event.HideAll1", "All players have magically reappeared!");
            stringData.addDefault("Event.HideAll2", "All players have magically disappeared!");
            stringData.addDefault("Event.Chat", "&0[&b%RANK%&0] &f%PLAYER%&0:&f %MESSAGE%");
            stringData.addDefault("Event.DefaultRank", "Newbie");

            stringData.addDefault("Parkour.Join", "Joined &b%COURSE%");
            stringData.addDefault("Parkour.JoinLives", "&7You have &3%AMOUNT% &7lives on this course!");
            stringData.addDefault("Parkour.TimerStarted", "Timer started!");
            stringData.addDefault("Parkour.Leave", "You left &b%COURSE%");
            stringData.addDefault("Parkour.Created", "&b%COURSE% &fhas been created and selected!");
            stringData.addDefault("Parkour.Delete", "&b%COURSE% &fhas been deleted!");
            stringData.addDefault("Parkour.DeleteCheckpoint", "Checkpoint &b%CHECKPOINT% &fwas deleted on &b%COURSE%");
            stringData.addDefault("Parkour.Reset", "&b%COURSE% &fhas been reset!");
            stringData.addDefault("Parkour.Finish", "&b%COURSE% &fhas been set to finished!");
            stringData.addDefault("Parkour.FinishBroadcast", "&3%PLAYER% &ffinished &b%COURSE% &fwith &b%DEATHS% &fdeaths, in &b%TIME%&f!");
            stringData.addDefault("Parkour.FinishCourse1", "Finished &b%COURSE%&f!");
            stringData.addDefault("Parkour.FinishCourse2", "In %TIME%, dying %DEATHS% times");
            stringData.addDefault("Parkour.Lobby", "You have joined the lobby");
            stringData.addDefault("Parkour.LobbyOther", "You have joined the &b%LOBBY% &flobby");
            stringData.addDefault("Parkour.Continue", "Continuing Parkour on &b%COURSE%");
            stringData.addDefault("Parkour.TimeReset", "&fYour time has been restarted!");
            stringData.addDefault("Parkour.Teleport", "You have teleported to &b%COURSE%");
            stringData.addDefault("Parkour.Invite.Send", "Invitation to &b%COURSE% &fsent to &b%TARGET%");
            stringData.addDefault("Parkour.Invite.Recieve1", "&b%PLAYER% &fhas invited you to &b%COURSE%");
            stringData.addDefault("Parkour.Invite.Recieve2", "To accept, type &3/pa join %COURSE%");
            stringData.addDefault("Parkour.MaxDeaths", "Sorry, you reached the maximum amount of deaths: &b%AMOUNT%");
            stringData.addDefault("Parkour.Die1", "You died! Going back to the start!");
            stringData.addDefault("Parkour.Die2", "You died! Going back to checkpoint &b%POINT%");
            stringData.addDefault("Parkour.LifeCount", "&b%AMOUNT% &flives remaining!");
            stringData.addDefault("Parkour.Playing", " &b%PLAYER% &f- &8C: &7%COURSE% &8D: &7%DEATHS% &8T: &7%TIME%");
            stringData.addDefault("Parkour.ChallengeReceive", "You have been challenged by &b%PLAYER% &fto course &b%COURSE%");
            stringData.addDefault("Parkour.ChallengeSend", "You have challenged &b%PLAYER% &fto course &b%COURSE%");
            stringData.addDefault("Parkour.Accept", "&7Enter &a/pa accept &7to accept.");
            stringData.addDefault("Parkour.RewardLevel", "Your level has been set to &b%LEVEL% &ffor completing &b%COURSE%&f!");
            stringData.addDefault("Parkour.RewardRank", "Your rank has been set to %RANK%");
            stringData.addDefault("Parkour.RewardParkoins", "&b%AMOUNT% &fParkoins rewarded! New total: &7%TOTAL%");
            stringData.addDefault("Parkour.SetMode", "Mode for &b%COURSE% &fset to &b%MODE%");

            stringData.addDefault("Error.NotOnCourse", "You are not on this course!");
            stringData.addDefault("Error.NotOnAnyCourse", "You are not on a course!");
            stringData.addDefault("Error.TooMany", "Too many arguments!");
            stringData.addDefault("Error.TooLittle", "Not enough arguments!");
            stringData.addDefault("Error.Exist", "This course already exists!");
            stringData.addDefault("Error.NoExist", "&b%COURSE% &fdoesn't exist!");
            stringData.addDefault("Error.Unknown", "Unknown course!");
            stringData.addDefault("Error.Command", "Non-Parkour commands have been disabled!");
            stringData.addDefault("Error.Sign", "Non-Parkour signs have been disabled!");
            stringData.addDefault("Error.Selected", "You have not selected a course!");
            stringData.addDefault("Error.WrongWorld", "You are in the wrong world!");
            stringData.addDefault("Error.WorldTeleport", "Teleporting to a different world has been cancelled");
            stringData.addDefault("Error.Something", "Something went wrong: &4%ERROR%");
            stringData.addDefault("Error.RequiredLvl", "You require level &b%LEVEL% &fto join!");
            stringData.addDefault("Error.Finished1", "This course is not ready for you to play yet!");
            stringData.addDefault("Error.Finished2", "WARNING: This course is not finished yet.");
            stringData.addDefault("Error.SignProtected", "This sign is protected!");
            stringData.addDefault("Error.Syntax", "&cInvalid Syntax: &f/pa &8%COMMAND% &7%ARGUMENTS%");
            stringData.addDefault("Error.UnknownSignCommand", "Unknown sign command!");
            stringData.addDefault("Error.UnknownCommand", "Unknown command!");
            stringData.addDefault("Error.UnknownPlayer", "This player does not exist!");
            stringData.addDefault("Error.Cheating1", "Please do not cheat.");
            stringData.addDefault("Error.Cheating2", "&lYou must achieve all &4%AMOUNT% &f&lcheckpoints!");
            stringData.addDefault("Error.Cooldown", "Slow down! Please wait &b%AMOUNT% &fmore seconds.");
            stringData.addDefault("Error.NotCompleted", "You have not yet completed &b%COURSE%&f!");
            stringData.addDefault("Error.AlreadyVoted", "You have already voted for &b%COURSE%&f!");
            stringData.addDefault("Error.PrizeCooldown", "You have to wait &b%TIME% &fbefore you can receive this prize again!");

            stringData.addDefault("Help.Command", "&7/pa help &9%COMMAND% &0: &7To learn more about this command.");
            stringData.addDefault("Help.Commands", "&3/pa &bcmds &8: &fTo display the Parkour commands menu.");
            stringData.addDefault("Help.SignCommands", "&3/pa &bcmds signs &8: &fTo display the Parkour sign commands menu.");

            stringData.addDefault("Other.Item_LastCheckpoint", "&7SHIFT + &6Right click to go back to last checkpoint");
            stringData.addDefault("Other.Item_HideAll", "&7SHIFT + &6Right click to toggle visibility");
            stringData.addDefault("Other.Item_Leave", "&7SHIFT + &6Right click to leave course");
            stringData.addDefault("Other.Item_Book", "&6View course stats");
            stringData.addDefault("Other.Reload", "Config Reloaded!");
            stringData.addDefault("Other.Kit", "ParkourKit Given!");

            stringData.addDefault("Economy.Insufficient", "You require at least &b%AMOUNT% &fbefore joining &b%COURSE%");
            stringData.addDefault("Economy.Fee", "&b%AMOUNT% &fhas been deducted from your balance for joining &b%COURSE%");
            stringData.addDefault("Economy.Reward", "You earned &b%AMOUNT% &ffor completing &b%COURSE%&f!");

            stringData.addDefault("Kit.Speed", "&bSpeed Block");
            stringData.addDefault("Kit.Climb", "&bClimb Block");
            stringData.addDefault("Kit.Launch", "&bLaunch Block");
            stringData.addDefault("Kit.Finish", "&bFinish Block");
            stringData.addDefault("Kit.Norun", "&bNoRun Block");
            stringData.addDefault("Kit.Nofall", "&bNoFall Block");
            stringData.addDefault("Kit.Nopotion", "&bNoPotion Block");
            stringData.addDefault("Kit.Sign", "&bSign");
            stringData.addDefault("Kit.Death", "&bDeath Block");
            stringData.addDefault("Kit.Bounce", "&bBounce Block");
            stringData.addDefault("Kit.Repulse", "&bRepulse Block");

            stringData.addDefault("Mode.Spectate.AlertPlayer", "You are now being spectated by &b%PLAYER%");
            stringData.addDefault("Mode.Spectate.FinishedSpec", "You are no longer being spectated");
            stringData.addDefault("Mode.Freedom.ItemName", "&6Freedom Tool");
            stringData.addDefault("Mode.Freedom.JoinText", "&6Freedom Mode &f- Right click: &2Save&f, Left click: &5Load");
            stringData.addDefault("Mode.Freedom.Save", "Position saved");
            stringData.addDefault("Mode.Freedom.Load", "Position loaded");

            stringData.addDefault("Mode.Drunk.JoinText", "You feel strange...");
            stringData.addDefault("Mode.Darkness.JoinText", "It suddenly becomes dark...");

            stringData.addDefault("NoPermission", "You do not have Permission: &b%PERMISSION%");
            stringData.options().copyDefaults(true);
            stringData.save(stringFile);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Default configuration options
     */
    public void setupConfig(){
        FileConfiguration config = Parkour.getPlugin().getConfig();

        config.options().header("==== Parkour Config ==== #");

        config.addDefault("OnJoin.SetGamemode", 0);
        config.addDefault("OnJoin.EnforceWorld", false);
        config.addDefault("OnJoin.EnforceFinished", true);
        config.addDefault("OnJoin.AllowViaCommand", true);
        config.addDefault("OnJoin.FillHealth", true);
        config.addDefault("OnJoin.TreatFirstCheckpointAsStart", false);
        config.addDefault("OnJoin.Item.LastCheckpoint.Material", "ARROW");
        config.addDefault("OnJoin.Item.HideAll.Material", "BONE");
        config.addDefault("OnJoin.Item.HideAll.Global", true);
        config.addDefault("OnJoin.Item.Leave.Material", "SAPLING");

        config.addDefault("OnCourse.UseParkourKit", true);
        config.addDefault("OnCourse.DieInLiquid", false);
        config.addDefault("OnCourse.DieInVoid", false);
        config.addDefault("OnCourse.EnforceParkourCommands.Enabled", true);
        String[] whitelisted = {"login"};
        config.addDefault("OnCourse.EnforceParkourCommands.Whitelist", whitelisted);
        config.addDefault("OnCourse.EnforceParkourSigns", true);
        config.addDefault("OnCourse.DisablePlayerDamage", false);
        config.addDefault("OnCourse.MaxFallTicks", 80);
        config.addDefault("OnCourse.AllowTrails", false);
        config.addDefault("OnCourse.DisableItemDrop", false);
        config.addDefault("OnCourse.DisableItemPickup", false);
        config.addDefault("OnCourse.PreventPlateStick", true);
        config.addDefault("OnCourse.AttemptLessChecks", false);
        config.addDefault("OnCourse.DisplayLiveTime", false);
        config.addDefault("OnCourse.DisableFly", true);
        config.addDefault("OnCourse.SneakToInteractItems", true);
        config.addDefault("OnCourse.AdminPlaceBreakBlocks", true);
        config.addDefault("OnCourse.PreventOpeningOtherInventories", false);
        config.addDefault("OnCourse.PreventAttackingEntities", false);

        config.addDefault("OnFinish.EnforceCompletion", true);
        config.addDefault("OnFinish.TeleportAway", true);
        config.addDefault("OnFinish.SetGamemode", 0);
        config.addDefault("OnFinish.BroadcastLevel", 3);
        config.addDefault("OnFinish.Prize.Enabled", true);
        config.addDefault("OnFinish.DefaultPrize.Material", "DIAMOND");
        config.addDefault("OnFinish.DefaultPrize.Amount", 1);
        config.addDefault("OnFinish.DefaultPrize.XP", 0);
        config.addDefault("OnFinish.TeleportDelay", 0);
        config.addDefault("OnFinish.DisplayStats", true);
        config.addDefault("OnFinish.UpdatePlayerDatabaseTime", false);

        config.addDefault("OnLeave.TeleportToLinkedLobby", false);

        config.addDefault("OnDie.SetXPBarToDeathCount", false);
        config.addDefault("OnDie.ResetTimeWithNoCheckpoint", false);

        config.addDefault("OnLeaveServer.LeaveCourse", false);
        config.addDefault("OnLeaveServer.TeleportToLastCheckpoint", false);

        config.addDefault("ParkourModes.Challenge.HidePlayers", true);
        config.addDefault("ParkourModes.Speedy.SetSpeed", 0.8);
        config.addDefault("ParkourModes.Speedy.ResetSpeed", 0.2);
        config.addDefault("ParkourModes.Moon.Strength", 5);

        config.addDefault("DisplayTitle.FadeIn", 5);
        config.addDefault("DisplayTitle.Stay", 20);
        config.addDefault("DisplayTitle.FadeOut", 5);
        config.addDefault("DisplayTitle.JoinCourse", true);
        config.addDefault("DisplayTitle.Checkpoint", true);
        config.addDefault("DisplayTitle.RewardLevel", true);
        config.addDefault("DisplayTitle.Death", true);
        config.addDefault("DisplayTitle.Leave", true);
        config.addDefault("DisplayTitle.Finish", true);

        config.addDefault("Other.Economy.Enabled", true);
        config.addDefault("Other.BountifulAPI.Enabled", true);
        config.addDefault("Other.PlaceholderAPI.Enabled", true);
        config.addDefault("Other.CheckForUpdates", true);
        config.addDefault("Other.LogToFile", true);
        config.addDefault("Other.Parkour.ChatRankPrefix.Enabled", false);
        config.addDefault("Other.Parkour.ChatRankPrefix.OverrideChat", true);
        config.addDefault("Other.Parkour.SignProtection", true);
        config.addDefault("Other.Parkour.InventoryManagement", true);
        config.addDefault("Other.Parkour.SignPermissions", false);
        config.addDefault("Other.Parkour.CommandPermissions", false);
        config.addDefault("Other.Display.JoinWelcomeMessage", true);
        config.addDefault("Other.Display.LevelReward", true);
        config.addDefault("Other.Display.ShowMilliseconds", false);
        config.addDefault("Other.Display.PrizeCooldown", true);
        config.addDefault("Other.EnforceSafeCheckpoints", true);

        config.addDefault("SQLite.PathOverride", "");
        config.addDefault("MySQL.Use", false);
        config.addDefault("MySQL.Host", "Host");
        config.addDefault("MySQL.Port", 3306);
        config.addDefault("MySQL.User", "Username");
        config.addDefault("MySQL.Password", "Password");
        config.addDefault("MySQL.Database", "Database");
        config.addDefault("MySQL.Table", "Table");

        config.addDefault("Version", Double.parseDouble(Parkour.getPlugin().getDescription().getVersion()));

        config.addDefault("Lobby.Set", false);
        config.addDefault("Lobby.EnforceWorld", false);
        config.options().copyDefaults(true);
        Parkour.getPlugin().saveConfig();
    }
}