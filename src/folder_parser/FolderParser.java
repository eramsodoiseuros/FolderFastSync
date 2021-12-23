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
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    File f = file.toFile();

                    String nova = (f.getAbsolutePath()).substring(index);
                    log("FolderParser | VisitFile: " + nova, debuggerLevel);
                    map.put(nova, f.lastModified());

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.out.println("visitFileFailed: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException ignored) {
        }

        log("FolderParser | Metadata: " + map, debuggerLevel);
        return map;
    }

}

