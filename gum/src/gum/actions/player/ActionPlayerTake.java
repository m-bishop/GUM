package gum.actions.player;

import java.util.StringTokenizer;

import gum.Player;
import gum.User;
import gum.UserParser;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

public class ActionPlayerTake extends Action {
	
	public ActionPlayerTake(){
	}
	
	public void init(){
		this.setActionName("Take");
		this.setRange(EffectRange.USER);
	}

	@Override
	public boolean doAction(ActionHeader header) {
		Player player = header.getPlayer();
		//StringTokenizer st = new StringTokenizer(player.getCommand());
		StringTokenizer st = player.getCommandST();
		//String token = st.nextToken();
		//System.out.println("Token in Player Take:"+token);
		boolean result = false;
		
		// The naming convention token1,token2, ect seems terrible, but
		// it is much easier to parse the meaning of the string when
		// you know what order each word arrived in.

		String token2 = "";
		String token3 = "";
		String token4 = "";

		String item1 = "";
		String item2 = "";
		boolean personalInv = false;
		int positionOfItem1 = 1;
		int positionOfItem2 = 1;

		if (st.hasMoreTokens()) {
			token2 = st.nextToken();
			positionOfItem1 = UserParser.numTextToInt(token2);
			if (positionOfItem1 > 0) { // they've said 'take second...'
				if (st.hasMoreTokens()) {
					item1 = st.nextToken();
				} else {
					player.broadcast("take " + token2 + " what?\r\n");
				}
			} else if (token2.equals("my")) {
				player
						.broadcast("Can't 'take' from your own inventory.\r\n");
			} else {
				positionOfItem1 = 1;
				item1 = token2;
			}
		}
		if (st.hasMoreTokens()) {
			token3 = st.nextToken();
			if (token3.equals("from")) {
				if (st.hasMoreTokens()) {
					token4 = st.nextToken();
				}
				if (token4.equals("my")) {
					personalInv = true;
					if (st.hasMoreTokens()) {
						token4 = st.nextToken();
					} else {
						player.broadcast("You want to take " + item1
								+ " from your what?\r\n");
					}
				}
				positionOfItem2 = UserParser.numTextToInt(token4);
				if (positionOfItem2 > 0) {
					if (st.hasMoreTokens()) {
						item2 = st.nextToken();
					} else {
						player.broadcast("take what from where?\r\n");
					}
				} else {
					item2 = token4;
					positionOfItem2 = 1;
				}
				if (!item1.equals("") && !item2.equals("")) {
					// player.get(token4, token2); // add 'my' boolean
					// indicator
					player.get(item1, positionOfItem1, item2,
							positionOfItem2, personalInv);
				} else {
					player
							.broadcast("were you trying to get something?\r\n");
				}

				// } else if(positionOfItem1 > 0){
				// player.take(token3, positionOfItem1);

			} else {
				player
						.broadcast("were you trying to get something?\r\n");
				System.out.println(item1 + ":" + token2 + ":" + token3);
			}
		} else {
			player.take(item1, positionOfItem1);
			result = true;
			
		}
		return result;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure Take Player Action:\r\n";
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
	public boolean configActionRange(User u){
		u.broadcast("Take can only be used to give an item to the player who  \r\n " +
					"triggered the action. \r\n" );
		return false;	
	}


}
