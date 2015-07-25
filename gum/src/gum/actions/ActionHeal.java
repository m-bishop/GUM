package gum.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionHeal extends Action {
	
	private int healBase;
	private int healRoll;
	private HashMap<Integer,String> healMessage;
	
	public ActionHeal(){
	}
	
	public void init(){
		this.setActionName("Heal");
		healBase = 0;
		healRoll = 0;
		healMessage = new HashMap<Integer,String>();
		this.setRange(EffectRange.TARGET);
	}

	@Override
	public boolean doAction(ActionHeader actionHeader) {
		Player player = actionHeader.getPlayer();
		Player target = actionHeader.getTargetPlayer();
		ItemBase item = actionHeader.getItem();
		
		//return (heal(player,target,item));
		boolean result = true;
		int maxhp = target.getSetting("maxhp");
		int healRoll = player.roll(this.getHealRoll());
		float heal = healRoll+this.getHealBase();
		int totalHeal = (int)(target.getSetting("hitpoints")+heal);

    	//String healString = (player.getPlayerName()+" performed "+this.getActionName()+" on "+target.getPlayerName()+"!\r\n")
        //float heal = player.roll(healRoll)+this.getHealBase();
        if (totalHeal > maxhp){
        	totalHeal = maxhp;
        }
        	result = true;
            //removed float from calculation.
        	int healPercent = (int)((heal/maxhp)*100);
        	player.getCurrentRoom().chat(this.getClosestMessage(healPercent,player,target,item));
            target.setSetting("hitpoints", totalHeal);
            //player.getCurrentRoom().chat("damage:"+(int)damage);
			
		return result;		
	}

	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure heal Action:\r\n";
		menuString += "(01) Configure heal name \r\n";
		menuString += "(02) Configure heal base (BASE+(1->roll) = damage) \r\n";
		menuString += "(03) Configure heal roll (base+(1->ROLL) = damage)\r\n";
		menuString += "(04) Configure prereq setting\r\n";
		menuString += "(05) Configure range \r\n";
		menuString += "(06) Configure success action \r\n";
		menuString += "(07) configure heal message map \r\n";
		menuString += "(08) Display structure \r\n";
		menuString += "(09) save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 9, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2:
				this.configHealBase(u);
				break;				
			case 3:
				this.configHealRoll(u);
				break;
			case 4:
				this.configActionPrereqSetting(u);
				break;
			case 5:
				this.configActionRange(u);
				break;
			case 6:
				this.configSuccessAction(u);
				break;
			case 7: 
				this.configHealMessages(u);
				break;
			case 8:
				u.broadcast(this.getStructure());
				break;
			case 9: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Heal Configuration Menu.\r\n\r\n");	

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
	
	public boolean configHealMessages(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure heal messages:\r\n";
		menuString += "(1) List heal messages \r\n";
		menuString += "(2) Add an heal message \r\n";
		menuString += "(3) Remove an heal message \r\n";

		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configViewHealMessage(u);
				break;
			case 2: 
				this.configAddHealMessage(u);
				break;
			case 3: 
				this.configRemoveHealMessage(u);
				break;
			}
		}
		return done;
	}	
	
	public void configViewHealMessage(User u){
		String display = "Current heal Messages:\r\n\r\n";
		ArrayList<Integer> keys = new ArrayList<Integer>(healMessage.keySet());
		Collections.sort(keys);
		
		for (Integer i : keys){
			display += "("+i+") "+healMessage.get(i)+"\r\n";
		}
		
		u.broadcast(display);
	}
	
	public void configRemoveHealMessage(User u) throws MenuExitException{	
		
		String menuString =  "Enter a message index from the list below to remove it.\r\n";
		ArrayList<Integer> keys = new ArrayList<Integer>(healMessage.keySet());
		Collections.sort(keys);
		
		for (Integer i : keys){
			menuString += "("+i+") "+healMessage.get(i)+"\r\n";
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
	       if (p.display()){
	    	   if (healMessage.remove(p.getResult()) != null){
	    		   u.broadcast("Message removed.\r\n");
	    	   }else {
	    		   u.broadcast("Message not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Messages unchanged.\r\n");
	       }
	}

	public void configAddHealMessage(User u) throws MenuExitException{
		String menuString =  "Effect messages work by matching the percentage of your target's hp you've taken to a message.\r\n";
		       menuString += "For example, if you take 50% of your target's hp with an heal, the system will start at \r\n";
		       menuString += "index '50' and look up through the list until it finds a message. \r\n";
		       menuString += "If you enter an index already being used, this will replace it. \r\n";
		       menuString += "If you enter '[player]', '[target]' or '[item]' those tokens will \r\n";
		       menuString += "be replaced by the player, target and item names. \r\n";
		       
		       menuString += "Enter a new index: \r\n";
		       
		PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
		if (p.display()){
			healMessage.put(p.getResult(), this.configDescription(u));
		} else {
			u.broadcast("Messages unchanged.\r\n");
		}
	}
	
	public String configDescription(User u) throws MenuExitException {
		String menuString = "Enter a new description.\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Description:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the description, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		return result;
	}	
	
	public void configHealRoll(User u) throws MenuExitException {
		
		String menuString = "What will the heal Roll for this Action be, in the equation (ex. (Heal Roll)+Damage Base)?\r\n\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString);
		if (p.display()) {
			this.setHealRoll(p.getResult());
			u.broadcast("New Heal Roll:" + this.healRoll + "\r\n");
		} else {
			u.broadcast("Heal Roll unchanged!\r\n");
		}
	}    
    
	public void configHealBase(User u) throws MenuExitException {
		
		String menuString = "What will the Heal Base for this Action be, in the equation (ex. (Heal Roll)+Damage Base)?\r\n\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString);
		if (p.display()) {
			this.setHealBase(p.getResult());
			u.broadcast("New Heal Base:" + this.healBase + "\r\n");
		} else {
			u.broadcast("Heal Base unchanged!\r\n");
		}
	}	
	
	public String getClosestMessage(int percentEffect, Player player, Player target, ItemBase item){		
		if (percentEffect > 100){
			percentEffect = 100;
		}
		String result = "";
		boolean done = false;
		ArrayList<Integer> keys = new ArrayList<Integer>(healMessage.keySet());
		Collections.sort(keys);
		
		// set return to first message.
		// so that if there's only one,
		// it won't come back blank.
		Iterator<Integer> i = keys.iterator();
		if (i.hasNext()){ 
			result = healMessage.get(i.next());
		}
		// now look for the first entry <= to percentEffect.
		while (i.hasNext() && !done){
			int idx = i.next();
			System.out.println(idx);
			if (idx >= percentEffect){
				result = healMessage.get(idx);
				done = true;
			}
		}
		//finally, replace identifiers.
    	result = result.replace("[player]", player.getPlayerName());
    	result = result.replace("[target]", target.getPlayerName());
    	result = result.replace("[item]", item.getItemName());
		return result;
	}
	
	public String toString(){
		String result;
		
		result = "\r\n"
				+ "Heal Base: "+this.getHealBase() + "\r\n"
				+ "Heal Roll: "+this.getHealRoll() + "\r\n"
				+ "Heal Messages: "+this.getHealMessage();

		return result;
	}

	public int getHealBase() {
		return healBase;
	}

	public void setHealBase(int healBase) {
		this.healBase = healBase;
	}

	public int getHealRoll() {
		return healRoll;
	}

	public void setHealRoll(int healRoll) {
		this.healRoll = healRoll;
	}

	public HashMap<Integer, String> getHealMessage() {
		return healMessage;
	}

	public void setHealMessage(HashMap<Integer, String> healMessage) {
		this.healMessage = healMessage;
	}

	
	
}
