package gum.menus;

import gum.User;

public class MenuHandler extends Thread {

	User user = null;
	MenuContainer menu = null;

	public MenuHandler(User u, MenuContainer m) {
		this.user = u;
		this.menu = m;
		this.start();
	}

	public void run() {
		try{
			menu.menu(user);
		}catch (MenuExitException e){
			user.broadcast("Exiting Menu\r\n");
		}catch (Exception e){
			e.printStackTrace();
			user.broadcast("Error in Menu\r\n");
		}
	}

}
