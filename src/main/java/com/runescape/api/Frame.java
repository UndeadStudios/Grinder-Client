package com.runescape.api;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 14/04/2020
 */
public interface Frame {

    boolean isHasAlphaTransform();

    FrameBase getSkeleton();

    int getTransformCount();

    int[] getTransformSkeletonLabels();

    int[] getTransformXs();

    int[] getTransformYs();

    int[] getTransformZs();
}
