package gum;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import gum.actions.Action;
import gum.actions.ActionHeader;
import gum.actions.Action.EffectRange;
import gum.actions.room.ActionRoomLook;
import gum.items.Item;
import gum.items.ItemBase;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

import gum.menus.MenuContainer;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;
import gum.mobs.Mob;

public class Room implements respawnable, MenuContainer,ItemContainer {

	private String name;
	private String description;
	private String filename;

	// exits
	private String northExit;
	private Room northRoom;
	private String southExit;
	private Room southRoom;
	private String eastExit;
	private Room eastRoom;
	private String westExit;
	private Room westRoom;
	private String upExit;
	private Room upRoom;
	private String downExit;
	private Room downRoom;
	
	
	private HashMap<String,Action> triggerActions = new HashMap<String,Action>();
	
	private byte[] respawnItemsCopy = null;
	private int respawnMinutes = 0;
	private int respawnTimeout;

	private Vector<Player> players = new Vector<Player>();
	private Vector<Item> items = new Vector<Item>();
	private HashMap<String, Integer> settings = new HashMap<String, Integer>(); // setting map

	public static HashMap<String, String> WorldReplaceMap = new HashMap<String, String>();

	private Random random = ObjectFactory.getRandObject();

	public Room() {
		// create a totally blank room.
		 //init();
	}
	
	public void init(){
		filename = "DEFAULT";
		name = "DEFAULT";
		ActionRoomLook lookAction = new ActionRoomLook();
		lookAction.init();
		triggerActions.put("look", lookAction);
	}

/*	public Room(String file) {
		String itemList;
		String mobList;
		Item newItem;
		Mob newMob;

		// file = this.getImageDir() + "/"+file;
		System.out.print("loading " + file);
		FileHandler roomFile = new FileHandler();
		filename = file;
		name = roomFile.read_setting(file, "name", false);
		// System.out.print(" "+name);
		description = roomFile.read_setting(file, "description", true);
		// System.out.println(" "+description);
		northExit = roomFile.read_setting(file, "north", false);
		// System.out.println("north " + northExit);
		southExit = roomFile.read_setting(file, "south", false);
		// System.out.println("south " + southExit);
		eastExit = roomFile.read_setting(file, "east", false);
		// System.out.println("east " + eastExit);
		westExit = roomFile.read_setting(file, "west", false);
		// System.out.println("west " + westExit);
		upExit = roomFile.read_setting(file, "up", false);
		// System.out.println("up " + upExit);
		downExit = roomFile.read_setting(file, "down", false);
		// System.out.println("down " + downExit);

		itemList = roomFile.read_setting(file, "items", false);
		// System.out.println("itemlist:"+itemList);
		StringTokenizer itemST = new StringTokenizer(itemList, ";");
		while (itemST.hasMoreTokens()) {
			newItem = ObjectFactory.CreateItem(itemST.nextToken());
			// newItem = new SimpleItem(itemST.nextToken());
			if (newItem != null) {
				items.add(newItem);
			}
		}

		mobList = roomFile.read_setting(file, "mobs", false);
		// System.out.println("moblist:"+mobList);
		StringTokenizer mobST = new StringTokenizer(mobList, ";");
		while (mobST.hasMoreTokens()) {
			// newMob = new Mob(this.getImageDir()+"/"+mobST.nextToken());
			newMob = ObjectFactory.CreateMob(mobST.nextToken());
			this.addPlayer(newMob);
			newMob.setRespawnRoom(this);
		}
		System.out.println("adding:" + this.getRoomFilename());
		World.getArea().getRooms().addElement(this);

	}

*/

	public Room(Room root, String exitDir) {
		System.out.println("creating a new room");
		ActionRoomLook lookAction = new ActionRoomLook();
		lookAction.init();
		
		
		triggerActions.put("look", lookAction);
		
		if (exitDir.equalsIgnoreCase("north") || exitDir.equalsIgnoreCase("n")) {
			this.northExit = root.getRoomFilename();
			this.northRoom = root;
		}
		if (exitDir.equalsIgnoreCase("south") || exitDir.equalsIgnoreCase("s")) {
			this.southExit = root.getRoomFilename();
			this.southRoom = root;
		}
		if (exitDir.equalsIgnoreCase("east") || exitDir.equalsIgnoreCase("e")) {
			this.eastExit = root.getRoomFilename();
			this.eastRoom = root;
		}
		if (exitDir.equalsIgnoreCase("west") || exitDir.equalsIgnoreCase("w")) {
			this.westExit = root.getRoomFilename();
			this.westRoom = root;
		}
		if (exitDir.equalsIgnoreCase("up") || exitDir.equalsIgnoreCase("u")) {
			this.upExit = root.getRoomFilename();
			this.upRoom = root;
		}
		if (exitDir.equalsIgnoreCase("down") || exitDir.equalsIgnoreCase("d")) {
			this.downExit = root.getRoomFilename();
			this.downRoom = root;
		}

		this.name = "newroom";

		System.out.println("adding room to array");
		World.getArea().getRooms().addElement(this);
		// System.out.println("initializing exits");
		// this.initializeExits();
	}

	// TODO remove after new saving system is complete.
/*	public void saveRoom() {
		FileHandler roomFile = new FileHandler();
		String playerList = "";
		String itemList = "";
		String rootName = this.getRoomFilename();

		roomFile.clear_file(rootName);
		System.out.println("Saving Room:" + rootName);

		roomFile.write_setting(rootName, "name", name);
		roomFile.write_setting(rootName, "description", description);
		roomFile.write_setting(rootName, "filename", filename);
		roomFile.write_setting(rootName, "north", northExit);
		roomFile.write_setting(rootName, "south", southExit);
		roomFile.write_setting(rootName, "east", eastExit);
		roomFile.write_setting(rootName, "west", westExit);
		roomFile.write_setting(rootName, "up", upExit);
		roomFile.write_setting(rootName, "down", downExit);

		// add each mob saved to the mob list
		Enumeration<Player> m = players.elements();
		while (m.hasMoreElements()) {
			Player mob = (Player) m.nextElement();
			if (!(mob instanceof User)) {
				playerList = playerList + rootName + "." + mob.getPlayerName()
						+ ";\r\n";
				mob.save(rootName);
			}
		}
		roomFile.write_setting(rootName, "mobs", playerList);

		// add each item savd to the item list
		Enumeration<Item> e = items.elements();
		while (e.hasMoreElements()) {
			Item i = (Item) e.nextElement();
			itemList = itemList + rootName + "." + i.getItemName() + ";\r\n";
			i.save(rootName);
		}
		roomFile.write_setting(rootName, "items", itemList);
		roomFile.close_file();
	}

*/
	public void save(String fileName) {
		try {
			FileOutputStream os = new FileOutputStream(fileName);
			XMLEncoder encoder = new XMLEncoder(os);
			encoder.writeObject(this);
			encoder.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addToInventory(Item i) {
		items.add(i);
		i.setItemContainer(this);
	}
	


	/*
	 * public String getItemList(){ String output = "items: ";
	 * 
	 * Enumeration e = items.elements(); while (e.hasMoreElements()){ Item i =
	 * (Item)e.nextElement(); output = output + " " + i.getName(); }
	 * 
	 * return output; }
	 */
	public String getPlayerList() {
		String output = "players: ";

		Enumeration<Player> e = players.elements();
		while (e.hasMoreElements()) {
			Player p = (Player) e.nextElement();
			if (p.getFightingFlag()) {
				output = output + "%RED " + p.getPlayerName() + "%SANE ";
			} else {
				output = output + p.getPlayerName();
			}
			if (e.hasMoreElements()) {
				output += ",";
			}
		}

		return output;
	}

	public Boolean isExitBlocked(Item exitOut, Item exitIn){
		boolean result = true;
		boolean exitInVisible = false;
		boolean exitOutVisible = false;
		
		if (exitOut != null){ // if there is a door out
			if (exitOut.getIsOpen()){ // and it's open.
				exitOutVisible = true;
			}	
		} else { // if there's nothing blocking the exit
			exitOutVisible = true;
		}
		if (exitIn != null){ // if there is a door out
			if (exitIn.getIsOpen()){ // and it's open.
				exitInVisible = true;
			}	
		} else { // if there's nothing blocking the exit
			exitInVisible = true;
		}
		if (exitInVisible && exitOutVisible){
			result = false;
		}
		return result;
	}
	
	public String getVisibleExits() {
		//TODO this is a good example of why exits needed to be their own objects from the start. 

		String rooms = "";
		
		if (northRoom != null){
			if (!isExitBlocked(this.checkBlocking("north"),northRoom.checkBlocking("south"))){
				rooms += " north";
			}
		}
		if (southRoom != null){
			if (!isExitBlocked(this.checkBlocking("south"),southRoom.checkBlocking("north"))){
				rooms += " south";
			}
		}
		if (eastRoom != null){
			if (!isExitBlocked(this.checkBlocking("east"),eastRoom.checkBlocking("west"))){
				rooms += " east";
			}
		}
		if (westRoom != null){
			if (!isExitBlocked(this.checkBlocking("west"),westRoom.checkBlocking("east"))){
				rooms += " west";
			}
		}
		if (upRoom != null){
			if (!isExitBlocked(this.checkBlocking("up"),upRoom.checkBlocking("down"))){
				rooms += " up";
			}
		}
		if (downRoom != null){
			if (!isExitBlocked(this.checkBlocking("down"),downRoom.checkBlocking("up"))){
				rooms += " down";
			}
		}
		
/*		if (northRoom != null) {
			if ((this.checkBlocking("north") == null || this.checkBlocking("north").getIsOpen())
					&& (northRoom.checkBlocking("south") == null || this.checkBlocking("south").getIsOpen())) {
				rooms += " north";
			}
		}
		if (southRoom != null) {
			if ((this.checkBlocking("south") == null || this.checkBlocking("south").getIsOpen())
					&& (southRoom.checkBlocking("north") == null || this.checkBlocking("north").getIsOpen())) {
				rooms += " south";
			}
		}
		if (eastRoom != null) { 
			if ((this.checkBlocking("east") == null || this.checkBlocking("east").getIsOpen())
				&& (southRoom.checkBlocking("west") == null | southRoom.checkBlocking("west").getIsOpen())){
				rooms += " east";
			}
		}
		if (westRoom != null) {
			if ((this.checkBlocking("west") == null || this.checkBlocking("west").getIsOpen())
					&& (southRoom.checkBlocking("east") == null || this.checkBlocking("east").getIsOpen())) {
				rooms += " west";
			}
		}
		if (upRoom != null) {
			if ((this.checkBlocking("up") == null || this.checkBlocking("up").getIsOpen())
					&& (southRoom.checkBlocking("down") == null || this.checkBlocking("down").getIsOpen())) {
				rooms += " up";
			}
		}
		if (downRoom != null) {
			if ((this.checkBlocking("down") == null || this.checkBlocking("down").getIsOpen())
					&& (southRoom.checkBlocking("up") == null || this.checkBlocking("up").getIsOpen())) {
				rooms += " down";
			}
		}
		*/
		return rooms;
	}

	public Vector<String> getExitList() {
		Vector<String> exitList = new Vector<String>();

		if (northRoom != null) {
			exitList.add("north");
		}
		if (southRoom != null) {
			exitList.add("south");
		}
		if (eastRoom != null) {
			exitList.add("east");
		}
		if (westRoom != null) {
			exitList.add("west");
		}
		if (upRoom != null) {
			exitList.add("up");
		}
		if (downRoom != null) {
			exitList.add("down");
		}
		return exitList;
	}

	public Item removeItem(Item item) {
		// only return this item if was still in the system
		if (!items.remove(item)) {
			item = null;
		}
		return item;
	}

	public Item getExitItemByName(String itemName) { // still consdier revamping
														// exits...
		Item oItem = null;

		oItem = this.getExitItembyName(northRoom, "south", itemName);
		if (oItem == null) {
			oItem = this.getExitItembyName(southRoom, "north", itemName);
		}
		if (oItem == null) {
			oItem = this.getExitItembyName(eastRoom, "west", itemName);
		}
		if (oItem == null) {
			oItem = this.getExitItembyName(westRoom, "east", itemName);
		}
		if (oItem == null) {
			oItem = this.getExitItembyName(upRoom, "down", itemName);
		}
		if (oItem == null) {
			oItem = this.getExitItembyName(downRoom, "up", itemName);
		}
		return oItem;
	}

	public Item getExitItembyName(Room room, String dir, String itemName) {
		boolean found = false;
		Item oItem = null;

		if (room != null) {
			Enumeration<Item> ed = room.items.elements(); // should do a 'get
															// items' to return
															// the list...
			while (ed.hasMoreElements() && !found) {
				Item item = (Item) ed.nextElement();
				if (item.getBlocking().equals(dir)
						&& item.getItemName().equals(itemName)) {
					oItem = item;
					found = true;
				}
			}
		}
		return oItem;
	}

	public String getExitItemName(Room room, String checkDir) {
		String oString = "";
		boolean found = false;

		if (room != null) {
			Enumeration<Item> ed = room.items.elements(); // should do a 'get
															// items' to return
															// the list...
			while (ed.hasMoreElements() && !found) {
				Item item = (Item) ed.nextElement();
				System.out.println(item.getItemName() + ":blocks:"
						+ item.getBlocking());
				if (item.getBlocking().equals(checkDir)) {
					oString = item.getItemName();
					found = true;
				}
			}
		}
		System.out.println("Exit:" + checkDir + "found:" + oString);
		return oString;
	}

	public Item getItemByName(String itemName) {
		boolean Found = false;
		Item o_item = null;
		Enumeration<Item> e = items.elements();
		while (e.hasMoreElements() && !Found) {
			Item item = (Item) e.nextElement();
			if (item.getItemName().equals(itemName)) {
				o_item = item;
				Found = true;
			}
		}
		if (o_item == null) {
			o_item = this.getExitItemByName(itemName);
		}
		return o_item;
	}

	public Item getItemByNameAndPosition(String itemName, int pos) {
		boolean Found = false;
		Item o_item = null;
		Enumeration<Item> e = items.elements();
		while (e.hasMoreElements() && !Found) {
			Item item = (Item) e.nextElement();
			if (item.getItemName().equals(itemName)) {
				if (pos <= 1) {
					o_item = item;
					Found = true;
				} else {
					pos -= 1;
				}
			}
		}
		if (o_item == null) {
			o_item = this.getExitItemByName(itemName);
		}
		return o_item;
	}

	public Player getPlayerByName(String playerName) {
		boolean Found = false;
		Player o_player = null;
		Enumeration<Player> e = players.elements();
		while (e.hasMoreElements() && !Found) {
			Player player = (Player) e.nextElement();
			if (player.getPlayerName().equals(playerName)) {
				o_player = player;
				Found = true;
			}
		}
		return o_player;
	}

	public Vector<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Vector<Player> players) {
		this.players = players;
	}

	public Item checkBlocking(String direction) {
		Item blocking = null;

		Enumeration<Item> ei = items.elements();
		while (ei.hasMoreElements() && blocking == null) {
			Item item = (Item) ei.nextElement();
		 //if ((item.getBlocking().equals(direction)) &&
		//	 (!item.getIsOpen())){
			if ((item.getBlocking().equals(direction))) {
				blocking = item;
			}
		}

		return blocking;
	}

	public void initialize() {
		if (respawnMinutes != 0){
			this.respawnInit();
		}
		Enumeration<Player> e = players.elements();
		while (e.hasMoreElements()) {
			Player player = (Player) e.nextElement();
			player.setCurrentRoom(this);
			if (player instanceof Mob) {
				//not sure if it's more efficient to create a Mob variable or not.
				((Mob) player).respawnInit();
				((Mob)player).setRespawnRoom(this);
			}
		}

	}
	
	@Override
	public boolean respawnOnce() {
		return false;
	}
	
    public void respawnInit(){
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(os);
        encoder.writeObject(this.getItems());
        encoder.close();
        respawnItemsCopy = os.toByteArray();
        // if this isn't in the respawn list, add it.
        if (!(World.getArea().getRespawnList().contains(this))){ 
			this.setRespawnTimeout(respawnMinutes);
			World.getArea().addRespawn(this);
			
		}
    }

    @SuppressWarnings("unchecked")
	public void respawn(){
        //this.loadFromFile(this.filename);
    	if (respawnItemsCopy != null){
    		ByteArrayInputStream is = new ByteArrayInputStream(respawnItemsCopy);
    		XMLDecoder decoder = new XMLDecoder(is);
    		this.setItems((Vector<Item>) decoder.readObject());
    		this.setRespawnTimeout(respawnMinutes);
    		for (Item item : this.getItems()){
    			item.setItemContainer(this);
    		}
    	} else {
    		System.err.println("Room respawn copy not initialized!");
    	}
    }
    
	public void initializeExits() {
		northRoom = null;
		southRoom = null;
		eastRoom = null;
		westRoom = null;
		upRoom = null;
		downRoom = null;

		if (northExit != "") {
			if (northRoom == null) { // no longer need these checks, we know the
										// room is null.
				northRoom = World.getArea().getRoomByFilename(northExit);
				// System.out.println("in initializeExits northRoom:"+eastExit);
			}
		}
		if (southExit != "") {
			if (southRoom == null) {
				southRoom = World.getArea().getRoomByFilename(southExit);
				// System.out.println("in initializeExits southRoom:"+eastExit);
			}
		}
		if (eastExit != "") {
			if (eastRoom == null) {
				eastRoom = World.getArea().getRoomByFilename(eastExit);
				// System.out.println("in initializeExits eastRoom:"+eastExit);
			}
		}
		if (westExit != "") {
			if (westRoom == null) {
				westRoom = World.getArea().getRoomByFilename(westExit);
				// System.out.println("in initializeExits westRoom:"+eastExit);
			}
		}
		if (upExit != "") {
			if (upRoom == null) {
				upRoom = World.getArea().getRoomByFilename(upExit);
			}
		}
		if (downExit != "") {
			if (downRoom == null) {
				downRoom = World.getArea().getRoomByFilename(downExit);
			}
		}
		// create a better room management system.
		// if (northRoom == null && southRoom == null && eastRoom == null &&
		// westRoom == null && upRoom == null && downRoom == null){
		// rooms.remove(this); // if you aren't connected to anything, remove.
		// }
	}

	public boolean moveUser(Player p, String direction) {
		String opposingDir = "";
		String oppositeBlockingDir = "";
		Room o_room = null;
		Item i = null;
		boolean result = false;

		System.out.println("in move user");
		i = this.checkBlocking(direction);
		if (i == null || i.getIsOpen()) {
			if (i != null && i.getAutoClosing()) {
				i.setIsOpen(false);
				p.broadcast("The " + i.getItemName()
						+ " closes behind you.\r\n");
			}

			if (direction.equalsIgnoreCase("north") || direction.equalsIgnoreCase("n")) {
				opposingDir = "the south";
				oppositeBlockingDir = "south";
				// if (northExit != "") {
				// if (northRoom == null) {
				// northRoom = getRoomByFilename(northExit);
				// }
				o_room = northRoom;
				// }
			}
			if (direction.equalsIgnoreCase("south") || direction.equalsIgnoreCase("s")) {
				opposingDir = "the north";
				oppositeBlockingDir = "north";
				// if (southExit != "") {
				// if (southRoom == null) {
				// southRoom = getRoomByFilename(southExit);
				// }
				o_room = southRoom;
				// }
			}
			if (direction.equalsIgnoreCase("east") || direction.equalsIgnoreCase("e")) {
				opposingDir = "the west";
				oppositeBlockingDir = "west";
				// if (eastExit != "") {
				// if (eastRoom == null) {
				// eastRoom = getRoomByFilename(eastExit);
				// }
				o_room = eastRoom;
				// }
			}
			if (direction.equalsIgnoreCase("west") || direction.equalsIgnoreCase("w")) {
				opposingDir = "the east";
				oppositeBlockingDir = "east";
				// if (westExit != "") {
				// if (westRoom == null) {
				// westRoom = getRoomByFilename(westExit);
				// }
				o_room = westRoom;
				// }
			}
			if (direction.equalsIgnoreCase("up") || direction.equalsIgnoreCase("u")) {
				opposingDir = "below";
				oppositeBlockingDir = "down";
				// if (upExit != "") {
				// if (upRoom == null) {
				// upRoom = getRoomByFilename(upExit);
				// }
				o_room = upRoom;
				// }
			}
			if (direction.equalsIgnoreCase("down") || direction.equalsIgnoreCase("d")) {
				opposingDir = "above";
				oppositeBlockingDir = "up";
				// if (downExit != "") {
				// if (downRoom == null) {
				// downRoom = getRoomByFilename(downExit);
				// }
				o_room = downRoom;
				// }
			}
		}
		if (i != null && o_room == null) {
			p.broadcast(i.getItemName() + " blocks your exit!\r\n");
			System.out.println("first");
		}
		if (o_room != null) {
			i = o_room.checkBlocking(oppositeBlockingDir);
			if (i == null || i.getIsOpen()) {
				if (i != null && i.getAutoClosing()) {
					i.setIsOpen(false);
					p.broadcast("The " + i.getItemName()
							+ " closes behind you.\r\n");
				}
				this.removePlayer(p);
				o_room.addPlayer(p);
				this.chat(p.getPlayerName() + " exited " + direction + "\r\n");
				o_room.chat(p.getPlayerName() + " entered from " + opposingDir
						+ "\r\n");
				result = true;
			} else if (i != null && !i.getIsOpen()) {
				System.out.println("second");
				p.broadcast(i.getItemName() + " blocks your exit!\r\n");
			}
		} else if (i == null) {
			p.broadcast("cannot go this way!");
		}

		
	    Vector<Player> tempPlayerVector = new Vector<Player>();
	    tempPlayerVector.addAll(players);
		Enumeration<Player> e = tempPlayerVector.elements();
		while (e.hasMoreElements()) {
			Player fp = (Player) e.nextElement();
			if (fp.getFollowing().equalsIgnoreCase(p.getPlayerName())) {
				fp.move(direction);
			}
		}
		return result;
	}

	public String getRoomFilename() {
		return filename;
	}

	public void setRoomFilename(String newFileName) {
		filename = newFileName;
	}

	public String getRoomDescription() {
		String temp = "";
		String roomDescription;
		roomDescription = description;
		roomDescription = roomDescription + "Items:";
		Enumeration<Item> e = items.elements();
		while (e.hasMoreElements()) {
			Item i = (Item) e.nextElement();
			if (!(i.getIsInvisable())) {
				roomDescription = roomDescription + i.getItemName();
				if (e.hasMoreElements()) {
					roomDescription += ",";
				}
			}
		}
		temp = getExitItemName(northRoom, "south");
		if (!temp.equals("")) {
			roomDescription = roomDescription + " " + temp;
		}
		temp = getExitItemName(southRoom, "north");
		if (!temp.equals("")) {
			roomDescription = roomDescription + " " + temp;
		}
		temp = getExitItemName(eastRoom, "west");
		if (!temp.equals("")) {
			roomDescription = roomDescription + " " + temp;
		}
		temp = getExitItemName(westRoom, "east");
		if (!temp.equals("")) {
			roomDescription = roomDescription + " " + temp;
		}
		temp = getExitItemName(upRoom, "down");
		if (!temp.equals("")) {
			roomDescription = roomDescription + " " + temp;
		}
		temp = getExitItemName(downRoom, "up");
		if (!temp.equals("")) {
			roomDescription = roomDescription + " " + temp;
		}
		roomDescription = roomDescription + "\r\n";
		return roomDescription;
	}

	public void addPlayer(Player p) {
		players.addElement(p);
		p.setCurrentRoom(this);
		this.performTriggeredAction("ENTRANCE", p);
	}

	public void removePlayer(Player player) {
		// System.out.println("called remove_player");
		if (players.contains(player)) {
			players.removeElement(player);
			// System.out.println("player found and removed");
		} else {
			System.out.println("User not found");
		}
	}

	public void chat(String message) {
		Enumeration<Player> e = players.elements();
		while (e.hasMoreElements()) {
			Player p = (Player) e.nextElement();
			p.broadcast(message);
		}
	}

	public void prepForSave() {
		Enumeration<Player> e = players.elements();
		while (e.hasMoreElements()) {
			Player p = (Player) e.nextElement();
			if (p instanceof User) {
				p.quit();
				//players.remove(p);
			}else {
				((Mob)p).setRespawnCopy(null);
			}
		}
	}

	public Player getRandomPlayer(Player p) {
		Enumeration<Player> e = players.elements();
		Vector<Player> pv = new Vector<Player>(); // vector to hold possible
													// result players
		Player t, r = null;
		while (e.hasMoreElements()) {
			t = (Player) e.nextElement();
			if ((t != p) && (t instanceof User)) {
				// if this isn't the player requesting a random player and
				// it's classname is the same as classname, or no classname was
				// passed.
				pv.add(t); // add t to player vector
			}
		}
		if (pv.size() == 1){
			r = (Player) pv.elementAt(0);
		}
		else if (pv.size() > 0) {
			int roll = random.nextInt(pv.size()); // random number
			r = (Player) pv.elementAt(roll-1);
		}
		return r;
	}

	public void echo(String msg, int str) {
		chat(msg);
		if (northRoom != null) {
			northRoom.transmitSound(msg, "north", str, str, true);
		}
		if (southRoom != null) {
			southRoom.transmitSound(msg, "south", str, str, true);
		}
		if (westRoom != null) {
			westRoom.transmitSound(msg, "west", str, str, true);
		}
		if (eastRoom != null) {
			eastRoom.transmitSound(msg, "east", str, str, true);
		}
		if (upRoom != null) {
			upRoom.transmitSound(msg, "up", str, str, true);
		}
		if (downRoom != null) {
			downRoom.transmitSound(msg, "down", str, str, true);
		}

	}

	/*
	 * this function got a bit out of hand. Consider breaking it up, so there
	 * isn't so much code repeated in the directions.
	 */
	public void transmitSound(String msg, String direction, int distance,
			int strength, boolean echo) {
		String opp_dir = "";
		String str_msg = "";
		strength = strength - 1;
		/*
		 * if (echo){ System.out.println(this.getRoomFilename() + " " +
		 * direction + " " + String.valueOf(distance) + " - " +
		 * String.valueOf(strength) + " = " + String.valueOf(distance -
		 * strength));
		 * 
		 * } else { System.out.println(this.getRoomFilename() + " " + direction
		 * + " " + String.valueOf(strength)); }
		 */
		if (direction.equals("up")) {
			opp_dir = " from below";
			if (upRoom != null) {
				if (strength > 0) {
					upRoom.transmitSound(msg, "up", distance, strength, false);
				}
			}
		}
		if (direction.equals("down")) {
			opp_dir = " from above";
			if (downRoom != null) {
				if (strength > 0) {
					downRoom.transmitSound(msg, "down", distance, strength,
							false);
				}
			}
		}
		if (direction.equals("north")) {
			opp_dir = " to the south";
			if (northRoom != null) {
				if ((strength > 0) && echo) {
					northRoom.transmitSound(msg, "north", distance, strength,
							true);
					if ((eastRoom != null) && (strength > 0)) {
						eastRoom.transmitSound(msg, "east", distance,
								(distance - strength), false);
					}
					if ((westRoom != null) && (strength > 0)) {
						westRoom.transmitSound(msg, "west", distance,
								(distance - strength), false);
					}
				} else if (strength > 0) {
					northRoom.transmitSound(msg, "north", distance, strength,
							false);
				}
			}
		}
		if (direction.equals("south")) {
			opp_dir = " to the north";
			if (southRoom != null) {
				if ((strength > 0) && echo) {
					southRoom.transmitSound(msg, "south", distance, strength,
							true);
					if (eastRoom != null) {
						eastRoom.transmitSound(msg, "east", distance,
								(distance - strength), false);
					}
					if (westRoom != null) {
						westRoom.transmitSound(msg, "west", distance,
								(distance - strength), false);
					}
				} else if (strength > 0) {
					southRoom.transmitSound(msg, "south", distance, strength,
							false);
				}
			}
		}
		if (direction.equals("east")) {
			opp_dir = " to the west";
			if (eastRoom != null) {
				if ((strength > 0) && echo) {
					eastRoom.transmitSound(msg, "east", distance, strength,
							true);
					if (northRoom != null) {
						northRoom.transmitSound(msg, "north", distance,
								(distance - strength), false);
					}
					if (southRoom != null) {
						southRoom.transmitSound(msg, "south", distance,
								(distance - strength), false);
					}
				} else if (strength > 0) {
					eastRoom.transmitSound(msg, "east", distance, strength,
							false);
				}
			}
		}
		if (direction.equals("west")) {
			opp_dir = " to the east";
			if (westRoom != null) {
				if ((strength > 0) && echo) {
					westRoom.transmitSound(msg, "west", distance, strength,
							true);
					if (northRoom != null) {
						northRoom.transmitSound(msg, "north", distance,
								(distance - strength), false);
					}
					if (southRoom != null) {
						southRoom.transmitSound(msg, "south", distance,
								(distance - strength), false);
					}

				} else if (strength > 0) {
					westRoom.transmitSound(msg, "west", distance, strength,
							false);
				}
			}
		}
		str_msg = "You Hear";
		/*
		 * if ((distance - strength) < 2){ str_msg = "You hear "; } else if
		 * ((distance - strength) < 3){ str_msg = "From a distance you hear "; }
		 * else if ((distance - strength) < 5){ str_msg = "From far away hear ";
		 * }
		 */

		chat(str_msg + msg + opp_dir + "\r\n");
	}

	/*
	 * public void echo(String message, int volume){ volume--; Enumeration e =
	 * players.elements(); while (e.hasMoreElements()){ Player p =
	 * (Player)e.nextElement(); p.broadcast(message); } if (volume > 0){ if
	 * (northRoom != null and northRoom; southRoom; eastRoom; westRoom; upRoom;
	 * downRoom; } }
	 */
	/*
	 * public void pausePlayers(){ Enumeration e = players.elements(); while
	 * (e.hasMoreElements()){ Player p = (Player)e.nextElement();
	 * p.broadcast("Game is paused while saving...\r\n"); try { if
	 * (p.getIsMob()){ p.wait(); } } catch (InterruptedException ex) {
	 * ex.printStackTrace(); } }
	 * 
	 * }
	 * 
	 * public void unPausePlayers(){ Enumeration e = players.elements(); while
	 * (e.hasMoreElements()){ Player p = (Player)e.nextElement(); if
	 * (p.getIsMob()){ p.notify(); } p.broadcast("Done saving.\r\n"); }
	 * 
	 * }
	 */

	public void check(Player player) {
		String checkOutput = "";

		checkOutput = "Room Setting Checklist:\r\n";
		checkOutput += "Name = " + name + "\r\n";
		checkOutput += "description = " + description + "\r\n";
		checkOutput += "Filename = " + filename + "\r\n\r\n";
		checkOutput += "use 'set room name <name> to set name\r\n";
		checkOutput += "use 'set filename <filename> to set a new filename\r\n";
		checkOutput += "use 'set room description <description> to set a new room description \r\n";
		checkOutput += "use 'set room description+ <new description line> to add a line to description\r\n";
		player.broadcast(checkOutput);
	}

	public void menu(User u) throws MenuExitException {
		//boolean done = false;
		String menuString = "Configure Room:\r\n";
		menuString += "(1) Configure Room Name \r\n";
		menuString += "(2) Confifure Room Description \r\n";
		menuString += "(3) Load Room Description from file \r\n";
		menuString += "(4) Configure Room Filename \r\n";
		menuString += "(5) Configure Exits \r\n";
		menuString += "(6) Configure Mobs\r\n";
		menuString += "(7) Configure Items \r\n";
		menuString += "(8) Configure Actions \r\n";
		menuString += "(9) Configure Respawn Timeout \r\n";
		menuString += "(10) Configure Room Settings \r\n";		
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		// menuString += "(5) Configure Area Description \r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 10, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configRoomName(u);
				break;
			case 2:
				configRoomDescription(u);
				break;
			case 3:
				configAddDescriptionFromFile(u);
				break;
			case 4:
				configRoomFilename(u);
				break;
			case 5:
				configRoomExits(u);
				break;
			case 6: 
				configRoomMobs(u); 
				break;
			case 7: 
				configRoomItems(u); 
				break;
			case 8: 
				this.configActions(u);
				break;
			case 9:
				this.configRespawnTimeout(u);
				if (respawnMinutes != 0){
					this.respawnInit();
				}
				break;
			case 10: configItemSettings(u);break;
			}
		}
		u.broadcast("\r\nExiting Room Configuration Menu.\r\n\r\n");
	}

	public void configAddDescriptionFromFile(User u) throws MenuExitException{
		
		String menuString =  "Enter a filename (Example: room_description.txt)\r\n";
		       menuString += "GUM will try to locate the resource, and copy the desctiption to the Item.\r\n\r\n";
		       
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			  try {
				this.loadDescriptionFromFile(s.getResult());
				u.broadcast("Description Loaded.\r\n");
			} catch (FileNotFoundException e) {
				u.broadcast("File Not Found!\r\n");
			}
		}
	}
	
	public boolean configItemSettings(User u) throws MenuExitException{
		boolean done = false;
		int newSettingValue = 0;
		String settingMenuString = "Current Settings:\r\n";
		       settingMenuString += this.getSettingsAsString()+"\r\n";
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (newSettingName.equals("")){
			done = false;
		} else {
			newSettingValue = this.getSettingValue(u);
			this.setSetting(newSettingName, newSettingValue);
		}
		
		// create a list of settings for reference. 
		// explain that setting to zero = removal. 
		return done;
	}
	
	public String getSettingsAsString(){
        String settingString = "";

        Iterator<String> e = settings.keySet().iterator();
        while (e.hasNext()){
            String key = (String) e.next();
            settingString = settingString + key+","+settings.get(key)+";\r\n";
        }
        return settingString;
    }
	
	public String getSettingName(User u) throws MenuExitException {
		String result = "";
		String menuString = "Enter a setting name.\r\n\r\n";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			result = s.getResult();
		} 
		return result;
	}	
    
    public int getSettingValue(User u) throws MenuExitException{
    	int result = 0;
    	String menuString = "Enter a new setting value (a value of zero will delete the setting).\r\n\r\n";
    	       
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		result = (i.getResult());
    	}
    	return result;
    } 
	
    public void loadDescriptionFromFile(String filename) throws FileNotFoundException{

        File file = new File(filename);
        StringBuilder description = new StringBuilder();
        
        
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
            	description.append(scanner.nextLine()+"\r\n");
            }
            this.setDescription(description.toString());    
    }
	
    public boolean configRespawnTimeout(User u) throws MenuExitException{
    	String menuString = "Enter a new respawn timeout for this room.\r\n(example: '2' would be 'wait two minutes before respawning.)\r\n";
    	       menuString +="Current respawn timeout:"+this.getRespawnTimeout()+"\r\n";
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		respawnMinutes = i.getResult();
    		u.broadcast("Respawn timeout changed to:"+i.getResult()+"\r\n");
    	}else {
    		u.broadcast("Respawn timeout unchanged.\r\n");
    	}
    	return done;
    } 
	
	public void configActions(User u) throws MenuExitException { 
		
		String menuString = "Configure Room Actions:\r\n";
		menuString += "(1) Configure a Triggered Action\r\n";
		menuString += "(2) Remove a Triggered Action\r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 2, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1: 
				this.configTriggeredAction(u); 
				break;
			case 2: 
				this.configRemoveTriggeredAction(u); 
				break;
			}
		}
		u.broadcast("\r\nExiting Room Actions Configuration Menu.\r\n\r\n");
	}	
	
	public void configRemoveTriggeredAction(User u) throws MenuExitException{	
		
		String menuString =  "Enter a Triggered action from the list below to remove it.\r\n";
		int count = 0;
		ArrayList<String> keys = new ArrayList<String>(this.getTriggerActions().keySet());
		Collections.sort(keys);
		
		for (String s : keys){
			menuString += "("+count+") "+s+"\r\n";
			count++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, 100, 0);
	       if (p.display()){
	    	   if (this.getTriggerActions().remove(keys.get(p.getResult())) != null){
	    		   u.broadcast("Action removed.\r\n");
	    	   }else {
	    		   u.broadcast("Action not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Actions unchanged.\r\n");
	       }
	}
	
	public void configTriggeredAction(User u) throws MenuExitException{

		String menuString = "Configure Room Actions:\r\n";
		menuString += "(1) Config Entrance Action \r\n";
		menuString += "(2) Config Look Action \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		// menuString += "(5) Configure Area Description \r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 2, 1);
		while (p.display()) {
			switch (p.getResult()) {
	
			case 1: 
				if (this.getTriggerActions().get("ENTRANCE") == null){
					Action newAction = Action.configAddAction(u);
					newAction.setRange(EffectRange.USER);
					this.getTriggerActions().put("ENTRANCE",newAction);
				}
				this.getTriggerActions().get("ENTRANCE").menu(u);
				break;
			case 2: 
				if (this.getTriggerActions().get("LOOK") == null){
					Action newAction = Action.configAddAction(u);
					newAction.setRange(EffectRange.USER);
					this.getTriggerActions().put("LOOK",newAction);
				}
				this.getTriggerActions().get("LOOK").menu(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Room Actions Configuration Menu.\r\n\r\n");
	}
	
	public boolean configRoomName(User u) throws MenuExitException {
		String menuString = "Enter a new room name.\r\n";
		menuString += "Current room name:" + this.getName() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			this.setName(s.getResult());
			u.broadcast("New room name:\r\n" + s.getResult() + "\r\n");
		} else {
			u.broadcast("Room name unchanged. \r\n");
		} 
		return done;
	}

	public boolean configRoomDescription(User u) throws MenuExitException {
		String menuString = "Enter a new room description.\r\n";
		menuString += "Current room description:\r\n"
				+ this.getRoomDescription() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Description:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the description, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		this.setDescription(result);
		return done;
	}

	public boolean configRoomFilename(User u) throws MenuExitException {
		String menuString = "Enter a new room file name.(File names must be unique for each room in the game!)\r\n";
		menuString += "Current room file name:" + this.getFilename() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			this.setFilename(s.getResult());
			u.broadcast("New room File name:\r\n" + s.getResult() + "\r\n");
		} else {
			u.broadcast("Room File name unchanged. \r\n");
		}
		return done;
	}

	public boolean configRoomExits(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure Room Exits:\r\n";
		menuString += "(1) Add an Exit to a new room. \r\n";
		menuString += "(2) Remove Exit \r\n";
		menuString += "(3) Add an Exit to an existing room.\r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configAddExit(u);
				break;
			case 2: done = configRemoveExit(u); break;
			case 3: configCreateLink(u);	
			}
		}
		return done;
	}

	public boolean configCreateLink(User u) throws MenuExitException {

		String newExitDir = "";
		boolean recievedDirection, recievedFileName = false;

		String exits = "";
		for (String roomName : this.getExitList()) {
			exits += roomName + " ";
		}

		String menuString = "Enter the direction of the new exit.\r\n(north,south,east,west,up or down)\r\n";
			   menuString += "NOTE: This will only create an exit in ONE direction. \r\n";
			   menuString += "You must manually configure the exit from the destination back to this room.";
			   menuString += "This is so you can create 1-way exits (example: A slide you can't climb back up). \r\n";
			   menuString += "\r\nCurrent exits from this room:" + exits + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		recievedDirection = s.display();

		if (recievedDirection) {
			newExitDir = s.getResult();
			menuString = "Enter a room file name, of the destination room:\r\n";

			s = new PromptForString(u, menuString);
			recievedFileName = s.display();

			if (recievedFileName) {
				this.createNewLink(s.getResult(), newExitDir, u);
			} else {
				u.broadcast("User cancelled,  exit not created. \r\n");
			}
		} else {
			u.broadcast("User cancelled, exit not created. \r\n");
		}
		return recievedDirection && recievedFileName;
	}
	
	public boolean configAddExit(User u) throws MenuExitException {

		String newExitDir = "";
		boolean recievedDirection, recievedFileName = false;

		String exits = "";
		for (String roomName : this.getExitList()) {
			exits += roomName + " ";
		}

		String menuString = "Enter the direction of the new room to be created.\r\n(north,south,east,west,up or down)\r\n";
		menuString += "\r\nCurrent exits from this room:" + exits + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		recievedDirection = s.display();

		if (recievedDirection) {
			newExitDir = s.getResult();
			menuString = "Enter a room file name for new exit.\r\n(File names must be unique for each room in the game!)\r\n";

			s = new PromptForString(u, menuString);
			recievedFileName = s.display();

			if (recievedFileName) {

				// TODO add validation here!!
				u.broadcast("\r\ndirection:" + newExitDir + "::"
						+ s.getResult());
				// this.createNewExit(newExitDir, s.getResult());
				this.createNewRoom(s.getResult(), newExitDir);
				if (World.getArea().getRoomByFilename(s.getResult()) != null) {
					u.broadcast("New exit created:" + newExitDir + " <"
							+ s.getResult() + ">");
				} else {
					u.broadcast("\r\nError: Exit not created!\r\n");
				}
			} else {
				u.broadcast("User cancelled,  exit not created. \r\n");
			}
		} else {
			u.broadcast("User cancelled, exit not created. \r\n");
		}
		return recievedDirection && recievedFileName;
	}

	public boolean configRemoveExit(User u) throws MenuExitException{
		boolean done = false;
		int count = 0;
		Vector<String> exits = this.getExitList();
		String menuString = "\r\nChoose an exit from the list below to remove.\r\n";
		
		for (String exitName : exits) {
			menuString += "("+count+")"+exitName + "\r\n";
			count++;
		}
		menuString += "\r\nType 'exit' to return to menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString,(exits.size()-1), 0);
		if (p.display()) {
			this.destroyExit(exits.elementAt(p.getResult()));
		}
		return done;
	}
	
	public boolean configRoomMobs(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure Room Mobs:\r\n";
		menuString += "(1) Configure existing mobs. \r\n";
		menuString += "(2) Add a new mob. \r\n";
		menuString += "(3) Remove a Mob. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configMob(u);
				break;
			case 2: 
				Mob newMob = Mob.configAddMob(u);
				if (newMob != null){
					newMob.setCurrentRoom(this);
					this.players.add(newMob);
				}
				break;
			case 3: 
				this.configMobDelete(u); 
				break;
			}
		}
		return done;
	}	
	
	public void configMob(User u) throws MenuExitException{
		
		Vector <Mob> mobs = new Vector<Mob>();
		int count = 0;
		String menuString = "Choose a mob to configure:\r\n";
		
		for (Player p: this.getPlayers()){
			if (p instanceof Mob){
				menuString += "("+count+") "+p.getPlayerName()+"\r\n";
				mobs.add((Mob)p);
				count++;
			}
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, mobs.size()-1, 0);
		if (p.display()) {
			mobs.elementAt(p.getResult()).menu(u);
		}
	}
	
	public void configMobDelete(User u) throws MenuExitException{
		
		Vector <Mob> mobs = new Vector<Mob>();
		int count = 0;
		String menuString = "Choose a mob to delete:\r\n";
		
		for (Player p: this.getPlayers()){
			if (p instanceof Mob){
				menuString += "("+count+") "+p.getPlayerName()+"\r\n";
				mobs.add((Mob)p);
				count++;
			}
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, mobs.size()-1, 0);
		if (p.display()) {
			players.remove(mobs.elementAt(p.getResult()));
			u.broadcast("Selected mob removed from room.\r\n");
		} else {
			u.broadcast("Room unchanged.\r\n");
		}
	}	
	
/*	public void configMobAdd(User u){
		String menuString =  "Enter a filename (Example: zombie.xml) or a JAVA Class name (Example:BasicMob)\r\n";
		       menuString += "GUM will try to locate the resource, and add the mob to the current room.\r\n\r\n";
		       
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			if (createNewMob(s.getResult(), "DEFAULT")){
				u.broadcast("New mob added with name 'DEFAULT'.\r\n");
			} else {
				u.broadcast("Failed to locate resource. Check input and retry. \r\n");
			}
		}
	}
*/
	
	public boolean configRoomItems(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure Room Items:\r\n";
		menuString += "(1) Configure existing items. \r\n";
		menuString += "(2) Add a new item. \r\n";
		menuString += "(3) Remove an item. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configItem(u);
				break;
			case 2: 
				Item i = ItemBase.configAddItem(u);
				if (i != null){
					this.addToInventory(i);
				}
				break;
			case 3: 
				this.configItemDelete(u); 
				break;
			}
		}
		return done;
	}	
	
	public void configItem(User u) throws MenuExitException{
		int count = 0;
		String menuString = "Choose an item to configure:\r\n";
		Vector<Item> items = this.getItems();
		
		for (Item i: items){
			menuString += "("+count+") "+i.getItemName()+"\r\n";
			count++;			
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger i = new PromptForInteger(u, menuString, items.size()-1, 0);
		if (i.display()) {
			items.elementAt(i.getResult()).menu(u);
		}
	}
	
	public void configItemDelete(User u) throws MenuExitException{
		
		int count = 0;
		String menuString = "Choose an item to delete:\r\n";
		
		for (Item i: this.getItems()){
				menuString += "("+count+") "+i.getItemName()+"\r\n";
				count++;
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, items.size()-1, 0);
		if (p.display()) {
			items.remove(p.getResult());
			u.broadcast("Selected item removed from room.\r\n");
		} else {
			u.broadcast("Room unchanged.\r\n");
		}
	}	
	
/*	public void configItemAdd(User u){
		String menuString =  "Enter a filename (Example: door.xml) or a JAVA Class name (Example:gum.items.ItemBase)\r\n";
		       menuString += "GUM will try to locate the resource, and add the item to the current room.\r\n\r\n";
		       
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			if (createNewItem(s.getResult(), "DEFAULT")){
				u.broadcast("New mob added with name 'DEFAULT'.\r\n");
			} else {
				u.broadcast("Failed to locate resource. Check input and retry. \r\n");
			}
		}
	}	
*/
/*	
	public void configAddItem(User u) {
		String menuString = "Add a new item\r\n";
		menuString += "(1) Basic \r\n";
		menuString += "(2) Advanced \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configBasicAddItem(u);
				break;
			case 2: 
				this.configAdvancedAddItem(u);
				break;
			}
		}
	}
*/	
			
/*	
	public void configBasicAddItem(User u){
		boolean success = false;
		
		String menuString = "Choose a new basic item below:\r\n";
		menuString += "(1) Basic Item \r\n";
		menuString += "(2) XXXXXX \r\n";
		menuString += "(3) XXXXXX \r\n";
		menuString += "(4) XXXXXX \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				success = this.createNewItem("gum.items.ItemBase", "DEFAULT");
				break;
			case 2: 
				System.out.println("User chose heal.");
				break;
			case 3: 
				System.out.println("User chose notify.");
				break;
			case 4: 
				System.out.println("User chose modify.");;
				break;
			}
		}
		if (success){
			u.broadcast("New Item Created!\r\n");
		}else {
			u.broadcast("Item Creation failed\r\n");
		}
	}		
*/	
		
/*	
	public void configAdvancedAddItem(User u){
		String menuString =  "Enter a filename (Example: door.xml) or a JAVA Class name (Example:gum.items.ItemBase)\r\n";
		       menuString += "GUM will try to locate the resource, and add the item to the current room.\r\n\r\n";
		       
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			if (createNewItem(s.getResult(), "DEFAULT")){
				u.broadcast("New Item added with name 'DEFAULT'.\r\n");
			} else {
				u.broadcast("Failed to locate resource. Check input and retry. \r\n");
			}
		}
	}		
*/	
	
/*	public boolean createNewItem(String fileName, String name) {
		boolean result = false;
		Item i = ObjectFactory.CreateItem(fileName);
		if (i != null) {
			i.setName(name);
			this.addItem(i);
			result = true;
		} 
		return result;
	}
*/

/*	public boolean createNewMob(String fileName, String name) {
		Mob m = ObjectFactory.CreateMob(fileName);
		boolean result = false;
		if (m != null) { // needs a better way to handle a bad filename
			m.setPlayerName(name);
			this.addPlayer(m);
			result = true;
		} 
		return result;
	}
*/
	public void createNewExit(String exitDir, String fileName) {
		// fileName = "image/"+fileName;
		System.out.println("creating exit dir " + exitDir + " from "
				+ this.getRoomFilename() + " to " + fileName);

		Room r = World.getArea().getRoomByFilename(fileName); // create a new
																// room
		if (r == null) {
			// r.createNewLink(this.getRoomFilename(),opposingExit);
			System.out.println("room not found");
		} else {
			System.out.println("found:" + r.getRoomFilename());

			if (exitDir.equals("north")) {
				this.northExit = fileName;
				r.setSouthExit(this.getRoomFilename());
			}
			if (exitDir.equals("south")) {
				this.southExit = fileName;
				r.setNorthExit(this.getRoomFilename());
			}
			if (exitDir.equals("east")) {
				this.eastExit = fileName;
				r.setWestExit(this.getRoomFilename());
			}
			if (exitDir.equals("west")) {
				this.westExit = fileName;
				r.setEastExit(this.getRoomFilename());
			}
			if (exitDir.equals("up")) {
				this.upExit = fileName;
				r.setDownExit(this.getRoomFilename());
			}
			if (exitDir.equals("down")) {
				this.downExit = fileName;
				r.setUpExit(this.getRoomFilename());
			}

			this.initializeExits(); // initialize our room exits
			r.initializeExits();
		}
	}

	public Room getNorthRoom() {
		return northRoom;
	}

	public void setNorthRoom(Room northRoom) {
		this.northRoom = northRoom;
	}

	public Room getSouthRoom() {
		return southRoom;
	}

	public void setSouthRoom(Room southRoom) {
		this.southRoom = southRoom;
	}

	public Room getEastRoom() {
		return eastRoom;
	}

	public void setEastRoom(Room eastRoom) {
		this.eastRoom = eastRoom;
	}

	public Room getWestRoom() {
		return westRoom;
	}

	public void setWestRoom(Room westRoom) {
		this.westRoom = westRoom;
	}

	public Room getUpRoom() {
		return upRoom;
	}

	public void setUpRoom(Room upRoom) {
		this.upRoom = upRoom;
	}

	public Room getDownRoom() {
		return downRoom;
	}

	public void setDownRoom(Room downRoom) {
		this.downRoom = downRoom;
	}

	public void createNewLink(String fileName, String exitDir,User u) {
		System.out.println("creating link dir " + exitDir + " from "
				+ this.getRoomFilename() + " to " + fileName);
		// String opposingExit = "";

		if (exitDir.equals("north")) {
			this.northExit = fileName;
			// opposingExit = "south";
		}
		if (exitDir.equals("south")) {
			this.southExit = fileName;
			// opposingExit = "north";
		}
		if (exitDir.equals("east")) {
			this.eastExit = fileName;
			// opposingExit = "west";
		}
		if (exitDir.equals("west")) {
			this.westExit = fileName;
			// opposingExit = "east";
		}
		if (exitDir.equals("up")) {
			this.upExit = fileName;
			// opposingExit = "down";
		}
		if (exitDir.equals("down")) {
			this.downExit = fileName;
			// opposingExit = "up";
		}

		Room r = World.getArea().getRoomByFilename(fileName); // create a new
																// room
		if (r != null) {
			u.broadcast("Created exit to "+ r.getRoomFilename()+"\r\n");
			//r.createNewLink(this.getRoomFilename(),opposingExit);
		} else {
			u.broadcast("Target room not found, exit not created");
		}

		this.initializeExits(); // initialize our new room
	}

	public void createNewarea(int x, int y, String areaName) {
		int i, j = 0;
		Room r = null;

		Vector<Room> newRoomList = new Vector<Room>();

		for (i = 0; i < x; i++) {
			for (j = 0; j < y; j++) {
				System.out.print("make room" + String.valueOf(i) + '-'
						+ String.valueOf(j));
				r = new Room();
				r.init();
				r.setRoomFilename("image/" + areaName + String.valueOf(i) + '-'
						+ String.valueOf(j));
				r.setDescription(areaName + String.valueOf(i) + '-'
						+ String.valueOf(j));
				if (i < (x - 1)) {
					r.setNorthExit("image/" + areaName + String.valueOf(i + 1)
							+ '-' + String.valueOf(j));
					System.out.print("; north:" + String.valueOf(i + 1) + '-'
							+ String.valueOf(j));
				}
				if (i > 0) {
					r.setSouthExit("image/" + areaName + String.valueOf(i - 1)
							+ '-' + String.valueOf(j));
					System.out.print("; south:" + String.valueOf(i - 1) + '-'
							+ String.valueOf(j));
				}
				if (j > 0) {
					r.setWestExit("image/" + areaName + String.valueOf(i) + '-'
							+ String.valueOf(j - 1));
					System.out.print("; west:" + String.valueOf(i) + '-'
							+ String.valueOf(j - 1));
				}
				if (j < (y - 1)) {
					r.setEastExit("image/" + areaName + String.valueOf(i) + '-'
							+ String.valueOf(j + 1));
					System.out.print("; east:" + String.valueOf(i) + '-'
							+ String.valueOf(j + 1));
				}
				if (j == 0 && i == 0) {
					r.setSouthExit(this.getRoomFilename());
					this.setNorthExit(r.getRoomFilename());
				}
				System.out.println("");
				newRoomList.add(r);
				World.getArea().getRooms().add(r);
			}
		}
		Enumeration<Room> e = newRoomList.elements();
		while (e.hasMoreElements()) {
			Room rm = (Room) e.nextElement();
			rm.initializeExits();
		}
		this.initializeExits();
	}

	public void createNewRoom(String fileName, String exitDir) {
		System.out.println("in createNewExit");
		String opposingExit = "";
		// fileName = "image/"+fileName;

		if (exitDir.equals("north")) {
			this.northExit = fileName;
			opposingExit = "south";
		}
		if (exitDir.equals("south")) {
			this.southExit = fileName;
			opposingExit = "north";
		}
		if (exitDir.equals("east")) {
			this.eastExit = fileName;
			opposingExit = "west";
		}
		if (exitDir.equals("west")) {
			this.westExit = fileName;
			opposingExit = "east";
		}
		if (exitDir.equals("up")) {
			this.upExit = fileName;
			opposingExit = "down";
		}
		if (exitDir.equals("down")) {
			this.downExit = fileName;
			opposingExit = "up";
		}

		Room r = new Room(this, opposingExit); // create a new room
		r.setRoomFilename(fileName); // set room's filename
		System.out.println("room created:" + fileName + exitDir);

		this.initializeExits(); // initialize our new room
	}
	
	public void performTriggeredAction(String trigger,Player p){
		//rooms performing on players only have a target. 
		//so action ranges can only be 'target'. 
		if (p instanceof User){//only allow players to trigger actions
			Action triggeredAction = this.getTriggerActions().get(trigger);
			if ( triggeredAction != null){
				ActionHeader header = new ActionHeader(p,null,null,null);
				triggeredAction.perform(header);
			}
		}
	}

	public void destroyExit(String exitDir) {

		if (exitDir.equals("north")) {
			if (northRoom != null) {
				this.northExit = "";
				this.northRoom.setSouthExit("");
				this.northRoom.initializeExits();
				this.northRoom = null;
			}
		}
		if (exitDir.equals("south")) {
			if (southRoom != null) {
				this.southExit = "";
				this.southRoom.setNorthExit("");
				this.southRoom.initializeExits();
				this.northRoom = null;
			}
		}
		if (exitDir.equals("east")) {
			if (eastRoom != null) {
				this.eastExit = "";
				this.eastRoom.setWestExit("");
				this.eastRoom.initializeExits();
				this.eastRoom = null;
			}
		}
		if (exitDir.equals("west")) {
			if (westRoom != null) {
				this.westExit = "";
				this.westRoom.setEastExit("");
				this.westRoom.initializeExits();
				this.westRoom = null;
			}
		}
		if (exitDir.equals("up")) {
			if (upRoom != null) {
				this.upExit = "";
				this.upRoom.setDownExit("");
				this.upRoom.initializeExits();
				this.upRoom = null;
			}
		}
		if (exitDir.equals("down")) {
			if (downRoom != null) {
				this.downExit = "";
				this.downRoom.setUpExit("");
				this.downRoom.initializeExits();
				this.downRoom = null;
			}
		}
		this.initializeExits();
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public void setDescription(String newDescription) {
		description = newDescription;
	}

	public void addToDescription(String newDescription) {
		System.out.println("adding to description");

		description += newDescription;
	}

	public void setNorthExit(String roomName) {
		northExit = roomName;
	}

	public void setSouthExit(String roomName) {
		southExit = roomName;
	}

	public void setEastExit(String roomName) {
		eastExit = roomName;
	}

	public void setWestExit(String roomName) {
		westExit = roomName;
	}

	public void setUpExit(String roomName) {
		upExit = roomName;
	}

	public void setDownExit(String roomName) {
		downExit = roomName;
	}

	public void destroyRoom(String del_rm_file) {
		Enumeration<Room> e = World.getArea().getRooms().elements();
		while (e.hasMoreElements()) {
			Room room = (Room) e.nextElement();
			if (room.upExit.equals(del_rm_file)) {
				room.upExit = "";
				room.upRoom = null;
				room.initializeExits();
			}
			if (room.downExit.equals(del_rm_file)) {
				room.downExit = "";
				room.downRoom = null;
				room.initializeExits();
			}
			if (room.northExit.equals(del_rm_file)) {
				room.northExit = "";
				room.northRoom = null;
				room.initializeExits();
			}
			if (room.southExit.equals(del_rm_file)) {
				room.southExit = "";
				room.southRoom = null;
				room.initializeExits();
			}
			if (room.eastExit.equals(del_rm_file)) {
				room.eastExit = "";
				room.eastRoom = null;
				room.initializeExits();
			}
			if (room.westExit.equals(del_rm_file)) {
				room.westExit = "";
				room.westRoom = null;
				room.initializeExits();
			}
		}
	}

	public static void addWorldDescriptor(String key, String descriptor) {
		WorldReplaceMap.put(key, descriptor);
	}

	public static boolean removeWorldDescriptor(String key) {
		boolean result = true;

		if (WorldReplaceMap.remove(key) == null) {
			result = false;
		}
		return result;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getRespawnMinutes() {
		return respawnMinutes;
	}

	public void setRespawnMinutes(int respawnMinutes) {
		this.respawnMinutes = respawnMinutes;
	}

	public Vector<Item> getItems() {
		return items;
	}

	public void setItems(Vector<Item> items) {
		this.items = items;
	}

	public static HashMap<String, String> getWorldReplaceMap() {
		return WorldReplaceMap;
	}

    public void setSetting(String setting, int value){
        Integer intValue = new Integer(value);
        settings.put(setting,intValue); // should create new, or replace
    }

    public int getSetting(String setting){
        Integer returnValue = null;
        returnValue = (Integer)settings.get(setting);
        if (returnValue == null){
            returnValue = new Integer(0);
        }
        return returnValue.intValue();
    }
	
	public static void setWorldReplaceMap(
			HashMap<String, String> worldReplaceMap) {
		WorldReplaceMap = worldReplaceMap;
	}

	public String getDescription() {
		return description;
	}

	public String getNorthExit() {
		return northExit;
	}

	public String getSouthExit() {
		return southExit;
	}

	public String getEastExit() {
		return eastExit;
	}

	public String getWestExit() {
		return westExit;
	}

	public String getUpExit() {
		return upExit;
	}

	public String getDownExit() {
		return downExit;
	}

	public HashMap<String, Integer> getSettings() {
		return settings;
	}

	public void setRespawnTimeout(int respawnTimeout) {
		this.respawnTimeout = respawnTimeout;
	}

	public int getRespawnTimeout() {
		return respawnTimeout;
	}

	public void setSettings(HashMap<String, Integer> settings) {
		this.settings = settings;
	}

	public void setTriggerActions(HashMap<String,Action> triggerActions) {
		this.triggerActions = triggerActions;
	}

	public HashMap<String,Action> getTriggerActions() {
		return triggerActions;
	}


	
}
