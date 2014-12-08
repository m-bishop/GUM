package gum.actions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;

import gum.Player;
import gum.Room;
import gum.User;
import gum.World;
import gum.items.ItemBase;
import gum.menus.MenuExitException;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

public class ActionModify extends Action {
	
	private int roll;
	private int base;
	private boolean broadcast;
	private String modSetting;
	private String baseSetting;
	private Operator operator;
	
	public ActionModify(){
	}
	
	public void init(){
		this.setActionName("Modify");
		this.setRange(EffectRange.USER);
		roll = 0;
		base = 0;
		broadcast = false;
		modSetting = "";
		baseSetting = "";
		operator = Operator.REPLACE;		
		
	}
	
	public enum Operator {
		ADD,SUBTRACT,REPLACE
	}

	@Override
	public void performByRange(){
		
		ActionHeader header = this.getHeader();
		Player target = header.getTargetPlayer();
		Player player = header.getPlayer();
		ItemBase item = header.getItem();
		ItemBase targetItem = header.getTargetItem();
		int bSetting = player.getSetting(baseSetting);
		
		switch (this.getRange()) {	
		case TARGET: // in this case, the user performs the action on the target.
			if (target == null){
				player.broadcast("Who do you want to do this to? (Example: Use my bandage on bill).\r\n");
			}else {
				performOnTarget();
			}
			break;
		case USER: // in this case, the user performs the action on his self. 
			//this.getHeader().setTargetPlayer(player);
			performOnTarget();
			break;
		case ROOM: // Changes the room setting.
			Room cRoom = player.getCurrentRoom();
			int newRoomValue = this.getModifiedSetting(cRoom.getSetting(modSetting),bSetting, base, player.roll(roll));
			cRoom.setSetting(modSetting, newRoomValue);
			player.broadcast(this.getSuccessMessage());
			if (this.getSuccessAction() != null){
				this.getSuccessAction().perform(this.getHeader());
			}
			break;
		case WORLD: // Changes the world setting.
			gum.Area cArea = World.getArea();
			Random random = new Random();
			int rolled = 0;
	    	if (roll > 0){
	    		rolled = random.nextInt(roll)+1;
	    	}
			int newAreaValue = this.getModifiedSetting(cArea.getSetting(modSetting), bSetting, base, rolled);
			cArea.setSetting(modSetting, newAreaValue);
			if (player != null){// timed world actions have no player
				player.broadcast(this.getSuccessMessage());
			}
			if (this.getSuccessAction() != null){
				this.getSuccessAction().perform(this.getHeader());
			}
			break;
		case ENEMIES: //User performs action on each mob in the room.
			performOnEnemies();
			break;
		case ALLIES: //User performs action on each player in the room.
			performOnAllies();
			break;
		case BATTLE: //User performs action on each player in the battle.
			performOnBattle();
			break;
		case ITEM: //User performs action on the Item.
			int newItemValue = this.getModifiedSetting(target.getSetting(modSetting), bSetting, base, player.roll(roll));
			item.setSetting(modSetting, newItemValue);
			player.broadcast(this.getSuccessMessage());
			if (this.getSuccessAction() != null){
				this.getSuccessAction().perform(this.getHeader());
			}
			break;
		case TARGETITEM: //User performs action on the Item.
			if (targetItem == null){
				player.broadcast("What do you want to do this to? Example: 'Use my key on the door'\r\n");
			}else {
				int newTargetItemValue = this.getModifiedSetting(targetItem.getSetting(modSetting), bSetting,base, player.roll(roll));
				targetItem.setSetting(modSetting, newTargetItemValue);
				player.broadcast(this.getSuccessMessage());
				if (this.getSuccessAction() != null){
					this.getSuccessAction().perform(this.getHeader());
				}
				System.out.println(targetItem.getName()+" setting modified:"+modSetting+","+newTargetItemValue);
			}
			break;
		}
	}
	
	
	public int getModifiedSetting(int settingValue,int baseSetting, int base, int roll){
		int result = 0;
		switch (this.operator) {
		case ADD: 
			result = settingValue + (baseSetting+base+roll);
			break;
		case SUBTRACT: 
			result = settingValue - (baseSetting+base+roll);
			break;
		case REPLACE: 
			result = baseSetting+base+roll;
			break;
		
		}
		System.out.println("ActionModify>>getModifiedSetting:Setting:"+baseSetting+" base:"+base+" roll:"+roll+" result:"+result);
		return result;
	}
	
	@Override
	public boolean doAction(ActionHeader actionHeader) {
		Player target = actionHeader.getTargetPlayer();
		int rolled = target.roll(roll);
		int bSetting = 0;
				
		if (baseSetting.equalsIgnoreCase("*today")){
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			bSetting = Integer.valueOf(dateFormat.format(date));
		}
		else {
			bSetting = target.getSetting(baseSetting);
		}
		
		System.out.println("ActionModify>>doAction: bSetting:<name>"+baseSetting+"bSetting<value>:"+bSetting);
		
		if (this.broadcast){
			target.getCurrentRoom().chat(target.getPlayerName()+" rolled "+rolled+" on a "+this.getRoll()+" sided die.\r\n");
		}
		int newValue = this.getModifiedSetting(target.getSetting(modSetting),bSetting, base,rolled );
		target.setSetting(modSetting, newValue);
		target.broadcast(this.getSuccessMessage());
		return true;
	}
	
	@Override
	public void menu(User u) throws MenuExitException {
		String menuString = "Configure Modify Action:\r\n";
		menuString += "(01) Configure name \r\n";
		menuString += "(02) Configure base setting (Base setting)+(Item base damage)+(BASE+(1->roll) = damage) \r\n";
		menuString += "(03) Configure base (Base setting)+(Item base damage)+(BASE+(1->roll) = damage) \r\n";
		menuString += "(04) Configure roll (Base setting)+(Item base damage)+(base+(1->ROLL) = damage)\r\n";
		menuString += "(05) Configure setting to be modified \r\n";
		menuString += "(06) Configure modification operator (Add, Subtract, Replace) \r\n";
		menuString += "(07) Configure prereq setting\r\n";
		menuString += "(08) Configure range \r\n";
		menuString += "(09) Configure result broadcast flag \r\n";
		menuString += "(10) Configure success action \r\n";
		menuString += "(11) Configure success message\r\n";
		menuString += "(12) Display structure \r\n";
		menuString += "(13) Save \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 13, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configActionName(u);
				break;
			case 2: 
				this.configBaseSetting(u);
				break;
			case 3:
				this.configBase(u);
				break;				
			case 4:
				this.configRoll(u);
				break;
			case 5:
				this.configModSetting(u);
				break;
			case 6:
				this.configOperator(u);
				break;
			case 7:
				this.configActionPrereqSetting(u);
				break;
			case 8:
				this.configActionRange(u);
				break;
			case 9:
				this.configBroadcastFlag(u);
				break;
			case 10:
				this.configSuccessAction(u);
				break;
			case 11: 
				this.configActionSuccessMessage(u);
				break;
			case 12:
				u.broadcast(this.getStructure());
				break;
			case 13: 
				this.configActionSave(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Modify Configuration Menu.\r\n\r\n");	
	}
	
	public void configBaseSetting(User u) throws MenuExitException{

		String settingMenuString =  "This will configure the base setting to be used by this action.\r\n";
			   settingMenuString += "the new setting will be equal to the THIS+Base + (1->Roll).\r\n";
		       settingMenuString += "Currently this Action's base setting is:"+this.baseSetting+"\r\n";
		       settingMenuString += "Enter '*today', and zero for base and roll, to set to today's date. \r\n";
		       settingMenuString += "Enter a new setting:\r\n";
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (!newSettingName.equals("")){
			this.baseSetting = newSettingName;
		}
	}
	
	public void configBroadcastFlag(User u) throws MenuExitException{
		
		String menuString =  "This will set a flag for broadcasting the result of the roll.\r\n";
			   menuString +=  "If it is set to true, it will broadcast result of the roll to the room.\r\n";
			   menuString +=  "This was created with MUSH style Role Playing games in mind. It only works when \r\n";
			   menuString +=  "targeting a player.\r\n";
			   menuString += "Current setting:"+broadcast+"\r\n\r\n";
			   menuString += "Enter 'true' or 'false' to modify broadcast flag:\r\n";
				PromptForString s = new PromptForString(u, menuString);
				
				s.display();
				String result = s.getResult();
				if (result.equalsIgnoreCase("true")){
					broadcast = true;
					u.broadcast("Broadcast setting changed to 'true'.\r\n");
				}else if (result.equalsIgnoreCase("false")){
					broadcast = false;
					u.broadcast("Broadcast setting changed to 'false'.\r\n");
				}else {
					u.broadcast("Expected 'true' or 'false', setting unchanged.\r\n");
				} 
	}
	
	public void configBase(User u) throws MenuExitException {
		
		String menuString = "What will the Base for this Action be, in the equation (ex. (Roll)+Base)= new setting?\r\n\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString);
		if (p.display()) {
			this.setBase(p.getResult());
			u.broadcast("Base:" + this.base + "\r\n");
		} else {
			u.broadcast("Base unchanged!\r\n");
		}
	}
	
	public void configRoll(User u) throws MenuExitException {
		
		String menuString = "What will the Roll for this Action be, in the equation (ex. (1->Damage Roll)+Damage Base)?\r\n\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString);
		if (p.display()) {
			this.setRoll(p.getResult());
			u.broadcast("New Roll:" + this.roll + "\r\n");
		} else {
			u.broadcast("Roll unchanged!\r\n");
		}
	}

	public void configModSetting(User u) throws MenuExitException{

		String settingMenuString =  "This will configure a setting to be modified by this action.\r\n";
			   settingMenuString += "the new setting will be equal to the Base + (1->Roll).\r\n";
		       settingMenuString += "Currently this Action's modification setting is:"+this.getModSetting()+"\r\n";
		       settingMenuString += "Enter a new setting:\r\n";
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (!newSettingName.equals("")){
			this.setModSetting(newSettingName);
		}
	}
	
	public boolean configOperator(User u) throws MenuExitException {
		boolean done = false;
		String menuString =  "This will configure the action's Operator. By default, modification replaces";
			   menuString =  "the old setting. This allows you to choose to add or subtract the effect of this action.";
		       menuString += "Enter the number of the new operator below:\r\n";
		menuString += "(1) Add    \r\n";
		menuString += "(2) Subtract \r\n";
		menuString += "(3) Replace   \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 7, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				operator = Operator.ADD;
				break;
			case 2: 
				operator = Operator.SUBTRACT;
				break;
			case 3: 
				operator = Operator.REPLACE;
				break;
			}
		}
		return done;
	}	
	
	public int getRoll() {
		return roll;
	}

	public void setRoll(int roll) {
		this.roll = roll;
	}

	public String getBaseSetting() {
		return baseSetting;
	}

	public void setBaseSetting(String baseSetting) {
		this.baseSetting = baseSetting;
	}
	
	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public String getModSetting() {
		return modSetting;
	}

	public void setModSetting(String modSetting) {
		this.modSetting = modSetting;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}



	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}



	public boolean isBroadcast() {
		return broadcast;
	}
	
}
