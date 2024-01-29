package com.mygdx.game.Actors.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Actors.Player;
import com.mygdx.game.Actors.SuperActors.Character;
import com.mygdx.game.Base.BaseGame;
import com.mygdx.game.Utilities.Constants;
import com.mygdx.game.Utilities.SpriteSheet;

/**
 * Boss object used in LibGDX
 */
public class Boss extends Character {
    // FIELDS
    // control
    private float maxX;
    private float maxY;
    private int bombCount;
    public char direction;
    public int bombCounter;
    // game
    private BaseGame game;
    private Player player;
    public TextureRegion logo;

    /**
     * Boss constructer that sets up object.
     * @param g represents the game.
     * @param p represents the player.
     */
    public Boss(BaseGame g, Player p) {
        super(Constants.PUNCH_ENEMY_DAMAGE);
        this.game = g;
        this.loadAssets();
        this.setSize(Constants.CHARACTER_WIDTH, Constants.CHARACTER_HEIGHT);
        this.bombCounter = 0;
        this.direction = 'B';
        this.player = p;
    }

    // SETUP METHODS
    /**
     * Load sprites from SpriteSheets.
     */
    @Override
    public void loadAssets() {
        // SpriteSheet
        SpriteSheet spriteSheet = new SpriteSheet("enemies/goblin_sprite_sheet.png", this.game);

        // fly animation
        int[][] flyImage_coordinates = {{37, 347, 31, 45}, {73, 347, 31, 47}, {110, 347, 33, 48}};
        Animation<TextureRegion> flyAnim = spriteSheet.getAnimation
                (flyImage_coordinates, 0.10f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("fly", flyAnim);

        // throw bomb animation
        int[][] throwBombImage_coordinates = {{149, 344, 27, 50}, {182, 336, 37, 59}, {224, 338, 30, 56},
                {260, 340, 40, 52}};
        Animation<TextureRegion> throwBombAnim = spriteSheet.getAnimation
                (throwBombImage_coordinates, 0.25f, Animation.PlayMode.LOOP);
        this.storeAnimation("throwBomb", throwBombAnim);

        // walk animation
        int[][] walkImage_coordinates = {{45, 7, 28, 52}, {78, 7, 30, 52}, {112, 5, 30, 54}, {146, 6, 27, 53},
                {178, 6, 27, 53}, {210, 6, 25, 53}, {240, 6, 30, 53}, {277, 6, 25, 53}};
        Animation<TextureRegion> walkAnim = spriteSheet.getAnimation
                (walkImage_coordinates, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("walk", walkAnim);

        this.logo = spriteSheet.getImage(337, 419, 14,15);
    }

    /**
     * Setup properties of Box2D actor.
     * @param world represents the world of the Box2D actor.
     */
    @Override
    public void setProperties(World world) {
        this.setDynamic();
        this.setShapeRectangle();
        this.setPhysicsProperties(Constants.BOSS_DENSITY, Constants.BOSS_FRICTION, Constants.RESTITUTION);
        this.setMaxSpeedX(Constants.BOSS_MAX_SPEED_X);
        this.fixtureDef.filter.categoryBits = Constants.ENEMY_ENTITY;
        this.fixtureDef.filter.maskBits = Constants.WORLD_ENTITY|Constants.PLAYER_BULLET_ENTITY|
                Constants.PLAYER_ENTITY;
        this.setFixedRotation();
        this.initializePhysics(world);
        this.maxY = this.getY();
        this.maxX = this.getX();
        this.body.setGravityScale(0);
    }

    // UPDATE METHODS
    @Override
    public void update() {
        this.move();
        this.animate();
    }
    // move
    private void move() {
        if (this.isOnGround()) {

        } else {
            if (this.bombCount >= 350) {
                if (this.getY() < this.maxY) this.body.setLinearVelocity(0, 1f);
                else if (this.getY() >= this.maxY && this.getVelocity().y != 0) this.setVelocity(0, 0);
                else if (this.getVelocity().y == 0) {
                    if (this.player.getX() >= this.getX()) {
                        this.setScale(1, 1);
                        this.applyForce(new Vector2(1.0f, 0));
                    }
                    else if (this.player.getX() < this.getX()){
                        this.setScale(-1, 1);
                        this.applyForce(new Vector2(-1.0f, 0));
                    }
                }

                if (this.getHealth() >= 50) {
                    if (this.bombCounter > 5){
                        this.bombCounter = 0;
                        this.bombCount = 0;
                    }
                } else if (this.getHealth() < 50 && this.getHealth() > 30) {
                    if (this.bombCounter > 10) {
                        this.bombCounter = 0;
                        this.bombCount = 0;
                    }
                } else if (this.getHealth() <= 30) {
                    if (this.bombCounter > 15) {
                        this.bombCounter = 0;
                        this.bombCount = 0;
                    }
                }

            } else {
                if (this.getY() > 100) {
                    this.applyForce(new Vector2(0, -0.5f));
                } else {
                    if (this.getVelocity().y < 0) this.body.setLinearVelocity(0, 0);

                    if (this.direction == 'B') {
                        this.setScale(-1, 1);
                        this.applyForce(new Vector2(-1.0f, 0));
                    } else if (this.direction == 'F') {
                        this.setScale(1, 1);
                        this.applyForce(new Vector2(1.0f, 0));
                    }
                    this.bombCount++;
                }
            }
        }
    }

    private void animate() {
        if (this.getAnimationName().equals("fly")) {
            if (this.isOnGround()) this.setActiveAnimation("walk");
            if (this.getY() >= this.maxY) this.setActiveAnimation("throwBomb");
        }
        if (this.getAnimationName().equals("throwBomb")) {
            if (this.isOnGround()) this.setActiveAnimation("walk");
            else if (this.getY() <= this.maxY) this.setActiveAnimation("fly");
        }
    }
}