package gum.actions.player;

import gum.Player;
import gum.User;
import gum.UserParser;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

import java.util.StringTokenizer;

public class ActionPlayerLook extends Action {

	public ActionPlayerLook(){
	}
	
	public void init(){
		this.setActionName("Look");
		this.setRange(EffectRange.USER);
	}
	
	@Override
	public boolean doAction(ActionHeader header) {
		Player player = header.getPlayer();
		//StringTokenizer st = new StringTokenizer(player.getCommand());
		//String token = st.nextToken();
		String token = "";
		StringTokenizer st = player.getCommandST();
		
		
		if (st.hasMoreElements()){
			token = st.nextToken();
			if (token.equals("at")){
				if (st.hasMoreTokens()){
					String subject = st.nextToken("\r\n");
				
				
					token = "look"+subject+"\r\n";
					StringTokenizer itemSt = new StringTokenizer(token," ");
					if (!UserParser.parseItemAction(itemSt, player)){
						Player subjectPlayer = player.getCurrentRoom().getPlayerByName(subject.trim());
						System.out.println("looking for:"+subject);
						if (subjectPlayer != null){
							System.out.println("Found:"+subject);
							player.broadcast(subjectPlayer.getPlayerDescription()+"\r\n");
						}
					}
				}
				//UserParser.parsePlayerCommand(player, token);
			}else {
				player.broadcast("Did you want to 'look at' something?\r\n");
			}
		}else {
			player.getCurrentRoom().performTriggeredAction("look", player);
			//player.look();
		}
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure Look Player Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Configure success action \r\n";
		menuString += "(05) Configure success message \r\n";
		menuString += "(06) Display structure \r\n";
		menuString += "(07) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 7, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;				
			case 2:
				this.configActionPrereqSetting(u);
				break;
			case 3:
				this.configActionRange(u);
				break;
			case 4:
				this.configSuccessAction(u);
				break;
			case 5:
				this.configActionSuccessMessage(u);
				break;
			case 6: 
				u.broadcast(this.getStructure());
				break;
			case 7: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Configuration Menu.\r\n\r\n");	


	}
	
	@Override
	public boolean configActionRange(User u){
		u.broadcast("Look only effects the user who used the 'look' command." );
		return false;	
	}
}
