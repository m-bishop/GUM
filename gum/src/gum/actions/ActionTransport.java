package gum.actions;

import gum.Player;
import gum.Room;
import gum.User;
import gum.World;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionTransport extends Action {
	
	private String targetRoom = "";

	public ActionTransport(){
		this.setActionName("Transport");
	}
	
	@Override
	public boolean doAction(ActionHeader actionHeader) {
		Player target = actionHeader.getTargetPlayer();
		
		Room room = World.getArea().getRoomByFilename(targetRoom);
		if (room != null){
			target.getCurrentRoom().removePlayer(target);
			World.getArea().getRoomByFilename(targetRoom).addPlayer(target);
			target.broadcast(this.getSuccessMessage());
			if (this.getSuccessAction() != null){
				this.getSuccessAction().perform(actionHeader);
			}
		}else {
			target.broadcast("Teleport failed. Bad room filename.\r\n");
		}
		return false;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure Transport Action:\r\n";
		menuString += "(01) Configure transport name \r\n";
		menuString += "(02) Configure target room \r\n";
		menuString += "(03) Configure prereq setting\r\n";
		menuString += "(04) Configure range \r\n";
		menuString += "(05) Configure success action \r\n";
		menuString += "(06) configure success message \r\n";
		menuString += "(07) Display structure \r\n";
		menuString += "(08) save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 8, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2:
				this.configTargetRoom(u);
				break;				
			case 3:
				this.configActionPrereqSetting(u);
				break;
			case 4:
				this.configActionRange(u);
				break;
			case 5:
				this.configSuccessAction(u);
				break;
			case 6: 
				this.configActionSuccessMessage(u);
				break;
			case 7:
				u.broadcast(this.getStructure());
				break;
			case 8: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Transport Configuration Menu.\r\n\r\n");	
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
	
	public void configTargetRoom(User u) throws MenuExitException {
		
		String menuString =  "Enter a room filename below. The player will be transported to this room\r\n";
			   menuString += "when this action is activated. \r\n";
			   menuString += "\r\n";
			  
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			String result = s.getResult();
			if (World.getArea().getRoomByFilename(result) != null){
				u.broadcast("Target room set to:"+result+"\r\n");
				this.targetRoom = result;
			} else {
				u.broadcast("Target room not found!\r\n");
			}
			
		} 
	}
	
	public String getTargetRoom() {
		return targetRoom;
	}

	public void setTargetRoom(String targetRoom) {
		this.targetRoom = targetRoom;
	}
	
}
