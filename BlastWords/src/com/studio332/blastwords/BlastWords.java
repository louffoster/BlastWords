package com.studio332.blastwords;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.studio332.blastwords.model.BlastWordsGame.Mode;
import com.studio332.blastwords.screens.GameScreen;
import com.studio332.blastwords.screens.MainMenuScreen;
import com.studio332.blastwords.screens.SplashScreen;
import com.studio332.blastwords.util.Assets;
import com.studio332.blastwords.util.SoundManager;

public class BlastWords extends Game {
   
   // commmon gameplay constants
   public static final int LOCK_THRESHOLD = 17;
   public static final float TGT_WIDTH = 800;
   public static final float TGT_HEIGHT = 1205;

   
   //public static final String LOG_NAME = BlastWords.class.getSimpleName();
   
   @Override
   public void create() {
      //Gdx.app.log(LOG_NAME, "Creating game");

      Texture.setEnforcePotImages(false);
      Assets.instance().load();
      SoundManager.instance().init();
      setScreen( new SplashScreen(this) );
   }
   
   public void showMainMenu() {
      setScreen( new MainMenuScreen(this) );
   }
   
   public void showGameScreen( Mode mode) {
      setScreen( new GameScreen(this, mode) );
   }

   @Override
   public void resize(int width, int height) {
   }

   @Override
   public void render() {
      super.render();   // passes the render along to the current screen
      // output the current FPS
      //fpsLogger.log();
   }

   @Override
   public void pause() {
      //Gdx.app.log( LOG_NAME, "Pause Game" );
   }

   @Override
   public void resume() {
      //Gdx.app.log( LOG_NAME, "Resume Game" );
   }

   @Override
   public void dispose() {
      //Gdx.app.log( LOG_NAME, "Dispose Game" );
      getScreen().dispose();
   }

}
