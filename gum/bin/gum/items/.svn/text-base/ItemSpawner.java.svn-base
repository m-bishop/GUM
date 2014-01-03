package gum.items;

import gum.ObjectFactory;
import gum.Player;
import gum.Room;
import gum.World;

import java.util.StringTokenizer;

import gum.mobs.Mob;

/**
 * <p>Title: </p>
 *
 * <p>Description: This is an item that, when interacted with, will check
 * prerequisite setting, then produce a mob from a mob file in the room
 * with the player if command and prerequisite are right. </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ItemSpawner extends ItemBase {

    private String command;
    private String mobFile;
    private String interaction;
    private String failedInteraction;
    private boolean mobRespawn;


    public ItemSpawner() {
        super();
    }

/*    public ItemSpawner(String file) {
        super(file);

        FileHandler roomFile = new FileHandler();

        command = roomFile.read_setting(file,"command",false);
        System.out.println("command="+command);

        mobFile = roomFile.read_setting(file,"mobfile",false);
        System.out.println("mobfile="+mobFile);

        failedInteraction = roomFile.read_setting(file,"failedinteraction",false);
        System.out.println("failedinteraction="+failedInteraction);

        interaction = roomFile.read_setting(file,"interaction",false);
        System.out.println("interaction="+interaction);

        if (roomFile.read_setting(file,"mobrespawn",false).equalsIgnoreCase("true")){
            mobRespawn = true;
        } else {
            mobRespawn = false;
        }
        System.out.println("mobrespawn:"+String.valueOf(mobRespawn));

        this.setIsContainer(false);
        this.setBlocking("");
        this.setIsStationary(true);
    }

*/

/*    public void save(String rootName){
        super.save(rootName); // do all normal saving

        FileHandler itemFile = new FileHandler();

        String fileName = rootName + "." + this.getItemName();
        itemFile.write_setting(fileName, "class", "spawner");
        itemFile.write_setting(fileName,"command",command);
        itemFile.write_setting(fileName,"mobfile",mobFile);
        itemFile.write_setting(fileName,"interaction",interaction);
        itemFile.write_setting(fileName,"failedinteraction",failedInteraction);

        if (mobRespawn){
            itemFile.write_setting(fileName,"mobrespawn","TRUE");
        } else {
            itemFile.write_setting(fileName,"mobrespawn","FALSE");
        }
        itemFile.close_file();
    }
*/
    public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getMobFile() {
		return mobFile;
	}

	public void setMobFile(String mobFile) {
		this.mobFile = mobFile;
	}

	public String getInteraction() {
		return interaction;
	}

	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}

	public String getFailedInteraction() {
		return failedInteraction;
	}

	public void setFailedInteraction(String failedInteraction) {
		this.failedInteraction = failedInteraction;
	}

	public boolean isMobRespawn() {
		return mobRespawn;
	}

	public void setMobRespawn(boolean mobRespawn) {
		this.mobRespawn = mobRespawn;
	}

	public void check(Player player){
    String checkOutput = "";

    checkOutput = "SimpleItem Setting Checklist:\r\n";
    checkOutput += "Name = "+ this.getItemName() + "\r\n";
    checkOutput += "long description:\r\n " + this.getDescription() + "\r\n";

    checkOutput += "PreReq Setting Name = " + this.getPreReqSettingName() + "\r\n";
    checkOutput += "PreReq Setting Value = " + String.valueOf(this.getPreReqSettingValue()) + "\r\n";

    checkOutput += "command:\r\n " + command + "\r\n";
    checkOutput += "mobfile:\r\n " + mobFile + "\r\n";
    checkOutput += "interaction:\r\n " + interaction + "\r\n";
    checkOutput += "failedInteraction:\r\n " + failedInteraction + "\r\n";

    checkOutput += "invisable = " + String.valueOf(this.getIsInvisable()) + "\r\n";
    checkOutput += "infinite = " + String.valueOf(this.getIsInfinite()) + "\r\n";
    checkOutput += "mobrespawn = " + String.valueOf(mobRespawn) + "\r\n";

    player.broadcast(checkOutput);

}

public void parseItem(StringTokenizer st, Player player) {
    String token = "";

    if (st.hasMoreTokens()) {
        token = st.nextToken();
    }
    if (token.equals("name")) {
        if (st.hasMoreTokens()) {
            token = st.nextToken();
        }
        this.setName(token);
    }else if (token.equals("command")) {
        if (st.hasMoreTokens()) {
            command = st.nextToken();
        }
    }else if (token.equals("mobfile")) {
        if (st.hasMoreTokens()) {
            mobFile = st.nextToken();
        }
    }else if (token.equals("interaction")) {
        if (st.hasMoreTokens()) {
            interaction = st.nextToken();
        }
    }else if (token.equals("failedinteraction")) {
        if (st.hasMoreTokens()) {
            failedInteraction = st.nextToken();
        }
    }else if (token.equals("description")) {
        if (st.hasMoreTokens()) {
            token = st.nextToken("\r");
        }
        this.setDescription(token + "\r\n");
    } else if (token.equals("description+")) {
        if (st.hasMoreTokens()) {
            token = st.nextToken("\r");
        }
        this.addToDescription(token + "\r\n");
    } else if (token.equals("invisable")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            }
            if (token.equalsIgnoreCase("true")){
                this.setIsInvisable(true);
            } else if (token.equalsIgnoreCase("false")){
                this.setIsInvisable(false);
            } else {
                player.broadcast("isInvisable must be either true or false\r\n");
            }
        }else if (token.equals("infinite")) {
                  if (st.hasMoreTokens()) {
                      token = st.nextToken();
                  }
                  if (token.equalsIgnoreCase("true")){
                      this.setIsInfinite(true);
                  } else if (token.equalsIgnoreCase("false")){
                      this.setIsInfinite(false);
                  } else {
                      player.broadcast("infinite must be either true or false\r\n");
                  }
        }else if (token.equals("mobrespawn")) {
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            }
            if (token.equalsIgnoreCase("true")){
                mobRespawn = true;
            } else if (token.equalsIgnoreCase("false")){
                mobRespawn = false;
            } else {
                player.broadcast("mobrespawn must be either true or false\r\n");
            }
        } else if (token.equals("prereqsetting")) {
            if (st.hasMoreTokens()) {
                this.setPreReqSettingName(st.nextToken());
            }
        } else if (token.equals("prereqvalue")){
            if (st.hasMoreTokens()) {
                token = st.nextToken();
            } else {
                player.broadcast("expected integer\r\n");
            }
            try{
                this.setPreReqSettingValue(Integer.parseInt(token));
            }catch (Exception e){
                player.broadcast("error parsing int value.\r\n");
            }
        }
    }

    public boolean use(Player p, String pCommand, StringTokenizer st) {
        String token = "";

        System.out.println("token ='"+token+"' command = '"+command+"'");
        if ((pCommand.equals(command)) &&
            (p.getSetting(this.getPreReqSettingName()) == this.getPreReqSettingValue())){
            if (p.getSetting(this.getFilename()) == 0 || this.getIsInfinite()){
                p.setSetting(this.getFilename(), 1);

                /* // use this for stress testing.
                             for (int i = 1;i !=1000; i++){
                    Mob m = ObjectFactory.CreateMob(mobFile);
                    p.getRoom().addPlayer(m);
                    //m.start();
                    m.setRespawnRoom(p.getRoom());

                             }*/
                Mob m = ObjectFactory.CreateMob(World.getArea().getLibDir()+"/"+mobFile);
                if (m != null) { // needs a better way to handle a bad filename
                    Room r = p.getCurrentRoom();
                    r.addPlayer(m);
                    m.setRespawnRoom(r);
                    m.start();
                    m.setRespawnRoom(p.getCurrentRoom());
                    if (!mobRespawn){
                        m.setRespawnTimeout(0);
                    }
                    p.broadcast(interaction);
                } else {
                    p.broadcast("mob not found!<probably a bad filename>\r\n");
                }
            } else {
                p.broadcast("Hmm...Looks like you've already done this before.\r\n");
            }
        } else{
            p.broadcast(failedInteraction);
        }
        return true;
    }
}
