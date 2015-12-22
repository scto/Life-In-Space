package org.retorn.lifeinspace.engine;

import com.badlogic.gdx.math.Vector3;

public abstract class WeakCollider extends Entity {
	Entity topCY;//Used to determine the soonest collision on the y-axis.
	public boolean jumped;//Used to negate y-collision modification if this entity is supposed to lift off from a platform.
	public boolean onGround;
	public boolean collidedGround;//Indicates you collided with the ground. Flushed to onGround in tickMovement. Allows you to use onGround in the collide method.
	public float weight;//Must be set by the entity subclass.
	public WeakCollider(String n, float w, float h, float d, float x, float y, float z, int collisionType) {
		super(n, w, h, d, x, y, z, collisionType);
		weight = 1;
		//1300500
		jumped = false;
	}
	
	//REVISION: No longer have to reset onGround. Done automatically in tickMovement.
	//Applies gravity. Must reset onGround at the end of the subclass' tick.
	public void applyGrav(Level lvl) {
		//Gravity
		if(tweenMode == 0 && !jumped && !onGround)
			v.y -= lvl.gravity*LM.endTime*weight;//60
		
		else if(onGround && !jumped)
			v.y -= lvl.gravity*LM.delta*100;
	}
	
	public void applyTimelessGrav(Level lvl){
		if(tweenMode == 0 && !jumped && !onGround)
			v.y -= lvl.gravity*LM.delta*weight/LM.gameSpeed;//60
		
		else if(onGround && !jumped)
			v.y -= lvl.gravity*LM.delta*100/LM.gameSpeed;
	}

	
	//Must be supered by subclasses.
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		if(normal.y == -1 && !jumped && (b instanceof HardCollider))
			collidedGround = true;
		
		if(b instanceof HardCollider){
			if(normal.x != 0){
				if(normal.x == 1){
							//If you're not moving in the same direction at a greater speed.
							if(v.x >= b.v.x - b.vr.x/2 || v.x == 0){
								v.x = b.v.x - b.vr.x/2;
								//v.x += -(pos.x - (b.pos.x - dim.x))/LM.delta;
								v.x = getPosV(pos.x, b.pos.x - dim.x - 0.5f + b.v.x*LM.delta - b.vr.x/2*LM.delta - vr.x/2*LM.delta);
								v.x -= 1.0f;
					}
				}
			
				if(normal.x == -1){
							//If you're not moving in the same direction at a greater speed.
							if(v.x <= b.v.x - b.vr.x/2 || v.x == 0){
								v.x = b.v.x +b.vr.x/2;
							v.x = getPosV(pos.x, b.pos.x + b.dim.x + 0.5f + b.v.x*LM.delta + b.vr.x/2*LM.delta + vr.x/2*LM.delta);
							v.x += 1.0f;
							}
				}
			
				tweenMode = 0;
			}	
			
			if(normal.y != 0){
				if(normal.y == -1){
					if(v.y <= b.v.y + b.vr.y/2 + 1f || v.y == 0 && !jumped){
						v.y = b.v.y +b.vr.y/2;
						v.y = getPosV(pos.y, b.pos.y + b.dim.y + 0.2f+b.v.y*LM.delta+b.vr.y/2*LM.delta);//+ 1 MIGHT NEED TO GO?
						v.y += 1.0f;
						va.y = 0;
						//Storing how much is being added to the actual v.
						//ONLY so it can be removed later.
						//Va also accounts for vr-resizing and does so based on your distance from b's center.
						float cdx = Math.abs(getCenterPos().x - b.getCenterPos().x)/(b.dim.x/2);
						if(cdx > 1)
							cdx = 1;
						float cdz = Math.abs(getCenterPos().z - b.getCenterPos().z)/(b.dim.z/2);
						if(cdz > 1)
							cdz = 1;
						va.x += b.getCenterPos().x < getCenterPos().x ? b.v.x + (b.vr.x/2)*cdx : b.v.x - (b.vr.x/2)*cdx;
						va.z += b.getCenterPos().z < getCenterPos().z ? b.v.z + (b.vr.z/2)*cdz : b.v.z - (b.vr.z/2)*cdz;
						v.x += va.x;
						v.z += va.z;
					}
				}	
			
				if(normal.y == 1 && (v.y >= b.v.y - b.vr.y/2 || v.y == 0) ){
					v.y = b.v.y - b.vr.y/2;
					v.y = getPosV(pos.y, b.pos.y - dim.y - 0.5f + b.v.y*LM.delta - b.vr.y/2*LM.delta);
					v.y -= 1.0f;
				}
				
				tweenMode = 0;
			}
		
			if(normal.z != 0){
				if(normal.z == 1){
					//If you're not moving in the same direction at a greater speed.
					if(v.z >= b.v.z - b.vr.z/2 || v.z == 0){
					v.z = b.v.z - b.vr.z/2;
					v.z = getPosV(pos.z, b.pos.z - dim.z - 0.5f + b.v.z*LM.delta - b.vr.z/2*LM.delta);
					}
				}
			
				if(normal.z == -1){
					//If you're not moving in the same direction at a greater speed.
					if(v.z <= b.v.z - b.vr.z/2 || v.z == 0){
					v.z = b.v.z + b.vr.z/2;
					v.z = getPosV(pos.z, b.pos.z + b.dim.z + 0.5f + b.v.z*LM.delta + b.vr.z/2*LM.delta);
					v.z += 1.0f;
					}
				}
			
				tweenMode = 0;
			}	
			
			if(!normal.isZero() && hosting)
				setChildVelocities();
			
		}
		//Colliding with weak collider. Stops in it's tracks + no tweenMode switch-off.
		if(b instanceof WeakCollider){
			//These are all *0.5f to make sure nothing ends up exactly bordering anything else,
			//which throws off the collision.
			if(normal.x != 0){
				//If they're going in the same direction, only stop the fast one. sameDirV1*sameDirV2 = positive number. 
				if((Math.abs(v.x) > Math.abs(b.v.x) && v.x*b.v.x >= 0) || v.x*b.v.x < 0){
				v.x *= collisionTime*0.1f;
				vr.x *= collisionTime*0.1f;
				va.x *= collisionTime*0.1f;
				tweenMode = 0;
				}
			}
			
			if(normal.y != 0){
				if((Math.abs(v.y) > Math.abs(b.v.y) && v.y*b.v.y >= 0) || v.y*b.v.y < 0){
				v.y *= collisionTime*0.1f;
				vr.y *= collisionTime*0.1f;
				va.y *= collisionTime*0.1f;
				tweenMode = 0;
				}
			}
			
			if(normal.z != 0){
				if((Math.abs(v.z) > Math.abs(b.v.z) && v.y*b.v.z >= 0) || v.z*b.v.z < 0){
				v.z *= collisionTime*0.1f;
				v.z += normal.z*v.z*0.1f;
				vr.z *= collisionTime*0.1f;
				va.z *= collisionTime*0.1f;
				tweenMode = 0;
				}
			}
				
		}
		
		
	}
	
	public void tickMovement(){
		super.tickMovement();
		
		onGround = collidedGround;
		collidedGround = false;
		
	}
	
	//TAKEN FROM GHOST HOLE. SHOULD KEEP YOU IN THE SAME PLACE WHEN YOU JUMP.
	@Override
	public void removeVa(){
		if(!jumped){
			v.x -= va.x;
			v.y -= va.y;
			v.z -= va.z;
		}
		if(onGround)
			va.setZero();
		
		jumped = false;
	}
	
}

//Weak colliders get pushed around by hard colliders and awkwardly have to shuffle around other weak colliders.