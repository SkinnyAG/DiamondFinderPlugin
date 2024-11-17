package edu.ntnu.machine_learning.project.diamondFinder.commands;

import edu.ntnu.machine_learning.project.diamondFinder.DiamondFinder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartCommand implements CommandExecutor {
  private final DiamondFinder plugin;

  public StartCommand(DiamondFinder plugin) {
    this.plugin = plugin;
  }
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("This command can only be used by players");
      return true;
    }
    Player player = (Player) commandSender;

    if (!plugin.isConnected()) {
      player.sendMessage("You must connect to the server first using /connect");
      return true;
    }

    plugin.startCommunicationLoop();
    player.sendMessage("Loop started. Sending state...");
    return true;
  }
}
