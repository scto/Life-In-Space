package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.engine.WeakCollider;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Plunt extends WeakCollider {
	private TextureRegion plant;
	private Texture mask;
	public static ShaderProgram plantShader;
	private Pot pot;
	private float opacity = 1f;
	private float bright = 1f;
	private float gFac = 0f;
	private float gFacT = 1f;
	private float incDis;//Displacement on inc
	public int st;

	public Plunt(String n, Pot po) {
		super(n, 60, 400, 40, 0, 0, 0, 2);
		pot = po;
		
		rendering = false;
	}

	public void render(Level lvl) {
		mask.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		LM.batch.setShader(plantShader);
		plantShader.setUniformi("u_mask", 1);
		plantShader.setUniformf("bright", bright);
		plantShader.setUniformf("opacity", opacity);
		plantShader.setUniformf("sharp", 6f);
		plantShader.setUniformf("time", Main.inc*0.5f+incDis);
		plantShader.setUniformf("gFac", 1f-gFac);
		plantShader.setUniformf("wiggleFac", 5f);
		plantShader.setUniformf("wiggleMag", 0.03f);
		gFac += Tween.tween(gFac, gFacT, 0.1f);
		gFacT = 0.3f;
		
		LM.batch.draw(plant, 
				getCenterPos().x-plant.getRegionWidth()/2f +1,
				getCenterPos().y+getCenterPos().z-375,
				plant.getRegionWidth()/2f,
				180,
				plant.getRegionWidth(),
				plant.getRegionHeight(),
				0.5f+gFac*0.1f,
				0.5f+gFac*0.05f,
				0f
				);
		
		LM.batch.setShader(null);
	}

	public void tick(Level lvl) {
		incDis += Math.abs(v.x*0.00008f);
	}
	
	public void plant(Pot p){
		setCenterPos(p.getCenterPos());
		pos.y = p.pos.y + 18;
		pos.x -= 7;
		setParent(p);
		p.plant(this);
	}

	public void init(Level lvl) {
		LM.loadTexture("img/plant1.png");
		LM.loadTexture("img/plant1Mask.png");
	}

	public boolean doneLoad(Level lvl) {
		if(		LM.loader.isLoaded("img/plant1.png")
				&& LM.loader.isLoaded("img/pot_back.png")
				&& LM.loader.isLoaded("img/pot_front.png")
				&& LM.loader.isLoaded("img/plantShad.png")
				&& LM.loader.isLoaded("img/plant1Mask.png")){
			
			plant = new TextureRegion(LM.loader.get("img/plant1.png", Texture.class));
			mask = LM.loader.get("img/plant1Mask.png", Texture.class);
			
			incDis = LM.dice.nextFloat()*20;
			
			plant(pot);
			
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
