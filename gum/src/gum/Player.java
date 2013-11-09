package gum;
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
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.actions.player.ActionPlayerAttack;
import gum.actions.player.ActionPlayerChat;
import gum.actions.player.ActionPlayerConfig;
import gum.actions.player.ActionPlayerEmote;
import gum.actions.player.ActionPlayerFollow;
import gum.actions.player.ActionPlayerInventory;
import gum.actions.player.ActionPlayerLook;
import gum.actions.player.ActionPlayerMap;
import gum.actions.player.ActionPlayerMessages;
import gum.actions.player.ActionPlayerMission;
import gum.actions.player.ActionPlayerMove;
import gum.actions.player.ActionPlayerPut;
import gum.actions.player.ActionPlayerQuit;
import gum.actions.player.ActionPlayerStats;
import gum.actions.player.ActionPlayerTake;
import gum.actions.player.ActionPlayerTalk;
import gum.items.Drug;
import gum.items.Item;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.PromptForString;

import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

public abstract class Player extends Thread implements ItemContainer  {
	
        //settings
        private Room myCurrentRoom;
        private String playerName;
        private String playerDescription;
        private String blocking;
        private String following = "none"; // name of the character you are following.
        private BattleHandler battleHandler = null;
        private String command = ""; // keep track of the command the user is processing. TODO add this to a log. 
        private StringTokenizer commandST = null;
        
        //fighting
        private boolean dead = false;
        private boolean fighting;
        private boolean escape;
        // I did it this way so a player might eventually get multiple 'intentions' and be able to heal one player, and attack another,ect.
        private Item equipTarget = null;
        private Item useTarget = null; 
        private Player attackTarget = null;
        
        //flags 
        //TODO get rid of javascript support code
        private boolean javascript;

        //containers
        protected Item rightHand; // item in right hand
        protected Item leftHand;  // item in left hand
        private Vector<Item> items = new Vector<Item>(); // all items
        private Vector<Drug> drugs = new Vector<Drug>(); // all drugs and poison effecting player
        private HashMap<String, Integer> settings = new HashMap<String, Integer>(); // setting map
        private HashMap<String,Action> playerCommands = new HashMap<String,Action>();

        private Random random;

        //private Player body_double;
        
        private Integer initiative = 0;

    public Player() {
        random = ObjectFactory.getRandObject();
        // set flags to defaults
        this.setFightingFlag(false);
        this.setEscape(false);
        this.setBlocking("");
        equipTarget = null;
        useTarget = null; 
        attackTarget = null;
        
        //item interaction commands
        playerCommands.put("take", new ActionPlayerTake());
        playerCommands.put("get", new ActionPlayerTake());
        playerCommands.put("put", new ActionPlayerPut());
        playerCommands.put("look", new ActionPlayerLook());
        
        //movement commands
        playerCommands.put("north", new ActionPlayerMove());
        playerCommands.put("n", new ActionPlayerMove());
        playerCommands.put("south", new ActionPlayerMove());
        playerCommands.put("s", new ActionPlayerMove());
        playerCommands.put("east", new ActionPlayerMove());
        playerCommands.put("e", new ActionPlayerMove());
        playerCommands.put("west", new ActionPlayerMove());
        playerCommands.put("w", new ActionPlayerMove());
        playerCommands.put("up", new ActionPlayerMove());
        playerCommands.put("u", new ActionPlayerMove());
        playerCommands.put("down", new ActionPlayerMove());
        playerCommands.put("d", new ActionPlayerMove());
        
        //communtication commands
        playerCommands.put("chat", new ActionPlayerChat());
        playerCommands.put("emote", new ActionPlayerEmote());
        playerCommands.put("talk", new ActionPlayerTalk());
        playerCommands.put("tell", new ActionPlayerTalk());
        
        //misc commands
        playerCommands.put("stats", new ActionPlayerStats());
        playerCommands.put("mission", new ActionPlayerMission());
        playerCommands.put("attack", new ActionPlayerAttack());
        playerCommands.put("follow", new ActionPlayerFollow());
        playerCommands.put("quit", new ActionPlayerQuit());
        playerCommands.put("inv", new ActionPlayerInventory());
        playerCommands.put("inventory", new ActionPlayerInventory());
        playerCommands.put("config", new ActionPlayerConfig());
        playerCommands.put("configure", new ActionPlayerConfig());
        playerCommands.put("map", new ActionPlayerMap());
        playerCommands.put("mail", new ActionPlayerMessages());
    }
    
    // process a 'tick'. 
    public void process(){
    	this.heal();
    	this.processDrugs();
    }

    public void performAction(String actionName){
    	
    	Action action = this.getPlayerCommands().get(actionName);
    	if (action != null){
    		ActionHeader header = new ActionHeader(this,null,null,null);
    		action.perform(header);
    	}
    
    }
    
    public Vector<Item> getItems(){
        return items;
    }

    public Vector<Drug> getDrugs(){
        return drugs;
    }

    public void setItems(Vector<Item> new_items){
        items = new_items;
    }

    public HashMap<String, Action> getPlayerCommands() {
		return playerCommands;
	}

	public void setPlayerCommands(HashMap<String, Action> playerCommands) {
		this.playerCommands = playerCommands;
	}

	public void loadItems(String itemList){
        Item newItem = null;
        StringTokenizer itemST = new StringTokenizer(itemList,";");
        while (itemST.hasMoreTokens()){
            newItem = ObjectFactory.CreateItem(itemST.nextToken());
            // newItem = new SimpleItem(itemST.nextToken());
            if (newItem != null){
                //items.add(newItem);
                this.addToInventory(newItem);
            }
        }
    }

    public void takeRandomExit(){
        int roll = random.nextInt(6)+1; // random number from 1 to 6
        Vector<String> exitList = this.getCurrentRoom().getExitList();

        String dir = (String)exitList.elementAt(roll % exitList.size());
        System.out.println("random move to:"+dir);
        this.move(dir);

    }

    public int roll(int diceSides){
    	int result = 0;
    	if (diceSides > 0){
    		result = random.nextInt(diceSides)+1;
    	}
    	return result;
    }

    public void setSetting(String setting, int value){
        Integer intValue = new Integer(value);
        if (value == 0){
            settings.remove(setting);
        } else {
            settings.put(setting, intValue); // should create new, or replace
        }
    }

    public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public int getSetting(String setting){
        Integer returnValue = null;
        returnValue = (Integer)settings.get(setting);
        if (returnValue == null){
            returnValue = new Integer(0);
        }
        return returnValue.intValue();
    }

    protected void setCurrentRoom(Room r){
        myCurrentRoom = r;
    }

    public Room getCurrentRoom(){
        return myCurrentRoom;
    }

    public String getPlayerName(){
        return playerName;
    }

    public void setPlayerName(String newName){
        playerName = newName;
    }

    public Item getRightHand(){
        return rightHand;
    }
    
    public void setRightHand(Item newItem){
        rightHand = newItem;
    }
    
    public Item getLeftHand(){
        return leftHand;
    }
    
    public void setLeftHand(Item newItem){
        leftHand = newItem;
    }    

    public void setFightingFlag(boolean newFightingFlag){
        fighting = newFightingFlag;
    }

    public boolean getFightingFlag(){
        return fighting;
    }

    public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public void setjavascript(boolean newJSFlag){
        javascript = newJSFlag;
    }

    public boolean getjavascript(){
        return javascript;
    }

    public void setFollowing(String following){
        this.following = following;
    }

    public String getFollowing(){
        return this.following;
    }    
    
	public String getPlayerDescription() {
		return playerDescription;
	}

	public void setPlayerDescription(String playerDescription) {
		this.playerDescription = playerDescription;
	}

	public boolean configDescription(User u) throws MenuExitException {
		String menuString = "Enter a new description.\r\n";
		menuString += "Current description:\r\n"
				+ this.getPlayerDescription() + "\r\n";
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
		this.setPlayerDescription(result);
		return done;
	}

    public void addToInventory(Item i){
        items.add(i);
        i.setItemContainer(this);
    }

    public Item removeItem(Item item){
        // only return this item if was still in the system
            if (!items.remove(item)) {
                item = null;
            }
        return item;
    }
    
    public void talk (String target, String message){
        Player talkTarget = this.getCurrentRoom().getPlayerByName(target);
        if (talkTarget == null){
            this.broadcast("talk to who?\r\n");
        } else{
            talkTarget.broadcast(this.getPlayerName() + " says:" + message);
        }
    }

    public void destroyMob(String mobName){
        Player mob =  null;

        mob = this.getCurrentRoom().getPlayerByName(mobName);
        if (mob != null){
            this.getCurrentRoom().removePlayer(mob);
        } else {
            this.broadcast("cannot find mob for removal\r\n");
        }
    }

    public void destroy(String itemName){
        Item destroyedItem;
        destroyedItem = myCurrentRoom.getItemByName(itemName);
        destroyedItem = myCurrentRoom.removeItem(destroyedItem);
        if (destroyedItem != null){
            broadcast("Item destroyed\r\n");
        } else {
            broadcast("Cannot find item\r\n");
        }
    }

    public void give (Item itemGiven, String playerName){
        // This does not set effect settings, because it
        // does not allow a player to refuse an item.
        // so, you have to drop and re-take the item to activate.

        Player playerRecieving;

        
        playerRecieving = myCurrentRoom.getPlayerByName(playerName);

        if (this.items.contains(itemGiven)){
        
        
        	if ((playerRecieving != null)){
        		itemGiven.drop(this);
        		if (rightHand == itemGiven) {
        			rightHand = null;
        		}
        		if (leftHand == itemGiven) {
        			leftHand = null;
        		}
        		this.items.remove(itemGiven);
        		//playerRecieving.items.add(itemGiven);
        		playerRecieving.addToInventory(itemGiven);
        	} else {
        		this.broadcast("Can't find "+playerName+" in this room!\r\n");
        	}
        } else {
        	this.broadcast("You must be holding an item to give it to someone!");
        }
    }

 /*   public void remove(String itemName, String playerName){
        if (this.getSetting("builder") != 1){
            this.broadcast("Only builders may use this command!\r\n");
        } else{
            Item itemTaken;
            Player playerEffected;

            playerEffected = myCurrentRoom.getPlayerByName(playerName);
            itemTaken = playerEffected.getItemByName(itemName);


            if (playerEffected == null){
                this.broadcast("Can't find "+playerName+" in this room!\r\n");
            }
            if (itemTaken == null){
                this.broadcast("Can't find "+itemName+" in your inventory!\r\n");
            }
            if ((playerEffected != null) && (itemTaken != null)){
                playerEffected.drop(itemTaken);
            }
        }
    } */

    public void stats(){
        broadcast("=============================STATS==============================\r\n\r\n");
        broadcast("\t\tStrength:"+getSetting("str")+"\tIntelligence:"+getSetting("int")+"\r\n");
        broadcast("\t\tReflex:"+getSetting("ref")+"\tDexterity:"+getSetting("dex")+"\r\n");
        broadcast("\t\tFollowing:"+this.following+"\r\n");
        broadcast("\t\tHit Points:"+getSetting("hitpoints")+"\r\n");
        broadcast("\r\n=============================SKILLS=============================\r\n\r\n");
        Vector<String> skillList = World.getArea().getSkillList();
        for (String skill : skillList){
			broadcast("\t"+skill+"\t"+getSetting(skill)+"\r\n");
		}
        broadcast("\r\n============================MISSIONS============================\r\n\r\n");
        HashMap<String,String> missions = World.getArea().getMissionMap();
        for (String m : missions.keySet()){
        		if (this.getSetting(m) == 1){
        			this.broadcast(missions.get(m)+"\r\n");
	    	    }
        }
        
    }

    public void missions(){
        broadcast("\r\n========================CURRENT MISSIONS========================\r\n\r\n");
        HashMap<String,String> missions = World.getArea().getMissionMap();
        for (String m : missions.keySet()){
        		if (this.getSetting(m) == 1){
        			this.broadcast(missions.get(m)+"\r\n");
	    	    }
        }
        broadcast("\r\n=======================COMPLETE MISSIONS========================\r\n\r\n");
        for (String m : missions.keySet()){
        		if (this.getSetting(m) == 2){
        			this.broadcast(missions.get(m)+"\r\n");
	    	    }
        }
        
    }    
    
    public void take (String item_name, int pos){
        //Item taken_item;
    	//TODO clean this up as soon as Item/ItemBase issues are refactored. 
        ItemBase taken_item = (ItemBase)myCurrentRoom.getItemByNameAndPosition(item_name,pos);
        if (taken_item != null){
        	ActionHeader header = new ActionHeader(this,null,taken_item,null);
        	Action commandAction = taken_item.getActions().get("take");
        	if (!(commandAction == null)){
        		commandAction.perform(header);
           // if (taken_item.take(this)){
 /*               if (!taken_item.getIsStationary()) {
                    if (!taken_item.getIsInfinite()){
                        taken_item = myCurrentRoom.removeItem(taken_item);
                        this.addToInventory(taken_item); 
                        //items.add(taken_item);
                    } else {
                        //if (taken_item.getFilename().equals("")){
                        //    this.broadcast("Cannot take this item unti bulder runs 'saveimage'!");
                        //}
                        //Item new_item = ObjectFactory.CreateItem(taken_item.getFilename());
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        XMLEncoder encoder = new XMLEncoder(os);
                        encoder.writeObject(taken_item);
                        encoder.close();
                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                        XMLDecoder decoder = new XMLDecoder(is);
                        Item new_item = (Item)decoder.readObject();
              
                        new_item.setIsInfinite(false);
                        this.addToInventory(new_item);
                        //items.add(new_item);
                    }
                    if (taken_item == null) {
                        broadcast("Cannot find " + item_name + " here!\r\n");
                    } else{
                        this.getCurrentRoom().chat(this.getPlayerName() + " takes "+taken_item.getItemName() + "\r\n");
                    } */
   //         	this.getCurrentRoom().chat(this.getPlayerName() + " took "+taken_item.getItemName()+ "\r\n");
   //             } else {
   //                 this.broadcast("You can't take that!\r\n");
                }
        } else {
            broadcast("Cannot find " + item_name + " here!\r\n");
        }
    }

    public void open (Item openItem){
        //Item open_item;
     //   open_item = current_room.getItemByName(item_name);
     //   if (open_item == null){
     //       open_item = this.getItemByName(item_name);
     //   }
     //   if (open_item != null){
            openItem.open(this);
     //   } else {
     //       broadcast("Cannot find " + item_name + " here!\r\n");
     //   }
    }

    public void close (Item closeItem){
     //   Item close_item;
     //   close_item = current_room.getItemByName(item_name);
     //   if (close_item == null){
     //       close_item = this.getItemByName(item_name);
     //   }
     //   if (close_item != null){
            closeItem.close(this);
     //   } else {
     //       broadcast("Cannot find " + item_name + " here!\r\n");
     //   }
    }


    public void stow(Item item){
        if (this.items.contains(item)){
        	if ((rightHand == item || leftHand == item)){
        		//items.removeElement(item);
        		//this.addToInventory(item);
        		if (this.getRightHand() == item) {
                    this.setRightHand(null);
                } else if (this.getLeftHand() == item) {
                    this.setLeftHand(null);
                }
        		this.getCurrentRoom().chat(this.getPlayerName() + " stowed "+ item.getItemName() + "\r\n");
        	}
    } else {
    	this.broadcast("You must be holding an item to stow it");
    }
    }
    
 /*   public void drop(Item item){ // change to use 'get item by name'
        //boolean found = false;
        
        //Item item;
        //Enumeration e = items.elements();
        //while (e.hasMoreElements() && !found){
        //    item = (Item)e.nextElement();
        //    if (item.getItemName().equals(itemName)){
        if (this.items.contains(item)){

                if (item.drop(this)){
                    items.removeElement(item);
                    myCurrentRoom.addToInventory(item);
                    this.getCurrentRoom().chat(this.getPlayerName() + " dropped "+ item.getItemName() + "\r\n");
                }
        } else {
        	this.broadcast("You must be holding an item to drop it");
        }
          //      found = true;
          //  }
       // }
       // if (!found){
       //     broadcast ("You can't find "+itemName+"\r\n");
       // } else {
       //     this.getRoom().chat(this.getPlayerName() + " dropped "+ itemName + "\r\n");
       // }
    } */

    public Item getItemByName(String itemName){
        boolean Found = false;
        Item o_item = null;

        Enumeration<Item> e = items.elements();

        while (e.hasMoreElements() && !Found){
             Item item = (Item)e.nextElement();
             System.out.println("person get item:"+item.getItemName());
             if (item.getItemName().equals(itemName)){
                 o_item = item;
                 Found = true;
             }
        }
        return o_item;
    }
    
    public Item getItemByNameAndPosition(String itemName, int pos){
        boolean Found = false;
        Item o_item = null;

        Enumeration<Item> e = items.elements();

        while (e.hasMoreElements() && !Found){
             Item item = (Item)e.nextElement();
             System.out.println("person get item:"+item.getItemName());
             if (item.getItemName().equals(itemName)){
            	 if (pos <= 1){
            		 o_item = item;
            		 Found = true;
            	 } else {
            		 pos -= 1;
            	 }
             }
        }
        return o_item;
    }
    
    

    public void holdRight(Item item){
        if (this.items.contains(item)){
                if (item.hold()) {
                		if (leftHand != item){
                			rightHand = item;
                		} else{
                			this.broadcast("You are already holding that!\r\n");
                		}
                	
                    if (this.getCurrentRoom() != null){
                        this.getCurrentRoom().chat(this.getPlayerName() + " holds " +
                                            item.getItemName() +
                                            "\r\n");
                    }
                }
              
            } else {
            	this.broadcast("item must be in your inventory to hold it!");
            }
    
       }  	
    
    
    public void holdLeft(Item item){
        
        if (this.items.contains(item)){
                if (item.hold()) {
                		if (rightHand != item){
                			leftHand = item;
                		} else{
                			this.broadcast("You are already holding that!\r\n");
                		}
                	
                    if (this.getCurrentRoom() != null){
                        this.getCurrentRoom().chat(this.getPlayerName() + " holds " +
                                            item.getItemName() +
                                            "\r\n");
                    }
                }
              
            } else {
            	this.broadcast("item must be in your inventory to hold it!");
            }
    }
    public void listInventory(){
        String outString;
        Item currentItem;
        outString = "you Carry:\r\n";
        Enumeration<Item> e = items.elements();
        while (e.hasMoreElements()){
           currentItem = (Item)e.nextElement();
           outString = outString + currentItem.getItemName()+"\r\n";

        }
        if (!(rightHand == null)){ // if the right hand isn't empty
            outString = outString + "right hand:"+rightHand.getItemName()+"\r\n";
        }
        if (!(leftHand == null)){ // if the right hand isn't empty
            outString = outString + "left hand:"+leftHand.getItemName()+"\r\n";
        }
        broadcast(outString);
    }

    public void heal(){
    	//TODO make healing based on constitution
    	
        int maxHP = 0;
        int newHP = 0;

        maxHP = this.getSetting("maxhp");
        newHP = this.getSetting("hitpoints")+1;
        if (newHP < maxHP){
            this.setSetting("hitpoints",(this.getSetting("hitpoints") + 1));
        }
        if (this.getSetting("stun") > 0){
            this.setSetting("stun",this.getSetting("stun")-1);
        }
    }

    public void processDrugs(){
        Enumeration<Drug> e = drugs.elements();
        while (e.hasMoreElements()){
           Drug currentDrug = (Drug)e.nextElement();
           currentDrug.effectPlayer(this);
        }
    }

  public boolean move(String direction){
	  boolean result = false;
	  
        if (!fighting){
            result = getCurrentRoom().moveUser(this, direction);
            this.look();
        } else {
            broadcast("Can't do that while fighting!\r\n");
        }
        
        return result;
    }

    public String getBlocking(){
        return blocking;
    }

    public void setBlocking(String newBlocking){
        blocking = newBlocking;
    }

//    public Player getBodyDouble(){
//        return body_double;
//    }

//    public void setBodyDouble(Player bd){
//        body_double = bd;
//    }

    public void setEscape(boolean escaping){
        escape = escaping;
    }

    public boolean getEscape(){
        return escape;
    }
    
	public void setBattleHandler(BattleHandler battlehandler) {
		this.battleHandler = battlehandler;
	}

	public BattleHandler getBattleHandler() {
		return battleHandler;
	}

	public void setAttackTarget(Player attackTarget) {
		this.attackTarget = attackTarget;
	}

	public Player getAttackTarget() {
		return attackTarget;
	}

	public void setUseTarget(Item useTarget) {
		this.useTarget = useTarget;
	}

	public void setEquipTarget(Item equipTarget) {
		this.equipTarget = equipTarget;
	}

	public Item getEquipTarget() {
		return equipTarget;
	}

	public Item getUseTarget() {
		return useTarget;
	}

	public void setInitiative(Integer initiative) {
		this.initiative = initiative;
	}

	public Integer getInitiative() {
		return initiative;
	}

	public void put(Item containerItem, Item objectItem){
            if (containerItem.getIsOpen()) {
            	if (objectItem == containerItem){
                    this.broadcast("can't put something inside it's self!\r\n");
                }else {
                	if (this.items.contains(objectItem)){
                        //if (objectItem.drop(this)){ // if the player can drop it...
                            //containerItem.put(objectItem);
                            //this.items.remove(objectItem);
                            //this.getCurrentRoom().chat(this.getPlayerName() + " put "+objectItem.getItemName()+ " inside "+ containerItem.getItemName() + "\r\n");
                            
                            //TODO fix this once you get item/ItemBase refactored.
                            ActionHeader header = new ActionHeader(this,null,(ItemBase)objectItem,(ItemBase)containerItem);
                        	Action commandAction = ((ItemBase)objectItem).getActions().get("put");
                        	if (!(commandAction == null)){
                        		commandAction.perform(header);
                        }
                    } else {
                    	this.broadcast("You must be have an item to put it inside a container!\r\n");
                    }
                }
            } else {
                this.broadcast(containerItem.getItemName() + " is closed.");
            }
    }
    
    public void get(String object, int objectPosition,String container,int containerPosition,boolean personalInv){
        Item objectItem = null;
        Item containerItem = null;
        System.out.println("getting container item");
        if (personalInv){
        	containerItem = this.getItemByNameAndPosition(container,containerPosition);	
        	if (containerItem == null){
        		this.broadcast("could not find "+container+" in your inventory!\r\n");
        	}
        } else {
            containerItem = this.getCurrentRoom().getItemByNameAndPosition(container,containerPosition);
            if (containerItem == null){
            	this.broadcast("Could not find "+container+" in this room!\r\n");
            }
        }
        //System.out.println("container name:"+containerItem.getItemName());
        if (containerItem != null ){
            //System.out.println("getting object");
            if (containerItem.getIsOpen()){
                objectItem = containerItem.getItemByNameAndPosition(object,objectPosition);
                if (objectItem == null){
                	broadcast("Cannot find " + object + " here!\r\n");
                }
            } else {
                this.broadcast(containerItem.getItemName()+" is not open\r\n");
            }
            //System.out.println("object name:"+objectItem.getItemName());
        }
        ItemBase objectItemBase = (ItemBase)objectItem; //TODO clean this up as soon as Item/ItemBase issues are refactored. 
        if (objectItem != null){
        	ActionHeader header = new ActionHeader(this,null,objectItemBase,null);
        	Action commandAction = objectItemBase.getActions().get("take");
        	if (!(commandAction == null)){
        		commandAction.perform(header);
              //  if (objectItem.take(this)){ // if user can take this
                	//this.getCurrentRoom().chat(this.getPlayerName() + " took "+objectItem.getItemName()+ " from "+ containerItem.getItemName() + "\r\n");
                    //if (!objectItem.getIsInfinite() || (this.getSetting("builder") == 1)){
                	/*
                	if (!objectItem.getIsInfinite()){
                        objectItem = containerItem.removeItem(objectItem);
                        //items.add(objectItem);
                        this.addToInventory(objectItem);
                        this.getCurrentRoom().chat(this.getPlayerName() + " took "+objectItem.getItemName()+ " from "+ containerItem.getItemName() + "\r\n");
                        // items.add(taken_item);
                    } else {
                       // if (objectItem.getFilename().equals("")){
                       //     this.broadcast("Cannot take this item until builder runs 'saveimage'!\r\n");
                       // }else{
                            //Item new_item = ObjectFactory.CreateItem(objectItem.getFilename());
                            
                    	ByteArrayOutputStream os = new ByteArrayOutputStream();
                        XMLEncoder encoder = new XMLEncoder(os);
                        encoder.writeObject(objectItem);
                        encoder.close();
                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                        XMLDecoder decoder = new XMLDecoder(is);
                        Item new_item = (Item)decoder.readObject();
                    	new_item.setIsInfinite(false);
                        //items.add(new_item);
                        this.addToInventory(new_item);
                        this.getCurrentRoom().chat(this.getPlayerName() + " took "+objectItem.getItemName()+ " from "+ containerItem.getItemName() + "\r\n");
                        //}
                    } */
                }
        } else {
        	broadcast("Cannot find " + object + " here!\r\n");
         //   broadcast("Cannot find " + object + " here!\r\n");
        }
    }    

/*    public void get(String container,String object){
        Item objectItem = null;
        Item containerItem = null;
        System.out.println("getting container item");
        containerItem = this.getItemByName(container);
        if (containerItem == null){
            containerItem = this.getRoom().getItemByName(container);
        }
        //System.out.println("container name:"+containerItem.getItemName());
        if (containerItem != null ){
            //System.out.println("getting object");
            if (containerItem.getIsOpen()){
                objectItem = containerItem.getItemByName(object);
                if (objectItem == null){
                	broadcast("Cannot find " + object + " here!\r\n");
                }
            } else {
                this.broadcast(containerItem.getItemName()+" is not open\r\n");
            }
            //System.out.println("object name:"+objectItem.getItemName());
        }
        if (objectItem != null){
                if (objectItem.take(this)){ // if user can take this
                    if (!objectItem.getIsInfinite() || (this.getSetting("builder") == 1)){
                        objectItem = containerItem.removeItem(objectItem);
                        items.add(objectItem);
                        this.getRoom().chat(this.getPlayerName() + " took "+objectItem.getItemName()+ " from "+ containerItem.getItemName() + "\r\n");
                        // items.add(taken_item);
                    } else {
                        if (objectItem.getFilename().equals("")){
                            this.broadcast("Cannot take this item unti bulder runs 'saveimage'!");
                        }else{
                            Item new_item = ObjectFactory.CreateItem(objectItem.getFilename());
                            new_item.setIsInfinite(false);
                            items.add(new_item);
                            this.getRoom().chat(this.getPlayerName() + " took "+objectItem.getItemName()+ " from "+ containerItem.getItemName() + "\r\n");
                        }
                    }
                    if (objectItem == null) {
                        broadcast("Cannot find " + object + " here!\r\n");
                    }
                }
        } else {
         //   broadcast("Cannot find " + object + " here!\r\n");
        }
    }
*/
      
       public void inspect (Item i){
    	   i.inspect(this);
       }
    
        public void inspect(String itemName){
            Item item = null;
            item = this.getItemByName(itemName);
            if (item != null){
                item.inspect(this);
            } else {
                item = this.getCurrentRoom().getItemByName(itemName);
                if (item != null){
                    item.inspect(this);
                }
            }
        }

        
        public HashMap<String, Integer> getSettings() {
			return settings;
		}

		public void setSettings(HashMap<String, Integer> settings) {
			this.settings = settings;
		}

		public Random getRandom() {
			return random;
		}

		public void setRandom(Random random) {
			this.random = random;
		}

		public void setDrugs(Vector<Drug> drugs) {
			this.drugs = drugs;
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

       // public void save(String rootName) { // implement this
       //     String itemSaveFile = rootName+"."+this.getPlayerName();
       //     System.out.println("saving Itemname:"+itemSaveFile);
       //     Enumeration<Item> e = items.elements();
       //     while (e.hasMoreElements()){
       //         Item i = (Item)e.nextElement();
       //         i.save(itemSaveFile);
       //     }
       // }

        public void save(String fileName){
        	try{
        		
        		FileOutputStream os = new FileOutputStream(fileName);
        		XMLEncoder encoder = new XMLEncoder(os);
        		encoder.writeObject(this);
        		encoder.close();
        	} catch (Exception e){
        		System.out.println("Error Saving!");
        		e.printStackTrace();
        	}	
        }                
        

	public abstract void die();

    public abstract void setPassword(String newPass);
    public abstract String getPassword();
    
    
    protected void setCommandST(StringTokenizer st) {
		this.commandST = st;
	}

	public StringTokenizer getCommandST() {
		return commandST;
	}

	public abstract void quit();
    public abstract void broadcast(String message);
    public abstract void look();
   // public abstract String getClassName();
    public abstract void doBattle();

}
