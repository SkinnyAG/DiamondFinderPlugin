package edu.ntnu.machine_learning.project.diamondFinder;

import org.bukkit.Location;
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
  private int tilt;
  private String actionResult;
  private String direction;
  private Map<String, Material> surroundingBlocks;

  public PlayerState(int x, int y, int z, int tilt, String actionResult, String direction) {
    //this.playerName = playerName;
    this.x = x;
    this.y = y;
    this.z = z;
    this.tilt = tilt;
    this.actionResult = actionResult;
    this.direction = direction;
    this.surroundingBlocks = new HashMap<>();
  }
  public void updateSurroundingBlocks(Player player) {
    Set<Material> transparent = Set.of(Material.AIR, Material.WATER);
    Block target = player.getTargetBlock(transparent, 5);
    //Vector direction = player.getLocation().getDirection();
    //direction.setY(0);
    //direction.normalize();

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

    /*player.sendMessage("Target: " + target + ", below: " + currentBlock +
            ", above: " + above + ", underEast: " + underEast + ", underWest: " + underWest +
            ", underNorth: " + underNorth + ", underSouth: " + underSouth + ", lowerEast: " + lowerEast +
            ", lowerWest: " + lowerWest + ", lowerNorth: " + lowerNorth, ", lowerSouth: " + lowerSouth +
            ", upperEast: " + upperEast + ", upperWest: " + upperWest + ", upperNorth: " + upperNorth +
            ", upperSouth: " + upperSouth + ", aboveEast: " + aboveEast + ", aboveWest: " + aboveWest +
            ", aboveNorth: " + aboveNorth + ", aboveSouth: " + aboveSouth);*/

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
    //player.sendMessage("Surrounding blocks: " + surroundingBlocks);
    /*
    surroundingBlocks.put("targetBlock", target.getType());
    surroundingBlocks.put("forwardAbove", getRelativeBlock(player, direction.clone().add(new Vector(0,2,0))).getType());
    surroundingBlocks.put("forwardTop", getRelativeBlock(player, direction.clone().add(new Vector(0,1,0))).getType());
    surroundingBlocks.put("forwardBottom", getRelativeBlock(player, direction.clone()).getType());
    surroundingBlocks.put("forwardDown", getRelativeBlock(player, direction.clone().add(new Vector(0,-1,0))).getType());

    surroundingBlocks.put("behindBottom", getRelativeBlock(player, direction.clone().multiply(-1)).getType());
    surroundingBlocks.put("behindTop", getRelativeBlock(player, direction.clone().multiply(-1).add(new Vector(0,1,0))).getType());

    surroundingBlocks.put("leftBottom", getRelativeBlock(player, direction.clone().rotateAroundY(Math.toRadians(90))).getType());
    surroundingBlocks.put("leftTop", getRelativeBlock(player, direction.clone().rotateAroundY(Math.toRadians(90)).add(new Vector(0, 1, 0))).getType());
    surroundingBlocks.put("rightBottom", getRelativeBlock(player, direction.clone().rotateAroundY(Math.toRadians(-90))).getType());
    surroundingBlocks.put("rightTop", getRelativeBlock(player, direction.clone().rotateAroundY(Math.toRadians(-90)).add(new Vector(0, 1, 0))).getType());

    surroundingBlocks.put("up", getRelativeBlock(player, new Vector(0, 2, 0)).getType());
    surroundingBlocks.put("down", getRelativeBlock(player, new Vector(0, -1, 0)).getType());
    */
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