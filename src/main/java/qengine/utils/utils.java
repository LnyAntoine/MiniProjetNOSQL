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
    public static Literal<Integer> createLiteralFromInteger(Integer value) {
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }

    public static Literal<String> createLiteralFromString(String value) {
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }

    public static Literal<?> createLiteralFromObject(Object value) {
        if (value instanceof String) {
            return createLiteralFromString((String) value);
        } else if (value instanceof Integer) {
            return createLiteralFromInteger((Integer) value);
        }
        return SameObjectTermFactory.instance().createOrGetLiteral(value);
    }

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

    // Variante moderne avec java.nio.file (Java 8+)
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
    public static void addDataToFile(String filePath, String operation ,String Store,
                                     String dataFile,
                                     String queryFile,long timeValue) throws IOException {
        findOrCreateFile(filePath);
        Path path = Paths.get(filePath);
        String data = operation + "," + Store + "," + dataFile + "," + queryFile + "," + timeValue + "\n";
        Files.writeString(path, data, java.nio.file.StandardOpenOption.APPEND);
    }
}
