package gum.actions;

import gum.Player;
import gum.User;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public abstract class ActionMerchant extends Action {
	private boolean actionPerformed = false; // are we currently performing this action?
	private String menuString = "";
	private ActionHeader localHeader = null;
	private int markUp = 0;
	
	public abstract void performMenu(User u) throws MenuExitException;
	public abstract void configMenu(User u) throws MenuExitException;
		
	
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
		
		return false;// doesn't matter, since the success/fail logic is moved to the 'performMenu' menthod. 
	}

	public void performOnTarget(){
		// since the 'buy' action won't be something a user can do to himself, and the success/fail logic had to be moved
		// this override does very little. 
		
		this.doAction(this.getHeader());
		
	}	
	
	@Override
	public void menu(User u) throws MenuExitException{
		if (!actionPerformed){//if they're not performing the action, show config menu.
			this.configMenu(u); 
		}else {
			this.performMenu(u);
		}
	}
	
	public boolean configActionRange(User u) {
		boolean done = false;
		u.broadcast("This action can only effect the 'target'\r\n");
		return done;
	}

	public boolean configMenuDescription(User u) throws MenuExitException {
		String menuString = "Enter new menu text.\r\n";
		menuString += "Current menu text:\r\n"
				+ this.getMenuString() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Menu:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the menu, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		this.setMenuString(result);
		return done;
	}
	
    public int configMarkupValue(User u) throws MenuExitException{
    	int result = 0;
    	String menuString = "Enter a new markup value.\r\n\r\n";
    	       
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		result = (i.getResult());
    	}
    	return result;
    } 
	
	public boolean isActionPerformed() {
		return actionPerformed;
	}

	public void setActionPerformed(boolean actionPerformed) {
		this.actionPerformed = actionPerformed;
	}

	public String getMenuString() {
		return menuString;
	}

	public void setMenuString(String menuString) {
		this.menuString = menuString;
	}

	public ActionHeader getLocalHeader() {
		return localHeader;
	}

	public void setLocalHeader(ActionHeader localHeader) {
		this.localHeader = localHeader;
	}

	public int getMarkUp() {
		return markUp;
	}

	public void setMarkUp(int markUp) {
		this.markUp = markUp;
	}
	
	
}
