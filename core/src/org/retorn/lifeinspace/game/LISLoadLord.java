package org.retorn.lifeinspace.game;

import org.retorn.lifeinspace.engine.LM;
import org.retorn.lifeinspace.engine.LoadLord;
import org.retorn.lifeinspace.engine.Sol;
import org.retorn.lifeinspace.level.BG;
import org.retorn.lifeinspace.tech.ColProfile;

public class LISLoadLord extends LoadLord{

	public void reign() {
		BG.setUp();
		Sol.setUp();
		LM.loadTexture("img/drop.png");
		ColProfile.setUp();
	}

}