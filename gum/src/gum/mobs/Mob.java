package gum.mobs;

//import gum.FileHandler;

import gum.ObjectFactory;
import gum.Player;
import gum.Room;
import gum.User;
import gum.World;
import gum.respawnable;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.items.Item;
import gum.items.ItemBase;
import gum.menus.MenuContainer;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Vector;

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

public abstract class Mob extends Player implements respawnable, MenuContainer{
    private String script;
    private boolean dead;
    private String currentDirection; // the direction the mob is facing.
    protected MobParser parser = new MobParser();
    protected String filename;
    
    //respawn vars
    private Room respawnRoom;
    private int respawnTimeout;
    private int respawnTimeoutCopy;
    private Item respawnHolding;
    //private byte[] respawnCopy = null;
    
	private HashMap<String, Integer> RespawnSettings = new HashMap<String, Integer>(); // setting map
    
    private HashMap<String,Action> actions = new HashMap<String,Action>();

    public Mob(){
    	super();
    }
    
    public void init(){

    	// TODO make this configurable. 
    	// Many of these should be overwritten later, but this is
    	// to avoid any divide by zeroes in battles and stuff 
    	// like that. The game engine expects, at the very least,
    	// that these settings exist for anyone taking part 
    	// in a battle. 
    	
    	
    	this.script = "";
        this.setSetting("hitpoints",50); 
        this.setSetting("maxhp",50);
        this.setSetting("str",5);
        this.setSetting("int",5);
        this.setSetting("ref",5);
        this.setSetting("dex",5);
    	
    }

    //public Mob(String file){
    //    //load Mob
    //    this.loadFromFile(file);
    //    //this.start();
    //}

	@Override
	public boolean respawnOnce() {
		return true;
	}
    

	public void respawnInit(){
		this.setRespawnItems(null);
		this.setRespawnSettings(null);
    	
    	RespawnSettings = new HashMap<String, Integer>(this.getSettings());
    	
    	this.setRespawnTimeoutCopy(this.getRespawnTimeout());
    	
   // 	for(Map.Entry<String, Integer> entry : this.getSettings().entrySet()){
   //         RespawnSettings.put(new String(entry.getKey()), new Integer(entry.getValue()));
   //     }
    	
    	RespawnItems =  new Vector<Item>();
    	for (Item i : this.getItems()){
    		Item newItem = i.copy();
    		RespawnItems.add(newItem);
    		if (this.getRightHand() == i){
    			this.respawnHolding = newItem;
    		}
    	}
    	

        
    	/*
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(os);
        encoder.writeObject(this);
        encoder.close();
        respawnCopy = os.toByteArray();
        */
    }
    
    public void respawn(){
    	this.setItems(this.getRespawnItems());
    	this.setSettings(this.getRespawnSettings());
    	this.setRespawnTimeout(this.getRespawnTimeoutCopy());
    	this.setRightHand(respawnHolding);
    	this.respawnInit();
    	this.getRespawnRoom().addPlayer(this);
    	this.dead = false;
    	//this.start();
    	
    	//this.loadFromFile(this.filename);
//    	if (respawnCopy != null){
//    		ByteArrayInputStream is = new ByteArrayInputStream(respawnCopy);
//    		XMLDecoder decoder = new XMLDecoder(is);
//    		Mob newMob = (Mob)decoder.readObject();
//    		newMob.setRespawnCopy(respawnCopy);
//    		newMob.setRespawnRoom(this.getRespawnRoom());
//    		this.getRespawnRoom().addPlayer((Mob)newMob);
//    		newMob.start();
    		//this.dead = false;
//    	} else {
//    		System.err.println("Mob respawn copy not initialized!");
 //   	}
    }

    public void process(){
    	//by default, mobs do not heal.
    	this.processDrugs();
    }
    
    public void run(){
        System.out.println("starting mob:"+this.getPlayerName());
        System.out.println("mob script:"+script);
        try {
            StringTokenizer st = new StringTokenizer(script, "\r\n");
            String commandLine = "";

            dead = false;

            while (!dead || this.getRespawnTimeout() > 0){
                Thread.sleep(1000);
                if (!dead){
                //System.out.println("Running:"+this.getPlayerName());
                if (!this.getFightingFlag()){ // if you aren't fighting, pick a fight
                    this.startFight();
                }
                if (!this.getFightingFlag()){ // if the mob still isn't fighting
                    if (st.hasMoreTokens()){
                        commandLine = st.nextToken();
                    }
                    // System.out.println("mob command:"+commandLine);
                    //commandLine = parser.removeBackspace(commandLine);
                    //System.out.println("sending mob command:"+commandLine);
                    //System.out.println("Parsing command for Mob:"+this.getPlayerName()+" command:"+commandLine+"\r\n");
                    parser.ParseMobCommand(this, commandLine);

                    if (!st.hasMoreElements()) {
                        // if we're at the end of the script, start over.
                        st = new StringTokenizer(script, "\r\n");
                    }
                } else {
                    this.fight();
                }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

/*    public void loadFromFile(String file){

        filename = file;

        System.out.println("loading mob" + file);
        FileHandler mobFile = new FileHandler();

        this.setPlayerName(mobFile.read_setting(file,"name",false));
        System.out.println("Mob name:"+this.getPlayerName());
        this.setScript(mobFile.read_setting(file,"script",true));
        System.out.println("Mob script:\r\n"+this.getScript());
        this.setFightingFlag(false);

        try{
            this.setRespawnTimeout(Integer.valueOf(mobFile.read_setting(file,"respawn", false)).intValue());
        }catch (Exception e){
            e.printStackTrace();
        }

/*        Integer hp = null;
        try{
            hp = Integer.valueOf(mobFile.read_setting(file,"hitpoints", false));
        } catch (Exception e){
                hp = Integer.valueOf("0"); // gotta be a better way
        }
        if (hp.intValue() > 0 ){
            System.out.println("Loaded HP:"+String.valueOf(hp));
            this.setHitPoints(hp.intValue());
        } else {
            this.setHitPoints(100);
        }

        String settings = mobFile.read_setting(file,"settings",false);
        StringTokenizer settingST = new StringTokenizer(settings,";");
        StringTokenizer settingToken = null;
        while (settingST.hasMoreTokens()){
            settingToken = new StringTokenizer(settingST.nextToken(),",");
            this.setSetting(settingToken.nextToken(),Integer.parseInt(settingToken.nextToken()));
        }
        if (this.getSetting("hitpoints") < 1){
            this.setSetting("hitpoints",100);
        }
        this.loadItems(mobFile.read_setting(file,"items",false));
        System.out.println("try to hold:"+mobFile.read_setting(file,"holding",false));
        this.holdRight(mobFile.read_setting(file,"holding",false)); // make mob hold something
        //System.out.println("holding:"+this.getRightHand().getItemName());
    }

*/

    

    public void doBattle(){
  	  //this is really simple battle logic. Attack the 'first' player in the list, so 
      //by default, mobs will concentrate on one fighter, which should make multiples 
      //more dangerous, but also allow players to assign a 'tank' to teams. 
    	try{
    	Player p = this.getBattleHandler().getUsers().elementAt(0);
    	
  		  if (this.getRightHand().getIsWeapon()) {
  			this.getRightHand().attack(this,p);
  			} else {
  				this.broadcast(this.getRightHand().getItemName()
  						+ "is not a weapon!!\r\n");
  			}
    	} catch (Exception e){
    		// do nothing, it's possible the fight is just over. 
    	}
  	  }
    
    public void setScript(String newScript){
        script = newScript;
    }

    public void addToScript(String scriptAddition){
        script += scriptAddition;
    }

    public String getScript(){
        return script;
    }

    public void setRespawnRoom(Room respawn){
        respawnRoom = respawn;
    }

    public Room getRespawnRoom(){
        return respawnRoom;
    }

    public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public MobParser getParser() {
		return parser;
	}

	public void setParser(MobParser parser) {
		this.parser = parser;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getRespawnTimeout() {
		return respawnTimeout;
	}

	public void setRespawnTimeout(int respawnTimeout) {
		this.respawnTimeout = respawnTimeout;
	}
	
    public int getRespawnTimeoutCopy() {
		return respawnTimeoutCopy;
	}

	public void setRespawnTimeoutCopy(int respawnTimeoutCopy) {
		this.respawnTimeoutCopy = respawnTimeoutCopy;
	}
	private Vector<Item> RespawnItems = new Vector<Item>(); // all items
    public Vector<Item> getRespawnItems() {
		return RespawnItems;
	}

	public void setRespawnItems(Vector<Item> respawnItems) {
		RespawnItems = respawnItems;
	}

	public HashMap<String, Integer> getRespawnSettings() {
		return RespawnSettings;
	}

	public void setRespawnSettings(HashMap<String, Integer> respawnSettings) {
		RespawnSettings = respawnSettings;
	}

	public void broadcast(String message) {
		System.out.println("Mob heard:"+message);
		StringTokenizer st = new StringTokenizer(message, " ");
		
		if ((actions.size() > 0) && (message.contains("says:"))){
			System.out.println("Mob heard someone say something");
			Player player = this.getCurrentRoom().getPlayerByName(st.nextToken());
			ActionHeader header = new ActionHeader(this,player,null,null);// the mob is the 'player', the user is the 'target'
			if (player != null){ // only look for an action to perform if there's a user to perform it on.
				if (actions.size() == 1){// if there's only one action, do it regardless of the content of the message. 			
					Action a = actions.values().iterator().next();
					a.perform(header);
				}else {
					for (String s :  actions.keySet() ){
						if (message.contains(s)){
							//hopefully there won't be too many situations where the user matches more than one action
							//but you could probably find a use for it.
							actions.get(s).perform(header);
						}
					}
				}	
			}
		}
	}
	


	public void check(Player player){
        String checkOutput = "";
        Item i = null;

        checkOutput = "Mob Setting Checklist:\r\n";
        checkOutput += "name:"+ this.getPlayerName() + "\r\n";
        checkOutput += "respawn timeout ="+ this.getRespawnTimeout()+"\r\n";
        checkOutput += "Script:\r\n " + this.getScript() + "\r\n";
        if (this.getRightHand() != null){
            checkOutput += "hold: "+this.getRightHand().getItemName()+"\r\n";
        }
        checkOutput += "Items:\r\n";
        Enumeration<Item> e = this.getItems().elements();
        while (e.hasMoreElements()){
            i = (Item)e.nextElement();
            checkOutput += i.getItemName()+"\r\n";
        }
        checkOutput += "Settings:\r\n"+this.getSettingsAsString();
        player.broadcast(checkOutput);
    }

	public void menu(User u) throws MenuExitException {
		//boolean done = false;
		String menuString = "Configure Mob:"+this.getPlayerName()+"\r\n";
		menuString += "(1) Configure Mob Name \r\n";
		menuString += "(2) Confifure Mob Description \r\n";
		menuString += "(3) Configure Mob Respawn Timeout \r\n";
		menuString += "(4) Configure Mob Script \r\n";
		menuString += "(5) Configure Mob Settings\r\n";
		menuString += "(6) Configure Mob Items\r\n";
		menuString += "(7) Configure Mob Actions\r\n";
		menuString += "(8) Save Mob\r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		// menuString += "(5) Configure Area Description \r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 8, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configMobName(u);
				break;
			case 2:
				configDescription(u);
				break;
			case 3:
				configRespawnTimeout(u);
				break;
			case 4:
				configMobScript(u);
				break;
			case 5: 
				configMobSettings(u); 
				break;
			case 6: 
				configMobItems(u); 
				break;
			case 7: 
				configMobActions(u); 
				break;
			case 8: 
				configMobSave(u); 
				break;
			}
		}
		u.broadcast("\r\nExiting Room Configuration Menu.\r\n\r\n");
	}
    
	public boolean configMobActions(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure "+this.getPlayerName()+" Actions:\r\n";
		menuString += "(1) Configure existing actions. \r\n";
		menuString += "(2) Add a new action. \r\n";
		menuString += "(3) Remove an action. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
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
			}
		}
		return done;
	}	
	
	public void configActionAdd(User u) throws MenuExitException{
		String menuString =  "This will allow you to add a command to the mob, and associate it with an action. \r\n";
		       menuString += "Example: adding the new verb: <buy> allows the player to 'tell bartender buy'.\r\n";
		       menuString += "If there is only one action, 'tell <mob> <anything>' will trigger the action.\r\n";
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
	
	public boolean configMobItems(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure "+this.getPlayerName()+" Items:\r\n";
		menuString += "(1) Configure existing items. \r\n";
		menuString += "(2) Add a new item. \r\n";
		menuString += "(3) Remove an item. \r\n";
		menuString += "(4) Choose an item to hold. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configItem(u);
				break;
			case 2: 
				Item i = ItemBase.configAddItem(u);
				if (i != null){
					getItems().add(i);
				}
				break;
			case 3: 
				this.configItemDelete(u); 
				break;
			case 4: 
				this.configHolding(u); 
				break;
			}
		}
		return done;
	}	
	
	public void configHolding(User u) throws MenuExitException{
		int count = 0;
		String menuString  = "Note: The mob will *NOT* drop this item after a battle. \r\n";
			   menuString += "The mob will use the first action in this item to attack in battle. \r\n";
			   menuString += "This should be an item with only 1 action, and that action should be the. \r\n";
			   menuString += "fighting logic for this mob. \r\n";
			   menuString += "Choose an item for Mob to hold:\r\n";
		Vector<Item> items = this.getItems();
		
		for (Item i: items){
			menuString += "("+count+") "+i.getItemName()+"\r\n";
			count++;			
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger i = new PromptForInteger(u, menuString, items.size()-1, 0);
		if (i.display()) {
			Item itemToHold = items.elementAt(i.getResult());
			u.broadcast("Mob is holding:"+itemToHold.getItemName()+"\r\n");
			this.setRightHand(itemToHold);
		}
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
		
		//Vector <Mob> mobs = new Vector<Mob>();
		int count = 0;
		String menuString = "Choose an item to delete:\r\n";
		
		for (Item i: this.getItems()){
				menuString += "("+count+") "+i.getItemName()+"\r\n";
				count++;
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, this.getItems().size()-1, 0);
		if (p.display()) {
			this.getItems().remove(p.getResult());
			u.broadcast("Selected item removed from mob.\r\n");
		} else {
			u.broadcast("Room unchanged.\r\n");
		}
	}	

	
	public boolean configMobName(User u) throws MenuExitException {
		String menuString = "Enter a new mob name.\r\n";
		menuString += "Current mob name:" + this.getPlayerName() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			this.setPlayerName(s.getResult());
			u.broadcast("New mob name:\r\n" + s.getResult() + "\r\n");
		} else {
			u.broadcast("mob name unchanged. \r\n");
		}
		return done;
	}	
	
    public boolean configRespawnTimeout(User u) throws MenuExitException{
    	String menuString = "Enter a new respawn timeout for this mob.\r\n(example: '2' would be 'wait two minutes before respawning.)\r\n";
    	       menuString +="Current respawn timeout:"+this.getRespawnTimeout()+"\r\n";
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		this.setRespawnTimeout(i.getResult());
    		u.broadcast("Respawn timeout changed to:"+i.getResult()+"\r\n");
    	}else {
    		u.broadcast("Respawn timeout unchanged.\r\n");
    	}
    	return done;
    } 	
	
	public boolean configMobScript(User u) throws MenuExitException {
		String menuString  = "The mob's script will execute, one command per second.You may enter";
		       menuString += "User Commands, as well as mob commands, like 'cdmove' and 'attackPlayers'.\r\n";
		       menuString += "Enter a new mob script.\r\n";
		       menuString += "Current script:\r\n"
				+ this.getScript() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New script:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the script, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		this.setScript(result);
		this.setPlayerDescription(result);
		return done;
	}    
	
	public boolean configMobSettings(User u) throws MenuExitException{
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
	
    public void configMobSave(User u) throws MenuExitException{
		String menuString = "Enter filename for save.\r\n\r\n";
		String fileName = "";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			fileName = World.getArea().getLibDir()+"//"+s.getResult();
			this.save(fileName);
			u.broadcast("Mob saved as:"+fileName);
		} else {
			u.broadcast("mob not saved. \r\n");
		}
    }
    
    public void parseMob(StringTokenizer st, Player player) {
        String token = "";

        if (st.hasMoreTokens()) {
            token = st.nextToken();
        }
        if (token.equals("name")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            }
            this.setPlayerName(token);
        } else if (token.equals("script")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken("\r");
            }
            this.setScript(token + "\r\n");
        } else if (token.equals("script+")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken("\r");
            }
            this.addToScript(token + "\r\n");
        } else if (token.equals("additem")){
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            }
            Item i = ObjectFactory.CreateItem(token);
            if (i != null){
                if (st.hasMoreTokens()){
                    token = st.nextToken();
                    i.setName(token);
                    this.getItems().add(i);
                }
            } else {
                    player.broadcast("bad filename, item cannot be created\r\n");
            }
        } else if (token.equals("hold")){
            if (st.hasMoreTokens()){
                this.holdRight(st.nextToken());
            }
        } else if (token.equals("setting")){
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                try {
                    this.setSetting(token, Integer.parseInt(st.nextToken()));
                } catch (Exception e){
                    player.broadcast("Error: Setting could not be set!(setting <srting> <int>)\r\n");
                }
            }
        }
    }

    /*
    public void save(String rootName){
        FileHandler mobFile = new FileHandler();
        String itemList = "";
        String fileName = rootName + "." + this.getPlayerName();
        Vector<Item> items = this.getItems();

        System.out.println("saving Mob:"+rootName+"."+this.getPlayerName());
        mobFile.clear_file(fileName);

        mobFile.write_setting(fileName,"name",this.getPlayerName());
        mobFile.write_setting(fileName,"script",script);
        //mobFile.write_setting(fileName,"hitpoints",String.valueOf(this.getHitPoints()));
        mobFile.write_setting(filename,"settings",this.getSettings());
        mobFile.write_setting(fileName,"respawn",String.valueOf(this.getRespawnTimeout()));
        mobFile.write_setting(fileName,"holding",this.getRightHand().getItemName());

        Enumeration<Item> e = items.elements();
        while (e.hasMoreElements()){
            Item i = (Item)e.nextElement();
            itemList = itemList + rootName + "." + i.getItemName() + ";\r\n";
            i.save(rootName);
        }
        mobFile.write_setting(fileName,"items",itemList);
        mobFile.close_file();

    }
*/
    public void save(String fileName){
    	try{
    		this.setRespawnItems(null);
    		this.setRespawnSettings(null);
    		respawnRoom = null;
    		FileOutputStream os = new FileOutputStream(fileName);
    		XMLEncoder encoder = new XMLEncoder(os);
    		encoder.writeObject(this);
    		encoder.close();
    		this.respawnInit();
    	} catch (Exception e){
    		e.printStackTrace();
    	}	
    }

    public void setPassword(String newPass){
    }

    public String getPassword(){
        return "";
    }
    public void setCurrentDirection(String newCurrentDirection){
        currentDirection = newCurrentDirection;
    }

    public String getCurrentDirection(){
        return currentDirection;
    }
    
    public HashMap<String, Action> getActions() {
		return actions;
	}

	public void setActions(HashMap<String, Action> actions) {
		this.actions = actions;
	}

	public void look(){
    // this should load a set of different variables, like a list of users
    // room name, ect... so that you could write scripts that react to the
    // surroundings in different ways. This would save trouble of parsing the
    // description strings from the room descriptions.

    }

    public void holdRight(String itemName){
    	Item item = this.getItemByName(itemName);
    	
        if (item != null){
                if (item.hold()) {
                		if (this.getLeftHand() != item){
                			this.setRightHand(item);
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
            System.out.println("failed to hold:" + itemName);
            broadcast("You don't have the " + itemName + "\r\n");
        }  	
    }
    
    public void moveCurrentDirection(){ // try to take current direction.
        System.out.println("current dir move attemtped to:"+this.getCurrentDirection());
        Vector<String> exitList = this.getCurrentRoom().getExitList();

        if (exitList.contains(this.getCurrentDirection())){
            this.move(this.getCurrentDirection());
        } else {
            this.takeRandomExit();
        }
    }

   // public String getClassName(){
   //     return "mob";
   // }

    public void die() {
        Item i = null;

        dead = true;
        Enumeration<Item> e = this.getItems().elements();
        while (e.hasMoreElements()){
           i = (Item)e.nextElement();
           System.out.println("Found:"+i.getItemName());

        //   this.getItems().remove(i);
           if (this.getRightHand() != i){
           //don't drop item mob holds, so claws, teeth, ect aren't dropped
               this.getCurrentRoom().addToInventory(i);
               this.getCurrentRoom().chat(this.getPlayerName() + " drops " + i.getItemName() + "\r\n");
           }
        }
        this.getItems().removeAllElements();
        Room r = this.getCurrentRoom();
        r.removePlayer(this);
        if (this.getBattleHandler() != null){
    		this.getBattleHandler().removeFromBattle(this);
    		this.setBattleHandler(null); 
    	}
        System.out.println("Mob killed. Respawn timeout:"+this.getRespawnTimeout());
        if (this.getRespawnTimeout() != 0){ // 0 means do not respawn this mob.
            World.getArea().addRespawn(this);
            System.out.println("Added Mob:"+this.getPlayerName()+" to respawn.");
        }
    }
    
	public static Mob configAddMob(User u) throws MenuExitException {
		Mob result = null;
		String menuString = "Add a new Mob\r\n";
		menuString += "(1) Basic \r\n";
		menuString += "(2) Advanced \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				result =  Mob.configBasicAddMob(u);
				break;
			case 2: 
				result = Mob.configAdvancedAddMob(u);
				break;
			}
		}
	    result.respawnInit();
		return result;
	}		
	
	public static Mob configBasicAddMob(User u) throws MenuExitException{
		Mob result = null;
		
		String menuString = "Choose a new basic Mob below:\r\n";
		menuString += "(1) Basic Mob         - A mob with no default behaviour.\r\n";
		menuString += "(2) Wandering Monster - Wanders, follows sounds, and attacks on sight.\r\n";
		menuString += "(3) XXXXXX \r\n";
		menuString += "(4) XXXXXX \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				 
				result = Mob.createNewMob("gum.mobs.BasicMob", "DEFAULT");
				break;
			case 2: 
				result = Mob.createNewMob("gum.mobs.MobWanderingMonster","DEFAULT");
				break;
			case 3: 
				System.out.println("mob placeholder 3.");
				break;
			case 4: 
				System.out.println("mob placeholder 4.");;
				break;
			}
		}
		if (result != null){
			u.broadcast("New Mob Created!\r\n");
		}else {
			u.broadcast("Mob Creation failed\r\n");
		}
		return result;
	}		
		
	
	public static Mob configAdvancedAddMob(User u) throws MenuExitException{
		Mob result = null;
		String menuString =  "Enter a filename (Example: zombie.xml) or a JAVA Class name (Example:gum.mobs.BasicMob)\r\n";
		       menuString += "GUM will try to locate the resource, and add the item to the current room.\r\n\r\n";
		       
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			result = createNewMob(s.getResult(), "DEFAULT");
			if (result != null){
				u.broadcast("New Mob created with name 'DEFAULT'.\r\n");
			} else {
				u.broadcast("Failed to locate resource. Check input and retry. \r\n");
			}
		}
		return result;
	}	
	
	public static Mob createNewMob(String fileName, String name) {
		Mob result = null;
		Mob m = ObjectFactory.CreateMob(fileName);
		if (m != null) {
			m.setPlayerName(name);
			result = m;
		} 
		return result;
	}
    
    public abstract void quit();
    public abstract void fight();
    public abstract void startFight();// override this to make violent mobs.
    
}
