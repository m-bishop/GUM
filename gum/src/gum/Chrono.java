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

//import java.util.*;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

public class Chrono {

    private static Room index;
   // private static GameClock gameClock;

    public Chrono() {
    }

/*    public void loadRooms(String config, Area area) throws Exception{
        FileHandler file = new FileHandler();
        long randomSeed = 0;
        
        //area.setPort(file.read_setting(config,"port",false));
        area.setAreaDescription(file.read_setting(config,"area",true));
       // String lseed = parseLong(file.read_setting(config,"rseed",false));
       // Long lseed = new Long(parseLong(read_setting(config,"rseed",false)));
       try{
           randomSeed = Long.parseLong(file.read_setting(config, "rseed", false));
       }catch(Exception e){
           randomSeed = 123456;
       }
       System.out.println("randomseed=" + randomSeed);
       ObjectFactory.initializeRandObject(randomSeed);
       // Room.setImageDir(file.read_setting(config,"image",false));
        area.setImageDir("");
        ObjectFactory.setItemDir(area.getImageDir());
        System.out.println(area.getImageDir());
        area.setUserDir(file.read_setting(config,"user_data",false));
        //Room.setUserDir("");
        System.out.println(area.getUserDir());
        String room_list = file.read_setting(config,"rooms",false);
        StringTokenizer st = new StringTokenizer(room_list,";");
        if (st.hasMoreTokens()){
            index = new Room(st.nextToken());
            World.getArea().setIndex(index);
        }
        while (st.hasMoreTokens()){
          //  Room r = new Room(st.nextToken());
        	new Room(st.nextToken()); // the creation routine adds it to the room list.
        }
        //index = Room.getRoomByFilename("image/index");
        area.initRooms();

        // iterate through a list of rooms and tell them to load themselves.
    }

*/

    public void add_users (String config) {
       //FileHandler file = new FileHandler();
       // String port = file.read_setting(config,"port",false);
       // System.out.println("Port pulled:" + Integer.parseInt(port));
         System.out.println("Port:" + World.getArea().getPort());

        try{
            ServerSocket server = new ServerSocket(World.getArea().getPort());
            while (true) {
                Socket client = server.accept();
                System.out.println("Accepted test " + client.getInetAddress());
                User u = new User(client);
                //u.setCurrentRoom(World.getArea().getIndex());
                u.start();
             /*   if (u.login()){
                    index.addPlayer(u);
                } */
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

/*    
    public void createGameClock(){
        setGameClock(new GameClock());
    }
*/
    
    public static Room getIndex() {
		return index;
	}

/*
	public static void setGameClock(GameClock gameClock) {
		Chrono.gameClock = gameClock;
	}

	public static GameClock getGameClock() {
		return gameClock;
	}
	
*/
    
    public static void main(String[] args) {
    	try{
    		Chrono chrono_game = new Chrono();
    		Area area = new Area();
    		World.setArea(area);
    		GameClock gameClock = new GameClock();
    		World.setGameClock(gameClock);
    		OutputFilter.initialize();
    		//chrono_game.createGameClock();
    		if (args.length != 1) throw new RuntimeException ("no config file found");
    			//chrono_game.loadRooms(args[0], area);
    			//World.load();
    		World.load(args[0]);
    			chrono_game.add_users(args[0]);
    	} catch (Exception e){
    		e.printStackTrace();
    		System.exit(1);
    	}
    }
}
