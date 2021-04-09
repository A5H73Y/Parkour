Frequently Asked Questions
======

##### Parkour Welcome Message

To disable the Parkour Join message, in the `config.yml` find `Other.Display.JoinWelcomeMessage` and set it to `false`.

##### Stuck Pressure Plates

To allow for multiple Players to stand on a pressure plate at once, in the `config.yml` find `OnCourse.PreventPlateStick` and set it to `true`.

## Terms / Meanings

#### Course

A Parkour Course is a physical path that you've created for the Player to use while using the plugin. You can join a Course (a.k.a. level, arena, track), and the plugin will track the Player's progress and apply any effects you interact with.

#### Lobby

A Lobby is simply a location that allows you to join Parkour Courses. It also acts as a place for the Player to teleport to when they complete or leave a Course.

#### ParkourKit

A ParkourKit is a set of Materials which act as a set of toolbox for building a Course. Each Material in a ParkourKit must have an action, for example "death", "speed", etc. A Course must have a ParkourKit, even if it's empty.

#### Config

Parkour is incredibly customisable, allowing you to modify the plugin exactly to what your server requires. In your server, Parkour will have a folder of many configuration files, shortened to config. `config.yml` & `strings.yml` are the only files we suggest you edit, unless you know what you're doing. Some server implementations don't save the changes upon restarting the server, so we highly suggest you use the **/pa reload** when you've made any config changes, then you'll be safe to restart your server without losing any changes.

#### ParkourLevel

A Player can earn levels will unlock new Courses for them to join. An example is 'course2' will be locked until they complete 'course1'. The reward can either be an exact level, or an addition to their current level. For example, you can finish all the 'easy' Courses in any order before having enough ParkourLevel to join the 'medium' Courses. The Player's ParkourLevel can never decrease, only increase or remain the same.

#### ParkourRank

When the Parkour chat is enabled, the Player's ParkourRank will be included in their message prefix. The Player can achieve a new ParkourRank when passing the required ParkourLevel to unlock it. This can be a status symbol for the Player's that have completed many of the harder Courses for example.
