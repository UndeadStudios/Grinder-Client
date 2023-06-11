package com.runescape.input;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.model.BroadCastManager;
import com.grinder.model.ChatBox;
import com.grinder.model.Console;
import com.grinder.model.Emojis;
import com.runescape.Client;
import com.runescape.cache.graphics.widget.*;
import com.runescape.cache.graphics.widget.custom.CustomWidgetLoader;
import com.runescape.entity.PlayerRelations;
import com.runescape.io.packets.outgoing.impl.*;
import com.runescape.util.StringUtils;

import java.awt.event.KeyEvent;
import java.util.Objects;

public class KeyParser {

    public static void manageTextInputs(Client client) {
        do {
            int key = KeyHandler.readChar();

            if (key == -1)
                break;

            if ((key == 96 || key == 167) && client.clientRights >= 2 && client.clientRights <= 7) {
                Console.consoleOpen = !Console.consoleOpen;
            } else if (Console.consoleOpen) {
                if (key == 8 && ClientCompanion.console.consoleInput.length() > 0)
                    ClientCompanion.console.consoleInput = ClientCompanion.console.consoleInput.substring(0, ClientCompanion.console.consoleInput.length() - 1);
                if (key == 9) { // Tab
                    if (ClientCompanion.console.previousMessage != null) {
                        ClientCompanion.console.consoleInput += ClientCompanion.console.previousMessage;
                    } else {
                        ClientCompanion.console.printMessage("No previous command entered.", 1);
                    }
                }
                if (key >= 32 && key <= 122 && ClientCompanion.console.consoleInput.length() < 80)
                    ClientCompanion.console.consoleInput += (char) key;

                if ((key == 13 || key == 10) && ClientCompanion.console.consoleInput.length() >= 1) {


                    ClientCompanion.console.printMessage(ClientCompanion.console.consoleInput, 0);
                    if (ClientCompanion.console.consoleInput.startsWith("change-password-request")) {
                        client.sendPacket(new SendChangePasswordRequest("test1", "test2", "test3").create());
                    } else
                        ClientCompanion.console.sendCommandPacket(ClientCompanion.console.consoleInput);
                    ClientCompanion.console.consoleInput = "";
                    ChatBox.setUpdateChatbox(true);
                }

            } else if (ClientCompanion.interfaceInputSelected != -1) {

                final Widget input = Widget.interfaceCache[ClientCompanion.interfaceInputSelected];

                if (ClientCompanion.openInterfaceId == Broadcast.INTERFACE_ID) {
                    BroadCastManager.handleBroadcastInputSelection(key);
                }

                if (ClientCompanion.openInterfaceId == 51000) {
                    /*
                      TAB-Key Change Input box focus
                     */
                    if (key == 9) {
                        if (ClientCompanion.interfaceInputSelected == 51006) {
                            ClientCompanion.interfaceInputSelected = 51008;
                        } else if (ClientCompanion.interfaceInputSelected == 51008) {
                            ClientCompanion.interfaceInputSelected = 51010;
                        }
                    }
                    /*
                     * TAB-Key Change Input box focus (reset)
                     */
                    if (KeyHandler.pressedKeys[81] && key == 9 && ClientCompanion.interfaceInputSelected == 51010) {
                        ClientCompanion.interfaceInputSelected = 51006;
                    }
                    /*
                     * Handling Change Password Interface Changes
                     */
                    // A-Z keys
                    if (key >= 32 && key <= 122 && input.getDefaultText().length() < 14) {

                        if (input.pattern == null || input.pattern.matcher(input.getDefaultText() + (char) key).matches()) {
                            onInputPatternMatch(input, (char) key);
                        }
                        if (ClientCompanion.interfaceInputSelected == ChangePassword.CHANGEPASSWORD_NEW_PASSWORD_INPUT_ID) {
                            if (!Objects.equals(input.getDefaultText(), input.getDefaultText().toLowerCase())) {
                                client.sendString("Note: Passwords are case SenSiTiVe!", 51013);
                            } else if (Objects.equals(input.getDefaultText(), input.getDefaultText().toLowerCase())) {
                                client.sendString("", 51013);
                            }
                            if (input.getDefaultText().length() <= 4) {
                                client.sendString("Password strengh: @red@Weak", 51011);
                            }
                            if (input.getDefaultText().length() >= 5 && input.getDefaultText().length() <= 6) {
                                client.sendString("Password strengh: @or2@Fair", 51011);
                            } else if (input.getDefaultText().length() >= 7 && input.getDefaultText().length() <= 8) {
                                client.sendString("Password strengh: @yel@Good", 51011);
                            } else if (input.getDefaultText().length() >= 9 && input.getDefaultText().length() <= 14) {
                                client.sendString("Password strengh: @gre@Excellent", 51011);
                            }
                            client.sendString("Password length: " + input.getDefaultText().length(), 51012);
                        }
                    } else if (key == KeyEvent.VK_BACK_SPACE) {
                        if (input.getDefaultText().length() > 0) {
                            input.setDefaultText(input.getDefaultText().substring(0, input.getDefaultText().length() - 1));
                            if (ClientCompanion.interfaceInputSelected == ChangePassword.CHANGEPASSWORD_NEW_PASSWORD_INPUT_ID) {
                                if (!Objects.equals(input.getDefaultText(), input.getDefaultText().toLowerCase())) {
                                    client.sendString("Note: Passwords are case SenSiTiVe!", 51013);
                                } else if (Objects.equals(input.getDefaultText(), input.getDefaultText().toLowerCase())) {
                                    client.sendString("", 51013);
                                }
                                if (input.getDefaultText().length() <= 4) {
                                    client.sendString("Password strengh: @red@Weak", 51011);
                                }
                                if (input.getDefaultText().length() >= 5 && input.getDefaultText().length() <= 6) {
                                    client.sendString("Password strengh: @or2@Fair", 51011);
                                } else if (input.getDefaultText().length() >= 7 && input.getDefaultText().length() <= 8) {
                                    client.sendString("Password strengh: @yel@Good", 51011);
                                } else if (input.getDefaultText().length() >= 9 && input.getDefaultText().length() <= 14) {
                                    client.sendString("Password strengh: @gre@Excellent", 51011);
                                }
                                client.sendString("Password length: " + input.getDefaultText().length(), 51012);
                            }
                        }
                        // Remove the ability for players to do crowns..
                        if (input.getDefaultText().toLowerCase().contains("<img")) {
                            input.setDefaultText(input.getDefaultText().replaceAll(
                                    "<img", ""));
                        } else if (input.getDefaultText().toLowerCase().contains("<clan")) {
                            input.setDefaultText(input.getDefaultText().replaceAll(
                                    "<clan", ""));
                        } else {
                            onInputPatternNoMatch(client, input);
                        }
                    }

                } else {

                    String temp = input.defaultText;

                    if (key >= 32 && key <= 122 && input.defaultText.length() < 120) {
                        if (input.pattern == null || input.pattern.matcher(input.defaultText + (char) key).matches())
                            onInputPatternMatch(input, (char) key);
                        else
                            onInputPatternNoMatch(client, input);
                    }

                    if (key == 8 && input.defaultText.length() > 0) {
                        input.defaultText = input.defaultText.substring(0, input.defaultText.length() - 1);
                    }

                    if (input.autoUpdate) {
                        /*
                         * Inputs that update on key press
                         */
                        if (!temp.equals(input.getDefaultText())) {
                            if (input.id == ColorPicker.INPUT_ID) {
                                /*
                                 * Color picked is handled fully client side
                                 */
                                ColorPicker.updatePicker();
                            } else {
                                /*
                                 * Queue the request for SendInput packet
                                 */
                                SendInput.lastSentId = input.id;
                                SendInput.lastSentTimeStamp = System.currentTimeMillis();
                            }
                        }
                    } else {
                        /*
                         * Inputs that update on pressing enter key
                         */
                        if (key == 13 || key == 10) {
                            if (input.defaultText.length() > 0) {
                                client.sendPacket(new SendInput(input.defaultText, input.id).create());
                            }
                        }
                    }
                    if (input.id == ColorPicker.INPUT_ID && key == KeyEvent.VK_ENTER) {
                        ColorPicker.confirmSelection();
                    }
                }

            } else if (ClientCompanion.openInterfaceId != -1 && ClientCompanion.openInterfaceId == client.reportAbuseInterfaceID) {
                if (key == 8 && client.reportAbuseInput.length() > 0)
                    client.reportAbuseInput = client.reportAbuseInput.substring(0, client.reportAbuseInput.length() - 1);
                if ((key >= 97 && key <= 122 || key >= 65 && key <= 90 || key >= 48 && key <= 57 || key == 32)
                        && client.reportAbuseInput.length() < 12)
                    client.reportAbuseInput += (char) key;
            } else if (client.messagePromptRaised) {
                if (key >= 32 && key <= 122 && client.promptInput.length() < 120) {
                    client.promptInput += (char) key;
                    ChatBox.setUpdateChatbox(true);
                }
                if (key == 8 && client.promptInput.length() > 0) {
                    client.promptInput = client.promptInput.substring(0, client.promptInput.length() - 1);
                    ChatBox.setUpdateChatbox(true);
                }
                if (key == 13 || key == 10) {
                    client.messagePromptRaised = false;
                    ChatBox.setUpdateChatbox(true);
                    if (client.friendsListAction == 1) {
                        long l = StringUtils.encodeBase37(client.promptInput);
                        client.addFriend(l);
                    }
                    if (client.friendsListAction == 2 && PlayerRelations.friendsCount > 0) {
                        long l1 = StringUtils.encodeBase37(client.promptInput);
                        client.removeFriend(l1);
                    }
                    if (client.friendsListAction == 3 && client.promptInput.length() > 0) {
                        client.promptInput = StringUtils.capitalizeFirst(client.promptInput);
                        // Remove the ability for players to do crowns and colored text in private message..
                        String[] badCharacterCombinations =
                                {"@cr", "<img", "<clan", "@red@", "@whi@", "@gre@", "@blu@",
                                        "@cya@", "@or1@", "@or2@", "@or3@", "@gr3@", "@gr2@",
                                        "@gr1@", "@lre@", "@dre@", "@yel@"};
                        for (String text : badCharacterCombinations) {
                            if (client.promptInput.contains(text)) {
                                client.promptInput = client.promptInput.replaceAll(text, "");
                            }
                        }
                        client.sendPacket(new PrivateChatMessage(client.encodedPrivateMessageRecipientName, client.promptInput).create());
//                        promptInput = TextInput.processText(promptInput);
                        client.sendMessage(client.promptInput, ChatBox.CHAT_TYPE_TO_PRIVATE, StringUtils.formatText(client.privateMessageRecipientName));
                        if (ChatBox.privateChatMode == 2) {
                            ChatBox.privateChatMode = 1;
                            // privacy option
                            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
                        }
                    }
                    if (client.friendsListAction == 4 && PlayerRelations.ignoreCount < 100) {
                        long l2 = StringUtils.encodeBase37(client.promptInput);
                        client.addIgnore(l2);
                    }
                    if (client.friendsListAction == 5 && PlayerRelations.ignoreCount > 0) {
                        long l3 = StringUtils.encodeBase37(client.promptInput);
                        client.removeIgnore(l3);
                    }
                    if (client.friendsListAction == 6) {
                        long l3 = StringUtils.encodeBase37(client.promptInput);
                        // chatJoin(l3);
                    }
                }
            } else if (client.inputDialogState == 1) {
                if (key >= 48 && key <= 57 && client.amountOrNameInput.length() < 25) {
                    client.amountOrNameInput += (char) key;
                    ChatBox.setUpdateChatbox(true);
                }
                if ((!client.amountOrNameInput.toLowerCase().contains("k") && !client.amountOrNameInput.toLowerCase().contains("m")
                        && !client.amountOrNameInput.toLowerCase().contains("b")) && (key == 107 || key == 109) || key == 98) {
                    client.amountOrNameInput += (char) key;
                    ChatBox.setUpdateChatbox(true);
                }
                if (key == 8 && client.amountOrNameInput.length() > 0) {
                    client.amountOrNameInput = client.amountOrNameInput.substring(0, client.amountOrNameInput.length() - 1);
                    ChatBox.setUpdateChatbox(true);
                }
                if (key == 13 || key == 10) {
                    if (client.amountOrNameInput.length() > 0) {
                        int length = client.amountOrNameInput.length();
                        char lastChar = client.amountOrNameInput.charAt(length - 1);

                        if (lastChar == 'k') {
                            client.amountOrNameInput = client.amountOrNameInput.substring(0, length - 1) + "000";
                        } else if (lastChar == 'm') {
                            client.amountOrNameInput = client.amountOrNameInput.substring(0, length - 1) + "000000";
                        } else if (lastChar == 'b') {
                            client.amountOrNameInput = client.amountOrNameInput.substring(0, length - 1) + "000000000";
                        }

                        long amount = 0;

                        try {
                            amount = Long.parseLong(client.amountOrNameInput);

                            // overflow concious code
                            if (amount < 0) {
                                amount = 0;
                            } else if (amount > Integer.MAX_VALUE) {
                                amount = Integer.MAX_VALUE;
                            }
                        } catch (Exception ignored) {
                        }

                        if (amount > 0 || ClientCompanion.openInterfaceId == Bank.INTERFACE_ID) { // Can enter <= 0 if banking
                            client.sendPacket(new EnterAmount((int) amount).create());
                        }
                    }
                    client.inputDialogState = 0;
                    ChatBox.setUpdateChatbox(true);
                }
            } else if (client.inputDialogState == 2) {
                if (key >= 32 && key <= 122 && client.amountOrNameInput.length() < 25) {
                    client.amountOrNameInput += (char) key;
                    ChatBox.setUpdateChatbox(true);
                    client.onInputKeyPress();
                }
                if (key == 8 && client.amountOrNameInput.length() > 0) {
                    client.amountOrNameInput = client.amountOrNameInput.substring(0, client.amountOrNameInput.length() - 1);
                    ChatBox.setUpdateChatbox(true);
                    client.onInputKeyPress();
                }
                if (key == 13 || key == 10) {
                    if (client.amountOrNameInput.length() > 0) {
                        client.sendPacket(new SendSyntax(client.amountOrNameInput).create());
                    }
                    client.inputDialogState = 0;
                    ChatBox.setUpdateChatbox(true);
                }
            } else if (client.backDialogueId == -1) {
                if (key >= 32 && key <= 122 && client.inputString.length() < 120) {
                    client.inputString += (char) key;
                    ChatBox.setUpdateChatbox(true);
                }
                if (key == 8 && client.inputString.length() > 0) {
                    String endingWordEmoji = Emojis.Emoji.getEndingWordEmoji(client.inputString);
                    if (Configuration.enableEmoticons && endingWordEmoji != null) {
                        // If the last word typed is an emoji, remove it all in one backspace press
                        client.inputString = client.inputString.substring(0, client.inputString.length() - endingWordEmoji.length());
                    } else {
                        client.inputString = client.inputString.substring(0, client.inputString.length() - 1);
                    }
                    ChatBox.setUpdateChatbox(true);
                }
                if (key == 9) {
                    tabToReplyPm(client);
                }

                // Remove the ability for players to do crowns and colored text in chatbox..
                String[] badCharacterCombinations =
                        {"@cr", "<img", "<clan", "@red@", "@whi@", "@gre@", "@blu@",
                                "@cya@", "@or1@", "@or2@", "@or3@", "@gr3@", "@gr2@",
                                "@gr1@", "@lre@", "@dre@", "@yel@"};
                for (String text : badCharacterCombinations) {
                    if(client.inputString.contains(text)) {
                        client.inputString = client.inputString.replaceAll(text, "");
                    }
                }

                if ((key == 13 || key == 10) && client.inputString.length() > 0) {

                    // Temp command to remove snow in-client until next restart.
/*                    if (client.inputString.toLowerCase().equals("::offsnow")) {
                        Snow.disabledSnow = true;
                        Client.instance.sendMessage("@red@Snowfall system has been temporarily disabled.");
                    }*/


                    if (client.inputString.startsWith("/")) {
                        client.inputString = "::" + client.inputString;
                    }
                    if (client.inputString.startsWith("::rsi")) {
                        CustomWidgetLoader.init();
                    }
                    if (client.inputString.startsWith("::findrsi")) {
                        String[] data = Client.instance.inputString.split(" ");
                        int id = Integer.parseInt(data[1]);

                        int amount = 0;
                        int mainId = 0;

                        for (int i = 22_000; i < Widget.interfaceCache.length; i++) {
                            if (Widget.interfaceCache[i] == null) {
                                amount++;
                                if (mainId == -1) {
                                    mainId = i;
                                }
                            } else {
                                amount = 0;
                                mainId = -1;
                            }

                            if (amount == id) {
                                break;
                            }
                        }
                        String output = "mainId: " + mainId + " " + amount;
                        Client.instance.sendMessage(output, 0, "");
                        System.out.println(output);
                    }
                    if (client.inputString.startsWith("::")) {
                        client.sendPacket(new Command(client.inputString.substring(2)).create());
                    } else {
                        String text = client.inputString.toLowerCase();
                        int colorCode = 0;
                        if (text.startsWith("yellow:")) {
                            client.inputString = client.inputString.substring(7);
                        } else if (text.startsWith("red:")) {
                            colorCode = 1;
                            client.inputString = client.inputString.substring(4);
                        } else if (text.startsWith("green:")) {
                            colorCode = 2;
                            client.inputString = client.inputString.substring(6);
                        } else if (text.startsWith("cyan:")) {
                            colorCode = 3;
                            client.inputString = client.inputString.substring(5);
                        } else if (text.startsWith("purple:")) {
                            colorCode = 4;
                            client.inputString = client.inputString.substring(7);
                        } else if (text.startsWith("white:")) {
                            colorCode = 5;
                            client.inputString = client.inputString.substring(6);
                        } else if (text.startsWith("flash1:")) {
                            colorCode = 6;
                            client.inputString = client.inputString.substring(7);
                        } else if (text.startsWith("flash2:")) {
                            colorCode = 7;
                            client.inputString = client.inputString.substring(7);
                        } else if (text.startsWith("flash3:")) {
                            colorCode = 8;
                            client.inputString = client.inputString.substring(7);
                        } else if (text.startsWith("glow1:")) {
                            colorCode = 9;
                            client.inputString = client.inputString.substring(6);
                        } else if (text.startsWith("glow2:")) {
                            colorCode = 10;
                            client.inputString = client.inputString.substring(6);
                        } else if (text.startsWith("glow3:")) {
                            colorCode = 11;
                            client.inputString = client.inputString.substring(6);
                        }
                        text = client.inputString.toLowerCase();
                        int effectCode = 0;
                        if (text.startsWith("wave:")) {
                            effectCode = 1;
                            client.inputString = client.inputString.substring(5);
                        } else if (text.startsWith("wave2:")) {
                            effectCode = 2;
                            client.inputString = client.inputString.substring(6);
                        } else if (text.startsWith("shake:")) {
                            effectCode = 3;
                            client.inputString = client.inputString.substring(6);
                        } else if (text.startsWith("scroll:")) {
                            effectCode = 4;
                            client.inputString = client.inputString.substring(7);
                        } else if (text.startsWith("slide:")) {
                            effectCode = 5;
                            client.inputString = client.inputString.substring(6);
                        }
                        final int color = colorCode;
                        final int effect = effectCode;
//                        inputString = StringUtils.formatText(inputString);
//                        OutgoingPacket chat = new PublicChatMessage(textStream, inputString, color, effect);
//                        sendPacket(chat.create());
//                        inputString = TextInput.processText(inputString);

                        final MessagePublic messagePublic = new MessagePublic(0, color, effect, client.inputString);
                        client.sendPacket(messagePublic.create());

                        if (ChatBox.publicChatMode == 2) {
                            ChatBox.publicChatMode = 3;
                            // privacy option
                            client.sendPacket(new ChatSettings(client.gameChatMode, ChatBox.publicChatMode, ChatBox.privateChatMode, ChatBox.clanChatMode, ChatBox.tradeMode, ChatBox.yellMode).create());
                        }
                    }
                    client.inputString = "";
                    ChatBox.setUpdateChatbox(true);
                }
            } else if (!client.continuedDialogue) {
                if (key == KeyEvent.VK_SPACE) { // Space button to trigger "continue" in the statement dialogue.
                    if (Dialogue.chatInterfaceIsContinueDialogue()) {
                        client.sendPacket(new NextDialogue(4892).create()); // Server side does not check for or use this id, use 4892 as placeholder
                        client.continuedDialogue = true;
                    }
                } else if (key == KeyEvent.VK_1 || key == KeyEvent.VK_2 || key == KeyEvent.VK_3 || key == KeyEvent.VK_4 || key == KeyEvent.VK_5) { // Keys 1 to 5 to trigger option 1-5/1-4/1-3/1-2 on dialogue options.
                    if (Dialogue.chatInterfaceIsOptionDialogue()) {
                        int keyIndex = key - KeyEvent.VK_1;
                        int buttonIdStart = client.backDialogueId + Dialogue.OPTION_DIALOGUE_ID_OFFSET;
                        client.sendPacket(new ClickButton(buttonIdStart + keyIndex).create());
                    }
                }
            }
        } while (true);

        /*
         * Send a SendInput packet when it's been SendInput.LAST_SEND_DELAY milliseconds since the last queued key press
         */
        if (SendInput.lastSentId != -1 && SendInput.lastSentTimeStamp != -1 && System.currentTimeMillis() - SendInput.lastSentTimeStamp > SendInput.LAST_SENT_DELAY) {
            client.sendPacket(new SendInput(Widget.interfaceCache[SendInput.lastSentId].defaultText, SendInput.lastSentId).create());
            SendInput.lastSentId = -1;
            SendInput.lastSentTimeStamp = -1;
        }
    }

    public static void onInputPatternMatch(Widget input, char key){
        input.defaultText += (char) key;
    }

    public static void onInputPatternNoMatch(Client client, Widget widget) {
        if (widget.id == ColorPicker.INPUT_ID) {
            client.sendMessage("The color picker input only allows hexadecimal values to be entered into the input, or the input is full..");
        } else {
            client.sendMessage("The input you have selected does not allow the key you pressed to be entered into the input, or the input is full.");
        }
    }

    public static void tabToReplyPm(Client client) {
        String name = null;

        for (int k = 0; k < 500; k++) {
            if (client.chatMessages[k] == null) {
                continue;
            }

            int l = client.chatTypes[k];

            if (l == 3 || l == 7) {
                name = client.chatNames[k];
                break;
            }
        }

        if (name == null) {
            client.sendMessage("You haven't received any messages to which you can reply.", ChatBox.CHAT_TYPE_ALL, "");
            return;
        }

/*        if (name.startsWith("@cr")) {
            if (name.charAt(4) != '@') {
                name = name.substring(6);
            } else {
                name = name.substring(5);
            }
        }*/

        final String unformatted = name;

        if (name.contains(">"))
            name = name.substring(name.lastIndexOf(">") + 1);

        long nameAsLong = StringUtils.encodeBase37(name.trim());

        if (nameAsLong != -1) {

            ChatBox.setUpdateChatbox(true);
            client.inputDialogState = 0;
            client.messagePromptRaised = true;
            client.promptInput = "";
            client.friendsListAction = 3;
            client.encodedPrivateMessageRecipientName = nameAsLong;
            client.privateMessageRecipientName = name; // unformatted
            client.inputPromptTitle = "Enter a message to send to" + name; //name -> unformatted
        }
    }
}
