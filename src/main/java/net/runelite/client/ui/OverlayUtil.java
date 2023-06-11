/*
 * Copyright (c) 2016-2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.ui;

import com.google.common.base.Strings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import net.runelite.api.Point;


/**
 * Created by Kyle Fricilone on Jun 09, 2017.
 */
public class OverlayUtil
{
	private static final int MINIMAP_DOT_RADIUS = 4;
	private static final double UNIT = Math.PI / 1024.0d;

	public static void renderPolygon(Graphics2D graphics, Polygon poly, Color color)
	{
		graphics.setColor(color);
		final Stroke originalStroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(2));
		graphics.drawPolygon(poly);
		graphics.setColor(new Color(0, 0, 0, 50));
		graphics.fillPolygon(poly);
		graphics.setStroke(originalStroke);
	}

	public static void renderMinimapLocation(Graphics2D graphics, Point mini, Color color)
	{
		graphics.setColor(Color.BLACK);
		graphics.fillOval(mini.getX() - MINIMAP_DOT_RADIUS / 2, mini.getY() - MINIMAP_DOT_RADIUS / 2 + 1, MINIMAP_DOT_RADIUS, MINIMAP_DOT_RADIUS);
		graphics.setColor(color);
		graphics.fillOval(mini.getX() - MINIMAP_DOT_RADIUS / 2, mini.getY() - MINIMAP_DOT_RADIUS / 2, MINIMAP_DOT_RADIUS, MINIMAP_DOT_RADIUS);
	}


	public static void renderTextLocation(Graphics2D graphics, Point txtLoc, String text, Color color)
	{
		if (Strings.isNullOrEmpty(text))
		{
			return;
		}

		int x = txtLoc.getX();
		int y = txtLoc.getY();

		graphics.setColor(Color.BLACK);
		graphics.drawString(text, x + 1, y + 1);

		graphics.setColor(color);
		graphics.drawString(text, x, y);
	}


}
