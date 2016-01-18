package org.retorn.lifeinspace.game;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.level.Main;
import org.retorn.lifeinspace.tech.RTimer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LMAssembly {
	public static LM get(int width, int height, boolean fc){
		return new LM(width, height, Color.valueOf("000000"), fc, "main", false,
				new Level[]{
				new Main()
		}){
			private Color bg;
			private GlyphLayout gLay;
			private float textSc = 1f;
			private float inc;
			private String lText = "loading";
			private Sound cluck;
			private TextureRegion logo;
			private int st = -1;
			
			@Override
			public void initLoadingAssets() {
				loadLord = new LISLoadLord();
				loadSound("audio/cluck.ogg");
				loadTexture("img/logo.png");
				bg = Color.valueOf("120106");
				gLay = new GlyphLayout();
				fontName = "rockfire";
				loader.finishLoading();
				cluck = loader.get("audio/cluck.ogg");
				logo = new TextureRegion(loader.get("img/logo.png", Texture.class));
				
				addTimer(new RTimer(){public void act(){
					st = 0;
					cluck.play(0.5f, 1f, 0.5f);
					
					addTimer(new RTimer(){public void act(){
						st = 1;
						loadIntroDone = true;
					}}, 0.5f);
				}}, 0.2f);
			}

			@Override
			public void renderLoadingScreen() {
				if(st == 0){
					Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1f);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
					batch.begin();
					LM.batch.draw(logo, LM.WIDTH/2f-logo.getRegionWidth()/2f,
							LM.HEIGHT/2f-logo.getRegionHeight()/2f);
					batch.end();
				}
			
			}
				
		};
	}
}
