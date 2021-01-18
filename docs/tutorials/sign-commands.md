Sign Commands
======

## Join Course

A sign used to join a Parkour Course. These should be stored in your Parkour Lobby so they can be easy to find and interact with. Validation will execute when the sign is clicked to make sure the Player is allowed to join first before they are teleported away.

##### Syntax

![Join Syntax](https://i.imgur.com/uokArkd.png "Join Syntax")

##### Example

![Join Example](https://i.imgur.com/bfVf3li.png "Join Example")

##### Additional information

If the Course has a minimum level requirement to join, the level number will be displayed underneath.

![Join Requirement](https://i.imgur.com/2uYGWLe.png "Join Requirement")

## Finish Course

A Finish sign is used as an alternative to a Finish Block. Validation will execute when the sign is clicked to make sure the Player is allowed to finish the Course before they are teleported away.

##### Syntax

![Finish Syntax](https://i.imgur.com/h7DE6xn.png "Finish Syntax")

##### Example

![Finish Example](https://i.imgur.com/dDbkmw4.png "Finish Example")

## Parkour Lobby

A Parkour lobby is an area of Parkour Join signs, which you are teleported to when you leave or finish a Course. There can be multiple lobbies that are identified by a unique name, these can have a minimum level requirement validation on before they can be teleported to.

##### Syntax

![Lobby Syntax](https://i.imgur.com/6ouLdKC.png "Lobby Syntax")

##### Example

![Lobby Example](https://i.imgur.com/34QGgZ2.png "Lobby Example")

##### Additional information

If no lobby name is specified the default lobby will be used.

If the lobby has a minimum level requirement to join, the level number will be displayed underneath.

![Join Requirement](https://i.imgur.com/45kDF0b.png "Join Requirement")

## Leave Course

Create a sign for Players to easily leave the Course.

##### Example

![Leave Example](https://i.imgur.com/Zgnyw6w.png "Leave Example")

## Effects Signs

You can apply several effects to the Player on the Course. These are activated by right clicking the signs whilst on a Course.

The effects are derived from PotionEffectType available from the server.

There are 2 hardcoded effects, these include:

    Heal
        Fully restore the Player's health.
    Gamemode
        Requires a GameMode on the bottom line to apply the GameMode specified.

For the other effects, you simply put the name of the PotionEffectType on the second line. For example `JUMP`, then on the bottom line you will put the duration and strength in the format of `duration:strength` for example `1000:6`

For resistance effects that may not fit on the line such as `DAMAGE_RESISTANCE` this can be shortened to `DAMAGE_RESIST`.

##### Syntax

![Effect Syntax](https://i.imgur.com/7kp3Ll9.png "Effect Syntax")

##### Example

![Effect Example](https://i.imgur.com/soQ552h.png "Effect Example")

## Course Stats

Display the Course information in the Player's chat when they right click the sign.

##### Syntax

![Stats Syntax](https://i.imgur.com/fhpmUMv.png "Stats Syntax")

##### Example

![Stats Example](https://i.imgur.com/pdzaWks.png "Stats Example")

##### Additional information

Each Course will display the relevant statistics based on how they are set up. Each Course would have a dynamic list of information based on how they are setup. For example if the Course has a custom lobby set, that would display also.

![Stats Results](https://i.imgur.com/C1pBaJA.png "Stats Results")

## Course Leaderboards

Display the leaderboards of the Course, the amount of results can be placed at the bottom of the sign.

##### Syntax

![Leaderboards Syntax](https://i.imgur.com/OAQuSgE.png "Leaderboards Syntax")

##### Example

![Leaderboards Example](https://i.imgur.com/5xFIkaz.png "Leaderboards Example")

![Leaderboards Results](https://i.imgur.com/2njojiq.png "Leaderboards Results")

## Course Checkpoint

You can have a manual checkpoint sign, instead of walking on a pressure-plate. The number must equal the next checkpoint you need to achieve to apply the checkpoint.

These work the same way as previous "SetPoint" signs, they simply load the checkpoint, and do not create one. The checkpoint must be created via `/pa checkpoint` and then the pressure plate can be replaced by the checkpoint sign to activate the loading of the checkpoint.

##### Syntax

![Checkpoint Syntax](https://i.imgur.com/0QroXwc.png "Checkpoint Syntax")

##### Example

![Checkpoint Example](https://i.imgur.com/Rp66GzL.png "Checkpoint Example")

## Join All

Present a GUI with all the available Courses to join, simply clicking an entry will trigger the Player to join a Course.  
The layout can be customised from the config, with the ability to navigate through the courses on different pages.

Permission `Parkour.Basic.JoinAll` is required for the Player to use.

##### Example

![JoinAll Example](https://i.imgur.com/LAoFM3s.png "JoinAll Example")
