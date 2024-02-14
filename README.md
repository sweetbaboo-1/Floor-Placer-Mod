# CarpetMod Extension: Floor-Placer-Mod

## Description
Floor-Placer-Mod is an extension mod for CarpetMod designed to automate the process of building floors in Minecraft. It provides a convenient command, "/player <name> buildFloor <filename> <rows> <columns>", which allows players to automatically construct floors by specifying the filename of the floor layout, the number of rows, and the number of columns.

Additionally, this mod introduces functionality to control the automation process. Players can start and pause the floor-building command using "/player <name> use interval <interval>" and "/player <name> stop" commands respectively.

## Features
- Automates floor building using the "/player <name> buildFloor <filename> <rows> <columns>" command.
- Controls automation with "/player <name> use interval <interval>" and "/player <name> stop" commands.
- Requires floor layouts to be saved in the server's /resources folder.

## Installation
1. Ensure you have CarpetMod installed on your Minecraft server.
2. Ensure you have Syncmatica installed on your Minecraft server.
3. Download the latest release of Floor-Placer-Mod from the GitHub repository.
4. Place the downloaded .jar file into your server's "mods" folder.
5. Restart your Minecraft server.

## Usage
1. Ensure that CarpetMod, Syncmatica, and Floor-Placer-Mod are correctly installed and loaded on your server.
2. Save the schematic that you would like to build first selecting the north-west point, then the south-east point. Make sure that your schematic is in the x, z plane and only 1 block tall.
3. Create a placement of the schematic and use Syncmatica to sync it to the server. This is what uploads the file to a location where the mod is able to access it. The location of the synced schematic is not relevant. 
4. In-game, use the command "/player <name> buildFloor <schematic-name> <rows> <columns>" to initialize the player. Every time the player is told to "use" it will place the next block.
5. Control the automation process using "/player <name> use interval <interval>" to set an interval between each block placement, and "/player <name> stop" to pause the process.

You can save the progress of the build with the "/player <name> buildFloor saveState" command, and load it with the "/player <name> buildFloor loadState". Only one build can be saved at a time, saving again overwrites the previous save.

## Known Issues
If you begin building a floor, stop the bot, save the state then manually change the bot's hotbar, then load the state and continue placing, one block will be wrong. I don't know why anyone would do this but someone will, and now I can say it was in the readme go away.
Because the mod doesn't change the orientation of the blocks (they're all placed horizontally) blocks that require specific orientation are not compatible. 

## TODO
1. Add error handling
2. Fix save and load state.


## Contributing
Contributions to Floor-Placer-Mod are welcome! If you encounter any bugs or have suggestions for improvements, please open an issue on the GitHub repository.

## License
Floor-Placer-Mod is licensed under the [MIT License](LICENSE).

## Credits
Thank you to KikuGie for help with the mixins.
