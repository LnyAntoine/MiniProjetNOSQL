package qengine.storage;

import org.junit.jupiter.api.Test;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;

import java.util.List;

import static qengine.program.Example.parseRDFData;
import static qengine.program.Example.parseSparQLQueries;
import static qengine.utils.utils.findOrCreateFile;

public class BenchGiantTableTest {

    private static final String WORKING_DIR = "data/";
    private static final String RSQ_SUBSET_DIR = "refined_queries_concatenated/";
    private static final String DEGREE_DIR = "refined_queries_degree/";

    private static final String DATA_2M_FILE = WORKING_DIR + "data2M.nt";
    private static final String DATA_500K_FILE = WORKING_DIR + "500K.nt";
    private static final String DATA_100K_FILE = WORKING_DIR + "100K.nt";


    private static final String DEGREE_1_FILE = "refined_queries_degree_1.queryset";
    private static final String DEGREE_1_FILE_PATH = WORKING_DIR + DEGREE_DIR + DEGREE_1_FILE;

    private static final String DEGREE_3_4_FILE = "refined_queries_degree_3_and_4.queryset";
    private static final String DEGREE_3_4_FILE_PATH = WORKING_DIR + DEGREE_DIR + DEGREE_3_4_FILE;

    private static final String LITTLE_SUBSET_FILE = "little_subset.queryset";
    private static final String LITTLE_SUBSET_FILE_PATH =WORKING_DIR + RSQ_SUBSET_DIR + LITTLE_SUBSET_FILE;
    private static final String AVERAGE_SUBSET_FILE = "average_subset.queryset";
    private static final String AVERAGE_SUBSET_FILE_PATH =WORKING_DIR + RSQ_SUBSET_DIR + AVERAGE_SUBSET_FILE;
    private static final String BIG_SUBSET_FILE = "big_subset.queryset";
    private static final String BIG_SUBSET_FILE_PATH= WORKING_DIR + RSQ_SUBSET_DIR + BIG_SUBSET_FILE;


    @Test
    public void benchmarkMatchLittleSubset() {
        // Implémentation du benchmark à venir
        try {
            RDFStorage storage = new GiantTableStore();
            findOrCreateFile(LITTLE_SUBSET_FILE_PATH);
            findOrCreateFile(DATA_500K_FILE);
            List<RDFTriple> rdfTriples = parseRDFData(DATA_500K_FILE);
            List<StarQuery> starQueries = parseSparQLQueries(LITTLE_SUBSET_FILE_PATH);

            Long startTime = System.nanoTime();
            for (RDFTriple triple : rdfTriples) {
                storage.add(triple);  // Stocker chaque RDFAtom dans le store
            }
            for (StarQuery query : starQueries) {
                storage.match(query);
            }
            Long endTime = System.nanoTime();
            Long duration = endTime - startTime;
            System.out.println("Durée totale pour le petit sous-ensemble: " + duration + " nanosecondes");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void benchmarkMatchAverageSubset() {
        // Implémentation du benchmark à venir
        try {
            RDFStorage storage = new GiantTableStore();
            findOrCreateFile(AVERAGE_SUBSET_FILE_PATH);
            findOrCreateFile(DATA_500K_FILE);
            List<RDFTriple> rdfTriples = parseRDFData(DATA_500K_FILE);
            List<StarQuery> starQueries = parseSparQLQueries(AVERAGE_SUBSET_FILE_PATH);

            Long startTime = System.nanoTime();
            for (RDFTriple triple : rdfTriples) {
                storage.add(triple);  // Stocker chaque RDFAtom dans le store
            }
            for (StarQuery query : starQueries) {
                storage.match(query);
            }
            Long endTime = System.nanoTime();
            Long duration = endTime - startTime;
            System.out.println("Durée totale pour le moyen sous-ensemble: " + duration + " nanosecondes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void benchmarkMatchBigSubset() {
        // Implémentation du benchmark à venir
        try {
            RDFStorage storage = new GiantTableStore();
            findOrCreateFile(BIG_SUBSET_FILE_PATH);
            findOrCreateFile(DATA_500K_FILE);
            List<RDFTriple> rdfTriples = parseRDFData(DATA_500K_FILE);
            List<StarQuery> starQueries = parseSparQLQueries(BIG_SUBSET_FILE_PATH);

            Long startTime = System.nanoTime();
            for (RDFTriple triple : rdfTriples) {
                storage.add(triple);  // Stocker chaque RDFAtom dans le store
            }
            for (StarQuery query : starQueries) {
                storage.match(query);
            }
            Long endTime = System.nanoTime();
            Long duration = endTime - startTime;
            System.out.println("Durée totale pour le gros sous-ensemble: " + duration + " nanosecondes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void benchMarkMatchDegree1() {
        // Implémentation du benchmark à venir
        try {
            RDFStorage storage = new GiantTableStore();
            findOrCreateFile(DEGREE_1_FILE);
            findOrCreateFile(DATA_500K_FILE);
            List<RDFTriple> rdfTriples = parseRDFData(DATA_500K_FILE);
            List<StarQuery> starQueries = parseSparQLQueries(DEGREE_1_FILE);

            Long startTime = System.nanoTime();
            for (RDFTriple triple : rdfTriples) {
                storage.add(triple);  // Stocker chaque RDFAtom dans le store
            }
            for (StarQuery query : starQueries) {
                storage.match(query);
            }
            Long endTime = System.nanoTime();
            Long duration = endTime - startTime;
            System.out.println("Durée totale pour le gros sous-ensemble: " + duration + " nanosecondes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void benchMarkMatchDegree3and4() {
        // Implémentation du benchmark à venir

        try {
            RDFStorage storage = new RDFHexaStoreNoStatistic();
            findOrCreateFile(DEGREE_3_4_FILE);
            findOrCreateFile(DATA_500K_FILE);
            List<RDFTriple> rdfTriples = parseRDFData(DATA_500K_FILE);
            List<StarQuery> starQueries = parseSparQLQueries(DEGREE_3_4_FILE);

            Long startTime = System.nanoTime();
            for (RDFTriple triple : rdfTriples) {
                storage.add(triple);  // Stocker chaque RDFAtom dans le store
            }
            for (StarQuery query : starQueries) {
                storage.match(query);
            }
            Long endTime = System.nanoTime();
            Long duration = endTime - startTime;
            System.out.println("Durée totale pour le gros sous-ensemble: " + duration + " nanosecondes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
