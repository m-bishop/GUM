package gum.actions;

import gum.Player;
import gum.User;
import gum.items.Item;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

import java.util.Iterator;
import java.util.Vector;

public class ActionBuy extends ActionMerchant{

	public ActionBuy(){
		this.setActionName("Buy");
	}
	
	@Override
	public void configMenu(User u) throws MenuExitException{
		String menuString = "Configure Buy Action: Allows a Mob to buy things from the user.\r\n";
		menuString += "(01) Configure action name \r\n";
		menuString += "(02) Configure mark-up (buy at this percent of item's value.) \r\n";
		menuString += "(03) Configure prereq setting\r\n";
		menuString += "(04) Configure range \r\n";
		menuString += "(05) Configure menu description \r\n";
		menuString += "(06) Configure success action  (triggered when user sells something)\r\n";
		menuString += "(07) configure success message (triggered when user sells something)\r\n";
		menuString += "(08) Configure failure action  (triggered when user exits the 'buy' menu) \r\n";
		menuString += "(09) configure failure message (triggered when user exits the 'buy' menu) \r\n";
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
				this.setMarkUp(this.configMarkupValue(u)); 
				break;
			case 3:
				this.configActionPrereqSetting(u);
				break;
			case 4:
				this.configActionRange(u);
				break;
			case 5:
				this.configMenuDescription(u);
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
		u.broadcast("\r\nExiting Buy Configuration Menu.\r\n\r\n");			
	}	
	
	
	public void performMenu(User u) throws MenuExitException {
		String menuString =  this.getMenuString();
		int count = 0;
		int value = 0;
		int adjustedValue = 0;
		Player buyer = this.getHeader().getPlayer();
		
		Vector<Item> items = u.getItems();
		Vector<Item> itemsForSale = new Vector<Item>();
		
	
		for (Item i : items){
			value = i.getSetting("value");
			float markup = this.getMarkUp();
			// if markup is 0, set to 90% of value
			if (markup == 0f){
				this.setMarkUp(90);
				markup = 90f;
			}
			adjustedValue = (int) (value * (markup/100));
			System.out.println("Found Item:"+i.getItemName()+" with the value:"+value+" adjusted to:"+adjustedValue+" with markup:"+markup);
			if (value > 0){
					menuString += "("+count+") "+i.getItemName()+" for "+adjustedValue+"\r\n";
					itemsForSale.add(count, i);
				count++;
			}
		}
        if (count == 0){
        	u.broadcast("You have no items to sell\r\n");
        }else {
        	menuString += "\r\nType 'exit' to exit menu. \r\n";
        	PromptForInteger p = new PromptForInteger(u, menuString, count, 0);
        	if (p.display()){
        		//Item itemChosen = Items.get(p.getResult());
        		Item itemChosen = itemsForSale.remove(p.getResult());
        		items.remove(itemChosen);
        		
        		//check to see if the buyer already has an item with the same name. If he does, don't add it. 
        		Vector<Item> buyerInv = buyer.getItems();
        		boolean found = false;
        		Iterator<Item> i = buyerInv.iterator();
        		while (i.hasNext() && !found){
        			if ((i.next()).getItemName().equalsIgnoreCase(itemChosen.getItemName())){
        				found = true;
        			}
        		}
        		if (!found){
        			buyer.addToInventory(itemChosen);
        		}
        		float markup = this.getMarkUp();
        		System.out.println("changing player money to:"+(itemChosen.getSetting("value") * (markup/100)));
    			adjustedValue = (int) (itemChosen.getSetting("value") * (markup/100));
        		
        		u.setSetting("money", u.getSetting("money")+adjustedValue);
        		u.broadcast(this.getSuccessMessage());
        		if (this.getSuccessAction() != null){
        			this.getSuccessAction().perform(this.getHeader());
        		}
			
        	} else {
        		u.broadcast(getFailMessage());
        		if (this.getFailAction() != null){
        			this.getFailAction().perform(this.getHeader());
        		}
        	}
        }
		this.setActionPerformed(false);// we're no longer performing this action. 
		
	}
}
