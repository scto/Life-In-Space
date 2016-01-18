package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.Level;

import com.badlogic.gdx.math.Vector3;

public class PlantDet extends Entity {
	public int id;

	public PlantDet(float x, float y, float z, int id) {
		super("pd222222"+id, 114, 400, 150, x, y, z, 4);
		rendering = false;
	}

	public void render(Level lvl) {
		
	}

	public void tick(Level lvl) {
		
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
	
	}

	public void init(Level lvl) {
		
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
