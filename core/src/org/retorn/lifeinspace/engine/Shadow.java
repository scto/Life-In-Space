package org.retorn.lifeinspace.engine;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

//A shadow provided to Shadowables by ShadowProviders.
//fX/fY are displacement factors.
//When a shadowable draws a shadow, it will typically draw at:
//ent.pos.x+fX*(pY-(shadowable.pos.y+shadowable.dim.y)), ent.pos.z+shadowable.pos.y+fY*(pY-(shadowable.pos.y+shadowable.dim.y))

public class Shadow extends Sprite {
	public float fX, fY;//Factors
	public float pY;//PosY of the Caster. Gotta be set by ShadowProvider.
	
	public Shadow(Texture t, float x, float y){
		super(t);
		fX = x;
		fY = y;
	}
	
	public Shadow(Sprite t, float x, float y){
		super(t);
		fX = x;
		fY = y;
	}

}
