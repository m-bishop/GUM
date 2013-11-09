package gum;

import gum.items.Item;
import gum.items.ItemBase;
import gum.menus.MenuContainer;
import gum.menus.MenuExitException;
import gum.menus.PromptForBoolean;
import gum.menus.PromptForInteger;
import gum.menus.PromptForString;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Vector;

public class CharacterClass implements MenuContainer{
	
	private String name = "";
	private String description = null;
	
	private Vector<Item> items = new Vector<Item>();
	private Vector<CharacterClass> subClasses = new Vector<CharacterClass>();
	private static Vector<CharacterClass> classLibrary = new Vector<CharacterClass>();
	
	HashMap<String,Integer> startSetting = new HashMap<String,Integer>();
	
	public CharacterClass(){
		setName("DEFAULT");
	}
	
	public void menu(User u) throws MenuExitException{
		if (u.getSetting("builder") != 0){
			configMenu(u);
		}else {
			addClassToUser(u);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addClassToUser(User u) throws MenuExitException{
		u.broadcast(this.getDescription());
		u.getSettings().putAll((HashMap<String,Integer>)deepCopy(this.getStartSetting()));
		u.getItems().addAll((Vector<Item>)deepCopy(items));
		if (!subClasses.isEmpty()){
			displaySubClass(u);
		}else{
			u.broadcast("Character Creation Complete!\r\nType 'look' to look at your surroundings!\r\n");
		}
	}
	
	public Object deepCopy(Object baseObject){
		//could probably do this with 1 byte array.
		Object result = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(os);
        encoder.writeObject(baseObject);
        encoder.close();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        XMLDecoder decoder = new XMLDecoder(is);
        result = decoder.readObject();
        return result;
	}
	
	public void displaySubClass(User u) throws MenuExitException{
		int count = 0;
		 String menuString = "Choose a subClass from the list below:\r\n";
		
		for (CharacterClass c: subClasses){
			count++;
			menuString += "("+count+") "+c.getName()+"\r\n";			
		}
		
		PromptForInteger i = new PromptForInteger(u, menuString, subClasses.size(), 0);
		if (i.display()) {
			subClasses.get(i.getResult()-1).addClassToUser(u);
		}
	}
	
	
	public void configMenu(User u) throws MenuExitException{
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "(1) Configure class name \r\n";
		menuString += "(2) Configure class Description \r\n";
		menuString += "(3) Configure class Settings \r\n";
		menuString += "(4) Configure class Items\r\n";
		menuString += "(5) Configure sub-class Choices \r\n";
		menuString += "Choose from the above. Type 'exit' to exit the menu.\r\n";
		
		PromptForInteger p = new PromptForInteger(u, menuString, 5, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				this.configCharacterClassName(u);
				break;
			case 2:
				this.configCharacterClassDescription(u);
				break;	
			case 3:
				this.configClassSettings(u);
				break;
			case 4:
				this.configClassItems(u);
				break;
			case 5:
				this.configSubClasses(u);
				break;
			}
		}
		u.broadcast("\r\nExiting Class Configuration Menu.\r\n\r\n");	
	}
	
	public boolean configSubClasses(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Configure Class subclasses:\r\n";
		menuString += "(1) Configure existing subClasses. \r\n";
		menuString += "(2) Add a new subClass. \r\n";
		menuString += "(3) Remove a subClass. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configSubClass(u);
				break;
			case 2: 
				configAddSubClass(u);
				break;
			case 3: 
				configSubClassRemove(u); 
				break;
			}
		}
		return done;
	}
	
	public void configSubClassRemove(User u) throws MenuExitException{
		int count = 0;
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Choose a subClass to remove:\r\n";
		
		for (CharacterClass c: subClasses){
			menuString += "("+count+") "+c.getName()+"\r\n";
			count++;			
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger i = new PromptForInteger(u, menuString, items.size()-1, 0);
		if (i.display()) {
			CharacterClass removedClass = subClasses.remove(i.getResult());
			PromptForBoolean b = new PromptForBoolean(u,"Remove this from the subClass Library as well?\r\n");
			b.display();
			if (b.getResult()){
				CharacterClass.classLibrary.remove(removedClass);
			}
		}
	}
	
	public Item configAddSubClass(User u) throws MenuExitException {
		Item result = null;
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Add a new subClass:\r\n";
		menuString += "(1) Create new subClass \r\n";
		menuString += "(2) Add a subClass from the subClass Library\r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 2, 1);
		if (p.display()) {
			switch (p.getResult()) {
			case 1:
				CharacterClass newSubClass = new CharacterClass();
				this.subClasses.add(newSubClass);
				CharacterClass.classLibrary.add(newSubClass);
				u.broadcast("New subClass created with the name 'DEFAULT'");
				break;
			case 2: 
				;
				break;
			}
		}
		return result;
	}	
	
	public void configSubClass(User u) throws MenuExitException{
		int count = 0;
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Choose a subClass to configure:\r\n";
		
		for (CharacterClass c: subClasses){
			menuString += "("+count+") "+c.getName()+"\r\n";
			count++;			
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger i = new PromptForInteger(u, menuString, subClasses.size()-1, 0);
		if (i.display()) {
			subClasses.elementAt(i.getResult()).configMenu(u);
		}
	}
	
	
	
	public boolean configClassItems(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Configure Class Items:\r\n";
		menuString += "(1) Configure existing items. \r\n";
		menuString += "(2) Add a new item. \r\n";
		menuString += "(3) Remove an item. \r\n";
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configItem(u);
				break;
			case 2: 
				Item i = ItemBase.configAddItem(u);
				if (i != null){
					items.add(i);
				}
				break;
			case 3: 
				this.configItemDelete(u); 
				break;
			}
		}
		return done;
	}	
	
	public void configItem(User u) throws MenuExitException{
		int count = 0;
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Choose an item to configure:\r\n";
		Vector<Item> items = this.getItems();
		
		for (Item i: items){
			menuString += "("+count+") "+i.getItemName()+"\r\n";
			count++;			
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger i = new PromptForInteger(u, menuString, items.size()-1, 0);
		if (i.display()) {
			items.elementAt(i.getResult()).menu(u);
		}
	}
	
	public void configItemDelete(User u) throws MenuExitException{
		
		int count = 0;
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Choose an item to delete:\r\n";
		
		for (Item i: this.getItems()){
				menuString += "("+count+") "+i.getItemName()+"\r\n";
				count++;
		}
		
		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, items.size()-1, 0);
		if (p.display()) {
			items.remove(p.getResult());
			u.broadcast("Selected item removed from class.\r\n");
		} else {
			u.broadcast("Room unchanged.\r\n");
		}
	}
	
	public void configCharacterClassName(User u) throws MenuExitException {
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Enter a new Class name.\r\n";
		menuString += "Current action name:" + this.getName() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			this.setName(s.getResult());
			u.broadcast("New Class name:\r\n" + this.getName() + "\r\n");
		} else {
			u.broadcast("Class name unchanged. \r\n");
		}
	}
	
	public void configCharacterClassDescription(User u) throws MenuExitException {
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Enter a new character class description to be displayed after the class is chosen.\r\n";
		menuString += "Current character class description:\r\n"
				+ this.getDescription() + "\r\n";
		PromptForString s = new PromptForString(u, menuString);
		String result = "";

		while (s.display()) {
			result += s.getResult();
			result += "\r\n";
			menuString = "New Description:\r\n"
					+ result
					+ "\r\n\r\n Type another line to add to the description, 'exit' to quit.\r\n";
			s.setMenuOptions(menuString);
		}
		this.setDescription(result);
	}
	
	public boolean configClassSettings(User u) throws MenuExitException {
		boolean done = false;
		String menuString = "Configure Class:"+name+"\r\n";
		menuString += "Configure character class settings:\r\n";
		menuString += "(1) List character class settings. \r\n";
		menuString += "(2) Add a new setting. \r\n";
		menuString += "(3) Remove a setting. \r\n";

		menuString += "Choose from the above. Type 'exit' to return to the previous menu.\r\n";
		PromptForInteger p = new PromptForInteger(u, menuString, 3, 1);
		while (p.display()) {
			switch (p.getResult()) {
			case 1:
				configListStartSettings(u);
				break;
			case 2: 
				configAddStartSetting(u);
				break;
			case 3: 
				configRemoveStartSetting(u);
				break;
			}
		}
		return done;
	}		
	
	public void configListStartSettings(User u){
		String settingMenuString = "Configure Class:"+name+"\r\n";
	    settingMenuString += "Currently this class has the following starting settings:\r\n\r\n";
	       
	       for (String s : startSetting.keySet()){
	    	   settingMenuString += "setting:"+s+" value:"+startSetting.get(s)+"\r\n";   
	       }
	       u.broadcast(settingMenuString);
	}
	
	public void configAddStartSetting(User u) throws MenuExitException{

		int newSettingValue = 0;
		String settingMenuString = "Configure Class:"+name+"\r\n";
	    settingMenuString +=  "This will add a setting to this character class.\r\n";
			   settingMenuString += "If the user already has this setting, it will be replaced.\r\n";
		       settingMenuString += "Currently this class has the following starting settings:\r\n\r\n";
		       
		       for (String s : startSetting.keySet()){
		    	   settingMenuString += "setting:"+s+" value:"+startSetting.get(s)+"\r\n";
		    	   
		       }
		
		u.broadcast(settingMenuString);
		
		String newSettingName = getSettingName(u);
		if (!newSettingName.equals("")){
			newSettingValue = this.getSettingValue(u);
			startSetting.put(newSettingName, newSettingValue);
		}
	}	
	
	public void configRemoveStartSetting(User u) throws MenuExitException{
		
		   Object[] keyArray= startSetting.keySet().toArray();
	       String menuString = "Choose a setting to remove, or type 'exit':\r\n\r\n";
	       int count = 0;
	       
	       for (Object s : keyArray){
	    	   count++;
	    	   menuString += "("+count+") setting:"+((String)s)+" value:"+startSetting.get(s)+"\r\n";   
	       }
	       PromptForInteger p = new PromptForInteger(u, menuString,keyArray.length+1,1);
	       if (p.display()){
	    	   String removed = (String)keyArray[(p.getResult()-1)];
	    	   u.broadcast("Removing:"+removed+"\r\n");
	    	   startSetting.remove(removed);
	       }
	}	
	
	
	public String getSettingName(User u) throws MenuExitException {
		String result = "";
		String menuString = "Enter a setting name.\r\n\r\n";
		
		PromptForString s = new PromptForString(u, menuString);
		boolean done = s.display();

		if (done) {
			result = s.getResult();
		} 
		return result;
	}	
    
    public int getSettingValue(User u) throws MenuExitException{
    	int result = 0;
    	String menuString = "Enter a new setting value.\r\n\r\n";
    	       
    	PromptForInteger i = new PromptForInteger(u,menuString);
    	boolean done = i.display();
    	if (done){
    		result = (i.getResult());
    	}
    	return result;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Vector<Item> getItems() {
		return items;
	}

	public void setItems(Vector<Item> items) {
		this.items = items;
	}

	public Vector<CharacterClass> getSubClasses() {
		return subClasses;
	}

	public void setSubClasses(Vector<CharacterClass> subClasses) {
		this.subClasses = subClasses;
	}

	public static Vector<CharacterClass> getClassLibrary() {
		return classLibrary;
	}

	public static void setClassLibrary(Vector<CharacterClass> classLibrary) {
		CharacterClass.classLibrary = classLibrary;
	}

	public HashMap<String, Integer> getStartSetting() {
		return startSetting;
	}

	public void setStartSetting(HashMap<String, Integer> startSetting) {
		this.startSetting = startSetting;
	}     

}
