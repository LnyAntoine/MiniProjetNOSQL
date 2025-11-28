package qengine.storage;

import java.util.Set;

public class ThrdValue {
    Integer stat;
    Set<Integer> set;
    public ThrdValue(Integer stat) {
        this.stat = stat;
    }
    public ThrdValue(Set<Integer> set) {
        this.set = set;
    }
}
