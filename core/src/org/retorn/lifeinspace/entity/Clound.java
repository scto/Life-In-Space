package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Shadow;
import org.retorn.lifeinspace.engine.ShadowProvider;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class Clound extends Entity{
	private Texture mask, mask2;
	private TextureRegion draw;
	private Shadow shadow;
	public static ShaderProgram plantShader;
	CloundTrigger cT0, cT1;
	
	private float bright = 1f;
	private float gFac;
	
	private float incDis;
	
	private int st;
	
	private final int NORMAL = 0;
	private final int DEAD = 1;

	public Clound(String n,float x, float y, float z) {
		super(n, 250, 100, 50, x, y, z, 1);
	}

	public void render(Level lvl) {
		if(st == NORMAL) mask.bind(1);
		else mask2.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		LM.batch.setShader(plantShader);
		plantShader.setUniformi("u_mask", 1);
		plantShader.setUniformf("bright", bright);
		plantShader.setUniformf("opacity", 1f);
		plantShader.setUniformf("sharp", 0.5f);
		plantShader.setUniformf("time", Main.inc*0.025f + incDis);
		plantShader.setUniformf("gFac", 1f-gFac);
		plantShader.setUniformf("wiggleFac", 10f);
		plantShader.setUniformf("wiggleMag", 0.03f);
		
		LM.batch.draw(draw, 
				getCenterPos().x-draw.getRegionWidth()/2f + 8,
				getCenterPos().y+pos.z-135,
				draw.getRegionWidth()/2f,
				180,
				draw.getRegionWidth(),
				draw.getRegionHeight(),
				0.6f,
				0.6f,
				0f
				);
		
		LM.batch.setShader(null);
	}

	public void tick(Level lvl) {
		if(st == NORMAL) gFac += Tween.tween(gFac, (float)(Math.cos(Main.inc*0.5f + incDis)+1f)/2f*0.4f+0.4f, 10f);
		
		if(st == DEAD){
			gFac += Tween.tween(gFac, -0.005f, gFac > 0.7f ? 5f : gFac > 0.1f ? 0.3f : 2f);
			v.y += Tween.tween(v.y, 10f, 25f);
		}
		
		if(gFac < 0.01f) remove();
		
		provideShadow(lvl);
	}
	
	public void trigger(int type, Level lvl){
		Main.popSound.play(0.5f, type == 1 ? 0.5f : 1f, 0.5f);
		Main.jumpSound.play(0.2f, 2.5f, 0.5f);
		st = DEAD;
		cT0.remove();
		cT1.remove();
		gFac = 1f;
		v.y = 1000;
		
		if(type == 0){
			for(int i = 0; i < 3; i++){
			lvl.addEnt(new WaterPiece(
				"wp"+LM.dice.nextFloat(), 
				getCenterPos().x-30, 
				lvl.entity("coin").pos.y,
				0, 
				lvl.entity("coin").v.x*0.8f+200*LM.dice.nextFloat(), 4000 + 300*i, 0));
			}
		}
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		
	}
	
	private void provideShadow(Level lvl){
		shadow.setSize(307.2f-gFac*20 , 307.2f);
		shadow.setAlpha(st == NORMAL ? 1f : gFac*0.5f+0.5f);
		shadow.setPosition(pos.x+v.x*LM.endTime-20 + gFac*10, pos.z+v.z*LM.endTime-15 + (1f-gFac)*10);
		lvl.addEnt(new ShadowProvider(name+"_SP", dim.x+100, dim.z+100, pos.x-50+v.x*LM.endTime, pos.y+dim.y, pos.z-50+v.z*LM.endTime, shadow));
	}

	public void init(Level lvl) {
		LM.loadTexture("img/clound.png");
		LM.loadTexture("img/clound_mask.png");
		LM.loadTexture("img/clound_mask2.png");
		LM.loadTexture("img/clound_shad.png");
		
		cT0 = new  CloundTrigger(name+"cT0", pos.x+75, pos.y, pos.z+dim.z, 0, this);
		cT1 = new  CloundTrigger(name+"cT1", pos.x+120, pos.y+140, pos.z+dim.z, 1, this);
		
		lvl.addEnt(cT0);
		lvl.addEnt(cT1);
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/clound.png") 
			&& LM.loader.isLoaded("img/clound_mask.png")
			&& LM.loader.isLoaded("img/clound_mask2.png")
			&& LM.loader.isLoaded("img/clound_shad.png")){
			
			draw = new TextureRegion(LM.loader.get("img/clound.png", Texture.class));
			mask = LM.loader.get("img/clound_mask.png", Texture.class);
			mask2 = LM.loader.get("img/clound_mask2.png", Texture.class);
			
			shadow = new Shadow(LM.loader.get("img/clound_shad.png", Texture.class), 0f, 0f);
			
			incDis = LM.dice.nextFloat()*100;
			
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
