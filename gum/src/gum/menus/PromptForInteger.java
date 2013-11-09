package gum.menus;

import gum.Room;
import gum.User;

public class PromptForInteger {

	private User user = null;
	private String menuOptions = "";
	private int maxResult = 2147483647;
	private int minResult = -2147483648;
	private int menuResult = 0;

	public PromptForInteger(User user, String menuOptions) {	
		this.user = user;
		this.menuOptions = menuOptions;
	}
	
	public PromptForInteger(User user, String menuOptions,int maxResult, int minResult){		
		this.user = user;
		this.menuOptions = menuOptions;
		this.maxResult = maxResult;
		this.minResult = minResult;
	}

	
	public synchronized boolean display() throws MenuExitException {
		boolean done = false;
		boolean result = false;

		String response = "";

		while (!done && !user.isDead()) {
			user.broadcast(menuOptions);
			response = user.getMenuResponse();
			if (response.equalsIgnoreCase("exit") || response.equalsIgnoreCase("e")){
				done = true;
				result = false;
			}else if (response.equals("exitall")){
				throw new MenuExitException();
			}else {
				try {
					Integer r = new Integer(response);
					if (r > maxResult ){
						user.broadcast("Invalid response. Cannot be greater than "+maxResult+"\r\n");
					}else if (r < minResult){
						user.broadcast("Invalid response. Cannot be less than "+minResult+"\r\n");
					}else {
						this.menuResult = r;
						result = true;
						done = true;
					}
				} catch (Exception e) {
					Room r = user.getCurrentRoom();
					if (r != null){
						user.getCurrentRoom().chat(
						user.getPlayerName() + " chats:" + response + "\r\n");
					}
				}
			}

		}

		return result;
	}

	public int getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	public int getMinResult() {
		return minResult;
	}

	public void setMinResult(int minResult) {
		this.minResult = minResult;
	}


	public int getResult() {
		return menuResult;
	}	
	
	
}
