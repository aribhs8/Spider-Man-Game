package com.mygdx.game.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Base.BaseGame;

import static com.badlogic.gdx.scenes.scene2d.ui.Label.*;

/**
 * This class represents the screen when the player has lost the game.
 */
public class GameOverScreen extends ScreenAdapter implements InputProcessor {
    // CONSTANTS
    // world dimensions
    private static final float WORLD_WIDTH = 256;
    private static final float WORLD_HEIGHT = 223;

    // screen variables
    private Stage stage;
    private Texture backgroundTexture;
    private final BaseGame game;

    public GameOverScreen(BaseGame g) {
        game = g;
        g.getAssetManager().get("sounds/game_over.mp3", Music.class).setLooping(true);
        g.getAssetManager().get("sounds/game_over.mp3", Music.class).setVolume(0.25f);
        g.getAssetManager().get("sounds/game_over.mp3", Music.class).play();
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        InputMultiplexer im = new InputMultiplexer(this,stage);
        Gdx.input.setInputProcessor( im );

        // set background
        backgroundTexture = game.getAssetManager().get("game_over_background.png");
        Image background = new Image(backgroundTexture);
        stage.addActor(background);

        //ask to play again
        BitmapFont font = new BitmapFont();
        String text = "Press P to play again, M for Main Menu";
        LabelStyle style = new LabelStyle(font, Color.YELLOW);
        Label instructions = new Label( text, style );
        instructions.setFontScale(1);
        instructions.setPosition(0, 50);
        // repeating color pulse effect
        instructions.addAction(
                Actions.forever(
                        Actions.sequence(
                                Actions.color( new Color(1, 1, 0, 1), 0.5f ),
                                Actions.delay( 0.5f ),
                                Actions.color( new Color(0.5f, 0.5f, 0, 1), 0.5f )
                        )
                )
        );
        stage.addActor( instructions );
    }

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
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Checks for key presses
     * @param keycode represents key being pressed
     * @return false always.
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.P) {
            game.getAssetManager().get("sounds/game_over.mp3", Music.class).stop();
            game.setScreen(new LoadingScreen(game));
        }
        else if (keycode == Input.Keys.M) {
            game.getAssetManager().get("sounds/game_over.mp3", Music.class).stop();
            game.setScreen(new StartScreen(game));
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}