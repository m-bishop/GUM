package gum.actions;

import gum.Player;
import gum.User;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionCheckDescription extends Action {
	
	
	private String searchString;
	
	public ActionCheckDescription(){
	}
	
	public void init(){
		searchString = "";
		this.setActionName("CheckDescription");
		this.setRange(EffectRange.USER);
	}

	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure Check Description Item Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure Search String \r\n";
		menuString += "(03) Configure prereq setting\r\n";
		menuString += "(04) Configure range \r\n";
		menuString += "(05) Configure success action \r\n";
		menuString += "(06) Configure success message \r\n";
		menuString += "(07) Configure fail message \r\n";
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
				this.configActionSearchString(u);
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

	private void configActionSearchString(User u) throws MenuExitException{
		String menuString = "Enter a search string. '#Player' will be replaced by the player's name. \r\n";
			   menuString += "If the item description contains the search string, the action will succeed.\r\n";
		       menuString += "New Search String:"; 
		PromptForString s = new PromptForString(u, menuString);
		if (s.display()){
			this.setSearchString(s.getResult());
		}
	}
	
	
	@Override
	public boolean doAction(ActionHeader header) {
		String modifiedSearchString; // search string after parsing. 
		boolean result = false;
		Player p = header.getPlayer();
		
		String descriptionString = header.getItem().getDescription();
		modifiedSearchString = this.getSearchString().replace("#Player", header.getPlayer().getPlayerName());

		if (descriptionString.contains(modifiedSearchString)){
			p.broadcast(this.getSuccessMessage());
			result = true;
		}else {
			p.broadcast(this.getFailMessage());
		}
		return result;
	}
	
	@Override
	public boolean configActionRange(User u){
		u.broadcast("Look can only target the user who triggered the action \r\n" );
		return false;	
	}
	
	public String toString(){
		String result;
		
		result = "\r\n"
				+ "Search String: "+this.getSearchString() + "\r\n";
		
		return result;
	}
	
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}
