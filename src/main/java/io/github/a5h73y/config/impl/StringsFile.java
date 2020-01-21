package io.github.a5h73y.config.impl;

import java.io.IOException;

import io.github.a5h73y.config.ParkourConfiguration;

public class StringsFile extends ParkourConfiguration {

	@Override
	protected String getFileName() {
		return "strings.yml";
	}

	@Override
	protected void initializeConfig() throws IOException {
		this.addDefault("Parkour.Prefix", "&0[&bParkour&0] &f");
		this.addDefault("Parkour.SignHeading", "&0[&bParkour&0]");
		this.addDefault("Event.Join", "This server uses &bParkour &3%VERSION%");
		this.addDefault("Event.Checkpoint", "Checkpoint set to ");
		this.addDefault("Event.AllCheckpoints", "All checkpoints achieved!");
		this.addDefault("Event.HideAll1", "All players have magically reappeared!");
		this.addDefault("Event.HideAll2", "All players have magically disappeared!");
		this.addDefault("Event.Chat", "&0[&b%RANK%&0] &f%PLAYER%&0:&f %MESSAGE%");
		this.addDefault("Event.DefaultRank", "Newbie");

		this.addDefault("Parkour.Join", "Joined &b%COURSE%");
		this.addDefault("Parkour.JoinLives", "&7You have &3%AMOUNT% &7lives on this course!");
		this.addDefault("Parkour.TimerStarted", "Timer started!");
		this.addDefault("Parkour.Restarting", "Restarting course");
		this.addDefault("Parkour.Leave", "You left &b%COURSE%");
		this.addDefault("Parkour.Created", "&b%COURSE% &fhas been created and selected!");
		this.addDefault("Parkour.Delete", "&b%COURSE% &fhas been deleted!");
		this.addDefault("Parkour.DeleteCheckpoint", "Checkpoint &b%CHECKPOINT% &fwas deleted on &b%COURSE%");
		this.addDefault("Parkour.Reset", "&b%COURSE% &fhas been reset!");
		this.addDefault("Parkour.Finish", "&b%COURSE% &fhas been set to finished!");
		this.addDefault("Parkour.FinishBroadcast", "&3%PLAYER% &ffinished &b%COURSE% &fwith &b%DEATHS% &fdeaths, in &b%TIME%&f!");
		this.addDefault("Parkour.FinishCourse1", "Finished &b%COURSE%&f!");
		this.addDefault("Parkour.FinishCourse2", "In %TIME%, dying %DEATHS% times");
		this.addDefault("Parkour.Lobby", "You have joined the lobby");
		this.addDefault("Parkour.LobbyOther", "You have joined the &b%LOBBY% &flobby");
		this.addDefault("Parkour.JoinLocation", "You have returned to your original location");
		this.addDefault("Parkour.Continue", "Continuing Parkour on &b%COURSE%");
		this.addDefault("Parkour.TimeReset", "&fYour time has been restarted!");
		this.addDefault("Parkour.Teleport", "You have teleported to &b%COURSE%");
		this.addDefault("Parkour.Invite.Send", "Invitation to &b%COURSE% &fsent to &b%TARGET%");
		this.addDefault("Parkour.Invite.Recieve1", "&b%PLAYER% &fhas invited you to &b%COURSE%");
		this.addDefault("Parkour.Invite.Recieve2", "To accept, type &3/pa join %COURSE%");
		this.addDefault("Parkour.MaxDeaths", "Sorry, you reached the maximum amount of deaths: &b%AMOUNT%");
		this.addDefault("Parkour.MaxTime", "Sorry, you have reached the maximum time limit of %TIME%!");
		this.addDefault("Parkour.Die1", "You died! Going back to the start!");
		this.addDefault("Parkour.Die2", "You died! Going back to checkpoint &b%POINT%");
		this.addDefault("Parkour.LifeCount", "&b%AMOUNT% &flives remaining!");
		this.addDefault("Parkour.Playing", " &b%PLAYER% &f- &8C: &7%COURSE% &8D: &7%DEATHS% &8T: &7%TIME%");
		this.addDefault("Parkour.Accept", "&7Enter &a/pa accept &7to accept.");
		this.addDefault("Parkour.RewardLevel", "Your level has been set to &b%LEVEL% &ffor completing &b%COURSE%&f!");
		this.addDefault("Parkour.RewardRank", "Your rank has been set to %RANK%");
		this.addDefault("Parkour.RankInfo", "* For level &b%LEVEL% &fyou earn: %RANK%");
		this.addDefault("Parkour.RewardParkoins", "&b%AMOUNT% &fParkoins rewarded! New total: &7%TOTAL%");
		this.addDefault("Parkour.SetMode", "Mode for &b%COURSE% &fset to &b%MODE%");
		this.addDefault("Parkour.Countdown", "Starting in &b%AMOUNT% &fseconds...");
		this.addDefault("Parkour.Go", "Go!");
		this.addDefault("Parkour.BestTime", "Your new best time!");
		this.addDefault("Parkour.CourseRecord", "New course record!");
		this.addDefault("Parkour.LeaderboardHeading", "%COURSE% : Top %AMOUNT% results");
		this.addDefault("Parkour.LeaderboardEntry", "%POSITION%) &b%PLAYER% &fin &3%TIME%&f, dying &7%DEATHS% &ftimes");
		this.addDefault("Parkour.QuietOn", "Quiet Mode: &bON");
		this.addDefault("Parkour.QuietOff", "Quiet Mode: &bOFF");
		this.addDefault("Parkour.Challenge.Receive", "You have been challenged by &b%PLAYER% &fto course &b%COURSE%");
		this.addDefault("Parkour.Challenge.Send", "You have challenged &b%PLAYER% &fto course &b%COURSE%");
		this.addDefault("Parkour.Challenge.Wager", " &fwith a wager of &b%AMOUNT%");
		this.addDefault("Parkour.Challenge.Terminated", "&b%PLAYER% &fhas terminated the challenge!");
		this.addDefault("Parkour.Challenge.Forfeited", "&b%PLAYER% &fhas forfeited the challenge. Complete the course to win!");
		this.addDefault("Parkour.Challenge.Quit", "You have forfeited the challenge. &b%PLAYER% &fmust complete the course to win!");
		this.addDefault("Parkour.Challenge.Winner", "Congratulations! You beat &b%PLAYER% &fat &b%COURSE%!");
		this.addDefault("Parkour.Challenge.Loser", "&b%PLAYER% &fhas completed &b%COURSE% &fbefore you!");

		this.addDefault("Error.NotOnCourse", "You are not on this course!");
		this.addDefault("Error.NotOnAnyCourse", "You are not on a course!");
		this.addDefault("Error.TooMany", "Too many arguments!");
		this.addDefault("Error.TooLittle", "Not enough arguments!");
		this.addDefault("Error.Exist", "This course already exists!");
		this.addDefault("Error.NoExist", "&b%COURSE% &fdoesn't exist!");
		this.addDefault("Error.Unknown", "Unknown course!");
		this.addDefault("Error.Command", "Non-Parkour commands have been disabled!");
		this.addDefault("Error.Sign", "Non-Parkour signs have been disabled!");
		this.addDefault("Error.Selected", "You have not selected a course!");
		this.addDefault("Error.WrongWorld", "You are in the wrong world!");
		this.addDefault("Error.WorldTeleport", "Teleporting to a different world has been cancelled");
		this.addDefault("Error.Something", "Something went wrong: &4%ERROR%");
		this.addDefault("Error.RequiredLvl", "You require level &b%LEVEL% &fto join!");
		this.addDefault("Error.Finished1", "This course is not ready for you to play yet!");
		this.addDefault("Error.Finished2", "WARNING: This course is not finished yet.");
		this.addDefault("Error.SignProtected", "This sign is protected!");
		this.addDefault("Error.Syntax", "&cInvalid Syntax: &f/pa &8%COMMAND% &7%ARGUMENTS%");
		this.addDefault("Error.UnknownSignCommand", "Unknown sign command!");
		this.addDefault("Error.UnknownCommand", "Unknown command!");
		this.addDefault("Error.UnknownPlayer", "This player does not exist!");
		this.addDefault("Error.Cheating1", "Please do not cheat.");
		this.addDefault("Error.Cheating2", "&lYou must achieve all &4%AMOUNT% &f&lcheckpoints!");
		this.addDefault("Error.Cooldown", "Slow down! Please wait &b%AMOUNT% &fmore seconds.");
		this.addDefault("Error.NotCompleted", "You have not yet completed &b%COURSE%&f!");
		this.addDefault("Error.AlreadyVoted", "You have already voted for &b%COURSE%&f!");
		this.addDefault("Error.PrizeCooldown", "You have to wait &b%TIME% &fbefore you can receive this prize again!");
		this.addDefault("Error.NoQuestion", "You have not been asked a question!");
		this.addDefault("Error.JoiningAnotherCourse", "You can not join another course while on a course.");

		this.addDefault("Help.Command", "&7/pa help &9%COMMAND% &0: &7To learn more about this command.");
		this.addDefault("Help.Commands", "&3/pa &bcmds &8: &fTo display the Parkour commands menu.");
		this.addDefault("Help.SignCommands", "&3/pa &bcmds signs &8: &fTo display the Parkour sign commands menu.");

		this.addDefault("Other.Item_LastCheckpoint", "&7SHIFT + &6Right click to go back to last checkpoint");
		this.addDefault("Other.Item_HideAll", "&7SHIFT + &6Right click to toggle visibility");
		this.addDefault("Other.Item_Leave", "&7SHIFT + &6Right click to leave course");
		this.addDefault("Other.Item_Restart", "&7SHIFT + &6Right click to restart course");
		this.addDefault("Other.Item_Book", "&6View course stats");
		this.addDefault("Other.Reload", "Config Reloaded!");
		this.addDefault("Other.Kit", "ParkourKit Given!");

		this.addDefault("Scoreboard.MainHeading", "&b&l== Parkour ==");
		this.addDefault("Scoreboard.TitleFormat", "&b%TITLE%");
		this.addDefault("Scoreboard.TextFormat", "&f%TEXT%");
		this.addDefault("Scoreboard.NotCompleted", "Not Completed");
		this.addDefault("Scoreboard.CourseTitle", "Course:");
		this.addDefault("Scoreboard.BestTimeTitle", "Best Time:");
		this.addDefault("Scoreboard.BestTimeNameTitle", "Best Player:");
		this.addDefault("Scoreboard.MyBestTimeTitle", "My Best Time:");
		this.addDefault("Scoreboard.CurrentTimeTitle", "Current Time:");
		this.addDefault("Scoreboard.CurrentDeathsTitle", "Current Deaths:");
		this.addDefault("Scoreboard.CheckpointsTitle", "Checkpoints:");

		this.addDefault("ParkourGUI.NextPage", "&bNext page &f>");
		this.addDefault("ParkourGUI.PreviousPage", "< &bPrevious page");
		this.addDefault("ParkourGUI.AllCourses.Title", "Courses - Page %PAGE%");
		this.addDefault("ParkourGUI.AllCourses.Description", "&fJoin &b%COURSE%");
		this.addDefault("ParkourGUI.AllCourses.Command", "pa join %COURSE%");

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

		this.addDefault("Mode.Freedom.ItemName", "&6Freedom Tool");
		this.addDefault("Mode.Freedom.JoinText", "&6Freedom Mode &f- Right click: &2Save&f, Left click: &5Load");
		this.addDefault("Mode.Freedom.Save", "Position saved");
		this.addDefault("Mode.Freedom.Load", "Position loaded");

		this.addDefault("Mode.Drunk.JoinText", "You feel strange...");
		this.addDefault("Mode.Darkness.JoinText", "It suddenly becomes dark...");
		this.addDefault("Mode.Rockets.JoinText", "Use the Rocket Launcher to launch yourself");
		this.addDefault("Mode.Rockets.ItemName", "Rocket Launcher");

		this.addDefault("NoPermission", "You do not have Permission: &b%PERMISSION%");

		this.options().copyDefaults(true);
	}
}
