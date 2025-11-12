package qengine.storage;

import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.api.Variable;
import fr.boreal.model.logicalElements.factory.api.TermFactory;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GiantTableStore extends Dictionnaire implements RDFStorage{
    private List<RDFTriple> storage;
    public GiantTableStore() {
        storage = new ArrayList<>();
    }
    @Override
    public boolean add(RDFTriple triple) {
        triple = this.encode(triple);
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
        for (RDFTriple triple : storage) {
            Substitution s = matchTriple(a, triple);
            if (s!=null) {
                substitutions.add(s);
            }
        }
        return substitutions.iterator();
    }

    public Substitution matchTriple(RDFTriple request, RDFTriple triple) {
        Substitution s = new SubstitutionImpl();
        for (int i = 0;i<3;i++){
            Term term = request.getTerm(i);
            Term term2 = triple.getTerm(i);
            if (term instanceof Variable var) {
                s.add(var,term2);
            } else return null;
        }
        return s;
    }

    @Override
    public Iterator<Substitution> match(StarQuery q) {
        return null;
    }

    @Override
    public long howMany(RDFTriple a) {
        return 0;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public Collection<RDFTriple> getAtoms() {
        return List.of();
    }
}
