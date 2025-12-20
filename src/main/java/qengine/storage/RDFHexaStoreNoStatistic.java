package qengine.storage;

import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.api.Variable;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import org.eclipse.rdf4j.query.algebra.In;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;

import java.util.*;


/**
 * Implémentation d'un HexaStore pour stocker des RDFAtom.
 * Cette classe utilise six index pour optimiser les recherches.
 * Les index sont basés sur les combinaisons (Sujet, Prédicat, Objet), (Sujet, Objet, Prédicat),
 * (Prédicat, Sujet, Objet), (Prédicat, Objet, Sujet), (Objet, Sujet, Prédicat) et (Objet, Prédicat, Sujet).
 */
public class RDFHexaStoreNoStatistic implements RDFStorage {
    private final Map<Integer,Map<Integer,Set<Integer>>> SPO;
    private final Map<Integer,Map<Integer,Set<Integer>>> POS;
    private final Map<Integer,Map<Integer,Set<Integer>>> SOP;
    private final Map<Integer,Map<Integer,Set<Integer>>> PSO;
    private final Map<Integer,Map<Integer,Set<Integer>>> OSP;
    private final Map<Integer,Map<Integer,Set<Integer>>> OPS;
    private long size_SPO = 0;
    private long size_POS = 0;
    private long size_SOP = 0;
    private long size_PSO = 0;
    private long size_OSP = 0;
    private long size_OPS = 0;


    private final Dictionnaire dictionnaire;
    //private DB db;

    public String getType(){
        return "RDFHexaStoreNoStatistic";
    }

    public RDFHexaStoreNoStatistic() {

        dictionnaire = new Dictionnaire();

        SPO = new HashMap<>();
        SOP = new HashMap<>();
        PSO = new HashMap<>();
        POS = new HashMap<>();
        OSP = new HashMap<>();
        OPS = new HashMap<>();

    }


    public boolean addGeneric(Map<Integer, Map<Integer,Set<Integer>>> map, Integer fst, Integer snd, Integer thrd, String mapName) {
        if (!map.containsKey(fst)) {
            HashMap<Integer, Set<Integer>> fstValueHashMap = new HashMap<>();
            map.put(fst, fstValueHashMap);
        }
        if (!map.get(fst).containsKey(snd)) {
            HashSet<Integer> set = new HashSet<>();
            map.get(fst).put(snd, set);
        }

        Set<Integer> set = map.get(fst).get(snd);
        if (set.add(thrd)) { // Increment size only if the element is new
            incrementSize(mapName);
        }
        map.get(fst).put(snd, set);
        return true;
    }
    private void incrementSize(String mapName) {
        switch (mapName) {
            case "SPO":
                size_SPO++;
                break;
            case "POS":
                size_POS++;
                break;
            case "SOP":
                size_SOP++;
                break;
            case "PSO":
                size_PSO++;
                break;
            case "OSP":
                size_OSP++;
                break;
            case "OPS":
                size_OPS++;
                break;
            default:
                throw new IllegalArgumentException("Invalid map name: " + mapName);
        }
    }
    @Override
    public Iterator<Substitution> match(StarQuery q){
        return RDFStorage.super.matchWithoutStatistic(q);
    }

    public ArrayList<Substitution> matchGeneric(Map<Integer,Map<Integer,Set<Integer>>> map, Term fst, Term snd, Term thrd) {
        ArrayList<Substitution> substitutions = new ArrayList<>();
        if (fst == null || snd == null || thrd == null) {
            return substitutions;
        }
        if (!fst.isLiteral()) {
            return matchAll(fst, snd, thrd);
        }

        Map<Integer, Set<Integer>> snd_thrd_map =
                map.get(Integer.parseInt(fst.label())) != null
                        ? map.get(Integer.parseInt(fst.label()))
                        : new HashMap<>();
        if (snd_thrd_map.isEmpty()) {
            return substitutions;
        }
        if (snd.isLiteral()) {
            Set<Integer> thrd_set = snd_thrd_map.get(Integer.parseInt(snd.label())) != null
                    ? snd_thrd_map.get(Integer.parseInt(snd.label()))
                    : new HashSet<>();
            if (thrd_set.isEmpty()) {
                return substitutions;
            }
            for (Integer i : thrd_set) {
                Substitution sub = new SubstitutionImpl();
                sub.add((Variable) thrd, dictionnaire.getDecodageAsTerm(i));
                substitutions.add(sub);
            }
        } else {
            for (Integer is : snd_thrd_map.keySet()) {
                Set<Integer> thrd_set = snd_thrd_map.get(is);
                for (Integer io : thrd_set) {
                    Substitution sub = new SubstitutionImpl();
                    sub.add((Variable) snd, dictionnaire.getDecodageAsTerm(is));
                    sub.add((Variable) thrd, dictionnaire.getDecodageAsTerm(io));
                    substitutions.add(sub);
                }
            }
        }
        return substitutions;
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
        Integer p = Integer.parseInt(triple.getTerms()[1].label());
        Integer o = Integer.parseInt(triple.getTerms()[2].label());


        addGeneric(SPO, s, p, o, "SPO");
        addGeneric(POS, p, o, s, "POS");
        addGeneric(SOP, s, o, p, "SOP");
        addGeneric(OPS, o, p, s, "OPS");
        addGeneric(OSP, o, s, p, "OSP");
        addGeneric(PSO, p, s, o, "PSO");

        return true;
    }

    @Override
    public long size() {
        if (checkSynchronization()) return size_SOP;
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
                substitutions = matchGeneric(SOP,s,o,p);
            }
            else { //Si S est littéral O est variable, P est donc litteral (sinn géré pas cas de base)
                substitutions = matchGeneric(SPO,s,p,o);
            }
        } else {
            if (!o.isVariable()) {
                // S est variable, O est littéral, P peut etre littéral donc OPS
                substitutions = matchGeneric(OPS,o, p, s);
            }
            else { //S est variable, O est variable, p doit etre littéral donc PSO
                substitutions = matchGeneric(PSO,p,s,o);
        }
        }
        return substitutions.iterator();
    }


    @Override
    public long howMany(RDFTriple triple) {
        return -1;
    }

    @Override
    public Collection<RDFTriple> getAtoms() {
        ArrayList<RDFTriple> atoms = new ArrayList<>();
        for (Integer is : SPO.keySet()) {
            if (is.equals(-1)) {
                continue;
            }
            for (Integer ip : SPO.get(is).keySet()) {
                if (ip.equals(-1)) {
                    continue;
                }
                for (Integer io : SPO.get(is).get(ip)) {
                    if (io.equals(-1)) {
                        continue;
                    }
                    RDFTriple triple = new RDFTriple(
                            dictionnaire.getDecodageAsTerm(is),
                            dictionnaire.getDecodageAsTerm(ip),
                            dictionnaire.getDecodageAsTerm(io)
                    );
                    atoms.add(triple);
                }
            }
        }
        return atoms;
    }

    private ArrayList<Substitution> matchAll(Term s, Term p, Term o){
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

    private boolean checkSynchronization() {
        return size_SPO == size_POS &&
                size_POS == size_PSO &&
                size_PSO == size_SOP &&
                size_SOP == size_OPS &&
                size_OPS == size_OSP;
    }



}
