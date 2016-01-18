package org.retorn.lifeinspace.level;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.tech.ColProfile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

//Renders background
public class BG {
	public static Color col, tCol;
	private static Texture draw;
	private static Texture tex, tex2, stex;
	private static ShaderProgram bgShader;
	private static ColProfile cp, cpTarg, cpBlue, cpSpace, cpRedSpace;
	
	public static void setUp(){
		col = Color.valueOf("A3C9C3");
		//col = Color.valueOf("FFF2C7");
		//col = Color.valueOf("000000");
		tCol = col.cpy();
		
		Pixmap p = new Pixmap(2,2, Format.RGB888);
		p.setColor(Color.valueOf("AAAAAA"));
		p.fill();
		
		draw = new Texture(p);
		
		LM.loadTexture("img/ctex.png");
		LM.loadTexture("img/ctex2.png");
		LM.loadTexture("img/stex.png");
		
		bgShader = new ShaderProgram(Gdx.files.internal("shaders/bgWig.vsh"), Gdx.files.internal("shaders/bgWig.fsh"));
		System.out.println("BG SHADER COMPILED: "+bgShader.isCompiled() +bgShader.getLog());
		bgShader.pedantic = false;
	}
	
	public static void doneLoad(){
		tex = LM.loader.get("img/ctex.png", Texture.class);
		tex2 = LM.loader.get("img/ctex2.png", Texture.class);
		tex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		stex = LM.loader.get("img/stex.png", Texture.class);
		stex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		cpBlue = new ColProfile(
				new String[]{"436c89", "436c89", "8dafa7", "dfd8b9", "fff9de", "ece8e8"},
				new float[]{0.0f, 0.52f, 0.69f, 0.87f});
		
		cpSpace = new ColProfile(
				new String[]{"000000", "1a0008", "191627", "ae7949", "ffe5b4", "ffe5b4"},
				new float[]{0.19f, 0.38f, 0.61f, 0.77f});
		
		cpRedSpace = new ColProfile(
				new String[]{"000000", "1a0008", "191627", "ae4949", "ee987a", "ffe5b4"},
				new float[]{0.19f, 0.38f, 0.62f, 0.86f});
		
		cp = ColProfile.getDefault();
		cp.set(cpSpace);
		
		cpTarg = ColProfile.getDefault();
		cpTarg.set(cpSpace);
	}
	
	
	public static void render(Level lvl){
		if(!Main.inShip)tex.bind(1);
		else stex.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		LM.batch.setShader(bgShader);
		bgShader.setUniformf("iResolution", LM.WIDTH, LM.HEIGHT);
		bgShader.setUniformf("iGlobalTime", Main.inShip ? Main.inc*0.001f + lvl.getCam().pos.x*0.00001f :  Main.inc*0.01f);
		bgShader.setUniformf("intensity",Main.inShip ? 300f : 2f);
		bgShader.setUniformf("cDis", lvl.getCam().pos.x*(Main.inShip ? 0.00001f : 0.00002f)+Main.inc*0.001f, -lvl.getCam().pos.y*0.00002f-0.6f);
		float scale = LM.HEIGHT/(float)tex.getHeight();
		bgShader.setUniformf("u_size", tex.getWidth()*scale, tex.getHeight()*scale);
		bgShader.setUniformi("u_texture1", 1);
		setBGColours();
		
		LM.useDefaultCamera();
		LM.batch.draw(draw, 0, 0, LM.WIDTH, LM.HEIGHT);
		LM.useLevelCamera();
		
		LM.batch.setShader(null);
		LM.useDefaultCamera();
		LM.batch.setShader(LM.brightShader);
		LM.brightShader.setUniformf("bright", 1);
		LM.brightShader.setUniformf("alpha", Main.inShip ? 0.1f :0.3f);
		LM.batch.draw(tex2, 0, 0, LM.WIDTH, LM.HEIGHT);
		LM.useLevelCamera();
	}
	
	private static void setBGColours(){
		bgShader.setUniformf("alpha", 1f);
		bgShader.setUniformf("col0", cp.cols[0]);
		bgShader.setUniformf("col1", cp.cols[1]);
		bgShader.setUniformf("col2", cp.cols[2]);
		bgShader.setUniformf("col3", cp.cols[3]);
		bgShader.setUniformf("col4", cp.cols[4]);
		bgShader.setUniformf("colf", cp.cols[5]);
		bgShader.setUniformf("s1", cp.s[0]);
		bgShader.setUniformf("s2", cp.s[1]);
		bgShader.setUniformf("s3", cp.s[2]);
		bgShader.setUniformf("s4", cp.s[3]);
	}
	
	public static void tick(Level lvl){
		if(!Main.inShip) cpTarg = cpBlue;
		else cpTarg = cpSpace;
		
		cp.tween(cpTarg, 0.025f);
		col.lerp(tCol, 0.05f);
	}

	
	public static void setTarg(String s){
		tCol = Color.valueOf(s);
	}

}
