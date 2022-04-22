Rewarding the Player
======

There are several ways we can reward the Player for completing a Course.  
_Note that `OnFinish.EnablePrizes` must be set to `true` in the `config.yml` for any prize to be given._

## Course Prizes

You can reward the Player several ways after they complete a Course, this can be configured by starting the Prize conversation which is initiated by entering `/pa prize (course)`.

This will start a conversation of what you want the prize to be, these can be stacked meaning you can have all options happen when the Player finishes a Course.

![Course Prize](https://i.imgur.com/syeM4Cn.jpg "Course Prize")

This was made to be as simple as possible, to allow you to answer each question with what you want. Follow each conversation as demonstrated below:

### ParkourLevels & ParkourRanks

We are able to reward the Player with ParkourLevels that allow them to unlock new Courses, or achieve a ParkourRank that showcases how far they've progressed.

More information: [Click Here](/tutorials/parkour-level-ranks.md)

### Material

The Player can be rewarded with a Material for completing the Course, and amount can be specified.  
_Once the Player completes the Course, the ItemStack will be inserted into their inventory after their original inventory is restored._

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

## Delaying / Limiting the Rewards

You are able to delay the time a reward is given to the Player in hours, including decimals such as '0.5' = 30 minutes, or '48' = 2 full days. For example the Player could receive the prize on their first completion, but may have to wait 2 full days before they are able to receive the prize again. This is achieved by entering `/pa rewarddelay (course) (hours)`.

You can also have the Player only be rewarded a single time after they complete a Course. This is heavily recommended if you are doing anything advanced with ParkourLevels, such as using rewardleveladd. This is achieved by entering `/pa rewardonce (course)`.

## Parkoins

Parkoins are a currency within the Parkour plugin that can be configured like any other prize.

Unfortunately due to time constraints and more important features, this has been underutilized by the plugin itself. Fortunately due to the easy developer API, you can create a plugin that can build on top of Parkour that uses the currency. An example could be requiring a certain amount of Parkoins before a Course can be purchased to join.
