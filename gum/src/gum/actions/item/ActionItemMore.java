package gum.actions.item;


// This action allows you to read multi-page long descriptions
// in roughly the same style as the 'more' command in unix.


import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForBoolean;
import gum.menus.PromptForInteger;

public class ActionItemMore extends Action {
	
	private boolean actionPerformed = false; // are we currently performing this action?
	private ActionHeader localHeader = null;
	
	
	public ActionItemMore(){
		super();
		this.setRange(EffectRange.USER);
		this.setActionName("More");
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

	
	public boolean presentData(User u) {
		
		Player player = this.localHeader.getPlayer();
		ItemBase item = (ItemBase)localHeader.getItem();
		String lines[] = item.getLongDescription().split("\\r?\\n");
		String menuString = "More? (Yes or No)";
		int pageCount,count = 0;
		boolean done = false;
		
		while ((count < lines.length) && !done){
			pageCount = 0;
			while ((count <= lines.length) && pageCount < 20){
				pageCount++;count++;
				player.broadcast(lines[count]);
			}
			if (count < lines.length){ // ask if they want more
				PromptForBoolean b = new PromptForBoolean((User) player,menuString);
				try {
					if (b.display()){
						done = !b.getResult();
					}
				} catch (MenuExitException e) {
					// TODO add logging
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}

	
	@Override
	public void menu(User u) throws MenuExitException{
		if (!actionPerformed){//if they're not performing the action, show config menu.
			this.configMenu(u); 
		}else {
			this.presentData(u);
		}
	}
	
	public void configMenu(User u) throws MenuExitException {
		
		String menuString = "Configure Look Item Action:\r\n";
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
		u.broadcast("Look can only target the user who triggered the action \r\n" );
		return false;	
	}


}
