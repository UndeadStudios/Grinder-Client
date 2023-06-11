package com.runescape.entity;

import com.runescape.Client;
import com.grinder.Configuration;
import com.runescape.cache.ModelData;
import com.runescape.cache.anim.Animation;
import com.runescape.cache.anim.Graphic;
import com.runescape.cache.anim.RSFrame317;
import com.runescape.cache.def.ItemDefinition;
import com.runescape.cache.def.NpcDefinition;
import com.runescape.cache.graphics.sprite.SpriteLoader;
import com.runescape.cache.graphics.widget.ColorPicker;
import com.runescape.cache.graphics.widget.ItemColorCustomizer;
import com.runescape.cache.graphics.widget.ItemColorCustomizer.ColorfulItem;
import com.runescape.collection.EvictingDualNodeHashTable;
import com.runescape.entity.model.IdentityKit;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import com.runescape.util.StringUtils;
import com.grinder.client.ClientCompanion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Player extends Mob {

	public static EvictingDualNodeHashTable PlayerAppearance_cachedModels = new EvictingDualNodeHashTable(260);
	public final int[] appearanceColors = new int[5];
	public final int[] equipment = new int[12];
	public NpcDefinition npcDefinition;
	public boolean isUnanimated;
	public int team;
	public String name;
	public int combatLevel;
	public int headIcon;
	public int skullIcon;
	public int hintIcon;
	public int objectModelStart;
	public int objectModelStop;
	public int tileHeight;
	public boolean visible;
	public boolean isHidden;
	public int objectXPos;
	public int objectCenterHeight;
	public int objectYPos;
	public Model playerModel;
	public int objectAnInt1719LesserXLoc;
	public int objectAnInt1720LesserYLoc;
	public int objectAnInt1721GreaterXLoc;
	public int objectAnInt1722GreaterYLoc;
	public int skill;
	public String clanName = "None";
	public int rights;
	public String title="";
	public int rightsToShow;
	private long cachedModel = -1L;
	private int gender;
	private long appearanceOffset;
	public boolean colorNeedsUpdate = false;
	private Map<ColorfulItem, Integer[]> colorfulItemMap = ColorfulItem.getDefaultColorfulItemMap();

	public Map<ColorfulItem, Integer[]> getColorfulItemMap() {
		return colorfulItemMap;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Model getRotatedModel() {
		if (!visible) {
			return null;
		}

		Model var3 = getAnimatedModel();
		if (var3 == null) {
			return null;
		} else {
			var3.calculateBoundsCylinder();
			super.defaultHeight = var3.height;
			Model var5;
			Model[] var6;
			if (!isUnanimated && super.spotAnimation != -1 && super.getSpotAnimationFrame() != -1) {
				var5 = Graphic.getSpotAnimationDefinition(super.spotAnimation).getModel(spotAnimationFrame);
				if (var5 != null) {
					var5.offsetBy(0, -super.heightOffset, 0);
					var6 = new Model[]{var3, var5};
					var3 = new Model(var6, 2);
				}
			}

			if (!isUnanimated && playerModel != null) {
				if (Client.tick >= objectModelStop)
					playerModel = null;
				if (Client.tick >= objectModelStart && Client.tick < objectModelStop) {
					var5 = playerModel;
					var5.offsetBy(objectXPos - super.x, objectCenterHeight - tileHeight, objectYPos - super.y);
					if (super.orientation == 512) {
						var5.rotate90Degrees();
						var5.rotate90Degrees();
						var5.rotate90Degrees();
					} else if (super.orientation == 1024) {
						var5.rotate90Degrees();
						var5.rotate90Degrees();
					} else if (super.orientation == 1536)
						var5.rotate90Degrees();

					var6 = new Model[]{ var3, var5 };
					var3 = new Model(var6, 2);
					if (super.orientation == 512)
						var5.rotate90Degrees();
					else if (super.orientation == 1024) {
						var5.rotate90Degrees();
						var5.rotate90Degrees();
					} else if (super.orientation == 1536) {
						var5.rotate90Degrees();
						var5.rotate90Degrees();
						var5.rotate90Degrees();
					}
					var5.offsetBy(super.x - objectXPos, tileHeight - objectCenterHeight, super.y - objectYPos);
				}
			}
			var3.isSingleTile = true;
			return var3;
		}
	}
	/**
	 * Colorful Item update reading.
	 *
	 * @param buffer the {@link Buffer} of the player update packet.
	 */
	public void readColorfulItemUpdate(Buffer buffer){
		for (ColorfulItem item : ColorfulItem.VALUES) {
			for (int i = 0; i < item.getColorsFor(this).length; i++) {
				item.getColorsFor(this)[i] = buffer.readInt();
				ItemDefinition.sprites.remove(item.getCopyId());
				ItemDefinition.sprites.remove(item.getItemId());
			}
		}
	}

	public void readAppearanceUpdate(Buffer buffer) {
		buffer.index = 0;

		skullIcon = buffer.readUnsignedByte();
		hintIcon = buffer.readUnsignedByte();
		headIcon = buffer.readUnsignedByte();
		gender = buffer.readUnsignedByte();
		isHidden = buffer.getUnsignedByte() == 1;

		final boolean updateColorfulItems = buffer.readUnsignedByte() == 1;

		if(updateColorfulItems){
			readColorfulItemUpdate(buffer);
		}

		npcDefinition = null;
		team = 0;
		size = 1;

		for (int bodyPart = 0; bodyPart < 12; bodyPart++) {

			int reset = buffer.readUnsignedByte();

			if (reset == 0) {
				equipment[bodyPart] = 0;
				continue;
			}

			int id = buffer.readUnsignedByte();

			equipment[bodyPart] = (reset << 8) + id;

			if (bodyPart == 0 && equipment[0] == 65535) {
				npcDefinition = NpcDefinition.lookup(buffer.readUShort());
//				if(npcDefinition != null)
//					size = npcDefinition.size;
				break;
			}

			if (equipment[bodyPart] >= 512 && equipment[bodyPart] - 512 < ItemDefinition.getTotalItems()) {
				int team = ItemDefinition.lookup(equipment[bodyPart] - 512).team;

				if (team != 0) {
					this.team = team;
				}

			}

		}

		for (int part = 0; part < 5; part++) {
			int color = buffer.readUnsignedByte();
			if (color < 0 || color >= ClientCompanion.PLAYER_BODY_RECOLOURS[part].length) {
				color = 0;
			}
			appearanceColors[part] = color;
		}

		super.idleSequence = buffer.readUShort();
		if (super.idleSequence == 65535) {
			super.idleSequence = -1;
		}

		super.turnLeftSequence = buffer.readUShort();
		if (super.turnLeftSequence == 65535) {
			super.turnLeftSequence = -1;
		}
		super.turnRightSequence = super.turnLeftSequence;

		super.walkSequence = buffer.readUShort();
		if (super.walkSequence == 65535) {
			super.walkSequence = -1;
		}

		super.walkTurnSequence = buffer.readUShort();
		if (super.walkTurnSequence == 65535) {
			super.walkTurnSequence = -1;
		}

		super.walkTurnLeftSequence = buffer.readUShort();
		if (super.walkTurnLeftSequence == 65535) {
			super.walkTurnLeftSequence = -1;
		}

		super.walkTurnRightSequence = buffer.readUShort();
		if (super.walkTurnRightSequence == 65535) {
			super.walkTurnRightSequence = -1;
		}

		super.runSequence = buffer.readUShort();
		if (super.runSequence == 65535) {
			super.runSequence = -1;
		}

		name = StringUtils.formatText(StringUtils.decodeBase37(buffer.readLong()));
		combatLevel = buffer.readUnsignedByte();
		rights = buffer.readInt();
		rightsToShow = buffer.readUnsignedByte();
		title = buffer.readString();

		// skill = buffer.readUShort();
		visible = true;

		updateHash();
	}

	public int getPrimaryRights() {
		return (rights >> 24) & 0xFF;
	}

	public int getDonatorRights() {
		return (rights >> 16) & 0xFF;
	}

	public int getGameMode() {
		return rights & 0xFF;
	}

	public String getImages() {
		return getImages(rights, rightsToShow);
	}

	public static String getImages(int hash, int rightsToShow) {
		int primary = (hash >> 24) & 0xFF;
		int donator = (hash >> 16) & 0xFF;
		int gameMode = hash & 0xFF;

		final StringBuilder builder = new StringBuilder();

		if (rightsToShow > 0 || primary > 0) {
			final Rights rights = Rights.get(rightsToShow > 0 ? rightsToShow : primary);

			if (rights != null) {
				builder.append(rights.getImage()).append(" ");
			}
		}

		final GameMode mode = GameMode.get(gameMode);

		if (mode != null && !mode.getImage().isEmpty()) {
			builder.append(mode.getImage()).append(" ");
		}

		if (donator > 0) {
			builder.append("<img=" + Rights.MEMBER.get(donator - 1).getSpriteId()).append("> ");
		}

		return builder.toString();
	}

	private void updateHash() {
		appearanceOffset = -1L;

		long[] crcTable = Buffer.CRC_64;

		for (int index = 0; index < 12; index++) {
			int id = equipment[index];

			appearanceOffset = (appearanceOffset >>> 8 ^ crcTable[(int) ((appearanceOffset ^ id >> 8) & 0xffL)]);
			appearanceOffset = (appearanceOffset >>> 8 ^ crcTable[(int) ((appearanceOffset ^ id) & 0xffL)]);

//			if (colorNeedsUpdate) {
//				colorNeedsUpdate = false;
				if (id >= 512 && id - 512 < ItemDefinition.getTotalItems()) {
					ColorfulItem item = ColorfulItem.forId(id - 512);
					if (item != null) {
						for (int i = 0; i < item.getColorsFor(this).length; i++) {
							int color = item.getColorsFor(this)[i];

							appearanceOffset = (appearanceOffset >>> 8 ^ crcTable[(int) ((appearanceOffset ^ color >> 8) & 0xffL)]);
							appearanceOffset = (appearanceOffset >>> 8 ^ crcTable[(int) ((appearanceOffset ^ color) & 0xffL)]);
						}
					}
				}
//			}
		}

		for (int index = 0; index < 5; index++) {
			appearanceOffset = (appearanceOffset >>> 8 ^ crcTable[(int) ((appearanceOffset ^ appearanceColors[index]) & 0xffL)]);
		}

		appearanceOffset = (appearanceOffset >>> 8 ^ crcTable[(int) ((appearanceOffset ^ gender) & 0xffL)]);
	}

	public Model getAnimatedModel() {
		Animation var1 = super.sequence != -1 && super.sequenceDelay == 0? Animation.getSequenceDefinition(super.sequence):null;
		Animation var2 = super.movementSequence != -1 && (super.movementSequence != super.idleSequence || var1 == null)? Animation.getSequenceDefinition(super.movementSequence):null;
		return getModel(var1, super.sequenceFrame, var2, super.movementFrame);
	}

	public Model getModel(Animation var1, int var2, Animation var3, int var4) {
		if (npcDefinition != null) {
			return npcDefinition.getModel(var1, var2, var3, var4);
		} else {
			long var5 = appearanceOffset;
			int[] var7 = this.equipment;
			if (var1 != null && (var1.shield >= 0 || var1.weapon >= 0)) {
				var7 = new int[12];

				for(int var8 = 0; var8 < 12; ++var8) {
					var7[var8] = this.equipment[var8];
				}

				if (var1.shield >= 0) {
					var5 += (long)(var1.shield - this.equipment[5] << 40);
					var7[5] = var1.shield;
				}

				if (var1.weapon >= 0) {
					var5 += (long)(var1.weapon - this.equipment[3] << 48);
					var7[3] = var1.weapon;
				}
			}

			Model var18 = (Model)PlayerAppearance_cachedModels.get(var5);
			if (var18 == null) {
				boolean var9 = false;

				int var11;
				for(int var10 = 0; var10 < 12; ++var10) {
					var11 = var7[var10];
					if (var11 >= 256 && var11 < 512 && !IdentityKit.KitDefinition_get(var11 - 256).ready()) {
						var9 = true;
					}

					if (var11 >= 512 && !ItemDefinition.lookup(var11 - 512).method3918(this.gender)) {
						var9 = true;
					}
				}

				if (var9) {
					if (this.cachedModel != -1L) {
						var18 = (Model)PlayerAppearance_cachedModels.get(this.cachedModel);
					}

					if (var18 == null) {
						return null;
					}
				}

				if (var18 == null) {
					ModelData[] var19 = new ModelData[12];
					var11 = 0;

					int var13;
					for(int var12 = 0; var12 < 12; ++var12) {
						var13 = var7[var12];
						if (var13 >= 256 && var13 < 512) {
							ModelData var14 = IdentityKit.KitDefinition_get(var13 - 256).getModelData();
							if (var14 != null) {
								var19[var11++] = var14;
							}
						}

						if (var13 >= 512) {
							ItemDefinition var22 = ItemDefinition.lookup(var13 - 512);
							ModelData var15 = var22.method3919(this.gender);
							if (var15 != null) {
								var15 = ItemColorCustomizer.getColorfulItemData(var15, this, var13 - 512, gender);
								/*if (this.playerCompositionColorTextureOverrides != null) {
									PlayerCompositionColorTextureOverride var16 = this.playerCompositionColorTextureOverrides[var12];
									if (var16 != null) {
										int var17;
										if (var16.playerCompositionRecolorTo != null && var22.recolorFrom != null && var16.playerCompositionRecolorTo.length == var22.recolorTo.length) {
											for(var17 = 0; var17 < var22.recolorFrom.length; ++var17) {
												var15.recolor(var22.recolorTo[var17], var16.playerCompositionRecolorTo[var17]);
											}
										}

										if (var16.playerCompositionRetextureTo != null && var22.retextureFrom != null && var22.retextureTo.length == var16.playerCompositionRetextureTo.length) {
											for(var17 = 0; var17 < var22.retextureFrom.length; ++var17) {
												var15.retexture(var22.retextureTo[var17], var16.playerCompositionRetextureTo[var17]);
											}
										}
									}
								}*/

								var19[var11++] = var15;
							}
						}
					}

					ModelData var20 = new ModelData(var19, var11);

					for (var13 = 0; var13 < 5; var13++)
						if (appearanceColors[var13] != 0) {
							var20.recolor((short) ClientCompanion.PLAYER_BODY_RECOLOURS[var13][0],
									(short) ClientCompanion.PLAYER_BODY_RECOLOURS[var13][appearanceColors[var13]]);
							if (var13 == 1)
								var20.recolor((short) ClientCompanion.anIntArray1204[0], (short) ClientCompanion.anIntArray1204[appearanceColors[var13]]);
						}

					var18 = var20.toModel(64, 850, -30, -50, -30);
					PlayerAppearance_cachedModels.put(var18, var5);
					this.cachedModel = var5;
				}
			}

			Model var21;
			if (var1 == null && var3 == null) {
				var21 = var18.toSharedSequenceModel(true);
			} else if (var1 != null && var3 != null) {
				var21 = var1.applyTransformations(var18, var2, var3, var4);
			} else if (var1 != null) {
				var21 = var1.transformActorModel(var18, var2);
			} else {
				var21 = var3.transformActorModel(var18, var4);
			}

			return var21;
		}
	}

	public ModelData getModelData() {
		if (npcDefinition != null) {
			return npcDefinition.method3670();
		} else {
			boolean var1 = false;

			int var3;
			for(int var2 = 0; var2 < 12; ++var2) {
				var3 = this.equipment[var2];
				if (var3 >= 256 && var3 < 512 && !IdentityKit.KitDefinition_get(var3 - 256).headLoaded()) {
					var1 = true;
				}

				if (var3 >= 512 && !ItemDefinition.lookup(var3 - 512).isDialogueModelCached(this.gender)) {
					var1 = true;
				}
			}

			if (var1) {
				return null;
			} else {
				ModelData[] var7 = new ModelData[12];
				var3 = 0;

				int var5;
				for(int var4 = 0; var4 < 12; ++var4) {
					var5 = this.equipment[var4];
					ModelData var6;
					if (var5 >= 256 && var5 < 512) {
						var6 = IdentityKit.KitDefinition_get(var5 - 256).getKitDefinitionModels();
						if (var6 != null) {
							var7[var3++] = var6;
						}
					}

					if (var5 >= 512) {
						var6 = ItemDefinition.lookup(var5 - 512).method3921(this.gender);
						if (var6 != null) {
							var7[var3++] = var6;
						}
					}
				}

				ModelData var8 = new ModelData(var7, var3);
				for (var5 = 0; var5 < 5; var5++) {
					if (appearanceColors[var5] != 0) {
						var8.recolor((short) ClientCompanion.PLAYER_BODY_RECOLOURS[var5][0],
								(short) ClientCompanion.PLAYER_BODY_RECOLOURS[var5][appearanceColors[var5]]);
						if (var5 == 1) {
							var8.recolor((short) ClientCompanion.anIntArray1204[0], (short) ClientCompanion.anIntArray1204[appearanceColors[var5]]);
						}
					}
				}
				return var8;
			}
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public String getNameWithTitle() {
		if (title == null || title.isEmpty())
			return name;
		if (title.startsWith("@su"))
			return name + " " + title.substring(3);
		return title + " " + name;
	}

	public enum Rights {
		NONE(-1),
		SERVER_SUPPORTER(763),
		MODERATOR(740),
		GLOBAL_MODERATOR(741),
		ADMINISTRATOR(742),
		CO_OWNER(788),
		OWNER(743),
		DEVELOPER(744),


		BRONZE_MEMBER(1025),
		RUBY_MEMBER(745),
		TOPAZ_MEMBER(746),
		AMETHYST_MEMBER(747),
		LEGENDARY_MEMBER(1026),
		PLATINUM_MEMBER(1027),
		TITANIUM_MEMBER(1227),
		DIAMOND_MEMBER(1228),
		DICER(770),

		YOUTUBER(748),
		WIKI_EDITOR(796),
		DESIGNER(751),
		MIDDLEMAN(939),
		EVENT_HOST(940),
		VETERAN(941),
		EX_STAFF(942),
		RESPECTED(943),
		CAMPAIGN_DEVELOPER(1028),
		CONTRIBUTOR(1229),
		MOTM(1241),
		;

		private int spriteId;

		public int getSpriteId() {
			return spriteId;
		}

		public String formattedName() {
			if (name().equals("MOTM")) {
				return "MOTM";
			}
			return StringUtils.capitalizeEachWord(name().replace("_", " "));
		}

		public String getImage() {
			return "<img=" + spriteId + ">";
		}

		Rights(int spriteId) {
			this.spriteId = spriteId;
		}

		public static List<Rights> MEMBER = new ArrayList<>(Arrays.asList(
				DICER, BRONZE_MEMBER, RUBY_MEMBER, TOPAZ_MEMBER, AMETHYST_MEMBER, LEGENDARY_MEMBER, PLATINUM_MEMBER, TITANIUM_MEMBER, DIAMOND_MEMBER
		));

		public boolean isMember() {
			return MEMBER.contains(this);
		}

		public static void loadModIcons() {
			/**
			 * Rights crowns
			 */
			for (Rights rights : values()) {
				if (rights == NONE) continue;

				Client.instance.getModIcons()[rights.ordinal() - 1] = SpriteLoader.getSprite(rights.getSpriteId());
			}

			/**
			 * Game Mode crowns
			 */
//			Client.instance.getModIcons()[29] = SpriteLoader.getSprite(77);  // Classic
//			Client.instance.getModIcons()[30] = SpriteLoader.getSprite(807); // Ironman
//			Client.instance.getModIcons()[31] = SpriteLoader.getSprite(78);  // HCIM
//			Client.instance.getModIcons()[32] = SpriteLoader.getSprite(808); // UIM

		}

		public static Rights get(int id) {
			if (id < 0 || id >= values().length) {
				return null;
			}

			return values()[id];
		}

	}

	public enum GameMode {
		ONE_LIFE("<img=1235>"),
		REALISM("<img=1236>"),
		CLASSIC("<img=77>"),
		NORMAL(""),
		PURE("<img=1238>"),
		MASTER("<img=1237>"),
		SPAWN("<img=1242>"),
		IRONMAN("<img=807>"),
		HARDCORE("<img=78>"),
		ULTIMATE("<img=808>");

		private final String image;

		GameMode(String image) {
			this.image = image;
		}

		public String getImage() {
			return image;
		}

		public static GameMode get(int id) {
			if (id < 0 || id >= values().length) {
				return null;
			}

			return values()[id];
		}
	}

	public boolean isDeveloper(){
		return getPrimaryRights() == 7 || getPrimaryRights() == 6;
	}
}
