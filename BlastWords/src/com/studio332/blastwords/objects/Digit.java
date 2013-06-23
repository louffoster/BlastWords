package com.studio332.blastwords.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.studio332.blastwords.util.Assets;
import com.studio332.blastwords.util.SoundManager;

public class Digit extends Actor {
   private int currentValue;
   private int direction;
   private float x; 
   private float y;
   private final int digitW = 60;
   private final int digitH = 83;
   
   public Digit() {
      super();
      this.currentValue = 0;
      this.x = 0.0f; 
      this.y = 0.0f;
   }
   
   @Override
   public void setPosition(float x, float y) {
      this.x = x;
      this.y = y;
   }
   
   @Override
   public float getWidth() {
      return this.digitW;
   }
   
   @Override
   public float getHeight() {
      return this.digitH;
   }

   private void flipDigit() {
      SoundManager.instance().playSound(SoundManager.TILE_CLICK);
      this.currentValue+= this.direction;
   }
   
   public void setNumber( int num ) {
      this.direction = 0;
      if ( num < this.currentValue ) { 
         this.direction = -1;
      } else if ( num > this.currentValue ) {
         this.direction = 1;
      }
      
      if ( this.direction == 0 ) {
         return;
      }
      
      int delta = Math.abs( num - this.currentValue );

      Action a = sequence(new Action() {
         @Override
         public boolean act(float delta) {
            flipDigit();
            return true;
         }  
      }, delay(0.15f) );
      this.clearActions();
      this.addAction( repeat(delta, a));
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      
      batch.draw(Assets.instance().getAtlasRegion("number-"+this.currentValue), 
            this.x, this.y, this.digitW, this.digitH);
   }
}
