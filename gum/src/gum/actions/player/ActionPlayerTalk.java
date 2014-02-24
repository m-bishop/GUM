package gum.actions.player;

import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

import java.util.StringTokenizer;

public class ActionPlayerTalk extends Action{

	public ActionPlayerTalk(){
	}
	
	public void init(){
		this.setActionName("Talk");
		this.setRange(EffectRange.USER);
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Player player = header.getPlayer();
		StringTokenizer st = player.getCommandST();

		String token = "";
		String target = null;
		
		if (st.hasMoreTokens()) {
			target = st.nextToken();
		}

		if (st.hasMoreTokens()) {
			token = st.nextToken("\r\n");
		}
		player.talk(target, (token + "\r\n"));

	return true;	
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure talk Player Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Configure success action \r\n";
		menuString += "(05) Configure success message \r\n";
		menuString += "(06) Display structure \r\n";
		menuString += "(07) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 9, 1);
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
			case 8: 
				u.broadcast(this.getStructure());
				break;
			case 9: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Configuration Menu.\r\n\r\n");	


	}
	
	@Override
	public boolean configActionRange(User u){
		u.broadcast(" Player Action can only be used by the player who triggered it. \r\n" );
		return false;	
	}
}
