Frequently Asked Questions
======

##### Server version support

Parkour 7.0.0+ supports all versions between 1.8 to 1.19+.  
Sound and Material names may differ between server versions and will need to be updated accordingly in your config.

##### Parkour Welcome Message

By default, a Parkour message is displayed to the Player when they join the Server.  
To disable, set `Other.Display.JoinWelcomeMessage` to `false` in the `config.yml`.

##### Stuck Pressure Plates

By default, Players can stand on pressure plates preventing others from activating them while on a Course.  
To allow for multiple Players to stand on a pressure plate at once, set `OnCourse.PreventPlateStick` to `true` in the `config.yml`.  
_Note that this will prevent normal pressure plates from working while on a Course._

##### Leaving Course Lobby

By default, when a Player leaves a Course they are taken to the default lobby.  
To change the Player to be teleported to the Linked Lobby instead of the default lobby, set `OnLeave.TeleportToLinkedLobby` to `true` in the `config.yml`.

##### Placeholders aren't working

Ensure you *don't* have Parkour Expansion installed, all the Placeholders now come included within the parkour plugin.  
To quickly test what the issue could be, try and run the command `/pa parse %parkour_global_version%` which may display any issues.

##### Remove Sneak / Shift for ParkourTools

By default, the Player needs to Sneak and Right-click the tools to activate the ParkourTools.  
To change it to just needing Right-click, set `OnCourse.SneakToInteract` to `false` in the `config.yml`.

_Note that you will need to update the `ParkourTool` strings.yml entries to remove the `SHIFT +` part._

##### Why aren't prizes working?

The setting `OnFinish.EnablePrizes` must be set to `true` in the `config.yml` to give the Player any prize, including Materials, running commands, ParkourLevels, ParkourRanks, etc.  
If the `RewardOnce` flag is set to `true` for the Course, you will not receive a Prize if you have an appropriate entry in the `course-completions.yml` file.

_Note if you are looking to stop giving the Player a diamond by default, set `CourseDefault.Prize.Amount` to `0`._
