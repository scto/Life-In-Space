package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.Level;

import com.badlogic.gdx.math.Vector3;

public class CloundTrigger extends Entity {
	private Clound cl;
	private int type;
	
	public CloundTrigger(String n, float x, float y, float z, int t, Clound c) {
		super(n, t ==  0 ? 100 : 10, t == 0 ? 20 : 500, 100, x, y, z, 0);
		cl = c;
		type = t;
		//rendering = false;
	}

	public void render(Level lvl) {
		
	}

	public void tick(Level lvl) {
		
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		if((b instanceof Coin || (b instanceof WaterPiece && type == 0)) && !normal.isZero()) cl.trigger(type, lvl);
		
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
