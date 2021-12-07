import java.io.File;
import java.util.HashSet;

import java.util.Set;

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

    void compareFiles(File f1, File f2) {
        this.f1=new HashSet<>();
        this.f2=new HashSet<>();

        boolean b = false;
        File[] fs1 = f1.listFiles();
        File[] fs2 = f2.listFiles();
        int i = 0, flag;

        int max, min;
        if (fs2.length > fs1.length) {
            max = fs2.length;
            min = fs1.length;
            b=true;
        } else {
            max = fs1.length;
            min = fs2.length;
            b=false;
        }
        for (i = 0; i < max; i++) {
            if(fs1[i]!=null && fs2[i]!=null) {
                if ((flag = compareFile(fs1[i], fs2[i])) > 0) {
                    if (flag == 1) {
                        this.f2.add(fs1[i]);
                    } else {
                        this.f1.add(fs2[i]);
                    }
                }
            }else
            if(fs1[i]==null){
                this.f1.add(fs2[i]);
            }else{
                this.f2.add(fs1[i]);
            }
            i++;
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
}


    // metodo que lista ficheiros diferentes (diretoria a, diretoria b) => lista dos elementos de a que sao != b e o mesmo para b

    // metodo que percorre a estrutura

    // metodo que cria a estrutura (lê pastas)

    // metodo que escreve um ficheiro numa diretoria

    // metodo que devolve o caminho de um ficheiro numa diretoria

