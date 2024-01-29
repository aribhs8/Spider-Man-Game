package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Base.BaseGame;

/**
 * This class is the screen the player will see when the first see the game.
 */
public class StartScreen extends ScreenAdapter {
    // CONSTANTS
    private static final float WORLD_WIDTH = 1920;
    private static final float WORLD_HEIGHT = 1080;

    // game variables
    private Stage stage;
    private final BaseGame game;

    // assets
    private Texture backgroundTexture;
    private Texture playTexture;
    private Texture playPressTexture;
    private final Music titleTrack;

    /**
     * Creates a new StartScreen and plays music
     * @param g the BaseGame used to launch the game
     */
    public StartScreen(BaseGame g) {
        this.game = g;
        titleTrack = Gdx.audio.newMusic(Gdx.files.internal("sounds/title_track.mp3"));
        titleTrack.play();
    }

    /**
     * What will be displayed on the screen
     */
    @Override
    public void show() {
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // set background
        backgroundTexture = new Texture(Gdx.files.internal("title/title_background.png"));
        Image background = new Image(backgroundTexture);
        stage.addActor(background);

        // set play button
        playTexture = new Texture(Gdx.files.internal("title/play.png"));
        playPressTexture = new Texture(Gdx.files.internal("title/playPress.png"));
        ImageButton play = new ImageButton(new TextureRegionDrawable(new TextureRegion(playTexture)),
                new TextureRegionDrawable(new TextureRegion(playPressTexture)));
        play.addListener(new ActorGestureListener() {
                             @Override
                             public void tap(InputEvent event, float x, float y, int count, int button) {
                                 super.tap(event, x, y, count, button);
                                 game.setScreen(new LoadingScreen(game));
                                 titleTrack.stop();
                                 dispose();
                             }
                         });

        play.setPosition(WORLD_WIDTH/2 - play.getWidth()/2, WORLD_HEIGHT/4 - play.getHeight()/2);
        stage.addActor(play);

    }

    /**
     * Resize the screen
     * @param width the width of the screen
     * @param height the height of the screen
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    /**
     * What is rendered onto the screen
     * @param delta the time elapsed.
     */
    @Override
    public void render(float delta) {
        super.render(delta);
        clearScreen();
        stage.act(delta);
        stage.draw();
    }

    /**
     * Dispose after used
     */
    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        backgroundTexture.dispose();
        playTexture.dispose();
        playPressTexture.dispose();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

}
