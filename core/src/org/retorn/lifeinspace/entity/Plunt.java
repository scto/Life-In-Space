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
import com.badlogic.gdx.math.Vector3;

public class Plunt extends WeakCollider  implements DisEnt{
	private String path;
	private TextureRegion plant;
	private Texture mask;
	public static ShaderProgram plantShader;
	private float opacity = 1f;
	private float bright = 1f;
	private float gFac = 0f;
	public int st;

	public Plunt(String n, float w, float h, float d, float x, float y, float z, String p) {
		super(n, w, h, d, x, y, z, 1);
		dis.set(x, y, z);
		path = p;
		
		rendering = false;
	}

	public void spawn(Vector3 iPos) {
		setCenterPos(iPos);
		setCenterTarget(iPos.x + dis.x, iPos.y + dis.y, iPos.z + dis.z, 10f);
	}

	public void render(Level lvl) {
		mask.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		LM.batch.setShader(plantShader);
		plantShader.setUniformi("u_mask", 1);
		plantShader.setUniformf("bright", bright);
		plantShader.setUniformf("opacity", opacity);
		plantShader.setUniformf("sharp", 6f);
		plantShader.setUniformf("time", Main.inc*0.5f);
		plantShader.setUniformf("gFac", 1f-gFac);
		plantShader.setUniformf("wiggleFac", 5f);
		plantShader.setUniformf("wiggleMag", 0.03f);
		gFac += Tween.tween(gFac, 1f, 0.1f);
		
		LM.batch.draw(plant, 
				getCenterPos().x-plant.getRegionWidth()/2f +1,
				getCenterPos().y+getCenterPos().z-370,
				plant.getRegionWidth()/2f,
				170,
				plant.getRegionWidth(),
				plant.getRegionHeight(),
				0.5f+gFac*0.2f,
				0.5f+gFac*0.05f,
				0f
				);
		
		LM.batch.setShader(null);
	}

	public void tick(Level lvl) {
		if(st == IN){
			v.setZero();
			cType = 2;
		}
		
		if(st == OUT){
			cType = 1;
		}
	}

	public void init(Level lvl) {
		LM.loadTexture(path);
		LM.loadTexture("img/plant1Mask.png");
	}

	public boolean doneLoad(Level lvl) {
		if(		LM.loader.isLoaded(path)
				&& LM.loader.isLoaded("img/pot_back.png")
				&& LM.loader.isLoaded("img/pot_front.png")
				&& LM.loader.isLoaded("img/plantShad.png")
				&& LM.loader.isLoaded("img/plant1Mask.png")){
			
			plant = new TextureRegion(LM.loader.get(path, Texture.class));
			mask = LM.loader.get("img/plant1Mask.png", Texture.class);
			
			return true;
		}
		outPrint("fuck");
		return false;
	}

	public void dispose() {
		
	}

	public String getDebug() {
		return null;
	}


}
