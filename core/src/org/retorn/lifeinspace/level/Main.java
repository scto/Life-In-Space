package org.retorn.lifeinspace.level;

import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Sol;
import org.retorn.lifeinspace.entity.Coin;

import com.badlogic.gdx.graphics.Color;

public class Main extends Level{

	public Main() {
		super(Color.BLACK, "main", 60);
	}

	public void init() {
		BG.setUp();
		Sol.setUp();
		addEnt(new Coin("coin", 10, 10, 10));
		addEnt(new Sol("floor", 1400, 100, 300, -700, -100, -150, Color.valueOf("000000"), 1f));
	}

	public void postLoad() {
		
	}

	public void render() {
		
	}

	public void tick() {
		getCam().setTarget(entity("coin"), 4f);
		getCam().setZoomTarget(2f, 3f);
		
		
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
