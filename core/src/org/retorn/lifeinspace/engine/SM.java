package org.retorn.lifeinspace.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class SM {
	//In general, here's the loop:
	//Make new SaveManager, if there's no file make a new one with a blank save,
	//If there is already a file, read the "save" from it and fill this save with that.
	//During the game, if you want to modify the object map within save, you do that.
	//When the file gets saved again (e.g. after you've modified save,) it rewrites the
	//whole thing. Any time something is saved, that's just the entirety of the CURRENT
	//save object-map being written to the file, in the form of a "save" object.
	//ALL YOU EVER DO IS MODIFY THE SAVE OBJECT, NO INTERACTION WITH THE ACTUAL FILE
	//IS HANDLED ANYWHERE ELSE BUT HERE! ALL YOU'RE CONCERNED ABOUT IS THIS OBJECT MAP
	//INSIDE OF THE SAVE OBJECT.
	public static FileHandle file = Gdx.files.local("save.json");//Where the file will be saved/what it's called.
	public static FileHandle backupFile = Gdx.files.local("bsave.json");
	public static Save save = new Save();//The thing that holds the object map with all the values. Will be filled if the file already has one saved in it.
	private static Json json;//Used to read/write from the file.
	//Fills the save if there is a file already.
	public SM(){
		json = new Json();
		json.setOutputType(OutputType.json);
		if(!file.exists()){
			file.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
			//outPrint("No Save file. Writing blank one.");
		}
		else{
			getSaveFromFile();
			//outPrint("File exists. Set Save to the one read from file.");
		}
	}
	
	public static void getSaveFromFile(){
		//Reads the save that was saved inside of the json.
		save = json.fromJson(Save.class, Base64Coder.decodeString(file.readString()));
	}
	
	public static void backupFile(){
		backupFile.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);//Saves to regular file first.
	}
	
	//Returns a boolean for whether or not the backup file existed.
	public static boolean restoreFromBackup(){
		if(backupFile.exists()){
		backupFile.copyTo(file);
		getSaveFromFile();//Refreshed the Save's object map after copying over the backup to the file.
		return true;
		}
		else{
			//outPrint("Attempted Backup Failed. No file.");
			return false;
		}
		
	}
	
	//An object gets added to "save's" object map. Then the whole thing is saved to the file.
	//You also call this to overwrite an already-contained-in-the-object-map-value, because it will just replace it as long as it uses the same key.
	public static void saveValue(String key, Object ob){
		save.data.put(key, ob);
		//Whatever the current save is, it's getting saved to the file. json. file.
		//So basically, the "save" object is what is actually getting recorded, not the object map.
		//We are just writing an encoded string to the file. The string is what json converts "save" to, which contains the object map.
		writeString();
	}
	
	//Like save value, but doesn't write. Works well in tandem with writeString after a big series of a a a a gHOOOOOOST? saving files/setting them fuck your grandma's baking honestly she's shit I'm sorry but christ this stuff
	//is dry also she shouldn't serve it out of an urn I heard that's 2 minutes bad luck.
	public static void setValue(String key, Object ob){
		save.data.put(key, ob);
	}
	
	//Writes "save" to the file. Useful is you're updating a lot of values and just want to set them, then save all at once when the save object map is updated.
	public static void writeString(){
		file.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);//Completely changes the file to whatever save has.
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(String key){
		if(save.data.containsKey(key)) return  (T) save.data.get(key);
		else return null;
	}
	
	public static boolean containsKey(String key){
		try{
		return save.data.containsKey(key);
		}
		catch(NullPointerException e){
			outPrint("why does this happen?"); 
			file.delete();
			new SM();
			return containsKey(key);
		}
	}
	
	
	//What will be "saved" within the json. Basically just a way to get at this object map. Filled on load if there's already a file with one saved in it.
	public static class Save{
		public ObjectMap<String, Object> data = new ObjectMap<String, Object>();
	}
	
	
	private static void outPrint(String s){
		System.out.println("[SM]:"+s+"");
	}
	
}
