package qengine.storage;

import java.util.Map;

public class SndValue {
    Integer stat;
    Map<Integer,ThrdValue> map;
    public SndValue(Integer stat) {
        this.stat = stat;
    }
    public SndValue(Map<Integer, ThrdValue> map) {
        this.map = map;
    }
}
