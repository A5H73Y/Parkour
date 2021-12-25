Parkour Lobby
======

A Lobby is simply a location that allows you to join Parkour Courses. It also acts as a place for the Player to teleport to when they complete or leave a Course.

## Creating a Lobby

Parkour can allow Courses to be grouped into different Lobbies, which could be used to add different stages to the server. For example "easy", "medium" and "hard" Courses. The command `/pa setlobby (name)` is used to create a Lobby in your current position.

![Parkour Lobby Created](https://i.imgur.com/AGl0p1A.jpg "Parkour Lobby Created")

A restriction can be placed on Lobbies to only be joinable by Players with a required ParkourLevel. This will enforce the Player to achieve a certain ParkourLevel in the Courses to be able to join the Lobby, for example completing all the Courses in the easy Lobby to be able to join the hard Lobby. This is achieved by providing a minimum Parkour level required to join.

_Command: `/pa setlobby (name) [parkour-level]`_  
The Lobby will only be joinable by Players with the required Parkour level (when provided).

![Parkour Level Lobby Created](https://i.imgur.com/py34xti.jpg "Parkour Level Lobby Created")

To teleport to the Lobby, simply enter `/pa lobby [name]`.

## Moving a Lobby

You are able to move the location of an existing Parkour Lobby by re-entering the command used to create the Lobby.

## Delete a Lobby

You can delete a Lobby by using the command `/pa delete lobby (lobby)`.

If there are any Courses linked to this lobby, it will error and present a list of Courses still dependent on this lobby. You must link these Courses to a different Lobby before you are able to delete it.

![Delete Validation](https://i.imgur.com/wCO9jrU.png "Delete Validation")

_Command: `/pa delete lobby (lobby)`_
