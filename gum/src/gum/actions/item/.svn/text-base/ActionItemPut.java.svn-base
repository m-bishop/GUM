package gum.actions.item;

import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.items.Item;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

public class ActionItemPut extends Action {

	public ActionItemPut(){
		super();
		this.setRange(EffectRange.USER);
	}
	
	@Override
	public boolean doAction(ActionHeader header) {
		Item containerItem = header.getTargetItem();
		Item objectItem = header.getItem();
		Player player = header.getPlayer();
		
		if (containerItem != null){
			containerItem.put(objectItem);
			objectItem.drop(player);
			player.getItems().remove(objectItem);
			player.getCurrentRoom().chat(player.getPlayerName() + " put "+objectItem.getItemName()+ " inside "+ containerItem.getItemName() + "\r\n");
		}else {
			if (player.getItems().contains(objectItem)){
				objectItem.drop(player);
				player.getItems().remove(objectItem);
				player.getCurrentRoom().addToInventory(objectItem);
				player.getCurrentRoom().chat(player.getPlayerName() + " dropped "+ objectItem.getItemName() + "\r\n");
			}else {
				player.broadcast("You must be holding an item to drop it");
			}
		}
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure Put Item Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Configure success action \r\n";
		menuString += "(05) Configure success message \r\n";
		menuString += "(06) Display structure \r\n";
		menuString += "(07) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 7, 1);
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
				u.broadcast(this.getStructure());
				break;
			case 7: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Configuration Menu.\r\n\r\n");	

	}

}
