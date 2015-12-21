package org.retorn.lifeinspace.level;

import com.badlogic.gdx.graphics.Color;

//Renders background
public class BG {
	public static Color col, tCol;
	
	public static void setUp(){
		col = Color.valueOf("FFF6E3");
		tCol = col.cpy();
	}
	
	public static void tick(){
		col.lerp(tCol, 0.05f);
	}
	
	public void render(){
		
	}

}
