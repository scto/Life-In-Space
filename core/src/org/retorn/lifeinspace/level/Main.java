package org.retorn.lifeinspace.level;

import java.util.ArrayList;
import java.util.Iterator;

import org.retorn.lifeinspace.engine.Camera;
import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.SM;
import org.retorn.lifeinspace.engine.Sol;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.entity.ButtonSave;
import org.retorn.lifeinspace.entity.ButtonShip;
import org.retorn.lifeinspace.entity.ButtonUp;
import org.retorn.lifeinspace.entity.Clound;
import org.retorn.lifeinspace.entity.Coin;
import org.retorn.lifeinspace.entity.Debutton;
import org.retorn.lifeinspace.entity.Monitor;
import org.retorn.lifeinspace.entity.Plunt;
import org.retorn.lifeinspace.entity.Pot;
import org.retorn.lifeinspace.entity.SeedAsp;
import org.retorn.lifeinspace.entity.Ship;
import org.retorn.lifeinspace.entity.Smoke;
import org.retorn.lifeinspace.entity.WaterPiece;
import org.retorn.lifeinspace.tech.ColProfile;
import org.retorn.lifeinspace.tech.PotProfile;
import org.retorn.lifeinspace.tech.RTimer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

import sun.awt.im.InputMethodManager;

public class Main extends Level{
	public static float inc;
	public static float blur = 1f;
	public static float blurT = 0f;
	public static float blurSpeed = 1f;
	public static float oAlpha = 1f;
	public static float oAlphaT = 0f;
	public static float bAlpha = 1f;
	public static float bAlphaT = 0.0f;
	public static float bright = 1f;
	
	public static float transitionStart;//Where the camera is when a transition starts.
	public static float transitionTime = 0.3f;
	public static float tinc;
	public static int transitionSt;
	public static final int NOT = 0;
	public static final int AM = 1;
	
	public static boolean inShip = true;
	public static Vector2 resFac;
	public static ShaderProgram tintShader;
	public static ShaderProgram plantShader;
	private static FrameBuffer fbo;
	private static Color tint, tintT;
	private static ColProfile cp, cpTarg, cpDef, cpDark;
	private TextureRegion vig;
	public static Sound introSound, beepSound, kissSound, coinSound, jumpSound, landSound, clickSound, plantSound, popSound, breakSound, warpSound, grownSound;
	public static Music windMusic, shipMusic, shipAmb;
	public static float windVol, ambVol;
	
	public static boolean virgin;
	
	public static ArrayList<Sol> bufferSols, activeSols;

	public Main() {
		super(Color.BLACK, "main", 60);
	}

	public void init() {
		manageSave();
		
		resFac = new Vector2();
		setUpShaders();
		
		LM.loadSound("audio/coin_jump.ogg");
		LM.loadSound("audio/coin_jump_OG.ogg");
		LM.loadSound("audio/coin_land.ogg");
		LM.loadSound("audio/click.ogg");
		LM.loadSound("audio/plant.ogg");
		LM.loadSound("audio/pop.ogg");
		LM.loadSound("audio/break.ogg");
		LM.loadSound("audio/warp.ogg");
		LM.loadSound("audio/grown.ogg");
		LM.loadSound("audio/kiss.ogg");
		LM.loadSound("audio/beep.ogg");
		LM.loadSound("audio/intro.ogg");
		
		LM.loadMusic("audio/wind.ogg");
		LM.loadMusic("audio/ambNoise.ogg");
		LM.loadMusic("audio/endsong.ogg");
		
		LM.loadTexture("img/vig.png");
		
		setUpSols();
		
		addEnt(new Monitor());
		
		addEnt(new ButtonUp("ib", 10, 10));
		addEnt(new ButtonShip("sb", 35, LM.HEIGHT-125));
		addEnt(new ButtonSave("sab", LM.WIDTH-135, LM.HEIGHT-125));
		
		addEnt(new Ship());
		
		if(virgin){
			addEnt(new Pot("pot0", 500, 10001, 80));
			addEnt(new SeedAsp("seed0", 300, 10001, 80));
		}
		
		
		
		addEnt(new Coin("coin", -120, 10000, -10));
		
		camList.put("spaceCam", new Camera(1280, 720));
		camList.get("spaceCam").setPos(2100, 500);
		camList.get("spaceCam").zoom = 3.5f;
		
		fbo = new FrameBuffer(Format.RGB888, LM.WIDTH, LM.HEIGHT, false);
		
		setUpColors();
	}

	public void postLoad() {
		BG.doneLoad();
		
		vig = new TextureRegion(LM.loader.get("img/vig.png", Texture.class));
		
		jumpSound = LM.loader.get("audio/coin_jump.ogg", Sound.class);
		coinSound = LM.loader.get("audio/coin_jump_OG.ogg", Sound.class);
		landSound = LM.loader.get("audio/coin_land.ogg", Sound.class);
		clickSound = LM.loader.get("audio/click.ogg", Sound.class);
		plantSound = LM.loader.get("audio/plant.ogg", Sound.class);
		popSound = LM.loader.get("audio/pop.ogg", Sound.class);
		breakSound = LM.loader.get("audio/break.ogg", Sound.class);
		warpSound = LM.loader.get("audio/warp.ogg", Sound.class);
		grownSound = LM.loader.get("audio/grown.ogg", Sound.class);
		kissSound = LM.loader.get("audio/kiss.ogg", Sound.class);
		beepSound = LM.loader.get("audio/beep.ogg", Sound.class);
		introSound = LM.loader.get("audio/intro.ogg", Sound.class);
		
		windMusic = LM.loader.get("audio/wind.ogg", Music.class);
		windMusic.setLooping(true);
		windMusic.play();
		
		shipAmb = LM.loader.get("audio/ambNoise.ogg", Music.class);
		shipAmb.setLooping(true);
		shipAmb.play();
		
		shipMusic = LM.loader.get("audio/endsong.ogg", Music.class);
		
		getCam().setPos(entity("coin"));
		
		if(virgin)	initialCutscene();
		else{
			warpSound.play(0.5f, 1f, 0.5f);
			Monitor.st = 4;
		}
	}
	
	public void superRender(){
		startFBO();
		BG.render(this);
		entity("mon").render(this);
		LM.useDefaultCamera();
		//LM.drawText("Coinship", 100, 420, Color.WHITE, 5f);
		LM.useLevelCamera();
	}

	public void render() {
		renderButtons();
		
		LM.useDefaultCamera();
		LM.batch.setShader(LM.brightShader);
		LM.brightShader.setUniformf("bright", 1f);
		LM.brightShader.setUniformf("alpha", inShip ? 0.3f : 0.1f);
		LM.batch.draw(vig, 0, 0);
		LM.batch.setShader(null);
		LM.useLevelCamera();
		
		fbo.end();

		//Color-Correction
		ColProfile.useShader(cp, bAlpha, tint, oAlpha, bright);
		LM.useDefaultCamera();
		LM.batch.draw(fbo.getColorBufferTexture(), 0, LM.HEIGHT, LM.WIDTH, -LM.HEIGHT);
		LM.batch.setShader(null);
		
		manageColours();
		
		LM.useLevelCamera();
	}
	
	private void manageColours(){
		if(!inShip){
			cpTarg = cpDef;
			oAlphaT = 1f;
			tintT = Color.valueOf("F5CB71");
		}
		
		else{
			cpTarg = cpDark;
			oAlphaT = 2f+LM.dice.nextFloat()*0.5f;
			bAlphaT = 0f;
			tintT = Color.valueOf("ED99F0");
		}
		
		
		tint.lerp(tintT, 0.1f);
		if(transitionSt == NOT) bright += Tween.tween(bright, 1f, 2f - (virgin ? 1.5f : 0f));
		blur += Tween.tween(blur, blurT, blurSpeed);
		oAlpha += Tween.tween(oAlpha, oAlphaT, 1f -(virgin ? 0.9f : 0f));
		bAlpha += Tween.tween(bAlpha, bAlphaT, 1f - (virgin ? 0.9f : 0f));
		cp.tween(cpTarg, 0.1f - (virgin ? 0.05f : 0f));
	}

	public void tick() {
		inc += LM.endTime;
		tickResFacs();
		
		if(virgin && Monitor.st >= 4) virgin = false;

		if((Monitor.lit >= 6 && Monitor.st == 4)){
			Monitor.st = 5;
			beepSound.play(0.1f, 1f, 0.5f);
			LM.addTimer(new RTimer(){public void act(){
				beepSound.play(0.1f, 0.9f, 0.5f);
				Monitor.st = 6;
					LM.addTimer(new RTimer(){public void act(){
						beepSound.play(0.1f, 0.85f, 0.5f);
						Monitor.st = 7;
						LM.addTimer(new RTimer(){public void act(){
							beepSound.play(0.1f, 1.0f, 0.5f);
							Monitor.st = 8;
							LM.addTimer(new RTimer(){public void act(){
								beepSound.play(0.1f, 1.0f, 0.5f);
								Monitor.st = 9;
								LM.addTimer(new RTimer(){public void act(){
									beepSound.play(0.1f, 1.0f, 0.5f);
									Monitor.st = 10;
									LM.addTimer(new RTimer(){public void act(){
										beepSound.play(0.1f, 1.0f, 0.5f);
										Monitor.st = 11;
										shipMusic.play();
								}}, 2f);
							}}, 0.1f);
						}}, 0.3f);
					}}, 3f);
				}}, 1f);
			}}, 1f);
		}
			
		
		
		
		manageButtons();
		if(transitionSt == AM) manageTransition();
		
		if(transitionSt == NOT){
			manageCam();
	
		}
		
		manageMusic();
		
		BG.tick(this);
		bg.set(BG.col);
		
		if(!inShip) manageSols();
		
	}
	
	private void manageCam(){
		if(inShip){
			getCam().setTarget(entity("Ship Floor").getCenterPos().x, 10600, 3);
			getCam().tPos.x += (entity("coin").getCenterPos().x - getCam().tPos.x)*0.24f;
			getCam().tPos.y += (entity("coin").getCenterPos().y - getCam().tPos.y)*0.2f;
			camList.get("default").setZoomTarget(3.0f, 3f - (virgin ? 2f : 0f));
		}
		
		if(!inShip){
			camList.get("default").setTarget(entity("coin"), 3f);
			if(entity("coin").pos.y < Coin.standardHeight || Coin.dead) camList.get("default").tPos.y = Coin.standardHeight+140;
			else camList.get("default").tPos.y += 140*camList.get("default").zoom;
			camList.get("default").setZoomTarget(3f, 3f);
		}
	}
	
	private void manageMusic(){
		if(!inShip){
			windVol += Tween.tween(windVol, 0.4f, 1f);
			ambVol += Tween.tween(ambVol, 0.0f, 1f);
		}
		
		if(inShip){
			windVol += Tween.tween(windVol, 0f, 10f);
			ambVol += Tween.tween(ambVol, 0.4f, 1f);
		}
		
		windMusic.setVolume(windVol);
		shipAmb.setVolume(ambVol);
	}
	
	private void manageTransition(){
		if(!inShip){
			if(tinc < transitionTime) tinc += LM.endTime;
			getCam().pos.y = Tween.cubicTween(tinc, transitionStart, 800, transitionTime);
			bright = Tween.cubicTween(tinc, 1f, -1f, transitionTime);
			transitionTime = 0.3f;
		}
		
		else{
			if(tinc < transitionTime) tinc += LM.endTime;
			getCam().pos.y = Tween.cubicTween(tinc, transitionStart, -200, transitionTime);
			bright = Tween.cubicTween(tinc, 1f, -1f, transitionTime);
			transitionTime = 0.3f;
		}
	}
	
	public void manageButtons(){
		if(ButtonSave.pressed) save();
		
		if(ButtonShip.pressed || InputManager.pressedTab){
			if(!inShip){
				save();
				tinc = 0f;
				warpSound.play(0.5f, 1f, 0.5f);
				transitionSt = AM;
				transitionStart = getCam().pos.y;
				bright = 1f;
				getCam().setShake(30, 30, 100);
						
				LM.addTimer(new RTimer(){public void act(){
				getCam().setShake(30, 50, 100);
				entity("coin").pos.x = -50;
				entity("coin").pos.y = 10001;
				entity("coin").v.setZero();
				getCam().pos.x = entity("coin").getCenterPos().x;
				getCam().pos.y = entity("coin").getCenterPos().y+100;
				cp.set(cpDark);
				bAlpha = 1f;
				bright = 0.0f;
				inShip = true;
				transitionSt = NOT;
				}}, transitionTime);
				
			}
			
			else{
				tinc = 0f;
				warpSound.play(0.5f, 1f, 0.5f);
				transitionSt = AM;
				transitionStart = getCam().pos.y;
				bright = 1f;
				getCam().setShake(30, 30, 100);
						
				LM.addTimer(new RTimer(){public void act(){
				getCam().setShake(50, 50, 100);
				entity("coin").pos.x = Coin.standardX;
				entity("coin").pos.y = Coin.standardHeight + 201;
				entity("coin").v.setZero();
				getCam().pos.x = entity("coin").getCenterPos().x;
				getCam().pos.y = entity("coin").getCenterPos().y + 280*camList.get("default").zoom;
				cp.set(cpDark);
				bAlpha = 1f;
				bright = 0.0f;
				inShip = false;
				transitionSt = NOT;
				}}, transitionTime);
				tinc = 0f;
				bright = 1.5f;
			}
		}
	}
	
	public void renderButtons(){
			entity("ib").render(this);
			entity("sb").render(this);
			entity("sab").render(this);
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
					lowestRight.pos.x - w - (LM.dice.nextFloat()*GAP + 100),
					lowestRight.pos.y - 250 + LM.dice.nextFloat()*500,
					w
					);
		}
		//LEFT
		if(highestLeft.pos.x+highestLeft.dim.x - entity("coin").getCenterPos().x < 2000)
			putSol(
					highestLeft.pos.x + highestLeft.dim.x + LM.dice.nextFloat()*GAP + 100,
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
			
			s.c.set(Color.valueOf("E2E8C3"));
			
			activeSols.add(s);
			eList.put(s.name, s);
			
			//CLOUNDS
			if(LM.dice.nextInt(100) == 0){
				addEnt(new Clound("clound"+LM.dice.nextFloat(),
						s.pos.x + s.dim.x*LM.dice.nextFloat() -125, s.pos.y+s.dim.y+1, -100));
			}

			
			//POTS
			if(LM.dice.nextInt(80) == 0){
				addEnt(new Pot("pot"+LM.dice.nextFloat(),
						s.pos.x + s.dim.x*LM.dice.nextFloat() -40, s.pos.y+s.dim.y+1, 80));
			}
			
			//SEEDS
			if(LM.dice.nextInt(60) == 0){
				addEnt(new SeedAsp("seed"+LM.dice.nextFloat(),
						s.pos.x + s.dim.x*LM.dice.nextFloat() -22.5f, s.pos.y+s.dim.y+1, 80));
			}
			
			//WATER PIECES
			if(LM.dice.nextInt(2) == 0){ 
				float wx = s.pos.x+s.dim.x-30 - s.dim.x*LM.dice.nextFloat();
				if(eList.containsKey("coin")) wx += (entity("coin").getCenterPos().x - wx)*0.2f;
				addEnt(
					new WaterPiece(
					"wp"+LM.dice.nextFloat(), 
					wx, 
					s.pos.y+s.dim.y+1500,
					0, 
					0, 0,
					2));
			}
		}
	}
	
	private void setUpSols(){
		bufferSols = new ArrayList<Sol>();
		activeSols = new ArrayList<Sol>();
		
		for(int i = 0; i < 10; i++){
			bufferSols.add(new Sol("stock"+i, 1000, 2000, 300, 0, 9000, -150, Color.valueOf("EDCDB7"), 0.1f, "img/newtex.png", 1f));
			bufferSols.get(i).init(this);
		}
		
		LM.loader.finishLoading();
		
		for(int i = 0; i < 10; i++){
			bufferSols.get(i).doneLoad(this);
		}
		
		putSol(-500, -2000, 1000);
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
		tintShader = new ShaderProgram(Gdx.files.internal("shaders/tint.vsh"), Gdx.files.internal("shaders/tint.fsh"));
		outPrint("Tint Shader compiled: " +tintShader.isCompiled() +"\n"+tintShader.getLog());
		tintShader.setUniformf("alpha", 1f);
		
		plantShader = new ShaderProgram(Gdx.files.internal("shaders/plant.vsh"), Gdx.files.internal("shaders/plant.fsh"));
		outPrint("PLANT SHADER COMPILED: "+plantShader.isCompiled() +plantShader.getLog());
		plantShader.pedantic = false;
		Plunt.plantShader = plantShader;
		Pot.plantShader = plantShader;
		Clound.plantShader = plantShader;
		Smoke.plantShader = plantShader;
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
	
	private void initialCutscene(){
		introSound.play(0.5f, 1f, 0.5f);
		bright = 0f;
		LM.addTimer(new RTimer(){public void act(){
			bright = 5f;
			oAlpha = 2f;
			bAlpha = 0f;
			Monitor.st = 1;
			
			LM.addTimer(new RTimer(){public void act(){
				beepSound.play(0.2f, 0.95f+LM.dice.nextFloat()*0.1f, 0.5f);
				Monitor.st = 2;
				
				LM.addTimer(new RTimer(){public void act(){
					beepSound.play(0.2f, 0.65f+LM.dice.nextFloat()*0.1f, 0.5f);
					Monitor.st = 3;
					
					LM.addTimer(new RTimer(){public void act(){
						beepSound.play(0.2f, 0.45f+LM.dice.nextFloat()*0.1f, 0.5f);
						Monitor.st = 4;
					}}, 2f);
				}}, 2f);
			}}, 5f);
			
		}}, 2.236f);
	}
	
	private void save(){
		ArrayList<Pot> es = new ArrayList<Pot>();
		for(Entity e : eList.values()){
			if(e instanceof Pot) es.add((Pot)e);
		}
		
		PotProfile pp = new PotProfile();
		
		int q = 0;
		for(Pot p : es){
			if(AABB(p, entity("shiparea"))) q++;
		}
		pp.worths = new float[q + (entity("coin", Coin.class).heldEnt instanceof Pot ? 1 : 0)];
		
		int i = 0;
		outPrint("");
		for(Pot p : es){
			outPrint(p.name +" worth: "+( p.plunt != null ? p.plunt.worth:"NULL"));
			if(AABB(p, entity("shiparea"))) pp.worths[i] = p.plunt != null ? p.plunt.worth : -1f;
		}
		
		SM.saveValue("potprofile", pp);
		
		SM.saveValue("mon", Monitor.st);
		
	}
	
	private void manageSave(){
		//Your first time launching the game/writing a save-file.
		if(!SM.containsKey("virgin")){
			SM.saveValue("virgin", false);
			virgin = true;
		}
		else virgin = false;
		
		if(!SM.containsKey("mon"))
			SM.saveValue("mon", 0);
		Monitor.st = SM.getValue("mon");
		
		//POTS
		if(!SM.containsKey("potprofile"))
			SM.saveValue("potprofile", new PotProfile());
		
		PotProfile pp = SM.getValue("potprofile");
		if(pp.worths != null){
		for(float f : pp.worths){
			if(f == -1f) addEnt(new Pot("pot"+LM.dice.nextFloat(), -1500 + 3000*LM.dice.nextFloat(), 10001, 80));
			else{
				Pot p = new Pot("pot"+LM.dice.nextFloat(), -1500 + 3000*LM.dice.nextFloat(), 10001, 80);
				addEnt(new Plunt("plunt"+LM.dice.nextFloat(), p, f));
				addEnt(p);
			}
		}
		}
		
	}

	public void enter() {
		
	}

	public void exit() {
		
	}

	public String getDebug() {
		return "bright: "+bright
					+"\ninShip: "+inShip
					+"\nVirgin: "+virgin;
	}

	public void pause() {
		
	}

	public void resume() {
		
	}

}
