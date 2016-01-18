package org.retorn.lifeinspace.entity;

import org.retorn.lifeinspace.engine.Entity;
import org.retorn.lifeinspace.engine.HardCollider;
import org.retorn.lifeinspace.engine.InputManager;
import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.Level;
import org.retorn.lifeinspace.engine.Shadow;
import org.retorn.lifeinspace.engine.ShadowProvider;
import org.retorn.lifeinspace.engine.Sol;
import org.retorn.lifeinspace.engine.Tween;
import org.retorn.lifeinspace.engine.WeakCollider;
import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Coin extends WeakCollider {
	private Music rollMusic;
	private TextureRegion front, rim;
	private Texture ref;
	private Shadow shadow;
	
	Vector3 projPos;
	
	private ShaderProgram refShader;
	
	private float maxV = 3.0f;
	private float accel = 10f;
	private float rollVol = 1f;
	
	private float refAlpha;
	private float refAlphaT = 0.5f;

	public boolean dragging;//If you are currently dragging. Used to let you drag outside the edge-buffer if you already are.
	
	public static boolean dead;
	
	public boolean canPickUp;
	public boolean carrying;
	public Pickup heldEnt;
	public Pickup potPickup;//PotEnt is potentialEnt ie one that you're in front of/highlighting.
	public Inter potInter;
	
	public static float standardHeight = -100;
	public static float standardX = 0;
	public static Sol standardSol;
	
	private float rot;
	private float rotV;//Amount added to rot each frame.
	public int st;
	
	public final int ONGROUND = 0;
	public final int INAIR = 1;
	public final int GRAVLESS = 2;
	
	public final float drawSize = 128;

	public Coin(String n, float x, float y, float z) {
		super(n, 119, 113, 18, x, y, z, 1);
	}

	public void render(Level lvl) {
		useShader(lvl);
		drawCoin();
		LM.batch.setShader(null);
	}

	public void tick(Level lvl) {
		if(pos.y < standardHeight-1000) die();
		managePickups(lvl);
		
		if(st == ONGROUND){
			manageDeathPenalty(lvl);
			manageInput(lvl);
			if(InputManager.deltaDrag.y > 35*Main.resFac.y)  jump();
			else if(!onGround) st = INAIR;
			applyGrav(lvl);
			rollVol = Math.abs(v.x)/1000f;
			if(rollVol > 2.2f) rollVol = 2.2f;
		}
		
		else if(st == INAIR){
			if(v.y < 0) weight += Tween.tween(weight, 320, 3); //Tween up weight if you're falling
			manageInput(lvl);
			if(onGround) land(lvl);
			applyGrav(lvl);
			if(v.y < -25000) v.y = -25000;
			rollVol = 0f;
		}
		
		else if(st == GRAVLESS){
			manageGravlessInput(lvl);
		}
		
		refAlpha += Tween.tween(refAlpha, refAlphaT, 3f);
		rollMusic.setVolume(rollVol);
		manageRot();
		provideShadow(lvl);
		
	}
	
	private void manageDeathPenalty(Level lvl){
		if(dead){//Kill a water-piece.
			if(heldEnt != null){
				if(heldEnt instanceof Pot){
					if(((Pot)heldEnt).plunt != null){
						if(((Pot)heldEnt).plunt.st != 1){
						float loops = ((Pot)heldEnt).plunt.worth*0.1f + (LM.dice.nextInt(10) == 0 && ((Pot)heldEnt).plunt.worth != 0 ? 1f : 0f);
						for(int i = 0; i < loops; i++){
						if(((Pot)heldEnt).plunt.worth > 0){
							if(LM.dice.nextInt(30) != 0) ((Pot)heldEnt).plunt.worth--;
							lvl.addEnt(new WaterPiece(
									"wp"+LM.dice.nextFloat(), 
									getCenterPos().x-30, 
									lvl.entity("coin").pos.y,
									0, 
									lvl.entity("coin").v.x*0.8f+200*LM.dice.nextFloat(), 3000 + 300*i, 0));
							Main.breakSound.play(0.5f, 1f, 0.5f);
						}
						}
						}
					}
				}
			}
			dead = false;
			Main.clickSound.play(0.5f, 1f, 0.5f);
		}
	}
	
	public void gainWP(){
		if(heldEnt != null)
			if(heldEnt instanceof Pot)
				((Pot)heldEnt).water();
			
		Main.breakSound.play(0.5f,1.5f, 0.5f);
	}
	
	private void die(){
		pos.y = standardHeight + 2000;
		dead = true;
		//Destroy any item you might be holding?
		//Decrease the growth of a plant you're holding?
	}
	
	private void pickUp(Pickup p, Level lvl){
		heldEnt = p;
		carrying = true;
		p.pickup(lvl);
	}
	
	public void drop(Level lvl){
		heldEnt.drop(lvl);
		heldEnt.v.y = v.y;
		heldEnt = null;
		carrying = false;
		lvl.getCam().setShake(7, 60, 50);
	}
	
	public void cleanse(Level lvl){
		heldEnt.drop(lvl);
		heldEnt = null;
		carrying = false;
	}
	
	private void swap(Pickup p1, Level lvl){
		drop(lvl);
		pickUp(p1, lvl);
	}
	
	private void managePickups(Level lvl){
		canPickUp = false;
		potPickup = null;
		potInter = null;
		
		float maxOverlap = 35;
		
		//FIND POT-PICKUP & POT-INTER
		for(Entity e : lvl.eList.values()){
			if(e instanceof Inter){
				//Check overlap
				Rectangle r1 = new Rectangle(e.pos.x, e.pos.y+e.pos.z, e.dim.x, e.dim.y+e.dim.z);
				Rectangle r2 = new Rectangle(pos.x, pos.y+pos.z, dim.x, dim.y+dim.z);
				if(r1.overlaps(r2)){
					//If the overlap is higher than the current max, make this the pickUpCandidate
					float xOverlap = Math.min(r1.x+r1.width, r2.x+r2.width) - Math.max(r1.x, r2.x);
					if(xOverlap > maxOverlap && xOverlap > 35 && heldEnt != e){
						potInter = (Inter) e;
						potPickup = (Pickup) e;
						maxOverlap = xOverlap;
					}
				}
			}
		}
		
		//IF YOU HAVE A POT PICKUP
		if(potPickup != null) canPickUp = true;
			
		if(ButtonUp.pressed){
			//Manage pick-up/drop
			//Pick up | Have only potPickup
				if(potPickup != null && heldEnt == null) pickUp(potPickup, lvl);
			//Drop | Have a heldEnt & nothing else
				else if(heldEnt != null && potInter == null && potPickup == null) drop(lvl);
			//Swap | Have heldEnt & potPickup
			else if(potPickup != null && heldEnt != null)
					if(!potPickup.interact(heldEnt, lvl)) swap(potPickup, lvl);
			//Interact & Drop if you can't | Have heldEnt & potInter
				else if(potInter != null && heldEnt != null)
					if(!potInter.interact(heldEnt, lvl)) drop(lvl);
			}
		
		//If there's nothing to be picked up but you have an entity & hit the button.
		else if(ButtonUp.pressed && heldEnt != null) drop(lvl);
	}
	
	private void manageInput(Level lvl){
		if(InputManager.upLMB)
			dragging = false;
		
		float x = InputManager.M.x/Main.resFac.x;
		boolean withinBound =( x > 50 && x < LM.WIDTH-50);

		if(InputManager.downLMB && !(!withinBound && !dragging)){
			dragging = true;
			float wMX =(InputManager.getWorldMouse(lvl.getCam()).x-getCenterPos().x)*maxV;
			
			v.x += Tween.tween(v.x, wMX, accel * (!onGround ? 0.1f : 1f));
		}
		
		else v.x += Tween.tween(v.x, 0, accel*0.8f * (!onGround ? 0.2f : 1f));//If mouse is behind
	}
	
	private void manageGravlessInput(Level lvl){
		if(InputManager.downLMB){ 
			float wMX = (InputManager.getWorldMouse(lvl.getCam()).x-getCenterPos().x)*maxV;
			float wMY = (InputManager.getWorldMouse(lvl.getCam()).y-getCenterPos().y)*maxV;
			
			v.x += Tween.tween(v.x, wMX, accel*0.05f);
			v.y += Tween.tween(v.y, wMY, accel*0.05f);
		}
		
		else{
			v.x += Tween.tween(v.x, 0f, accel*0.15f);
			v.y += Tween.tween(v.y, 0f, accel*0.15f);
		}
	}
	
	private void manageRot(){
		if(st == ONGROUND) rotV = -v.x * 0.024f;
		if(st == INAIR) rotV += Tween.tween(rotV, 0f, 1f);
		if(st == GRAVLESS){
				rotV += Tween.tween(rotV, Math.abs(v.x) > 1500 ? -v.x*0.001f : 0f,  1f);
		}
		
		rot += rotV;
	}
	
	
	
	@Override
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		if(normal.y == -1 && b.name.contains("stock")){
			standardHeight = b.pos.y + b.dim.y - 200;
			standardSol = (Sol)b;
			standardX = pos.x;
		}
		
		if(normal.x != 0 && b instanceof HardCollider && st == INAIR){
			v.x *= -0.25f;//Bounce
			v.y += Math.abs(v.x)*0.9f;
			Main.landSound.play(LM.gameSoundEffectVolume*0.5f*Math.abs(v.x/4000f), 1.0f+LM.dice.nextFloat()*0.1f, 0.5f);
		}
		else if(st == GRAVLESS && b instanceof HardCollider){
			if(normal.y != 0){
				v.y *= -0.3f;
				rotV = -v.x*0.024f;
			}
			if(normal.x != 0){
				v.x *= -0.3f;
				rotV = -v.y*0.024f*normal.x;
			}
		}
		else super.collide(b, collisionTime, normal, lvl);
	}

	private void jump(){
		v.y = 2250;
		onGround =  false;
		jumped = true;
		st = INAIR;
		Main.jumpSound.play(LM.gameSoundEffectVolume*0.2f, 1.3f+LM.dice.nextFloat()*0.2f, 0.5f);
		Main.landSound.play(LM.gameSoundEffectVolume*0.1f, 1.3f+LM.dice.nextFloat()*0.1f, 0.5f);
	}
	
	private void land(Level lvl){
		weight = 120;//Reset weight after it was tweened up in the air.
		st = ONGROUND;
		if(!dead) lvl.getCam().setShake(18f*lvl.getCam().zoom, 100f, 70f*lvl.getCam().zoom);
		else  lvl.getCam().setShake(22f*lvl.getCam().zoom, 50f, 70f*lvl.getCam().zoom);
		if(Main.inc > 1f)Main.landSound.play(LM.gameSoundEffectVolume*0.1f, 1.15f+LM.dice.nextFloat()*0.1f, 0.5f);
		refAlpha = 3f;
		Main.blurFast(0.3f);
		
		//Make pots bounce
		/*for(Entity e: lvl.eList.values()){
			if(e instanceof Pot) e.v.y += 2000f - e.getCenterPos().dst(getCenterPos());
		}*/
	}
	
	private void drawCoin(){
		if(st == ONGROUND || st == INAIR || st == GRAVLESS){
			LM.batch.draw(
					rim, 
					getCenterPos().x-drawSize/2f, 
					getCenterPos().y+pos.z-drawSize/2f + 15, 
					drawSize/2f, drawSize/2f, drawSize, drawSize,
					1f, 1f, rot*0.5f);
			LM.batch.draw(
					front, 
					getCenterPos().x-drawSize/2f, 
					getCenterPos().y+pos.z-drawSize/2f + 2, 
					drawSize/2f, drawSize/2f, drawSize, drawSize,
					1f, 1f, rot*0.5f);
		}
	}
	
	public void superRender(Level lvl) {

	}

	private void useShader(Level lvl){
		LM.batch.setShader(refShader);
		ref.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		refShader.setUniformi("u_texture1", 1);
		refShader.setUniformf("alpha", refAlpha);
		refShader.setUniformf("time", -Main.inc*0.3f);
		refShader.setUniformf("u_size", LM.WIDTH, LM.HEIGHT);
		refShader.setUniformf("u_screensize", LM.WIDTH*lvl.getCam().zoom, LM.HEIGHT*lvl.getCam().zoom);
		refShader.setUniformf("xdis", 0);
		refShader.setUniformf("ydis", Main.inc*0.005f);
	}

	public void init(Level lvl) {
		weight = 120;
		
		LM.loadTexture("img/coin_face_front.png");
		LM.loadTexture("img/coin_rim.png");
		LM.loadTexture("img/coin_shad.png");
		LM.loadTexture("img/cloudtex.png");
		
		LM.loadMusic("audio/coin_roll2.ogg");
		
		refShader = new ShaderProgram(Gdx.files.internal("shaders/coin.vsh"), Gdx.files.internal("shaders/coin.fsh"));
		outPrint("REF SHADER COMPILED: "+refShader.isCompiled() +refShader.getLog());
		refShader.pedantic = false;
		
		projPos = new Vector3();
	}

	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded("img/coin_face_front.png")
		   && LM.loader.isLoaded("img/coin_rim.png")
		   && LM.loader.isLoaded("img/coin_shad.png")
		   && LM.loader.isLoaded("img/cloudtex.png")
		   && LM.loader.isLoaded("audio/coin_roll2.ogg")){
			
			front = new TextureRegion(LM.loader.get("img/coin_face_front.png", Texture.class));
			rim = new TextureRegion(LM.loader.get("img/coin_rim.png", Texture.class));
			
			ref = LM.loader.get("img/cloudtex.png", Texture.class);
			ref.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			
			shadow = new Shadow(LM.loader.get("img/coin_shad.png", Texture.class), 0f, 0f);
			
			rollMusic = LM.loader.get("audio/coin_roll2.ogg", Music.class);
			rollMusic.setLooping(true);
			rollMusic.play();
			
			onGround = true;//Assures you don't fast-switch to INAIR & back.
			
			return true;
		}
		
		return false;
	}
	
	private void provideShadow(Level lvl){
		shadow.setSize(300, 55);
		shadow.setPosition(pos.x+v.x*LM.endTime-88, pos.z+v.z*LM.endTime-16);
		lvl.addEnt(new ShadowProvider(name+"_SP", dim.x+100, dim.z+100, pos.x-50+v.x*LM.endTime, pos.y+dim.y, pos.z-50+v.z*LM.endTime, shadow));
	}

	public void dispose() {
		
	}

	public String getDebug() {
		return "weight: "+weight
				+"\nPotential Pickup: "+(potPickup != null ? potPickup.name : "NULL")
				+"\nPotential Pickup: "+(potInter != null ? potInter.getName() : "NULL")
				+"\nHeld Ent: "+(heldEnt != null ? heldEnt.name : "NULL")
				+"\nStandard Height: "+standardHeight
				+"\nDead: "+dead;
	}

}
