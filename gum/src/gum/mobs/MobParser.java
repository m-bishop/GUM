package gum.mobs;

import gum.Player;
import gum.UserParser;
import gum.items.Item;

import java.util.StringTokenizer;
// import java.util.Vector;


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
public class MobParser extends UserParser {
    public MobParser(){
    }

    public boolean ParseMobCommand(Mob mob,String command){
        boolean found = true;

        if (!this.parsePlayerCommand(mob,command)){ // mobs are players.
            String token;
            StringTokenizer st = new StringTokenizer(command, " ");

            if (st.hasMoreTokens()) {
                token = st.nextToken();
                if (token.matches("pause")) {
                    token = st.nextToken();
                    int miliseconds = Integer.parseInt(token);
                    try {
                        Thread.sleep(miliseconds);
                    } catch (InterruptedException ex) {
                        //add something here to notify the mob's owner
                    }
                } else if (token.equals("cdmove")) {
                    mob.moveCurrentDirection();
                    //here for no good reason, but I know I'll be adding commands
                } else if (token.equals("attackPlayers")){ // attack any player
                   // figure this out...where does this belong?
                   // maybe make the mob automatically do this based on some
                   // kind of measurement of violence?
                	//System.out.println(mob.getPlayerName()+" Called start fight.\r\n");
            		Player victim = mob.getCurrentRoom().getRandomPlayer(mob);
            		if (victim != null){
            		      this.parsePlayerCommand(mob, "attack " + victim.getPlayerName());
            		  }
                } else if (st.hasMoreTokens()){
                                //if the player is a mob, do this at the end of mob parser.
                                System.out.println("got here");
                                Item i;
                                i = mob.getItemByName(st.nextToken());
                                if (i != null){
                                    i.use(mob,token,st);
                                }
                } else {
                    found = false;
                }
            }
        }
        return found;
    }


}
