package folder_parser;

import app.FFSync;
import ffrapid_protocol.packet.Metadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static common.debugger.Debugger.log;
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

    public FolderParser() {
        this.f1 = new HashSet<>();
        this.f2 = new HashSet<>();
    }

    void compareFiles(File f1, File f2) {

        // boolean b = false;
        File[] fs1 = f1.listFiles();
        File[] fs2 = f2.listFiles();
        int j = 0;
        for (File f : fs1) {
            int r = compareFile(f, fs2[j]);
            switch (r) {
                case 1:
                    //f e mais atualizada
                    this.f2.add(f);
                case 2:
                    //fs2[j] e mais atualizada
                    this.f1.add(fs2[j]);
                case 0:
                    //sao iguais

                default:
                    //System.out.println("erro");
            }
            j++;
        }
        if (fs2.length > fs1.length) {
            for (; j < fs2.length; j++) {
                this.f1.add(fs2[j]);
            }

        }
        //return b;
    }

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

    public void print(Map<String, Long> l) {
        for (var x : l.entrySet()) {
            System.out.println(x);
        }
    }


        public void listar1()  {

            System.out.println("estou aqui - " + System.getProperty("user.dir"));
            File directoryPath = new File(System.getProperty("user.dir"));
            JSONObject obj = new JSONObject();
            File[] filesList = directoryPath.listFiles();

            if (filesList != null) {
                System.out.println("nao era nulo");
                for(File file : filesList) {
                    try{
                        obj.put("file path",file.getAbsolutePath());
                        obj.put("file name", file.getName());
                        obj.put("file last update", file.lastModified());
                        obj.put("file size", file.getTotalSpace());
                }catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                }
                System.out.println(obj);
            }

            try {
                FileWriter file = new FileWriter(System.getProperty("user.dir")+"log.txt");
                file.write(obj.toString());
                //toJSONString()
            } catch (Exception e) {
                System.out.println("erro HTTP - JSON WRITE [" + e.getMessage() + "]");
            }
        }

    public static JSONObject listar(){
        String dirName= "user.home";
        File directoryPath = new File(System.getProperty(dirName));
        JSONObject obj = new JSONObject();
        File[] filesList = directoryPath.listFiles();

        if (filesList != null) {

            for(File file : filesList) {
                JSONArray ficheiro = new JSONArray();
                ficheiro.add("file name: "+file.getName()+" ; ");//duas vezes
                ficheiro.add("file path: "+file.getAbsolutePath()+" ; ");
                LocalDateTime d = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
                ficheiro.add("file last update: "+d+" ; ");
                ficheiro.add("file size: "+file.getTotalSpace()+" bytes; ");
                //adicionar linha?
                obj.put("Name: "+file.getName(),ficheiro);
            }
            System.out.println(obj);
        }
        return obj;
    }
    //em teste
    public void listSubDir(File f,List<File> lf){
        File[] files=f.listFiles();
        for(File f1:files) {
            if (f1.isFile()) {
                lf.add(f1);
            } else if (f.isDirectory()) {
                listSubDir(f, lf);
            }
        }
    }
    //em teste
    public List<Map.Entry<String, Long>> compareMetadata1(Metadata metadata) {
        File files[] = FFSync.getCurrentDirectory().listFiles();
        List<File> lf= new ArrayList<>();

        Map<String, Long> filesMeta = new HashMap();

        for(File f: files)
            listSubDir(f,lf);

        for(File f: lf)
            filesMeta.put(f.getPath(),f.lastModified());

        Predicate<Map.Entry<String, Long>> different = e -> {
            Long time = filesMeta.get(e.getKey());
            return !Objects.equals(time, e.getValue()) || time == null;
        };
        log("Local files: " + filesMeta);
        log("Other files: " + metadata.metadata);
        return metadata.metadata.entrySet().stream().filter(different).collect(Collectors.toList());
    }
    //em teste

    public static void main(String[] args) {
        /*
        FolderParser fp = new FolderParser();
        List<String> l = new ArrayList<>();
        l.add("a.txt");
        l.add("d.txt");
        l.add("teste");
        Map<String, Long> lx = metadata(l);
        fp.print(lx);
        //fp.compareFiles("\~\fteste","\~\fteste");
        System.out.println("oi");
        */
        FolderParser.listar();
    }

}


// metodo que lista ficheiros diferentes (diretoria a, diretoria b) => lista dos elementos de a que sao != b e o mesmo para b

// metodo que percorre a estrutura

// metodo que cria a estrutura (lê pastas)

// metodo que escreve um ficheiro numa diretoria

// metodo que devolve o caminho de um ficheiro numa diretoria

