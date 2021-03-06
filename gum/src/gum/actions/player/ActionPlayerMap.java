package gum.actions.player;

import gum.Player;
import gum.Room;
import gum.User;
import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;

public class ActionPlayerMap extends Action {
	
	public ActionPlayerMap(){
	}
	
	public void init(){
		this.setRange(EffectRange.USER);
		this.setActionName("Map");
	}

	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure Map Player Action:\r\n";
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
	public boolean doAction(ActionHeader header) {
		
		Player p = header.getPlayer();
		Room cRoom = p.getCurrentRoom();
		
		String current = this.formatRoomName(p.getCurrentRoom());
		String north = this.formatRoomName(cRoom.getNorthRoom());
		String nArrow1 = "||";
		String nArrow2 = "||";
		String nArrow3 = "||";
		String south = this.formatRoomName(cRoom.getSouthRoom());
		String sArrow1 = "||";
		String sArrow2 = "||";
		String sArrow3 = "||";
		String east = this.formatRoomName(cRoom.getEastRoom());
		String eArrow = "------";
		String west = this.formatRoomName(cRoom.getWestRoom());
		String wArrow = "------";
		String northNorth = "        ";
		String nnArrow = "||";
		String northEast = "        ";
		String neArrow = "---";
		String northWest = "        ";
		String nwArrow = "---";
		String eastEast = "        ";
		String eeArrow = "---";
		String eastNorth = "        ";
		String enArrow = "||";
		String eastSouth = "        ";
		String esArrow = "||";
		String westWest = "        ";
		String wwArrow = "---";
		String westNorth = "        ";
		String wnArrow = "||";
		String westSouth = "        ";
		String wsArrow = "||";
		String southSouth = "        ";
		String ssArrow = "||";
		String southEast = "        ";
		String seArrow = "---";
		String southWest = "        ";
		String swArrow = "---";
		
		if (cRoom.getNorthRoom() != null){
			northNorth = this.formatRoomName(cRoom.getNorthRoom().getNorthRoom());
			if (northNorth.equals("        ")){
				nnArrow = "  ";
			}
			northEast = this.formatRoomName(cRoom.getNorthRoom().getEastRoom());
			if (northEast.equals("        ")){
				neArrow = "   ";
			}
			northWest = this.formatRoomName(cRoom.getNorthRoom().getWestRoom());
			if (northWest.equals("        ")){
				nwArrow = "   ";
			}
		} else {
			nArrow1 = "  ";
			nArrow2 = "  ";
			nArrow3 = "  ";
			nnArrow = "  ";
			neArrow = "   ";
			nwArrow = "   ";
		}
		
		if (cRoom.getEastRoom() != null){
			eastEast = this.formatRoomName(cRoom.getEastRoom().getEastRoom());
			if (eastEast.equals("        ")){
				eeArrow = "   ";
			}
			eastNorth = this.formatRoomName(cRoom.getEastRoom().getNorthRoom());
			if (eastNorth.equals("        ")){
				enArrow = "  ";
			}
			eastSouth = this.formatRoomName(cRoom.getEastRoom().getSouthRoom());
			if (eastSouth.equals("        ")){
				esArrow = "  ";
			}
		} else {
			eArrow = "      ";
			eeArrow = "   ";
			enArrow = "  ";
			esArrow = "  ";
		}
		
		if (cRoom.getWestRoom() != null){
			westWest = this.formatRoomName(cRoom.getWestRoom().getWestRoom());
			if (westWest.equals("        ")){
				wwArrow = "   ";
			}
			westNorth = this.formatRoomName(cRoom.getWestRoom().getNorthRoom());
			if (westNorth.equals("        ")){
				wnArrow = "  ";
			}
			westSouth = this.formatRoomName(cRoom.getWestRoom().getSouthRoom());
			if (westSouth.equals("        ")){
				wsArrow = "  ";
			}
		} else {
			wArrow = "      ";
			wwArrow = "   ";
			wnArrow = "  ";
			wsArrow = "  ";
		}
		
		if (cRoom.getSouthRoom() != null){
			southSouth = this.formatRoomName(cRoom.getSouthRoom().getSouthRoom());
			if (southSouth.equals("        ")){
				ssArrow = "  ";
			}
			southEast = this.formatRoomName(cRoom.getSouthRoom().getEastRoom());
			if (southEast.equals("        ")){
				seArrow = "  ";
			}
			southWest = this.formatRoomName(cRoom.getSouthRoom().getWestRoom());
			if (southWest.equals("        ")){
				swArrow = "  ";
			}
		} else {
			sArrow1 = "  ";
			sArrow2 = "  ";
			sArrow3 = "  ";
			ssArrow = "  ";
			seArrow = "   ";
			swArrow = "   ";
		}
		 
		
		p.broadcast("YOU ARE HERE: %MAGENTA * %SANE                                  MAP UTIL Ver 0.2");		
		p.broadcast("                                  "+northNorth+"                                    ");
		p.broadcast("                                     "+nnArrow+"                                       ");
		p.broadcast("                       "+northWest+nwArrow+north+neArrow+northEast+"                         ");
		p.broadcast("                                     "+nArrow3+"                                       ");
		p.broadcast("                   "+westNorth+"          "+nArrow2+"          "+eastNorth+"                     ");
		p.broadcast("                      "+wnArrow+"           %MAGENTA * %SANE "+nArrow1+"             "+enArrow+"                        ");
		p.broadcast("         "+westWest+wwArrow+west+wArrow+current+eArrow+east+eeArrow+eastEast+"           ");
		p.broadcast("                      "+wsArrow+"             "+sArrow1+"             "+esArrow+"                        ");
		p.broadcast("                   "+westSouth+"          "+sArrow2+"          "+eastSouth+"                     ");
		p.broadcast("                                     "+sArrow3+"                                       ");
		p.broadcast("                       "+southWest+swArrow+south+seArrow+southEast+"                         ");
		p.broadcast("                                     "+ssArrow+"                                       ");
		p.broadcast("                                  "+southSouth+"                                    ");
		p.broadcast("                                                                              ");
	//	p.broadcast("                                                                                ");
	//	p.broadcast("********************************************************************************");
		
		
		return false;
	}
	
	private String formatRoomName(Room room){
		
		String result = null;
		String prepad = "";
		
		if (room == null){
			result = "        ";
		}
		else{
			result = room.getName();
			if (result.equals("null")){
				result = "        ";
			}
		}

//		if (result.length() > 8){
//			result = result.substring(0,8);
//		}
		
		if (result.length() < 7){
			for (int i = 0;i < ((7-result.length())/2);i++){
				prepad = prepad+" ";
				System.out.println(i+" compared to "+ ((7-result.length())/2));
			}
			result = prepad+result;
		}
		if (result.length() < 7) {
			while (result.length() < 7){
				result = result + " ";
			}
		}
		if (room != null){
			switch (room.getSetting("COLOR")) {
				case 0: result = result + " %SANE ";break;
				case 1: result = "%GREEN " + result + " %SANE "; break;
				case 2: result = "%YELLOW " + result + " %SANE "; break;
				case 3: result = "%RED " + result + " %SANE "; break;
				case 4: result = "%MAGENTA " + result + " %SANE "; break;
			}
		}
		
		return result;
	}

}
