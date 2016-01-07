package org.retorn.lifeinspace.entity;

import com.badlogic.gdx.math.Vector3;

public interface DisEnt{
	public Vector3 dis = new Vector3();
	final int IN = 0;
	final int OUT = 1;

	public void spawn(Vector3 iPos);
	
}
