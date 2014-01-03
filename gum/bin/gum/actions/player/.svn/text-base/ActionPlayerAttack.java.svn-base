package gum.actions.player;

import gum.BattleHandler;
import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

import java.util.StringTokenizer;

public class ActionPlayerAttack extends Action {

	public ActionPlayerAttack(){
		super();
		this.setRange(EffectRange.USER);
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Player player = header.getPlayer();
		
		StringTokenizer st = player.getCommandST();
		
		String token = "";
		if (st.hasMoreTokens()) {
			token = st.nextToken();
		}
		Player enemy = null;
		enemy = player.getCurrentRoom().getPlayerByName(token);
		if ((player.getRightHand() == null)
				&& (player.getLeftHand() == null)) {
			player.broadcast("You aren't holding a weapon!\r\n");
		}if ((player instanceof User) && (enemy.getRightHand() == null)// let mobs attack unarmed players.
				&& (enemy.getLeftHand() == null)) {
			player.broadcast("Your target appears to be unarmed!\r\n");
		}else if (player.getFightingFlag()) {
			player.broadcast("You are already fighting!\r\n");
		} else if (player == enemy) {
			player.broadcast("You cannot attack yourself!\r\n");
		} else if (enemy != null) {
			new BattleHandler(player, enemy);
		} else {
			player.broadcast("attack who?\r\n");
		}
		
		return true;
		
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure attack Player Action:\r\n";
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
		u.broadcast(" Player Action can only be used by the player who triggered it. \r\n" );
		return false;	
	}


}
