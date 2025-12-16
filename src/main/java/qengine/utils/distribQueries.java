package qengine.utils;

import java.io.IOException;
import java.util.HashMap;
import java.io.*;
import java.util.*;

public class distribQueries {
    private static final String WORKING_DIR = "data/";
    private static final String SAMPLE_QUERY_FILE = WORKING_DIR + "refined_queries_concatenated/refined_queries_concatenated_3_4_2_1.queryset";
    public static void main(String[] args) {

        Map<Integer, Integer> degreeDistribution = new HashMap<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader(SAMPLE_QUERY_FILE))) {

            String line;
            boolean inWhere = false;
            int currentDegree = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("SELECT")) {
                    currentDegree = 0;
                    inWhere = false;
                }

                if (line.contains("WHERE")) {
                    inWhere = true;
                }
                if (inWhere
                        && line.contains(".")
                        && line.contains("<")
                        && !line.startsWith("}")) {
                    currentDegree++;
                }

                if (line.contains("}")) {
                    degreeDistribution.put(
                            currentDegree,
                            degreeDistribution.getOrDefault(currentDegree, 0) + 1
                    );
                    inWhere = false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nDistribution des degrés :");
        degreeDistribution.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e ->
                        System.out.println("Degré " + e.getKey() +
                                " : " + e.getValue() + " requêtes"));
    }

}
