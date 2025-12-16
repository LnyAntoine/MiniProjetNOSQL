package qengine.utils;

import qengine.model.StarQuery;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static qengine.program.Example.parseSparQLQueries;
import static qengine.utils.utils.*;

public class concatenateAndCreateSubset {
    private static final String WORKING_DIR = "data/";
    private static final String RSQ_INTPUT_DIR = "refined_1/";
    private static final String RSQ_OUTPUT_SUBSET_DIR = "refined_queries_concatenated/";

    private static final String LITTLE_SUBSET_FILE = "little_subset.queryset";
    private static final String LITTLE_SUBSET_FILE_PATH =WORKING_DIR + RSQ_OUTPUT_SUBSET_DIR + LITTLE_SUBSET_FILE;
    private static final String AVERAGE_SUBSET_FILE = "refined_queries_concatenated_34_1.queryset";
    private static final String AVERAGE_SUBSET_FILE_PATH =WORKING_DIR + RSQ_OUTPUT_SUBSET_DIR + AVERAGE_SUBSET_FILE;
    private static final String BIG_SUBSET_FILE = "big_subset.queryset";
    private static final String BIG_SUBSET_FILE_PATH= WORKING_DIR + RSQ_OUTPUT_SUBSET_DIR + BIG_SUBSET_FILE;

    private static final int MAX_QUERIES_IN_LITTLE_SUBSET = 100;
    private static final int MAX_QUERIES_IN_AVERAGE_SUBSET = 796;
    private static final int MAX_QUERIES_IN_BIG_SUBSET = 1000;

    // Va concatener tous les fichiers raffinés et créer des sous-ensembles petits, moyens et grands
    public static void main(String[] args) {
        try {
            Random random = new Random();
            List<StarQuery> allRefinedQueries = new java.util.ArrayList<>();
            for (int i =1; i <5; i++) {
                for (String fileName : listFileNamesNio(WORKING_DIR + RSQ_INTPUT_DIR)) {
                    if (!fileName.startsWith("refined_queries_degree_" + i)) continue;
                    System.out.println("Fichier trouvé: " + fileName);
                    List<StarQuery> queries = parseSparQLQueries(WORKING_DIR + RSQ_INTPUT_DIR + fileName);
                    for (StarQuery query : queries) {
                        if (!allRefinedQueries.contains(query)) {
                            allRefinedQueries.add(query);
                        }
                    }
                }
            }
            System.out.println("Nombre total de requêtes raffinées uniques: " + allRefinedQueries.size());
            int subsetSize;
            replaceOrCreateFile(LITTLE_SUBSET_FILE_PATH);
            try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter(LITTLE_SUBSET_FILE_PATH))) {

                subsetSize = Math.min(
                        MAX_QUERIES_IN_LITTLE_SUBSET,
                        allRefinedQueries.size()
                );

                for (int i = 0; i < subsetSize; i++) {
                    writer.write(
                            allRefinedQueries
                                    .get(random.nextInt(allRefinedQueries.size()))
                                    .getLabel()
                    );
                    writer.newLine();
                }
            }

            System.out.println("Petit sous-ensemble créé avec succès.");
            System.out.println("Taille du petit sous-ensemble: " + subsetSize);

            replaceOrCreateFile(AVERAGE_SUBSET_FILE_PATH);
            try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter(AVERAGE_SUBSET_FILE_PATH))) {

                subsetSize = Math.min(
                        MAX_QUERIES_IN_AVERAGE_SUBSET,
                        allRefinedQueries.size()
                );

                for (int i = 0; i < subsetSize; i++) {
                    writer.write(
                            allRefinedQueries
                                    .get(random.nextInt(allRefinedQueries.size()))
                                    .getLabel()
                    );
                    writer.newLine();
                }
            }
            System.out.println("Sous-ensemble moyen créé avec succès.");
            System.out.println("Taille du sous-ensemble moyen: " + subsetSize);

            replaceOrCreateFile(BIG_SUBSET_FILE_PATH);
            try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter(BIG_SUBSET_FILE_PATH))) {

                subsetSize = Math.min(
                        MAX_QUERIES_IN_BIG_SUBSET,
                        allRefinedQueries.size()
                );

                for (int i = 0; i < subsetSize; i++) {
                    writer.write(
                            allRefinedQueries
                                    .get(random.nextInt(allRefinedQueries.size()))
                                    .getLabel()
                    );
                    writer.newLine();
                }
            }
            System.out.println("Grand sous-ensemble créé avec succès.");
            System.out.println("Taille du grand sous-ensemble: " + subsetSize);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

