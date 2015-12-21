package org.retorn.lifeinspace.level;

import org.retorn.lifeinspace.tech.Loadus;
import org.retorn.lifeinspace.tech.RTimer;
import org.retorn.lifeinspace.tech.Timus;
import org.retorn.lifeinspace.util.InputManager;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {
	public static float HEIGHT, WIDTH;
	public static float delta;
	public static SpriteBatch batch;
	public static boolean doneLoad;
	
	public void create () {
		batch = new SpriteBatch();
		Loadus.setUp();
		Timus.setUp();
		BG.setUp();
		setUpInput();
	}

	public void render () {
		tick();
		Timus.manageTimers();
		beginRender();
		if(!doneLoad) renderLoading();
		else renderGame();
		batch.end();
		
		InputManager.resetPress();
	}
	
	private void beginRender(){
		Gdx.gl.glClearColor(BG.col.r, BG.col.g, BG.col.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
	}
	
	private void renderGame(){
		
	}

	private void renderLoading(){
		
	}
	
	public void tick(){
		delta = Gdx.graphics.getDeltaTime();
		BG.tick();
		
		if(InputManager.pressedE) BG.tCol.set(Color.valueOf("291421"));

	}
	
	public void addTimer(RTimer r, float t){
		Timus.addTimer(r, t);
	}

	public void resize(int width, int height) {
		super.resize(width, height);
	}

	public void pause() {
		super.pause();
	}

	public void resume() {
		super.resume();
	}

	public void dispose() {
		super.dispose();
	}
	
	private void setUpInput(){
		InputManager im = new InputManager();
		im.init();
		Gdx.input.setInputProcessor(im);
	}
	
	
}
