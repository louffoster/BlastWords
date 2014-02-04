/**
 * BlastWords
 * ParticleEffectActor.java
 * 
 * Created by Lou Foster
 * Copyright Studio332 2013. All rights reserved.
 */
package com.studio332.blastwords.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {
   private ParticleEffect effect;
   
   public ParticleEffectActor( final String fileName, float x, float y ) {
      super();
      this.effect = new ParticleEffect();
      this.effect.load(  Gdx.files.internal("data/"+fileName), Gdx.files.internal("data"));
      this.effect.setPosition(x,y);
      this.effect.start();
   }

   @Override
   public void act(float delta) {
       this.effect.update(delta);
       super.act(delta);
   }

   @Override
   public void draw(SpriteBatch batch, float parentAlpha){
       this.effect.draw(batch);
   }
   
   public void dispose() {
      this.effect.dispose();
   }
   
}
