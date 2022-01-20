package io.github.a5h73y.parkour.configuration.impl;

import java.io.File;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import de.leonhard.storage.util.FileUtils;

/**
 * Parkour strings configuration.
 * Stored in strings.yml and is used to offer customisable messages throughout the plugin.
 * Messages are sent to Players through the {@code TranslationUtils}.
 * Yaml config that automatically reloads itself when a change detected, order retained.
 */
public class StringsConfig extends Yaml {

	public StringsConfig(File file) {
		super(file.getName(), FileUtils.getParentDirPath(file), null,
				ReloadSettings.INTELLIGENT, ConfigSettings.SKIP_COMMENTS, DataType.SORTED);

		this.setDefault("Parkour.Prefix", "&0[&bParkour&0] &f");
		this.setDefault("Parkour.SignHeader", "&0[&bParkour&0]");
		this.setDefault("Parkour.ConfigReloaded", "The config has been reloaded.");
		this.setDefault("Parkour.SignRemoved", "Parkour sign removed!");
		this.setDefault("Parkour.SignCreated", "&b%VALUE% &fsign created.");
		this.setDefault("Parkour.Heading", "-- &9&l%VALUE% &r--");

		this.setDefault("Parkour.Join", "Joined &b%VALUE%");
		this.setDefault("Parkour.JoinLives", "&7You have &3%VALUE% &7lives on this course!");
		this.setDefault("Parkour.JoinTime", "&7You have &3%VALUE% &7to finish this course!");
		this.setDefault("Parkour.JoinLivesAndTime", "&7You have &3%MAXTIME% &7and &3%LIVES% &7lives to finish this course!");
		this.setDefault("Parkour.TimerStarted", "Timer started!");
		this.setDefault("Parkour.Restarting", "Restarting course");
		this.setDefault("Parkour.Leave", "You left &b%VALUE%");
		this.setDefault("Parkour.Created", "&b%VALUE% &fhas been created and selected!");
		this.setDefault("Parkour.CheckpointCreated", "Checkpoint &3%CHECKPOINT% &fhas been set on &b%COURSE%&f!");
		this.setDefault("Parkour.Selected", "Now editing &b%VALUE%");
		this.setDefault("Parkour.Deselected", "Finish editing.");
		this.setDefault("Parkour.WhenReady", "&7Once you have finished editing &b%VALUE%&7, enter &b/pa ready");
		this.setDefault("Parkour.Delete", "&b%VALUE% &fhas been deleted!");
		this.setDefault("Parkour.DeleteCheckpoint", "Checkpoint &b%CHECKPOINT% &fwas deleted on &b%COURSE%");
		this.setDefault("Parkour.Reset", "&b%VALUE% &fhas been reset!");
		this.setDefault("Parkour.JoinBroadcast", "&3%PLAYER% &fjoined &b%COURSE%&f!");
		this.setDefault("Parkour.FinishBroadcast", "&3%PLAYER% &ffinished &b%COURSE% &fwith &b%DEATHS% &fdeaths, in &b%TIME%&f!");
		this.setDefault("Parkour.FinishCourse1", "Finished &b%VALUE%&f!");
		this.setDefault("Parkour.FinishCourse2", "In &b%TIME%&f, dying &b%DEATHS% &ftimes");
		this.setDefault("Parkour.Lobby", "You have joined the lobby");
		this.setDefault("Parkour.LobbyOther", "You have joined the &b%VALUE% &flobby");
		this.setDefault("Parkour.JoinLocation", "You have returned to your original location");
		this.setDefault("Parkour.Continue", "Continuing progress on &b%VALUE%");
		this.setDefault("Parkour.TimeReset", "Your time has been restarted!");
		this.setDefault("Parkour.Teleport", "You have teleported to &b%VALUE%");
		this.setDefault("Parkour.MaxDeaths", "Sorry, you reached the maximum amount of deaths: &b%VALUE%");
		this.setDefault("Parkour.MaxTime", "Sorry, you have reached the maximum time limit of %VALUE%!");
		this.setDefault("Parkour.Die1", "You died! Going back to the start!");
		this.setDefault("Parkour.Die2", "You died! Going back to checkpoint &b%VALUE%");
		this.setDefault("Parkour.LifeCount", "&b%VALUE% &flives remaining!");
		this.setDefault("Parkour.Playing", " &b%PLAYER% &f- &8C: &7%COURSE% &8D: &7%DEATHS% &8T: &7%TIME%");
		this.setDefault("Parkour.Accept", "&7Enter &a/pa accept &7to accept.");
		this.setDefault("Parkour.RewardLevel", "Your level has been set to &b%LEVEL% &ffor completing &b%COURSE%&f!");
		this.setDefault("Parkour.RewardRank", "Your rank has been set to %VALUE%");
		this.setDefault("Parkour.RankInfo", "* For level &b%LEVEL% &fyou earn: %RANK%");
		this.setDefault("Parkour.RewardParkoins", "&b%AMOUNT% &fParkoins rewarded! New total: &7%TOTAL%");
		this.setDefault("Parkour.Countdown", "Starting in &b%VALUE% &fseconds...");
		this.setDefault("Parkour.Go", "Go!");
		this.setDefault("Parkour.BestTime", "Your new best time!");
		this.setDefault("Parkour.CourseRecord", "New course record!");
		this.setDefault("Parkour.LeaderboardHeading", "%COURSE% : Top %AMOUNT% results");
		this.setDefault("Parkour.LeaderboardEntry", "%POSITION%) &b%PLAYER% &fin &3%TIME%&f, dying &7%DEATHS% &ftimes");
		this.setDefault("Parkour.QuietOn", "Quiet Mode: &bON");
		this.setDefault("Parkour.QuietOff", "Quiet Mode: &bOFF");
		this.setDefault("Parkour.TestModeOn", "Test Mode: &bON&f. Simulating &b%VALUE%&f ParkourKit.");
		this.setDefault("Parkour.TestModeOff", "Test Mode: &bOFF");
		this.setDefault("Parkour.AlreadyCompleted", "You have already completed this course.");
		this.setDefault("Parkour.Question", "Please enter &a/pa yes &fto confirm, or &c/pa no &fto cancel.");

		this.setDefault("Parkour.Challenge.Created", "Challenge for &b%VALUE% &fcreated");
		this.setDefault("Parkour.Challenge.StartCommand", "When ready, enter &b/pa challenge start");
		this.setDefault("Parkour.Challenge.Wager", " &fwith a wager of &b%VALUE%&f!");
		this.setDefault("Parkour.Challenge.InviteSent", "A Challenge invite was sent to &b%VALUE%&f!");
		this.setDefault("Parkour.Challenge.InviteReceived", "You have been Challenged to &b%COURSE% &fby &b%PLAYER%&f!");
		this.setDefault("Parkour.Challenge.AcceptDecline", "To accept &a/pa accept &for &c/pa decline &fto decline.");
		this.setDefault("Parkour.Challenge.Joined", "You have joined a Challenge on &b%VALUE%&f. Please wait until the host starts.");
		this.setDefault("Parkour.Challenge.Terminated", "&b%VALUE% &fhas terminated the challenge!");
		this.setDefault("Parkour.Challenge.Forfeited", "&b%VALUE% &fhas forfeited the challenge. Complete the course to win!");
		this.setDefault("Parkour.Challenge.Quit", "You have forfeited the challenge. Another player must complete the course to win!");
		this.setDefault("Parkour.Challenge.Winner", "Congratulations! You won the challenge on &b%VALUE%!");
		this.setDefault("Parkour.Challenge.Loser", "&b%PLAYER% &fhas completed &b%COURSE% &fbefore you!");

		this.setDefault("Event.Join", "This server uses &bParkour &3%VALUE%");
		this.setDefault("Event.Checkpoint", "Checkpoint set to &b%CURRENT% &8/ &7%TOTAL%");
		this.setDefault("Event.FreeCheckpoints", "Checkpoint set");
		this.setDefault("Event.AllCheckpoints", "All checkpoints achieved!");
		this.setDefault("Event.HideAll1", "All players have magically reappeared!");
		this.setDefault("Event.HideAll2", "All players have magically disappeared!");
		this.setDefault("Event.Chat", "&0[&b%RANK%&0] &f%PLAYER%&0:&f %MESSAGE%");
		this.setDefault("Event.DefaultRank", "Newbie");

		this.setDefault("Error.NotOnCourse", "You are not on this course!");
		this.setDefault("Error.NotOnAnyCourse", "You are not on a course!");
		this.setDefault("Error.TooMany", "Too many arguments! (%VALUE%)");
		this.setDefault("Error.TooLittle", "Not enough arguments! (%VALUE%)");
		this.setDefault("Error.Exist", "This course already exists!");
		this.setDefault("Error.NoExist", "&b%VALUE% &fdoesn't exist!");
		this.setDefault("Error.Command", "Non-Parkour commands have been disabled!");
		this.setDefault("Error.Sign", "Non-Parkour signs have been disabled!");
		this.setDefault("Error.Selected", "You have not selected a course!");
		this.setDefault("Error.WrongWorld", "You are in the wrong world!");
		this.setDefault("Error.WorldTeleport", "Teleporting to a different world has been prevented!");
		this.setDefault("Error.RequiredLvl", "You require level &b%VALUE% &fto join!");
		this.setDefault("Error.NotReady", "This course is not ready for you to play yet!");
		this.setDefault("Error.NotReadyWarning", "This course is not ready yet.");
		this.setDefault("Error.SignProtected", "This sign is protected!");
		this.setDefault("Error.Syntax", "&cInvalid Syntax: &f/pa &9%COMMAND% &7%ARGUMENTS%");
		this.setDefault("Error.UnknownSignCommand", "Unknown sign command!");
		this.setDefault("Error.UnknownCommand", "Unknown command!");
		this.setDefault("Error.UnknownPlayer", "Unknown Parkour player!");
		this.setDefault("Error.UnknownMaterial", "Unknown Material: &4%VALUE%&c");
		this.setDefault("Error.UnknownPotionEffectType", "Unknown Potion Effect type: &4%VALUE%&c");
		this.setDefault("Error.UnknownLobby", "%VALUE% lobby does not exist!");
		this.setDefault("Error.UnknownWorld", "The requested world doesn't exist.");
		this.setDefault("Error.UnknownParkourKit", "Unknown ParkourKit.");
		this.setDefault("Error.UnknownCheckpoint", "Unknown or invalid Checkpoint.");
		this.setDefault("Error.Cheating1", "Please do not cheat.");
		this.setDefault("Error.Cheating2", "&lYou must achieve all &4&l%VALUE% &f&lcheckpoints!");
		this.setDefault("Error.Cooldown", "Slow down! Please wait &b%VALUE% &fmore seconds.");
		this.setDefault("Error.PrizeCooldown", "You have to wait &b%VALUE% &fbefore you can receive this prize again!");
		this.setDefault("Error.NoQuestion", "You have not been asked a question!");
		this.setDefault("Error.JoiningAnotherCourse", "You can not join another course while on a course.");
		this.setDefault("Error.PluginNotLinked", "&b%VALUE% &fhas not been linked.");
		this.setDefault("Error.NoPermission", "You do not have Permission: &b%VALUE%");
		this.setDefault("Error.InvalidAmount", "Amount needs to be numeric.");
		this.setDefault("Error.InvalidQuestionAnswer", "Invalid Question Answer.");
		this.setDefault("Error.QuestionAnswerChoices", "Please use either &a/pa yes &for &c/pa no");
		this.setDefault("Error.LimitExceeded", "The player limit for the course has been reached.");
		this.setDefault("Error.InvalidSession", "Your ParkourSession is invalid.");
		this.setDefault("Error.ChallengeOnly", "This Course is limited to Challenges only.");
		this.setDefault("Error.OnChallenge", "You are already in a Challenge.");
		this.setDefault("Error.InvalidValue", "Please enter a valid value.");

		this.setDefault("Help.Command", "&7/pa help &9%VALUE% &0: &7To learn more about the command.");
		this.setDefault("Help.Commands", "To display the commands menu, enter &b/pa cmds");
		this.setDefault("Help.ConsoleCommands", "To display all commands, enter &f/pac cmds");
		this.setDefault("Help.SignCommands", "To display the sign commands menu, enter &b/pa cmds signs");
		this.setDefault("Help.CommandSyntax", "&7Syntax: &f/pa %VALUE%");
		this.setDefault("Help.ConsoleCommandSyntax", "&7Syntax: &f%VALUE%");
		this.setDefault("Help.CommandExample", "&7Example: &f%VALUE%");
		this.setDefault("Help.CommandUsage", "&3/pa &b%COMMAND%&e%ARGUMENTS% &0: &f%TITLE%");
		this.setDefault("Help.SignUsage", "&b%COMMAND% &e%SHORTCUT% &0: &f%DESCRIPTION%");

		this.setDefault("ParkourTool.LastCheckpoint", "&7SHIFT + &6Right click to go back to last checkpoint");
		this.setDefault("ParkourTool.HideAll", "&7SHIFT + &6Right click to toggle visibility");
		this.setDefault("ParkourTool.HideAllEnabled", "&7SHIFT + &6Right click to toggle visibility");
		this.setDefault("ParkourTool.Leave", "&7SHIFT + &6Right click to leave course");
		this.setDefault("ParkourTool.Restart", "&7SHIFT + &6Right click to restart course");
		this.setDefault("ParkourTool.RestartConfirmation", "Please use the Restart Tool again to confirm.");
		this.setDefault("ParkourTool.Freedom", "&6Freedom Tool");
		this.setDefault("ParkourTool.Rockets", "&6Rocket Launcher");

		this.setDefault("Other.Kit", "&b%VALUE% &fParkourKit received!");
		this.setDefault("Other.PropertySet", "The &3%PROPERTY% &ffor &3%COURSE% &fwas set to &b%VALUE%&f!");

		this.setDefault("Lobby.Created", "&b%VALUE% &flobby created.");
		this.setDefault("Lobby.RequiredLevelSet", "You have set the required ParkourLevel to &b%VALUE%&f.");

		this.setDefault("Scoreboard.MainHeading", "&b&l== Parkour ==");
		this.setDefault("Scoreboard.TitleFormat", "&b%VALUE%");
		this.setDefault("Scoreboard.TextFormat", "&f%VALUE%");
		this.setDefault("Scoreboard.NotCompleted", "Not Completed");

		this.setDefault("Scoreboard.CourseNameTitle", "Course:");
		this.setDefault("Scoreboard.BestTimeEverTitle", "Best Time:");
		this.setDefault("Scoreboard.BestTimeEverNameTitle", "Best Player:");
		this.setDefault("Scoreboard.MyBestTimeTitle", "My Best Time:");
		this.setDefault("Scoreboard.CurrentDeathsTitle", "Current Deaths:");
		this.setDefault("Scoreboard.RemainingDeathsTitle", "Remaining Deaths:");
		this.setDefault("Scoreboard.CheckpointsTitle", "Checkpoints:");
		this.setDefault("Scoreboard.LiveTimerTitle", "Current Time:");
		this.setDefault("Scoreboard.TimeRemainingTitle", "Time Remaining:");

		this.setDefault("Economy.Insufficient", "You require at least &b%AMOUNT% &fbefore joining &b%COURSE%");
		this.setDefault("Economy.Fee", "&b%AMOUNT% &fhas been deducted from your balance for joining &b%COURSE%");
		this.setDefault("Economy.Reward", "You earned &b%AMOUNT% &ffor completing &b%COURSE%&f!");

		this.setDefault("Kit.Speed", "&bSpeed Block");
		this.setDefault("Kit.Climb", "&bClimb Block");
		this.setDefault("Kit.Launch", "&bLaunch Block");
		this.setDefault("Kit.Finish", "&bFinish Block");
		this.setDefault("Kit.Norun", "&bNoRun Block");
		this.setDefault("Kit.Nofall", "&bNoFall Block");
		this.setDefault("Kit.Nopotion", "&bNoPotion Block");
		this.setDefault("Kit.Sign", "&bSign");
		this.setDefault("Kit.Death", "&bDeath Block");
		this.setDefault("Kit.Bounce", "&bBounce Block");
		this.setDefault("Kit.Repulse", "&bRepulse Block");
		this.setDefault("Kit.Potion", "&bPotion Block");

		this.setDefault("Mode.Freedom.JoinText", "&6Freedom Mode &f- Right click: &2Save&f, Left click: &5Load");
		this.setDefault("Mode.Freedom.Save", "Position saved");
		this.setDefault("Mode.Freedom.Load", "Position loaded");
		this.setDefault("Mode.Rockets.JoinText", "Use the Rocket Launcher to launch yourself");
		this.setDefault("Mode.Rockets.Reloading", "Reloading rocket...");

		this.setDefault("PlaceholderAPI.UnknownCourse", "Unknown Course");
		this.setDefault("PlaceholderAPI.InvalidSyntax", "Invalid syntax");
		this.setDefault("PlaceholderAPI.NoTimeRecorded", "No time recorded");
		this.setDefault("PlaceholderAPI.CurrentCourseCompleted", "Yes");
		this.setDefault("PlaceholderAPI.CurrentCourseNotCompleted", "No");
		this.setDefault("PlaceholderAPI.TopTenResult", "&f%POSITION%) &b%PLAYER%&f in &a%TIME%");
		this.setDefault("PlaceholderAPI.CourseActive", "Active");
		this.setDefault("PlaceholderAPI.CourseInactive", "Inactive");

		this.setDefault("GUI.JoinCourses.Heading", "Parkour Courses");
		this.setDefault("GUI.JoinCourses.Setup.Line1", "         ");
		this.setDefault("GUI.JoinCourses.Setup.Line2", " ggggggg ");
		this.setDefault("GUI.JoinCourses.Setup.Line3", "  fp nl  ");
		this.setDefault("GUI.JoinCourses.Description", "&fJoin &b%VALUE%");
		this.setDefault("GUI.JoinCourses.Players", "Players: &b%VALUE%");
		this.setDefault("GUI.JoinCourses.Checkpoints", "Checkpoints: &b%VALUE%");
		this.setDefault("GUI.CourseSettings.Heading", "%VALUE% Settings");
		this.setDefault("GUI.CourseSettings.Setup.Line1", "  zxcv   ");
		this.setDefault("GUI.CourseSettings.Setup.Line2", " qwertyu ");
		this.setDefault("GUI.CourseSettings.Setup.Line3", " ioasdgh ");

		this.setDefault("Display.Day", "%VALUE% day");
		this.setDefault("Display.Days", "%VALUE% days");
		this.setDefault("Display.Hour", "%VALUE% hour");
		this.setDefault("Display.Hours", "%VALUE% hours");
		this.setDefault("Display.Minute", "%VALUE% minute");
		this.setDefault("Display.Minutes", "%VALUE% minutes");
		this.setDefault("Display.Second", "%VALUE% second");
		this.setDefault("Display.Seconds", "%VALUE% seconds");
	}
}
