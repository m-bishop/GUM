package gum.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import gum.Player;
import gum.User;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionMenu extends Action {

	
	private boolean actionPerformed; // are we currently performing this action?
	private HashMap<String,Action> actions;
	private String menuString;
	private ActionHeader localHeader;

	public ActionMenu(){
	}
	
	public void init(){
		this.setActionName("Menu");
		this.setRange(EffectRange.USER);
		actionPerformed = false; // are we currently performing this action?
		actions = new HashMap<String,Action>();
		menuString = "";
		localHeader = null;		
	}
	
	@Override
	public boolean doAction(ActionHeader header) {
		this.localHeader = header;
		Player target = header.getTargetPlayer();
		this.actionPerformed = true;
		
		
		// this action only works on a player. If the target is a mob, just ignore it.
		if (target instanceof User){
			User u = (User)header.getTargetPlayer();
			if (u.inMenu){// if this is a submenu, then we're already in a menuhandler thread.
				try{
				this.menu(u);
				}catch (MenuExitException e){
					//TODO figure out if I should allow to escape game menus.
				}
			}else {
				new MenuHandler(u, this);
			}
		}
		
		return false;// doesn't matter, since there will be no success or fail actions.
	}

	public void configureMenu(User u){
		
	}
	
	@Override
	public void menu(User u) throws MenuExitException{
		if (!actionPerformed){//if they're not performing the action, show config menu.
			this.configMenu(u); 
		}else {
			this.performMenu(u);
		}
	}
	
	
/*	public void performMenu(User u) {
		String menuString = "Menu Action:\r\n";
		menuString += "(01) Say success! \r\n";
		menuString += "(02) Say fail...\r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 8, 1);
		p.display(); 
		switch (p.getResult()) {
			case 1:
				u.broadcast("\r\nSuccess!\r\n\r\n");
				break;				
			case 2:
				u.broadcast("\r\nFail...\r\n\r\n");
				break;
		}
		
		this.actionPerformed = false;// we're no longer performing this action. 
		
	}
	
*/
	public void performMenu(User u) throws MenuExitException {
		String menuString =  this.menuString;
		int count = 0;
		ArrayList<String> keys = new ArrayList<String>(this.actions.keySet());
		Collections.sort(keys);
	
		for (String s : keys){
			menuString += "("+count+") "+s+"\r\n";
			count++;
		}
       
		PromptForInteger p = new PromptForInteger(u, menuString, keys.size(), 0);
		if (p.display()){
			actions.get(keys.get(p.getResult())).perform(this.localHeader);
			u.broadcast(this.getSuccessMessage());
		} else {
			u.broadcast(getFailMessage());
		}
		this.actionPerformed = false;// we're no longer performing this action. 
		
	}
	
	private void configMenu(User u) throws MenuExitException{
		String menuString = "Configure Menu Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Configure success message, if the user chooses an option \r\n";
		menuString += "(05) Configure exit message, if a user tyes 'exit' to leave the menu \r\n";
		menuString += "(06) Configure menu text\r\n";
		menuString += "(07) Configure menu options \r\n";
		menuString += "(08) Display structure \r\n";
		menuString += "(09) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 9, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;				
			case 2:
				this.configActionPrereqSetting(u);
				break;
			case 3:
				this.configActionRange(u);
				break;
			case 4:
				this.configActionSuccessMessage(u);
				break;
			case 5: 
				this.configActionFailMessage(u);
				break;
			case 6:
				this.configMenuDescription(u);
				break;
			case 7:
				this.configItemActions(u);
				break;
			case 8: 
				u.broadcast(this.getStructure());
				break;
			case 9: 
				this.configActionSave(u);
				break;
			}
		}
	}

	public boolean configItemActions(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure menu Actions:\r\n";
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
				this.addOptions(u); 
				break;
			case 3: 
				this.configRemoveAction(u); 
				break;
			}
		}
		return done;
	}		
	
	public void addOptions(User u) throws MenuExitException{
		String menuString =  "This will allow you to add a menu option, and associate it with an action. \r\n";
		       menuString += "New menu option:";
		PromptForString s = new PromptForString(u, menuString);

		if (s.display()) {
			this.actions.put(s.getResult(), Action.configAddAction(u));
				u.broadcast("Added new option to the menu.");
		} else {
				u.broadcast("Option not added. \r\n");
		}
	}
	
	public void configRemoveAction(User u) throws MenuExitException{	
		
		String menuString =  "Enter an option from the list below to remove it.\r\n";
		int count = 0;
		ArrayList<String> keys = new ArrayList<String>(this.actions.keySet());
		Collections.sort(keys);
		
		for (String s : keys){
			menuString += "("+count+") "+s+"\r\n";
			count++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, keys.size(), 0);
	       if (p.display()){
	    	   if (actions.remove(keys.get(p.getResult())) != null){
	    		   u.broadcast("Option removed.\r\n");
	    	   }else {
	    		   u.broadcast("Option not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Options unchanged.\r\n");
	       }
	}	
	
	public void configExistingAction(User u) throws MenuExitException{	
		
		String menuString =  "Enter an option from the list below to configure it.\r\n";
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
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 7, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.setRange(EffectRange.USER);
				break;
			case 2: 
				this.setRange(EffectRange.TARGET);
				break;
			case 3: 
				this.setRange(EffectRange.ROOM);
				break;
			case 4: 
				this.setRange(EffectRange.WORLD);
				break;
			case 5: 
				this.setRange(EffectRange.ENEMIES);
				break;
			case 6: 
				this.setRange(EffectRange.ALLIES);
				break;
			case 7: 
				this.setRange(EffectRange.BATTLE);
				break;
			}
		}
		return done;
	}

	public boolean configMenuDescription(User u) throws MenuExitException {
		String menuString = "Enter new menu text.\r\n";
		menuString += "Current menu text:\r\n"
				+ this.getMenuString() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Menu:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the menu, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		this.setMenuString(result);
		return done;
	}
	
	
	public boolean isActionPerformed() {
		return actionPerformed;
	}

	public void setActionPerformed(boolean actionPerformed) {
		this.actionPerformed = actionPerformed;
	}

	public HashMap<String, Action> getActions() {
		return actions;
	}

	public void setActions(HashMap<String, Action> actions) {
		this.actions = actions;
	}

	public String getMenuString() {
		return menuString;
	}

	public void setMenuString(String menuString) {
		this.menuString = menuString;
	}

	public ActionHeader getLocalHeader() {
		return localHeader;
	}

	public void setLocalHeader(ActionHeader localHeader) {
		this.localHeader = localHeader;
	}
	
}
