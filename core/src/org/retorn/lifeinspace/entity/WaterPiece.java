package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.HardCollider;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Shadow;
import org.retorn.lifeinspace.engine.ShadowProvider;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class WaterPiece extends Entity {
	private Shadow shadow;
	private TextureRegion draw;
	
	private float bright = 4f;
	private float rot;
	private float inc;
	
	private int st;
	
	private final int INMOTION = 0;
	private final int FALLING = 1;
	private final int FLOAT = 2;

	public WaterPiece(String n, float x, float y, float z, float vx, float vy, int s) {
		super(n, 60, 70, 50, x, y, z, 1);
		v.x = vx;
		v.y = vy;
		st = s;
	}

	public void render(Level lvl) {
		LM.batch.setShader(LM.brightShader);
		LM.brightShader.setUniformf("bright", bright);
		LM.brightShader.setUniformf("alpha", 1f);
		
		LM.batch.draw(
				draw, 
				pos.x - 34, 
				pos.y+pos.z-5,
				64, 25f,
				128, 128,
				1f, 1f,
				rot-180);
		
		LM.batch.setShader(null);
	}

	public void tick(Level lvl) {
		inc += LM.endTime;
		bright += Tween.tween(bright, 1f, 5f);
		
		if(st == INMOTION){
			v.y -= 10000*LM.endTime;
			//v.x += Tween.tween(v.x, 0f, 1f);
			rot += Tween.tween(rot, (float)Math.toDegrees(Math.atan2(-v.x, v.y)), 10f);
			
			if(v.y < 0) st = FALLING;
		}
		
		if(st == FALLING){
			v.y -= 10000*LM.endTime;
			//v.x += Tween.tween(v.x, 0f, 1f);
			rot += Tween.tween(rot, (float)Math.toDegrees(Math.atan2(-v.x, v.y)), 10f);
			if(inc > 3f) remove();
		}
		
		if(st == FLOAT){
			v.y = -300;
			v.x += Tween.tween(v.x, lvl.entity("coin").v.x*0.9f, 1f);
			rot += Tween.tween(rot, (float)Math.toDegrees(Math.atan2(-v.x, v.y)), 10f);
			if(getCenterPos().dst(lvl.entity("coin").getCenterPos()) > 4000) remove();
		}
		
		provideShadow(lvl);
	}
	
	private void provideShadow(Level lvl){
		shadow.setSize(128, 128);
		shadow.setPosition(pos.x+v.x*LM.endTime-40, pos.z+v.z*LM.endTime-80);
		lvl.addEnt(new ShadowProvider(name+"_SP", dim.x+100, dim.z+100, pos.x-50+v.x*LM.endTime, pos.y+dim.y, pos.z-50+v.z*LM.endTime, shadow));
	}
	
	private void die(Level lvl, float y, int id){
		lvl.addEnt(new Smoke(name+"smoke", getCenterPos().x-20 + v.x*LM.delta, y, pos.z, v.x*0.01f));
		remove();
		
		if(id == 0)Main.breakSound.play(0.1f, 3f-LM.dice.nextFloat()*0.4f, 0.5f);
	}
	
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		if(!normal.isZero() && b instanceof HardCollider) die(lvl, pos.y+v.y*LM.endTime*collisionTime, 0);
		
		if(!normal.isZero() && (st == FALLING || st == FLOAT) && b instanceof Coin){
			die(lvl, pos.y+v.y*LM.endTime*collisionTime, 1);
			((Coin)b).gainWP();
		}
		
		if(!normal.isZero() && (st == FALLING || st == FLOAT) && b instanceof Pot){
			die(lvl, pos.y+v.y*LM.endTime*collisionTime, 1);
			((Pot)b).water();
		}
	}

	public void init(Level lvl) {
		LM.loadTexture("img/waterpiece.png");
		LM.loadTexture("img/waterpiece_shad.png");
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/waterpiece.png")
			&& LM.loader.isLoaded("img/waterpiece_shad.png")){
			
			draw = new TextureRegion(LM.loader.get("img/waterpiece.png", Texture.class));
			shadow = new Shadow(LM.loader.get("img/waterpiece_shad.png", Texture.class), 0f, 0f);

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
