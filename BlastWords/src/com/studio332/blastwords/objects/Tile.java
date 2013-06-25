package com.studio332.blastwords.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.blastwords.model.LetterInfo;
import com.studio332.blastwords.model.LetterInfo.Type;
import com.studio332.blastwords.util.Assets;
import com.studio332.blastwords.util.SoundManager;


public class Tile extends Image {
   private final int id;
   private Image lockOverlay;
   private LetterInfo info;
   private boolean selected = false;
   private boolean hold = false;
   private float velocity = 0.0f;
   private static int instaceCounter = 0;

   public Tile(LetterInfo info) {
      super();

      this.id = Tile.instaceCounter++;
      this.info = info;
      
      String imgName = info.getCharacter().toString().toLowerCase()+"-off";
      if ( info.getType().equals(Type.WILD)) {
         imgName = "wild-off";
      } else if ( info.getType().equals(Type.BLOCKER)) {
         imgName = "blocker-off";
      }
      
      Image i = new Image(Assets.instance().getDrawable(imgName));
      setDrawable(i.getDrawable() );
      setWidth(i.getWidth());
      setHeight(i.getHeight());

      if (this.isLocked()) {
         this.lockOverlay = new Image( Assets.instance().getDrawable("locked"));
      }
   }
   
   public boolean canFall() {
      return (this.selected == false && this.hold == false);
   }
   
   public void fadeThenDrop() {
      getColor().a = 0.0f;
      this.hold = true;
      Action a = sequence( fadeIn(0.4f), delay(0.4f), new Action() {
         @Override
         public boolean act(float delta) {
            hold = false;
            return false;
         } 
      });
      addAction(a);
   }

   public float getVelocity() {
      return this.velocity;
   }

   
   public void setVelocity( float v) {
      this.velocity = v;
   }

   public void stop() {
      this.velocity = 0.0f;
   }

   public boolean isSelected() {
      return this.selected;
   }
   
   public Type getType() {
      return this.info.getType();
   }

   public void setSelected(boolean sel) {
      this.selected = sel;
      this.hold = false;

      String imgName = this.info.getCharacter().toString().toLowerCase();
      if ( info.getType().equals(Type.WILD)) {
         imgName = "wild";
      } else if ( info.getType().equals(Type.BLOCKER)) {
         imgName = "blocker";
      }
      
      if (this.selected == false) {
         setDrawable(Assets.instance().getDrawable(imgName+"-off"));
      } else {
         SoundManager.instance().playSound(SoundManager.SELECT_TILE);
         setDrawable(Assets.instance().getDrawable(imgName+"-on"));
      }
   }
   
   public void hide() {
      setDrawable(Assets.instance().getDrawable("blank-off"));
   }
   
   public void show() {
      String imgName = this.info.getCharacter().toString().toLowerCase()+"-off";
      setDrawable(Assets.instance().getDrawable(imgName));
   }
   
   @Override
   public void setPosition(float x, float y) {
      super.setPosition(x, y);
      if (this.lockOverlay != null) {
         this.lockOverlay.setPosition(x, y);
      }
   }

   public Character getCharacter() {
      return this.info.getCharacter();
   }
   
   public boolean isBlocker() {
      return this.info.getType().equals(Type.BLOCKER);
   }
   
   public boolean isWild() {
      return this.info.getType().equals(Type.WILD);
   }

   public boolean isLocked() {
      return (isBlocker() || this.info.getType().equals(Type.LOCKED));
   }

   @Override
   public void draw(SpriteBatch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      if (isLocked()) {
         this.lockOverlay.draw(batch, parentAlpha);
      }
   }

   public int getId() {
      return this.id;
   }

   @Override
   public String toString() {
      return "Tile " + this.info.getCharacter()+" pos: "+getX()+","+getY();
   }

   public boolean collidesWith(Tile other) {
      Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight());
      Rectangle r2 = new Rectangle(other.getX(), other.getY(), other.getWidth(), other.getHeight());
      return r.overlaps(r2);
   }
}
