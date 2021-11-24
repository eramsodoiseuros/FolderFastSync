import java.io.File;
import java.net.DatagramPacket;
import java.time.LocalDateTime;
import java.util.TreeMap;

/**
 * HANDLER DE UM CLIENTE (equipamento sincronizado com a rede)
 * Objetivo:
 * - o cliente deve ser capaz de reconhecer mudanças na rede
 * - consequentemente o cliente deve ser capaz de sincronizar com as mudanças na rede
 * - o cliente deve ser capaz de listar metadados dos ficheiros que guarda
 * - o cliente deve utilizar udp para: TRANSFERENCIA DE FICHEIROS
 * - o cliente deve utilizar tcp para: MONITORIZAÇAO DO ESTADO DE CADA CLIENTE
 */

public class ClientHandler implements Runnable {
    private TreeMap<String, File> lista_ficheiros; // replace file with metadados somehow
    private LocalDateTime versao;
    private int tag_tcp_udp;

    ClientHandler(DatagramPacket inPacket) {

    }


    @Override
    public void run() {

    }
}
