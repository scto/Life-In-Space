package org.retorn.lifeinspace.level;

import org.retorn.lifeinspace.engine.Level;

import com.badlogic.gdx.graphics.Color;

public class Main extends Level{

	public Main() {
		super(Color.BLACK, "main", 60);
	}

	public void init() {
		BG.setUp();
	}

	public void postLoad() {
		
	}

	public void render() {
		
	}

	public void tick() {
		bg.set(BG.col);
	}

	public void enter() {
		
	}

	public void exit() {
		
	}

	public String getDebug() {
		return null;
	}

	public void pause() {
		
	}

	public void resume() {
		
	}

}
