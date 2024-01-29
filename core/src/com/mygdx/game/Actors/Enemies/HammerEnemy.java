package com.mygdx.game.Actors.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Actors.SuperActors.Character;
import com.mygdx.game.Base.BaseGame;
import com.mygdx.game.Utilities.Constants;
import com.mygdx.game.Utilities.SpriteSheet;

/**
 * Class used for HammerEnemies that are shown on tiledmap.
 */
public class HammerEnemy extends Character {
    // FIELDS
    // control
    private float maxRange;
    private float minRange;
    public char direction;
    public TextureRegion logo;
    // game
    private BaseGame game;

    public HammerEnemy(BaseGame g, float minRange, float maxRange) {
        super(Constants.HAMMER_ENEMY_DAMAGE);
        this.game = g;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.direction = 'F';
        this.loadAssets();
    }

    // SETUP METHODS

    /**
     * Load images from SpriteSheet.
     */
    @Override
    public void loadAssets() {
        // SpriteSheet
        SpriteSheet spriteSheet = new SpriteSheet("enemies/goblin_goon_club_sprite_sheet.png", this.game);

        // walk animation
        int[][] walkImage_coordinates = {{12, 74, 30, 48}, {74, 75, 30, 47}, {142, 74, 29, 47},
                {205, 74, 36, 47}, {265, 75, 47, 47}, {336, 74, 34, 48}};
        Animation<TextureRegion> walkAnim = spriteSheet.getAnimation
                (walkImage_coordinates, 0.15f, Animation.PlayMode.LOOP_PINGPONG);
        this.storeAnimation("walk", walkAnim);

        // stab animation
        int[][] stabImage_coordinates = {{402, 74, 30, 48}, {469, 74, 25, 48}, {533, 74, 48, 48},
                {600, 74, 42, 48}, {663, 74, 35, 48}};
        Animation<TextureRegion> stabAnim = spriteSheet.getAnimation
                (stabImage_coordinates, 0.10f, Animation.PlayMode.LOOP);
        this.storeAnimation("stab", stabAnim);

        // club animation
        int[][] clubImage_coordinates = {{729, 75, 30, 47}, {794, 75, 25, 47}, {859, 77, 48, 45},
                {924, 77, 50, 45}, {989, 79, 48, 43}, {1055, 75, 39, 47}};
        Animation<TextureRegion> clubAnim = spriteSheet.getAnimation
                (clubImage_coordinates, 0.10f, Animation.PlayMode.LOOP);
        this.storeAnimation("attack", clubAnim);

        // death animation
        int[][] deathImage_coordinates = {{80, 278, 33, 41}, {137, 289, 44, 31}};
        Animation<TextureRegion> deathAnim = spriteSheet.getAnimation
                (deathImage_coordinates, 0.25f, Animation.PlayMode.NORMAL);
        this.storeAnimation("death", deathAnim);

        // damage image
        TextureRegion damageReg = spriteSheet.getImage(80, 278, 33, 41);
        this.storeAnimation("damage", damageReg);

        logo = spriteSheet.getImage(16, 265, 33, 48);
    }

    /**
     * Set Box2D properties of object.
     * @param world represents the world of the box2d actor.
     */
    @Override
    public void setProperties(World world) {
        this.setDynamic();
        this.setShapeRectangle();
        this.setPhysicsProperties(Constants.HAMMER_ENEMY_DENSITY, Constants.HAMMER_ENEMY_FRICTION,
                Constants.RESTITUTION);
        this.setMaxSpeed(Constants.HAMMER_ENEMY_MAX_WALK_SPEED);
        this.fixtureDef.filter.categoryBits = Constants.ENEMY_ENTITY;
        this.fixtureDef.filter.maskBits = Constants.WORLD_ENTITY|Constants.PLAYER_ENTITY|Constants.PLAYER_BULLET_ENTITY;
        this.setFixedRotation();
        this.initializePhysics(world);
    }

    // UPDATE METHODS
    @Override
    public void update() {
        this.move();
        this.animate();
    }

    private void move() {
        if (this.getX() >= this.maxRange && this.minRange != this.maxRange) this.direction = 'B';
        else if (this.getX() <= this.minRange && this.minRange != this.maxRange) this.direction = 'F';
        if (this.minRange == this.maxRange) this.direction = 's';

        if (this.direction == 'F') {
            this.setScale(1, 1);
            this.applyForce(new Vector2(1.0f, 0));
        } else if (this.direction == 'B') {
            this.setScale(-1, 1);
            this.applyForce(new Vector2(-1.0f, 0));
        }
    }
    private void animate() {
        if (this.getAnimationName().equals("walk")) {
            if (this.getTarget() != null) {
                if (!this.getTarget().getAnimationName().equals("punch") && !this.getTarget().getAnimationName().equals("upKick")
                        && !this.getTarget().getAnimationName().equals("uppercut")) {
                    this.setActiveAnimation("attack");
                } else {
                    this.setActiveAnimation("damage");
                }
            }
        }
        if (this.getAnimationName().equals("attack")) {
            if (this.getTarget() != null) {
                if (!this.getTarget().getAnimationName().equals("punch") && !this.getTarget().getAnimationName().equals("upKick")
                        && !this.getTarget().getAnimationName().equals("uppercut")) {
                    if (this.isAnimationFinished()) {

                        // set direction of target
                        if (this.getX() > this.getTarget().getX()) this.getTarget().setScaleX(1);
                        else this.getTarget().setScaleX(-1);

                        // change animations
                        if (this.getTarget().getHealth() > 0) this.getTarget().setActiveAnimation("damage");
                        if (this.getX() < this.getTarget().getX())
                            this.getTarget().applyImpulse(new Vector2(0.10f, 0.15f));
                        else this.getTarget().applyImpulse(new Vector2(-0.15f, 0.15f));

                        this.getTarget().setHealth(this.getDamage());
                        this.resetAnimation("attack");
                    }
                } else this.setActiveAnimation("damage");
            } else this.setActiveAnimation("walk");
        }
        if (this.getAnimationName().equals("damage")) {
            if (this.getTarget() != null) {
                if (!this.getTarget().getAnimationName().equals("punch") && !this.getTarget().getAnimationName().equals("upKick")
                        && !this.getTarget().getAnimationName().equals("uppercut")) {
                    this.setActiveAnimation("attack");
                }
                this.getBody().setLinearVelocity(0, 0);
            }
            else this.setActiveAnimation("walk");
        }
        if (this.getHealth() <= 0) {
            this.setActiveAnimation("death");
            this.getBody().setLinearVelocity(0, 0);
        }
    }
}
