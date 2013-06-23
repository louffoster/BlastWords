package com.studio332.blastwords.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.studio332.blastwords.BlastWords;
import com.studio332.blastwords.model.BlastWordsGame.Mode;
import com.studio332.blastwords.model.Settings;
import com.studio332.blastwords.util.Assets;
import com.studio332.blastwords.util.SoundManager;

public class MainMenuScreen extends AbstractScreen {

   public MainMenuScreen(BlastWords game) {
      super(game);
   }

   @Override
   public void show() {
      super.show();

      // load the menu bkg image and create the texture region
      final Group bkg = Assets.instance().makeFullscreenImg("menu");

      // title banner
      final Image title =  new Image( Assets.instance().getAtlasRegion("menu_title"));
      title.setPosition(40, 844);

      // Tooggle music button
      Button musicToggle = new Button(
            Assets.instance().getDrawable("music-off"),
            Assets.instance().getDrawable("music-off"),
            Assets.instance().getDrawable("music-on") );
      musicToggle.setSize(100, 120);
      musicToggle.setPosition(224, 337);
      musicToggle.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            Settings.instance().toggleMusic();
            if (Settings.instance().isMusicOn()) {
               SoundManager.instance().playMenuMusic();
            } else {
               SoundManager.instance().stopMusic();
            }
            return false;
         }
      });
      musicToggle.setChecked(Settings.instance().isMusicOn());

      // toggle sound
      Button soundToggle = new Button(
            Assets.instance().getDrawable("sound-off"),
            Assets.instance().getDrawable("sound-off"),
            Assets.instance().getDrawable("sound-on") );
      soundToggle.setSize(60,72);
      soundToggle.setPosition(musicToggle.getX() + 80, musicToggle.getY());
      soundToggle.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            SoundManager.instance().playSound(SoundManager.CLICK);
            Settings.instance().toggleSound();
            return false;
         }
      });
      soundToggle.setChecked(Settings.instance().isSoundOn());

      // credits
      final Image credits = new Image( Assets.instance().getAtlasRegion("credits"));
      credits.setPosition(-credits.getWidth(),
            (BlastWords.TGT_HEIGHT - credits.getHeight()) * 0.6f);
      credits.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y,
               int pointer, int button) {
            SoundManager.instance().playSound(SoundManager.PAGE_TURN);
            credits.addAction(moveTo(-credits.getWidth(),
                  (BlastWords.TGT_HEIGHT - credits.getHeight()) * 0.6f, 0.75f,
                  Interpolation.swingOut));
            return false;
         }
      });

      // legend
      final Group legend = Assets.instance().makeFullscreenImg("legend");
      legend.setPosition(-legend.getWidth(),(BlastWords.TGT_HEIGHT-legend.getHeight())/2);
      legend.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y,
               int pointer, int button) {
            SoundManager.instance().playSound(SoundManager.PAGE_TURN);
            legend.addAction(moveTo(-legend.getWidth(),
                  (BlastWords.TGT_HEIGHT - legend.getHeight()) * 0.6f, 0.75f,
                  Interpolation.swingOut));
            return false;
         }
      });
      
      // MENU options ( and handler )
      final Image opts = new Image( Assets.instance().getAtlasRegion("menu_opts"));
      opts.setPosition(200, 300);
      opts.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y,
               int pointer, int button) {
            float h = opts.getHeight();
            float flippedY = h - y;
            if (x >= 150.0f && x <= 330) {
               if (flippedY >= 65.0f && flippedY <= 150.0f) {
                  startGame(Mode.TIMED);
               } else if (flippedY > 150.0f && flippedY <= 235.0f) {
                  startGame(Mode.CLEAR);
               } else if (flippedY > 235.0f && flippedY <= 320.0f) {
                  startGame(Mode.ENDLESS);
               } else if (flippedY > 365.0f && flippedY <= 410.0f) {
                  showPage(legend, legend.getWidth(), legend.getHeight());
               } else if (flippedY > 410.0f && flippedY <= 455.0f) {
                  showPage(credits, credits.getWidth(), credits.getHeight());
               }
            } 
            return true;
         }
      });
      
      Group menuGroup = new Group();
      menuGroup.addActor(bkg);
      menuGroup.addActor(opts);
      menuGroup.addActor(title);
      menuGroup.addActor(musicToggle);
      menuGroup.addActor(soundToggle);
      menuGroup.addActor(credits);
      menuGroup.addActor(legend);
      menuGroup.getColor().a = 0.0f;
      this.stage.addActor(menuGroup);

      Action actions = sequence(fadeIn(0.4f), Actions.delay(0.3f), new Action() {
         @Override
         public boolean act(float delta) {
            SoundManager.instance().playMenuMusic();
            return true;
         }

      });

      menuGroup.addAction(actions);
   }

   private void showPage(Actor page, float pageW, float pageH) {
      SoundManager.instance().playSound(SoundManager.CLICK);
      page.addAction(moveTo((BlastWords.TGT_WIDTH - pageW) * 0.6f,
            (BlastWords.TGT_HEIGHT - pageH) * 0.6f, 0.75f, Interpolation.swingIn));
      page.addAction(parallel(sequence(delay(0.5f), new Action() {
         @Override
         public boolean act(float delta) {
            SoundManager.instance().playSound(SoundManager.PAGE_TURN);
            return true;
         }
      })));
   }

   private void startGame(final Mode mode) {
      SoundManager.instance().playSound(SoundManager.CLICK);
      SoundManager.instance().stopMusic();
      Action actions = sequence(fadeOut(0.1f), new Action() {
         @Override
         public boolean act(float delta) {
            MainMenuScreen.this.game.showGameScreen(mode);
            return false;
         }

      });
      this.stage.getRoot().addAction(actions);
   }
}
