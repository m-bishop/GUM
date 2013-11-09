package gum.items;

import java.util.HashMap;

import gum.actions.*;

//import gum.FileHandler;

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

public class itemSimple extends ItemBase {
	
	public itemSimple(){
		
		// TODO remove after testing
		
		ActionStab stab = new ActionStab();
		HashMap<String, Action> actions = this.getActions();
		
		actions.put("stab",stab);
		this.getActions().put("slash",new ActionSlash());
		this.getActions().put("claw",(new ActionClaw()));
	}

/*	
    public itemSimple(String load_file) {
        super(load_file);
    }

    public void save(String rootName) { // implement this
        super.save(rootName); // do all inherent saving

        FileHandler itemFile = new FileHandler();
        //String itemList = "";

        String fileName = rootName + "." + this.getItemName();
        itemFile.write_setting(fileName, "class", "simpleitem");
        itemFile.close_file();
    }

*/    
    
}
