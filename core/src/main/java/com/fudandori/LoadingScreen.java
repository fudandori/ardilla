package com.fudandori;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;
    private static final float PROGRESS_BAR_WIDTH = 200;
    private static final float PROGRESS_BAR_HEIGHT = 25;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;
    private float progress = 0;
    private final Ardilla juegoArdilla;

    public LoadingScreen(Ardilla juegoArdilla) {
        this.juegoArdilla = juegoArdilla;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        juegoArdilla.getAssetManager().load("pete.tmx", TiledMap.class);
        juegoArdilla.getAssetManager().load("track1.mp3", Music.class);
        juegoArdilla.getAssetManager().load("track2.ogg", Music.class);
        juegoArdilla.getAssetManager().load("track3.mp3", Music.class);
        juegoArdilla.getAssetManager().load("track4.ogg", Music.class);
        juegoArdilla.getAssetManager().load("jump.ogg", Sound.class);
        juegoArdilla.getAssetManager().load("puaj.ogg", Sound.class);
        juegoArdilla.getAssetManager().load("yuck.ogg", Sound.class);
        juegoArdilla.getAssetManager().load("ouch.ogg", Sound.class);
        juegoArdilla.getAssetManager().load("yes.ogg", Sound.class);
        juegoArdilla.getAssetManager().load("bonk.ogg", Sound.class);
        juegoArdilla.getAssetManager().load("fall.ogg", Sound.class);
        juegoArdilla.getAssetManager().load("pickedacorn.ogg", Sound.class);
        juegoArdilla.getAssetManager().load("bigbat.png", Texture.class);
        juegoArdilla.getAssetManager().load("spikes.png", Texture.class);
        juegoArdilla.getAssetManager().load("mist.png", Texture.class);
        juegoArdilla.getAssetManager().load("frontbat.png", Texture.class);
        juegoArdilla.getAssetManager().load("mole.png", Texture.class);
        juegoArdilla.getAssetManager().load("night_sky.png", Texture.class);
        juegoArdilla.getAssetManager().load("pete.png", Texture.class);
        juegoArdilla.getAssetManager().load("acorn.png", Texture.class);
        juegoArdilla.getAssetManager().load("blueacorn.png", Texture.class);
        juegoArdilla.getAssetManager().load("goldenacorn.png", Texture.class);
        juegoArdilla.getAssetManager().load("redacorn.png", Texture.class);
        juegoArdilla.getAssetManager().load("purpleacorn.png", Texture.class);
        juegoArdilla.getAssetManager().load("score.fnt", BitmapFont.class);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render(float delta) {
        update();
        clearScreen();
        draw();
    }

    private void update() {
        if (juegoArdilla.getAssetManager().update()) {
            juegoArdilla.setScreen(new GameScreen(juegoArdilla));
        } else {
            progress = juegoArdilla.getAssetManager().getProgress();
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
                (WORLD_WIDTH - PROGRESS_BAR_WIDTH) / 2,
                WORLD_HEIGHT / 2 - PROGRESS_BAR_HEIGHT / 2,
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }
}
