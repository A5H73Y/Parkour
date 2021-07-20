package io.github.a5h73y.parkour.configuration.impl;

import io.github.a5h73y.parkour.configuration.ParkourConfiguration;

public class StringsConfig extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "strings.yml";
	}

	@Override
	protected void initializeConfig() {
		this.addDefault("Parkour.Prefix", "&0[&bParkour&0] &f");
		this.addDefault("Parkour.SignHeader", "&0[&bParkour&0]");
		this.addDefault("Parkour.ConfigReloaded", "The config has been reloaded.");
		this.addDefault("Parkour.SignRemoved", "Parkour sign removed!");
		this.addDefault("Parkour.SignCreated", "&b%VALUE% &fsign created.");
		this.addDefault("Parkour.Heading", "-- &9&l%VALUE% &r--");

		this.addDefault("Parkour.Join", "Joined &b%VALUE%");
		this.addDefault("Parkour.JoinLives", "&7You have &3%VALUE% &7lives on this course!");
		this.addDefault("Parkour.JoinTime", "&7You have &3%VALUE% &7to finish this course!");
		this.addDefault("Parkour.JoinLivesAndTime", "&7You have &3%MAXTIME% &7and &3%LIVES% &7lives to finish this course!");
		this.addDefault("Parkour.TimerStarted", "Timer started!");
		this.addDefault("Parkour.Restarting", "Restarting course");
		this.addDefault("Parkour.Leave", "You left &b%VALUE%");
		this.addDefault("Parkour.Created", "&b%VALUE% &fhas been created and selected!");
		this.addDefault("Parkour.CheckpointCreated", "Checkpoint &3%CHECKPOINT% &fhas been set on &b%COURSE%&f!");
		this.addDefault("Parkour.Selected", "Now editing &b%VALUE%");
		this.addDefault("Parkour.Deselected", "Finish editing.");
		this.addDefault("Parkour.WhenReady", "&7Once you have finished editing &b%VALUE%&7, enter &b/pa ready");
		this.addDefault("Parkour.Delete", "&b%VALUE% &fhas been deleted!");
		this.addDefault("Parkour.DeleteCheckpoint", "Checkpoint &b%CHECKPOINT% &fwas deleted on &b%COURSE%");
		this.addDefault("Parkour.Reset", "&b%VALUE% &fhas been reset!");
		this.addDefault("Parkour.JoinBroadcast", "&3%PLAYER% &fjoined &b%COURSE%&f!");
		this.addDefault("Parkour.FinishBroadcast", "&3%PLAYER% &ffinished &b%COURSE% &fwith &b%DEATHS% &fdeaths, in &b%TIME%&f!");
		this.addDefault("Parkour.FinishCourse1", "Finished &b%VALUE%&f!");
		this.addDefault("Parkour.FinishCourse2", "In &b%TIME%&f, dying &b%DEATHS% &ftimes");
		this.addDefault("Parkour.Lobby", "You have joined the lobby");
		this.addDefault("Parkour.LobbyOther", "You have joined the &b%VALUE% &flobby");
		this.addDefault("Parkour.JoinLocation", "You have returned to your original location");
		this.addDefault("Parkour.Continue", "Continuing progress on &b%VALUE%");
		this.addDefault("Parkour.TimeReset", "Your time has been restarted!");
		this.addDefault("Parkour.Teleport", "You have teleported to &b%VALUE%");
		this.addDefault("Parkour.MaxDeaths", "Sorry, you reached the maximum amount of deaths: &b%VALUE%");
		this.addDefault("Parkour.MaxTime", "Sorry, you have reached the maximum time limit of %VALUE%!");
		this.addDefault("Parkour.Die1", "You died! Going back to the start!");
		this.addDefault("Parkour.Die2", "You died! Going back to checkpoint &b%VALUE%");
		this.addDefault("Parkour.LifeCount", "&b%VALUE% &flives remaining!");
		this.addDefault("Parkour.Playing", " &b%PLAYER% &f- &8C: &7%COURSE% &8D: &7%DEATHS% &8T: &7%TIME%");
		this.addDefault("Parkour.Accept", "&7Enter &a/pa accept &7to accept.");
		this.addDefault("Parkour.RewardLevel", "Your level has been set to &b%LEVEL% &ffor completing &b%COURSE%&f!");
		this.addDefault("Parkour.RewardRank", "Your rank has been set to %VALUE%");
		this.addDefault("Parkour.RankInfo", "* For level &b%LEVEL% &fyou earn: %RANK%");
		this.addDefault("Parkour.RewardParkoins", "&b%AMOUNT% &fParkoins rewarded! New total: &7%TOTAL%");
		this.addDefault("Parkour.Countdown", "Starting in &b%VALUE% &fseconds...");
		this.addDefault("Parkour.Go", "Go!");
		this.addDefault("Parkour.BestTime", "Your new best time!");
		this.addDefault("Parkour.CourseRecord", "New course record!");
		this.addDefault("Parkour.LeaderboardHeading", "%COURSE% : Top %AMOUNT% results");
		this.addDefault("Parkour.LeaderboardEntry", "%POSITION%) &b%PLAYER% &fin &3%TIME%&f, dying &7%DEATHS% &ftimes");
		this.addDefault("Parkour.QuietOn", "Quiet Mode: &bON");
		this.addDefault("Parkour.QuietOff", "Quiet Mode: &bOFF");
		this.addDefault("Parkour.TestModeOn", "Test Mode: &bON&f. Simulating &b%VALUE%&f ParkourKit.");
		this.addDefault("Parkour.TestModeOff", "Test Mode: &bOFF");
		this.addDefault("Parkour.AlreadyCompleted", "You have already completed this course.");
		this.addDefault("Parkour.Question", "Please enter &a/pa yes &fto confirm, or &c/pa no &fto cancel.");

		this.addDefault("Parkour.Challenge.Created", "Challenge for &b%VALUE% &fcreated");
		this.addDefault("Parkour.Challenge.StartCommand", "When ready, enter &b/pa challenge start");
		this.addDefault("Parkour.Challenge.Wager", " &fwith a wager of &b%VALUE%&f!");
		this.addDefault("Parkour.Challenge.InviteSent", "A Challenge invite was sent to &b%VALUE%&f!");
		this.addDefault("Parkour.Challenge.InviteReceived", "You have been Challenged to &b%COURSE% &fby &b%PLAYER%&f!");
		this.addDefault("Parkour.Challenge.AcceptDecline", "To accept &a/pa accept &for &c/pa decline &fto decline.");
		this.addDefault("Parkour.Challenge.Joined", "You have joined a Challenge on &b%VALUE%&f. Please wait until the host starts.");
		this.addDefault("Parkour.Challenge.Terminated", "&b%VALUE% &fhas terminated the challenge!");
		this.addDefault("Parkour.Challenge.Forfeited", "&b%VALUE% &fhas forfeited the challenge. Complete the course to win!");
		this.addDefault("Parkour.Challenge.Quit", "You have forfeited the challenge. Another player must complete the course to win!");
		this.addDefault("Parkour.Challenge.Winner", "Congratulations! You won the challenge on &b%VALUE%!");
		this.addDefault("Parkour.Challenge.Loser", "&b%PLAYER% &fhas completed &b%COURSE% &fbefore you!");

		this.addDefault("Event.Join", "This server uses &bParkour &3%VALUE%");
		this.addDefault("Event.Checkpoint", "Checkpoint set to &b%CURRENT% &8/ &7%TOTAL%");
		this.addDefault("Event.FreeCheckpoints", "Checkpoint set");
		this.addDefault("Event.AllCheckpoints", "All checkpoints achieved!");
		this.addDefault("Event.HideAll1", "All players have magically reappeared!");
		this.addDefault("Event.HideAll2", "All players have magically disappeared!");
		this.addDefault("Event.Chat", "&0[&b%RANK%&0] &f%PLAYER%&0:&f %MESSAGE%");
		this.addDefault("Event.DefaultRank", "Newbie");

		this.addDefault("Error.NotOnCourse", "You are not on this course!");
		this.addDefault("Error.NotOnAnyCourse", "You are not on a course!");
		this.addDefault("Error.TooMany", "Too many arguments! (%VALUE%)");
		this.addDefault("Error.TooLittle", "Not enough arguments! (%VALUE%)");
		this.addDefault("Error.Exist", "This course already exists!");
		this.addDefault("Error.NoExist", "&b%VALUE% &fdoesn't exist!");
		this.addDefault("Error.Command", "Non-Parkour commands have been disabled!");
		this.addDefault("Error.Sign", "Non-Parkour signs have been disabled!");
		this.addDefault("Error.Selected", "You have not selected a course!");
		this.addDefault("Error.WrongWorld", "You are in the wrong world!");
		this.addDefault("Error.WorldTeleport", "Teleporting to a different world has been prevented!");
		this.addDefault("Error.RequiredLvl", "You require level &b%VALUE% &fto join!");
		this.addDefault("Error.NotReady", "This course is not ready for you to play yet!");
		this.addDefault("Error.NotReadyWarning", "This course is not ready yet.");
		this.addDefault("Error.SignProtected", "This sign is protected!");
		this.addDefault("Error.Syntax", "&cInvalid Syntax: &f/pa &8%COMMAND% &7%ARGUMENTS%");
		this.addDefault("Error.UnknownSignCommand", "Unknown sign command!");
		this.addDefault("Error.UnknownCommand", "Unknown command!");
		this.addDefault("Error.UnknownPlayer", "Unknown Parkour player!");
		this.addDefault("Error.UnknownMaterial", "Unknown Material: &4%VALUE%&c");
		this.addDefault("Error.UnknownPotionEffectType", "Unknown Potion Effect type: &4%VALUE%&c");
		this.addDefault("Error.UnknownLobby", "%VALUE% lobby does not exist!");
		this.addDefault("Error.UnknownWorld", "The requested world doesn't exist.");
		this.addDefault("Error.UnknownParkourKit", "Unknown ParkourKit.");
		this.addDefault("Error.UnknownCheckpoint", "Unknown or invalid Checkpoint.");
		this.addDefault("Error.Cheating1", "Please do not cheat.");
		this.addDefault("Error.Cheating2", "&lYou must achieve all &4&l%VALUE% &f&lcheckpoints!");
		this.addDefault("Error.Cooldown", "Slow down! Please wait &b%VALUE% &fmore seconds.");
		this.addDefault("Error.PrizeCooldown", "You have to wait &b%VALUE% &fbefore you can receive this prize again!");
		this.addDefault("Error.NoQuestion", "You have not been asked a question!");
		this.addDefault("Error.JoiningAnotherCourse", "You can not join another course while on a course.");
		this.addDefault("Error.AllowViaCommand", "Joining a course via the command has been disabled.");
		this.addDefault("Error.PluginNotLinked", "&b%VALUE% &fhas not been linked.");
		this.addDefault("Error.NoPermission", "You do not have Permission: &b%VALUE%");
		this.addDefault("Error.InvalidAmount", "Amount needs to be numeric.");
		this.addDefault("Error.InvalidQuestionAnswer", "Invalid Question Answer.");
		this.addDefault("Error.QuestionAnswerChoices", "Please use either &a/pa yes &for &c/pa no");
		this.addDefault("Error.LimitExceeded", "The player limit for the course has been reached.");
		this.addDefault("Error.InvalidSession", "Your ParkourSession is invalid.");
		this.addDefault("Error.ChallengeOnly", "This Course is limited to Challenges only.");
		this.addDefault("Error.OnChallenge", "You are already in a Challenge.");
		this.addDefault("Error.InvalidValue", "Please enter a valid value.");

		this.addDefault("Help.Command", "&7/pa help &9%VALUE% &0: &7To learn more about the command.");
		this.addDefault("Help.Commands", "To display the commands menu, enter &b/pa cmds");
		this.addDefault("Help.ConsoleCommands", "To display all commands, enter &f/pac cmds");
		this.addDefault("Help.SignCommands", "To display the sign commands menu, enter &b/pa cmds signs");
		this.addDefault("Help.CommandSyntax", "&7Syntax: &f/pa %VALUE%");
		this.addDefault("Help.ConsoleCommandSyntax", "&7Syntax: &f%VALUE%");
		this.addDefault("Help.CommandExample", "&7Example: &f%VALUE%");
		this.addDefault("Help.CommandUsage", "&3/pa &b%COMMAND%&e%ARGUMENTS% &0: &f%TITLE%");
		this.addDefault("Help.SignUsage", "&b%COMMAND% &e%SHORTCUT% &0: &f%DESCRIPTION%");

		this.addDefault("ParkourTool.LastCheckpoint", "&7SHIFT + &6Right click to go back to last checkpoint");
		this.addDefault("ParkourTool.HideAll", "&7SHIFT + &6Right click to toggle visibility");
		this.addDefault("ParkourTool.HideAllEnabled", "&7SHIFT + &6Right click to toggle visibility");
		this.addDefault("ParkourTool.Leave", "&7SHIFT + &6Right click to leave course");
		this.addDefault("ParkourTool.Restart", "&7SHIFT + &6Right click to restart course");
		this.addDefault("ParkourTool.Freedom", "&6Freedom Tool");
		this.addDefault("ParkourTool.Rockets", "&6Rocket Launcher");

		this.addDefault("Other.Kit", "&b%VALUE% &fParkourKit received!");
		this.addDefault("Other.PropertySet", "The &3%PROPERTY% &ffor &3%COURSE% &fwas set to &b%VALUE%&f!");

		this.addDefault("Lobby.Created", "&b%VALUE% &flobby created.");
		this.addDefault("Lobby.RequiredLevelSet", "You have set the required ParkourLevel to &b%VALUE%&f.");

		this.addDefault("Scoreboard.MainHeading", "&b&l== Parkour ==");
		this.addDefault("Scoreboard.TitleFormat", "&b%VALUE%");
		this.addDefault("Scoreboard.TextFormat", "&f%VALUE%");
		this.addDefault("Scoreboard.NotCompleted", "Not Completed");

		this.addDefault("Scoreboard.CourseNameTitle", "Course:");
		this.addDefault("Scoreboard.BestTimeEverTitle", "Best Time:");
		this.addDefault("Scoreboard.BestTimeEverNameTitle", "Best Player:");
		this.addDefault("Scoreboard.MyBestTimeTitle", "My Best Time:");
		this.addDefault("Scoreboard.CurrentDeathsTitle", "Current Deaths:");
		this.addDefault("Scoreboard.RemainingDeathsTitle", "Remaining Deaths:");
		this.addDefault("Scoreboard.CheckpointsTitle", "Checkpoints:");
		this.addDefault("Scoreboard.LiveTimerTitle", "Current Time:");
		this.addDefault("Scoreboard.TimeRemainingTitle", "Time Remaining:");

		this.addDefault("Economy.Insufficient", "You require at least &b%AMOUNT% &fbefore joining &b%COURSE%");
		this.addDefault("Economy.Fee", "&b%AMOUNT% &fhas been deducted from your balance for joining &b%COURSE%");
		this.addDefault("Economy.Reward", "You earned &b%AMOUNT% &ffor completing &b%COURSE%&f!");

		this.addDefault("Kit.Speed", "&bSpeed Block");
		this.addDefault("Kit.Climb", "&bClimb Block");
		this.addDefault("Kit.Launch", "&bLaunch Block");
		this.addDefault("Kit.Finish", "&bFinish Block");
		this.addDefault("Kit.Norun", "&bNoRun Block");
		this.addDefault("Kit.Nofall", "&bNoFall Block");
		this.addDefault("Kit.Nopotion", "&bNoPotion Block");
		this.addDefault("Kit.Sign", "&bSign");
		this.addDefault("Kit.Death", "&bDeath Block");
		this.addDefault("Kit.Bounce", "&bBounce Block");
		this.addDefault("Kit.Repulse", "&bRepulse Block");
		this.addDefault("Kit.Potion", "&bPotion Block");

		this.addDefault("Mode.Freedom.JoinText", "&6Freedom Mode &f- Right click: &2Save&f, Left click: &5Load");
		this.addDefault("Mode.Freedom.Save", "Position saved");
		this.addDefault("Mode.Freedom.Load", "Position loaded");
		this.addDefault("Mode.Rockets.JoinText", "Use the Rocket Launcher to launch yourself");
		this.addDefault("Mode.Rockets.Reloading", "Reloading rocket...");

		this.addDefault("PlaceholderAPI.InvalidSyntax", "Invalid syntax");
		this.addDefault("PlaceholderAPI.NoTimeRecorded", "No time recorded");
		this.addDefault("PlaceholderAPI.CurrentCourseCompleted", "Yes");
		this.addDefault("PlaceholderAPI.CurrentCourseNotCompleted", "No");
		this.addDefault("PlaceholderAPI.TopTenResult", "&f%POSITION%) &b%PLAYER%&f in &a%TIME%");

		this.addDefault("GUI.JoinCourses.Heading", "Parkour Courses");
		this.addDefault("GUI.JoinCourses.Setup.Line1", "         ");
		this.addDefault("GUI.JoinCourses.Setup.Line2", " ggggggg ");
		this.addDefault("GUI.JoinCourses.Setup.Line3", "  fp nl  ");
		this.addDefault("GUI.JoinCourses.Description", "&fJoin &b%VALUE%");
		this.addDefault("GUI.JoinCourses.Players", "Players: &b%VALUE%");
		this.addDefault("GUI.JoinCourses.Checkpoints", "Checkpoints: &b%VALUE%");
		this.addDefault("GUI.CourseSettings.Heading", "%VALUE% Settings");
		this.addDefault("GUI.CourseSettings.Setup.Line1", "   zxc   ");
		this.addDefault("GUI.CourseSettings.Setup.Line2", " qwertyu ");
		this.addDefault("GUI.CourseSettings.Setup.Line3", " ioasdgh ");

		this.addDefault("Display.Day", "%VALUE% day");
		this.addDefault("Display.Days", "%VALUE% days");
		this.addDefault("Display.Hour", "%VALUE% hour");
		this.addDefault("Display.Hours", "%VALUE% hours");
		this.addDefault("Display.Minute", "%VALUE% minute");
		this.addDefault("Display.Minutes", "%VALUE% minutes");
		this.addDefault("Display.Second", "%VALUE% second");
		this.addDefault("Display.Seconds", "%VALUE% seconds");

		this.options().copyDefaults(true);
	}
}
