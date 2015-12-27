package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.tech.RTimer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
//Button that renders with defaultCam. Used to debug on android.
public abstract class Debutton extends Entity {
	private Color col;
	private int st;
	
	private final int NORMAL = 0;
	private final int PRESSED = 1;
	
	private GlyphLayout gLay;
	private Rectangle bounds;

	public Debutton(String n, float x, float y) {
		super(n, 1, 1, 1, x, y, 0, 2);
		rendering = false;
	}

	public void render(Level lvl) {
		LM.useDefaultCamera();
		LM.drawText(name, getCenterPos().x-gLay.width, getCenterPos().y+gLay.height, col, 2f);
		LM.useLevelCamera();
	}

	public void tick(Level lvl) {
		if(st == NORMAL){
			if(bounds.contains(InputManager.getWorldMouse(LM.defaultCam)) && InputManager.pressedLMB) press(lvl);
		}
	}
	
	public void press(Level lvl){
		InputManager.pressedLMB = false;//Stops this from affecting the Coin.
		InputManager.downLMB = false;
		execute();
		col = Color.valueOf("EDDEBE");
		st = PRESSED;
		
		LM.addTimer(new RTimer(){                                       
			public void act(){
			st = NORMAL; 
			col = Color.valueOf("FF5542");
			}},
			0.05f);
	}
	
	public abstract void execute();

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		
	}

	public void init(Level lvl) {
		col = Color.valueOf("FF5542");
		gLay = new GlyphLayout();
		
		gLay.setText(LM.drawText, name);
		dim.x = gLay.width*2f;
		dim.y = (gLay.height*2f+30);
		
		bounds = new Rectangle(pos.x, pos.y, dim.x, dim.y);
	}

	public boolean doneLoad(Level lvl) {
		return true;
	}

	public void dispose() {
		
	}

	public String getDebug() {
		return null;
	}

}
