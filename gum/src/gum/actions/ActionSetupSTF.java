package gum.actions;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import gum.Player;
import gum.STFPlayer;
import gum.User;
import gum.World;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionSetupSTF extends Action {

	private boolean actionPerformed; // are we currently performing this action?
	private int minutesPerPerson;
	private  Vector<STFPlayer> newSTFPlayers = new Vector<STFPlayer>();

	public ActionSetupSTF() {
	}

	public void init() {
		this.setActionName("ActionSetupSTF");
		this.setRange(EffectRange.USER);
	}
	
	@Override
	public void menu(User u) throws MenuExitException{
		if (!actionPerformed){//if they're not performing the action, show config menu.
			this.configMenu(u); 
		}else {
			ActionProcessSTF game = this.getExistingGame();
			if (game == null){
				this.performSetupMenu(u);
			}else {
				if (game.getPlayerByName(u.getPlayerName()) != null){
					this.performGameMenu(u,game);
				} else {
					u.broadcast("There is a game in progress. Please wait until the next round.\r\n");
				}
				
			}
		}
	}
	
	public void performGameMenu(User u, ActionProcessSTF game) throws MenuExitException {
		String menuString = "Welcome to Spot The Fed! \r\n";
		menuString += "(01) Vote \r\n";
		menuString += "(02) Commit Suicide \r\n";
		menuString += "(03) Use Role \r\n";
		menuString += "(04) Read Notifications \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";

		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				vote(u);
				break;
			case 2:
				suicide(u);
				break;
			case 3:
				useRole(u);
				break;
			case 4:
				u.broadcast("Notifications are not yet implemented!");
				break;
			}
		}
		u.broadcast("\r\nExiting Configuration Menu.\r\n\r\n");
	}
	
	private void useRole(User u) throws MenuExitException{
		ActionProcessSTF game = this.getExistingGame();
		STFPlayer player = game.getPlayer(u);
		
		String menuString = "";
		
		
		if (player != null){
			
			switch (player.getPlayerRole()){
			case PROTECTOR:
				menuString = "Who do you want to protect this round: \r\n";
			break;
			case AGENT:
				menuString = "Who do you want to investigate this round: \r\n";
			break;
			case ASSASIN:
				menuString = "Who do you want to out this round: \r\n";
			break;
			case PUNK:
				menuString = "You're just a punk. Who do you want to hate this round: \r\n";
			break;
			}
			@SuppressWarnings("unchecked")
			Vector<STFPlayer> playerList = (Vector<STFPlayer>) game.getPlayers().clone();
			for (int i = 0; i < playerList.size() ; i++){
				menuString += "("+(i+1)+") "+playerList.get(i).getUserName()+"\r\n";
			}
			PromptForInteger p = new PromptForInteger(u, menuString, playerList.size(), 1);
			if (p.display()) {
				String target = playerList.get(p.getResult()-1).getUserName();
				player.setTarget(target);
				u.broadcast("Targeting: "+target);
			}
			
		}
	}
	
	private void suicide(User u) throws MenuExitException{
		ActionProcessSTF game = this.getExistingGame();
		String menuString = "Are you sure you want to commit suicide? \r\n";
			  menuString += "(1) Yes \r\n";
			  menuString += "(2) No \r\n";
			  
			  PromptForInteger p = new PromptForInteger(u, menuString, 2, 1);
			  if (p.display()){
				  if (p.getResult() == 1){
						@SuppressWarnings("unchecked")
						Vector<STFPlayer> playerList = (Vector<STFPlayer>) game.getPlayers().clone();
						for (int i = 0; i < playerList.size() ; i++){
							if (u.getPlayerName() == playerList.get(i).getUserName()){
								game.hangPlayer(i);
							}
						}
				  }
			  }
	}
	
	private void vote(User u) throws MenuExitException{
		ActionProcessSTF game = this.getExistingGame();
		String menuString = "Who do you want to vote for: \r\n";
		
		@SuppressWarnings("unchecked")
		Vector<STFPlayer> playerList = (Vector<STFPlayer>) game.getPlayers().clone();
		for (int i = 0; i < playerList.size() ; i++){
			menuString += "("+(i+1)+") "+playerList.get(i).getUserName()+"\r\n";
		}

		PromptForInteger p = new PromptForInteger(u, menuString, playerList.size(), 1);
		if (p.display()) {
			String votedName = playerList.get(p.getResult()-1).getUserName();
			game.castVote(u, votedName);
			World.getArea().GlobalChat(u.getPlayerName()+" voted for "+votedName);
		}
	}
	
	public void configMenu(User u) throws MenuExitException {
		String menuString = "Configure Command Loop Item Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Display structure \r\n";
		menuString += "(04) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";

		PromptForInteger p = new PromptForInteger(u, menuString, 5, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2:
				this.configActionPrereqSetting(u);
				break;
			case 3:
				u.broadcast(this.getStructure());
				break;
			case 4:
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Configuration Menu.\r\n\r\n");
	}

	
	public void performSetupMenu(User u) throws MenuExitException{
		String menuString = "Configure Spot The Fed Game:\r\n";
		menuString += "(01) Add Player \r\n";
		menuString += "(02) Remove Player \r\n";
		menuString += "(03) List Players \r\n";
		menuString += "(04) Config Round Times \r\n";
		menuString += "(05) Start Game \r\n";
		menuString += "(06) Load \r\n";
		menuString += "(07) Save \r\n";
		menuString += "(08) Exit \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";

		PromptForInteger p = new PromptForInteger(u, menuString, 8, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.menuConfigAddPlayer(u);
				break;
			case 2:
				u.broadcast("List Players Not Yet Implemented\r\n");
				break;
			case 3:
				u.broadcast("Load Game Not Yet Implemented\r\n");
				break;
			case 4:
				this.menuConfigRoundTime(u);
				break;
			case 5:
				this.startGame(u);
				break;
			case 6:
				u.broadcast("Load Game Not Yet Implemented\r\n");
				break;
			case 7:
				this.configActionSave(u);
				break;
			case 8:
				throw new MenuExitException();
			}
		}
		u.broadcast("\r\nExiting Configuration Menu.\r\n\r\n");
	}
	
	private void menuConfigAddPlayer(User u) throws MenuExitException{
		
		boolean recieved = false;
		boolean found = false;
		String result = null;
		String menuString = "Enter new players username:";
		String fileName = null;
		PromptForString s = null;
		User newPlayer = new User();   
		STFPlayer newSTFPlayer = new STFPlayer();
       
		//Get the username for the new player
        	s = new PromptForString(u, menuString);
        	recieved = s.display();
		
        	if (recieved){ // if user entered a recipient, try to find that user			
        		result = s.getResult();
        		
        		for (STFPlayer player : newSTFPlayers){
        			if (player.getUserName().equals(result)){// if the player you're looking for already exists in the Array ...
        				u.broadcast("Player Already Added!");
        				throw new MenuExitException(); // let the system know this menu item had an error
        			}
        		} 
        	
		
			Enumeration<User> e = User.getUsers().elements();//TODO dangerous to access, in this instance a copy of the vector and it's elements may be preferred. 
			while (e.hasMoreElements() && !found) {
				Player player = (Player) e.nextElement();
				if (player.getPlayerName().equals(result)) {
					found = true;
					newPlayer = (User) player; 
					//TODO if you're writing a message to someone and they log off, this will fail. 
				}
			}
			// if the user isn't currently logged in, look for the user's file. 
			if (!found){
				fileName = World.getArea().getUserDir() + "/" + s.getResult() + ".xml";
				found = newPlayer.loadUser(fileName);
			}
			if (found){
				// add user to game
				newSTFPlayer.setUserName(newPlayer.getPlayerName());
				newSTFPlayers.add(newSTFPlayer);
				u.broadcast("Player Added!");
			} else { // If still not found, report to the user that the username is invalid. 
				u.broadcast("User not found.");
			}
        	}
	}
	
	private void menuConfigRoundTime(User u) throws MenuExitException{
		String menuString = "Configure Round Time:\r\n";
		menuString += "Enter the number of minutes, per user, each round will last.\r\n";
		menuString += "If you have 6 players, and enter '10', the first round will last 60 minutes.\r\n";
		menuString += "Enter a number between 1 and 10:";

		PromptForInteger p = new PromptForInteger(u, menuString, 10, 1);
		if (p.display()){
			this.setMinutesPerPerson(p.getResult());
		} else {
			u.broadcast("Round Time unchanged");
		}			
	}

	private ActionProcessSTF getExistingGame(){
		
		ActionProcessSTF result = null;
		
        System.out.println("processing Action list.");
		@SuppressWarnings("unchecked") // We know this Vector only contains that type. TODO: change vectors to lists.
		// Make a copy so that actions can remove themselves without concurrency issues.
		Vector<ActionTimed> ActionListCopy = (Vector<ActionTimed>) World.getArea().getActionList().clone();
        
        for (ActionTimed a : ActionListCopy){
        	if (a.getTimedAction() instanceof ActionProcessSTF ){
        		result = (ActionProcessSTF) a.getTimedAction();	
        	}
        }
		
		
		return result;	
	}
	
	private void setRoles(){	
		// set everyone to a punk
		for (int i = 0; i < newSTFPlayers.size() ; i++){
			newSTFPlayers.get(i).setPlayerRole(STFPlayer.role.PUNK);
		}
		// shuffle the group, then set 1/4 of the players to FBI Agents.
		Collections.shuffle(newSTFPlayers);
		int fedCount = (newSTFPlayers.size() / 4);
		
		if (fedCount < 1){
			fedCount = 1; // there must be at least one fed, but really, any less than 4 players is unplayable. 
		}
		
		for (int i = 0; i <= fedCount-1 ; i++){
			STFPlayer cPlayer;
			cPlayer = newSTFPlayers.get(i);
			
			cPlayer.setFed(true);
			cPlayer.setPlayerRole(STFPlayer.role.AGENT);	
		}
		// set a protector and assasin
		newSTFPlayers.get(fedCount).setPlayerRole(STFPlayer.role.PROTECTOR);
		newSTFPlayers.get(fedCount+1).setPlayerRole(STFPlayer.role.ASSASIN);
		// re-shuffle so people don't realize the players at the top of the list are most important.
		Collections.shuffle(newSTFPlayers); 
	}
	
	@SuppressWarnings("unchecked")
	private void startGame(User u) throws MenuExitException{
		ActionTimed gameTimer = new ActionTimed();
		ActionProcessSTF gameProcess = new ActionProcessSTF();
		
		if (newSTFPlayers.size() < 4){
			u.broadcast("At least 4 players required to play. 8 recommended.\r\n");
			throw new MenuExitException();
		}
		
		setRoles();
		
		gameProcess.setRoundTimer(this.getMinutesPerPerson() * this.getNewSTFPlayers().size());
		gameProcess.setMinutesPerPerson(minutesPerPerson);
		gameProcess.setParent(gameTimer);
		gameProcess.setPlayers((Vector<STFPlayer>) this.getNewSTFPlayers().clone());
		
		gameTimer.setName("STF_GameTimer");
		gameTimer.setTimedAction(gameProcess);
		World.getArea().getActionList().add(gameTimer);
		newSTFPlayers.clear();
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
	public boolean configActionRange(User u) {
		u.broadcast("Look can only target the user who triggered the action \r\n");
		return false;
	}

	public int getMinutesPerPerson() {
		return minutesPerPerson;
	}

	public void setMinutesPerPerson(int minutesPerPerson) {
		this.minutesPerPerson = minutesPerPerson;
	}

	public Vector<STFPlayer> getNewSTFPlayers() {
		return newSTFPlayers;
	}

	public void setNewSTFPlayers(Vector<STFPlayer> newSTFPlayers) {
		this.newSTFPlayers = newSTFPlayers;
	}

}
