package org.retorn.lifeinspace.level;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Sol;
import org.retorn.lifeinspace.entity.Coin;
import org.retorn.lifeinspace.entity.Debutton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class Main extends Level{
	public static float inc;
	public static Vector2 resFac;
	public static ShaderProgram blurShader;

	public Main() {
		super(Color.BLACK, "main", 60);
	}

	public void init() {
		resFac = new Vector2();
		setUpShaders();
		
		addDebuttons();
		addEnt(new Coin("coin", 10, 10, 0));
		addEnt(new Sol("floor", 10400, 1000, 300, -700, -1000, -150, Color.valueOf("DFE3C8"), 0.1f, "img/stonetex.png", 1f));
	}

	public void postLoad() {
		
	}
	
	public void superRender(){
		LM.useDefaultCamera();
		//LM.drawText("Coinship", 100, 420, Color.WHITE, 5f);
		LM.useLevelCamera();
	}

	public void render() {
		renderDebuttons();
	}

	public void tick() {
		inc += LM.endTime;
		tickResFacs();
		
		getCam().setTarget(entity("coin"), 5f);
		if(entity("coin").pos.y < -100) getCam().tPos.y = -100*getCam().zoom;
		else getCam().tPos.y += 120*getCam().zoom+(float)Math.cos(inc*0.1f)*10;
		getCam().setZoomTarget(2.0f, 3f);
		
		BG.tick(this);
		bg.set(BG.col);
	}
	
	public void renderDebuttons(){
		for(Entity e : eList.values()){
			if(e instanceof Debutton) e.render(this);
		}
	}
	
	private void addDebuttons(){
		addEnt(new Debutton("resetX", 10, 10){
			public void execute(){
			entity("coin").pos.x = 0;
			}});
		addEnt(new Debutton("resetY", 10, 70){
			public void execute(){
			entity("coin").pos.y = 700;
			}});
		addEnt(new Debutton("Go Gravless", 10, 130){
			public void execute(){
			entity("coin", Coin.class).st = 2;
			}});
		addEnt(new Debutton("Go Groundful", 10, 190){
			public void execute(){
			entity("coin", Coin.class).st = 1;
			}});
	}
	
	private void tickResFacs(){
		resFac.set( Gdx.graphics.getWidth()/(float)LM.WIDTH_OG,
						      Gdx.graphics.getHeight()/(float)LM.HEIGHT_OG);
	}
	
	private void setUpShaders(){
		blurShader = new ShaderProgram(Gdx.files.internal("shaders/mBlur.vsh"), Gdx.files.internal("shaders/mBlur.fsh"));
		outPrint("Blur Shader compiled: " +blurShader.isCompiled() +"\n"+blurShader.getLog());
	}

	public void enter() {
		
	}

	public void exit() {
		
	}

	public String getDebug() {
		return null;
	}

	public void pause() {
		
	}

	public void resume() {
		
	}

}
