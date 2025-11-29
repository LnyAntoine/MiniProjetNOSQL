package qengine.storage;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

import fr.boreal.model.logicalElements.api.Substitution;
import fr.lirmm.graphik.graal.api.core.AtomComparator;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.utils;

/**
 * Contrat pour un système de stockage de données RDF
 */
public interface RDFStorage {

    /**
     * Ajoute un RDFAtom dans le store.
     *
     * @param t le triplet à ajouter
     * @return true si le RDFAtom a été ajouté avec succès, false s'il est déjà présent
     */
    boolean add(RDFTriple t);


    /**
     * @param a atom
     * @return un itérateur de substitutions correspondant aux match des atomes
     *          (i.e., sur quels termes s'envoient les variables)
     */
    Iterator<Substitution> match(RDFTriple a);

    /**
     * @param q star query
     * @return an itérateur de subsitutions décrivrant les réponses à la requete
     */
    default Iterator<Substitution> match(StarQuery q){
        try {
            q.getCentralVariable();

            List<RDFTriple> sortedAtoms = sortAtoms(q.getRdfAtoms());
            RDFTriple triple = sortedAtoms.getFirst();
            Iterator<Substitution> it = match(triple);
            for (int i = 1; i < sortedAtoms.size() && it.hasNext(); i++) {
                RDFTriple t = sortedAtoms.get(i);
                Iterator<Substitution> it2 = match(t);
                if (!it2.hasNext()) {
                    it = new ArrayList<Substitution>().iterator();
                    break;
                }
                it = utils.intersectTwoIterators(it, it2);
            }
            return it;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Substitution>().iterator();
        }
    }

    default List<RDFTriple> sortAtoms(List<RDFTriple> atomsList){
        List<RDFTriple> sortedAtoms = atomsList.stream().sorted(
                (o1, o2) -> Long.compare(howMany(o1), howMany(o2))
        ).toList();
        return sortedAtoms;
    }

    /**
     * @param a atom
     * @return
     */

    default long howMany(RDFTriple a){
        return -1;
    }


    /**
     * Retourne le nombre d'atomes dans le Store.
     *
     * @return le nombre d'atomes
     */
    long size();

    /**
     * Retourne une collections contenant tous les atomes du store.
     * Utile pour les tests unitaires.
     *
     * @return une collection d'atomes
     */
    Collection<RDFTriple> getAtoms();

    /**
     * Ajoute des RDFAtom dans le store.
     *
     * @param atoms les RDFAtom à ajouter
     * @return true si au moins un RDFAtom a été ajouté, false s'ils sont tous déjà présents
     */
    default boolean addAll(Stream<RDFTriple> atoms) {
        return atoms.map(this::add).reduce(Boolean::logicalOr).orElse(false);
    }

    /**
     * Ajoute des RDFAtom dans le store.
     *
     * @param atoms les RDFAtom à ajouter
     * @return true si au moins un RDFAtom a été ajouté, false s'ils sont tous déjà présents
     */
    default boolean addAll(Collection<RDFTriple> atoms) {
        return this.addAll(atoms.stream());
    }
}
