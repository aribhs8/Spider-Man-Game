package com.mygdx.game;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.game.Base.BaseGame;
import com.mygdx.game.Screens.StartScreen;

public class MainLauncher extends BaseGame {

    @Override
    public void create() {
        // initialize and set screen
        this.getAssetManager().setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        StartScreen l1 = new StartScreen(this);
        setScreen(l1);
    }
}
