package org.retorn.lifeinspace.desktop;

import org.retorn.lifeinspace.game.LMAssembly;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Life In Space Jam | RT";
		config.width = 1280;
		config.height = 720;
		
		new LwjglApplication(LMAssembly.get(config.width, config.height, true), config);
	}
}
