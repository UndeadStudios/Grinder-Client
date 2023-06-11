package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.entity.Player;
import com.runescape.cache.graphics.widget.ItemColorCustomizer.ColorfulItem;
import com.grinder.client.ClientCompanion;

public class DummyPlayerModel {

    /**
     * Equipment array cheatsheet
     *  equip[0] = hat
     * 	equip[1] = cape
     * 	equip[2] = necklace
     * 	equip[3] = weapon
     * 	equip[4] = body
     * 	equip[5] = shield
     * 	equip[6] = shoulders/arms
     * 	equip[7] = pants
     * 	equip[8] = beard
     * 	equip[9] = hands
     * 	equip[10] = shoes
     * 	equip[11] = head
     */

    public static void copyLocalPlayer() {
        Player dummy = ClientCompanion.dummyPlayer;
        Player local = Client.localPlayer;

        dummy.setGender(local.getGender());

        for (int i = 0; i < dummy.appearanceColors.length; i++) {
            dummy.appearanceColors[i] = local.appearanceColors[i];
        }

        for (int j = 0; j < dummy.equipment.length; j++) {
            dummy.equipment[j] = local.equipment[j];
        }

        for (ColorfulItem item : ColorfulItem.VALUES) {
            item.updateColorsFor(dummy, item.getColorsFor(local).clone());
        }
    }

    public static void setPlayerModel(int[] equipment, int animationId) {
        copyLocalPlayer();

        Player dummy = ClientCompanion.dummyPlayer;

        for (int j = 0; j < dummy.equipment.length; j++) {
            dummy.equipment[j] = equipment[j];
        }

        int standingAnimationId = animationId;
        dummy.idleSequence = standingAnimationId;
        dummy.setMovementSequence(standingAnimationId);
        dummy.PlayerAppearance_cachedModels.clear();
        dummy.colorNeedsUpdate = true;
    }

}
