package org.retorn.lifeinspace.level;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Sol;
import org.retorn.lifeinspace.entity.Coin;
import org.retorn.lifeinspace.entity.Debutton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Main extends Level{
	public static Vector2 resFac;

	public Main() {
		super(Color.BLACK, "main", 60);
	}

	public void init() {
		resFac = new Vector2();
		BG.setUp();
		Sol.setUp();
		
		addDebuttons();
		addEnt(new Coin("coin", 10, 10, 0));
		addEnt(new Sol("floor", 6400, 1000, 300, -700, -1000, -150, Color.valueOf("CFE0A8"), 0.1f));
		addEnt(new Sol("floor2", 600, 100, 300, 1400, 0, -150, Color.valueOf("DFE3C8"), 0.1f));
		addEnt(new Sol("floor3", 600, 400, 300, 2400, 0, -150, Color.valueOf("DFE3C8"), 0.1f));
		addEnt(new Sol("wall", 600, 800, 300, 3000, 0, -150, Color.valueOf("DFE3C8"), 0.1f));
		addEnt(new Sol("floorUpper", 1600, 50, 300, 1000, 900, -150, Color.valueOf("DFE3C8"), 0.1f));
		addEnt(new Sol("floor4", 600, 400, 300, 3600, 0, -150, Color.valueOf("DFE3C8"), 0.1f));
		
	}

	public void postLoad() {
		
	}

	public void render() {
		renderDebuttons();
	}

	public void tick() {
		tickResFacs();
		
		getCam().setTarget(entity("coin"), 5f);
		if(entity("coin").pos.y < -100) getCam().tPos.y = -100*getCam().zoom;
		getCam().setZoomTarget(2.0f, 3f);
		
		bg.set(BG.col);
	}
	
	public void renderDebuttons(){
		for(Entity e : eList.values()){
			if(e instanceof Debutton) e.render(this);
		}
	}
	
	private void addDebuttons(){
		addEnt(new Debutton("resetY", 10, 100){
			public void execute(){
			entity("coin").pos.y = 700;
			}});
		addEnt(new Debutton("resetX", 10, 30){
			public void execute(){
			entity("coin").pos.x = 0;
			}});
	}
	
	private void tickResFacs(){
		resFac.set( Gdx.graphics.getWidth()/(float)LM.WIDTH_OG,
						      Gdx.graphics.getHeight()/(float)LM.HEIGHT_OG);
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
