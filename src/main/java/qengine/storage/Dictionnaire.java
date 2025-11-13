package qengine.storage;

import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.impl.LiteralImpl;
import fr.boreal.model.logicalElements.impl.VariableImpl;
import qengine.model.RDFTriple;

import java.util.ArrayList;
import java.util.HashMap;

public class Dictionnaire {
    protected HashMap<String, Integer> tableEncodage;
    protected HashMap<Integer, String> tableDecodage;
    protected RDFTriple encode(RDFTriple triple) {
        for (Term term  : triple.getTerms()){
            if (!tableEncodage.containsKey(term.label())){
                if (!tableDecodage.containsKey(tableEncodage.size())){
                    tableEncodage.put(term.label(),tableEncodage.size());
                    tableDecodage.put(tableEncodage.size(), term.label());
                }
                return null;
            }
        }
        return new RDFTriple(
                new LiteralImpl<Integer>(getEncodage(triple.getTerms()[0])),
                new LiteralImpl<Integer>(getEncodage(triple.getTerms()[1])),
                new LiteralImpl<Integer>(getEncodage(triple.getTerms()[2]))
        );
    }

    protected RDFTriple decode(RDFTriple triple) {
        LiteralImpl<String> s = new LiteralImpl<String>(getDecodage(triple.getTerms()[0]));
        LiteralImpl<String> p = new LiteralImpl<String>(getDecodage(triple.getTerms()[1]));
        LiteralImpl<String> o = new LiteralImpl<String>(getDecodage(triple.getTerms()[2]));
        return new RDFTriple(s, p, o);
    }

    protected Integer getEncodage(String label) {
        return tableEncodage.get(label);
    }

    protected Integer getEncodage(Term term) {
        String label = term.label();
        return tableEncodage.get(label);
    }

    protected String getDecodage(Integer value) {
        return tableDecodage.get(value);
    }

    protected String getDecodage(Term term) {
        Integer label = Integer.parseInt(term.label());
        return tableDecodage.get(label);
    }

}
