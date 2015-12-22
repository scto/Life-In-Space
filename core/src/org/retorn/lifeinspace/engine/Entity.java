package org.retorn.lifeinspace.engine;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public abstract class Entity {
	public boolean removed;
	public boolean inited;
	public boolean tickedMovement;//Whether or not tickMovement has already been called/the velocity has been applied to the position. Only relevant in other tickedMovement methods.
	public boolean shadowable;//Whether or not shadows get cast on this.
	public boolean colliding;//Master switch for collision.
	public boolean rendering;//Whether or not this renders. Used for render-sorting.
	public boolean superRender;//Whether or not superRender will be called on this.
	public boolean loaded;//If it passed the doneLoad method. Set by level in addPendingEnts.
	public int tweenMode;
	
	protected HashMap<String, Entity> children;//List of children this has.
	protected Entity parent;//This entity's velocity-link.
	
	public boolean hosting;//Whether or not there are children linked to this.
	public boolean child;//Whether this is a child.
		
	public int cType;//Collision Type. 0 = world static (1, 3) 1 = moving(0,1,3) 2 = no collisions. 3 = collective (0,1) 4 = AABB (not swept) 
	public ArrayList<String> ncList;//No-Collide Groups-List.
	public ArrayList<String> scList;//Specific-Collide Groups-List.
	
	private float tweenSpeed;
	private float tweenVar;//Used for the "tweenVar" method i.e. a type 3 tween.
	
	public Vector3 pos;
	public Vector3 dim;
	public Vector3 v;//Velocities
	public Vector3 vr;//Amount the edges will change in a frame.
	public Vector3 tPos;//TargetPos for tweens.
	public Vector3 oPos;//Original pos.
	public Vector3 va;//Additional velocity taken away after tickMovement.
	public Vector3 vb;//Velocity added just before collision.
	
	public String name;
	
	public Entity(String n, float w, float h, float d, float x, float y, float z, int collisionType){
			dim = new Vector3(w,h,d);//Width, Height, Depth.
			pos = new Vector3(x,y,z);//-,|,\.

		if(!(this instanceof Box)){
			tPos = new Vector3(pos);
			oPos = new Vector3(pos);
			v = new Vector3();
			vr = new Vector3();
			va = new Vector3();
			vb = new Vector3();
		
			name = n;
		
			cType = collisionType;
			ncList = new ArrayList<String>();
			scList = new ArrayList<String>();
		
			shadowable = true;//True by default, set it to false for loadcubes/invisible shit.
			colliding = true;
			rendering = true;
		
			children = new HashMap<String, Entity>();
		}
		
	}
	public abstract void render(Level lvl);
	public abstract void tick(Level lvl);
	public abstract void collide(Entity b, float collisionTime, Vector3 normal, Level lvl);//A level is passed in for spawning entities based on collisions.
	public abstract void init(Level lvl);//The level is for entities spawned by other entities like shadows.
	public abstract boolean doneLoad(Level lvl);
	public abstract void dispose();
	public abstract String getDebug();
	//Checks if everything's loaded, then assigns variables if it is. 
	//Doesn't matter if the entity is added on level-load because addPendingEnts isn't called until everything's loaded, however if this was added while a level was already loaded, this will assure it's assets are ready
	//when it tries to make objects with them.
	
	public void setPos(Vector3 p){
		//Managing children.
		if(hosting){
			for(Entity e: children.values()){
				Vector3 newPos = new Vector3();
				newPos.set(e.pos);
				newPos.sub(pos);
				newPos.add(p.x, p.y, p.z);
				e.setPos(newPos);
			}
		}
		
		pos = p;
		tPos = pos;
	}
	
	public void setPos(float x, float y, float z){
		//Managing children.
		if(hosting){
			for(Entity e: children.values()){
				Vector3 newPos = new Vector3();
				newPos.set(e.pos);
				newPos.sub(pos);
				newPos.add(x, y, z);
				e.setPos(newPos);
			}
		}
		
		pos.set(x,y,z);
		tPos = pos;
	}
	
	public void superRender(Level lvl){
		//Can be overridden if you'd like. Called while the renderables are being sorted.
	}
	
	public void setX(float x){
		pos.x = x;
		tPos.x = pos.x;
	}
	
	public void setY(float y){
		pos.y = y;
		tPos.y = pos.y;
	}
	
	public void setZ(float z){
		pos.z = z;
		tPos.z = pos.z;
	}
	public void tickTweens(){
	tickedMovement = false;
		
	if(tweenMode == 1){
		v.x = Tween.tween(pos.x, tPos.x, tweenSpeed);
		v.y = Tween.tween(pos.y, tPos.y, tweenSpeed);
		v.z = Tween.tween(pos.z, tPos.z, tweenSpeed);

		if(atTarget()) tweenMode = 0;
	}
	
	if(tweenMode == 2){
		v.x = Tween.tweenLoose(pos.x, tPos.x, tweenSpeed);
		v.y = Tween.tweenLoose(pos.y, tPos.y, tweenSpeed);
		v.z = Tween.tweenLoose(pos.z, tPos.z, tweenSpeed);

		if(atTarget()) tweenMode = 0;
	}
	
	
	}
	
	
	public void tickMovement(){
		//v.add(va);//Not sure why it wasn't like this before. Might have to change it back in WC collide method.
		pos.x += v.x*LM.delta - vr.x/2*LM.delta;
		pos.y += v.y*LM.delta - vr.y/2*LM.delta;
		pos.z += v.z*LM.delta - vr.z/2*LM.delta;
	
		dim.x += vr.x*LM.delta;
		dim.y += vr.y*LM.delta;
		dim.z += vr.z*LM.delta;
		
		tickedMovement = true;
	}
	
	//Remove added velocity.
	public void removeVa(){
		//Remove va from last frame. Called the frame after it was added
		//so if you'd prefer to not remove it in response to something this
		//frame, you can.
		v.x -= va.x;
		v.y -= va.y;
		v.z -= va.z;
		va.setZero();
	}
	
	//Gives velocity necessary to go directly from one position to the other.
	//e.g. v.x = getPosV(pos.x, pos.x + 100) will move 100 up.
	//Useful for teleporting while still allowing collisions.
	public float getPosV(float from, float to){
		return (to - from)/(LM.endTime);
	}
	
	public Vector3 getPosV(Vector3 from, Vector3 to){
		return new Vector3(
				(to.x - from.x)/(LM.endTime),
				(to.y - from.y)/(LM.endTime),
				(to.z - from.z)/(LM.endTime)
				);
	}
	
	public Vector3 getPosV(Vector3 from, float x, float y, float z){
		return new Vector3(
			(x - from.x)/(LM.endTime),
			(y - from.y)/(LM.endTime),
			(z - from.z)/(LM.endTime)
			);
	}
	
	
	
	public void flushVBuffer(){
		v.add(vb);
		vb.setZero();
	}
	
	//Called before collisions are checked.
	public void applyGameSpeed(){
		v.x *= LM.gameSpeed;
		v.y *= LM.gameSpeed;
		v.z *= LM.gameSpeed;
		
		vr.x *= LM.gameSpeed;
		vr.y *= LM.gameSpeed;
		vr.z *= LM.gameSpeed;
	}
	
	//Called after tickMovement/end of the update logic.
	public void removeGameSpeed(){
		v.x /= LM.gameSpeed;
		v.y /= LM.gameSpeed;
		v.z /= LM.gameSpeed;
		
		vr.x /= LM.gameSpeed;
		vr.y /= LM.gameSpeed;
		vr.z /= LM.gameSpeed;
	}
	
	public Vector3 getCenterPos(){
		return new Vector3(pos.x + dim.x/2, pos.y + dim.y/2, pos.z + dim.z/2);
	}
	
	//"Render Pos." Accounts for velocity to make things render NOT one frame behind the collision box.
	public Vector3 rPos(){
		return new Vector3(pos.x - v.x*LM.delta*LM.gameSpeed, pos.y - v.y*LM.delta*LM.gameSpeed, pos.z - v.z*LM.delta*LM.gameSpeed);
	}
	
	//Setting pos.
	public void setCenterPos(float x, float y, float z){
		pos.set(x - dim.x/2, y + dim.y/2, z + dim.z/2);
	}
	
	public void setCenterPos(Vector3 v){
		pos.set(v.x - dim.x/2, v.y + dim.y/2, v.z + dim.z/2);
	}
	
	//Setting tPos for type 1 + 2 tweens.
	public void setTarget(Vector3 p, float s){
		tweenSpeed = s;
		tweenMode = 1;
		tPos.set(p);
	}
	
	public void setTarget(float x, float y, float z, float s){
		tweenSpeed = s;
		tweenMode = 1;
		tPos.set(x,y,z);
	}
	
	public void setCenterTarget(Vector3 p, float s){
		tweenSpeed = s;
		tweenMode = 1;
		tPos.set(p.x-dim.x/2,p.y - dim.y/2, p.z - dim.z/2);
	}
	
	public void setCenterTarget(float x, float y, float z, float s){
		tweenSpeed = s;
		tweenMode = 1;
		tPos.set(x-dim.x/2, y - dim.y/2, z - dim.z/2);
	}

	
	public Boolean atTarget(){
		if(Tween.atTarget(pos.x, tPos.x) 
		&& Tween.atTarget(pos.y, tPos.y)
		&& Tween.atTarget(pos.z, tPos.z) ){
		v.x = 0;
		v.y = 0;
		v.z = 0;
		pos.set(tPos);
		tweenMode = 0;
		return true;
		}
		return false;
	}
	
	public Rectangle getRect(Camera c){
		Vector3 vPos =  c.project(new Vector3(pos.x, pos.y + pos.z, 0f));
		Vector3 vDPos = c.project(new Vector3(pos.x + dim.x, pos.y + pos.z + dim.y + dim.z, 0f));
		return new Rectangle(vPos.x, vPos.y, vDPos.x - vPos.x, vDPos.y - vPos.y);
	}
	
	public Rectangle getRect(Level l){
		Vector3 vPos =  l.camList.get(l.camIndex).project(new Vector3(pos.x, pos.y + pos.z, 0f));
		Vector3 vDPos = l.camList.get(l.camIndex).project(new Vector3(pos.x + dim.x, pos.y + pos.z + dim.y + dim.z, 0f));
		return new Rectangle(vPos.x, vPos.y, vDPos.x - vPos.x, vDPos.y - vPos.y);
	}
	
	public Rectangle getWorldRect(){
		//Gets a rectangle that remains in-world coordinates.
		//Just useful for getting a rectangle to check if there's a vector2 inside.
		return new Rectangle(pos.x, pos.y+pos.z, dim.x, dim.y+dim.z);
	}
	
	public void stopMovement(){
		v.setZero();
		vr.setZero();
		tweenMode = 0;
	}
	
	public void playSound(Sound s, float vol, float pitch, Level lvl){//outPrint(name +" played sound.");
		float pan = lvl.camList.get(lvl.camIndex).project(new Vector3(pos.x, pos.y+pos.z, 0)).x/LM.WIDTH;
		if(pan > 1f) pan = 1f;
		if(pan < 0) pan = 0;
		float volume = (vol*0.05f+vol*0.95f*(1f-(lvl.getCam().zoom/15f)))*LM.gameVolume*LM.gameSoundEffectVolume;
		if(volume < 0)
			volume = 0;
		s.play(volume, pitch, pan);
	}
	//Sets hosting, sets this as the parent's child.
	public void setParent(Entity e){
		if(child)
			removeParent();
		parent = e;//Entity that this entity is linked to velocity-wise.
		e.addChild(this);//Parents can modify velocities directly but children can collide + halt parents/other children.
		child = true;//True if this is a child.
	}
	
	public void removeParent(){
		child = false;
		parent.children.remove(name);
		if(parent.children.isEmpty()) parent.hosting = false;
		parent = null;
	}
	
	public void addChild(Entity e){
		children.put(e.name, e);
		e.parent = this;
		e.child = true;
		hosting = true;
	}
	
	public void removeChild(String n){
		children.get(n).child = false;
		children.get(n).removeParent();
		children.remove(n);
		if(children.isEmpty())
			hosting = false;
		
	}
	
	public void setChildVelocities(){
		for(Entity c: children.values()){
			c.v.set(v);
			//If the child's a host, it sets all it's children to the velocity it was just set to.
			//The top-level host is the master.
			if(c.hosting)
				c.setChildVelocities();
		}
	}
	
	//Find the smallest velocities in the child-tree, and set everything in it to those velocities.
	public void resolveChildVelocities(){
		Vector3 lowestV = new Vector3();
		lowestV.set(v);
		for(Entity c: children.values()){
			if(c.hosting)
				c.resolveChildVelocities();//Sets c's velocity to the lowest one in the tree.
			
			if(Math.abs(c.v.x) < Math.abs(lowestV.x)){
				lowestV.x = c.v.x;
				}
			
			if(Math.abs(c.v.z) < Math.abs(lowestV.z))
				lowestV.z = c.v.z;
			
			if(Math.abs(c.v.y) < Math.abs(lowestV.y))
				lowestV.y = c.v.y;
		}
		v.set(lowestV);
		setResolvedChildVelocities(lowestV);
		
		
	}
	//Takes vector3 resolved from the above method and applies it to the children in the tree.
	public void setResolvedChildVelocities(Vector3 lv){
		for(Entity c: children.values()){
			c.v.set(lv);
			if(c.hosting)
				c.setResolvedChildVelocities(lv);
		}
	}
	
	public void setOpacity(float a){
		for(Entity c: children.values()){
			c.setOpacity(a);
		}
	}
	
	public void addNC(String s){
		ncList.add(s);
	}
	
	public void addSC(String s){
		scList.add(s);
	}
	
	public void removeNC(String s){
		try{ncList.remove(ncList.indexOf(s));}
		catch(Exception e){outPrint("NCList does not contain '"+s+"'.");}
	}
	
	public void removeSC(String s){
		try{scList.remove(ncList.indexOf(s));}
		catch(Exception e){outPrint("SCList does not contain '"+s+"'.");}
	}
	
	public void remove(){
		removed = true;
	}
	
	
	public void outPrint(String s){
		System.out.println("["+name.toUpperCase()+"]: "+s);
	}

}
