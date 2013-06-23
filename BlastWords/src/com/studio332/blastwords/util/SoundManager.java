package com.studio332.blastwords.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.studio332.blastwords.model.BlastWordsGame.Mode;
import com.studio332.blastwords.model.Settings;

public class SoundManager {
   private static final SoundManager instance = new SoundManager();
   private Music currMusic = null;
   private Map<String, Sound> sounds = new HashMap<String, Sound>();

   public static final String PAGE_TURN = "pageturn";
   public static final String CLICK = "click";
   public static final String TILE_CLICK = "clack";
   public static final String GLASS = "glass";
   public static final String SELECT_TILE = "select";
   public static final String NO_SWAP = "noswap";
   public static final String NOT_WORD = "notword";
   public static final String BLAST = "blast";
   public static final String TICK = "tick";
   public static final String RING = "ring";
   public static final String WARN = "warn";
   public static final String HI_SCORE = "hiscore";


   public static SoundManager instance() {
      return SoundManager.instance;
   }

   public void init() {
      FileHandle soundFile = Gdx.files.internal("sound/pageturn.mp3");
      this.sounds.put(PAGE_TURN, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/menu_click.mp3");
      this.sounds.put(CLICK, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/clack.mp3");
      this.sounds.put(TILE_CLICK, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/glass.mp3");
      this.sounds.put(GLASS, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/select.mp3");
      this.sounds.put(SELECT_TILE, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/no_swap.mp3");
      this.sounds.put(NO_SWAP, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/not_word.mp3");
      this.sounds.put(NOT_WORD, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/blast.mp3");
      this.sounds.put(BLAST, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/ticking.mp3");
      this.sounds.put(TICK, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/ring.mp3");
      this.sounds.put(RING, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/alert.mp3");
      this.sounds.put(WARN, Gdx.audio.newSound(soundFile));
      soundFile = Gdx.files.internal("sound/new_hi_score.mp3");
      this.sounds.put(HI_SCORE, Gdx.audio.newSound(soundFile));
   }

   public void playMenuMusic() {
      if (Settings.instance().isMusicOn() == false) {
         return;
      }

      if (this.currMusic != null ) {
         this.currMusic.stop();
         this.currMusic.dispose();
      }

      this.currMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/menu_music.ogg"));
      this.currMusic.setLooping(true);
      this.currMusic.play();
   }

   public void stopMusic() {
      if (this.currMusic != null) {
         this.currMusic.stop();
         this.currMusic.dispose();
         this.currMusic = null;
      }
   }

   public void playGameMusic(Mode mode) {
      if (Settings.instance().isMusicOn() == false) {
         return;
      }
      
      if (this.currMusic != null && this.currMusic.isPlaying()) {
         this.currMusic.stop();
         this.currMusic.dispose();
      }
      
      String musicFile = "sound/game_music.ogg";
      if ( mode.equals(Mode.CLEAR) ) {
         musicFile = "sound/game_music_clear.ogg";
      } 
      this.currMusic = Gdx.audio.newMusic(Gdx.files.internal(musicFile));
      this.currMusic.setLooping(true);
      this.currMusic.play();
   }
   
   public void pause() {
      if ( this.currMusic != null ) {
         this.currMusic.pause();
      }
   }
   
   public void resume() {
      if ( this.currMusic != null ) {
         this.currMusic.play();
      }
   }

   public void playSound(final String sound) {
      if (Settings.instance().isSoundOn()) {
         Sound s = this.sounds.get(sound);
         if (s != null) {
            s.play();
         }
      }
   }
}
