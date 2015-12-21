package org.retorn.lifeinspace.desktop;

import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Life In Space Jam | RT";
		config.width = 1280;
		config.height = 720;
		config.resizable = false;
		
		new LwjglApplication(new Main(), config);
	}
}
