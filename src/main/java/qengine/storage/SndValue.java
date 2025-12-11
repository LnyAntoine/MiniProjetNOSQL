package qengine.storage;

import java.io.Serializable;
import java.util.Map;

public class SndValue implements Serializable {
    long stat;
    Map<Integer,ThrdValue> map;
    public SndValue(long stat) {
        this.stat = stat;
    }
    public SndValue(Map<Integer, ThrdValue> map) {
        this.map = map;
    }
}
