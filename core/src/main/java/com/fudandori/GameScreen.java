package com.fudandori;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 480 * 16 / 9, WORLD_HEIGHT = 480f;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private final Ardilla juegoArdilla;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Pete pete;
    private final int CELL_SIZE = 16;
    private final Array<Acorn> acorns = new Array<Acorn>();

    //AMPLIACIÓN
    private boolean blinded = false;
    private int musicTrack;
    Music music;
    Sound jumpSound;
    Sound pickedAcornSound;
    private final Array<Enemy> enemies = new Array<Enemy>();
    private final Array<Threat> threats = new Array<Threat>();
    private State state;
    private HUD hud;
    private Texture nightSkyTexture;
    private final float DAY_DURATION = 60f, NIGHT_DURATION = 25f;
    private boolean isNight = false;
    private float timeLapse = DAY_DURATION;

    //Enemy Types
    private final int BAT = 0, MOLE = 2, SPIKES = 10;

    //Threat Types
    private final int VINE = 0, FOG = 1;

    //Acorn TYPES
    private final int NORMAL = 0, RED = 1, GOLDEN = 2;

    private Sound hitSound;
    private int lives = 5;
    private int score = 0;

    private enum State {
        NORMAL, HIT, INVINCIBLE, FALLING, DEAD, ENTANGLED, GAME_OVER
    }
//----------------------------------------------------

    public GameScreen(Ardilla juegoArdilla) {
        this.juegoArdilla = juegoArdilla;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

    }

    @Override
    public void show() {
        //Rendering
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        tiledMap = juegoArdilla.getAssetManager().get("pete.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, spriteBatch);
        orthogonalTiledMapRenderer.setView(camera);
        Texture peteText = juegoArdilla.getAssetManager().get("pete.png");
        nightSkyTexture = juegoArdilla.getAssetManager().get("night_sky.png");

        //Populate World
        populateAcorns();
        populateEnemies();
        populateThreats();

        //Sounds
        musicTrack = 1;
        music = juegoArdilla.getAssetManager().get("track1.mp3");
        music.setLooping(true);
        music.play();
        jumpSound = juegoArdilla.getAssetManager().get("jump.ogg");
        pickedAcornSound = juegoArdilla.getAssetManager().get("pickedacorn.ogg");
        hitSound = juegoArdilla.getAssetManager().get("ouch.ogg");

        //Player
        state = State.NORMAL;
        pete = new Pete(peteText, jumpSound);
        hud = new HUD(juegoArdilla.getAssetManager().get("score.fnt", BitmapFont.class), WORLD_HEIGHT, WORLD_WIDTH);
    }

    @Override
    public void render(float delta) {
        clearScreen();
        spriteBatch.setProjectionMatrix(camera.projection);
        spriteBatch.setTransformMatrix(camera.view);
        renderSky(delta);
        orthogonalTiledMapRenderer.render();
        update(delta);
        draw(delta);
    }

    private void update(float delta) {

        Input input = Gdx.input;

        if (input.isKeyJustPressed(Input.Keys.NUM_1) && musicTrack != 1) {
            music.stop();
            music = juegoArdilla.getAssetManager().get("track1.mp3");
            music.play();
            musicTrack = 1;
        } else if (input.isKeyJustPressed(Input.Keys.NUM_2) && musicTrack != 2) {
            music.stop();
            music = juegoArdilla.getAssetManager().get("track2.ogg");
            music.play();
            musicTrack = 2;
        } else if (input.isKeyJustPressed(Input.Keys.NUM_3) && musicTrack != 3) {
            music.stop();
            music = juegoArdilla.getAssetManager().get("track3.mp3");
            music.play();
            musicTrack = 3;
        }else if (input.isKeyJustPressed(Input.Keys.NUM_4) && musicTrack != 4) {
            music.stop();
            music = juegoArdilla.getAssetManager().get("track4.ogg");
            music.play();
            musicTrack = 4;
        }

        if (state == State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                pete.setPosition(0, 0);
                camera.position.set(WORLD_WIDTH / 2, camera.position.y, camera.position.z);
                camera.update();
                orthogonalTiledMapRenderer.setView(camera);
                state = State.NORMAL;
            }
        } else {
            if (pete.getY() < -10 && state != State.FALLING) {
                triggerFall();
            } else {
                pete.setEntangled(state == State.ENTANGLED);
                pete.update(delta);
                stopPeteLeavingTheScreen();
                handlePeteCollision();
                handlePeteCollisionWithAcorn();
                handlePeteCollisionWithEnemy();
                handlePeteCollisionWithThreats();
            }
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g,
                Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw(float delta) {
        spriteBatch.setProjectionMatrix(camera.projection);
        spriteBatch.setTransformMatrix(camera.view);
        spriteBatch.begin();
        pete.draw(spriteBatch);
        drawObjects(delta, spriteBatch);
        if (blinded) {
            smokeScreen(spriteBatch);
        }
        hud.draw(spriteBatch, camera.position.x, getStatusString(), lives, score, musicTrack);
        spriteBatch.end();
        updateCameraX();
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        pete.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }

    private void stopPeteLeavingTheScreen() {

        if (pete.getX() < 0) {
            pete.setPosition(0, pete.getY());
        }
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Solid_Ground");
        float levelWidth = tiledMapTileLayer.getWidth()
                * tiledMapTileLayer.getTileWidth();
        if (pete.getX() + Pete.WIDTH > levelWidth) {
            pete.setPosition(levelWidth - Pete.WIDTH, pete.getY());
        }
    }

    private Array<CollisionCell> whichCellsDoesPeteCover() {
        float x = pete.getX();
        float y = pete.getY();
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();
        float cellX = x / CELL_SIZE;
        float cellY = y / CELL_SIZE;
        int bottomLeftCellX = MathUtils.floor(cellX);
        int bottomLeftCellY = MathUtils.floor(cellY);
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Solid_Ground");
        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLeftCellX,
                bottomLeftCellY), bottomLeftCellX, bottomLeftCellY));
        if (cellX % 1 != 0 && cellY % 1 != 0) {
            int topRightCellX = bottomLeftCellX + 1;
            int topRightCellY = bottomLeftCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topRightCellX,
                    topRightCellY), topRightCellX, topRightCellY));
        }
        if (cellX % 1 != 0) {
            int bottomRightCellX = bottomLeftCellX + 1;
            int bottomRightCellY = bottomLeftCellY;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomRightCellX,
                    bottomRightCellY), bottomRightCellX, bottomRightCellY));
        }
        if (cellY % 1 != 0) {
            int topLeftCellX = bottomLeftCellX;
            int topLeftCellY = bottomLeftCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topLeftCellX,
                    topLeftCellY), topLeftCellX, topLeftCellY));
        }
        return cellsCovered;
    }

    private Array<CollisionCell> filterOutNonTiledCells(Array<CollisionCell> cells) {
        for (Iterator<CollisionCell> iter = cells.iterator();
                iter.hasNext();) {
            CollisionCell collisionCell = iter.next();
            if (collisionCell.isEmpty()) {
                iter.remove();
            }
        }
        return cells;
    }

    private void handlePeteCollision() {
        Array<CollisionCell> peteCells = whichCellsDoesPeteCover();
        peteCells = filterOutNonTiledCells(peteCells);
        for (CollisionCell cell : peteCells) {
            float cellLevelX = cell.cellX * CELL_SIZE;
            float cellLevelY = cell.cellY * CELL_SIZE;
            Rectangle intersection = new Rectangle();
            Intersector.intersectRectangles(pete.getCollisionRectangle(),
                    new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE),
                    intersection);
            if (intersection.getHeight() < intersection.getWidth()) {
                pete.setPosition(pete.getX(), intersection.getY()
                        + intersection.getHeight());
                pete.landed();
            } else if (intersection.getWidth() < intersection.getHeight()) {
                if (intersection.getX() == pete.getX()) {
                    pete.setPosition(intersection.getX()
                            + intersection.getWidth(), pete.getY());
                }
                if (intersection.getX() > pete.getX()) {
                    pete.setPosition(intersection.getX() - Pete.WIDTH,
                            pete.getY());
                }
            }
        }
    }

    private void populateAcorns() {
        MapLayer mapLayer = tiledMap.getLayers().get("Collectables");
        for (MapObject mapObject : mapLayer.getObjects()) {
            int type = mapObject.getProperties().get("type", Integer.class);
            Texture texture = null;

            switch (type) {
                case NORMAL:
                    texture = juegoArdilla.getAssetManager().get("acorn.png", Texture.class);
                    break;
                case RED:
                    texture = juegoArdilla.getAssetManager().get("redacorn.png", Texture.class);
                    break;
                case GOLDEN:
                    texture = juegoArdilla.getAssetManager().get("goldenacorn.png", Texture.class);
                    break;
            }

            acorns.add(
                    new Acorn(texture,
                            mapObject.getProperties().get("x", Float.class),
                            mapObject.getProperties().get("y", Float.class),
                            type
                    )
            );
        }
    }

    private void handlePeteCollisionWithAcorn() {
        for (Iterator<Acorn> iter = acorns.iterator(); iter.hasNext();) {
            Acorn acorn = iter.next();
            if (pete.getCollisionRectangle().overlaps(acorn.getCollisionRectangle())) {
                iter.remove();

                switch (acorn.getType()) {
                    case NORMAL:
                        pickedAcornSound.play();
                        score++;
                        if (score % 30 == 0) {
                            lives++;
                        }
                        break;
                    case RED:
                        Random rng = new Random();
                        Sound sound = rng.nextInt(2) == 0
                                ? juegoArdilla.getAssetManager().get("puaj.ogg", Sound.class)
                                : juegoArdilla.getAssetManager().get("yuck.ogg", Sound.class);
                        if (isVulnerable()) {
                            triggerDamaged(sound);
                        }
                        break;
                    case GOLDEN:
                        triggerInvulnerability();
                        break;
                }
            }
        }
    }

    private void updateCameraX() {
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Solid_Ground");
        float levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();

        if ((pete.getX() > WORLD_WIDTH / 2f) && (pete.getX() < (levelWidth - WORLD_WIDTH / 2f))) {
            camera.position.set(pete.getX(), camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView(camera);
        }
    }

    private class CollisionCell {

        private final TiledMapTileLayer.Cell cell;
        private final int cellX;
        private final int cellY;

        public CollisionCell(TiledMapTileLayer.Cell cell, int cellX, int cellY) {
            this.cell = cell;
            this.cellX = cellX;
            this.cellY = cellY;
        }

        public boolean isEmpty() {
            return cell == null;
        }
    }

    //AMPLIACIÓN
    private void smokeScreen(SpriteBatch spriteBatch) {
        spriteBatch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.9f));
        shapeRenderer.rect(camera.position.x - WORLD_WIDTH / 2, 0, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        spriteBatch.begin();
    }

    private void drawObjects(float delta, SpriteBatch spriteBatch) {
        for (Acorn acorn : acorns) {
            acorn.draw(spriteBatch);
        }
        for (Enemy enemy : enemies) {
            enemy.draw(delta, spriteBatch);
        }
        for (Threat threat : threats) {
            threat.draw(spriteBatch);
        }

    }

    private void populateEnemies() {
        MapLayer mapLayer = tiledMap.getLayers().get("Enemies");
        for (MapObject mapObject : mapLayer.getObjects()) {
            int type = mapObject.getProperties().get("enemy_type", Integer.class);

            switch (type) {
                case BAT:
                    final int HORIZONTAL = 0,
                     VERTICAL = 1;

                    int direction = mapObject.getProperties().get("direction", Integer.class);
                    Texture texture = null;

                    switch (direction) {
                        case HORIZONTAL:
                            texture = juegoArdilla.getAssetManager().get("bigbat.png", Texture.class);
                            break;
                        case VERTICAL:
                            texture = juegoArdilla.getAssetManager().get("frontbat.png", Texture.class);
                            break;
                    }

                    enemies.add(
                            new Bat(texture,
                                    mapObject.getProperties().get("x", Float.class),
                                    mapObject.getProperties().get("y", Float.class),
                                    mapObject.getProperties().get("speed", Float.class) * CELL_SIZE,
                                    mapObject.getProperties().get("wander", Float.class) * CELL_SIZE,
                                    direction
                            )
                    );
                    break;

                case SPIKES:
                    enemies.add(
                            new Spikes(
                                    juegoArdilla.getAssetManager().get("spikes.png", Texture.class),
                                    mapObject.getProperties().get("x", Float.class),
                                    mapObject.getProperties().get("y", Float.class)
                            )
                    );
                    break;

                case MOLE:
                    enemies.add(
                            new Mole(juegoArdilla.getAssetManager().get("mole.png", Texture.class),
                                    mapObject.getProperties().get("x", Float.class),
                                    mapObject.getProperties().get("y", Float.class),
                                    mapObject.getProperties().get("speed", Float.class) * CELL_SIZE,
                                    mapObject.getProperties().get("wander", Float.class) * CELL_SIZE
                            )
                    );
                    break;
            }
        }
    }

    private void handlePeteCollisionWithEnemy() {
        for (Iterator<Enemy> iter = enemies.iterator(); iter.hasNext();) {
            Enemy enemy = iter.next();
            if (pete.getCollisionRectangle().overlaps(enemy.getCollisionRectangle())) {
                if (isVulnerable()) {
                    triggerDamaged(hitSound);
                } else if (state == State.INVINCIBLE) {
                    if (enemy.getClass() == Bat.class) {
                        juegoArdilla.getAssetManager().get("bonk.ogg", Sound.class).play();
                        score += 10;
                        iter.remove();
                    }
                }
            }
        }
    }

    private void populateThreats() {
        MapLayer mapLayer = tiledMap.getLayers().get("Threats");
        Texture texture = null;
        float x;
        float y;
        float width = 0;
        float height = 0;
        int type;

        for (MapObject mapObject : mapLayer.getObjects()) {
            type = mapObject.getProperties().get("type", Integer.class);

            x = mapObject.getProperties().get("x", Float.class);
            y = mapObject.getProperties().get("y", Float.class);

            switch (type) {
                case VINE:
                    texture = juegoArdilla.getAssetManager().get("vines.png", Texture.class);
                    break;
                case FOG:
                    texture = juegoArdilla.getAssetManager().get("mist.png", Texture.class);
                    break;
            }

            width = mapObject.getProperties().get("width", Float.class);
            height = mapObject.getProperties().get("height", Float.class);

            threats.add(new Threat(texture, x, y, width, height, type));
        }
    }

    private void handlePeteCollisionWithThreats() {
        boolean gotBlinded = false;
        boolean gotRooted = false;
        boolean finished = false;
        Rectangle player = pete.getCollisionRectangle();
        Iterator<Threat> iter = threats.iterator();
        while (iter.hasNext() && !finished) {
            Threat threat = iter.next();

            if (threat.touched(player)) {
                finished = true;
                switch (threat.getType()) {
                    case VINE:
                        gotRooted = true;
                        break;
                    case FOG:
                        gotBlinded = true;
                        break;
                }
            }
        }

        State newState = gotRooted ? State.ENTANGLED : State.NORMAL;
        state = isVulnerable() ? newState : state;

        blinded = gotBlinded;
    }

    private boolean isVulnerable() {
        return state != State.INVINCIBLE
                && state != State.HIT
                && state != State.FALLING
                && state != State.DEAD;
    }

    private void triggerFall() {
        state = State.FALLING;
        juegoArdilla.getAssetManager().get("fall.ogg", Sound.class).play();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                state = State.DEAD;
            }
        }, 1000);
    }

    private void triggerInvulnerability() {
        juegoArdilla.getAssetManager().get("yes.ogg", Sound.class).play();
        state = State.INVINCIBLE;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                state = state != State.INVINCIBLE ? state : State.NORMAL;
            }
        }, 10000);
    }

    private void triggerDamaged(Sound sound) {
        sound.play();
        state = State.HIT;
        lives--;
        final Timer blinking = new Timer();
        blinking.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pete.setInvisible(!pete.isInvisible());
            }
        }, 0, 250);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                blinking.cancel();
                state = state == State.HIT ? State.NORMAL : state;
                pete.setInvisible(false);
            }
        }, 3000);
    }

    private String getStatusString() {
        String statusName = "";

        switch (state) {
            case NORMAL:
                statusName = "Normal";
                break;
            case DEAD:
            case FALLING:
                statusName = "Dead";
                break;
            case INVINCIBLE:
                statusName = "Invulnerable";
                break;
            case HIT:
                statusName = "Recovering";
                break;

        }
        if (state == State.ENTANGLED) {
            statusName = "Rooted";
        }
        if (blinded) {
            statusName += " Blinded!";
        }
        return statusName;
    }

    private void renderSky(float delta) {
        spriteBatch.begin();
        spriteBatch.draw(nightSkyTexture, camera.position.x - WORLD_WIDTH / 2, 0);
        spriteBatch.end();
        float alpha = isNight ? 0 : 1;

        if (isNight) {
            timeLapse += delta;
            if (timeLapse >= NIGHT_DURATION / 2) {
                alpha = (timeLapse / 2) / NIGHT_DURATION;
            }
        } else {
            timeLapse -= delta;
            if (timeLapse <= DAY_DURATION / 3) {
                alpha = timeLapse / (DAY_DURATION / 3);
            }
        }

        if (alpha < 0) {
            isNight = true;
            timeLapse = 0;
        } else if (alpha > 1) {
            isNight = false;
            timeLapse = DAY_DURATION;
        }

        System.out.println("ALPHA: " + alpha);
        System.out.println("TIMELAPSE: " + timeLapse);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(Color.SKY.r, Color.SKY.g, Color.SKY.b, alpha));
        shapeRenderer.rect(camera.position.x - WORLD_WIDTH / 2, 0, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
