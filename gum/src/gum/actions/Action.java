package gum.actions;

import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.util.Vector;

import gum.ObjectFactory;
import gum.Player;
import gum.Room;
import gum.User;
import gum.World;
import gum.items.ItemBase;
import gum.menus.MenuContainer;

import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;
import gum.mobs.Mob;

public abstract class Action implements MenuContainer{
	
	private String actionName = null;
	private String preReqSetting = null;
	private int preReqValue = 0;
	private String successMessage = "";
	private String failMessage = "";
	private Action successAction = null;
	private Action failAction = null;
	private EffectRange range = EffectRange.TARGET;
	private ActionHeader header = null;
		
	public enum EffectRange {
		USER,TARGET,ROOM,WORLD,ENEMIES,ALLIES,BATTLE,ITEM,TARGETITEM
	}
	
	//public abstract void perform (Player player, Player target, ItemBase item, ItemBase targetItem);

	public abstract boolean doAction(ActionHeader header);
	
	public void perform(ActionHeader newHeader) {
		header = newHeader; // this should be sent through each call, not global.
		performByRange();
	}
	
	
	public void performByRange(){
		switch (this.getRange()) {	
		case TARGET: // in this case, the user performs the action on the target.
			if (header.getTargetPlayer() == null && header.getPlayer() == null){
				System.out.println("Action called without player or target!");
			}else if (header.getTargetPlayer() == null){
				header.getPlayer().broadcast("Who do you want to do this to? (Example: Use my bandage on bill).\r\n");
			}else {
				performOnTarget();
			}
			break;
		case USER: // in this case, the user performs the action on his self.
			
			//header.setTargetPlayer(header.getPlayer());
			performOnTarget();
			break;
		case ROOM: //User performs action on each player in the room.
			performOnRoom();
			break;
		case WORLD: //User performs action on each player in the world.
			performOnWorld();
			break;
		case ENEMIES: //User performs action on each mob in the room.
			performOnEnemies();
			break;
		case ALLIES: //User performs action on each player in the room.
			performOnAllies();
			break;
		case BATTLE: //User performs action on each player in the battle.
			performOnBattle();
			break;
		}
	}
	
	public void performOnBattle(){
		 Vector<Player> targets = header.getPlayer().getBattleHandler().getUsers();
	
		for (Player target: targets){
			header.setTargetPlayer(target);
			performOnTarget();
		}
		
	}
	
	public void performOnAllies(){
		Vector<Player> enemies = null;
		Player player = header.getPlayer();
		
		if (player instanceof Mob){
			enemies = player.getBattleHandler().getMobs();
		}else {
			enemies = player.getBattleHandler().getUsers();
		}
		
		for (Player target: enemies){
			header.setTargetPlayer(target);
			performOnTarget();
		}
		
	}
	
	public void performOnEnemies(){
		Vector<Player> enemies = null;
		Player player = header.getPlayer();
		
		if (player instanceof Mob){
			enemies = player.getBattleHandler().getUsers();
		}else {
			enemies = player.getBattleHandler().getMobs();
		}
		
		for (Player target: enemies){
			header.setTargetPlayer(target);
			performOnTarget();
		}
		
	}
	
	public void performOnWorld(){
		Vector<Room> world = World.getArea().getRooms();
		for (Room effectRoom: world){	
		Vector<Player> players = effectRoom.getPlayers();
			for (Player target: players){
				header.setTargetPlayer(target);
				performOnTarget();
			}
		}
	}	
	
	public void performOnRoom(){
		Room effectRoom = header.getPlayer().getCurrentRoom();
		Vector<Player> players = new Vector<Player>(effectRoom.getPlayers());
		
		for (Player target: players){
			header.setTargetPlayer(target);
			performOnTarget();
		}
		
	}
	
	public void performOnTarget(){
		boolean actionResult;
		
		//This will allow a temporary redefinition of the header. 
		//I'm not sure how often we'll want to do that, but I thought it 
		//would be useful to make it possible to insert a custom header into the 
		//'doAction' by overriding this method. 
		
		if (this.getRange() == EffectRange.USER){
			ActionHeader tempHeader = new ActionHeader(header.getPlayer(),header.getPlayer(),header.getItem(),header.getTargetItem());
			actionResult = this.doAction(tempHeader);
		}else {
			actionResult = this.doAction(header);
		}
		if (actionResult){
			if (this.getSuccessAction() != null){
				this.getSuccessAction().perform(header);
			}
		} else {
			if (this.getFailAction() != null){
				this.getFailAction().perform(header);
			}
		}
	}
	
	public boolean checkActionPrerequisites(Player player, Player target, ItemBase item, ItemBase targetItem){
		boolean result = true;
		
		
		//the whole prereq system could be handled by 'check' actions, and probably should be.
		//this is only meant to provide a dead-simple way of enforcing skills. 
		
		if (!player.getFightingFlag()){ 
			player.broadcast("You must be in combat to do that!\r\n");
			result = false;
		} else if (player.getSetting(this.getPreReqSetting()) == 0){
			player.broadcast("You don't have the required skill for that action!\r\n");
			result = false;	
		}
		
		return result;
	}
	
    public void configActionSave(User u) throws MenuExitException{
		String menuString = "Enter filename for save.\r\n\r\n";
		String fileName = "";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			fileName = World.getArea().getLibDir()+"\\"+s.getResult();
			this.save(fileName);
			u.broadcast("Action saved as:"+fileName);
		} else {
			u.broadcast("Action not saved. \r\n");
		}
    }	
	
	public boolean configActionRange(User u) throws MenuExitException {
		boolean done = false;
		String menuString =  "This will configure the action's range. Be default, the action effects the target.";
			   menuString =  "All effects of this action will be handled individually.";
		       menuString += "Configure "+this.getActionName()+" range:\r\n";
		menuString += "(1) User    	  - effects the user who trigggered the action.\r\n";
		menuString += "(2) Target  	  - effects the target chosen for this action. \r\n";
		menuString += "(3) Room    	  - effects everyone in the room.  \r\n";
		menuString += "(4) World   	  - effects everyone in the world. \r\n";
		menuString += "(5) Enemies 	  - effects all enemies in this battle. \r\n";
		menuString += "(6) Allies  	  - effects all allies in this battle.\r\n";
		menuString += "(7) Battle  	  - effects everyone in this battle. \r\n";
		menuString += "(8) Item    	  - effects the Item that contains this action \r\n";
		menuString += "(9) TargetItem - effects the the item that you've used the action container on \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 9, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				range = EffectRange.USER;
				break;
			case 2: 
				range = EffectRange.TARGET;
				break;
			case 3: 
				range = EffectRange.ROOM;
				break;
			case 4: 
				range = EffectRange.WORLD;
				break;
			case 5: 
				range = EffectRange.ENEMIES;
				break;
			case 6: 
				range = EffectRange.ALLIES;
				break;
			case 7: 
				range = EffectRange.BATTLE;
				break;
			case 8: 
				range = EffectRange.ITEM;
				break;
			case 9: 
				range = EffectRange.TARGETITEM;
				break;
			}
		}
		return done;
	}	

	public void configFailureAction(User u) throws MenuExitException{
		
		int menuItems = 2;
		String menuString = "Configure "+this.getActionName()+" failure action:\r\n";
		menuString += "(1) Add basic failure action \r\n";
		menuString += "(2) Add advanced failure action \r\n";
		if (this.failAction != null){
			menuString += "(3) Config existing failure action \r\n";
			menuString += "(4) Remove existing failure action \r\n";
			menuItems = 4;
		}
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, menuItems, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.setFailAction(Action.configBasicAddAction(u));
				if (this.getFailAction() != null){
					this.getFailAction().menu(u);
				}
				break;
			case 2: 
				this.setFailAction(Action.configAdvancedAddAction(u));
				if (this.getFailAction() != null){
					this.getFailAction().menu(u);
				}
				break;
			case 3: 
				this.failAction.menu(u);
				break;
			case 4: 
				this.failAction = null;
				break;
			}
		}
	}	
	
	public void configSuccessAction(User u) throws MenuExitException{
		int menuItems = 2;
		String menuString = "Configure "+this.getActionName()+" success action:\r\n";
		menuString += "(1) Add and configure basic success action \r\n";
		menuString += "(2) Add and configure advanced success action \r\n";
		if (this.successAction != null){
			menuString += "(3) Config existing success action \r\n";
			menuString += "(4) Remove existing success action \r\n";
			menuItems = 4;
		}
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, menuItems, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.setSuccessAction(Action.configBasicAddAction(u));
				if (this.getSuccessAction() != null){
					this.getSuccessAction().menu(u);
				}
				break;
			case 2: 
				this.setSuccessAction(Action.configAdvancedAddAction(u));
				if (this.getSuccessAction() != null){
					this.getSuccessAction().menu(u);
				}
				break;
			case 3: 
				this.successAction.menu(u);
				break;
			case 4: 
				this.successAction = null;
				break;
			}
		}
	}
	
	public static Action configAddAction(User u) throws MenuExitException {
		Action newAction = null;
		String menuString = "Add a new action\r\n";
		menuString += "(1) Basic Actions\r\n";
		menuString += "(2) Item Actions\r\n";
		menuString += "(3) Player Actions\r\n";
		menuString += "(4) Advanced Actions\r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				newAction = Action.configBasicAddAction(u);
				break;
			case 2:
				newAction = Action.configItemAddAction(u);
				break;
			case 3:
				newAction = Action.configPlayerAddAction(u);
				break;
			case 4: 
				newAction = Action.configAdvancedAddAction(u);
				break;
			}
		}
		return newAction;
	}		
	
	public static Action configPlayerAddAction(User u) throws MenuExitException{
		Action newAction = null;
		String menuString = "Player actions are actions that perform the basic tasks commands \r\n";
		menuString += "a player can perform. For instance, if you wanted to make a player check against \r\n";
		menuString += "blindness, you could create a new look that has a 'check' action first, then only\r\n";
		menuString += "performs a 'look' action if the player is not blind.\r\n\r\n";
		menuString += "Choose a new basic action below:\r\n";
		menuString += "(01) Attack \r\n";
		menuString += "(02) Chat \r\n";
		menuString += "(03) Emote \r\n";
		menuString += "(04) Follow \r\n";
		menuString += "(05) Look \r\n";
		menuString += "(06) Mission \r\n";
		menuString += "(07) Move \r\n";
		menuString += "(08) Put \r\n";
		menuString += "(09) Quit \r\n";
		menuString += "(10) Stats \r\n";
		menuString += "(11) Take \r\n";
		menuString += "(12) Talk \r\n";
		menuString += "(13) Inventory \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 13, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerAttack");
				break;
			case 2: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerChat");
				break;
			case 3: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerEmote");
				break;
			case 4: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerFollow");
				break;
			case 5: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerLook");
				break;	
			case 6: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerMission");
				break;	
			case 7: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerMove");
				break;
			case 8: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerPut");
				break;
			case 9: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerQuit");
				break;
			case 10: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerStats");
				break;
			case 11: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerTake");
				break;
			case 12: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerTalk");
				break;
			case 13: 
				newAction = ObjectFactory.CreateAction("gum.actions.player.ActionPlayerInventory");
				break;
			}
		}
		if (newAction != null){
			u.broadcast("New Action Created!\r\n");
		}
		return newAction;
	}		
		
	
	public static Action configItemAddAction(User u) throws MenuExitException{
		Action newAction = null;
		String menuString = "Item Actions are actions that perform the basic tasks for item interaction. \r\n";
		menuString += "For instance, when you 'take' an item, the basic item will call it's 'take' action.\r\n";
		menuString += "If you wanted to make another action in the item, i.e. 'remove', do the same thing \r\n";
		menuString += "as 'take', you would use the special Item Action below.\r\n\r\n";
		menuString += "Choose a new Item action below:\r\n";
		menuString += "(01) Close \r\n";
		menuString += "(02) Open \r\n";
		menuString += "(03) Hold \r\n";
		menuString += "(04) Look \r\n";
		menuString += "(05) Take \r\n";
		menuString += "(06) Put \r\n";
		menuString += "(07) More \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 7, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				newAction = ObjectFactory.CreateAction("gum.actions.item.ActionItemClose");
				break;
			case 2: 
				newAction = ObjectFactory.CreateAction("gum.actions.item.ActionItemOpen");
				break;
			case 3: 
				newAction = ObjectFactory.CreateAction("gum.actions.item.ActionItemHold");
				break;
			case 4: 
				newAction = ObjectFactory.CreateAction("gum.actions.item.ActionItemLook");
				break;
			case 5: 
				newAction = ObjectFactory.CreateAction("gum.actions.item.ActionItemTake");
				break;	
			case 6: 
				newAction = ObjectFactory.CreateAction("gum.actions.item.ActionItemPut");
				break;	
			case 7: 
				newAction = ObjectFactory.CreateAction("gum.actions.item.ActionItemMore");
				break;
			}
		}
		if (newAction != null){
			u.broadcast("New Action Created!\r\n");
		}
		return newAction;
	}
	
	public static Action configBasicAddAction(User u) throws MenuExitException{
		Action newAction = null;
		String menuString = "Choose a new basic action below:\r\n";
		menuString += "(01) Attack \r\n";
		menuString += "(02) Heal \r\n";
		menuString += "(03) Notify \r\n";
		menuString += "(04) Roll \r\n";
		menuString += "(05) Check \r\n";
		menuString += "(06) Modify \r\n";
		menuString += "(07) Transport \r\n";
		menuString += "(08) Spawn Mob \r\n";
		menuString += "(09) Spawn Item \r\n";
		menuString += "(10) Destroy Item \r\n";
		menuString += "(11) Menu  \r\n";
		menuString += "(12) Buy  \r\n";
		menuString += "(13) Sell  \r\n";
		menuString += "(14) Set Start Room \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 14, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				newAction = ObjectFactory.CreateAction("gum.actions.ActionAttack");
				break;
			case 2: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionHeal");
				break;
			case 3: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionNotify");
				break;
			case 4: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionRoll");
				break;
			case 5: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionCheck");
				break;	
			case 6: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionModify");
				break;	
			case 7: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionTransport");
				break;
			case 8: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionSpawnMob");
				break;
			case 9: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionSpawnItem");
				break;
			case 10: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionDestroyItem");
				break;
			case 11: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionMenu");
				break;
			case 12: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionBuy");
				break;
			case 13: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionSell");
				break;
			case 14: 
				newAction = ObjectFactory.CreateAction("gum.actions.ActionSetStartRoom");
				break;
			}
		}
		if (newAction != null){
			u.broadcast("New Action Created!\r\n");
		}
		return newAction;
	}		
	
	
	public static Action configAdvancedAddAction(User u) throws MenuExitException{
		Action newAction = null;
		String menuString =  "Enter a filename (Example: Attack.xml) or a JAVA Class name (Example:gum.items.actions.AttackAction)\r\n";
		       menuString += "GUM will try to locate the resource, and add the item to the current room.\r\n\r\n";
		       
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			newAction = ObjectFactory.CreateAction(s.getResult());
			if (newAction != null){
				newAction.setActionName("DEFAULT");
				
				u.broadcast("New action added with name 'DEFAULT'.\r\n");
			} else {
				u.broadcast("Failed to locate resource. Check input and retry. \r\n");
			}
		}
		return newAction;
	}	
	
	public void configActionSuccessMessage(User u) throws MenuExitException {
		String menuString = "Enter a new action success message.\r\n";
		menuString += "Current success message:\r\n"
				+ this.getSuccessMessage() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Success Message:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the description, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		this.setSuccessMessage(result);
	}	

	public void configActionFailMessage(User u) throws MenuExitException {
		String menuString = "Enter a new action fail message.\r\n";
		menuString += "Current fail message:\r\n"
				+ this.getFailMessage() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New fail message:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the description, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		this.setFailMessage(result);
	}	
	
	public void configActionName(User u) throws MenuExitException {
		String menuString = "Enter a new Action name.\r\n";
		menuString += "Current action name:" + this.getActionName() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			this.setActionName(s.getResult());
			u.broadcast("New action name:\r\n" + this.getActionName() + "\r\n");
		} else {
			u.broadcast("Action name unchanged. \r\n");
		}
	}	
	
	public void configActionPrereqSetting(User u) throws MenuExitException{

		int newSettingValue = 0;
		String settingMenuString =  "This will configure a prerequisite setting for this action.\r\n";
			   settingMenuString += "If the user does not have the setting to an equal or higher value, the action will fail.\r\n";
		       settingMenuString += "Currently this Action's prerequisite setting is:"+this.getPreReqSetting()+" Value:"+this.getPreReqValue()+".\r\n";
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (!newSettingName.equals("")){
			newSettingValue = this.getSettingValue(u);
			this.setPreReqSetting(newSettingName);
			this.setPreReqValue(newSettingValue);
		}
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
    	String menuString = "Enter a new setting value.\r\n\r\n";
    	       
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		result = (i.getResult());
    	}
    	return result;
    } 
	
	public void save(String fileName){
		try{
			System.out.println("saving Action:"+this.getActionName()+" as:"+fileName);
			FileOutputStream os = new FileOutputStream(fileName);
			XMLEncoder encoder = new XMLEncoder(os);
			encoder.writeObject(this);
			encoder.close();
		} catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	public String getStructure(){
		
		String a1 = this.getActionName();
		String a2 = "";
		String a3 = "";
		String a4 = "";
		String a5 = "";
		String a6 = "";
		String a7 = "";
		String offset1 = "";
		String offset2 = "";
		
		if (this.getSuccessAction() != null){
			a2 = this.getSuccessAction().getActionName();
			if (this.successAction.getSuccessAction() != null){
				a3 = this.successAction.getSuccessAction().getActionName();
			} 
			if (this.getSuccessAction().getFailAction() != null){
				a4 = this.getSuccessAction().getFailAction().getActionName();
			}
		}
		
		if (this.getFailAction() != null){
			a5 = this.getFailAction().getActionName();
			if (this.getFailAction().getSuccessAction() != null){
				a6 = this.getFailAction().getSuccessAction().getActionName();
			}
			if (this.getFailAction().getFailAction() != null){
				a7 = this.getFailAction().getFailAction().getActionName();
			}
		}
		
		for (int count = 1 ; count <= a2.length(); count++){
			offset1 += " ";
		}
		
		for (int count = 1 ; count <= a5.length(); count++){
			offset2 += " ";
		}
		
		String line1 = "\t     "+" %GREEN "+offset1+a3+" %SANE "+"\r\n";
		String line2 = "\t  "+" %GREEN "+a2+" %SANE "+"<"+"\r\n";
		String line3 = "\t /   "+" %RED "+offset1+a4+" %SANE "+"\r\n";
		String line4 = "<- %BLUE "+a1+"%SANE "+"\r\n";
		String line5 = "\t \\   "+" %GREEN "+offset2+a6+" %SANE "+"\r\n";
		String line6 = "\t  "+" %RED "+a5+" %SANE "+"<"+"\r\n";
		String line7 = "\t     "+" %GREEN "+offset2+a7+" %SANE "+"\r\n" ;
		
		return line1+line2+line3+line4+line5+line6+line7;
		
	}
	
	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getPreReqSetting() {
		return preReqSetting;
	}

	public void setPreReqSetting(String preReqSetting) {
		this.preReqSetting = preReqSetting;
	}

	public int getPreReqValue() {
		return preReqValue;
	}

	public void setPreReqValue(int preReqValue) {
		this.preReqValue = preReqValue;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public String getFailMessage() {
		return failMessage;
	}

	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}

	public Action getSuccessAction() {
		return successAction;
	}

	public void setSuccessAction(Action successAction) {
		this.successAction = successAction;
	}

	public Action getFailAction() {
		return failAction;
	}

	public void setFailAction(Action failAction) {
		this.failAction = failAction;
	}

	public EffectRange getRange() {
		return range;
	}

	public void setRange(EffectRange range) {
		this.range = range;
	}

	protected ActionHeader getHeader() {
		return header;
	}

	protected void setHeader(ActionHeader header) {
		this.header = header;
	}
	
	



}
