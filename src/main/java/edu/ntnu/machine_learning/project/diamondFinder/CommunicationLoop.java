package edu.ntnu.machine_learning.project.diamondFinder;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CommunicationLoop extends BukkitRunnable {
  private final DiamondFinder plugin;
  private final PrintWriter out;
  private final BufferedReader in;
  private boolean running = true;

  public CommunicationLoop(DiamondFinder plugin, PrintWriter out, BufferedReader in) {
    this.plugin = plugin;
    this.in = in;
    this.out = out;
  }

  @Override
  public void run() {
    Player player = plugin.getServer().getPlayer("SkinnyAG");
    if (player == null || !plugin.isConnected()) {
      cancel();
      return;
    }

    try {
      while (running && !isCancelled()) {
        String action = in.readLine();

        if (action == null) {
          running = false;
          cancel();
          break;
        }

        switch (action.trim()) {
          case "RESET":
            plugin.getServer().getScheduler().runTask(plugin, () -> {
              resetPlayer(player);
              sendState(player, "started");
            });
            break;
          default:
            plugin.getServer().getScheduler().runTask(plugin, () -> {
              String result = plugin.handleAction(action, player);
              sendState(player, result);
            });
            break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      cancel();
    }
  }

  private void resetPlayer(Player player) {
    player.sendMessage("Reset called");
    player.performCommand("clear");

    Location startLocation = new Location(player.getWorld(), 64.5, 32, 64.5);
    startLocation.setYaw(0);
    startLocation.setPitch(0);
    player.teleport(startLocation);
    // TODO Add rotation
    player.performCommand("/rotate 90");
    player.performCommand("/paste");
  }

  private void sendState(Player player, String result) {
    PlayerState currentState = new PlayerState(
            (int) player.getX(),
            (int) player.getY(),
            (int) player.getZ(),
            Math.round(player.getPitch()),
            result
    );
    currentState.updateSurroundingBlocks(player);
    String jsonState = new Gson().toJson(currentState);
    out.println(jsonState);
  }
}
