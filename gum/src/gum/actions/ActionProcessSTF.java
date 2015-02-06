package gum.actions;


import java.util.Vector;


import gum.STFPlayer;
import gum.User;
import gum.World;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.menus.MenuExitException;


public class ActionProcessSTF extends Action {
	
	private int roundTimer;
	private ActionTimed parent;
	private int minutesPerPerson;
	private  Vector<STFPlayer> players = new Vector<STFPlayer>();

	public ActionProcessSTF(){
		this.setActionName("ActionProcessSTF");
		this.setRange(EffectRange.TIMED);
	}
	
	public void init(){
		this.setActionName("ActionProcessSTF");
		this.setRange(EffectRange.TIMED);
	}

	private boolean isGameOver(){
		boolean result = false;
		int fedCount = 0;
		
		if (players.isEmpty()){
			processTPK();
			result = true;
		}else {
		
		for (int i = 0; i < players.size() ; i++){
			if (players.get(i).isFed()){
				fedCount++;
			}
		}
		
		if (fedCount >= (players.size()-fedCount)) { // if there are as many, or more, feds than punks, the feds win.
			result = true;
			processFedWin();
		} 
		
		else if (fedCount <= 0){ // if all the feds are outed, punks win.
			result = true;
			processPunkWin();
		} 
		}
		
		
		return result;
	}
	
	private void processTPK(){
		World.getArea().GlobalChat("Everyone has been outed ... SETEC ASTRONOMY!\r\n");	
	}
	
	private void processFedWin(){
		World.getArea().GlobalChat("FBI has succeeded in identifying suspects. Please wait patiently for the police to arrive.\r\n");		
	}
	
	private void processPunkWin(){
		World.getArea().GlobalChat("All Feds have been identified. Enjoy your evening.\r\n");
	}
	
	private void clearRound(){
		STFPlayer currentPlayer;
		
		processRoles();
		if (isGameOver()){
			World.getArea().getActionList().remove(parent);
			World.getArea().GlobalChat("Spot the Fed has ended!  \r\n");
		} else {	
			roundTimer = (minutesPerPerson * players.size());
			World.getArea().GlobalChat("Starting new round. This round will last "+roundTimer+" minutes.");
		}
		for (int i = 0; i < players.size() ; i++){
			currentPlayer = players.get(i);
			currentPlayer.setVotedFor("None");
			currentPlayer.setVotesAgainst(0);
		}
		
	}
	
	private void processVotes(){
		STFPlayer currentPlayer;	
		int hungPlayer = -1;
		
		for (int i = 0; i < players.size() ; i++){
			currentPlayer = players.get(i);
			for (int j = 0; j < players.size() ; j++){
				if (players.get(j).getVotedFor().equals(currentPlayer.getUserName())){
					currentPlayer.setVotesAgainst(currentPlayer.getVotesAgainst()+1);
				}
			}
			if (currentPlayer.getVotesAgainst() > (players.size() - currentPlayer.getVotesAgainst())){// if the majority is voting to hang 
				hungPlayer = i;
			}
		}
		if (hungPlayer > -1){ 
			hangPlayer(hungPlayer);	
		}
	}
	
	public void hangPlayer(int hungPlayer){
		STFPlayer player = players.get(hungPlayer);
		
		String role = "Agent";
		if (player.getPlayerRole() != STFPlayer.role.AGENT){
			role = "Punk";
		}
		if (player != null){
			players.remove(player);
			World.getArea().GlobalChat(player.getUserName() + " has been voted out! Turns out they're a "+ role + ".\r\n");
		    this.notifyAll(player.getUserName() + " has been voted out! Turns out they're a "+ role + ".\r\n");
		}
		clearRound();
	}
	
	public void processRoles(){
		String protectedPlayer = "";
		STFPlayer cPlayer,removedPlayer;
		@SuppressWarnings("unchecked")
		Vector<STFPlayer> playerList = (Vector<STFPlayer>) players.clone();
		
		for (int i = 0; i < players.size() ; i++){
			if (players.get(i).getPlayerRole() == STFPlayer.role.PROTECTOR){
				protectedPlayer = players.get(i).getTarget();
			}
		}
		
		for (int i = 0; i < playerList.size() ; i++){
			
				cPlayer = playerList.get(i);
				switch (cPlayer.getPlayerRole()){

				case AGENT:
					if (!cPlayer.getTarget().equals(protectedPlayer)){
						removedPlayer = getPlayerByName(cPlayer.getTarget());
						if (removedPlayer != null){
						String role = "Agent";
						if (removedPlayer.getPlayerRole() != STFPlayer.role.AGENT){
							role = "Punk";
						}
						if (removedPlayer != null){
							players.remove(removedPlayer);
							World.getArea().GlobalChat(removedPlayer.getUserName() + " has been identified as a suspect by Agents! Turns out they're a "+ role + ".\r\n");
						    this.notifyAll(removedPlayer.getUserName() + " has been identified as a suspect by Agents! Turns out they're a "+ role + ".\r\n");
						}
						}
					}
				break;
				case ASSASIN:
					if (!cPlayer.getTarget().equals(protectedPlayer)){
						removedPlayer = getPlayerByName(cPlayer.getTarget());
						if (removedPlayer != null){
						String role = "Agent";
						if (removedPlayer.getPlayerRole() != STFPlayer.role.AGENT){
							role = "Punk";
						}
						if (removedPlayer != null){
							players.remove(removedPlayer);
							World.getArea().GlobalChat(removedPlayer.getUserName() + " was outed by a hacker! Turns out they're a "+ role + ".\r\n");
							this.notifyAll(removedPlayer.getUserName() + " was outed by a hacker! Turns out they're a "+ role + ".\r\n");
						}
						}
					}
				break;
				case INVESTIGATOR:
					if (!cPlayer.getTarget().equals(protectedPlayer)){
						removedPlayer = getPlayerByName(cPlayer.getTarget());
						if (removedPlayer != null){
						String role = "Agent";
						if (removedPlayer.getPlayerRole() != STFPlayer.role.AGENT){
							role = "Punk";
						}
						if (removedPlayer != null){
							cPlayer.setMessages(cPlayer.getMessages()+"Your investigation revealed that "+removedPlayer.getUserName()+" is a "+role+".\r\n");
						}
						}
					}
				break;
				default: // hey, not everyone gets a cool superpower.
				break;

			}
		}
		
	}
	
	public void processMissedVote(){
		World.getArea().GlobalChat("Since no one could agree, we'll just move on...");
		clearRound();
		// I'm on the fence about hanging someone at random.
		// Random random = ObjectFactory.getRandObject();	
		// int randomPlayer = random.nextInt(players.size());
		// hangPlayer(randomPlayer);
	} 
	
	@Override
	public boolean doAction(ActionHeader header) {
		try{
		this.roundTimer = this.roundTimer - 1;
		World.getArea().GlobalChat("Minutes left in this Spot The Fed Round: "+roundTimer+"\r\n");
		processVotes();
		
		switch (roundTimer){
			case 5:
				World.getArea().GlobalChat("Only five minutes left to vote!");
			break;
			case 2:
				World.getArea().GlobalChat("Only two minutes left to vote!");
			break;
			case 1:
				World.getArea().GlobalChat("Only one minute left to vote!");
			break;
			case 0:
				processMissedVote();
			break;
		}
		}
		catch (Exception e){
			World.getArea().GlobalChat("Spot the Fed exited abnoramally.");
			World.getArea().getActionList().remove(parent);
			e.printStackTrace();
			
		}
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		u.broadcast("This Action is not user configurable.");
		
	}
	
	public void notifyAll(String note){
		for (int i = 0; i < players.size() ; i++){
			STFPlayer player = players.get(i);
			String messages = player.getMessages();
			player.setMessages(messages+note);
		}
	}
	
	public STFPlayer getPlayer(User u){
		STFPlayer result = null;
		boolean found = false;
		int i = 0;
		
		synchronized (players) { // in case someone tries to vote at the same time someone is hung. 
									// TODO use iterators. Sheesh.
		while ((!found) && (i < players.size()) ){
			if (players.get(i).getUserName().equals(u.getPlayerName())){
				result = players.get(i);
				found = true;
			} else {
				i++;
			}
		} 
		}
		return result;
	}
	
	public STFPlayer getPlayerByName(String name){
		STFPlayer result = null;
		boolean found = false;
		int i = 0;
		
		synchronized (players) { // in case someone tries to vote at the same time someone is hung.
									// TODO use iterators. Sheesh.
		while ((!found) && (i < players.size()) ){
			if (players.get(i).getUserName().equals(name)){
				result = players.get(i);
				found = true;
			} else {
				i++;
			}
		} 
		}
		return result;
	}
	
	public void castVote(User u, String vote){
		boolean found = false;
		int i = 0;
		if (this.getPlayer(u) != null){ // don't let players vote if they've been outed.
			synchronized (players) { // in case someone tries to vote at the same time someone is hung. 
				// TODO use iterators. Sheesh.
				while ((!found) && (i < players.size()) ){
					if (players.get(i).getUserName().equals(u.getPlayerName())){
						players.get(i).setVotedFor(vote);
						found = true;
					} else {
						i++;
					}
				} 
				if (!found){ // concurrency is a bitch
					u.broadcast("User not found.");
				}
			}
		}
	}

	public int getRoundTimer() {
		return roundTimer;
	}

	public void setRoundTimer(int roundTimer) {
		this.roundTimer = roundTimer;
	}

	public ActionTimed getParent() {
		return parent;
	}

	public void setParent(ActionTimed parent) {
		this.parent = parent;
	}

	public Vector<STFPlayer> getPlayers() {
		return players;
	}

	public void setPlayers(Vector<STFPlayer> players) {
		this.players = players;
	}

	public int getMinutesPerPerson() {
		return minutesPerPerson;
	}

	public void setMinutesPerPerson(int minutesPerPerson) {
		this.minutesPerPerson = minutesPerPerson;
	}
	
	

}
