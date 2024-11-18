package edu.ntnu.machine_learning.project.diamondFinder;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CommunicationLoop extends BukkitRunnable {
  private final DiamondFinder plugin;
  private final PrintWriter out;
  private final BufferedReader in;
  private MiningAreaManager miningAreaManager;
  private int randomSeed = 0;

  private boolean running = true;

  public CommunicationLoop(DiamondFinder plugin, PrintWriter out, BufferedReader in) {
    this.plugin = plugin;
    this.in = in;
    this.out = out;
    this.miningAreaManager = new MiningAreaManager(randomSeed);
  }

  @Override
  public void run() {
    Player player = plugin.getServer().getOnlinePlayers().stream().findFirst().get();
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
              miningAreaManager.checkAndExpandArea(player);
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
    randomSeed++;

    this.miningAreaManager = new MiningAreaManager(randomSeed);

    player.performCommand("/pos1 0,1,0");
    player.performCommand("/pos2 128,-60,128");
    player.performCommand("/regen " + randomSeed);
    player.performCommand("/cut -m !deepslate_diamond_ore,deepslate_iron_ore,deepslate_gold_ore,deepslate_redstone_ore,deepslate_lapis_ore deepslate");
    player.performCommand("remove minecarts 128");
    player.performCommand("/pos1 64,-30,64");
    player.performCommand("/pos2 64,-29,64");
    player.performCommand("/cut");
    Location startLocation = new Location(player.getWorld(), 64.5, -30, 64.5);
    startLocation.setYaw(0);
    startLocation.setPitch(0);
    player.performCommand("clear");
    player.teleport(startLocation);
  }

  private void sendState(Player player, String result) {
    PlayerState currentState = new PlayerState(
            (int) player.getX(),
            (int) player.getY(),
            (int) player.getZ(),
            result
            //player.getFacing().name()
    );
    currentState.updateSurroundingBlocks(player);
    String jsonState = new Gson().toJson(currentState);
    out.println(jsonState);
  }
}
