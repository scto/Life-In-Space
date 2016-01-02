package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.HardCollider;
import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Shadow;
import org.retorn.lifeinspace.engine.ShadowProvider;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.engine.WeakCollider;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class Coin extends WeakCollider {
	private Sound jumpSound;
	private TextureRegion front, rim;
	private Shadow shadow;
	
	private float maxV = 3.0f;
	private float accel = 10f;
	
	private float rot;
	private float rotV;//Amount added to rot each frame.
	public int st;
	
	public final int ONGROUND = 0;
	public final int INAIR = 1;
	public final int GRAVLESS = 2;
	
	public final float drawSize = 128;

	public Coin(String n, float x, float y, float z) {
		super(n, 119, 113, 18, x, y, z, 1);
	}

	public void render(Level lvl) {
		if(st == ONGROUND || st == INAIR || st == GRAVLESS){
			LM.batch.draw(
					rim, 
					getCenterPos().x-drawSize/2f, 
					getCenterPos().y+pos.z-drawSize/2f + 15, 
					drawSize/2f, drawSize/2f, drawSize, drawSize,
					1f, 1f, rot*0.5f);
			LM.batch.draw(
					front, 
					getCenterPos().x-drawSize/2f, 
					getCenterPos().y+pos.z-drawSize/2f + 2, 
					drawSize/2f, drawSize/2f, drawSize, drawSize,
					1f, 1f, rot*0.5f);
		}
		
		LM.useDefaultCamera();
		//LM.drawText("v.x: "+v.x, 100, 600);
		LM.useLevelCamera();
	}

	public void tick(Level lvl) {
		if(st == ONGROUND){
			manageInput(lvl);
			if(InputManager.deltaDrag.y > 35*Main.resFac.y)  jump();
			else if(!onGround) st = INAIR;
			applyGrav(lvl);
		}
		
		else if(st == INAIR){
			if(v.y < 0) weight += Tween.tween(weight, 300, 3); //Tween up weight if you're falling
			manageInput(lvl);
			if(onGround) land(lvl);
			applyGrav(lvl);
		}
		
		else if(st == GRAVLESS){
			manageGravlessInput(lvl);
		}
		
		manageRot();
		provideShadow(lvl);
		onGround = false;
		
		//debug
		if(InputManager.pressedE) pos.y = 1000;
	}
	
	private void manageInput(Level lvl){
		if(InputManager.downLMB){
			float wMX =(InputManager.getWorldMouse(lvl.getCam()).x-getCenterPos().x)*maxV;
			
			v.x += Tween.tween(v.x, wMX, accel * (!onGround ? 0.1f : 1f));
		}
		
		else v.x += Tween.tween(v.x, 0, accel*0.8f * (!onGround ? 0.2f : 1f));//If mouse is behind
	}
	
	private void manageGravlessInput(Level lvl){
		if(InputManager.downLMB){ 
			float wMX = (InputManager.getWorldMouse(lvl.getCam()).x-getCenterPos().x)*maxV;
			float wMY = (InputManager.getWorldMouse(lvl.getCam()).y-getCenterPos().y)*maxV;
			
			v.x += Tween.tween(v.x, wMX, accel*0.05f);
			v.y += Tween.tween(v.y, wMY, accel*0.05f);
		}
		
		else{
			v.x += Tween.tween(v.x, 0f, accel*0.15f);
			v.y += Tween.tween(v.y, 0f, accel*0.15f);
		}
	}
	
	private void manageRot(){
		if(st == ONGROUND) rotV = -v.x * 0.024f;
		if(st == INAIR) rotV += Tween.tween(rotV, 0f, 1f);
		if(st == GRAVLESS){
				rotV += Tween.tween(rotV, Math.abs(v.x) > 1500 ? -v.x*0.001f : 0f,  1f);
		}
		
		rot += rotV;
	}
	
	
	
	@Override
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		if(normal.x != 0 && b instanceof HardCollider && st == INAIR){
			v.x *= -0.4f;//Bounce
			v.y += Math.abs(v.x)*0.9f;
		}
		else if(st == GRAVLESS && b instanceof HardCollider){
			if(normal.y != 0){
				v.y *= -0.3f;
				rotV = -v.x*0.024f;
			}
			if(normal.x != 0){
				v.x *= -0.3f;
				rotV = -v.y*0.024f;
			}
		}
		else super.collide(b, collisionTime, normal, lvl);
	}

	private void jump(){
		v.y = 2250;
		onGround =  false;
		jumped = true;
		st = INAIR;
		jumpSound.play(LM.gameSoundEffectVolume*0.5f, 0.9f+LM.dice.nextFloat()*0.2f, 0.5f);
	}
	
	private void land(Level lvl){
		weight = 120;
		st = ONGROUND;
		lvl.getCam().setShake(10f*lvl.getCam().zoom, 100f, 30f*lvl.getCam().zoom);
	}

	public void init(Level lvl) {
		weight = 120;
		
		LM.loadTexture("img/coin_face_front.png");
		LM.loadTexture("img/coin_rim.png");
		LM.loadTexture("img/coin_shad.png");
		
		LM.loadSound("audio/coin_jump.ogg");
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/coin_face_front.png")
		   && LM.loader.isLoaded("img/coin_rim.png")
		   && LM.loader.isLoaded("img/coin_shad.png")
		   && LM.loader.isLoaded("audio/coin_jump.ogg")){
			
			front = new TextureRegion(LM.loader.get("img/coin_face_front.png", Texture.class));
			rim = new TextureRegion(LM.loader.get("img/coin_rim.png", Texture.class));
			
			shadow = new Shadow(LM.loader.get("img/coin_shad.png", Texture.class), 0f, 0f);
			
			jumpSound = LM.loader.get("audio/coin_jump.ogg", Sound.class);
			
			return true;
		}
		
		return false;
	}
	
	private void provideShadow(Level lvl){
		shadow.setSize(300, 55);
		shadow.setPosition(pos.x+v.x*LM.endTime-88, pos.z+v.z*LM.endTime-17);
		lvl.addEnt(new ShadowProvider(name+"_SP", dim.x+100, dim.z+100, pos.x-50+v.x*LM.endTime, pos.y+dim.y, pos.z-50+v.z*LM.endTime, shadow));
	}

	public void dispose() {
		
	}

	public String getDebug() {
		return "weight: "+weight;
	}

}
