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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.mygdx.game.Actors.Bullet;
import com.mygdx.game.Actors.Player;
import com.mygdx.game.Actors.Enemies.*;
import com.mygdx.game.Actors.SuperActors.BaseActor;
import com.mygdx.game.Actors.SuperActors.Box2DActor;
import com.mygdx.game.Actors.SuperActors.Character;
import com.mygdx.game.Base.BaseGame;
import com.mygdx.game.Base.BaseScreen;
import com.mygdx.game.Utilities.GameUtils;

import java.util.ArrayList;

import static com.badlogic.gdx.Input.Keys;

/**
 * This is the first level of the game
 * @author Arib Hussain
 */
public class Level1 extends BaseScreen {
    // CONSTANTS
    // game world dimensions
    private final int WORLD_WIDTH = 1920;
    private final int WORLD_HEIGHT = 1080;

    // DECLARE VARIABLES
    private World world;

    // tilemap
    private TiledMap tiledMap;
    private OrthographicCamera tiledCamera;
    private TiledMapRenderer tiledMapRenderer;
    private int[] backgroundLayer = {0};
    private int[] building_backgroundLayer = {1};
    private int[] extrasLayer = {2};
    private int[] tileLayer = {3};

    // game
    private ArrayList<Character> enemies;

    public Level1(BaseGame g) {
        super(g);
    }

    /**
     * Initialize variables
     */
    @Override
    public void create() {
        enemies = new ArrayList<Character>();
    }

    /**
     * Show contents on screen
     */
    @Override
    public void show() {
        // load assets from asset manager
        loadAssets();

        // create world
        world = new World(new Vector2(0, -9.8f), true);
        // background image provided by tilemap

        // create control image
        showControls();

        player = new Player(game);
        mainStage.addActor(player);

        // setup tilemap
        setupTileMap();

        // setup player
        player.setProperties(world);
        // get collisions
        checkCollisions();
    }

    /**
     * Updates objects on screen.
     * @param dt represents time.
     */
    @Override
    public void update(float dt) {
        world.step(1/60f, 6, 2);

        // remove items
        for (Box2DActor ba : removeList) {
            if (ba instanceof Bullet) bullets.remove(ba);
            if (ba instanceof Character) enemies.remove(ba);
            ba.destroy();
            world.destroyBody(ba.getBody());
        }
        removeList.clear();

        // update player
        player.update();

        // transition to boss level
        if (player.getY() <= 155 && player.getX() >= 290 && player.getX() <= 600 &&
        player.getHealth() > 0) {
            mainStage.addAction(Actions.sequence(Actions.fadeOut(0.50f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new BossLevel(game, player.getHealth()));
                    game.getAssetManager().get("sounds/level_track.mp3", Music.class).stop();
                    dispose();
                }
            })));
        }

        // update enemies
        for (Character enemy : enemies) {
            enemy.update();
            // check for death
            if (enemy.getAnimationName().equals("death")) {
                if (enemy.isAnimationFinished()) {
                    removeList.add(enemy);
                    enemyBar.setVisible(false);
                    enemyLogo.setVisible(false);
                    lastEnemy = null;
                }
            }
        }

        // check for death
        if (player.getAnimationName().equals("death")) {
            if (player.isAnimationFinished()) {
                this.game.getAssetManager().get("sounds/level_track.mp3", Music.class).stop();
                game.setScreen(new GameOverScreen(game));
                enemies.clear();
                player.destroy();
                dispose();
            }
        }

        // update bullets
        for (Bullet bullet : bullets) {
            bullet.move();
        }
    }

    /**
     * Renders objects onto screen
     * @param dt represents time.
     */
    @Override
    public void render(float dt) {
        uiStage.act(dt);
        mainStage.act(dt);
        update(dt);

        // render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Camera mainCamera = mainStage.getCamera();
        mainCamera.position.x = player.getX() + player.getOriginX();
        mainCamera.position.y = player.getY() + player.getOriginY();
        // bound character to layout
        mainCamera.position.x = MathUtils.clamp(mainCamera.position.x, viewWidth / 2,
                WORLD_WIDTH - viewWidth/2);
        mainCamera.position.y = MathUtils.clamp(mainCamera.position.y, viewHeight/2,
                WORLD_HEIGHT - viewHeight/2);
        mainCamera.update();

        // tiled-camera
        renderTileMap(mainCamera);

        // draw
        mainStage.draw();
        uiStage.draw();
        // draw player health bar
        super.drawHealthBar('h', 87, 696, 72, 12, player.getHealth());
        // draw web fluid bar
        super.drawHealthBar('w', 90, 678, 400, 8, player.bulletCapacity);
        // draw enemy health bar
        if (enemyBar.isVisible() && lastEnemy != null)
            super.drawHealthBar('h', 795, 696, 100,
                12, lastEnemy.getHealth());
    }

    /**
     * Responsible for detecting individual key presses.
     * @param keycode represents the key that has been pressed.
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
     * Method that showcases controls to screen
     */
    private void showControls() {
        BaseActor controls = new BaseActor();
        controls.setTexture(game.getAssetManager().get("controls.png", Texture.class));
        controls.setSize(131, 44);
        controls.setPosition(20, 150);
        mainStage.addActor(controls);
    }

    /**
     * Get assets from asset manager
     */
    private void loadAssets() {
        // music
        this.game.getAssetManager().get("sounds/level_track.mp3", Music.class).setLooping(true);
        this.game.getAssetManager().get("sounds/level_track.mp3", Music.class).play();

        // tiled-map
        tiledMap = this.game.getAssetManager().get("map/map_level_1.tmx");
    }

    /**
     * Sets up the tilemap by setting the renderer and the camera.
     * Also, objects in the TileMap both physics based and static are identified
     * and added appropriately.
     */
    private void setupTileMap() {
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tiledCamera = new OrthographicCamera();
        tiledCamera.setToOrtho(false, viewWidth, viewHeight);
        tiledCamera.update();

        int[][] enemyRanges = {
                {416, 672}, {736, 896}, {960, 1180}, {1312, 1472}, {1408, 1568}, {545, 762}, {480, 640},
                {415, 415}, {380, 512}, {380, 512}, {380, 512}, {575, 705}, {1280, 1376}, {1408, 1504}, {1376, 1567},
                {1216, 1344}
        };

        MapObjects objects = tiledMap.getLayers().get("ObjectData").getObjects();
        int counter = 0;
        for (MapObject object : objects) {
            String name = object.getName();
            // all object data assumed to be rectangles
            RectangleMapObject rectangleObject = (RectangleMapObject) object;
            Rectangle r = rectangleObject.getRectangle();

            if (name.equals("player")) player.setPosition(r.x, r.y);
            else if (name.equals("punch_enemy")) {
                PunchEnemy enemy = new PunchEnemy(game, enemyRanges[counter][0], enemyRanges[counter][1]);
                enemy.setPosition(r.x, r.y);
                mainStage.addActor(enemy);
                enemy.setProperties(world);
                enemies.add(enemy);
                counter++;
            } else if (name.equals("dagger_enemy")) {
                DaggerEnemy enemy = new DaggerEnemy(game, enemyRanges[counter][0], enemyRanges[counter][1]);
                enemy.setPosition(r.x, r.y);
                mainStage.addActor(enemy);
                enemy.setProperties(world);
                enemies.add(enemy);
                counter++;
            } else if (name.equals("hammer_enemy")) {
                HammerEnemy enemy = new HammerEnemy(game, enemyRanges[counter][0], enemyRanges[counter][1]);
                enemy.setPosition(r.x, r.y);
                mainStage.addActor(enemy);
                enemy.setProperties(world);
                enemies.add(enemy);
                counter++;
            }
            else
                System.err.println("Unknown tilemap object: " + name);
        }

        objects = tiledMap.getLayers().get("PhysicsData").getObjects();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject)
                super.addSolid((RectangleMapObject) object, world);
            else
                System.err.println("Unknown PhysicsData object.");
        }
    }

    /**
     * Renders the tilemap layers and positions the tilemap camera to move in relation
     * with the mainCamera.
     * @param mainCamera represents the mainCamera of the game.
     */
    private void renderTileMap(Camera mainCamera) {
        tiledCamera.position.x = mainCamera.position.x/4 + WORLD_WIDTH/4;
        tiledCamera.position.y = mainCamera.position.y/4 + WORLD_HEIGHT/4;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);
        tiledMapRenderer.render(backgroundLayer);

        tiledCamera.position.x = mainCamera.position.x;
        tiledCamera.position.y = mainCamera.position.y;
        tiledCamera.update();
        tiledMapRenderer.setView(tiledCamera);
        tiledMapRenderer.render(building_backgroundLayer);
        tiledMapRenderer.render(extrasLayer);
        tiledMapRenderer.render(tileLayer);
    }

    /**
     * Checks collisions occurring in Box2D world.
     */
    private void checkCollisions() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                // objects
                Object objP;
                Object objEP;
                Object objEH;
                Object objED;
                Object objS;

                // PLAYER
                // bottom fixture
                objP = GameUtils.getContactObject(contact, Player.class, "bottom");
                if (objP != null) {
                    Player p = (Player) objP;
                    p.adjustGroundCount(1);
                    p.playLandingSound();
                }
                // right fixture
                objP = GameUtils.getContactObject(contact, Player.class, "right");
                if (objP != null && !objP.equals(Character.class)) {
                    Player p = (Player) objP;
                    if (!p.isOnGround()) {
                        p.getBody().setGravityScale(0);
                        p.setStickDirection('R');
                        if (!p.isSticking()) p.getBody().setLinearVelocity(0, 0);
                        p.adjustStickCount(1);
                    }
                }
                // left fixture
                objP = GameUtils.getContactObject(contact, Player.class, "left");
                if (objP != null && !objP.equals(Character.class)) {
                    Player p = (Player) objP;
                    if (!p.isOnGround()) {
                        p.getBody().setGravityScale(0);
                        p.setStickDirection('L');
                        if (!p.isSticking()) p.getBody().setLinearVelocity(0, 0);
                        p.adjustStickCount(1);
                    }
                }

                // ENEMY
                // punch enemies
                objEP = GameUtils.getContactObject(contact, PunchEnemy.class, "main");
                if (objEP != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    objS = GameUtils.getContactObject(contact, Bullet.class, "main");
                    if (objP != null) {
                        PunchEnemy e = (PunchEnemy) objEP;
                        Player p = (Player) objP;
                        // get health bar
                        enemyBar.setVisible(true);
                        enemyLogo.region = e.logo;
                        enemyLogo.setSize(16, 26);
                        enemyLogo.setVisible(true);
                        lastEnemy = e;

                        // move characters
                        if (p.getX() < e.getX()) e.direction = 'B';
                        else e.direction = 'F';
                        e.setVelocity(new Vector2(0, 0));
                        p.setVelocity(new Vector2(0, 0));
                        e.setTarget(p);
                        p.setTarget(e);
                    }
                    if (objS != null) {
                        PunchEnemy e = (PunchEnemy) objEP;
                        Bullet b = (Bullet) objS;

                        // get health bar
                        enemyBar.setVisible(true);
                        enemyLogo.region = e.logo;
                        enemyLogo.setSize(16, 26);
                        enemyLogo.setVisible(true);
                        lastEnemy = e;

                        e.setHealth(-5);
                        removeList.add(b);

                    }
                }

                // hammer enemies
                objEH = GameUtils.getContactObject(contact, HammerEnemy.class, "main");
                if (objEH != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    objS = GameUtils.getContactObject(contact, Bullet.class, "main");
                    if (objP != null) {
                        HammerEnemy e = (HammerEnemy) objEH;
                        Player p = (Player) objP;
                        // get health bar
                        enemyBar.setVisible(true);
                        enemyLogo.region = e.logo;
                        enemyLogo.setSize(16, 26);
                        enemyLogo.setVisible(true);
                        lastEnemy = e;

                        // move characters
                        if (p.getX() < e.getX()) e.direction = 'B';
                        else e.direction = 'F';
                        e.setVelocity(new Vector2(0, 0));
                        p.setVelocity(new Vector2(0, 0));
                        e.setTarget(p);
                        p.setTarget(e);
                    }
                    if (objS != null) {
                        HammerEnemy e = (HammerEnemy) objEH;
                        Bullet b = (Bullet) objS;

                        // get health bar
                        enemyBar.setVisible(true);
                        enemyLogo.region = e.logo;
                        enemyLogo.setSize(16, 26);
                        enemyLogo.setVisible(true);
                        lastEnemy = e;

                        e.setHealth(-5);
                        removeList.add(b);

                    }
                }

                // dagger enemies
                objED = GameUtils.getContactObject(contact, DaggerEnemy.class, "main");
                if (objED != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    objS = GameUtils.getContactObject(contact, Bullet.class, "main");
                    if (objP != null) {
                        DaggerEnemy e = (DaggerEnemy) objED;
                        Player p = (Player) objP;
                        // get health bar
                        enemyBar.setVisible(true);
                        enemyLogo.region = e.logo;
                        enemyLogo.setSize(16, 26);
                        enemyLogo.setVisible(true);
                        lastEnemy = e;

                        // move characters
                        if (p.getX() < e.getX()) e.direction = 'B';
                        else e.direction = 'F';
                        e.setVelocity(new Vector2(0, 0));
                        p.setVelocity(new Vector2(0, 0));
                        e.setTarget(p);
                        p.setTarget(e);
                    }
                    if (objS != null) {
                        DaggerEnemy e = (DaggerEnemy) objED;
                        Bullet b = (Bullet) objS;

                        // get health bar
                        enemyBar.setVisible(true);
                        enemyLogo.region = e.logo;
                        enemyLogo.setSize(16, 26);
                        enemyLogo.setVisible(true);
                        lastEnemy = e;

                        e.setHealth(-5);
                        removeList.add(b);
                    }
                }

                // check bullet collision(s)
                objS = GameUtils.getContactObject(contact, Bullet.class, "main");
                if (objS != null) {
                    Bullet b = (Bullet) objS;
                    removeList.add(b);
                }
            }

            @Override
            public void endContact(Contact contact) {
                // initialize object
                Object objP;
                Object objEP;
                Object objEH;
                Object objED;

                // bottom fixture
                objP = GameUtils.getContactObject(contact, Player.class, "bottom");
                if (objP != null) {
                    Player p = (Player) objP;
                    p.adjustGroundCount(-1);
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

                // ENEMIES
                // punch enemy
                objEP = GameUtils.getContactObject(contact, PunchEnemy.class, "main");
                if (objEP != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    if (objP != null) {
                        PunchEnemy e = (PunchEnemy) objEP;
                        Player p = (Player) objP;
                        e.setTarget(null);
                        p.setTarget(null);
                        p.setAttackFrame(0);
                    }
                }

                // hammer enemy
                objEH = GameUtils.getContactObject(contact, HammerEnemy.class, "main");
                if (objEH != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    if (objP != null) {
                        HammerEnemy e = (HammerEnemy) objEH;
                        Player p = (Player) objP;
                        e.setTarget(null);
                        p.setTarget(null);
                        p.setAttackFrame(0);
                    }
                }

                // dagger enemy
                objED = GameUtils.getContactObject(contact, DaggerEnemy.class, "main");
                if (objED != null) {
                    objP = GameUtils.getContactObject(contact, Player.class, "main");
                    if (objP != null) {
                        DaggerEnemy e = (DaggerEnemy) objED;
                        Player p = (Player) objP;
                        e.setTarget(null);
                        p.setTarget(null);
                        p.setAttackFrame(0);
                    }
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