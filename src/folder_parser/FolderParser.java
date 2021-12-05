package folder_parser;

import java.time.LocalDateTime;

/**
 *
 *  Preciso saber todos os files dentro de uma pasta e em pastas de pastas (arvore de files)
 *
 *  é preciso saber o nome dos ficheiros, o tamanho dos ficheiros e a ultima atualizaçao desse ficheiro / pasta
 *
 *  aprender sobre metadados
 *
 * . app.FFSync consegue obter uma lista dos ficheiros da pasta a sincronizar e listá-los no “log” ou na saída normal
 * */

public class FolderParser {

    public static class Directory {

        // arvore de files e diretorias

        public static class File {
            // inicial, eventualmente aprender sobre metadados
            private String name;
            private LocalDateTime ultima_atualizacao;
            private int size;

        }

        // root
        // diretoria = diretoria e/ou ficheiro
    }

    // metodo que lista ficheiros diferentes (diretoria a, diretoria b) => lista dos elementos de a que sao != b e o mesmo para b

    // metodo que percorre a estrutura

    // metodo que cria a estrutura (lê pastas)

    // metodo que escreve um ficheiro numa diretoria

    // metodo que devolve o caminho de um ficheiro numa diretoria
}
