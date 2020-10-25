Creating a Course
======

## Design the Course

Start by building your Course and decide where you may want your checkpoints to be.

*If it helps, we can place different blocks to mark where we want the checkpoints to be, we can remove these later.*

![Design Course](https://i.imgur.com/Ckjueri.jpg "Design Course")

## Create the Course

Decide where you want the Course to start, stand in the position and face the direction you want the players to teleport to. When you are happy, enter the command `/pa create (course)`.

![Create Course](https://i.imgur.com/OcbCbL8.jpg "Create Course")

This example has named the Course "tutorial", the name you have chosen will be used to reference the course later on. If everything was successful, the following message will appear.

*This will create a Course entry in the database, allowing us to now track times against it.*

![Create Success](https://i.imgur.com/hA8HpnU.jpg "Create Success")

The term "selected" means that you are editing the Course. This can be manually achieved by using the command `/pa select (course)`, you can now execute commands without having to specify the course, for example the following `/pa checkpoint` command does not require you to specify a course.

## Create Checkpoints

To create a checkpoint stand where you want your first checkpoint to be, face the direction you want the players to teleport to and enter `/pa checkpoint`.

_This will place a pressure plate to activate the checkpoint._

![Checkpoint Created](https://i.imgur.com/IYgHBJs.jpg "Checkpoint Created")

There is no limit to how many checkpoints you can create, they are automatically generated every time you enter `/pa checkpoint`. The checkpoint is generated for the Course you have selected (editing); To select a course use the command `/pa select (course)`.

Now you've learnt how to create a single checkpoint, repeat the command to create as many checkpoints as you want for the course!

![Checkpoint 2 Created](https://i.imgur.com/TXum8Wx.jpg "Checkpoint 2 Created")
![Checkpoints Created](https://i.imgur.com/nlFsGsC.jpg "Checkpoints Created")

If you happen to make a mistake, or want to move a checkpoint, you can use the same command but with an optional number parameter to overwrite that existing checkpoint. For example `/pa checkpoint 2` would overwrite the second checkpoint.

## Ability to Finish Course

For the player to be able to finish the course, we need use a "Finish Block" or a Finish Parkour Sign.

A "Finish Block" is part of a ParkourKit and will trigger the course being finished when walked upon. For now we can set it to the default Finish Block, you can change it later. Simply enter `/pa kit` to populate your inventory with each item of the default ParkourKit, then find the Material named "Finish Block", and simply place these materials where you want the finish area to be.

For more information on ParkourKits, click here. //TODO UPDATE ME

## Course Ready Status

Once all the checkpoints are created for the course, it's time to set the status of the course to "ready", so other players can join it. To mark the course as ready, enter `/pa ready (course)`.

By default, players can not join a course that has not been set to ready (so they don't join a half-finished course), this can be disabled by changing `OnJoin.EnforceReady` to `false` in the `config.yml`.

You can enter `/pa done` to stop editing the course.

![Course Ready](https://i.imgur.com/kd1KkqU.jpg "Course Ready")

## Test Mode

You are able to simulate how each ParkourKit will behave by entering Test Mode, enable it with `/pa test [kitName]`.
Please note that this doesn't do anything besides allow you to interact with the ParkourKit and its corresponding actions. If you "die" while in Test Mode, it will take you back to the location you initiated Test Mode.

_To disable Test Mode, enter `/pa test` again._

## Teleporting to a Course

If you want to teleport to the start of a course, you can enter `/pa tp (course)`. This will only teleport you, exactly as you are, without starting the course.

If you wish to teleport to a specific checkpoint in the course you can enter `/pa tpc (course) (checkpoint)`.
