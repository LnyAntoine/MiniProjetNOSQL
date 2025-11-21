package qengine;

import fr.boreal.model.logicalElements.api.Literal;
import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class utils {
    public static Literal<Integer> createLiteralFromInteger(Integer value) {
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }

    public static Literal<String> createLiteralFromString(String value) {
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }

    public static Literal<?> createLiteralFromObject(Object value) {
        if (value instanceof String) {
            return createLiteralFromString((String) value);
        } else if (value instanceof Integer) {
            return createLiteralFromInteger((Integer) value);
        }
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }

    public static Iterator<Substitution> intersectIterators2(List<Iterator<Substitution>> iterators) {
        ArrayList<Substitution> result = new ArrayList<>();
        if (iterators.isEmpty()) {
            return result.iterator();
        }
        Iterator<Substitution> iterator = iterators.getFirst();
        while (iterator.hasNext()) {
            Substitution sub = iterator.next();
            boolean found = true;
            for (int i = 1; i < iterators.size(); i++) {
                Iterator<Substitution> it = iterators.get(i);
                List<Substitution> subList = new ArrayList<>();
                while (it.hasNext()) {
                    Substitution s = it.next();
                    subList.add(s);
                    if (!s.equals(sub)) {
                        found = false;
                        break;
                    }
                }
                it = subList.iterator();
                iterators.set(i, it);
            }
            if (found) {
                result.add(sub);
            }
        }
        return result.iterator();
    }

    public static Iterator<Substitution> intersectIterators(List<Iterator<Substitution>> iterators) {
        ArrayList<List<Substitution>> list = new ArrayList<>();
        for (Iterator<Substitution> it : iterators) {
            List<Substitution> singleList = new ArrayList<>();
            while (it.hasNext()) {
                Substitution s = it.next();
                singleList.add(s);
            }
            list.add(singleList);
        }
        ArrayList<Substitution> result = new ArrayList<>();
        int minimalSizeIndex = 0;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).size() < list.get(minimalSizeIndex).size()) {
                minimalSizeIndex = i;
            }
        }
        for (Substitution s : list.get(minimalSizeIndex)) {
            boolean presentInAll = true;
            for (int i = 1; i < list.size(); i++) {
                if (!list.get(i).contains(s)) {
                    presentInAll = false;
                    break;
                }
            }
            if (presentInAll) {
                result.add(s);
            }
        }
        return result.iterator();
    }
}
