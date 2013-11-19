package com.studio332.blastwords.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.studio332.blastwords.BlastWords;
import com.studio332.blastwords.model.BlastWordsGame;
import com.studio332.blastwords.model.BlastWordsGame.Mode;
import com.studio332.blastwords.model.BlastWordsGame.State;
import com.studio332.blastwords.model.LetterInfo;
import com.studio332.blastwords.model.LetterInfo.Type;
import com.studio332.blastwords.model.Settings;
import com.studio332.blastwords.objects.Blaster;
import com.studio332.blastwords.objects.Bomb;
import com.studio332.blastwords.objects.Bomb.BombListener;
import com.studio332.blastwords.objects.GameTimer;
import com.studio332.blastwords.objects.ScoreBoard;
import com.studio332.blastwords.objects.Tile;
import com.studio332.blastwords.util.Assets;
import com.studio332.blastwords.util.BlastWordsActions;
import com.studio332.blastwords.util.Overlay;
import com.studio332.blastwords.util.ParticleEffectActor;
import com.studio332.blastwords.util.SoundManager;

public class GameScreen extends AbstractScreen implements Blaster.Listener, GameTimer.Listener,
      GameStateListener, BombListener {
   private BlastWordsGame gameModel;
   private Blaster blaster;
   private ScoreBoard score;
   private GameTimer gameTimer;
   private Button giveUp;
   private Button pause;
   private float scrW;
   private float scrH;
   private Group gameGrp;
   private List<Tile> tiles = new ArrayList<Tile>();
   private boolean drugOverGap = false;
   private boolean blasting = false;
   private Overlay overlay = null;
   private Timer endlessTimer = null;
   private boolean inWarningZone = false;
   private Random rand = new Random();
   private int lastDropCol = -1;
   private int tileW = -1;
   private int tileH = -1;

   private static final float BOARD_LEFT = 95;
   private static final float BOARD_BOTTOM = 195;
   private static final float FAST_FALL = 900.0f;
   private static final int NUM_ROWS = 10;
   private static final int NUM_COLS = 7;

   public GameScreen(BlastWords game, Mode mode) {
      super(game);
      this.gameModel = new BlastWordsGame(mode);
   }
   
   @Override
   public void show() {
      super.show();
      
      // load the menu bkg image and create the texture region
      final Group bkg = Assets.instance().makeFullscreenImg("background");
      this.scrW = bkg.getWidth();
      this.scrH = bkg.getHeight();

      this.blaster = new Blaster();
      this.blaster.setPosition(608, 6);
      this.blaster.setListener(this);

      this.score = new ScoreBoard();
      this.score.setPosition(0.0f, this.scrH - this.score.getHeight());

      this.gameTimer = new GameTimer(this.gameModel);
      this.gameTimer.setPosition(this.scrW - this.gameTimer.getWidth() - 2.0f, scrH
            - this.gameTimer.getHeight() - 2.0f);
      this.gameTimer.setListener(this);

      createSurrenderButton();
      createPauseButton();

      this.gameGrp = new Group();
      this.gameGrp.addActor(bkg);
      this.gameGrp.addActor(this.score);
      this.gameGrp.addActor(this.gameTimer);
      this.gameGrp.addActor(this.giveUp);
      addBombs();
      this.stage.addActor(this.gameGrp);

      Image img = new Image(Assets.instance().getDrawable("overlay"));
      img.setPosition(30, 167);
      this.stage.addActor(img);
      this.stage.addActor(this.blaster);

      // fade the screen in
      this.stage.getRoot().getColor().a = 0.0f;
      this.stage.getRoot().addAction(sequence(fadeIn(0.75f), delay(0.3f), new Action() {
         @Override
         public boolean act(float delta) {
            gameScreenVisible();
            return true;
         }
      }));

      this.overlay = new Overlay();
      this.stage.addActor(this.overlay);
      this.stage.addActor(this.pause);

      this.stage.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            return handleTouchDown(event, x, y, pointer, button);
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            handleTouchUp(event, x, y, pointer, button);
         }

         public void touchDragged(InputEvent event, float x, float y, int pointer) {
            handleTouchDrag(event, x, y, pointer);
         }
      });
      
      if (this.gameModel.getMode().equals(Mode.ENDLESS)) {
         this.endlessTimer = new Timer();
         this.endlessTimer.scheduleTask(new Task() {
            @Override
            public void run() {
               endlessModeTileDrop();
            }
         }, 1.0f, 1.0f);

      }
   }
   
   private void createSurrenderButton() {
      this.giveUp = new Button(
            Assets.instance().getDrawable("surrender-off"),
            Assets.instance().getDrawable("surrender-on"),
            Assets.instance().getDrawable("surrender-on") );
      this.giveUp.setSize(105, 120);
      this.giveUp.setPosition(476, 1066);
      this.giveUp.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if ( blasting || gameModel.getState().equals(State.GAME_OVER ) ||
                 gameModel.getState().equals(State.PAUSED )) {
               return false;
            }
            handleGiveUpTap();
            return false;
         }
      });
   }

   public void createPauseButton() {
      this.pause = new Button(
            Assets.instance().getDrawable("pause-off"), 
            Assets.instance().getDrawable("pause-on"), 
            Assets.instance().getDrawable("pause-on"));
      this.pause.setSize(87, 69);
      this.pause.setPosition(16, 0);
      this.pause.addListener(new ClickListener() {
         @Override
         public void clicked(InputEvent event, float x, float y) {
            if (blasting == false && !gameModel.getState().equals(State.GAME_OVER) ) {
               handlePauseTap();
            }
         }
      });
   }

   private void addBombs() {
      for (int i = 0; i < this.gameModel.getStartingBombCount(); i++) {
         Bomb b = new Bomb();
         b.setPosition(this.scrW * 0.84f, this.scrH * 0.66f - (b.getHeight() + 20.0f) * i);
         this.gameGrp.addActor(b);
         b.setBombListener(this);
      }
   }

   private void gameScreenVisible() {
      SoundManager.instance().playGameMusic(this.gameModel.getMode());
      if (Settings.instance().rulesEverSeen() == false) {
         showRules(true);
      } else {
         initBoard();
      }
   }

   private void showRules(final boolean firstTime) {
      Settings.instance().rulesViewed();
      final Group rules = Assets.instance().makeFullscreenImg("rules1-", 3);
      final Group rules2 = Assets.instance().makeFullscreenImg("rules2-", 3);
      rules.setPosition(0, 1024.0f);
      rules2.setPosition(0, 1024.0f);
      this.stage.addActor(rules);
      rules.addAction(moveTo(0.0f, this.scrH - rules.getHeight(), 0.25f, Interpolation.pow2In));
      rules.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            stage.addActor(rules2);
            rules2.addAction( sequence(
                  moveTo(0.0f, scrH - rules.getHeight(), 0.25f, Interpolation.pow2In),
                  new Action() {
                     @Override
                     public boolean act(float delta) {
                        rules.remove();
                        return false;
                     }
                  }
                  ));
            return true;
         }
      });
      rules2.addListener(new InputListener() {
         @Override
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            hideRules(rules2, firstTime);
            return true;
         }
      });
   }

   private void hideRules(final Actor rules, final boolean firstTime) {
      rules.addAction(sequence(moveTo(0.0f, 1200f, 0.25f, Interpolation.pow2Out), new Action() {
         @Override
         public boolean act(float delta) {
            rules.remove();
            if (firstTime) {
               initBoard();
            }
            return false;
         }
      }));
   }
   
   private int countTileType( Type tileType) {
      int cnt = 0;
      for ( Tile tile : this.tiles ) {
         if ( tile.getType().equals(tileType ) ) {
            cnt++;
         }
      }
      return cnt;
  }

  // Get a new letter and ensure that there are not too many blockers
  private LetterInfo pickNewLetter() {
     int blockerCnt = countTileType(Type.BLOCKER);
     int wildCnt = countTileType(Type.WILD);
     int lockCnt = countTileType(Type.LOCKED);
     LetterInfo info = this.gameModel.newLetter(wildCnt, blockerCnt,lockCnt);
     return info;
  }

   private void initBoard() {      
      int totalRows = NUM_ROWS;
      if ( this.gameModel.getMode().equals(Mode.ENDLESS )) {
         totalRows = 5;
      }
      
      final int targetTiles = NUM_COLS*totalRows;
      
      Timer.schedule(new Task() {
         @Override
         public void run() {
            for (int i = 0; i < NUM_COLS; i++) {
               LetterInfo info = pickNewLetter();
               Tile tile = new Tile(info);
               if  ( tileW == -1 ) {
                  tileW = (int)tile.getWidth();
                  tileH = (int)tile.getHeight();
               }
               int r = tiles.size() / NUM_COLS;
               int c = tiles.size() % NUM_COLS;
               tile.setPosition(BOARD_LEFT + tile.getWidth() * c, BOARD_BOTTOM + tile.getHeight()*r);
               tiles.add(tile);
               gameGrp.addActor(tile);
            }
            SoundManager.instance().playSound(SoundManager.CLICK);
            if ( tiles.size() == targetTiles ) {
               showReady();
            }
         }
         
      }, 0.0f, 0.05f, totalRows-1);
   }

   private void startGame() {
      gameModel.setState(State.PLAYING);
      this.gameTimer.start();
      this.gameTimer.setListener(this);
   }

   /**
    * Show ready. go.. message, then set gameplay state to PLAYING!
    */
   private void showReady() {
      this.gameModel.setState(State.READY);
      final Image ready = new Image(Assets.instance().getDrawable("ready"));
      ready.setPosition(152, 600);
      ready.getColor().a = 0.0f;
      final Image go = new Image(Assets.instance().getDrawable("go"));
      go.getColor().a = 0.0f;
      go.setPosition(240, 462);
      this.stage.addActor(ready);
      this.stage.addActor(go);
      SoundManager.instance().playSound(SoundManager.PAGE_TURN);
      ready.addAction(sequence(fadeIn(0.25f), delay(0.75f, new Action() {
         @Override
         public boolean act(float delta) {
            SoundManager.instance().playSound(SoundManager.PAGE_TURN);
            go.addAction(sequence(fadeIn(0.25f), delay(0.75f, new Action() {
               public boolean act(float delta) {
                  ready.remove();
                  go.remove();
                  startGame();
                  return true;
               }
            })));
            return true;
         }
      })));
   }

   private boolean isGameStarted() {
      return (
            this.gameModel.getState().equals(State.PAUSED) || 
            this.gameModel.getState().equals( State.PLAYING) );
   }

   private Tile findTouchedTile(float x, float y) {
      for (Tile t : this.tiles) {
         if (x >= t.getX() && x <= t.getX() + t.getWidth() && y >= t.getY()
               && y <= t.getY() + t.getHeight()) {
            if ( t.getVelocity() > 0 ) {
               t.setVelocity(FAST_FALL);
               this.gameModel.dropNow();
               return null;
            }
            return t;
         }
      }
      return null;
   }

   private int getSelectedCount() {
      int cnt = 0;
      for (Tile t : this.tiles) {
         if (t.isSelected()) {
            cnt++;
         }
      }
      return cnt;
   }

   private synchronized void clearTouched() {
      this.drugOverGap = false;
      for (Tile t : this.tiles) {
         t.setSelected(false);
      }
   }
   
   private boolean isOffGrid( float x, float y ) {
      float r = BOARD_LEFT+7*this.tileW;
      float t = BOARD_BOTTOM+this.tileH*10;
      if ( x <= BOARD_LEFT || x > r||
               y < BOARD_BOTTOM || y >t ) {
         return true;
      }
      return false;
   }
   
   private int rowFromY(float screenY) {
      int y = Math.round(screenY - BOARD_BOTTOM);
      return (y / this.tileH);
   }

   private int colFromX(float screenX) {
      int x = Math.round(screenX - BOARD_LEFT);
      return (x / this.tileW);
   }
   
   private void swapTiles() {
      Tile one = null;
      Tile two = null;
      for (Tile t : this.tiles) {
         if (t.isSelected()) {
            if (one == null) {
               one = t;
            } else {
               two = t;
               break;
            }
         }
      }
      
      one.setSelected(false);
      two.setSelected(false);
      if ( one.isLocked() || two.isLocked() ) {
         SoundManager.instance().playSound(SoundManager.NO_SWAP);
         return;
      }
      
      float x = one.getX();
      float y = one.getY();
      one.setPosition(two.getX(), two.getY());
      two.setPosition(x, y); 
   }

   /**
    * Finger down
    */
   private boolean handleTouchDown(InputEvent event, float x, float y, int pointer, int button) {
      if (isGameStarted() == false || this.gameModel.getState().equals(State.PAUSED) || this.blasting) {
         return true;
      }

      if (this.blaster.grab(x, y)) {
         return true;
      }

      Tile touched = findTouchedTile(x, y);
      if (touched == null) {
         return true;
      }

      // new touch wipes all others.. but if the tile
      // touched is one that has NOT been touched, selected it
      if (getSelectedCount() > 1) {
         if (touched.isSelected() == false) {
            // touched something away from existing word. clear
            // and select current tile
            clearTouched();
            touched.setSelected(true);
         } else {
            // touched an existing word, clear it
            clearTouched();
         }
      } else {
         // special case: 0 or 1 tile touched. allow this one
         // for the possibility of a 2 tile swap
         touched.setSelected( !touched.isSelected()  );
      }

      return true;
   }
   
   /**
    * Finger DRAGGED
    */
   private void handleTouchDrag(InputEvent event, float x, float y, int pointer) {
      if (isGameStarted() == false || this.gameModel.getState().equals(State.PAUSED) || this.blasting) {
         return;
      }

      if (this.blaster.isGrabbed()) {
         this.blaster.dragPlunger(x, y);
         return;
      }
     
      // off screen or nothing selected, all done
      int touchCnt = getSelectedCount();
      if (isOffGrid(x, y) || touchCnt == 0) {
         return;
      }

      Tile tile = findTouchedTile(x, y);
      if (tile == null) {
         // no tile. now over a gap.
         this.drugOverGap = true;
         if (touchCnt == 1) {

            // get the prior tile
            Tile otherTile = null;
            for (Tile t : this.tiles) {
               if (t.isSelected()) {
                  otherTile = t;
                  break;
               }
            }

            // locked tiles cant be drug. done.
            if (otherTile.isLocked()) {
               SoundManager.instance().playSound(SoundManager.NO_SWAP);
               clearTouched();
               return;
            }

            // if row is same, drag previously selcetd tile into blank col
            int row = rowFromY(y);
            int col = colFromX(x);
            float newX = BOARD_LEFT+col*this.tileW;
            float newY = BOARD_BOTTOM+row*this.tileH;
            int tileRow = rowFromY( otherTile.getY());
            if ( tileRow == row ) {
               otherTile.setPosition(newX, newY);
            }
         } else {
            // many tiles selected, just clear the selection.
            clearTouched();
         }
         return;
      }

      // once a gap has been drug over and only 1 tile is selected, as soon as
      // we are over anoter tile, kill the selection. this prevents skipping
      // the selected tile across a gap and a single tile into the next gap.
      if (this.drugOverGap && touchCnt == 1 && tile.isSelected() == false) {
         clearTouched();
         return;
      }

      // If this is a tile that already has been touched, we're done
      if (tile.isSelected()) {
         return;
      }

      // EVIL case: 1 tile selected. Another selected that is not attached, then
      // finger dragged. Clear the original tile and contine selection from new
      if (touchCnt == 2) {
         // Get the first 2 tiles
         Tile one = null;
         Tile two = null;
         for (Tile t : this.tiles) {
            if (t.isSelected()) {
               if (one == null) {
                  one = t;
               } else {
                  two = t;
                  break;
               }
            }
         }

         // easy case: rows different?
         if (one.getY() != tile.getY()) {
            one.setSelected(false);
            tile.setSelected(true);
            return;
         } else {
            // harder: row same but cols shifted
            if (Math.abs(one.getX() - two.getX()) > this.tileW) {
               one.setSelected(false);
               tile.setSelected(true);
               return;
            }
         }
      }

      // select new tile and make sure everything has the same row (y coord)
      tile.setSelected(true);
      if (touchCnt > 1) {
         float commonY = -1f;
         for (Tile t : this.tiles) {
            if (t.isSelected()) {
               if (commonY < 0) {
                  commonY = t.getY();
               } else {
                  if (commonY != t.getY()) {
                     clearTouched();
                     return;
                  }
               }
            }
         }
      }
   }

   /**
    * Finger UP
    */
   private void handleTouchUp(InputEvent event, float x, float y, int pointer, int button) {
      if (isGameStarted() == false || this.gameModel.getState().equals(State.PAUSED) || this.blasting) {
         return;
      }
      
      if (this.blaster.isGrabbed()) {
         this.blaster.letGo();
         return;
      }
      
      // nothing touched!
      int touchCnt = getSelectedCount();
      if ( touchCnt == 0) {
          return;
      }
      
      // off the grid??
      if ( isOffGrid(x, y)  ) {
          if ( touchCnt == 1 ) {
              clearTouched();
          } else if ( touchCnt == 2 ) {
              swapTiles();
          }
          return;
      }
      
      // if only 1 tile was touched, see if it was
      // drug over some gaps. If so, drop it now.
      if ( touchCnt == 1 ) {
          if ( this.drugOverGap ) {
              clearTouched();
          }
          return;
      }
      
      // exactly 2 tiles is always a swap
      if ( touchCnt == 2 ) {
         swapTiles();
      } 
   }

   private void handleGiveUpTap() {
      if (this.gameModel.getState().equals(State.PLAYING)) {
         SoundManager.instance().playSound(SoundManager.GLASS);
         this.gameModel.setState(State.PAUSED);
         SoundManager.instance().pause();

         hideTiles(true);
         this.overlay.dimScreen();
         
         Popup p = Popup.createGiveUpPopup();
         p.setName("popup");
         p.setListener(this);
         this.stage.addActor(p);
      }
   }

   private void handlePauseTap() {
      if (isGameStarted()) {
         SoundManager.instance().playSound(SoundManager.CLICK);
         if (this.gameModel.getState().equals(State.PLAYING)) {
            this.gameModel.setState(State.PAUSED);
            SoundManager.instance().pause();
            hideTiles(true);
            this.overlay.dimScreen();
            Popup p = Popup.createPausePopup();
            p.setName("popup");
            p.setListener(this);
            this.stage.addActor(p);
         } else {
            resumeGame();
         }
      }
   }

   private void resumeGame() {
      this.gameModel.setState(State.PLAYING);
      this.pause.setChecked(false);
      this.giveUp.setChecked(false);
      SoundManager.instance().resume();

      for (Actor a : this.stage.getActors()) {
         if (a.getName() == null) {
            continue;
         }
         if (a.getName().equals("popup")) {
            ((Popup) a).fadeOut();
         }
         if (a.getName().equals("rules")) {
            hideRules(a, false);
         }
      }
      this.overlay.fadeOut();
      hideTiles(false);
      clearTouched();
   }
   
   private Tile findTileAtPos( float x, float y ) {
      for ( Tile t : this.tiles ) {
         if ( t.getX() == x && t.getY() == y ) {
            return t;
         }
      }
      return null;
   }
   
   private void replenishTiles() {
      Timer.schedule(new Task() {
         @Override
         public void run() {
            int col = 0;
            float topRowY = BOARD_BOTTOM + 9 * tileH;
            int tileCnt = tiles.size();
            for (int i = 0; i < (70 - tileCnt); i++) {
               LetterInfo info = pickNewLetter();
               Tile tile = new Tile(info);
               while (col < 7) {
                  float colX = BOARD_LEFT + col * tileW;
                  if (findTileAtPos(colX, topRowY) == null) {
                     tile.setPosition(colX, topRowY+tileH);
                     tiles.add(tile);
                     gameGrp.addActor(tile);
                     col++;
                     break;
                  } else {
                     col++;
                  }
               }
            }
         }
      }, 0.5f);
   }

   private void endlessModeTileDrop() {
      if (this.gameModel.getState().equals(State.PLAYING)) {
         if (this.inWarningZone) {
            SoundManager.instance().playSound(SoundManager.WARN);
            this.overlay.setColor(new Color(0.7f, 0f, 0f, 0f));
            this.overlay.addAction(BlastWordsActions.pulse(1.0f));
         }

         if (this.gameModel.readyToDropTile()) {
             Tile tile = new Tile( pickNewLetter() );
             int col = this.rand.nextInt(7);
             if ( this.lastDropCol != -1 ) {
                while ( col == this.lastDropCol) {
                   col = this.rand.nextInt(7);
                }
             }
             
             this.lastDropCol = col;
             tile.setPosition(BOARD_LEFT+col*tile.getWidth(), 
                   BOARD_BOTTOM+10*tile.getHeight()+tileH);
             tile.setVelocity( this.gameModel.getFallRate() );
             this.tiles.add(tile);
             tile.fadeThenDrop();
             this.gameGrp.addActor(tile);
         }
      }
   }

   @Override
   public void blastHandler() {
      blastTiles(true);
   }
   
   @Override
   public boolean detonateRequested() {
      if ( getSelectedCount() > 0 ) {
         blastTiles(false);
         this.gameModel.bombUsed();
         return true;
      }
      SoundManager.instance().playSound(SoundManager.NOT_WORD);
      return false;
   }
   
   /**
    * Explode all selected tiles. Conditionally score them.
    * @param score
    */
   private void blastTiles( final boolean score ) {
      // join letters into word
      int lockedCount = 0;
      final List<Tile> word = new ArrayList<Tile>();
      for (Tile t : this.tiles) {
         if (t.isSelected()) {
            word.add(t);
            if (t.isLocked()) {
               lockedCount++;
            }
         }
      }
      
      // just bail if no letters selected
      if ( word.size() == 0 ) {
          SoundManager.instance().playSound(SoundManager.NOT_WORD);
          return;
      }

      // sort into left->right order
      Collections.sort(word, new Comparator<Tile>() {
         @Override
         public int compare(Tile a, Tile b) {
            if (a.getX() < b.getX()) {
               return -1;
            } else if (a.getX() > b.getX()) {
               return 1;
            }
            return 0;
         }

      });
      
      if ( score ) {
         StringBuilder sb = new StringBuilder();
         for ( Tile t : word ) {
            sb.append(t.getCharacter());
         }
         if ( this.gameModel.isWord(sb.toString()) == false ) {
            SoundManager.instance().playSound(SoundManager.NOT_WORD);
            this.score.updateScore(this.gameModel.getScore());
            return;
         }
         
         this.gameModel.scoreWord( sb.toString(), lockedCount);
      }
      
      this.blasting = true;
      Timer.schedule(new Task(){
         @Override
         public void run() {
            final Tile t = word.remove(0);
            if (word.size() == 0 ) {
               blasting = false;
               GameScreen.this.score.updateScore(gameModel.getScore());
               if ( gameModel.getMode().equals(Mode.TIMED) ) {
                  replenishTiles();
               }
            }
            
            explodeTile(t);
            if ( score && t.isLocked() ) {
               final Image x2 = new Image( Assets.instance().getDrawable("times2") );
               x2.getColor().a = 0f;
               x2.setPosition( t.getX()+(t.getWidth()-x2.getWidth())*0.5f, t.getY()+t.getHeight()*0.5f);
               Action moveFade = parallel( fadeOut(1.75f), moveBy(0f, 170f, 1.75f) );
               Action s = sequence( fadeIn(0.15f), moveFade, new Action() {
                  @Override
                  public boolean act(float delta) {
                     x2.remove();
                     return false;
                  }
                  
               });
               gameGrp.addActor(x2);
               x2.addAction( s );
            }
         }
      }, 0.0f, 0.15f, word.size()-1 );
   }
   
   private void explodeTile( final Tile t ) {
      float x = t.getX()+this.tileW*0.5f;
      float y = t.getY()+this.tileH*0.5f;
      SoundManager.instance().playSound(SoundManager.BLAST);
      ParticleEffectActor pea = new ParticleEffectActor("blast.p", x,y);
      gameGrp.addActor(pea);
      stage.addAction(sequence(delay(0.4f, new Action() {
         @Override
         public boolean act(float delta) {
            tiles.remove(t);
            t.remove();
            return false;
         }
      })));
   }
   
   private void hideTiles( boolean hidden ) {
      for ( Tile t: this.tiles) {
         if ( hidden ) {
            t.hide();
         } else {
            t.show();
         }
      }
   }

   @Override
   public void timeUp() {
      SoundManager.instance().stopMusic();
      this.gameModel.setState(State.GAME_OVER);
      hideTiles(true);
      Timer.schedule(new Task() {
         @Override
         public void run() {
            gameOver();
         }
      }, 2.5f);
   }
   
   @Override
   public void timerWarning() {
      this.overlay.setColor( new Color(0.7f,0f,0f,0f));
      this.overlay.addAction( BlastWordsActions.pulse(1.0f));
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   @Override
   public void resumeTapped() {
      resumeGame();
   }

   @Override
   public void restartTapped() {
      this.game.showGameScreen(this.gameModel.getMode());
   }

   @Override
   public void quitTapped() {
      Action actions = sequence(fadeOut(0.25f), new Action() {
         @Override
         public boolean act(float delta) {
            GameScreen.this.game.showMainMenu();
            return true;
         }

      });
      this.stage.getRoot().addAction(actions);
   }

   @Override
   public void helpTapped() {
      showRules(false);
   }

   @Override
   public void giveUpTapped() {
      for (Actor a : this.stage.getActors()) {
         if (a.getName() == null) {
            continue;
         }
         if (a.getName().equals("popup")) {
            ((Popup) a).fadeOut();
            break;
         }
      }
      this.gameGrp.addAction( BlastWordsActions.fadeTo(1.0f, 0.5f));
      gameOver();
   }
   
   private void gameOver() {
      
      if ( this.endlessTimer != null ) {
         this.endlessTimer.clear();
         this.endlessTimer = null;
      }
      
      this.gameModel.setState(State.GAME_OVER);
      GameOver over = new GameOver(this.gameModel, this.score, this.tiles.size());
      over.setListener(this);
      this.overlay.dimScreen();
      this.stage.addActor(over);
      over.addAction(fadeIn(0.5f));
   }
   
   private void updateTiles( float delta ) {
      final float DROP_RATE = 420.0f;
      if ( this.gameModel.getState().equals(State.PLAYING) ) {
         for (Tile tile : this.tiles ) {
            if ( tile.canFall() == false ) {
               continue;
            }
            
            boolean canClick = true;
            if ( tile.getVelocity() == 0.0f ) {
               tile.setVelocity( DROP_RATE );
               canClick = false;
            }
            
            float origY =  tile.getY();
            float projectedY = tile.getY() - tile.getVelocity() * delta;
            if ( projectedY <= BOARD_BOTTOM) {
               tile.setPosition(tile.getX(), BOARD_BOTTOM);
               tile.stop();
            } else {
               
               for ( Tile other : tiles ) {
                  if ( tile.getId() == other.getId() ) {
                     continue;
                  }
                  if ( other.getX() == tile.getX() ) {
                     float otherTop = other.getY()+other.getHeight();
                     if ( tile.getY() >= otherTop && projectedY < otherTop  ) {
                        tile.setPosition(tile.getX(), other.getY()+other.getHeight() );
                        tile.stop();
                     }
                  }
               }
                           
               if ( tile.getVelocity() > 0.0f ) {
                  tile.setPosition(tile.getX(), projectedY);
               } else {
                  if ( canClick && tile.getY() != origY ) {
                     SoundManager.instance().playSound(SoundManager.TILE_CLICK);
                  }
               }
            }
         }
      }
   }
   
   private boolean isGameOver() {
      this.inWarningZone = false;
      if (this.gameModel.getMode().equals(Mode.ENDLESS) == false) {
         return false;
      }

      for (Tile tile : tiles) {
         if (tile.getVelocity() == 0.0f) {
            int row = rowFromY(tile.getY());
            if (row > (NUM_ROWS - 1)) {
               explodeTile(tile);
               return true;
            } else if (row == NUM_ROWS - 2) {
               this.inWarningZone = true;
            }
         }
      }
      return false;
   }
   
   @Override
   public void render(float delta) {
      float frameTime = delta;
      frameTime = Math.min(frameTime, 0.1f);
      if ( this.gameModel.getState().equals(State.PLAYING) ) {
         updateTiles(frameTime);
      }
      
      // check for end game conditions
      if ( this.gameModel.getState().equals(State.PLAYING) ) {
         if ( this.tiles.size() == 0 && this.gameModel.getMode().equals(Mode.CLEAR)) {
            Timer.schedule(new Task() {
               @Override
               public void run() {
                  if ( gameModel.getState().equals(State.GAME_OVER) == false) {
                     gameOver();
                  }
               }  
            }, 2.0f);
         } else if ( isGameOver() ) {
            if ( gameModel.getState().equals(State.GAME_OVER) == false) {
               this.gameModel.setState(State.GAME_OVER);
               hideTiles(true);
               Timer.schedule(new Task() {
                  @Override
                  public void run() {
                     gameOver();
                  }  
               }, 2.0f);
            }
         }
      }
      
      super.render(delta);
   }
}
