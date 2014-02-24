package gum.actions;

import java.util.Vector;

import gum.Player;
import gum.User;
// import gum.actions.Action.EffectRange;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionNotify extends Action {
	
	int messageIndex = 0;
	
	
	Vector<String> messages;

	public ActionNotify(){
	}
	
	public void init(){
		messages = new Vector<String>();
		this.setRange(EffectRange.USER);
		this.setActionName("Notify");
	}
	
	@Override
	public void performByRange(){
		super.performByRange();
		messageIndex++;
		if (messageIndex >= messages.size()){
			messageIndex = 0; // roll the message Index over.
		}
	}
	
	@Override
	public boolean doAction(ActionHeader actionHeader) {
		Player target = actionHeader.getTargetPlayer();
		
		boolean result = false;
		try{
			target.broadcast(messages.get(messageIndex));
		} catch (java.lang.ArrayIndexOutOfBoundsException e){
			target.broadcast("message index error, index out of bounds:"+messageIndex+"\r\n");
		} catch (Exception e){
			System.out.println("null pointer exception in Action Notify\r\n");
		}
		
		
		if ((messageIndex+1) == messages.size()){
			result = true; // we've displayed the last message, do 'success' action. 
		}
		return result;
	}

	@Override
	public void menu(User u) throws MenuExitException{
		
		String menuString = "Configure Notify Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Configure success action \r\n";
		menuString += "(05) Configure failure action \r\n";
		menuString += "(06) Configure messages \r\n";
		menuString += "(07) Display structure \r\n";
		menuString += "(08) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 8, 1);
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
				this.configFailureAction(u);
				break;
			case 6:
				this.configMessages(u);
				break;
			case 7: 
				u.broadcast(this.getStructure());
				break;
			case 8: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Notify Configuration Menu.\r\n\r\n");	
	}	
	
	public boolean configActionRange(User u) throws MenuExitException {
		boolean done = false;
		String menuString =  "This will configure the action's range. Be default, the action effects the target.";
			   menuString =  "All effects of this action will be handled individually.";
		       menuString += "Configure "+this.getActionName()+" range:\r\n";
		menuString += "(1) User    	  - effects the user who trigggered the action.\r\n";
		menuString += "(2) Target  	  - effects the target chosen for this action. \r\n";
		menuString += "(3) Room    	  - effects everyone in the room.  \r\n";
		menuString += "(4) World   	  - effects everyone in the world. \r\n";
		menuString += "(5) Enemies 	  - effects all enemies in this battle. \r\n";
		menuString += "(6) Allies  	  - effects all allies in this battle.\r\n";
		menuString += "(7) Battle  	  - effects everyone in this battle. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 7, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.setRange(EffectRange.USER);
				break;
			case 2: 
				this.setRange(EffectRange.TARGET);
				break;
			case 3: 
				this.setRange(EffectRange.ROOM);
				break;
			case 4: 
				this.setRange(EffectRange.WORLD);
				break;
			case 5: 
				this.setRange(EffectRange.ENEMIES);
				break;
			case 6: 
				this.setRange(EffectRange.ALLIES);
				break;
			case 7: 
				this.setRange(EffectRange.BATTLE);
				break;
			}
		}
		return done;
	}
	
	public void configMessages(User u) throws MenuExitException {
		String menuString = "Configure messages:\r\n";
		menuString += "(1) List messages \r\n";
		menuString += "(2) Add a message \r\n";
		menuString += "(3) Remove a message \r\n";

		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configViewMessage(u);
				break;
			case 2: 
				this.configNewMessage(u);
				break;
			case 3: 
				this.configRemoveMessage(u);
				break;
			}
		}
	}
	
	public void configViewMessage(User u){
		String display = "Current Messages:\r\n\r\n";
		int i = 1;
		
		for (String message : messages){
			display += "("+i+") "+message+"\r\n";
			i++;
		}
		
		u.broadcast(display);
	}
	
	public void configRemoveMessage(User u) throws MenuExitException{	
		
		String menuString =  "Enter a message index from the list below to remove it.\r\n";
		int i = 1;
		
		for (String message : messages){
			menuString += "("+i+") "+message+"\r\n";
			i++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, messages.size(), 1);
	       if (p.display()){
	    	   if (messages.remove(p.getResult()-1) != null){
	    		   u.broadcast("Message removed.\r\n");
	    	   }else {
	    		   u.broadcast("Message not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Messages unchanged.\r\n");
	       }
	}
	
	public void configNewMessage(User u) throws MenuExitException {
		String menuString = "Enter a new message below, and it will be added to the end of the message list.\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Message:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the description, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		messages.add(result);
	}

	public int getMessageIndex() {
		return messageIndex;
	}

	public void setMessageIndex(int messageIndex) {
		this.messageIndex = messageIndex;
	}

	public Vector<String> getMessages() {
		return messages;
	}

	public void setMessages(Vector<String> messages) {
		this.messages = messages;
	}
	
}
