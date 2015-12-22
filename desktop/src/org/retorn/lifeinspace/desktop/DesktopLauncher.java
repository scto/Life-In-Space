package org.retorn.lifeinspace.desktop;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Life In Space Jam | RT";
		config.width = 1280;
		config.height = 720;
		config.resizable = false;
		
		new LwjglApplication(new LM(config.width, config.height, Color.valueOf("000000"), false, "main", true,
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

				
		}, config);
	}
}
