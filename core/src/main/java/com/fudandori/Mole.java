package com.fudandori;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Mole extends Enemy {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;

    private int sense;
    private final int RIGHT = 1, LEFT = -1;
    private final float WANDER_DISTANCE, SPEED;
    private final float PIVOT;

    Animation animation;
    private float animationTimer = 0;

    public Mole(Texture texture, float x, float y, float speed, float wanderDistance) {
        TextureRegion[] frames = TextureRegion.split(texture, WIDTH, HEIGHT)[0];

        PIVOT = x;
        super.x = PIVOT;
        super.y = y;
        this.SPEED = speed;
        sense = RIGHT;
        WANDER_DISTANCE = wanderDistance;
        animation = new Animation(0.2f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        animationTimer += delta;
        TextureRegion textureRegion = (TextureRegion) animation.getKeyFrame(animationTimer);

        if (x > PIVOT + WANDER_DISTANCE) {
            sense = LEFT;
        } else if (x < PIVOT - WANDER_DISTANCE) {
            sense = RIGHT;
        }
        x += delta * SPEED * sense;
        flip(textureRegion);

        batch.draw(textureRegion, x, y);
    }

    public void flip(TextureRegion texture) {
        if (sense == RIGHT && !texture.isFlipX()) {
            texture.flip(true, false);
        } else if (sense == LEFT && texture.isFlipX()) {
            texture.flip(true, false);
        }
    }

    @Override
    public Rectangle getCollisionRectangle() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
