package com.mygdx.game.Actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Actors.Enemies.Boss;
import com.mygdx.game.Actors.SuperActors.Character;
import com.mygdx.game.Base.BaseGame;
import com.mygdx.game.Utilities.Constants;
import com.mygdx.game.Utilities.SpriteSheet;

/**
 * Class used for Player Object.
 */
public class Player extends Character {
    // FIELDS
    // control
    private int shootCounter;
    private char stickDirection;
    private int stickCount;
    private int attackFrame;
    public int bulletCapacity;
    // game
    private BaseGame game;
    // sounds
    private Sound landingSound;
    private Sound airPunchSound;
    private Sound punchSound;

    public Player(BaseGame g) {
        super(Constants.PLAYER_DAMAGE);
        this.game = g;
        this.loadAssets();
        this.stickCount = 0;
        this.shootCounter = 0;
        this.attackFrame = 0;
        this.bulletCapacity = 15;
    }

    // SETUP METHODS
    @Override
    public void loadAssets() {
        // SpriteSheet
        SpriteSheet spriteSheet = new SpriteSheet("player/normal_spritesheet.png", this.game);

        // running animation
        int[][] runImage_coordinates = {{3, 330, 40, 34}, {59, 334, 34, 30}, {106, 336, 44, 28},
                {166, 333, 54, 30}, {233, 333, 48, 30}, {295, 334, 33, 30}, {342, 334, 44, 30},
                {397, 334, 52, 30}, {466, 332, 46, 31}};
        Animation<TextureRegion> runAnim = spriteSheet.getAnimation
                (runImage_coordinates, 0.09f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("run", runAnim);

        // halting animation
        int[][] haltImage_coordinates = {{6, 414, 48, 31}, {65, 414, 42, 31}, {126, 414, 30, 32}, {173, 413, 30, 33}};
        Animation<TextureRegion> haltAnim = spriteSheet.getAnimation
                (haltImage_coordinates, 0.05f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("halt", haltAnim);

        // jumping animation
        int[][] jumpImage_coordinates = {{16, 511, 24, 21}, {67, 491, 21, 24}, {126, 482, 24, 21},
                {172, 493, 39, 34}};
        Animation<TextureRegion> jumpAnim = spriteSheet.getAnimation
                (jumpImage_coordinates, 0.09f, Animation.PlayMode.NORMAL);
        this.storeAnimation("jump", jumpAnim);

        // wall_stance animation
        int[][] wallStanceImage_coordinates = {{10, 1018, 16, 32}, {35, 1018, 16, 32}, {61, 1018, 16, 32},
                {86, 1019, 16, 31}};
        Animation<TextureRegion> wallStanceAnim = spriteSheet.getAnimation
                (wallStanceImage_coordinates, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("wall_stance", wallStanceAnim);

        // lower animation
        int[][] lowerImage_coordinates = {{9, 626, 44, 31}, {63, 638, 58, 19}};
        Animation<TextureRegion> lowerAnim = spriteSheet.getAnimation
                (lowerImage_coordinates, 0.05f, Animation.PlayMode.NORMAL);
        this.storeAnimation("lower", lowerAnim);

        // ground-stance animation
        int[][] groundStanceImages_coordinates = {{190, 638, 56, 19}, {260, 638, 55, 19}, {327, 638, 55, 19},
                {397, 638, 55, 19}, {471, 638, 55, 19}, {544, 638, 56, 19}};
        Animation<TextureRegion> groundStanceAnim = spriteSheet.getAnimation
                (groundStanceImages_coordinates, 0.20f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("ground_stance", groundStanceAnim);

        // crawling animation
        int[][] crawlingImage_coordinates = {{11, 826, 58, 18}, {87, 826, 44, 18}, {145, 826, 49, 18},
                {212, 826, 55, 19}, {286, 826, 46, 20}, {352, 826, 52, 19}};
        Animation<TextureRegion> crawlAnim = spriteSheet.getAnimation
                (crawlingImage_coordinates, 0.12f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("crawl", crawlAnim);

        // rise animation
        int[][] riseImage_coordinates = {{9, 730, 38, 17}, {62, 730, 37, 17}, {119, 729, 33, 18},
                {175, 729, 33, 18}, {231, 702, 34, 43}, {282, 704, 34, 38}, {332, 719, 48, 28},
                {393, 719, 41, 28}, {451, 716, 30, 31}, {497, 715, 30, 33}, {547, 715, 30, 33}};
        Animation<TextureRegion> riseGroundAnimation = spriteSheet.getAnimation
                (riseImage_coordinates, 0.075f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("rise_from_ground", riseGroundAnimation);

        // turning-left animation
        int[][] turningLeftImageCoordinates = {{240, 416, 42, 32}, {290, 416, 45, 30}};
        Animation<TextureRegion> turningLeftAnimation = spriteSheet.getAnimation
                (turningLeftImageCoordinates, 0.075f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("turnLeft", turningLeftAnimation);

        // turning-right animation
        int[][] turningRightImageCoordinates = {{349, 415, 42, 31}, {407, 415, 35, 31}};
        Animation<TextureRegion> turningRightAnimation = spriteSheet.getAnimation
                (turningRightImageCoordinates, 0.075f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("turnRight", turningRightAnimation);

        // jab punch animation
        int[][] punchImage_coordinates = {{8, 1179, 36, 34}, {61, 1180, 50, 34}, {123, 1180, 49, 34},
                {181, 1180, 48, 33}, {240, 1181, 41, 32}, {295, 1183, 37, 30}, {346, 1180, 30, 33}};
        Animation<TextureRegion> punchAnim = spriteSheet.getAnimation
                (punchImage_coordinates, 0.05f, Animation.PlayMode.NORMAL);
        this.storeAnimation("punch", punchAnim);

        // upward kick animation
        int[][] upKickImage_coordinates = {{12, 1423, 35, 38}, {68, 1412, 34, 28}, {128, 1392, 41, 50},
                {209, 1383, 35, 58}, {276, 1386, 31, 54}, {337, 1409, 40, 38}, {405, 1424, 32, 39}};
        Animation<TextureRegion> upKickAnim = spriteSheet.getAnimation
                (upKickImage_coordinates, 0.05f, Animation.PlayMode.NORMAL);
        this.storeAnimation("upKick", upKickAnim);

        // uppercut animation
        int[][] uppercutImage_coordinates = {{7, 1574, 43, 33}, {62, 1572, 46, 35}, {144, 1542, 31, 60},
                {206, 1525, 20, 65}, {267, 1525, 20, 65}, {328, 1515, 30, 68}, {391, 1510, 30, 75},
                {454, 1515, 31, 72}, {510, 1534, 29, 61}, {561, 1547, 31, 51}};
        Animation<TextureRegion> uppercutAnim = spriteSheet.getAnimation
                (uppercutImage_coordinates, 0.045f, Animation.PlayMode.NORMAL);
        this.storeAnimation("uppercut", uppercutAnim);

        // air-kick animation
        int[][] airKickImage_coordinates = {{422, 1162, 47, 47}, {484, 1160, 46, 49}};
        Animation<TextureRegion> airKickAnim = spriteSheet.getAnimation
                (airKickImage_coordinates, 0.10f, Animation.PlayMode.NORMAL);
        this.storeAnimation("airKick", airKickAnim);

        // crawl-punch animation
        int[][] crawlPunchImage_coordinates = {{10, 1295, 58, 19}, {87, 1294, 52, 20}, {150, 1284, 45, 30},
                {218, 1280, 43, 34}, {277, 1281, 52, 33}, {349, 1287, 63, 28}, {437, 1296, 59, 18}};
        Animation<TextureRegion> crawlPunchAnim = spriteSheet.getAnimation
                (crawlPunchImage_coordinates, 0.05f, Animation.PlayMode.NORMAL);
        this.storeAnimation("crawlPunch", crawlPunchAnim);

        // shooting animation
        int[][] webShootImage_coordinates = {{12, 1764, 34, 39}, {61, 1764, 38, 41}, {108, 1769, 38, 38},
                {160, 1769, 45, 37}};
        Animation<TextureRegion> webShootAnim = spriteSheet.getAnimation
                (webShootImage_coordinates, 0.05f, Animation.PlayMode.NORMAL);
        this.storeAnimation("shoot", webShootAnim);

        // damage animation
        int[][] damageImage_coordinates = {{128, 2537, 32, 36}, {188, 2530, 33, 39}, {242, 2533, 35, 29},
                {295, 2532, 35, 26}, {350, 2533, 35, 26}, {407, 2533, 37, 26}, {461, 2532, 37, 27}, {511, 2535, 35, 36}};
        Animation<TextureRegion> damageImageAnim = spriteSheet.getAnimation
                (damageImage_coordinates, 0.075f, Animation.PlayMode.NORMAL);
        this.storeAnimation("damage", damageImageAnim);

        // death animation
        int[][] deathImage_coordinates = {{20, 2637, 37, 36}, {69, 2637, 36, 36}, {118, 2639, 33, 34},
                {165, 2644, 30, 29}, {217, 2642, 30, 31}, {266, 2642, 33, 31}, {315, 2642, 34, 31},
                {368, 2641, 33, 32}, {419, 2641, 32, 32}, {466, 2641, 40, 32}, {519, 2641, 42, 32},
                {573, 2647, 50, 26}, {18, 2718, 50, 16}, {88, 2724, 50, 10}};
        Animation<TextureRegion> deathAnim = spriteSheet.getAnimation
                (deathImage_coordinates, 0.10f, Animation.PlayMode.NORMAL);
        this.storeAnimation("death", deathAnim);

        // standing animation
        int[][] standImage_coordinates = {{8, 252, 29, 32}, {51, 252, 30, 32}, {96, 252, 30, 32},
                {137, 252, 30, 32}, {183, 252, 30, 32}, {227, 252, 30, 32}, {268, 252, 30, 32},
                {312, 252, 30, 32}};
        Animation<TextureRegion> standAnim = spriteSheet.getAnimation
                (standImage_coordinates, 0.25f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("stand", standAnim);

        // sound effects
        this.landingSound = this.game.getAssetManager().get("sounds/landing_sound.wav", Sound.class);
        this.airPunchSound = this.game.getAssetManager().get("sounds/missed_punch_sound.wav", Sound.class);
        this.punchSound = this.game.getAssetManager().get("sounds/punch_sound.wav", Sound.class);
    }

    @Override
    public void setProperties(World world) {
        this.setDynamic();
        this.setShapeRectangle();
        this.setPhysicsProperties(Constants.PLAYER_DENSITY, Constants.PLAYER_FRICTION,
                Constants.RESTITUTION);
        this.fixtureDef.filter.categoryBits = Constants.PLAYER_ENTITY;
        this.fixtureDef.filter.maskBits =
                Constants.WORLD_ENTITY|Constants.ENEMY_ENTITY|Constants.ENEMY_BULLET_ENTITY;
        this.setFixedRotation();
        this.initializePhysics(world);
    }

    // UPDATE METHODS
    @Override
    public void update() {
        if (this.getHealth() <= 0) this.setActiveAnimation("death");
        else {
            this.move();
            this.animate();
        }
    }

    private void move() {
        if (this.isSticking()) {
            this.setMaxSpeedY(Constants.PLAYER_MAX_CRAWL_SPEED);
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                this.setVelocity(0, this.getVelocity().y);
                this.applyForce(new Vector2(0, Constants.PLAYER_CRAWL_SPEED));
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                this.setVelocity(0, this.getVelocity().y);
                this.applyForce(new Vector2(0, -Constants.PLAYER_CRAWL_SPEED));
            } else {
                if (this.getStickDirection() == 'R') {
                    this.body.setLinearVelocity(9.81f, 0);
                    this.setScale(-1, 1);
                }
                if (this.getStickDirection() == 'L') {
                    this.body.setLinearVelocity(-9.81f, 0);
                    this.setScale(1, 1);
                }
            }
        } else if (this.getAnimationName().equals("ground_stance") || this.getAnimationName().equals("crawl")) {
            // move horizontally
            this.setMaxSpeedX(Constants.PLAYER_MAX_CRAWL_SPEED);
            this.setMaxSpeedY(120);
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                this.setScale(-1, 1);
                this.applyForce(new Vector2(-Constants.PLAYER_CRAWL_SPEED, 0));
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                this.setScale(1, 1);
                this.applyForce(new Vector2(Constants.PLAYER_CRAWL_SPEED, 0));
            }
        } else {
            // move horizontally
            this.setMaxSpeedX(Constants.PLAYER_MAX_RUN_SPEED);
            this.setMaxSpeedY(120);
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                if (this.getScaleX() > 0) this.setActiveAnimation("turnLeft");
                this.setScale(-1, 1);
                this.applyForce(new Vector2(-Constants.PLAYER_RUN_SPEED, 0));
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if (this.getScaleX() < 0) this.setActiveAnimation("turnRight");
                this.setScale(1, 1);
                this.applyForce(new Vector2(Constants.PLAYER_RUN_SPEED, 0));
            }
        }
    }

    private void animate() {
        // change stand animation
        if (this.getAnimationName().equals("stand")) {
            // set run animation
            if (this.getSpeed() > 0.1) {
                this.setActiveAnimation("run");
            }

            // set jump animation
            if (!this.isOnGround()) {
                this.setActiveAnimation("jump");
            }
        }

        // change run animation
        if (this.getAnimationName().equals("run")) {
            // set halt animation
            if (this.getSpeed() < 0.1) {
                this.setActiveAnimation("halt");
            }

            // set jump animation
            if (!this.isOnGround()) {
                this.setActiveAnimation("jump");
            }
        }

        // change halt animation
        if (this.getAnimationName().equals("halt")) {
            if (this.isAnimationFinished()) {
                this.setActiveAnimation("stand");
            }
        }

        /// change jump animation
        if (this.getAnimationName().equals("jump")) {
            if (this.isOnGround()) {
                this.setActiveAnimation("stand");
            }
            if (this.isSticking()) this.setActiveAnimation("wall_stance");
        }

        // change wall-stance animation
        if (this.getAnimationName().equals("wall_stance")) {
            // set transition crawling animation
            if (this.getVelocity().y != 0) this.setActiveAnimation("crawl");
        }

        // change lower transition-animation
        if (this.getAnimationName().equals("lower")) {
            if (this.isAnimationFinished()) {
                this.setActiveAnimation("ground_stance");
            }
        }

        // change ground-stance animation
        if (this.getAnimationName().equals("ground_stance")) {
            if (this.getSpeed() > 0.1) {
                this.setActiveAnimation("crawl");
            }
        }

        // change crawling animation
        if (this.getAnimationName().equals("crawl")) {
            if (this.isSticking()) {
                if (this.getStickDirection() == 'R') {
                    this.rotateBy(90);
                    if (this.getBody().getLinearVelocity().y > 0) this.setScale(1, 1);
                    else if (this.getBody().getLinearVelocity().y < 0) this.setScale(-1, 1);
                    else {
                        this.setRotation(0);
                        this.setActiveAnimation("wall_stance");
                    }
                } else if (this.getStickDirection() == 'L') {
                    this.rotateBy(270);
                    if (this.getBody().getLinearVelocity().y > 0) this.setScale(-1, 1);
                    else if (this.getBody().getLinearVelocity().y < 0) this.setScale(1, 1);
                    else {
                        this.setRotation(0);
                        this.setActiveAnimation("wall_stance");
                    }
                }
            } else {
                if (this.getSpeed() < 0.1) this.setActiveAnimation("ground_stance");
                if (this.getVelocity().y != 0) this.setActiveAnimation("jump");
            }
        }

        // change rise-from-ground transition-animation
        if (this.getAnimationName().equals("rise_from_ground")) {
            if (this.isAnimationFinished()) {
                this.setActiveAnimation("stand");
            }
        }

        // change turn left animation
        if (this.getAnimationName().equals("turnLeft") || this.getAnimationName().equals("turnRight")) {
            if (this.isAnimationFinished()) {
                if (this.getSpeed() > 0.3) {
                    this.setActiveAnimation("run");
                }
                if (this.getSpeed() < 0.1) {
                    this.setActiveAnimation("stand");
                }
            }
        }

        // change punch animation
        if (this.getAnimationName().equals("punch")) {
            if (this.isAnimationFinished()) {
                if (this.getTarget() != null && !(this.getTarget() instanceof Boss)) {
                    this.getTarget().setHealth(-25);
                    this.attackFrame = 1;
                    this.playPunchingSound();
                    this.setActiveAnimation("stand");
                } else {
                    this.playAirPunchSound();
                    this.setActiveAnimation("stand");
                }
            }
        }

        // change upKick animation
        if (this.getAnimationName().equals("upKick")) {
            if (this.isAnimationFinished()) {
                if (this.getTarget() != null) {
                    this.getTarget().setHealth(-25);
                    this.attackFrame = 2;
                    this.playPunchingSound();
                    this.setActiveAnimation("stand");
                } else {
                    this.playAirPunchSound();
                    this.setActiveAnimation("stand");
                }
            }
        }

        // change uppercut animation
        if (this.getAnimationName().equals("uppercut")) {
            if (this.isAnimationFinished()) {
                if (this.getTarget() != null) {
                    this.getTarget().setHealth(-25);
                    this.attackFrame = 0;
                    this.playPunchingSound();
                    this.setActiveAnimation("stand");
                } else {
                    this.playAirPunchSound();
                    this.setActiveAnimation("stand");
                }
            }
        }

        // change airKick animation
        if (this.getAnimationName().equals("airKick")) {
            if (this.getTarget() == null) {
                if (this.getScaleX() == 1) {
                    this.applyForce(new Vector2(1.5f, -1.0f));
                } else {
                    this.applyForce(new Vector2(-1.5f, -1.0f));
                }
            }

            if (this.isAnimationFinished()) {
                if (this.getTarget() != null) {
                    this.getTarget().setHealth(-5);
                    this.playPunchingSound();
                    if (this.getScaleX() == 1) this.applyImpulse(new Vector2(-2f, 0.5f));
                    else this.applyImpulse(new Vector2(2f, 0.5f));
                    this.setActiveAnimation("jump");
                } else {
                    this.playAirPunchSound();
                }
            }

            if (this.isOnGround()) {
                this.setActiveAnimation("stand");
            }
        }

        // change crawl punch animation
        if (this.getAnimationName().equals("crawlPunch")) {
            if (this.isAnimationFinished()) {
                this.setActiveAnimation("ground_stance");
            }
        }

        // change shoot animation
        if (this.getAnimationName().equals("shoot")) {
            if (this.isAnimationFinished()) {
                this.shootCounter++;
                if (this.shootCounter > 15) {
                    this.setActiveAnimation("stand");
                    this.shootCounter = 0;
                }
            }
        }

        // change damage animation
        if (this.getAnimationName().equals("damage")) {
            if (this.isAnimationFinished()) this.setActiveAnimation("stand");
        }
    }

    @Override
    public Player clone() {
        Player newbie = this;
        newbie.copy(this);
        return newbie;
    }

    // FIELD METHODS
    // stick
    public boolean isSticking() { return (this.stickCount > 0); }
    public void adjustStickCount(int i) { this.stickCount += i; }
    public char getStickDirection() { return this.stickDirection; }
    public void setStickDirection(char s) { this.stickDirection = s; }
    // attacking
    public int getAttackFrame() { return this.attackFrame; }
    public void setAttackFrame(int f) { this.attackFrame = f; }
    // sound effects
    public void playLandingSound() { this.landingSound.play(0.5f); }
    public void playAirPunchSound() { this.airPunchSound.play(0.75f); }
    public void playPunchingSound() { this.punchSound.play(); }
}