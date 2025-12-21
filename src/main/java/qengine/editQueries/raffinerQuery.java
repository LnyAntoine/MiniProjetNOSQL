package qengine.editQueries;

import fr.boreal.model.kb.api.FactBase;
import fr.boreal.storage.natives.SimpleInMemoryGraphStore;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;
import qengine.utils.utils;

import static qengine.program.Example.*;

import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class raffinerQuery {
    private static final String WORKING_DIR = "data/";
    private static final String TEMPLATE_DIR = "template_degree/";

    private static final String SAMPLE_DATA_FILE = WORKING_DIR + "data2M.nt";
    private static final String QUERIES_1_ELIGIBLE_REGION =  WORKING_DIR+TEMPLATE_DIR + "Q_1_eligibleregion_10000.queryset";
    private static final String QUERIES_1_INCLUDES =  WORKING_DIR+TEMPLATE_DIR + "Q_1_includes_10000.queryset";
    private static final String QUERIES_1_LIKES =  WORKING_DIR+TEMPLATE_DIR + "Q_1_likes_10000.queryset";
    private static final String QUERIES_1_NATIONALITY =  WORKING_DIR+TEMPLATE_DIR + "Q_1_nationality_10000.queryset";
    private static final String QUERIES_1_SUBSCRIBE =  WORKING_DIR+TEMPLATE_DIR + "Q_1_subscribes_10000.queryset";
    private static final String SAMPLE_QUERY_FILE = WORKING_DIR + "sample_query.queryset";
    private static final String OUTPUT_QUERY_FILE = WORKING_DIR + "refined_queries.queryset";

    //Raffine les queries en excluants les doublons et les requetes vides
    public static void main(String[] args){
        FactBase factBase = new SimpleInMemoryGraphStore();
        HashMap<String, Integer> queriesCountMap = new HashMap<>();
        int counterDoublonsInitial = 0;
        int counterDoublons = 0;
        try {
            utils.findOrCreateFile(OUTPUT_QUERY_FILE);

            List<StarQuery> outputQueries = parseSparQLQueries(OUTPUT_QUERY_FILE);
            List<StarQuery> initialOutputQueries = new ArrayList<>(outputQueries);
            System.out.println("Nombre de requetes deja presentes dans le fichier de sortie: " + outputQueries.size());
            List<RDFTriple> rdfTriples = parseRDFData(SAMPLE_DATA_FILE);
            List<StarQuery> queries = parseSparQLQueries(QUERIES_1_LIKES);
            List<StarQuery> queriesNotNull = new ArrayList<>();
            for (RDFTriple triple : rdfTriples) {
                factBase.add(triple);  // Stocker chaque RDFAtom dans le store
            }
            for (StarQuery query : queries){
                if (executeStarQuery(query, factBase).hasNext()) queriesNotNull.add(query);
            }

            // Écrire les labels des requêtes retenues dans le fichier de sortie (mode append)
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(OUTPUT_QUERY_FILE, true)))) {
                for (StarQuery query : queriesNotNull){
                    queriesCountMap.put(query.getLabel(), queriesCountMap.getOrDefault(query.getLabel(), 0) + 1);
                    if (outputQueries.contains(query)){
                        System.out.println("Requete deja presente: " + query.getLabel());
                        System.out.println("Requete dans la liste des requetes de sortie: " + outputQueries.get(outputQueries.indexOf(query)).getLabel());
                        if (initialOutputQueries.contains(query)) {
                            counterDoublonsInitial+=1;
                            System.out.println("Requete dans la liste initiale" + initialOutputQueries.get(initialOutputQueries.indexOf(query)).getLabel());}
                        else {
                            counterDoublons+=1;
                            System.out.println("Requete pas dans la liste initiale");}
                        continue;
                    }
                    out.println(query.getLabel());
                    outputQueries.add(query);
                }
            }

            System.out.println("nombre de requetes ecrites retenues au départ " + initialOutputQueries.size());
            System.out.println("Nombre de requetes initiales: " + queries.size());
            System.out.println("Nombre de requetes Non nulles: " + queriesNotNull.size());
            System.out.println("Nombre de requetes ajoutées : " + (outputQueries.size() - initialOutputQueries.size()));

            System.out.println("Nombre total de requetes dans le fichier de sortie: " + outputQueries.size());

            System.out.println("Détails du nombre d'occurrences par requête retenue:");
            for (String label : queriesCountMap.keySet()) {
                System.out.println("Requête: " + label + ", Occurrences: " + queriesCountMap.get(label));
            }
            System.out.println("Nombre de requetes uniques retenues: " + queriesCountMap.size());
            System.out.println("Nombre de doublons dans les requetes initiales: " + counterDoublonsInitial);
            System.out.println("Nombre de doublons dans les requetes ajoutées: " + counterDoublons);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
