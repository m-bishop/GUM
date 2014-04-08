package gum;


import gum.actions.ActionTimed;
import gum.menus.MenuContainer;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Area implements MenuContainer{
	
	
    //private static Area areaRef; 
    private Vector<Room> rooms = new Vector<Room>();
    private String areaDescription;
    private String imageDir;
    private String userDir;
    private String libDir;
    private int port;
    private Room index;
    private boolean paused;
    private CharacterClass baseClass;
    
    private HashMap<String, Integer> settings = new HashMap<String, Integer>(); // setting map
    private HashMap<String,String> missionMap = new HashMap<String,String>();
    private Vector<respawnable> respawnList = new Vector<respawnable>();
    private Vector<ActionTimed> actionList = new Vector<ActionTimed>();
    private Vector<String> skillList = new Vector<String>(); 
    
  
    // no arg constructor required for bean.
    public Area() { 
    	//make a default directory structure here. 
    	libDir = "library";
    	imageDir = "image";
    	userDir = "user_data";
    	baseClass = new CharacterClass();
    }
    
    public void check(Player p){
    	String oString = "";
    
    	oString += "\r\nimageDir:"+imageDir+"\r\n";
    	oString += "userDir:"+userDir+"\r\n";
    	oString += "libDir:"+libDir+"\r\n";
    	oString += "Port:"+port+"\r\n";
    	oString += "index:"+index.getName()+"\r\n";
    	oString += "areaDescription:\r\n"+areaDescription+"\r\n";
    	
    	p.broadcast(oString);
    }
    
    public synchronized void clearArea(User u){
    	//This method is utility used to create a new area. 
    	try {
    		//pause game clock
    		//synchronized (World.getGameClock()){World.getGameClock().wait();}
			Room newIndex = new Room();
			newIndex.init();
			this.setPaused(true);
			this.prepForSave();
			
    	
			// kill all players and mobs, so their threads don't keep 
			// rooms alive. 
			for (Room room : rooms){
				Vector<Player> players = new Vector<Player>(room.getPlayers());
				for(Player player : players ){
					
						player.die();
					
				}
			}
    	
			//clear action and respawn lists. 
			respawnList.clear();
			actionList.clear();
			rooms.clear();
			//respawnList = new Vector<respawnable>();
			//actionList = new Vector<ActionTimed>();
			//rooms = new Vector<Room>();
			baseClass = new CharacterClass();
			
			rooms.add(newIndex);
			this.setIndex(newIndex);
			this.setAreaDescription("Default Area Description\r\n");
			this.setPaused(false);
			//World.getGameClock().notify();
    	} catch (Exception e) {
			u.broadcast("The follow error occured while creating new area:\r\n"+e+"\r\n");
			e.printStackTrace();
		}
    	
    	
    }

    public synchronized void menu(User u) throws MenuExitException{
    	boolean done = false;
    	String menuString =  "Configure Area:\r\n";
    		   menuString += "(01) Configure Image Directory \r\n";
    		   menuString += "(02) Confifure User Directory \r\n";
    		   menuString += "(03) Configure Library Directory \r\n";
    		   menuString += "(04) Configure Port \r\n";
    		   menuString += "(05) Configure Area Description \r\n";
    		   menuString += "(06) Configure Character Creation\r\n";
    		   menuString += "(07) Configure Character Skills\r\n";
    		   menuString += "(08) Configure Missions\r\n";
    		   menuString += "(09) Configure Timed Actions\r\n";
    		   menuString += "(10) Create blank area **YOU WILL BE DISCONNECTED**\r\n";
    		   menuString += "(11) Load Area in Edit Mode. \r\n";
    		   menuString += "(12) Run current Area. \r\n";
    		   menuString += "(13) Save current Area\r\n";
    		   menuString += "(14) Save rooms by range.\r\n";
    		   menuString += "(15) Load saved rooms.\r\n";
        PromptForInteger p = new PromptForInteger(u, menuString, 15, 1);
        while (p.display() && !done){
        	switch (p.getResult()){
        		case 1:  done = configureImageDir(u); break;
        		case 2:  done = configureUserDir(u); break;
        		case 3:  done = configureLibDir(u); break;
        		case 4:  done = configurePort(u); break;
        		case 5:  done = configureAreaDescription(u); break;
        		case 6:  baseClass.menu(u); break;
        		case 7:  configSkills(u);break;
        		case 8:  configMissions(u);break;
        		case 9:  ConfigTimedActions(u); break;
        		case 10:  clearArea(u);break;
        		case 11: configAreaLoad(u); break;
        		case 12: this.startAllMobs(); break;
        		case 13: configAreaSave(u); break;
        		case 14: configAreaRoomsSave(u); break;
        		case 15: configAreaRoomsLoad(u); break;
        	}
        }
        u.broadcast("Exiting Area Configuration.\r\n");
    }
    
    public void configAreaRoomsSave(User u) throws MenuExitException{
    	// May need to do more prep for save, like breaking connections with exits. the exit objects should be replaced with 
    	// blank rooms with the filename of the room that it connects to, so that the 'load' function can use that to create
    	// new links within new rooms. 
    	
    	String menuRoomSearchString = "All players in target rooms will be removed from the game!\r\n";
    	menuRoomSearchString += "Enter 3 character search wildcard for rooms:";
		String menuFileNameString = "Enter filename for save.\r\n\r\n";

		Room currentRoom = null;
		Vector<Room> roomsToSave = new Vector<Room>();
		
		PromptForString fileString = new PromptForString(u, menuFileNameString);
		PromptForString searchString = new PromptForString(u, menuRoomSearchString);
		
		boolean done = searchString.display();
		
		if (done){
			Iterator<Room> itr = rooms.iterator();
			
			while (itr.hasNext()){
				currentRoom = itr.next();
				if (currentRoom.getRoomFilename().startsWith(searchString.getResult())){
					
					//save filenames, so we can remove object references for save.
					if (currentRoom.getNorthExit() != null){currentRoom.setNorthExit(currentRoom.getNorthRoom().getRoomFilename());}
					if (currentRoom.getSouthExit() != null){currentRoom.setSouthExit(currentRoom.getSouthRoom().getRoomFilename());}
					if (currentRoom.getEastExit() != null){currentRoom.setEastExit(currentRoom.getEastRoom().getRoomFilename());}
					if (currentRoom.getWestExit() != null){currentRoom.setWestExit(currentRoom.getWestRoom().getRoomFilename());}
					if (currentRoom.getUpExit() != null){currentRoom.setUpExit(currentRoom.getUpRoom().getRoomFilename());}
					if (currentRoom.getDownExit() != null){currentRoom.setDownExit(currentRoom.getDownRoom().getRoomFilename());}
					
					//break object connections before saving. 
					currentRoom.setNorthRoom(null);
					currentRoom.setSouthRoom(null);
					currentRoom.setEastRoom(null);
					currentRoom.setWestRoom(null);
					currentRoom.setUpRoom(null);
					currentRoom.setDownRoom(null);
					
					roomsToSave.add(currentRoom);
				}
			}
			
			if (roomsToSave.size() > 0){
			done = fileString.display();

				if (done) {
				
					try{
						FileOutputStream os = new FileOutputStream(fileString.getResult());
						XMLEncoder encoder = new XMLEncoder(os);
						encoder.writeObject(roomsToSave);
						encoder.close();
						u.broadcast("Rooms saved as:"+fileString.getResult());
					} catch (Exception e){
						e.printStackTrace();
					}
				} else {
					u.broadcast("Rooms not saved. \r\n");
				}
			} else {
				u.broadcast("Rooms not found.");
			}
		}   
		Iterator<Room> itr = roomsToSave.iterator();
		
		while (itr.hasNext()){
			//reconnect the objects.
			
			currentRoom = itr.next();
		
			currentRoom.setNorthRoom(this.getRoomByFilename(currentRoom.getNorthExit()));
			currentRoom.setSouthRoom(this.getRoomByFilename(currentRoom.getSouthExit()));
			currentRoom.setEastRoom(this.getRoomByFilename(currentRoom.getEastExit()));
			currentRoom.setWestRoom(this.getRoomByFilename(currentRoom.getWestExit()));
			currentRoom.setUpRoom(this.getRoomByFilename(currentRoom.getUpExit()));
			currentRoom.setDownRoom(this.getRoomByFilename(currentRoom.getDownExit()));
			
		}
    }
    
    public void configAreaRoomsLoad(User u) throws MenuExitException{
    	// Method must take in a string to replace the prefix used to identify the rooms, so they will have 
    	// unique filename within the system. 
    	// when loading the new rooms, you will have to remember to re-link the exits to their proper rooms. 
		String menuString = "Enter filename to Load.\r\n\r\n";
		String fileName = "";
	
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			fileName = s.getResult();
			u.broadcast("Loading Room File:"+fileName);
			try{
	    		FileInputStream is = new FileInputStream(fileName);
	    		XMLDecoder decoder = new XMLDecoder(is);
	    		
	    		@SuppressWarnings("unchecked")
				Vector<Room> roomsToLoad = (Vector<Room>)decoder.readObject();
	    		decoder.close(); 
	    		
				Iterator<Room> itr = roomsToLoad.iterator();
				
				while (itr.hasNext()){
					Room currentRoom = itr.next();
					
					currentRoom.setNorthRoom(this.getRoomByFilename(roomsToLoad, currentRoom.getNorthExit()));
					currentRoom.setSouthRoom(this.getRoomByFilename(roomsToLoad, currentRoom.getSouthExit()));
					currentRoom.setEastRoom(this.getRoomByFilename(roomsToLoad, currentRoom.getEastExit()));
					currentRoom.setWestRoom(this.getRoomByFilename(roomsToLoad, currentRoom.getWestExit()));
					currentRoom.setUpRoom(this.getRoomByFilename(roomsToLoad, currentRoom.getUpExit()));
					currentRoom.setDownRoom(this.getRoomByFilename(roomsToLoad, currentRoom.getDownExit()));
				}
				
				
				menuString = "Enter 3 character prefix to replace:";
				s = new PromptForString(u, menuString);
				
				if (s.display()){
				
					itr = roomsToLoad.iterator();
				
					while (itr.hasNext()){
						Room currentRoom = itr.next();
						currentRoom.setFilename(s.getResult()+currentRoom.getFilename().substring(3));
					}
					
					itr = roomsToLoad.iterator();
					
					while (itr.hasNext()){
						Room currentRoom = itr.next();

						if (currentRoom.getNorthRoom() != null){currentRoom.setNorthExit(currentRoom.getNorthRoom().getRoomFilename());}
						if (currentRoom.getSouthRoom() != null){currentRoom.setSouthExit(currentRoom.getSouthRoom().getRoomFilename());}
						if (currentRoom.getEastRoom() != null){currentRoom.setEastExit(currentRoom.getEastRoom().getRoomFilename());}
						if (currentRoom.getWestRoom() != null){currentRoom.setWestExit(currentRoom.getWestRoom().getRoomFilename());}
						if (currentRoom.getUpRoom() != null){currentRoom.setUpExit(currentRoom.getUpRoom().getRoomFilename());}
						if (currentRoom.getDownRoom() != null){currentRoom.setDownExit(currentRoom.getDownRoom().getRoomFilename());}
					}
					this.rooms.addAll(roomsToLoad);
					u.broadcast("Rooms Loaded.\r\n");
				}
	    	}catch (Exception e){
				e.printStackTrace();
				u.broadcast("Error loading rooms file. Check filename and try again.\r\n");
			}
			
		} else {
			u.broadcast("Rooms not found. \r\n");
		}
    }
    
	public boolean configMissions(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure Missions:\r\n";
		menuString += "(1) List missions. \r\n";
		menuString += "(2) Add a new mission. \r\n";
		menuString += "(3) Remove a mission. \r\n";

		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configListMissions(u);
				break;
			case 2: 
				configAddMission(u);
				break;
			case 3: 
				configRemoveMission(u);
				break;
			}
		}
		return done;
	}		
	
	public void configListMissions(User u){
		String settingMenuString = "Currently this class has the following missions:\r\n\r\n";
	    
	       for (String m : missionMap.keySet()){
	    	   settingMenuString += "mission setting:"+m+" \r\ndescription:\r\n"+missionMap.get(m)+"\r\n";   
	       }
	       u.broadcast(settingMenuString);
	}
	
	public void configAddMission(User u) throws MenuExitException{
		
	    String settingMenuString =  "This will add a missions to this area.\r\n";
			   settingMenuString += "If the area already has this missions, it will be replaced.\r\n";
		       settingMenuString += "Currently this class has the following missions:\r\n\r\n";
		       
		       for (String m : missionMap.keySet()){
		    	   settingMenuString += "mission setting:"+m+" \r\ndescription:\r\n"+missionMap.get(m)+"\r\n";   
		       }
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (!newSettingName.equals("")){
			String missionDescription = this.configureMissionDescription(u);
			if (!missionDescription.equals("")){
				missionMap.put(newSettingName, missionDescription);
				u.broadcast("Mission Added");
			}
		}
	}	
	
	public void configRemoveMission(User u) throws MenuExitException{
		
		   Object[] keyArray= missionMap.keySet().toArray();
	       String menuString = "Choose a mission to remove, or type 'exit':\r\n\r\n";
	       int count = 0;
	       
	       for (Object s : keyArray){
	    	   count++;
	    	   menuString += "("+count+") setting:"+((String)s)+" \r\ndescription:\r\n"+missionMap.get(s)+"\r\n";   
	       }
	       PromptForInteger p = new PromptForInteger(u, menuString,keyArray.length+1,1);
	       if (p.display()){
	    	   String removed = (String)keyArray[(p.getResult()-1)];
	    	   u.broadcast("Removing:"+removed+"\r\n");
	    	   missionMap.remove(removed);
	       }
	}	
    
	public String getSettingName(User u) throws MenuExitException {
		String result = "";
		String menuString = "Enter a setting name to represent the mission.\r\n\r\n";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			result = s.getResult();
		} 
		return result;
	}
	
	
	public void configSkills(User u) throws MenuExitException {
		String menuString = "Configure messages:\r\n";
		menuString += "(1) List skills \r\n";
		menuString += "(2) Add a skill \r\n";
		menuString += "(3) Remove a skill \r\n";

		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 4, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configViewSkills(u);
				break;
			case 2: 
				this.configNewSkill(u);
				break;
			case 3: 
				this.configRemoveSkill(u);
				break;
			}
		}
	}
	
	public void configViewSkills(User u){
		String display = "Current Skills:\r\n\r\n";
		int i = 1;
		
		for (String message : skillList){
			display += "("+i+") "+message+"\r\n";
			i++;
		}
		u.broadcast(display);
	}
	
	public void configRemoveSkill(User u) throws MenuExitException{	
		
		String menuString =  "Enter a skill index from the list below to remove it.\r\n";
		int i = 1;
		
		for (String skill : skillList){
			menuString += "("+i+") "+skill+"\r\n";
			i++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, skillList.size(), 1);
	       if (p.display()){
	    	   if (skillList.remove(p.getResult()-1) != null){
	    		   u.broadcast("Skill removed.\r\n");
	    	   }else {
	    		   u.broadcast("Skill not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Skill unchanged.\r\n");
	       }
	}
	
	public void configNewSkill(User u) throws MenuExitException {
		String menuString =  "Enter a new Skill below, and it will be added to the end of the skill list.\r\n";
			   menuString += "New Setting:\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";
		s.display();
		result += s.getResult();
		
		if (!result.equals("")){
			skillList.add(result);
		}
	}
    
	public void ConfigTimedActions(User u) throws MenuExitException {
		boolean done = false;
    	String menuString =  "Configure Timed Actions:\r\n";
    		   menuString += "(1) Add a timed action. \r\n";
    		   menuString += "(2) Configure a timed action. \r\n";
    		   menuString += "(3) Remove a timed action.";
    		   
        PromptForInteger p = new PromptForInteger(u, menuString, 16, 1);
        while (p.display() && !done){
        	switch (p.getResult()){
        		case 1:  
        			ActionTimed timedAction = new ActionTimed();
        			this.getActionList().add(timedAction); 
        			timedAction.menu(u);
        			break;
        		case 2:  configExistingAction(u); break;
        		case 3:  configRemoveAction(u); break;
        	}
        }	
	}
    
    public void configAreaLoad(User u) throws MenuExitException{
		String menuString =  "***YOU WILL BE DISCONNECTED***\r\n";
		       menuString += "Enter filename to Load.\r\n\r\n";
		String fileName = "";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			fileName = this.getImageDir()+"\\"+s.getResult();
			u.broadcast("Loading Area:"+fileName);
			u.quit();
			World.load(fileName);
		} else {
			u.broadcast("Area not found. \r\n");
		}
    }    
    
    public void configAreaSave(User u) throws MenuExitException{
		String menuString = "Enter filename for save.\r\n\r\n";
		String fileName = "";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			String room = u.getCurrentRoom().getFilename();
			u.getCurrentRoom().removePlayer(u); 
			this.prepForSave();
			//fileName = this.getImageDir()+"\\"+s.getResult();
			fileName = s.getResult();
			this.save(fileName);
			u.broadcast("Area saved as:"+fileName);
			this.getRoomByFilename(room).addPlayer(u);
		} else {
			u.broadcast("Area not saved. \r\n");
		}
    }    

    public String configureMissionDescription(User u) throws MenuExitException{
		String menuString = "Enter a new mission description:\r\n";
		
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
		return result;
    } 
    
    public boolean configureAreaDescription(User u) throws MenuExitException{
		String menuString = "Enter a new area description.\r\n";
		menuString += "Current area description:\r\n"
				+ World.getArea().getAreaDescription() + "\r\n";
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
		World.getArea().setAreaDescription(result);
		return done;
    }    
    
    public boolean configurePort(User u) throws MenuExitException{
    	String menuString = "Enter a new TCP/IP port for the game.(I.E. '3456')\r\n";
    	       menuString +="Current port:"+World.getArea().getPort()+"\r\n";
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		this.setPort(i.getResult());
    		u.broadcast("Game port changed to:"+i.getResult()+"\r\nYou must save, and restart for setting to take effect!\r\n");
    	}else {
    		u.broadcast("Game port unchanged.\r\n");
    	}
    	
    	return done;
    }    
    
    public boolean configureImageDir(User u) throws MenuExitException{
    	String menuString = "Enter a directory to hold world data. (I.E. 'image')\r\n";
    		   menuString += "Current Image dir:"+this.getImageDir()+"\r\n";
    	PromptForString s = new PromptForString(u,menuString);
    	boolean done = s.display();
    	
    	if (done){
    		this.setImageDir(s.getResult());
    		u.broadcast("Image directory set to:"+s.getResult()+"\r\n");
    	} else {
    		u.broadcast("Image directory unchanged. \r\n");
    	}
    	return done;
    }
    
    public boolean configureUserDir(User u) throws MenuExitException{
    	String menuString = "Enter a directory to hold user data. (I.E. 'user')\r\n";
    		   menuString += "Current User dir:"+this.getUserDir()+"\r\n";
    	PromptForString s = new PromptForString(u,menuString);
    	boolean done = s.display();
    	
    	if (done){
    		this.setUserDir(s.getResult());
    		u.broadcast("User directory set to:"+s.getResult()+"\r\n");
    	} else {
    		u.broadcast("User directory unchanged. \r\n");
    	}
    	return done;
    }

    public boolean configureLibDir(User u) throws MenuExitException{
    	String menuString = "Enter a directory to library user data. (I.E. 'library')\r\n";
    		   menuString += "Current library dir:"+this.getLibDir()+"\r\n";
    	PromptForString s = new PromptForString(u,menuString);
    	boolean done = s.display();
    	
    	if (done){
    		this.setUserDir(s.getResult());
    		u.broadcast("Library directory set to:"+s.getResult()+"\r\n");
    	} else {
    		u.broadcast("Library directory unchanged. \r\n");
    	}
    	return done;
    }   
    
	public void configRemoveAction(User u) throws MenuExitException{	
		
		String menuString =  "Enter an action from the list below to delete it.\r\n";
		int count = 0;
		
		for (ActionTimed a : actionList){
			menuString += "("+count+") "+a.getName()+"\r\n";
			count++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, actionList.size()-1, 0);
	       if (p.display()){
	    	   //u.broadcast("retrieving: " +keys.get(p.getResult())+" from: "+this.getItemName()+"\r\n");
	    	   
	    	   if (this.actionList.get(p.getResult()) != null){
	    		   this.actionList.remove(p.getResult());
	    	   }else {
	    		   u.broadcast("Action not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Actionss unchanged.\r\n");
	       }
	}
    
	public void configExistingAction(User u) throws MenuExitException{	
		
		String menuString =  "Enter an action from the list below to configure it.\r\n";
		int count = 0;
		
		for (ActionTimed a : actionList){
			menuString += "("+count+") "+a.getName()+"\r\n";
			count++;
		}
	       
	       PromptForInteger p = new PromptForInteger(u, menuString, actionList.size()-1, 0);
	       if (p.display()){
	    	   //u.broadcast("retrieving: " +keys.get(p.getResult())+" from: "+this.getItemName()+"\r\n");
	    	   
	    	   if (this.actionList.get(p.getResult()) != null){
	    		   this.actionList.get(p.getResult()).menu(u);
	    	   }else {
	    		   u.broadcast("Action not found.\r\n");
	    	   }
	       } else {
	    	   u.broadcast("Actionss unchanged.\r\n");
	       }
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
    
    public synchronized String getAreaDescription(){
        return areaDescription;
    }

    public synchronized void setAreaDescription(String description){
        areaDescription = description;
    }

    public synchronized int getPort(){
        return port;
    }

    public synchronized void setPort(int i){
        port = i;
    }


    public String getLibDir() {
		return libDir;
	}

	public void setLibDir(String libDir) {
		this.libDir = libDir;
	}

	public synchronized void setImageDir(String inputDir){
        imageDir = inputDir;
    }

    public synchronized String getImageDir(){
        return imageDir;
    }

    public synchronized void setUserDir(String inputDir){
        userDir = inputDir;
    }

    public synchronized String getUserDir(){
        return userDir;
    }
    
    public synchronized Room getRoomByFilename(String file){
        boolean Found = false;
        Room o_room = null;
        Enumeration<Room> e = rooms.elements();
         while (e.hasMoreElements() && !Found){
             Room room = (Room)e.nextElement();
             if (room.getRoomFilename().equals(file)){
                 o_room = room;
                 Found = true;
             }
        }
        return o_room;
    }
    
    public synchronized Room getRoomByFilename(Vector<Room> rooms, String file){
        boolean Found = false;
        Room o_room = null;
        Enumeration<Room> e = rooms.elements();
         while (e.hasMoreElements() && !Found){
             Room room = (Room)e.nextElement();
             if (room.getRoomFilename().equals(file)){
                 o_room = room;
                 Found = true;
             }
        }
        return o_room;
    }

    public synchronized void GlobalChat(String message){
        Enumeration<Room> e = rooms.elements();
        while (e.hasMoreElements()){
            Room room = (Room)e.nextElement();
            room.chat(message);
        }
    }
    
    public synchronized void prepForSave(){
        Enumeration<Room> e = rooms.elements();
        while (e.hasMoreElements()){
            Room room = (Room)e.nextElement();
            room.prepForSave();
        }
    }

    public synchronized void processPlayers(){
        Enumeration<Room> e = rooms.elements();
        while (e.hasMoreElements()){
            Room room = (Room)e.nextElement();
            Enumeration<Player> p = room.getPlayers().elements();
            while (p.hasMoreElements()){
                Player player = (Player)p.nextElement();
                if (!player.getFightingFlag()){// if they're fighting, let battlehandler deal with it. 
                	player.process();
                }
            }
        }
    }

    public synchronized boolean getpaused(){
        return paused;
    }

    public Vector<Room> getRooms() {
		return rooms;
	}

	public void setRooms(Vector<Room> rooms) {
		this.rooms = rooms;
	}

	public synchronized boolean isPaused() {
		return paused;
	}

	public synchronized void setPaused(boolean paused) {
		this.paused = paused;
	}

    public Room getIndex() {
		return index;
	}

	public void setIndex(Room index) {
		this.index = index;
	}	
	
    public CharacterClass getBaseClass() {
		return baseClass;
	}

	public void setBaseClass(CharacterClass baseClass) {
		this.baseClass = baseClass;
	}

	public HashMap<String, Integer> getSettings() {
		return settings;
	}

	public void setSettings(HashMap<String, Integer> settings) {
		this.settings = settings;
	}
	
	public void addRespawn(respawnable r){
		respawnList.add(r);
	}
	
	public Vector<respawnable> getRespawnList() {
		return respawnList;
	}

	public void setRespawnList(Vector<respawnable> respawnList) {
		this.respawnList = respawnList;
	}

	public Vector<ActionTimed> getActionList() {
		return actionList;
	}

	public void setActionList(Vector<ActionTimed> actionList) {
		this.actionList = actionList;
	}


	public void setSkillList(Vector<String> skillList) {
		this.skillList = skillList;
	}

	public Vector<String> getSkillList() {
		return skillList;
	}

	public void setMissionMap(HashMap<String,String> missionMap) {
		this.missionMap = missionMap;
	}

	public HashMap<String,String> getMissionMap() {
		return missionMap;
	}

	public synchronized void save(String filename){
    	try{
    		//respawnList.clear();
    		//this.setIndex(null);
    		//World.getGameClock().setTerminate(true);
    		//World.setGameClock(null);
			//actionList.clear();
			//rooms.clear();
    		FileOutputStream os = new FileOutputStream(filename);
    		XMLEncoder encoder = new XMLEncoder(os);
    		//encoder.writeObject(this);
    		encoder.writeObject(World.getArea());
    		encoder.close();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	
    }	
	
/*	public synchronized void saveImage(){
      String roomList = "";
      FileHandler configFile = new FileHandler();
      String fileName = "config.cfg";
      Long random_seed = new Long(ObjectFactory.getRandObject().nextLong());

      //Enumeration e1 = rooms.elements ();
/*      while (e1.hasMoreElements ()) {
          Room room = (Room) e1.nextElement();
          room.pausePlayers();
      }*/
/*
      setPaused(true);
      Enumeration<Room> e2 = rooms.elements ();
      while (e2.hasMoreElements ()) {
          Room room = (Room) e2.nextElement();
          roomList = roomList + room.getRoomFilename()+";\r\n";
          room.saveRoom();
      }
      setPaused(false);
/*      Enumeration e3 = rooms.elements ();
      while (e3.hasMoreElements ()) {
          Room room = (Room) e3.nextElement();
          room.unPausePlayers();
      }
      configFile.clear_file(fileName);
      configFile.write_setting(fileName,"area",areaDescription);
      configFile.write_setting(fileName,"image",imageDir);
      configFile.write_setting(fileName,"user_data",userDir);
      configFile.write_setting(fileName,"port",String.valueOf(port));
      configFile.write_setting(fileName,"rseed",random_seed.toString());
      configFile.write_setting(fileName,"rooms",roomList);
      configFile.close_file();

    }
*/
	public synchronized void startAllMobs(){
        Enumeration<Room> e = rooms.elements ();
      while (e.hasMoreElements ()) {
          Room room = (Room)e.nextElement();
          Enumeration<Player> eP = room.getPlayers().elements();
          while (eP.hasMoreElements()){
              Player p = (Player)eP.nextElement();
              if (!(p instanceof User)){
                  try {
                      p.start();
                  } catch (Exception eX){
                      // do nothing, we expect situations
                      // where some will already be started
                      // and will throw an exception when
                      // you try to start them again.
                  }
              }
          }
      }
    }

    public synchronized void checkPlayerTimeout(){
        Enumeration<Room> e = rooms.elements ();
      while (e.hasMoreElements ()) {
          Room room = (Room)e.nextElement();
          User u = null;
          Enumeration<Player> eP = room.getPlayers().elements();
          while (eP.hasMoreElements()){
              Player p = (Player)eP.nextElement();
              if (p instanceof User){
                  try {
                      u = (User)p;
                      u.setTimeoutCounter(u.GetTimeoutCounter()+1);
                      if ((u.GetTimeoutCounter()) > 10){
                          u.broadcast("Timed out (10 minute limit)\r\n");
                          u.quit(); 
                      }
                  } catch (Exception eX){
                      // do nothing, we expect situations
                      // where some will already be started
                      // and will throw an exception when
                      // you try to start them again.
                  }
              }
          }
      }
    }

    public synchronized void initRooms(){
        Enumeration<Room> e = rooms.elements ();
      while (e.hasMoreElements ()) {
          Room room = (Room)e.nextElement();
          
          //create room init method, and call it from here. 
          //make it set all mob's current rooms to whatever 
          //room they're in, and then have all items and 
          //mobs save themselves for repop.
          // TODO finish room respawn code. 
          room.initialize();
          //room.init();
          // room.initializeExits();
          // System.out.println(room.filename);
          // System.out.println(room.name);
          // System.out.println(room.description);
      }
    }
    

}
