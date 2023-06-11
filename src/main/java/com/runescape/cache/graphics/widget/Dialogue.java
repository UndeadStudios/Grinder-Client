package com.runescape.cache.graphics.widget;

import com.runescape.Client;

import java.util.stream.IntStream;

public class Dialogue {
    /**
     * This array contains the child id where the dialogue
     * statement starts for npc and item dialogues.
     */
    public static final int[] NPC_DIALOGUE_ID = {
            4885,
            4890,
            4896,
            4903
    };

    /**
     * This array contains the child id where the dialogue
     * statement starts for player dialogues.
     */
    public static final int[] PLAYER_DIALOGUE_ID = {
            971,
            976,
            982,
            989
    };

    /**
     * This array contains the child id where the dialogue
     * statement starts for option dialogues.
     */
    public static final int[] OPTION_DIALOGUE_ID = {
            13760,
            2461,
            2471,
            2482,
            2494,
    };

    /**
     * This array contains the chatbox interfaces
     * for statements.
     */
    public static final int[] STATEMENT_DIALOGUE_ID = {
            356,
            359,
            363,
            368,
            374,
    };

    /**
     * This array contains the chatbox interfaces
     * for skills.
     */
    public static final int[] SKILL_DIALOGUE_ID = {
            6247,
            6253,
            6206,
            6216,
            4443,
            6242,
            6211,
            6226,
            4272,
            6231,
            6258,
            4282,
            6263,
            6221,
            4416,
            6237,
            4277,
            4261,
            12122,
            5267,
            4267,
            7267,
            8267,
    };

    // The offset used to get the actual npc or player dialogue id
    public static final int NPC_OR_PLAYER_DIALOGUE_ID_OFFSET = 3;

    // The offset used to get the actual option dialogue id
    public static final int OPTION_DIALOGUE_ID_OFFSET = 2;

    public static boolean chatInterfaceIsContinueDialogue() {
        return chatInterfaceIsDialogueType(NPC_DIALOGUE_ID)|| chatInterfaceIsDialogueType(PLAYER_DIALOGUE_ID) || chatInterfaceIsDialogueType(STATEMENT_DIALOGUE_ID) || chatInterfaceIsDialogueType(SKILL_DIALOGUE_ID);
    }

    public static boolean chatInterfaceIsOptionDialogue() {
        return chatInterfaceIsDialogueType(OPTION_DIALOGUE_ID);
    }

    public static boolean chatInterfaceIsDialogueType(int[] dialogueType) {
        int offset = 0;
        if (dialogueType == NPC_DIALOGUE_ID || dialogueType == PLAYER_DIALOGUE_ID) {
            offset = NPC_OR_PLAYER_DIALOGUE_ID_OFFSET;
        } else if (dialogueType == OPTION_DIALOGUE_ID) {
            offset = OPTION_DIALOGUE_ID_OFFSET;
        }
        int dialogId = Client.instance.backDialogueId + offset;
        return IntStream.of(dialogueType).anyMatch(x -> x == dialogId);
    }
}
