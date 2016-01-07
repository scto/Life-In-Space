package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Shadow;
import org.retorn.lifeinspace.engine.ShadowProvider;
import org.retorn.lifeinspace.engine.WeakCollider;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

public class Pot extends WeakCollider implements DisEnt {
	private TextureRegion front, back;
	private Shadow shadow;

	public Pot(String n, float x, float y, float z) {
		super(n, 80, 75, 30, x, y, z, 1);
	}

	public void spawn(Vector3 iPos) {
		
	}

	public void render(Level lvl) {
		LM.batch.draw(
				back, 
				pos.x-25, pos.y+pos.z-10, 
				128, 128);
		
		//RENDER PLANT
		lvl.entity("plunt1").render(lvl);
		
		LM.batch.draw(
				front, 
				pos.x-25, pos.y+pos.z-10, 
				128, 128);
	}

	public void tick(Level lvl) {
		applyGrav(lvl);
		provideShadow(lvl);
	}
	
	private void provideShadow(Level lvl){
		shadow.setSize(202, 88);
		shadow.setPosition(pos.x+v.x*LM.endTime-62, pos.z+v.z*LM.endTime-30);
		lvl.addEnt(new ShadowProvider(name+"_SP", dim.x+100, dim.z+100, pos.x-50+v.x*LM.endTime, pos.y+dim.y, pos.z-50+v.z*LM.endTime, shadow));
	}

	public void init(Level lvl) {
		weight = 120;
	}

	public boolean doneLoad(Level lvl) {
		if( LM.loader.isLoaded("img/pot_back.png")
			&& LM.loader.isLoaded("img/pot_front.png")
			&& LM.loader.isLoaded("img/plantShad.png")){
				
				back = new TextureRegion(LM.loader.get("img/pot_back.png", Texture.class));
				front = new TextureRegion(LM.loader.get("img/pot_front.png", Texture.class));
				
				shadow = new Shadow(LM.loader.get("img/plantShad.png", Texture.class), 0f, 0f);
				
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
