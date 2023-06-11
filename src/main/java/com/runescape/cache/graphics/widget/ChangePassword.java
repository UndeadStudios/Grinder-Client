package com.runescape.cache.graphics.widget;

import com.runescape.Client;
import com.runescape.cache.graphics.GameFont;
import com.runescape.io.packets.outgoing.OutgoingPacket;
import com.runescape.io.packets.outgoing.impl.SendChangePasswordRequest;


/**
 * Class controlling the ChangePassword Interface and it's given functionality.
 * 
 * @author Awakening
 */

public class ChangePassword extends Widget {

	public static final int CHANGEPASSWORD_INTERFACE_ID = 51000;
	public static final int CHANGEPASSWORD_ENTER_PASSWORD_INPUT_ID = CHANGEPASSWORD_INTERFACE_ID + 6;
	public static final int CHANGEPASSWORD_NEW_PASSWORD_INPUT_ID = CHANGEPASSWORD_INTERFACE_ID + 8;
	public static final int CHANGEPASSWORD_REENTER_NEW_PASSWORD_INPUT_ID = CHANGEPASSWORD_INTERFACE_ID + 10;
	public static final int CONFIRM_BUTTON = CHANGEPASSWORD_INTERFACE_ID + 14;
	
    public static void widget(GameFont[] tda) {
    	
		int id = 51000;
		int frame = 0;

		int width = 170;
		
		Widget w = addInterface(id);
		id++;

		w.totalChildren(17);

		addSprite(id, 628);
		w.child(frame++, id, 85, 13);
		id++;

		addText(id, "@or1@Grinderscape Password Changer", tda, 2, 00000, true, true);
		w.child(frame++, id, 255, 16);
		id++;

		addText(id, "@or1@Change your account password using the inputs below", tda, 0, 00000, true, true);
		w.child(frame++, id, 256, 39);
		id++;

		addText(id,
				"Grinderscape suggests that you change your password every\\n"
						+ "so often. This keeps your account secure. You should also\\n"
						+ "consider other alternative options available for account\\n" + "security.\\n",
				tda, 0, 0xffffff, true, true);
		w.child(frame++, id, 256, 65);
		id++;

		addText(id, "@or1@Current password:", tda, 0, 00000, false, true);
		w.child(frame++, id, 128, 110);
		id++;

        addInput(id, "*ENTER CURRENT PASSWORD*", "^(\\w|\\s){0,53}$", width, 20, 0xE69138);
        w.child(frame++, id, 125, 123);
		id++;
		
		addText(id, "@or1@New password:", tda, 0, 00000, false, true);
		w.child(frame++, id, 128, 145);
		id++;

        addInput(id, "*ENTER NEW PASSWORD*", "^(\\w|\\s){0,54}$", width, 20, 0xE69138);
        w.child(frame++, id, 125, 158);
		id++;
		
		addText(id, "@or1@Repeat new password:", tda, 0, 00000, false, true);
		w.child(frame++, id, 128, 180);
		id++;
		
        addInput(id, "*RE-ENTER NEW PASSWORD*", "^(\\w|\\s){0,55}$", width, 20, 0xE69138);
        w.child(frame++, id, 125, 193);
		id++;
		
		/**
		 * Password Strength
		 */
		addText(id, "Password strength: @red@Weak", tda, 0, 0xffffff, false, true);
		w.child(frame++, id, 128, 223);
		id++;

		/**
		 * Password Length
		 */
		addText(id, "Password length: 0", tda, 0, 0xffffff, false, true);
		w.child(frame++, id, 128, 236);
		id++;
		
		addText(id, "", tda, 0, 0xFFB000, false, true);
		w.child(frame++, id, 170, 253);
		id++;
		
		/**
		 * Confirm Button
		 */
		addHoverButton(id, 621, 124, 32, "Stand", 0, id + 1, 1);
		addHoveredButton(id + 1, 622, 124, 32, id + 2);
		addText(id + 3, "Confirm", tda, 2, 0x6AA84F, true);
		
		w.child(frame++, id, 194, 278);
		w.child(frame++, id + 1, 194, 278);
		w.child(frame++, id + 3, 254, 285);
		
		id += 4;
			
		/**
		 * Close Interface (Button)
		 */
		w.child(frame++, 43702, 400, 16);
		id++;
	}	

	public static void clearInputFields(){
		Widget.interfaceCache[CHANGEPASSWORD_ENTER_PASSWORD_INPUT_ID].setDefaultText("");
		Widget.interfaceCache[CHANGEPASSWORD_NEW_PASSWORD_INPUT_ID].setDefaultText("");
		Widget.interfaceCache[CHANGEPASSWORD_REENTER_NEW_PASSWORD_INPUT_ID].setDefaultText("");
		Widget.interfaceCache[CHANGEPASSWORD_REENTER_NEW_PASSWORD_INPUT_ID].setDefaultText("");
	}

    public static void sendValidationRequestToServer() {

    	/*
    	 * Current Password
    	 */
    	final String enteredPassword = Widget.interfaceCache[CHANGEPASSWORD_ENTER_PASSWORD_INPUT_ID].getDefaultText();
    	final String newPassword = Widget.interfaceCache[CHANGEPASSWORD_NEW_PASSWORD_INPUT_ID].getDefaultText();
    	final String confirmationPassword = Widget.interfaceCache[CHANGEPASSWORD_REENTER_NEW_PASSWORD_INPUT_ID].getDefaultText();

    	/*
         * TODO: Not sure if this is what you are looking for?
         */

    	// Creates the request that must be validated by the server.
    	final OutgoingPacket request = new SendChangePasswordRequest(enteredPassword, newPassword, confirmationPassword);

    	// Creates and sends the packet detailing the request.
		Client.instance.sendPacket(request.create());
    }
    
}
