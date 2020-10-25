Compatible Plugins
======

## Economy / Vault

Parkour supports the ability to use Economy through the Vault plugin, created by Sleaker.

The configuration option in the config.yml is "Other.Economy.Enabled" and must be set to true. When Parkour starts up, it will print a message whether it connected to the Vault plugin successfully.

Once successfully linked, you are able to reward players with an amount of currency, as well as charge them for joining a Course.

If there are Courses that exist before the plugin was linked with Vault, these need adding to the economy.yml, which is made easy by entering `/pa econ recreate`.

Once the process is complete it will notify you how many Courses were updated. At this stage the economy.yml file should contain all the courses on your server, and a prize and fee set to 0.

The joining fee and reward amount can be set in game, using `/pa econ setprize (course) (amount)` and `/pa econ setfee (course) (amount)`. Amount being the amount of currency to add / retract.

## BountifulAPI

BountifulAPI adds the ability to display titles and various other notifications, rather than printing the messages into the chat.

![Bountiful Example 1](https://i.imgur.com/E8BighB.png "Bountiful Example 1")
![Bountiful Example 2](https://i.imgur.com/fDsUmHV.png "Bountiful Example 2")
![Bountiful Example 3](https://i.imgur.com/bRvhdp8.png "Bountiful Example 3")

Created by connorlinfoot, available here: https://www.spigotmc.org/resources/bountifulapi-1-8-1-13.1394/

## Parkour Expansion

Allows for PlaceholderAPI to be used with the Parkour plugin, provides a large amount of available placeholder values. Can be used to extend to other plugins to allow for powerful compatibility.

![Parkour Expansion Example](https://i.imgur.com/ONkkWzM.png "Parkour Expansion Example")

### Available Parkour placeholders

    version
        What version of the Parkour plugin installed.
    course_count
        How many courses have been created on the server.
    player_count
        How many players currently have a ParkourSession on the server.
    last_completed
        The course last completed by the player. Player required.
    courses_completed
        The number of courses completed by the player. Player required.
    last_played
        The course last played by the player. Player required.
    parkoins
        The number of Parkoins accumilated by the player. Player required.
    level
        The ParkourLevel of the player. Player required.
    rank
        The ParkourRank of the player. Player required.

Plugin and image created by steve4744, available here: https://www.spigotmc.org/resources/parkour-expansion.41874/

## Parkour Top Ten

Allows for the player's Head to be proudly displayed next to their best times, great for a competitive Parkour server.

![Parkour Top Ten](https://i.imgur.com/c2n6QUM.png "Parkour Top Ten")

Plugin and image created by steve4744, available here: https://www.spigotmc.org/resources/parkour-top-ten.46268/