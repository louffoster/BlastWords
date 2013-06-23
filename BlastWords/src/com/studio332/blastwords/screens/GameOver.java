package com.studio332.blastwords.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.studio332.blastwords.BlastWords;
import com.studio332.blastwords.model.BlastWordsGame;
import com.studio332.blastwords.model.BlastWordsGame.Mode;
import com.studio332.blastwords.model.Settings;
import com.studio332.blastwords.objects.ScoreBoard;
import com.studio332.blastwords.util.Assets;
import com.studio332.blastwords.util.SoundManager;

/**
 * Game over image. Covers main playing area and shows score summary Buttons to
 * replay and go to main menu
 * 
 * @author lfoster
 * 
 */
public final class GameOver extends Group {
   private BlastWordsGame gameModel;
   private ScoreBoard scoreBoard;
   private int tilesLeft;
   private GameStateListener listener;
   private final float indentX = BlastWords.TGT_WIDTH * 0.04f;
   private final float groupX = BlastWords.TGT_WIDTH * 0.23f;
   private final float bonusX = BlastWords.TGT_WIDTH * 0.27f;
   
   public static final String LOG_NAME = BlastWords.class.getSimpleName();

   public GameOver(BlastWordsGame model, ScoreBoard score, int tilesLeft) {
      Image bkg = new Image( Assets.instance().getAtlasRegion("gameoverscreen2"));
      setSize(bkg.getWidth(), bkg.getHeight());
      
      this.gameModel = model;
      this.scoreBoard = score;
      this.tilesLeft = tilesLeft;
      getColor().a = 0.0f;

      bkg.setPosition((BlastWords.TGT_WIDTH - getWidth()) * 0.5f,
            (BlastWords.TGT_HEIGHT - getHeight()) * 0.5f);

      Color c = new Color(0.408f, .282f, .075f, 1.0f);
      LabelStyle st = new LabelStyle(Assets.instance().getFont(), c);

      // game over label
      Label l = new Label("Game Over", st);
      l.setFontScale(1.2f);
      l.setPosition((BlastWords.TGT_WIDTH - l.getWidth()) * 0.5f, BlastWords.TGT_HEIGHT * 0.75f);

      // controls
      Label a = new Label("Play Again", st);
      a.setPosition(BlastWords.TGT_WIDTH * 0.6f, BlastWords.TGT_HEIGHT * 0.22f);

      Label q = new Label("Quit", st);
      q.setPosition(BlastWords.TGT_WIDTH * 0.20f, BlastWords.TGT_HEIGHT * 0.22f);

      addActor(bkg);
      addActor(l);
      addActor(a);
      addActor(q);

      // fade groups: game time
      addGameTime(st);
      
      q.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            if (listener != null)
               listener.quitTapped();
            return false;
         }
      });

      a.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            if (listener != null)
               listener.restartTapped();
            return false;
         }
      });
   }

   private void addGameTime(final LabelStyle st) {
      int s = this.gameModel.getElapsedTime();
      int h = s / 3600;
      s -= (h * 3600);
      int m = s / 60;
      s -= (m * 60);
      Group timeGrp = new Group();
      Label hdr = new Label("Game Time", st);
      hdr.setPosition(0f, BlastWords.TGT_HEIGHT * 0.025f);
      String val = String.format("%02d:%02d:%02d", h, m, s);
      Label dat = new Label(val, st);
      dat.setFontScale(0.8f);
      dat.setPosition(this.indentX, 0f);
      timeGrp.addActor(hdr);
      timeGrp.setPosition(groupX, BlastWords.TGT_HEIGHT * 0.67f);
      timeGrp.getColor().a = 0f;
      timeGrp.addActor(dat);
      addActor(timeGrp);

      timeGrp.addAction(sequence(delay(0.5f), fadeIn(0.6f), new Action() {
         @Override
         public boolean act(float delta) {
            addWordCount(st);
            return true;
         }
      }));
   }

   private void addWordCount(final LabelStyle st) {
      Group grp = new Group();
      Label hdr = new Label("Word Count", st);
      float y = BlastWords.TGT_HEIGHT * 0.025f;
      hdr.setPosition(0f, y);
      grp.addActor(hdr);

      y -= (BlastWords.TGT_HEIGHT * 0.028f);
      for (int wc = 3; wc <= 7; wc++) {
         Label l = new Label(wc + " Letter Words = ", st);
         l.setFontScale(0.8f);
         l.setPosition(this.indentX, y);
         grp.addActor(l);

         Label c = new Label(this.gameModel.getWordCount(wc) + "", st);
         c.setFontScale(0.8f);
         c.setPosition(BlastWords.TGT_WIDTH * 0.32f, y);
         grp.addActor(c);
         y -= (BlastWords.TGT_HEIGHT * 0.019f);

      }

      y -= (BlastWords.TGT_WIDTH * 0.01f);
      Label l = new Label("Total = ", st);
      l.setFontScale(0.8f);
      l.setPosition(BlastWords.TGT_WIDTH * 0.184f, y);
      grp.addActor(l);

      Label c = new Label(this.gameModel.getTotalWords() + "", st);
      c.setFontScale(0.8f);
      c.setPosition(BlastWords.TGT_WIDTH * 0.32f, y);
      grp.addActor(c);
      grp.getColor().a = 0;
      addActor(grp);
      grp.setPosition(groupX, BlastWords.TGT_HEIGHT * 0.61f);
      
      grp.addAction(sequence(fadeIn(0.6f), new Action() {
         @Override
         public boolean act(float delta) {
            addBonusLabel(st);
            return true;
         }
      }));
   }
   
   private void addBonusLabel( final LabelStyle st ) {
      Label b = new Label("Bonus", st);
      b.setPosition(groupX, BlastWords.TGT_HEIGHT * 0.47f);
      b.getColor().a = 0;
      addActor(b);
      b.addAction(sequence(fadeIn(0.6f), new Action() {
         @Override
         public boolean act(float delta) {
            addSpeedBonus(st);
            return true;
         }
      }));
   }

   private void addSpeedBonus(final LabelStyle st) {
      Group grp = new Group();
      Label hdr = new Label(String.format("Words / Minute = %2.1f",
            this.gameModel.getWordsPerMinute()), st);
      hdr.setFontScale(0.8f);
      grp.addActor(hdr);

      Label b = new Label(String.format("Bonus = %d", this.gameModel.getSpeedBonus()), st);
      b.setFontScale(0.8f);
      b.setPosition(BlastWords.TGT_WIDTH * 0.32f, 0f);
      grp.addActor(b);
      grp.getColor().a = 0;
      grp.setPosition(bonusX, BlastWords.TGT_HEIGHT * 0.435f);
      addActor(grp);
      
      grp.addAction(sequence(fadeIn(0.6f), new Action() {
         @Override
         public boolean act(float delta) {
            scoreBoard.updateScore(gameModel.getScore());
            addLengthBonus(st);
            return true;
         }
      }));
   }

   private void addLengthBonus(final LabelStyle st) {
      Group grp = new Group();
      Label hdr = new Label(String.format("Average Length = %2.1f",
            this.gameModel.getAverageWordLength()), st);
      hdr.setFontScale(0.8f);
      grp.addActor(hdr);

      Label b = new Label(String.format("Bonus = %d", this.gameModel.getLengthBonus()), st);
      b.setFontScale(0.8f);
      b.setPosition(BlastWords.TGT_WIDTH * 0.32f, 0f);
      grp.addActor(b);
      grp.getColor().a = 0;
      grp.setPosition(bonusX, BlastWords.TGT_HEIGHT * 0.415f);
      addActor(grp);
      
      grp.addAction(sequence(fadeIn(0.6f), new Action() {
         @Override
         public boolean act(float delta) {
            scoreBoard.updateScore(gameModel.getScore());
            addBombBonus(st);
            return true;
         }
      }));
   }

   private void addBombBonus(final LabelStyle st) {
      Group grp = new Group();
      Label hdr = new Label(String.format("Bombs Remaining = %d", this.gameModel.getBombCount()),
            st);
      hdr.setFontScale(0.8f);
      grp.addActor(hdr);

      Label b = new Label(String.format("Bonus = %d", this.gameModel.getBombsBonus()), st);
      b.setFontScale(0.8f);
      b.setPosition(BlastWords.TGT_WIDTH * 0.32f, 0f);
      grp.addActor(b);
      grp.getColor().a = 0;
      addActor(grp);
      grp.setPosition(bonusX, BlastWords.TGT_HEIGHT * 0.395f);
      
      grp.addAction(sequence(fadeIn(0.6f), new Action() {
         @Override
         public boolean act(float delta) {
            scoreBoard.updateScore(gameModel.getScore());
            addClearBonus(st);
            return true;
         }
      }));
   }

   private void addClearBonus(final LabelStyle st) {
      if ( this.gameModel.getMode().equals(Mode.CLEAR) == false ) {
         addFinalScore(st);
         return;
      }
      
      Group grp = new Group();
      if (this.tilesLeft == 0) {
         Label hdr = new Label("Board cleared!", st);
         hdr.setFontScale(0.8f);
         grp.addActor(hdr);

         Label b = new Label(String.format("Bonus = %d", this.gameModel.getClearAllBonus()), st);
         b.setFontScale(0.8f);
         b.setPosition(BlastWords.TGT_WIDTH * 0.32f, 0f);
         grp.addActor(b);
      } else {
         Label hdr = new Label("Letters leftover!", st);
         hdr.setFontScale(0.8f);
         grp.addActor(hdr);

         Label b = new Label(String.format("PENALTY = %d",
               this.gameModel.getTilesLeftPenalty(this.tilesLeft)), st);
         b.setFontScale(0.8f);
         b.setPosition(BlastWords.TGT_WIDTH * 0.32f, 0f);
         grp.addActor(b);
      }
      grp.getColor().a = 0;
      grp.setPosition(bonusX, BlastWords.TGT_HEIGHT * 0.375f);
      addActor(grp);
      
      grp.addAction(sequence(fadeIn(0.6f), new Action() {
         @Override
         public boolean act(float delta) {
            scoreBoard.updateScore(gameModel.getScore());
            addFinalScore(st);
            return true;
         }
      }));

   }

   private void addFinalScore(final LabelStyle st) {
      final Group grp = new Group();
      String val = String.format("Final Score = %d", this.gameModel.getScore());
      Label hdr = new Label(val, st);
      grp.addActor(hdr);

      grp.getColor().a = 0;
      if ( this.gameModel.getMode().equals(Mode.CLEAR)) {
         grp.setPosition(groupX, BlastWords.TGT_HEIGHT * 0.32f);
      } else {
         grp.setPosition(groupX, BlastWords.TGT_HEIGHT * 0.342f);
      }
      addActor(grp);
   
      grp.addAction(sequence(fadeIn(0.6f), new Action() {
         @Override
         public boolean act(float delta) {
            if ( gameModel.getScore() > Settings.instance().getHighScoreForMode(gameModel.getMode())) {
               SoundManager.instance().playSound(SoundManager.HI_SCORE);
               Label note = new Label("New High Score!", st);
               note.setPosition(0f, BlastWords.TGT_HEIGHT * -0.04f);
               grp.addActor(note);
               Settings.instance().setHighScoreForMode(gameModel.getMode(), gameModel.getScore());
            } else {
               Label note = new Label("High Score = "+
                     Settings.instance().getHighScoreForMode(gameModel.getMode()), st);
               note.setPosition(0f, BlastWords.TGT_HEIGHT * -0.04f);
               grp.addActor(note);
            }
            return true;
         }
      }));
   }
   

   public void setListener(GameStateListener l) {
      this.listener = l;
   }
}
