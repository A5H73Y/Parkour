Creating a Course
======

## Design the Course

Start by building the Course and decide where you may want the checkpoints to be.

![Design Course](https://i.imgur.com/Ckjueri.jpg "Design Course")

## Create the Course

Decide where the Course starts, stand in the position and face the direction you want the Players to teleport to, then enter the command `/pa create course (name)`.

![Create Course](https://i.imgur.com/OcbCbL8.jpg "Create Course")

The Course is named `tutorial` in this tutorial. The unique name you choose will be used to reference the Course later on. If everything was successful, the following message will appear.

![Create Success](https://i.imgur.com/hA8HpnU.jpg "Create Success")

## Create Checkpoints

To create a Checkpoint, stand where you want your first checkpoint to be and face the direction you want the Players to teleport to. Then simply enter `/pa create checkpoint (course)`.

_This will automatically place a pressure plate to activate the checkpoint._

![Checkpoint Created](https://i.imgur.com/IYgHBJs.jpg "Checkpoint Created")

Now you've learnt how to create a single checkpoint, repeat the command to create as many checkpoints as needed for the Course.

There is no limit to how many checkpoints you can create, they are automatically generated every time you enter `/pa create checkpoint (course)`.

![Checkpoint 2 Created](https://i.imgur.com/TXum8Wx.jpg "Checkpoint 2 Created")
![Checkpoints Created](https://i.imgur.com/nlFsGsC.jpg "Checkpoints Created")

If you make a mistake, or want to move a Checkpoint, the same command can be used but with an extra number provided to overwrite that existing checkpoint.  
Using `/pa create checkpoint 2` would overwrite the second checkpoint. If preferred, you can [delete the checkpoint](/tutorials/administration?id=delete-a-checkpoint).

By default, Checkpoints have to be achieved sequentially, 1 - 2 - 3 etc. If preferred, you can disable this by setting `OnCourse.SequentialCheckpoints.Enabled` to `false`. Alternatively you can set the Course to allow [manualcheckpoints](/tutorials/parkour-courses?id=manualcheckpoints) which will let the Player achieve a Checkpoint anywhere.

## Ability to Finish Course

For the Player to be able to finish the Course they can interact with a "Finish Block", or a [Finish Sign](/tutorials/sign-commands?id=finish-course).

A "Finish Block" is part of a [ParkourKit](/tutorials/parkour-kits) and will trigger the Course being finished when walked upon. For now, we can set it to the default Finish Block, however you can change it later. Simply enter `/pa kit` to populate your inventory with each item of the default ParkourKit, then find the Material named "Finish Block", and simply place these materials where you want the finish area to be.

_If preferred, the final Checkpoint can trigger the Course finish by setting `OnCourse.TreatLastCheckpointAsFinish` to `true` in the `config.yml`._

## Course Ready Status

Once the Course is ready for other Players to join, then it's time to set the status of the Course to "ready". To set the Course as ready, enter `/pa setcourse (course) ready`.

By default, Players are unable to join a Course that has not been set to ready (so they don't join a half-finished Course), this can be disabled by setting `OnJoin.EnforceReady` to `false` in the `config.yml`.

![Course Ready](https://i.imgur.com/kd1KkqU.jpg "Course Ready")

## Test Mode

You are able to simulate how each ParkourKit will behave by enabling Test Mode, this can be achieved using `/pa test [kitName]`.  
This allows you to interact with the specified ParkourKit and its corresponding actions. If you "die" while in Test Mode, it will take you back to the location you started Test Mode.

_To disable Test Mode, enter `/pa test` again._

## Teleporting to a Course

If you want to teleport to the start of a Course, you can enter `/pa tp (course)`. This will only teleport you, exactly as you are, without starting the Course.

If you wish to teleport to a specific checkpoint in the Course you can enter `/pa tpc (course) (checkpoint)`.
