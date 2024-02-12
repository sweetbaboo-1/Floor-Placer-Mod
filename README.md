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
2. Download the latest release of Floor-Placer-Mod from the GitHub repository.
3. Place the downloaded .jar file into your server's "mods" folder.
4. Restart your Minecraft server.

## Usage
1. Ensure that CarpetMod and Floor-Placer-Mod are correctly installed and loaded on your server.
2. Save the floor schematic in the server's /resources folder.
3. In-game, use the command "/player <name> buildFloor <filename> <rows> <columns>" to initalize the player. Every time the player is told to "use" it will place the next block.
4. Control the automation process using "/player <name> use interval <interval>" to set an interval between each block placement, and "/player <name> stop" to pause the process.

## Known Issues
1. The first use command sent to the player may occasionally place the incorrect block.
2. The highest row of the floor may not be built properly in some cases.

## Contributing
Contributions to Floor-Placer-Mod are welcome! If you encounter any bugs or have suggestions for improvements, please open an issue on the GitHub repository.

## License
AutoFloor is licensed under the [MIT License](LICENSE).

## Credits
Thank you to KikuGie for general help
