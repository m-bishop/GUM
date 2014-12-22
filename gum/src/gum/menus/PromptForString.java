package gum.menus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
			}else if (response.startsWith("*file")){
				
				  try {
						this.loadDescriptionFromFile(response.substring(6));
						user.broadcast("Description Loaded.\r\n");
						done = true;
						result = true;
					} catch (FileNotFoundException e) {
						user.broadcast("File Not Found!\r\n");
						done = true;
						result = false;
					}
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
	
	private void loadDescriptionFromFile(String filename) throws FileNotFoundException{

        File file = new File(filename);
        StringBuilder description = new StringBuilder();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
            	description.append(scanner.nextLine()+"\r\n");
            }
            menuResult = description.toString();    
    }
}
