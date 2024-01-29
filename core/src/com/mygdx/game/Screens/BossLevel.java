package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.mygdx.game.Actors.Enemies.Boss;
import com.mygdx.game.Actors.Explosion;
import com.mygdx.game.Actors.SuperActors.BaseActor;
import com.mygdx.game.Actors.SuperActors.Box2DActor;
import com.mygdx.game.Actors.Bullet;
import com.mygdx.game.Actors.Player;
import com.mygdx.game.Base.BaseGame;
import com.mygdx.game.Base.BaseScreen;
import com.mygdx.game.Utilities.GameUtils;

import java.util.ArrayList;

import static com.badlogic.gdx.Input.*;

/**
 * The second level of the game (a.k.a. the boss level).
 */
public class BossLevel extends BaseScreen {
    // CONSTANTS
    // game world dimensions
    private final int WORLD_WIDTH = 640;
    private final int WORLD_HEIGHT = 416;

    // DECLARE VARIABLES
    // world
    private World world;

    // tilemap
    private TiledMap tiledMap;
    private OrthographicCamera tiledCamera;
    private TiledMapRenderer tiledMapRenderer;
    private int[] backgroundLayer = {0};
    private int[] tileLayer = {1};

    // characters
    private Boss boss;

    // game
    private BaseGame game;
    private ArrayList<Explosion> explosions;
    private BaseActor winText;
    private boolean win;

    /**
     * Create a new BossLevel
     * @param g represents the BaseGame used to launch the game
     */
    BossLevel(BaseGame g, float health) {
        super(g);
        this.game = g;
        player = new Player(game);
        player.setExplicitHealth(health);

        // win message
        winText = new BaseActor();
        winText.setTexture(game.getAssetManager().get("you-win.png", Texture.class));
        winText.setPosition( 170, 60 );
        winText.setVisible( false );
        uiStage.addActor( winText );

        win = false;
    }

    @Override
    public void create() {
        explosions = new ArrayList<Explosion>();
    }

    /**
     * Show what's on the screen
     */
    @Override
    public void show() {
        // load assets from asset manager
        loadAssets();
        // create world
        world = new World(new Vector2(0, -9.8f), true);
        // background image provided by tilemap

        // create boss
        boss = new Boss(game, player);
        mainStage.addActor(boss);

        // boss logo
        BaseActor logo = new BaseActor();
        logo.setSize(20, 20);
        logo.setPosition(viewWidth/2 + 75, 10);
        logo.region = boss.logo;
        uiStage.addActor(logo);

        // create player
        mainStage.addActor(player);

        // setup tilemap
        setupTileMap();

        // setup player properties
        player.setProperties(world);

        // setup boss properties
        boss.setProperties(world);

        // check collisions
        checkCollisions();

        mainStage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1)));

    }

    /**
     * Update contents on screen.
     * @param dt represents the time elapsed.
     */
    @Override
    public void update(float dt) {
        world.step(1/60f, 6, 2);
        // remove items
        for (Box2DActor ba : removeList) {
            if (ba instanceof Explosion) explosions.remove(ba);
            ba.destroy();
            world.destroyBody(ba.getBody());
        }
        removeList.clear();

        // update player
        player.update();

        // check for player death
        if (player.getAnimationName().equals("death")) {
            if (player.isAnimationFinished()) {
                this.game.getAssetManager().get("sounds/boss_track.mp3", Music.class).stop();
                game.setScreen(new GameOverScreen(game));
                boss.destroy();
                player.destroy();
                dispose();
            }
        }

        // update boss
        boss.update();

        // check for boss death
        if (boss.getHealth() <= 0 && boss != null && !win) {
            win = true;
            Action spinShrinkFadeOut = Actions.parallel(
                    Actions.alpha(1),         // set transparency value
                    Actions.rotateBy(360, 1), // rotation amount, duration
                    Actions.scaleTo(0,0, 2),  // x amount, y amount, duration
                    Actions.fadeOut(1)        // duration of fade in
            );
            boss.addAction( spinShrinkFadeOut );

            Action fadeInColorCycleForever = Actions.sequence(
                    Actions.alpha(0),   // set transparency value
                    Actions.show(),     // set visible to true
                    Actions.fadeIn(2),  // duration of fade out
                    Actions.forever(
                            Actions.sequence(
                                    // color shade to approach, duration
                                    Actions.color( new Color(1,0,0,1), 1 ),
                                    Actions.color( new Color(0,0,1,1), 1 )
                            )
                    )
            );
            winText.addAction( fadeInColorCycleForever );
            boss.destroy();
            dispose();
        }

        // add Boss bullet
        if (boss.getAnimationName().equals("throwBomb")) {
            if (boss.isAnimationFinished()) {
                super.createBullet('D', boss,'b',
                        boss.getX() + (boss.getWidth()/2),
                        boss.getY() - 10, world);
                boss.resetAnimation("throwBomb");
                boss.bombCounter += 1;
            }
        }

        // update bullets
        for (Bullet bullet : bullets) {
            bullet.move();
        }
        // update explosions
        for (Explosion explosion : explosions) {
            if (explosion.isAnimationFinished()) {
                removeList.add(explosion);
            }
        }
    }

    /**
     * Render contents on screen
     * @param dt represents time elapsed
     */
    @Override
    public void render(float dt) {
        uiStage.act(dt);
        mainStage.act(dt);
        update(dt);

        // render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // setup main camera
        Camera mainCamera = mainStage.getCamera();
        mainCamera.position.x = player.getX() + player.getOriginX();
        mainCamera.position.y = player.getY() + player.getOriginY();
        // bound camera position
        mainCamera.position.x = MathUtils.clamp(mainCamera.position.x, viewWidth / 2,
                WORLD_WIDTH - viewWidth/2);
        mainCamera.position.y = MathUtils.clamp(mainCamera.position.y, viewHeight/2,
                WORLD_HEIGHT - viewHeight/2);
        mainCamera.update();

        // render tiled-camera
        renderTileMap(mainCamera);

        // draw
        mainStage.draw();
        uiStage.draw();

        // HEALTH BARS
        // player
        super.drawHealthBar('h', 87, 696, 72, 12, player.getHealth());
        // boss
        super.drawHealthBar('h', 520, 25, 100, 30, boss.getHealth());
        // web fluid
        super.drawHealthBar('w', 90, 678, 400, 8, player.bulletCapacity);
    }

    /**
     * Check for single key-press.
     * @param keycode represents the key pressed.
     * @return false
     */
    @Override
    public boolean keyDown(int keycode) {
        if (player.isOnGround()) {
            // set crawling and shooting
            if (player.getAnimationName().equals("stand") || player.getAnimationName().equals("run")) {
                if (keycode == Keys.S) player.setActiveAnimation("lower");
                if (keycode == Keys.L && player.bulletCapacity > 0) {
                    player.setActiveAnimation("shoot");
                    super.createBullet(player.getScaleX() == 1 ? 'F' : 'B', player, 'w',
                            player.getScaleX() == 1 ? player.getX() + player.getWidth() + 10 :
                                    player.getX() - 10, player.getY() + (player.getHeight()/2), world);
                    player.bulletCapacity--;
                }
            }
            // set transition animation
            if (player.getAnimationName().equals("ground_stance")) {
                if (keycode == Keys.W) player.setActiveAnimation("rise_from_ground");
            } else {
                // jump
                if (keycode == Keys.K) {
                    Vector2 jumpVec = new Vector2(0, 0.5f);
                    player.applyImpulse(jumpVec);
                }
                // attack
                if (keycode == Keys.J) {
                    if (player.getAttackFrame() == 0) {
                        player.setActiveAnimation("punch");
                    }
                    else if (player.getAttackFrame() == 1) {
                        player.applyImpulse(new Vector2(0, 0.15f));
                        player.setActiveAnimation("upKick");
                    }
                    else if (player.getAttackFrame() == 2) {
                        player.setActiveAnimation("uppercut");
                    }
                }
            }
        } else if (player.isSticking()) {
            // jump
            if (keycode == Keys.K) {
                if (player.getStickDirection() == 'R') {
                    player.getBody().setGravityScale(1.0f);
                    player.applyImpulse(new Vector2(-10f, 2.5f));
                    player.adjustStickCount(-1);
                }
                else if (player.getStickDirection() == 'L') {
                    player.getBody().setGravityScale(1.0f);
                    player.applyImpulse(new Vector2(10f, 2.5f));
                    player.adjustStickCount(-1);
                }
            }
        } else if (!player.isOnGround() && !player.isSticking()) {
            if (keycode == Keys.J){
                player.setActiveAnimation("airKick");
            }
        }

        return false;
    }

    /**
     * Get assets from AssetManager.
     */
    private void loadAssets() {
        // tiled-map
        tiledMap = this.game.getAssetManager().get("map/boss_level.tmx");

        // music
        game.getAssetManager().get("sounds/boss_track.mp3", Music.class).setLooping(true);
        game.getAssetManager().get("sounds/boss_track.mp3", Music.class).play();

        // health bar
        // create new boss health bar
        BaseActor bossBar = new BaseActor();
        bossBar.setTexture(this.game.getAssetManager().get("enemies/boss_health_bar.png", Texture.class));
        bossBar.setPosition(viewWidth/2, 10);
        bossBar.setSize(59, 22);

        uiStage.addActor(bossBar);
    }

    /**
     * Load objects from TileMap
     * Setup TileMap camera
     */
    private void setupTileMap() {
        // setup camera
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tiledCamera = new OrthographicCamera();
        tiledCamera.setToOrtho(false, viewWidth, viewHeight);
        tiledCamera.update();

        // get tiled objects
        MapObjects objects = tiledMap.getLayers().get("ObjectData").getObjects();
        for (MapObject object : objects) {
            String name = object.getName();
            // all objects assumed to be rectangles
            RectangleMapObject rectangleObject = (RectangleMapObject) object;
            Rectangle r = rectangleObject.getRectangle();

            if (name.equals("player")) player.setPosition(r.x, r.y);
            else if (name.equals("boss")) boss.setPosition(r.x, r.y);
        }

        // get physics-based tiled objects
        objects = tiledMap.getLayers().get("PhysicsData").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) super.addSolid((RectangleMapObject) object, world);
            else System.err.println("Unknown PhysicsData object.");
        }
    }

    /**
     * Render contents of tilemap visible on screen.
     * @param mainCamera represents the mainCamera used for rendering.
     */
    private void renderTileMap(Camera mainCamera) {
        tiledCamera.position.x = mainCamera.position.x/4 + WORLD_WIDTH/4;
        tiledCamera.position.y = mainCamera.position.y/4 + WORLD_HEIGHT/4;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);

        tiledCamera.position.x = mainCamera.position.x;
        tiledCamera.position.y = mainCamera.position.y;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);
        tiledMapRenderer.render(backgroundLayer);
        tiledMapRenderer.render(tileLayer);
    }

    /**
     * Check for collisions occurring in world (Box2D level)
     */
    private void checkCollisions() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                // objects
                Object objP;
                Object objB;
                Object objS;
                Object objE;

                // PLAYER
                // bottom fixture
                objP = GameUtils.getContactObject(contact, Player.class, "bottom");
                if (objP != null) {
                    objB = GameUtils.getContactObject(contact, Boss.class, "main");
                    if (objB == null) {
                        Player p = (Player) objP;
                        p.adjustGroundCount(1);
                    }
                }
                // right fixture
                objP = GameUtils.getContactObject(contact, Player.class, "right");
                if (objP != null) {
                    objB = GameUtils.getContactObject(contact, Boss.class, "main");
                    if (objB == null) {
                        Player p = (Player) objP;
                        if (!p.isOnGround()) {
                            p.getBody().setGravityScale(0);
                            p.setStickDirection('R');
                            if (!p.isSticking()) p.getBody().setLinearVelocity(0, 0);
                            p.adjustStickCount(1);
                        }
                    }
                }
                // left fixture
                objP = GameUtils.getContactObject(contact, Player.class, "left");
                if (objP != null) {
                    objB = GameUtils.getContactObject(contact, Boss.class, "main");
                    if (objB == null) {
                        Player p = (Player) objP;
                        if (!p.isOnGround()) {
                            p.getBody().setGravityScale(0);
                            p.setStickDirection('L');
                            if (!p.isSticking()) p.getBody().setLinearVelocity(0, 0);
                            p.adjustStickCount(1);
                        }
                    }
                }

                // BOSS
                // left fixture
                objB = GameUtils.getContactObject(contact, Boss.class, "left");
                if (objB != null && !(objB instanceof Player)) {
                    Boss b = (Boss) objB;
                    b.direction = 'F';
                }

                // right fixture
                objB = GameUtils.getContactObject(contact, Boss.class, "right");
                if (objB != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    if (objP == null) {
                        Boss b = (Boss) objB;
                        b.direction = 'B';
                    }
                }

                // enemy collision
                objB = GameUtils.getContactObject(contact, Boss.class, "main");
                if (objB != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    if (objP != null) {
                        Player p = (Player) objP;
                        Boss b = (Boss) objB;
                        p.setTarget(b);
                        b.setTarget(p);
                    }
                }

                // BULLET
                objS = GameUtils.getContactObject(contact, Bullet.class, "main");
                if (objS != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    if (objP != null) {
                        Player p = (Player) objP;
                        p.setHealth(-5);
                    }
                    Bullet b = (Bullet) objS;
                    removeList.add(b);
                    bullets.remove(b);
                }

                // EXPLOSION
                objE = GameUtils.getContactObject(contact, Explosion.class);
                if (objE != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    if (objP != null) {
                        Player p = (Player) objP;
                        p.setHealth(-10);
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
                // objects
                Object objP;
                Object objB;
                Object objS;
                Object objE;

                // PLAYER
                // bottom fixture
                objP = GameUtils.getContactObject(contact, Player.class, "bottom");
                if (objP != null) {
                    objB = GameUtils.getContactObject(contact, Boss.class, "main");
                    if (objB == null) {
                        Player p = (Player) objP;
                        p.adjustGroundCount(-1);
                    }
                }
                // right fixture
                objP = GameUtils.getContactObject(contact, Player.class, "right");
                if (objP != null) {
                    Player p = (Player) objP;
                    if (p.isSticking()) {
                        p.getBody().setGravityScale(1.0f);
                        if (p.getVelocity().y > 0) p.applyImpulse(new Vector2(0, 0.20f));
                        p.adjustStickCount(-1);
                    }
                }
                // left fixture
                objP = GameUtils.getContactObject(contact, Player.class, "left");
                if (objP != null) {
                    Player p = (Player) objP;
                    if (p.isSticking()) {
                        p.getBody().setGravityScale(1.0f);
                        if (p.getVelocity().y > 0) p.applyImpulse(new Vector2(0, 0.05f));
                        p.adjustStickCount(-1);
                    }
                }

                // BOSS
                // player contact
                objB = GameUtils.getContactObject(contact, Boss.class, "main");
                if (objB != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    if (objP != null) {
                        Player p = (Player) objP;
                        p.setAttackFrame(0);
                        p.setTarget(null);
                    }
                }

                // BULLET
                objS = GameUtils.getContactObject(contact, Bullet.class, "main");
                if (objS != null) {
                    Bullet b = (Bullet) objS;
                    Explosion explosion = new Explosion(game, b.getX(), b.getY(), 32, 33);
                    explosion.setProperties(world);
                    explosions.add(explosion);
                    mainStage.addActor(explosion);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }
}
