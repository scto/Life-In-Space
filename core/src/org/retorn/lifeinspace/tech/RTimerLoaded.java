package org.retorn.lifeinspace.tech;

import org.retorn.lifeinspace.level.Main;


public class RTimerLoaded {
	public RTimer r;
	public float t;
	public float inc;
	
	public RTimerLoaded(RTimer rt, float time){
		r = rt;
		t = time;
	}
	
	public void tick(){inc += Main.delta;}
}
