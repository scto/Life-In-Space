package org.retorn.lifeinspace.engine;

import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
//"Solid", Hard Collider
public class Sol extends HardCollider {
	private static Pixmap top, side;
	public static Sprite drawTop, drawSide;
	protected Color c;
	protected static ShaderProgram shadowShader;
	protected static ShaderProgram sideShadowShader;
	protected static ShaderProgram sideBlankShader;
	public float fadeFactor;//How dark the bottom-fade-colour fades.
	protected Vector3 projPos;
	protected FrameBuffer fbo;//Used to render shadows.
	public ArrayList<Shadow> shadowImages;//Provided each frame by shadow-providing entities.  Used to pre-render the shadows.
	protected Texture shadowDraw;//FrameBuffer's Color Texture.
	
	
	public Sol(String n, float w, float h, float d, float x, float y, float z, Color col, float ff) {
		super(n, w, h, d, x, y, z, 0);
		c = col;
		fadeFactor = ff;
		superRender = true;
	}

	public void tick(Level lvl) {
		shadowImages.clear();
	}

	public void render(Level lvl) {
		if(!shadowImages.isEmpty() && superRender){
			render(lvl, shadowDraw);
			}
		else{
		LM.batch.setShader(sideBlankShader);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("fadeFactor"), 0f);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("baseCol"), c);
		LM.batch.draw(drawTop, pos.x, pos.y+pos.z+dim.y, dim.x, dim.z);
		
		LM.batch.setShader(sideBlankShader);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("fadeFactor"), fadeFactor);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("u_height"), dim.y);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("u_texHeight"), drawSide.getRegionHeight());
		LM.batch.draw(drawSide, pos.x, pos.y+pos.z, dim.x, dim.y);
		LM.batch.setShader(null);
		}
	}
	
	public void render(Level lvl, Texture shadows) {
		float WIDTH = fbo.getWidth();
		float HEIGHT = fbo.getHeight();
		projPos = lvl.getCam().project(new Vector3(pos.x, pos.z+pos.y+dim.y, 0));
		shadows.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		LM.batch.setShader(shadowShader);
		shadowShader.setUniformf(shadowShader.getUniformLocation("u_size"), new Vector2(dim.x, dim.z));
		shadowShader.setUniformf(shadowShader.getUniformLocation("u_screensize"), new Vector2(WIDTH*lvl.getCam().zoom, HEIGHT*lvl.getCam().zoom));
		shadowShader.setUniformf(shadowShader.getUniformLocation("baseCol"), c);
		shadowShader.setUniformf(shadowShader.getUniformLocation("xdis"), (-projPos.x)/WIDTH);
		shadowShader.setUniformf(shadowShader.getUniformLocation("ydis"), (projPos.y)/HEIGHT);
		shadowShader.setUniformf(shadowShader.getUniformLocation("alpha"), 0.5f);
		LM.batch.draw(drawTop, pos.x, pos.y+pos.z+dim.y, dim.x, dim.z);
		
		LM.batch.setShader(sideShadowShader);
		sideShadowShader.setUniformf(sideShadowShader.getUniformLocation("baseCol"), c);
		sideShadowShader.setUniformf(sideShadowShader.getUniformLocation("u_size"), new Vector2(dim.x, dim.y));
		sideShadowShader.setUniformf(sideShadowShader.getUniformLocation("u_screensize"), new Vector2(WIDTH*lvl.getCam().zoom, HEIGHT*lvl.getCam().zoom));
		sideShadowShader.setUniformf(sideShadowShader.getUniformLocation("xdis"), (-projPos.x)/WIDTH);
		sideShadowShader.setUniformf(sideShadowShader.getUniformLocation("ysamp"), 1-projPos.y/HEIGHT);
		sideShadowShader.setUniformf(sideShadowShader.getUniformLocation("fadeFactor"), fadeFactor);
		//float alpha = 1-Math.abs((lvl.eList.get("vc").pos.y-(pos.y+dim.y))/1500); if(alpha > 1) alpha = 1; if(alpha < 0) alpha = 0;
		float alpha = 1f;
		sideShadowShader.setUniformf(sideShadowShader.getUniformLocation("alpha"), alpha*0.5f);
		LM.batch.draw(drawSide, pos.x, pos.y+pos.z, dim.x, dim.y);
		LM.batch.setShader(null);		
	}
	
	

	@Override
	public void superRender(Level lvl) {
		if(!shadowImages.isEmpty()){
			if((fbo.getWidth() != LM.WIDTH || fbo.getHeight() != LM.HEIGHT))
				fbo = new FrameBuffer(Format.RGBA8888, LM.WIDTH, LM.HEIGHT, false);

			fbo.begin();
			
			Gdx.gl.glClearColor(1, 1, 1, 0);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	        LM.batch.begin();
	        for(Shadow s: shadowImages){
	        	if(s != null){
	        		float factorDisY = s.fY*(s.pY-(pos.y+dim.y));
	        		float sY = (pos.y+dim.y)+factorDisY;
	        		float factorDisX = s.fX*(s.pY-(pos.y+dim.y));
	        		float sX = factorDisX;
	        		
	        		s.setY(s.getY()+sY);
	        		s.setX(s.getX()+sX);
	        		s.draw(LM.batch, s.getColor().a);
	        		s.setY(s.getY()-sY);
	        		s.setX(s.getX()-sX);
	        		}
	        	}
	        LM.batch.end();
	        
	        fbo.end();
	        fbo.getColorBufferTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	        shadowDraw = fbo.getColorBufferTexture();
	        
	        Gdx.gl.glClearColor(lvl.bg.r, lvl.bg.g, lvl.bg.b, 0);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
	}

	@Override
	public void collide(Entity b, float collisionTime, Vector3 normal, Level lvl) {
		if(b instanceof ShadowProvider && normal.y == 1){
			shadowImages.add(((ShadowProvider) b).tr);
		}
	}

	@Override
	public void init(Level lvl) {
		projPos = new Vector3();
		
		//List for pre-rendering shadows.
		shadowImages = new ArrayList<Shadow>();
		fbo = new FrameBuffer(Format.RGBA8888, LM.WIDTH, LM.HEIGHT, false);
		
	}

	@Override
	public boolean doneLoad(Level lvl) {
			//Setting the index of the shadow's binded texture in the Shader.
			shadowShader.begin();
			shadowShader.setUniformi("u_texture1", 1);
			shadowShader.end();
			
			sideShadowShader.begin();
			sideShadowShader.setUniformi("u_texture1", 1);
			sideShadowShader.end();
			
			//Binding the shadow texture to index 1, then resetting the active texture to 0.
			//shadow.bind(1);
			//Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
			
			return true;
	}
	
	//SET UP FOR ALL SOLS. CALLED IN LOADLORDS.
	public static void setUp(){
		//TOP/BOTTOM IMAGES
		//Drawing top/bottom onto p.
		top = new Pixmap(2, 2, Format.RGBA8888);
		top.setColor(Color.WHITE);
		top.fill();
		//c2.sub(0.03f, 0.08f, 0.035f, 0f);
		side = new Pixmap(2, 2, Format.RGBA8888);
		side.setColor(Color.WHITE);
		side.fill();
		
		Texture to = new Texture(top); to.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Texture si = new Texture(side); si.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		drawTop = new Sprite(to);
		drawSide = new Sprite(si);
		
		String pathPrefix = "shaders/";
		
		shadowShader = new ShaderProgram(Gdx.files.internal(pathPrefix+"SolShadow.vsh"), Gdx.files.internal(pathPrefix+"SolShadow.fsh"));
			System.out.println("Shadow Shader Compiled: "+shadowShader.isCompiled() +shadowShader.getLog());
		sideShadowShader = new ShaderProgram(Gdx.files.internal(pathPrefix+"SolSideShadow.vsh"), Gdx.files.internal(pathPrefix+"SolSideShadow.fsh"));
			System.out.println("Side-Shadow Shader Compiled: "+sideShadowShader.isCompiled() +sideShadowShader.getLog());
		sideBlankShader = new ShaderProgram(Gdx.files.internal(pathPrefix+"SolSideBlank.vsh"), Gdx.files.internal(pathPrefix+"SolSideBlank.fsh"));
			System.out.println("Side-Blank Shader Compiled: "+sideBlankShader.isCompiled() +sideBlankShader.getLog());
		
	}

	@Override
	public void dispose() {
		drawTop.getTexture().dispose();
		drawSide.getTexture().dispose();
	}

	@Override
	public String getDebug() {
		return "v:"+v;
	}

}
