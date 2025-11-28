package qengine.storage;

import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.api.Variable;
import fr.boreal.model.logicalElements.factory.api.TermFactory;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GiantTableStore implements RDFStorage{
    private List<RDFTriple> storage;
    private Dictionnaire dictionnaire;

    public GiantTableStore() {
        dictionnaire = new Dictionnaire();
        storage = new ArrayList<>();
    }

    @Override
    public boolean add(RDFTriple triple) {
        triple = dictionnaire.encode(triple);
        if (triple==null) {
            return false;
        }
        if (storage.contains(triple)) {
            return false;
        }
        storage.add(triple);
        return true;
    }

    @Override
    public Iterator<Substitution> match(RDFTriple a) {
        List<Substitution> substitutions = new ArrayList<>();
        a = dictionnaire.encodeTripleQuery(a);
        for (RDFTriple triple : storage) {
            Substitution s = matchTriple(a, triple);
            if (s!=null) {
                System.out.println("match found: " + s);
                substitutions.add(s);
            }
        }
        return substitutions.iterator();
    }

    public Substitution matchTriple(RDFTriple request, RDFTriple triple) {
        Substitution s = new SubstitutionImpl();

        System.out.println("Matching request: " + request + " with triple: " + triple);
        for (int i = 0; i < 3; i++) {
            Term termReq = request.getTerm(i);
            Term termDb = triple.getTerm(i);

            if (termReq.isVariable()) {
                s.add((Variable) termReq, dictionnaire.getDecodageAsTerm(Integer.parseInt(termDb.label())));


            } else {
                if (!termReq.equals(termDb)) {
                    System.out.println("no match on term: " + termReq + " vs " + termDb);
                    return null; // pas le même terme → pas de match
                }
            }
        }

        return s;
    }




    @Override
    public long howMany(RDFTriple a) {
        return 0;
    }

    @Override
    public long size() {
        int size = 0;
        for (RDFTriple triple : storage) {
            size++;
        }
        return size;
    }

    @Override
    public Collection<RDFTriple> getAtoms() {
        return List.of();
    }
}
