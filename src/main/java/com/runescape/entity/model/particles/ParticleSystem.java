package com.runescape.entity.model.particles;

import com.runescape.Client;
import com.runescape.draw.Rasterizer2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private ParticleDefinition definition;


    private List<Particle> currentParticles;
    private Random random;

    /**
     * The following are stepper variables, to improve efficiency after instantiation.
     */
    private Vector velocityStep;
    private int colorStep;
    private float sizeStep;
    private Vector position;


    public ParticleSystem(ParticleDefinition definition) {
        this.definition = definition;

        this.currentParticles = new ArrayList<Particle>(definition.getMaxParticles());
        this.random = new Random(System.currentTimeMillis());

        this.sizeStep = (definition.getEndSize() - definition.getStartSize()) / definition.getLifespan();
        this.colorStep = (definition.getEndColor() - definition.getStartColor()) / definition.getLifespan();
        this.velocityStep = definition.getEndVelocity().subtract(definition.getStartVelocity(0)).divide(definition.getLifespan());
    }

    public void tick() {
        List<Particle> deadParticles = new ArrayList<Particle>();
        for (Particle p : currentParticles) {
            if (p == null) continue;
            if (p.getAge() == definition.getLifespan()) {
                deadParticles.add(p);
                continue;
            }
            p.setAge(p.getAge() + 1);
            p.setColor(p.getColor() + colorStep);
            p.setSize(p.getSize() + sizeStep);
            p.getPosition().addLocal(p.getVelocity());
            p.getVelocity().addLocal(velocityStep);
            if (definition.getGravity() != null) {
                p.getPosition().addLocal(definition.getGravity());
            }
        }
        currentParticles.removeAll(deadParticles);
        if (currentParticles.size() < definition.getMaxParticles()) {
            for (int i = 0; i < definition.getSpawnRate(); i++) {
                // Create the particle.
                Particle p = new Particle(definition.getStartColor(), definition.getStartSize(), definition.getStartVelocity(0).clone(), definition.getSpawnShape().getPoint(random).addLocal(position), definition.getStartAlpha(), definition.getzBuffer());
                currentParticles.add(p);
            }
        }
    }

    public void render() {
        tick();
        Client c = Client.instance;
        boolean spriteMode = getDefinition().getSprite() != null;

        for (Particle p : getCurrentParticles()) {
            int i = p.getPosition().getX();
            int j = p.getPosition().getY();
            int k = p.getPosition().getZ();
            c.calcEntityScreenPos(i, j, k);
            //ps.getDefinition().getSprite().drawCenteredImage(l1, i2);
            float alpha = 1f - (p.getAge() / (getDefinition().getLifespan() * 1f));
            int alphaI = (int)(alpha * 255);
            if (spriteMode)
                getDefinition().getSprite().drawAdvancedSprite(c.spriteDrawX, c.spriteDrawY, alphaI);
            else {
                Rasterizer2D.drawFilledCircle(c.spriteDrawX, c.spriteDrawY, (int)(p.getSize() * 4), p.getColor(), alphaI);
            }
        }
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public List<Particle> getCurrentParticles() {
        return currentParticles;
    }

    public ParticleDefinition getDefinition() {
        return definition;
    }
}

