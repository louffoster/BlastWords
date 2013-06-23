package com.studio332.blastwords.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
   private static final Assets instance = new Assets();
   private Map<String, Texture> gfxMap = new HashMap<String, Texture>();
   public TextureAtlas gameAtlas;
   private BitmapFont font;

   public static Assets instance() {
      return Assets.instance;
   }
   
   public void load() {
      add("pixel","pixel.png");

      this.font = new BitmapFont(
            Gdx.files.internal("data/copperplate.fnt"), 
            Gdx.files.internal("data/copperplate.png"), false);
      
      this.gameAtlas = new TextureAtlas(Gdx.files.internal( "data/game_atlas.atlas"));
   }
   
   public BitmapFont getFont() {
      return this.font;
   }
   
   private void add( final String name, final String file) {
      Texture tx = new Texture("data/"+file);
      tx.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      this.gfxMap.put(name, tx);
   }
   
   public Texture getPixel() {
      return this.gfxMap.get("pixel");
   }
   
   public AtlasRegion getAtlasRegion(final String name) {
      return Assets.instance().gameAtlas.findRegion(name);
   }
   
   public Drawable getDrawable(final String name) {
      AtlasRegion ar =  Assets.instance().getAtlasRegion(name);
      return ( new TextureRegionDrawable( ar) );
   }
   
   public Group makeFullscreenImg( final String name ) {
      return makeFullscreenImg(name, 3);
   }
   public Group makeFullscreenImg( final String name, int parts ) {
      // load the menu bkg image and create the texture region
      Image image[] = new Image[parts];
      float h = 0;
      Group g = new Group();

      for ( int i=0;i<parts;i++) {
         String idx = ""+(i+1);
         image[i] = new Image( Assets.instance().getAtlasRegion(name+idx));
         h+= image[i].getHeight();
         g.addActor(image[i]);
      }
      float y =0;
      for ( int i=parts-1; i>=0; i--) {
         image[i].setPosition(0, y);
         y+= image[i].getHeight();
      }
      
      g.setWidth( image[0].getWidth());
      g.setHeight( h );
      return g;
      
   }
}
