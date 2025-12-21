package qengine.storage;

import java.util.Map;
import java.util.Set;


//Objet représentant une donnée pour l'hexastore
public class DataValue {
    private long LongValue;
    private Map<Integer, DataValue> MapValue;
    private Set<Integer> SetValue;
    private boolean isLong;
    private boolean isSet;
    private boolean isMap;
    public DataValue(long longValue) {
        LongValue = longValue;
        isLong = true;
    }
    public DataValue(Map<Integer,DataValue> MapValue) {
        this.MapValue = MapValue;
        isMap = true;
    }
    public DataValue(Set<Integer> SetValue) {
        this.SetValue = SetValue;
        isSet = true;
    }
    public boolean isLong() {
        return isLong;
    }
    public boolean isMap() {
        return isMap;
    }
    public boolean isSet() {
        return isSet;
    }
    public long getLongValue() {
        return LongValue;
    }
    public Map<Integer, DataValue> getMapValue() {
        return MapValue;
    }
    public boolean addToSet(int value) {
        return SetValue.add(value);
    }
    public boolean addToMap(int key, DataValue value) {
        if (MapValue.containsKey(key)) {
            return false;
        }
        MapValue.put(key, value);
        return true;
    }
    public Set<Integer> getSetValue() {
        return SetValue;
    }
}
