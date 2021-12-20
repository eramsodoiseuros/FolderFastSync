package folder_parser;

import app.FFSync;


import java.io.File;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.*;

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


    public static Map<String, Long> metadata(List<String> file_names) {
        Map<String, Long> r = new HashMap<>();
        for (String s : file_names) {
            File f = new File(FFSync.getCurrentDirectory() + "/" + s);
            // LocalDateTime d = LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault());
            long d = f.lastModified();
            r.put(s, d);
        }
        return r;
        // return file_names.stream().map(fazer cenas).collect(Collectors.toList);
    }

    public static void main(String[] args) throws IOException {
        //    FolderParser.listar();
        String s = System.getProperty("user.dir");
        System.out.println(s);
        HashMap<String, Long> map = new HashMap();
        File curDir = FFSync.getCurrentDirectory();
        String curDirPath = curDir.getPath();
        int index=curDir.getPath().length() - curDir.getName().length() - 1;
        Files.walkFileTree(Paths.get(curDirPath), new HashSet<>(), 2, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    {
                //throws IOException
                File f = new File(String.valueOf(dir));
                if (f.isDirectory()) {
                    String nova = (f.getPath()).substring(index);
                    map.put(nova, f.lastModified());
                }
               // System.out.println("preVisitDirectory: " + dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                   {
                //throws IOException
                File f = new File(String.valueOf(file));
                String nova = (f.getPath()).substring(index);
                map.put(nova, f.lastModified());

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    {
                //throws IOException
                System.out.println("visitFileFailed: " + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    {
                //throws IOException
               // System.out.println("postVisitDirectory: " + dir);
                return FileVisitResult.CONTINUE;
            }
        });

        for (Map.Entry<String, Long> par : map.entrySet()) {
            System.out.println("nome: "+par.getKey()+", tempo: "+par.getValue());
        }
    }
}




// metodo que lista ficheiros diferentes (diretoria a, diretoria b) => lista dos elementos de a que sao != b e o mesmo para b

// metodo que percorre a estrutura

// metodo que cria a estrutura (lê pastas)

// metodo que escreve um ficheiro numa diretoria

// metodo que devolve o caminho de um ficheiro numa diretoria

