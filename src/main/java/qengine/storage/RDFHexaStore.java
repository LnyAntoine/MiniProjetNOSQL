package qengine.storage;

import fr.boreal.model.logicalElements.api.*;
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
    private Map<Integer,Map<Integer,Set<Integer>>> SPO;
    private Map<Integer,Map<Integer,Set<Integer>>> SOP;
    private Map<Integer,Map<Integer,Set<Integer>>> PSO;
    private Map<Integer,Map<Integer,Set<Integer>>> POS;
    private Map<Integer,Map<Integer,Set<Integer>>> OSP;
    private Map<Integer,Map<Integer,Set<Integer>>> OPS;
    private Dictionnaire dictionnaire;


    public RDFHexaStore() {
        dictionnaire = new Dictionnaire();
        SPO = new HashMap<>();
        SOP = new HashMap<>();
        PSO = new HashMap<>();
        POS = new HashMap<>();
        OSP = new HashMap<>();
        OPS = new HashMap<>();
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
        if (!SOP.containsKey(s)) {
            SOP.put(s,new HashMap<>());
        }
        if (!SOP.get(s).containsKey(o)){
            SOP.get(s).put(o,new HashSet<>());
        }
        Set<Integer> set = SOP.get(s).get(o);
        set.add(p);
        SOP.get(s).put(o,set);
    }
    public void addPOS(Integer s, Integer o, Integer p) {
        if (!POS.containsKey(p)) {
            POS.put(p,new HashMap<>());
        }
        if (!POS.get(p).containsKey(o)){
            POS.get(p).put(o,new HashSet<>());
        }
        Set<Integer> set = POS.get(p).get(o);
        set.add(s);
        POS.get(p).put(o,set);
    }
    public void addPSO(Integer s, Integer o, Integer p) {
        if (!PSO.containsKey(p)) {
            PSO.put(p,new HashMap<>());
        }
        if (!PSO.get(p).containsKey(s)){
            PSO.get(p).put(s,new HashSet<>());
        }
        Set<Integer> set = PSO.get(p).get(s);
        set.add(o);
        PSO.get(p).put(s,set);
    }
    public void addOSP(Integer s, Integer o, Integer p) {
        if (!OSP.containsKey(o)) {
            OSP.put(o,new HashMap<>());
        }
        if (!OSP.get(o).containsKey(s)){
            OSP.get(o).put(s,new HashSet<>());
        }
        Set<Integer> set = OSP.get(o).get(s);
        set.add(p);
        OSP.get(o).put(s,set);
    }
    public void addOPS(Integer s, Integer o, Integer p) {
        if (!OPS.containsKey(o)) {
            OPS.put(o,new HashMap<>());
        }
        if (!OPS.get(o).containsKey(p)){
            OPS.get(o).put(p,new HashSet<>());
        }
        Set<Integer> set = OPS.get(o).get(p);
        set.add(s);
        OPS.get(o).put(p,set);
    }

    @Override
    public boolean add(RDFTriple triple) {
        triple = dictionnaire.encode(triple);
        if (triple==null) {
            return false;
        }

        if (triple.getTerm(0)==null ||
            triple.getTerm(1)==null ||
            triple.getTerm(2)==null) {
            return false;
        }

        if (triple.getTerm(0).isVariable() ||
            triple.getTerm(1).isVariable() ||
            triple.getTerm(2).isVariable()) {
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
        System.out.println("Added triple: " + triple);
        System.out.println(SPO.keySet());
        for (Integer key : SPO.keySet()) {
            System.out.println("    Key: " + key + ", Value: " + SPO.get(key));
        }

        return true;
    }

    @Override
    public long size() {
        if (checkSynchronization()) return SPO.size();
        else return -1;
    }

    @Override
    public Iterator<Substitution> match(RDFTriple triple) {
        ArrayList<Substitution> substitutions = new ArrayList<>();

        if (triple==null) {
            return substitutions.iterator();
        }
        if (triple.getTerm(0)==null ||
            triple.getTerm(1)==null ||
            triple.getTerm(2)==null) {
            return substitutions.iterator();
        }

        triple = dictionnaire.encodeTripleQuery(triple);
        Term s = triple.getTerm(0);
        Term p = triple.getTerm(1);
        Term o = triple.getTerm(2);

        //Cas de base requete vide ou "pleine"
        if (s.isVariable() && p.isVariable() && o.isVariable()) {
            //Va tout demander ?
            return matchAll(s,p,o).iterator();
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
            if (!o.isVariable()) {
                // S est variable, O est littéral, P peut etre littéral donc OPS
                substitutions = matchOPS(s, p, o);
            }
            else { //S est variable, O est variable, p doit etre littéral donc PSO
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
        ArrayList<RDFTriple> atoms = new ArrayList<>();
        for (Integer is : SOP.keySet()) {
            for (Integer io : SOP.get(is).keySet()) {
                for (Integer ip : SOP.get(is).get(io)) {
                    RDFTriple triple = new RDFTriple(
                            dictionnaire.getDecodageAsTerm(is),
                            dictionnaire.getDecodageAsTerm(io),
                            dictionnaire.getDecodageAsTerm(ip)
                    );
                    atoms.add(triple);
                }
            }
        }
        return atoms;
    }

    private ArrayList<Substitution> matchAll(Term s, Term p, Term o){;
        ArrayList<Substitution> substitutions = new ArrayList<>();
        if (!s.isVariable() || !p.isVariable() || !o.isVariable()) {
            return substitutions;
        }
        for (Integer is : SPO.keySet()) {
            Map<Integer, Set<Integer>> po_hashmap = SPO.get(is);
            for (Integer ip : po_hashmap.keySet()) {
                Set<Integer> o_set = po_hashmap.get(ip);
                for (Integer io : o_set) {
                    Substitution sub = new SubstitutionImpl();
                    sub.add((Variable) s, dictionnaire.getDecodageAsTerm(is));
                    sub.add((Variable) p, dictionnaire.getDecodageAsTerm(ip));
                    sub.add((Variable) o, dictionnaire.getDecodageAsTerm(io));
                    substitutions.add(sub);
                }
            }
        }

        return substitutions;
    }

    private ArrayList<Substitution> matchSPO(Term s, Term p, Term o){
        ArrayList<Substitution> substitutions = new ArrayList<>();
        if (s == null || p == null || o == null) {
            return substitutions;
        }
        if (!s.isLiteral()) {
            return substitutions;
        }
        Map<Integer, Set<Integer>> po_hashmap =
                SPO.get(Integer.parseInt(s.label()))!=null
                        ? SPO.get(Integer.parseInt(s.label()))
                        : new HashMap<>();
        if (SPO.isEmpty()) {
            return substitutions;
        }
        if (p.isLiteral()) {
            Set<Integer> o_set = po_hashmap.get(Integer.parseInt(p.label()))!=null
                    ? po_hashmap.get(Integer.parseInt(p.label()))
                    : new HashSet<>();
            if (o_set.isEmpty()) {
                return substitutions;
            }
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

    private ArrayList<Substitution> matchSOP(Term s, Term p, Term o){
        ArrayList<Substitution> substitutions = new ArrayList<>();
        if (s == null || p == null || o == null) {
            return substitutions;
        }
        if (!s.isLiteral()) {
            return substitutions;
        }
        Map<Integer, Set<Integer>> op_hashmap =  SOP.get(Integer.parseInt(s.label()))!=null?
                SOP.get(Integer.parseInt(s.label()))
                : new HashMap<>();
        if (SOP.isEmpty()) {
            return substitutions;
        }
        if (o.isLiteral()) {
            Set<Integer> p_set = op_hashmap.get(Integer.parseInt(o.label()))!=null
                    ? op_hashmap.get(Integer.parseInt(o.label()))
                    : new HashSet<>();
            if (p_set.isEmpty()) {
                return substitutions;
            }
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

    private ArrayList<Substitution> matchOPS(Term s, Term p, Term o){
        ArrayList<Substitution> substitutions = new ArrayList<>();
        if (o == null || p == null || s == null) {
            return substitutions;
        }
        if (!o.isLiteral()) {
            return substitutions;
        }
        Map<Integer, Set<Integer>> ps_hashmap =  OPS.get(Integer.parseInt(o.label()))!=null?
                OPS.get(Integer.parseInt(o.label()))
                : new HashMap<>();
        if (OPS.isEmpty()) {
            return substitutions;
        }
        if (p.isLiteral()) {
            Set<Integer> s_set = ps_hashmap.get(Integer.parseInt(p.label()))!=null
                    ? ps_hashmap.get(Integer.parseInt(p.label()))
                    : new HashSet<>();
            if (s_set.isEmpty()) {
                return substitutions;
            }
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

    private ArrayList<Substitution> matchPSO(Term s, Term p, Term o){
        ArrayList<Substitution> substitutions = new ArrayList<>();
        if (p == null || s == null || o == null) {
            return substitutions;
        }
        if (!p.isLiteral()) {
            return substitutions;
        }
        Map<Integer, Set<Integer>> so_hashmap =
                PSO.get(Integer.parseInt(p.label()))!=null
                        ? PSO.get(Integer.parseInt(p.label()))
                        : new HashMap<>();
        if (PSO.isEmpty()) {
            return substitutions;
        }
        if (s.isLiteral()) {
            Set<Integer> o_set = so_hashmap.get(Integer.parseInt(s.label()))!=null
                    ? so_hashmap.get(Integer.parseInt(s.label()))
                    : new HashSet<>();
            if (o_set.isEmpty()) {
                return substitutions;
            }
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

    private boolean checkSynchronization() {
        return SPO.size()== POS.size() &&
                SPO.size() == SOP.size() &&
                SPO.size() == OPS.size() &&
                SPO.size() == OSP.size() &&
                SPO.size() == PSO.size();
    }

}
