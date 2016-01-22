package org.retorn.lifeinspace.engine;

import java.util.ArrayList;

import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
//"Solid", Hard Collider
public class Sol extends HardCollider {
	private static Pixmap top, side;
	public static Sprite drawTop, drawSide;
	public Color c;
	protected static ShaderProgram shadowShader;
	protected static ShaderProgram sideShadowShader;
	protected static ShaderProgram sideBlankShader;
	public float fadeFactor;//How dark the bottom-fade-colour fades.
	public float darkFactor = 0.4f;//How dark the side is by default.
	protected Vector3 projPos;
	protected FrameBuffer fbo;//Used to render shadows.
	public ArrayList<Shadow> shadowImages;//Provided each frame by shadow-providing entities.  Used to pre-render the shadows.
	protected Texture shadowDraw;//FrameBuffer's Color Texture.
	private String path;//Path for texture
	private Texture tex;
	
	
	public Sol(String n, float w, float h, float d, float x, float y, float z, Color col, float ff) {
		super(n, w, h, d, x, y, z, 0);
		c = col;
		fadeFactor = ff;
		superRender = true;
		path = "img/defTex.png";
	}
	
	public Sol(String n, float w, float h, float d, float x, float y, float z, Color col, float ff, String p) {
		super(n, w, h, d, x, y, z, 0);
		c = col;
		fadeFactor = ff;
		superRender = true;
		path = p;
	}
	
	public Sol(String n, float w, float h, float d, float x, float y, float z, Color col, float ff, String p, float df) {
		super(n, w, h, d, x, y, z, 0);
		c = col;
		fadeFactor = ff;
		darkFactor = df;
		superRender = true;
		path = p;
	}

	public void tick(Level lvl) {
		shadowImages.clear();
	}

	public void render(Level lvl) {
		if(!shadowImages.isEmpty() && superRender){
			render(lvl, shadowDraw);
			}
		else{
			
		tex.bind(2);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		
		LM.batch.setShader(sideBlankShader);
		sideBlankShader.setUniformi("u_texture2", 2);
		sideBlankShader.setUniformf("u_size", dim.x, dim.z);
		sideBlankShader.setUniformf("u_texSize", tex.getWidth(), tex.getHeight());
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("fadeFactor"), 0f);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("fOn"), 0f);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("baseCol"), c);
		LM.batch.draw(drawTop, pos.x, pos.y+pos.z+dim.y, dim.x, dim.z);
		
		LM.batch.setShader(sideBlankShader);
		sideBlankShader.setUniformi("u_texture2", 2);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("fadeFactor"), fadeFactor);
		sideBlankShader.setUniformf("u_size", dim.x, dim.y);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("fOn"), darkFactor);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("u_height"), dim.y);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("baseCol"), c);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("u_texHeight"), drawSide.getRegionHeight());
		LM.batch.draw(drawSide, pos.x, pos.y+pos.z, dim.x, dim.y);
		LM.batch.setShader(null);
		}
	}
	
	public void render(Level lvl, Texture shadows) {
		float WIDTH = fbo.getWidth();
		float HEIGHT = fbo.getHeight();
		projPos = lvl.getCam().project(new Vector3(pos.x, pos.z+pos.y+dim.y, 0));
		projPos.x /= Main.resFac.x;
		projPos.y /= Main.resFac.y;

		tex.bind(2);
		shadows.bind(1);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		LM.batch.setShader(shadowShader);
		shadowShader.setUniformi("u_texture2", 2);
		shadowShader.setUniformf(shadowShader.getUniformLocation("u_size"), dim.x, dim.z);
		shadowShader.setUniformf(shadowShader.getUniformLocation("u_texSize"), tex.getWidth(), tex.getHeight());
		shadowShader.setUniformf(shadowShader.getUniformLocation("u_screensize"), new Vector2(WIDTH*lvl.getCam().zoom, HEIGHT*lvl.getCam().zoom));
		shadowShader.setUniformf(shadowShader.getUniformLocation("baseCol"), c);
		shadowShader.setUniformf(shadowShader.getUniformLocation("xdis"), (-projPos.x)/WIDTH);
		shadowShader.setUniformf(shadowShader.getUniformLocation("ydis"), (projPos.y)/HEIGHT);
		shadowShader.setUniformf(shadowShader.getUniformLocation("alpha"), 0.5f);
		LM.batch.draw(drawTop, pos.x, pos.y+pos.z+dim.y, dim.x, dim.z);

		LM.batch.setShader(sideBlankShader);
		sideBlankShader.setUniformi("u_texture2", 2);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("fadeFactor"), fadeFactor);
		sideBlankShader.setUniformf("u_size", dim.x, dim.y);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("fOn"), darkFactor);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("u_height"), dim.y);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("baseCol"), c);
		sideBlankShader.setUniformf(sideBlankShader.getUniformLocation("u_texHeight"), drawSide.getRegionHeight());
		LM.batch.draw(drawSide, pos.x, pos.y+pos.z, dim.x, dim.y);
		LM.batch.setShader(null);
		
		LM.useDefaultCamera();
		//LM.batch.draw(fbo.getColorBufferTexture(), 0, LM.HEIGHT, fbo.getWidth(), -fbo.getHeight());
		LM.useLevelCamera();
	}
	
	

	@Override
	public void superRender(Level lvl) {
		if(!shadowImages.isEmpty()){
			
			//Maintain fbo.
			if((fbo.getWidth() != LM.WIDTH || fbo.getHeight() != LM.HEIGHT)){
				fbo = new FrameBuffer(Format.RGBA8888, LM.WIDTH, LM.HEIGHT, false);
			}

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
		
		if(!LM.loader.isLoaded(path)) LM.loadTexture(path);
	}

	@Override
	public boolean doneLoad(Level lvl) {
		if(LM.loader.isLoaded(path)){
			tex = new Texture(path);
			tex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			
			return true;
		}
		
			return false;
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
		shadowShader.pedantic = false;	
			System.out.println("Shadow Shader Compiled: "+shadowShader.isCompiled() +shadowShader.getLog());
		sideShadowShader = new ShaderProgram(Gdx.files.internal(pathPrefix+"SolSideShadow.vsh"), Gdx.files.internal(pathPrefix+"SolSideShadow.fsh"));
		sideShadowShader.pedantic = false;
			System.out.println("Side-Shadow Shader Compiled: "+sideShadowShader.isCompiled() +sideShadowShader.getLog());
		sideBlankShader = new ShaderProgram(Gdx.files.internal(pathPrefix+"SolSideBlank.vsh"), Gdx.files.internal(pathPrefix+"SolSideBlank.fsh"));
		sideBlankShader.pedantic = false;	
			System.out.println("Side-Blank Shader Compiled: "+sideBlankShader.isCompiled() +sideBlankShader.getLog());
			
		//Setting the index of the shadow's binded texture in the Shader.
		shadowShader.begin();
		shadowShader.setUniformi("u_texture1", 1);
		shadowShader.end();
			
		sideShadowShader.begin();
		sideShadowShader.setUniformi("u_texture1", 1);
		sideShadowShader.end();
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
