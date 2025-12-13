package qengine.storage;

import org.junit.jupiter.api.Test;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.utils.WelfordAlgorithm;

import java.io.IOException;
import java.util.List;

import static qengine.program.Example.parseRDFData;
import static qengine.program.Example.parseSparQLQueries;

public class RDFHexaStoreVsNoStatisticTest {
    private static final String WORKING_DIR = "data/";
    private static final String SAMPLE_DATA_FILE = WORKING_DIR + "sample_data.nt";
    private static final String SAMPLE_QUERY_FILE = WORKING_DIR + "sample_query.queryset";
    private static final String SAMPLE_BIG_DATA_FILE = WORKING_DIR + "100K.nt";
    private static final String SAMPLE_BIG_QUERY_FILE = WORKING_DIR + "STAR_ALL_workload.queryset";
    @Test
    public void RDFHexaStoreVsNoStatisticTestAdd() throws IOException {
        RDFStorage hexaStore = new RDFHexaStore();
        RDFStorage hexaStoreNoStat = new RDFHexaStoreNoStatistic();
        List<RDFTriple> rdfAtoms = parseRDFData(SAMPLE_BIG_DATA_FILE);

        //List<StarQuery> starQueries = parseSparQLQueries(SAMPLE_BIG_QUERY_FILE);

        WelfordAlgorithm globalWelfordHexa = new WelfordAlgorithm();
        WelfordAlgorithm globalWelfordHexaNoStat = new WelfordAlgorithm();

        for (RDFTriple atom : rdfAtoms) {
            long start = System.nanoTime();
            hexaStore.add(atom);
            long end = System.nanoTime();
            globalWelfordHexa.add(end - start);

            start = System.nanoTime();
            hexaStoreNoStat.add(atom);
            end = System.nanoTime();
            globalWelfordHexaNoStat.add(end - start);
        }
        double thresholdHexa = globalWelfordHexa.getMean() + 3 * globalWelfordHexa.getStdDev();
        double thresholdHexaNoStats = globalWelfordHexaNoStat.getMean() + 3 * globalWelfordHexaNoStat.getStdDev();

        hexaStore = new RDFHexaStore();
        hexaStoreNoStat = new RDFHexaStoreNoStatistic();
        WelfordAlgorithm WelfordHexa = new WelfordAlgorithm();
        WelfordAlgorithm WelfordHexaNoStat = new WelfordAlgorithm();


        for (RDFTriple atom : rdfAtoms) {
            long start = System.nanoTime();
            hexaStore.add(atom);
            long end = System.nanoTime();
            long durationHexa = end - start;
            if (durationHexa <= thresholdHexa) {
                WelfordHexa.add(durationHexa);
            }

            start = System.nanoTime();
            hexaStoreNoStat.add(atom);
            end = System.nanoTime();
            long durationHexaNoStat = end - start;
            if (durationHexaNoStat <= thresholdHexaNoStats) {
                WelfordHexaNoStat.add(durationHexaNoStat);
            }
        }

        System.out.println("Start Hexastore avec et sans statistiques sur Add");

        System.out.println("HexaStore:");
        System.out.println("  Moyenne = " + WelfordHexa.getMean() + " ns");
        System.out.println("  Ecart type  = " + WelfordHexa.getStdDev() + " ns");

        System.out.println("HexaStoreNoStat:");
        System.out.println("  Moyenne = " + WelfordHexaNoStat.getMean() + " ns");
        System.out.println("  Ecart type  = " + WelfordHexaNoStat.getStdDev() + " ns");

    }
    @Test
    public void RDFHexaStoreVsNoStatisticTestMatch() throws IOException {

        RDFHexaStore hexaStore = new RDFHexaStore();
        RDFHexaStoreNoStatistic hexaStoreNoStat = new RDFHexaStoreNoStatistic();
        List<RDFTriple> rdfAtoms = parseRDFData(SAMPLE_BIG_DATA_FILE);

        List<StarQuery> starQueries = parseSparQLQueries(SAMPLE_BIG_QUERY_FILE);

        for (RDFTriple atom : rdfAtoms) {
            hexaStore.add(atom);
            hexaStoreNoStat.add(atom);
        }

        WelfordAlgorithm globalWelfordHexa = new WelfordAlgorithm();
        WelfordAlgorithm globalWelfordHexaNoStat = new WelfordAlgorithm();

        for (StarQuery query : starQueries) {
            long start = System.nanoTime();
            hexaStore.match(query);
            long end = System.nanoTime();
            globalWelfordHexa.add(end - start);

            start = System.nanoTime();
            hexaStoreNoStat.matchWithoutStatistic(query);
            end = System.nanoTime();
            globalWelfordHexaNoStat.add(end - start);
        }


        double thresholdHexa = globalWelfordHexa.getMean() + 3 * globalWelfordHexa.getStdDev();
        double thresholdHexaNoStats = globalWelfordHexaNoStat.getMean() + 3 * globalWelfordHexaNoStat.getStdDev();

        hexaStore = new RDFHexaStore();
        hexaStoreNoStat = new RDFHexaStoreNoStatistic();
        WelfordAlgorithm WelfordHexa = new WelfordAlgorithm();
        WelfordAlgorithm WelfordHexaNoStat = new WelfordAlgorithm();

        for (StarQuery query : starQueries) {
            long start = System.nanoTime();
            hexaStore.match(query);
            long end = System.nanoTime();
            long durationHexa = end - start;
            if (durationHexa <= thresholdHexa) {
                WelfordHexa.add(durationHexa);
            }

            start = System.nanoTime();
            hexaStoreNoStat.matchWithoutStatistic(query);
            end = System.nanoTime();
            long durationHexaNoStat = end - start;
            if (durationHexaNoStat <= thresholdHexaNoStats) {
                WelfordHexaNoStat.add(durationHexaNoStat);
            }
        }


        System.out.println("Start Hexastore avec et sans statistiques sur Match");
        System.out.println("HexaStore:");
        System.out.println("  Moyenne = " + WelfordHexa.getMean() + " ns");
        System.out.println("  Ecart type  = " + WelfordHexa.getStdDev() + " ns");

        System.out.println("HexaStoreNoStat:");
        System.out.println("  Moyenne = " + WelfordHexaNoStat.getMean() + " ns");
        System.out.println("  Ecart type  = " + WelfordHexaNoStat.getStdDev() + " ns");

    }
}
