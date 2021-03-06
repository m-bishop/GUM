package gum;

//import gum.items.Item;
import gum.menus.MenuContainer;
import gum.menus.MenuExitException;
import gum.menus.MenuHandler;
import gum.menus.PromptForString;

import java.net.Socket;
import java.beans.XMLDecoder;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;

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

import java.util.*;

public class User extends Player implements MenuContainer{

	private Socket sock;
	private BufferedReader in;
	private DataOutputStream out;
	private String password = "";
	private String startRoom = "";
	private int timeoutCounter;

	private String menuBuff = "";

	public boolean inMenu = false;
	private Vector<UserMessage> messages = new Vector<UserMessage>();
	
	public Vector<UserMessage> getMessages() {
		return messages;
	}

	public void setMessages(Vector<UserMessage> messages) {
		this.messages = messages;
	}

	private static Vector<User> users = new Vector<User>(); // a vector of all
															// logged in users

	public User() {
		super();
		
	}

	public User(Socket s) throws IOException {
		// this.setSetting("Str",10);
		// this.setSetting("Ref",8);
		// this.setSetting("Int",10);
		super();
		this.setPlayerDescription("To set your character's desctiption, type 'set description'\r\n");
		this.sock = s;
		out = new DataOutputStream(
				new BufferedOutputStream(s.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));

	}

	public void run() {
		// setPlayerName( sock.getInetAddress ().toString ());
		String msg;
		if (this.login()) {
			// this.login();
			User.users.add(this);
			//this.getCurrentRoom().addPlayer(this);
			try {
				// broadcast ((getPlayerName()) + " has test1.");
				// handlers.addElement (this);
				UserParser parser = new UserParser();
				while (!this.isDead()) {
					msg = this.getResponse();
					if (msg == null) {
						IOException ex = new IOException();
						throw ex;
					}
					this.setCommand(msg);
					System.out.println("from user:" + msg);
					if (inMenu) {
						System.out
								.println("in menu, synchronizing and notifying.");
						synchronized (this) {
							this.menuBuff = msg;
							this.notify();
						}
					} else {
						parser.parsePlayerCommand(this, msg);
						sendUserPrompt();
					}

					// }
				}
			} catch (Exception ex) {
				System.out.println("Exception in main loop.");
				ex.printStackTrace();
			} finally {
				this.quit();
			}
		}
	}

	public String getResponse() {
		// add backspace handling here.
		String oString;
		try {
			System.out.println("in getResponse");
			oString = in.readLine();
			System.out.println(oString);
			oString = this.removeBackspace(oString);
			//oString = oString.replaceAll("[^a-zA-Z0-9 |\\/,`.?()-_'*!%]", "");
			this.setTimeoutCounter(0);
		} catch (IOException ex) {
			this.quit();
			return null;
		}
		return oString;
	}

	public String getMenuResponse() {
		// must be called from an outside thread!
		String oString = "";

		this.setTimeoutCounter(0);
		System.out.println("In menu Response.\r\n");
		synchronized (this) {
			try {
				this.inMenu = true;
				System.out.println("In menu Response.\r\n");
				wait();
				oString = this.menuBuff;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.inMenu = false;
		return oString;
	}

	public String removeBackspace(String msg) {
		String s = "";
		if (msg != null) {
			for (int i = 0; i < msg.length(); i++) {
				if (!(msg.charAt(i) == '\b')) { // if it's not a backspace, add
												// it
					s = s + msg.charAt(i);
				} else { // remove the character before the backspace
					if (s.length() > 0) {
						s = s.substring(0, s.length() - 1);
					}
				}
			}
		} else {
			this.quit();
		}
		return s;
	}

	//@SuppressWarnings("deprecation")
	public void quit() {
try {
		
		this.setDead(true); // notification for the battlehandler that you're no
							// longer in the battle.
		synchronized (this) {
			this.notify();
		}

		if (this.getPlayerName() != null) {
			broadcast(getPlayerName() + " has left.");
		}
		if (this.getCurrentRoom() != null){
			this.getCurrentRoom().removePlayer(this);
		}
		try {
			if (sock != null){
			sock.close();
			User.users.remove(this);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (this.getBattleHandler() != null) {
			this.getBattleHandler().removeFromBattle(this);
			this.setBattleHandler(null);
		}
		String userDir = World.getArea().getUserDir() + "/";
		String playerName = this.getPlayerName();
		
		this.save( userDir+playerName 
				+ ".xml");
		//TODO add gamestats back here once we make a new gamestats class
		//new ().createWebStats(); // update web stats
		//this.stop();
} catch (Exception e){
	System.out.println("exception in quit.");
}
	}

	public boolean login() { // this may need rewriting...
		boolean success = true;
		int retryCount = 0;
		String response;
		// String username;
		this.setPlayerName(null);

		this.broadcast(World.getArea().getAreaDescription());

		while (this.getPlayerName() == null && !sock.isClosed()) {
			retryCount++;
			if (retryCount >= 3){
				this.broadcast("Login Failed. Connection Terminated.\r\n");
				success = false;
				try {
					sock.close();
				} catch (IOException e) {
					// TODO Add to a system log
					e.printStackTrace();
				}
			} else {
			System.out.println("called getUserAndPass");
			response = this.getUserAndPass();
			if (response != null) {
				System.out.println("response:" + response);
				if (response.matches("loaded")) {
					System.out.println("in user loaded logic");
					// should add LoadUser, and not make getUserAndPass do it
					Room indexRoom = World.getArea().getRoomByFilename(this.getStartRoom());
					System.out.println("Start room = "+this.getStartRoom()+" found: "+indexRoom);
					if (indexRoom == null){
						indexRoom = World.getArea().getIndex();
					}
					indexRoom.addPlayer(this);
					this.look();
					
				} else if (response.equals("error")) {
					this.broadcast("User is already logged in!\r\n");
					this.setPlayerName(null);
				} else if (response.equals("badpass")){
					this.broadcast("Invalid Password\r\n");
					this.setPlayerName(null);
				} else {
					System.out.println("creating new user");
					if (this.createNewUser(response)){
						//this.setPlayerName(null);
						Room r = World.getArea().getIndex();
						if (r != null){
							r.addPlayer(this);
						} else {
							System.out.println("No index room found!");
						}
						this.look();
					}
				}
			}
		  }
		}
		this.setTimeoutCounter(0);
		return success;
	}

	protected boolean createNewUser(String username) {
		boolean result = false;
		String response;

		this.broadcast("create a new user?(Y/N)\n");
		response = this.getResponse();
		if (response.equalsIgnoreCase("Y")) { // make new user
			this.setPlayerName(username);
			this.setSettings(new HashMap<String,Integer>());
			this.setSetting("hitpoints", 100); // just here for the sake of
												// testing...change to some
												// system
			this.setSetting("maxhp", 100);
			this.setSetting("str", 10);
			this.setSetting("int", 10);
			this.setSetting("ref", 10);
			this.setSetting("dex", 10);
			//this.loadCharacterClass();
			while (!this.getNewPassword()) {
				this
						.broadcast("There was a problem with your password. Please re-try.\r\n");
			}
			
			if (World.getArea().getBaseClass() != null){
				new MenuHandler(this,World.getArea().getBaseClass());
			}
			//this.broadcast("Your new player has been created.\r\n");
			//this.look();
			// this.start();
			result = true;
		}
		else {
			result = false;
		}
		return result;
	}

	public boolean getNewPassword() {
		String pass1;
		String pass2;
		boolean result;

		this.broadcast("New Password:\r\n");
		pass1 = this.getResponse();
		this.broadcast("Re-type Password:\r\n");
		pass2 = this.getResponse();
		if (!pass1.equals("") && (pass1.equals(pass2))) {
			this.setPassword(pass1);
			result = true;
		} else {
			this.broadcast("passwords did not match!\r\n");
			result = false;
		}
		return result;
	}

	protected String getUserAndPass() {
		String response;
		String result = null;
		// FileHandler file = new FileHandler();

		this.broadcast("Username:\n");
		response = this.getResponse();
		if (response != null) {
			System.out.println("response:" + response);
			if (response.equals("set javascript true")) {
				System.out.println("got here");
				this.setjavascript(true);
				response = this.getResponse();
			}
			if (!response.matches("")) {
				response = response.replaceAll("[^a-zA-Z0-9]", "");
				this.getCurrentRoom();

				// String fileName = World.getArea().getUserDir() + "/" +
				// response + ".usr";
				String fileName = World.getArea().getUserDir() + "/" + response
						+ ".xml";
				this.loadUser(fileName);

				// Password = file.read_setting(fileName, "password", false);

				if (this.getPassword().equals("")) {
					return response;
				}
				this.broadcast("Password:\n");
				String username = response;
				response = this.getResponse();
				// this.setPassword(file.read_setting(fileName, "password",
				// false));
				// this.setPassword(Password);
				System.out.println("password:" + this.getPassword());

				if ((this.getPassword().equals(response))) {
					boolean found = false;
					Enumeration<User> e = users.elements();
					while (e.hasMoreElements() && !found) {
						Player player = (Player) e.nextElement();
						if (player.getPlayerName().equals(username)) {
							found = true;
						}
					}
					if (!found) {
						this.broadcast("Loading User...\r\n"); // break this out
																// to new method
																// 'load user'
						// result = this.loadUser(username,file,fileName);
						//this.loadItems(file.read_setting(fileName,"items",false
						// ));
						this.broadcast("User loaded\r\n");
						result = "loaded";
					} else {
						result = "error";
					}

				} else if (this.getPassword().matches("")) { // no password
																// means no user
					result = username;
				} else {
					result = "badpass";
					this.setPlayerName(null);
					this.setPassword("");
				}
			} else {
				System.out.println("empty string returned");
				
			}
		}
		return result;
	}

/*	public void loadCharacterClass() {
		FileHandler classConfig = new FileHandler();
		int response = -1;
		String setting = "";
		String out = "";
		boolean done = false;

		setting = classConfig.read_setting("class.cfg", "base_class", false);

		while (!done) {
			out = "Enter the number that corrisponds to your choice:\r\n";
			String[] lines = setting.split(";");
			for (int i = 0; i < lines.length; i++) {
				out = out + "(" + i + ")" + lines[i] + "\r\n";
			}
			this.broadcast(out);
			try {
				response = Integer.valueOf(this.getResponse()).intValue();
			} catch (Exception e) {
				response = -1;
			}
			if ((response >= lines.length) || (response == -1)) {
				this.broadcast("Invalid response, please try again.\r\n");
			} else {
				this.broadcast("You chose: " + lines[response] + "\r\n");
				String[] settingLines = classConfig.read_setting("class.cfg",
						(lines[response] + "_settings"), false).split(";");
				if (settingLines[0] != "") {// check to make sure there are
											// settins to load
					for (int i = 0; i < settingLines.length; i++) {
						String[] userSettings = settingLines[i].split(",");
						this.setSetting(userSettings[0], (Integer
								.valueOf(userSettings[1])).intValue());
					}
				}
				String[] itemLines = classConfig.read_setting("class.cfg",
						(lines[response] + "_items"), false).split(";");
				System.out.println(itemLines[0] + " " + itemLines.length);
				if (itemLines[0] != "") {// check to make sure there are items
											// to load.
					for (int i = 0; i < itemLines.length; i++) {
						Item startGear = ObjectFactory.CreateItem(itemLines[i]);
						if (startGear != null) {
							this.addToInventory(startGear);
							this.broadcast("Added " + startGear.getItemName()
									+ " to your starting gear.\r\n");
						} else {
							this.broadcast("error loading starting gear.\r\n");
						}
					}
				}
				setting = classConfig.read_setting("class.cfg",
						(lines[response] + "_class"), false);
				if (setting == "") {
					done = true;
				}
			}
		}
	}
*/

	public boolean loadUser(String fileName) {
		try {
			FileInputStream is = new FileInputStream(fileName);
			XMLDecoder decoder = new XMLDecoder(is);
			User u = (User) decoder.readObject();
			decoder.close();
			this.setPassword(u.getPassword());
			this.setPlayerName(u.getPlayerName());
			this.setSettings(u.getSettings());
			this.setStartRoom(u.getStartRoom());
			this.setItems(u.getItems());
			this.setMessages(u.getMessages());
			// Give dead users 100hp
			if (this.getSetting("hitpoints") < 1) {
				this.setSetting("hitpoints", 100);
			}
			
			//this.setSetting("builder", 1);
			this.setDead(false);
			return true;
		} catch (Exception e) {
			// if the user doesn't exist yet, we'll get here.
			System.out.print(e.toString());
			return false;
		}
	}

	/*
	 * public String loadUser(String username, FileHandler file,String
	 * fileName){ this.setPlayerName(username); this.setFightingFlag(false);
	 * 
	 * String settings = file.read_setting(fileName,"settings",false); //
	 * System.out.println("username:"+settings); StringTokenizer settingST = new
	 * StringTokenizer(settings,";"); StringTokenizer settingToken = null; while
	 * (settingST.hasMoreTokens()){ settingToken = new
	 * StringTokenizer(settingST.nextToken(),",");
	 * this.setSetting(settingToken.nextToken
	 * (),Integer.parseInt(settingToken.nextToken())); } //if user was killed,
	 * give him 100hp to start with. if (this.getSetting("hitpoints") < 1){
	 * this.setSetting("hitpoints",100); }
	 * 
	 * 
	 * //this.start(); return "loaded"; }
	 */

	public void sendUserPrompt() {
		broadcast("\r\n");
	}

	public void broadcast(String output) {
    if (output != null){
    	
    	String[] splitOutput;
		String filteredOutput = "";

		filteredOutput.contains("%");

		splitOutput = output.split("\n");
		System.out.println("jscript" + this.getjavascript());
		for (int i = 0; i < splitOutput.length; i++) {
			System.out.println(i + ":" + splitOutput[i]);
			if (!splitOutput[i].trim().startsWith("%js")) {
				filteredOutput = filteredOutput + splitOutput[i] + "\n";
			} else {
				if (this.getjavascript()) {
					this.send(splitOutput[i].trim() + "\n");
				}
			}
		}

		filteredOutput = OutputFilter.filter(filteredOutput);		
		this.send(filteredOutput);
    }
	}

	
	public void send(String output) {
		System.out.println("sending:" + output);
		if (out != null) {
			try {
				synchronized (out) {
					out.writeBytes(output);
					/*
					 * StringTokenizer st = new StringTokenizer(output,"\n");
					 * while (st.hasMoreTokens()){
					 * out.writeBytes(st.nextToken()+"\r\n"); }
					 */
				}
				out.flush();
			} catch (IOException ex) {
				// getRoom().removePlayer(this);
				try {
					sock.close();
				} catch (IOException e) {

				}
				// this.stop();
			}
		}

	}
	
	public void menu(User u) throws MenuExitException {
		String menuString = "Enter a new description for your character.\r\n";
		menuString += "Current character description:\r\n"
				+ this.getPlayerDescription() + "\r\n";
		PromptForString s = new PromptForString(this, menuString);
		String result = "";
		this.setInMenu(true);
		boolean done = s.display();

		while (done) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New character description:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the description, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
			done = s.display();
		}
		this.setInMenu(false);
		this.setPlayerDescription(result);
	}

	public void setPassword(String newPass) {

		this.password = newPass;
	}

	public String getPassword() {
		return this.password;
	}

	public void setTimeoutCounter(int NewTimeoutCounter) {
		this.timeoutCounter = NewTimeoutCounter;
	}

	public int GetTimeoutCounter() {
		return this.timeoutCounter;
	}

	public String getStartRoom() {
		return startRoom;
	}

	public void setStartRoom(String startRoom) {
		this.startRoom = startRoom;
	}

	public static Vector<User> getUsers() {
		return users;
	}

	public static void setUsers(Vector<User> users) {
		User.users = users;
	}

	public boolean isInMenu() {
		return inMenu;
	}

	public void setInMenu(boolean inMenu) {
		synchronized (this) {
			this.inMenu = inMenu;
		}
	}

/*	public void save() {
		this.getCurrentRoom();
		String filename = World.getArea().getUserDir() + "/"
				+ this.getPlayerName() + ".usr";
		FileHandler file = new FileHandler();
		String itemList = "";

		file.clear_file(filename);
		file.write_setting(filename, "password", this.getPassword());
		file.write_setting(filename, "name", this.getPlayerName());
		file.write_setting(filename, "settings", this.getSettingsAsString());
		Vector<Item> items = this.getItems();
		Enumeration<Item> e = items.elements();
		while (e.hasMoreElements()) {
			Item i = (Item) e.nextElement();
			itemList = itemList + filename + "." + i.getItemName() + ";\r\n";
			i.save(filename);
		}
		file.write_setting(filename, "items", itemList);
		file.close_file();
	}

*/
	public void look() {
		Room r = this.getCurrentRoom();
		//r.performTriggeredAction("LOOK", this);
		String output;
		output = r.getRoomDescription();
		output += r.getPlayerList() + "\r\n";
		output += "Exits:" + r.getVisibleExits()
				+ "\r\n";
		this.broadcast(output);
	}

	public void doBattle() {
		// check all possibilities, because eventually we may
		// allow players to have multiple intentions per round.

		if (this.getAttackTarget() != null && this.getRightHand() != null) {
			if (this.getRightHand().getIsWeapon()) {
				this.getRightHand().attack(this, this.getAttackTarget());
			} else {
				this.broadcast(this.getRightHand().getItemName()
						+ "is not a weapon!!\r\n");
			}
		}
		if (this.getEquipTarget() != null) {
			this.holdRight(this.getEquipTarget());
		}
		if (this.getEscape()) {
			this.getBattleHandler().escape(this);
		}
		this.setAttackTarget(null);
		this.setUseTarget(null);
		this.setEquipTarget(null);

	}

	//public String getClassName() {
	//	return "user";
	//}

	public void die() {
		if (!this.isDead()) {
			this.setDead(true);
			this.broadcast("YOU HAVE DIED!!!!\r\n");
			this.save(World.getArea().getUserDir() + "/" + this.getPlayerName()
					+ ".xml");
			this.quit();
		}
	}
}
