package gum;

//import java.io.IOException;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.items.Item;

import java.util.StringTokenizer;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class UserParser {

	//private StringTokenizer st;

	public UserParser() {

	}

	public static int numTextToInt(String token) {
		// this could be handled with a map.
		// remember to edit so that class level initializes with a replacement
		// map.

		int result = 0;
		if (token.equals("first")) {
			result = 1;
		} else if (token.equals("second")) {
			result = 2;
		} else if (token.equals("third")) {
			result = 3;
		} else if (token.equals("fourth")) {
			result = 4;
		} else if (token.equals("fifth")) {
			result = 5;
		}
		return result;
	}

	public static Item parseForItem(StringTokenizer st, Player player, boolean verbose) {
		Item result = null;
		// create a copy of the string tokenizer

		String token = "";
		int positionOfItem = 1;
		boolean personalInv = false;

		if (st.hasMoreTokens()) {
			token = st.nextToken();
			// TODO create a map of declarative pronouns to check against. 
			//if (token.equalsIgnoreCase("the")){
			//	token = st.nextToken();
			// }
		}
		if (token.equals("my")) {
			personalInv = true;
			if (st.hasMoreTokens()) {
				token = st.nextToken();
			} else {
				if (verbose) {
					player
							.broadcast("Cannot find what you're looking for in your inventory\r\n");
				}
			}
		}
		positionOfItem = numTextToInt(token);
		if (positionOfItem > 0) {
			if (st.hasMoreTokens()) {
				token = st.nextToken();
			} else {
				if (verbose) {
					player
							.broadcast("Cannot find what you're looking for.\r\n");
				}
			}
		} else {
			positionOfItem = 1;
		}
		if (personalInv) {
			result = player.getItemByNameAndPosition(token, positionOfItem);
			if (result == null) {
				if (verbose) {
					player
							.broadcast("Could not find item in your inventory!\r\n");
				}
			}
		} else {
			result = player.getCurrentRoom().getItemByNameAndPosition(token,
					positionOfItem);
			if (result == null){
				result = player.getItemByNameAndPosition(token, positionOfItem);
			}
			if (result == null) {
				if (verbose) {
					player.broadcast("Could not find "+token+" here.\r\n");
				}
			}
		}

		return result;
	}
	
	public static boolean parsePlayerAction(StringTokenizer st, Player player){
		boolean found = false;
		try{
		//StringTokenizer st = new StringTokenizer(player.getCommand());
		player.setCommandST(st);
		
		if (st.hasMoreTokens()){
			Action playerAction = (Action) player.getPlayerCommands().get(st.nextToken());
			if (playerAction != null){
				ActionHeader header = new ActionHeader(player,null,null,null);
				playerAction.perform(header);
				
				found = true;
			}
		}
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("error in Parse Player Action.");
		}
		return found;
	}
	
	public static boolean parseItemAction(StringTokenizer st, Player player){
		
		boolean result = false;
		String token = "";
		if (st.hasMoreTokens()){
			token = st.nextToken();
		}
		
		Item i = UserParser.parseForItem(st, player, false);

		if (i != null) {// found an item, pass this to item's 'use' method.
			result = true;
			System.out.println("got here <calling i.use> on player item");
			i.use(player, token, st);
		} 
		
	return result;	
	}

	public boolean parsePlayerCommand(Player player, String msg) {
		
		boolean found = true;
		
		msg = msg.replace("the ", "");
		msg = msg.replace("a ", "");
		msg = msg.replace("an ", "");
		msg = msg.replace("to ", "");
		System.out.println("Message to parse:"+msg);
		
		//if (player instanceof User){		
			found = UserParser.parsePlayerAction(new StringTokenizer(msg, " "),player);
		//}
		if (!found ){// temporarily limit item use to 
			found = UserParser.parseItemAction(new StringTokenizer(msg, " "), player);
		}

		if ((player instanceof User) && !found) {			
				player.setCommandST(new StringTokenizer(player.getCommand()));
				player.performAction("chat");
				
			}
		return found;
	}

	
}
