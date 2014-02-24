package gum.actions;

import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionRoll extends Action {

	private int target;
	private String setting;
	
	public ActionRoll(){
	}
	
	public void init(){
		this.setActionName("Roll");
		this.setRange(EffectRange.USER);
		target = 0;
		setting = "";		
	}
	
	@Override
	public boolean doAction(ActionHeader actionHeader) {
		//perform roll from the target.
		
		Player player = actionHeader.getTargetPlayer();
		
		boolean result = false;
		int roll = player.roll(player.getSetting(setting));
		//player.broadcast("roll:"+roll+" "+"target:"+this.target+"\r\n");
		if (roll > this.target){
			if (player != null){
				player.broadcast(this.getSuccessMessage());
			}
			result = true;
		}else {
			player.broadcast(this.getFailMessage());
		}
		return result;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure Roll Action:\r\n";
		menuString += "(01) Configure action name \r\n";
		menuString += "(02) Configure target (a roll higher than this is a success.) \r\n";
		menuString += "(03) Configure roll setting \r\n";
		menuString += "(04) Configure prereq setting\r\n";
		menuString += "(05) Configure range \r\n";
		menuString += "(06) Configure success action \r\n";
		menuString += "(07) configure success message \r\n";
		menuString += "(08) Configure failure action \r\n";
		menuString += "(09) configure failure message \r\n";
		menuString += "(10) Display structure \r\n";
		menuString += "(11) save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 11, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2:
				this.configTarget(u);
				break;				
			case 3:
				setting = getSettingName(u);
				break;
			case 4:
				this.configActionPrereqSetting(u);
				break;
			case 5:
				this.configActionRange(u);
				break;
			case 6:
				this.configSuccessAction(u);
				break;
			case 7: 
				this.configActionSuccessMessage(u);
				break;
			case 8: 
				this.configFailureAction(u);
				break;
			case 9: 
				this.configActionFailMessage(u);
				break;
			case 10:
				u.broadcast(this.getStructure());
				break;
			case 11: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Roll Configuration Menu.\r\n\r\n");	
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
	
	public String configNewMessage(User u) throws MenuExitException {
		String menuString = "Enter a new message below.\r\n";
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
	
	public String getSettingName(User u) throws MenuExitException {
		String result = "";
		String menuString =  "A random value will be generated between 0 and this setting value.\r\n";
			   menuString += "This random value will be compared to the target. If it's greater\r\n";
			   menuString += "then the action will succed. For example, the player has an int=10,\r\n";
			   menuString += "and the target is 8. You roll against int, generate a 9, which succeeds.\r\n";
			   menuString += "Enter a new Setting below:\r\n";
			   
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			result = s.getResult();
		} 
		return result;
	}
	
	public void configTarget(User u) throws MenuExitException {
		
		String menuString = "What will the target for this Action be? \r\n\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString);
		if (p.display()) {
			this.setTarget(p.getResult());
			u.broadcast("New Heal Base:" + this.target + "\r\n");
		} else {
			u.broadcast("Heal Base unchanged!\r\n");
		}
	}
	
	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public String getSetting() {
		return setting;
	}

	public void setSetting(String setting) {
		this.setting = setting;
	}

}
