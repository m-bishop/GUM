package gum;

import gum.items.Item;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import gum.mobs.Mob;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class BattleHandler extends Thread {

	private Vector<Player> users = new Vector<Player>();
	private Vector<Player> mobs  = new Vector<Player>();
	
	public BattleHandler(Player p1, Player p2) {

		if (verifyFighters(p1, p2)) {
			if (p1 instanceof User) {
				users.add((User) p1);
				mobs.add((Mob) p2);
			} else {
				mobs.add((Mob) p1);
				users.add((User) p2);
			}
			p1.setFightingFlag(true);
			p2.setFightingFlag(true);

			p1.setBattleHandler(this);
			p2.setBattleHandler(this);
			
			p1.getCurrentRoom().chat(
					p1.getPlayerName() + " ATTACKS "
							+ p2.getPlayerName() + "!\r\n");
			this.start();
		}


	}

	public boolean verifyFighters(Player p1, Player p2) {
		boolean result = true;
		
		if (p1 instanceof User){
			if (((User) p1).inMenu){
				result = false;
			}
		}
		
		if (p2 instanceof User){
			if (((User) p2).inMenu){
				result = false;
			}
		}
		
		if (!result){
			// this would be false if a mob attacked a player while in a menu, which shouldn't be allowed.
		} else if ((p1 instanceof User) && (p2 instanceof User)) {
			p1.broadcast("You cannot attack another player!\r\n");
			result = false;
		}else if (p1 == p2){
			p1.broadcast("You cannot attack yourself!\r\n");
			result = false;
		}else if ((p1 instanceof Mob) && (p2 instanceof Mob)) {
			p1.broadcast("You cannot attack another mob!\r\n");
			result = false;
		}else if (p1.getFightingFlag()) {
			p1.broadcast("You are already in a fight!\r\n");
			result = false;
		}else if (p2.getFightingFlag()){
			p1.broadcast("Joining battle, please wait for the next round to begin...\r\n");
			p2.getBattleHandler().addToBattle(p1);
			result = false;
		}
		return result;
	}

	public synchronized void addToBattle(Player newEnemy){
		
		if (newEnemy instanceof Mob){
			mobs.add((Mob)newEnemy);
		} else {
			users.add((User) newEnemy);
		}
		newEnemy.setFightingFlag(true);
		newEnemy.getCurrentRoom().chat( newEnemy.getPlayerName()+ " joins the fight!\r\n");
		newEnemy.setBattleHandler(this);
	}
	
	public synchronized void removeFromBattle(Player p){
		p.setFightingFlag(false);
		p.setBattleHandler(null);
		
		if (p instanceof Mob){
			mobs.remove(p);
		} else {
			users.remove(p);
		}
		
	}
	
	private void checkForDeadPlayers(){
		
		//TODO It looked like there might be a bug where
		// after the users all exited a battle, the 
		// mob was trapped in a loop. 
		Vector<Player> players = new Vector<Player>();
		players.addAll(mobs);
		players.addAll(users);
		
		Iterator<Player> i = players.iterator();
        while (i.hasNext()){
            Player p = (Player) i.next();
            if (p.getSetting("hitpoints") <= 0){
            	p.die();
            }
        }
        if (mobs.isEmpty()){
        	this.endBattle(users);
        } else if (users.isEmpty()){
        	this.endBattle(mobs);
        }
	}
	
	
	//arrange players based on initiative, as well as applying drug and healing effects.
	private Vector<Player> initRound(){
		Vector<Player> fighters = new Vector<Player>();
		fighters.addAll(users);
		fighters.addAll(mobs);
		Integer dex,ref,initRoll;
		Iterator<Player> i = fighters.iterator();
		
		while (i.hasNext()){
		    Player p = (Player) i.next();
		    dex = p.getSetting("dex");
		    ref = p.getSetting("ref");
		    initRoll = p.roll(10);
		    p.setInitiative(dex+ref+initRoll);
		    p.process();
		}
		Collections.sort(fighters,new InitiativeComparator());
		return fighters;
	}
	
	private void resolveCombat(Vector<Player> fighters){
		Iterator<Player> i = fighters.iterator();
		while (i.hasNext()){
			Player p = i.next();
			p.doBattle();
		}
	}
	
	private synchronized void presentBattleMenus(){	
		Iterator<Player> i = users.iterator();
		while (i.hasNext()){
			User u = (User) i.next();
			u.getCurrentRoom().chat("Waiting on:"+u.getPlayerName()+"\r\n");
			u.broadcast("You have "+u.getSetting("hitpoints")+" hit points remaining!\r\n");
			displayAttackMenu(u);
		}
	}
	
	private void displayAttackMenu(User u){
		
		boolean done = false;
		
		String response = "";
		String attackMenu  = "Choose an action:\r\n";
			   attackMenu += "(1)Fight\r\n";
			   attackMenu += "(2)Use\r\n";
			   attackMenu += "(3)Equip\r\n";
			   attackMenu += "(4)Escape\r\n";
			
		while (!done && !u.isDead()){	   
			u.broadcast(attackMenu);
			response = u.getMenuResponse();
			if (response.equals("1")){
				done = this.displayFightMenu(u);
			}else if (response.equals("2")){
				done = this.displayUseItemMenu(u);
			}else if (response.equals("3")){
				done = this.displayEquipMenu(u);
			}else if (response.equals("4")){
				u.setEscape(true);
				done = true;
			}else {
				u.getCurrentRoom().chat(u.getPlayerName() + " chats:" + response + "\r\n");
			}
			
		}
		
	} 
	
	private synchronized boolean displayFightMenu(User u){
	
		Integer enemyIndex = 1;
		boolean done = false;
		boolean result = true;
		
		String response = "";
		String fightMenu  = "Which enemy will you target?\r\n";
		fightMenu += ("(0) Previous Menu\r\n");
		 
		Iterator<Player> i = mobs.iterator();
		while (i.hasNext()){
			Mob m = (Mob) i.next();
			fightMenu += ("("+enemyIndex.toString()+") "+ m.getPlayerName()+"\r\n");
			enemyIndex++;
		}
			
		while (!done && !u.isDead()){	
			u.broadcast(fightMenu);
			response = u.getMenuResponse();
			try {
				Integer index = Integer.valueOf(response);
				if (index == 0){
					result = false;
					done = true;
				}else if (mobs.elementAt(index-1) != null){
					u.setAttackTarget(mobs.elementAt(index-1));
					if (u.getRightHand() != null){
						//TODO make this also check to see if it's a weapon.
					    done = u.getRightHand().displayAttackMenu(u);
					}else {
						u.broadcast("You are not holding a weapon!");
						done = true;
						result = false;
					}
				}
			}
			catch (Exception e){ // chat any illegal commands. 
				u.getCurrentRoom().chat(u.getPlayerName() + " chats:" + response + "\r\n");
			}
		}		
		return result;
	}

	private synchronized boolean displayUseItemMenu(User u){
		
		Integer itemIndex = 1;
		boolean done = false;
		boolean result = true;
		
		String response = "";
		String itemMenu  = "Which item will you use?\r\n";
		itemMenu += ("(0) Previous Menu\r\n");
		 
		Iterator<Item> i = u.getItems().iterator();
		while (i.hasNext()){
			Item item = (Item) i.next();
			itemMenu += ("("+itemIndex.toString()+") "+item.getItemName()+"\r\n");
			itemIndex++;
		}
			
		while (!done && !u.isDead()){	
			u.broadcast(itemMenu);
			response = u.getMenuResponse();
			try {
				Integer index = Integer.valueOf(response);
				if (index == 0){
					result = false;
					done = true;
				}else if (u.getItems().elementAt(index-1) != null){
					u.setUseTarget(u.getItems().elementAt(index-1));
					done = displayUseTargetMenu(u);
				}
			}
			catch (Exception e){ // chat any illegal commands. 
				u.getCurrentRoom().chat(u.getPlayerName() + " chats:" + response + "\r\n");
			}
		}		
		return result;
	}	
	
	private synchronized boolean displayUseTargetMenu(User u){
		
		Integer enemyIndex = 1;
		boolean done = false;
		boolean result = true;
		Vector<Player> players = new Vector<Player>();
		players.addAll(mobs);
		players.addAll(users);
		
		String response = "";
		String fightMenu  = "Who will you use this on?\r\n";
		fightMenu += ("(0) Previous Menu\r\n");
		 
		Iterator<Player> i = players.iterator();
		while (i.hasNext()){
			Player p = (Player) i.next();
			fightMenu += ("("+enemyIndex.toString()+") "+ p.getPlayerName()+"\r\n");
			enemyIndex++;
		}
			
		while (!done && !u.isDead()){	
			u.broadcast(fightMenu);
			response = u.getMenuResponse();
			try {
				Integer index = Integer.valueOf(response);
				if (index == 0){
					result = false;
					done = true;
				}else if (players.elementAt(index-1) != null){
					u.setAttackTarget(players.elementAt(index-1));
					done = true;
				}
			}
			catch (Exception e){ // chat any illegal commands. 
				u.getCurrentRoom().chat(u.getPlayerName() + " chats:" + response + "\r\n");
			}
		}		
		return result;
	}	
	
	private synchronized boolean displayEquipMenu(User u){
		
		Integer itemIndex = 1;
		boolean done = false;
		boolean result = true;
		
		String response = "";
		String itemMenu  = "What item will you equip?\r\n";
		itemMenu += ("(0) Previous Menu\r\n");
		 
		Iterator<Item> i = u.getItems().iterator();
		while (i.hasNext()){
			Item item = (Item) i.next();
			itemMenu += ("("+itemIndex.toString()+") "+ item.getItemName()+"\r\n");
			itemIndex++;
		}
			
		while (!done && !u.isDead()){	
			u.broadcast(itemMenu);
			response = u.getMenuResponse();
			try {
				Integer index = Integer.valueOf(response);
				if (index == 0){
					result = false;
					done = true;
				}else if (u.getItems().elementAt(index-1) != null){
					u.setEquipTarget(u.getItems().elementAt(index-1));
					done = true;
				}
			}
			catch (Exception e){ // chat any illegal commands. 
				u.getCurrentRoom().chat(u.getPlayerName() + " chats:" + response + "\r\n");
			}
		}		
		return result;
	}	
	
	public void run() {		
		while (!users.isEmpty() && !mobs.isEmpty()){
			try{
				presentBattleMenus();
				resolveCombat(this.initRound());
				checkForDeadPlayers();
			} 
			//catch (NullPointerException e){
			//	System.err.println("Null pointer Exception in Battle.(Not an error if a User left mid-battle)\r\n"+e.toString());
			//}
			catch (Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	public synchronized void endBattle(Vector<Player> winners) {
		Iterator<Player> i = winners.iterator();
		while (i.hasNext()){
			Player p = i.next();
			p.setFightingFlag(false);
			p.setBattleHandler(null);
			p.broadcast("You won!\r\n");
		}
		winners.clear();
	}

	public boolean allStunned(Vector<Player> players){
		boolean result = true;
		boolean done = false;
		
		Iterator<Player> i = players.iterator();
		
		while (i.hasNext() && !done){
			Player p = i.next();
			if (p.getSetting("stun") == 0){
				result = false;
				done = true;
			}
		}
		return result;
	}
	
	public void escape(Player p) {
		
		try{
			Player m = mobs.elementAt(0);
			if ((allStunned(mobs)) || ((p.roll(p.getSetting("dex")) > m.roll(m.getSetting("ref"))))){
				p.broadcast("You make a run for an exit!\r\n");
				this.removeFromBattle(p);
				p.takeRandomExit();
				checkForDeadPlayers(); //if this was the only fighter, the battle is over. 
			} else {
				p.broadcast("You fail to escape the battle!\r\n");
			}
			p.setEscape(false);
		}
		catch (Exception e){ 
			//this should only fail if the mobs are empty. Best to try to end the battle gracefully. 
			this.checkForDeadPlayers();
		}
	}

	public Vector<Player> getUsers() {
		return users;
	}

	public Vector<Player> getMobs() {
		return mobs;
	}
}
