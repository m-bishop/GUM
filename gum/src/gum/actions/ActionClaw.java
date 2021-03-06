package gum.actions;

import gum.Player;
import gum.items.ItemBase;

public class ActionClaw extends ActionAttack {
	
	public ActionClaw(){
		this.setActionName("claw");
	}

	@Override
	public void perform(ActionHeader header) {
		
		this.setHeader(header);
		Player player = header.getPlayer();
		Player enemy = header.getTargetPlayer();
		ItemBase item = header.getItem();
	
		
    	String attackString = "";
    	int damageRoll = 3;
    	
    	attackString = (player.getPlayerName()+" clawed " +enemy.getPlayerName()+"!\r\n");

        int dex = player.getSetting("dex");
        int ref = enemy.getSetting("ref");

        if (dex < 1){
            dex = 1;
        }
        if (ref < 1){
            ref = 1;
        }
 

        // replace with player.roll
        int attack_roll = player.roll(dex); // random number from 1 to 20
        int defend_roll = enemy.roll(ref);


        System.out.println("attack roll:"+String.valueOf(attack_roll));
        System.out.println("defend roll:"+String.valueOf(defend_roll));
        int damage = player.roll(damageRoll)+1;
        System.out.println("damage:"+String.valueOf(damage));
        if(attack_roll > defend_roll){
            enemy.setSetting("hitpoints",enemy.getSetting("hitpoints") - damage);
            player.getCurrentRoom().chat(attackString+"\r\n");
            player.getCurrentRoom().chat(item.getHitString()+"\r\n");
        } else {
            player.getCurrentRoom().chat(item.getMissString()+"\r\n");     
        }		
	}
}
