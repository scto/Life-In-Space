package org.retorn.lifeinspace.tech;

import java.util.ArrayList;

public class Timus {
	private static ArrayList<RTimerLoaded> timers;
	
	public static void setUp(){
		timers = new ArrayList<RTimerLoaded>();
	}
	
	public static void manageTimers(){
		for(int i = 0; i < timers.size(); i++){
			RTimerLoaded rt = timers.get(i);
			rt.tick();
			if(rt.inc > rt.t){
				rt.r.act();
				timers.remove(rt);
				i--;
			}
		}
	}
	
	public static void addTimer(RTimer r, float t){
		timers.add(new RTimerLoaded(r, t));
	}

}
