package gum.actions;

import gum.User;
import gum.actions.Action.EffectRange;
import gum.menus.MenuContainer;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionTimed implements MenuContainer {
	
	private int interval = 1; //how often, in seconds, should this action take place
	private int counter = 0;
	private Action timedAction = null; // the action to take place. 
	private String name = "DEFAULT";
	
	public ActionTimed(){
		counter = interval;
	}
	
	@Override
	public void menu(User u) throws MenuExitException {
		boolean done = false;
    	String menuString =  "Configure Timed Action:\r\n";
    		   menuString += "(1) Configure name\r\n";
    		   menuString += "(2) Config Action contained in this timer. \r\n";
    		   menuString += "(3) Delete existing action. \r\n";
    		   menuString += "(4) Configure action interval.\r\n";
    		   
        PromptForInteger p = new PromptForInteger(u, menuString, 16, 1);
        while (p.display() && !done){
        	switch (p.getResult()){
        		case 1:  configName(u); break;
        		case 2:  
        			if (this.getTimedAction() == null){
        				this.setTimedAction(Action.configAddAction(u));
        				
        			}
        			this.getTimedAction().setRange(EffectRange.WORLD);
        			this.getTimedAction().menu(u); 
        			break;
        		case 3: timedAction = null;
        		case 4: this.setInterval(configInterval(u));
        	}
        }	
	}
	
    public int configInterval(User u) throws MenuExitException{
    	int result = 0;
    	String menuString = "Set the interval of this timed action in seconds.\r\n";
    	       menuString += "The server will execute the action associated with this timer\r\n";
    	       menuString += "every (interval) seconds.\r\n";
    	        	
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		result = (i.getResult());
    	}
    	return result;
    } 
	
	public void configName(User u) throws MenuExitException {
		String menuString = "Enter a new Timer name.\r\n";
		menuString += "Current timer name:" + this.getName() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			this.setName(s.getResult());
			u.broadcast("New timer name:\r\n" + this.getName() + "\r\n");
		} else {
			u.broadcast("Timer name unchanged. \r\n");
		}
	}
	
	public int processTimer(){
		int result;
		
		counter = counter - 1;
		result = counter;
		if (counter == 0){
			counter = interval;
		}
		return result;
	}
	
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public Action getTimedAction() {
		return timedAction;
	}
	public void setTimedAction(Action timedAction) {
		this.timedAction = timedAction;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
