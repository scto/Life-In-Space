package org.retorn.lifeinspace.level;

import org.retorn.lifeinspace.engine.Camera;
import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Sol;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.entity.Coin;
import org.retorn.lifeinspace.entity.Debutton;
import org.retorn.lifeinspace.tech.ColProfile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class Main extends Level{
	public static float inc;
	public static float oAlpha = 1f;
	public static float bright = 1f;
	public static Vector2 resFac;
	public static ShaderProgram blurShader, tintShader;
	private static FrameBuffer fbo, fbo2;
	private static Color tint;
	private static ColProfile cp, cpDef;

	public Main() {
		super(Color.BLACK, "main", 60);
	}

	public void init() {
		resFac = new Vector2();
		setUpShaders();
		
		addDebuttons();
		addEnt(new Coin("coin", 10, 10, 0));
		addEnt(new Sol("floor1", 1000, 1000, 300, 0, -1000, -150, Color.valueOf("DFE3C8"), 0.1f, "img/stonetex.png", 1f));
		addEnt(new Sol("floor2", 1000, 1000, 300, 1600, -1000, -150, Color.valueOf("291B21"), 0.1f, "img/stonetex.png", 1f));
		addEnt(new Sol("floor3", 1000, 1000, 300, 3200, -1000, -150, Color.valueOf("DFE3C8"), 0.1f, "img/stonetex.png", 1f));
		
		camList.put("spaceCam", new Camera(1280, 720));
		camList.get("spaceCam").setPos(2100, 500);
		camList.get("spaceCam").zoom = 3.5f;
		
		fbo = new FrameBuffer(Format.RGB888, LM.WIDTH, LM.HEIGHT, false);
		fbo2 = new FrameBuffer(Format.RGB888, LM.WIDTH, LM.HEIGHT, false);
		
		setUpColors();
	}

	public void postLoad() {
		
	}
	
	public void superRender(){
		startFBO();
		
		LM.useDefaultCamera();
		//LM.drawText("Coinship", 100, 420, Color.WHITE, 5f);
		LM.useLevelCamera();
	}

	public void render() {
		renderDebuttons();
		fbo.end();

		ColProfile.useShader(cpDef, 0.2f, tint, oAlpha, bright);
		LM.useDefaultCamera();
		LM.batch.draw(fbo.getColorBufferTexture(), 0, LM.HEIGHT, LM.WIDTH, -LM.HEIGHT);
		LM.useLevelCamera();
		LM.batch.setShader(null);
		
		tint.lerp(Color.valueOf("C46481"), 0.1f);
		bright += Tween.tween(bright, 1f, 1f);
		oAlpha = 1f;
	}

	public void tick() {
		inc += LM.endTime;
		tickResFacs();
		
		camList.get("default").setTarget(entity("coin"), 3f);
		if(camList.get("default").tPos.y < -100*camList.get("default").zoom) camList.get("default").tPos.y = -100*camList.get("default").zoom;
		else camList.get("default").tPos.y += 120*camList.get("default").zoom+(float)Math.cos(inc*0.1f)*10;
		camList.get("default").setZoomTarget(2.0f, 3f);
		
		BG.tick(this);
		bg.set(BG.col);
		
		if(InputManager.pressedR){
			entity("coin").pos.x = 100;
			getCam().pos.x = entity("coin").pos.x;
			bright = 1.5f;
		}
	}
	
	public void renderDebuttons(){
		for(Entity e : eList.values()){
			if(e instanceof Debutton) e.render(this);
		}
	}
	
	private void addDebuttons(){
		addEnt(new Debutton("Go Groundful", 10, 200, 1.5f, false){
			public void execute(){
			entity("coin", Coin.class).st = 1;
			}});
		addEnt(new Debutton("Go Gravless", 10, 150, 1.5f, false){
			public void execute(){
			entity("coin", Coin.class).st = 2;
			}});
		addEnt(new Debutton("resetY", 10, 100, 1.5f, false){
			public void execute(){
			entity("coin").pos.y = 700;
			}});
		addEnt(new Debutton("resetX", 10, 50, 1.5f, false){
			public void execute(){
			entity("coin").pos.x = 0;
			}});
		addEnt(new Debutton("View Debuttons", 10, 0, 1.5f, true){
			public void execute(){
			for(Entity e: eList.values()){
				if(e instanceof Debutton && e != this)
					 ((Debutton)e).on = !((Debutton)e).on;
			}
			}});
	
	}
	
	private void tickResFacs(){
		resFac.set( Gdx.graphics.getWidth()/(float)LM.WIDTH_OG,
						      Gdx.graphics.getHeight()/(float)LM.HEIGHT_OG);
	}
	
	private void startFBO(){
		fbo.begin();
		
		Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	private void setUpShaders(){
		blurShader = new ShaderProgram(Gdx.files.internal("shaders/mBlur.vsh"), Gdx.files.internal("shaders/mBlur.fsh"));
		outPrint("Blur Shader compiled: " +blurShader.isCompiled() +"\n"+blurShader.getLog());
		
		tintShader = new ShaderProgram(Gdx.files.internal("shaders/tint.vsh"), Gdx.files.internal("shaders/tint.fsh"));
		outPrint("Tint Shader compiled: " +tintShader.isCompiled() +"\n"+tintShader.getLog());
		tintShader.setUniformf("alpha", 1f);
	}
	
	private void setUpColors(){
		tint = Color.valueOf("400623");
		cp = ColProfile.getDefault();
		cpDef = new ColProfile(new String[]{
				"1f1d18",
				"360D1C",
				"C83632",
				"F4AF9C",
				"FFFFFF",
				"FFFFFF"
				},
				new float[]{
				0.17f, 0.59f, 0.91f, 1.0f
		});
	}

	public void enter() {
		
	}

	public void exit() {
		
	}

	public String getDebug() {
		return "bright: "+bright;
	}

	public void pause() {
		
	}

	public void resume() {
		
	}

}
