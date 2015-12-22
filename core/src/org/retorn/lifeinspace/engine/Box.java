package org.retorn.lifeinspace.engine;

import com.badlogic.gdx.math.Vector3;

public class Box extends Entity {
	
	public Box(float w, float h, float d, float x, float y, float z) {
		super("", w, h, d, x, y, z, 2);
	}
	


	@Override
	public void init(Level lvl) {
		shadowable = false;
		
	}

	@Override
	public void tick(Level lvl) {
		
		
	}

	@Override
	public void render(Level lvl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDebug() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean doneLoad(Level lvl) {
		// TODO Auto-generated method stub
		return false;
	}

}
