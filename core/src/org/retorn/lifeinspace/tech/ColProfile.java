package org.retorn.lifeinspace.tech;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ColProfile {
	private static ShaderProgram colShader;
	public Color[] cols;
	public float[] s;
	
	public ColProfile(String[] st, float[] f){
		//1. Initialize cols with the right size
		int len = st.length;
		cols = new Color[len];

		//2. Fill cols with colours based on st's hex-strings.
		for(int q = 0; q < len; q++){
			cols[q] = Color.valueOf(st[q]);
		}
		
		//3. Set s-array.
		s = f;
	}
	
	public static void useShader(ColProfile cp, float a, Color c, float ca, float b){
		LM.batch.setShader(colShader);
		colShader.setUniformf(colShader.getUniformLocation("alpha"), a);
		colShader.setUniformf(colShader.getUniformLocation("bright"), b);
		colShader.setUniformf(colShader.getUniformLocation("overlayAlpha"), ca);
		colShader.setUniformf(colShader.getUniformLocation("tint"), c);
		//Colours
		colShader.setUniformf(colShader.getUniformLocation("col0"), cp.cols[0]);
		colShader.setUniformf(colShader.getUniformLocation("col1"), cp.cols[1]);
		colShader.setUniformf(colShader.getUniformLocation("col2"), cp.cols[2]);
		colShader.setUniformf(colShader.getUniformLocation("col3"), cp.cols[3]);
		colShader.setUniformf(colShader.getUniformLocation("col4"), cp.cols[4]);
		colShader.setUniformf(colShader.getUniformLocation("colf"), cp.cols[5]);
		//Segments
		colShader.setUniformf(colShader.getUniformLocation("s1"), cp.s[0]);
		colShader.setUniformf(colShader.getUniformLocation("s2"), cp.s[1]);
		colShader.setUniformf(colShader.getUniformLocation("s3"), cp.s[2]);
		colShader.setUniformf(colShader.getUniformLocation("s4"), cp.s[3]);
	}
	
	public void tween(ColProfile cp, float alpha){
		//1. Loops through & tweens. Stops if you run out of one of them.
		for(int i = 0; i < Math.min(cols.length, cp.cols.length); i++){
			cols[i].lerp(cp.cols[i], alpha);
		}
		
		//2. Same with s.
		for(int i = 0; i < Math.min(s.length, cp.s.length); i++){
			s[i] += Tween.tween(s[i], cp.s[i], alpha);
		}
	}
	
	//TWEEN WITH AN ARRAY OF ALPHAS FOR DIFFERENT COL-SPEEDS.
	public void tween(ColProfile cp, float[] alpha){
		//1. Loops through & tweens. Stops if you run out of one of them.
		for(int i = 0; i < Math.min(cols.length, cp.cols.length); i++){
			cols[i].lerp(cp.cols[i], alpha[i]);
		}
		
		//2. Same with s.
		for(int i = 0; i < Math.min(s.length, cp.s.length); i++){
			s[i] += Tween.tween(s[i], cp.s[i], alpha[i]);
		}
	}
	
	public void set(ColProfile cp){
		//1. Loops through & sets. Stops if you run out of one of them.
		for(int i = 0; i < Math.min(cols.length, cp.cols.length); i++){
			cols[i].set(cp.cols[i]);
		}
		
		//2. Same with s.
		for(int i = 0; i < Math.min(s.length, cp.s.length); i++){
			s[i] = cp.s[i];
		}
		
		
	}
	
	//Only ever called once on startup.
	public static void setUp(){
		//1. Set up colShader.
		colShader = new ShaderProgram(Gdx.files.internal("shaders/bg.vsh"), Gdx.files.internal("shaders/bg.fsh"));
		outPrint("COL SHADER COMPILED: "+colShader.isCompiled() +colShader.getLog());
		colShader.pedantic = false;
		
		//2. Set up the basic & static greyscale shader.
	}
	
	public static ColProfile getDefault(){
		return new ColProfile(new String[]{
				"000000",
				"000000",
				"000000",
				"000000",
				"000000",
				"000000"
				},
				new float[]{
				0f, 0f, 0f, 0f
		});
	}
	
	public static void outPrint(String s){
		System.out.println("ColProfile: "+s);
	}

}
