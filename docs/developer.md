Developer Tutorials
======

Are you a developer? Great! You can make the most of out Parkour's functionality to create the perfect experience for your server. I've made the plugin super easy to expand and integrate into, first we will import Parkour project to begin working with it.

## Importing Parkour into your Project

You will want to add Parkour's repository to the list of repositories so that you can bring in the Parkour project.

```
<repository>
    <id>a5h73y-repo</id>
    <url>https://dl.bintray.com/a5h73y/repo/</url>
</repository>
```

Add the following dependency to your list of dependencies and let Maven import the project.

```
<dependency>
    <groupId>io.github.a5h73y</groupId>
    <artifactId>Parkour</artifactId>
    <version>(INSERT LATEST VERSION)</version>
    <type>jar</type>
    <scope>provided</scope>
</dependency>
```

Maven should now import the Parkour project and its dependencies, make sure your project builds correctly and then continue.

You can also add the Parkour.jar to your classpath if you wish for the same outcome, but Maven makes this stage considerably easier.

## Setting up your Plugin

You'll need to decide if your plugin depends on my plugin, or is just an optional dependency. In your plugin.yml enter either of the following:

```
depend: [Parkour]
softdepend: [Parkour]
```

This will allow Parkour to fully initialize before you start to use it.

Now you need to check if the Parkour plugin has started correctly within the code, this is for you to check and handle. This will look something like: 

```
Plugin parkour = getServer().getPluginManager().getPlugin("Parkour");
if (parkour != null && parkour.isEnabled()) {
    System.out.println("Found Parkour v" + parkour.getDescription().getVersion())
} else {
    /* oh no, Parkour isn't installed */
}
```

If your Plugin successfully links with Parkour, your plugin can now interact with Parkour and listen to the events it fires.

## Parkour Events

There are a list of Events that Parkour creates, that you can listen to:

- PlayerJoinCourseEvent
- PlayerLeaveCourseEvent
- PlayerAchieveCheckpointEvent
- PlayerDeathEvent
- PlayerFinishCourseEvent
- PlayerParkourLevelEvent

Each of these will give you at least the Player Object and the Course name for you to use. The Checkpoint and Level event will give you the relevant values also.

To listen for a Parkour event you must create a Listener class and register it correctly, then create an EventHandler like any normal event:

```
@EventHandler
public void onCourseCompletion(PlayerFinishCourseEvent event) {
    String completedCourse = event.getCourseName();
    Player player = event.getPlayer();

    player.sendMessage("You completed " + completedCourse);
}
```

## Retrieving Information

There are helper classes to help you easily find information about the plugin, these are covered under `PlayerInfo` and `CourseInfo`. These will allow you to find out the player's relevant Parkour stored information, as well as everything about the Parkour Courses.

    Do you feel like Parkour and its users could benefit from the changes you've made?
    
    Create a Pull Request and I will take a look at it.


