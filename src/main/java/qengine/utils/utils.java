package qengine.utils;

import fr.boreal.model.logicalElements.api.Literal;
import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class utils {

    //Crée des littéraux avec la factory à partir d'un entier
    public static Literal<Integer> createLiteralFromInteger(Integer value) {
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }
    //Crée des littéraux avec la factory à partir d'une chaine de caractère
    public static Literal<String> createLiteralFromString(String value) {
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }
    //Crée des littéraux avec la factory à partir d'un objet
    public static Literal<?> createLiteralFromObject(Object value) {
        if (value instanceof String) {
            return createLiteralFromString((String) value);
        } else if (value instanceof Integer) {
            return createLiteralFromInteger((Integer) value);
        }
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }

    //Fait l'intersection entre deux itérateurs
    public static Iterator<Substitution> intersectTwoIterators(Iterator<Substitution> it1, Iterator<Substitution> it2) {
        HashSet<Substitution> list1 = new HashSet<>();
        ArrayList<Substitution> result = new ArrayList<>();
        if (!it1.hasNext() || !it2.hasNext()) {
            return result.iterator();
        }
        while (it1.hasNext()) {
            Substitution s = it1.next();
            list1.add(s);
        }
        while (it2.hasNext()) {
            Substitution s = it2.next();
            if (list1.contains(s)) {
                result.add(s);
            }
        }
        return result.iterator();
    }

    //Liste les fichiers dans un dossier
    public static List<String> listFileNamesNio(String dirPath) throws IOException {
        Path dir = Paths.get(dirPath);
        if (!Files.isDirectory(dir)) return Collections.emptyList();
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }
    //Trouve ou crée le fichier donné
    public static void findOrCreateFile(String filePath) throws IOException {
        File outFile = new File(filePath);
        if (!outFile.exists()) {
            File parent = outFile.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean createdDir = parent.mkdirs();
                if (!createdDir) {
                    throw new IOException("Impossible de créer le dossier parent: " + parent.getAbsolutePath());
                }
            }
            boolean createdFile = outFile.createNewFile();
            if (!createdFile) {
                throw new IOException("Impossible de créer le fichier de sortie: " + outFile.getAbsolutePath());
            }
        }
    }

    //Trouve ou crée le fichier de sortie csv, rajoute les headers
    public static void findOrCreateFileOutputCSV(String filePath) throws IOException {
        File outFile = new File(filePath);
        if (!outFile.exists()) {
            File parent = outFile.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean createdDir = parent.mkdirs();
                if (!createdDir) {
                    throw new IOException("Impossible de créer le dossier parent: " + parent.getAbsolutePath());
                }
                String header = "operation_type,storage_name,data_file_path,query_file_path,duration_ns\n";
                Files.writeString(Paths.get(filePath), header);
            }
            boolean createdFile = outFile.createNewFile();
            if (!createdFile) {
                throw new IOException("Impossible de créer le fichier de sortie: " + outFile.getAbsolutePath());
            }
        }
    }
    //Remplace le fichier par un fichier vide ou en crée un vide
    public static void replaceOrCreateFile(String filePath) throws IOException {
        File outFile = new File(filePath);
        if (!outFile.exists()) {
            File parent = outFile.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean createdDir = parent.mkdirs();
                if (!createdDir) {
                    throw new IOException("Impossible de créer le dossier parent: " + parent.getAbsolutePath());
                }
            }
            boolean createdFile = outFile.createNewFile();
            if (!createdFile) {
                throw new IOException("Impossible de créer le fichier de sortie: " + outFile.getAbsolutePath());
            }
        } else {
            boolean deleted = outFile.delete();
            if (!deleted) {
                throw new IOException("Impossible de supprimer le fichier existant: " + outFile.getAbsolutePath());
            }
            boolean createdFile = outFile.createNewFile();
            if (!createdFile) {
                throw new IOException("Impossible de créer le fichier de sortie: " + outFile.getAbsolutePath());
            }
        }
    }
    //Ajoute les données dans le csv
    public static void addDataToOutPutCSV(String filePath, String operation , String Store,
                                          String dataFile,
                                          String queryFile, long timeValue) throws IOException {
        findOrCreateFileOutputCSV(filePath);
        Path path = Paths.get(filePath);
        String data = operation + "," + Store + "," + dataFile + "," + queryFile + "," + timeValue + "\n";
        Files.writeString(path, data, java.nio.file.StandardOpenOption.APPEND);
    }
}
