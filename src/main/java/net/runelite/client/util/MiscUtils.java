package net.runelite.client.util;

import java.awt.Polygon;

public class MiscUtils
{
	private static int[] abovePointsX = {2944, 3392, 3392, 2944};
	private static int[] abovePointsY = {3523, 3523, 3971, 3971};
	private static int[] belowPointsX = {2944, 2944, 3264, 3264};
	private static int[] belowPointsY = {9918, 10360, 10360, 9918};

	private static Polygon abovePoly = new Polygon(abovePointsX, abovePointsY, abovePointsX.length);
	private static Polygon belowPoly = new Polygon(belowPointsX, belowPointsY, belowPointsX.length);

	//test replacement so private for now
	public static boolean inWildy(int x, int y)
	{

		return abovePoly.contains(x, y) || belowPoly.contains(x, y);
	}
//
//	public static int getWildernessLevelFrom(Client client, WorldPoint point)
//	{
//		if (client == null)
//		{
//			return 0;
//		}
//
//		if (point == null)
//		{
//			return 0;
//		}
//
//		int x = point.getX();
//
//		if (point.getPlane() == 0 && (x < 2940 || x > 3391))
//		{
//			return 0;
//		}
//
//		int y = point.getY();
//		//v underground        //v above ground
//		int wildernessLevel = clamp(y > 6400 ? ((y - 9920) / 8) + 1 : ((y - 3520) / 8) + 1, 0, 56);
//
//		if (point.getPlane() > 0)
//		{
//			if (y < 9920)
//			{
//				wildernessLevel = 0;
//			}
//		}
//
//		return Math.max(0, wildernessLevel);
//	}

	public static int clamp(int val, int min, int max)
	{
		return Math.max(min, Math.min(max, val));
	}

	public static float clamp(float val, float min, float max)
	{
		return Math.max(min, Math.min(max, val));
	}

}
