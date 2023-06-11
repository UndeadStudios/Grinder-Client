package com.grinder.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class DiscordRP {

    private boolean running = true;

    private long created = 0;

    private String topLine = "";
    private String bottomLine = "";

    private int lastX = 0;
    private int lastY = 0;

    public static boolean ignoreTopLine = false;
    public static boolean ignoreBottomLine = false;

    public void start() {
        this.created = System.currentTimeMillis();
        DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "898655845372538941";
        String steamId = "898655845372538941";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = "Booting up..";
        lib.Discord_UpdatePresence(presence);
        // in a worker thread
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
        /*

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {

            @Override
            public void apply(DiscordUser user) {
                update("Booting up...", "");

            }

        }).build();

        DiscordRPC.INSTANCE.Discord_Initialize("898655845372538941", handlers, true, "898655845372538941");

        new Thread("Discord RPC Callback") {

            @Override
            public void run() {
                while(running) {
                    DiscordRPC.INSTANCE.Discord_RunCallbacks();
                }
            }

        }.start();
*/
    }

    public void shutdown() {
        running = false;
        DiscordRPC.INSTANCE.Discord_Shutdown();
    }

    public void setTopLine(String topLine) {
        this.topLine = topLine;
        update(topLine, bottomLine);
    }

    public void setBottomLine(String bottomLine) {
        this.bottomLine = bottomLine;
        update(topLine, bottomLine);
    }

    public void setTrainingId(int skillId) {
        if (skillId == 3) {
            return; //return as HP
        }
        String skillString = "";
        switch (skillId) {
            case 0:
                skillString = "Attack";
                break;
            case 1:
                skillString = "Defence";
                break;
            case 2:
                skillString = "Strength";
                break;
            case 4:
                skillString = "Range";
                break;
            case 5:
                skillString = "Prayer";
                break;
            case 6:
                skillString = "Magic";
                break;
            case 7:
                skillString = "Cooking";
                break;
            case 8:
                skillString = "Woodcutting";
                break;
            case 9:
                skillString = "Fletching";
                break;
            case 10:
                skillString = "Fishing";
                break;
            case 11:
                skillString = "Firemaking";
                break;
            case 12:
                skillString = "Crafting";
                break;
            case 13:
                skillString = "Smithing";
                break;
            case 14:
                skillString = "Mining";
                break;
            case 15:
                skillString = "Herblore";
                break;
            case 16:
                skillString = "Agility";
                break;
            case 17:
                skillString = "Thieving";
                break;
            case 18:
                skillString = "Slayer";
                break;
            case 19:
                skillString = "Farming";
                break;
            case 20:
                skillString = "Runecrafting";
                break;
            case 21:
                skillString = "Hunter";
                break;
            case 22:
                skillString = "Construction";
                break;
        }
        setTopLine("Training: " + skillString);
    }

    public void setRegionId(int localX, int localY) {
        if (lastX == localX && lastY == localY || ignoreBottomLine)
            return;

        lastX = localX;
        lastY = localY;
        if (localX >= 3058 && localX <= 3123 &&
                localY >= 3460 && localY <= 3522) {
            setLocation("Home");
            return;
        } else if (localX >= 3324 && localX <= 3396 &&
                localY >= 3200 && localY <= 3290) {
            setLocation("Duel Arena");
            return;
        } else if (localX >= 2947 && localX <= 3001 &&
                localY >= 4225 && localY <= 4290) {
            setLocation("Weapon Minigame");
            return;
        } else if ((localX >= 3199 && localX <= 3245 &&
                localY >= 9794 && localY <= 9827) ||
                (localX >= 3199 && localX <= 3245 &&
                        localY >= 9794 && localY <= 9827)) {
            setLocation("Jail");
            return;
        } else if (localX >= 2816 && localX <= 2879 &&
                localY >= 4096 && localY <= 4159) {
            setLocation("Secret Edgeville");
            return;
        } else if (localX >= 2241 && localX <= 2304 &&
                localY >= 2882 && localY <= 2945) {
            setLocation("The New Peninsula");
            return;
        } else if (localX >= 2624 && localX <= 2675 &&
                localY >= 4608 && localY <= 4672) {
            setLocation("Custom Zone 3");
            return;
        } else if (localX >= 2659 && localX <= 2690 &&
                localY >= 3705 && localY <= 3737) {
            setLocation("Rock Crabs");
            return;
        } else if ((localX >= 3253 && localX <= 3265 &&
                localY >= 3255 && localY <= 3300) ||
                (localX >= 3240 && localX <= 3252 &&
                        localY >= 3271 && localY <= 3298)) {
            setLocation("Lumbridge Cows");
            return;
        } else if (localX >= 3225 && localX <= 3237 &&
                localY >= 3287 && localY <= 3301) {
            setLocation("Lumbridge Chickens");
            return;
        } else if (localX >= 3201 && localX <= 3226 &&
                localY >= 3201 && localY <= 3237) {
            setLocation("Lumbridge Castle");
            return;
        } else if (localX >= 2527 && localX <= 2548 &&
                localY >= 4709 && localY <= 4724) {
            setLocation("Mage Bank");
            return;
        } else if ((localX >= 3546 && localX <= 3584 &&
                localY >= 3264 && localY <= 3317) ||
                (localX >= 3529 && localX <= 3588 &&
                        localY >= 9674 && localY <= 9733) ||
                (localX >= 3521 && localX <= 3579 &&
                        localY >= 9665 && localY <= 9724)) {
            setLocation("Barrows");
            return;
        } else if (localX >= 1715 && localX <= 1791 &&
                localY >= 5127 && localY <= 5256) {
            setLocation("Giant Mole");
            return;
        } else if (localX >= 2239 && localX <= 2303 &&
                localY >= 2560 && localY <= 2624) {
            setLocation("The Cursed Vault");
            return;
        } else if (localX >= 3460 && localX <= 3510 &&
                localY >= 9477 && localY <= 9533) {
            setLocation("Kalphite Queen");
            return;
        } else if (localX >= 2435 && localX <= 2559 &&
                localY >= 10116 && localY <= 10175) {
            setLocation("Dagannoth Kings");
            return;
        } else if (localX >= 2961 && localX <= 3000 &&
                localY >= 4365 && localY <= 4402) {
            setLocation("Corporeal Beast");
            return;
        } else if (localX >= 2833 && localX <= 2862 &&
                localY >= 2579 && localY <= 2608) {
            setLocation("Medallion Casino");
            return;
        } else if (localX >= 3711 && localX <= 3772 &&
                localY >= 5633 && localY <= 5696) {
            setLocation("Motherlode Mine");
            return;
        } else if ((localX >= 2435 && localX <= 2446 &&
                localY >= 3081 && localY <= 3098) ||
                (localX >= 2356 && localX <= 2445 &&
                        localY >= 9473 && localY <= 9546) ||
                (localX >= 2366 && localX <= 2433 &&
                        localY >= 3069 && localY <= 3139)) {
            setLocation("Castle Wars");
            return;
        } else if (localX >= 3274 && localX <= 3298 &&
                localY >= 3922 && localY <= 3948) {
            setLocation("Wilderness Thieving");
            return;
        } else if (localX >= 2253 && localX <= 2290 &&
                localY >= 4676 && localY <= 4715) {
            setLocation("King Black Dragon");
            return;
        } else if (localX >= 3247 && localX <= 3316 &&
                localY >= 3904 && localY <= 3921) {
            setLocation("Chaos Elemental");
            return;
        } else if (localX >= 1933 && localX <= 1958 &&
                localY >= 4954 && localY <= 4976) {
            setLocation("Blast Furnace");
            return;
        } else if (localX >= 2756 && localX <= 2811 &&
                localY >= 9540 && localY <= 9596) {
            setLocation("Brimhaven Agility Arena");
            return;
        } else if (localX >= 1562 && localX <= 1657 &&
                localY >= 3471 && localY <= 3517) {
            setLocation("Woodcutting Guild");
            return;
        } else if (localX >= 3400 && localX <= 3455 &&
                localY >= 3530 && localY <= 3580) {
            setLocation("Slayer Tower");
            return;
        } else if (localX >= 3400 && localX <= 3450 &&
                localY >= 9926 && localY <= 9981) {
            setLocation("Slayer Basement");
            return;
        } else if (localX >= 3189 && localX <= 3269 &&
                localY >= 10054 && localY <= 10232) {
            setLocation("Revenants Cave");
            return;
        } else if (localX >= 3174 && localX <= 3196 &&
                localY >= 3924 && localY <= 3944) {
            setLocation("Wilderness Resource Area");
            return;
        } else if (localX >= 2466 && localX <= 2492 &&
                localY >= 3408 && localY <= 3441) {
            setLocation("Agility Training Area");
            return;
        } else if (localX >= 2523 && localX <= 2555 &&
                localY >= 3536 && localY <= 3577) {
            setLocation("Barbarian Outpost");
            return;
        } else if (localX >= 2151 && localX <= 2300 &&
                localY >= 3261 && localY <= 3457) {
            setLocation("Prifddinas");
            return;
        } else if (localX >= 2135 && localX <= 2314 &&
                localY >= 3115 && localY <= 3024) {
            setLocation("Zulrah");
            return;
        } else if (localX >= 3330 && localX <= 3386 &&
                localY >= 2818 && localY <= 2866) {
            setLocation("Agility Pyramid");
            return;
        } else if (localX >= 3132 && localX <= 3152 &&
                localY >= 3438 && localY <= 3455) {
            setLocation("Cook's Guild");
            return;
        } else if (localX >= 2782 && localX <= 2875 &&
                localY >= 3406 && localY <= 3449) {
            setLocation("Catherby");
            return;
        } else if (localX >= 2575 && localX <= 2642 &&
                localY >= 3392 && localY <= 3453) {
            setLocation("Fishing Guild");
            return;
        } else if (localX >= 3642 && localX <= 3713 &&
                localY >= 2933 && localY <= 3004) {
            setLocation("La Isla Ebana");
            return;
        } else if (localX >= 2112 && localX <= 2175 &&
                localY >= 2560 && localY <= 2623) {
            setLocation("The Deserted Reef");
            return;
        } else if (localX >= 1269 && localX <= 1354 &&
                localY >= 2991 && localY <= 3126) {
            setLocation("Aquais Neige");
            return;
        } else {
            setBottomLine(""); // Hide bottom line
        }


    }

    public void setLocation(String location) {
        setBottomLine("Location: " + location);
    }

    public void update(String firstLine, String secondLine) {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = firstLine;
        presence.smallImageText = secondLine;
        lib.Discord_UpdatePresence(presence);
    }



    public void update(String firstLine, String secondLine, String imageName) {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = firstLine;
        presence.smallImageKey = imageName;
        presence.smallImageText = secondLine;
        lib.Discord_UpdatePresence(presence);

        /*
        DiscordRichPresence b = new DiscordRichPresence(secondLine);

        b.setBigImage(imageName, "");
        b.setDetails(firstLine);
        b.setStartTimestamps(created);

        DiscordRPC.INSTANCE.Discord_UpdatePresence(b);*/
    }

}
