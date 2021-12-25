Challenge Mode
======

You are able to challenge other Players to a Course to see who can complete the Course the fastest and prove who is the best. This can become competitive when a monetary wager is introduced (If Economy is enabled), the winner will have the amount `(wager * (participants - 1))` added to their account, and the loser(s) will have the amount deducted. _Forfeiting (leaving the Course or server) will be treated as a loss, and the wager will be deducted in the event of a Player completing the Course._

A new Challenge can be created manually by using a command and which can then invite other players. Alternatively you can interact with a Challenge sign to join with other Players interested in a Challenge.

## Creating a Challenge

Each challenge must have a host, they have elevated permissions to invite and start the Challenge when they decide to. If the create command is used then that player becomes the host; if a Challenge Sign is used it will be the first player to interact with the sign.

### Command

If a Player has permission to create a Challenge then they can specify a Course, followed by an optional wager they are placing.

_Command: /pa challenge create (course) \[wager]_

![Challenge Created](https://i.imgur.com/1oU1Ha6.png "Challenge Created")

![Challenge Created Wager](https://i.imgur.com/1RnbR3j.png "Challenge Created Wager")

### Sign

When a Player interacts with a Challenge Sign they will either become the Host, or become a participant in an existing, but not started, Challenge.

![Challenge Sign](https://i.imgur.com/1n66HUv.png "Challenge Sign")

![Challenge Sign Wager](https://i.imgur.com/AQ7pTe8.png "Challenge Sign Wager")

## Inviting Players

Each Player that is invited will receive a summary of the challenge, followed by a command on how to accept or decline.

Each Player will be validated to ensure they are able to join the Course as normal, including any ParkourLevel requirements etc. If there is a wager, the Player must have sufficient funds before being able to join.

To prevent spamming, there is a cool-down to sending invites, and a Player can only be invited to 1 challenge at a time until accepted or declined.

_Command: /pa invite (players...)_

![Challenge Invite](https://i.imgur.com/TwPNbz3.png "Challenge Invite")

## Starting the Challenge

Once the host happy there are sufficient players they can initiate the Challenge countdown. Depending on the config, the player's will be teleported to the start of the Course unable to move while the Countdown begins, once the Countdowns ends their movement is restored, and the Course begins as normal.

The first Player to finish the Course is declared the winner, and the participants are notified. If there is a wager the amount will be deducted from each participant and rewarded to the winning Player.

_Command: /pa challenge info_

![Challenge Info](https://i.imgur.com/G3ZQ5Dw.png "Challenge Info")

_Command: /pa challenge begin_

![Challenge Begin](https://i.imgur.com/CMMcOX5.png "Challenge Begin")
