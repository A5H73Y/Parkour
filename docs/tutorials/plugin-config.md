Plugin Configuration
======

Parkour will create and maintain various YAML files in the plugin's config folder, the only files which you are able to edit freely are `config.yml` and `strings.yml`.

To make changes, edit to the value you want, save the file, then enter `/pa reload` into game or the console. Reloading the server can sometimes not apply the changes.

Some properties require the server to restart to apply the changes, these include changes to scoreboard, adding 3rd party plugin support, etc.

It's important to know that YAML has very strict formatting rules and will fail if you accidentally break any of these, if you are having any issues please use a [YAML Validator](https://codebeautify.org/yaml-validator).

## config.yml

This configuration file is very large and can look daunting at first, but each section is broken up into logical sections with clear names to hopefully make it easier.

View the annotated configuration file here: [https://pastebin.com/TZicmuhi](https://pastebin.com/TZicmuhi).

_This is correct as of Parkour v6.5_

## strings.yml

You are able to modify the contents of this file, then enter `/pa reload` for the changes to immediately apply.

If you are having problems, it may be because the yml is considered invalid if it requires `'` either side of the string, when using certain characters such as %.

_When in doubt, run your `strings.yml` contents through a validator such as: [https://codebeautify.org/yaml-validator](https://codebeautify.org/yaml-validator) which should identify any problems._

### User submitted translations

Here are translations submitted by users for a specific language, I take no responsibility for their accuracy.

[Chinese / Mandarin (CH)](files/translations/ch/strings.yml)

[Spanish (ES)](files/translations/es/strings.yml)
