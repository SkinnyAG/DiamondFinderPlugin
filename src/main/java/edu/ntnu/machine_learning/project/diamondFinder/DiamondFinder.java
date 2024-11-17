package edu.ntnu.machine_learning.project.diamondFinder;

import edu.ntnu.machine_learning.project.diamondFinder.commands.ConnectCommand;
import edu.ntnu.machine_learning.project.diamondFinder.commands.DisconnectCommand;
import edu.ntnu.machine_learning.project.diamondFinder.commands.StartCommand;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.Set;

public final class DiamondFinder extends JavaPlugin implements Listener {
  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;
  private boolean isConnected = false;
  private CommunicationLoop communicationLoop;

  @Override
  public void onEnable() {
    getCommand("connect").setExecutor(new ConnectCommand(this));
    getCommand("start").setExecutor(new StartCommand(this));
    getCommand("disconnectsocket").setExecutor(new DisconnectCommand(this));
  }

  @Override
  public void onDisable() {
    stopCommunicationLoop();
    closeSocket();
  }

  public void startCommunicationLoop() {
      getLogger().info("Inside communication loop");
      communicationLoop = new CommunicationLoop(this, out, in);
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
      case "mine-below-lower" -> mineBelowLowerBlock(player);
      case "mine-above-upper" -> mineAboveUpperBlock(player);
      case "mine-down" -> mineDown(player);
      case "mine-above" -> mineAbove(player);
      //case "tilt-down" -> tiltDown(player);
      //case "tilt-up" -> tiltUp(player);
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
    Block down = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

    Block forwardUnder = down.getRelative(player.getFacing());
    Block forwardLower = forwardUnder.getRelative(BlockFace.UP);
    Block forwardUpper = forwardLower.getRelative(BlockFace.UP);

    if (down.isSolid() && forwardUnder.isSolid() && !forwardLower.isSolid() && !forwardUpper.isSolid()) {
      float yaw = player.getYaw();
      float pitch = player.getPitch();
      player.sendMessage("You moved forward");
      player.teleport(forwardLower.getLocation().add(0.5,0,0.5));
      player.setRotation(yaw, pitch);
      return "successful-forward";
    } else {
      player.sendMessage("You can't move forward");
      return "illegal-forward";
    }
  }

  private String moveDiagonallyUp(Player player) {
    Block down = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

    Block forwardLower = down.getRelative(player.getFacing()).getRelative(BlockFace.UP);
    Block forwardUpper = forwardLower.getRelative(BlockFace.UP);
    Block forwardAbove = forwardUpper.getRelative(BlockFace.UP);
    Block above = down.getRelative(0,3,0);

    if (down.isSolid() && forwardLower.isSolid() && !forwardUpper.isSolid() &&
            !forwardAbove.isSolid() && !above.isSolid()) {
      float yaw = player.getYaw();
      float pitch = player.getPitch();
      player.sendMessage("You moved diagonally up");
      player.teleport(forwardUpper.getLocation().add(0.5,0,0.5));
      player.setRotation(yaw, pitch);
      return "successful-diag";
    } else {
      player.sendMessage("You can't move diagonally up");
      return "illegal-diag";
    }
  }

  private String moveDiagonallyDown(Player player) {
    Block down = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

    Block forwardDown = down.getRelative(player.getFacing());
    Block forwardDownUnder = forwardDown.getRelative(BlockFace.DOWN);
    Block forwardLower = forwardDown.getRelative(BlockFace.UP);
    Block forwardUpper = forwardLower.getRelative(BlockFace.UP);

    if (down.isSolid() && forwardDownUnder.isSolid() && !forwardDown.isSolid() &&
            !forwardLower.isSolid() && !forwardUpper.isSolid()) {
      float yaw = player.getYaw();
      float pitch = player.getPitch();
      player.sendMessage("You moved diagonally down");
      player.teleport(forwardDown.getLocation().add(0.5,0,0.5));
      player.setRotation(yaw, pitch);
      return "successful-diag";
    } else {
      player.sendMessage("You can't move diagonally down");
      return "illegal-diag";
    }
  }

  private String mineBlock(Player player) {
    Set<Material> transparent = Set.of(Material.AIR, Material.WATER);
    Block target = player.getTargetBlock(transparent, 5);
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
      if (!target.isSolid()) {
        return "illegal-mine-air";
      } else {
        return "illegal-mine-bedrock";
      }
    }
  }

  private String mineLowerBlock(Player player) {
    Block target = player.getLocation().getBlock().getRelative(player.getFacing());
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
      player.sendMessage("You are not allowed to mine this (lower-block-mine)");
      if (!target.isSolid()) {
        return "illegal-mine-air";
      } else  {
        return "illegal-mine-bedrock";
      }
    }
  }

  private String mineBelowLowerBlock(Player player) {
    Block blockToMine = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(player.getFacing());
    Block blockAbove = blockToMine.getRelative(BlockFace.UP);

    if (blockToMine.isSolid() && blockToMine.getType() != Material.BEDROCK && !blockAbove.isSolid()) {
      Collection<ItemStack> drops = blockToMine.getDrops();

      for (ItemStack drop : drops) {
        player.getInventory().addItem(drop);
      }
      Material originalTarget = blockToMine.getType();
      blockToMine.setType(Material.AIR);
      player.sendMessage("You mined: " + originalTarget);
      return ("successful-mine-" + originalTarget).toLowerCase();

    } else {
      player.sendMessage("You are not allowed to mine this (below-lower-block-mine)");
      if (!blockToMine.isSolid()) {
        return "illegal-mine-air";
      } else if (blockAbove.isSolid())  {
        return "illegal-diag-mine";
      } else {
        return "illegal-mine-bedrock";
      }
    }
  }

  private String mineAboveUpperBlock(Player player) {
    Block blockToMine = player.getLocation().getBlock().getRelative(0,2, 0).getRelative(player.getFacing());
    Block belowTarget = blockToMine.getRelative(BlockFace.DOWN);
    Block abovePlayer = blockToMine.getRelative(player.getFacing().getOppositeFace());

    if (blockToMine.isSolid() && blockToMine.getType() != Material.BEDROCK && (!belowTarget.isSolid() || !abovePlayer.isSolid())) {
      Collection<ItemStack> drops = blockToMine.getDrops();

      for (ItemStack drop : drops) {
        player.getInventory().addItem(drop);
      }
      Material originalTarget = blockToMine.getType();
      blockToMine.setType(Material.AIR);
      player.sendMessage("You mined: " + originalTarget);
      return ("successful-mine-" + originalTarget).toLowerCase();

    } else {
      player.sendMessage("You are not allowed to mine this (above-upper-block-mine)");
      if (!blockToMine.isSolid()) {
        return "illegal-mine-air";
      } else if (belowTarget.isSolid() && abovePlayer.isSolid())  {
        return "illegal-diag-mine";
      } else {
        return "illegal-mine-bedrock";
      }
    }
  }

  private String mineDown(Player player) {
    Block blockToMine = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
    if (blockToMine.isSolid() && blockToMine.getType() != Material.BEDROCK) {
      Collection<ItemStack> drops = blockToMine.getDrops();

      for (ItemStack drop : drops) {
        player.getInventory().addItem(drop);
      }
      Material originalTarget = blockToMine.getType();
      blockToMine.setType(Material.AIR);
      player.sendMessage("You mined: " + originalTarget);
      return ("successful-mine-" + originalTarget).toLowerCase();

    } else {
      player.sendMessage("You are not allowed to mine this (mine-down)");
      if (!blockToMine.isSolid()) {
        return "illegal-mine-air";
      } else  {
        return "illegal-mine-bedrock";
      }
    }
  }

  private String mineAbove(Player player) {
    Block blockToMine = player.getLocation().getBlock().getRelative(0,2,0);
    if (blockToMine.isSolid() && blockToMine.getType() != Material.BEDROCK) {
      Collection<ItemStack> drops = blockToMine.getDrops();

      for (ItemStack drop : drops) {
        player.getInventory().addItem(drop);
      }
      Material originalTarget = blockToMine.getType();
      blockToMine.setType(Material.AIR);
      player.sendMessage("You mined: " + originalTarget);
      return ("successful-mine-" + originalTarget).toLowerCase();

    } else {
      player.sendMessage("You are not allowed to mine this (mine-above)");
      if (!blockToMine.isSolid()) {
        return "illegal-mine-air";
      } else  {
        return "illegal-mine-bedrock";
      }
    }
  }

  /*
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
  }*/
}
