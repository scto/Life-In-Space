package org.retorn.lifeinspace.engine;

public abstract class HardCollider extends Shadowable {
	//Shoves around weak colliders, doesn't collide with other hard colliders.
	public HardCollider(String n, float w, float h, float d, float x, float y, float z, int collisionType) {
		super(n, w, h, d, x, y, z, collisionType);
		
	}

}
