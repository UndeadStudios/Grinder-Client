package com.runescape.cache.anim;

import com.grinder.client.util.Log;
import com.runescape.Client;
import com.runescape.api.Frame;
import com.runescape.api.FrameBase;
import com.runescape.cache.Frames;
import com.runescape.io.Buffer;

import java.util.Arrays;

public final class RSFrame317 implements Frame {

    public static boolean USE_OSRS_FRAMES = true;

    public static RSFrame317[][] animationlist;
    private boolean hasAlphaTransform;
    public int duration;
    private FrameBase317 skeleton;
    private int transformCount;
    private int[] transformSkeletonLabels;
    private int[] transformXs;
    private int[] transformYs;
    private int[] transformZs;

    public static void load(int file, byte[] array) {
        try {
            //if(file == 2405){
            //    System.out.println("file = " + file + ", array = " + Arrays.toString(array));;
            // }
            final Buffer buffer = new Buffer(array);
            final FrameBase317 frameBase = new FrameBase317(buffer);
            final int n = buffer.readUShort();

            ;
            animationlist[file] = new RSFrame317[n * 3];
            final int[] array2 = new int[500];
            final int[] array3 = new int[500];
            final int[] array4 = new int[500];
            final int[] array5 = new int[500];
            for (int j = 0; j < n; ++j) {
                final int frameCount = buffer.readUShort();
                final Frame[] frames = animationlist[file];
                final RSFrame317 frame = new RSFrame317();
                frames[frameCount] = frame;
                frame.skeleton = frameBase;
                final int length = buffer.readUnsignedByte();
                int c2 = 0;
                int n3 = -1;
                for (int l = 0; l < length; ++l) {
                    final int f2;
                    if ((f2 = buffer.readUnsignedByte()) > 0) {
                        if (frameBase.getTransformTypes()[l] != 0) {
                            for (int n4 = l - 1; n4 > n3; --n4) {
                                if (frameBase.getTransformTypes()[n4] == 0) {
                                    array2[c2] = n4;
                                    array3[c2] = 0;
                                    array5[c2] = (array4[c2] = 0);
                                    ++c2;
                                    break;
                                }
                            }
                        }
                        array2[c2] = l;
                        int n4 = 0;
                        if (frameBase.getTransformTypes()[l] == 3) {
                            n4 = 128;
                        }
                        if ((f2 & 0x1) != 0x0) {
                            array3[c2] = buffer.readShort2();
                        } else {
                            array3[c2] = n4;
                        }
                        if ((f2 & 0x2) != 0x0) {
                            array4[c2] = buffer.readShort2();
                        } else {
                            array4[c2] = n4;
                        }
                        if ((f2 & 0x4) != 0x0) {
                            array5[c2] = buffer.readShort2();
                        } else {
                            array5[c2] = n4;
                        }
                        n3 = l;
                        ++c2;
                        if(frameBase.getTransformTypes()[l] == 5) {
                            frame.hasAlphaTransform = true;
                        }
                    }
                }
                frame.transformCount = c2;
                frame.transformSkeletonLabels = new int[c2];
                frame.transformXs = new int[c2];
                frame.transformYs = new int[c2];
                frame.transformZs = new int[c2];
                for (int l = 0; l < c2; ++l) {
                    frame.transformSkeletonLabels[l] = array2[l];
                    frame.transformXs[l] = array3[l];
                    frame.transformYs[l] = array4[l];
                    frame.transformZs[l] = array5[l];
                }
            }
        } catch (Exception ex) {
            Log.error("Could not load frame, file = "+file, ex);
            ex.printStackTrace();
        }
    }

    public static Frame getFrames(int frame) {
        int file = frame >> 16;
        final int index = frame &= 65535;
        final Frames frames = Frames.getFrames(file);

        if (frames == null) {
            return null;
        }

        return frames.frames[index];

/*        try {


            int k = frame & 0xffff;

            if (animationlist[file].length == 0) {
                Client.instance.resourceProvider.provide(1, file);
                return null;
            }

            return animationlist[file][k];
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }*/
    }

    public static boolean hasAlphaTransform(int file, int var1) {
        return animationlist[file][var1].hasAlphaTransform;
    }
    public static boolean noAnimationInProgress(int frame) {
        return frame == -1;
    }

    public static void clear() {
        animationlist = null;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "hasAlphaTransform=" + hasAlphaTransform +
                ", duration=" + duration +
                ", skeleton=" + skeleton +
                ", transformCount=" + transformCount +
                ", transformSkeletonLabels=" + Arrays.toString(transformSkeletonLabels) +
                ", transformXs=" + Arrays.toString(transformXs) +
                ", transformYs=" + Arrays.toString(transformYs) +
                ", transformZs=" + Arrays.toString(transformZs) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RSFrame317)) return false;

        RSFrame317 frame = (RSFrame317) o;

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