package org.retorn.lifeinspace.engine;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public abstract class Shadowable extends Entity{
	//An entity that can be rendered with a shadow/generate shadows to use based on sprites/coordinates that are fed to it.
	protected ArrayList<Shadow> shadows;
	private ArrayList<Vector2> coords;//Each coord matches up with a shadow.
	private static ShaderProgram shadowBlack;//Shader that (hopefully) removes the white parts of the fbo shadows.
	
	public Shadowable(String n, float w, float h, float d, float x, float y, float z, int collisionType) {
		super(n, w, h, d, x, y, z, collisionType);
		shadows = new ArrayList<Shadow>();
		coords = new ArrayList<Vector2>();
	}
	
	public void addShadow(Shadow s, Vector2 c){
		shadows.add(s);
		coords.add(c);
	}
	
	public void addShadow(Shadow s, float x, float y){
		shadows.add(s);
		coords.add(new Vector2(x, y));
	}
	
	public void clearShadows(){
		shadows.clear();
	}

}
