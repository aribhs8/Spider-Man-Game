package com.mygdx.game.Actors.SuperActors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Utilities.Constants;

/**
 * Class that houses all important Player properties
 * (disregarding physics which is in Box2DActor)
 */

public abstract class Character extends Box2DActor {
    private float health;
    private float damage;
    protected int groundCount;
    private Character target;

    public Character(float damage) {
        super();
        this.health = Constants.HEALTH;
        this.damage = damage;
        this.groundCount = 0;
        this.setSize(Constants.CHARACTER_WIDTH, Constants.CHARACTER_HEIGHT);
    }

    // SETUP METHODS
    // load assets
    public abstract void loadAssets();
    // set properties
    public abstract void setProperties(World world);

    @Override
    public void initializePhysics(World world) {
        super.initializePhysics(world);

        // FIXTURES
        // declare coordinate + dimension variables
        float x;
        float y;
        float w;
        float h;
        // initialize Fixtures
        FixtureDef bottomSensor = new FixtureDef();
        FixtureDef rightSensor = new FixtureDef();
        FixtureDef leftSensor = new FixtureDef();

        // set sensors
        bottomSensor.isSensor = true;
        rightSensor.isSensor = true;
        leftSensor.isSensor = true;

        PolygonShape sensorShape = new PolygonShape();
        // BOTTOM FIXTURE
        // center coordinates of sensor box - offset from body center
        x = 0;
        y = -20;

        // dimensions of sensor box
        w = getWidth() - 8;
        h = getHeight();
        sensorShape.setAsBox(w/150, h/1600, new Vector2(x/200, y/50), 0);
        bottomSensor.shape = sensorShape;

        Fixture bottomFixture = body.createFixture(bottomSensor);
        bottomFixture.setUserData("bottom");

        // RIGHT FIXTURE
        // center coordinates of sensor box - offset from body center
        x = 20;
        y = 0;

        // dimensions of sensor box
        w = getWidth();
        h = getHeight() - 40;
        sensorShape.setAsBox(w/1600, h/150, new Vector2(x/75, y/200), 0);
        rightSensor.shape = sensorShape;

        Fixture rightFixture = body.createFixture(rightSensor);
        rightFixture.setUserData("right");

        // LEFT FIXTURE
        // center coordinates of sensor box - offset from body center
        x = -20;
        y = 0;

        // dimensions of sensor box
        w = getWidth();
        h = getHeight() - 40;
        sensorShape.setAsBox(w/1600, h/150, new Vector2(x/75, y/200), 0);
        leftSensor.shape = sensorShape;

        Fixture leftFixture = body.createFixture(rightSensor);
        leftFixture.setUserData("left");

        sensorShape.dispose();
    }

    // FIELD METHODS
    // health methods
    public float getHealth() { return health; }
    public void setHealth(float h) { health = (health + h); }
    public void setExplicitHealth(float h) { health = h; }

    // damage methods
    public float getDamage() { return damage; }
    public void setDamage(float d) { damage = (damage - d); }

    // ground methods
    public boolean isOnGround() { return (groundCount > 0); }
    public void adjustGroundCount(int i) { groundCount += i; }

    // target methods
    public Character getTarget() { return target; }
    public void setTarget(Character t) { target = t; }

    // UPDATE method
    public abstract void update();
}
