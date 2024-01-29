package com.mygdx.game.Base;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.Actors.Player;

public abstract class BaseGame extends Game
{
    // used to store resources common to multiple screens
    Skin skin;
    private final AssetManager assetManager = new AssetManager();
    
    public BaseGame()
    {
        skin = new Skin();
    }
    
    public abstract void create();

    public void dispose()
    {
        skin.dispose();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}