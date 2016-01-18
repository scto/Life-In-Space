package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Sol;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Ship extends Entity{

	public Ship() {
		super("ship", 1, 1, 1, 1, 1, 1, 2);
		rendering = false;
	}

	public void render(Level lvl) {
		
	}

	public void tick(Level lvl) {
		
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		
	}

	public void init(Level lvl) {
		lvl.addEnt(new ShipArea());
		
		lvl.addEnt(new Sol("Ship Edge 0", 500, 4000, 270, -3000, 9000, -150, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
		lvl.addEnt(new Sol("Ship Edge 1", 500, 4000, 270, 2500, 9000, -150, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
		
		lvl.addEnt(new Sol("Ship Wall 0", 5000, 1000, 10, -2500, 9110, 130, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
		lvl.addEnt(new Sol("Ship Wall 1", 5000, 1000, 10, -2500, 9100, 120, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
		lvl.addEnt(new Sol("Ship Wall 1 Mini", 5000, 20, 5, -2500, 10000, 115, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
		lvl.addEnt(new Sol("Ship Wall 2", 5000, 4500, 20, -2500, 11400, 120, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
		lvl.addEnt(new Sol("Ship Floor", 5000, 1000, 270, -2500, 9000, -150, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
		
		//lvl.addEnt(new Sol("Ship Pillar", 80, 2000, 50, -1900, 10800, -130, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
	//	lvl.addEnt(new Sol("Ship Pillar Base", 100, 800, 70, -1910, 10000, -140, Color.valueOf("292428"), 0.1f, "img/shiptex.png", 1f));
		
	}

	public boolean doneLoad(Level lvl) {
		return true;
	}

	public void dispose() {
		
	}

	public String getDebug() {
		return null;
	}

}
