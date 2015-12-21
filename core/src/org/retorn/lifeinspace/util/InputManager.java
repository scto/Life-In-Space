package org.retorn.lifeinspace.util;

import org.retorn.lifeinspace.level.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
//Unified input.
public class InputManager extends InputAdapter{
	public static boolean  pressedKey, pressedBackspace, pressedNum0, pressedNum1, pressedNum2, pressedNum3, pressedNum4, pressedNum5, pressedNum6, pressedNum7, pressedNum8, pressedNum9, pressedR, pressedLShift, pressedF, pressedE, pressedQ, pressedSpace, pressedEnter, pressedEscape, pressedTab, pressedW, pressedA, pressedS, pressedD, pressedI, pressedK, pressedJ, pressedL, pressedUp, pressedDown, pressedLeft, pressedRight, pressedF11, pressedF1, pressedF2, pressedF3, pressedF4;
	public static boolean downRControl, downLControl, downBackspace, downU, downC, downK, downN, downG, downI, downNum0, downNum1, downNum5, downDNorth, downDEast, downDSouth, downDWest, downR, downF3, downF, downSpace, downLShift, downRShift, downW, downD, downS, downA, downUp,  downRight,  downLeft, downDown, downQ, downE, downNum2, downNum4, downNum6, downNum7, downNum8, downNum9;
	public static boolean pressedMenu, pressedBack, pressedMainB, pressedRMB, pressedMMB, downMainB, downRMB, downMMB; 
	public static boolean upMainB;
	public static int scroll;
	public static Vector2 M, T, deltaM, prevM, deltaDrag, prevDrag;//T = touch. Updated when touchDown.
	
	public static String typedChar = "";
	public static boolean charWasTyped;
	
	public static boolean downTypedBackspace;//Backspacing via holding it down for a period of time.
	public static float backspaceTime, initialBackspaceTime;//Initial is just the gate to the backspace-cascade.
	
	public void init(){
		//Top/Bottoms are flipped by me.
		M = new Vector2();
		T = new Vector2();
		deltaDrag = new Vector2();
		deltaM = new Vector2();
		prevM = new Vector2();
		prevDrag = new Vector2();
	}
	

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0)
			prevDrag.set(screenX, screenY);
		if(button == Buttons.LEFT && pointer == 0){
			downMainB = true; pressedMainB = true;
			M.set(Gdx.input.getX(), Main.HEIGHT-Gdx.input.getY());
		}
		if(button == Buttons.RIGHT){downRMB = true; pressedRMB = true;}
		if(button == Buttons.MIDDLE){downMMB = true; pressedMMB = true;}
		T.set(screenX, screenY);//If you stop touching, this will be wherever you left off.
		return super.touchDown(screenX, screenY, pointer, button);
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pointer == 0){
			prevDrag.set(0,0);
			deltaDrag.set(0,0);
		}
		if(button == Buttons.LEFT && pointer == 0){downMainB = false; upMainB = true;}
		if(button == Buttons.RIGHT){downRMB = false;}
		if(button == Buttons.MIDDLE){downMMB = false;}
		return super.touchUp(screenX, screenY, pointer, button);
	}


	@Override
	public boolean keyDown(int keycode) {
		//Activates variables that are reset in resetPressed if the button is pressed.
		if(keycode == Keys.SPACE){pressedSpace = true; downSpace = true;}
		if(keycode == Keys.ENTER){pressedEnter = true;}
		if(keycode == Keys.TAB){pressedTab = true;}
		if(keycode == Keys.ESCAPE){pressedEscape = true;}
		if(keycode == Keys.BACKSPACE){pressedBackspace = true; downBackspace = true;}
		
		if(keycode == Keys.W){pressedW = true; downW = true;}
		if(keycode == Keys.S){pressedS = true; downS = true;}
		if(keycode == Keys.D){pressedD = true; downD = true;}
		if(keycode == Keys.A){pressedA = true; downA = true;}
		
		if(keycode == Keys.I){pressedI = true; downI = true;}
		if(keycode == Keys.K){pressedK = true; downK = true;}
		if(keycode == Keys.L){pressedL = true;}
		if(keycode == Keys.J){pressedJ = true;}
		
		if(keycode == Keys.N){downN = true;}
		if(keycode == Keys.G){downG = true;}
		
		if(keycode == Keys.U){downU = true;}
		if(keycode == Keys.C){downC = true;}
		
		if(keycode == Keys.E){pressedE = true; downE = true;}
		if(keycode == Keys.Q){pressedQ = true; downQ = true;}
		if(keycode == Keys.F){pressedF = true; downF = true;}
		if(keycode == Keys.R){pressedR = true; downR = true;}
		
		if(keycode == Keys.UP){pressedUp = true; downUp = true;}
		if(keycode == Keys.DOWN){pressedDown = true; downDown = true;}
		if(keycode == Keys.LEFT){pressedLeft = true; downLeft = true;}
		if(keycode == Keys.RIGHT){pressedRight = true; downRight = true;}
		
		if(keycode == Keys.F11){pressedF11 = true;}
		if(keycode == Keys.F1){pressedF1 = true;}
		if(keycode == Keys.F2){pressedF2 = true;}
		if(keycode == Keys.F3){pressedF3 = true; downF3 = true;}
		if(keycode == Keys.F4){pressedF4 = true;}
		
		
		//Activates variables that are reset when keycodeReleased is Called.
		if(keycode == Keys.SHIFT_LEFT){downLShift = true; pressedLShift = true;}
		if(keycode == Keys.SHIFT_RIGHT){downRShift = true;}
		if(keycode == Keys.CONTROL_RIGHT){downRControl = true;}
		if(keycode == Keys.CONTROL_LEFT){downLControl = true;}
		
		if(keycode == Keys.NUM_0){downNum0 = true; pressedNum0 = true; System.out.println("fuck");}
		if(keycode == Keys.NUM_1){downNum1 = true; pressedNum1 = true;}
		if(keycode == Keys.NUM_2){downNum2 = true; pressedNum2 = true;}
		if(keycode == Keys.NUM_4){downNum4 = true; pressedNum4 = true;}
		if(keycode == Keys.NUM_1){downNum5 = true; pressedNum5 = true;}
		if(keycode == Keys.NUM_6){downNum6 = true; pressedNum6 = true;}
		if(keycode == Keys.NUM_7){downNum7 = true; pressedNum7 = true;}
		if(keycode == Keys.NUM_8){downNum8 = true; pressedNum8 = true;}
		if(keycode == Keys.NUM_9){downNum9 = true; pressedNum9 = true;}
		
		if(keycode == Keys.BACK) pressedBack = true;
		if(keycode == Keys.MENU) pressedMenu = true;
		
		pressedKey = true;
		
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
	if(keycode == Keys.SPACE){downSpace = false;}
	if(keycode == Keys.SHIFT_LEFT){downLShift = false;}
	if(keycode == Keys.SHIFT_RIGHT){downRShift = false;}
	if(keycode == Keys.CONTROL_LEFT){downLControl = false;}
	if(keycode == Keys.CONTROL_RIGHT){downRControl = false;}
	if(keycode == Keys.BACKSPACE){downBackspace = false; backspaceTime = 0f; initialBackspaceTime = 0f;}
	
	if(keycode == Keys.W){downW = false;}
	if(keycode == Keys.D){downD = false;}
	if(keycode == Keys.S){downS = false;}
	if(keycode == Keys.A){downA = false;}
	
	if(keycode == Keys.N){downN = false;}
	if(keycode == Keys.G){downG = false;}
	if(keycode == Keys.I){downI = false;}
	
	if(keycode == Keys.U){downU = false;}
	if(keycode == Keys.C){downC = false;}
	if(keycode == Keys.K){downK = false;}
	
	if(keycode == Keys.UP){downUp = false;}
	if(keycode == Keys.RIGHT){downRight = false;}
	if(keycode == Keys.DOWN){downDown = false;}
	if(keycode == Keys.LEFT){downLeft = false;}
	
	if(keycode == Keys.NUM_0){downNum0 = false;}
	if(keycode == Keys.NUM_1){downNum1 = false;}
	if(keycode == Keys.NUM_2){downNum2 = false;}
	if(keycode == Keys.NUM_4){downNum4 = false;}
	if(keycode == Keys.NUM_6){downNum6 = false;}
	if(keycode == Keys.NUM_7){downNum7 = false;}
	if(keycode == Keys.NUM_8){downNum8 = false;}
	if(keycode == Keys.NUM_9){downNum9 = false;}
	
	if(keycode == Keys.F3){downF3 = false;}
	
	if(keycode == Keys.Q){downQ = false;}
	if(keycode == Keys.E){downE = false;}
	if(keycode == Keys.F){downF = false;}
	if(keycode == Keys.R){downR = false;}
		return super.keyUp(keycode);
	}
	
	//Called at the end of the tick method in LeveMainanager to assure that they are only "pressed" for one frame, until they are pressed again.
	public static void resetPress(){
		pressedSpace = false;
		pressedEnter = false;
		pressedTab = false;
		pressedEscape = false;
		pressedBackspace = false;
		pressedLShift = false;
		pressedW = false;
		pressedS = false;
		pressedD = false;
		pressedA = false;
		pressedI = false;
		pressedK = false;
		pressedJ = false;
		pressedL = false;
		pressedE = false;
		pressedQ = false;
		pressedF = false;
		pressedR = false;
		pressedUp = false;
		pressedDown = false;
		pressedLeft = false;
		pressedRight = false;
		pressedF11 = false;
		pressedF1 = false;
		pressedF2 = false;
		pressedF3 = false;
		pressedF4 = false;
		pressedNum1 = false;
		pressedNum2 = false;
		pressedNum3 = false;
		pressedNum4 = false;
		pressedNum5 = false;
		pressedNum6 = false;
		pressedNum7 = false;
		pressedNum8 = false;
		pressedNum9 = false;
		
		pressedMainB = false;
		pressedRMB = false;
		pressedMMB = false;

		pressedBack = false;
		pressedMenu = false;
		
		pressedKey = false;
		
		
		upMainB = false;
		
		charWasTyped = false;
		
		scroll = 0;
		M.set(Gdx.input.getX(), Main.HEIGHT-Gdx.input.getY());
		prevM.set(M);
		deltaDrag.setZero();
		deltaM.setZero();
		
		
		downTypedBackspace = false;
		
		if(downBackspace){
			initialBackspaceTime += Main.delta;
			backspaceTime += Main.delta;
			if(initialBackspaceTime > 0.15f && backspaceTime > 0.06f){ downTypedBackspace = true; backspaceTime = 0f;}
		}
		
		else backspaceTime = 0f;
		
		typedChar = "";
	}
	
	@Override
	public boolean keyTyped(char character) {
		typedChar += Character.toString(character);
		charWasTyped = true;
		return true;
	}
	
	//Must be used in conjunction with if(InputManager.charWasTyped)
	public String getTypedChar(){
		return typedChar;
	}
	
	//Modifies the string based on keyboard-input.
	public static String editType(String s){
		if(charWasTyped) s += typedChar;
		if((pressedBackspace || downTypedBackspace) && s.length() > 1) 
			s = s.substring(0, s.length()-2);
		if(pressedEnter) s += "\n";
		return s;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pointer == 0){
		deltaDrag.set(screenX-prevDrag.x, prevDrag.y-screenY);
		prevDrag.set(screenX, screenY);}
		return super.touchDragged(screenX, screenY, pointer);
	}

	
	@Override
	public boolean scrolled(int amount) {
		scroll = amount;
		return super.scrolled(amount);
	}


	//Unprojects that shit into the camera.
	public static Vector2 getWorldMouse(OrthographicCamera defaultCam){
		M.set(Gdx.input.getX(), Main.HEIGHT-Gdx.input.getY());
		Vector3 unproj = defaultCam.unproject(new Vector3(M.x, Main.HEIGHT-M.y, 0));
		return new Vector2(unproj.x, unproj.y);
	}


	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		deltaM.set((float)screenX-prevM.x, (Main.HEIGHT-prevM.y)-(float)screenY);
		return super.mouseMoved(screenX, screenY);
	}
	
	public void outPrint(String s){
		System.out.println("[INPUTMANAGER]: "+s);
	}
	
	
	
	

}
