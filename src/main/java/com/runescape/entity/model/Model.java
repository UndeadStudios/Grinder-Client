package com.runescape.entity.model;

import com.grinder.client.ClientCompanion;
import com.grinder.client.util.Log;
import com.runescape.Client;
import com.grinder.Configuration;
import com.runescape.api.Frame;
import com.runescape.cache.*;
import com.runescape.api.FrameBase;
import com.runescape.cache.anim.RSFrame317;
import com.runescape.cache.anim.animaya.*;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;
import com.runescape.entity.model.particles.Particle;
import com.runescape.entity.model.particles.ParticleAttachment;
import com.runescape.entity.model.particles.ParticleDefinition;
import com.runescape.entity.model.particles.Vector;
import com.runescape.io.Buffer;
import com.grinder.net.Provider;
import com.runescape.scene.SceneGraph;
import com.runescape.scene.ViewportMouse;
import net.runelite.api.model.Triangle;
import net.runelite.api.model.Vertex;

import java.util.*;

public class Model extends Renderable {

    public static final int MAX_MODELS = 80000;
    public static int MODEL_DRAW_DISTANCE = 3500;//3500

    public static int modelCreationCount;
    public static final Model EMPTY_MODEL = new Model();
    public static boolean aBoolean1684;
    public static int lastMouseX;
    public static int lastMouseY;
    public static int objectCount;
    public static long[] objectUIDs = new long[1000];
    public static int[] SINE;
    public static int[] COSINE;
    static ModelHeader[] modelHeaders;
    static Provider resourceProvider;
    static boolean[] indicesOutOfBounds = new boolean[6500];
    static boolean[] indicesOutOfReach = new boolean[6500];
    static int[] projected_vertex_x = new int[6500];
    static int[] projected_vertex_y = new int[6500];
    static int[] projected_vertex_z = new int[6500];
    static int[] projected_vertex_z_abs = new int[6500];
    static int[] camera_vertex_x = new int[6500];
    static int[] camera_vertex_y = new int[6500];
    static int[] camera_vertex_z = new int[6500];
    static char[] depthListIndices = new char[6000];
    static char[][] faceLists = new char[6000][512];
    static int[] faceRenderPriorityListIndices = new int[12];
    static int[][] faceRenderPriorityLists = new int[12][2000];
    static int[] mediumPriorityFaceLists = new int[2000];
    static int[] highPriorityFaceLists = new int[2000];
    static int[] skippedRenderPriorityDepths = new int[12];
    static int[] out_of_bounds_projected_vertex_x = new int[10];
    static int[] out_of_bounds_projected_vertex_y = new int[10];
    static int[] out_of_bounds_face_colors = new int[10];
    static int Model_transformTempX;
    static int Model_transformTempY;
    static int Model_transformTempZ;
    static int[] hsl_to_rgb;
    static int[] modelIntArray4;

    static {
        SINE = Rasterizer3D.Rasterizer3D_sine;
        COSINE = Rasterizer3D.Rasterizer3D_cosine;
        hsl_to_rgb = Rasterizer3D.hslToRgb;
        modelIntArray4 = Rasterizer3D.__et_p;
    }

    public short[] faceTextures;
    public byte[] texture_coordinates;
    public byte[] texture_type;
    public int verticesCount;
    public int offX = 0, offY = 0, offZ = 0, viewOrientation = 0;
    public int[] verticesParticle;
    public int[] verticesX;
    public int[] verticesY;
    public int[] verticesZ;
    public int indicesCount;
    public int[] indices1;
    public int[] indices2;
    public int[] indices3;
    public int[] faceColors1;
    public int[] faceColors2;
    public int[] faceColors3;
    public byte[] faceDrawType;
    public byte[] faceRenderPriorities;
    public byte[] faceAlphas;
    public short[] triangleColours;
    public byte face_priority = 0;
    public int numberOfTexturesFaces;
    public int[] textures_face_a;
    public int[] textures_face_b;
    public int[] textures_face_c;
    public int minimumXVertex;
    public int maximumXVertex;
    public int maximumZVertex;
    public int minimumZVertex;
    public int xzRadius;
    public int maximumYVertex;
    public int diameter;
    public int radius;
    public int[] vertexVSkin;
    public int[] triangleTSkin;
    public int[][] vertexLabels;
    public int[][] faceLabelsAlpha;
    public boolean isSingleTile;

    static class421 field2656 = new class421();
    static class421 field2660 = new class421();
    static class421 field2724 = new class421();

    public Model() {
        verticesCount = 0;
        indicesCount = 0;
        face_priority = 0;
        numberOfTexturesFaces = 0;
        isSingleTile = false;
        xMidOffset = -1;
        yMidOffset = -1;
        zMidOffset = -1;
    }

    //dunno what these are
    public int animayaGroups[][];
    public int animayaScales[][];

    public Model(int modelId) {

        final byte[] data = modelHeaders[modelId].data;
        if (data[data.length - 1] == -3 && data[data.length - 2] == -1) {
            ModelLoader.decodeType3(this, data);
        } else if (data[data.length - 1] == -2 && data[data.length - 2] == -1) {
            ModelLoader.decodeType2(this, data);
        } else if (data[data.length - 1] == -1 && data[data.length - 2] == -1) {
            ModelLoader.decodeType1(this, data);
        } else {
            ModelLoader.decodeOldFormat(this, data);
        }

        configureParticles(modelId);
    }

    public Model(Model[] var1, int var2) {
        try {
            boolean var3 = false;
            boolean var4 = false;
            boolean var5 = false;
            boolean var6 = false;
            this.verticesCount = 0;
            this.indicesCount = 0;
            this.numberOfTexturesFaces = 0;
            this.face_priority = -1;

            int var7;
            Model var8;
            for (var7 = 0; var7 < var2; ++var7) {
                var8 = var1[var7];
                if (var8 != null) {
                    this.verticesCount += var8.verticesCount;
                    this.indicesCount += var8.indicesCount;
                    this.numberOfTexturesFaces += var8.numberOfTexturesFaces;
                    if (var8.faceRenderPriorities != null) {
                        var3 = true;
                    } else {
                        if (this.face_priority == -1) {
                            this.face_priority = var8.face_priority;
                        }

                        if (this.face_priority != var8.face_priority) {
                            var3 = true;
                        }
                    }

                    var4 |= var8.faceAlphas != null;
                    var5 |= var8.faceTextures != null;
                    var6 |= var8.texture_coordinates != null;
                }
            }

            this.verticesParticle = new int[this.verticesCount];
            this.verticesX = new int[this.verticesCount];
            this.verticesY = new int[this.verticesCount];
            this.verticesZ = new int[this.verticesCount];
            this.indices1 = new int[this.indicesCount];
            this.indices2 = new int[this.indicesCount];
            this.indices3 = new int[this.indicesCount];
            this.faceColors1 = new int[this.indicesCount];
            this.faceColors2 = new int[this.indicesCount];
            this.faceColors3 = new int[this.indicesCount];
            if (var3) {
                this.faceRenderPriorities = new byte[this.indicesCount];
            }

            if (var4) {
                this.faceAlphas = new byte[this.indicesCount];
            }

            if (var5) {
                this.faceTextures = new short[this.indicesCount];
            }

            if (var6) {
                this.texture_coordinates = new byte[this.indicesCount];
            }

            if (this.numberOfTexturesFaces > 0) {
                this.textures_face_a = new int[this.numberOfTexturesFaces];
                this.textures_face_b = new int[this.numberOfTexturesFaces];
                this.textures_face_c = new int[this.numberOfTexturesFaces];
            }

            this.verticesCount = 0;
            this.indicesCount = 0;
            this.numberOfTexturesFaces = 0;

            for (var7 = 0; var7 < var2; ++var7) {
                var8 = var1[var7];
                if (var8 != null) {
                    int var9;
                    for (var9 = 0; var9 < var8.indicesCount; ++var9) {
                        this.indices1[this.indicesCount] = this.verticesCount + var8.indices1[var9];
                        this.indices2[this.indicesCount] = this.verticesCount + var8.indices2[var9];
                        this.indices3[this.indicesCount] = this.verticesCount + var8.indices3[var9];
                        this.faceColors1[this.indicesCount] = var8.faceColors1[var9];
                        this.faceColors2[this.indicesCount] = var8.faceColors2[var9];
                        this.faceColors3[this.indicesCount] = var8.faceColors3[var9];
                        if (var3) {
                            if (var8.faceRenderPriorities != null) {
                                this.faceRenderPriorities[this.indicesCount] = var8.faceRenderPriorities[var9];
                            } else {
                                this.faceRenderPriorities[this.indicesCount] = var8.face_priority;
                            }
                        }

                        if (var4 && var8.faceAlphas != null) {
                            this.faceAlphas[this.indicesCount] = var8.faceAlphas[var9];
                        }

                        if (var5) {
                            if (var8.faceTextures != null) {
                                this.faceTextures[this.indicesCount] = var8.faceTextures[var9];
                            } else {
                                this.faceTextures[this.indicesCount] = -1;
                            }
                        }

                        if (var6) {
                            if (var8.texture_coordinates != null && var8.texture_coordinates[var9] != -1) {
                                this.texture_coordinates[this.indicesCount] = (byte) (this.numberOfTexturesFaces + var8.texture_coordinates[var9]);
                            } else {
                                this.texture_coordinates[this.indicesCount] = -1;
                            }
                        }

                        ++this.indicesCount;
                    }

                    for (var9 = 0; var9 < var8.numberOfTexturesFaces; ++var9) {
                        this.textures_face_a[this.numberOfTexturesFaces] = this.verticesCount + var8.textures_face_a[var9];
                        this.textures_face_b[this.numberOfTexturesFaces] = this.verticesCount + var8.textures_face_b[var9];
                        this.textures_face_c[this.numberOfTexturesFaces] = this.verticesCount + var8.textures_face_c[var9];
                        ++this.numberOfTexturesFaces;
                    }

                    for (var9 = 0; var9 < var8.verticesCount; ++var9) {
                        this.verticesX[this.verticesCount] = var8.verticesX[var9];
                        this.verticesY[this.verticesCount] = var8.verticesY[var9];
                        this.verticesZ[this.verticesCount] = var8.verticesZ[var9];
                        ++this.verticesCount;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureParticles(int modelId) {
        int[][] attachments = ParticleAttachment.getAttachments(modelId);

        if (attachments != null) {
            for (int[] attach : attachments) {
                if (attach[0] == -1) {
                    for (int i : indices1) verticesParticle[i] = attach[1] + 1;
                } else if (attach[0] == -2) {
                    for (int i : indices2) verticesParticle[i] = attach[1] + 1;
                } else if (attach[0] == -3) {
                    for (int i : indices3) verticesParticle[i] = attach[1] + 1;
                } else if (attach[0] == -4) {
                    for (int i : indices1) verticesParticle[i] = attach[1] + 1;
                    for (int i : indices2) verticesParticle[i] = attach[1] + 1;
                    for (int i : indices3) verticesParticle[i] = attach[1] + 1;
                } else if (attach[0] == -5) {
                    verticesParticle[indices1[indices1.length - 1]] = attach[1] + 1;
                    verticesParticle[indices2[indices2.length - 1]] = attach[1] + 1;
                    verticesParticle[indices3[indices3.length - 1]] = attach[1] + 1;
                } else {
                    verticesParticle[attach[0]] = attach[1] + 1;
                }
            }
        }
    }

    public static void clear() {
        modelHeaders = null;
        indicesOutOfBounds = null;
        indicesOutOfReach = null;
        projected_vertex_y = null;
        projected_vertex_z = null;
        projected_vertex_z_abs = null;
        camera_vertex_x = null;
        camera_vertex_y = null;
        camera_vertex_z = null;
        depthListIndices = null;
        faceLists = null;
        faceRenderPriorityListIndices = null;
        faceRenderPriorityLists = null;
        mediumPriorityFaceLists = null;
        highPriorityFaceLists = null;
        skippedRenderPriorityDepths = null;
        SINE = null;
        COSINE = null;
        hsl_to_rgb = null;
        modelIntArray4 = null;
    }

    public static void readModelHeader(byte[] data, int modelId) {
        try {
            if (data == null) {
                ModelHeader header = modelHeaders[modelId] = new ModelHeader();
                header.anInt369 = 0;
                header.anInt370 = 0;
                header.anInt371 = 0;
                return;
            }
            Buffer stream = new Buffer(data);
            stream.index = data.length - 18;
            ModelHeader class21_1 = modelHeaders[modelId] = new ModelHeader();
            class21_1.data = data;
            class21_1.anInt369 = stream.readUShort();
            class21_1.anInt370 = stream.readUShort();
            class21_1.anInt371 = stream.readUnsignedByte();
            int k = stream.readUnsignedByte();
            int l = stream.readUnsignedByte();
            int i1 = stream.readUnsignedByte();
            int j1 = stream.readUnsignedByte();
            int k1 = stream.readUnsignedByte();
            int l1 = stream.readUShort();
            int i2 = stream.readUShort();
            int j2 = stream.readUShort();
            int k2 = stream.readUShort();
            int l2 = 0;
            class21_1.anInt372 = l2;
            l2 += class21_1.anInt369;
            class21_1.anInt378 = l2;
            l2 += class21_1.anInt370;
            class21_1.anInt381 = l2;
            if (l == 255)
                l2 += class21_1.anInt370;
            else
                class21_1.anInt381 = -l - 1;
            class21_1.anInt383 = l2;
            if (j1 == 1)
                l2 += class21_1.anInt370;
            else
                class21_1.anInt383 = -1;
            class21_1.anInt380 = l2;
            if (k == 1)
                l2 += class21_1.anInt370;
            else
                class21_1.anInt380 = -1;
            class21_1.anInt376 = l2;
            if (k1 == 1)
                l2 += class21_1.anInt369;
            else
                class21_1.anInt376 = -1;
            class21_1.anInt382 = l2;
            if (i1 == 1)
                l2 += class21_1.anInt370;
            else
                class21_1.anInt382 = -1;
            class21_1.anInt377 = l2;
            l2 += k2;
            class21_1.anInt379 = l2;
            l2 += class21_1.anInt370 * 2;
            class21_1.anInt384 = l2;
            l2 += class21_1.anInt371 * 6;
            class21_1.anInt373 = l2;
            l2 += l1;
            class21_1.anInt374 = l2;
            l2 += i2;
            class21_1.anInt375 = l2;
            l2 += j2;
        } catch (Exception e) {
            Log.error("Failed to read model header for model "+modelId, e);
        }
    }

    public static void init(Provider provider) {
        modelHeaders = new ModelHeader[MAX_MODELS];
        resourceProvider = provider;
    }

    public static void resetModelHeader(int file) {
        modelHeaders[file] = null;
    }

    public static Model getModel(int file) {
        if (modelHeaders == null)
            return null;

        ModelHeader header = modelHeaders[file];
        if (header == null) {
            resourceProvider.provide(file);
            return null;
        } else {
            return new Model(file);
        }
    }

    public static boolean isCached(int file) {
        if (modelHeaders == null)
            return false;

        ModelHeader class21 = modelHeaders[file];
        if (class21 == null) {
            resourceProvider.provide(file);
            return false;
        } else {
            return true;
        }
    }

    public static int applyLighting(int hsl, int light, int type) {
        if (hsl == 65535)
            return 0;

        if ((type & 2) == 2) {
            if (light < 0)
                light = 0;
            else if (light > 127)
                light = 127;

            light = 127 - light;
            return light;
        }

        light = light * (hsl & 127) >> 7;
        if (light < 2)
            light = 2;
        else if (light > 126)
            light = 126;

        return (hsl & 0b1111111110000000) + light;
    }


    private int method465(Model model, int face) {
        int vertex = -1;
        if (face > model.verticesX.length || face > model.verticesY.length || face > model.verticesZ.length) {
            return vertex;
        }
        int p = model.verticesParticle[face];
        int x = model.verticesX[face];
        int y = model.verticesY[face];
        int z = model.verticesZ[face];
        for (int index = 0; index < verticesCount; index++) {
            if (x != verticesX[index] || y != verticesY[index] || z != verticesZ[index])
                continue;
            vertex = index;
            break;
        }
        if (vertex == -1) {
            verticesParticle[verticesCount] = p;
            verticesX[verticesCount] = x;
            verticesY[verticesCount] = y;
            verticesZ[verticesCount] = z;
            if (model.vertexVSkin != null)
                vertexVSkin[verticesCount] = model.vertexVSkin[face];

            vertex = verticesCount++;
        }
        return vertex;
    }

    public void calculateDistances() {
        super.height = 0;
        xzRadius = 0;
        maximumYVertex = 0;
        for (int i = 0; i < verticesCount; i++) {
            int j = verticesX[i];
            int k = verticesY[i];
            int l = verticesZ[i];
            if (-k > super.height)
                super.height = -k;
            if (k > maximumYVertex)
                maximumYVertex = k;
            int i1 = j * j + l * l;
            if (i1 > xzRadius)
                xzRadius = i1;
        }
        xzRadius = (int) (Math.sqrt(xzRadius) + 0.98999999999999999D);
        radius = (int) (Math.sqrt(xzRadius * xzRadius + super.height
                * super.height) + 0.98999999999999999D);
        diameter = radius
                + (int) (Math.sqrt(xzRadius * xzRadius + maximumYVertex
                * maximumYVertex) + 0.98999999999999999D);
    }

    public void computeSphericalBounds() {
        super.height = 0;
        maximumYVertex = 0;
        for (int i = 0; i < verticesCount; i++) {
            int j = verticesY[i];
            if (-j > super.height)
                super.height = -j;
            if (j > maximumYVertex)
                maximumYVertex = j;
        }

        radius = (int) (Math.sqrt(xzRadius * xzRadius + super.height
                * super.height) + 0.98999999999999999D);
        diameter = radius
                + (int) (Math.sqrt(xzRadius * xzRadius + maximumYVertex
                * maximumYVertex) + 0.98999999999999999D);
    }

    public void calculateVertexData(int i) {
        super.height = 0;
        xzRadius = 0;
        maximumYVertex = 0;
        minimumXVertex = 999999;
        maximumXVertex = -999999;
        maximumZVertex = -99999;
        minimumZVertex = 99999;
        for (int j = 0; j < verticesCount; j++) {
            int x = verticesX[j];
            int y = verticesY[j];
            int z = verticesZ[j];
            if (x < minimumXVertex)
                minimumXVertex = x;
            if (x > maximumXVertex)
                maximumXVertex = x;
            if (z < minimumZVertex)
                minimumZVertex = z;
            if (z > maximumZVertex)
                maximumZVertex = z;
            if (-y > super.height)
                super.height = -y;
            if (y > maximumYVertex)
                maximumYVertex = y;
            int j1 = x * x + z * z;
            if (j1 > xzRadius)
                xzRadius = j1;
        }
        xzRadius = (int) Math.sqrt(xzRadius);
        radius = (int) Math.sqrt(xzRadius * xzRadius + super.height * super.height);
        if (i == 21073)
            diameter = radius + (int) Math.sqrt(xzRadius * xzRadius + maximumYVertex * maximumYVertex);
    }

    public void skin() {
        if (vertexVSkin != null) {
            int[] ai = new int[256];
            int j = 0;
            for (int l = 0; l < verticesCount; l++) {
                int j1 = vertexVSkin[l];
                ai[j1]++;
                if (j1 > j)
                    j = j1;
            }
            vertexLabels = new int[j + 1][];
            for (int k1 = 0; k1 <= j; k1++) {
                vertexLabels[k1] = new int[ai[k1]];
                ai[k1] = 0;
            }
            for (int j2 = 0; j2 < verticesCount; j2++) {
                int l2 = vertexVSkin[j2];
                vertexLabels[l2][ai[l2]++] = j2;
            }
            vertexVSkin = null;
        }
        if (triangleTSkin != null) {
            int[] ai1 = new int[256];
            int k = 0;
            for (int i1 = 0; i1 < indicesCount; i1++) {
                int l1 = triangleTSkin[i1];
                ai1[l1]++;
                if (l1 > k)
                    k = l1;
            }
            faceLabelsAlpha = new int[k + 1][];
            for (int i2 = 0; i2 <= k; i2++) {
                faceLabelsAlpha[i2] = new int[ai1[i2]];
                ai1[i2] = 0;
            }
            for (int k2 = 0; k2 < indicesCount; k2++) {
                int i3 = triangleTSkin[k2];
                faceLabelsAlpha[i3][ai1[i3]++] = k2;
            }
            triangleTSkin = null;
        }
    }

    public void applyAnimationFrame(int frame, int nextFrame, int end, int cycle) {
        if (!Configuration.enableTweening) {
            applyTransform(frame);
            return;
        }
        interpolateFrames(frame, nextFrame, end, cycle);
    }

    public void interpolateFrames(int frame, int nextFrame, int end, int cycle) {

        if ((vertexLabels != null && frame != -1)) {
            Frame currentAnimation = RSFrame317.getFrames(frame);
            if (currentAnimation == null)
                return;
            FrameBase currentList = currentAnimation.getSkeleton();
            Model_transformTempX = 0;
            Model_transformTempY = 0;
            Model_transformTempZ = 0;
            Frame nextAnimation = null;
            FrameBase nextList = null;
            if (nextFrame != -1) {
                nextAnimation = RSFrame317.getFrames(nextFrame);
                if (nextAnimation == null || nextAnimation.getSkeleton() == null)
                    return;
                FrameBase nextSkin = nextAnimation.getSkeleton();
                if (nextSkin != currentList)
                    nextAnimation = null;
                nextList = nextSkin;
            }
            if (nextAnimation == null || nextList == null) {
                for (int opcodeLinkTableIdx = 0; opcodeLinkTableIdx < currentAnimation.getTransformCount(); opcodeLinkTableIdx++) {
                    int i_264_ = currentAnimation.getTransformSkeletonLabels()[opcodeLinkTableIdx];
                    transform(currentList.getTransformTypes()[i_264_], currentList.getLabels()[i_264_], currentAnimation.getTransformXs()[opcodeLinkTableIdx], currentAnimation.getTransformYs()[opcodeLinkTableIdx], currentAnimation.getTransformZs()[opcodeLinkTableIdx]);
                }
            } else {

                for (int i1 = 0; i1 < currentAnimation.getTransformCount(); i1++) {
                    int n1 = currentAnimation.getTransformSkeletonLabels()[i1];
                    int opcode = currentList.getTransformTypes()[n1];
                    int[] skin = currentList.getLabels()[n1];
                    int x = currentAnimation.getTransformXs()[i1];
                    int y = currentAnimation.getTransformYs()[i1];
                    int z = currentAnimation.getTransformZs()[i1];
                    boolean found = false;
                    label0:
                    for (int i2 = 0; i2 < nextAnimation.getTransformCount(); i2++) {
                        int n2 = nextAnimation.getTransformSkeletonLabels()[i2];
                        if (nextList.getLabels()[n2].equals(skin)) {
                            //Opcode 3 = Rotation
                            if (opcode != 2) {
                                x += (nextAnimation.getTransformXs()[i2] - x) * cycle / end;
                                y += (nextAnimation.getTransformYs()[i2] - y) * cycle / end;
                                z += (nextAnimation.getTransformZs()[i2] - z) * cycle / end;
                            } else {
                                x &= 0xff;
                                y &= 0xff;
                                z &= 0xff;
                                int dx = nextAnimation.getTransformXs()[i2] - x & 0xff;
                                int dy = nextAnimation.getTransformYs()[i2] - y & 0xff;
                                int dz = nextAnimation.getTransformZs()[i2] - z & 0xff;
                                if (dx >= 128) {
                                    dx -= 256;
                                }
                                if (dy >= 128) {
                                    dy -= 256;
                                }
                                if (dz >= 128) {
                                    dz -= 256;
                                }
                                x = x + dx * cycle / end & 0xff;
                                y = y + dy * cycle / end & 0xff;
                                z = z + dz * cycle / end & 0xff;
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        if (opcode != 3 && opcode != 2) {
                            x = x * (end - cycle) / end;
                            y = y * (end - cycle) / end;
                            z = z * (end - cycle) / end;
                        } else if (opcode == 3) {
                            x = (x * (end - cycle) + (cycle << 7)) / end;
                            y = (y * (end - cycle) + (cycle << 7)) / end;
                            z = (z * (end - cycle) + (cycle << 7)) / end;
                        } else {
                            x &= 0xff;
                            y &= 0xff;
                            z &= 0xff;
                            int dx = -x & 0xff;
                            int dy = -y & 0xff;
                            int dz = -z & 0xff;
                            if (dx >= 128) {
                                dx -= 256;
                            }
                            if (dy >= 128) {
                                dy -= 256;
                            }
                            if (dz >= 128) {
                                dz -= 256;
                            }
                            x = x + dx * cycle / end & 0xff;
                            y = y + dy * cycle / end & 0xff;
                            z = z + dz * cycle / end & 0xff;
                        }
                    }
                    transform(opcode, skin, x, y, z);
                }
            }
        }
    }

    private void transform(int animationType, int[] skin, int x, int y, int z) {

        int i1 = skin.length;
        if (animationType == 0) {
            int j1 = 0;
            Model_transformTempX = 0;
            Model_transformTempY = 0;
            Model_transformTempZ = 0;
            for (int l3 : skin) {
                if (l3 < vertexLabels.length) {
                    int[] ai5 = vertexLabels[l3];
                    for (int j6 : ai5) {
                        Model_transformTempX += verticesX[j6];
                        Model_transformTempY += verticesY[j6];
                        Model_transformTempZ += verticesZ[j6];
                        j1++;
                    }

                }
            }

            if (j1 > 0) {
                Model_transformTempX = Model_transformTempX / j1 + x;
                Model_transformTempY = Model_transformTempY / j1 + y;
                Model_transformTempZ = Model_transformTempZ / j1 + z;
                return;
            } else {
                Model_transformTempX = x;
                Model_transformTempY = y;
                Model_transformTempZ = z;
                return;
            }
        }
        if (animationType == 1) {
            for (int l2 : skin) {
                if (l2 < vertexLabels.length) {
                    int[] ai1 = vertexLabels[l2];
                    for (int j5 : ai1) {
                        verticesX[j5] += x;
                        verticesY[j5] += y;
                        verticesZ[j5] += z;
                    }
                }
            }
            return;
        }
        if (animationType == 2) {
            for (int i3 : skin) {
                if (i3 < vertexLabels.length) {
                    int[] ai2 = vertexLabels[i3];
                    for (int k5 : ai2) {
                        verticesX[k5] -= Model_transformTempX;
                        verticesY[k5] -= Model_transformTempY;
                        verticesZ[k5] -= Model_transformTempZ;
                        int k6 = (x & 0xff) * 8;
                        int l6 = (y & 0xff) * 8;
                        int i7 = (z & 0xff) * 8;
                        if (i7 != 0) {
                            int j7 = SINE[i7];
                            int i8 = COSINE[i7];
                            int l8 = verticesY[k5] * j7 + verticesX[k5] * i8 >> 16;
                            verticesY[k5] = verticesY[k5] * i8 - verticesX[k5] * j7 >> 16;
                            verticesX[k5] = l8;
                        }
                        if (k6 != 0) {
                            int k7 = SINE[k6];
                            int j8 = COSINE[k6];
                            int i9 = verticesY[k5] * j8 - verticesZ[k5] * k7 >> 16;
                            verticesZ[k5] = verticesY[k5] * k7 + verticesZ[k5] * j8 >> 16;
                            verticesY[k5] = i9;
                        }
                        if (l6 != 0) {
                            int l7 = SINE[l6];
                            int k8 = COSINE[l6];
                            int j9 = verticesZ[k5] * l7 + verticesX[k5] * k8 >> 16;
                            verticesZ[k5] = verticesZ[k5] * k8 - verticesX[k5] * l7 >> 16;
                            verticesX[k5] = j9;
                        }
                        verticesX[k5] += Model_transformTempX;
                        verticesY[k5] += Model_transformTempY;
                        verticesZ[k5] += Model_transformTempZ;
                    }

                }
            }

            return;
        }
        if (animationType == 3) {
            for (int j3 : skin) {
                if (j3 < vertexLabels.length) {
                    int[] ai3 = vertexLabels[j3];
                    for (int l5 : ai3) {
                        verticesX[l5] -= Model_transformTempX;
                        verticesY[l5] -= Model_transformTempY;
                        verticesZ[l5] -= Model_transformTempZ;
                        verticesX[l5] = (verticesX[l5] * x) / 128;
                        verticesY[l5] = (verticesY[l5] * y) / 128;
                        verticesZ[l5] = (verticesZ[l5] * z) / 128;
                        verticesX[l5] += Model_transformTempX;
                        verticesY[l5] += Model_transformTempY;
                        verticesZ[l5] += Model_transformTempZ;
                    }

                }
            }

            return;
        }
        if (animationType == 5 && faceLabelsAlpha != null && faceAlphas != null) {
            for (int jointGroup : skin) {
                if (jointGroup < faceLabelsAlpha.length) {
                    int[] ai4 = faceLabelsAlpha[jointGroup];
                    for (int l4 = 0; l4 < ai4.length; l4++) {
                        int var13 = ai4[l4]; // L: 441
                        int var14 = (this.faceAlphas[var13] & 255) + x * 8; // L: 442
                        if (var14 < 0) {
                            var14 = 0;
                        } else if (var14 > 255) {
                            var14 = 255;
                        }

                        this.faceAlphas[var13] = (byte)var14; // L: 445

                    }
                }
            }

        }
    }

    public void applyTransform(int frameId) {
        if (vertexLabels == null)
            return;
        if (frameId == -1)
            return;
       Frame animationFrame = RSFrame317.getFrames(frameId);
        if (animationFrame == null)
            return;
        FrameBase class18 = animationFrame.getSkeleton();
        Model_transformTempX = 0;
        Model_transformTempY = 0;
        Model_transformTempZ = 0;
        for (int k = 0; k < animationFrame.getTransformCount(); k++) {
            int l = animationFrame.getTransformSkeletonLabels()[k];
            transform(class18.getTransformTypes()[l], class18.getLabels()[l],
                    animationFrame.getTransformXs()[k], animationFrame.getTransformYs()[k],
                    animationFrame.getTransformZs()[k]);
        }

    }

    public void applyAnimationFrames(int[] ai, int j, int k) {
        if (k == -1)
            return;
        if (ai == null || j == -1) {
            applyTransform(k);
            return;
        }
        Frame class36 = RSFrame317.getFrames(k);
        if (class36 == null)
            return;
        Frame class36_1 = RSFrame317.getFrames(j);
        if (class36_1 == null) {
            applyTransform(k);
            return;
        }
        FrameBase class18 = class36.getSkeleton();
        Model_transformTempX = 0;
        Model_transformTempY = 0;
        Model_transformTempZ = 0;
        int l = 0;
        int i1 = ai[l++];
        for (int j1 = 0; j1 < class36.getTransformCount(); j1++) {
            int k1;
            for (k1 = class36.getTransformSkeletonLabels()[j1]; k1 > i1; i1 = ai[l++])
                ;
            if (k1 != i1 || class18.getTransformTypes()[k1] == 0)
                transform(class18.getTransformTypes()[k1], class18.getLabels()[k1], class36.getTransformXs()[j1], class36.getTransformYs()[j1], class36.getTransformZs()[j1]);
        }

        Model_transformTempX = 0;
        Model_transformTempY = 0;
        Model_transformTempZ = 0;
        l = 0;
        i1 = ai[l++];
        for (int l1 = 0; l1 < class36_1.getTransformCount(); l1++) {
            int i2;
            for (i2 = class36_1.getTransformSkeletonLabels()[l1]; i2 > i1; i1 = ai[l++])
                ;
            if (i2 == i1 || class18.getTransformTypes()[i2] == 0)
                transform(class18.getTransformTypes()[i2], class18.getLabels()[i2], class36_1.getTransformXs()[l1], class36_1.getTransformYs()[l1], class36_1.getTransformZs()[l1]);
        }
    }

    public void rotate90Degrees() {
        for (int point = 0; point < verticesCount; point++) {
            int k = verticesX[point];
            verticesX[point] = verticesZ[point];
            verticesZ[point] = -k;
        }
        resetBounds();
    }

    public void leanOverX(int i) {
        int k = SINE[i];
        int l = COSINE[i];
        for (int point = 0; point < verticesCount; point++) {
            int j1 = verticesY[point] * l - verticesZ[point] * k >> 16;
            verticesZ[point] = verticesY[point] * k + verticesZ[point] * l >> 16;
            verticesY[point] = j1;
        }
        resetBounds();
    }

    public void translate(int x, int y, int z) {
        for (int point = 0; point < verticesCount; point++) {
            verticesX[point] += x;
            verticesY[point] += y;
            verticesZ[point] += z;
        }
        resetBounds();
    }

    public void recolor(int found, int replace) {
        if (triangleColours != null)
            for (int face = 0; face < indicesCount; face++)
                if (triangleColours[face] == (short) found)
                    triangleColours[face] = (short) replace;
    }

    public Set<Integer> getColors() {
        Set<Integer> colors = new HashSet<>();
        if (triangleColours != null)
            for (int face = 0; face < indicesCount; face++)
                colors.add((int) triangleColours[face]);
        return colors;
    }

    public void retexture(short found, short replace) {
        if (faceTextures != null)
            for (int face = 0; face < indicesCount; face++)
                if (faceTextures[face] == found)
                    faceTextures[face] = replace;
    }

    public void method477() {
        for (int index = 0; index < verticesCount; index++)
            verticesZ[index] = -verticesZ[index];

        for (int face = 0; face < indicesCount; face++) {
            int l = indices1[face];
            indices1[face] = indices3[face];
            indices3[face] = l;
        }
    }

    public void scale(int i, int j, int l) {
        for (int index = 0; index < verticesCount; index++) {
            verticesX[index] = (verticesX[index] * i) / 128;
            verticesY[index] = (verticesY[index] * l) / 128;
            verticesZ[index] = (verticesZ[index] * j) / 128;
        }
        resetBounds();
    }

    public void transparency(byte value){
        if(faceAlphas == null){
            faceAlphas = new byte[indicesCount];
        }
        Arrays.fill(faceAlphas, value);
    }

    public void light(int light, int shadow, int k, int l, int i1, boolean flag) {
        //light(light, shadow, k, l, i1, flag, false);
        System.out.println("TODO: Convert light call");
    }

    /*public void light(int i, int j, int k, int l, int i1, boolean flag, boolean player) {
        int j1 = (int) Math.sqrt(k * k + l * l + i1 * i1);
        int k1 = j * j1 >> 8;
        faceColors1 = new int[indicesCount];
        faceColors2 = new int[indicesCount];
        faceColors3 = new int[indicesCount];
        if (super.vertexNormals == null) {
            super.vertexNormals = new VertexNormal[verticesCount];
            for (int index = 0; index < verticesCount; index++)
                super.vertexNormals[index] = new VertexNormal();

        }
        for (int face = 0; face < indicesCount; face++) {
            int j2 = indices1[face];
            int l2 = indices2[face];
            int i3 = indices3[face];
            int j3 = verticesX[l2] - verticesX[j2];
            int k3 = verticesY[l2] - verticesY[j2];
            int l3 = verticesZ[l2] - verticesZ[j2];
            int i4 = verticesX[i3] - verticesX[j2];
            int j4 = verticesY[i3] - verticesY[j2];
            int k4 = verticesZ[i3] - verticesZ[j2];
            int l4 = k3 * k4 - j4 * l3;
            int i5 = l3 * i4 - k4 * j3;
            int j5;
            for (j5 = j3 * j4 - i4 * k3; l4 > 8192 || i5 > 8192 || j5 > 8192 || l4 < -8192 || i5 < -8192 || j5 < -8192; j5 >>= 1) {
                l4 >>= 1;
                i5 >>= 1;
            }
            int k5 = (int) Math.sqrt(l4 * l4 + i5 * i5 + j5 * j5);
            if (k5 <= 0)
                k5 = 1;

            l4 = (l4 * 256) / k5;
            i5 = (i5 * 256) / k5;
            j5 = (j5 * 256) / k5;

            short texture_id;
            int type;
            if (faceDrawType != null)
                type = faceDrawType[face];
            else
                type = 0;

            if (faceTextures == null) {
                texture_id = -1;
            } else {
                texture_id = faceTextures[face];
            }

            if (faceDrawType == null || (faceDrawType[face] & 1) == 0) {
                VertexNormal class33_2 = super.vertexNormals[j2];
                class33_2.x += l4;
                class33_2.y += i5;
                class33_2.z += j5;
                class33_2.magnitude++;
                class33_2 = super.vertexNormals[l2];
                class33_2.x += l4;
                class33_2.y += i5;
                class33_2.z += j5;
                class33_2.magnitude++;
                class33_2 = super.vertexNormals[i3];
                class33_2.x += l4;
                class33_2.y += i5;
                class33_2.z += j5;
                class33_2.magnitude++;
            } else {
                if (texture_id != -1) {
                    type = 2;
                }
                int light = i + (k * l4 + l * i5 + i1 * j5) / (k1 + k1 / 2);
                faceColors1[face] = applyLighting(triangleColours[face], light, type);
            }
        }
        if (flag) {
            doShading(i, k1, k, l, i1, player);
            calculateDistances();
        } else {
            alsoVertexNormals = new VertexNormal[verticesCount];
            for (int point = 0; point < verticesCount; point++) {
                VertexNormal class33 = super.vertexNormals[point];
                VertexNormal class33_1 = alsoVertexNormals[point] = new VertexNormal();
                class33_1.x = class33.x;
                class33_1.y = class33.y;
                class33_1.z = class33.z;
                class33_1.magnitude = class33.magnitude;
            }
            calculateVertexData(21073);
        }
    }*/

    public final void method482(int j, int k, int l, int i1, int j1, int k1) {
        int l1 = Rasterizer3D.originViewX;
        int i2 = Rasterizer3D.originViewY;
        int l2 = SINE[j];
        int i3 = COSINE[j];
        int j3 = SINE[k];
        int k3 = COSINE[k];
        int l3 = SINE[l];
        int i4 = COSINE[l];
        int j4 = j1 * l3 + k1 * i4 >> 16;
        for (int k4 = 0; k4 < verticesCount; k4++) {
            int l4 = verticesX[k4];
            int i5 = verticesY[k4];
            int j5 = verticesZ[k4];
            if (k != 0) {
                int k5 = i5 * j3 + l4 * k3 >> 16;
                i5 = i5 * k3 - l4 * j3 >> 16;
                l4 = k5;
            }
            if (j != 0) {
                int i6 = j5 * l2 + l4 * i3 >> 16;
                j5 = j5 * i3 - l4 * l2 >> 16;
                l4 = i6;
            }
            l4 += i1;
            i5 += j1;
            j5 += k1;
            int j6 = i5 * i4 - j5 * l3 >> 16;
            j5 = i5 * l3 + j5 * i4 >> 16;
            i5 = j6;
            projected_vertex_z[k4] = j5 - j4;
            projected_vertex_z_abs[k4] = 0;
            projected_vertex_x[k4] = l1 + (l4 << 9) / j5;
            projected_vertex_y[k4] = i2 + (i5 << 9) / j5;
            if (numberOfTexturesFaces > 0) {
                camera_vertex_x[k4] = l4;
                camera_vertex_y[k4] = i5;
                camera_vertex_z[k4] = j5;
            }
        }

        try {
            draw0(false, false, false, 0L);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public final void renderDraw(int viewOrientation, int yCameraSine, int yCameraCosine, int xCurveSine, int xCurveCosine,
                                 int x, int y, int z, long uid) {
        if (boundsType != 1) {
            calculateBoundsCylinder();
        }
        offX = x + Client.instance.cameraX;
        offY = y + Client.instance.cameraY;
        offZ = z + Client.instance.cameraZ;
        this.viewOrientation = viewOrientation;
        int j2 = z * xCurveCosine - x * xCurveSine >> 16;
        int k2 = y * yCameraSine + j2 * yCameraCosine >> 16;
        int l2 = xzRadius * yCameraCosine >> 16;
        int i3 = k2 + l2;
        if (i3 <= 50 || k2 >= MODEL_DRAW_DISTANCE)
            return;

        int j3 = z * xCurveSine + x * xCurveCosine >> 16;
        int k3 = j3 - xzRadius << SceneGraph.viewDistance;
        if (k3 / i3 >= Rasterizer2D.viewportCenterX)
            return;

        int l3 = j3 + xzRadius << SceneGraph.viewDistance;
        if (l3 / i3 <= -Rasterizer2D.viewportCenterX)
            return;

        int i4 = y * yCameraCosine - j2 * yCameraSine >> 16;
        int j4 = xzRadius * yCameraSine >> 16;
        int k4 = i4 + j4 << SceneGraph.viewDistance;
        if (k4 / i3 <= -Rasterizer2D.viewportCenterY)
            return;

        int l4 = j4 + (super.height * yCameraCosine >> 16);
        int i5 = i4 - l4 << SceneGraph.viewDistance;
        if (i5 / i3 >= Rasterizer2D.viewportCenterY)
            return;

        int j5 = l2 + (super.height * yCameraSine >> 16);
        boolean flag = false;
        if (k2 - j5 <= 50)
            flag = true;

        boolean flag1 = false;
        if (uid > 0 && aBoolean1684) {
            int k5 = k2 - l2;
            if (k5 <= 50)
                k5 = 50;
            if (j3 > 0) {
                k3 /= i3;
                l3 /= k5;
            } else {
                l3 /= i3;
                k3 /= k5;
            }
            if (i4 > 0) {
                i5 /= i3;
                k4 /= k5;
            } else {
                k4 /= i3;
                i5 /= k5;
            }
            int i6 = lastMouseX - Rasterizer3D.originViewX;
            int k6 = lastMouseY - Rasterizer3D.originViewY;
            if (i6 > k3 && i6 < l3 && k6 > i5 && k6 < k4)
                if (isSingleTile) {
                    objectUIDs[objectCount++] = uid;
                    ViewportMouse.method2081(uid);
                } else
                    flag1 = true;
        }
        int l5 = Rasterizer3D.originViewX;
        int j6 = Rasterizer3D.originViewY;
        int l6 = 0;
        int i7 = 0;
        if (viewOrientation != 0) {
            l6 = SINE[viewOrientation];
            i7 = COSINE[viewOrientation];
        }
        for (int j7 = 0; j7 < verticesCount; j7++) {
            int k7 = verticesX[j7];
            int l7 = verticesY[j7];
            int i8 = verticesZ[j7];
            if (viewOrientation != 0) {
                int j8 = i8 * l6 + k7 * i7 >> 16;
                i8 = i8 * i7 - k7 * l6 >> 16;
                k7 = j8;
            }
            k7 += x;
            l7 += y;
            i8 += z;
            int position = i8 * xCurveSine + k7 * xCurveCosine >> 16;
            i8 = i8 * xCurveCosine - k7 * xCurveSine >> 16;
            k7 = position;

            position = l7 * yCameraCosine - i8 * yCameraSine >> 16;
            i8 = l7 * yCameraSine + i8 * yCameraCosine >> 16;
            l7 = position;

            projected_vertex_z[j7] = i8 - k2;
            projected_vertex_z_abs[j7] = i8;
            if (i8 >= 50) {
                projected_vertex_x[j7] = l5 + (k7 << SceneGraph.viewDistance) / i8;
                projected_vertex_y[j7] = j6 + (l7 << SceneGraph.viewDistance) / i8;
            } else {
                projected_vertex_x[j7] = -5000;
                flag = true;
            }
            if (flag || numberOfTexturesFaces > 0) {
                camera_vertex_x[j7] = k7;
                camera_vertex_y[j7] = l7;
                camera_vertex_z[j7] = i8;
            }
        }
        try {
            draw0(flag, flag1, isSingleTile, uid);
        } catch (Exception ignored) { }
    }

    private void draw0(boolean var1, boolean var2, boolean var3, long var4) {
        if (this.diameter < 6000) {
            int var6;
            for(var6 = 0; var6 < this.diameter; ++var6) {
                depthListIndices[var6] = 0;
            }

            var6 = var3 ? 20 : 5;

            int var7;
            int var8;
            int var9;
            int var10;
            int var11;
            int var12;
            int var15;
            int var16;
            int var18;
            int var28;
            for(var7 = 0; var7 < this.indicesCount; var7 = (char)(var7 + 1)) {
                if (this.faceColors3[var7] != -2) {
                    var8 = this.indices1[var7];
                    var9 = this.indices2[var7];
                    var10 = this.indices3[var7];
                    var11 = projected_vertex_x[var8];
                    var12 = projected_vertex_x[var9];
                    var28 = projected_vertex_x[var10];
                    int var29;
                    if (!var1 || var11 != -5000 && var12 != -5000 && var28 != -5000) {
                        if (var2 && ViewportMouse.method5164(projected_vertex_y[var8], projected_vertex_y[var9], projected_vertex_y[var10], var11, var12, var28, var6)) {
                            objectUIDs[objectCount++] = var4;

                            ViewportMouse.method2081(var4);
                            var2 = false;
                        }

                        if ((var11 - var12) * (projected_vertex_y[var10] - projected_vertex_y[var9]) - (var28 - var12) * (projected_vertex_y[var8] - projected_vertex_y[var9]) > 0) {
                            indicesOutOfReach[var7] = false;
                            if (var11 >= 0 && var12 >= 0 && var28 >= 0 && var11 <= Rasterizer3D.lastX && var12 <= Rasterizer3D.lastX && var28 <= Rasterizer3D.lastX) {
                                indicesOutOfBounds[var7] = false;
                            } else {
                                indicesOutOfBounds[var7] = true;
                            }

                            var29 = (projected_vertex_z[var8] + projected_vertex_z[var9] + projected_vertex_z[var10]) / 3 + this.radius;
                            faceLists[var29][depthListIndices[var29]++] = (char)var7;
                        }
                    } else {
                        var29 = camera_vertex_x[var8];
                        var15 = camera_vertex_x[var9];
                        var16 = camera_vertex_x[var10];
                        int var30 = camera_vertex_y[var8];
                        var18 = camera_vertex_y[var9];
                        int var19 = camera_vertex_y[var10];
                        int var20 = camera_vertex_z[var8];
                        int var21 = camera_vertex_z[var9];
                        int var22 = camera_vertex_z[var10];
                        var29 -= var15;
                        var16 -= var15;
                        var30 -= var18;
                        var19 -= var18;
                        var20 -= var21;
                        var22 -= var21;
                        int var23 = var30 * var22 - var20 * var19;
                        int var24 = var20 * var16 - var29 * var22;
                        int var25 = var29 * var19 - var30 * var16;
                        if (var15 * var23 + var18 * var24 + var21 * var25 > 0) {
                            indicesOutOfReach[var7] = true;
                            int var26 = (projected_vertex_z[var8] + projected_vertex_z[var9] + projected_vertex_z[var10]) / 3 + this.radius;
                            faceLists[var26][depthListIndices[var26]++] = (char)var7;
                        }
                    }
                }
            }

            char[] var27;
            char var31;
            if (this.faceRenderPriorities == null) {
                for(var7 = this.diameter - 1; var7 >= 0; --var7) {
                    var31 = depthListIndices[var7];
                    if (var31 > 0) {
                        var27 = faceLists[var7];

                        for(var10 = 0; var10 < var31; ++var10) {
                            this.drawFace(var27[var10]);
                        }
                    }
                }

            } else {
                for(var7 = 0; var7 < 12; ++var7) {
                    faceRenderPriorityListIndices[var7] = 0;
                    skippedRenderPriorityDepths[var7] = 0;
                }

                for(var7 = this.diameter - 1; var7 >= 0; --var7) {
                    var31 = depthListIndices[var7];
                    if (var31 > 0) {
                        var27 = faceLists[var7];

                        for(var10 = 0; var10 < var31; ++var10) {
                            char var32 = var27[var10];
                            byte var33 = this.faceRenderPriorities[var32];
                            var28 = faceRenderPriorityListIndices[var33]++;
                            faceRenderPriorityLists[var33][var28] = var32;
                            if (var33 < 10) {
                                int[] var34 = skippedRenderPriorityDepths;
                                var34[var33] += var7;
                            } else if (var33 == 10) {
                                mediumPriorityFaceLists[var28] = var7;
                            } else {
                                highPriorityFaceLists[var28] = var7;
                            }
                        }
                    }
                }

                var7 = 0;
                if (faceRenderPriorityListIndices[1] > 0 || faceRenderPriorityListIndices[2] > 0) {
                    var7 = (skippedRenderPriorityDepths[1] + skippedRenderPriorityDepths[2]) / (faceRenderPriorityListIndices[1] + faceRenderPriorityListIndices[2]);
                }

                var8 = 0;
                if (faceRenderPriorityListIndices[3] > 0 || faceRenderPriorityListIndices[4] > 0) {
                    var8 = (skippedRenderPriorityDepths[3] + skippedRenderPriorityDepths[4]) / (faceRenderPriorityListIndices[3] + faceRenderPriorityListIndices[4]);
                }

                var9 = 0;
                if (faceRenderPriorityListIndices[6] > 0 || faceRenderPriorityListIndices[8] > 0) {
                    var9 = (skippedRenderPriorityDepths[8] + skippedRenderPriorityDepths[6]) / (faceRenderPriorityListIndices[8] + faceRenderPriorityListIndices[6]);
                }

                var11 = 0;
                var12 = faceRenderPriorityListIndices[10];
                int[] var13 = faceRenderPriorityLists[10];
                int[] var14 = mediumPriorityFaceLists;
                if (var11 == var12) {
                    var11 = 0;
                    var12 = faceRenderPriorityListIndices[11];
                    var13 = faceRenderPriorityLists[11];
                    var14 = highPriorityFaceLists;
                }

                if (var11 < var12) {
                    var10 = var14[var11];
                } else {
                    var10 = -1000;
                }

                for(var15 = 0; var15 < 10; ++var15) {
                    while(var15 == 0 && var10 > var7) {
                        this.drawFace(var13[var11++]);
                        if (var11 == var12 && var13 != faceRenderPriorityLists[11]) {
                            var11 = 0;
                            var12 = faceRenderPriorityListIndices[11];
                            var13 = faceRenderPriorityLists[11];
                            var14 = highPriorityFaceLists;
                        }

                        if (var11 < var12) {
                            var10 = var14[var11];
                        } else {
                            var10 = -1000;
                        }
                    }

                    while(var15 == 3 && var10 > var8) {
                        this.drawFace(var13[var11++]);
                        if (var11 == var12 && var13 != faceRenderPriorityLists[11]) {
                            var11 = 0;
                            var12 = faceRenderPriorityListIndices[11];
                            var13 = faceRenderPriorityLists[11];
                            var14 = highPriorityFaceLists;
                        }

                        if (var11 < var12) {
                            var10 = var14[var11];
                        } else {
                            var10 = -1000;
                        }
                    }

                    while(var15 == 5 && var10 > var9) {
                        this.drawFace(var13[var11++]);
                        if (var11 == var12 && var13 != faceRenderPriorityLists[11]) {
                            var11 = 0;
                            var12 = faceRenderPriorityListIndices[11];
                            var13 = faceRenderPriorityLists[11];
                            var14 = highPriorityFaceLists;
                        }

                        if (var11 < var12) {
                            var10 = var14[var11];
                        } else {
                            var10 = -1000;
                        }
                    }

                    var16 = faceRenderPriorityListIndices[var15];
                    int[] var17 = faceRenderPriorityLists[var15];

                    for(var18 = 0; var18 < var16; ++var18) {
                        this.drawFace(var17[var18]);
                    }
                }

                while(var10 != -1000) {
                    this.drawFace(var13[var11++]);
                    if (var11 == var12 && var13 != faceRenderPriorityLists[11]) {
                        var11 = 0;
                        var13 = faceRenderPriorityLists[11];
                        var12 = faceRenderPriorityListIndices[11];
                        var14 = highPriorityFaceLists;
                    }

                    if (var11 < var12) {
                        var10 = var14[var11];
                    } else {
                        var10 = -1000;
                    }
                }

            }
        }

        createParticles();
    }

    private void createParticles() {

        if(!Configuration.enableParticles)
            return;

        /*
         * Particles
         */
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int pid = verticesParticle[vertex] - 1;

            if (pid >= 0) {
                ParticleDefinition def = ParticleDefinition.cache[pid];

                int particleX = verticesX[vertex];
                int particleY = verticesY[vertex];
                int particleZ = verticesZ[vertex];
                int particleDepth = projected_vertex_z_abs[vertex];

                if (viewOrientation != 0) {
                    int sine = Model.SINE[viewOrientation];
                    int cosine = Model.COSINE[viewOrientation];
                    int rotatedX = particleZ * sine + particleX * cosine >> 16;
                    particleZ = particleZ * cosine - particleX * sine >> 16;
                    particleX = rotatedX;
                }

                particleX += offX;
                particleZ += offZ;

                Vector basePos = new Vector(particleX, -particleY, particleZ);
                for (int p = 0; p < def.getSpawnRate(); p++) {
                    Particle particle = new Particle(def, basePos, particleDepth, pid);
                    ClientCompanion.particleSystem.add(particle);
                }
            }
        }
    }

    /**
     * Obfuscated name = method484
     *
     * @param var1 the triangle to draw
     */
    private void drawFace(int var1) {
        if (indicesOutOfReach[var1]) {
            this.drawOutOfBoundsTriangle(var1);
        } else {
            int var2 = this.indices1[var1];
            int var3 = this.indices2[var1];
            int var4 = this.indices3[var1];
            Rasterizer3D.triangleIsOutOfBounds = indicesOutOfBounds[var1];
            if (this.faceAlphas == null) {
                Rasterizer3D.alpha = 0;
            } else {
                Rasterizer3D.alpha = this.faceAlphas[var1] & 255;
            }

            this.method4596(var1, projected_vertex_y[var2], projected_vertex_y[var3], projected_vertex_y[var4], projected_vertex_x[var2], projected_vertex_x[var3], projected_vertex_x[var4], this.faceColors1[var1], this.faceColors2[var1], this.faceColors3[var1]);
        }
    }

    /**
     * Obfuscated name = method485
     *
     * @param var1 the index of the triangle (face) to render.
     */
    private void drawOutOfBoundsTriangle(int var1) {
        int var2 = Rasterizer3D.originViewX;
        int var3 = Rasterizer3D.originViewY;
        int var4 = 0;
        int var5 = indices1[var1];
        int var6 = indices2[var1];
        int var7 = indices3[var1];
        int var8 = camera_vertex_z[var5];
        int var9 = camera_vertex_z[var6];
        int var10 = camera_vertex_z[var7];
        if (faceAlphas == null)
            Rasterizer3D.alpha = 0;
        else {
            Rasterizer3D.alpha = faceAlphas[var1] & 255;
        }

        int var11;
        int var12;
        int var13;
        int var14;
        if (var8 >= 50) {
            out_of_bounds_projected_vertex_x[var4] = projected_vertex_x[var5];
            out_of_bounds_projected_vertex_y[var4] = projected_vertex_y[var5];
            out_of_bounds_face_colors[var4++] = this.faceColors1[var1];
        } else {
            var11 = camera_vertex_x[var5];
            var12 = camera_vertex_y[var5];
            var13 = this.faceColors1[var1];
            if (var10 >= 50) {
                var14 = modelIntArray4[var10 - var8] * (50 - var8);
                out_of_bounds_projected_vertex_x[var4] = var2 + (var11 + ((camera_vertex_x[var7] - var11) * var14 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_projected_vertex_y[var4] = var3 + (var12 + ((camera_vertex_y[var7] - var12) * var14 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_face_colors[var4++] = var13 + ((this.faceColors3[var1] - var13) * var14 >> 16);
            }

            if (var9 >= 50) {
                var14 = modelIntArray4[var9 - var8] * (50 - var8);
                out_of_bounds_projected_vertex_x[var4] = var2 + (var11 + ((camera_vertex_x[var6] - var11) * var14 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_projected_vertex_y[var4] = var3 + (var12 + ((camera_vertex_y[var6] - var12) * var14 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_face_colors[var4++] = var13 + ((this.faceColors2[var1] - var13) * var14 >> 16);
            }
        }
        if (var9 >= 50) {
            out_of_bounds_projected_vertex_x[var4] = projected_vertex_x[var6];
            out_of_bounds_projected_vertex_y[var4] = projected_vertex_y[var6];
            out_of_bounds_face_colors[var4++] = faceColors2[var1];
        } else {
            int l2 = camera_vertex_x[var6];
            int l3 = camera_vertex_y[var6];
            int l4 = faceColors2[var1];
            if (var8 >= 50) {
                int i6 = (50 - var9) * modelIntArray4[var8 - var9];
                out_of_bounds_projected_vertex_x[var4] = var2 + (l2 + ((camera_vertex_x[var5] - l2) * i6 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_projected_vertex_y[var4] = var3 + (l3 + ((camera_vertex_y[var5] - l3) * i6 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_face_colors[var4++] = l4 + ((faceColors1[var1] - l4) * i6 >> 16);
            }
            if (var10 >= 50) {
                int j6 = (50 - var9) * modelIntArray4[var10 - var9];
                out_of_bounds_projected_vertex_x[var4] = var2 + (l2 + ((camera_vertex_x[var7] - l2) * j6 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_projected_vertex_y[var4] = var3 + (l3 + ((camera_vertex_y[var7] - l3) * j6 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_face_colors[var4++] = l4 + ((faceColors3[var1] - l4) * j6 >> 16);
            }
        }
        if (var10 >= 50) {
            out_of_bounds_projected_vertex_x[var4] = projected_vertex_x[var7];
            out_of_bounds_projected_vertex_y[var4] = projected_vertex_y[var7];
            out_of_bounds_face_colors[var4++] = this.faceColors3[var1];
        } else {
            var11 = camera_vertex_x[var7];
            var12 = camera_vertex_y[var7];
            var13 = this.faceColors3[var1];
            if (var9 >= 50) {
                var14 = modelIntArray4[var9 - var10] * (50 - var10);
                out_of_bounds_projected_vertex_x[var4] = var2 + (var11 + ((camera_vertex_x[var6] - var11) * var14 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_projected_vertex_y[var4] = var3 + (var12 + ((camera_vertex_y[var6] - var12) * var14 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_face_colors[var4++] = var13 + ((this.faceColors2[var1] - var13) * var14 >> 16);
            }

            if (var8 >= 50) {
                var14 = modelIntArray4[var8 - var10] * (50 - var10);
                out_of_bounds_projected_vertex_x[var4] = var2 + (var11 + ((camera_vertex_x[var5] - var11) * var14 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_projected_vertex_y[var4] = var3 + (var12 + ((camera_vertex_y[var5] - var12) * var14 >> 16) << SceneGraph.viewDistance) / 50;
                out_of_bounds_face_colors[var4++] = var13 + ((this.faceColors1[var1] - var13) * var14 >> 16);
            }
        }
        var11 = out_of_bounds_projected_vertex_x[0];
        var12 = out_of_bounds_projected_vertex_x[1];
        var13 = out_of_bounds_projected_vertex_x[2];
        var14 = out_of_bounds_projected_vertex_y[0];
        int var15 = out_of_bounds_projected_vertex_y[1];
        int var16 = out_of_bounds_projected_vertex_y[2];
        Rasterizer3D.triangleIsOutOfBounds = false;
        if (var4 == 3) {
            if (var11 < 0 || var12 < 0 || var13 < 0 || var11 > Rasterizer3D.lastX || var12 > Rasterizer3D.lastX || var13 > Rasterizer3D.lastX) {
                Rasterizer3D.triangleIsOutOfBounds = true;
            }

            this.method4596(var1, var14, var15, var16, var11, var12, var13, out_of_bounds_face_colors[0], out_of_bounds_face_colors[1], out_of_bounds_face_colors[2]);
        }
        if (var4 == 4) {
            if (var11 < 0 || var12 < 0 || var13 < 0 || var11 > Rasterizer3D.lastX || var12 > Rasterizer3D.lastX || var13 > Rasterizer3D.lastX || out_of_bounds_projected_vertex_x[3] < 0 || out_of_bounds_projected_vertex_x[3] > Rasterizer3D.lastX) {
                Rasterizer3D.triangleIsOutOfBounds = true;
            }

            int var18;
            float z_a, z_b, z_c;
            if (this.faceTextures != null && this.faceTextures[var1] != -1) {
                int var19;
                int var21;
                if (this.texture_coordinates != null && this.texture_coordinates[var1] != -1) {
                    int var20 = this.texture_coordinates[var1] & 255;
                    var21 = this.textures_face_a[var20];
                    var18 = this.textures_face_b[var20];
                    var19 = this.textures_face_c[var20];
                } else {
                    var21 = var5;
                    var18 = var6;
                    var19 = var7;
                }

                z_a = projected_vertex_z_abs[var21];
                z_b = projected_vertex_z_abs[var18];
                z_c = projected_vertex_z_abs[var19];

                short var22 = this.faceTextures[var1];
                if (this.faceColors3[var1] == -1) {
                    Rasterizer3D.method4352(z_a, z_b, z_c, var14, var15, var16, var11, var12, var13, this.faceColors1[var1], this.faceColors1[var1], this.faceColors1[var1], camera_vertex_x[var21], camera_vertex_x[var18], camera_vertex_x[var19], camera_vertex_y[var21], camera_vertex_y[var18], camera_vertex_y[var19], camera_vertex_z[var21], camera_vertex_z[var18], camera_vertex_z[var19], var22);
                    Rasterizer3D.method4352(z_a, z_b, z_c, var14, var16, out_of_bounds_projected_vertex_y[3], var11, var13, out_of_bounds_projected_vertex_x[3], this.faceColors1[var1], this.faceColors1[var1], this.faceColors1[var1], camera_vertex_x[var21], camera_vertex_x[var18], camera_vertex_x[var19], camera_vertex_y[var21], camera_vertex_y[var18], camera_vertex_y[var19], camera_vertex_z[var21], camera_vertex_z[var18], camera_vertex_z[var19], var22);
                } else {
                    Rasterizer3D.method4352(z_a, z_b, z_c, var14, var15, var16, var11, var12, var13, out_of_bounds_face_colors[0], out_of_bounds_face_colors[1], out_of_bounds_face_colors[2], camera_vertex_x[var21], camera_vertex_x[var18], camera_vertex_x[var19], camera_vertex_y[var21], camera_vertex_y[var18], camera_vertex_y[var19], camera_vertex_z[var21], camera_vertex_z[var18], camera_vertex_z[var19], var22);
                    Rasterizer3D.method4352(z_a, z_b, z_c, var14, var16, out_of_bounds_projected_vertex_y[3], var11, var13, out_of_bounds_projected_vertex_x[3], out_of_bounds_face_colors[0], out_of_bounds_face_colors[2], out_of_bounds_face_colors[3], camera_vertex_x[var21], camera_vertex_x[var18], camera_vertex_x[var19], camera_vertex_y[var21], camera_vertex_y[var18], camera_vertex_y[var19], camera_vertex_z[var21], camera_vertex_z[var18], camera_vertex_z[var19], var22);
                }
            } else {
                z_a = projected_vertex_z_abs[var5];
                z_b = projected_vertex_z_abs[var6];
                z_c = projected_vertex_z_abs[var7];
                boolean var17 = this.method4599(var1);
                if (this.faceColors3[var1] == -1 && var17) {
                    /*var18 = field2717[this.faceColors1[var1]];
                    Rasterizer3D.method4300(var14, var15, var16, var11, var12, var13, var18, this.overrideHue, this.overrideSaturation, this.overrideLuminance, this.overrideAmount);
                    Rasterizer3D.method4300(var14, var16, out_of_bounds_projected_vertex_y[3], var11, var13, out_of_bounds_projected_vertex_x[3], var18, this.overrideHue, this.overrideSaturation, this.overrideLuminance, this.overrideAmount);*/
                } else if (this.faceColors3[var1] == -1) {
                    var18 = hsl_to_rgb[this.faceColors1[var1]];
                    Rasterizer3D.method4269(z_a, z_b, z_c, var14, var15, var16, var11, var12, var13, var18);
                    Rasterizer3D.method4269(z_a, z_b, z_c, var14, var16, out_of_bounds_projected_vertex_y[3], var11, var13, out_of_bounds_projected_vertex_x[3], var18);
                } else if (var17) {
                    /*Rasterizer3D.method4285(var14, var15, var16, var11, var12, var13, out_of_bounds_face_colors[0], out_of_bounds_face_colors[1], out_of_bounds_face_colors[2], this.overrideHue, this.overrideLuminance, this.overrideSaturation, this.overrideAmount);
                    Rasterizer3D.method4285(var14, var16, out_of_bounds_projected_vertex_y[3], var11, var13, out_of_bounds_projected_vertex_x[3], out_of_bounds_face_colors[0], out_of_bounds_face_colors[2], out_of_bounds_face_colors[3], this.overrideHue, this.overrideLuminance, this.overrideSaturation, this.overrideAmount);*/
                } else {
                    Rasterizer3D.method4270(z_a, z_b, z_c, var14, var15, var16, var11, var12, var13, out_of_bounds_face_colors[0], out_of_bounds_face_colors[1], out_of_bounds_face_colors[2]);
                    Rasterizer3D.method4270(z_a, z_b, z_c, var14, var16, out_of_bounds_projected_vertex_y[3], var11, var13, out_of_bounds_projected_vertex_x[3], out_of_bounds_face_colors[0], out_of_bounds_face_colors[2], out_of_bounds_face_colors[3]);
                }
            }
        }
    }

    private boolean method4599(int var1) { // TODO
        //return this.overrideAmount > 0 && var1 < this.field2725;
        return false;
    }

    final void method4596(int index, int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int hsl_a, int hsl_b, int hsl_c) {
        int var12;
        int var13;
        int var15;
        if (this.faceTextures != null && this.faceTextures[index] != -1) {
            if (this.texture_coordinates != null && this.texture_coordinates[index] != -1) {
                int var14 = this.texture_coordinates[index] & 255;
                var15 = this.textures_face_a[var14];
                var12 = this.textures_face_b[var14];
                var13 = this.textures_face_c[var14];
            } else {
                var15 = this.indices1[index];
                var12 = this.indices2[index];
                var13 = this.indices3[index];
            }

            float z_a = projected_vertex_z_abs[var15];
            float z_b = projected_vertex_z_abs[var12];
            float z_c = projected_vertex_z_abs[var13];

            if (this.faceColors3[index] == -1) {
                Rasterizer3D.method4352(z_a, z_b, z_c, y_a, y_b, y_c, x_a, x_b, x_c, hsl_a, hsl_a, hsl_a, camera_vertex_x[var15], camera_vertex_x[var12], camera_vertex_x[var13], camera_vertex_y[var15], camera_vertex_y[var12], camera_vertex_y[var13], camera_vertex_z[var15], camera_vertex_z[var12], camera_vertex_z[var13], this.faceTextures[index]);
            } else {
                Rasterizer3D.method4352(z_a, z_b, z_c, y_a, y_b, y_c, x_a, x_b, x_c, hsl_a, hsl_b, hsl_c, camera_vertex_x[var15], camera_vertex_x[var12], camera_vertex_x[var13], camera_vertex_y[var15], camera_vertex_y[var12], camera_vertex_y[var13], camera_vertex_z[var15], camera_vertex_z[var12], camera_vertex_z[var13], this.faceTextures[index]);
            }
        } else {
            var15 = this.indices1[index];
            var12 = this.indices2[index];
            var13 = this.indices3[index];
            float z_a = projected_vertex_z_abs[var15];
            float z_b = projected_vertex_z_abs[var12];
            float z_c = projected_vertex_z_abs[var13];
            boolean var11 = this.method4599(index);
            if (this.faceColors3[index] == -1 && var11) {
                //Rasterizer3D.method4300(var2, var3, var4, var5, var6, var7, hsl_to_rgb[this.faceColors1[var1]], this.overrideHue, this.overrideSaturation, this.overrideLuminance, this.overrideAmount);
            } else if (this.faceColors3[index] == -1) {
                Rasterizer3D.method4269(z_a, z_b, z_c, y_a, y_b, y_c, x_a, x_b, x_c, hsl_to_rgb[this.faceColors1[index]]);
            } else if (var11) {
                //Rasterizer3D.method4285(var2, var3, var4, var5, var6, var7, var8, var9, var10, this.overrideHue, this.overrideSaturation, this.overrideLuminance, this.overrideAmount);
            } else {
                Rasterizer3D.method4270(z_a, z_b, z_c, y_a, y_b, y_c, x_a, x_b, x_c, hsl_a, hsl_b, hsl_c);
            }
        }

    }

    private boolean method486(int i, int j, int k, int l, int i1, int x_a, int x_b, int x_c) {
        if (j < k && j < l && j < i1)
            return false;
        if (j > k && j > l && j > i1)
            return false;
        if (i < x_a && i < x_b && i < x_c)
            return false;
        return i <= x_a || i <= x_b || i <= x_c;
    }

    static Model Model_sharedSequenceModel = new Model();
    static Model Model_sharedSpotAnimationModel = new Model();
    static byte[] Model_sharedSequenceModelFaceAlphas = new byte[1];
    static byte[] Model_sharedSpotAnimationModelFaceAlphas = new byte[1];;

    public Model toSharedSpotAnimationModel(boolean var1) {
        if(!var1 && Model_sharedSpotAnimationModelFaceAlphas.length < this.indicesCount) {
            Model_sharedSpotAnimationModelFaceAlphas = new byte[this.indicesCount + 100];
        }

        return this.buildSharedModel(var1, Model_sharedSpotAnimationModel, Model_sharedSpotAnimationModelFaceAlphas);
    }

    public Model toSharedSequenceModel(boolean var1) {
        if(!var1 && Model_sharedSequenceModelFaceAlphas.length < this.indicesCount) {
            Model_sharedSequenceModelFaceAlphas = new byte[this.indicesCount + 100];
        }

        return this.buildSharedModel(var1, Model_sharedSequenceModel, Model_sharedSequenceModelFaceAlphas);
    }

    Model buildSharedModel(boolean var1, Model var2, byte[] var3) {
        var2.verticesCount = this.verticesCount;
        var2.indicesCount = this.indicesCount;
        var2.numberOfTexturesFaces = this.numberOfTexturesFaces;
        if(var2.verticesX == null || var2.verticesX.length < this.verticesCount) {
            var2.verticesParticle = new int[this.verticesCount + 100];
            var2.verticesX = new int[this.verticesCount + 100];
            var2.verticesY = new int[this.verticesCount + 100];
            var2.verticesZ = new int[this.verticesCount + 100];
        }

        int var4;
        for(var4 = 0; var4 < this.verticesCount; ++var4) {
            var2.verticesParticle[var4] = this.verticesParticle[var4];
            var2.verticesX[var4] = this.verticesX[var4];
            var2.verticesY[var4] = this.verticesY[var4];
            var2.verticesZ[var4] = this.verticesZ[var4];
        }

        if(var1) {
            var2.faceAlphas = this.faceAlphas;
        } else {
            var2.faceAlphas = var3;
            if(this.faceAlphas == null) {
                for(var4 = 0; var4 < this.indicesCount; ++var4) {
                    var2.faceAlphas[var4] = 0;
                }
            } else {
                for(var4 = 0; var4 < this.indicesCount; ++var4) {
                    var2.faceAlphas[var4] = this.faceAlphas[var4];
                }
            }
        }

        var2.indices1 = this.indices1;
        var2.indices2 = this.indices2;
        var2.indices3 = this.indices3;
        var2.faceColors1 = this.faceColors1;
        var2.faceColors2 = this.faceColors2;
        var2.faceColors3 = this.faceColors3;
        var2.faceRenderPriorities = this.faceRenderPriorities;
        var2.texture_coordinates = this.texture_coordinates;
        var2.faceTextures = this.faceTextures;
        var2.face_priority = this.face_priority;
        var2.textures_face_a = this.textures_face_a;
        var2.textures_face_b = this.textures_face_b;
        var2.textures_face_c = this.textures_face_c;
        var2.vertexLabels = this.vertexLabels;
        var2.faceLabelsAlpha = this.faceLabelsAlpha;
        var2.animayaGroups = this.animayaGroups;
        var2.animayaScales = this.animayaScales;
        var2.isSingleTile = this.isSingleTile;
        var2.resetBounds();
        return var2;
    }

    @Deprecated
    public void animate(Frame firstFrame) {
        if (vertexLabels == null)
            return;

        if (firstFrame == null)
            return;
        FrameBase class18 = firstFrame.getSkeleton();
        Model_transformTempX = 0;
        Model_transformTempY = 0;
        Model_transformTempZ = 0;
        for (int k = 0; k < firstFrame.getTransformCount(); k++) {
            int l = firstFrame.getTransformSkeletonLabels()[k];
            transform(class18.getTransformTypes()[l], class18.getLabels()[l],
                    firstFrame.getTransformXs()[k], firstFrame.getTransformYs()[k],
                    firstFrame.getTransformZs()[k]);
        }
    }

    public void animate2(Frame var1, Frame var3, int[] var5) {
        if(var1 != null) {
            if(var5 != null && var3 != null) {
                FrameBase var8 = var1.getSkeleton();
                Model_transformTempX = 0;
                Model_transformTempY = 0;
                Model_transformTempZ = 0;
                byte var9 = 0;
                int var13 = var9 + 1;
                int var10 = var5[var9];

                int var11;
                int var12;
                for(var11 = 0; var11 < var1.getTransformCount(); ++var11) {
                    for(var12 = var1.getTransformSkeletonLabels()[var11]; var12 > var10; var10 = var5[var13++]) {
                    }

                    if(var12 != var10 || var8.getTransformTypes()[var12] == 0) {
                        this.transform(var8.getTransformTypes()[var12], var8.getLabels()[var12], var1.getTransformXs()[var11], var1.getTransformYs()[var11], var1.getTransformZs()[var11]);
                    }
                }

                Model_transformTempX = 0;
                Model_transformTempY = 0;
                Model_transformTempZ = 0;
                var9 = 0;
                var13 = var9 + 1;
                var10 = var5[var9];

                for(var11 = 0; var11 < var3.getTransformCount(); ++var11) {
                    for(var12 = var3.getTransformSkeletonLabels()[var11]; var12 > var10; var10 = var5[var13++]) {
                    }

                    if(var12 == var10 || var8.getTransformTypes()[var12] == 0) {
                        this.transform(var8.getTransformTypes()[var12], var8.getLabels()[var12], var3.getTransformXs()[var11], var3.getTransformYs()[var11], var3.getTransformZs()[var11]);
                    }
                }

//                this.resetBounds();
            } else {
                this.animate(var1);
            }
        }
    }

    int boundsType;
    int xMidOffset;
    int yMidOffset;
    int zMidOffset;

    public int method4625() {
        this.calculateBoundsCylinder();
        return this.xzRadius;
    }

    public void resetBounds() {
        this.boundsType = 0;
        this.xMidOffset = -1;
    }


    public void rotateY90Ccw() {
        for(int var1 = 0; var1 < this.verticesCount; ++var1) {
            int var2 = this.verticesX[var1];
            this.verticesX[var1] = this.verticesZ[var1];
            this.verticesZ[var1] = -var2;
        }

        this.resetBounds();
    }
    public void rotateY180() {
        for(int var1 = 0; var1 < this.verticesCount; ++var1) {
            this.verticesX[var1] = -this.verticesX[var1];
            this.verticesZ[var1] = -this.verticesZ[var1];
        }

        this.resetBounds();
    }
    public void rotateY270Ccw() {
        for(int var1 = 0; var1 < this.verticesCount; ++var1) {
            int var2 = this.verticesZ[var1];
            this.verticesZ[var1] = this.verticesX[var1];
            this.verticesX[var1] = -var2;
        }

        this.resetBounds();
    }
    public void rotateZ(int var1) {
        int var2 = SINE[var1];
        int var3 = COSINE[var1];

        for(int var4 = 0; var4 < this.verticesCount; ++var4) {
            int var5 = var3 * this.verticesY[var4] - var2 * this.verticesZ[var4] >> 16;
            this.verticesZ[var4] = var2 * this.verticesY[var4] + var3 * this.verticesZ[var4] >> 16;
            this.verticesY[var4] = var5;
        }

        this.resetBounds();
    }
    public void offsetBy(int var1, int var2, int var3) {
        for(int var4 = 0; var4 < this.verticesCount; ++var4) {
            this.verticesX[var4] += var1;
            this.verticesY[var4] += var2;
            this.verticesZ[var4] += var3;
        }

        this.resetBounds();
    }
    public void animate(Frames var1, int var2) {
        if(this.vertexLabels != null) {
            if(var2 != -1) {
                Animation var3 = var1.frames[var2];
                Skeleton var4 = var3.skeleton;
                Model_transformTempX = 0;
                Model_transformTempY = 0;
                Model_transformTempZ = 0;

                for(int var5 = 0; var5 < var3.transformCount; ++var5) {
                    int var6 = var3.transformSkeletonLabels[var5];
                    this.transform(var4.getTransformTypes()[var6], var4.getLabels()[var6], var3.transformXs[var5], var3.transformYs[var5], var3.transformZs[var5]);
                }

                this.resetBounds();
            }
        }
    }
    public void animate2(Frames var1, int var2, Frames var3, int var4, int[] var5) {
        if(var2 != -1) {
            if(var5 != null && var4 != -1) {
                Animation var6 = var1.frames[var2];
                Animation var7 = var3.frames[var4];
                Skeleton var8 = var6.skeleton;
                Model_transformTempX = 0;
                Model_transformTempY = 0;
                Model_transformTempZ = 0;
                byte var9 = 0;
                int var13 = var9 + 1;
                int var10 = var5[var9];

                int var11;
                int var12;
                for(var11 = 0; var11 < var6.transformCount; ++var11) {
                    for(var12 = var6.transformSkeletonLabels[var11]; var12 > var10; var10 = var5[var13++]) {
                    }

                    if(var12 != var10 || var8.getTransformTypes()[var12] == 0) {
                        this.transform(var8.getTransformTypes()[var12], var8.getLabels()[var12], var6.transformXs[var11], var6.transformYs[var11], var6.transformZs[var11]);
                    }
                }

                Model_transformTempX = 0;
                Model_transformTempY = 0;
                Model_transformTempZ = 0;
                var9 = 0;
                var13 = var9 + 1;
                var10 = var5[var9];

                for(var11 = 0; var11 < var7.transformCount; ++var11) {
                    for(var12 = var7.transformSkeletonLabels[var11]; var12 > var10; var10 = var5[var13++]) {
                    }

                    if(var12 == var10 || var8.getTransformTypes()[var12] == 0) {
                        this.transform(var8.getTransformTypes()[var12], var8.getLabels()[var12], var7.transformXs[var11], var7.transformYs[var11], var7.transformZs[var11]);
                    }
                }

                this.resetBounds();
            } else {
                this.animate(var1, var2);
            }
        }
    }

    public void method4617(Frames var1, int var2, int[] var3, boolean var4) {
        if (var3 == null) {
            this.animate(var1, var2);
        } else {
            Animation var5 = var1.frames[var2];
            Skeleton var6 = var5.skeleton;
            int var7 = 0;
            int var8 = var3[var7++];
            Model_transformTempX = 0;
            Model_transformTempY = 0;
            Model_transformTempZ = 0;

            for(int var9 = 0; var9 < var5.transformCount; ++var9) {
                int var10;
                for(var10 = var5.transformSkeletonLabels[var9]; var10 > var8; var8 = var3[var7++]) {
                }

                if (var4) {
                    if (var10 == var8 || var6.getTransformTypes()[var10] == 0) {
                        this.transform(var6.getTransformTypes()[var10], var6.getLabels()[var10], var5.transformXs[var9], var5.transformYs[var9], var5.transformZs[var9]);
                    }
                } else if (var10 != var8 || var6.getTransformTypes()[var10] == 0) {
                    this.transform(var6.getTransformTypes()[var10], var6.getLabels()[var10], var5.transformXs[var9], var5.transformYs[var9], var5.transformZs[var9]);
                }
            }

        }
    }

    public void method4603(class134 var1, int var2) {
        Skeleton var3 = var1.field1570;
        class220 var4 = var3.method4377();
        if (var4 != null) {
            var3.method4377().method4362(var1, var2);
            this.method4605(var3.method4377(), var1.method3058());
        }

        if (var1.method3043()) {
            this.method4604(var1, var2);
        }

        this.resetBounds();
    }

    void method4604(class134 var1, int var2) {
        Skeleton var3 = var1.field1570;

        for(int var4 = 0; var4 < var3.count; ++var4) {
            int var5 = var3.getTransformTypes()[var4];
            if (var5 == 5 && var1.field1568 != null && var1.field1568[var4] != null && var1.field1568[var4][0] != null && this.faceLabelsAlpha != null && this.faceAlphas != null) {
                class127 var6 = var1.field1568[var4][0];
                int[] var7 = var3.getLabels()[var4];
                int var8 = var7.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    int var10 = var7[var9];
                    if (var10 < this.faceLabelsAlpha.length) {
                        int[] var11 = this.faceLabelsAlpha[var10];

                        for(int var12 = 0; var12 < var11.length; ++var12) {
                            int var13 = var11[var12];
                            int var14 = (int)((float)(this.faceAlphas[var13] & 255) + var6.method2974(var2) * 255.0F);
                            if (var14 < 0) {
                                var14 = 0;
                            } else if (var14 > 255) {
                                var14 = 255;
                            }

                            this.faceAlphas[var13] = (byte)var14;
                        }
                    }
                }
            }
        }

    }

    void method4605(class220 var1, int var2) {
        this.method4662(var1, var2);
    }

    void method4662(class220 var1, int var2) {
        if (this.animayaGroups != null) {
            for(int var3 = 0; var3 < this.verticesCount; ++var3) {
                int[] var4 = this.animayaGroups[var3];
                if (var4 != null && var4.length != 0) {
                    int[] var5 = this.animayaScales[var3];
                    field2656.method7877();

                    for(int var6 = 0; var6 < var4.length; ++var6) {
                        int var7 = var4[var6];
                        class124 var8 = var1.method4360(var7);
                        if (var8 != null) {
                            field2660.method7849((float)var5[var6] / 255.0F);
                            field2724.method7850(var8.method2934(var2));
                            field2724.method7851(field2660);
                            field2656.method7885(field2724);
                        }
                    }

                    this.method4624(var3, field2656);
                }
            }

        }
    }

    void method4624(int var1, class421 var2) {
        float var3 = (float)this.verticesX[var1];
        float var4 = (float)(-this.verticesY[var1]);
        float var5 = (float)(-this.verticesZ[var1]);
        float var6 = 1.0F;
        this.verticesX[var1] = (int)(var2.field4608[0] * var3 + var2.field4608[4] * var4 + var2.field4608[8] * var5 + var2.field4608[12] * var6);
        this.verticesY[var1] = -((int)(var2.field4608[1] * var3 + var2.field4608[5] * var4 + var2.field4608[9] * var5 + var2.field4608[13] * var6));
        this.verticesZ[var1] = -((int)(var2.field4608[2] * var3 + var2.field4608[6] * var4 + var2.field4608[10] * var5 + var2.field4608[14] * var6));
    }

    public int[] getTrianglesX() {
        return indices1;
    }
    public int[] getTrianglesY() {
        return indices2;
    }
    public int[] getTrianglesZ() {
        return indices3;
    }
    public int getTrianglesCount(){
        return indicesCount;
    }
    public int[] getVerticesX(){
        return verticesX;
    }
    public int[] getVerticesY(){
        return verticesY;
    }
    public int[] getVerticesZ(){
        return verticesZ;
    }
    public int getVerticesCount(){
        return verticesCount;
    }

    public List<Triangle> getTriangles()
    {
        int[] trianglesX = getTrianglesX();
        int[] trianglesY = getTrianglesY();
        int[] trianglesZ = getTrianglesZ();

        List<Vertex> vertices = getVertices();
        List<Triangle> triangles = new ArrayList<Triangle>(getTrianglesCount());

        for (int i = 0; i < getTrianglesCount(); ++i)
        {
            int triangleX = trianglesX[i];
            int triangleY = trianglesY[i];
            int triangleZ = trianglesZ[i];

            Triangle triangle = new Triangle(
                    vertices.get(triangleX),
                    vertices.get(triangleY),
                    vertices.get(triangleZ)
            );
            triangles.add(triangle);
        }

        return triangles;
    }
    public List<Vertex> getVertices()
    {
        int[] verticesX = getVerticesX();
        int[] verticesY = getVerticesY();
        int[] verticesZ = getVerticesZ();

        List<Vertex> vertices = new ArrayList<Vertex>(getVerticesCount());

        for (int i = 0; i < getVerticesCount(); ++i)
        {
            Vertex v = new Vertex(
                    verticesX[i],
                    verticesY[i],
                    verticesZ[i]
            );
            vertices.add(v);
        }

        return vertices;
    }
    public void calculateBoundsCylinder() {
        if(this.boundsType != 1) {
            this.boundsType = 1;
            super.height = 0;
            this.maximumYVertex = 0;
            this.xzRadius = 0;

            for(int var1 = 0; var1 < this.verticesCount; ++var1) {
                int var2 = this.verticesX[var1];
                int var3 = this.verticesY[var1];
                int var4 = this.verticesZ[var1];
                if(-var3 > super.height) {
                    super.height = -var3;
                }

                if(var3 > this.maximumYVertex) {
                    this.maximumYVertex = var3;
                }

                int var5 = var2 * var2 + var4 * var4;
                if(var5 > this.xzRadius) {
                    this.xzRadius = var5;
                }
            }

            this.xzRadius = (int)(Math.sqrt(this.xzRadius) + 0.99D);
            this.radius = (int)(Math.sqrt(this.xzRadius * this.xzRadius + super.height * super.height) + 0.99D);
            this.diameter = this.radius + (int)(Math.sqrt(this.xzRadius * this.xzRadius + this.maximumYVertex * this.maximumYVertex) + 0.99D);
        }
    }

    public Model contourGround(int[][] var1, int var2, int var3, int var4, boolean var5, int var6) {
        this.calculateBoundsCylinder();
        int var7 = var2 - this.xzRadius;
        int var8 = var2 + this.xzRadius;
        int var9 = var4 - this.xzRadius;
        int var10 = var4 + this.xzRadius;
        if(var7 >= 0 && var8 + 128 >> 7 < var1.length && var9 >= 0 && var10 + 128 >> 7 < var1[0].length) {
            var7 >>= 7;
            var8 = var8 + 127 >> 7;
            var9 >>= 7;
            var10 = var10 + 127 >> 7;
            if(var3 == var1[var7][var9] && var3 == var1[var8][var9] && var3 == var1[var7][var10] && var3 == var1[var8][var10]) {
                return this;
            } else {
                Model var11;
                if(var5) {
                    var11 = new Model();
                    var11.verticesCount = this.verticesCount;
                    var11.indicesCount = this.indicesCount;
                    var11.numberOfTexturesFaces = this.numberOfTexturesFaces;
                    var11.verticesParticle = this.verticesParticle;
                    var11.verticesX = this.verticesX;
                    var11.verticesZ = this.verticesZ;
                    var11.indices1 = this.indices1;
                    var11.indices2 = this.indices2;
                    var11.indices3 = this.indices3;
                    var11.faceColors1 = this.faceColors1;
                    var11.faceColors2 = this.faceColors2;
                    var11.faceColors3 = this.faceColors3;
                    var11.faceRenderPriorities = this.faceRenderPriorities;
                    var11.faceAlphas = this.faceAlphas;
                    var11.texture_coordinates = this.texture_coordinates;
                    var11.faceTextures = this.faceTextures;
                    var11.face_priority = this.face_priority;
                    var11.textures_face_a = this.textures_face_a;
                    var11.textures_face_b = this.textures_face_b;
                    var11.textures_face_c = this.textures_face_c;
                    var11.vertexLabels = this.vertexLabels;
                    var11.faceLabelsAlpha = this.faceLabelsAlpha;
                    var11.isSingleTile = this.isSingleTile;
                    var11.verticesY = new int[var11.verticesCount];
                } else {
                    var11 = this;
                }

                int var12;
                int var13;
                int var14;
                int var15;
                int var16;
                int var17;
                int var18;
                int var19;
                int var20;
                int var21;
                if(var6 == 0) {
                    for(var12 = 0; var12 < var11.verticesCount; ++var12) {
                        var13 = var2 + this.verticesX[var12];
                        var14 = var4 + this.verticesZ[var12];
                        var15 = var13 & 127;
                        var16 = var14 & 127;
                        var17 = var13 >> 7;
                        var18 = var14 >> 7;
                        var19 = var1[var17][var18] * (128 - var15) + var1[var17 + 1][var18] * var15 >> 7;
                        var20 = var1[var17][var18 + 1] * (128 - var15) + var15 * var1[var17 + 1][var18 + 1] >> 7;
                        var21 = var19 * (128 - var16) + var20 * var16 >> 7;
                        var11.verticesY[var12] = var21 + this.verticesY[var12] - var3;
                    }
                } else {
                    for(var12 = 0; var12 < var11.verticesCount; ++var12) {
                        var13 = (-this.verticesY[var12] << 16) / super.height;
                        if(var13 < var6) {
                            var14 = var2 + this.verticesX[var12];
                            var15 = var4 + this.verticesZ[var12];
                            var16 = var14 & 127;
                            var17 = var15 & 127;
                            var18 = var14 >> 7;
                            var19 = var15 >> 7;
                            var20 = var1[var18][var19] * (128 - var16) + var1[var18 + 1][var19] * var16 >> 7;
                            var21 = var1[var18][var19 + 1] * (128 - var16) + var16 * var1[var18 + 1][var19 + 1] >> 7;
                            int var22 = var20 * (128 - var17) + var21 * var17 >> 7;
                            var11.verticesY[var12] = (var6 - var13) * (var22 - var3) / var6 + this.verticesY[var12];
                        }
                    }
                }

                var11.resetBounds();
                return var11;
            }
        } else {
            return this;
        }
    }

    public void method4639(Skeleton var1, class134 var2, int var3, boolean[] var4, boolean var5, boolean var6) {
        class220 var7 = var1.method4377();
        if (var7 != null) {
            var7.method4371(var2, var3, var4, var5);
            if (var6) {
                this.method4605(var7, var2.method3058());
            }
        }

        if (!var5 && var2.method3043()) {
            this.method4604(var2, var3);
        }

    }
}