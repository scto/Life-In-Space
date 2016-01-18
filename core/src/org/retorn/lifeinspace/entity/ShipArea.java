package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.math.Vector3;

public class ShipArea extends Entity {
	private boolean inShipFlag;
	
	public ShipArea() {
		super("shiparea", 5000, 10000, 5000, -2500, 5000, -2500, 4);
		rendering = false;
	}

	public void render(Level lvl) {
		
	}

	public void tick(Level lvl) {
		
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		if(!normal.isZero() && b instanceof Coin){
			inShipFlag = true;
		}
	}
	
	

	public void tickMovement() {
		super.tickMovement();
		Main.inShip = inShipFlag;
		inShipFlag = false;
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
