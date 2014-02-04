/**
 * BlastWords
 * BlastWordsActions.java
 * 
 * Created by Lou Foster
 * Copyright Studio332 2013. All rights reserved.
 */
package com.studio332.blastwords.util;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class BlastWordsActions {
   
   static public Action fadeTo (float alpha, float duration) {
      AlphaAction action = new AlphaAction();
      action.setAlpha(alpha);
      action.setDuration(duration);
      return action;
   }
   
   static public Action pulse (float duration) {
      SequenceAction sa = new SequenceAction();
      sa.addAction(BlastWordsActions.fadeTo(0.2f, duration*0.5f));
      sa.addAction( fadeOut(duration*0.5f));
      return sa;
   }
}
