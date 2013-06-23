package com.studio332.blastwords.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.blastwords.util.Assets;

public class Blaster extends Actor {
   private Image plunger;
   private Image body;
   private final float plungerYOffset = 135.0f;
   private boolean grabbed = false;
   private float grabbedAtY;
   private Listener listener;
   
   public Blaster() {
      super();
      
      this.body = new Image(Assets.instance().getDrawable("blaster-body"));
      this.plunger = new Image(Assets.instance().getDrawable("blaster-plunger"));
   }
   
   public void setListener( Listener l ) {
      this.listener = l;
   }
   
   public boolean grab (float x, float y) {
      float l = this.plunger.getX();
      float t = this.plunger.getY();
      float w = this.plunger.getWidth();
      float h = this.plunger.getHeight();
      if (x >= l && x <= l+w && y>=t && y<=t+h) {
         this.grabbed = true;
         this.grabbedAtY = y;
      }
      return false;
   }
   
   public boolean isGrabbed() {
      return this.grabbed;
   }
   
   public void letGo() {
      this.grabbed = false;
      float x = this.plunger.getX();
      float y =  this.body.getY()+this.plungerYOffset;
      this.plunger.addAction( moveTo(x,y,0.2f));
   }
   
   public void dragPlunger( float x, float y ) {
      
      float delY = y - this.grabbedAtY;
      float newY = this.body.getY()+this.plungerYOffset+delY;
      if ( newY > this.body.getY()+this.plungerYOffset ) {
         this.plunger.setPosition( this.plunger.getX(), this.body.getY()+this.plungerYOffset);
         return;
      }

      float blastY = this.body.getY()+this.plungerYOffset-110.0f;
      if ( newY <= blastY) {
          if ( this.listener != null ) {
             this.listener.blastHandler();
          }
          letGo();
          return;
      }
      this.plunger.setPosition(this.plunger.getX(), this.body.getY()+this.plungerYOffset+delY);
   }
   
   @Override
   public void setPosition(float x, float y) {
      super.setPosition(x, y);
      this.body.setPosition(x, y);
      this.plunger.setPosition(x, y+this.plungerYOffset);
   }
   
   @Override
   public void act(float delta) {
      this.plunger.act(delta);
      super.act(delta);
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      this.plunger.draw(batch, parentAlpha);
      this.body.draw(batch, parentAlpha);
   }
   
   /*
    * Listener for blast events
    */
   public interface Listener {
      void blastHandler();
   }
}
