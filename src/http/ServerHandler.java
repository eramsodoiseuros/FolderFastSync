package http;

import java.net.ServerSocket;

public class ServerHandler implements Runnable{
    private boolean running;

    public ServerHandler(){
        running = true;
    }

    public void change(boolean r){
        running = r;
    }

    @Override
    public void run() {
        try {
            ServerSocket s = new ServerSocket(80);

            while(running){

                ClientHandler handler = new ClientHandler(s.accept());
                System.out.println("... socket accepted:");

                Thread t = new Thread(handler);

                t.start();
            }

            s.close();
        } catch (Exception e){
            System.out.println("erro HTTP - main [" + e.getMessage() + "]");
        }
    }
}
