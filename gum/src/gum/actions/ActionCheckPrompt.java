package gum.actions;

import gum.Player;
import gum.User;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionCheckPrompt extends Action {

	private boolean actionPerformed; // are we currently performing this action?
	private String searchString;
	private String promptString;
	private ActionHeader localHeader;

	public ActionCheckPrompt() {
	}

	public void init() {
		searchString = "";
		this.setActionName("CheckPrompt");
		this.setRange(EffectRange.USER);
	}
	
	@Override
	public void menu(User u) throws MenuExitException{
		if (!actionPerformed){//if they're not performing the action, show config menu.
			this.configMenu(u); 
		}else {
			this.performMenu(u);
		}
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String getPromptString() {
		return promptString;
	}

	public void setPromptString(String promptString) {
		this.promptString = promptString;
	}

	
	public void configMenu(User u) throws MenuExitException {
		String menuString = "Configure Check Prompt Item Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prompt String \r\n";
		menuString += "(03) Configure Search String \r\n";
		menuString += "(04) Configure prereq setting\r\n";
		menuString += "(05) Configure range \r\n";
		menuString += "(06) Configure success action \r\n";
		menuString += "(07) Configure success message \r\n";
		menuString += "(08) Configure fail message \r\n";
		menuString += "(09) Display structure \r\n";
		menuString += "(10) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";

		PromptForInteger p = new PromptForInteger(u, menuString, 10, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2:
				this.configActionPromptString(u);
				break;
			case 3:
				this.configActionSearchString(u);
				break;
			case 4:
				this.configActionPrereqSetting(u);
				break;
			case 5:
				this.configActionRange(u);
				break;
			case 6:
				this.configSuccessAction(u);
				break;
			case 7:
				this.configActionSuccessMessage(u);
				break;
			case 8:
				this.configActionFailMessage(u);
				break;
			case 9:
				u.broadcast(this.getStructure());
				break;
			case 10:
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Configuration Menu.\r\n\r\n");

	}

	private void configActionPromptString(User u) throws MenuExitException {
		String menuString = "Enter a prompt string. This string will be displayed to the user when \r\n";
		menuString += "this action is triggered. For example, to promt for a password, you could \r\n";
		menuString += "enter 'Password:' here.\r\n";
		menuString += "New prompt String:";
		PromptForString s = new PromptForString(u, menuString);
		if (s.display()) {
			this.setPromptString(s.getResult());
		}
	}
	
	private void configActionSearchString(User u) throws MenuExitException {
		String menuString = "Enter a search string. '#Player' will be replaced by the player's name. \r\n";
		menuString += "If the user enters this string at the prompt, the action will succeed.\r\n";
		menuString += "New Search String:";
		PromptForString s = new PromptForString(u, menuString);
		if (s.display()) {
			this.setSearchString(s.getResult());
		}
	}

	
	public void performMenu(User u) throws MenuExitException{

		String modifiedSearchString; // search string after parsing.
		Player p = u;

		String menuString = this.getPromptString();
		
		
		PromptForString s = new PromptForString(u, menuString);
		
			if (s.display()) {
				String promptInput = (s.getResult());
			
				modifiedSearchString = this.getSearchString().replace("#Player", u.getPlayerName());

					if (promptInput.equals(modifiedSearchString)) {
						p.broadcast(this.getSuccessMessage());
						if (this.getSuccessAction() != null){
							this.getSuccessAction().perform(this.localHeader);
						}
						
						
			} else {
				p.broadcast(this.getFailMessage());
				
				if (this.getFailAction() != null){
					this.getFailAction().perform(this.localHeader);
				}
			}	
		}
	}

	@Override
	public boolean doAction(ActionHeader header) {
		this.localHeader = header;
		Player target = header.getTargetPlayer();
		this.actionPerformed = true;
		
		
		// this action only works on a player. If the target is a mob, just ignore it.
		if (target instanceof User){
			User u = (User)header.getTargetPlayer();
			if (u.inMenu){// if this is a submenu, then we're already in a menuhandler thread.
				try{
				this.menu(u);
				}catch (MenuExitException e){
					//TODO figure out if I should allow to escape game menus.
				}
			}else {
				new MenuHandler(u, this);
			}
		}
		
		return false;// doesn't matter, since there will be no success or fail actions.
	}
		
	
	@Override
	public boolean configActionRange(User u) {
		u.broadcast("Look can only target the user who triggered the action \r\n");
		return false;
	}

}
