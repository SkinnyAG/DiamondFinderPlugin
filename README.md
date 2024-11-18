# Diamond Finder Plugin
Required custom plugin for communication between the Diamond Finder model and the Minecraft environment/agent.

Building the plugin .jar file
`mvn clean package`

For setting up the Minecraft server, follow the steps in this repository: //TODO

## Comunication Loop
Once loaded into the game, the player would start setting up communication with the python server using a "/connect" command, this requires the Python server to be running. A socket connection is then established between the two on localhost:5000.

The player can then issue a "/start" command to start the main communication loop. The loop starts with the model sending a "RESET" message to the plugin, resetting the agent and environment to its starting state for the episode.
The reset would generate a brand new mining area for each episode, with different ore placement, but following the game's generation rules. This was achived in conjuction with the custom plugin and the [WorldEdit](https://enginehub.org/worldedit) plugin. After the reset, the plugin collects the state of the player (coordinates, surroundingBlocks, actionResult, direction) and sends it over the socket.
The actionResult determines whether the action was successful or illegal and maps to an appropriate reward value (not calculated when "RESET" is sent).

If the player wished to stop the training before all planned episodes were completed, they could issue a "/disconnectsocket" command to stop the communication loop, close the socket connection and have the model save training statistics.
