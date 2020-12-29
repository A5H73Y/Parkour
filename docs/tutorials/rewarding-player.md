Rewarding the Player
======

There are several ways we can reward the player for completing a Course. We are able to reward the Player with ParkourLevels that allow them to unlock new courses, or achieve a ParkourRank that showcases how far they've progressed. All can be configured to match any server requirements, and even allow developers to natively add their own functionality.

## Course Prizes

You can reward the player several ways after they complete a Course, this can be configured by starting the Prize conversation which is initiated by entering `/pa prize (course)`.

This will start a conversation of what you want the prize to be, these can be stacked meaning you can have all options happen when the player finishes a Course.

![Course Prize](https://i.imgur.com/syeM4Cn.jpg "Course Prize")

This was made to be as simple as possible, breaking the stages up so you answer each question with what you want. Follow each conversation as demonstrated below:

### Material

You can provide the player with a Material for completing the Course, and you can specify the amount. Once the player completes the course, the item and amount will be inserted into their inventory after their original inventory is restored.

![Material Prize](https://i.imgur.com/xgLug6k.jpg "Material Prize")

### Commands

You are able to execute multiple commands when a player completes the Course, simply choose the "command" option and it will ask you for the command to execute. Note that these commands will be run by the server, so will have elevated permission to run anything.

You can use the placeholder `%PLAYER%` to insert the players name at execution, it will have to be exactly that or it will not be recognized by the plugin. As a basic example I have set the command prize to `give %PLAYER% minecraft:torch 10` when they complete, the plugin will give you the option to run the command to test to see if it works correctly, by inserting your name into the placeholder.

![Command Prize](https://i.imgur.com/i9Vfb98.jpg "Command Prize")

You can see that once I entered 'yes', it executed the command which gave the player 10 torches, make sure you use `%PLAYER%` exactly so it replaces that with the actual players name.

You will be able to run this conversation as many times as you want to create a list of commands that get executed once a player completes the Course.

### XP

You can give the player Minecraft XP when they complete the Course, which is as simple as entering a positive number into the conversation.

![XP Prize](https://i.imgur.com/43qKmUn.jpg "XP Prize")

### Ecomomy

If the plugin is linked to Vault, you will have an option to specify a numeric amount to reward the player with on completion.

TODO image

## Rewarding ParkourLevels

There are currently 3 ways to achieve a new ParkourLevel.

### /pa rewardlevel (course) (level)

This means when you complete the specified Course, you will receive the specified level. If the player's level is currently higher than the reward level, it will not be changed.

The main purpose of this is incremental Courses, so you would have to complete the Level1 course, before you could join the Level2 course, etc.

### /pa rewardleveladd (course) (amount)

This means when you complete the specified course, your level has the amount added to it. So if you had a ParkourLevel of 10, and completed the course which had a rewardLevelAdd of 2, your new ParkourLevel becomes 12.

The main purpose for this is for lobbies where you have to complete all of the courses to unlock a new lobby; If you had an "Easy" lobby with 5 courses, you would set the level requirement for "Medium" to 5, and add a rewardLevelAdd to 1 for each course in Easy, so they can be completed in any order. NOTE: For this you would have to enable `/pa rewardonce (course)` so they only get leveled up once per course.

### /pa setlevel (player) (level)

This can be used by Admins to manually set a player's level. This can be done for VIPs, or simply to quickly test what you've created is working as intended.

## What is a ParkourRank?

A ParkourRank is simply a title a player earns when they reach a certain level.

A basic example could be similar to the following, when they reach each ParkourLevel they get the specified ParkourRank:

    5 = &3Beginner
    10 = &4Amateur
    20 = &5Professional
    30 = &6Expert
    50 = &1M&2a&3s&4t&5e&6r

## Rewarding ParkourRanks

There are currently 2 ways to achieve a new ParkourRank.

### /pa rewardrank (level) (rank)

As demonstrated above, this will create a ParkourRank for a corresponding ParkourLevel, when the achieve that level. It can be colour coded, for example `/pa rewardlevel 50 &1Expert`.

### /pa setrank (player) (rank)

This will manually set a player's ParkourRank to the specified rank. Can be used for testing how it will look, or giving to VIPs, etc.

## ParkourRank Configuration

By default this functionality is disabled, to avoid interfering with the Server's chat plugin.

To enable, find `Other.Parkour.ChatRankPrefix.Enabled` in the `config.yml`, and set it to `true`.

You must decide if you want to use Parkour's chat implementation, or extend your current chat plugin.

If you choose to use Parkour's chat implementation, the chat string format is found in the `strings.yml` under `Event.Chat`.

If you choose to extend your current chat plugin, you must set `Other.Parkour.ChatRankPrefix.OverrideChat` to `false` in the `config.yml`. You can then add `%RANK%` to your chat plugin format, which the Parkour plugin will detect and replace with the player's ParkourRank.

## Delaying / Limiting the Rewards

You are able to delay the time a reward is given to the player in hours, including decimals such as '0.5' = 30 minutes, or '48' = 2 full days. For example the player could receive the prize on their first completion, but may have to wait 2 full days before they are able to receive the prize again. This is achieved by entering `/pa rewarddelay (course) (hours)`.

You can also have the player only be rewarded a single time after they complete a course. This is heavily recommended if you are doing anything advanced with ParkourLevels, such as using rewardleveladd. This is achieved by entering `/pa rewardonce (course)`.

## Parkoins

Parkoins are a currency within the Parkour plugin that can be configured like any other prize.

Unfortunately due to time constraints and more important features, this has been underutilized by the plugin itself. Fortunately due to the easy developer API, you can create a plugin that can build on top of Parkour that uses the currency. An example could be requiring a certain amount of Parkoins before a course can be purchased to join.

## Challenge Mode

You are able to challenge a player to a course to see who can complete the course the fastest. This can become competitive when a monetary wager is introduced (If Economy is enabled), the winner will have the amount added to the account and the loser will have the amount deducted. 
_Forfeiting (leaving the course or server) will be treated as a loss and the wager will be deducted._

To begin you must send the target player a Challenge request using `/pa challenge (player) (course) [wager]` for example `/pa challenge A5H73Y fastrun`.

If the target player accepts using `/pa accept` then both players are teleported to the start of the course and a countdown is initiated. It can be configured for the opponent to be invisible for the duration of the challenge.
