Parkour Lobby
======

A Lobby is simply a location that allows Players to join Parkour Courses. It will also act as a place for the Player to teleport to once they complete or leave a Course.

## Creating a Lobby

Parkour can allow Courses to be grouped into different Lobbies, which could be used to add different stages to the server. For example "easy", "medium" and "hard" Courses. 

The command `/pa create lobby [name]` is used to create a Lobby in your current position.

![Parkour Lobby Created](https://i.imgur.com/AGl0p1A.jpg "Parkour Lobby Created")

### ParkourLevel requirement

Lobbies can be limited to only be joinable by Players with the required ParkourLevel. For example completing all the Courses in the easy Lobby to be able to join the hard Lobby. This is achieved by providing a minimum Parkour level required to join.

_Command: `/pa create lobby (name) [parkour-level]`_

![Parkour Level Lobby Created](https://i.imgur.com/py34xti.jpg "Parkour Level Lobby Created")

_[More information on ParkourLevels.](/tutorials/parkour-level-ranks)_

## Teleporting to a Lobby

To teleport to the Lobby, simply enter `/pa lobby [name]`.

If the Player has a minimum ParkourLevel requirement, this will check to see if the Player has a high enough level before allowing teleportation.

## Moving a Lobby

You are able to move the location of an existing Parkour Lobby by re-entering the command used to create the Lobby.

## Delete a Lobby

[Administration - Delete a Lobby](/tutorials/administration?id=delete-a-lobby)
