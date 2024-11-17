package edu.ntnu.machine_learning.project.diamondFinder;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MiningAreaManager {
  private List<MiningArea> miningAreas;
  private final int expansionSize = 128;
  private int randomSeed;
  private final int boundaryThreshold = 8;

  public MiningAreaManager(int randomSeed) {
    this.randomSeed = randomSeed;
    this.miningAreas = new ArrayList<>();
    miningAreas.add(new MiningArea(0, 128, 0, 128));
  }

  public static class MiningArea {
    int xMin, xMax, zMin, zMax;
    boolean expandedEast, expandedWest, expandedNorth, expandedSouth;

    public MiningArea(int xMin, int xMax, int zMin, int zMax) {
      this.xMin = xMin;
      this.xMax = xMax;
      this.zMin = zMin;
      this.zMax = zMax;
    }

    public boolean containsPlayer(int x, int z) {
      return x >= xMin && x <= xMax && z >= zMin && z <= zMax;
    }
  }

  public void checkAndExpandArea(Player player) {
    Location location = player.getLocation();
    int playerX = location.getBlockX();
    int playerZ = location.getBlockZ();

    MiningArea currentArea = null;
    for (MiningArea area : miningAreas) {
      if (area.containsPlayer(playerX, playerZ)) {
        currentArea = area;
        break;
      }
    }

    if (currentArea == null) {
      player.sendMessage("You are outside the defined mining areas.");
      return;
    }

    if (needsExpansion(playerX, playerZ, currentArea)) {
      expandMiningArea(currentArea, player, playerX, playerZ);
    }
  }

  private boolean needsExpansion(int playerX, int playerZ, MiningArea currentArea) {
    return playerX >= currentArea.xMax - boundaryThreshold || playerX <= currentArea.xMin + boundaryThreshold ||
            playerZ >= currentArea.zMax - boundaryThreshold || playerZ <= currentArea.zMin + boundaryThreshold;
  }
   private void expandMiningArea(MiningArea currentArea, Player player, int playerX, int playerZ) {
    int newMinX = currentArea.xMin;
    int newMaxX = currentArea.xMax;
    int newMinZ = currentArea.zMin;
    int newMaxZ = currentArea.zMax;

    if (playerX + boundaryThreshold >= currentArea.xMax && !currentArea.expandedEast) {
      player.sendMessage("Expanding east");
      newMinX = currentArea.xMax;
      newMaxX = currentArea.xMax + expansionSize;
      currentArea.expandedEast = true;

      MiningArea newArea = new MiningArea(newMinX, newMaxX, newMinZ, newMaxZ);
      miningAreas.add(newArea);
      newArea.expandedWest = true;

      generateArea(player, newMinX, newMaxX, newMinZ, newMaxZ);
      return;
    } else if (playerX - boundaryThreshold <= currentArea.xMin && !currentArea.expandedWest) {
      player.sendMessage("Expanding west");
      newMinX = currentArea.xMin - expansionSize;
      newMaxX = currentArea.xMin;
      currentArea.expandedWest = true;

      MiningArea newArea = new MiningArea(newMinX, newMaxX, newMinZ, newMaxZ);
      miningAreas.add(newArea);
      newArea.expandedEast = true;

      generateArea(player, newMinX, newMaxX, newMinZ, newMaxZ);
      return;
    }

    if (playerZ + boundaryThreshold  >= currentArea.zMax && !currentArea.expandedNorth) {
      player.sendMessage("Expanding north");
      newMinZ = currentArea.zMax;
      newMaxZ = currentArea.zMax + expansionSize;
      currentArea.expandedNorth = true;

      MiningArea newArea = new MiningArea(newMinX, newMaxX, newMinZ, newMaxZ);
      miningAreas.add(newArea);
      newArea.expandedSouth = true;

      generateArea(player, newMinX, newMaxX, newMinZ, newMaxZ);

    } else if (playerZ - boundaryThreshold <= currentArea.zMin && !currentArea.expandedSouth) {
      player.sendMessage("Expanding south");
      newMinZ = currentArea.zMin - expansionSize;
      newMaxZ = currentArea.zMin;
      currentArea.expandedSouth = true;

      MiningArea newArea = new MiningArea(newMinX, newMaxX, newMinZ, newMaxZ);
      miningAreas.add(newArea);
      newArea.expandedNorth = true;

      generateArea(player, newMinX, newMaxX, newMinZ, newMaxZ);
    }
   }

   private void generateArea(Player player, int newMinX, int newMaxX, int newMinZ, int newMaxZ) {
     player.performCommand("/pos1 " + newMinX + ",1," + newMinZ);
     player.performCommand("/pos2 " + newMaxX + ",-60," + newMaxZ);
     player.performCommand("/regen " + randomSeed);
     player.performCommand("/cut -m !deepslate_diamond_ore,deepslate_iron_ore,deepslate_gold_ore,deepslate_redstone_ore,deepslate_lapis_ore deepslate");
     player.performCommand("remove minecarts 128");
     player.performCommand("/pos1 " + newMinX + ",-61," + newMinZ);
     player.performCommand("/pos2 " + newMaxX + ",-61," + newMaxZ);
     player.performCommand("/replace bedrock");
     player.performCommand("/replace air bedrock");
     player.sendMessage("Added new mining area");
   }
}
