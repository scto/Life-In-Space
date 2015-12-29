package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.HardCollider;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
//A raindrop. Should make a separate, type-1 collider one that can spawn a splash.
//Draws with a motion-blur shader from Main.
public class Drop extends Entity {
	private Texture draw;
	
	public Drop(String n,  float x, float y, float z) {
		super(n, 25, 25, 1, x, y, z, 1);
	}

	public void render(Level lvl) {
		LM.batch.setShader(Main.blurShader);
		Main.blurShader.setUniformf("diff", -v.x*LM.endTime, v.y*LM.endTime);
		LM.batch.draw(draw, pos.x, pos.y+pos.z, 64, 64);
		LM.batch.setShader(null);
	}

	public void tick(Level lvl) {
		v.y += Tween.tween(v.y, 0f, 15f);
	}

	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		if(normal.y == -1 && b instanceof HardCollider) remove();
	}

	public void init(Level lvl) {
		v.y = -15200;
		v.x = -100;
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/drop.png")){
			draw = LM.loader.get("img/drop.png", Texture.class);
			
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
