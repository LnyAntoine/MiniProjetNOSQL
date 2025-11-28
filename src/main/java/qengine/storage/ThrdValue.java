package qengine.storage;

import java.util.Set;

public class ThrdValue {
    long stat;
    Set<Integer> set;
    public ThrdValue(long stat) {
        this.stat = stat;
    }
    public ThrdValue(Set<Integer> set) {
        this.set = set;
    }
}
