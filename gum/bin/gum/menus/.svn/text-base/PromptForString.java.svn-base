package gum.menus;

import gum.User;

public class PromptForString {

	private User user = null;
	private String menuOptions = "";
	private String menuResult = "";

	public PromptForString(User user, String menuOptions) {
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
			}else if (response.equals("exitall")){
				throw new MenuExitException();
			}else {
				done = true;
				result = true;
				menuResult = response;
			}
		}

		return result;
	}
	
	public void setMenuOptions(String newMenu){
		menuOptions = newMenu;
	}

	public String getResult() {
		return menuResult;
	}		
}
