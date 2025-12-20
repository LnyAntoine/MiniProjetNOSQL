package qengine.utils;

import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.storage.RDFStorage;

import java.util.List;

import static qengine.program.Example.parseRDFData;
import static qengine.program.Example.parseSparQLQueries;
import static qengine.utils.utils.findOrCreateFile;

public class testForBenchmark {
    private static final String WORKING_DIR = "data/";
    private static final String OUTPUT_FILE = "benchmark_results.csv";
    public static long testMatch(String QUERY_FILE,
                                 String DATA_FILE, RDFStorage storage) {

        try {
            findOrCreateFile(QUERY_FILE);
            findOrCreateFile(DATA_FILE);
            List<RDFTriple> rdfTriples = parseRDFData(DATA_FILE);
            List<StarQuery> starQueries = parseSparQLQueries(QUERY_FILE);

            storage.addAll(rdfTriples);

            for (StarQuery query : starQueries) {
                storage.match(query);
            }
            Long startTime = System.nanoTime();
            for (StarQuery query : starQueries) {
                storage.match(query);
            }
            Long endTime = System.nanoTime();
            long duration = endTime - startTime;
            System.out.println("Durée totale pour "+DATA_FILE+ ": " + duration + " nanosecondes");
            utils.addDataToFile(WORKING_DIR+OUTPUT_FILE,"MATCH" ,storage.getType(), DATA_FILE, QUERY_FILE, duration);
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static void testAdd(
                                 String DATA_FILE, RDFStorage storage) {

        try {

            findOrCreateFile(DATA_FILE);
            List<RDFTriple> rdfTriples = parseRDFData(DATA_FILE);
            Long startTime = System.nanoTime();
            storage.addAll(rdfTriples);
            Long endTime = System.nanoTime();
            long duration = endTime - startTime;
            System.out.println("Durée totale pour "+DATA_FILE+ ": " + duration + " nanosecondes");
            utils.addDataToFile(WORKING_DIR+OUTPUT_FILE,"ADD" ,storage.getType(), DATA_FILE, "N/A", duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
