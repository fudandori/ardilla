package com.fudandori;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Spikes extends Enemy {

    private final float WIDTH = 32;
    private final float HEIGHT = 16;

    public Spikes(Texture texture, float x, float y) {
        super.x = x;
        super.y = y;
        super.texture = texture;
        System.out.println("X: " +x+ " Y: "+y);
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    @Override
    public Rectangle getCollisionRectangle() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

}
