package gum.actions;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import gum.Player;
import gum.User;
import gum.items.Item;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;


public class ActionSpawnItem extends Action {

	private Item createdItem = null;
	
	public ActionSpawnItem(){
		this.setActionName("Spawn Item");
		this.setRange(EffectRange.USER);
	}

	@Override
	public boolean doAction(ActionHeader actionHeader) {
		Player target = actionHeader.getTargetPlayer();
	
		ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(os);
        encoder.writeObject(createdItem);
        encoder.close();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        XMLDecoder decoder = new XMLDecoder(is);
        Item newItem = (Item)decoder.readObject();
        target.getCurrentRoom().addToInventory(newItem);
        target.getCurrentRoom().chat(this.getSuccessMessage());
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure Spawn Item Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Create item to be spawned \r\n";
		menuString += "(05) Configure Item to be spawned \r\n";
		menuString += "(06) Configure success action \r\n";
		menuString += "(07) Configure success message \r\n";
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
				createdItem = ItemBase.configAddItem(u);
				break;
			case 5:
				// if there's no item to configure create one. 
				if (createdItem == null){
					createdItem = ItemBase.configAddItem(u);
				}
				// if the user has created an item to configure, then configure it. 
				if (createdItem != null){
					createdItem.menu(u);
				}
				break;
			case 6:
				this.configSuccessAction(u);
				break;
			case 7:
				this.configActionSuccessMessage(u);
				break;
			case 8: 
				u.broadcast(this.getStructure());
				break;
			case 9: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Spawn Mob Configuration Menu.\r\n\r\n");	

	}
	
	@Override
	public boolean configActionRange(User u){
		u.broadcast("Spawn Item can only be used to spawn the Item in the users's \r\n " +
					"Current room.  \r\n");
		return false;	
	}
	
/*
	public boolean configActionRange(User u) throws MenuExitException {
		boolean done = false;
		String menuString =  "This will configure the action's range. Be default, the action effects the target.";
			   menuString =  "All effects of this action will be handled individually.";
		       menuString += "Configure "+this.getActionName()+" range:\r\n";
		menuString += "(1) Item       - effects the Item that contains this action \r\n";
		menuString += "(2) TargetItem - effects the the item that you've used the action container on \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 9, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1: 
				this.setRange(EffectRange.ITEM);
				break;
			case 2: 
				this.setRange(EffectRange.TARGETITEM);
				break;
			}
		}
		return done;
	}

*/
	public Item getCreatedItem() {
		return createdItem;
	}

	public void setCreatedItem(Item createdItem) {
		this.createdItem = createdItem;
	}
	
}
