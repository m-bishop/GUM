package gum;

public class STFPlayer {
	
	private String userName; 
	private String votedFor; // who is this player voting for?
	private String target; // who is this player's role targeting in night phase?
	private int votesAgainst;// how many votes against this player?
	private boolean fed; // true if this player is a fed.
	private String revealText; // player description revealed when the player is lynched
	private role playerRole; // This players role. 
	
	public static enum role {PROTECTOR,AGENT,ASSASIN,PUNK}; // player role.
	
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getVotedFor() {
		return votedFor;
	}
	public void setVotedFor(String votedFor) {
		this.votedFor = votedFor;
	}
	public int getVotesAgainst() {
		return votesAgainst;
	}
	public void setVotesAgainst(int votesAgainst) {
		this.votesAgainst = votesAgainst;
	}
	public boolean isFed() {
		return fed;
	}
	public void setFed(boolean fed) {
		this.fed = fed;
	}
	public String getRevealText() {
		return revealText;
	}
	public void setRevealText(String revealText) {
		this.revealText = revealText;
	}
	public role getPlayerRole() {
		return playerRole;
	}
	public void setPlayerRole(role playerRole) {
		this.playerRole = playerRole;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

}
