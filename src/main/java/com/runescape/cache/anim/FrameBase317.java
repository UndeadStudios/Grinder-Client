package com.runescape.cache.anim;

import com.runescape.api.FrameBase;
import com.runescape.io.Buffer;

import java.util.Arrays;

public final class FrameBase317 implements FrameBase {

    private final int[] transformTypes;

    private final int[][] labels;

    public FrameBase317(Buffer stream) {
        int count = stream.readUShort();

        transformTypes = new int[count];
        labels = new int[count][];

        for (int index = 0; index < count; index++) {
            transformTypes[index] = stream.readUShort();
        }

        for (int label = 0; label < count; label++) {
            labels[label] = new int[stream.readUShort()];
        }

        for (int label = 0; label < count; label++) {
            for (int index = 0; index < labels[label].length; index++) {
                labels[label][index] = stream.readUShort();
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FrameBase317)) return false;

        FrameBase317 frameBase = (FrameBase317) o;

        if (!Arrays.equals(transformTypes, frameBase.transformTypes)) return false;
        return Arrays.deepEquals(labels, frameBase.labels);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(transformTypes);
        result = 31 * result + Arrays.deepHashCode(labels);
        return result;
    }

    /**
     * The type of each transformation.
     */
    @Override
    public int[] getTransformTypes() {
        return transformTypes;
    }

    @Override
    public int[][] getLabels() {
        return labels;
    }
}
