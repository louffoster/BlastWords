package com.studio332.blastwords.objects;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.blastwords.util.Assets;

public class ScoreBoard extends Group {
   private List<Digit> digits = new ArrayList<Digit>();
   private final int maxDigits = 6;
   
   public ScoreBoard() {
      super();
     Image bg = new Image(Assets.instance().getDrawable("scorebox"));
     addActor(bg);
     setWidth(bg.getWidth());
     setHeight(bg.getHeight());
      for ( int i = 0; i<this.maxDigits; i++  ) {
         this.digits.add( new Digit() );
      }
   }
   
   @Override
   public void setPosition(float x, float y) {
      float digitX = getWidth();
      float offX = 42.0f;
      float offY = 38.0f;
      for ( Digit d : this.digits ) {
         d.setPosition(digitX-d.getWidth()-offX, y+offY);
         digitX -= d.getWidth();
      }
      super.setPosition(x, y);
   }
   
   public void updateScore( int score ) {
      int digitPos = 0;
      while (score > 0 && digitPos < this.maxDigits) {
          Digit digit = this.digits.get(digitPos++);
          digit.setNumber(score%10);
          score /= 10;
          if ( digitPos == this.maxDigits ) {
              throw new RuntimeException("You're too high dude");
          }
      }
      
      while ( digitPos < this.maxDigits ) {
         Digit digit = this.digits.get(digitPos++);
         digit.setNumber(0);
      }
   }
   
   @Override
   public void act(float delta) {
      for (Digit d : this.digits ) {
         d.act(delta);
      }
      super.act(delta);
   }
   
   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      for ( Digit d : this.digits ) {
         d.draw(batch, parentAlpha);
      }
   }
}
