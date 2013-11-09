package gum.actions;

import gum.Player;
import gum.User;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

public class ActionDestroyItem extends Action {

	public ActionDestroyItem() {
		this.setActionName("Destroy Item");
		this.setRange(EffectRange.ITEM);
		
	}
	
	@Override
	public void perform(ActionHeader newHeader) {
		this.setHeader(newHeader);
		this.doAction(newHeader);
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Player user = header.getPlayer();
		Player target = header.getTargetPlayer();
		ItemBase thisItem = header.getItem();
		
		
		//If either of the players in this action are holding the
		//Item, remove it from that player's hands. 
		if (user != null){
			user.broadcast(this.getSuccessMessage());
			if (user.getRightHand() == thisItem){
				user.setRightHand(null);
			}
			if (user.getLeftHand() == thisItem){
				user.setLeftHand(null);
			}
		}
		if (target != null){
			if (target.getLeftHand() == thisItem){
				target.setLeftHand(null);
			}
			if (target.getRightHand() == thisItem){
				target.setRightHand(null);
			}
		}
		// tell the item to remove it's self from it's container.
		// this should remove all references to the item from the 
		// game, allowing the garbage collector to clean it up.
		System.out.println("Item destroy called on:"+thisItem.getItemName());
		
		thisItem.destroy();
		
		if (this.getSuccessAction() != null){
			this.getSuccessAction().perform(header);
		}
		
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure Destroy Item Action:\r\n";
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
		u.broadcast("\r\nExiting Spawn Mob Configuration Menu.\r\n\r\n");	


	}
	
	@Override
	public boolean configActionRange(User u){
		u.broadcast("Destroy can only be used to destroy the item containing \r\n " +
					"the destroy action. This is intentional, to help prevent \r\n" +
					"the destruction of important plot items by an user item. \r\n.");
		return false;	
	}

}
