package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Shadow;
import org.retorn.lifeinspace.engine.ShadowProvider;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SeedAsp extends Pickup {
	private Sprite draw;
	private Shadow shadow;
	
	private int st;
	
	private final int ONGROUND = 0;
	private final int INAIR = 1;

	public SeedAsp(String n, float x, float y, float z) {
		super(n, 45, 30, 27, x, y, z, 1);
	}

	public void render(Level lvl) {
		LM.batch.draw(
				draw, 
				pos.x-9, pos.y+pos.z-4, 
				60, 60);
	}
	
	public void plant(Level lvl, Pot p){
		lvl.addEnt(new Plunt("plunt"+LM.dice.nextFloat(), p));
		remove();
	}
	
	public boolean interact(Entity e, Level lvl) {
		return false;
	}
	
	private void land(){
		st = ONGROUND;
		Main.landSound.play(0.05f, 4.3f+LM.dice.nextFloat()*0.2f, 0.5f);
	}

	public void tick(Level lvl) {
		if(st == ONGROUND){
			if(!onGround) st = INAIR;
			v.x += Tween.tween(v.x, 0f, 5f);
		}
		
		if(st == INAIR){
			if(onGround) land();
		}
		
		managePSt(lvl);
		provideShadow(lvl);
	}
	
	private void provideShadow(Level lvl){
		shadow.setSize(92, 64);
		shadow.setPosition(pos.x+v.x*LM.endTime-24, pos.z+v.z*LM.endTime-28);
		lvl.addEnt(new ShadowProvider(name+"_SP", dim.x+100, dim.z+100, pos.x-50+v.x*LM.endTime, pos.y+dim.y, pos.z-50+v.z*LM.endTime, shadow));
	}

	public void init(Level lvl) {
		LM.loadTexture("img/seed1.png");
		LM.loadTexture("img/seed1_shad.png");
		
		weight = 120;
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/seed1.png") && LM.loader.isLoaded("img/seed1_shad.png")){
			
			draw = new Sprite(LM.loader.get("img/seed1.png", Texture.class));	
			shadow = new Shadow(LM.loader.get("img/seed1_shad.png", Texture.class), 0f, 0f);
			
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
