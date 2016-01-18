package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class Smoke extends Entity {
	public static ShaderProgram plantShader;
	private Texture effMask, eff;
	
	private float effFac = 1f;

	public Smoke(String n, float x, float y, float z, float vx) {
		super(n, 80, 75, 30, x, y, z, 2);
		v.x = vx;
	}

	public void render(Level lvl) {
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
				pos.x-40+10*effFac, pos.y+pos.z-35*effFac + 10 + 30*(1f-effFac), 
				158-20*effFac, 158);
		
		LM.batch.setShader(null);
	}

	public void tick(Level lvl) {
		effFac += Tween.tween(effFac, -0.005f, effFac > 0.4f ? 10f + LM.dice.nextFloat()*10f : 1f);
		if(effFac < 0 || Tween.atPreciseTarget(effFac, 0f)) effFac = 0f;
		
		if(effFac < 0.001f) remove();
		v.x += Tween.tween(v.x, 0f, 1f);
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		
	}

	public void init(Level lvl) {
		LM.loadTexture("img/smoke_eff.png");
		LM.loadTexture("img/smoke_eff_mask.png");
	}

	public boolean doneLoad(Level lvl) {
		if( LM.loader.isLoaded("img/smoke_eff_mask.png")
		&& LM.loader.isLoaded("img/smoke_eff.png")){
			
			eff = LM.loader.get("img/smoke_eff.png", Texture.class);
			effMask = LM.loader.get("img/smoke_eff_mask.png", Texture.class);
			
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
