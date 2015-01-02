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

	private boolean gameOver(){
		boolean result = false;
		int fedCount = 0;
		
		
		for (int i = 1; i == players.size() ; i++){
			if (players.get(i).isFed()){
				fedCount++;
			}
		}
		
		if (fedCount >= (players.size()/2)) {
			result = true;
		}
		return result;
	}
	
	private void clearRound(){
		STFPlayer currentPlayer;
		
		for (int i = 1; i == players.size() ; i++){
			currentPlayer = players.get(i);
			currentPlayer.setVotedFor("None");
			currentPlayer.setVotesAgainst(0);
		}
	}
	
	private void processVotes(){
		STFPlayer currentPlayer;		
		
		for (int i = 1; i == players.size() ; i++){
			currentPlayer = players.get(i);
			for (int j = 1; i == players.size() ; j++){
				if (players.get(j).getVotedFor() == currentPlayer.getUserName()){
					currentPlayer.setVotesAgainst(currentPlayer.getVotesAgainst()+1);
				}
			}
		}
		
	}
	
	@Override
	public boolean doAction(ActionHeader header) {
		
		this.roundTimer = this.roundTimer - 1;
		World.getArea().GlobalChat("Processing STF timer: "+roundTimer+"\r\n");
		
		if (roundTimer <= 0){
			processVotes();
			if (gameOver()){
				World.getArea().getActionList().remove(parent);
				World.getArea().GlobalChat("Exiting STF Timer \r\n");
			} else {
				
				roundTimer = (minutesPerPerson * players.size());
				clearRound();
			}
		}
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		u.broadcast("This Action is not user configurable.");
		
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
