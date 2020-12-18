Plugin Configuration
======

## config.yml

This configuration file is very large and can look daunting at first, but each section is broken up into logical section with clear names to hopefully make it easier.

To make changes, edit to the value you want, save the file, then enter `/pa reload` into game or the console.

Some properties require the server to restart to apply the changes, these include changes to scoreboard, adding 3rd party plugin support, etc.

![config.yml contents](https://i.imgur.com/jP8p9GY.png "config.yml contents")

View the raw annotated configuration file here: https://pastebin.com/eQGLqpVD.

_This is correct as of Parkour v5.1_

## strings.yml

You are able to modify the contents of this file, then enter `/pa reload` for the changes to immediately apply.

If you are having problems, it may be because the yml is considered invalid if it requires `'` either side of the string, when using certain characters such as %.