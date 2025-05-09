package com.fudandori;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Threat {
    public final float WIDTH;
    public final float HEIGHT;

    private final Texture texture;
    private final float x;
    private final float y;
    private final int type;
    private final Rectangle collisionRectangle;

    public Threat(Texture texture, float x, float y , float width, float height, int type) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.type = type;
        WIDTH = width;
        HEIGHT = height;
        collisionRectangle = new Rectangle(x + 7, y, WIDTH - 14, HEIGHT/2);
    }

    public int getType() {
        return type;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public boolean touched(Rectangle collisionRectangle) {
        return collisionRectangle.overlaps(this.collisionRectangle);
    }
}
