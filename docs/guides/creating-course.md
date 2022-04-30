Creating a Course
======

## Design the Course

Start by building your Course and decide where you may want your checkpoints to be.

*If it helps, we can place different blocks to mark where we want the checkpoints to be, we can remove these later.*

![Design Course](https://i.imgur.com/Ckjueri.jpg "Design Course")

## Create the Course

Decide where you want the Course to start, stand in the position and face the direction you want the Players to teleport to. When you are happy, enter the command `/pa create (course)`.

![Create Course](https://i.imgur.com/OcbCbL8.jpg "Create Course")

This example has named the Course "tutorial", the name you have chosen will be used to reference the Course later on. If everything was successful, the following message will appear.

![Create Success](https://i.imgur.com/hA8HpnU.jpg "Create Success")

*This will create a Course entry in the database, allowing us to now track times against it.*

## Create Checkpoints

To create a checkpoint stand where you want your first checkpoint to be, face the direction you want the Players to teleport to and enter `/pa create checkpoint (course)`.

_This will place a pressure plate to activate the checkpoint._

![Checkpoint Created](https://i.imgur.com/IYgHBJs.jpg "Checkpoint Created")

There is no limit to how many checkpoints you can create, they are automatically generated every time you enter `/pa create checkpoint (course)`.

Now you've learnt how to create a single checkpoint, repeat the command to create as many checkpoints as needed for the Course.

![Checkpoint 2 Created](https://i.imgur.com/TXum8Wx.jpg "Checkpoint 2 Created")
![Checkpoints Created](https://i.imgur.com/nlFsGsC.jpg "Checkpoints Created")

If you happen to make a mistake, or want to move a checkpoint, you can use the same command but with an optional number parameter to overwrite that existing checkpoint.  
For example `/pa checkpoint 2` would overwrite the second checkpoint. If preferred, you can [delete the checkpoint](tutorials/administration?id=delete-a-checkpoint).

_Note that these checkpoints have to be achieved sequentially, otherwise they will not work. If you'd prefer there be no restriction to the order they are achieved use the [Free Checkpoint ParkourMode](tutorials/parkour-modes?id=free-checkpoint)._

## Ability to Finish Course

For the Player to be able to finish the Course they can interact with a "Finish Block", or a Finish Parkour Sign.

A "Finish Block" is part of a ParkourKit and will trigger the Course being finished when walked upon. For now, we can set it to the default Finish Block, you can change it later. Simply enter `/pa kit` to populate your inventory with each item of the default ParkourKit, then find the Material named "Finish Block", and simply place these materials where you want the finish area to be.

For more information on ParkourKits, [click here](../tutorials/parkour-kits.md).

If you prefer the final checkpoint to trigger the Course finish, set `OnCourse.TreatLastCheckpointAsFinish` to `true` in the `config.yml`.

## Course Ready Status

Once you've finished setting up the Course, and it's ready for other players to use then it's time to set the status of the Course to "ready". To mark the Course as ready, enter `/pa ready (course)`.

By default, Players can not join a Course that has not been set to ready (so they don't join a half-finished Course), this can be disabled by changing `OnJoin.EnforceReady` to `false` in the `config.yml`.

You can enter `/pa done` to stop editing the Course.

![Course Ready](https://i.imgur.com/kd1KkqU.jpg "Course Ready")

## Test Mode

You are able to simulate how each ParkourKit will behave by entering Test Mode, enable it with `/pa test [kitName]`.
Please note that this doesn't do anything besides allow you to interact with the ParkourKit and its corresponding actions. If you "die" while in Test Mode, it will take you back to the location you initiated Test Mode.

_To disable Test Mode, enter `/pa test` again._

## Teleporting to a Course

If you want to teleport to the start of a Course, you can enter `/pa tp (course)`. This will only teleport you, exactly as you are, without starting the Course.

If you wish to teleport to a specific checkpoint in the Course you can enter `/pa tpc (course) (checkpoint)`.
