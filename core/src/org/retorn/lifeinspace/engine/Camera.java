package org.retorn.lifeinspace.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class Camera extends OrthographicCamera {
	public boolean tweenMode, tweenZoomMode;
	public float tweenSpeed, tweenZoomSpeed, tZoom;
	public Vector2 tPos;
	public Vector2 pos;//Position without the shake.
	public Vector2 shakeDis;//X/Y shake displacements.
	public float shake;//Radius of shake. Gets decreased by shakeDecay.
	private float shakeSpeed;//How fast sin/cos are iterated through.
	public float inc;//Constantly increasing for use with sin/cos. Increment.
	private float shakeDecay;//How fast the shake decays.
	public float rotation;//Current angle.
	public float tRotation;//Target Rotation.
	public float rotationTweenSpeed;
	public boolean tweenRotation;//If true, do it.
	
	public Camera(float Width, float Height){
		super(Width, Height);
		tPos = new Vector2(0,0);
		pos = new Vector2(0,0);
		shakeDis = new Vector2(0,0);
		tweenMode = false;
	}
	
	@Override
	public void rotate(float angle) {
		rotation += angle;
		super.rotate(angle);
	}
	//My own setRotation
	public void setRotation(float angle){
		super.rotate(-rotation);
		super.rotate(angle);
		rotation = angle;
	}
	
	//Tween-rotation.
	public void setRotationTarget(float angle, float speed){
		tRotation = angle;
		rotationTweenSpeed = speed;
		tweenRotation = true;
	}

	public void setTarget(Vector2 f, float speed){
		tPos.set(f);
		tweenSpeed = speed;
		tweenMode = true;
	}
	
	public void setZoomTarget(float t, float speed){
		tweenZoomSpeed = speed;
		tZoom = t;
		tweenZoomMode = true;
	}
	
	public void setTarget(float x, float y, float speed){
		tPos.set(x, y);
		tweenSpeed = speed;
		tweenMode = true;
	}
	
	public void setTarget(Entity e, float speed){
		tPos.set(e.getCenterPos().x, e.getCenterPos().y + e.getCenterPos().z);
		tweenSpeed = speed;
		tweenMode = true;
	}
	
	public void setTarget(Entity e, Entity b, float speed){
		tPos.set((e.getCenterPos().x+b.getCenterPos().x)/2f, (e.getCenterPos().y+b.getCenterPos().y)/2f + (e.getCenterPos().z+b.getCenterPos().z)/2f);
		tweenSpeed = speed;
		tweenMode = true;
	}
	
	public void setPos(Entity e){
		pos.set(e.getCenterPos().x, e.getCenterPos().y + e.getCenterPos().z);
	}
	
	public void setPos(Entity e, Entity b){
		pos.set((e.getCenterPos().x+b.getCenterPos().x)/2f, (e.getCenterPos().y+b.getCenterPos().y)/2f + (e.getCenterPos().z+b.getCenterPos().z)/2f);
	}
	
	public void setPos(float x, float y){
		pos.set(x, y);
	}
	
	@Override
	public void update(){
		//Checks whether constructor is done since the ortho cam's constructor calls update.
		if(pos != null){
		
		if(tweenMode){
			pos.x += Tween.tween(pos.x, tPos.x, tweenSpeed);
			pos.y += Tween.tween(pos.y, tPos.y, tweenSpeed);
			
			if(atTarget())
				tweenMode = false;
		}
	
		if(tweenZoomMode){
			zoom += Tween.tween(zoom, tZoom, tweenZoomSpeed);
			
			//Tween is done/can't tween any more.
			if(Tween.atVeryPreciseTarget(zoom, tZoom) || Tween.tween(zoom, tZoom, tweenZoomSpeed) == 0.0f){
				tweenZoomMode = false;}
		}
		
		if(tweenRotation){
			setRotation(rotation + Tween.tween(rotation, tRotation, rotationTweenSpeed));
			
			//Tween's done/can't do it anymore.
			if(Tween.atPreciseTarget(rotation, tRotation) || Tween.tween(rotation, tRotation, rotationTweenSpeed) == 0.0f)
				tweenRotation = false;
		}
		
		//Managing shakes.
		if(shake > 0) shake -= shakeDecay*LM.endTime;
		if(shake < 0) shake = 0;
		inc += shakeSpeed*LM.endTime;
		//Tweening dis values
		shakeDis.x += Tween.tween(shakeDis.x,(float)Math.cos(inc)*shake - (float)Math.sin(inc*2f)*shake*0.5f, 15.5f);
		shakeDis.y += Tween.tween(shakeDis.y,(float)Math.sin(inc)*shake + (float)Math.cos(inc*1.3f)*shake*0.5f*(float)Math.cos(inc*0.4f), 15.5f);
		
		//Camera's actual position is set to pos + shake values.
		position.x = pos.x + shakeDis.x;
		position.y = pos.y + shakeDis.y;
		
	}
		super.update(true);
	}
	
	//Radius is how far out the shake goes.
	//Decay is the speed at which is decreases to 0.
	//Speed is just how fast it moves around while it decays.
	public void setShake(float radius, float speed, float decay){
		shake = radius;
		shakeSpeed = speed;
		shakeDecay = decay;
	}
	
	//Starts the shake at a certain angle in degrees.
	public void setShake(float radius, float speed, float decay, float startAng){
		shake = radius;
		shakeSpeed = speed;
		shakeDecay = decay;
		inc = (startAng/360f)*(float)Math.PI*2;
	}
	
	public void addShake(float s){
		shake += s;
	}
	
	public boolean atTarget(){
		if(Tween.atTarget(pos.x, tPos.x)
		&&Tween.atTarget(pos.y, tPos.y))
			return true;
		else
			return false;
			
	}
	
	public boolean atZoomTarget(){
		if(Tween.atPreciseTarget(zoom, tZoom))
			return true;
		else
			return false;
	}
	
	
}
