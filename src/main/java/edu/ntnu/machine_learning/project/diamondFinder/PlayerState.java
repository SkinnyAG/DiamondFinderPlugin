package edu.ntnu.machine_learning.project.diamondFinder;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerState {
  private int x, y, z;
  private String actionResult;
  private String direction;
  private Map<String, Material> surroundingBlocks;

  public PlayerState(int x, int y, int z, String actionResult, String direction) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.actionResult = actionResult;
    this.direction = direction;
    this.surroundingBlocks = new HashMap<>();
  }
  public void updateSurroundingBlocks(Player player) {
    Set<Material> transparent = Set.of(Material.AIR, Material.WATER);
    Block target = player.getTargetBlock(transparent, 5);

    surroundingBlocks.clear();
    Block currentBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
    Block above = currentBlock.getRelative(0,3,0);

    Block underEast = currentBlock.getRelative(BlockFace.EAST);
    Block underWest = currentBlock.getRelative(BlockFace.WEST);
    Block underNorth = currentBlock.getRelative(BlockFace.NORTH);
    Block underSouth = currentBlock.getRelative(BlockFace.SOUTH);

    Block lowerEast = underEast.getRelative(BlockFace.UP);
    Block lowerWest = underWest.getRelative(BlockFace.UP);
    Block lowerNorth = underNorth.getRelative(BlockFace.UP);
    Block lowerSouth = underSouth.getRelative(BlockFace.UP);

    Block upperEast = lowerEast.getRelative(BlockFace.UP);
    Block upperWest = lowerWest.getRelative(BlockFace.UP);
    Block upperNorth = lowerNorth.getRelative(BlockFace.UP);
    Block upperSouth = lowerSouth.getRelative(BlockFace.UP);

    Block aboveEast = upperEast.getRelative(BlockFace.UP);
    Block aboveWest = upperWest.getRelative(BlockFace.UP);
    Block aboveNorth = upperNorth.getRelative(BlockFace.UP);
    Block aboveSouth = upperSouth.getRelative(BlockFace.UP);

    surroundingBlocks.put("targetBlock", target.getType());

    surroundingBlocks.put("down", currentBlock.getType());
    surroundingBlocks.put("up", above.getType());

    surroundingBlocks.put("underEast", underEast.getType());
    surroundingBlocks.put("underWest", underWest.getType());
    surroundingBlocks.put("underNorth", underNorth.getType());
    surroundingBlocks.put("underSouth", underSouth.getType());

    surroundingBlocks.put("lowerEast", lowerEast.getType());
    surroundingBlocks.put("lowerWest", lowerWest.getType());
    surroundingBlocks.put("lowerNorth", lowerNorth.getType());
    surroundingBlocks.put("lowerSouth", lowerSouth.getType());

    surroundingBlocks.put("upperEast", upperEast.getType());
    surroundingBlocks.put("upperWest", upperWest.getType());
    surroundingBlocks.put("upperNorth", upperNorth.getType());
    surroundingBlocks.put("upperSouth", upperSouth.getType());

    surroundingBlocks.put("aboveEast", aboveEast.getType());
    surroundingBlocks.put("aboveWest", aboveWest.getType());
    surroundingBlocks.put("aboveNorth", aboveNorth.getType());
    surroundingBlocks.put("aboveSouth", aboveSouth.getType());

  }

  private Block getRelativeBlock(Player player, Vector offset) {
    return player.getLocation().clone().add(offset).getBlock();
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  public Map<String, Material> getSurroundingBlocks() {
    return surroundingBlocks;
  }
}