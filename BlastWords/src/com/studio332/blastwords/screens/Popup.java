/**
 * BlastWords
 * Popup.java
 * 
 * Created by Lou Foster
 * Copyright Studio332 2013. All rights reserved.
 */
package com.studio332.blastwords.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.studio332.blastwords.BlastWords;
import com.studio332.blastwords.util.Assets;


public final class Popup extends Image {
   
   private enum Mode {PAUSE, GIVE_UP};
   private final Mode mode;
   private GameStateListener listener;

   /**
    * Create an instance of the popup image in PAUSE mode
    * @return
    */
   public static Popup createPausePopup() {
      Popup p = new Popup(Mode.PAUSE, Assets.instance().getDrawable("pauseMenu"));
      p.getColor().a = 0;
      p.addAction( fadeIn(0.5f) );
      p.setPosition( 
            (BlastWords.TGT_WIDTH-p.getWidth())*0.5f, 
            (BlastWords.TGT_HEIGHT-p.getHeight())*0.65f);
      return p;
   }
   
   /**
    * Create an instance of the popup image in Give Up mode
    * @return
    */
   public static Popup createGiveUpPopup() {
      Popup p = new Popup(Mode.GIVE_UP,  Assets.instance().getDrawable("giveUpMenu"));
      p.getColor().a = 0;
      p.addAction( fadeIn(0.5f) );
      p.setPosition( 
            (BlastWords.TGT_WIDTH-p.getWidth())*0.5f, 
            (BlastWords.TGT_HEIGHT-p.getHeight())*0.65f);
      return p;
   }
   
   private Popup(Mode m, Drawable drawable) {
      super( drawable );
      this.mode = m;

      addListener( new InputListener() {
         @Override
         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            if ( mode.equals(Mode.PAUSE )) {
               handlePauseTap(x,y);
            } else {
               handleGiveUpTap(x,y);
            }
         }
         
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return true;
         }
      });
   }
   
   private void handlePauseTap( float x, float y) {
      if ( listener == null || x < 110 || x > 210 ) {
         return;
      }
      
      if ( y <= 85 ) {
         listener.helpTapped();
      } else if ( y <= 130 ) {
         listener.quitTapped();
      } else if ( y <= 175 ) {
         listener.restartTapped();
      } else if ( y <= 220 ) {
         listener.resumeTapped();
      }
   }
   
   private void handleGiveUpTap(float x, float y) {
      if ( listener == null || x < 95 || x > 250 ) {
         return;
      }
      
      if ( y >= 65 && y <= 105 ) {
         listener.giveUpTapped();
      } else if ( y <= 155 ) {
         listener.resumeTapped();
      } 
   }

   public void setListener(GameStateListener l) {
      this.listener = l;
   }
   
   public void fadeOut() {
      addAction( Actions.sequence(Actions.fadeOut(0.25f), new Action() {
         @Override
         public boolean act(float delta) {
            Popup.this.remove();
            return false;
         }}));
   }
}
