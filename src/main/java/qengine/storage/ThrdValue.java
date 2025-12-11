package qengine.storage;

import java.io.Serializable;
import java.util.Set;

public class ThrdValue implements Serializable {
    long stat;
    Set<Integer> set;
    public ThrdValue(long stat) {
        this.stat = stat;
    }
    public ThrdValue(Set<Integer> set) {
        this.set = set;
    }
}
