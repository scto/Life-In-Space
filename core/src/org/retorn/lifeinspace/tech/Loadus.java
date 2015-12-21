package org.retorn.lifeinspace.tech;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Loadus extends AssetManager{
	
	public static AssetManager loader;
	public static TextureParameter tParam;
	
	public static void setUp(){
		tParam = new TextureParameter();
		tParam.genMipMaps = true;
		tParam.minFilter = TextureFilter.MipMapLinearLinear;
		tParam.magFilter = TextureFilter.MipMapLinearLinear;
	}
	
	public static void loadTexture(String path){
		loader.load(path, Texture.class, tParam);
	}
	
	public static void loadSound(String path){
		loader.load(path, Sound.class);
	}
	
	public static void loadMusic(String path){
		loader.load(path, Music.class);
	}

}
