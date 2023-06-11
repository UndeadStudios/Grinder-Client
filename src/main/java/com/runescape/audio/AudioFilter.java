package com.runescape.audio;

import com.runescape.io.Buffer;

/**
 * @version 1.0
 * @since 13/02/2020
 */
public class AudioFilter {

    private static float[][] coefficients;
    static int[][] coefficientRanges;
    private static float audioFrequency;
    static int audioFrequencyRange;

    int[] pairCounts;
    private int[][][] phasePairs;
    private int[][][] magnitudes;
    private int[] unity;

    static {
        coefficients = new float[2][8];
        coefficientRanges = new int[2][8];
    }

    AudioFilter() {
        pairCounts = new int[2];
        phasePairs = new int[2][2][4];
        magnitudes = new int[2][2][4];
        unity = new int[2];
    }

    private float adaptMagnitude(int direction, int i, float multiplier) {
        float amplitude =
                (float)magnitudes[direction][0][i]
                        + multiplier * (float)(magnitudes[direction][1][i] - magnitudes[direction][0][i]);
        amplitude *= 1.5258789E-03f;
        return 1.0F - (float)Math.pow(10.0D, -amplitude / 20.0F);
    }

    private float adaptPhase(int direction, int i, float multiplier) {
        float amplitude =
                (float) phasePairs[direction][0][i]
                        + multiplier * (float) (phasePairs[direction][1][i] - phasePairs[direction][0][i]);

        amplitude *= 1.2207031E-4F;
        return normalize(amplitude);
    }

    int compute(int dir, float factor) {
        float amplitude;
        if(dir == 0) {
            amplitude = (float) unity[0] + (float)(unity[1] - unity[0]) * factor;
            amplitude *= 3.0517578E-03f;
            audioFrequency = (float)Math.pow(0.1D, amplitude / 20.0F);
            audioFrequencyRange = (int)(audioFrequency * 65536.0F);
        }

        if(pairCounts[dir] == 0) {
            return 0;
        } else {
            amplitude = adaptMagnitude(dir, 0, factor);
            coefficients[dir][0] = -2.0F * amplitude * (float)Math.cos(adaptPhase(dir, 0, factor));
            coefficients[dir][1] = amplitude * amplitude;

            int i;
            for(i = 1; i < pairCounts[dir]; ++i) {
                amplitude = adaptMagnitude(dir, i, factor);
                float var5 = -2.0F * amplitude * (float)Math.cos(adaptPhase(dir, i, factor));
                float var6 = amplitude * amplitude;
                coefficients[dir][i * 2 + 1] = coefficients[dir][i * 2 - 1] * var6;
                coefficients[dir][i * 2] = coefficients[dir][i * 2 - 1] * var5 + coefficients[dir][i * 2 - 2] * var6;

                for(int var7 = i * 2 - 1; var7 >= 2; --var7) {
                    coefficients[dir][var7] += coefficients[dir][var7 - 1] * var5 + coefficients[dir][var7 - 2] * var6;
                }

                coefficients[dir][1] += coefficients[dir][0] * var5 + var6;
                coefficients[dir][0] += var5;
            }

            if(dir == 0) {
                for(i = 0; i < pairCounts[0] * 2; ++i)
                    coefficients[0][i] *= audioFrequency;
            }

            for(i = 0; i < pairCounts[dir] * 2; ++i)
                coefficientRanges[dir][i] = (int)(coefficients[dir][i] * 65536.0F);

            return pairCounts[dir] * 2;
        }
    }
    final void decode(Buffer buffer, SoundEnvelope envelope) {
        int pairCount = buffer.readUnsignedByte();
        pairCounts[0] = pairCount >> 4;
        pairCounts[1] = pairCount & 15;
        if(pairCount != 0) {
            unity[0] = buffer.getUnsignedLEShort();
            unity[1] = buffer.getUnsignedLEShort();
            int migrated = buffer.readUnsignedByte();

            int dir;
            int term;
            for(dir = 0; dir < 2; ++dir) {
                for(term = 0; term < pairCounts[dir]; ++term) {
                    phasePairs[dir][0][term] = buffer.getUnsignedLEShort();
                    magnitudes[dir][0][term] = buffer.getUnsignedLEShort();
                }
            }

            for(dir = 0; dir < 2; ++dir) {
                for(term = 0; term < pairCounts[dir]; ++term) {
                    if((migrated & 1 << dir * 4 << term) != 0) {
                        phasePairs[dir][1][term] = buffer.getUnsignedLEShort();
                        magnitudes[dir][1][term] = buffer.getUnsignedLEShort();
                    } else {
                        phasePairs[dir][1][term] = phasePairs[dir][0][term];
                        magnitudes[dir][1][term] = magnitudes[dir][0][term];
                    }
                }
            }

            if(migrated != 0 || unity[1] != unity[0]) {
                envelope.decodeSegments(buffer);
            }
        } else {
            int[] var7 = unity;
            unity[1] = 0;
            var7[0] = 0;
        }

    }
    private static float normalize(float var0) {
        float var1 = 32.703197F * (float)Math.pow(2.0D, var0);
        return var1 * 3.1415927F / 11025.0F;
    }
}
