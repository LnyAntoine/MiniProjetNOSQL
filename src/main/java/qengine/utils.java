package qengine;

import fr.boreal.model.logicalElements.api.Literal;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;

public class utils {
    public static Literal<Integer> createLiteralFromInteger(Integer value) {
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }
    public Literal<String> createLiteralFromString(String value) {
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }
    public static Literal<?> createLiteralFromObject(Object value) {
        if  (value instanceof String) {
            return createLiteralFromString((String) value);
        }
        else if (value instanceof Integer) {
            return createLiteralFromInteger((Integer) value);
        }
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }
}
