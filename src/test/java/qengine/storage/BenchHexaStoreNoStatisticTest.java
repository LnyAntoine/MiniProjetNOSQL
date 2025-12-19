package qengine.storage;

import org.junit.jupiter.api.Test;
import qengine.utils.testForBenchmark;

public class BenchHexaStoreNoStatisticTest {

    private static final String WORKING_DIR = "data/";
    private static final String RSQ_SUBSET_DIR = "subsets/";
    private static final String RSQ_SUBSET_DIR34 = "refined_queries_concatenated_34_1/";
    private static final String DEGREE_DIR = "refined_queries_concatenated/";

    private static final String DATA_2M_FILE = WORKING_DIR + "data2M.nt";
    private static final String DATA_500K_FILE = WORKING_DIR + "500K.nt";
    private static final String DATA_100K_FILE = WORKING_DIR + "100K.nt";


    private static final String DEGREE_ALL_FILE = "refined_queries_concatenated_3_4_2_1.queryset";
    private static final String DEGREE_ALL_FILE_PATH = WORKING_DIR + DEGREE_DIR + DEGREE_ALL_FILE;

    private static final String DEGREE_3_4_FILE = "refined_queries_concatenated_34_3_4.queryset";
    private static final String DEGREE_34_1_FILE = "refined_queries_concatenated_34_1.queryset";
    private static final String DEGREE_3_4_FILE_PATH = WORKING_DIR + RSQ_SUBSET_DIR34 + DEGREE_3_4_FILE;
    private static final String DEGREE_34_1_FILE_PATH = WORKING_DIR + RSQ_SUBSET_DIR34 + DEGREE_34_1_FILE;


    private static final String LITTLE_SUBSET_FILE = "little_subset.queryset";
    private static final String LITTLE_SUBSET_FILE_PATH =WORKING_DIR + DEGREE_DIR + LITTLE_SUBSET_FILE;
    private static final String AVERAGE_SUBSET_FILE = "average_subset.queryset";
    private static final String AVERAGE_SUBSET_FILE_PATH =WORKING_DIR + DEGREE_DIR + AVERAGE_SUBSET_FILE;
    private static final String BIG_SUBSET_FILE = "big_subset.queryset";
    private static final String BIG_SUBSET_FILE_PATH= WORKING_DIR + DEGREE_DIR + BIG_SUBSET_FILE;


    @Test void benchMarkAdd500K(){
        testForBenchmark.testAdd(DATA_500K_FILE, new RDFHexaStoreNoStatistic());
    }
    @Test void benchMarkAdd100K(){
        testForBenchmark.testAdd(DATA_100K_FILE, new RDFHexaStoreNoStatistic());
    }
    @Test void benchMarkAdd2M(){
        testForBenchmark.testAdd(DATA_2M_FILE, new RDFHexaStoreNoStatistic());
    }

    @Test
    public void benchmarkMatchLittleSubset() {
        // Implémentation du benchmark à venir
        testForBenchmark.testMatch(LITTLE_SUBSET_FILE_PATH, DATA_500K_FILE, new RDFHexaStoreNoStatistic());
    }
    @Test
    public void benchmarkMatchAverageSubset() {
        // Implémentation du benchmark à venir
        testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_500K_FILE, new RDFHexaStoreNoStatistic());
    }
    @Test
    public void benchmarkMatchBigSubset() {
        testForBenchmark.testMatch(BIG_SUBSET_FILE_PATH, DATA_500K_FILE, new RDFHexaStoreNoStatistic());
    }
    @Test
    public void benchMarkMatchDegree1() {
        // Implémentation du benchmark à venir
        testForBenchmark.testMatch(DEGREE_34_1_FILE_PATH, DATA_500K_FILE, new RDFHexaStoreNoStatistic());
    }
    @Test
    public void benchMarkMatchDegree3and4() {
        // Implémentation du benchmark à venir
        testForBenchmark.testMatch(DEGREE_3_4_FILE_PATH, DATA_500K_FILE, new RDFHexaStoreNoStatistic());
    }
}
