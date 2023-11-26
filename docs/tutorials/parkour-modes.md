Parkour Modes
======

A Course can have a ParkourMode assigned to it, these allow the Courses to behave differently and add a new dynamic. Below each ParkourMode is listed with the gameplay affects they make once applied.

To set a ParkourMode to a Course, you start the conversation by entering `/pa setcourse (course) mode`, which will talk you through the setup process of each option. Some ParkourModes require additional information to set up.

_If you want to remove a ParkourMode, you can set the mode back to "None"._

## Freedom

This mode allows you to set and load your own checkpoints anywhere throughout the Course, using the freedom tool. Which will be added to your inventory when you join:

![Freedom Inventory](https://i.imgur.com/1GmoO1k.png "Freedom Inventory")

When you join a Freedom Course, it will display a message to explain how the tool works:

![Freedom Information](https://i.imgur.com/JObldpv.png "Freedom Information")

It's as simple as that, right click with the tool to save your position, and left click to load it.

![Freedom Usage](https://i.imgur.com/TZ4p8UM.png "Freedom Usage")

## Potion

This ParkourMode replaces the existing "Darkness", "Drunk" and "Jump" ParkourModes by allowing you to choose your own Potion Effects to apply the Player once they join the Course.

![Potion Conversation](https://i.imgur.com/zse9E0X.png "Potion Conversation")

_All valid PotionEffectTypes can be [found here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html)._

When requested, the Duration and Amplifier must be separated with a comma `,` for example `10000,1`.

![Potion Applied](https://i.imgur.com/EhFL3Ku.png "Potion Applied")

## Speedy

Simply sets the Player's movement speed to what is set in the `config.yml`. Allows you to run faster and jump further. Movement speed is reset to default once the Player leaves.

## Dropper

This has now been replaced by the ability to set MaxFallTicks to 0, and setting "hasFallDamage" to false in the Course's config.

## Rockets

Inspired by CodJumping, when you use an RPG's explosion to propel yourself into the air to gain additional height and velocity.

You will be given a 'Rocket Launcher', which is a Rocket by default. Simply right click with the item in your hand, and you will have a knock-back applied to your Player, which you can use to reach new heights and distances. The time between reuse can be configured, to reduce risk of it being abused.

![Rocket Launcher Item](https://i.imgur.com/5xFkFHR.png "Rocket Launcher Item")

![Rocket Launcher Use](https://i.imgur.com/i1f2Dmc.png "Rocket Launcher Use")

## Free Checkpoint

This ParkourMode has been replaced by a per-course [manualcheckpoints](/tutorials/parkour-courses?id=manualcheckpoints) setting now.
