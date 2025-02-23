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
package com.runescape.cache.definition;


public class SpriteDefinition
{
	private int id;
	private int frame;
	private int offsetX;
	private int offsetY;
	private int width;
	private int height;
	private int[] pixels;
	private int maxWidth;
	private int maxHeight;

	public transient byte[] pixelIdx;
	public transient int[] palette;

	public void normalize()
	{
		if (this.width != this.maxWidth || this.height != this.maxHeight)
		{
			byte[] var1 = new byte[this.maxWidth * this.maxHeight];
			int var2 = 0;

			for (int var3 = 0; var3 < this.height; ++var3)
			{
				for (int var4 = 0; var4 < this.width; ++var4)
				{
					var1[var4 + (var3 + this.offsetY) * this.maxWidth + this.offsetX] = this.pixelIdx[var2++];
				}
			}

			this.pixelIdx = var1;
			this.width = this.maxWidth;
			this.height = this.maxHeight;
			this.offsetX = 0;
			this.offsetY = 0;
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setPixelIdx(byte[] pixelIdx) {
		this.pixelIdx = pixelIdx;
	}

	public void setPalette(int[] palette) {
		this.palette = palette;
	}

	public int getId() {
		return id;
	}

	public int getFrame() {
		return frame;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPixels() {
		return pixels;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public byte[] getPixelIdx() {
		return pixelIdx;
	}

	public int[] getPalette() {
		return palette;
	}
}
