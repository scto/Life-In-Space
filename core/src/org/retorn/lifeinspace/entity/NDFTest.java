package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;

public class NDFTest extends Entity {
	private Sprite sr, circ;
	private FrameBuffer fbo;
	
	public NDFTest() {
		super("ndftest", 1280, 720, 1, 0, 0, 0, 2);
		superRender = true;
	}

	public void render(Level lvl) {
		LM.useDefaultCamera();
		LM.batch.setShader(LM.fontShader);
		LM.fontShader.setUniformf("alph", 1f);
		LM.fontShader.setUniformf("scale", 10f);
		LM.batch.draw(fbo.getColorBufferTexture(), 0, LM.HEIGHT, LM.WIDTH, -LM.HEIGHT);
		LM.batch.setShader(null);
		LM.useLevelCamera();
	}
	
	public void superRender(Level lvl){
		LM.useDefaultCamera();
		LM.batch.begin();
		fbo.begin();
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		sr.draw(LM.batch);
		circ.draw(LM.batch);
		LM.batch.end();
		fbo.end();
		LM.useLevelCamera();
	}

	public void tick(Level lvl) {
		if(InputManager.downLeft) circ.translateX(-10);
		if(InputManager.downRight) circ.translateX(10);
		if(InputManager.downUp) circ.translateY(10);
		if(InputManager.downDown) circ.translateY(-10);
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		
	}

	public void init(Level lvl) {
		LM.loadTexture("img/st1.png");
		LM.loadTexture("img/st2.png");
		
		fbo = new FrameBuffer(Format.RGBA8888, 1280, 720, false);
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/st1.png") && LM.loader.isLoaded("img/st2.png")){
			sr = new Sprite(LM.loader.get("img/st2.png", Texture.class)); 
			sr.setPosition(100, 100);
			circ = new Sprite(LM.loader.get("img/st2.png", Texture.class));
			circ.setPosition(300, 100);
			
			return true;
		}
		
		return false;
	}

	public void dispose() {
		
	}

	public String getDebug() {
		return null;
	}

}
