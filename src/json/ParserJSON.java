package json;

import app.FFSync;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;

public class ParserJSON {
    private static String jsonToHtml( Object obj ) {
        StringBuilder html = new StringBuilder( );

        try {
            if (obj instanceof JSONObject jsonObject) {
                List<String> keys = jsonObject.keySet().stream().toList();
                html.append("<div class=\"json_object\">");

                if (keys.size() > 0) {
                    for (String key : keys) {

                        html.append("<div><span class=\"json_key\"><pre><b>")
                                .append(key).append("</b></span> -> \n\t");

                        Object val = jsonObject.get(key);

                        html.append( jsonToHtml( val ) );
                        html.append("<pre></div>");
                    }
                }

                html.append("</div>");

            } else if (obj instanceof JSONArray array) {
                for (Object o : array) {
                    html.append("<pre>\t");
                    html.append(jsonToHtml(o));
                    html.append("<pre>");
                }
            } else {
                html.append( obj );
            }
        } catch (Exception e) {
            System.out.println("Error - JSON2HTML [" + e + "]");
        }

        return html.toString();
    }

    private static JSONObject parser(File file)  {
        JSONParser jp = new JSONParser();
        JSONObject ret = new JSONObject();
        try (FileReader reader = new FileReader(file)){
            Object obj = jp.parse(reader);

            ret = (JSONObject) obj;
        } catch (Exception e){
            System.out.println("Error - JSON Parser [" + e + "]");
        }

        return ret;
    }

    private static JSONObject metadata() throws IOException {
        JSONObject obj = new JSONObject();
        File curDir = FFSync.getCurrentDirectory();
        String curDirPath = curDir.getPath();
        int index = curDir.getAbsolutePath().length();

        Files.walkFileTree(Paths.get(curDirPath), new HashSet<>(), 20, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                File f = file.toFile();
                String nova = (f.getAbsolutePath()).substring(index);

                JSONArray ficheiro = new JSONArray();
                ficheiro.add("file name: " + f.getName() + " ; ");
                ficheiro.add("file path: " + f.getAbsolutePath() + " ; ");
                ficheiro.add("file last update: " + f.lastModified() + " ; ");
                ficheiro.add("file size: " + f.getTotalSpace() + " ; ");
                obj.put("Name: " + nova,ficheiro);

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.out.println("visitFileFailed: " + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });

        return obj;
    }

    public static JSONObject listar(){
        File directoryPath = new File(System.getProperty("user.dir"));
        JSONObject obj = new JSONObject();
        File[] filesList = directoryPath.listFiles();

        if (filesList != null) {

            for(File file : filesList) {
                JSONArray ficheiro = new JSONArray();
                ficheiro.add("file name: "+file.getName()+" ; ");
                ficheiro.add("file path: "+file.getAbsolutePath()+" ; ");
                ficheiro.add("file last update: "+file.lastModified()+" ; ");
                ficheiro.add("file size: "+file.getTotalSpace()+" ; ");
                obj.put("Name: "+file.getName(),ficheiro);
            }
            System.out.println(obj);
        }

        return obj;
    }

    private static JSONArray merge(File f){
        JSONArray returnValue = new JSONArray();
        Charset charset = StandardCharsets.UTF_8;
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(f.getPath()), charset)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                returnValue.add(line);
            }
        } catch (Exception e) {
            System.out.println("Error - MERGE LOGS [" + e + "]");
        }
        return returnValue;
    }

    public static String html_logs(){
        File curDir = new File(System.getProperty("user.dir"));
        File[] filesList = curDir.listFiles();
        JSONObject obj = new JSONObject();
        JSONArray f1;

        if (filesList != null) {
            for(File file : filesList) {
                System.out.println("\t\tfound ->" + file.getName() + "<-");
                if(file.isFile()){
                    f1 = merge(file);
                    obj.put("Name: " + file.getName(), f1);
                }
            }
        }

        return jsonToHtml(obj);
    }

    public static String html_files(){
        String returnValue = "";

        try {
            returnValue = jsonToHtml(metadata());
        } catch (Exception e) {
            System.out.println("Error - HMTL FILE LISTING [" + e + "]");
        }

        return returnValue;
    }
}

