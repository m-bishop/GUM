package gum.actions.item;


// This action allows you to read multi-page long descriptions
// in roughly the same style as the 'more' command in unix.


import java.util.ArrayList;
import java.util.Arrays;

import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionItemDescriptionEditor extends Action {
	
	private boolean actionPerformed ; // are we currently performing this action?
	private ActionHeader localHeader;
	private int lineCount;
	private ArrayList<String> lines;
	
	public ActionItemDescriptionEditor(){
	}
	
	public void init(){
		this.setRange(EffectRange.USER);
		this.setActionName("DescriptionEditor");
		actionPerformed = false; // are we currently performing this action?
		localHeader = null;
		lineCount = 0;
		lines = new ArrayList<String>();
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
		this.lines.addAll(Arrays.asList(item.getLongDescription().split("\\r?\\n"))) ;
		String menuString = "(N)ext page (P)revious Page (E)xit (I)nsert line (D)elete or line number to edit \r\n" +
				            "               ";
		int pageCount = 0;
		this.lineCount = 0;
		boolean done = false;
		String formattedCount,command = "";
		
		while ((this.lineCount < lines.size()) && !done){
			pageCount = 0;
			while ((this.lineCount <= lines.size()) && pageCount < 10){
				pageCount++;this.lineCount++;
				formattedCount = String.format("%02d", this.lineCount);
				player.broadcast("Line:"+formattedCount+"==+=========+=========+========+=========+=========+=========+=========+");
				player.broadcast(lines.get(this.lineCount));
			}
			if (this.lineCount < lines.size()){ // ask to edit file
				PromptForString s = new PromptForString((User) player,menuString);
				try {
					if (s.display()){
						command = s.getResult();
						done = this.parseInput(command);
					} 
				} catch (MenuExitException e) {
					// TODO add logging
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	
	public boolean parseInput(String command) throws MenuExitException{
		boolean done = false;
		int commandLine;
		
		
		try {
			commandLine = Integer.parseInt(command);
			this.lineCount = this.lineCount - 10; // we want to re-display the same page
			if (this.lineCount < 0){
				this.lineCount = 0;
			}
			this.editLine(commandLine, ((User) this.localHeader.getPlayer()));
		} catch (NumberFormatException e){ // if it's not a number, parse it. 
			
		if (command.equalsIgnoreCase("E") || command.equalsIgnoreCase("Exit")){
			this.localHeader.getPlayer().broadcast("Goodbye!\r\n");
			done = true;
		} 
		else if (command.equalsIgnoreCase("P") || command.equalsIgnoreCase("Previous")){
			this.lineCount = this.lineCount - 20;
			if (this.lineCount < 0){
				this.lineCount = 0;
			}
		}
		else if (command.equalsIgnoreCase("N") || command.equalsIgnoreCase("Next")){
			// do nothing, this is the default if we don't break out of the loop anyway. 
		}
		else if (command.equalsIgnoreCase("I") || command.equalsIgnoreCase("Insert")){
			this.lineCount = this.lineCount - 10; // we want to re-display the same page
			if (this.lineCount < 0){
				this.lineCount = 0;
			}
			this.insertLine((User) this.localHeader.getPlayer());
		}
		else if (command.equalsIgnoreCase("D") || command.equalsIgnoreCase("Delete")){
			this.lineCount = this.lineCount - 10;
			if (this.lineCount < 0){
				this.lineCount = 0;
			}
			this.deleteLine(this.chooseLine((User) this.localHeader.getPlayer()));
		}
	
	} // catch string conversion	
		return done;
	}

	public void insertLine(User u) throws MenuExitException{
		// insert a new line after lineNumber
		u.broadcast("insert line:\r\n");
		String iMenuString = "Line number to insert after:";
		String sMenuString = "New Line:\r\n";
		Boolean done = false;
		Integer eLine = 0;
		
		PromptForInteger i  = new PromptForInteger(u,iMenuString);
		PromptForString s = new PromptForString(u,sMenuString);
		
		while (!done){
			i.display();
			eLine = i.getResult();
			if ((eLine > lines.size()) || (eLine < 0)){
				u.broadcast("Line out of bounds.\r\n");
			} else {
				s.display();
				this.lines.add(i.getResult()+1,s.getResult());
				done = true;
			}
		}
	}	

	public void editLine(int editLine, User u) throws MenuExitException{
		// replace line number with new line
		
		String sMenuString = "New Line:\r\n";
		PromptForString s = new PromptForString(u,sMenuString);

			if ((editLine > lines.size()) || (editLine < 0)){
				u.broadcast("Line out of bounds.\r\n");
			} else {
				s.display();
				this.lines.set(editLine,s.getResult());
			}
	}
	
	@Override
	public void menu(User u) throws MenuExitException{
		if (!actionPerformed){//if they're not performing the action, show config menu.
			this.configMenu(u); 
		}else {
			this.presentData(u);
		}
	}
	
	public int chooseLine(User u) throws MenuExitException{
		String menuString = "Choose a line: \r\n";
		int lineNumber = -1;
		
		PromptForString s = new PromptForString(u,menuString);
		try {
			if (s.display()){
				try {
					lineNumber = Integer.parseInt(s.getResult());
				} catch (NumberFormatException e){
					u.broadcast("Invalid Line Number");
				}
			} 
		} catch (MenuExitException e) {
			e.printStackTrace();
			throw e;
		}
		return lineNumber;
	}
	
	public void deleteLine(int deletedLine){
		
		this.lines.remove(deletedLine);
		
/*
		String first[] = java.util.Arrays.copyOfRange(this.lines, 0, deletedLine);
		String second[] = java.util.Arrays.copyOfRange(this.lines, deletedLine+1,this.lines.length );
		
		String[] result = java.util.Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		
		this.lines = result;
		
*/
	}
	
	public void configMenu(User u) throws MenuExitException {
		
		String menuString = "Configure Description Editor Item Action:\r\n";
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
