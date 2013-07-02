package com.studio332.blastwords.objects;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.blastwords.util.Assets;

public class Bomb extends Image {
   private BombListener listener = null;
   private boolean used = false;
   
   public Bomb() {
      super(Assets.instance().getAtlasRegion("bomb-on"));     
      addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if ( listener != null && used == false ) {
               if ( listener.detonateRequested() ) {
                  detonate();
               }
            }
            return false;
         }
      });
   }
   
   public void setBombListener( BombListener l ) {
      this.listener = l;
   }
   
   private void detonate() {
      this.used = true;
      this.setDrawable( Assets.instance().getDrawable("bomb-used"));
   }
   
   public interface BombListener {
      boolean detonateRequested();
   }
   
}
