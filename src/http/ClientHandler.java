package http;

import json.ParserJSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

            String httpResponse;
            String httpResponseBody;

            String fromClient = in.readLine();
            String[] firstLine = fromClient.split(" ");

            System.out.println("... receiving from client:");
            while(!fromClient.isEmpty()){
                fromClient = in.readLine();
            }

            if(firstLine[0].compareTo(HTTPCodes.GET) ==0 && firstLine[2].compareTo(HTTPCodes.HTTP_VERSION) == 0){
                switch (firstLine[1]) {
                    case HTTPCodes.Route_Root -> {
                        httpResponse = HTTPCodes.OK;
                        System.out.println("\t... received /");
                        httpResponseBody = HTMLCodes.HTML_Home();
                    }
                    case HTTPCodes.Route_Log -> {
                        httpResponse = HTTPCodes.OK;
                        System.out.println("\t... received /log");
                        httpResponseBody = HTMLCodes.HTML_Title("Logs") + ParserJSON.html_logs();
                    }
                    case HTTPCodes.Route_Status -> {
                        httpResponse = HTTPCodes.OK;
                        System.out.println("\t... received /status");
                        httpResponseBody = HTMLCodes.HTML_Title("Lista de Ficheiros") + ParserJSON.html_files();
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

            socket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));

            in.close();
            socket.close();

        } catch (Exception e){
            System.out.println("Error HTTP - ClientHandler [" + e + "]");
        }

    }
}

