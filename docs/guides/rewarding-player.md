Rewarding the Player
======

There are several ways we can reward the Player for completing a Course. We are able to reward the Player with ParkourLevels that allow them to unlock new Courses, or achieve a ParkourRank that showcases how far they've progressed. All can be configured to match any server requirements, and even allow developers to natively add their own functionality.

## Course Prizes

You can reward the Player several ways after they complete a Course, this can be configured by starting the Prize conversation which is initiated by entering `/pa prize (course)`.

This will start a conversation of what you want the prize to be, these can be stacked meaning you can have all options happen when the Player finishes a Course.

![Course Prize](https://i.imgur.com/syeM4Cn.jpg "Course Prize")

This was made to be as simple as possible, to allow you to answer each question with what you want. Follow each conversation as demonstrated below:

### Material

You can provide the Player with a Material for completing the Course, and you can specify the amount. Once the Player completes the Course, the item and amount will be inserted into their inventory after their original inventory is restored.

![Material Prize](https://i.imgur.com/xgLug6k.jpg "Material Prize")

### Commands

You are able to execute multiple commands when a Player completes the Course, simply choose the "command" option, and it will ask you for the command to execute. Note that these commands will be run by the server, so will have elevated permission to run anything.

You can use the placeholder `%PLAYER%` to insert the Players name at execution, this is case-sensitive to be recognized by the plugin. As a basic example I have set the command prize to `give %PLAYER% minecraft:torch 10` when they complete, the plugin will give you the option to run the command to test to see if it works correctly, by inserting your name into the placeholder.

![Command Prize](https://i.imgur.com/i9Vfb98.jpg "Command Prize")

You can see that once I entered 'yes', it executed the command which gave the Player 10 torches, make sure you use `%PLAYER%` exactly so that it replaces that with the actual Player's name.

You will be able to run this conversation as many times as you want to create a list of commands that get executed once a Player completes the Course.

### XP

You can give the Player Minecraft XP when they complete the Course, which is as simple as entering a positive number into the conversation.

![XP Prize](https://i.imgur.com/43qKmUn.jpg "XP Prize")

### Ecomomy

If the plugin is linked to Vault, you are able to set a financial reward when the player completes the course.

![Economy Prize Example](https://i.imgur.com/VKjMNk6.png "Economy Prize Example")

_Command: `/pa economy setprize (course) (amount)`_

## Rewarding ParkourLevels

There are currently 3 ways to achieve a new ParkourLevel.

### /pa rewardlevel (course) (level)

This means when you complete the specified Course, you will receive the specified level. If the Player's level is currently higher than the reward level, it will not be changed.

The main purpose of this is incremental Courses, so you would have to complete the Level1 Course, before you could join the Level2 Course, etc.

### /pa rewardleveladd (course) (amount)

This means when you complete the specified Course, your level has the amount added to it. So if you had a ParkourLevel of 10, and completed the Course which had a rewardLevelAdd of 2, your new ParkourLevel becomes 12.

The main purpose for this is for lobby setups where you have to complete all the Courses to unlock a new lobby; If you had an "Easy" lobby with 5 Courses, you would set the level requirement for "Medium" lobby to 5, and add a rewardLevelAdd to 1 for each Course in Easy, so they can be completed in any order. NOTE: For this you would have to enable `/pa rewardonce (course)` so they only get leveled up once per Course.

### /pa setlevel (player) (level)

This can be used by Admins to manually set a Player's level. This can be done for VIPs, or simply to quickly test what you've created is working as intended.

## What is a ParkourRank?

A ParkourRank is simply a title a Player earns when they reach a required ParkourLevel. As soon as the Player's ParkourLevel passes the threshold of the next ParkourRank to unlock, it will be applied to the player.

A basic example could be similar to the following, when they reach or exceed each ParkourLevel they get the specified ParkourRank:

    5 = &3Beginner
    10 = &4Amateur
    20 = &5Professional
    30 = &6Expert
    50 = &1M&2a&3s&4t&5e&6r

## Rewarding ParkourRanks

There are currently 2 ways to achieve a new ParkourRank.

### /pa rewardrank (level) (rank)

As demonstrated above, this will create a ParkourRank for a corresponding ParkourLevel. It can be colour coded, for example `/pa rewardlevel 30 &6Expert`.

### /pa setrank (player) (rank)

This will manually set a Player's ParkourRank to the specified rank. Can be used for testing how it will look, or giving to VIPs, etc.

## ParkourRank Configuration

ParkourRanks are disabled by default to avoid interfering with the Server's chat plugin.

To enable, find `Other.Parkour.ChatRankPrefix.Enabled` in the `config.yml`, and set it to `true`.

You must decide if you want to use Parkour's chat implementation, or extend your current chat plugin.

If you choose to use Parkour's chat implementation, the chat string format is found in the `strings.yml` under `Event.Chat`.

If you choose to extend your current chat plugin, you must set `Other.Parkour.ChatRankPrefix.OverrideChat` to `true` in the `config.yml`. You can then add `%RANK%` to your chat plugin format, which the Parkour plugin will detect and replace with the Player's ParkourRank.  
Alternatively, if you use a Chat plugin that supports PlaceholderAPI, you can use the `%parkour_player_rank%` placeholder.

## Delaying / Limiting the Rewards

You are able to delay the time a reward is given to the Player in hours, including decimals such as '0.5' = 30 minutes, or '48' = 2 full days. For example the Player could receive the prize on their first completion, but may have to wait 2 full days before they are able to receive the prize again. This is achieved by entering `/pa rewarddelay (course) (hours)`.

You can also have the Player only be rewarded a single time after they complete a Course. This is heavily recommended if you are doing anything advanced with ParkourLevels, such as using rewardleveladd. This is achieved by entering `/pa rewardonce (course)`.

## Parkoins

Parkoins are a currency within the Parkour plugin that can be configured like any other prize.

Unfortunately due to time constraints and more important features, this has been underutilized by the plugin itself. Fortunately due to the easy developer API, you can create a plugin that can build on top of Parkour that uses the currency. An example could be requiring a certain amount of Parkoins before a Course can be purchased to join.
