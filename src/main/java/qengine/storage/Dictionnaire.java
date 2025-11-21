package qengine.storage;

import fr.boreal.model.logicalElements.api.Literal;
import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.impl.LiteralImpl;
import qengine.model.RDFTriple;

import java.util.HashMap;

import static qengine.utils.createLiteralFromObject;
import static qengine.utils.createLiteralFromString;

public class Dictionnaire {
    protected HashMap<String, Integer> tableEncodage;
    protected HashMap<Integer, String> tableDecodage;

    public Dictionnaire() {
        tableEncodage = new HashMap<>();
        tableDecodage = new HashMap<>();
    }

    protected RDFTriple encodeTripleQuery(RDFTriple triple) {
        encode(triple);
        Term sEncoded = triple.getTerm(0).isLiteral()
                ? createLiteralFromObject(getEncodage(triple.getTerm(0)))
                : triple.getTerm(0);
        Term pEncoded = triple.getTerm(1).isLiteral()
                ? createLiteralFromObject(getEncodage(triple.getTerm(1)))
                : triple.getTerm(1);
        Term oEncoded = triple.getTerm(2).isLiteral()
                ? createLiteralFromObject(getEncodage(triple.getTerms()[2]))
                : triple.getTerm(2);

        return new RDFTriple(sEncoded, pEncoded, oEncoded);
    }

    protected RDFTriple encode(RDFTriple triple) {
        for (Term term  : triple.getTerms()){
            if (term.isVariable()) {
                continue;
            }
            if (!tableEncodage.containsKey(term.label())){
                if (!tableDecodage.containsKey(tableEncodage.size())){
                    tableEncodage.put(term.label(),tableEncodage.size());
                    tableDecodage.put(tableEncodage.size()-1, term.label());
                } else {
                    //should not happen
                    return null;
                }
            }
        }
        return new RDFTriple(
                getEncodageAsTerm(triple.getTerms()[0].label()),
                getEncodageAsTerm(triple.getTerms()[1].label()),
                getEncodageAsTerm(triple.getTerms()[2].label())
        );
    }

    protected RDFTriple decode(RDFTriple triple) {
        Literal<String> s =
                (Literal<String>) createLiteralFromString(getDecodage(triple.getTerms()[0]));
        Literal<String> p =
                (Literal<String>) createLiteralFromString(getDecodage(triple.getTerms()[1]));
        Literal<String> o =
                (Literal<String>) createLiteralFromString(getDecodage(triple.getTerms()[2]));

        return new RDFTriple(s, p, o);
    }

    protected Integer getEncodage(String label) {
        return label.isEmpty() || tableEncodage.get(label)!=null ? tableEncodage.get(label) : -1;
    }

    protected Integer getEncodage(Term term) {
        String label = term.label();
        return label.isEmpty() || tableEncodage.get(label)!=null ? tableEncodage.get(label) : -1;
    }

    protected String getDecodage(Integer value) {
        return value == -1 || tableDecodage.get(value)!=null ? tableDecodage.get(value) : "";
    }

    protected String getDecodage(Term term) {
        Integer value = Integer.parseInt(term.label());
        return value == -1 || tableDecodage.get(value)!=null ? tableDecodage.get(value) : "";
    }

    protected Term getDecodageAsTerm(Integer value)
    {
        String label = getDecodage(value);
        return !label.isEmpty() ? createLiteralFromObject(label): null;
    }
    protected Term getEncodageAsTerm(String label)
    {
        Integer value = getEncodage(label);
        return value != -1 ? createLiteralFromObject(value): null;
    }
}
