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
  //private String direction;
  private Map<String, Material> surroundingBlocks;

  public PlayerState(int x, int y, int z, String actionResult) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.actionResult = actionResult;
    //this.direction = direction;
    this.surroundingBlocks = new HashMap<>();
  }
  public void updateSurroundingBlocks(Player player) {
    Set<Material> transparent = Set.of(Material.AIR, Material.WATER);
    Block target = player.getTargetBlock(transparent, 5);

    surroundingBlocks.clear();
    Block currentBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
    Block above = currentBlock.getRelative(0,3,0);

    Block underForward;
    Block underBehind;
    Block underRight;
    Block underLeft;

    // Make into switch
    if (player.getFacing() == BlockFace.EAST) {
      underForward = currentBlock.getRelative(BlockFace.EAST);
      underBehind = currentBlock.getRelative(BlockFace.WEST);
      underRight = currentBlock.getRelative(BlockFace.SOUTH);
      underLeft = currentBlock.getRelative(BlockFace.NORTH);
    } else if (player.getFacing() == BlockFace.WEST) {
      underForward = currentBlock.getRelative(BlockFace.WEST);
      underBehind = currentBlock.getRelative(BlockFace.EAST);
      underRight = currentBlock.getRelative(BlockFace.NORTH);
      underLeft = currentBlock.getRelative(BlockFace.SOUTH);
    } else if (player.getFacing() == BlockFace.SOUTH) {
      underForward = currentBlock.getRelative(BlockFace.SOUTH);
      underBehind = currentBlock.getRelative(BlockFace.NORTH);
      underRight = currentBlock.getRelative(BlockFace.WEST);
      underLeft = currentBlock.getRelative(BlockFace.EAST);
    } else if (player.getFacing() == BlockFace.NORTH) {
      underForward = currentBlock.getRelative(BlockFace.NORTH);
      underBehind = currentBlock.getRelative(BlockFace.SOUTH);
      underRight = currentBlock.getRelative(BlockFace.EAST);
      underLeft = currentBlock.getRelative(BlockFace.WEST);
    } else {
      player.sendMessage("Error determining relative blocks");
      return;
    }

    Block lowerForward = underForward.getRelative(BlockFace.UP);
    Block lowerBehind = underBehind.getRelative(BlockFace.UP);
    Block lowerRight = underRight.getRelative(BlockFace.UP);
    Block lowerLeft = underLeft.getRelative(BlockFace.UP);

    Block upperForward = lowerForward.getRelative(BlockFace.UP);
    Block upperBehind = lowerBehind.getRelative(BlockFace.UP);
    Block upperRight = lowerRight.getRelative(BlockFace.UP);
    Block upperLeft = lowerLeft.getRelative(BlockFace.UP);

    Block aboveForward = upperForward.getRelative(BlockFace.UP);
    Block aboveBehind = upperBehind.getRelative(BlockFace.UP);
    Block aboveRight = upperRight.getRelative(BlockFace.UP);
    Block aboveLeft = upperLeft.getRelative(BlockFace.UP);
    /*
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
    */

    surroundingBlocks.put("targetBlock", target.getType());

    surroundingBlocks.put("down", currentBlock.getType());
    surroundingBlocks.put("up", above.getType());

    surroundingBlocks.put("underForward", underForward.getType());
    surroundingBlocks.put("underBehind", underBehind.getType());
    surroundingBlocks.put("underRight", underRight.getType());
    surroundingBlocks.put("underLeft", underLeft.getType());

    surroundingBlocks.put("lowerForward", lowerForward.getType());
    surroundingBlocks.put("lowerBehind", lowerBehind.getType());
    surroundingBlocks.put("lowerRight", lowerRight.getType());
    surroundingBlocks.put("lowerLeft", lowerLeft.getType());

    surroundingBlocks.put("upperForward", upperForward.getType());
    surroundingBlocks.put("upperBehind", upperBehind.getType());
    surroundingBlocks.put("upperRight", upperRight.getType());
    surroundingBlocks.put("upperLeft", upperLeft.getType());

    surroundingBlocks.put("aboveForward", aboveForward.getType());
    surroundingBlocks.put("aboveBehind", aboveBehind.getType());
    surroundingBlocks.put("aboveRight", aboveRight.getType());
    surroundingBlocks.put("aboveLeft", aboveLeft.getType());
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