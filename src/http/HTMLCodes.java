package http;

public class HTMLCodes {

    public static final String HTML_NOTFOUND = "<html><body><h1>ERROR 404</h1><h2>Not Found</h2></body></html>";
    public static final String HTML_BADREQUEST = "<html><body><h1>ERROR 400</h1><h2>Bad Request</h2></body></html>";

    public static String HTML_Title(String s){
        return "<h1>"+s+"</h1>";
    }

    public static String HTML_Home(){
        StringBuilder s = new StringBuilder();

        s.append("<html><body>");
        s.append("<h1>HOMEPAGE</h1>");
        s.append("<p><b>Paginas Disponiveis: </b></p>");
        s.append("<pre>\t-> /log : ver o ficheiro de LOGS.</pre>");
        s.append("<pre>\t-> /status : ver os ficheiros atuais do sistema.</pre>");
        s.append("</body></html>");

        return s.toString();
    }
}

