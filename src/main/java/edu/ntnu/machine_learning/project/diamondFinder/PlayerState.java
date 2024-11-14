package edu.ntnu.machine_learning.project.diamondFinder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerState {
  private int x, y, z;
  private int tilt;
  private String actionResult;
  private Map<String, Material> surroundingBlocks;

  public PlayerState(int x, int y, int z, int tilt, String actionResult) {
    //this.playerName = playerName;
    this.x = x;
    this.y = y;
    this.z = z;
    this.tilt = tilt;
    this.actionResult = actionResult;
    this.surroundingBlocks = new HashMap<>();
  }
  public void updateSurroundingBlocks(Player player) {
    Set<Material> transparent = Set.of(Material.AIR, Material.WATER);
    Block target = player.getTargetBlock(transparent, 5);
    Vector direction = player.getLocation().getDirection();
    direction.setY(0);
    direction.normalize();

    surroundingBlocks.clear();

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