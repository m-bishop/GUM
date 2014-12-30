package gum.actions;

import gum.Player;
import gum.User;
import gum.UserParser;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionCommandLoop extends Action {

	private boolean actionPerformed; // are we currently performing this action?
	private ActionHeader localHeader;
	private String prompt;

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public ActionCommandLoop() {
	}

	public void init() {
		this.setActionName("CommandLoop");
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
	
	public void configMenu(User u) throws MenuExitException {
		String menuString = "Configure Command Loop Item Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure prompt string\r\n";
		menuString += "(04) Display structure \r\n";
		menuString += "(05) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";

		PromptForInteger p = new PromptForInteger(u, menuString, 5, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2:
				this.configActionPrereqSetting(u);
				break;
			case 3:
				this.configActionPromptString(u);
				break;
			case 4:
				u.broadcast(this.getStructure());
				break;
			case 5:
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
			this.setPrompt(s.getResult());
		}
	}
	
	public void performMenu(User u) throws MenuExitException{

		String menuString = this.getPrompt().replace("\r\n", "");
		UserParser parser = new UserParser();
		
		while (true){		
			PromptForString s = new PromptForString(u, menuString);
			
				if (s.display()) {
					String promptInput = (s.getResult()) + " " + (this.localHeader.getItem().getName());
					parser.parsePlayerCommand(u, promptInput);
				}
				else {
					throw new MenuExitException();
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
