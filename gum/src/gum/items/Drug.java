package gum.items;

import gum.Player;

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
public interface Drug {

    public void takeDrug(Player p);
    public void effectPlayer(Player p);
    public void wearOff(Player p);

}
