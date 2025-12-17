package qengine.storage;

import org.junit.jupiter.api.Test;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.utils.WelfordAlgorithm;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static qengine.program.Example.parseRDFData;
import static qengine.program.Example.parseSparQLQueries;

public class RDFHexaStoreVsNoStatisticTest {
    private static final String WORKING_DIR = "data/";
    private static final String SAMPLE_DATA_FILE = WORKING_DIR + "data500k.nt";
    private static final String SAMPLE_QUERY_FILE = WORKING_DIR + "subsets/average_subset.queryset";
    private static final String SAMPLE_BIG_DATA_FILE = WORKING_DIR + "data2M.nt";
    private static final String SAMPLE_BIG_QUERY_FILE = WORKING_DIR + "subsets/big_subset.queryset";
    @Test
    public void RDFHexaStoreVsNoStatisticTestAdd() throws IOException {
        RDFStorage hexaStore = new RDFHexaStore();
        RDFStorage hexaStoreNoStat = new RDFHexaStoreNoStatistic();
        List<RDFTriple> rdfAtoms = parseRDFData(SAMPLE_BIG_DATA_FILE);

        //List<StarQuery> starQueries = parseSparQLQueries(SAMPLE_BIG_QUERY_FILE);



        WelfordAlgorithm globalWelfordHexa = new WelfordAlgorithm();
        WelfordAlgorithm globalWelfordHexaNoStat = new WelfordAlgorithm();
        final int BATCH_SIZE = 5;
        boolean altern = true;

        Iterator<RDFTriple> it = rdfAtoms.iterator();

        while (it.hasNext()) {

            long durationHexa = 0;
            long durationHexaNoStat = 0;

            for (int i = 0; i < BATCH_SIZE && it.hasNext(); i++) {
                RDFTriple atom = it.next();

                if (altern) {
                    long start = System.nanoTime();
                    hexaStore.add(atom);
                    durationHexa += System.nanoTime() - start;

                    start = System.nanoTime();
                    hexaStoreNoStat.add(atom);
                    durationHexaNoStat += System.nanoTime() - start;
                } else {
                    long start = System.nanoTime();
                    hexaStoreNoStat.add(atom);
                    durationHexaNoStat += System.nanoTime() - start;

                    start = System.nanoTime();
                    hexaStore.add(atom);
                    durationHexa += System.nanoTime() - start;
                }
            }
            globalWelfordHexa.add((double) durationHexa / BATCH_SIZE);
            globalWelfordHexaNoStat.add((double) durationHexaNoStat / BATCH_SIZE);

            altern = !altern;
        }


        double thresholdHexa = globalWelfordHexa.getMean() + 3 * globalWelfordHexa.getStdDev();
        double thresholdHexaNoStats = globalWelfordHexaNoStat.getMean() + 3 * globalWelfordHexaNoStat.getStdDev();

        hexaStore = new RDFHexaStore();
        hexaStoreNoStat = new RDFHexaStoreNoStatistic();
        WelfordAlgorithm WelfordHexa = new WelfordAlgorithm();
        WelfordAlgorithm WelfordHexaNoStat = new WelfordAlgorithm();

       altern = true;

        it = rdfAtoms.iterator();

        while (it.hasNext()) {

            long totalHexa = 0;
            long totalHexaNoStat = 0;
            int count = 0;

            for (int i = 0; i < BATCH_SIZE && it.hasNext(); i++) {
                RDFTriple atom = it.next();
                count++;

                if (altern) {
                    long start = System.nanoTime();
                    hexaStore.add(atom);
                    totalHexa += System.nanoTime() - start;

                    start = System.nanoTime();
                    hexaStoreNoStat.add(atom);
                    totalHexaNoStat += System.nanoTime() - start;
                } else {
                    long start = System.nanoTime();
                    hexaStoreNoStat.add(atom);
                    totalHexaNoStat += System.nanoTime() - start;

                    start = System.nanoTime();
                    hexaStore.add(atom);
                    totalHexa += System.nanoTime() - start;
                }
            }

            // moyenne par opération pour le batch
            long avgHexa = totalHexa / count;
            long avgHexaNoStat = totalHexaNoStat / count;

            // filtrage des outliers
            if (avgHexa <= thresholdHexa) {
                WelfordHexa.add(avgHexa);
            }

            if (avgHexaNoStat <= thresholdHexaNoStats) {
                WelfordHexaNoStat.add(avgHexaNoStat);
            }

            altern = !altern;
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
        for (StarQuery query : starQueries) {
            System.out.println(query.getLabel());
        }


        for (RDFTriple atom : rdfAtoms) {
            hexaStore.add(atom);
            hexaStoreNoStat.add(atom);
        }

        WelfordAlgorithm globalWelfordHexa = new WelfordAlgorithm();
        WelfordAlgorithm globalWelfordHexaNoStat = new WelfordAlgorithm();

        final int BATCH_SIZE = 5;
        boolean altern = true;

        Iterator<StarQuery> it = starQueries.iterator();

        while (it.hasNext()) {

            long totalHexa = 0;
            long totalHexaNoStat = 0;
            int count = 0;

            for (int i = 0; i < BATCH_SIZE && it.hasNext(); i++) {
                StarQuery query = it.next();
                count++;

                if (altern) {
                    long start = System.nanoTime();
                    hexaStore.match(query);
                    totalHexa += System.nanoTime() - start;

                    start = System.nanoTime();
                    hexaStoreNoStat.matchWithoutStatistic(query);
                    totalHexaNoStat += System.nanoTime() - start;
                } else {
                    long start = System.nanoTime();
                    hexaStoreNoStat.matchWithoutStatistic(query);
                    totalHexaNoStat += System.nanoTime() - start;

                    start = System.nanoTime();
                    hexaStore.match(query);
                    totalHexa += System.nanoTime() - start;
                }
            }

            // moyenne par requête dans le batch
            globalWelfordHexa.add((double) totalHexa / count);
            globalWelfordHexaNoStat.add((double) totalHexaNoStat / count);

            altern = !altern;
        }



        double thresholdHexa = globalWelfordHexa.getMean() + 3 * globalWelfordHexa.getStdDev();
        double thresholdHexaNoStats = globalWelfordHexaNoStat.getMean() + 3 * globalWelfordHexaNoStat.getStdDev();

        hexaStore = new RDFHexaStore();
        hexaStoreNoStat = new RDFHexaStoreNoStatistic();
        WelfordAlgorithm WelfordHexa = new WelfordAlgorithm();
        WelfordAlgorithm WelfordHexaNoStat = new WelfordAlgorithm();

        altern = true;
        it = starQueries.iterator();

        while (it.hasNext()) {

            long totalHexa = 0;
            long totalHexaNoStat = 0;
            int count = 0;

            for (int i = 0; i < BATCH_SIZE && it.hasNext(); i++) {
                StarQuery query = it.next();
                count++;

                if (altern) {
                    long start = System.nanoTime();
                    hexaStore.match(query);
                    totalHexa += System.nanoTime() - start;

                    start = System.nanoTime();
                    hexaStoreNoStat.matchWithoutStatistic(query);
                    totalHexaNoStat += System.nanoTime() - start;
                } else {
                    long start = System.nanoTime();
                    hexaStoreNoStat.matchWithoutStatistic(query);
                    totalHexaNoStat += System.nanoTime() - start;

                    start = System.nanoTime();
                    hexaStore.match(query);
                    totalHexa += System.nanoTime() - start;
                }
            }

            long avgHexa = totalHexa / count;
            long avgHexaNoStat = totalHexaNoStat / count;

            if (avgHexa <= thresholdHexa) {
                WelfordHexa.add(avgHexa);
            }

            if (avgHexaNoStat <= thresholdHexaNoStats) {
                WelfordHexaNoStat.add(avgHexaNoStat);
            }

            altern = !altern;
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
