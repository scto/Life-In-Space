package org.retorn.lifeinspace.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.retorn.lifeinspace.tech.RTimer;
import org.retorn.lifeinspace.tech.RTimerLoaded;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class LM extends ApplicationAdapter{
	
	public static SpriteBatch batch;
	public static FrameBuffer buffer;//Used to render everything to pre-post-processing effects.
	public static ShapeRenderer shaper;//Possibly should go somewhere else.
	public static int WIDTH, HEIGHT, WIDTH_OG, HEIGHT_OG;//Actively Useful/Initial.
	private static String lvlID;
	private static String newLvlID;//Used to store lvlID for when it's safe to change levels.
	public static BitmapFont debugText, drawText;
	protected static HashMap<String, Level> lvls;
	public static Camera defaultCam;
	public static boolean usingLevelCamera;
	public static boolean usingDefaultCamera;
	public static boolean debug;
	public static boolean allowDebug = false;//Used for release-versions.
	public static ShaderProgram defaultShader;
	public static ShaderProgram fontShader;//Used by Levels for rendering debug text.
	public static ShaderProgram brightShader;//General shader ft. alpha & bright.
	public static HashMap<String, ShaderProgram> uShaders;
	protected static LoadLord loadLord;//Used to init universal stuff. Done in DesktopLauncher.
	public static Random dice;//Universal dice stuff can use.
	public static float gameSpeed;//Universal speed that entities must take into account.
	public static float gameVolume;//Universal volume used by entity .playSound() methods.
	public static float gameSoundEffectVolume, gameMusicVolume;//Individual ones.
	public static AssetManager loader;
	public static TextureParameter tParam;//This is a TextureParameter w/ mipmapping.
	public static Sprite loadingImg;//Loading-Image that appears on loading screen.
	protected static Color lBG;//Bg colour for loading screens.
	public static boolean loadingComplete;//This means everything is done loading in loader.
	public static Level[] gameLevels;//Array containing every level in the whole dang game. Set up in Desktop Launcher, assigned in this constructor.
	private static ArrayList<RTimerLoaded> timers;
	private static boolean paused;//Whether or not this is paused.
	public static float delta;//Capped delta.
	public static float endTime;//Delta*Gamespeed.
	public static boolean fixedDim;//Whether or not the app stretches when resized.
	public static SM SM;
	public static InputManager inputManager;
	protected static boolean loadIntroDone;//Controlled by the loading screen stuff. Used to stop the game from starting if you need to finish showing an initial intro.
	protected static String fontName = "grobe2";
	private boolean useControllers;
	
	public LM(int W, int H, Color c, boolean fd, String initialLevel, boolean useController, Level[] levels){
		WIDTH = W;
		HEIGHT = H;
		WIDTH_OG = W;
		HEIGHT_OG = H;
		fixedDim = fd;
		lBG = c;
		newLvlID = initialLevel;
		lvlID = initialLevel;
		gameLevels = levels;
		useControllers = useController;
	}

	public void create () {
		new SM();
		//Setting up loader + tParam.
		loader = new AssetManager();
		tParam = new TextureParameter();
		tParam.genMipMaps = true;
		tParam.minFilter = TextureFilter.MipMapLinearLinear;
		tParam.magFilter = TextureFilter.MipMapLinearLinear;
		
		//Dice
		dice = new Random(); 
		
		//Timers
		timers = new ArrayList<RTimerLoaded>();
		
		loadLord = getDefaultLoadLord();
		initLoadingAssets();
		
		defaultCam = new Camera(WIDTH, HEIGHT);
		defaultCam.pos.x = WIDTH/2; defaultCam.pos.y = HEIGHT/2;
		defaultCam.position.x = WIDTH/2; defaultCam.position.y = HEIGHT/2;
		defaultCam.setToOrtho(false, WIDTH, HEIGHT);
		defaultCam.update();
		//outPrint("Default Cam set up with with "+WIDTH +"x"+HEIGHT);
		shaper = new ShapeRenderer(); 
		//outPrint("ShapeRenderer set up.");
		batch = new SpriteBatch();
		batch.enableBlending();
		batch.setBlendFunction(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
		
		buffer = new FrameBuffer(Format.RGBA8888, 1920, 1080, false);
		
		gameSpeed = 1f; 
		gameVolume = 1f;
		gameSoundEffectVolume = 1f;
		gameMusicVolume = 1f;

		//Setting up fonts.
		//Debug font. Set up with Distance-Field.
		Texture fTex;
		fTex = new Texture(Gdx.files.internal("Font/eastCoast.png"), true);
		fTex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		debugText = new BitmapFont(Gdx.files.internal("Font/eastCoast.fnt"), new TextureRegion(fTex), false);
		
		debugText.setUseIntegerPositions(false);
		debugText.getData().setLineHeight(25);
		
		//Draw Font.
		try{
		fTex = new Texture(Gdx.files.internal("Font/"+fontName+".png"), true);
		fTex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		}
		catch(GdxRuntimeException g){
		try{fTex = new Texture(Gdx.files.internal((Gdx.app.getType() != ApplicationType.Android ? "engineAssets/":"")+"Font/"+fontName+".png"), true);}
		catch(Exception e){fTex = new Texture(Gdx.files.internal("Font/"+fontName+".png"), true);}
		fTex.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		}
		
		try{drawText = new BitmapFont(Gdx.files.internal((Gdx.app.getType() != ApplicationType.Android ? "engineAssets/":"")+"Font/"+fontName+".fnt"), new TextureRegion(fTex), false);}
		catch(Exception e){drawText = new BitmapFont(Gdx.files.internal("Font/"+fontName+".fnt"), new TextureRegion(fTex), false);}
		drawText.setUseIntegerPositions(false);
		drawText.getData().setLineHeight(25);
		
		fontShader = new ShaderProgram(Gdx.files.internal("shaders/font.vsh"), Gdx.files.internal("shaders/font.fsh"));	
		
		if(!fontShader.isCompiled()) outPrint(fontShader.getLog());
		
		//Universal Shaders.
		uShaders = new HashMap<String, ShaderProgram>();
		brightShader = new ShaderProgram(Gdx.files.internal("shaders/brightness.vsh"), Gdx.files.internal("shaders/brightness.fsh"));
		
		
		//Input
		inputManager = new InputManager();
		inputManager.init();//Has to be instanced so it can be fed to Gdx.input.setInputProcessor().
		Gdx.input.setInputProcessor(inputManager);
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		
		//Load Lord.
		if(loadLord != null) loadLord.reign();
		
		//Levels
		lvls = new HashMap<String, Level>();

		//Add levels.
		for(Level l: gameLevels){
			addLevel(l);
		}
		//Initing first level.
		lvls.get(lvlID).setup();
		lvls.get(lvlID).init();
		lvls.get(lvlID).inited = true;
		
		debug = false;
	}
	
	public void render () {
		delta = Gdx.graphics.getDeltaTime() < 0.06f? Gdx.graphics.getDeltaTime() : 0.016f;
		delta = delta == 0 ? 0.0001f : delta;
		endTime = delta*gameSpeed;
		//If there's a new level, change it.
	if(!lvlID.equals(newLvlID)) migrateLevels();
	if(loader.update()){
		//If the whole level was just loaded, do the post-load stuff.
		if(!loadingComplete){
			lvls.get(lvlID).addPendingEnts();
			lvls.get(lvlID).postLoad();
			lvls.get(newLvlID).enter();
		}
		loadingComplete = true;
		}
	//Done loading + level inited. To prevent trying to use cameras before they exist.
	if(!paused){
	if(loadingComplete && lvls.get(lvlID).inited && loadIntroDone){
		//Make current buffer.
		Gdx.gl.glClearColor(lvls.get(lvlID).bg.r, lvls.get(lvlID).bg.g, lvls.get(lvlID).bg.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		manageTimers();
		batch.begin();
		update();
		//Update level cameras
		for(Camera c: lvls.get(lvlID).camList.values()){ c.update();}
		useLevelCamera();
		lvls.get(lvlID).renderEnts();
		lvls.get(lvlID).render();
		if(debug) lvls.get(lvlID).renderDebug(1);
		batch.end();
		//Rendering debug cube.
		if(debug){
			shaper.begin(ShapeType.Line);
			lvls.get(lvlID).renderDebug(0);
			shaper.end();
		}
		batch.setProjectionMatrix(lvls.get(lvlID).camList.get(lvls.get(lvlID).camIndex).combined);
		shaper.setProjectionMatrix(lvls.get(lvlID).camList.get(lvls.get(lvlID).camIndex).combined);
	}
	//Loading
	else{
		Gdx.gl.glClearColor(lvls.get(lvlID).bg.r, lvls.get(lvlID).bg.g, lvls.get(lvlID).bg.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		manageTimers();
		useDefaultCamera();
		renderLoadingScreen();
	}
		InputManager.resetPress();
	}
	else{
		}
	}
	
	//Should be supered AFTER LOADING OTHER STUFF if there's a custom loading screen outside of loadimg.
	//It calls "finishLoading" after loading loadimg, so that's why.
	public void initLoadingAssets(){
		//Forcing it to load the loading image for loading screens. Determined by individual projects to enable semi-custom loading screens.
		loader.load("img/misc/loadimg.png", Texture.class, tParam);
		loader.finishLoading();
		//Setting loading image.
		loadingImg = new Sprite(loader.get("img/misc/loadimg.png", Texture.class));
		
	}
	
	
	public abstract void renderLoadingScreen();
		
	
	public void update() {
		lvls.get(lvlID).tick();
		lvls.get(lvlID).tickEnts();
		
		if(Gdx.app.getType() != ApplicationType.Android){
		if((InputManager.pressedF1) && allowDebug)
			debug = !debug;
		if(gameSpeed > 0.9999 && gameSpeed < 1)
			gameSpeed = 1;
		}
		
	}
	
	public void resize(int width, int height){
		lvls.get(lvlID).resize(width, height);
		
		if(!fixedDim){
		WIDTH = width;
		HEIGHT = height;
		
		defaultCam.setToOrtho(false, WIDTH, HEIGHT);
		defaultCam.pos.set(WIDTH/2, HEIGHT/2);
		defaultCam.update();
		outPrint("W:"+WIDTH +"H: "+HEIGHT);
		for(Level l: lvls.values()){
			for(Camera c: l.camList.values()){
				float q = c.rotation;
				c.setToOrtho(false, WIDTH, HEIGHT);
				c.rotation = 0;
				c.setRotation(q);
			}
		}
		}
	}
	
	public static void changeLevels(String n){
		if(lvls.get(newLvlID) != null){
			lvls.get(lvlID).exit();
			newLvlID = n;
			//lvls.get(newLvlID).enter();
			if(!lvls.get(newLvlID).inited){
			lvls.get(newLvlID).setup();
			lvls.get(newLvlID).init();
			lvls.get(newLvlID).inited = true;
			}
		}
	}
	
	public static void migrateLevels(){
		//Exit, dispose of the old level. Init + enter the new one.
		lvls.get(lvlID).exit();
		lvls.get(lvlID).dispose();
		lvls.get(lvlID).eList.clear();
		lvlID = newLvlID;
		lvls.get(lvlID).addPendingEnts();
		lvls.get(lvlID).enter();
		
		System.out.println("Entered level "+lvls.get(lvlID).name+".");
		loadingComplete = false;
		
	}
	
	public void addLevel(Level l){
		lvls.put(l.name, l);
		System.out.println("Added level "+l.name +".");
	}
	
	public void addShader(String n, ShaderProgram s){
		uShaders.put(n, s);
	}
	
	public ShaderProgram getShader(String n){
		try{
			return uShaders.get(n);
		}
		catch(NullPointerException e){
			outPrint("No shader indexed called '"+n+"'.");
			return null;
		}
	}
	
	public void dispose(){
		batch.dispose();
		shaper.dispose();
		loader.dispose();
		for(Level l: lvls.values()){ l.dispose();}
		loader.dispose();
	}
	
	//public static void pause(){
	//	paused = true;
	//}
	
	public static void useDefaultCamera(){
		usingLevelCamera = false;
		usingDefaultCamera = true;
		batch.setProjectionMatrix(defaultCam.combined);
		shaper.setProjectionMatrix(defaultCam.combined);
	}
	
	public static void useLevelCamera(){
		usingLevelCamera = true;
		usingDefaultCamera = false;
		batch.setProjectionMatrix(lvls.get(lvlID).camList.get(lvls.get(lvlID).camIndex).combined);
		shaper.setProjectionMatrix(lvls.get(lvlID).camList.get(lvls.get(lvlID).camIndex).combined);
	}
	
	public static void loadTexture(String path){
		LM.loader.load(path, Texture.class, tParam);
	}
	
	public static void loadSound(String path){
		LM.loader.load(path, Sound.class);
	}
	
	public static void loadMusic(String path){
		LM.loader.load(path, Music.class);
	}
	
	public static void drawText(String t, float x, float y){
		batch.setShader(fontShader);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("alph"), drawText.getColor().a);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("scale"), LM.drawText.getScaleX()*2f);
		
		drawText.draw(batch, t, x, y);
		
		batch.setShader(null);
	}
	

	public static void drawText(String t, float x, float y, float sc) {
		Color c = drawText.getColor();
		drawText.setColor(c);
		drawText.getData().scaleX = sc;
		drawText.getData().scaleY = sc;
		
		batch.setShader(fontShader);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("alph"), c.a);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("scale"), sc);
		
		drawText.draw(batch, t, x, y);
		
		batch.setShader(null);
		
		drawText.getData().scaleX = 1f;
		drawText.getData().scaleY = 1f;
		
	}
	
	public static void drawText(String t, float x, float y, Color c){
		Color q = drawText.getColor();
		drawText.setColor(c);
		
		batch.setShader(fontShader);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("alph"), c.a);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("scale"), LM.drawText.getScaleX()*2f);
		
		drawText.draw(batch, t, x, y);
		
		batch.setShader(null);
		drawText.setColor(q);
	}
	
	public static void drawText(String t, float x, float y, Color c, float sc){
		Color q = drawText.getColor();
		drawText.setColor(c);
		drawText.getData().scaleX = sc;
		drawText.getData().scaleY = sc;
		
		batch.setShader(fontShader);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("alph"), c.a);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("scale"), sc);
		
		drawText.draw(batch, t, x, y);
		
		batch.setShader(null);
		
		drawText.setColor(q);
		drawText.getData().scaleX = 1f;
		drawText.getData().scaleY = 1f;
	}
	
	public static void drawText(String t, float x, float y, Color c, float sc, float alpha){
		Color q = drawText.getColor();
		drawText.setColor(c);
		drawText.getData().scaleX = sc;
		drawText.getData().scaleY = sc;
		
		batch.setShader(fontShader);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("alph"), alpha);
		fontShader.setUniformf(LM.fontShader.getUniformLocation("scale"), sc);
		
		drawText.draw(batch, t, x, y);
		
		batch.setShader(null);
		
		drawText.setColor(q);
		drawText.getData().scaleX = 1f;
		drawText.getData().scaleY = 1f;
	}
	
	public static FrameBuffer maintainFbo(FrameBuffer fb){
		if((fb.getWidth() != LM.WIDTH || fb.getHeight() != LM.HEIGHT))
			fb = new FrameBuffer(Format.RGBA8888, LM.WIDTH == 0 ? 1 : LM.WIDTH, LM.HEIGHT == 0 ? 1 : LM.HEIGHT, false);
		return fb;
	}
	
	//TIMERS
	public void manageTimers(){
		for(int i = 0; i < timers.size(); i++){
			RTimerLoaded rt = timers.get(i);
			rt.tick();
			if(rt.inc > rt.t){
				rt.r.act();
				timers.remove(rt);
				i--;
			}
		}
	}
	
	public static void addTimer(RTimer r, float t){
		timers.add(new RTimerLoaded(r, t));
	}
	
	//LOAD LORD
	private LoadLord getDefaultLoadLord(){
		return new LoadLord(){
			public void reign(){
				Sol.setUp();
			}
		};
	}
	
	@Override
	public void pause() {
		lvls.get(lvlID).pause();
		//paused = true;
	}

	@Override
	public void resume() {
		lvls.get(lvlID).resume();
		//paused = false;
	}

	public static void outPrint(String n){
		System.out.println("[LM]: "+n);
	}

	
}
