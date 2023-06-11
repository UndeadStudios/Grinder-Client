package com.grinder.particle;

import com.grinder.Configuration;
import com.grinder.client.ClientCompanion;
import com.grinder.client.util.Log;
import com.runescape.Client;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.model.Model;
import com.runescape.entity.model.particles.Particle;
import com.runescape.entity.model.particles.ParticleDefinition;
import com.runescape.entity.model.particles.Vector;
import com.runescape.scene.SceneGraph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;

/**
 * Represents a system for rendering particle effects.
 *
 * Original author is unknown, edited by Stan van der Bend.
 *
 * @version 1.0
 * @since 13/01/2020
 */
public class ParticleSystem {

    private final static int[] EMPTY_VERTEX_PROJECTION = new int[3];
    private final static int MINIMUM_DISTANCE = 50;
    private final static int MAXIMUM_DISTANCE = 3500;
    private final static float DEPTH_CONSTANT = 15f;

    private final Deque<Particle> particleDeque = new ArrayDeque<>(4000);

    public static void updateParticles(Client client) {

        Iterator<Particle> iterator;
        Particle particle;
        if (Configuration.enableParticles) {

//            long start = System.nanoTime();

            // cheaphax for changing size of particles based on zCameraPos (corresponds to camera zoom but also accounts for vertical camera rotation)
            double zoomModifier = Math.log((SceneGraph.viewDistance == 9 ? 885.0F : 982.0F) / Math.abs(client.cameraY)) / Math.log(10);
            iterator = client.particleAddScreenList.iterator();
            while (iterator.hasNext()) {
                particle = iterator.next();
                if (particle != null) {
                    particle.tick();
                    if (particle.isDead()) {
                        client.particleRemoveScreenList.add(particle);
                    } else {
                        renderParticle(client, particle, (float) zoomModifier);
                    }
                }
            }

//            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
//            if(duration > 0)
//                System.out.println("Particle update took "+ duration+"ms!");
        } else {
            if (!client.particleAddScreenList.isEmpty()) {
                iterator = client.particleAddScreenList.iterator();
                while (iterator.hasNext()) {
                    particle = iterator.next();
                    if (particle != null) {
                        particle.tick();
                        if (particle.isDead()) {
                            client.particleRemoveScreenList.add(particle);
                        }
                    }
                }
            }
        }
        client.particleAddScreenList.removeAll(client.particleRemoveScreenList);
        client.particleRemoveScreenList.clear();
    }

    public static void renderParticle(Client client, Particle particle, float zoomModifier) {
        try {
            ParticleDefinition def = particle.getDefinition();
            int displayX = particle.getPosition().getX();
            int displayY = particle.getPosition().getY();
            int displayZ = particle.getPosition().getZ();
            int width;
            int height;
            if (def.getSprite() == null) {
                width = 8;
                height = 8;
            } else {
                def.getSprite();
                width = Rasterizer2D.Rasterizer2D_width / 4;
                def.getSprite();
                height = Rasterizer2D.Rasterizer2D_height / 4;
            }
            float zoomOffset = particle.getSize() * zoomModifier;
            float size = particle.getSize() + zoomOffset;
            width = (int) (width * size);
            height = (int) (height * size);
            int[] projection = projection(client, displayX, displayY, displayZ, width, height);
            int alpha = (int) (particle.getAlpha() * 255.0F);
            int radius = (int) ((4.0F) * size);
            int srcAlpha = 256 - alpha;
            int srcR = (particle.getColor() >> 16 & 255) * alpha;
            int srcG = (particle.getColor() >> 8 & 255) * alpha;
            int srcB = (particle.getColor() & 255) * alpha;
            int y1 = projection[1] - radius;
            if (y1 < 0) {
                y1 = 0;
            }
            int y2 = projection[1] + radius;
            if (y2 >= Rasterizer2D.Rasterizer2D_height) {
                y2 = Rasterizer2D.Rasterizer2D_height - 1;
            }
            for (int iy = y1; iy <= y2; ++iy) {
                int dy = iy - projection[1];
                int dist = (int) Math.sqrt(radius * radius - dy * dy);
                int x1 = projection[0] - dist;
                if (x1 < 0) {
                    x1 = 0;
                }
                int x2 = projection[0] + dist;
                if (x2 >= Rasterizer2D.Rasterizer2D_width) {
                    x2 = Rasterizer2D.Rasterizer2D_width - 1;
                }
                int pixel = x1 + iy * Rasterizer2D.Rasterizer2D_width;

                if (Rasterizer3D.depthBuffer != null) {

                    if (pixel + 1 >= Rasterizer3D.depthBuffer.length)
                        break;
//                                    System.out.println("depth = "+Rasterizer3D.depthBuffer.length+", pixels = "+ClientCompanion.gameScreenImageProducer.pixels.length);
                    if (Rasterizer3D.depthBuffer[pixel] >= projection[2] - size - 15 || Rasterizer3D.depthBuffer[pixel++] >= projection[2] + size + 15) {
                        for (int ix = x1; ix <= x2; ++ix) {
                            int dstR = (ClientCompanion.gameScreenImageProducer.pixels[pixel] >> 16 & 255) * srcAlpha;
                            int dstG = (ClientCompanion.gameScreenImageProducer.pixels[pixel] >> 8 & 255) * srcAlpha;
                            int dstB = (ClientCompanion.gameScreenImageProducer.pixels[pixel] & 255) * srcAlpha;
                            int rgb = (srcR + dstR >> 8 << 16) + (srcG + dstG >> 8 << 8) + (srcB + dstB >> 8);
                            ClientCompanion.gameScreenImageProducer.pixels[pixel++] = rgb;
                        }
                    }
                }
            }
        } catch(Exception e){
            Log.error("Particle render error", e);
        }
    }

    public static void addParticle(ArrayList<Particle> particleAddScreenList, Particle particle) {
        particleAddScreenList.add(particle);
    }

    public static int[] projection(Client client, int x, int y, int z, int width, int height) {
        if (x < 128 || z < 128 || x > 13056 || z > 13056) {
            return new int[]{0, 0, 0, 0, 0, 0, 0};
        }
        int displayHeight = client.getTileHeight(Client.plane, z, x) - y;
        x -= client.cameraX;
        displayHeight -= client.cameraY;
        z -= client.cameraZ;
        int sinY = Model.SINE[client.cameraPitch];
        int cosY = Model.COSINE[client.cameraPitch];
        int sinX = Model.SINE[client.cameraYaw];
        int cosX = Model.COSINE[client.cameraYaw];
        int j2 = z * sinX + x * cosX >> 16;
        z = z * cosX - x * sinX >> 16;
        x = j2;
        j2 = displayHeight * cosY - z * sinY >> 16;
        z = displayHeight * sinY + z * cosY >> 16;
        displayHeight = j2;
        if (z >= 50 && z <= 3500) {
            return new int[]{Rasterizer3D.originViewX + (x << SceneGraph.viewDistance) / z,
                    Rasterizer3D.originViewY + (displayHeight << SceneGraph.viewDistance) / z, z,
                    Rasterizer3D.originViewX + ((x - (width / 2)) << SceneGraph.viewDistance) / z,
                    Rasterizer3D.originViewY + ((displayHeight - (height / 2)) << SceneGraph.viewDistance) / z,
                    Rasterizer3D.originViewX + ((x + (width / 2)) << SceneGraph.viewDistance) / z,
                    Rasterizer3D.originViewY + ((displayHeight + (height / 2)) << SceneGraph.viewDistance) / z};
        }
        return new int[]{0, 0, 0, 0, 0, 0, 0};
    }

    public void add(Particle particle){
        try {
            particleDeque.add(particle);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void process(){

//        particleDeque.removeIf(particle -> {
//            particle.tick();
//            return particle.isDead();
//        });

    }

    public void enable(){

    }

    public void disable(){
        particleDeque.clear();
    }

    public void render(){

//        long start = System.nanoTime();

        final int[] pixels = ClientCompanion.gameScreenImageProducer.pixels;
        final float[] pixelsDepth = Rasterizer3D.depthBuffer;

        if(pixels == null || pixelsDepth == null)
            return;

        final int minPoint = 0;
        final int maxPoint = pixelsDepth.length-1;

        final double zoomModifier = calculateZoom();

        final int sinX = Model.SINE[Client.instance.cameraYaw];
        final int sinY = Model.SINE[Client.instance.cameraPitch];
        final int cosX = Model.COSINE[Client.instance.cameraYaw];
        final int cosY = Model.COSINE[Client.instance.cameraPitch];

        final int minX = 0;
        final int minY = 0;
        final int maxX = Math.max(0, Rasterizer2D.Rasterizer2D_width -1);
        final int maxY = Math.max(0, Rasterizer2D.Rasterizer2D_height -1);

//        int textY = 200;
//        int yOffset = 20;
//        Client.instance.newBoldFont.drawBasicString("cam("+Client.instance.xCameraCurve+", "+Client.instance.yCameraCurve+", "+Client.instance.zCameraPos+", "+Client.cameraZoom+", "+Client.viewportZoom+"", 20, textY+=yOffset, JagexColors.YELLOW_INTERFACE_TEXT.getRGB());
//        Client.instance.newBoldFont.drawBasicString("zoom = "+zoomModifier, 20, textY+=yOffset, JagexColors.YELLOW_INTERFACE_TEXT.getRGB());
//        Client.instance.newBoldFont.drawBasicString("sin("+sinX+", "+sinY+")", 20, textY+=yOffset, JagexColors.YELLOW_INTERFACE_TEXT.getRGB());
//        Client.instance.newBoldFont.drawBasicString("cos("+cosX+", "+cosY+")", 20, textY+=yOffset, JagexColors.YELLOW_INTERFACE_TEXT.getRGB());
//        Client.instance.newBoldFont.drawBasicString("count("+particleDeque.size()+")", 20, textY+yOffset, JagexColors.YELLOW_INTERFACE_TEXT.getRGB());

        final Iterator<Particle> particleIterator = particleDeque.iterator();

        try {
            while (particleIterator.hasNext()) {

                final Particle particle = particleIterator.next();

                particle.tick();

                if (particle.isDead()) {
                    particleIterator.remove();
                    continue;
                }

                final Vector position = particle.getPosition();
                final float size = particle.getSize();
                final float zoom = (float) (size * zoomModifier);
                final float scaledSize = size + zoom;

                final int[] vertices = vertexProjection(position, sinX, sinY, cosX, cosY);

                final int alpha = (int) (particle.getAlpha() * 255f);
                final int srcAlpha = 256 - alpha;
                final int rgb = particle.getColor();
                final int r = (rgb >> 16 & 255) * alpha;
                final int g = (rgb >> 8 & 255) * alpha;
                final int b = (rgb & 255) * alpha;

                final int radius = (int) (scaledSize * 4F);
                final int centerX = vertices[0];
                final int centerY = vertices[1];
                final int startY = Math.max(minY, centerY - radius);
                final int endY = Math.min(maxY, centerY + radius);

                final double base = radius * radius;

                final int renderHeight = vertices[2];
                final float minDepth = renderHeight - scaledSize - DEPTH_CONSTANT;
                final float maxDepth = renderHeight + scaledSize + DEPTH_CONSTANT;

                for (int y = startY; y <= endY; y++) {

                    final int deltaY = y - centerY;
                    final int distance = (int) Math.sqrt(base - deltaY * deltaY);

                    final int startX = Math.max(minX, centerX - distance);
                    final int endX = Math.min(maxX, centerX + distance);

                    int pixel = startX + (y * Rasterizer2D.Rasterizer2D_width);

                    for (int x = startX; x <= endX; x++) {

                        if (!inBounds(pixel, minPoint, maxPoint))
                            continue;

                        final float depth = pixelsDepth[pixel];

                        if (depth >= minDepth || depth >= maxDepth) {
                            pixels[pixel] = blendRGB(pixels[pixel], r, g, b, srcAlpha);
                        }
                        pixel++;
                    }
                }
            }
//            long duration = System.nanoTime() - start;
//            System.out.println("Particle rendering took "+duration+" ns ("+ TimeUnit.NANOSECONDS.toMillis(duration)+" ms)!");
        } catch (Exception e){
            e.printStackTrace();
        }

    }



    public int[] vertexProjection(Vector position, int sinX, int sinY, int cosX, int cosY){

        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();

        if (x < 128 || z < 128 || x > 13056 || z > 13056)
            return EMPTY_VERTEX_PROJECTION;

        int displayHeight = Client.instance.getTileHeight(Client.plane, z, x);
        displayHeight -= y;
        displayHeight -= Client.instance.cameraY;
        x -= Client.instance.cameraX;
        z -= Client.instance.cameraZ;

        int projection = z * sinX + x * cosX >> 16;
        z = z * cosX - x * sinX >> 16;
        x = projection;
        projection = displayHeight * cosY - z * sinY >> 16;
        z = displayHeight * sinY + z * cosY >> 16;
        displayHeight = projection;

        if (z >= MINIMUM_DISTANCE && z <= Model.MODEL_DRAW_DISTANCE) {
            final int viewDistance = SceneGraph.viewDistance;
            final int viewportX = Rasterizer3D.originViewX;
            final int viewportY = Rasterizer3D.originViewY;
            return new int[]{
                    viewportX + (x << viewDistance) / z,
                    viewportY + (displayHeight << viewDistance) / z,
                    z
            };
        }

        return EMPTY_VERTEX_PROJECTION;
    }

    public int blendRGB(int rgb, int red, int green, int blue, int alpha) {
        int r = (rgb >> 16 & 255) * alpha;
        int g = (rgb >> 8 & 255) * alpha;
        int b = (rgb & 255) * alpha;
        return (((r + red) >> 8) << 16)
                + (((g + green) >> 8) << 8)
                + ((b + blue) >> 8);
    }

    public boolean inBounds(int point, int minPoint, int maxPoint){
        if(point < minPoint || point > maxPoint){
            System.out.println("Particle point["+point+"] is out of depth bounds["+minPoint+", "+maxPoint+"]");
            return false;
        }
        return true;
    }

    /**
     * This method is a cheap-hax for changing the size
     * of particles based on zCameraPos which corresponds to camera zoom
     * but also accounts for vertical camera rotation.
     *
     * @return a double representing the zoom variable of the particles.
     */
    private double calculateZoom(){
        return Math.log((SceneGraph.viewDistance == 9 ? 885.0F : 982.0F)
                / Math.abs(Client.instance.cameraY)
        ) / Math.log(10);
    }
}
