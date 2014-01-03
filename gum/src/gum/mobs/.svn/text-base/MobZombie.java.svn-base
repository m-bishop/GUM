package gum.mobs;

//import gum.FileHandler;
import gum.Player;

import java.util.StringTokenizer;
//import java.util.Vector;
//import java.util.Enumeration;

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
public class MobZombie extends BasicMob {
    public MobZombie() {
        super();
    }

    //public MobZombie(String file) {
    //    super(file);
    //}

    public void broadcast(String message) {
    String token;
 //   String user;
    
 //   user = "";
    token = "";

    StringTokenizer st = new StringTokenizer(message, " ");
    //System.out.println("connie heard "+st.nextToken("\r\n"));
    if (st.hasMoreTokens()) {
 //       user = st.nextToken();
    }
    if (st.hasMoreTokens()) {
        token = st.nextToken();
    }
    if (token.equals("Hear")){
        token = st.nextToken("\r\n");
    }
        else if (token.endsWith("north")) {
            parser.ParseMobCommand(this, "north");
            this.setCurrentDirection("north");
        } else if (token.endsWith("south")) {
            parser.ParseMobCommand(this, "south");
            this.setCurrentDirection("south");
        } else if (token.endsWith("east")) {
            parser.ParseMobCommand(this, "east");
            this.setCurrentDirection("east");
        } else if (token.endsWith("west")) {
            parser.ParseMobCommand(this, "west");
            this.setCurrentDirection("west");
        } else if (token.endsWith("above")) {
            parser.ParseMobCommand(this, "up");
            this.setCurrentDirection("up");
        } else if (token.endsWith("below")) {
            parser.ParseMobCommand(this, "down");
            this.setCurrentDirection("down");
        }
    }


public void startFight(){
   // override this to make violent mobs.
   // this will be simple, just pick a fight with any user. This should
   // be easy to override so you can have mobs be more selective when
   // starting fights.
   Player victim = this.getCurrentRoom().getRandomPlayer(this);
   if (victim != null){
       parser.ParseMobCommand(this, "attack " + victim.getPlayerName());
   }
}

/*
public void save(String rootName){
    super.save(rootName);

    FileHandler mobFile = new FileHandler();
    String fileName = rootName + "." + this.getPlayerName();

    mobFile.write_setting(fileName,"class","mobzombie");  // MUST BE CHANGED WHEN WRITING NEW MOBS!!
    mobFile.close_file();
}
*/

//public String getClassName(){
//    return "mobzombie";
//}
}
