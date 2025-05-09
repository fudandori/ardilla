package com.fudandori;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class Acorn {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    private final Rectangle collisionRectangle;
    private final Texture texture;
    private final float x;
    private final float y;
    private int type;

    public int getType() {
        return type;
    }

    public Acorn(Texture texture, float x, float y, int type) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.collisionRectangle = new Rectangle(x, y, WIDTH, HEIGHT);
        this.type = type;

    }

    public Rectangle getCollisionRectangle() {
        return collisionRectangle;
    }

    public void draw(Batch batch) {
        batch.draw(texture, x, y);
    }
}
