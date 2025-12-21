package qengine.editQueries;

import fr.boreal.model.kb.api.FactBase;
import fr.boreal.storage.natives.SimpleInMemoryGraphStore;
import qengine.model.RDFTriple;
import qengine.model.StarQuery;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static qengine.program.Example.*;
import static qengine.utils.utils.findOrCreateFile;
import static qengine.utils.utils.listFileNamesNio;

public class refinedAllQueries {
    private static final String WORKING_DIR = "data/";
    private static final String TEMPLATE_DIR = "template_degree/";
    private static final String RSQ_INTPUT_DIR = "watdiv-mini-projet-partie-2/testsuite/queries/template-aug/";
    private static final String RSQ_OUTPUT_DIR = "refined_queries_degree/";
    private static final String SAMPLE_DATA_FILE = WORKING_DIR + "data500k.nt";
    private static final String QUERIES_1_ELIGIBLE_REGION =  WORKING_DIR+TEMPLATE_DIR + "Q_1_eligibleregion_10000.queryset";
    private static final String QUERIES_1_INCLUDES =  WORKING_DIR+TEMPLATE_DIR + "Q_1_includes_10000.queryset";
    private static final String QUERIES_1_LIKES =  WORKING_DIR+TEMPLATE_DIR + "Q_1_likes_10000.queryset";
    private static final String QUERIES_1_NATIONALITY =  WORKING_DIR+TEMPLATE_DIR + "Q_1_nationality_10000.queryset";
    private static final String QUERIES_1_SUBSCRIBE =  WORKING_DIR+TEMPLATE_DIR + "Q_1_subscribes_10000.queryset";
    private static final String SAMPLE_QUERY_FILE = WORKING_DIR + "sample_query.queryset";
    private static final String OUTPUT_QUERY_FILE_CST = WORKING_DIR +RSQ_OUTPUT_DIR+ "refined_queries.queryset";

    //Raffine toutes les queries de degree 1 à 4 en un seul fichier
    public static void main(String[] args){
        FactBase factBase = new SimpleInMemoryGraphStore();
        HashMap<String, Integer> queriesCountMap = new HashMap<>();

        try {
            findOrCreateFile(SAMPLE_DATA_FILE);
            List<RDFTriple> rdfTriples = parseRDFData(SAMPLE_DATA_FILE);
            for (RDFTriple triple : rdfTriples) {
                factBase.add(triple);  // Stocker chaque RDFAtom dans le store
            }

            for (int i =1; i <5; i++){
                System.out.println("----------------------------------------");
                System.out.println("Traitement pour les requetes de degree: " + i);
                String output_query_file = WORKING_DIR + RSQ_OUTPUT_DIR +"refined_queries_degree_" + i + ".queryset";
                // Vérifier que le fichier de sortie existe; sinon créer son dossier parent (si nécessaire) et le fichier
                findOrCreateFile(output_query_file);

                List<StarQuery> outputQueries = parseSparQLQueries(output_query_file);
                List<StarQuery> initialOutputQueries = new ArrayList<>(outputQueries);
                int counterDoublonsInitial = 0;
                int counterDoublons = 0;
                int cptRequest = 0;
                int cptRequestNonNull = 0;
                for (String fileName : listFileNamesNio( RSQ_INTPUT_DIR)) {
                    if (!fileName.startsWith("Q_"+i)) continue;
                    System.out.println("    Fichier trouvé: " + fileName);
                    String INPUT_QUERY_FILE =  RSQ_INTPUT_DIR + fileName;

                    System.out.println("        Nombre de requetes deja presentes dans le fichier de sortie: " + outputQueries.size());

                    List<StarQuery> queries = parseSparQLQueries(INPUT_QUERY_FILE);
                    cptRequest+= queries.size();
                    List<StarQuery> queriesNotNull = new ArrayList<>();

                    for (StarQuery query : queries){
                        if (executeStarQuery(query, factBase).hasNext()) queriesNotNull.add(query);
                    }
                    cptRequestNonNull += queriesNotNull.size();

                    // Écrire les labels des requêtes retenues dans le fichier de sortie (mode append)
                    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output_query_file, true)))) {
                        for (StarQuery query : queriesNotNull){
                            queriesCountMap.put(query.getLabel(), queriesCountMap.getOrDefault(query.getLabel(), 0) + 1);
                            if (outputQueries.contains(query)){
                                if (initialOutputQueries.contains(query)) {
                                    counterDoublonsInitial += 1;
                                }
                                else {
                                    counterDoublons += 1;
                                }
                                continue;
                            }
                            out.println(query.getLabel());
                            outputQueries.add(query);
                        }
                    }

                }
                System.out.println("    Traitement terminé pour tous les fichiers de requêtes.");
                System.out.println("    Nombre total de requetes initiales traitées pour "+i+": "+cptRequest);
                System.out.println("    Nombre total de requetes Non nulles traitées pour "+i+" : "+cptRequestNonNull);
                System.out.println("    Nombre de doublons initiaux dans le fichier de sortie pour "+i+" : " + counterDoublonsInitial);
                System.out.println("    Nombre de doublons ajoutés dans le fichier de sortie pour "+i+" : " + counterDoublons);
                System.out.println("    Nombre total de requetes dans le fichier de sortie pour "+i+" : " + outputQueries.size());
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
