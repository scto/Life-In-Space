package org.retorn.lifeinspace.game;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

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
			
			@Override
			public void initLoadingAssets() {
				bg = Color.valueOf("120106");
				gLay = new GlyphLayout();
				fontName = "rockfire";
			}

			@Override
			public void renderLoadingScreen() {
				inc += endTime;
				if(inc > 0.1f){
					inc = 0f;
					lText += ".";
					if(lText.equals("loading....")){ 
						loadIntroDone = true;
						lText = "loading";
						}
				}
				
				Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				batch.begin();
				gLay.setText(drawText, "loading");
				drawText(lText, WIDTH/2f-gLay.width/2f*textSc, HEIGHT/2f+gLay.height/2f*textSc, Color.valueOf("EB366C"), textSc);
				batch.end();		
			}

				
		};
	}
}
