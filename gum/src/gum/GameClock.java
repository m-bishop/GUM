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

import gum.actions.ActionHeader;
import gum.actions.ActionTimed;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

public class GameClock extends Thread {
	
	private boolean terminate = false;

    public GameClock() {
       // super();
    	this.setTerminate(false);
        this.start();
        System.out.println("creating clock");
    }

    public void run(){
    	Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        while (!this.isTerminate()) {
            try {
                // System.out.println("tick");
                // add checks here
                World.getArea().checkPlayerTimeout();
                this.processRespawnList();
                this.processActionList();
                World.getArea().processPlayers();
                cal = Calendar.getInstance();
                Room.addWorldDescriptor("time", sdf.format(cal.getTime()));
                // System.out.println("Time:"+Room.WorldReplaceMap.get("time"));
                Thread.sleep(60000); // count by minutes
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void processActionList(){
        // System.out.println("processing Action list.");
		@SuppressWarnings("unchecked") // We know this Vector only contains that type. TODO: change vectors to lists.
		// Make a copy so that actions can remove themselves without concurrency issues.
		Vector<ActionTimed> ActionListCopy = (Vector<ActionTimed>) World.getArea().getActionList().clone();
        
        for (ActionTimed a : ActionListCopy){
        	if (a.processTimer() == 0){
        	    ActionHeader header = new ActionHeader (null,null,null,null);
        	    
        	    a.getTimedAction().perform(header);
        	}
        }
    }
    
    private void processRespawnList(){
        //System.out.println("processing respawn list.");
        Enumeration<respawnable> e = World.getArea().getRespawnList().elements();
        while (e.hasMoreElements()){
            respawnable r = (respawnable)e.nextElement();
            r.setRespawnTimeout(r.getRespawnTimeout() - 1);
            if (r.getRespawnTimeout() <= 0){
                r.respawn();
                if (r.respawnOnce()){
                	World.getArea().getRespawnList().remove(r);
                }
            }
        }
    }

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

	public boolean isTerminate() {
		return terminate;
	}
	
}
