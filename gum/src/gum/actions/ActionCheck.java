package gum.actions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import gum.Player;
import gum.Room;
import gum.User;
import gum.World;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

//TODO : This class has some pre-ActionHeader code that needs to be cleaned up. 

public class ActionCheck extends Action {

	private int target;
	private String targetSetting;
	private String checkSetting; 
	private Condition condition;
	private HashMap<Integer,String> messages;

	public ActionCheck(){
	}
	
	public void init(){
		this.setActionName("Check");
		target = 0;
		checkSetting = ""; 
		condition = Condition.GREATER;
		messages = new HashMap<Integer,String>();
		this.setRange(EffectRange.USER);
	}
	
	public enum Condition {
		GREATER,LESS,EQUAL,GREATEREQUAL,LESSEQUAL
	}
	
	@Override
	public void performByRange(){
		// TODO just rewrite this ... 
		// This whole mess needs rewritten. Most of it could be cleaned up by defining an interface for everything that has settings, 
		// so that you could abstract the checks against different things that have settings. No more Area, Item, etc needing their 
		// own little pieces of code to process, just have any target require the interface that targets have settings and write it once. 
		
		Player player = this.getHeader().getPlayer();
		Player target = this.getHeader().getTargetPlayer();
		ItemBase item = this.getHeader().getItem();
		ItemBase targetItem = this.getHeader().getTargetItem();
		int tSetting;
		gum.Area cArea = World.getArea();
		
		switch (this.getRange()) {	
		case TARGET: // in this case, the user performs the action on the target.
			if (this.getHeader().getTargetPlayer() == null){
				this.getHeader().getPlayer().broadcast("Who do you want to do this to? (Example: Use my bandage on bill).\r\n");
			}else {
				performOnTarget();
			}
			break;
		case USER: // in this case, the user performs the action on his self. 
			//this.getHeader().setTargetPlayer(this.getHeader().getPlayer());
			performOnTarget();
			break;
		case ROOM: // Changes the room setting.
			Room cRoom = this.getHeader().getPlayer().getCurrentRoom();
			
			if (targetSetting.equalsIgnoreCase("*today")){
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				tSetting = Integer.valueOf(dateFormat.format(date));
				tSetting = tSetting+this.target;
			}
			else {
				tSetting = (cRoom.getSetting(targetSetting))+this.target;
			}
			this.performCheck(player, target, item, targetItem, cRoom.getSetting(checkSetting), tSetting);
			break;
		case WORLD: // Changes the world setting.
			
			if (targetSetting.equalsIgnoreCase("*today")){
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				tSetting = Integer.valueOf(dateFormat.format(date));
				tSetting = tSetting+this.target;
			}
			else {
				tSetting = (cArea.getSetting(targetSetting))+this.target;
			}
			
			this.performCheck(player, target, item, targetItem, cArea.getSetting(checkSetting), tSetting);
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
		case ITEM: //User performs action on the Item.	
			if (targetSetting.equalsIgnoreCase("*today")){
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				tSetting = Integer.valueOf(dateFormat.format(date));
				tSetting = tSetting+this.target;
			}
			else {
				tSetting = (item.getSetting(targetSetting))+this.target;
			}
			this.performCheck(player, target, item, targetItem, item.getSetting(checkSetting),tSetting);
			break;
		case TARGETITEM: // User performs action on target Item
			if (targetItem == null){
				player.broadcast("What do you want to do this to? Example: 'Use my key on the door'\r\n");
			}else {
				if (targetSetting.equalsIgnoreCase("*today")){
					DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
					Date date = new Date();
					tSetting = Integer.valueOf(dateFormat.format(date));
					tSetting = tSetting+this.target;
				}
				else {
					tSetting = (targetItem.getSetting(targetSetting))+this.target;
				}
				this.performCheck(player, target, item, targetItem, targetItem.getSetting(checkSetting),tSetting);
			}
			break;
		case TIMED:
			if (targetSetting.equalsIgnoreCase("*today")){
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				tSetting = Integer.valueOf(dateFormat.format(date));
				tSetting = tSetting+this.target;
			}
			else {
				tSetting = (cArea.getSetting(targetSetting))+this.target;
			}
			
			this.performCheck(player, target, item, targetItem, cArea.getSetting(checkSetting), tSetting);
			break;
		}
	}
	

	
	
	//TODO remember to clean up these methods that don't use the 'header' info
	// When this was written, it used the four seperate variables. Now those are
	// contained in the ActionHeader. 
	public void performCheck(Player player, Player target, ItemBase item, ItemBase targetItem,int settingValue,int targetValue){
		if (this.getCompareResult(settingValue,targetValue)){
			if (this.getSuccessAction() != null){
				System.out.println("Actioncheck called success action:"+this.getSuccessAction().getActionName());
				this.getSuccessAction().perform(this.getHeader());
			}
		} else {
			if (this.getFailAction() != null){
				this.getFailAction().perform(this.getHeader());
			}
		}
		if (player != null){
			player.broadcast(this.getClosestMessage(settingValue, player, target, item));
		}
	}
	
	@Override
	public boolean doAction(ActionHeader actionHeader) {
		Player target = actionHeader.getTargetPlayer();
		Player player = actionHeader.getPlayer();
		ItemBase item = actionHeader.getItem();
		
		int settingValue = target.getSetting(checkSetting);
		int tSetting;
		
		if (targetSetting.equalsIgnoreCase("*today")){
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			tSetting = Integer.valueOf(dateFormat.format(date));
			tSetting = tSetting+this.target;
		}
		else {
			tSetting = (target.getSetting(targetSetting))+this.target;
		}
		
		boolean result = getCompareResult(settingValue,tSetting);
		player.broadcast(this.getClosestMessage(settingValue, player, target, item));
		return result;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure Check Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure setting to be checked \r\n";
		menuString += "(03) Configure setting to be checked against (will be added to static target) \r\n";
		menuString += "(04) Configure target to be checked against (will be added to setting target)\r\n";
		menuString += "(05) Configure condition (Greater,Less,Equal,ect) \r\n";
		menuString += "(06) Configure prereq setting\r\n";
		menuString += "(07) Configure range \r\n";
		menuString += "(08) Configure success action \r\n";
		menuString += "(09) Configure failure action \r\n";
		menuString += "(10) Configure messages\r\n";
		menuString += "(11) Display structure \r\n";
		menuString += "(12) save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 12, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2:
				this.configCheckSetting(u);
				break;
			case 3:
				this.configTargetSetting(u);
				break;
			case 4:
				this.configTarget(u);
				break;
			case 5:
				this.configCondition(u);
				break;
			case 6:
				this.configActionPrereqSetting(u);
				break;
			case 7:
				this.configActionRange(u);
				break;
			case 8:
				this.configSuccessAction(u);
				break;
			case 9:
				this.configFailureAction(u);
				break;
			case 10: 
				this.configMessages(u);
				break;
			case 11:
				u.broadcast(this.getStructure());
				break;
			case 12: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Check Configuration Menu.\r\n\r\n");	
	}

	public void configTargetSetting(User u) throws MenuExitException{

		String settingMenuString =  "This will configure the target setting to be used by this action.\r\n";
			   settingMenuString += "the setting to be checked will be equal to the THIS+static target .\r\n";
		       settingMenuString += "Currently this Action's target setting is:"+this.targetSetting+"\r\n";
		       settingMenuString += "Enter '*today', and zero for a static target to check against today's date. \r\n";
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (!newSettingName.equals("")){
			this.targetSetting = newSettingName;
		}
	}
	
	public void configCheckSetting(User u) throws MenuExitException{

		String settingMenuString =  "This will configure a setting to be checked by this action.\r\n";
			   settingMenuString += "If the setting meets the condition when compared to the target,\r\n";
			   settingMenuString += "It succeeds. Otherwise it fails. \r\n";
			   settingMenuString += "Currently this Action's check setting is:"+this.getCheckSetting()+"\r\n\r\n";
		       
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (!newSettingName.equals("")){
			this.setCheckSetting(newSettingName);
		}
	}
	
	public boolean configCondition(User u) throws MenuExitException {
		boolean done = false;
		String menuString =  "This will configure the action's success condition."; 
			   menuString += "By default, this succeeds if the target is greater.";
			   menuString += "than the setting being read.";
		       menuString += "Enter the number of the new condition below:\r\n";
		menuString += "(1) Greater    \r\n";
		menuString += "(2) Less \r\n";
		menuString += "(3) Equal   \r\n";
		menuString += "(4) Less or Equal \r\n";
		menuString += "(5) Greater or Equal   \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 7, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				condition = Condition.GREATER;
				break;
			case 2: 
				condition = Condition.LESS;
				break;
			case 3: 
				condition = Condition.EQUAL;
				break;
			case 4: 
				condition = Condition.LESSEQUAL;
				break;
			case 5: 
				condition = Condition.GREATEREQUAL;
				break;
			}
		}
		return done;
	}
	
	public void configTarget(User u) throws MenuExitException {
		
		String menuString = "What will the Target value for this Action be?\r\n\r\n";
			   menuString += "the target is a value that you will compare the setting to";
			   menuString += "If condition were GREATER, then you would need > target to succeed.";
		PromptForInteger p = new PromptForInteger(u, menuString);
		if (p.display()) {
			this.setTarget(p.getResult());
			u.broadcast("Target:" + this.target + "\r\n");
		} else {
			u.broadcast("Target unchanged!\r\n");
		}
	}

	public boolean getCompareResult(int settingValue, int target){
		boolean result = false;
		
		switch (this.condition) {
		case EQUAL: 
			result = settingValue == target;
			break;
		case GREATER: 
			result = settingValue > target;
			break;
		case LESS: 
			result = settingValue < target;
			break;
		case GREATEREQUAL: 
			result = settingValue >= target;
			break;
		case LESSEQUAL: 
			result = settingValue <= target;
			break;
		}
		System.out.println("ActionCheck compared:"+settingValue+" against:"+target+" using operator:"+this.condition+" yeilding result:"+result);
		return result;
	}
	
	public String getClosestMessage(int percentEffect, Player player, Player target, ItemBase item){		
		
		String result = "";
		boolean done = false;
		ArrayList<Integer> keys = new ArrayList<Integer>(messages.keySet());
		Collections.sort(keys);
		
		// set return to first message.
		// so that if there's only one,
		// it won't come back blank.
		Iterator<Integer> i = keys.iterator();
		if (i.hasNext()){ 
			result = messages.get(i.next());
		}
		// now look for the first entry <= to percentEffect.
		while (i.hasNext() && !done){
			int idx = i.next();
			//System.out.println("index:"+idx+"effect:"+percentEffect);
			if (idx > percentEffect){
				done = true;
			}else {
				result = messages.get(idx);
			}
		}
		//finally, replace identifiers.
		//TODO move this to the header, add 'target Item'
		if (player != null){
    		result = result.replace("[player]", player.getPlayerName());
		}
    	if (target != null){
    		result = result.replace("[target]", target.getPlayerName());
    	}
    	if (item != null){
    		result = result.replace("[item]", item.getItemName());
    	}
		return result;
	}
	
	public boolean configMessages(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure messages:\r\n";
		menuString += "(1) List messages \r\n";
		menuString += "(2) Add a message \r\n";
		menuString += "(3) Remove a message \r\n";

		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configViewMessages(u);
				break;
			case 2: 
				this.configAddMessages(u);
				break;
			case 3: 
				this.configRemoveMessages(u);
				break;
			}
		}
		return done;
	}	
	
	public void configViewMessages(User u){
		String display = "Current Messages:\r\n\r\n";
		ArrayList<Integer> keys = new ArrayList<Integer>(messages.keySet());
		Collections.sort(keys);
		
		for (Integer i : keys){
			display += "("+i+") "+messages.get(i)+"\r\n";
		}
		
		u.broadcast(display);
	}
	
	public void configRemoveMessages(User u) throws MenuExitException{	
		
		String menuString =  "Enter a message index from the list below to remove it.\r\n";
		ArrayList<Integer> keys = new ArrayList<Integer>(messages.keySet());
		Collections.sort(keys);
		
		for (Integer i : keys){
			menuString += "("+i+") "+messages.get(i)+"\r\n";
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
	       if (p.display()){
	    	   if (messages.remove(p.getResult()) != null){
	    		   u.broadcast("Message removed.\r\n");
	    	   }else {
	    		   u.broadcast("Message not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Messages unchanged.\r\n");
	       }
	}

	public void configAddMessages(User u) throws MenuExitException{
		String menuString =  "Check messages work by matching the Base+Roll to a message.\r\n";
		       menuString += "For example, if you Roll a 20, the system will start at \r\n";
		       menuString += "index '20' and look up through the list until it finds a message. \r\n";
		       menuString += "If you enter an index already being used, this will replace it. \r\n";
		       menuString += "If you enter '[player]', '[target]' or '[item]' those tokens will \r\n";
		       menuString += "be replaced by the player, target and item names. \r\n";
		       
		       menuString += "Enter a new index: \r\n";
		       
		PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
		if (p.display()){
			messages.put(p.getResult(), this.configDescription(u));
		} else {
			u.broadcast("Messages unchanged.\r\n");
		}
	}
	
	public String configDescription(User u) throws MenuExitException {
		String menuString = "Enter a new message.\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Message:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the message, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		return result;
	}
	
	public String getCheckSetting() {
		return checkSetting;
	}

	public void setCheckSetting(String checkSetting) {
		this.checkSetting = checkSetting;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public String getTargetSetting() {
		return targetSetting;
	}

	public void setTargetSetting(String targetSetting) {
		this.targetSetting = targetSetting;
	}

	public HashMap<Integer, String> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<Integer, String> messages) {
		this.messages = messages;
	}
}
