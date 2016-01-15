package org.retorn.lifeinspace.level;

import org.retorn.lifeinspace.engine.Level;

import com.badlogic.gdx.graphics.Color;

//Renders background
public class BG {
	public static Color col, tCol;
	
	public static void setUp(){
		col = Color.valueOf("A3C9C3");
		//col = Color.valueOf("FFF2C7");
		//col = Color.valueOf("000000");
		tCol = col.cpy();
	}
	
	public static void tick(Level lvl){
		col.lerp(tCol, 0.05f);
	}
	
	public void render(){
		
	}
	
	public static void setTarg(String s){
		tCol = Color.valueOf(s);
	}

}
