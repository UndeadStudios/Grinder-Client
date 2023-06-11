package com.runescape.cache;

import com.runescape.api.Frame;
import com.runescape.api.FrameBase;
import com.runescape.io.Buffer;

import java.util.Arrays;

/**
 * @version 1.0
 * @since 14/02/2020
 */
public class Animation implements Frame {

    static int[] indexFrameIds;
    static int[] translator_x;
    static int[] translator_y;
    static int[] translator_z;

    public Skeleton skeleton;
    public int transformCount;
    public int[] transformSkeletonLabels;
    public int[] transformXs;
    public int[] transformYs;
    public int[] transformZs;
    public boolean hasAlphaTransform;

    static {
        indexFrameIds = new int[500];
        translator_x = new int[500];
        translator_y = new int[500];
        translator_z = new int[500];
    }

    Animation(byte[] data, Skeleton skeleton) {
        this.skeleton = null;
        this.transformCount = -1;
        this.hasAlphaTransform = false;
        this.skeleton = skeleton;
        Buffer buffer1 = new Buffer(data);
        Buffer buffer2 = new Buffer(data);
        buffer1.index = 2;
        int length = buffer1.getUnsignedByte();
        int lastI = -1;
        int translatorCount = 0;
        buffer2.index = length + buffer1.index;

        int i;
        for(i = 0; i < length; ++i) {

            // read byte at every i
            int frameMetaData = buffer1.getUnsignedByte();

            // if byte is larger than 0
            if(frameMetaData > 0) {

                // if type of i is not 0
                if(this.skeleton.getTransformTypes()[i] != 0) {
                    for(int skip = i - 1; skip > lastI; --skip) {
                        if(this.skeleton.getTransformTypes()[skip] == 0) {
                            indexFrameIds[translatorCount] = skip;
                            translator_x[translatorCount] = 0;
                            translator_y[translatorCount] = 0;
                            translator_z[translatorCount] = 0;
                            ++translatorCount;
                            break;
                        }
                    }
                }

                indexFrameIds[translatorCount] = i;
                short var11 = 0;
                if(this.skeleton.getTransformTypes()[i] == 3) {
                    var11 = 128;
                }

                if((frameMetaData & 1) != 0) {
                    translator_x[translatorCount] = buffer2.readByteOrShort1();
                } else {
                    translator_x[translatorCount] = var11;
                }

                if((frameMetaData & 2) != 0) {
                    translator_y[translatorCount] = buffer2.readByteOrShort1();
                } else {
                    translator_y[translatorCount] = var11;
                }

                if((frameMetaData & 4) != 0) {
                    translator_z[translatorCount] = buffer2.readByteOrShort1();
                } else {
                    translator_z[translatorCount] = var11;
                }

                lastI = i;
                ++translatorCount;
                if(this.skeleton.getTransformTypes()[i] == 5) {
                    this.hasAlphaTransform = true;
                }
            }
        }

        if(data.length != buffer2.index) {
            throw new RuntimeException();
        } else {
            this.transformCount = translatorCount;
            this.transformSkeletonLabels = new int[translatorCount];
            this.transformXs = new int[translatorCount];
            this.transformYs = new int[translatorCount];
            this.transformZs = new int[translatorCount];

            for(i = 0; i < translatorCount; ++i) {
                this.transformSkeletonLabels[i] = indexFrameIds[i];
                this.transformXs[i] = translator_x[i];
                this.transformYs[i] = translator_y[i];
                this.transformZs[i] = translator_z[i];
            }

        }
    }

    @Override
    public String toString() {
        return "Animation{" +
                "skeleton=" + skeleton +
                ", transformCount=" + transformCount +
                ", transformSkeletonLabels=" + Arrays.toString(transformSkeletonLabels) +
                ", transformXs=" + Arrays.toString(transformXs) +
                ", transformYs=" + Arrays.toString(transformYs) +
                ", transformZs=" + Arrays.toString(transformZs) +
                ", hasAlphaTransform=" + hasAlphaTransform +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Animation)) return false;

        Animation frame = (Animation) o;

        if (hasAlphaTransform != frame.hasAlphaTransform) return false;
        if (transformCount != frame.transformCount) return false;
        if (skeleton != null ? !skeleton.equals(frame.skeleton) : frame.skeleton != null) return false;
        if (!Arrays.equals(transformSkeletonLabels, frame.transformSkeletonLabels)) return false;
        if (!Arrays.equals(transformXs, frame.transformXs)) return false;
        if (!Arrays.equals(transformYs, frame.transformYs)) return false;
        return Arrays.equals(transformZs, frame.transformZs);
    }

    @Override
    public int hashCode() {
        int result = (hasAlphaTransform ? 1 : 0);
        result = 31 * result + (skeleton != null ? skeleton.hashCode() : 0);
        result = 31 * result + transformCount;
        result = 31 * result + Arrays.hashCode(transformSkeletonLabels);
        result = 31 * result + Arrays.hashCode(transformXs);
        result = 31 * result + Arrays.hashCode(transformYs);
        result = 31 * result + Arrays.hashCode(transformZs);
        return result;
    }

    @Override
    public boolean isHasAlphaTransform() {
        return hasAlphaTransform;
    }

    @Override
    public FrameBase getSkeleton() {
        return skeleton;
    }

    @Override
    public int getTransformCount() {
        return transformCount;
    }

    @Override
    public int[] getTransformSkeletonLabels() {
        return transformSkeletonLabels;
    }

    @Override
    public int[] getTransformXs() {
        return transformXs;
    }

    @Override
    public int[] getTransformYs() {
        return transformYs;
    }

    @Override
    public int[] getTransformZs() {
        return transformZs;
    }
}
