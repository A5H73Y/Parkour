Frequently Asked Questions
======

##### Parkour Welcome Message

To disable the Parkour Join message, in the `config.yml` find `Other.Display.JoinWelcomeMessage` and set it to `false`.

##### Stuck Pressure Plates

To allow for multiple Players to stand on a pressure plate at once, in the `config.yml` find `OnCourse.PreventPlateStick` and set it to `true`.

[//]: # (TODO move these somwhere)

#### ParkourLevel

A Player can earn levels which will unlock new Courses for them to join. An example is 'course2' will be locked until they complete 'course1'. The reward can either be an exact level, or an addition to their current level. For example, you can finish all the 'easy' Courses in any order before having enough ParkourLevel to join the 'medium' Courses. The Player's ParkourLevel can never decrease, only increase or remain the same.

#### ParkourRank

When the Parkour chat is enabled, the Player's ParkourRank will be included in their message prefix. The Player can achieve a new ParkourRank when passing the required ParkourLevel to unlock it. This can be a status symbol for the Player's that have completed many of the harder Courses for example.
