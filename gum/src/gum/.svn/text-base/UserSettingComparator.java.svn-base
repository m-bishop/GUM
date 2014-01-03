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
import java.util.Comparator;

public class UserSettingComparator implements Comparator<Object>{
    String setting;

    public UserSettingComparator(String newSetting) {
        setting = newSetting;
    }

    public boolean equals(Object obj) {
        return false;
    }

    public int compare(Object o1, Object o2) {
        int setting1 = 0;
        int setting2 = 0;

        setting1 = ((User)o1).getSetting(setting);
        setting2 = ((User)o2).getSetting(setting);

        if(setting1 < setting2)return 1;
        if(setting2 < setting1)return -1;
        return 0;
    }
}
