package com.runescape.cache.graphics.sprite;

import com.runescape.cache.graphics.sprite.Sprite;
import com.runescape.cache.graphics.widget.Widget;
import com.runescape.util.SkillConstants;

import java.awt.*;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 12/12/2019
 */
public class SpriteCompanion {


    public static Sprite mapFlag;
    public static Sprite mapMarker;
    public static Sprite aClass30_Sub2_Sub1_Sub1_931;
    public static Sprite aClass30_Sub2_Sub1_Sub1_932;
    public static Sprite[] sideIcons;
    public static Sprite[] hitMarks;
    public static Sprite multiOverlay;
    public static Sprite scrollBar1;
    public static Sprite scrollBar2;
    public static Sprite scrollBar3;
    public static Sprite scrollBar4;
    public static Sprite[] mapFunctions;
    public static Sprite mapDotItem;
    public static Sprite mapDotNPC;
    public static Sprite mapDotPlayer;
    public static Sprite mapDotFriend;
    public static Sprite mapDotTeam;
    public static Sprite mapDotClan;
    public static Sprite[] headIcons;
    public static Sprite[] skullIcons;
    public static Sprite[] headIconsHint;
    public static Sprite compass;
    public static Sprite[] mapIcons;
    public static Sprite[] crosses;
    public static Sprite minimapImage;
    public static Sprite[] smallSkillSprites = new Sprite[SkillConstants.SKILL_COUNT];
    public static Sprite[] bigSkillSprites = new Sprite[SkillConstants.SKILL_COUNT];
    public static Sprite hp;
    public static Sprite[] cacheSprite;
    public static Sprite[] autoBackgroundSprites;
    public static Sprite[] autoBackgroundSprites2;
    public static Sprite hpBar;

    public static void setSkillSprites() {
        int[] widgetIds = {3965, 3971, 3968,
                3966, 3974, 3977,
                3980, 3976, 3982,
                3981, 3973, 3979,
                3978, 3970, 3967,
                3972, 3969, 3975,
                12165, 13925, 4151,
                18795, 18796};

        for (int i = 0; i < widgetIds.length; i++) {
            Sprite sprite = Widget.interfaceCache[widgetIds[i]].disabledSprite;
            bigSkillSprites[i] = sprite;
            smallSkillSprites[i] = new Sprite(sprite, (int) (sprite.myWidth * 0.66), (int) (sprite.myHeight * 0.66), Image.SCALE_AREA_AVERAGING);
        }
    }
}
