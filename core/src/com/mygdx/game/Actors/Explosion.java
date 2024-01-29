package com.mygdx.game.Actors;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Actors.SuperActors.Box2DActor;
import com.mygdx.game.Base.BaseGame;
import com.mygdx.game.Utilities.Constants;
import com.mygdx.game.Utilities.SpriteSheet;

/**
 * This class is used to control the explosion effect shown after a bomb explosion.
 */
public class Explosion extends Box2DActor {
    // FIELDS
    // game
    private BaseGame game;
    private Sound boomSound;

    /**
     * Constructor used to create a new explosion.
     * @param g represents the game.
     * @param x represents the x-coord of the explosion.
     * @param y represents the y-coord of the explosion.
     * @param sizeX represents the width of the explosion.
     * @param sizeY represnets the height of the explosion.
     */
    public Explosion(BaseGame g, float x, float y, float sizeX, float sizeY) {
        super();
        this.game = g;
        this.loadAssets();
        this.setPosition(x, y);
        this.setSize(sizeX, sizeY);
    }

    private void loadAssets() {
        SpriteSheet spriteSheet = new SpriteSheet("explosion.png", this.game);

        int[][] explosionCoordinates = {{5, 5, 192, 192}, {207, 5, 152, 150}, {207, 165, 82, 91},
                {299, 165, 92, 102}, {5, 277, 120, 124}, {135, 277, 133, 134}, {278, 277, 138, 140},
                {369, 5, 143, 144}, {426, 159, 149, 151}};
        Animation<TextureRegion> explosionAnim = spriteSheet.getAnimation
                (explosionCoordinates, 0.15f, Animation.PlayMode.NORMAL);
        this.storeAnimation("explosion", explosionAnim);

        this.boomSound = this.game.getAssetManager().get("sounds/explosion.wav", Sound.class);
        this.boomSound.play();
    }

    public void setProperties(World world) {
        this.setStatic();
        this.setShapeCircle();
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = Constants.ENEMY_BULLET_ENTITY;
        fixtureDef.filter.maskBits = Constants.WORLD_ENTITY|Constants.PLAYER_ENTITY;
        super.initializePhysics(world);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        region.setRegion(activeAnim.getKeyFrame(elapsedTime));
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);
        if ( isVisible() )
            batch.draw( region, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation() );
        if (isTransform()) applyTransform(batch, computeTransform());
        drawChildren(batch, parentAlpha);
        if (isTransform()) resetTransform(batch);
    }

}
