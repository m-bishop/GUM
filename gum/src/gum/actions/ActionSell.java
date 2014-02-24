package gum.actions;

import gum.User;
import gum.items.Item;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

public class ActionSell extends ActionMerchant {

	public ActionSell(){
	}
	
	public void init(){
		this.setActionName("Sell");
		this.setRange(EffectRange.USER);
	}
	
	@Override
	public void configMenu(User u) throws MenuExitException{
		String menuString = "Configure Sell Action: Allows a Mob to sell things to the user.\r\n";
		menuString += "(01) Configure action name \r\n";
		menuString += "(02) Configure mark-up (sell at this percent of the item's value.) \r\n";
		menuString += "(03) Configure prereq setting\r\n";
		menuString += "(04) Configure range \r\n";
		menuString += "(05) Configure menu description \r\n";
		menuString += "(06) Configure success action  (triggered when user buys something)\r\n";
		menuString += "(07) configure success message (triggered when user buys something)\r\n";
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
		int money = u.getSetting("money");
		int adjustedValue = 0;
		
		Vector<Item> Items = this.getHeader().getPlayer().getItems();
		
		menuString += "You have: "+u.getSetting("money")+" to spend.\r\n";
	
		for (Item i : Items){
			value = i.getSetting("value");
			float markup = this.getMarkUp();
			//if no markup set, sell at value
			if (markup == 0f){
				markup = 100f;
			}
			adjustedValue = (int) (value * (markup/100));
			
				if (money < value){
					menuString += "%RED "+"("+count+") "+i.getItemName()+" for "+adjustedValue+" %SANE \r\n";
				}else {
					menuString += "("+count+") "+i.getItemName()+" for "+adjustedValue+"\r\n";
				}
				count++;
			
		}
       
		menuString += "\r\nType 'exit' to exit menu. \r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, count, 0);
		if (p.display()){
			Item itemChosen = Items.get(p.getResult());
			value = itemChosen.getSetting("value");
			
			float markup = this.getMarkUp();
			adjustedValue = (int) (value * (markup/100));
			
			if (money > adjustedValue){
				u.setSetting("money", (money - adjustedValue));
				//actions.get(keys.get(p.getResult())).perform(this.localHeader);
			
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				XMLEncoder encoder = new XMLEncoder(os);
				encoder.writeObject(itemChosen);
				encoder.close();
				ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
				XMLDecoder decoder = new XMLDecoder(is);
               
				u.addToInventory((Item)decoder.readObject());
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
