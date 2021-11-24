import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 *  SISTEMA RELATIVO A UM SERVIDOR
 *
 *  Objetivo:
 *      - o servidor deve ser responsável por garantir conectividade à rede
 *      - deve ser definido uma lista de conexões (nodos presentes na rede)
 *      - a comunicação entre nodos pode, ou não, passar pelo servidor (?)
 *      - o servidor comunica por tcp ou udp (?)
 *
 * */

public class Servidor {

    public static void main(String[] args) {
        int porta = Integer.parseInt(args[0]);

        try {
            ServerSocket server = new ServerSocket(porta);
            System.out.println("[listening on port: " + porta + "]");
            boolean r = true;
            while(r){
                Socket socket = server.accept();
                // add cliente to lista de conexões

                Thread t = new Thread(/* handler */);
                t.start();

                socket.close();
                // remove cliente from lista de conexões
            }
            server.close();
            System.out.println("... a encerrar o servidor ...");
        } catch (IOException e) {
            System.out.println("erro [" + e.getMessage() + "]");
        }

        System.out.println("Servidor fechado.");
    }

}
