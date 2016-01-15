package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.Level;

import com.badlogic.gdx.math.Vector3;

//Interactable. Can be Coin's PotEnt.
public interface Inter{
	//Takes whatever entity is interacting with it ie what Coin's holding. Returns true if it succeeded.
	public boolean interact(Entity e, Level lvl);
	public String getName();
	public Vector3 getCenterPos();
}
