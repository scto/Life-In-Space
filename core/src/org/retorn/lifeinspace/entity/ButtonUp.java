package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.level.Main;
import org.retorn.lifeinspace.tech.RTimer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class ButtonUp extends Entity {
	private TextureRegion draw, norm;
	
	private Rectangle bounds;
	
	private float inc;
	private float animTime = 0.08f;
	private float animDist = 25;
	private float scale;
	private float alpha;
	private float dis;
	public static boolean pressed;
	
	private boolean pressed1, canPress1;
	
	public int vst;
	public final int VISIBLE = 0;
	public final int INVISIBLE = 1;
	
	public int st;
	public final int NORMAL = 0;
	public final int PRESSED = 1;
	public final int DOWN = 2;

	public ButtonUp(String n, float x, float y) {
		super(n, 200, 200, 1, x, y, 0, 2);
		rendering = false;
	}

	public void render(Level lvl) {
		LM.useDefaultCamera();
		
		LM.batch.setShader(LM.brightShader);
		LM.brightShader.setUniformf("alpha", alpha);
		LM.brightShader.setUniformf("bright", 1f);
		
		LM.batch.draw(draw, pos.x, pos.y+dis,
				dim.x/2f, dim.y/2f,
				dim.x, dim.y,
				scale, scale, 0f);
		
		LM.batch.setShader(null);
		LM.useLevelCamera();
	}

	public void tick(Level lvl) {
		pressed1 = false;
		pressed = false;
		if(!Gdx.input.isTouched(1)) canPress1 = true;
		if(Gdx.input.isTouched(1) && canPress1){
			pressed1 = true;
			canPress1 = false;
		}
		
		
		//Pressed with first finger
		if((bounds.contains(InputManager.getWorldMouse(LM.defaultCam)) && InputManager.pressedLMB) )
			press(lvl, 0);
		
		//Pressed with second finger
		if((bounds.contains(Gdx.input.getX(1)/Main.resFac.x, LM.HEIGHT-Gdx.input.getY(1)/Main.resFac.y) && pressed1))
			press(lvl, 1);
		
		if(st == NORMAL)
			scale += Tween.tween(scale, 1f, 10f);
		
		manageVisibleAnims(lvl);
		
	}
	
	public void manageVisibleAnims(Level lvl){
			if(lvl.entity("coin", Coin.class).canPickUp || lvl.entity("coin", Coin.class).carrying){
					if(vst == INVISIBLE) inc = 0f;
					vst = VISIBLE;
					if(inc < animTime) inc += LM.endTime;
					dis = Tween.cubicTween(inc, -animDist, animDist, animTime);
					alpha = 1f-dis/-animDist;
			}
				
			else{
					if(vst == VISIBLE) inc = 0f;
					vst = INVISIBLE;
					if(inc < animTime*2f) inc += LM.endTime;
					dis = Tween.cubicTween(inc, 0, -animDist, animTime*2f);
					alpha = 1f-dis/-animDist;
			}
	}
	
	public void execute(){
		
	}
	
	public void press(Level lvl, int id){
		pressed = true;
		scale = 0.95f;
		if(id == 0){
			InputManager.pressedLMB = false;//Stops this from affecting the Coin.
			InputManager.downLMB = false;
		}
		execute();
		st = PRESSED;
		
		//ANIMATION
		LM.addTimer(new RTimer(){
			public void act(){
				st = NORMAL;
			scale = 1.00f;
			}},
			0.10f);
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		
	}

	public void init(Level lvl) {
		LM.loadTexture("img/button.png");
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/button.png")){
			norm = new TextureRegion(LM.loader.get("img/button.png", Texture.class));
			draw = norm;
			
			bounds = new Rectangle(pos.x, pos.y, dim.x, dim.y);
			scale = 1f;
			
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
