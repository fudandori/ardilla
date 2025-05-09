package com.fudandori;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public abstract class Enemy {
    protected float x;
    protected float y;
    protected Texture texture;

    public abstract void draw(float delta, SpriteBatch batch);
    public abstract Rectangle getCollisionRectangle();
}
