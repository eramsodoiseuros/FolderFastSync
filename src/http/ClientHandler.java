package http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ClientHandler implements Runnable{
    private final Socket socket;

    public ClientHandler(Socket s){
        socket = s;
    }

    @Override
    public void run() {
        try{
            System.out.println("\t" + socket.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String httpResponse = "";
            String httpResponseBody = "";

            String fromClient = in.readLine();
            String[] firstLine = fromClient.split(" ");

            System.out.println("... receiving from client:");
            while(!fromClient.isEmpty()){
                System.out.println("\t|"+ fromClient + "|");
                fromClient = in.readLine();
            }

            if(firstLine[0].compareTo(HTTPCodes.GET) ==0 && firstLine[2].compareTo(HTTPCodes.HTTP_VERSION) == 0){
                switch (firstLine[1]) {
                    case HTTPCodes.Route_Root -> {
                        httpResponse = HTTPCodes.OK;
                        httpResponseBody = HTMLCodes.HTML_RouteRoot;
                    }
                    case HTTPCodes.Route_Log -> {
                        httpResponse = HTTPCodes.OK;
                        httpResponseBody = HTMLCodes.HTML_RouteLog;
                    }
                    case HTTPCodes.Route_Status -> {
                        httpResponse = HTTPCodes.OK;
                        httpResponseBody = HTMLCodes.HTML_RouteStatus;
                    }
                    default -> {
                        httpResponse = HTTPCodes.NOT_FOUND;
                        httpResponseBody = HTMLCodes.HTML_NOTFOUND;
                    }
                }
            } else {
                httpResponse = HTTPCodes.BAD_REQUEST;
                httpResponseBody = HTMLCodes.HTML_BADREQUEST;
            }

            httpResponse += "\nServer: CC2021"
                    + "\nDate: " + new Date()
                    + "\nContent-type: text/html"
                    + "\nContent-length: " + httpResponseBody.length()
                    + "\nConnection: Closed"
                    + "\n\n"
                    + httpResponseBody;

            socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));

            in.close();
            socket.close();

        } catch (Exception e){
            System.out.println("erro HTTP - ClientHandler [" + e.getMessage() + "], [" + e.toString() +"]");
        }

    }
}

