package org.retorn.lifeinspace.entity;

import java.util.ArrayList;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class Monitor extends Entity {
	private TextureRegion coinship, location, liferequired, lifefound, circle, circleFilled, win;
	private static ArrayList<Boolean> life;
	public static int lit;
	
	public static int st;
	
	public static final int NONE = 0;
	public static final int COINSHIP = 1;
	public static final int LOCATION = 2;
	public static final int LIFEREQUIRED = 3;
	public static final int REGULAR = 4;

	public Monitor() {
		super("mon", 1, 1, 1, 0, 0, 0, 2);
		rendering = false;
	}

	public void render(Level lvl) {
		LM.batch.setShader(LM.brightShader);
		LM.brightShader.setUniformf("bright", 1f+LM.dice.nextFloat()*0.15f);
		LM.brightShader.setUniformf("alpha", 1f+LM.dice.nextFloat()*0.1f);
		
		if(st == COINSHIP){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
		}
		
		if(st == LOCATION){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
			LM.batch.draw(location, -location.getRegionWidth()/2f, 10600);
		}
		
		if(st == LIFEREQUIRED){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
			LM.batch.draw(location, -location.getRegionWidth()/2f, 10600);
			LM.batch.draw(liferequired, -liferequired.getRegionWidth()/2f, 10650);
		}
		
		if(st == REGULAR){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
			LM.batch.draw(location, -location.getRegionWidth()/2f, 10600);
			LM.batch.draw(liferequired, -liferequired.getRegionWidth()/2f, 10650);
			renderCircles();
		}
		
		if(st == 5){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
			LM.batch.draw(location, -location.getRegionWidth()/2f, 10600);
			renderCircles();
		}
		
		if(st == 6){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
			renderCircles();
		}
		
		if(st == 7){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
			LM.batch.draw(lifefound, -lifefound.getRegionWidth()/2f, 10680);
			renderCircles();
		}
		
		if(st == 8){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
			LM.batch.draw(lifefound, -lifefound.getRegionWidth()/2f, 10680);
		}
		
		if(st == 9){
			LM.batch.draw(coinship, -coinship.getRegionWidth()/2f, 10800);
		}
		
		if(st == 10){
			
		}
		
		if(st == 11){
			LM.batch.draw(win, -win.getRegionWidth()/2f, 10580);
		}
	}
	
	private void renderCircles(){
		float cw = circle.getRegionWidth()*6 + 200*5;
		for(int i = 0; i < 6; i++){
			LM.batch.draw(circle, -cw/2f+i*314, 10400);
			if(i < lit) LM.batch.draw(circleFilled, -cw/2f+i*314, 10400);
		}
	}
	
	public void tickMovement() {
		super.tickMovement();
		for(boolean b : life){ b = false;}
	}

	public void tick(Level lvl) {
		lit = 0;
		
		for(Entity e: lvl.eList.values()){
			if(e instanceof Pot){
				if(lvl.AABB(e, lvl.entity("shiparea"))){
					if(((Pot)e).plunt != null){
						if(((Pot)e).plunt.done) lit++;
					}
				}
			}
		}
	}
	
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		
	}

	public void init(Level lvl) {
		LM.loadTexture("img/s/coinship.png");
		LM.loadTexture("img/s/found.png");
		LM.loadTexture("img/s/life.png");
		LM.loadTexture("img/s/location.png");
		LM.loadTexture("img/s/ring_filled.png");
		LM.loadTexture("img/s/ring.png");
		LM.loadTexture("img/s/win.png");
		
		life = new ArrayList<Boolean>();
		for(int i = 0; i < 6; i++){
			life.add(false);
		}
		
		float cw =114*6 + 200*5;
		for(int i = 0; i < 6; i++){
			lvl.addEnt(new PlantDet(-cw/2f+i*314, 10000, 65, i));
		}
	}

	public boolean doneLoad(Level lvl) {
		if(
			LM.loader.isLoaded("img/s/coinship.png")
			&& LM.loader.isLoaded("img/s/found.png")
			&& LM.loader.isLoaded("img/s/life.png")
			&& LM.loader.isLoaded("img/s/location.png")
			&& LM.loader.isLoaded("img/s/ring_filled.png")
			&& LM.loader.isLoaded("img/s/ring.png")
			&& LM.loader.isLoaded("img/s/win.png")
				){
			coinship = new TextureRegion(LM.loader.get("img/s/coinship.png", Texture.class));
			location = new TextureRegion(LM.loader.get("img/s/location.png", Texture.class));
			liferequired = new TextureRegion(LM.loader.get("img/s/life.png", Texture.class));
			circle = new TextureRegion(LM.loader.get("img/s/ring.png", Texture.class));
			circleFilled = new TextureRegion(LM.loader.get("img/s/ring_filled.png", Texture.class));
			lifefound = new TextureRegion(LM.loader.get("img/s/found.png", Texture.class));
			win = new TextureRegion(LM.loader.get("img/s/win.png", Texture.class));
			
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
