Parkour Level & Ranks
======

A Player can be rewarded with levels _(ParkourLevels)_ which will unlock new Courses for them to join. An example could be 'course2' will be locked until they complete 'course1'. The ParkourLevel reward can either be an exact level, or an addition to their current level. For example, you can finish all the 'easy' Courses in any order before having enough levels to join the 'medium' Courses. The Player's ParkourLevel can never decrease, only increase or remain the same.

## Rewarding ParkourLevels

There are 3 ways to achieve a new ParkourLevel.

### /pa rewardlevel (course) (level)

This means when you complete the specified Course, your ParkourLevel will be **set** to specified level. If the Player's level is currently higher than the reward level, it will not be changed.

The main purpose of this is incremental Courses, so you would have to complete the Level1 Course, before you could join the Level2 Course, etc.

### /pa rewardleveladd (course) (amount)

This means when you complete the specified Course, your level has the amount added to it. So if you had a ParkourLevel of 10, and completed the Course which had a rewardLevelAdd of 2, your new ParkourLevel becomes 12.

The main purpose for this is for lobby setups where you have to complete all the Courses to unlock a new lobby; If you had an "Easy" lobby with 5 Courses, you would set the level requirement for "Medium" lobby to 5, and add a rewardLevelAdd to 1 for each Course in Easy, so they can be completed in any order. NOTE: For this you would have to enable `/pa rewardonce (course)` so they only get leveled up once per Course.

### /pa setlevel (player) (level)

This can be used by Admins to manually set a Player's level. This can be done for VIPs, or simply to quickly test what you've created is working as intended.

## ParkourLevel Restrictions

ParkourLevels can be used to apply restrictions to Courses and lobbies. For example, if a Course has a minimum ParkourLevel requirement of 10, and the Player has a ParkourLevel of 12, then they will be allowed to join the Course otherwise the Player will be notified.

_Command: `/pa setminimumlevel (course) (level)`_  
Set the minimum level of the specified Course to the specified ParkourLevel. Any Player that now attempts to join this Course will be prevented from doing so unless they have the minimum ParkourLevel required.

_Command: `/pa setlobby (name) (level)`_  
Create a Lobby and set the minimum level requirement for teleportation to the ParkourLevel specified. This is used if you organize your Courses into separate lobbies and wish to prevent the Player from teleporting unless they have a high enough ParkourLevel.


## What is a ParkourRank?

A ParkourRank is simply a title which is displayed in the chat. This can be a status symbol for the Player's that have completed many of the harder Courses for example.

A Player earns a new ParkourRank when they reach the required ParkourLevel. As soon as the Player's ParkourLevel passes the threshold of the next ParkourRank to unlock, it will be applied to the Player.

A basic example could be similar to the following, when they reach or exceed each ParkourLevel they get the specified ParkourRank:

5 = <span style="color:blue">Beginner</span>  
10 = <span style="color:green">Amateur</span>  
20 = <span style="color:red">Professional</span>  
30 = <span style="color:gold">Expert</span>  
50 = <span style="color:blue">M</span><span style="color:darkblue">a</span><span style="color:darkcyan">s</span><span style="color:darkslateblue">t</span><span style="color:cornflowerblue">e</span><span style="color:blueviolet">r</span>

## Rewarding ParkourRanks

There are currently 2 ways to achieve a new ParkourRank.

### /pa rewardrank (level) (rank)

As demonstrated above, this will create a ParkourRank for a corresponding ParkourLevel. It can be colour coded, for example `/pa rewardlevel 30 &6Expert`.

### /pa setrank (player) (rank)

This will manually set a Player's ParkourRank to the specified rank. Can be used for testing how it will look, or giving to VIPs, etc.

## ParkourRank Configuration

ParkourRanks are disabled by default to avoid interfering with the Server's chat plugin.

To enable, set `ParkourRankChat.Enabled` to `true` in the `config.yml`.

You must decide if you want to use Parkour's chat implementation, or extend your current chat plugin.

### Parkour's Chat System

You must set `ParkourRankChat.OverrideChat` to `true` in the `config.yml`.

The chat string format is found in the `strings.yml` under `Event.Chat`.  
_PlaceholderAPI values will be evaluated._

### External Chat System

You can add `%RANK%` to your chat plugin's format, which the Parkour plugin will detect and replace with the Player's ParkourRank.  

Alternatively, if you use a Chat plugin that supports PlaceholderAPI, you can use the `%parkour_player_rank%` placeholder.
