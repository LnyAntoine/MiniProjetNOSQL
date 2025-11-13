package qengine.storage;

import fr.boreal.model.logicalElements.api.*;
import fr.boreal.model.logicalElements.factory.api.TermFactory;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.model.logicalElements.impl.LiteralImpl;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import org.apache.commons.lang3.NotImplementedException;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;

import java.util.*;

/**
 * Implémentation d'un HexaStore pour stocker des RDFAtom.
 * Cette classe utilise six index pour optimiser les recherches.
 * Les index sont basés sur les combinaisons (Sujet, Prédicat, Objet), (Sujet, Objet, Prédicat),
 * (Prédicat, Sujet, Objet), (Prédicat, Objet, Sujet), (Objet, Sujet, Prédicat) et (Objet, Prédicat, Sujet).
 */
public class RDFHexaStore implements RDFStorage {
    private List<RDFTriple> storage;
    private Map<Integer,Map<Integer,Set<Integer>>> SPO;
    private Map<Integer,Map<Integer,Set<Integer>>> SOP;
    private Map<Integer,Map<Integer,Set<Integer>>> PSO;
    private Map<Integer,Map<Integer,Set<Integer>>> POS;
    private Map<Integer,Map<Integer,Set<Integer>>> OSP;
    private Map<Integer,Map<Integer,Set<Integer>>> OPS;
    private Dictionnaire dictionnaire;

    public RDFHexaStore() {
        storage = new ArrayList<>();
        dictionnaire = new Dictionnaire();
    }

    public void addSPO(Integer s, Integer o, Integer p) {
        if (!SPO.containsKey(s)) {
            SPO.put(s,new HashMap<>());
        }
        if (!SPO.get(s).containsKey(p)){
            SPO.get(s).put(p,new HashSet<>());
        }
        Set<Integer> set = SPO.get(s).get(p);
        set.add(o);
        SPO.get(s).put(p,set);
    }
    public void addSOP(Integer s, Integer o, Integer p) {
        if (!SPO.containsKey(s)) {
            SPO.put(s,new HashMap<>());
        }
        if (!SPO.get(s).containsKey(o)){
            SPO.get(s).put(p,new HashSet<>());
        }
        Set<Integer> set = SPO.get(s).get(o);
        set.add(p);
        SPO.get(s).put(o,set);
    }
    public void addPOS(Integer s, Integer o, Integer p) {
        if (!SPO.containsKey(p)) {
            SPO.put(p,new HashMap<>());
        }
        if (!SPO.get(p).containsKey(o)){
            SPO.get(p).put(o,new HashSet<>());
        }
        Set<Integer> set = SPO.get(p).get(o);
        set.add(s);
        SPO.get(p).put(o,set);
    }
    public void addPSO(Integer s, Integer o, Integer p) {
        if (!SPO.containsKey(p)) {
            SPO.put(p,new HashMap<>());
        }
        if (!SPO.get(p).containsKey(s)){
            SPO.get(p).put(s,new HashSet<>());
        }
        Set<Integer> set = SPO.get(p).get(s);
        set.add(o);
        SPO.get(p).put(s,set);
    }
    public void addOSP(Integer s, Integer o, Integer p) {
        if (!SPO.containsKey(o)) {
            SPO.put(o,new HashMap<>());
        }
        if (!SPO.get(o).containsKey(s)){
            SPO.get(o).put(s,new HashSet<>());
        }
        Set<Integer> set = SPO.get(o).get(s);
        set.add(p);
        SPO.get(o).put(s,set);
    }
    public void addOPS(Integer s, Integer o, Integer p) {
        if (!SPO.containsKey(o)) {
            SPO.put(o,new HashMap<>());
        }
        if (!SPO.get(o).containsKey(p)){
            SPO.get(o).put(p,new HashSet<>());
        }
        Set<Integer> set = SPO.get(o).get(p);
        set.add(s);
        SPO.get(o).put(p,set);
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

        Integer s = Integer.parseInt(triple.getTerms()[0].label());
        Integer o = Integer.parseInt(triple.getTerms()[1].label());
        Integer p = Integer.parseInt(triple.getTerms()[2].label());

        addSPO(s, o, p);
        addPOS(s, o, p);
        addSOP(s, o, p);
        addOPS(s, o, p);
        addOSP(s, o, p);
        addPSO(s, o, p);

        return true;
    }

    public ArrayList<Substitution> matchSPO(Term s, Term p, Term o){
        ArrayList<Substitution> substitutions = new ArrayList<>();
       Map<Integer, Set<Integer>> po_hashmap =  SPO.get(Integer.parseInt(s.label()));
       if (p.isLiteral()) {
           Set<Integer> o_set = po_hashmap.get(Integer.parseInt(p.label()));
           for (Integer i : o_set) {
               Substitution sub = new SubstitutionImpl();
               sub.add((Variable) o, dictionnaire.getDecodageAsTerm(i));
               substitutions.add(sub);
           }
       }else{
           for (Integer ip: po_hashmap.keySet()) {
               Set<Integer> o_set = po_hashmap.get(ip);
               for (Integer io : o_set) {
                   Substitution sub = new SubstitutionImpl();
                   sub.add((Variable) p, dictionnaire.getDecodageAsTerm(ip));
                   sub.add((Variable) o, dictionnaire.getDecodageAsTerm(io));
                   substitutions.add(sub);
               }
           }
       }
        return substitutions;
    }
    public ArrayList<Substitution> matchSOP(Term s, Term p, Term o){
        ArrayList<Substitution> substitutions = new ArrayList<>();
        Map<Integer, Set<Integer>> op_hashmap =  SOP.get(Integer.parseInt(s.label()));
        if (o.isLiteral()) {
            Set<Integer> p_set = op_hashmap.get(Integer.parseInt(o.label()));
            for (Integer i : p_set) {
                Substitution sub = new SubstitutionImpl();
                sub.add((Variable) p, dictionnaire.getDecodageAsTerm(i));
                substitutions.add(sub);
            }
        }else{
            for (Integer io: op_hashmap.keySet()) {
                Set<Integer> o_set = op_hashmap.get(io);
                for (Integer ip : o_set) {
                    Substitution sub = new SubstitutionImpl();
                    sub.add((Variable) o, dictionnaire.getDecodageAsTerm(io));
                    sub.add((Variable) p, dictionnaire.getDecodageAsTerm(ip));
                    substitutions.add(sub);
                }
            }
        }
        return substitutions;
    }

    public ArrayList<Substitution> matchOPS(Term s, Term p, Term o){
        ArrayList<Substitution> substitutions = new ArrayList<>();
        Map<Integer, Set<Integer>> ps_hashmap =  OPS.get(Integer.parseInt(o.label()));
        if (p.isLiteral()) {
            Set<Integer> s_set = ps_hashmap.get(Integer.parseInt(p.label()));
            for (Integer i : s_set) {
                Substitution sub = new SubstitutionImpl();
                sub.add((Variable) s, dictionnaire.getDecodageAsTerm(i));
                substitutions.add(sub);
            }
        }else{
            for (Integer ip: ps_hashmap.keySet()) {
                Set<Integer> s_set = ps_hashmap.get(ip);
                for (Integer is : s_set) {
                    Substitution sub = new SubstitutionImpl();
                    sub.add((Variable) p, dictionnaire.getDecodageAsTerm(ip));
                    sub.add((Variable) s, dictionnaire.getDecodageAsTerm(is));
                    substitutions.add(sub);
                }
            }
        }
        return substitutions;
    }


    public ArrayList<Substitution> matchPSO(Term s, Term p, Term o){
        ArrayList<Substitution> substitutions = new ArrayList<>();
        Map<Integer, Set<Integer>> so_hashmap =  PSO.get(Integer.parseInt(p.label()));
        if (s.isLiteral()) {
            Set<Integer> o_set = so_hashmap.get(Integer.parseInt(s.label()));
            for (Integer i : o_set) {
                Substitution sub = new SubstitutionImpl();
                sub.add((Variable) o, dictionnaire.getDecodageAsTerm(i));
                substitutions.add(sub);
            }
        }else{
            for (Integer is: so_hashmap.keySet()) {
                Set<Integer> o_set = so_hashmap.get(is);
                for (Integer io : o_set) {
                    Substitution sub = new SubstitutionImpl();
                    sub.add((Variable) s, dictionnaire.getDecodageAsTerm(is));
                    sub.add((Variable) o, dictionnaire.getDecodageAsTerm(io));
                    substitutions.add(sub);
                }
            }
        }
        return substitutions;
    }
    @Override
    public long size() {
        throw new NotImplementedException();
    }

    @Override
    public Iterator<Substitution> match(RDFTriple triple) {
        triple = dictionnaire.encode(triple);
        Term s = triple.getTerm(0);
        Term p = triple.getTerm(1);
        Term o = triple.getTerm(2);
        ArrayList<Substitution> substitutions = new ArrayList<>();

        //Cas de base requete vide ou "pleine"
        if (s.isVariable() && p.isVariable() && o.isVariable()) {
            //Va tout demander ?
            return substitutions.iterator();
        }
        else if (!s.isVariable() && !p.isVariable() && !o.isVariable()) {
            //Pas une requete ?
            return substitutions.iterator();
        }

        if (!s.isVariable()) {
            //Match dans S..
            if (!o.isVariable()) { //S littéral, O littéral, P doit etre variable
                //Match SOP
                substitutions = matchSOP(s, p, o);
            }
            else { //Si S est littéral O est variable, P est donc litteral (sinn géré pas cas de base)
                substitutions = matchSPO(s, p, o);
            }
        } else {
            if (!o.isVariable()) { // S est variable, O est littéral, P peut etre littéral ou variable
                substitutions = matchOPS(s, p, o);            }
            else { //S est variable, O est variable, p doit etre littéral
                substitutions = matchPSO(s, p, o);            }
        }
        return substitutions.iterator();
    }

    @Override
    public Iterator<Substitution> match(StarQuery q) {
        throw new NotImplementedException();
    }

    @Override
    public long howMany(RDFTriple triple) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<RDFTriple> getAtoms() {
        throw new NotImplementedException();
    }
}
