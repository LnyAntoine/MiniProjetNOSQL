package qengine.storage;

import org.junit.jupiter.api.Test;
import qengine.utils.WelfordAlgorithm;
import qengine.utils.testForBenchmark;

public class BenchAllTest
{
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

    @Test
    public void benchmarkMatchGT_LTL_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore Little 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(LITTLE_SUBSET_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_LTL_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark Hexastore LittleSubset 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(LITTLE_SUBSET_FILE_PATH, DATA_2M_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_LTL_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark HexaStoreNoStats LittleSubset 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }


    @Test
    public void benchmarkMatchGT_AVG_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore AVGSubset 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_AVG_2M() {
        System.out.println("--- Benchmark RDFHexaStore AVGSubset 2M ---");
        // Implémentation du benchmark à venir
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_2M_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("HexaStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("HexaStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_AVG_2M() {
        System.out.println("--- Benchmark RDFHexaStoreNoStatistic AVGSubset 2M ---");
        // Implémentation du benchmark à venir
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_2M_FILE, new RDFHexaStoreNoStatistic());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("HexaStoreNoStats temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("HexaStoreNoStats écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }

    @Test
    public void benchmarkMatchGT_BIG_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore Big 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(BIG_SUBSET_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_BIG_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark Hexastore BigSubset 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(BIG_SUBSET_FILE_PATH, DATA_2M_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_BIG_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark HexaStoreNoStats BigSubset 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(BIG_SUBSET_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }

    @Test
    public void benchmarkMatchGT_D34_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore Degree 3+4 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_3_4_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_D34_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark Hexastore Degree 3+4 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_3_4_FILE_PATH, DATA_2M_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_D34_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark HexaStoreNoStats Degree 3+4 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_3_4_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchGT_D1_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore Degree 1 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_34_1_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_D1_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark Hexastore Degree 1 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_34_1_FILE_PATH, DATA_2M_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_D1_2M() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark HexaStoreNoStats Degree 1 2M ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_34_1_FILE_PATH, DATA_2M_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }

    @Test
    public void benchmarkMatchGT_LTL_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore Little 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(LITTLE_SUBSET_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_LTL_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark Hexastore LittleSubset 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(LITTLE_SUBSET_FILE_PATH, DATA_500K_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_LTL_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark HexaStoreNoStats LittleSubset 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }


    @Test
    public void benchmarkMatchGT_AVG_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore AVGSubset 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_AVG_500K() {
        System.out.println("--- Benchmark RDFHexaStore AVGSubset 500K ---");
        // Implémentation du benchmark à venir
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_500K_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("HexaStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("HexaStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_AVG_500K() {
        System.out.println("--- Benchmark RDFHexaStoreNoStatistic AVGSubset 500K ---");
        // Implémentation du benchmark à venir
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(AVERAGE_SUBSET_FILE_PATH, DATA_500K_FILE, new RDFHexaStoreNoStatistic());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("HexaStoreNoStats temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("HexaStoreNoStats écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }

    @Test
    public void benchmarkMatchGT_BIG_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore Big 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(BIG_SUBSET_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_BIG_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark Hexastore BigSubset 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(BIG_SUBSET_FILE_PATH, DATA_500K_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_BIG_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark HexaStoreNoStats BigSubset 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(BIG_SUBSET_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }

    @Test
    public void benchmarkMatchGT_D34_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore Degree 3+4 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_3_4_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_D34_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark Hexastore Degree 3+4 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_3_4_FILE_PATH, DATA_500K_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_D34_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark HexaStoreNoStats Degree 3+4 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_3_4_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchGT_D1_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark GiantTableStore Degree 1 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_34_1_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("GiantTableStore temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("GiantTableStore écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHS_D1_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark Hexastore Degree 1 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_34_1_FILE_PATH, DATA_500K_FILE, new RDFHexaStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }
    @Test
    public void benchmarkMatchHSNS_D1_500K() {
        // Implémentation du benchmark à venir
        System.out.println("--- Benchmark HexaStoreNoStats Degree 1 500K ---");
        WelfordAlgorithm welfordAlgorithm = new WelfordAlgorithm();
        for (int i = 0; i < 5; i++) {
            long startTime = System.nanoTime();
            testForBenchmark.testMatch(DEGREE_34_1_FILE_PATH, DATA_500K_FILE, new GiantTableStore());
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            welfordAlgorithm.add(duration);
        }
        System.out.println("temps moyen : " + welfordAlgorithm.getMean() + " ns");
        System.out.println("écart type : " + welfordAlgorithm.getStdDev() + " ns");
    }

}
