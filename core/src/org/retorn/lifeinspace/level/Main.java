package org.retorn.lifeinspace.level;

import java.util.ArrayList;
import java.util.Iterator;

import org.retorn.lifeinspace.engine.Camera;
import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Sol;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.entity.ButtonUp;
import org.retorn.lifeinspace.entity.Coin;
import org.retorn.lifeinspace.entity.Debutton;
import org.retorn.lifeinspace.entity.Plunt;
import org.retorn.lifeinspace.entity.Pot;
import org.retorn.lifeinspace.entity.SeedAsp;
import org.retorn.lifeinspace.tech.ColProfile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class Main extends Level{
	public static float inc;
	public static float blur = 1f;
	public static float blurT = 0f;
	public static float blurSpeed = 1f;
	public static float oAlpha = 1f;
	public static float oAlphaT = 0f;
	public static float bAlpha = 1f;
	public static float bAlphaT = 0.3f;
	public static float bright = 1f;
	public static Vector2 resFac;
	public static ShaderProgram blurShader, tintShader;
	public static ShaderProgram plantShader;
	private static ShaderProgram blurVShader, blurHShader;
	private static FrameBuffer fbo;
	private static Color tint;
	private static ColProfile cp, cpTarg, cpDef, cpDark;
	public static Sound jumpSound, landSound, clickSound, plantSound;
	
	public static ArrayList<Sol> bufferSols, activeSols;

	public Main() {
		super(Color.BLACK, "main", 60);
	}

	public void init() {
		resFac = new Vector2();
		setUpShaders();
		
		LM.loadSound("audio/coin_jump.ogg");
		LM.loadSound("audio/coin_land.ogg");
		LM.loadSound("audio/click.ogg");
		LM.loadSound("audio/plant.ogg");
		
		setUpSols();
		
		//addDebuttons();
		addEnt(new ButtonUp("ib", 10, 10));
		
		addEnt(new Sol("Ship Floor", 1000, 1000, 300, 0, 9000, -150, Color.valueOf("EDCDB7"), 0.1f, "img/stonetex.png", 1f));
		
		addEnt(new Pot("pot-2", 300, 10100, 80));
		addEnt(new Pot("pot-1", 400, 10100, 80));
		addEnt(new Pot("pot0", 500, 10100, 80));
		addEnt(new Pot("pot1", 600, 10100, 80));
		addEnt(new Pot("pot2", 700, 10100, 80));
		addEnt(new Pot("pot3", 800, 10100, 80));
		addEnt(new Pot("pot4", 900, 10100, 80));
		
		addEnt(new SeedAsp("seed0", 100, 10100, 80));
		addEnt(new SeedAsp("seed1", 50, 10100, 80));
		addEnt(new SeedAsp("seed2", 0, 10100, 80));
		
		addEnt(new Coin("coin", 0, 10000, -10));
		
		camList.put("spaceCam", new Camera(1280, 720));
		camList.get("spaceCam").setPos(2100, 500);
		camList.get("spaceCam").zoom = 3.5f;
		
		fbo = new FrameBuffer(Format.RGB888, LM.WIDTH, LM.HEIGHT, false);
		
		setUpColors();
	}

	public void postLoad() {
		BG.doneLoad();
		
		jumpSound = LM.loader.get("audio/coin_jump.ogg", Sound.class);
		landSound = LM.loader.get("audio/coin_land.ogg", Sound.class);
		clickSound = LM.loader.get("audio/click.ogg", Sound.class);
		plantSound = LM.loader.get("audio/plant.ogg", Sound.class);
		
		getCam().setPos(entity("coin"));
	}
	
	public void superRender(){
		startFBO();
		BG.render(this);
		LM.useDefaultCamera();
		//LM.drawText("Coinship", 100, 420, Color.WHITE, 5f);
		LM.useLevelCamera();
	}

	public void render() {
		//renderDebuttons();
		renderButtons();
		fbo.end();

		//Color-Correction
		//fbo2.begin();
		ColProfile.useShader(cp, bAlpha, tint, oAlpha, bright);
		LM.useDefaultCamera();
		LM.batch.draw(fbo.getColorBufferTexture(), 0, LM.HEIGHT, LM.WIDTH, -LM.HEIGHT);
		LM.batch.setShader(null);
		//fbo2.end();
		
		//Blur V
		/*fbo.begin();
		LM.batch.setShader(blurVShader);
		blurVShader.setUniformf(blurVShader.getUniformLocation("blur"), blur);
		LM.batch.draw(fbo2.getColorBufferTexture(), 0, LM.HEIGHT, LM.WIDTH, -LM.HEIGHT);
		LM.batch.setShader(null);
		fbo.end();
		
		//Blur H
		LM.batch.setShader(blurHShader);
		blurHShader.setUniformf(blurHShader.getUniformLocation("blur"), blur);
		LM.batch.draw(fbo.getColorBufferTexture(), 0, LM.HEIGHT, LM.WIDTH, -LM.HEIGHT);
		LM.batch.setShader(null);
		LM.useLevelCamera();*/
		
		tint.lerp(Color.valueOf("C46481"), 0.1f);
		bright += Tween.tween(bright, 1f, 2f);
		blur += Tween.tween(blur, blurT, blurSpeed);
		oAlpha += Tween.tween(oAlpha, oAlphaT, 1f);
		bAlpha += Tween.tween(bAlpha, bAlphaT, 1f);
		cp.tween(cpTarg, 0.1f);
		
		//LM.useDefaultCamera();
		//LM.drawText("fps: "+Gdx.graphics.getFramesPerSecond(), 0, 100);
		LM.useLevelCamera();
	}

	public void tick() {
		inc += LM.endTime;
		tickResFacs();
		
		camList.get("default").setTarget(entity("coin"), 3f);
		if(entity("coin").pos.y < Coin.standardHeight) camList.get("default").tPos.y = Coin.standardHeight*camList.get("default").zoom;
		else camList.get("default").tPos.y += 120*camList.get("default").zoom+(float)Math.cos(inc*0.1f)*10;
		camList.get("default").setZoomTarget(3f, 3f);
		
		BG.tick(this);
		bg.set(BG.col);
		
		manageSols();
		
		if(InputManager.pressedR){
			entity("coin").pos.x = 100;
			getCam().pos.x = entity("coin").getCenterPos().x;
			bright = 1.5f;
			addEnt(new SeedAsp("seed2"+LM.dice.nextFloat(), 10, 10100, 80));
		}
	}
	
	public void renderButtons(){
			entity("ib").render(this);
	}
	
	public void renderDebuttons(){
		for(Entity e : eList.values()){
			if(e instanceof Debutton) e.render(this);
		}
	}
	
	//ONLY DO THIS ALSO IF YOU'RE NOT IN THE SHIP
	private void manageSols(){
		//REMOVE
		Iterator i = activeSols.iterator();
		while(i.hasNext()){
			Sol s =(Sol) i.next();
			if((entity("coin").pos.x - (s.pos.x + s.dim.x) > 3000) || (s.pos.x - (entity("coin").pos.x + entity("coin").dim.x) > 3000)){
				eList.remove(s.name);
				bufferSols.add(s);
				i.remove();
			}
		}
		
		//GET EXTENTS
		Sol lowestRight = null;
		Sol highestLeft = null;
		
		for(Sol s: activeSols){
			if(lowestRight == null || s.pos.x < lowestRight.pos.x) lowestRight = s;
			if(highestLeft == null || s.pos.x + s.dim.x > highestLeft.pos.x + highestLeft.dim.x) highestLeft = s;
		}
		
		float WIDTH = 200;
		float GAP = 1000;

		//RIGHT
		if(entity("coin").getCenterPos().x - lowestRight.pos.x < 2000){
			float w = 1000 + LM.dice.nextFloat()*WIDTH;
			putSol(
					lowestRight.pos.x - w - (LM.dice.nextFloat()*GAP),
					lowestRight.pos.y - 250 + LM.dice.nextFloat()*500,
					w
					);
		}
		//LEFT
		if(highestLeft.pos.x+highestLeft.dim.x - entity("coin").getCenterPos().x < 2000)
			putSol(
					highestLeft.pos.x + highestLeft.dim.x + LM.dice.nextFloat()*GAP,
					highestLeft.pos.y - 250 + LM.dice.nextFloat()*500,
					1000 + LM.dice.nextFloat()*WIDTH
					);
		
	}
	
	private void putSol(float x, float y, float w){
		if(!bufferSols.isEmpty()){
			Sol s = bufferSols.get(0);
			bufferSols.remove(0);
		
			s.dim.x = w;
			s.pos.x = x;
			s.pos.y = y;
			
			s.c.set(Color.valueOf("EDCDB7"));
			s.c.r += 0.2f*LM.dice.nextFloat()-0.1f;
			s.c.g += 0.2f*LM.dice.nextFloat()-0.1f;
			s.c.b+= 0.2f*LM.dice.nextFloat()-0.1f;
			
			activeSols.add(s);
			eList.put(s.name, s);
		}
	}
	
	private void setUpSols(){
		bufferSols = new ArrayList<Sol>();
		activeSols = new ArrayList<Sol>();
		
		for(int i = 0; i < 10; i++){
			bufferSols.add(new Sol("stock"+i, 1000, 1000, 300, 0, 9000, -150, Color.valueOf("EDCDB7"), 0.1f, "img/stonetex.png", 1f));
			bufferSols.get(i).init(this);
		}
		
		LM.loader.finishLoading();
		
		for(int i = 0; i < 10; i++){
			bufferSols.get(i).doneLoad(this);
		}
		
		putSol(-500, -1000, 1000);
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
	
	public static void blur(float b){
		blur = b;
		blurSpeed = 1f;
	}
	
	public static void blurFast(float b){
		blur = b;
		blurSpeed = 4f;
	}
	
	private void setUpShaders(){
		blurShader = new ShaderProgram(Gdx.files.internal("shaders/mBlur.vsh"), Gdx.files.internal("shaders/mBlur.fsh"));
		outPrint("Blur Shader compiled: " +blurShader.isCompiled() +"\n"+blurShader.getLog());
		
		tintShader = new ShaderProgram(Gdx.files.internal("shaders/tint.vsh"), Gdx.files.internal("shaders/tint.fsh"));
		outPrint("Tint Shader compiled: " +tintShader.isCompiled() +"\n"+tintShader.getLog());
		tintShader.setUniformf("alpha", 1f);
		
		blurVShader = new ShaderProgram(Gdx.files.internal("shaders/BlurV.vsh"), Gdx.files.internal("shaders/BlurU.fsh"));
		outPrint("BLUR V SHADER COMPILED: "+blurVShader.isCompiled() +blurVShader.getLog());
		blurVShader.pedantic = false;
		
		blurHShader = new ShaderProgram(Gdx.files.internal("shaders/BlurH.vsh"), Gdx.files.internal("shaders/BlurU.fsh"));
		outPrint("BLUR H SHADER COMPILED: "+blurHShader.isCompiled() +blurHShader.getLog());
		blurHShader.pedantic = false;
		
		plantShader = new ShaderProgram(Gdx.files.internal("shaders/plant.vsh"), Gdx.files.internal("shaders/plant.fsh"));
		outPrint("PLANT SHADER COMPILED: "+plantShader.isCompiled() +plantShader.getLog());
		plantShader.pedantic = false;
		Plunt.plantShader = plantShader;
		Pot.plantShader = plantShader;
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
		
		cpDark = new ColProfile(new String[]{
				"5d3c47",
				"2c101a",
				"291118",
				"16131b",
				"100e19",
				"100e19"
				},
				new float[]{
				0.31f, 0.57f, 0.85f, 0.98f
		});
		
		cpTarg = cpDef;
		cp.set(cpTarg);
	}
	
	
	private void addDebuttons(){
		addEnt(new Debutton("Change ColProfile", 10, 250, 1.5f, false){
			public void execute(){
			if(cpTarg == cpDef){
				cpTarg = cpDark;
				//cp.set(cpDark);
				bright = 0f;
				bAlphaT = 1f;
				bAlpha = 1f;
				oAlphaT = 0f;
				oAlpha = 0f;
			}
			else{
				cpTarg = cpDef;
				bAlphaT = 0f;
				oAlphaT = 0f;
			}
			}});
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
