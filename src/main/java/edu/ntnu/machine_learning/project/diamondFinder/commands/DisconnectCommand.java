package edu.ntnu.machine_learning.project.diamondFinder.commands;

import edu.ntnu.machine_learning.project.diamondFinder.DiamondFinder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisconnectCommand implements CommandExecutor {
  private final DiamondFinder plugin;

  public DisconnectCommand(DiamondFinder plugin) {
    this.plugin = plugin;
  }
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage("This command can only be used by players.");
      return true;
    }
    Player player = (Player) commandSender;

    if (!plugin.isConnected()) {
      player.sendMessage("Not currently connected to the server.");
      return true;
    }

    plugin.stopCommunicationLoop();
    plugin.closeSocket();

    player.sendMessage("Communication with main server stopped");
    return true;
  }
}
