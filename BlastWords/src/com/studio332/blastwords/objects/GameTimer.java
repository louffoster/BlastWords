package com.studio332.blastwords.objects;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.blastwords.model.BlastWordsGame;
import com.studio332.blastwords.model.BlastWordsGame.State;
import com.studio332.blastwords.util.Assets;
import com.studio332.blastwords.util.SoundManager;

public class GameTimer extends Group {
   public enum Mode {STOPWATCH, COUNTDOWN};
   private Image minuteHand;
   private Image secondHand;
   private float tickDelta;
   private Listener listener = null;
   private Mode mode;
   private Action tickAction;
   private boolean timeUp = false;
   private BlastWordsGame gameModel;
   private int timeRemaining;
   
   public GameTimer( BlastWordsGame gameModel ) {
      this.gameModel = gameModel;
      this.mode = Mode.STOPWATCH;
      if ( this.gameModel.getMode().equals(BlastWordsGame.Mode.TIMED )) {
         this.mode = Mode.COUNTDOWN;
      }
      
      float secRot = 0.0f;
      Image face;
      if ( this.mode == Mode.STOPWATCH ) {
         face = new Image( Assets.instance().getDrawable("timer") );
         this.tickDelta = -6.0f;
      } else {
         face = new Image( Assets.instance().getDrawable("timer3") );
         this.tickDelta = 3.6f;
         
         // countdown timer has 100 ticks on it so each tick is 3.6
         // it does not start at 0 tho - starts 10 ticks ccx
         float start = -360.0f + 36.0f;
         secRot = start + (90.0f-this.gameModel.getTimedModeDurationSecs())*3.6f;
         this.timeRemaining = this.gameModel.getTimedModeDurationSecs();
      }
      
      this.minuteHand = new Image( Assets.instance().getDrawable("min-hand") );
      this.secondHand = new Image( Assets.instance().getDrawable("sec-hand") );
      this.secondHand.setOrigin(84.0f, 85.0f);
      this.minuteHand.setOrigin(84.0f, 85.0f);
      this.secondHand.setRotation(secRot);
      
      this.addActor(face);
      this.addActor(this.minuteHand);
      this.addActor(this.secondHand);
      setWidth(face.getWidth());
      setHeight(face.getHeight());
   }
   
   public void setListener( Listener l) {
      this.listener = l;
   }
   
   public void stop() {
      this.timeUp = true;
   }
   
   public void start() {
      this.tickAction = forever(sequence( delay(1.0f),new Action() {
         @Override
         public boolean act(float delta) {
            tick();
            return true;
         }  
      } ));
      this.addAction(this.tickAction);
   }
   
   private void tick() {
      if ( this.gameModel.getState().equals(State.PLAYING) == false ) {
         return;
      }
      
      this.timeRemaining--;
      this.gameModel.tick();
      
      float pos = this.secondHand.getRotation();
      pos += this.tickDelta;
      if ( pos == -360.0f ){
          pos = 0.0f;
          float minPos = this.minuteHand.getRotation();
          minPos -= 6.0f;
          this.minuteHand.setRotation(minPos);
      }
      this.secondHand.setRotation(pos);
      
      if ( this.mode == Mode.COUNTDOWN) { 
         if ( this.timeRemaining < 10 && this.listener != null) {
            this.listener.timerWarning();
         }
         
         if ( this.timeRemaining == 9 ) {
            SoundManager.instance().playSound(SoundManager.TICK);
         }
         
         if ( this.timeRemaining == 0 ) {
            SoundManager.instance().playSound(SoundManager.RING);
            this.timeUp = true; 
         }
      }
   }
   
   @Override
   public void act(float delta) {
      super.act(delta);
      if ( this.timeUp && this.getActions().size > 0) {
         this.clearActions();
         if ( this.listener != null ) {
            this.listener.timeUp();
         }
      }
   }
   
   public interface Listener {
      void timeUp();
      void timerWarning();
   }

}
