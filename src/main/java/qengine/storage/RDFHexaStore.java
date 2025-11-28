package qengine.storage;

import fr.boreal.model.logicalElements.api.*;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import org.mapdb.DB;
import org.mapdb.DBMaker;
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
    /*
    private Map<Integer,Map<Integer,Set<Integer>>> SPO;
    private Map<Integer,Map<Integer,Set<Integer>>> SOP;
    private Map<Integer,Map<Integer,Set<Integer>>> PSO;
    private Map<Integer,Map<Integer,Set<Integer>>> POS;
    private Map<Integer,Map<Integer,Set<Integer>>> OSP;
    private Map<Integer,Map<Integer,Set<Integer>>> OPS;
     */
    private Map<Integer,SndValue> SPO;
    private Map<Integer,SndValue> POS;
    private Map<Integer,SndValue> SOP;
    private Map<Integer,SndValue> PSO;
    private Map<Integer,SndValue> OSP;
    private Map<Integer,SndValue> OPS;



    private Dictionnaire dictionnaire;
    private DB db;

    public RDFHexaStore() {

        dictionnaire = new Dictionnaire();
        db = DBMaker.memoryDB().make();

        SPO = new HashMap<>();
        SOP = new HashMap<>();
        PSO = new HashMap<>();
        POS = new HashMap<>();
        OSP = new HashMap<>();
        OPS = new HashMap<>();

        /*
        SPO = db.treeMap("SPO", Serializer.INTEGER, Serializer.JAVA).createOrOpen();
        SOP = db.treeMap("SOP", Serializer.INTEGER, Serializer.JAVA).createOrOpen();
        PSO = db.treeMap("PSO", Serializer.INTEGER, Serializer.JAVA).createOrOpen();
        POS = db.treeMap("POS", Serializer.INTEGER, Serializer.JAVA).createOrOpen();
        OSP = db.treeMap("OSP", Serializer.INTEGER, Serializer.JAVA).createOrOpen();
        OPS = db.treeMap("OPS", Serializer.INTEGER, Serializer.JAVA).createOrOpen();
         */
    }

    public void addGeneric(Map<Integer,SndValue> map, Integer fst, Integer snd, Integer thrd) {

        if (!map.containsKey(fst)) {
            HashMap<Integer,ThrdValue> fstValueHashMap = new HashMap<>();

            fstValueHashMap.put(-1,new ThrdValue(0));
            map.put(fst,new SndValue(fstValueHashMap));
            map.put(-1,new SndValue(0));

        }
        if (!map.get(fst).map.containsKey(snd)){
            HashSet <Integer> set = new HashSet<>();
            map.get(fst).map.put(snd,new ThrdValue(set));
            map.get(fst).map.put(-1,new ThrdValue(0)); //Nombre de snd pour ce fst

        }

        Set<Integer> set = map.get(fst).map.get(snd).set;
        set.add(thrd);
        map.get(fst).map.put(snd,new ThrdValue(set));
        map.get(fst).map.put(-1,new ThrdValue(map.get(fst).map.get(-1).stat + 1)); //Incrémenter le nombre de snd pour ce fst
    }

    public ArrayList<Substitution> matchGeneric(Map<Integer,SndValue> map, Term fst, Term snd, Term thrd) {
        ArrayList<Substitution> substitutions = new ArrayList<>();
        if (fst == null || snd == null || thrd == null) {
            return substitutions;
        }
        if (!fst.isLiteral()) {
            return matchAll(fst, snd, thrd);
        }

        Map<Integer, ThrdValue> snd_thrd_map =
                map.get(Integer.parseInt(fst.label())) != null
                        ? map.get(Integer.parseInt(fst.label())).map
                        : new HashMap<>();
        if (snd_thrd_map.isEmpty()) {
            return substitutions;
        }
        if (snd.isLiteral()) {
            Set<Integer> thrd_set = snd_thrd_map.get(Integer.parseInt(snd.label())) != null
                    ? snd_thrd_map.get(Integer.parseInt(snd.label())).set
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
                Set<Integer> thrd_set = snd_thrd_map.get(is).set;
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


        addGeneric(SPO,s,p,o);
        addGeneric(POS,p,o,s);
        addGeneric(SOP,s,o,p);
        addGeneric(OPS,o,p,s);
        addGeneric(OSP,o,s,p);
        addGeneric(PSO,p,s,o);

        /*
        addSPO(s, p, o);
        addPOS(s, p, o);
        addSOP(s, p, o);
        addOPS(s, p, o);
        addOSP(s, p, o);
        addPSO(s, p, o);

        addToSimpleStatistic(statisticS,s);
        addToSimpleStatistic(statisticP,p);
        addToSimpleStatistic(statisticO,o);

        addToDoubleStatistic(statisticSP,s,p);
        addToDoubleStatistic(statisticSO,s,o);
        addToDoubleStatistic(statisticOP,o,p);
        addToDoubleStatistic(statisticOS,o,s);
        addToDoubleStatistic(statisticPO,p,o);
        addToDoubleStatistic(statisticPS,p,s);

         */
        return true;
    }

    @Override
    public long size() {
        if (checkSynchronization()) return SPO.size();
        else return -1;
    }

    @Override
    public Iterator<Substitution> match(RDFTriple triple) {
        System.out.println("Matching triple: " + triple);
        System.out.println("SPO :");
        System.out.println(SPO.keySet());
        for (Integer key : SPO.keySet()) {
            System.out.println("    Key: " + key + ", Value: " + SPO.get(key));
        }
        ArrayList<Substitution> substitutions = new ArrayList<>();
        System.out.println();
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
    public Iterator<Substitution> match(StarQuery q) {
        return RDFStorage.super.match(q);
    }

    @Override
    public long howMany(RDFTriple triple) {
        Term s =  triple.getTerm(0);
        Term p = triple.getTerm(1);
        Term o = triple.getTerm(2);
        if (s.isVariable() && p.isVariable() && o.isVariable()) {
            //TODO return dict size
            return -1;
        }
        Integer sEncode = dictionnaire.getEncodage(s);
        Integer pEncode = dictionnaire.getEncodage(p);
        Integer oEncode = dictionnaire.getEncodage(o);
        Long howmany = 0L;
        //sEncode, pEncode et oEncode sont == -1 si variable ou si pas dans la base de donnée
        if (!sEncode.equals(-1)) {
            if (!pEncode.equals(-1)) {
                if (!oEncode.equals(-1)) {
                    howmany = SPO.get(sEncode)!=null
                            ? SPO.get(sEncode).map.get(pEncode)!=null
                                ? SPO.get(sEncode).map.get(pEncode).set.contains(oEncode)
                                    ? 1L
                                    :0L
                                : 0L
                            :0L;
                } else {
                    howmany = SPO.get(sEncode)!=null
                            ? SPO.get(sEncode).map.get(pEncode)!=null
                                ? (long) SPO.get(sEncode).map.get(-1).stat
                                : 0L
                            :0L;

                }
            }
            else {
                if (!oEncode.equals(-1)) {
                    howmany = SOP.get(sEncode)!=null
                            ? SOP.get(sEncode).map.get(oEncode)!=null
                                ? (long) SOP.get(sEncode).map.get(oEncode).stat
                                : 0L
                            :0L;
                }
                else {
                    howmany = SOP.get(sEncode)!=null
                            ? (long) SOP.get(sEncode).map.get(-1).stat
                            : 0L;
                }

            }
        } else {
            if (!pEncode.equals(-1)) {
                if (!oEncode.equals(-1)) {
                    howmany = POS.get(pEncode)!=null
                            ? POS.get(pEncode).map.get(oEncode)!=null
                                ? (long) POS.get(pEncode).map.get(oEncode).stat
                                : 0L
                            :0L;
                }
            } else {
                if (!oEncode.equals(-1)) {
                    howmany = OPS.get(oEncode)!=null
                            ? (long) OPS.get(oEncode).map.get(-1).stat
                            : 0L;
                }
            }
        }
        return howmany;
    }

    @Override
    public Collection<RDFTriple> getAtoms() {
        ArrayList<RDFTriple> atoms = new ArrayList<>();
        for (Integer is : SPO.keySet()) {
            for (Integer ip : SPO.get(is).map.keySet()) {
                for (Integer io : SPO.get(is).map.get(ip).set) {
                    //SameObjectTermFactory.instance().createOrGetLiteral();
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

    private ArrayList<Substitution> matchAll(Term s, Term p, Term o){;
        ArrayList<Substitution> substitutions = new ArrayList<>();
        if (!s.isVariable() || !p.isVariable() || !o.isVariable()) {
            return substitutions;
        }
        for (Integer is : SPO.keySet()) {
            Map<Integer, ThrdValue> po_hashmap = SPO.get(is).map;
            for (Integer ip : po_hashmap.keySet()) {
                Set<Integer> o_set = po_hashmap.get(ip).set;
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
        return SPO.size()== POS.size() &&
                SPO.size() == SOP.size() &&
                SPO.size() == OPS.size() &&
                SPO.size() == OSP.size() &&
                SPO.size() == PSO.size();
    }



}
