package folder_parser;

import app.FFSync;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static common.debugger.Debugger.log;

/**
 * Preciso saber todos os files dentro de uma pasta e em pastas de pastas (arvore de files)
 * <p>
 * é preciso saber o nome dos ficheiros, o tamanho dos ficheiros e a ultima atualizaçao desse ficheiro / pasta
 * <p>
 * aprender sobre metadados
 * <p>
 * . FFSync consegue obter uma lista dos ficheiros da pasta a sincronizar e listá-los no “log” ou na saída normal
 */

public class FolderParser {
    private static final int debuggerLevel = 2;

    public static Map<String, Long> metadata() {
        HashMap<String, Long> map = new HashMap<>();
        File curDir = FFSync.getCurrentDirectory();
        log("Current directory: " + curDir, debuggerLevel);
        String curDirPath = curDir.getAbsolutePath();
        log("Current directory path: " + curDirPath, debuggerLevel);
        int index = curDir.getAbsolutePath().length();
        try {
            Files.walkFileTree(Paths.get(curDirPath), new HashSet<>(), 20, new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    /*
                    File f = new File(String.valueOf(dir));
                    if (f.isDirectory()) {
                        String nova = (f.getPath()).substring(index);
                        map.put(nova, f.lastModified());
                    }

                     */
                    if (dir.toFile().getName().equals("~")) return FileVisitResult.SKIP_SUBTREE;
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    File f = file.toFile();

                    String nova = (f.getAbsolutePath()).substring(index);
                    log("FolderParser | VisitFile: " + nova, debuggerLevel);
                    if (!f.getName().startsWith("~")) {
                        log("AAAAAAAAAAAAAAAAAAAAAAAAAQUIIIIIIIIIIIIIII");
                        map.put(nova, f.lastModified());
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.out.println("visitFileFailed: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    // System.out.println("postVisitDirectory: " + dir);
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException ignored) {
        }

        log("!!!! FolderParser | Metadata: " + map + " !!!!", debuggerLevel);
        return map;
    }

}

