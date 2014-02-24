package gum.actions.item;

import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.items.Item;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

public class ActionItemTake extends Action {
	
	public ActionItemTake(){
	}
	
	public void init(){
		this.setRange(EffectRange.USER);
		this.setActionName("Take");
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Item item = header.getItem();
		Player player = header.getPlayer();
		boolean result = item.take(player);
		System.out.println("In doAction:"+String.valueOf(result)+" "+this.getSuccessMessage() +" "+ this.getFailMessage());
		
		if (result){
			player.broadcast(this.getSuccessMessage());
		} else {
			player.broadcast(this.getFailMessage());
		}
		
		return result;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure Take Item Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Configure success action \r\n";
		menuString += "(05) Configure success message \r\n";
		menuString += "(06) Configure Failure action \r\n";
		menuString += "(07) Configure Failure message \r\n";
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
				this.configSuccessAction(u);
				break;
			case 5:
				this.configActionSuccessMessage(u);
				break;
				
			case 6:
				this.configFailureAction(u);
				break;
			case 7:
				this.configActionFailMessage(u);
				break;
				
			case 8: 
				u.broadcast(this.getStructure());
				break;
			case 9: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Configuration Menu.\r\n\r\n");	


	}
	
	@Override
	public boolean configActionRange(User u){
		u.broadcast("Take can only be used to give an item to the player who  \r\n " +
					"triggered the action. \r\n" );
		return false;	
	}

}
