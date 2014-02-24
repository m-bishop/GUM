package gum.actions;

import gum.Player;
import gum.User;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.mobs.Mob;

public class ActionSpawnMob extends Action {
	
	private Mob createdMob;
	
	public ActionSpawnMob(){
	}
	
	public void init(){
		this.setActionName("Spawn Mob");
		this.setRange(EffectRange.USER);
		createdMob = null;
	}

	@Override
	public boolean doAction(ActionHeader actionHeader) {
		Player target = actionHeader.getTargetPlayer();
		//create a mob by telling the mob to respawn in the player room.
		if (createdMob.getRespawnCopy() == null){
			createdMob.respawnInit();
		}
		createdMob.setRespawnRoom(target.getCurrentRoom());
		createdMob.respawn();
		target.getCurrentRoom().chat(this.getSuccessMessage());
		createdMob.setRespawnRoom(null);
		createdMob.setRespawnCopy(null);// don't want the user holding on to this.
										
		return true;
	}

	@Override
	public void menu(User u) throws MenuExitException {
		
		String menuString = "Configure Spawn Mob Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure prereq setting\r\n";
		menuString += "(03) Configure range \r\n";
		menuString += "(04) Create new Mob to be spawned\r\n";
		menuString += "(05) Configure Mob to spawned \r\n";
		menuString += "(06) Configure success action \r\n";
		menuString += "(07) Configure success message \r\n";
		menuString += "(08) Display structure \r\n";
		menuString += "(09) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 9, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;				
			case 2:
				this.configActionPrereqSetting(u);
				break;
			case 3:
				this.configActionRange(u);
				break;
			case 4:
				createdMob = Mob.configAddMob(u);
				createdMob.setRespawnCopy(null); // don't want items holding these binary copies. 
				createdMob.setRespawnRoom(null);
				break;
			case 5:
				if (createdMob == null){
					createdMob = Mob.configAddMob(u);
					createdMob.setRespawnCopy(null); // don't want items holding these binary copies. 
					createdMob.setRespawnRoom(null);
				}
				if (createdMob != null){
					createdMob.menu(u);
				}
				break;
			case 6:
				this.configSuccessAction(u);
				break;
			case 7:
				this.configActionSuccessMessage(u);
				break;
			case 8: 
				u.broadcast(this.getStructure());
				break;
			case 9: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Spawn Mob Configuration Menu.\r\n\r\n");	

	}
	
	@Override
	public boolean configActionRange(User u){
		u.broadcast("Spawn Mob can only be used to spawn the mob in the target's \r\n " +
					"Current room.  \r\n");
		return false;	
	}
	
/*	public boolean configActionRange(User u) throws MenuExitException {
		boolean done = false;
		String menuString =  "This will configure the action's range. Be default, the action effects the target.";
			   menuString =  "All effects of this action will be handled individually.";
		       menuString += "Configure "+this.getActionName()+" range:\r\n";
		menuString += "(1) Item       - effects the Item that contains this action \r\n";
		menuString += "(2) TargetItem - effects the the item that you've used the action container on \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 9, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1: 
				this.setRange(EffectRange.ITEM);
				break;
			case 2: 
				this.setRange(EffectRange.TARGETITEM);
				break;
			}
		}
		return done;
	}

*/
	public Mob getCreatedMob() {
		return createdMob;
	}

	public void setCreatedMob(Mob createdMob) {
		this.createdMob = createdMob;
	}

	
	
}
