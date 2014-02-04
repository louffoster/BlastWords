/**
 * BlastWords
 * GameStateListener.java
 * 
 * Created by Lou Foster
 * Copyright Studio332 2013. All rights reserved.
 */
package com.studio332.blastwords.screens;

public interface GameStateListener {
   public void resumeTapped();
   public void restartTapped();
   public void quitTapped();
   public void helpTapped();
   public void giveUpTapped();
}
