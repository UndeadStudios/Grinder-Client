package com.runescape.entity.model.particles;

import java.util.HashMap;
import java.util.Map;

public class ParticleAttachment {
    private static final Map<Integer, int[][]> attachments = new HashMap<>();
    public static int[][] getAttachments(int model) {
        return attachments.get(model);
    }
    static {

        // Max Cape
        attachments.put(29616, new int[][] {{270, 3}, {268, 3}, {56, 3}, {48, 3}, {72, 3}, {4, 3}, {42, 3}, {285, 3}, {284, 3}, {311, 3}, {312, 3}});
        attachments.put(29624, new int[][] {{247, 3}, {245, 3}, {56, 3}, {48, 3}, {37, 3}, {4, 3}, {41, 3}, {281, 3}, {280, 3}, {309, 3}, {310, 3}});

        // Infernal Max Cape
        attachments.put(33102, new int[][] {{206, 2}, {202, 2}, {47, 2}, {39, 2}, {54, 2}, {4, 2}, {20, 2}, {259, 2}, {258, 2}, {267, 2}, {268, 2}});
        attachments.put(33114, new int[][] {{190, 2}, {186, 2}, {47, 2}, {39, 2}, {53, 2}, {4, 2}, {20, 2}, {234, 2}, {233, 2}, {243, 2}, {244, 2}});

        // Infernal Cape
        attachments.put(33103, new int[][] {{38, 2}, {40, 2}, {30, 2}, {59, 2}, {64, 2}, {55, 2}, {61, 2}, {52, 2}, {5, 2}, {2, 2}, {18, 2}});
        attachments.put(33111, new int[][] {{38, 2}, {40, 2}, {30, 2}, {59, 2}, {64, 2}, {55, 2}, {61, 2}, {52, 2}, {5, 2}, {2, 2}, {18, 2}});

        // Master capes
        attachments.put(44966, new int[][] {{38, 2}, {40, 2}, {30, 2}, {59, 2}, {64, 2}, {55, 2}, {61, 2}, {52, 2}, {5, 2}, {2, 2}, {18, 2}});
        attachments.put(44966, new int[][] {{38, 2}, {40, 2}, {30, 2}, {59, 2}, {64, 2}, {55, 2}, {61, 2}, {52, 2}, {5, 2}, {2, 2}, {18, 2}});

    }
}

