package com.mygdx.game.Actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Actors.SuperActors.Box2DActor;
import com.mygdx.game.Actors.SuperActors.Character;
import com.mygdx.game.Base.BaseGame;
import com.mygdx.game.Utilities.Constants;
import com.mygdx.game.Utilities.SpriteSheet;

/**
 * This class controls bullets that are created on the screen.
 */
public class Bullet extends Box2DActor {
    // DECLARE VARIABLES
    // coordinates
    private float x;
    private float y;

    // control
    private char type;
    private char direction;

    // game
    private BaseGame game;
    private Character character;

    /**
     * Constructor used to initialize bullet object
     * @param g represents the game.
     * @param c represents the character firing the bullet.
     * @param type represents the type of bullet (e.g. Bomb or Web).
     * @param direction represents the direction of the bullet.
     * @param x represents the x-coord of the bullet.
     * @param y represents the y-coord of the bullet.
     */

    public Bullet(BaseGame g, Character c, char type, char direction, float x, float y) {
        super();
        this.game = g;
        this.character = c;
        this.type = type;
        this.direction = direction;
        this.loadAssets();
        this.setSize(5, 10);
        this.setPosition(x, y);
    }

    /**
     * Load bullet images
     */
    private void loadAssets() {
        if (this.type == 'w') {
            SpriteSheet spriteSheet = new SpriteSheet("player/web_spritesheet.png", this.game);
            // web anim
            int[][] webImage_coordinates = {{4, 8, 5, 6}, {35, 8, 6, 9}, {68, 10, 7, 6}};
            Animation<TextureRegion> webAnim = spriteSheet.getAnimation
                    (webImage_coordinates, 0.25f, Animation.PlayMode.NORMAL);
            this.storeAnimation("web", webAnim);
        } else if (this.type == 'b') {
            SpriteSheet spriteSheet = new SpriteSheet("enemies/goblin_sprite_sheet.png", this.game);
            // bomb anim
            int[][] bombImage_coordinates = {{4, 476, 8, 8}, {12, 476, 8, 8}, {21, 477, 9, 7}, {32, 477, 7, 8}};
            Animation<TextureRegion> bombAnim = spriteSheet.getAnimation
                    (bombImage_coordinates, 0.25f, Animation.PlayMode.LOOP);
            this.storeAnimation("bomb", bombAnim);
        }
    }

    /**
     * Initialize properties of bullet.
     * @param world represents Box2D world the bullet is in.
     */
    @Override
    public void initializePhysics(World world) {
        super.initializePhysics(world);
        if (this.type == 'w') this.body.setGravityScale(0);

    }

    /**
     * Sets up the properties of the bullet.
     * @param world represents the world of the bullet.
     */
    public void setProperties(World world) {
        this.setDynamic();
        this.setShapeRectangle();
        this.setPhysicsProperties(0.5f, 0f, 0.1f);
        if (this.character instanceof Player) {
            this.fixtureDef.filter.categoryBits = Constants.PLAYER_BULLET_ENTITY;
            this.fixtureDef.filter.maskBits = Constants.WORLD_ENTITY|Constants.ENEMY_ENTITY;
        } else {
            this.fixtureDef.filter.categoryBits = Constants.ENEMY_BULLET_ENTITY;
            this.fixtureDef.filter.maskBits = Constants.WORLD_ENTITY|Constants.PLAYER_ENTITY;
        }
        this.setFixedRotation();
        this.initializePhysics(world);
    }

    // UPDATE
    // move
    public void move() {
        if (this.type == 'w') {
            if (this.direction == 'F') {
                this.setScale(1, 1);
                this.body.setLinearVelocity(2f, 0);
            } else if (this.direction == 'B') {
                this.setScale(-1, 1);
                this.body.setLinearVelocity(-2f, 0);
            }
        }
    }

    // FIELD METHODS
    public char getType() { return this.type; }
}
