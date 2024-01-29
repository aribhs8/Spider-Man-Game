package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Base.BaseGame;

/**
 * This class is a loading screen that loads all assets before the start of the game.
 */
public class LoadingScreen extends ScreenAdapter {
    // CONSTANTS
    // world dimensions
    private static final float WORLD_WIDTH = 1920;
    private static final float WORLD_HEIGHT = 1080;
    // progress bar
    private static final float PROGRESS_BAR_WIDTH = 1000;
    private static final float PROGRESS_BAR_HEIGHT = 50;

    // VARIABLES
    // view
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;

    // background
    private float progress = 0;
    private final BaseGame baseGame;

    /**
     * Create a new loading screen
     * @param g the BaseGame used to create the game
     */
    LoadingScreen(BaseGame g) {
        this.baseGame = g;
    }

    /**
     * Resizes the screen
     * @param width represents the width of the screen
     * @param height represents the height of the screen
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    /**
     * What is shown on the screen
     */
    @Override
    public void show() {
        super.show();

        // create new cameras
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();

        // LOAD ASSETS
        // sprite-sheets
        baseGame.getAssetManager().load("player/normal_spritesheet.png", Texture.class);
        baseGame.getAssetManager().load("enemies/goblin_sprite_sheet.png", Texture.class);
        baseGame.getAssetManager().load("enemies/goblin_sprite_sheet.png", Texture.class);
        baseGame.getAssetManager().load("player/web_spritesheet.png", Texture.class);
        baseGame.getAssetManager().load("enemies/goblin_goon_club_sprite_sheet.png", Texture.class);
        baseGame.getAssetManager().load("enemies/goblin_goon_dagger_sprite_sheet.png", Texture.class);
        baseGame.getAssetManager().load("enemies/goblin_goon_punch_sprite_sheet.png", Texture.class);
        baseGame.getAssetManager().load("explosion.png", Texture.class);
        // tilemap
        baseGame.getAssetManager().load("map/map_level_1.tmx", TiledMap.class);
        baseGame.getAssetManager().load("map/boss_level.tmx", TiledMap.class);
        // health bars
        baseGame.getAssetManager().load("player/health_bar.png", Texture.class);
        baseGame.getAssetManager().load("enemies/health_bar.png", Texture.class);
        baseGame.getAssetManager().load("enemies/boss_health_bar.png", Texture.class);
        // audio
        baseGame.getAssetManager().load("sounds/level_track.mp3", Music.class);
        baseGame.getAssetManager().load("sounds/game_over.mp3", Music.class);
        baseGame.getAssetManager().load("sounds/boss_track.mp3", Music.class);
        baseGame.getAssetManager().load("sounds/landing_sound.wav", Sound.class);
        baseGame.getAssetManager().load("sounds/missed_punch_sound.wav", Sound.class);
        baseGame.getAssetManager().load("sounds/punch_sound.wav", Sound.class);
        baseGame.getAssetManager().load("sounds/explosion.wav", Sound.class);
        // background
        baseGame.getAssetManager().load("game_over_background.png", Texture.class);
        // other
        baseGame.getAssetManager().load("you-win.png", Texture.class);
        baseGame.getAssetManager().load("controls.png", Texture.class);
    }

    /**
     * Render onto screen
     * @param delta represents time elapsed
     */
    @Override
    public void render(float delta) {
        super.render(delta);
        update();
        clearScreen();
        draw();
    }

    /**
     * Dispose after use
     */
    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }

    /**
     * Update contents on screen, change
     * screen once asset manager is complete.
     */
    private void update() {
        if (baseGame.getAssetManager().update()) {
            baseGame.setScreen(new Level1(baseGame));
        } else {
            progress = baseGame.getAssetManager().getProgress();
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Draw contents to screen
     */
    private void draw() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect((WORLD_WIDTH  - PROGRESS_BAR_WIDTH) / 2, (WORLD_HEIGHT - PROGRESS_BAR_HEIGHT) / 2,
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }
}
