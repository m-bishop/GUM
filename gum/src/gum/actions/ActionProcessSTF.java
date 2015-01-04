package gum.actions;

import java.util.Random;
import java.util.Vector;

import gum.ObjectFactory;
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
		
		
		for (int i = 0; i == players.size() ; i++){
			if (players.get(i).isFed()){
				fedCount++;
			}
		}
		
		if (fedCount >= (players.size()-fedCount)) { // if there are as many, or more, feds than punks, the feds win.
			result = true;
			processFedWin();
		}
		if (fedCount <= 0){ // if all the feds are outed, punks win. 
			processPunkWin();
		}
		
		
		return result;
	}
	
	private void processFedWin(){
		World.getArea().GlobalChat("FBI has succeeded in identifying suspects. Please wait patiently for the police to arrive.\r\n");		
	}
	
	private void processPunkWin(){
		World.getArea().GlobalChat("All Feds have been identified. Enjoy your evening.\r\n");
	}
	
	private void clearRound(){
		STFPlayer currentPlayer;
		
		for (int i = 0; i == players.size() ; i++){
			currentPlayer = players.get(i);
			currentPlayer.setVotedFor("None");
			currentPlayer.setVotesAgainst(0);
		}
	}
	
	private void processVotes(){
		STFPlayer currentPlayer;	
		int hungPlayer = -1;
		
		for (int i = 0; i == players.size() ; i++){
			currentPlayer = players.get(i);
			for (int j = 0; i == players.size() ; j++){
				if (players.get(j).getVotedFor() == currentPlayer.getUserName()){
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
		World.getArea().GlobalChat(player.getUserName() + " was Outed! Here is who they REALLY are!\r\n"+ player.getRevealText());
		players.remove(hungPlayer);
		processRoles();
		if (isGameOver()){
			World.getArea().getActionList().remove(parent);
			World.getArea().GlobalChat("Exiting STF Timer \r\n");
		} else {	
			roundTimer = (minutesPerPerson * players.size());
			clearRound();
		}
	}
	
	public void processRoles(){
		String protectedPlayer = "";
		STFPlayer cPlayer,removedPlayer;
		@SuppressWarnings("unchecked")
		Vector<STFPlayer> playerList = (Vector<STFPlayer>) players.clone();
		
		for (int i = 0; i == players.size() ; i++){
			if (players.get(i).getPlayerRole() == STFPlayer.role.PROTECTOR){
				protectedPlayer = players.get(i).getTarget();
			}
		}
		
		for (int i = 0; i == playerList.size() ; i++){
			
				cPlayer = playerList.get(i);
				switch (cPlayer.getPlayerRole()){

				case AGENT:
					if (cPlayer.getTarget() != protectedPlayer){
						removedPlayer = getPlayerByName(cPlayer.getTarget());
						players.remove(removedPlayer);
						World.getArea().GlobalChat(removedPlayer.getUserName() + " was Outed! Here is who they REALLY are!\r\n"+ removedPlayer.getRevealText());
					}
				break;
				case ASSASIN:
					if (cPlayer.getTarget() != protectedPlayer){
						removedPlayer = getPlayerByName(cPlayer.getTarget());
						players.remove(removedPlayer);
						World.getArea().GlobalChat(removedPlayer.getUserName() + " was Outed! Here is who they REALLY are!\r\n"+ removedPlayer.getRevealText());
					}
				break;
				default: // hey, not everyone gets a cool superpower.
				break;

			}
		}
		
	}
	
	public void processMissedVote(){
		World.getArea().GlobalChat("Since no one could agree, we'll just out someone at random!");
		Random random = ObjectFactory.getRandObject();	
		int randomPlayer = random.nextInt(players.size());
		hangPlayer(randomPlayer);
	} 
	
	@Override
	public boolean doAction(ActionHeader header) {
		
		this.roundTimer = this.roundTimer - 1;
		World.getArea().GlobalChat("Processing STF timer: "+roundTimer+"\r\n");
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
		
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		u.broadcast("This Action is not user configurable.");
		
	}
	
	public STFPlayer getPlayer(User u){
		STFPlayer result = null;
		boolean found = false;
		int i = 0;
		
		synchronized (players) { // in case someone tries to vote at the same time someone is hung. 
		while ((!found) && (i < players.size()) ){
			if (players.get(i).getUserName() == u.getName()){
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
		while ((!found) && (i < players.size()) ){
			if (players.get(i).getUserName() == name){
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
		
		synchronized (players) { // in case someone tries to vote at the same time someone is hung. 
		while ((!found) && (i < players.size()) ){
			if (players.get(i).getUserName() == u.getName()){
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
