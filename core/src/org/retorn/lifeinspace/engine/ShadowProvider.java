package org.retorn.lifeinspace.engine;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

//This is a class that shoots directly down and hits Shadowables.
//The coordinates/dimensions of this class only determine what it hits, not anything to do with the shadow.
//If you provide a Sprite & Entity to the constructor, it makes a shadow with some defaults.
//If only a Sprite, it's like messed up and uses the x/y of this provider, which might not be the same as the entity.

public class ShadowProvider extends Entity {
	public Shadow tr;
	public float tY;//Topmost Y it collided with. BASICALLY USELESS YFETCH IS BETTER FUCK MULTITASKING.
	
	public ShadowProvider(String n, float w, float d, float x, float y, float z, Shadow t, float py) {
		super(n, w, 1, d, x, y, z, 3);
		v.y = -1000000/LM.gameSpeed;
		rendering = false;
		tr = t;
		t.pY = py;
		tY = -1000000/LM.gameSpeed;
	}
	
	public ShadowProvider(String n, float w, float d, float x, float y, float z, Sprite t) {
		super(n, w, 1, d, x, y, z, 3);
		v.y = -1000000/LM.gameSpeed;
		rendering = false;
		tr = new Shadow(t, 0f, 0f);
		tr.pY = pos.y;
		tY = -1000000/LM.gameSpeed;
	}

	@Override
	public void tick(Level lvl) {
		//These are spawned in tick and therefore won't be ticked until the next frame.
		//Also probably just gets bumped out of eList because something with the same name gets added.
		//If an entity added this to eList, the Entity will always update before this. So the entity can use information from this.
		this.remove();
	}

	@Override
	public void render(Level lvl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		//Setting topmost Y.
		if(b.pos.y+b.dim.y > tY && b instanceof HardCollider && normal.y == -1) tY = b.pos.y+b.dim.y;
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
	public void init(Level lvl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean doneLoad(Level lvl) {
		// TODO Auto-generated method stub
		return true;
	}

}
