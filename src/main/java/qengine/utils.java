package qengine;

import fr.boreal.model.logicalElements.api.Literal;
import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;

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

    public static Iterator<Substitution> intersectTwoIterators(Iterator<Substitution> it1, Iterator<Substitution> it2) {
        ArrayList<Substitution> list1 = new ArrayList<>();
        ArrayList<Substitution> result = new ArrayList<>();
        while (it1.hasNext()) {
            Substitution s = it1.next();
            list1.add(s);
        }
        while (it2.hasNext()) {
            Substitution s = it2.next();
            if (list1.contains(s)) {
                result.add(s);
            }
        }
        return result.iterator();
    }
}
