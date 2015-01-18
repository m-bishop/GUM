package gum.items;

import gum.ItemContainer;
import gum.ObjectFactory;
import gum.Player;
import gum.User;
import gum.UserParser;
import gum.World;
import gum.actions.Action;
import gum.actions.ActionAttack;
import gum.actions.ActionHeader;
import gum.actions.item.ActionItemClose;
import gum.actions.item.ActionItemHold;
import gum.actions.item.ActionItemLook;
import gum.actions.item.ActionItemOpen;
import gum.actions.item.ActionItemPut;
import gum.actions.item.ActionItemTake;
import gum.menus.MenuExitException;
import gum.menus.PromptForBoolean;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;
import gum.mobs.Mob;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Scanner;
import java.io.File;


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
public class ItemBase extends Item {


    private String name;
    private String longDescription;
    private String filename;
    //private boolean isWeapon;
    private boolean isStationary;
    private boolean isDoor;
    private boolean isOpen;
    private boolean isContainer;
    private boolean isInvisable;
    private boolean isInfinite;
    private boolean autoClosing;
    private String blocking;
    private String hitString;
    private String missString;
    private String effectSettingName;
    private int effectSettingValue;
    private String preReqSettingName;
    private int preReqSettingValue;
    private Action currentAction;
    private Action itemReaction = null;
    
    
    //hold on to a pointer to whatever is containing this item
    private ItemContainer itemContainer = null;
	
    private Vector<Item> items = new Vector<Item>();
    private HashMap<String,Action> actions = new HashMap<String,Action>();

    public Random random; // 
    private HashMap<String, Integer> settings = new HashMap<String, Integer>(); // setting map

    public ItemBase() {}
        
    public void init() {
    	currentAction = null;
    	ActionItemTake takeAction = new ActionItemTake();
    	takeAction.init();
    	ActionItemPut putAction = new ActionItemPut();
    	putAction.init();
    	ActionItemPut dropAction = new ActionItemPut();
    	dropAction.init();
    	ActionItemLook lookAction = new ActionItemLook();
    	lookAction.init();
    	ActionItemOpen openAction = new ActionItemOpen();
    	openAction.init();
    	ActionItemClose closeAction = new ActionItemClose();
    	closeAction.init();
    	ActionItemHold holdAction = new ActionItemHold();
    	holdAction.init();
    	
    	takeAction.setSuccessMessage("Taken\r\n");
    	takeAction.setFailMessage("You can't take that.\r\n");
    	this.getActions().put("take", takeAction);
    	this.getActions().put("put", putAction);
    	this.getActions().put("drop", dropAction);
    	this.getActions().put("inspect", lookAction);
    	this.getActions().put("open", openAction);
    	this.getActions().put("close", closeAction);
    	this.getActions().put("hold", holdAction);
    	
    }
    
    public void addToInventory(Item i){
        items.add(i);
        i.setItemContainer(this);
    }
    
	public void destroy(){
		//remove yourself from whatever is holding on to you.
		//if anyone knows how to get a list of references from an object,
		//let me know. 
		
		this.getItemContainer().removeItem(this);
	}
	
/*   public ItemBase(String file) {
        System.out.println("loading item" + file);

        this.filename = file;
        FileHandler roomFile = new FileHandler();
        String itemList;
        Item newItem;

        random = ObjectFactory.getRandObject();

        name = roomFile.read_setting(file,"name",false);
        System.out.println("name="+name);

        blocking = roomFile.read_setting(file,"blocking",false);
        System.out.println("blocking="+blocking);

        longDescription = roomFile.read_setting(file,"longDescription",false);
        System.out.println("description="+longDescription);

        hitString = roomFile.read_setting(file,"hitstring",false);
        System.out.println("hitstring="+hitString);

        missString = roomFile.read_setting(file,"missstring",false);
        System.out.println("missstring="+missString);

        effectSettingName = roomFile.read_setting(file,"effectSettingName",false);
        System.out.println("effectSettingName = "+ effectSettingName);
        try{
            effectSettingValue = Integer.parseInt(roomFile.read_setting(file,
                    "effectSettingValue", false));
            System.out.println("effectSettingValue ="+String.valueOf(effectSettingValue));
        } catch (Exception e){
            System.out.println("effectSettingValue was not set");
            // this will happen whenever this value doesn't contain an int, so it's
            // expected pretty regularly.
            // e.printStackTrace();
        }

        preReqSettingName = roomFile.read_setting(file,"preReqSettingName",false);
        System.out.println("preReqsettingname = "+ preReqSettingName);
        try{
            preReqSettingValue = Integer.parseInt(roomFile.read_setting(file,
                    "preReqSettingValue", false));
            System.out.println("preReqSettingValue ="+String.valueOf(preReqSettingValue));
        } catch (Exception e){
            System.out.println("preReqSettingValue was not set");
            // this will happen whenever this value doesn't contain an int, so it's
            // expected pretty regularly.
            // e.printStackTrace();
        }

        try{
            damageBase = Integer.parseInt(roomFile.read_setting(file,
                "damagebase", false));
            System.out.println("dameagebase ="+String.valueOf(damageBase));
        } catch (Exception e){
            System.out.println("damageBase not set");
        }
        try{
            damageRoll = Integer.parseInt(roomFile.read_setting(file,
                "damageroll", false));
            System.out.println("damageRoll ="+String.valueOf(damageRoll));
        }catch (Exception e){
            System.out.println("damageRoll not set");
        }

        if (roomFile.read_setting(file,"isWeapon",false).equalsIgnoreCase("true")){
            isWeapon = true;
        } else {
            isWeapon = false;
        }
        System.out.println("isWeapon:"+String.valueOf(isWeapon));

        if (roomFile.read_setting(file,"autoClosing",false).equalsIgnoreCase("true")){
            this.setAutoClosing(true);
        } else {
            this.setAutoClosing(false);
        }
        System.out.println("isWeapon:"+String.valueOf(isWeapon));

        if (roomFile.read_setting(file,"isStationary",false).equalsIgnoreCase("true")){
            isStationary = true;
        } else {
            isStationary = false;
        }
        System.out.println("isStationary:"+String.valueOf(isStationary));

        if (roomFile.read_setting(file,"isOpen",false).equalsIgnoreCase("true")){
            isOpen = true;
        } else {
            isOpen = false;
        }
        System.out.println("isOpen:"+String.valueOf(isOpen));

        if (roomFile.read_setting(file,"isContainer",false).equalsIgnoreCase("true")){
            isContainer = true;
        } else {
            isContainer = false;
        }
        System.out.println("isContainer:"+String.valueOf(isContainer));

        if (roomFile.read_setting(file,"isDoor",false).equalsIgnoreCase("true")){
            isDoor = true;
        } else {
            isDoor = false;
        }
        System.out.println("isDoor:"+String.valueOf(isDoor));

        if (roomFile.read_setting(file,"isInvisable",false).equalsIgnoreCase("true")){
            isInvisable = true;
        } else {
            isInvisable = false;
        }
        System.out.println("isInvisable:"+String.valueOf(isInvisable));

        if (roomFile.read_setting(file,"isinfinite",false).equalsIgnoreCase("true")){
            isInfinite = true;
        } else {
            isInfinite = false;
        }
        System.out.println("isInfinite:"+String.valueOf(isInvisable));

        itemList = roomFile.read_setting(file,"items",false);
        System.out.println("itemlist:"+itemList);
        StringTokenizer itemST = new StringTokenizer(itemList,";");
        while (itemST.hasMoreTokens()){
            //String test = itemST.nextToken();
            //System.out.println(test);
            newItem = ObjectFactory.CreateItem(itemST.nextToken());
            System.out.println("NewItemName:"+newItem.getItemName());
            if (newItem != null){
                items.add(newItem);
            }
        }

        String settings = roomFile.read_setting(file,"settings",false);
        StringTokenizer settingST = new StringTokenizer(settings,";");
        StringTokenizer settingToken = null;
        while (settingST.hasMoreTokens()){
            settingToken = new StringTokenizer(settingST.nextToken(),",");
            this.setSetting(settingToken.nextToken(),Integer.parseInt(settingToken.nextToken()));
                      }

    }
*/

//    public String getClassType() {
//        return "SimpleItem";
//    }

    public String getItemName() {
        return this.getName();
    }

    public String getFilename(){
        return filename;
    }
    
    public void loadDescriptionFromFile(String filename) throws FileNotFoundException{

        File file = new File(filename);
        StringBuilder description = new StringBuilder();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
            	description.append(scanner.nextLine()+"\r\n");
            }
            this.setLongDescription(description.toString());    
    }

    public void inspect(Player p) {
        String outString;
        Item currentItem;

        p.broadcast(longDescription+"\r\n");
        if (!isOpen && (isDoor || isContainer)){
            p.broadcast(this.getItemName()+" is not open.\r\n");
        }
        if (isContainer && isOpen){
            outString = this.getItemName()+" contains:\r\n";
            Enumeration<Item> e = items.elements();
            while (e.hasMoreElements()) {
                currentItem = (Item) e.nextElement();
                outString = outString + currentItem.getItemName() + "\r\n";

            }
            p.broadcast(outString);
        }
    }

    public void check(Player player){
        String checkOutput = "";

        checkOutput = "SimpleItem Setting Checklist:\r\n";
        checkOutput += "Name = "+ name + "\r\n";
        checkOutput += "long description:\r\n " + longDescription + "\r\n";
        checkOutput += "Blocking = " + blocking + "\r\n";
        checkOutput += "Hit String = " + hitString + "\r\n";
        checkOutput += "Miss String = " + missString + "\r\n";
        checkOutput += "Effect Setting Name = " + effectSettingName + "\r\n";
        checkOutput += "Effect Setting Value = " + String.valueOf(effectSettingValue) + "\r\n";
        checkOutput += "PreReq Setting Name = " + preReqSettingName + "\r\n";
        checkOutput += "PreReq Setting Value = " + String.valueOf(preReqSettingValue) + "\r\n";
        checkOutput += "isWeapon = " + String.valueOf(this.getIsWeapon()) + "\r\n";
        checkOutput += "isDoor = " + String.valueOf(isDoor) + "\r\n";
        checkOutput += "isOpen = " + String.valueOf(isOpen) + "\r\n";
        checkOutput += "autoClosing = " + String.valueOf(autoClosing) + "\r\n";
        checkOutput += "isContainer = " + String.valueOf(isContainer) + "\r\n";
        checkOutput += "isInvisable = " + String.valueOf(isInvisable) + "\r\n";
        checkOutput += "isStationary = " + String.valueOf(isStationary) + "\r\n";
        checkOutput += "isInfinite = " + String.valueOf(isInfinite) + "\r\n";


        player.broadcast(checkOutput);

    }

    public synchronized void menu(User u) throws MenuExitException{
    	boolean done = false;
    	String menuString =  "Configure item:"+this.getItemName()+"\r\n";
    		   menuString += "(01) Configure Item Name. \r\n";
    		   menuString += "(02) Configure Item Description. \r\n";
    		   menuString += "(03) Load Item Description from a File. \r\n";
    		   menuString += "(04) Which Exit is this Item Blocking? \r\n";
    		   menuString += "(05) Configure Item Effect Setting. \r\n";
    		   menuString += "(06) Configure Item Prerequisite Setting. \r\n";
    		   menuString += "(07) Configure Door flag. \r\n";
    		   menuString += "(08) Configure Container flag. \r\n";
    		   menuString += "(09) Configure Open flag. \r\n";
    		   menuString += "(10) Configure auto-close flag. \r\n";
    		   menuString += "(11) Configure invisibility flag. \r\n";
    		   menuString += "(12) Configure Stationary flag. \r\n";
    		   menuString += "(13) Configure infinite flag. \r\n";
    		   menuString += "(14) Configure items contained in this item. \r\n";
    		   menuString += "(15) Configure actions contained in this item. \r\n";
    		   menuString += "(16) Configure reaction to other item. \r\n";
    		   menuString += "(17) Configure settings contained in this item. \r\n";
    		   menuString += "(18) Save. \r\n";
    		   
        PromptForInteger p = new PromptForInteger(u, menuString, 18, 1);
        while (p.display() && !done){
        	switch (p.getResult()){
        		case 1:  configItemName(u); break;
        		case 2:  configItemDescription(u); break;
        		case 3:  configAddDescriptionFromFile(u); break;  
        		case 4:  configExitBlocking(u); break;
        		case 5:  configItemEffectSetting(u); break;
        		case 6:  configItemPrereqSetting(u); break;
        		case 7:  configItemDoor(u); break;
        		case 8: configItemContainer(u); break;
        		case 9: configItemOpen(u); break;
        		case 10: configItemAutoClose(u);break;
        		case 11: configItemInvisible(u);break;
        		case 12: configItemStationary(u);break;
        		case 13: configItemInfinite(u);break;
        		case 14: configItems(u);break;
        		case 15: configItemActions(u);break;
        		case 16: configItemReaction(u);break;
        		case 17: configItemSettings(u);break;
        		case 18: configItemSave(u);break;
        	}
        }
    }

	public void configItemReaction(User u) throws MenuExitException{
		int menuItems = 2;
		String menuString = "Configure "+this.getItemReaction()+"  action:\r\n";
		menuString += "(1) Add and configure basic reaction action \r\n";
		menuString += "(2) Add and configure advanced reaction action \r\n";
		if (this.getItemReaction() != null){
			menuString += "(3) Config existing reaction action \r\n";
			menuString += "(4) Remove existing reaction action \r\n";
			menuItems = 4;
		}
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, menuItems, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.setItemReaction(Action.configBasicAddAction(u));
				if (this.getItemReaction() != null){
					this.getItemReaction().menu(u);
				}
				break;
			case 2: 
				this.setItemReaction(Action.configAdvancedAddAction(u));
				if (this.getItemReaction() != null){
					this.getItemReaction().menu(u);
				}
				break;
			case 3: 
				this.getItemReaction().menu(u);
				break;
			case 4: 
				this.setItemReaction(null);
				break;
			}
		}
	}
    
	public boolean configItemSettings(User u) throws MenuExitException{
		boolean done = false;
		int newSettingValue = 0;
		String settingMenuString = "Current Settings:\r\n";
		       settingMenuString += this.getSettingsAsString()+"\r\n";
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (newSettingName.equals("")){
			done = false;
		} else {
			newSettingValue = this.getSettingValue(u);
			this.setSetting(newSettingName, newSettingValue);
		}
		
		// create a list of settings for reference. 
		// explain that setting to zero = removal. 
		return done;
	}
	
	public String getSettingsAsString(){
        String settingString = "";

        Iterator<String> e = settings.keySet().iterator();
        while (e.hasNext()){
            String key = (String) e.next();
            settingString = settingString + key+","+settings.get(key)+";\r\n";
        }
        return settingString;
    }
    
    public void configItemSave(User u) throws MenuExitException{
		String menuString = "Enter filename for save.\r\n\r\n";
		String fileName = "";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			fileName = World.getArea().getLibDir()+"//"+s.getResult();
			this.save(fileName);
			u.broadcast("Item saved as:"+fileName);
		} else {
			u.broadcast("Item not saved. \r\n");
		}
    }    
    
	public boolean configItems(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure "+this.getItemName()+" Items:\r\n";
		menuString += "(1) Configure existing items. \r\n";
		menuString += "(2) Add a new item. \r\n";
		menuString += "(3) Remove an item. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configItem(u);
				break;
			case 2: 
				Item i = ItemBase.configAddItem(u);
				this.setContainer(true);//if you're adding something, it's a container.
				if (i != null){
					this.addToInventory(i);
				}
				break;
			case 3: 
				this.configItemDelete(u); 
				break;
			}
		}
		return done;
	}	
	
	public void configItem(User u) throws MenuExitException{
		int count = 0;
		String menuString = "Choose an item to configure:\r\n";
		Vector<Item> items = this.getItems();
		
		for (Item i: items){
			menuString += "("+count+") "+i.getItemName()+"\r\n";
			count++;			
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger i = new PromptForInteger(u, menuString, items.size()-1, 0);
		if (i.display()) {
			items.elementAt(i.getResult()).menu(u);
		}
	}
	
	public void configItemDelete(User u) throws MenuExitException{
		
		Vector <Mob> mobs = new Vector<Mob>();
		int count = 0;
		String menuString = "Choose an item to delete:\r\n";
		
		for (Item i: this.getItems()){
				menuString += "("+count+") "+i.getItemName()+"\r\n";
				count++;
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, mobs.size()-1, 0);
		if (p.display()) {
			items.remove(p.getResult());
			u.broadcast("Selected item removed from room.\r\n");
		} else {
			u.broadcast("Room unchanged.\r\n");
		}
	}	

	public boolean configItemActions(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure "+this.getItemName()+" Actions:\r\n";
		menuString += "(1) Configure existing actions. \r\n";
		menuString += "(2) Add a new action. \r\n";
		menuString += "(3) Remove an action. \r\n";
		menuString += "(4) Change an action's verb. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configExistingAction(u);
				break;
			case 2: 
				this.configActionAdd(u); 
				break;
			case 3: 
				this.configRemoveAction(u); 
				break;
			case 4:
				this.configModifyVerb(u);
				break;
			}
		}
		return done;
	}	
	
	public void configModifyVerb(User u) throws MenuExitException{
		String menuString =  "Enter an action from the list below to change it's verb.\r\n";
		int count = 0;
		Action renamedAction = null;
		String renamedVerb = null;
		ArrayList<String> keys = new ArrayList<String>(this.actions.keySet());
		Collections.sort(keys);
		
		for (String s : keys){
			menuString += "("+count+") "+s+"\r\n";
			count++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
	       if (p.display()){   
	    	   renamedVerb = keys.get(p.getResult());
	    	   renamedAction = actions.remove(keys.get(p.getResult()));
	    	   if ( renamedAction != null){
			       menuString += "New verb:";
			       PromptForString s = new PromptForString(u, menuString);

			       if (s.display()) {
			    	   this.actions.put(s.getResult(), renamedAction);
			    	   u.broadcast("Verb changed.");
			       } else {
			    	   this.actions.put(renamedVerb, renamedAction); // put the action back on failure. 
			    	   u.broadcast("Verb not changed \r\n");
			       }
	    		   
	    	   }else {
	    		   u.broadcast("Action not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Actions unchanged.\r\n");
	       }
	}
	
	public void configActionAdd(User u) throws MenuExitException{
		String menuString =  "This will allow you to add a verb to the item, and associate it with an action. \r\n";
		       menuString += "Example: adding the new verb: <slap> allows the player to 'slap <item>'.\r\n";
		       menuString += "New verb:";
		PromptForString s = new PromptForString(u, menuString);

		if (s.display()) {
			this.actions.put(s.getResult(), Action.configAddAction(u));
				u.broadcast("Added new action to item.");
		} else {
				u.broadcast("Action not added. \r\n");
		}
	}
	
	public void configRemoveAction(User u) throws MenuExitException{	
		
		String menuString =  "Enter an action from the list below to remove it.\r\n";
		int count = 0;
		ArrayList<String> keys = new ArrayList<String>(this.actions.keySet());
		Collections.sort(keys);
		
		for (String s : keys){
			menuString += "("+count+") "+s+"\r\n";
			count++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
	       if (p.display()){
	    	   if (actions.remove(keys.get(p.getResult())) != null){
	    		   u.broadcast("Action removed.\r\n");
	    	   }else {
	    		   u.broadcast("Action not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Actions unchanged.\r\n");
	       }
	}	
	
	public void configExistingAction(User u) throws MenuExitException{	
		
		String menuString =  "Enter an action from the list below to configure it.\r\n";
		int count = 0;
		ArrayList<String> keys = new ArrayList<String>(this.actions.keySet());
		Collections.sort(keys);
		
		for (String s : keys){
			menuString += "("+count+") "+s+"\r\n";
			count++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, keys.size()-1, 0);
	       if (p.display()){
	    	   //u.broadcast("retrieving: " +keys.get(p.getResult())+" from: "+this.getItemName()+"\r\n");
	    	   if (this.actions.get(keys.get(p.getResult())) != null){
	    		   this.actions.get(keys.get(p.getResult())).menu(u);
	    	   }else {
	    		   u.broadcast("Action not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Actionss unchanged.\r\n");
	       }
	}
	
	public static Item configAddItem(User u) throws MenuExitException {
		Item result = null;
		String menuString = "Add a new item\r\n";
		menuString += "(1) Basic \r\n";
		menuString += "(2) Advanced \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				result =  ItemBase.configBasicAddItem(u);
				break;
			case 2: 
				result = ItemBase.configAdvancedAddItem(u);
				break;
			}
		}
		return result;
	}		
	
	public static Item configBasicAddItem(User u) throws MenuExitException{
		Item result = null;
		
		String menuString = "Choose a new basic item below:\r\n";
		menuString += "(1) Basic Item \r\n";
		menuString += "(2) XXXXXX \r\n";
		menuString += "(3) XXXXXX \r\n";
		menuString += "(4) XXXXXX \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				//TODO First, create menus as separate classes. this is only in item class
				//for simplicity. Second, try to make sure menus that are repeated are
				//contained in a base class, getting rid of any repeated code. Should be
				//a simple change. 
				result = ItemBase.createNewItem("gum.items.ItemBase", "DEFAULT");
				break;
			case 2: 
				System.out.println("User chose heal.");
				break;
			case 3: 
				System.out.println("User chose notify.");
				break;
			case 4: 
				System.out.println("User chose modify.");;
				break;
			}
		}
		if (result != null){
			u.broadcast("New Item Created!\r\n");
		}else {
			u.broadcast("Item Creation failed\r\n");
		}
		return result;
	}		
		
	
	public static Item configAdvancedAddItem(User u) throws MenuExitException{
		Item result = null;
		String menuString =  "Enter a filename (Example: door.xml) or a JAVA Class name (Example:gum.items.ItemBase)\r\n";
		       menuString += "GUM will try to locate the resource, and add the item to the current room.\r\n\r\n";
		       
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			result = createNewItem(s.getResult(), "DEFAULT");
			if (result != null){
				u.broadcast("New Item created with name 'DEFAULT'.\r\n");
			} else {
				u.broadcast("Failed to locate resource. Check input and retry. \r\n");
			}
		}
		return result;
	}	
	
	public void configAddDescriptionFromFile(User u) throws MenuExitException{
		
		String menuString =  "Enter a filename (Example: door_description.txt)\r\n";
		       menuString += "GUM will try to locate the resource, and copy the desctiption to the Item.\r\n\r\n";
		       
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			  try {
				this.loadDescriptionFromFile(s.getResult());
				u.broadcast("Description Loaded.\r\n");
			} catch (FileNotFoundException e) {
				u.broadcast("File Not Found!\r\n");
			}
		}
	}
	
	public static Item createNewItem(String fileName, String name) {
		Item result = null;
		Item i = ObjectFactory.CreateItem(fileName);
		if (i != null) {
			i.setName(name);
			result = i;
		} 
		return result;
	}
    
    
    public void configItemInfinite(User u) throws MenuExitException{
    	String menuString = "Is this item infinite? (note: An infinite item can be taken over and over, like water from a stream.)\r\n\r\n";
		PromptForBoolean b = new PromptForBoolean(u,menuString);
		if (b.display()){
			this.setInfinite(b.getResult()); 
			u.broadcast("Item Infinite Flag set to:"+this.getIsInfinite()+"\r\n");
		} else {
			u.broadcast("Item Infinite Flag unchanged!\r\n");
		}
    } 
    
    public void configItemStationary(User u) throws MenuExitException{
    	String menuString = "Is this item stationary? (note: A stationary item cannot be moved or taken.)\r\n\r\n";
		PromptForBoolean b = new PromptForBoolean(u,menuString);
		if (b.display()){
			this.setStationary(b.getResult());
			u.broadcast("Item Stationary Flag set to:"+this.getIsStationary()+"\r\n");
		} else {
			u.broadcast("Item Stationary Flag unchanged!\r\n");
		}
    }    
    
    public void configItemInvisible(User u) throws MenuExitException{
    	String menuString = "Is this item invisible? (note:An invisible item will not show up on the list of items in a room.)\r\n\r\n";
		PromptForBoolean b = new PromptForBoolean(u,menuString);
		if (b.display()){
			this.setIsInvisable(b.getResult()); //TODO fix the spelling of invisible
			u.broadcast("Item Invisible Flag set to:"+this.getIsInvisable()+"\r\n");
		} else {
			u.broadcast("Item Invisible Flag unchanged!\r\n");
			
		}
    }    
    
    public void configItemAutoClose(User u) throws MenuExitException{
    	String menuString = "Does this item automatically close after use? (exmple: A door that closes behind the player)\r\n\r\n";
		PromptForBoolean b = new PromptForBoolean(u,menuString);
		if (b.display()){
			this.setAutoClosing(b.getResult());
			u.broadcast("Item Auto-Close Flag set to:"+this.getAutoClosing()+"\r\n");
		} else {
			u.broadcast("Item Auto-Close Flag unchanged!\r\n");
		}
    }    
    
    public void configItemOpen(User u) throws MenuExitException{
    	String menuString = "Is this Item currently open?(Yes or No)\r\n\r\n";
		PromptForBoolean b = new PromptForBoolean(u,menuString);
		if (b.display()){
			this.setIsOpen(b.getResult());
			u.broadcast("Item Open Flag set to:"+this.getIsOpen()+"\r\n");
			//TODO figure out why some things have 'getIsOpen', and some bools don't.
		} else {
			u.broadcast("Item Open Flag unchanged!\r\n");
		}
    }     
    
    public void configItemContainer(User u) throws MenuExitException{
    	String menuString = "Is this Item a Container?(Yes or No)\r\n\r\n";
		PromptForBoolean b = new PromptForBoolean(u,menuString);
		if (b.display()){
			this.setIsContainer(b.getResult());
			u.broadcast("Item Container Flag set to:"+this.isContainer()+"\r\n");
		} else {
			u.broadcast("Item Container Flag unchanged!\r\n");
		}
    }    
    
    public void configItemDoor(User u) throws MenuExitException{
    	String menuString = "Is this Item a Door?(Yes or No)\r\n\r\n";
		PromptForBoolean b = new PromptForBoolean(u,menuString);
		if (b.display()){
			this.setIsDoor(b.getResult());
			u.broadcast("Item Door Flag set to:"+this.isDoor()+"\r\n");
		} else {
			u.broadcast("Item Door Flag unchanged!\r\n");
		}
    }       
    
    //TODO Change all the 'boolean' methods to void unless the return value is used.    
        
    
	public void configItemName(User u) throws MenuExitException {
		String menuString = "Enter a new Item name.\r\n";
		menuString += "Current item name:" + this.getItemName() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			this.setName(s.getResult());
			u.broadcast("New Item name:\r\n" + this.getItemName() + "\r\n");
		} else {
			u.broadcast("Item name unchanged. \r\n");
		}
	}    
    
	public boolean configItemDescription(User u) throws MenuExitException {
		String menuString = "Enter a new Item description.\r\n";
		menuString += "Current Item description:\r\n"
				+ this.getDescription() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Description:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the description, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		this.setDescription(result);
		return done;
	}
	

	public void configExitBlocking(User u) throws MenuExitException {

		String menuString = "Enter the direction this item is blocking.\r\n(north,south,east,west,up or down)\r\n";
		menuString += "\r\nThis item is currently blocking:" + this.blocking + "\r\n";
		PromptForString s = new PromptForString(u, menuString);

		if (s.display()) {
			this.setBlocking(s.getResult());
			u.broadcast("This item now blocks the "+this.getBlocking()+" exit. \r\n");
		} else {
			u.broadcast("User cancelled, item exit blocking not changed. \r\n");
		}
	}	
	
	public  void configItemEffectSetting(User u) throws MenuExitException{
		
		int newSettingValue = 0;
		String settingMenuString =  "This will configure a setting that will be added to the user\r\n";
		       settingMenuString += "when the user takes this item.\r\n";
		       settingMenuString += "Currently this Item sets:"+this.getEffectSettingName()+" to:"+this.getEffectSettingValue()+".\r\n";
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (newSettingName.equals("")){

		} else {
			newSettingValue = this.getSettingValue(u);
			this.setEffectSettingName(newSettingName);
			this.setEffectSettingValue(newSettingValue);
		}
		
	}
	
	public boolean configItemPrereqSetting(User u) throws MenuExitException{
		boolean done = false;
		int newSettingValue = 0;
		String settingMenuString =  "This will configure the prereq setting needed to use this item.\r\n";
		       settingMenuString += "If this item is a door, the player must have this setting to open it.\r\n";
		       settingMenuString += "Current prereq:"+this.getPreReqSettingName()+" must be:"+this.getPreReqSettingValue()+".\r\n";
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (newSettingName.equals("")){
			done = false;
		} else {
			newSettingValue = this.getSettingValue(u);
			this.setPreReqSettingName(newSettingName);
			this.setPreReqSettingValue(newSettingValue);
		
		}
		
		return done;
	}	
	
	public String getSettingName(User u) throws MenuExitException {
		String result = "";
		String menuString = "Enter a setting name.\r\n\r\n";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			result = s.getResult();
		} 
		return result;
	}	
    
    public int getSettingValue(User u) throws MenuExitException{
    	int result = 0;
    	String menuString = "Enter a new setting value (a value of zero will delete the setting).\r\n\r\n";
    	       
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		result = (i.getResult());
    	}
    	return result;
    } 		
	
    public boolean getIsWeapon() {
    	//TODO this could be optimized.
    	boolean result = false;
    	
    	for (String key : this.actions.keySet()){
    		if (actions.get(key) instanceof ActionAttack){
    			result = true;
    		}
    	}
        return result;
    }

    public boolean getIsStationary(){
        return isStationary;
    }

    public void setName(String newName) {
        name = newName;
    }

    public boolean getIsInfinite() {
        return isInfinite;
    }

    public void setIsInfinite(boolean newInfiniteSetting) {
        isInfinite = newInfiniteSetting;
    }

    public String getBlocking(){
        String out = "";
      //  if (!this.isOpen()){ // if the door is closed, then it's blocking
        if (blocking != null){
            out = blocking;
        }
      //  }
        //System.out.println("blocking:"+String.valueOf(blocking));
        return out;
    }

    public void setBlocking(String newBlocking){
        blocking = newBlocking;
    }

    public void setAutoClosing(boolean setting){
        autoClosing = setting;
    }

    public boolean getAutoClosing(){
        return autoClosing;
    }

    public void setEffectSettingName (String newName){
        effectSettingName = newName;
    }

    public String getEffectSettingName (){
        return effectSettingName;
    }

    public int getEffectSettingValue(){
        return effectSettingValue;
    }

    public void setPreReqSettingName (String newName){
        preReqSettingName = newName;
    }

    public String getPreReqSettingName (){
        return preReqSettingName;
    }

    public void setPreReqSettingValue( int newValue){
        preReqSettingValue = newValue;
    }

    public int getPreReqSettingValue(){
        return preReqSettingValue;
    }
    
    
    public boolean displayAttackMenu(User u){
		boolean done = false;
		boolean result = true;
		int count = 1;
		
		String response = "";
		String attackMenu  = "Which attack will you use?\r\n";
		attackMenu += ("(0) Previous Menu\r\n");
		Vector<Action> attacks = new Vector<Action>();
		
		//get attacks, and add them to the menu.
		for (String k : actions.keySet()){
			Action idxAction = actions.get(k); // get action at index
			if (idxAction instanceof ActionAttack){
			attacks.add(idxAction);
			attackMenu += ("("+count+") "+idxAction.getActionName()+"\r\n");
			count++;
			}
		}
		
		while (!done && !u.isDead()){	   
			u.broadcast(attackMenu);
			response = u.getMenuResponse();
			try{ 
				Integer r = new Integer(response);
				if (r != 0){
					this.setCurrentAction(attacks.elementAt(r-1));
					done = true;
				} else {
					done = true;
					result = false;
				}
			} catch (Exception e){
				u.getCurrentRoom().chat(u.getPlayerName() + " chats:" + response + "\r\n");
			}
//			if (response.equals("0")){
//				done = true;
//				result = false;
//			}else if (response.equals("1")){
//				this.attackType = 1;
//				done = true;
//			}else {
//				u.getCurrentRoom().chat(u.getPlayerName() + " chats:" + response + "\r\n");
//			}
			
		}
		
		return result;
    }
    
    public void attack (Player player, Player enemy){
    	ActionHeader header = new ActionHeader(player, enemy, this, null);
    	// If the item isn't set to a specific attack, just do the first
    	// action contained in the item the mob is holding. 
    	Iterator<String> actionList = actions.keySet().iterator();
    	if (this.getCurrentAction() != null){
    		this.getCurrentAction().perform(header);
    	}else if (actionList.hasNext()){
    		//If there is no action chosen, search through the list and grab the first attack.
    		boolean done = false;
    		while (actionList.hasNext() && !done){
    			Action cAction = actions.get(actionList.next());
    			System.out.println("Checking Action "+cAction.getActionName()+" for attack.");
    			if (cAction instanceof ActionAttack){
    				cAction.perform(header);
    				System.out.println(player.getPlayerName()+" Attacked "+enemy.getPlayerName()+" with "+cAction.getActionName());
    				done = true;
    			}
    		}
    	}else {		
    		player.broadcast("This item is not a weapon! \r\n");
    	}
    	
/*    	if (getAttackType() == null){
    		
    	}
    	
    	
    	
    	String attackString = "";
    	
    	if (this.attackType == 0){
    		//assume this is a mob using this weapon. 
    	}else if (this.attackType == 1){
    		attackString = (player.getPlayerName()+" Stabs " +enemy.getPlayerName()+"!\r\n");
    	} else {
    		attackString = (player.getPlayerName()+" Slices "+enemy.getPlayerName()+"!\r\n");
    	}
    	
    	
        int dex = player.getSetting("dex");
        int ref = enemy.getSetting("ref");

        if (dex < 1){
            dex = 1;
        }
        if (ref < 1){
            ref = 1;
        }
        if (damageRoll < 1){
            damageRoll = 1;
        }


        int attack_roll = random.nextInt(dex); // random number from 1 to 20
        int defend_roll = random.nextInt(ref);


        System.out.println("attack roll:"+String.valueOf(attack_roll));
        System.out.println("defend roll:"+String.valueOf(defend_roll));
        int damage = random.nextInt(damageRoll)+damageBase;
        System.out.println("damage:"+String.valueOf(damage));
        if(attack_roll > defend_roll){
            enemy.setSetting("hitpoints",enemy.getSetting("hitpoints") - damage);
            player.getCurrentRoom().chat(attackString+"\r\n");
            player.getCurrentRoom().chat(this.hitString+"\r\n");
        } else {
            player.getCurrentRoom().chat(missString+"\r\n");     
        }
*/
    }

    public void open(Player player) { // build on this when implementing locks
        // current system looks that key value is greater than or equal to prereq.
        // more complex systems could be added for looking
        // for specific values.

        int pvalue = player.getSetting(preReqSettingName);
        if (pvalue >= preReqSettingValue) {
            if (isContainer || isDoor) {
                player.broadcast(this.getItemName() + " is now opened\r\n");
                isOpen = true;
            }
        }else {
            player.broadcast(this.getItemName()+" is locked.");
        }
   }

    public void close(Player player) {
        if (isContainer || isDoor){
            player.broadcast(this.getItemName() + " is now closed\r\n");
            isOpen = false;
        }
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public Item getItemByName(String itemName){
        //System.out.println("getItemByName:"+itemName);
        boolean Found = false;
        Item o_item = null;
        if (isContainer && isOpen){
            Enumeration<Item> e = items.elements();
            while (e.hasMoreElements() && !Found) {
                Item item = (Item) e.nextElement();
                if (item.getItemName().equals(itemName)) {
                    //System.out.println("getItemByName...2:"+item.getItemName());
                    o_item = item;
                    Found = true;
                }
            }
        }


        return o_item;
    }
    
    public Item getItemByNameAndPosition(String itemName, int pos){
        //System.out.println("getItemByName:"+itemName);
        boolean Found = false;
        Item o_item = null;
        if (isContainer && isOpen){
            Enumeration<Item> e = items.elements();
            while (e.hasMoreElements() && !Found) {
                Item item = (Item) e.nextElement();
                if (item.getItemName().equals(itemName)) {
                    //System.out.println("getItemByName...2:"+item.getItemName());
                	 if (pos <= 1){
                		 o_item = item;
                		 Found = true;
                	 } else {
                		 pos -= 1;
                	 }
                }
            }
        }
        return o_item;
    }

    public Item removeItem(Item item){
        // only return this item if was still in the system
        if (isContainer && isOpen){
            if (!items.remove(item)) {
                item = null;
            }
        }
        return item;
    }

//    public Item take(Item takenItem) {
//        return null;
//    }

   public boolean take(Player p){
	   boolean result = false;
	   
       if (this.getEffectSettingName() != ""){
           p.setSetting(this.getEffectSettingName(),(p.getSetting(this.getEffectSettingName())+this.getEffectSettingValue()));
       }
       if (!this.getIsStationary()) {
           if (!this.getIsInfinite()){
        	   this.getItemContainer().removeItem(this);
               p.addToInventory(this); 
               //items.add(taken_item);
           } else {
        	        
        	   try {
        		   /* proper serialization method
        	   ByteArrayOutputStream os = new ByteArrayOutputStream();
               ObjectOutputStream oos = new ObjectOutputStream(os);
               oos.writeObject(this);
               oos.flush();
               ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
               ObjectInputStream decoder = new ObjectInputStream(is);
               */
        		ItemContainer temp = this.getItemContainer();
           		this.setItemContainer(null);   
        		ByteArrayOutputStream os = new ByteArrayOutputStream();  
           		XMLEncoder encoder = new XMLEncoder(os);
           		encoder.writeObject(this);
           	
           		encoder.close();
               ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
             
               XMLDecoder decoder = new XMLDecoder(is);
               Item new_item = (Item)decoder.readObject();
               decoder.close();
     
               this.setItemContainer(temp);
               
               new_item.setIsInfinite(false);
               p.addToInventory(new_item);
              
   			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           }   
           result = true;
       }
       return result;
   }

   public void drop(Player p){
       if (this.getEffectSettingName() != ""){
           int newSetting = p.getSetting(this.getEffectSettingName())-this.getEffectSettingValue();
           System.out.println("drop setting>"+this.getEffectSettingName()+":"+newSetting);
           p.setSetting(this.getEffectSettingName(),newSetting);
           if (p.getRightHand() == this) {
               p.setRightHand(null);
           } else if (p.getLeftHand() == this) {
               p.setLeftHand(null);
           } 
       }
   }

   public boolean hold(){ // can this be held?
       return true;
   }

    public void put(Item putItem) {
        if (isContainer && isOpen){
            //items.add(putItem); 
            this.addToInventory(putItem);
        }
    }

    public void save(String fileName){
    	try{
    		ItemContainer temp = this.getItemContainer();
    		this.setItemContainer(null);
    		System.out.println("saving Item:"+this.getItemName()+" as:"+fileName);
    		FileOutputStream os = new FileOutputStream(fileName);
    		XMLEncoder encoder = new XMLEncoder(os);
    		encoder.writeObject(this);
    		encoder.close();
    		this.setItemContainer(temp);
    	} catch (Exception e){
    		e.printStackTrace();
    	}	
    }
    
    
    public boolean getIsInvisable() {
        return isInvisable;
    }

    public void setIsInvisable(boolean setting) {
        isInvisable = setting;
    }

    public void setIsStationary(boolean setting) {
        isStationary = setting;
    }

    public void setDescription(String newDescription) {
        longDescription = newDescription;
    }

    public String getDescription(){
        return longDescription;
    }

    public void addToDescription(String newDescription){
        System.out.println("adding to description");

        longDescription += newDescription;
    }

    public void setHitString(String newHitString) {
        hitString = newHitString;
    }

    public void setMissString(String newMissString) {
        missString = newMissString;
    }

    public void setIsDoor(boolean setting) {
        isDoor = setting;
    }

    public void setIsOpen(boolean setting) {
        isOpen = setting;
    }

    public void setIsContainer(boolean setting) {
        isContainer = setting;
    }

    public void setSetting(String setting, int value){
        Integer intValue = new Integer(value);
        settings.put(setting,intValue); // should create new, or replace
    }

    public int getSetting(String setting){
        Integer returnValue = null;
        returnValue = (Integer)settings.get(setting);
        if (returnValue == null){
            returnValue = new Integer(0);
        }
        return returnValue.intValue();
    }

   public String getHitString(){
       return this.hitString;
   }

   public String getMissString(){
       return this.missString;
   }

    public String getLongDescription() {
	return longDescription;
}
public void setLongDescription(String longDescription) {
	this.longDescription = longDescription;
}
public boolean isDoor() {
	return isDoor;
}
public void setDoor(boolean isDoor) {
	this.isDoor = isDoor;
}
public boolean isContainer() {
	return isContainer;
}
public void setContainer(boolean isContainer) {
	this.isContainer = isContainer;
}
public Action getCurrentAction() {
	return currentAction;
}
public void setCurrentAction(Action currentAction) {
	this.currentAction = currentAction;
}
public Vector<Item> getItems() {
	return items;
}
public void setItems(Vector<Item> items) {
	this.items = items;
}

public ItemContainer getItemContainer() {
	return itemContainer;
}

public void setItemContainer(ItemContainer itemContainer) {
	this.itemContainer = itemContainer;
}

public HashMap<String, Action> getActions() {
	return actions;
}
public void setActions(HashMap<String, Action> actions) {
	this.actions = actions;
}
public Random getRandom() {
	return random;
}
public void setRandom(Random random) {
	this.random = random;
}
public String getName() {
	return name;
}
public void setFilename(String filename) {
	this.filename = filename;
}

public void setItemReaction(Action itemReaction) {
	this.itemReaction = itemReaction;
}

public Action getItemReaction() {
	return itemReaction;
}

public void setStationary(boolean isStationary) {
	this.isStationary = isStationary;
}
public void setOpen(boolean isOpen) {
	this.isOpen = isOpen;
}
public void setInvisable(boolean isInvisable) {
	this.isInvisable = isInvisable;
}
public void setInfinite(boolean isInfinite) {
	this.isInfinite = isInfinite;
}
public void setEffectSettingValue(int effectSettingValue) {
	this.effectSettingValue = effectSettingValue;
}
public void setSettings(HashMap<String, Integer> settings) {
	this.settings = settings;
}

public HashMap<String, Integer> getSettings() {
	return settings;
}

//public String getSettings(){
//        String settingString = "";
//
//        Iterator<String> e = settings.keySet().iterator();
//        while (e.hasNext()){
//            String key = (String) e.next();
//            settingString = settingString + key+","+settings.get(key)+";\r\n";
//        }
//        return settingString;
//        }
/*
    public void save(String rootName) { // implement this
        FileHandler itemFile = new FileHandler();
        String itemList = "";

        String fileName = rootName+"."+this.getItemName();
        System.out.println("saving Itemname:"+fileName);
        itemFile.clear_file(fileName);

        //must overload this to include item class
        itemFile.write_setting(fileName,"name",name);
        itemFile.write_setting(fileName,"longDescription",longDescription);
        itemFile.write_setting(fileName,"blocking",blocking);
        itemFile.write_setting(fileName,"hitstring",hitString);
        itemFile.write_setting(fileName,"missstring",missString);
        itemFile.write_setting(fileName,"effectSettingName",effectSettingName);
        itemFile.write_setting(fileName,"effectSettingValue",String.valueOf(effectSettingValue));
        itemFile.write_setting(fileName,"preReqSettingName",preReqSettingName);
        itemFile.write_setting(fileName,"preReqSettingValue",String.valueOf(preReqSettingValue));
        itemFile.write_setting(fileName,"damagebase",String.valueOf(damageBase));
        itemFile.write_setting(fileName,"damageroll",String.valueOf(damageRoll));
        itemFile.write_setting(fileName,"settings",this.getSettings());

        if (isWeapon){
            itemFile.write_setting(fileName,"isWeapon","TRUE");
        } else {
            itemFile.write_setting(fileName,"isWeapon","FALSE");
        }

        if (autoClosing){
            itemFile.write_setting(fileName,"autoClosing","TRUE");
        } else {
            itemFile.write_setting(fileName,"autoClosing","FALSE");
        }

        if (isStationary){
            itemFile.write_setting(fileName,"isStationary","TRUE");
        } else {
            itemFile.write_setting(fileName,"isStationary","FALSE");
        }

        if (isDoor){
            itemFile.write_setting(fileName,"isDoor","TRUE");
        } else {
            itemFile.write_setting(fileName,"isDoor","FALSE");
        }

        if (isOpen){
            itemFile.write_setting(fileName,"isOpen","TRUE");
        } else {
            itemFile.write_setting(fileName,"isOpen","FALSE");
        }

        if (isContainer){
            itemFile.write_setting(fileName,"isContainer","TRUE");
        } else {
            itemFile.write_setting(fileName,"isContainer","FALSE");
        }

        if (isInvisable){
            itemFile.write_setting(fileName,"isInvisable","TRUE");
        } else {
            itemFile.write_setting(fileName,"isInvisable","FALSE");
        }

        if (isInfinite){
            itemFile.write_setting(fileName,"isinfinite","TRUE");
        } else {
            itemFile.write_setting(fileName,"isinfinite","FALSE");
        }

        rootName = rootName + "." + this.getItemName();
        Enumeration<Item> e = items.elements();
        while (e.hasMoreElements()){
            Item i = (Item)e.nextElement();
            itemList = itemList + rootName + "." + i.getItemName() + ";\r\n";
            i.save(rootName);
        }
        itemFile.write_setting(fileName,"items",itemList);
        itemFile.close_file();

    }
*/
    


	public void parseItem(StringTokenizer st, Player player) {
        String token = "";

        if (st.hasMoreTokens()) {
            token = st.nextToken();
        }
        if (token.equals("name")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            }
            this.setName(token);
        } else if (token.equals("description")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken("\r");
            }
            this.setDescription(token + "\r\n");
        } else if (token.equals("description+")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken("\r");
            }
            this.addToDescription(token + "\r\n");
        } else if (token.equals("blocking")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            }
            if (token.equalsIgnoreCase("north") ||
                token.equalsIgnoreCase("south") ||
                token.equalsIgnoreCase("east")  ||
                token.equalsIgnoreCase("west")  ||
                token.equalsIgnoreCase("up")    ||
                token.equalsIgnoreCase("down")){
                this.setBlocking(token);
            } else {
                player.broadcast("unknown direction\r\n");
            }
            } else if (token.equals("hitstring")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken("\r");
                }
                this.setHitString(token);
            } else if (token.equals("misstring")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken("\r");
            }
            this.setMissString(token);
            }else if (token.equals("door")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                }
                if (token.equalsIgnoreCase("true")){
                    this.setIsDoor(true);
                } else if (token.equalsIgnoreCase("false")){
                    this.setIsDoor(false);
                } else {
                    player.broadcast("isDoor must be either true or false\r\n");
                }
            }else if (token.equals("open")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                }
                if (token.equalsIgnoreCase("true")){
                    this.setIsOpen(true);
                } else if (token.equalsIgnoreCase("false")){
                    this.setIsOpen(false);
                } else {
                    player.broadcast("isOpen must be either true or false\r\n");
                }
            }else if (token.equals("container")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                }
                if (token.equalsIgnoreCase("true")){
                    this.setIsContainer(true);
                } else if (token.equalsIgnoreCase("false")){
                    this.setIsContainer(false);
                } else {
                    player.broadcast("isContainer must be either true or false\r\n");
                }
            }else if (token.equals("invisable")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                }
                if (token.equalsIgnoreCase("true")){
                    this.setIsInvisable(true);
                } else if (token.equalsIgnoreCase("false")){
                    this.setIsInvisable(false);
                } else {
                    player.broadcast("isInvisable must be either true or false\r\n");
                }
            }else if (token.equals("infinite")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                }
                if (token.equalsIgnoreCase("true")){
                    this.setIsInfinite(true);
                } else if (token.equalsIgnoreCase("false")){
                    this.setIsInfinite(false);
                } else {
                    player.broadcast("isInfinite must be either true or false\r\n");
                }
            }else if (token.equals("autoclose")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                }
                if (token.equalsIgnoreCase("true")){
                    this.setAutoClosing(true);
                } else if (token.equalsIgnoreCase("false")){
                    this.setAutoClosing(false);
                } else {
                    player.broadcast("autoclose must be either true or false\r\n");
                }
            }else if (token.equals("stationary")) {
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                }
                if (token.equalsIgnoreCase("true")){
                    this.setIsStationary(true);
                } else if (token.equalsIgnoreCase("false")){
                    this.setIsStationary(false);
                } else {
                    player.broadcast("isStationary must be either true or false\r\n");
                }
            } else if (token.equals("effectsetting")) {
                if (st.hasMoreTokens()) {
                    this.setEffectSettingName(st.nextToken());
                }
            }else if (token.equals("effectvalue")){
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                } else {
                    player.broadcast("expected integer\r\n");
                }
                try{
                    this.setEffectSettingValue(Integer.parseInt(token));
                }catch (Exception e){
                    player.broadcast("error parsing int value.\r\n");
                }
            } else if (token.equals("prereqsetting")) {
                if (st.hasMoreTokens()) {
                    this.setPreReqSettingName(st.nextToken());
                }
            } else if (token.equals("prereqvalue")){
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                } else {
                    player.broadcast("expected integer\r\n");
                }
                try{
                    this.setPreReqSettingValue(Integer.parseInt(token));
                }catch (Exception e){
                    player.broadcast("error parsing int value.\r\n");
                }
            }
    }

    public boolean use(Player p, String command, StringTokenizer st) {
    	Player targetPlayer = null;
    	ItemBase   targetItem = null;
    	Action commandAction = actions.get(command);
    	boolean result = true;
    	
    	//if (st.hasMoreTokens()){
			//TODO find a better way to remove prepositions.
		//	st.nextToken();
		//}
    	if (st.hasMoreTokens()){
    		String temp = st.nextToken("\r\n");
    		System.out.println("test:"+temp);
    		StringTokenizer stBackup = new StringTokenizer(temp);
			st = new StringTokenizer(temp);
			//TODO this has to be fixed once I move ItemBase to be Item.
			targetItem = (ItemBase)UserParser.parseForItem(stBackup, p, false);
			System.out.println(targetItem);
    		if (targetItem == null){ // if not an item, look for a player.
    			String playerName = st.nextToken();
    			targetPlayer = p.getCurrentRoom().getPlayerByName(playerName);
    			System.out.println("searched for:"+playerName);
    		}
    	}
    	if (commandAction != null){
    		ActionHeader header = new ActionHeader(p, targetPlayer, this, targetItem);
    		commandAction.perform(header);
    		//If the 'targetItem' is not equal to null, perform modified
    		//header on the item's reaction action for responding to being targeted. 
    		
    		if ((targetItem != null)){
    			if (targetItem.getItemReaction() != null){
    				//the 'target Item' becomes the primary item! This is, hopefully, less confusing than the other way. 
    				System.out.println("Calling reaction action for:"+targetItem.getItemName());
    				ActionHeader reactionHeader = new ActionHeader(p, targetPlayer, targetItem,this );
    				targetItem.getItemReaction().perform(reactionHeader);
    			}
    		}
    	
    		
    	}else {
    		result = false;
    		p.broadcast("you can't"+' '+command+" the "+this.getItemName());
    	}
    	
    	return result;
    }

}
