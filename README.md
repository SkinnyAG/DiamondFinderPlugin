# Diamond Finder Plugin
Required custom plugin for communication between the Diamond Finder model and the Minecraft environment/agent.
- [OreAnalyzer](https://github.com/MindChirp/ore-analyzer)
- [DiamondFinder](https://github.com/SkinnyAG/DiamondFinder)

***This plugin is meant to run on Minecraft version 1.21.1/1.21***

The plugin is built with [PaperMC](https://papermc.io/), and thus is compatible with both [Bukkit](https://dev.bukkit.org/) and [SpigotMC](https://www.spigotmc.org/) plugin APIs.

Building the plugin .jar file

`$ mvn clean package`

You should now see a 'DiamondFinder-1.0-SNAPSHOT.jar' file in your target directory.

Diving into how to set up a Minecraft server for testing the model is outside of the scope of this assignment. Here is a generalvideo explaining how to set up a basic server with plugin support as well as links to required plugins.
- [Minecraft Server with Plugins](https://youtu.be/9xXFrN8OhHA?si=Kr4yAiGg_EJ34133)
- [WorldEdit](https://modrinth.com/plugin/worldedit/version/ecqqLKUO)
- [FastAsyncWorldEdit](https://modrinth.com/plugin/fastasyncworldedit)
- The DiamondFinder .jar

Once on the server, it is recommended to enable night vision with the command:

`/effect give <playerName> minecraft:night_vision infinite true`

## Comunication Loop
Once loaded into the game, the player would start setting up communication with the python server using a "/connect" command, this requires the Python server to be running. A socket connection is then established between the two on localhost:5000.

The player can then issue a "/start" command to start the main communication loop. The loop starts with the model sending a "RESET" message to the plugin, resetting the agent and environment to its starting state for the episode.
The reset would generate a brand new mining area for each episode, with different ore placement, but following the game's generation rules. This was achived in conjuction with the custom plugin and the [WorldEdit](https://enginehub.org/worldedit) plugin. After the reset, the plugin collects the state of the player (coordinates, surroundingBlocks, actionResult) and sends it over the socket.
The actionResult determines whether the action was successful or illegal and maps to an appropriate reward value (not calculated when "RESET" is sent).

If the player wishes to stop the training before all planned episodes are completed, they could issue a "/disconnectsocket" command to stop the communication loop, close the socket connection and have the model save training statistics.
