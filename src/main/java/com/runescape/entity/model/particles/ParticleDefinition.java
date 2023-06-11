package com.runescape.entity.model.particles;

import com.runescape.cache.graphics.sprite.Sprite;

import java.util.Random;

public class ParticleDefinition {

    public static final Random RANDOM = new Random(System.currentTimeMillis());
    public static ParticleDefinition[] cache = new ParticleDefinition[]{
            new ParticleDefinition() {
                { // Respected Member's Cape
                    setStartVelocity(new Vector(0, -1, 0));
                    setEndVelocity(new Vector(0, -1, 0));
                    setGravity(new Vector(0, 1 / 2, 0));
                    setLifespan(19);
                    setStartAlpha(0.500f);
                    setSpawnRate(3);
                    setSprite(new Sprite("particle 1"));
                    updateSteps();

                }
            },
            new ParticleDefinition() {
                { // Tok-Haar Kal
                    setStartVelocity(new Vector(0, -3, 0)); // x z y
                    setEndVelocity(new Vector(0, -3, 0));
                    setGravity(new Vector(0, 1 / 2, 0));
                    setLifespan(19);
                    setStartColor(0xFF0800);
                    setSpawnRate(1);
                    setStartSize(1f);
                    setEndSize(0);
                    setStartAlpha(0.075f);
                    updateSteps();
                    setColorStep(0x000900);
                }
            },
            new ParticleDefinition() {
                { // Master Dungeoneering Cape
                    setStartVelocity(new Vector(0, -1, 0));
                    setEndVelocity(new Vector(0, -1, 0));
                    setGravity(new Vector(0, 2 / 4, 0));
                    setLifespan(19);
                    setStartColor(0x000000);
                    setSpawnRate(3);
                    setStartSize(1f);
                    setEndSize(0.05f);
                    setStartAlpha(0.015f);
                    updateSteps();
                    setColorStep(0x000000);
                }
            },
            new ParticleDefinition() {
                { // Completionist Cape
                    setStartVelocity(new Vector(0, -3, 0));
                    setEndVelocity(new Vector(0, -3, 0));
                    setGravity(new Vector(0, 2 / 4, 0));
                    setLifespan(19);
                    setStartColor(0xFFFFFF);
                    setSpawnRate(4);
                    setStartSize(0.75f);
                    setEndSize(0);
                    setStartAlpha(0.035f);
                    updateSteps();
                    setColorStep(0x000000);
                }
            },
            new ParticleDefinition() {
                { // wyrm
                    setStartVelocity(new Vector(0, 2, 0));
                    setEndVelocity(new Vector(0, 2, 0));
                    setGravity(new Vector(0, 33 / 2, 0));
                    setLifespan(19);
                    setStartColor(0x000000);
                    setSpawnRate(3);
                    setStartSize(0.7f);
                    setEndSize(0.5f);
                    setStartAlpha(0f);
                    setEndAlpha(0.035f);
                    updateSteps();
                    setColorStep(0x000000);
                }
            },
            new ParticleDefinition() {
                { // wyrm
                    setStartVelocity(new Vector(0, 2, 0));
                    setEndVelocity(new Vector(0, 2, 0));
                    setGravity(new Vector(0, 3 / 2, 0));
                    setLifespan(19);
                    setStartColor(0xFF0800);
                    setSpawnRate(4);
                    setStartSize(2f);
                    setEndSize(0.5f);
                    setStartAlpha(0f);
                    setEndAlpha(0.045f);
                    updateSteps();
                    setColorStep(0x000900);
                }
            },};
    /**
     * The size at which the particle starts.
     */
    private float startSize = 1f;
    /**
     * The size at which the particle is at the end of it's life.
     */
    private float endSize = 1f;
    /**
     * The start color of the particles.
     */
    private int startColor = 0xFFFFFFFF;
    /**
     * The end color of the particles.
     */
    private int endColor = 0xFFFFFFFF;
    /**
     * The velocity when the particle is first spawned.
     */
    private Vector startVelocity = Vector.ZERO;
    /**
     * The velocity of the particle when it dies.
     */
    private Vector endVelocity = Vector.ZERO;
    /**
     * The amount of frames a particle survives for.
     */
    private int lifespan = 1;
    /**
     * The maximum amount of particles at any given moment.
     */
    private static int maxParticles = 10000;
    /**
     * The amount of particles spawned each frame.
     */
    private int spawnRate = 1;
    /**
     * The sprite of the particles.
     */
    private int zBuffer;
    private Sprite sprite;

    private float startAlpha = 1f;
    private float endAlpha = 0.05f;

    public Vector getGravity() {
        return gravity;
    }

    public void setGravity(Vector gravity) {
        this.gravity = gravity;
    }

    public int getzBuffer() {
        return zBuffer;
    }

    public void setzBuffer(int zBuffer) {
        this.zBuffer = zBuffer;
    }
    /**
     * The gravitational force for this object.
     */
    private Vector gravity;

    public float getStartAlpha() {
        return startAlpha;
    }

    public void setStartAlpha(float startAlpha) {
        this.startAlpha = startAlpha;
    }

    public float getEndAlpha() {
        return endAlpha;
    }

    public void setEndAlpha(float endAlpha) {
        this.endAlpha = endAlpha;
    }

    public float getAlphaStep() {
        return alphaStep;
    }

    public void setAlphaStep(float alphaStep) {
        this.alphaStep = alphaStep;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public SpawnShape getSpawnShape() {
        return spawnShape;
    }

    public void setSpawnShape(SpawnShape spawnShape) {
        this.spawnShape = spawnShape;
    }

    public int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }
    /**
     * The shape which defines the potential origins for particle spawns.
     */
    private SpawnShape spawnShape = new PointSpawnShape(Vector.ZERO);

    public int getSpawnRate() {
        return spawnRate;
    }

    public void setSpawnRate(int spawnRate) {
        this.spawnRate = spawnRate;
    }

    public static int getMaxParticles() {
        return maxParticles;
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    public float getStartSize() {
        return startSize;
    }

    public void setStartSize(float startSize) {
        this.startSize = startSize;
    }

    public float getEndSize() {
        return endSize;
    }

    public void setEndSize(float endSize) {
        this.endSize = endSize;
    }

    public int getStartColor() {
        return startColor;
    }

    public void setStartColor(int startColor) {
        this.startColor = startColor;
    }

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }

    public Vector getStartVelocity(int id) {
        switch (id) {
            default:
                return this.startVelocity;
            case 3:
                return new Vector(this.startVelocity.getX() + randomWithRange(-1, 1), this.startVelocity.getY() + randomWithRange(0, 3), this.startVelocity.getZ() + randomWithRange(-1, 1));
            case 2:
                return new Vector(this.startVelocity.getX() + randomWithRange(-1, 1), this.startVelocity.getY(), this.startVelocity.getZ() + randomWithRange(-1, 1));
            case 4:
            case 5:
                return new Vector(this.startVelocity.getX() + randomWithRange(-3, 3), this.startVelocity.getY() + randomWithRange(0, 9), this.startVelocity.getZ() + randomWithRange(-3, 3));

        }
    }

    public void setStartVelocity(Vector startVelocity) {
        this.startVelocity = startVelocity;
    }

    public Vector getEndVelocity() {
        return endVelocity;
    }

    public void setEndVelocity(Vector endVelocity) {
        this.endVelocity = endVelocity;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    private Vector velocityStep;
    private int colorStep;

    public void setVelocityStep(Vector velocityStep) {
        this.velocityStep = velocityStep;
    }

    public void setColorStep(int colorStep) {
        this.colorStep = colorStep;
    }

    public void setSizeStep(float sizeStep) {
        this.sizeStep = sizeStep;
    }

    public float getSizeStep() {
        return sizeStep;
    }

    public Vector getVelocityStep() {
        return velocityStep;
    }

    public int getColorStep() {
        return colorStep;
    }

    private float sizeStep;

    private float alphaStep;

    public void updateSteps() {
        this.sizeStep = (endSize - startSize) / (lifespan * 1f);
        this.colorStep = (endColor - startColor) / lifespan;
        this.velocityStep = endVelocity.subtract(startVelocity).divide(lifespan);
        this.alphaStep = (endAlpha - startAlpha) / lifespan;
    }

    public void updateStepsNoAlpha() {
        this.sizeStep = (endSize - startSize) / (lifespan * 1f);
        this.colorStep = (endColor - startColor) / lifespan;
        this.velocityStep = endVelocity.subtract(startVelocity).divide(lifespan);
        //this.alphaStep = (endAlpha - startAlpha) / lifespan;
    }
}

