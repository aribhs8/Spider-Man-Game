package com.mygdx.game.Base;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.Actors.Player;
import com.mygdx.game.Actors.SuperActors.BaseActor;
import com.mygdx.game.Actors.SuperActors.Box2DActor;
import com.mygdx.game.Actors.Bullet;
import com.mygdx.game.Actors.SuperActors.Character;
import com.mygdx.game.Utilities.Constants;

import java.util.ArrayList;

public abstract class BaseScreen implements Screen, InputProcessor
{
    protected BaseGame game;

    protected Stage mainStage;
    protected Stage uiStage;
    protected ShapeRenderer shapeRenderer;

    protected Table uiTable;

    public final int viewWidth  = 530;
    public final int viewHeight = 400;

    private boolean paused;

    // game elements
    protected Player player;
    protected Character lastEnemy;
    protected ArrayList<Bullet> bullets;
    protected ArrayList<Box2DActor> removeList;
    protected BaseActor enemyBar;
    protected BaseActor enemyLogo;

    public BaseScreen(BaseGame g) {
        game = g;

        mainStage = new Stage( new FitViewport(viewWidth, viewHeight) );
        uiStage   = new Stage( new FitViewport(viewWidth, viewHeight) );
        shapeRenderer = new ShapeRenderer();

        uiTable = new Table();
        uiTable.setFillParent(true);
        uiStage.addActor(uiTable);

        paused = false;

        InputMultiplexer im = new InputMultiplexer(this, uiStage, mainStage);
        Gdx.input.setInputProcessor( im );

        // create new game elements
        bullets = new ArrayList<Bullet>();
        removeList = new ArrayList<Box2DActor>();

        // player health bar
        BaseActor healthBar = new BaseActor();
        healthBar.setTexture((Texture) game.getAssetManager().get("player/health_bar.png"));
        healthBar.setPosition(14, viewHeight-68);
        healthBar.setSize(82, 55);
        uiStage.addActor(healthBar);

        // enemy health bar
        enemyBar = new BaseActor();
        enemyBar.setTexture((Texture) game.getAssetManager().get("enemies/health_bar.png"));
        enemyBar.setPosition(viewWidth - 120, viewHeight - 50);
        enemyBar.setSize(100, 30);
        enemyBar.setVisible(false);
        uiStage.addActor(enemyBar);

        // enemy logo
        enemyLogo = new BaseActor();
        enemyLogo.setSize(20, 30);
        enemyLogo.setPosition(viewWidth - 47, viewHeight - 47);
        enemyLogo.setVisible(false);
        uiStage.addActor(enemyLogo);

        create();
    }

    public abstract void create();

    public abstract void update(float dt);

    // this is the gameloop. update, then render.
    public void render(float dt) {
        uiStage.act(dt);

        // only pause gameplay events, not UI events
        if ( !isPaused() )
        {
            mainStage.act(dt);
            update(dt);
        }

        // render
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        mainStage.draw();
        uiStage.draw();
    }

    // pause methods
    public boolean isPaused()
    {  return paused;  }

    public void setPaused(boolean b)
    {  paused = b;  }

    public void togglePaused()
    {  paused = !paused;  }

    // Box2D addSolid
    protected void addSolid(RectangleMapObject rmo, World world) {
        Rectangle r = rmo.getRectangle();
        Box2DActor solid = new Box2DActor();
        solid.setPosition(r.x, r.y);
        solid.setSize(r.width, r.height);
        solid.setStatic();
        solid.setShapeRectangle();
        solid.getFixtureDef().filter.categoryBits = Constants.WORLD_ENTITY;
        solid.getFixtureDef().filter.maskBits = Constants.PLAYER_ENTITY|Constants.ENEMY_ENTITY|
                Constants.ENEMY_BULLET_ENTITY|Constants.PLAYER_BULLET_ENTITY;
        solid.initializePhysics(world);
    }

    /**
     * Draw health bar
     * @param x represents x-coordinate of bar
     * @param y represents y-coordinate of bar
     * @param width represents width of bar
     * @param height represents height of bar
     * @param pct represents health of Character
     */
    protected void drawHealthBar(char type, float x, float y, int width, int height, float pct) {
        if (pct <= 0) pct = 0;

        // set colors
        if (type == 'h') {
            if (pct >= 50) shapeRenderer.setColor(Color.LIME);
            else if (pct < 50 && pct > 30) shapeRenderer.setColor(Color.YELLOW);
            else if (pct <= 30) shapeRenderer.setColor(Color.RED);
        } else if (type == 'w') {
            shapeRenderer.setColor(Color.CYAN);
        }

        float fill = (pct/100) * width;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(x, y, fill, height);
        shapeRenderer.end();
    }

    // create bullet
    protected void createBullet(char direction, Character c, char type, float x, float y, World world) {
        Bullet bullet = new Bullet(game, c, type, direction, x, y);
        bullet.setProperties(world);
        bullets.add(bullet);
        mainStage.addActor(bullet);
    }

    // methods required by Screen interface
    public void resize(int width, int height) 
    {    
        mainStage.getViewport().update(width, height, true); 
        uiStage.getViewport().update(width, height, true);
    }

    public void pause()   {  }

    public void resume()  {  }

    public void dispose() {
        bullets.clear();
        removeList.clear();
        enemyBar.destroy();
        enemyLogo.destroy();
        if (lastEnemy != null) lastEnemy.destroy();
    }

    public void show()    {  }

    public void hide()    {  }

    // methods required by InputProcessor interface
    public boolean keyDown(int keycode)
    {  return false;  }

    public boolean keyUp(int keycode)
    {  return false;  }

    public boolean keyTyped(char c) 
    {  return false;  }

    public boolean mouseMoved(int screenX, int screenY)
    {  return false;  }

    public boolean scrolled(int amount) 
    {  return false;  }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) 
    {  return false;  }

    public boolean touchDragged(int screenX, int screenY, int pointer) 
    {  return false;  }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) 
    {  return false;  }
}