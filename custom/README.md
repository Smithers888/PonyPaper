# PonyPaper Custom Pony Editor
This tool enables the creation of custom ponies to use on your Android wallpaper.

## Overview
PonyPaper represent each custom pony as an XML file. This editor is capable of creating and editting these files, selecting the desired sprites and behaviours of the pony.

Once a pony has been created by the editor, it can be loaded into the wallpaper by connecting your device to your computer and copying the .xml file into the `Android/data/uk.cpjsmith.ponypaper/files` directory and then forcing the wallpaper to reload custom ponies by going to the wallpaper's preferences and disabling and re-enabling custom ponies. (Note: you can disable all of the built-in ponies as a quick way to test that your custom pony has loaded).

## Sprites
For each action that your pony can perform, the app requires a sprite each for the left- and right-facing directions. The editor can import sprites in two formats.

1. You can import a GIF animation designed for use in Desktop Ponies. The editor will convert such files into the second format so that the app can use them.
2. This is the format that the wallpaper app requires. Each spritesheet is a single PNG image, containing all of the frames of the animation, layed out left to right. See [the built-in spritesheets](res/drawable) for examples. This is used together with a list of numbers specifying how long each frame lasts, in hundredths of a second. (The length of this array is also used to determine the number of frames).

The file [twilight-sparkle.xml](custom/twilight-sparkle.xml) contains a copy of the built-in Twilight Sparkle. Twilight can be either a unicorn or an alicorn and can both fly and teleport, so she has examples of many possible details in creating ponies.

## The Editor
The editor requires Java 8. It can be started by lauching `customponies.jar`, either from the file manager or from the command line with `java -jar customponies.jar`.

On the left side of the editor is the list of actions. You can create a new action or delete the selected action using the buttons underneath the list. Selecting an action in this list allows its properties to be edited on the right. These properties are:
* Special type: This field should usually be left blank. the only current exceptions to this rule are actions related to teleporting; see the section on 'Teleporting', below.
* Left/right sprite: The text field simply states whether an image has been loaded or not. You can use the 'Preview' button to display the image and the 'Import image' button to load a new one. Once you have entered the timings, moving the cursor over the preview will highlight the frames, allowing you to verify that the correct number of times have been entered.
* Left/right timings: The list of durations for each frame of the animation. These are represented in hundredths of a second and seperated by commas. Note: if you import a GIF animation, this field will be filled in automatically.
* Next moving/waiting/drag actions: The comma-seperated list of possible actions the pony can transition to when it decides to move/wait or is dragged by the user. Note that the same action can be used for more than one of these three states; for example, many pegasi reuse the same flying action for both movement and hovering in-place.

At the bottom is the list of 'Start actions', the ways the pony can choose to initially enter the scene.

In the action lists (either 'Start actions' or 'Next [whatever] actions') the same action can be repeated multiple times to make it more likely to be selected. For example, the built-in Rainbow Dash has a start action list of `trot,fly,fly,fly`, so she will choose the 'fly' action three-quarters of the time. Fluttershy, who prefers to keep her hooves on the ground, has a start action list of `trot,trot,trot,fly`.

## Teleporting
Normally, when a pony selects a moving action, it loops the animation while gradually moving towards its destination. To enable teleporting requires special handling.

Teleporting requires two actions. The first should have a 'Special type' of `teleport-out` and the second `teleport-in`. Other actions should contain the 'teleport-out' action on their 'Next moving actions' lists; the 'teleport-out' action should have the 'teleport-in' action as its only next moving action. When the pony decides to use the 'teleport-out' action, that animation will play only once without moving, then the pony will move instantly to the destination and play the 'teleport-in' animation once.
