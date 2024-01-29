package com.mygdx.game.Utilities;

/**
 * Stores constants used throughout game
 */
public class Constants {
    // UNIVERSAL CONSTANTS
    public static final float HEALTH = 100;
    public static final float CHARACTER_WIDTH = 30;
    public static final float CHARACTER_HEIGHT = 33;
    public static final float RESTITUTION = 0.1f;

    // COLLISION FILTERS
    public static final short PLAYER_ENTITY = 0x1;
    public static final short WORLD_ENTITY = 0x1 << 1;
    public static final short ENEMY_ENTITY = 0x1 << 2;
    public static final short ENEMY_BULLET_ENTITY = 0x1 << 3;
    public static final short PLAYER_BULLET_ENTITY = 0x1 << 4;

    // PLAYER CONSTANTS
    public static final float PLAYER_MAX_RUN_SPEED = 2.5f;
    public static final float PLAYER_MAX_CRAWL_SPEED = 2.25f;
    public static final float PLAYER_RUN_SPEED = 1.6f;
    public static final float PLAYER_CRAWL_SPEED = 1.0f;
    public static final float PLAYER_DENSITY = 1.25f;
    public static final float PLAYER_FRICTION = 2;
    public static final float PLAYER_DAMAGE = 2;

    // BOSS
    public static final float BOSS_MAX_SPEED_X = 2.5f;
    public static final float BOSS_SPEEDX = 1.6f;
    public static final float BOSS_DENSITY = 1.25f;
    public static final float BOSS_FRICTION = 2;

    // ENEMIES
    // PUNCH ENEMY
    public static final int PUNCH_ENEMY_DAMAGE = -10;
    public static final float PUNCH_ENEMY_MAX_WALK_SPEED = 2;
    public static final float PUNCH_ENEMY_DENSITY = 1;
    public static final float PUNCH_ENEMY_FRICTION = 1.25f;

    // HAMMER ENEMY
    public static final int HAMMER_ENEMY_DAMAGE = -20;
    public static final float HAMMER_ENEMY_MAX_WALK_SPEED = 1.25f;
    public static final float HAMMER_ENEMY_DENSITY = 2.5f;
    public static final float HAMMER_ENEMY_FRICTION = 0.01f;

    // DAGGER ENEMY
    public static final int DAGGER_ENEMY_DAMAGE = -15;
    public static final float DAGGER_ENEMY_MAX_WALK_SPEED = 1.75f;
    public static final float DAGGER_ENEMY_DENSITY = 1.10f;
    public static final float DAGGER_ENEMY_FRICTION = 1.30f;
}
