package org.retorn.lifeinspace.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;

public abstract class Level{
	public boolean inited;
	public static boolean debug;
	
	public String name;
	public String camIndex;
	public String debugIndex = "";//Debug index.
	
	//Lists of Entities and Cameras. The String is their associated name.
	//They are LinkedHashMaps because that means they have a guaranteed order to them, which hash-maps do not.
	public LinkedHashMap<String, Entity> eList;
	public LinkedHashMap<String, Entity> pendingEList;
	public LinkedHashMap<String, Camera> camList;
	
	private static Box box1, box2;
	
	private ArrayList<Entity> movingC;
	private ArrayList<Entity> staticC;
	private ArrayList<Entity> collectiveC;
	private ArrayList<Entity> basicC;
	
	private LinkedList<Entity> masterList;//Everything that is relevant/must render (unless covered up by something else entirely? Probably not?)
	private LinkedList<Entity> sortedZList;
	private LinkedList<Entity> zTakeable;
	private LinkedList<Entity> yTakeable;
	private LinkedList<Entity> sortedYList;
	protected LinkedList<Entity> sortedList;
	
	LinkedList<Entity> renderables;
	
	//private int st;//Available for other levels' tick methods.
	
	public float gravity;
	
	protected float normalx;
	protected float normaly;
	protected float normalz;
	
	private static GlyphLayout gLay;//Used for debugText.
	
	public Color bg;//Color screen will clear to/background colour.
	
	public Level(Color c, String n, float grav){
		bg = c;
		name = n;
		gravity = grav;
		
		camIndex = "default";
		
		eList = new LinkedHashMap<String, Entity>();
		pendingEList = new LinkedHashMap<String, Entity>();
		camList = new LinkedHashMap<String, Camera>(1);
	}
	
	public void setup(){
		movingC = new ArrayList<Entity>();
		staticC = new ArrayList<Entity>();
		collectiveC = new ArrayList<Entity>();
		basicC = new ArrayList<Entity>();
		
		masterList = new LinkedList<Entity>();
		sortedZList = new LinkedList<Entity>();
		zTakeable = new LinkedList<Entity>();
		sortedYList = new LinkedList<Entity>();
		yTakeable = new LinkedList<Entity>();
		sortedList = new LinkedList<Entity>();
		
		renderables = new LinkedList<Entity>();
		
		box1 = new Box(0,0,0,0,0,0);
		box2 = new Box(0,0,0,0,0,0);
		
		camList.put("default", new Camera(LM.WIDTH, LM.HEIGHT));
		camList.get("default").pos.set(LM.WIDTH/2, LM.HEIGHT/2);
		camList.get("default").update();
		
		if(gLay == null) gLay = new GlyphLayout();
	}
	
	public abstract void init();
	public abstract void postLoad(); //Assign variables after the Loader has loaded the level's stuff i.e. loader.update() returns true.
										//This won't be called until after all the level's initial entities are loaded, because it is held behind loader.update().
										//It might be better to just have entities pop in + have virtually no loading time. Maybe not. Loading time will probably be negligible anyways.
	public abstract void render();
	public abstract void tick();
	public abstract void enter();
	public abstract void exit();
	public abstract String getDebug();
	public abstract void pause();
	public abstract void resume();
	public void resize(int w, int h){}
	
	public void tickEnts(){
		if(!pendingEList.isEmpty()) addPendingEnts();

		movingC.clear();
		staticC.clear();
		collectiveC.clear();
		basicC.clear();
		//Checking if any entities are removed.
		if(!eList.isEmpty()){
			Iterator<Entity> it = eList.values().iterator();
			Entity qq;
			while(it.hasNext()){
				qq = (Entity)it.next();
				if(qq.removed){
					if(debugIndex.equals(qq.name))//Resetting debugIndex if it was removed.
						debugIndex = "";
					it.remove();}
				else//Removing added velocity from last frame.
					qq.removeVa();
			}
			
			//Entities are ticked here. This means velocities are modified.
			for(Entity e: eList.values()){e.tick(this);}
			
			//Adding any entities added during the tick. They will not be ticked until next frame.
			//But they will be included in the collision check. This is useful for one time entities with remove() in their ticks.
			addPendingEnts();
			//Removes anything that had remove() called during it's tick/another thing's tick.
			it = eList.values().iterator();
			while(it.hasNext()){
				qq = (Entity)it.next();
				if(qq.removed){
					if(debugIndex.equals(qq.name))//Resetting debugIndex if it was removed.
						debugIndex = "";
					it.remove();}
				else if(qq.colliding){//Adding entities to their collision list if they weren't removed.
					if(qq.cType == 0)//world statics
						staticC.add(qq);
					
					else if(qq.cType == 1)//moving
						movingC.add(qq);
					
					else if(qq.cType == 3)//collectives
						collectiveC.add(qq);
					
					else if(qq.cType == 4)//AABB/basic
						basicC.add(qq);
				}	
			}
			
			//Tweens are ticked, velocities are further modified.
			for(Entity e: eList.values()){
				e.flushVBuffer();
				e.tickTweens();
				//e.applyGameSpeed();
			}
						
			for(Entity e: eList.values()){if(e.hosting)e.setChildVelocities();}
						
			//Entities are checked for collisions here. Their velocities are corrected if need be.
			for(int i = 0; i < movingC.size(); i++){
				//Comparison of moving ents.
				for(int w = i+1; w < movingC.size(); w++){
					if(!checkNoCollides(movingC.get(i), movingC.get(w))){
					movingC.get(i).applyGameSpeed(); movingC.get(w).applyGameSpeed();
					float collTime = sweptAABB(movingC.get(i), movingC.get(w));
					movingC.get(i).removeGameSpeed(); movingC.get(w).removeGameSpeed();
					movingC.get(i).collide(movingC.get(w), collTime, new Vector3(normalx, normaly, normalz), this);
					movingC.get(w).collide(movingC.get(i), collTime, new Vector3(-normalx, -normaly, -normalz), this);
					}}
				//Comparison of moving ents and collective ents.
				for(Entity collectiveEnt: collectiveC){
					if(!checkNoCollides(movingC.get(i), collectiveEnt)){
					movingC.get(i).applyGameSpeed(); collectiveEnt.applyGameSpeed();
					float collTime = sweptAABB(movingC.get(i), collectiveEnt);
					movingC.get(i).removeGameSpeed(); collectiveEnt.removeGameSpeed();
					movingC.get(i).collide(collectiveEnt, collTime, new Vector3(normalx, normaly, normalz), this);
					collectiveEnt.collide(movingC.get(i), collTime, new Vector3(-normalx, -normaly, -normalz), this);	
					}}
				//Comparison of moving ents and world statics.
				for(Entity staticEnt: staticC){
					if(!checkNoCollides(movingC.get(i), staticEnt)){
					movingC.get(i).applyGameSpeed(); staticEnt.applyGameSpeed();
					float collTime = sweptAABB(movingC.get(i), staticEnt);
					movingC.get(i).removeGameSpeed(); staticEnt.removeGameSpeed();
					//if(collTime != 1f && movingC.get(i) instanceof WeakCollider){outPrint(movingC.get(i).name +" collides with "+staticEnt.name +" normals: \nX:"+normalx+" Y:"+normaly+" Z:"+normalz);}
					movingC.get(i).collide(staticEnt, collTime, new Vector3(normalx, normaly, normalz), this);
					staticEnt.collide(movingC.get(i), collTime, new Vector3(-normalx, -normaly, -normalz), this);
					}}
				
			}
			//Comparing collectives to world statics.
			for(Entity e: collectiveC){
				for(Entity staticEnt: staticC){
					if(!checkNoCollides(e, staticEnt)){
					e.applyGameSpeed(); staticEnt.applyGameSpeed();
					float collTime = sweptAABB(e, staticEnt);
					e.removeGameSpeed(); staticEnt.removeGameSpeed();
					e.collide(staticEnt, collTime, new Vector3(normalx, normaly, normalz), this);
					staticEnt.collide(e, collTime, new Vector3(-normalx, -normaly, -normalz), this);
				}}
			}
			
			//Comparing basics to moving + world statics.
			for(Entity e: basicC){
				for(Entity staticEnt: staticC){
					if(!checkNoCollides(e, staticEnt)){
					e.applyGameSpeed(); staticEnt.applyGameSpeed();
					float q = AABB(e, staticEnt) ? -2 : 0;
					e.removeGameSpeed(); staticEnt.removeGameSpeed();
					e.collide(staticEnt, 1f, new Vector3(q, 0, 0), this);
					staticEnt.collide(e, 1f, new Vector3(q, 0, 0), this);
				}}
				
				for(Entity movingEnt: movingC){
					if(!checkNoCollides(e, movingEnt)){
					e.applyGameSpeed(); movingEnt.applyGameSpeed();
					float q = AABB(e, movingEnt) ? -2 : 0;
					e.removeGameSpeed(); movingEnt.removeGameSpeed();
					e.collide(movingEnt, 1f, new Vector3(q, 0, 0), this);
					movingEnt.collide(e, 1f, new Vector3(q, 0, 0), this);
				}}
				
				for(Entity collectiveEnt: collectiveC){
					if(!checkNoCollides(e, collectiveEnt)){
					e.applyGameSpeed(); collectiveEnt.applyGameSpeed();
					float q = AABB(e, collectiveEnt) ? -2 : 0;
					e.removeGameSpeed(); collectiveEnt.removeGameSpeed();
					e.collide(collectiveEnt, 1f, new Vector3(q, 0, 0), this);
					collectiveEnt.collide(e, 1f, new Vector3(q, 0, 0), this);
				}}
				
			}
			//Apply gamespeed after collision.
			for(Entity e: eList.values()){
				e.applyGameSpeed();}
			//Setting everything in the parent-child tree to the lowest velocities after collision.
			for(Entity e: eList.values()){
				if(e.hosting)e.resolveChildVelocities();}
			
			//Entities are moved along their (possibly modified) velocities.
			for(Entity e: eList.values()){e.tickMovement();}
			for(Entity e: eList.values()){e.removeGameSpeed();}
			
		}
		
		addPendingEnts();
	}
	
	//Checks if these two things should collide based on their collide-lists.
	public boolean checkNoCollides(Entity a, Entity b){
		for(String s: a.ncList){
			if(b.ncList.contains(s)) return true;
		}
		return false;
	}
	
	public boolean checkSpecificCollides(Entity a, Entity b){
		for(String s: a.scList){
			if(b.scList.contains(s)) return true;
		}
		return false;
	}
	
	public void renderEnts(){
		if(!eList.isEmpty()){
			//Making list of correctly sorted renderables.
			renderables = getSortedRenderables();
			//This Level's superRender post-ent-super-render & pre-ent-regular-render.
			superRender();
			//Debug-Click
			String debugEnt = "";
			for(Entity e: renderables){//View Frustum culling.
				e.render(this);
				
				//If this entity was clicked, and is the closest to the screen, set it to the debug ent. High positions in renderables take precedence over lower ones.
				if(InputManager.pressedLMB && LM.debug){
						if(e.getRect(getCam()).contains(Gdx.input.getX(), LM.HEIGHT-Gdx.input.getY())){
							if(debugIndex.equals(e.name)){debugIndex = "";}
							else
								debugEnt = e.name;
						}
				}
			}
			//If a new entity was selected 
			if(LM.debug && !debugEnt.equals("")){debugIndex = debugEnt; outPrint("debugIndex:"+debugIndex);}
			//
			if(InputManager.pressedF2)
				debugIndex = "";
			//DRAWING DEBUG INFORMATION.
			if(LM.debug){
				
			}
		renderables.clear();
		}
		
	}
	
	/*public LinkedList<Entity> getSortedRenderables(){
		masterList.clear();//Everything that is relevant/must render (unless covered up by something else entirely? Probably not?)
		sortedZList.clear();
		sortedYList.clear();
		sortedList.clear();
		
		//Adding only entities that will render/in frustum+are rendering to the masterList.
		LM.batch.end();//Ending the batch so it can be opened in the individual superRenders.
		for(Entity e: eList.values()){
			if(e.rendering &&
			isEntityWithinViewFrustum(e))
				masterList.add(e);
			if(e.superRender) e.superRender(this);
		}
		
		LM.batch.begin();
		//Creating Sorted Z List.
		zTakeable.clear();//For sortedZList to cannibalize.
		zTakeable.addAll(masterList);
		while(sortedZList.size() != masterList.size()){
			Entity lo;//Lowest z-entity, nigga. get on my lev.
			lo = zTakeable.get(0);
			for(Entity e: zTakeable){
				if(!e.equals(lo)){
					if(e.pos.z >= lo.pos.z + lo.dim.z)//Lo is definitively in front of this new entity.
						lo = e;//The new entity is the new lo.
					else if(e.dim.y < lo.dim.y && (e.pos.z <= lo.pos.z + lo.dim.z) && e.pos.y < lo.pos.y+lo.dim.y && e.pos.z + e.dim.z > lo.pos.z)
						lo = e;
				}
			}
			sortedZList.add(lo);
			zTakeable.remove(lo);
		}
		
		//Sorted Z List is complete. Let's make sorted Y List now.
		yTakeable.clear();
		yTakeable.addAll(masterList);
		while(sortedYList.size() != masterList.size()){
			Entity lo;//Lowest z-entity, nigga. get on my lev.
			lo = yTakeable.get(0);
			for(Entity e: yTakeable){
				if(!e.equals(lo)){
					if(e.pos.y + e.dim.y <= lo.pos.y)//Lo is definitively underneath this new entity.
						lo = e;//The new entity is the new lo.
				}
			}
			sortedYList.add(lo);
			yTakeable.remove(lo);
		}
		
		//SORTING OF GODS WITH BOTH LISTS.
		while(sortedList.size() != masterList.size()){
		Entity loGod = sortedZList.get(0);
				//If e is the lowest on both lists, it is definitely the loGod.
				if(sortedYList.get(0).equals(loGod)){
					
				}
				//If e is higher on one list than another
				else{
					Entity forComp = loGod;
					int loGodIndex = sortedYList.indexOf(loGod);
					for(int i = loGodIndex-1; i >= 0; i--){
						Entity lowerY = sortedYList.get(i); //outPrint(forComp.name +"/" +lowerY.name);
						if(lowerY.pos.z >= forComp.pos.z + forComp.dim.z && forComp.pos.y + forComp.dim.y >= lowerY.pos.y && forComp.pos.y <= lowerY.pos.y + lowerY.dim.y ){
							//outPrint(forComp.name +" was in front of "+lowerY.name); 
							forComp = lowerY;}
						//If this entity is lower, and the forComp is not completely behind it, it is considered lower.
						if(lowerY.pos.y + lowerY.dim.y <= forComp.pos.y && forComp.pos.z + forComp.dim.z >= lowerY.pos.z && forComp.pos.z <= lowerY.pos.z + lowerY.dim.z){
							//outPrint(forComp.name +" was on top of "+lowerY.name);
							forComp = lowerY;}
					}
					loGod = forComp; //outPrint("loGod is now "+forComp.name +" which has been added to sortedList.");
					
					forComp = loGod;
					loGodIndex = sortedZList.indexOf(loGod);
					for(int i = loGodIndex-1; i >= 0; i--){
						Entity lowerY = sortedZList.get(i);// outPrint(forComp.name +"/" +lowerY.name);
						if(lowerY.pos.z >= forComp.pos.z + forComp.dim.z && forComp.pos.y + forComp.dim.y >= lowerY.pos.y && forComp.pos.y <= lowerY.pos.y + lowerY.dim.y ){
							//outPrint(forComp.name +" was in front of "+lowerY.name); 
							forComp = lowerY;}
					}
					loGod = forComp; //outPrint("loGod is now "+forComp.name +" which has been added to sortedList.");
					}
		sortedList.add(loGod);
		//finalList = "new sorted:"; for(Entity e: sortedList){finalList += e.name+" ";}
		//outPrint(finalList);
		sortedZList.remove(loGod);
		sortedYList.remove(loGod);
		}
		sortedZList.clear();
		sortedYList.clear();
		
		//String finalList = "SORTED:"; for(Entity e: sortedList){finalList += e.name+" ";}
		//outPrint(finalList+"\n");
		return sortedList;
	}*/
	
	public LinkedList<Entity> getSortedRenderables(){
		masterList.clear();//Everything that is relevant/must render (unless covered up by something else entirely? Probably not?)
		
		//Adding only entities that will render/in frustum+are rendering to the masterList.
		LM.batch.end();//Ending the batch so it can be opened in the individual superRenders.
		for(Entity e: eList.values()){
			if(e.rendering &&
			isEntityWithinViewFrustum(e))
				masterList.add(e);
			if(e.superRender) e.superRender(this);
		}
		
		try{Collections.sort(masterList, new entSorter());}
		catch(IllegalArgumentException e){outPrint("The Render Order Fucked Itself.");}
		
		LM.batch.begin();
		
		return masterList;
	}
	
	private class entSorter implements Comparator<Entity>{

		public int compare(Entity a, Entity b) {

			if(b.pos.y + b.dim.y <= a.pos.y) return 1;
			if(a.pos.z >= b.pos.z + b.dim.z) return -1;
			if(a.pos.y + a.dim.y <= b.pos.y) return -1;
			if(b.pos.z >= a.pos.z + a.dim.z) return 1;
			
			
			Vector3 cPos = new Vector3(0, 10000, 10000);
			Vector3 ap = new Vector3(a.pos.x+a.dim.x/2f, a.pos.y+a.dim.y, a.pos.z);
			Vector3 bp = new Vector3(b.pos.x+b.dim.x/2f, b.pos.y+b.dim.y, b.pos.z);
			
			if(cPos.y-(a.pos.y+a.dim.y) < cPos.y-(b.pos.y+b.dim.y)
			 &&cPos.z - (a.pos.z+a.dim.z) > cPos.y-(b.pos.z) 
					) return 1;

			if(cPos.y-(a.pos.y+a.dim.y) > cPos.y-(b.pos.y+b.dim.y)
			 &&cPos.z - (a.pos.z+a.dim.z) < cPos.y-(b.pos.z) ) return -1;

			if(cPos.dst(ap) > cPos.dst(bp))
				return -1;
			if(cPos.dst(bp) < cPos.dst(bp))
				return 1;
			
			if(a.getCenterPos().y > b.getCenterPos().y) return 1;
			if(b.getCenterPos().y > a.getCenterPos().y) return -1;
			
			if(b.getCenterPos().z > a.getCenterPos().z) return 1;
			if(a.getCenterPos().z > b.getCenterPos().z) return -1;
			
			return 0;
		}
		
	}
	
	/*
	public LinkedList<Entity> getSortedRenderables(){
		sortedList.clear();
		for(Entity e: eList.values()){
			if(e.rendering &&
			camList.get(camIndex).frustum.boundsInFrustum(new Vector3(e.getCenterPos().x, e.getCenterPos().y+e.getCenterPos().z, 0), new Vector3(e.dim.x, e.dim.y + e.dim.z, 0)))
				sortedList.add(e);
		}
		Collections.sort(sortedList, new entSorter());
		return sortedList;
	}
	
	private class entSorter implements Comparator<Entity>{

		@Override
		public int compare(Entity a, Entity b) {
			//Is something directly in front/behind something else?
			if(a.pos.z >= b.pos.z+b.dim.z) return -1;
			else if(a.pos.y+a.dim.y <= b.pos.y) return -1;
			else if(a.pos.z + a.dim.z <= b.pos.z) return 1;
			else if(a.pos.y >= b.pos.y + b.dim.y) return 1;
			//The two things are beside each other.
			else if(a.pos.y+a.dim.y <= b.pos.y+b.dim.y) return -1;
			else if(a.pos.z >= b.pos.z) return -1;
			else if(a.pos.y+a.dim.y >= b.pos.y+b.dim.y) return 1;
			else if(a.pos.z <= b.pos.z) return 1;
				
			return 0;
		}
		
	}*/
	
	public boolean isEntityWithinViewFrustum(Entity e){
		if(camList.get(camIndex).frustum.boundsInFrustum(new Vector3(e.getCenterPos().x, e.getCenterPos().y+e.getCenterPos().z, 0), new Vector3(e.dim.x+20, e.dim.y + e.dim.z + 20, 0)))
			return true;
		else return false;
	}

	//The IDs are basically separate methods. 0 is shapes, 1 text.
	public void renderDebug(int id){
		//For use while shaper is open.
		if(LM.debug){
		if(id == 0 && !debugIndex.equals("")){
			//Setting up dimensions for debug-cube.
			
			Vector3 dbgPos = eList.get(debugIndex).pos;
			Vector3 dbgDim = eList.get(debugIndex).dim;
			LM.shaper.setColor(Color.BLUE);
			LM.shaper.rect(dbgPos.x, dbgPos.z+dbgPos.y+dbgDim.z, dbgDim.x, dbgDim.y);
			LM.shaper.setColor(Color.MAGENTA);
			LM.shaper.rect(dbgPos.x, dbgPos.y+dbgPos.z, dbgDim.x, dbgDim.y);
		}
		//While shaper is closed, called after ents are rendered.
		if(id == 1){
			//Drawing debug text. If debugIndex != "", it includes debugIndex's.
			String debugDrawText = !debugIndex.equals("") && eList.containsKey(debugIndex) ?
					"Debug Mode!"
					+"\nFPS: "+Gdx.graphics.getFramesPerSecond()
					+"\nGame Speed: "+LM.gameSpeed
					+"\nLevel: "+name 
					+"\nCurrent Cam: "+camIndex 
					+"\nCurrent Level Cam Zoom: "+getCam().zoom
					+ "\nCamX: "+getCam().pos.x
					+ "\nCamY: "+getCam().pos.y
					+"\n# of Entities: "+eList.size()
					//+"\nMemory Used: "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) 
					+"\n"+getDebug() 
					+"\n\nDebug Ent: "+eList.get(debugIndex).name 
					+"\nClass: "+eList.get(debugIndex).getClass().getSimpleName()
					+"\nPos: "+Math.round(eList.get(debugIndex).pos.x)+", "+Math.round(eList.get(debugIndex).pos.y)+", "+Math.round(eList.get(debugIndex).pos.z)
					+"\nDim: "+Math.round(eList.get(debugIndex).dim.x)+", "+Math.round(eList.get(debugIndex).dim.y)+", "+Math.round(eList.get(debugIndex).dim.z)
					+"\n"+eList.get(debugIndex).getDebug()
					:
					"Debug Mode!"
					+"\nFPS: "+Gdx.graphics.getFramesPerSecond()
					+"\nGame Speed: "+LM.gameSpeed
					+"\nLevel: "+name 
					+"\nCam In Use: " +(LM.usingDefaultCamera ? "default":"level")
					+"\nCurrent Cam: "+camIndex
					+"\nCurrent Level Cam Zoom: "+getCam().zoom
					+ "\nCamX: "+getCam().pos.x
					+ "\nCamY: "+getCam().pos.y
					+"\n# of Entities: "+eList.size()
					//+"\nMemory Used: "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) 
					+"\n"+getDebug();
			LM.useDefaultCamera();
			gLay.setText(LM.debugText, debugDrawText);
			LM.debugText.setColor(Color.MAGENTA);
			//LM.debugText.drawMultiLine(LM.batch, debugDrawText, -(LM.WIDTH-LM.WIDTH_OG)/2+5, LM.debugText.getMultiLineBounds(debugDrawText).height+5f-(LM.HEIGHT-LM.HEIGHT_OG)/2);
			LM.batch.setShader(LM.fontShader);
			LM.debugText.getData().scaleX = 0.65f;
			LM.debugText.getData().scaleY = 0.65f;
			LM.fontShader.setUniformf(LM.fontShader.getUniformLocation("alph"), 1f);
			LM.fontShader.setUniformf(LM.fontShader.getUniformLocation("scale"), LM.debugText.getScaleX()*0.5f);
			LM.debugText.draw(LM.batch, debugDrawText, 5, gLay.height);
			LM.batch.setShader(null);
			LM.useLevelCamera();
		}
	}
		
	
	}
	
	public void cycleCam(int dir){
		//DO THIS.
	}
	
	public Boolean AABB(Entity a, Entity b){
		if(	((a.pos.x >= b.pos.x + b.dim.x) || (a.pos.x + a.dim.x <= b.pos.x))) return false; //On either side.
		if(	((a.pos.y >= b.pos.y + b.dim.y) || (a.pos.y + a.dim.y <= b.pos.y))) return false;
		if(	((a.pos.z >= b.pos.z + b.dim.z) || (a.pos.z + a.dim.z <= b.pos.z))) return false;

		else 
			return true;
	}
	
	public float sweptAABB(Entity b1, Entity b2){
		normalx = 0f;
		normaly = 0f;
		normalz = 0f;
		
		Vector3 RelativeVelocity = new Vector3(((b1.v.x + b1.vr.x/2) - (b2.v.x + b2.vr.x/2))*LM.delta,((b1.v.y + b1.vr.y/2) - (b2.v.y + b2.vr.y/2))*LM.delta,((b1.v.z + b1.vr.z/2) - (b2.v.z + b2.vr.z/2))*LM.delta );//How fast the gap between them is closing.
		//BASIC TOUCHING-WITHOUT-ACCELERATION COLLISION.
		if((b1.pos.x+b1.dim.x == b2.pos.x && RelativeVelocity.x == 0)){normalx = 1f; return 0f;}
		if((b2.pos.x+b2.dim.x == b1.pos.x && RelativeVelocity.x == 0)){normalx = -1f; return 0f;}
		if((b1.pos.z+b1.dim.z == b2.pos.z && RelativeVelocity.z == 0)){normalz = 1f; return 0f;}
		if((b2.pos.z+b2.dim.z == b1.pos.z && RelativeVelocity.z == 0)){normalz = -1f; return 0f;}
		if((b1.pos.y+b1.dim.y == b2.pos.y && RelativeVelocity.y == 0)){normaly = 1f; return 0f;}
		if((b2.pos.y+b2.dim.y == b1.pos.y && RelativeVelocity.y == 0)){normaly = -1f; return 0f;}
		
		//If it is equal to zero, they are either moving at the same speed, or not moving at all.
		if(RelativeVelocity.isZero())return 1f;
		//If b1 is to the left of b2, and RelativeVelocity is positive, they are coming together.
		//If b1 is to the right of b2, and RelativeVelocity is negative, they are coming together.
		
		
		//If the select velocity is less than zero, the position is going to be b1.pos + b1.v, because b1.v is negative.
		//This causes the right-bottom-front origin to move left/down/back, and then dim will expand to encompass the original position. 
		box1.pos.x = b1.v.x < 0 ? b1.pos.x + b1.v.x*LM.delta - b1.vr.x/2*LM.delta : b1.pos.x - b1.vr.x/2*LM.delta;
		box1.pos.y = b1.v.y < 0 ? b1.pos.y + b1.v.y*LM.delta - b1.vr.y/2*LM.delta : b1.pos.y - b1.vr.y/2*LM.delta;
		box1.pos.z = b1.v.z < 0 ? b1.pos.z + b1.v.z*LM.delta - b1.vr.z/2*LM.delta : b1.pos.z - b1.vr.z/2*LM.delta;
		box1.dim.x =b1.dim.x + Math.abs(b1.v.x*LM.delta) + b1.vr.x*LM.delta;
		box1.dim.y =b1.dim.y + Math.abs(b1.v.y*LM.delta) + b1.vr.y*LM.delta;
		box1.dim.z =b1.dim.z + Math.abs(b1.v.z*LM.delta) + b1.vr.z*LM.delta;
		
		box2.pos.x = b2.v.x < 0 ? b2.pos.x + b2.v.x*LM.delta - b2.vr.x/2*LM.delta : b2.pos.x - b2.vr.x/2*LM.delta;
		box2.pos.y = b2.v.y < 0 ? b2.pos.y + b2.v.y*LM.delta - b2.vr.y/2*LM.delta : b2.pos.y - b2.vr.y/2*LM.delta;
		box2.pos.z = b2.v.z < 0 ? b2.pos.z + b2.v.z*LM.delta - b2.vr.z/2*LM.delta : b2.pos.z - b2.vr.z/2*LM.delta;
		box2.dim.x =b2.dim.x + Math.abs(b2.v.x*LM.delta) + b2.vr.x*LM.delta;
		box2.dim.y =b2.dim.y + Math.abs(b2.v.y*LM.delta) + b2.vr.y*LM.delta;
		box2.dim.z =b2.dim.z + Math.abs(b2.v.z*LM.delta) + b2.vr.z*LM.delta;
		
		//Debugging thing. Draws broadphase boxes.
		/*
		LevelManager.shaper.setColor(Color.GREEN);
		LevelManager.shaper.rect(box1.pos.x, box1.pos.y+box1.pos.z, box1.dim.x, box1.dim.y);
		LevelManager.shaper.setColor(Color.YELLOW);
		LevelManager.shaper.rect(box1.pos.x, box1.pos.z+box1.pos.y+box1.dim.y, box1.dim.x, box1.dim.z);
		
		LevelManager.shaper.setColor(Color.NAVY);
		LevelManager.shaper.rect(box2.pos.x, box2.pos.y+box2.pos.z, box2.dim.x, box2.dim.y);
		LevelManager.shaper.setColor(Color.BLUE);
		LevelManager.shaper.rect(box2.pos.x, box2.pos.z+box2.pos.y+box2.dim.y, box2.dim.x, box2.dim.z);
		*/
		
		if(!AABB(box1, box2))
			return 1f;
		//X
		float xClose;
		float xFar;
		//Entity b1 is on the LEFT of b2.
		if(b1.getCenterPos().x < b2.getCenterPos().x){
			xClose = b2.pos.x - (b1.pos.x+b1.dim.x);//[b1{-}b2]
			xFar = (b2.pos.x + b2.dim.x) - b1.pos.x;//{b1]--[b2}
		}
		//Entity b1 is on the RIGHT of b2.
		else{
			xClose = b1.pos.x - (b2.pos.x + b2.dim.x);//[b2{-}b1]
			xFar = (b1.pos.x + b1.dim.x) - b2.pos.x;//{b2]--[b1}
		}
		
		//outPrint("xClose: "+xClose +" xFar:"+xFar +" Relative Velocity:"+RelativeVelocity.x);
		
		float xCloseTime;//TimeThroughFrameWhenVelocitiesWouldCoverThexClose;
		float xFarTime;//Time through frame necessary to cover the xFar distance.
		
		if(RelativeVelocity.x == 0){
			xCloseTime = -Float.MAX_VALUE;
			xFarTime = Float.MAX_VALUE;
		}
		else{
		xCloseTime = xClose/RelativeVelocity.x;//Percentage of xRelativeVelocity required to cover xClose.
		xFarTime = xFar/RelativeVelocity.x;//Same thing, but required to cover xFar.
		xCloseTime = Math.abs(xCloseTime);}
		
		//Z
		float zClose;
		float zFar;
		//Entity b1 is BEHIND b2.
		if(b1.getCenterPos().z < b2.getCenterPos().z){
			zClose = b2.pos.z - (b1.pos.z+b1.dim.z);//[b1{-}b2]
			zFar = (b2.pos.z + b2.dim.z) - b1.pos.z;//{b1]--[b2}
		}
		//Entity b1 is IN FRONT of b2.
		else{
			zClose = b1.pos.z - (b2.pos.z + b2.dim.z);//[b2{-}b1]
			zFar = (b1.pos.z + b1.dim.z) - b2.pos.z;//{b2]--[b1}
		}
		
		float zCloseTime;//TimeThroughFrameWhenVelocitiesWouldCoverThezClose;
		float zFarTime;//Time through frame necessary to cover the zFar distance.
		
		if(RelativeVelocity.z == 0){
			zCloseTime = -Float.MAX_VALUE;
			zFarTime = Float.MAX_VALUE;
		}
		else{
		zCloseTime = zClose/RelativeVelocity.z;//Percentage of zRelativeVelocity required to cover zClose.
		zFarTime = zFar/RelativeVelocity.z;//Same thing, but required to cover zFar.
		zCloseTime = Math.abs(zCloseTime);}
		
		//Y
		float yClose;
		float yFar;
		//Entity b1 is on the LEFT of b2.
		if(b1.getCenterPos().y < b2.getCenterPos().y){
			yClose = b2.pos.y - (b1.pos.y+b1.dim.y);//[b1{-}b2]
			yFar = (b2.pos.y + b2.dim.y) - b1.pos.y;//{b1]--[b2}
		}
		//Entity b1 is on the RIGHT of b2.
		else{
			yClose = b1.pos.y - (b2.pos.y + b2.dim.y);//[b2{-}b1]
			yFar = (b1.pos.y + b1.dim.y) - b2.pos.y;//{b2]--[b1}
		}
		
		float yCloseTime;//TimeThroughFrameWhenVelocitiesWouldCoverTheyClose;
		float yFarTime;//Time through frame necessary to cover the yFar distance.
		
		if(RelativeVelocity.y == 0){
			yCloseTime = -Float.MAX_VALUE;
			yFarTime = Float.MAX_VALUE;
		}
		else{
		yCloseTime = yClose/RelativeVelocity.y;//Percentage of yRelativeVelocity required to cover yClose.
		yFarTime = yFar/RelativeVelocity.y;//Same thing, but required to cover yFar.
		yCloseTime = Math.abs(yCloseTime);}

		//z collision
		if(xClose < 0 && yClose < 0 && zClose >= 0 && Math.abs(zCloseTime) < 1){
			normalz = b1.getCenterPos().z < b2.getCenterPos().z ? 1f:-1f;
			return zCloseTime;}
		//x collision
		else if(zClose < 0 && yClose < 0 && xClose >= 0 && Math.abs(xCloseTime) < 1){
			normalx = b1.getCenterPos().x < b2.getCenterPos().x ? 1f:-1f; 
			return xCloseTime;}
		//y collision
		else if(zClose < 0 && xClose < 0 && yClose >= 0 && Math.abs(yCloseTime) < 1){
			normaly = b1.getCenterPos().y < b2.getCenterPos().y ? 1f:-1f; 
			return yCloseTime;}
		//If xClose, yClose and zClose are all < 0, return 1f.
		//If any fucking single thing is over 1, fuck off.
		
		//outPrint("\nPAST THE FIRST DEFENSE.\n");
		//Resort to returning the highest number if somehow there are technically going to be two collisions.
		//x collision
		if((xCloseTime > zCloseTime || zCloseTime >= 1f) && (xCloseTime > yCloseTime || yCloseTime >= 1f) && xCloseTime < 1 && xCloseTime > 0 && xClose > 0){
			normalx = b1.getCenterPos().x < b2.getCenterPos().x ? 1f:-1f; 
			return xCloseTime;
		}
		//z collision
		if((zCloseTime > xCloseTime || xCloseTime >= 1f) && (zCloseTime > yCloseTime || yCloseTime >= 1f) && zCloseTime < 1 && zCloseTime > 0 && zClose > 0){
			normalz = b1.getCenterPos().z < b2.getCenterPos().z ? 1f:-1f;  
			return zCloseTime;
		}
		//y collision
		if((yCloseTime > zCloseTime || zCloseTime >= 1f) && (yCloseTime > xCloseTime || xCloseTime >= 1f) && yCloseTime < 1 && yCloseTime > 0 && yClose > 0){
			normaly = b1.getCenterPos().y < b2.getCenterPos().y ? 1f:-1f; 
			return yCloseTime;
		}
		/*
		outPrint("Name1:"+b1.name +" Name2:"+b2.name);
		outPrint("Made it through. xClose:"+xClose +" zClose:"+zClose +" yClose:"+yClose);
		outPrint("Times x:"+xCloseTime +" z:"+zCloseTime +" y:"+yCloseTime +"\n");*/
		return 1f;
	}
	
	public void superRender(){
		//Can be overriden by the levels. Is called after all entity super-renders but before entity regular-renders.
	}
	
	public void outPrint(String s){
		System.out.println("["+name.toUpperCase()+"]: "+s);
	}
	
	public void addEnt(Entity e){
		pendingEList.put(e.name, e);
		pendingEList.get(e.name).init(this);//Adds stuff to asset manager to load.
	}
	
	//Add entities directly into elist + init them.
	public void iAddEnt(Entity e){
		eList.put(e.name, e);
		eList.get(e.name).init(this);
	}
	
	//Used to safely add entities to eList. pendingEnts is a buffer that holds entities before they are added.
	//Entities will only be added after the level's tick, after tickEnts and after collisions have been checked.
	public void addPendingEnts(){
		Iterator<Entity> q = pendingEList.values().iterator();
		while(q.hasNext()){
			Entity e = q.next();
			if(e.doneLoad(this)){//If e is done loading. This also means all the variables were assigned.
				eList.put(e.name, e);//Puts it into eList.
				e.loaded = true;
				//outPrint("added "+e.name);
				q.remove();//Removes it from the pending List.
			}
		}
	}//9DDB1
	
	//Returns current camera
	public Camera getCam(){
		return camList.get(camIndex);
	}
	
	public Entity entity(String n){
		return eList.get(n);
	}
	
	public Entity entityPending(String n){
		return pendingEList.get(n);
	}
	
	public <T extends Entity> T entity(String n, Class<T> c){
		return c.cast(eList.get(n));
		
	}
	
	public <T extends Entity> T entityPending(String n, Class<T> c){
		return c.cast(pendingEList.get(n));
		
	}
	
	public void dispose() {
		for(Entity e: eList.values()){e.dispose();}
		eList.clear();
	}
	
	
	
	
	
	
	
	//BLINDNESS IS VIRTUE
	
	
	
	
}
