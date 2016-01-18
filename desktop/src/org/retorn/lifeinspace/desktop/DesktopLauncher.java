package org.retorn.lifeinspace.desktop;

import org.retorn.lifeinspace.game.LMAssembly;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "COINSHIP | RT | LIFE IN SPACE";
		config.width = 1280;
		config.height = 720;
		config.addIcon("img/icon128.png", Files.FileType.Local);
		config.addIcon("img/icon32.png", Files.FileType.Local);
		config.addIcon("img/icon16.png", Files.FileType.Local);
		
		new LwjglApplication(LMAssembly.get(config.width, config.height, true), config);
	}
}
