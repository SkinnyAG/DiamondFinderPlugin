package edu.ntnu.machine_learning.project.diamondFinder;

import com.google.gson.Gson;
import edu.ntnu.machine_learning.project.diamondFinder.commands.ConnectCommand;
import edu.ntnu.machine_learning.project.diamondFinder.commands.DisconnectCommand;
import edu.ntnu.machine_learning.project.diamondFinder.commands.StartCommand;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class DiamondFinder extends JavaPlugin implements Listener {
  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;
  private boolean isConnected = false;
  private CommunicationLoop communicationLoop;

  @Override
  public void onEnable() {
    //getServer().getPluginManager().registerEvents(this, this);
    getCommand("connect").setExecutor(new ConnectCommand(this));
    getCommand("start").setExecutor(new StartCommand(this));
    getCommand("disconnectsocket").setExecutor(new DisconnectCommand(this));
  }

  /*@EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    event.setDropItems(false);

    List<ItemStack> drops = event.getBlock().getDrops().stream().toList();

    for (ItemStack drop : drops) {
      event.getPlayer().getInventory().addItem(drop);
    }
  }*/

  @Override
  public void onDisable() {
    stopCommunicationLoop();
    closeSocket();
  }

  public void startCommunicationLoop() {
      getLogger().info("Inside communication loop");
      communicationLoop = new CommunicationLoop(this, out, in);
      //communicationLoop.runTaskTimerAsynchronously(this, 0L, 100L);
      communicationLoop.runTaskAsynchronously(this);
  }

  public void stopCommunicationLoop() {
      getLogger().info("Should stop communication loop");
      communicationLoop.cancel();
      getLogger().info("Communication loop stopped: " + communicationLoop.isCancelled());
  }

  public void closeSocket() {
    try {
      if (socket != null && !socket.isClosed()) {
        out.println("/disconnect");
        socket.close();
      }
      isConnected = false;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean connectToServer() {
    try {
      socket = new Socket("localhost", 5000);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      isConnected = true;
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean isConnected() {
    return isConnected;
  }

  public PrintWriter getOut() {
    return out;
  }

  public BufferedReader getIn() {
    return in;
  }

  public String handleAction(String action, Player player) {
    String result = switch (action) {
      case "turn-left" -> turnLeft(player);
      case "turn-right" -> turnRight(player);
      case "move-forward" -> moveForward(player);
      case "mine" -> mineBlock(player);
      case "mine-lower" -> mineLowerBlock(player);
      case "tilt-down" -> tiltDown(player);
      case "tilt-up" -> tiltUp(player);
      case "forward-up" -> moveDiagonallyUp(player);
      case "forward-down" -> moveDiagonallyDown(player);
      default -> {
        player.sendMessage("Unknown action received: " + action);
        yield "unknown";
      }
    };
    return result;
  }
  private String turnLeft(Player player) {
    float yaw = player.getYaw();
    float pitch = player.getPitch();
    if (pitch > 89.5 || pitch < -89.5) {
      return "useless-rotation";
    }

    player.setRotation(yaw - 90, pitch);
    player.sendMessage("You turned left");
    return "rotation";
  }

  private String turnRight(Player player) {
    float yaw = player.getYaw();
    float pitch = player.getPitch();
    if (pitch > 89.5 || pitch < -89.5) {
      return "useless-rotation";
    }
    player.setRotation(yaw + 90, pitch);
    player.sendMessage("You turned right");
    return "rotation";
  }

  private String moveForward(Player player) {
    Location currentLocation = player.getLocation();
    Vector direction = currentLocation.getDirection();
    direction.setY(0);
    direction.normalize();

    Location targetLocation = currentLocation.clone().add(direction);

    Block lowerBlock = targetLocation.getBlock();
    Block upperBlock = targetLocation.clone().add(0,1,0).getBlock();

    Block belowBlock = targetLocation.clone().add(0,-1,0).getBlock();

    if (lowerBlock.getType() == Material.AIR && upperBlock.getType() == Material.AIR && belowBlock.getType() != Material.AIR) {
      player.teleport(targetLocation);
      player.sendMessage("You moved forward");
      return "successful-forward";
    } else {
      player.sendMessage("You cant move forward");
      return "illegal-forward";
    }
  }

  private String moveDiagonallyUp(Player player) {
    Location currentLocation = player.getLocation();
    Vector direction = currentLocation.getDirection();
    direction.setY(0);
    direction.normalize();

    Location targetLocation = currentLocation.clone().add(direction).add(0,1,0);

    Block middleFrontBlock = targetLocation.getBlock();
    Block lowerFrontBlock = targetLocation.clone().add(0,-1,0).getBlock();
    Block upperFrontBlock = targetLocation.clone().add(0,1,0).getBlock();
    Block roofBlock = currentLocation.clone().add(0,2,0).getBlock();


    if (lowerFrontBlock.isSolid() &&
            !middleFrontBlock.isSolid() &&
            !upperFrontBlock.isSolid() &&
            !roofBlock.isSolid()) {
      player.teleport(targetLocation);
      player.sendMessage("You moved diagonally up");
      return "successful-diag";
    } else {
      player.sendMessage("You can't move diagonally up");
      return "illegal-diag";
    }
  }

  private String moveDiagonallyDown(Player player) {
    Location currentLocation = player.getLocation();
    Vector direction = currentLocation.getDirection();
    direction.setY(0);
    direction.normalize();

    Location targetLocation = currentLocation.clone().add(direction).add(0,-1,0);

    Block frontBlockUpper = targetLocation.clone().add(0,2,0).getBlock();
    Block frontBlockLower = targetLocation.clone().add(0,1,0).getBlock();
    Block targetBlock = targetLocation.getBlock();
    Block blockBelowTarget = targetLocation.clone().add(0,-1,0).getBlock();

    if (!frontBlockUpper.isSolid() &&
            !frontBlockLower.isSolid() &&
            !targetBlock.isSolid() &&
            blockBelowTarget.isSolid()) {
      player.teleport(targetLocation);
      player.sendMessage("You moved diagonally down");
      return "successful-diag";
    } else {
      player.sendMessage("You can't move diagonally down");
      return "illegal-diag";
    }
  }

  private String mineBlock(Player player) {
    Set<Material> transparent = Set.of(Material.AIR, Material.WATER);
    Block target = player.getTargetBlock(transparent, 5);
    //player.sendMessage("Your target is: " + target.getType());
    if (target.isSolid() && target.getType() != Material.BEDROCK) {
      Collection<ItemStack> drops = target.getDrops();

      for (ItemStack drop : drops) {
        player.getInventory().addItem(drop);
      }
      Material originalTarget = target.getType();
      target.setType(Material.AIR);
      player.sendMessage("You mined: " + originalTarget);
      return ("successful-mine-" + originalTarget).toLowerCase();

    } else {
      player.sendMessage("You are not allowed to mine this");
      return "illegal-mine";
    }
  }

  private String mineLowerBlock(Player player) {
    Set<Material> transparent = Set.of(Material.AIR, Material.WATER);
    BlockFace direction = player.getFacing();
    Block target = player.getLocation().getBlock().getRelative(direction).getRelative(BlockFace.UP);
    if (target.isSolid() && target.getType() != Material.BEDROCK) {
      Collection<ItemStack> drops = target.getDrops();

      for (ItemStack drop : drops) {
        player.getInventory().addItem(drop);
      }
      Material originalTarget = target.getType();
      target.setType(Material.AIR);
      player.sendMessage("You mined: " + originalTarget);
      return ("successful-mine-" + originalTarget).toLowerCase();

    } else {
      if (!target.isSolid()) {
        player.sendMessage("You are not allowed to mine this");
        return "illegal-mine-air";
      } else  {
        player.sendMessage("You are not allowed to mine this");
        return "illegal-mine-bedrock";
      }
    }
  }

  private String tiltDown(Player player) {
    float yaw = player.getYaw();
    float pitch = player.getPitch();
    if (pitch + 45 > 90) {
      player.setRotation(yaw, 90);
      return "illegal-tilt";
    }

    player.setRotation(yaw, pitch + 45);
    player.sendMessage("You tilted down");
    return "successful-tilt";
  }

  private String tiltUp(Player player) {
    float yaw = player.getYaw();
    float pitch = player.getPitch();
    if (pitch - 45 < -90) {
      player.setRotation(yaw, -90);
      return "illegal-tilt";
    }

    player.setRotation(yaw, pitch - 45);
    player.sendMessage("You tilted up");
    return "successful-tilt";
  }
}
