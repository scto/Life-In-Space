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

public class ButtonShip extends Entity {
	private TextureRegion draw, norm;
	
	private Rectangle bounds;
	
	private float rot;
	private float inc;
	private float animTime = 0.08f;
	private float animDist = 25;
	private float scale;
	private float alpha;
	private float dis;
	public static boolean pressed;
	
	public int vst;
	public final int VISIBLE = 0;
	public final int INVISIBLE = 1;
	
	public int rst = 1;
	public float rinc = 10f;
	public final int NOTINSHIP = 0;
	public final int INSHIP = 1;
	
	public int st;
	public final int NORMAL = 0;
	public final int PRESSED = 1;
	public final int DOWN = 2;

	public ButtonShip(String n, float x, float y) {
		super(n, 100, 100, 1, x, y, 0, 2);
		rendering = false;
	}

	public void render(Level lvl) {
		LM.useDefaultCamera();
		
		LM.batch.setShader(LM.brightShader);
		LM.brightShader.setUniformf("alpha", alpha*0.9f);
		LM.brightShader.setUniformf("bright", 1f + (1f+rot/180f)*0.6f);
		
		LM.batch.draw(draw, pos.x, pos.y+dis,
				dim.x/2f, dim.y/2f,
				dim.x, dim.y,
				scale, scale, rot);
		
		LM.batch.setShader(null);
		LM.useLevelCamera();
	}

	public void tick(Level lvl) {
		pressed = false;
		
		if(!Main.virgin){
		//Pressed with first finger
		if((bounds.contains(InputManager.getWorldMouse(LM.defaultCam)) && InputManager.pressedLMB) )
			press(lvl, 0);
		
		//Pressed with second finger
		if((bounds.contains(Gdx.input.getX(1)/Main.resFac.x, LM.HEIGHT-Gdx.input.getY(1)/Main.resFac.y) && (Gdx.input.isTouched(1))))
			press(lvl, 1);
		}
		
		if(st == NORMAL)
			scale += Tween.tween(scale, 1f, 10f);
		
		if(Main.inShip || (!Main.inShip && Main.transitionSt == Main.AM)){
			if(rst == NOTINSHIP) rinc = 0f;
			rst = INSHIP;
			if(rinc < 0.2f) rinc += LM.endTime;
			rot = Tween.cubicTween(rinc, 0, -180, 0.2f);
			
			if(rinc > 0.2f) rot = -180f;
		}
		
		else{
			if(rst == INSHIP) rinc = 0f;
			rst = NOTINSHIP;
			if(rinc < 0.2f) rinc += LM.endTime;
			rot = Tween.cubicTween(rinc, -180, 180, 0.2f);
			
			if(rinc > 0.2f) rot = 0f;
		}
		
		manageVisibleAnims(lvl);
		
	}
	
	public void manageVisibleAnims(Level lvl){
		if(!Main.virgin){
			if(vst == INVISIBLE) inc = 0f;
			vst = VISIBLE;
			if(inc < animTime) inc += LM.endTime;
			dis = Tween.cubicTween(inc, -animDist, animDist, animTime);
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
			scale = 1.05f;
			}},
			0.1f);
		
		LM.addTimer(new RTimer(){
			public void act(){
				st = NORMAL;
			scale = 1.00f;
			}},
			0.25f);
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		
	}

	public void init(Level lvl) {
		LM.loadTexture("img/buttonShip.png");
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/buttonShip.png")){
			norm = new TextureRegion(LM.loader.get("img/buttonShip.png", Texture.class));
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
