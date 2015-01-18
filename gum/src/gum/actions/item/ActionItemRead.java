package gum.actions.item;

import java.io.File;
import java.util.Scanner;

import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

public class ActionItemRead extends Action {
	
	public ActionItemRead(){
	}
	
	public void init(){
		this.setRange(EffectRange.USER);
		this.setActionName("Read");		
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Player player = header.getPlayer();
		ItemBase item = (ItemBase)header.getItem();
		
		try {
	        File file = new File(item.getFilename());
	       
	            Scanner scanner;
				
					scanner = new Scanner(file);
					while (scanner.hasNextLine()) {
		            	
		            	player.broadcast(scanner.nextLine());
					}
				} catch (Exception e) {
					
					e.printStackTrace();
					player.broadcast("File Read Error. Contact your system administrator \r\n");
				}
	            
	             
		
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure Read Item Action:\r\n";
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
	
	@Override
	public boolean configActionRange(User u){
		u.broadcast("Read can only target the user who triggered the action \r\n" );
		return false;	
	}


}
