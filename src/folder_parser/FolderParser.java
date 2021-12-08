import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
//import org.json;
/**
 *
 *  Preciso saber todos os files dentro de uma pasta e em pastas de pastas (arvore de files)
 *
 *  é preciso saber o nome dos ficheiros, o tamanho dos ficheiros e a ultima atualizaçao desse ficheiro / pasta
 *
 *  aprender sobre metadados
 *
 * . FFSync consegue obter uma lista dos ficheiros da pasta a sincronizar e listá-los no “log” ou na saída normal
 * */

public class FolderParser {
 //   private List<Directory1> directories;
    private Set<File> f1;
    private Set<File> f2;
    public FolderParser(){
        this.f1=new HashSet<>();
        this.f2=new HashSet<>();
    }
    void compareFiles(File f1, File f2) {

       // boolean b = false;
        File[] fs1 = f1.listFiles();
        File[] fs2 = f2.listFiles();
        int j=0;
        for(File f:fs1){
            int r=compareFile(f,fs2[j]);
            switch (r){
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
        if(fs2.length>fs1.length){
            for(;j<fs2.length;j++){
                this.f1.add(fs2[j]);
            }

        }
        //return b;
    }
   public int compareFile(File dir1, File dir2){
        int r=-1;

        if(dir1.compareTo(dir2)==0 && dir1.lastModified()==dir2.lastModified() && dir1.length()==dir2.length() )r=0;else{
            if(dir1.lastModified()>dir2.lastModified()){
                r=1;
            }else r=2;
        }
        return r;
    }

    public List<AbstractMap.SimpleEntry<String, LocalDateTime> > metadata(List<String> file_names) {
        List<AbstractMap.SimpleEntry<String,LocalDateTime>> r= new ArrayList<>();
        int i=0;
        for(String s: file_names){
            File f= new File(s);
            LocalDateTime d=LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault());
            AbstractMap.SimpleEntry<String,LocalDateTime> x = new AbstractMap.SimpleEntry<>(s,d);
            r.add(i,x);
        }
        return r;
       // return file_names.stream().map(fazer cenas).collect(Collectors.toList);
    }
    public void print(List<AbstractMap.SimpleEntry<String,LocalDateTime>> l){
        for(AbstractMap.SimpleEntry<String,LocalDateTime> x: l){
            System.out.println(x.toString());
        }
    }
/*
    public void listar() throws org.json.JSONException {

        System.out.println("estou aqui - " + System.getProperty("user.dir"));
        File directoryPath = new File(System.getProperty("user.dir"));
        org.json.JSONObject obj = new org.json.JSONObject();
        File[] filesList = directoryPath.listFiles();

        if (filesList != null) {
            System.out.println("nao era nulo");
            for(File file : filesList) {
                try{
                    obj.put("file path",file.getAbsolutePath());
                    obj.put("file name", file.getName());
                    obj.put("file last update", file.lastModified());
                    obj.put("file size", file.getTotalSpace());
            }catch(org.json.JSONException e){
                    System.out.println(e.getMessage());
                }
            }
            System.out.println(obj);
        }

        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir")+"log.txt");
            file.write(obj.toString());
            //toJSONString()
        } catch (IOException e) {
            System.out.println("erro HTTP - JSON WRITE [" + e.getMessage() + "]");
        }
    }
*/
    public static void main(String[] args) {
        FolderParser fp=  new FolderParser();
        List<String> l= new ArrayList<>();
        l.add("a.txt");
        l.add("d.txt");
        l.add("teste");
        List<AbstractMap.SimpleEntry<String,LocalDateTime>> lx= fp.metadata(l);
        fp.print(lx);
        //fp.compareFiles("\~\fteste","\~\fteste");
        System.out.println("oi");
    }

}


    // metodo que lista ficheiros diferentes (diretoria a, diretoria b) => lista dos elementos de a que sao != b e o mesmo para b

    // metodo que percorre a estrutura

    // metodo que cria a estrutura (lê pastas)

    // metodo que escreve um ficheiro numa diretoria

    // metodo que devolve o caminho de um ficheiro numa diretoria

