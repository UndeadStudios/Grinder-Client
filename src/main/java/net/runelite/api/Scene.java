package net.runelite.api;

import com.runescape.scene.object.tile.Tile;

/**
 * Represents the entire 3D scene
 */
public interface Scene
{
	/**
	 * Gets the tiles in the scene
	 *
	 * @return the tiles in [plane][x][y]
	 */
	Tile[][][] getTiles();

	int getDrawDistance();
	void setDrawDistance(int drawDistance);
}
