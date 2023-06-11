/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
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
package net.runelite.client.config;


import java.util.Objects;

public class ConfigItemDescriptor
{
	private final ConfigItem item;
	private final Class<?> type;
	private final Range range;
	private final Alpha alpha;

	ConfigItemDescriptor(ConfigItem item, Class<?> type, Range range, Alpha alpha) {
		this.item = item;
		this.type = type;
		this.range = range;
		this.alpha = alpha;
	}

	public ConfigItem getItem() {
		return item;
	}

	public Class<?> getType() {
		return type;
	}

	public Range getRange() {
		return range;
	}

	public Alpha getAlpha() {
		return alpha;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ConfigItemDescriptor)) return false;

		ConfigItemDescriptor that = (ConfigItemDescriptor) o;

		if (!Objects.equals(item, that.item)) return false;
		if (!Objects.equals(type, that.type)) return false;
		if (!Objects.equals(range, that.range)) return false;
		return Objects.equals(alpha, that.alpha);
	}

	@Override
	public int hashCode() {
		int result = item != null ? item.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (range != null ? range.hashCode() : 0);
		result = 31 * result + (alpha != null ? alpha.hashCode() : 0);
		return result;
	}
}
