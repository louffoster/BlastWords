package com.studio332.blastwords.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.studio332.blastwords.BlastWords;
import com.studio332.blastwords.util.Assets;
import com.studio332.blastwords.util.SoundManager;

public class SplashScreen extends AbstractScreen {
   
   public SplashScreen(BlastWords game) {
      super(game);
   }
   
   @Override
   public void show() {
      super.show();

      // load the splash image and create the texture region
      final Group splashGrp = Assets.instance().makeFullscreenImg("splash");
      splashGrp.getColor().a = 0f;
      this.stage.addActor(splashGrp);

      // configure the fade-in/out effect on the splash image
      Action actions = sequence(fadeIn(0.75f),
            delay(4.0f, fadeOut(0.25f)), 
            new Action() {
               @Override
               public boolean act(float delta) {
                  SoundManager.instance().playSound( SoundManager.PAGE_TURN );
                  SplashScreen.this.game.showMainMenu();
                  return true;
               }
               
            });

      splashGrp.addAction(actions);
   }
}
