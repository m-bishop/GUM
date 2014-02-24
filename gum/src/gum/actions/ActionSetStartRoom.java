package gum.actions;

import gum.Player;
import gum.User;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

public class ActionSetStartRoom extends Action {
	
	public void ActionStartRoom(){};
	
	public void init(){
		this.setActionName("SetStartRoom");
		this.setRange(EffectRange.USER);
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Player target = header.getTargetPlayer();
		if (target instanceof User){
			System.out.println("Setting start room for "+target.getPlayerName()+" to "+target.getCurrentRoom().getFilename());
			((User) target).setStartRoom(target.getCurrentRoom().getRoomFilename());
		}
		return false;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure Set Start Room Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Configure success action \r\n";
		menuString += "(05) Display structure \r\n";
		menuString += "(06) save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 6, 1);
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
				this.configSuccessAction(u);
				break;
			case 5:
				u.broadcast(this.getStructure());
				break;
			case 6: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Set Start Room Configuration Menu.\r\n\r\n");	
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
		menuString += "(5) Allies  	  - effects all allies in this battle.\r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 5, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				setRange(EffectRange.USER);
				break;
			case 2: 
				setRange(EffectRange.TARGET);
				break;
			case 3: 
				setRange(EffectRange.ROOM);
				break;
			case 4: 
				setRange(EffectRange.WORLD);
				break;
			case 5: 
				setRange(EffectRange.ALLIES);
				break;
			}
		}
		return done;
	}	
	
}
