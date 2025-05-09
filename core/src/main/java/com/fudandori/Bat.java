package com.fudandori;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bat extends Enemy {

    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;

    private int direction, sense;
    private final int RIGHT_UP = 1, LEFT_DOWN = -1;
    private final int VERTICAL = 1, HORIZONTAL = 0;
    private final float WANDER_DISTANCE, SPEED;
    private final Vector2 PIVOT;

    Animation animation;
    private float animationTimer = 0;

    public Bat(Texture texture, float x, float y, float speed, float wanderDistance, int direction) {
        TextureRegion[] frames = TextureRegion.split(texture, WIDTH, HEIGHT)[0];

        PIVOT = new Vector2(x, y);
        super.x = PIVOT.x;
        super.y = PIVOT.y;

        this.SPEED = speed;
        sense = LEFT_DOWN;
        WANDER_DISTANCE = wanderDistance;
        animation = new Animation(0.1f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        this.direction = direction;
    }

    @Override
    public void draw(float delta, SpriteBatch batch) {
        animationTimer += delta;
        TextureRegion textureRegion = (TextureRegion) animation.getKeyFrame(animationTimer);

        if (direction == HORIZONTAL) {

            if (x > PIVOT.x + WANDER_DISTANCE) {
                sense = LEFT_DOWN;
            } else if (x < PIVOT.x - WANDER_DISTANCE) {
                sense = RIGHT_UP;
            }
            x += delta * SPEED * sense;
            flip(textureRegion);
        } else if (direction == VERTICAL) {
            if (y > PIVOT.y + WANDER_DISTANCE) {
                sense = LEFT_DOWN;
            } else if (y < PIVOT.y - WANDER_DISTANCE) {
                sense = RIGHT_UP;
            }
            y += delta * SPEED * sense;
        }

        batch.draw(textureRegion, x, y);
    }

    public void flip(TextureRegion texture) {
        if (sense == LEFT_DOWN && !texture.isFlipX()) {
            texture.flip(true, false);
        } else if (sense == RIGHT_UP && texture.isFlipX()) {
            texture.flip(true, false);
        }
    }

    @Override
    public Rectangle getCollisionRectangle() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
