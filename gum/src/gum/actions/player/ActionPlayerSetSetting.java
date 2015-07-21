package gum.actions.player;

import gum.Player;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

import java.util.StringTokenizer;

public class ActionPlayerSetSetting extends Action {

	public ActionPlayerSetSetting() {
	}

	public void init() {
		this.setActionName("SetSetting");
		this.setRange(EffectRange.USER);
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Player player = header.getPlayer();
		StringTokenizer st = new StringTokenizer(player.getCommand());
		st.nextToken();
		boolean result = false;
		String settingName;
		Integer settingValue;

		if ((player.getSetting("builder")) == 1) {
			if (st.countTokens() != 2) {
				player.broadcast("SetSetting Requires a Setting and a Value \r\n");
			} else {
				settingName = st.nextToken();
				settingValue = Integer.valueOf(st.nextToken());
				player.setSetting(settingName, settingValue);
			}
		} else {
			player.broadcast("You must be a builder to run that command");
		}

		return result;
	}

	@Override
	public void menu(User u) throws MenuExitException {

		String menuString = "Configure Put Player Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Configure success action \r\n";
		menuString += "(05) Configure success message \r\n";
		menuString += "(06) Configure Failure action \r\n";
		menuString += "(07) Configure Failure message \r\n";
		menuString += "(08) Display structure \r\n";
		menuString += "(09) Save \r\n";
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
				this.configFailureAction(u);
				break;
			case 7:
				this.configActionFailMessage(u);
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
	public boolean configActionRange(User u) {
		u.broadcast("SetSetting can only be used by the player who triggered it. \r\n");
		return false;
	}

}
