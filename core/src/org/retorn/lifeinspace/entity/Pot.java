package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Shadow;
import org.retorn.lifeinspace.engine.ShadowProvider;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.level.Main;
import org.retorn.lifeinspace.tech.ColProfile;
import org.retorn.lifeinspace.tech.PotProfile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class Pot extends Pickup {
	private TextureRegion front, back, glowOrange;
	private Texture effMask, eff;
	public static ShaderProgram plantShader;
	private Shadow shadow;
	
	public ColProfile cp;
	public float bright = 1f;
	public float effFac = 1f;
	
	public float glowAlpha;
	public float glowInc;
	
	private int gSt;
	
	private final int GLOWLESS = 0;
	private final int GLOWFUL = 1;
	
	private int st;
	
	private final int ONGROUND = 0;
	private final int INAIR = 1;
	
	public boolean done;
	
	public Plunt plunt;
	
	public Pot(String n, float x, float y, float z) {
		super(n, 80, 75, 30, x, y, z, 1);
	}

	public void render(Level lvl) {
		ColProfile.useShader(cp, 1f, Color.BLACK, 0f, bright);
		LM.batch.draw(
				back, 
				pos.x-25, pos.y+pos.z-10, 
				128, 128);
		
		LM.batch.setShader(null);
		
		 drawGlow(0, lvl);
		
		if(plunt != null) plunt.render(lvl);
		
		if(plunt != null) drawEffect();
		
		ColProfile.useShader(cp, 1f, Color.BLACK, 0f, bright);
		LM.batch.draw(
				front, 
				pos.x-25, pos.y+pos.z-10, 
				128, 128);
		
		LM.batch.setShader(null);
		
		LM.useLevelCamera();
	}
	
	public void drawGlow(int id, Level lvl){
		manageGlowAlpha(lvl);
		
		LM.batch.setShader(LM.brightShader);
		LM.brightShader.setUniformf("alpha", glowAlpha*0.8f+LM.dice.nextFloat()*0.05f-0.025f + (plunt != null ? plunt.bright-1f : 0f));
		LM.brightShader.setUniformf("bright", 1f+LM.dice.nextFloat()*0.2f + (plunt != null ? plunt.bright-1f : 0f));
		
		if(id == 0)
			LM.batch.draw(
					glowOrange, 
					pos.x-25, pos.y+pos.z-10, 
					128, 128);
		
		
		LM.batch.setShader(null);
	}
	
	private void manageGlowAlpha(Level lvl){
		if((lvl.entity("coin", Coin.class).potInter == this && lvl.entity("coin", Coin.class).heldEnt instanceof SeedAsp) || plunt != null){
		//if(plunt != null){
			if(gSt == GLOWLESS){
				glowInc = 0f;
				Main.clickSound.play(0.02f, 10f, 0.5f);
			}
			gSt = GLOWFUL;
			if(glowInc < 0.15f) glowInc += LM.endTime;
			glowAlpha = Tween.cubicTween(glowInc, 0f, plunt != null ? 1f : 0.4f, 0.15f);
		}
		
		else{
			if(gSt == GLOWFUL) glowInc = 0f;
			gSt = GLOWLESS;
			if(glowInc < 0.5f) glowInc += LM.endTime;
			glowAlpha = Tween.cubicTween(glowInc, plunt != null ? 1f : 0.4f, -1f, plunt != null ? 0.5f : 0.2f);
		}
	}
	
	public void drawEffect(){
		effMask.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		LM.batch.setShader(plantShader);
		plantShader.setUniformi("u_mask", 1);
		plantShader.setUniformf("bright", 1f);
		plantShader.setUniformf("opacity", effFac < 0.02f ? effFac/0.02f : 1f);
		plantShader.setUniformf("sharp", 1f);
		plantShader.setUniformf("time", Main.inc*0.5f);
		plantShader.setUniformf("gFac", 1f-effFac);
		plantShader.setUniformf("wiggleFac", 15f);
		plantShader.setUniformf("wiggleMag", 0.03f);
		
		LM.batch.draw(
				eff, 
				pos.x-25+50*effFac, pos.y+pos.z+70-105*effFac, 
				128-100*effFac, 128);
		
		LM.batch.setShader(null);
	}

	public boolean interact(Entity e, Level lvl) {
		if(e instanceof SeedAsp){
			if(plunt != null){
				return false;
			}
			else{
				((SeedAsp)e).plant(lvl, this);
				lvl.entity("coin", Coin.class).cleanse(lvl);
				lvl.getCam().setShake(10, 7, 7, 180);
				Main.landSound.play(0.2f, 0.6f, 0.5f);
				Main.bright = 1.5f;
				return true;
			}
		}
		
		return false;
	}

	public void tick(Level lvl) {
		bright += Tween.tween(bright, 1f, 1f);
		
		if(plunt != null){
			effFac += Tween.tween(effFac, -0.005f, effFac > 0.7f ? 5f : effFac > 0.1f ? 0.3f : 2f);
			if(effFac < 0 || Tween.atPreciseTarget(effFac, 0f)) effFac = 0f;
		}
		
		weight = 70;
		if(st == ONGROUND){
			if(!onGround) st = INAIR;
			v.x += Tween.tween(v.x, 0f, 5f);
		}
		
		if(st == INAIR){
			if(onGround) land();
		}
		managePSt(lvl);
		provideShadow(lvl);
		
		if(pos.y < -10000){
			remove();
			if(plunt != null) plunt.remove();
		}
	}
	
	private void land(){
		st = ONGROUND;
		Main.landSound.play(0.2f, 2.3f+LM.dice.nextFloat()*0.2f, 0.5f);
	}
	
	//Called by plunts when they are planted in this.
	public void plant(Plunt p){
		plunt = p;
		bright = 1.1f;
	}
	
	private void provideShadow(Level lvl){
		shadow.setSize(202, 88);
		shadow.setPosition(pos.x+v.x*LM.endTime-62, pos.z+v.z*LM.endTime-30);
		lvl.addEnt(new ShadowProvider(name+"_SP", dim.x+100, dim.z+100, pos.x-50+v.x*LM.endTime, pos.y+dim.y, pos.z-50+v.z*LM.endTime, shadow));
	}
	
	public void water(){
		if(plunt != null) plunt.water();
	}
	
	public void drop(Level lvl){
		super.drop(lvl);
		v.y = -1000;
	}
	
	public void init(Level lvl) {
		if(cp == null){
			int id = LM.dice.nextInt(4);
			if(id == 0) 
				cp = new ColProfile(
					new String[]{"1f1d18", "72262f", "d45945", "e79852", "ffd060", "fff5d8"},
					new float[]{0.18f, 0.43f, 0.55f, 0.7f});
			
			if(id == 1) 
				cp = new ColProfile(
					new String[]{"1f1d18", "4d3159", "528c7b", "9fbf91", "ecfeb6", "fff4d6"},
					new float[]{0.30f, 0.51f, 0.7f, 0.91f});
			
			if(id == 2) 
				cp = new ColProfile(
					new String[]{"1f1d18", "2b161e", "702337", "953434", "a97152", "fff4d6"},
					new float[]{0.20f, 0.35f, 0.51f, 0.70f});
			
			if(id == 3) 
				cp = new ColProfile(
					new String[]{"1f1d18", "362947", "78846b", "a6bf59", "ebd485", "fff4d6"},
					new float[]{0.21f, 0.31f, 0.43f, 0.63f});
			
			for(Color c : cp.cols){
				c.r += 0.2f*LM.dice.nextFloat()-0.1f;
				c.g += 0.2f*LM.dice.nextFloat()-0.1f;
				c.b+= 0.2f*LM.dice.nextFloat()-0.1f;
			}
		}
		
		weight = 120;
	}

	public boolean doneLoad(Level lvl) {
		if( LM.loader.isLoaded("img/pot_back.png")
			&& LM.loader.isLoaded("img/pot_front.png")
			&& LM.loader.isLoaded("img/plantShad.png")
			&& LM.loader.isLoaded("img/pot_glow_back.png")
			&& LM.loader.isLoaded("img/pot_eff_mask.png")
			&& LM.loader.isLoaded("img/pot_eff.png")){
				
				back = new TextureRegion(LM.loader.get("img/pot_back.png", Texture.class));
				front = new TextureRegion(LM.loader.get("img/pot_front.png", Texture.class));
				
				glowOrange = new TextureRegion(LM.loader.get("img/pot_glow_back.png", Texture.class));
				
				shadow = new Shadow(LM.loader.get("img/plantShad.png", Texture.class), 0f, 0f);
				
				eff = LM.loader.get("img/pot_eff.png", Texture.class);
				effMask = LM.loader.get("img/pot_eff_mask.png", Texture.class);
				
				onGround = true;
				
				return true;
		}
		return false;
	}
	
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		super.collide(b, collisionTime, normal, lvl);
		
		//Overlap resolution
		/*if(b instanceof Pot &&
				getRect(lvl.getCam()).overlaps(b.getRect(lvl.getCam()))
		  ){
			v.x += -(1f-(getCenterPos().x-b.getCenterPos().x)/dim.x)*10f;
		}*/
	}

	public void dispose() {
		
	}

	public String getDebug() {
		return "gSt: "+gSt
				+"\nPlunt Worth: "+(plunt != null ? plunt.worth : "NO PLUNT");
	}

}
