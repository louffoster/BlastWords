/**
 * BlastWords
 * AbstractScreen.java
 * 
 * Created by Lou Foster
 * Copyright Studio332 2013. All rights reserved.
 */
package com.studio332.blastwords.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.studio332.blastwords.BlastWords;

public class AbstractScreen  implements Screen  {
   protected final Stage stage;
   protected final BlastWords game;
   
   public AbstractScreen(BlastWords game) {
      this.game = game;
      this.stage = new Stage(800,1205, false);
   }

   protected String getName() {
      return getClass().getSimpleName();
   }

   @Override
   public void render(float delta) {
      Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
      Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
      
      // update and draw the stage actors
      stage.act( delta );
      stage.draw();
   }

   @Override
   public void resize(int width, int height) {
   }
   
   public Stage getStage() {
      return this.stage;
   }

   @Override
   public void show() {
      // let this screen pocess events (like touch)
      Gdx.input.setInputProcessor( this.stage );
   }

   @Override
   public void hide() {
   }

   @Override
   public void pause() {
   }

   @Override
   public void resume() {
   }

   @Override
   public void dispose() {
      this.stage.dispose();
   }
}
