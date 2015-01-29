package gum;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class World {

	private static Area area;
	private static GameClock gameClock;
	
	public static synchronized void load(){
		//TODO check to see if 'prep for save' is still needed. 
    	try{
    		area.setRooms(null);
    		FileInputStream is = new FileInputStream("Area.xml");
    		XMLDecoder decoder = new XMLDecoder(is);
    		area = (Area)decoder.readObject();
    		decoder.close(); 
    		area.prepForSave();
    		area.initRooms();
    	}catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
    }
	
	public static synchronized void load(String filename){
		//TODO check to see if 'prep for save' is still needed. 
    	try{
    		FileInputStream is = new FileInputStream(filename);
    		XMLDecoder decoder = new XMLDecoder(is);
    		area.prepForSave();
    		area.setRooms(null);
    		area = (Area)decoder.readObject();
    		decoder.close(); 
    		area.prepForSave();
    		area.initRooms();
    		System.out.println("Area Loaded. \r\n");
    	}catch (Exception e){
			e.printStackTrace();
			area.GlobalChat("Error loading Mud file. Check filename and try again.\r\n");
			System.exit(1);
		}
    }
    
    public static synchronized void save(){
    	try{
    		FileOutputStream os = new FileOutputStream("C:/Area.xml");
    		XMLEncoder encoder = new XMLEncoder(os);
    		encoder.writeObject(area);
    		encoder.close();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	
    }

    public static synchronized Area getArea() {
		return area;
	}

	public static synchronized void setArea(Area area) {
		World.area = area;
	}

	public static GameClock getGameClock() {
		return gameClock;
	}

	public static void setGameClock(GameClock gameClock) {
		World.gameClock = gameClock;
	}
    
}
