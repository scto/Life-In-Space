package org.retorn.lifeinspace.engine;

import com.badlogic.gdx.math.Vector3;
//Used for tweening stuff.
public class Tween {
	//Can be used to set a velocity, or set the actual value.
	public static float tween(float from, float to, float tweenSpeed){
		if(!atPreciseTarget(from, to)){
			return (to - from) *LM.delta * tweenSpeed*LM.gameSpeed;//ONLY TIMES'D by 0.016 to accomodate when this was x'd by delta, so there's legacy uses where there's a big ass number call me to short-form you essay.
		}
		else
			return 0f;
	}
	
	public static Vector3 tween(Vector3 from, Vector3 to, float tweenSpeed){
		if(!atPreciseTarget(from, to)){
			return new Vector3(
					(to.x - from.x) *LM.delta * tweenSpeed*LM.gameSpeed,
					(to.y - from.y) *LM.delta * tweenSpeed*LM.gameSpeed,
					(to.z - from.z) *LM.delta * tweenSpeed*LM.gameSpeed
					);//ONLY TIMES'D by 0.016 to accomodate when this was x'd by delta, so there's legacy uses where there's a big ass number call me to short-form you essay.
		}
		else
			return new Vector3(0,0,0);
	}
	
	//Same thing, no time.
	public static float tweenTimeless(float from, float to, float tweenSpeed){
		if(!atPreciseTarget(from, to)){
			return (to - from) *0.016f * tweenSpeed;//ONLY TIMES'D by 0.016 to accomodate when this was x'd by delta, so there's legacy uses where there's a big ass number call me to short-form you essay.
		}
		else
			return 0f;
	}
	
	
	
	//Supposed to make you go slightly over the target, then come back again. Type "2" in entities.
	public static float tweenLoose(float from, float to, float tweenSpeed){
		if(LM.delta < 0.04f)
			return ((to - from) * (float) LM.delta * tweenSpeed)*1.5f;
		else
			return ((to - from) * (float) 0.016 * tweenSpeed)*1.5f;
	}
	
	
	public static boolean atTarget(float from, float to){
		if(Math.round(from) == Math.round(to))
				return true;
		else
			return false;
	}
	
	public static boolean atTarget(Vector3 from, Vector3 to){
		if(Math.round(from.x) == Math.round(to.x) &&
		   Math.round(from.y) == Math.round(to.y) &&
		   Math.round(from.z) == Math.round(to.z))
			return true;
		else
			return false;
	}
	
	public static boolean atPreciseTarget(float from, float to){
		if(Math.round(from*100)/100f == Math.round(to*100)/100f)
			return true;
		
		else
			return false;
	}
	
	public static boolean atPreciseTarget(Vector3 from, Vector3 to){
		if(		Math.round(from.x*100)/100f == Math.round(to.x*100)/100f
				&&Math.round(from.y*100)/100f == Math.round(to.y*100)/100f
				&&Math.round(from.z*100)/100f == Math.round(to.z*100)/100f
				)
			return true;
		
		else
			return false;
	}
	
	public static boolean atVeryPreciseTarget(float from, float to){
		if(Math.round(from*100)/100f == Math.round(to*100)/100f)
			return true;
		
		else
			return false;
	}
	
	public static float cubicTween(float inc, float from, float delta, float dur){
		inc /= dur/2;
		if (inc < 1) return delta/2*inc*inc*inc + from;
		inc -= 2;
		return delta/2*(inc*inc*inc + 2) + from;
	}
	
	public static float elasticTween(float inc, float from, float delta, float dur){
		float ts=(inc/=dur)*inc;
		float tc=ts*inc;
		return from+delta*(33*tc*ts + -106*ts*ts + 126*tc + -67*ts + 15*inc);
	}

}
