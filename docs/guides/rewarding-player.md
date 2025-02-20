Rewarding the Player
======

There are several ways we can reward the Player for completing a Course.  
_Note that `OnFinish.EnablePrizes` must be set to `true` in the `config.yml` for any prize to be given._

## ParkourLevels & ParkourRanks

We are able to reward the Player with ParkourLevels that allow them to unlock new Courses, or achieve a ParkourRank that showcases how far they've progressed.

More information: [Click Here](/tutorials/parkour-level-ranks.md)

## Course Prize

You can reward the Player several ways after they complete a Course. This can be configured by starting the Prize conversation which is initiated by entering `/pa setcourse (course) prize`.

This will start a conversation of what you want the prize to be, these can be stacked meaning you can have all options happen when the Player finishes a Course.

![Course Prize](https://i.imgur.com/syeM4Cn.jpg "Course Prize")

This was made to be as simple as possible, to allow you to answer each question with what you want. Follow each conversation as demonstrated below:

### Material

The Player can be rewarded with a Material for completing the Course. The amount and a custom display name can be specified. The example Materials available for each server version will differ, example Material names can be [found here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html).  
_Once the Player completes the Course, the ItemStack will be inserted into their inventory after their original inventory is restored._

![Material Prize](https://i.imgur.com/xgLug6k.jpg "Material Prize")

### Commands

Multiple commands can be executed when a Player completes the Course, simply choose the "command" option, and it will prompt for the command to execute.

This is a shortcut to the [Prize Event Command](/tutorials/parkour-courses?id=command).

As a basic example I have set the command prize to `give %PLAYER% minecraft:torch 10` when they complete, the plugin will give you the option to run the command to test to see if it works correctly, by inserting your name into the placeholder.

![Command Prize](https://i.imgur.com/i9Vfb98.jpg "Command Prize")

You can see that once I entered 'yes', it executed the command which gave the Player 10 torches, make sure you use `%PLAYER%` exactly so that it replaces that with the actual Player's name.

You will be able to run this conversation as many times as you want to create a list of commands that get executed once a Player completes the Course.

### XP

You can give the Player Minecraft XP when they complete the Course, which is as simple as entering a positive number into the conversation.

![XP Prize](https://i.imgur.com/43qKmUn.jpg "XP Prize")

## Economy

If the plugin is linked to Vault, you are able to set a financial reward when the player completes the course.

![Economy Prize Example](https://i.imgur.com/VKjMNk6.png "Economy Prize Example")

_Command: `/pa economy setprize (course) (amount)`_

## Delaying / Limiting the Rewards

Delay the time between rewards using the Course [rewarddelay Setting](/tutorials/parkour-courses?id=rewarddelay).

Limit the Prize to be received only once using Course [rewardonce Setting](/tutorials/parkour-courses?id=rewardonce).
