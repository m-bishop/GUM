package gum.menus;

import gum.User;

public class PromptForBoolean {

	private User user = null;
	private String menuOptions = "";
	private boolean menuResult = false;

	public PromptForBoolean(User user, String menuOptions) {
		this.user = user;
		this.menuOptions = menuOptions;
	}

	public boolean display() throws MenuExitException {
		boolean done = false;
		boolean result = false;

		String response = "";

		while (!done && !user.isDead()) {
			user.broadcast(menuOptions);
			response = user.getMenuResponse();
			if (response.equalsIgnoreCase("exit")){
				done = true;
				result = false;
			} else if (response.equals("exitall")){
				throw new MenuExitException();
			} else if (response.equalsIgnoreCase("yes")){
				done = true;
				result = true;
				menuResult = true;
			} else if (response.equalsIgnoreCase("no")){
				done = true;
				result = true;
				menuResult = false;				
			} else {	
				user.getCurrentRoom().chat(
				user.getPlayerName() + " chats:" + response + "\r\n");
				
			}

		}

		return result;
	}

	public boolean getResult() {
		return menuResult;
	}	
	
	
}
