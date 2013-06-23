package com.studio332.blastwords.model;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.studio332.blastwords.model.BlastWordsGame.Mode;

/**
 * Persisted blastwords game settings
 * 
 * @author lfoster
 *
 */
public final class Settings {
   private static final Settings instance = new Settings();
   private boolean soundOn = true;
   private boolean musicOn = true;
   private Map<Mode, Integer> highScores;
   private boolean rulesEverSeen = false;
   
   private static final String PREFS_NAME = "blastwords";
   
   public static Settings instance() {
      return Settings.instance;
   }
   
   private Settings() {
      this.highScores = new HashMap<BlastWordsGame.Mode, Integer>();
      this.highScores.put(Mode.TIMED ,0);
      this.highScores.put(Mode.CLEAR ,0);
      this.highScores.put(Mode.ENDLESS ,0);
      
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      if ( p.contains("soundOn")) {
         this.soundOn = p.getBoolean("soundOn");
      }
      if ( p.contains("musicOn")) {
         this.musicOn = p.getBoolean("musicOn");
      }
      if ( p.contains("rulesEverSeen")) { 
         this.rulesEverSeen = p.getBoolean("rulesEverSeen");
      }
      if ( p.contains(Mode.TIMED.toString())) { 
         this.highScores.put(Mode.TIMED, p.getInteger(Mode.TIMED.toString()));
      }
      if ( p.contains(Mode.CLEAR.toString())) { 
         this.highScores.put(Mode.CLEAR, p.getInteger(Mode.CLEAR.toString()));
      }
      if ( p.contains(Mode.ENDLESS.toString())) { 
         this.highScores.put(Mode.ENDLESS, p.getInteger(Mode.ENDLESS.toString()));
      }
   }
   
   public boolean rulesEverSeen() {
      return this.rulesEverSeen;
   }
   
   public void rulesViewed() {
      this.rulesEverSeen = true;
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      p.putBoolean("rulesEverSeen", this.rulesEverSeen);
      p.flush();
   }
   
   public void toggleSound() {
      this.soundOn = !this.soundOn;
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      p.putBoolean("soundOn", this.soundOn);
      p.flush();
   }
   
   public boolean isSoundOn() {
      return this.soundOn;
   }
   
   public void toggleMusic() {
      this.musicOn = !this.musicOn;
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      p.putBoolean("musicOn", this.musicOn);
      p.flush();
   }
   
   public boolean isMusicOn() {
      return this.musicOn;
   }
   
   public Integer getHighScoreForMode( Mode m ) {
      return this.highScores.get(m);
   }
   
   public void setHighScoreForMode( Mode m, int score ) {
      this.highScores.put(m, score);
      Preferences p =  Gdx.app.getPreferences( PREFS_NAME );
      p.putInteger(m.toString(), score);
      p.flush();
   }
}
