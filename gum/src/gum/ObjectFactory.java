package gum;

import gum.actions.Action;
import gum.items.Item;
import gum.mobs.Mob;
import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.util.Random;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ObjectFactory {
    public ObjectFactory() {
    }

    private static String ItemDir;

    // this seed shouldn't be important because it's shared across the system.
    private static Random rand = new Random(917823);

    public static void initializeRandObject(long seed){
        rand = new Random(seed);
    }

    public static Random getRandObject(){
        return rand;
    }

    public static void setItemDir(String newDir){
        ItemDir = newDir;
    }
    
    public static String getItemDir(){
    	return ItemDir;
    }

    public static Item CreateItem(String fileName){
        Item oItem = null;
        oItem = (Item) createClass(fileName);
        oItem.init();
        return oItem;
    }
    
    public static Mob CreateMob(String fileName){
    	Mob oMob = null;
    	oMob = (Mob) createClass(fileName);
    	oMob.init();
    	return oMob;
    }

    public static Action CreateAction(String fileName){
    	Action oAction = null;
    	oAction = (Action) createClass(fileName);
    	oAction.init();
    	return oAction;
    }
    
    public static Object createClass(String fileName){
    	Object retObject = null;
    	
    	
    	//TODO figure out if we're dealing with a filename or 
    	// a class name before trying to create the object. 
    	// this was a 'quick and dirty' method to hold over 
    	// until I get a chance to write a decent file regular expression
    	System.out.println("Creating object:"+fileName);
        try{
        	
        	FileInputStream is = new FileInputStream(World.getArea().getLibDir()+"//"+fileName);
        	XMLDecoder decoder = new XMLDecoder(is);
        	retObject = (Object)decoder.readObject();
        	decoder.close(); 
        } catch (Exception e){
        	// The only errors likely to occur are because the user is likely not loading from a  file. 
        	e.printStackTrace();
        	retObject = null;
        }
        if (retObject == null){
        	try{
        		System.out.println("Creating from class");
        		Class<?> classDefinition = Class.forName(fileName);
        		retObject = (Object) classDefinition.newInstance();  
        	}catch (Exception e){
        		// if this fails, report the error to the user. 
        		e.printStackTrace();
        		retObject = null;
        	}
        }
		
        return retObject;
    	
    }
    
/*    public static Mob CreateMob(String filename){
        Mob oMob = null;
        String mobClass;
        FileHandler file = new FileHandler();

        System.out.println("In ItemFactory loading mob:"+filename);
        mobClass = file.read_setting(filename,"class",false);
        System.out.println("mob class:"+ mobClass);

        if (mobClass.matches("mob")){ // use String.compare? or maybe equals?
            oMob = new BasicMob(filename);
        } else if (mobClass.equals("mobzombie")){
            oMob = new MobZombie(filename);
        }else {
            System.out.println("MOB CLASS NOT FOUND!! RETURNING NULL<WILL CRASH>");
        }

        return oMob;
    }
*/    
    
}
