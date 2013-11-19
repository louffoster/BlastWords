package com.studio332.blastwords.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.studio332.blastwords.model.LetterInfo.Type;

public class BlastWordsGame {
   public enum Mode {
      TIMED, CLEAR, ENDLESS
   };

   public enum State {
      INIT, READY, PLAYING, PAUSED, GAME_OVER
   };

   private static final int START_BOMBS = 3;
   private static final float START_FALL_RATE = 125.0f;
   private static final int BOMB_BONUS = 25;

   private Set<String> words = null;
   private List<Character> letterPool = null;
   private List<Type> letterTypes = null;
   private Mode mode;
   private State state;
   private final int timedModeDurationSecs = 90;
   private int bombs;
   private int score = 0;
   private int elapsedTime = 0;
   private int lastDropTime = 0;
   private boolean dropNow = false;
   private float fallRate = START_FALL_RATE;
   private Map<Integer, Integer> wordSizeCount;

   public BlastWordsGame(Mode mode) {

      this.mode = mode;
      this.bombs = START_BOMBS;
      this.state = State.INIT;
      this.wordSizeCount = new HashMap<Integer, Integer>();
      for (int i = 0; i <= 7; i++) {
         this.wordSizeCount.put(i, 0);
      }
      
      // create a list of tile types. this is used to reandomly
      // determine which type of tile will be drawn, normal, wild, locked, blocker
      this.letterTypes = new ArrayList<LetterInfo.Type>();
      this.letterTypes.add(Type.WILD);
      this.letterTypes.add(Type.BLOCKER);
      
      for ( int i=0; i<2; i++ ) { // 4 (orig), 3(first try) 
         this.letterTypes.add(Type.LOCKED);
      }
      for ( int i=0; i<8; i++ ) { // 10 orig
         this.letterTypes.add(Type.NORMAL);
      }

      // stuff the dictionary into a set
      FileHandle handle = Gdx.files.internal("data/blastwords_dict.txt");
      String[] wordList = handle.readString().split("\n");
      this.words = new HashSet<String>(Arrays.asList(wordList));

      // read in the letter distributon and use it to create a pool of letters
      this.letterPool = new ArrayList<Character>();
      refillLetterPool();
   }
   
   private void refillLetterPool() {
      this.letterPool.clear();
      FileHandle handle = Gdx.files.internal("data/letter_dist.dat");
      String[] lines = handle.readString().split("\n");
      for (int i = 0; i < lines.length; i++) {
         String[] inf = lines[i].split(",");
         int cnt = Integer.parseInt(inf[0]);
         for (int j = 1; j < inf.length; j++) {
            Character c = inf[j].charAt(0);
            for (int k = 0; k < cnt; k++) {
               this.letterPool.add(c);
            }
         }
      }
      Collections.shuffle(this.letterPool);
   }

   public void tick() {
      this.elapsedTime++;
   }

   public void dropNow() {
      this.dropNow = true;
   }

   public boolean readyToDropTile() {
      if (this.mode.equals(Mode.ENDLESS) == false) {
         return false;
      }

      if (this.dropNow) {
         this.dropNow = false;
         return true;
      }

      // drop rates = 5,4,3,2,2,1,1 seconds. levels change every 60 seconds
      final int level = this.elapsedTime / 60 + 1;
      final int dropThresholds[] = { 5, 4, 3, 3, 2, 2, 1, 1 }; // hold at 2 sec
                                                               // drops 1 extra
                                                               // level
      int dropAt = 1;
      if (level <= 8) {
         dropAt = dropThresholds[level - 1];
      } else {
         // starting at level 7, increase fall speed
         this.fallRate = START_FALL_RATE + 5 * (level - 8);
      }

      int delta = this.elapsedTime - this.lastDropTime;
      return (delta >= dropAt);
   }

   public float getFallRate() {
      return this.fallRate;
   }

   public int getStartingBombCount() {
      return START_BOMBS;
   }

   public int getBombCount() {
      return this.bombs;
   }

   public State getState() {
      return this.state;
   }

   public void setState(State state) {
      this.state = state;
   }

   public Mode getMode() {
      return this.mode;
   }

   public int getTimedModeDurationSecs() {
      return this.timedModeDurationSecs;
   }

   public LetterInfo newLetter(final int currWilds, int currBlockers, int currLocked) {
      final int maxWild = 1;
      final int maxBlockers = 4; // was 4
      final int maxLocked = 12;  // was 10
            
      boolean done = true;
      LetterInfo.Type type = Type.NORMAL;
      Collections.shuffle(this.letterTypes);
      do {
         done = true;
         type = this.letterTypes.get(0);
         if ( type.equals(Type.WILD) && currWilds == maxWild || 
              type.equals(Type.BLOCKER) && currBlockers == maxBlockers ||  
              type.equals(Type.LOCKED) && currLocked == maxLocked) {
            done = false;
            Collections.shuffle(this.letterTypes);
         }
      } while ( !done );
      
      // start with a normal draw
      Collections.shuffle(this.letterPool);
      Character character = null;
      character = this.letterPool.remove(0);
      
      // refill letters if empty
      if ( this.letterPool.size() == 0) {
         refillLetterPool();
      }
      
      // set the letter based on special types
      if (type.equals(Type.WILD)) {
         character = LetterInfo.WILD_CHAR;
      } else if ( type.equals(Type.BLOCKER )) {
         character = LetterInfo.BLOCKER_CHAR;
      }

      return new LetterInfo(type, character);
   }

   public boolean isWord(final String word) {
      if ( word.indexOf(LetterInfo.BLOCKER_CHAR) > -1 ) {
         return false;
      }

      String lcWordRegex = word.toLowerCase();
      for ( String dictWord : this.words) {
         int matchCnt = 0;
         if ( dictWord.length() == word.length()) {
            for ( int i=0;i<dictWord.length();i++) {
               if ( dictWord.charAt(i) == lcWordRegex.charAt(i) || lcWordRegex.charAt(i) == '?') {
                  matchCnt++;
               }
            }
            
            if ( matchCnt == dictWord.length()) {
               return true;
            }
            
         }
      }

      this.score -= 15;
      this.score = Math.max(0, this.score);
      return false;
   }

   public int getScore() {
      return this.score;
   }

   public void bombUsed() {
      this.bombs--;
   }

   public int getElapsedTime() {
      return this.elapsedTime;
   }

   public int getWordCount(int length) {
      return this.wordSizeCount.get(length);
   }

   public int getTotalWords() {
      int total = 0;
      for (Integer c : this.wordSizeCount.values()) {
         total += c;
      }
      return total;
   }

   public float getAverageWordLength() {
      float totalW = 0.0f;
      float totalL = 0.0f;
      for (int i = 3; i <= 7; i++) {
         int wordCnt = this.wordSizeCount.get(i);
         totalW += wordCnt;
         totalL += (i * wordCnt);
      }
      if (totalW == 0.0f || totalL == 0.0f) {
         return 0.0f;
      }
      return totalL / totalW;
   }

   public int getLengthBonus() {
      int wordLen = Math.round(getAverageWordLength());
      int lenBonus[] = { 0, 0, 0, 0, 200, 400, 800, 1600 };
      this.score += lenBonus[wordLen];
      return lenBonus[wordLen];
   }

   public float getWordsPerMinute() {
      float elapsedMin = this.elapsedTime / 60.0f;
      float tw = getTotalWords();
      if (elapsedMin < 1.0) {
         return tw;
      }

      return tw / elapsedMin;
   }

   public int getSpeedBonus() {
      int wpm = Math.round(getWordsPerMinute());
      // 4 5 6 7 8 9 10 11
      int wpmBonus[] = { 0, 0, 0, 0, 25, 25, 50, 50, 75, 100, 200, 400 };
      int bonus = 500;
      if (wpm <= 11) {
         bonus = wpmBonus[wpm];
      }
      this.score += bonus;
      return bonus;
   }

   public int getBombsBonus() {
      int bonus = this.bombs * BOMB_BONUS;
      this.score += bonus;
      return bonus;
   }

   public int getClearAllBonus() {
      int bonus = 250;
      this.score += bonus;
      return bonus;
   }
   
   public int getTilesLeftPenalty( final int left ) {
      int boo = left*10;
      this.score -= boo;
      this.score = Math.max(0, this.score);
      return boo;
   }

   public void scoreWord(String word, int lockedCount) {
      int wildCnt = 0;
      for (int i=0; i<word.length();i++) {
         if ( word.charAt(i) == LetterInfo.WILD_CHAR ) {
            wildCnt++;
         }
      }
      
      int wordLen = word.length() - wildCnt;
      int wordScore = 10 * wordLen;
      if (wordLen >= 5) {
         wordScore += 20; // doubled ... was 10, 20, 40
      }
      if (wordLen >= 6) {
         wordScore += 40;
      }
      if (wordLen == 7) {
         wordScore += 80;
      }

      for (int dbl = 0; dbl < lockedCount; dbl++) {
         wordScore *= 2;
      }

      // track the number of times each sized word is created
      this.score += wordScore;
      int oldCnt = this.wordSizeCount.get(wordLen);
      oldCnt++;
      this.wordSizeCount.put(wordLen, oldCnt);
   }
}
