package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.engine.WeakCollider;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Coin extends WeakCollider {
	private TextureRegion front, rim;
	private float rot;
	public int st;
	
	public final int ONGROUND = 0;
	public final int INAIR = 1;

	public Coin(String n, float x, float y, float z) {
		super(n, 64, 64, 9, x, y, z, 2);
	}

	public void render(Level lvl) {
		if(st == ONGROUND){
			LM.batch.draw(rim, pos.x, pos.y+pos.z, 32, 32, 64, 64, 1f, 1f, rot);
			LM.batch.draw(front, pos.x, pos.y+pos.z+10, 32, 32, 64, 64, 1f, 1f, rot);
		}
	}

	public void tick(Level lvl) {
		if(st == ONGROUND){
			manageInput(lvl);
			if(InputManager.deltaDrag.y > 100) jump();
			applyGrav(lvl);
		}
		
		else if(st == INAIR){
			manageInput(lvl);
			if(onGround) land();
			applyGrav(lvl);
		}
		
		
		onGround = false;
	}
	
	private void manageInput(Level lvl){
		if(InputManager.downLMB) v.x = Tween.tween(pos.x, InputManager.getWorldMouse(lvl.getCam()).x-dim.x/2f, 10f);
		else v.x += Tween.tween(v.x, 0f, 3f);
	}
	
	private void jump(){
		v.y = 1000;
		onGround =  false;
		jumped = true;
		st = INAIR;
	}
	
	private void land(){
		st = ONGROUND;
	}

	public void init(Level lvl) {
		weight = 50;
		
		LM.loadTexture("img/coin_face_front.png");
		LM.loadTexture("img/coin_rim.png");
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/coin_face_front.png")
		   && LM.loader.isLoaded("img/coin_rim.png")){
			
			front = new TextureRegion(LM.loader.get("img/coin_face_front.png", Texture.class));
			rim = new TextureRegion(LM.loader.get("img/coin_rim.png", Texture.class));
		
			return true;
		}
		
		return false;
	}

	public void dispose() {
		
	}

	public String getDebug() {
		return null;
	}

}
