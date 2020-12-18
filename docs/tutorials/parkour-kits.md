Parkour Kits
======

## What is a ParkourKit?

Parkour brings a whole new level to the kits, now referred to as ParkourKits. Each Course can now have it's own set of blocks to make each unique. You are able to specify exactly what you want the ParkourKit to consist of, allowing you to have as many of each type of action as you'd like. For example you could have 10 death blocks and a finish block, or have 3 of each type of action... it's completely configurable.

![Default ParkourKit](https://i.imgur.com/cYWhkHN.jpg "Default ParkourKit")

If you wish to create your own ParkourKit, you can start the setup conversation by entering `/pa createKit`. You will need to give the Kit a unique name which is used later on to link a Course to it, or by retrieving the kit contents by using `/pa kit (Kit name)`.

There is a "default" kit that each Course will use automatically by default, although you are still able to modify this kit, which we will cover later.

## Creating a ParkourKit

You are able to create a ParkourKit to be as customized as you'd like. Each ParkourKit must have a unique name to refer to them, this is used to link it to a Course, or to simulate using Test Mode. You are now able to choose as many or as little of each type of action; 'Action' meaning the result of walking on the specified Material.

For example, I could choose the Material TNT, and when I walk on it I want the action to be "death".

To start the conversation of creating a new ParkourKit, enter `/pa createKit`. The first step is providing a unique name for the Kit. Then followed by specifying a Material and the corresponding action you want it to be.

![Creating ParkourKit](https://i.imgur.com/TTlhZ6W.png "Creating ParkourKit")

Some actions require you to provide more information, for example a Speed block must have a strength, so it knows how fast to make you, and a duration for the effect to last. The default values for each will be presented so you can choose if it should be more / less powerful or duration lasting.

![Creating ParkourKit Extra](https://i.imgur.com/WEDf8pU.png "Creating ParkourKit Extra")

Once successfully created, we can enter `/pa kit [Kit name]` to populate our hotbar with the material that make up the ParkourKit we created. If no kit argument is specified, it will display the "default" ParkourKit.

![ParkourKit Created Example](https://i.imgur.com/TO0xVYi.png "ParkourKit Created Example")

_A Sign is always provided to your inventory by default after retrieving a kit, to allow you to easily create Parkour signs for your Course._

If something has gone wrong with your Kit and errors are appearing or Materials are missing, there is a validation command that will check your Kit for problems and display them.

This is achieved by entering `/pa validateKit [Kit name]`.

**Do not manually edit the ParkourKits from the config, as this is the main cause of problems. Instead edit them using the following tutorials.**

## Editing a ParkourKit

You are able to modify the contents of a ParkourKit at any point, including the default kit. This is done using the command `/pa editKit` then specifying the name of the ParkourKit.

You will have the option to add or remove a material. If you choose to add a material you will go through the the process of adding an material and a corresponding action, then you will have the option to continue editing the kit.

![Editing ParkourKit](https://i.imgur.com/yikde5m.png "Editing ParkourKit")

Similarly, if you choose to remove a Material from a ParkourKit, it will ask which Material to remove from the ParkourKit.

_Anybody using the ParkourKit before it was edited will not have the changes updated until they leave / join a Course that uses the kit._

## Linking a ParkourKit to a Course

Once we've created a ParkourKit, we can link this to as many Courses as we want. To achieve this, simply enter `/pa linkKit (course) (Kit name)`.

![Link ParkourKit](https://i.imgur.com/FGUy2aE.png "Link ParkourKit")

![Link ParkourKit Success](https://i.imgur.com/ENk3xiW.png "Link ParkourKit Success")

Now when you join the "test" Course, it will be using the "example" ParkourKit, meaning that the Death block will now be TNT (for this example).

## Viewing available ParkourKits

To view all the created ParkourKits, you can enter `/pa listKit`, which will display a list of the names of the ParkourKits; if you want to see the materials that make up the set, you can add an argument for the name using `/pa listKit (Kit name)`.

![View ParkourKit](https://i.imgur.com/7pQS7BO.png "View ParkourKit")

## Deleting a ParkourKit

[Click Here](tutorials/administration?id=delete-a-parkourkit)

## The ParkourBlocks

**Speed**: When the player walks on the block, a SPEED potion will be applied to the player with a configurable strength and duration.

**Climb**: When the player walks against the block, they will be levatated as if they were 'climbing' the block. The player can hold SNEAK to not climb the block.

**Launch**: When the player walks on the block, they will be launched into the air with a configurable strength.

**Finish**: When the player walks on the block, they will activate the "finish" stage of the course. Validation will be executed to ensure the player has completed the checkpoints, etc.

**Repulse**: When the player walks against the block, they will be pushed back from it as if being "repulsed" from the block.

**NoRun**: When the player runs on the block, they will be forced to walk.

**NoPotion**: When the player walks on the block, all their current Potions will be removed; including the ones given by the Parkour plugin.

**Death**: When the player walks on the block, they will activate a "death" on the block, taking them to the last checkpoint.

**Sponge**: Any time the player lands on a sponge, the fall damage will be absorbed (unless the height kills them).