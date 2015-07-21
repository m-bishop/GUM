package gum.mobs;

import java.util.StringTokenizer;

import gum.User;

public class MobWanderingMonster extends Mob {

	public MobWanderingMonster(){
		super();
	}
	
	@Override
    public void broadcast(String message) {
		System.out.println("Mob heard:"+message);
        String token = "";
       

        StringTokenizer st = new StringTokenizer(message, " ");
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
	
	@Override
	public void fight() {
		  
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startFight() {
		// System.out.println(this.getPlayerName()+" Called start fight.\r\n");
		User victim = this.getCurrentRoom().getRandomPlayer(this);
		if (victim != null && !victim.inMenu){
		      parser.ParseMobCommand(this, "attack " + victim.getPlayerName());
		  }
	}

	
	
}
