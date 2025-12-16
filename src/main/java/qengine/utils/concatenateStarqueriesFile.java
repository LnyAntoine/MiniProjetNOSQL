package qengine.utils;

import org.jetbrains.annotations.NotNull;
import qengine.model.StarQuery;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static qengine.program.Example.parseSparQLQueries;
import static qengine.utils.utils.findOrCreateFile;
import static qengine.utils.utils.replaceOrCreateFile;

public class concatenateStarqueriesFile {
    private static final String WORKING_DIR = "data/";
    private static final String RSQ_INTPUT_DIR = "refined_queries_degree/";
    private static final String RSQ_OUTPUT_SUBSET_DIR = "refined_queries_concatenated_34_1/";
    private static final String OUTPUT_FILE_NAME = "refined_queries_concatenated_34";

    private static final String INPUT_FILE_1 = WORKING_DIR + RSQ_INTPUT_DIR + "refined_queries_degree_1.queryset";
    private static final String INPUT_FILE_2 = WORKING_DIR + RSQ_INTPUT_DIR + "refined_queries_degree_2.queryset";
    private static final String INPUT_FILE_3 = WORKING_DIR + RSQ_INTPUT_DIR + "refined_queries_degree_3.queryset";
    private static final String INPUT_FILE_4 = WORKING_DIR + RSQ_INTPUT_DIR + "refined_queries_degree_4.queryset";

    private static final List<String> filesToConcatenate = java.util.Arrays.asList(
            INPUT_FILE_3,
            INPUT_FILE_4
            //INPUT_FILE_2,
            //INPUT_FILE_1
    );

    //Concatene les starqueries des fichiers donnés en entrée en supprimant les doublons
    public static void main(String[] args) {
        try {
            StringBuilder OUTPUT_FILE = getOutputFileName();
            replaceOrCreateFile(WORKING_DIR + RSQ_OUTPUT_SUBSET_DIR + OUTPUT_FILE);
            List<StarQuery> starqueries = new java.util.ArrayList<>();
            for (String filePath : filesToConcatenate) {
                findOrCreateFile(filePath);
                for (StarQuery starQuery : parseSparQLQueries(filePath)) {
                    if (!starqueries.contains(starQuery)) {
                        starqueries.add(starQuery);
                    }
                }
            }
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(WORKING_DIR + RSQ_OUTPUT_SUBSET_DIR + OUTPUT_FILE))) {
                for (StarQuery starQuery : starqueries) {
                    writer.write(starQuery.getLabel());
                }
            }
            System.out.println("Fichier de requêtes concaténé créé avec succès.");
            System.out.println("Nombre total de requêtes uniques: " + starqueries.size());
        }


        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @NotNull
    private static StringBuilder getOutputFileName() {
        StringBuilder OUTPUT_FILE = new StringBuilder(OUTPUT_FILE_NAME);
        for (String filePath : filesToConcatenate) {
            if (filePath.contains("1")) {
                OUTPUT_FILE.append("_1");
            } else if (filePath.contains("2")) {
                OUTPUT_FILE.append("_2");
            } else if (filePath.contains("3")) {
                OUTPUT_FILE.append("_3");
            } else if (filePath.contains("4")) {
                OUTPUT_FILE.append("_4");
            } else {
                OUTPUT_FILE.append(new Random().nextInt(10000));
                System.out.println("Warning: file name does not contain degree information.");
            }
        }
        OUTPUT_FILE.append(".queryset");
        System.out.println("Output file name: " + OUTPUT_FILE);
        return OUTPUT_FILE;
    }
}
