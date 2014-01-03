package gum.items;

//import gum.FileHandler;
import gum.Player;

import java.util.StringTokenizer;

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
public class itemDrug extends ItemBase implements Drug {

    private int effectTimer = 0;

    public itemDrug() {
        super();
    }

/*    public itemDrug(String file) {
        super(file);
    }
*/
/*    
    public void save(String rootName) { // implement this
        super.save(rootName); // do all inherent saving

        FileHandler itemFile = new FileHandler();
        // String itemList = "";

        String fileName = rootName + "." + this.getItemName();
        itemFile.write_setting(fileName, "class", "itemdrug");
        itemFile.close_file();
    }
*/
      public void inspect(Player p) {
        super.inspect(p);
        p.broadcast("You have "+String.valueOf(this.getSetting("uses"))+" pills left.\r\n");

    }

    public boolean use(Player p, String command, StringTokenizer st) {
        // String leftOver = "";

        if (command.equalsIgnoreCase("use")){
            if (this.getSetting("uses") > 0){
                this.setSetting("uses",this.getSetting("uses")-1);
                this.takeDrug(p);
            } else{
                p.broadcast("the pill bottle is empty.\r\n");
            }
        }else{
            p.broadcast("you can't"+' '+command+" the "+this.getItemName()+". You should just use it!\r\n");
        }
        return true;
    }

    public void takeDrug(Player p) {
        p.broadcast("you put one pill in your mouth!\r\n");
        // if (!p.getItems().remove(this)) p.getRoom().removeItem(this);
        p.getDrugs().add(this);
        this.effectTimer = p.roll(5);
    }

    public void effectPlayer(Player p) {
        p.broadcast("The medicine seems to be taking effect\r\n");
        this.effectTimer = effectTimer - 1;
        p.setSetting("hitpoints",p.getSetting("hitpoints") + p.roll(10));
        if (effectTimer <= 0){
            this.wearOff(p);
        }
    }

    public void wearOff(Player p) {
        p.getDrugs().remove(this);
        p.broadcast("The medicine seems to have worne off.\r\n");
    }


}
