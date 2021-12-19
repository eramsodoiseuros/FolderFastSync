package folder_parser;

import app.FFSync;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
//import org.json;

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
    //   private List<Directory1> directories;
    private final Set<File> f1;
    private final Set<File> f2;
    private Map<String,Long> map=new HashMap();

    public FolderParser() {
        this.f1 = new HashSet<>();
        this.f2 = new HashSet<>();
    }

        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
        //System.out.println("visitFile: " + file);
        File f= new File(String.valueOf(file));
        map.put(f.getName(),f.lastModified());
        return FileVisitResult.CONTINUE;
    }

    public static Map<String, Long> metadata(List<String> file_names) {
        Map<String, Long> r = new HashMap<>();
        int i = 0;
        for (String s : file_names) {
            File f = new File(FFSync.getCurrentDirectory() + "/" + s);
            // LocalDateTime d = LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault());
            long d = f.lastModified();
            r.put(s, d);
        }
        return r;
        // return file_names.stream().map(fazer cenas).collect(Collectors.toList);
    }
/*
    public static JSONObject listar() {
        String dirName = "user.home";
        File directoryPath = new File(System.getProperty(dirName));
        JSONObject obj = new JSONObject();
        File[] filesList = directoryPath.listFiles();

        if (filesList != null) {

            for (File file : filesList) {
                JSONArray ficheiro = new JSONArray();
                ficheiro.add("file name: " + file.getName() + " ; ");//duas vezes
                ficheiro.add("file path: " + file.getAbsolutePath() + " ; ");
                LocalDateTime d = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
                ficheiro.add("file last update: " + d + " ; ");
                ficheiro.add("file size: " + file.getTotalSpace() + " bytes; ");
                //adicionar linha?
                obj.put("Name: " + file.getName(), ficheiro);
            }
            System.out.println(obj);
        }
        return obj;
    }
*/
    public static void main(String[] args) throws IOException {
    //    FolderParser.listar();
            Map<String,Long> map= new HashMap();
            //String pathString = "/home/sdaslira/CC2021/src/test/FFSync/folder1/file1";
            String pathString= "/home/sdaslira/CC2021/src/test/FFSync/folder1";

            Files.walkFileTree(Paths.get(pathString),new HashSet<>(), 2, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    System.out.println("preVisitDirectory: " + dir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    File f =new File(String.valueOf(file));
                    File pai=f.getParentFile();

                    int index=f.getPath().length()-f.getName().length()-pai.getName().length()-2;
                    String nova= (f.getPath()).substring(index);
                    //String novas[]= f.getPath().split(pai);

                    map.put(nova,f.lastModified());
                    System.out.println("file: "+nova+","+f.lastModified());
                   // System.out.println("visitFile: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc)
                        throws IOException {
                    System.out.println("visitFileFailed: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                        throws IOException {
                    System.out.println("postVisitDirectory: " + dir);
                    return FileVisitResult.CONTINUE;
                }
            });


        }
    }





/*
    public int compareFile(File dir1, File dir2) {
        int r = -1;

        if (dir1.compareTo(dir2) == 0 && dir1.lastModified() == dir2.lastModified() && dir1.length() == dir2.length())
            r = 0;
        else {
            if (dir1.lastModified() > dir2.lastModified()) {
                r = 1;
            } else r = 2;
        }
        return r;
    }

    public void print(Map<String, Long> l) {
        for (var x : l.entrySet()) {
            System.out.println(x);
        }
    }

    public void listar1() {

        System.out.println("estou aqui - " + System.getProperty("user.dir"));
        File directoryPath = new File(System.getProperty("user.dir"));
        JSONObject obj = new JSONObject();
        File[] filesList = directoryPath.listFiles();

        if (filesList != null) {
            System.out.println("nao era nulo");
            for (File file : filesList) {
                try {
                    obj.put("file path", file.getAbsolutePath());
                    obj.put("file name", file.getName());
                    obj.put("file last update", file.lastModified());
                    obj.put("file size", file.getTotalSpace());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println(obj);
        }

        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "log.txt");
            file.write(obj.toString());
            //toJSONString()
        } catch (Exception e) {
            System.out.println("erro HTTP - JSON WRITE [" + e.getMessage() + "]");
        }
    }

    //em teste
    public static void listSubDir(File f, List<File> lf) {
        File[] files = f.listFiles();
        for (File f1 : files) {
            if (f1.isFile()) {
                lf.add(f1);
            } else if (f.isDirectory()) {
                listSubDir(f, lf);
            }
        }
    }
    //em teste

    //em teste
    public static Map<String, Long> getMetadata1() { // Pasta -> Listar os ficheiros e os ficheiros das sub-diretorias
        File[] files = FFSync.getCurrentDirectory().listFiles();
        List<File> lf = new ArrayList<>();
        Map<String, Long> filesMeta = new HashMap();
        for (File f : files)
            listSubDir(f, lf);
        for (File f : lf)
            filesMeta.put(f.getName(), f.lastModified());

        return filesMeta;
    }

}
*/

// metodo que lista ficheiros diferentes (diretoria a, diretoria b) => lista dos elementos de a que sao != b e o mesmo para b

// metodo que percorre a estrutura

// metodo que cria a estrutura (lê pastas)

// metodo que escreve um ficheiro numa diretoria

// metodo que devolve o caminho de um ficheiro numa diretoria

