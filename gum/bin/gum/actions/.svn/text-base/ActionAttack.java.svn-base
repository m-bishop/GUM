package gum.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import gum.Player;
import gum.User;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionAttack extends Action {
	
	private String attackSetting = "dex";
	private String defendSetting = "ref";
	private int attackBase = 0;
	private int attackRoll = 0;
	private HashMap<Integer,String> attackMessage = new HashMap<Integer,String>();

	public ActionAttack(){
		this.setActionName("Attack");
	}

	@Override
	public boolean doAction(ActionHeader actionHeader){
		Player player = actionHeader.getPlayer();
		Player target = actionHeader.getTargetPlayer();
		ItemBase item = actionHeader.getItem();
		//return (attack(player,target,item));
		boolean result = false;
		if (player.getFightingFlag()){
    	//String attackString = (player.getPlayerName()+" performed "+this.getActionName()+" on "+target.getPlayerName()+"!\r\n");
        int attack = player.getSetting(this.getAttackSetting());
        int defend = target.getSetting(this.getDefendSetting());
        int damageRoll = this.getAttackRoll();
                
        int attack_roll = player.roll(attack); // random number from 1 attack
        int defend_roll = target.roll(defend);
        float damage = player.roll(damageRoll)+this.getAttackBase();
        
        if(attack_roll > defend_roll){
        	result = true;
        	float currentHP = target.getSetting("hitpoints");
        	int damagePercent = (int)((damage/currentHP)*100);
        	player.getCurrentRoom().chat(this.getClosestMessage(damagePercent,player,target,item));
            target.setSetting("hitpoints",target.getSetting("hitpoints") - ((int)damage));
            //player.getCurrentRoom().chat("damage:"+(int)damage);
        } else {
            player.getCurrentRoom().chat(player.getPlayerName()+" missed! \r\n");     
        }			
		
		}else {
			player.broadcast("You are not fighting anyone!");
		}
		return result;		
	}
	
	public void menu(User u) throws MenuExitException{
		String menuString = "Configure Attack Action:\r\n";
		menuString += "(01) Configure attack name \r\n";
		menuString += "(02) Configure attack base (BASE+(1->roll) = damage) \r\n";
		menuString += "(03) Configure attack roll (base+(1->ROLL) = damage)\r\n";
		menuString += "(04) Configure attack skill setting \r\n";
		menuString += "(05) Configure attack defend setting \r\n";
		menuString += "(06) Configure attack prereq setting\r\n";
		menuString += "(07) Configure attack range \r\n";
		menuString += "(08) Configure success action \r\n";
		menuString += "(09) Configure failure action \r\n";
		menuString += "(10) Configure attack message map \r\n";
		menuString += "(11) Display structure \r\n";
		menuString += "(12) save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 12, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2:
				this.configAttackDamageBase(u);
				break;				
			case 3:
				this.configAttackDamageRoll(u);
				break;
			case 4:
				this.setAttackSetting(this.getSettingName(u));
				break;
			case 5:
				this.setDefendSetting(this.getSettingName(u));
				break;
			case 6:
				this.configActionPrereqSetting(u);
				break;
			case 7:
				this.configActionRange(u);
				break;
			case 8:
				this.configSuccessAction(u);
				break;
			case 9: 
				this.configFailureAction(u);
				break;
			case 10: 
				this.configAttackMessages(u);
				break;
			case 11: 
				u.broadcast(this.getSettingName(u));
				break;
			case 12:
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Attack Configuration Menu.\r\n\r\n");	
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
	
	public boolean configAttackMessages(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure attack messages:\r\n";
		menuString += "(1) List attack messages \r\n";
		menuString += "(2) Add an attack message \r\n";
		menuString += "(3) Remove an attack message \r\n";

		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configViewAttackMessage(u);
				break;
			case 2: 
				this.configAddAttackMessage(u);
				break;
			case 3: 
				this.configRemoveAttackMessage(u);
				break;
			}
		}
		return done;
	}	
	
	public void configViewAttackMessage(User u){
		String display = "Current Attack Messages:\r\n\r\n";
		ArrayList<Integer> keys = new ArrayList<Integer>(attackMessage.keySet());
		Collections.sort(keys);
		
		for (Integer i : keys){
			display += "("+i+") "+attackMessage.get(i)+"\r\n";
		}
		
		u.broadcast(display);
	}
	
	public void configRemoveAttackMessage(User u) throws MenuExitException{	
		
		String menuString =  "Enter a message index from the list below to remove it.\r\n";
		ArrayList<Integer> keys = new ArrayList<Integer>(attackMessage.keySet());
		Collections.sort(keys);
		
		for (Integer i : keys){
			menuString += "("+i+") "+attackMessage.get(i)+"\r\n";
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
	       if (p.display()){
	    	   if (attackMessage.remove(p.getResult()) != null){
	    		   u.broadcast("Message removed.\r\n");
	    	   }else {
	    		   u.broadcast("Message not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Messages unchanged.\r\n");
	       }
	}

	public void configAddAttackMessage(User u) throws MenuExitException{
		String menuString =  "Effect messages work by matching the percentage of your target's hp you've taken to a message.\r\n";
		       menuString += "For example, if you take 50% of your target's hp with an attack, the system will start at \r\n";
		       menuString += "index '50' and look up through the list until it finds a message. \r\n";
		       menuString += "If you enter an index already being used, this will replace it. \r\n";
		       menuString += "If you enter '[player]', '[target]' or '[item]' those tokens will \r\n";
		       menuString += "be replaced by the player, target and item names. \r\n";
		       
		       menuString += "Enter a new index: \r\n";
		       
		PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
		if (p.display()){
			attackMessage.put(p.getResult(), this.configDescription(u));
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
	
	public void configAttackDamageRoll(User u) throws MenuExitException {
		
		String menuString = "What will the damage Roll for this Action be, in the equation (ex. (1->Damage Roll)+Damage Base)?\r\n\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString);
		if (p.display()) {
			this.setAttackRoll(p.getResult());
			u.broadcast("New Damage Roll:" + this.attackRoll + "\r\n");
		} else {
			u.broadcast("Damage Roll unchanged!\r\n");
		}
	}    
    
	public void configAttackDamageBase(User u) throws MenuExitException {
		
		String menuString = "What will the damage Base for this Action be, in the equation (ex. (1->Damage Roll)+Damage Base)?\r\n\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString);
		if (p.display()) {
			this.setAttackBase(p.getResult());
			u.broadcast("New Damage Base:" + this.attackBase + "\r\n");
		} else {
			u.broadcast("Damage Base unchanged!\r\n");
		}
	}	
	
	public String getClosestMessage(int percentEffect, Player player, Player target, ItemBase item){		
		if (percentEffect > 100){
			percentEffect = 100;
		}
		String result = "";
		boolean done = false;
		ArrayList<Integer> keys = new ArrayList<Integer>(attackMessage.keySet());
		Collections.sort(keys);
		
		// set return to first message.
		// so that if there's only one,
		// it won't come back blank.
		Iterator<Integer> i = keys.iterator();
		if (i.hasNext()){ 
			result = attackMessage.get(i.next());
		}
		// now look for the first entry <= to percentEffect.
		while (i.hasNext() && !done){
			int idx = i.next();
			//System.out.println("index:"+idx+"effect:"+percentEffect);
			if (idx > percentEffect){
				done = true;
			}else {
				result = attackMessage.get(idx);
			}
		}
		//finally, replace identifiers.
    	result = result.replace("[player]", player.getPlayerName());
    	result = result.replace("[target]", target.getPlayerName());
    	result = result.replace("[item]", item.getItemName());
		return result;
	}
	
	public String getAttackSetting() {
		return attackSetting;
	}

	public void setAttackSetting(String attackSetting) {
		this.attackSetting = attackSetting;
	}

	public String getDefendSetting() {
		return defendSetting;
	}

	public void setDefendSetting(String defendSetting) {
		this.defendSetting = defendSetting;
	}

	public int getAttackBase() {
		return attackBase;
	}

	public void setAttackBase(int attackBase) {
		this.attackBase = attackBase;
	}

	public int getAttackRoll() {
		return attackRoll;
	}

	public void setAttackRoll(int attackRoll) {
		this.attackRoll = attackRoll;
	}


	public HashMap<Integer, String> getAttackMessage() {
		return attackMessage;
	}


	public void setAttackMessage(HashMap<Integer, String> attackMessage) {
		this.attackMessage = attackMessage;
	}


	
}
