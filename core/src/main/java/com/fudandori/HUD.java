package com.fudandori;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD {

    //STATUS rooted, invulnerable...
    private GlyphLayout glyphLayout;
    private BitmapFont bitmapFont;
    private final float WORLD_HEIGHT;
    private final float WORLD_WIDTH;

    public HUD(BitmapFont bitmapFont, float worldHeight, float worldWidth) {
        this.bitmapFont = bitmapFont;
        glyphLayout = new GlyphLayout();
        WORLD_HEIGHT = worldHeight;
        WORLD_WIDTH = worldWidth;
    }

    public void draw(SpriteBatch batch, float x, String status, int lives, int score, int track) {
        String liveString = "LIVES: " + lives;
        status = "STATUS: " + status;
        String scoreAsString = "SCORE " + Integer.toString(score);
        String musicTrack = "TRACK: " + track;
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString,
                (WORLD_WIDTH / 2 + x - glyphLayout.width) - 3,
                WORLD_HEIGHT - 5);

        glyphLayout.setText(bitmapFont, musicTrack);
        bitmapFont.draw(batch, musicTrack,
                (WORLD_WIDTH / 2 + x - glyphLayout.width) - 3,
                WORLD_HEIGHT - glyphLayout.height - 15);

        glyphLayout.setText(bitmapFont, liveString);
        bitmapFont.draw(batch, liveString,
                (x - WORLD_WIDTH / 2) + 3,
                WORLD_HEIGHT - 5);

        glyphLayout.setText(bitmapFont, status);
        bitmapFont.draw(batch, status,
                (x - WORLD_WIDTH / 2) + 3,
                WORLD_HEIGHT - glyphLayout.height - 15);

    }
}
