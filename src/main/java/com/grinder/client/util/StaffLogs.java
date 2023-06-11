package com.grinder.client.util;

import com.runescape.Client;
import com.runescape.io.Buffer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 03/04/2020
 */
public class StaffLogs {

    private static List<Entry> entries = new ArrayList<>();

    public static void read(Buffer buffer){
        final String name = buffer.readString();
        final int typeOrdinal = buffer.readUnsignedByte();
        final StaffLogType type = StaffLogType.values()[typeOrdinal];
        final int itemId = buffer.readInt();
        final int amount = buffer.readInt();

        if(!entries.isEmpty()){
            final Entry last = entries.get(entries.size()-1);
            if(last.playerName.equals(name)){
                if(last.type == type){
                    if(last.itemId == itemId){
                        last.setAmount(itemId);
                    }
                }
            }
            return;
        }

        entries.add(new Entry(name, type, itemId, amount));

    }


    static class Entry {
        private final String playerName;
        private final StaffLogType type;
        private final int itemId;
        private int amount;

        public Entry(String playerName, StaffLogType type, int itemId, int amount) {
            this.playerName = playerName;
            this.type = type;
            this.itemId = itemId;
            this.amount = amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry)) return false;

            Entry entry = (Entry) o;

            if (itemId != entry.itemId) return false;
            if (!playerName.equals(entry.playerName)) return false;
            return type == entry.type;
        }

        @Override
        public int hashCode() {
            int result = playerName.hashCode();
            result = 31 * result + type.hashCode();
            result = 31 * result + itemId;
            return result;
        }
    }

    enum  StaffLogType {
        ALCHING
    }
}
