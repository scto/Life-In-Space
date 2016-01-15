package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.engine.WeakCollider;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.math.Vector3;

public abstract class Pickup extends WeakCollider implements Inter {
	protected float fDis, fDisX;//Float dises.

	protected int pSt;//Pickup State
	
	protected final int DEFAULT = 0;
	protected final int CARRIED = 1;

	public Pickup(String n, float w, float h, float d, float x, float y, float z, int collisionType) {
		super(n, w, h, d, x, y, z, collisionType);
	}
	
	public String getName(){
		return name;
	}
	
	public void managePSt(Level lvl){
		if(pSt == DEFAULT){
			cType = 1;
			applyGrav(lvl);
		}
		
		if(pSt == CARRIED){
			fDis += Tween.tween(fDis, 5f, 10f);
			fDisX += Tween.tween(fDisX, 0f, 10f);
			cType = 2;
			Vector3 c = lvl.entity("coin").getCenterPos();
			Vector3 vc = lvl.entity("coin").v;
			v.x = getPosV(pos.x, c.x-dim.x/2f + vc.x*LM.endTime + fDisX);
			v.y = getPosV(pos.y, c.y + 60 + (vc.y > 0 ? vc.y * LM.endTime : 0f)+fDis);
			v.z = getPosV(pos.z, c.z-dim.z/2f);
		}
	}
	
	public void pickup(Level lvl){
		cType = 2;
		pSt = CARRIED;
		fDis = -10f;
		fDisX = -(lvl.entity("coin").getCenterPos().x-dim.x/2f-pos.x)*0.3f;
		Main.jumpSound.play(0.2f, 3.3f, 0.5f);
	}

	public void drop(Level lvl){
		cType = 1;
		pSt = DEFAULT;
		v.setZero();
		setPos(pos.x, lvl.entity("coin").pos.y, 80);
	}
	
}
