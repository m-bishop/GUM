package gum.actions.player;

import gum.Player;
import gum.User;
import gum.UserParser;
import gum.World;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.items.Item;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForInteger;
import gum.mobs.Mob;

import java.util.StringTokenizer;

public class ActionPlayerConfig extends Action {

	public ActionPlayerConfig(){
	}
	
	public void init(){
		this.setActionName("Config");
		this.setRange(EffectRange.USER);
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Player player = header.getPlayer();
		String token = "";
		
		StringTokenizer st = player.getCommandST();
		
		if (player.getSetting("builder") == 1) {
			if (st.hasMoreTokens()) {
				token = st.nextToken();
			} else {
				new MenuHandler((User) player, World.getArea());
			}
			if (token.equals("area")) {
				new MenuHandler((User) player, World.getArea());
			} else if (token.equals("room")) {
				if (player instanceof User) {
					new MenuHandler(((User) player), player.getCurrentRoom());
				}
			} else if (token.equals("mob")) {
				if (st.hasMoreTokens()) {
					token = st.nextToken();
				}
				Mob m = (Mob) player.getCurrentRoom().getPlayerByName(token);
				if (m != null) {
					new MenuHandler((User) player, m);
				} else {
					player.broadcast("Mob not found\r\n");
				}
			} else if (token.equals("item")) {
				Item configItem = UserParser.parseForItem(st, player, true);
				if (configItem != null) {
					new MenuHandler((User) player, configItem);
					;
				}
			} else {
				player.broadcast("What do you want to configure?\r\n");
			}
		} else {
			player
					.broadcast("You must be a builder to run the configure command.\r\n");
		}
	
		
		return true;
		
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure config Player Action:\r\n";
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
