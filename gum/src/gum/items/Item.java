package gum.items;

import gum.ItemContainer;
import gum.Player;
import gum.User;
import gum.menus.MenuContainer;

import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.util.StringTokenizer;

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
abstract public class Item implements Cloneable, MenuContainer, ItemContainer{

    public Item(){}

    abstract public void init(); 
    
    //consider breaking SimpleItem into SimpleDoor, simpleContainer, ect...

    //item's own parser, makes customizing items easier.
    abstract public void parseItem(StringTokenizer st, Player p);

    //handle user commands
    abstract public boolean use(Player p, String command, StringTokenizer st);

    // handle doors
    abstract public void open(Player p);
    abstract public void close(Player p);
    abstract public boolean getAutoClosing();
    abstract public void setAutoClosing (boolean setting);
    abstract public boolean getIsOpen();

    //handle containers
    abstract public Item getItemByName(String itemName);
    abstract public Item getItemByNameAndPosition(String itemName, int itemPosition);
    abstract public Item removeItem(Item item);
    abstract public void put(Item putItem);

    // this allows for special items that react to being taken, dropped and held
    //abstract public Item take(Item takenItem);
    abstract public boolean take(Player p);
    abstract public void drop(Player p);
    abstract public boolean hold();


    //item creation
    abstract public void check(Player p);
    
	// this is to set apart the getItemName method from 'getName'
	// so that you can add color logic to this method without 
	// screwing up the 'bean' definition. 
    abstract public String getItemName();
    
    abstract public String getFilename();
    abstract public String getBlocking();
    abstract public void setBlocking(String newBlocking);
    abstract public void setName(String newName);
    abstract public void inspect(Player p);
    abstract public boolean getIsWeapon();
    abstract public boolean getIsStationary();
    abstract public boolean getIsInvisable();
    abstract public boolean getIsInfinite();


    abstract public void setDescription(String newDescription);
    abstract public void addToDescription(String description);
    abstract public void setHitString(String newHitString);
    abstract public void setMissString(String newDescription);

    abstract public boolean displayAttackMenu(User u);
    abstract public void setIsStationary(boolean setting);
    abstract public void setIsInvisable(boolean setting);
    abstract public void setIsInfinite(boolean setting);

    abstract public void setIsDoor(boolean setting);
    abstract public void setIsOpen(boolean setting);
    abstract public void setIsContainer(boolean setting);
    abstract public void setItemContainer(ItemContainer itemContainer);
    
    public abstract void setSetting(String setting, int value);
    public abstract int getSetting(String setting);


    abstract public void attack(Player p1, Player p2);
    
    public void save(String fileName){
    	try{
    		System.out.println("saving Item:"+this.getItemName()+" as:"+fileName);
    		FileOutputStream os = new FileOutputStream(fileName);
    		XMLEncoder encoder = new XMLEncoder(os);
    		encoder.writeObject(this);
    		encoder.close();
    	} catch (Exception e){
    		e.printStackTrace();
    	}	
    }

}
