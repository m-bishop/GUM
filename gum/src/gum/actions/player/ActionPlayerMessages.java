package gum.actions.player;

import gum.Player;
import gum.User;
import gum.UserMessage;
import gum.World;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

import java.util.Date;
import java.util.Enumeration;

public class ActionPlayerMessages extends Action {

	private boolean actionPerformed = false;
	
	public ActionPlayerMessages(){
	}
	
	public void init(){
		this.setActionName("Messages");
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
	
	private void configMenu(User u){
		u.broadcast("This action has no configurable options.");
	}
	
	private void performMenu(User u) throws MenuExitException{
		String menuString = "Welcome to Mail:\r\n";
		menuString += "(1) Read Messages \r\n";
		menuString += "(2) New Message\r\n";
		menuString += "Choose from the above. Type 'exit' to exit.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 2, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.chooseMessages(u);
				break;		
			case 2:
				this.writeMessage(u,null);
				break;		
			}
		}
	}

	private void chooseMessages(User u){
		
		Integer index = 0;
		Integer count = 0;
		Integer selectedMessage = 0;
		String selection = null;
		boolean done = false;
		boolean received = false;
		
		try{
			if (!(u.getMessages().isEmpty())){
				
				while (!done){
					String menuString = "Read Messages:\r\n";
					while ((index < u.getMessages().size()) && (count < 10)){
						String subject = u.getMessages().get(index).getSubject();
						if (subject.length() > 75){
							subject = subject.substring(0,75);
						}
						menuString += "("+index+")"+subject+"\r\n"; 
						index = index+1;
						count = count+1;
					}
					count = 0;
					menuString += "(N)Next (P)rev (E)xit \r\n";
				
					PromptForString p = new PromptForString(u, menuString);

					received = p.display();
					if (received){
						selection = p.getResult();
						try {
							selectedMessage = Integer.parseInt(selection);
						} catch (Exception e){
							// ignore it, it's fine if it's not an integer.
							selectedMessage = null;
						}
						if (selection.equalsIgnoreCase("exit") || selection.equalsIgnoreCase("e")){
							done = true;
						} else if(selection.equalsIgnoreCase("prev") || selection.equalsIgnoreCase("p")){
							index = index - 10;
							if (index < 0){
								index = 0;
							}
						} else if (selection.equalsIgnoreCase("next") || selection.equalsIgnoreCase("n")){
							index = index + 10;
							if (index > u.getMessages().size()){
								index = u.getMessages().size();
							}
						} else if (selectedMessage != null){
							readMessage(u,selectedMessage);
							
						}
					}
			}
			}else {
				u.broadcast("You have no messages.\r\n");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void readMessage(User u, Integer selectedMessage) throws MenuExitException{
		String menuString;
		boolean done = false;
		
		
		while (!done){
			
			UserMessage message = u.getMessages().get(selectedMessage);	
		
		    menuString = ( "--------------------------------------------------------------------------------"+
		    				"Sender:"+message.getSender()+"\r\n"+
		    				"Subject:"+message.getSubject()+"\r\n"+
		    				message.getMessage()+
		    				"\r\n--------------------------------------------------------------------------------\r\n"
		    				+ "(N)Next (P)rev (D)elete (R)eply (E)xit \r\n"
		    			  );	
		 
		
		PromptForString p = new PromptForString(u, menuString);

		boolean received = p.display();
		
		if (received){
			String selection = p.getResult();
		
			if (selection.equalsIgnoreCase("exit") || selection.equalsIgnoreCase("e")){
				done = true;
			} else if(selection.equalsIgnoreCase("prev") || selection.equalsIgnoreCase("p")){
				selectedMessage = selectedMessage - 1;
				if (selectedMessage < 0){
					selectedMessage = 0;
				}
			} else if (selection.equalsIgnoreCase("next") || selection.equalsIgnoreCase("n")){
				selectedMessage = selectedMessage + 1;
			
				if (selectedMessage > u.getMessages().size()){
					selectedMessage = u.getMessages().size();
				}
			} else if (selection.equalsIgnoreCase("delete") || selection.equalsIgnoreCase("d")){
				u.getMessages().remove(selectedMessage);
				if (selectedMessage > u.getMessages().size()){
					selectedMessage = u.getMessages().size();
				}
			} else if (selection.equalsIgnoreCase("reply") || selection.equalsIgnoreCase("r")){
				writeMessage(u,message.getSender());
			}
		}
		}
	}
	
	private void writeMessage(User u, String replyTo) throws MenuExitException{
		
		boolean recieved = false;
		boolean found = false;
		boolean loadedFromFile = false;
		String result = null;
		String subject = "";
		String menuString = "Enter recipient user name.\r\n";
		String fileName = null;
		PromptForString s = null;
		User recipient = new User();
		
		if (replyTo != null){
			if (!replyTo.isEmpty()){
			    result = replyTo;	
			}
		}
		
		u.broadcast("write Message");
        
        if (result == null){
        	s = new PromptForString(u, menuString);
        	recieved = s.display();
		
        	if (recieved){ // if user entered a recipient, try to find that user
			
        		result = s.getResult();
        	}
		}
		
		
			Enumeration<User> e = User.getUsers().elements();//TODO dangerous to access, in this instance a copy of the vector and it's elements may be preferred. 
			while (e.hasMoreElements() && !found) {
				Player player = (Player) e.nextElement();
				if (player.getPlayerName().equals(result)) {
					found = true;
					recipient = (User) player; 
					//TODO if you're writing a message to someone and they log off, this will fail. 
				}
			}
			
			if (!found){
				fileName = World.getArea().getUserDir() + "/" + s.getResult() + ".xml";
				found = recipient.loadUser(fileName);
				if (found){
					loadedFromFile = true;				}
			}
		

		if (found){
			menuString = "Enter a message subject below.\r\n";
			s = new PromptForString(u, menuString);
			result = "";
			boolean done = s.display();
			if(done){
				subject = s.getResult();
			}
			
			menuString = "Enter a new message below.\r\n";
			s = new PromptForString(u, menuString);
			result = "";
			done = s.display();

			while (done) {
				result += s.getResult();
				result += "\r\n";
				menuString = "New Message:\r\n"
						+ result
						+ "\r\n\r\n Type another line to add to the message, 'exit' to quit.\r\n";
				s.setMenuOptions(menuString);
				done = s.display();
			}		
			// create the new message
			UserMessage m = new UserMessage();
			m.setSender(u.getPlayerName());
			m.setSubject(subject);
			m.setMessage(result);
			m.setRead(false);
			m.setTimeStamp(new Date());
			recipient.getMessages().addElement(m);
			
			if (loadedFromFile){
				recipient.quit();
			} else {
				recipient.broadcast("YOU HAVE MAIL!\r\n");
			}
			u.broadcast("Message Sent.\r\n");
			
			
		} else {
			u.broadcast("User not found!");
		}
	}
	
	@Override
	public boolean doAction(ActionHeader header) {
		
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
	public boolean configActionRange(User u){
		u.broadcast(" Player Action can only be used by the player who triggered it. \r\n" );
		return false;	
	}

}
