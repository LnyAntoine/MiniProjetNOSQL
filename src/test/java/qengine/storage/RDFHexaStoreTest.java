package qengine.storage;

import fr.boreal.model.formula.api.FOFormulaConjunction;
import fr.boreal.model.kb.api.FactBase;
import fr.boreal.model.logicalElements.api.*;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import fr.boreal.model.query.api.FOQuery;
import fr.boreal.storage.natives.SimpleInMemoryGraphStore;
import org.apache.commons.lang3.NotImplementedException;
import qengine.model.RDFTriple;
import org.junit.jupiter.api.Test;
import qengine.model.StarQuery;
import qengine.program.Example;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static qengine.program.Example.*;

/**
 * Tests unitaires pour la classe {@link RDFHexaStore}.
 */
public class RDFHexaStoreTest {
    private static final Literal<String> SUBJECT_1 = SameObjectTermFactory.instance().createOrGetLiteral("subject1");
    private static final Literal<String> PREDICATE_1 = SameObjectTermFactory.instance().createOrGetLiteral("predicate1");
    private static final Literal<String> OBJECT_1 = SameObjectTermFactory.instance().createOrGetLiteral("object1");
    private static final Literal<String> SUBJECT_2 = SameObjectTermFactory.instance().createOrGetLiteral("subject2");
    private static final Literal<String> PREDICATE_2 = SameObjectTermFactory.instance().createOrGetLiteral("predicate2");
    private static final Literal<String> OBJECT_2 = SameObjectTermFactory.instance().createOrGetLiteral("object2");
    private static final Literal<String> OBJECT_3 = SameObjectTermFactory.instance().createOrGetLiteral("object3");
    private static final Variable VAR_X = SameObjectTermFactory.instance().createOrGetVariable("?x");
    private static final Variable VAR_Y = SameObjectTermFactory.instance().createOrGetVariable("?y");
    private static final String WORKING_DIR = "data/";
    private static final String SAMPLE_DATA_FILE = WORKING_DIR + "sample_data.nt";
    private static final String SAMPLE_QUERY_FILE = WORKING_DIR + "sample_query.queryset";

    @Test
    public void testAddAllRDFAtoms() {
        RDFHexaStore store = new RDFHexaStore();

        // Version stream
        // Ajouter plusieurs RDFAtom
        RDFTriple rdfAtom1 = new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1);
        RDFTriple rdfAtom2 = new RDFTriple(SUBJECT_2, PREDICATE_2, OBJECT_2);

        Set<RDFTriple> rdfAtoms = Set.of(rdfAtom1, rdfAtom2);

        assertTrue(store.addAll(rdfAtoms.stream()), "Les RDFAtoms devraient être ajoutés avec succès.");

        // Vérifier que tous les atomes sont présents
        Collection<RDFTriple> atoms = store.getAtoms();

        assertTrue(atoms.contains(rdfAtom1), "La base devrait contenir le premier RDFAtom ajouté.");
        assertTrue(atoms.contains(rdfAtom2), "La base devrait contenir le second RDFAtom ajouté.");

        // Version collection
        store = new RDFHexaStore();
        assertTrue(store.addAll(rdfAtoms), "Les RDFAtoms devraient être ajoutés avec succès.");

        // Vérifier que tous les atomes sont présents
        atoms = store.getAtoms();
        assertTrue(atoms.contains(rdfAtom1), "La base devrait contenir le premier RDFAtom ajouté.");
        assertTrue(atoms.contains(rdfAtom2), "La base devrait contenir le second RDFAtom ajouté.");
    }

    @Test
    public void testAddRDFAtom() {
        RDFHexaStore store = new RDFHexaStore();

        // Version stream
        // Ajouter plusieurs RDFAtom
        RDFTriple rdfAtom1 = new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1);
        RDFTriple rdfAtom2 = new RDFTriple(SUBJECT_2, PREDICATE_2, OBJECT_2);

        assertTrue(store.add(rdfAtom1), "Le RDFAtom1 devrait être ajouté avec succès.");
        assertTrue(store.add(rdfAtom2), "Le RDFAtom2 devrait être ajouté avec succès.");

        // Vérifier que tous les atomes sont présents
        Collection<RDFTriple> atoms = store.getAtoms();
        System.out.println(atoms);
        System.out.println(rdfAtom1);
        System.out.println(rdfAtom2);

        assertTrue(atoms.contains(rdfAtom1), "La base devrait contenir le premier RDFAtom ajouté.");
        assertTrue(atoms.contains(rdfAtom2), "La base devrait contenir le second RDFAtom ajouté.");

    }

    @Test
    public void testAddDuplicateAtom() {
        RDFHexaStore store = new RDFHexaStore();

        RDFTriple rdfAtom1 = new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1);
        RDFTriple rdfAtom2 = new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1);

        assertTrue(store.add(rdfAtom1), "Le RDFAtom1 devrait être ajouté avec succès.");
        assertTrue(store.add(rdfAtom2), "Le RDFAtom2 devrait être ajouté avec succès.");
        assertEquals(1, store.size(), "Taille devrait être égale à 1");

    }

    @Test
    public void testSize() {
        RDFHexaStore store = new RDFHexaStore();

        // Version stream
        // Ajouter plusieurs RDFAtom
        RDFTriple rdfAtom1 = new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1);
        RDFTriple rdfAtom2 = new RDFTriple(SUBJECT_2, PREDICATE_2, OBJECT_2);
        assertTrue(store.add(rdfAtom1), "Le RDFAtom1 devrait être ajouté avec succès.");
        assertTrue(store.add(rdfAtom2), "Le RDFAtom2 devrait être ajouté avec succès.");
        assertTrue(store.size() == 2, "Taille devrait être à égale à 2");
    }

    @Test
    public void testMatchAtom() {
        RDFHexaStore store = new RDFHexaStore();
        store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1)); // RDFAtom(subject1, triple, object1)
        store.add(new RDFTriple(SUBJECT_2, PREDICATE_1, OBJECT_2)); // RDFAtom(subject2, triple, object2)
        store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_3)); // RDFAtom(subject1, triple, object3)

        // Case 1
        RDFTriple matchingAtom = new RDFTriple(SUBJECT_1, PREDICATE_1, VAR_X); // RDFAtom(subject1, predicate1, X)
        Iterator<Substitution> matchedAtoms = store.match(matchingAtom);
        System.out.println("matchedAtoms");
        System.out.println(matchedAtoms);
        List<Substitution> matchedList = new ArrayList<>();
        matchedAtoms.forEachRemaining(matchedList::add);

        Substitution firstResult = new SubstitutionImpl();
        firstResult.add(VAR_X, OBJECT_1);
        Substitution secondResult = new SubstitutionImpl();
        secondResult.add(VAR_X, OBJECT_3);
        System.out.println(matchedList.size());
        System.out.println(firstResult);
        System.out.println(secondResult);
        System.out.println(matchedList);

        assertEquals(2, matchedList.size(), "There should be two matched RDFAtoms");
        assertTrue(matchedList.contains(secondResult), "Missing substitution: " + firstResult);
        assertTrue(matchedList.contains(secondResult), "Missing substitution: " + secondResult);

        // Other cases
    }

    @Test
    public void testMatchStarQuery() {
        RDFHexaStore store = new RDFHexaStore();
        store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_1)); // RDFAtom(subject1, triple, object1)
        store.add(new RDFTriple(SUBJECT_2, PREDICATE_1, OBJECT_2)); // RDFAtom(subject2, triple, object2)
        store.add(new RDFTriple(SUBJECT_1, PREDICATE_1, OBJECT_3)); // RDFAtom(subject1, triple, object3)


        RDFTriple triple1 = new RDFTriple(VAR_X, PREDICATE_1, OBJECT_1);
        RDFTriple triple2 = new RDFTriple(VAR_X, PREDICATE_1, OBJECT_3);

        List<RDFTriple> rdfAtoms = List.of(triple1, triple2);
        Collection<Variable> answerVariables = List.of(VAR_X);

        StarQuery query = new StarQuery("Requête étoile valide", rdfAtoms, answerVariables);

        Iterator<Substitution> matchedAtoms = store.match(query);
        Substitution firstResult = new SubstitutionImpl();
        firstResult.add(VAR_X, SUBJECT_1);

        assertTrue(matchedAtoms.hasNext(), "Missing substitution: " + firstResult);
        assertTrue(matchedAtoms.next().equals(firstResult), "Missing substitution: " + firstResult);


    }


    @Test
    public void test_verify() throws IOException {
        System.out.println("=== Parsing RDF Data ===");
        List<RDFTriple> rdfAtoms = parseRDFData(SAMPLE_DATA_FILE);

        System.out.println("\n=== Parsing Sample Queries ===");
        List<StarQuery> starQueries = parseSparQLQueries(SAMPLE_QUERY_FILE);

        RDFHexaStore store = new RDFHexaStore();
        FactBase factBase = new SimpleInMemoryGraphStore();

        store.addAll(rdfAtoms);

        for (RDFTriple triple : rdfAtoms) {
            factBase.add(triple);  // Stocker chaque RDFAtom dans le store
        }

        for (StarQuery starQuery : starQueries) {
            Collection<Substitution> integraal_result = new ArrayList<>();
            executeStarQuery(starQuery, factBase).forEachRemaining(integraal_result::add);

            Collection<Substitution> matchedAtoms = new ArrayList<>();
            store.match(starQuery).forEachRemaining(matchedAtoms::add);

            if (!matchedAtoms.equals(integraal_result)) {
                System.out.println("helowezf");
//                while(matchedAtoms.hasNext() && integraal_result.hasNext()) {
//                    System.out.println("gmoqrg");
//                    System.out.println(matchedAtoms.equals(integraal_result));
//                    System.out.println(integraal_result.equals(matchedAtoms));
//                    Substitution firstResult = (Substitution) matchedAtoms.next();
//                    Substitution secondResult = (Substitution) integraal_result.next();
//                    System.out.println(firstResult +" - "+secondResult);
//                    System.out.println(firstResult.equals(secondResult));
//                    System.out.println(secondResult.equals(firstResult));
//                    System.out.println(integraal_result.hasNext() +" - "+matchedAtoms.hasNext());
//                }

            }
        }
    }

    // Vos autres tests d'HexaStore ici
}
